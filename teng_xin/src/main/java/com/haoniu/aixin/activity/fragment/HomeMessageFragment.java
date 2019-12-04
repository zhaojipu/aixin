package com.haoniu.aixin.activity.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.haoniu.aixin.R;
import com.haoniu.aixin.activity.ChatActivity;
import com.haoniu.aixin.adapter.HomeMessageAdapter;
import com.haoniu.aixin.base.Constant;
import com.haoniu.aixin.base.EaseConstant;
import com.haoniu.aixin.base.MyHelper;
import com.haoniu.aixin.domain.EaseGroupInfo;
import com.haoniu.aixin.entity.EventCenter;
import com.haoniu.aixin.entity.RoomInfo;
import com.haoniu.aixin.http.ApiClient;
import com.haoniu.aixin.http.AppConfig;
import com.haoniu.aixin.http.ResultListener;
import com.haoniu.aixin.widget.CommonDialog;
import com.haoniu.aixin.widget.PopWinShare;
import com.hyphenate.chat.EMChatRoom;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.zds.base.Toast.ToastUtil;
import com.zds.base.code.activity.CaptureActivity;
import com.zds.base.json.FastJsonUtil;
import com.zds.base.util.DensityUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


/**
 * 会话消息
 */
public class HomeMessageFragment extends EaseBaseFragment {

    protected boolean hidden;
    protected boolean isConflict;
    Unbinder unbinder;
    @BindView(R.id.bar)
    View bar;
    @BindView(R.id.ll_back)
    LinearLayout llBack;
    @BindView(R.id.toolbar_subtitle)
    TextView toolbarSubtitle;
    @BindView(R.id.img_right)
    ImageView imgRight;
    @BindView(R.id.toolbar_title)
    TextView toolbarTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.llayout_title_1)
    RelativeLayout llayoutTitle1;
    @BindView(R.id.recycle_view)
    RecyclerView recycleView;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;

    private PopWinShare popWinShare;


    private int page = 1;
    HomeMessageAdapter homeMessageAdapter;
    List<RoomInfo> roomInfos = new ArrayList<>();
    List<RoomInfo> roomInfoServer=new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_message, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        if (savedInstanceState != null && savedInstanceState.getBoolean("isConflict", false)) {
            return;
        }
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        this.hidden = hidden;
        if (!hidden && !isConflict) {
            refresh();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!hidden) {
            refresh();
        }
    }

    @Override
    protected void initLogic() {
        llBack.setVisibility(View.GONE);
        toolbarTitle.setText("爱聊");
        imgRight.setImageResource(R.mipmap.jiahao);
        imgRight.setVisibility(View.VISIBLE);
        imgRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopudown();
            }
        });
        homeMessageAdapter = new HomeMessageAdapter(roomInfos);
        recycleView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recycleView.setAdapter(homeMessageAdapter);
        homeMessageAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (roomInfos.get(position).getEmConversation()!=null&&roomInfos.get(position).getEmConversation().getType().equals(EMConversation.EMConversationType.Chat)){
                    startActivity(new Intent(getActivity(), ChatActivity.class).putExtra(EaseConstant.EXTRA_CHAT_TYPE, EaseConstant.CHATTYPE_SINGLE)
                            .putExtra(EaseConstant.EXTRA_USER_ID, roomInfos.get(position).getEmConversation().conversationId())
                           );
                }else {
                    EMChatRoom group = EMClient.getInstance().chatroomManager().getChatRoom(roomInfos.get(position).getHuanxinGroupId());
                    if (group == null) {
                        joinRoomPassword(roomInfos.get(position).getIsPassword()==1,roomInfos.get(position).getHuanxinGroupId(), roomInfos.get(position).getType());
                    } else {
                        startActivity(new Intent(getActivity(), ChatActivity.class).putExtra(EaseConstant.EXTRA_CHAT_TYPE, EaseConstant.CHATTYPE_CHATROOM)
                                .putExtra(EaseConstant.EXTRA_USER_ID, roomInfos.get(position).getHuanxinGroupId())
                                .putExtra(Constant.ROOMTYPE, roomInfos.get(position).getType()));
                    }
                }

            }
        });
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                getData();
            }
        });
        refreshLayout.setEnableLoadmore(false);
        refreshLayout.autoRefresh();
    }


    /**
     *
     *  显示浮窗菜单
     */
    private void showPopudown(){
        if (popWinShare == null) {
            //自定义的单击事件
            View.OnClickListener paramOnClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (v.getId()==R.id.layout_saoyisao){
                        startActivity(new Intent(getActivity(), CaptureActivity.class));

                    }
                }
            };
            popWinShare = new PopWinShare(getActivity(), paramOnClickListener, DensityUtils.dip2px(getActivity(), 105), DensityUtils.dip2px(getActivity(), 140));
            //监听窗口的焦点事件，点击窗口外面则取消显示
            popWinShare.getContentView().setOnFocusChangeListener(new View.OnFocusChangeListener() {

                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        popWinShare.dismiss();
                    }
                }
            });
        }
        //设置默认获取焦点
        popWinShare.setFocusable(true);
        //以某个控件的x和y的偏移量位置开始显示窗口
        popWinShare.showAsDropDown(imgRight, 0, DensityUtils.dip2px(getActivity(), 8));
        //如果窗口存在，则更新
        popWinShare.update();
    }

    /**
     * 刷新
     */
    public void refresh(){
        if (!handler.hasMessages(3)){
            handler.sendEmptyMessage(3);
        }
    }

    protected Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    //onConnectionDisconnected();
                    break;
                case 1:
                   // onConnectionConnected();
                    break;

                case 3: {
                    getData();
                }
                default:
                    break;
            }
        }
    };

    /**
     * load conversation list
     *
     * @return +
     */
    protected List<RoomInfo> loadConversationList(List<RoomInfo> list) {
        // get all conversations
        Map<String, EMConversation> conversations = EMClient.getInstance().chatManager().getAllConversations();
        List<Pair<Long, EMConversation>> sortList = new ArrayList<Pair<Long, EMConversation>>();
        List<RoomInfo> valueList = new ArrayList<>();
        if (list == null) {
            list = new ArrayList<>();
        }

        /**
         * lastMsgTime will change if there is new message during sorting
         * so use synchronized to make sure timestamp of last message won't change.
         */
        synchronized (conversations) {
            for (RoomInfo roomInfo : list) {
                if (conversations.containsKey(roomInfo.getHuanxinGroupId())) {
                    roomInfo.setEmConversation(conversations.get(roomInfo.getHuanxinGroupId()));
                }
            }
            for (EMConversation conversation : conversations.values()) {
                if (conversation.getAllMessages().size() != 0 && !conversation.getType().equals(EMConversation.EMConversationType.ChatRoom)&&!conversation.getType().equals(EMConversation.EMConversationType.GroupChat) && !conversation.conversationId().equals(Constant.ADMIN)) {
                    sortList.add(new Pair<Long, EMConversation>(conversation.getLastMessage().getMsgTime(), conversation));
                }else {
                    if (!conversation.getType().equals(EMConversation.EMConversationType.ChatRoom)){
                        conversation.markAllMessagesAsRead();
                    }
                }
            }
        }
        try {
            // Internal is TimSort algorithm, has bug
            sortConversationByLastChatTime(sortList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        valueList.addAll(list);
        for (Pair<Long, EMConversation> sortItem : sortList) {
            RoomInfo roomInfo = new RoomInfo();
            roomInfo.setHuanxinGroupId(sortItem.second.conversationId());
            roomInfo.setEmConversation(sortItem.second);
            valueList.add(roomInfo);
        }
        return valueList;
    }

    /**
     * sort conversations according time stamp of last message
     *
     * @param conversationList
     */
    private void sortConversationByLastChatTime(List<Pair<Long, EMConversation>> conversationList) {
        Collections.sort(conversationList, new Comparator<Pair<Long, EMConversation>>() {
            @Override
            public int compare(final Pair<Long, EMConversation> con1, final Pair<Long, EMConversation> con2) {
                if (con1.first.equals(con2.first)) {
                    return 0;
                } else if (con2.first.longValue() > con1.first.longValue()) {
                    return 1;
                } else {
                    return -1;
                }
            }

        });
    }

    @Override
    protected void onEventComing(EventCenter center) {

    }

    @Override
    protected void getBundleExtras(Bundle extras) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    //获取数据
    private void getData() {
        page = 1;
        Map<String, Object> map = new HashMap<>();
        map.put("pageNum", page);
        map.put("pageSize", 20);
        ApiClient.requestNetHandle(getActivity(), AppConfig.getRoomList, "", map, new ResultListener() {
            /**
             * 请求成功
             * @param json
             * @param msg
             */
            @Override
            public void onSuccess(String json, String msg) {
                if (json != null) {
                    List<RoomInfo> list = FastJsonUtil.getList(json, "list", RoomInfo.class);
                    roomInfos.clear();
                    roomInfoServer.clear();
                    if (list != null && list.size() > 0) {
                        roomInfoServer.addAll(list);
                        for (RoomInfo roomInfo : list) {
                            EaseGroupInfo easeGroupInfo = MyHelper.getInstance().getGroupById(roomInfo.getHuanxinGroupId());
                            easeGroupInfo.setHead(roomInfo.getRoomImg());
                            easeGroupInfo.setGroupName(roomInfo.getName());
                            easeGroupInfo.setGroupType(roomInfo.getType());
                            MyHelper.getInstance().saveGroup(easeGroupInfo);
                        }
                    }
                    roomInfos.addAll(loadConversationList(list));
                    homeMessageAdapter.notifyDataSetChanged();
                }
            }


            @Override
            public void onFinsh() {
                super.onFinsh();
                if (refreshLayout != null) {
                    refreshLayout.finishRefresh();
                    homeMessageAdapter.loadMoreComplete();
                }
            }

            /**
             * 请求失败
             *
             * @param msg
             */
            @Override
            public void onFailure(String msg) {
                ToastUtil.toast(msg);
            }
        });
    }
    


    /**
     * 加入群组
     */
    private void joinRoom(String password,final String groupId, final int type) {
        Map<String, Object> map = new HashMap<>();
        map.put("groupId", groupId);
        map.put("password",password);
        ApiClient.requestNetHandle(getActivity(), AppConfig.AddRoom, "正在加入房间...", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                if (commonDialog != null && commonDialog.isShowing()) {
                    commonDialog.dismiss();
                }
                startActivity(new Intent(getActivity(), ChatActivity.class).putExtra(EaseConstant.EXTRA_CHAT_TYPE, EaseConstant.CHATTYPE_CHATROOM)
                        .putExtra(EaseConstant.EXTRA_USER_ID, groupId)
                        .putExtra(Constant.ROOMTYPE, type));
            }

            @Override
            public void onFinsh() {
                super.onFinsh();
                if (commonDialog != null && commonDialog.isShowing()) {
                    commonDialog.dismiss();
                }
            }

            @Override
            public void onFailure(String msg) {
                ToastUtil.toast(msg);
            }
        });
    }
    CommonDialog commonDialog;
    TextView password;
    /**
     * 加入房间 （密码窗）
     */
    private void joinRoomPassword(boolean isHavaPassword, final String groupId, final int type) {
        if (isHavaPassword) {
            if (commonDialog != null && commonDialog.isShowing()) {
                commonDialog.dismiss();
            }
            commonDialog = new CommonDialog.Builder(getActivity()).setView(R.layout.dialog_password).center().loadAniamtion()
                    .setOnClickListener(R.id.tv_quxiao, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            commonDialog.dismiss();
                        }
                    })
                    .setOnClickListener(R.id.tv_queding, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (password.getText().toString() == null && "".equals(password.getText().toString())) {
                                toast("请输入密码");
                                return;
                            }
                            joinRoom(password.getText().toString(), groupId, type);
                        }
                    }).create();
            password = commonDialog.getView(R.id.tv_password);
            commonDialog.show();
        } else {
            joinRoom("", groupId, type);
        }

    }
}
