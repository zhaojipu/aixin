package com.haoniu.aixin.activity.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.haoniu.aixin.R;
import com.haoniu.aixin.activity.ChatActivity;
import com.haoniu.aixin.adapter.RoomAdapter;
import com.haoniu.aixin.base.Constant;
import com.haoniu.aixin.base.EaseConstant;
import com.haoniu.aixin.base.MyHelper;
import com.haoniu.aixin.domain.EaseGroupInfo;
import com.haoniu.aixin.entity.EventCenter;
import com.haoniu.aixin.entity.RoomInfo;
import com.haoniu.aixin.http.ApiClient;
import com.haoniu.aixin.http.AppConfig;
import com.haoniu.aixin.http.ResultListener;
import com.hyphenate.chat.EMChatRoom;
import com.hyphenate.chat.EMClient;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.zds.base.Toast.ToastUtil;
import com.zds.base.json.FastJsonUtil;
import com.zds.base.log.XLog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 作   者：赵大帅
 * 描   述: 房间列表
 * 日   期: 2017/12/30 9:46
 * 更新日期: 2017/12/30
 */
public class RoomListFragment extends EaseBaseFragment {


    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout mRefreshLayout;
    Unbinder unbinder;
    @BindView(R.id.tv_gf)
    TextView mTvGf;
    @BindView(R.id.tv_dl)
    TextView mTvDl;

    private RoomAdapter mRoomAdapter;
    private List<RoomInfo> mRoomInfoList;
    /**
     * 房间类型 (1、接龙2、踩雷)
     */
    private int type = 1;
    private int page = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_room_list, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    /**
     * 初始化逻辑
     */
    @Override
    protected void initLogic() {
        mRoomInfoList = new ArrayList<>();
        mRoomAdapter = new RoomAdapter(mRoomInfoList);
        LinearLayoutManager LinearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(LinearLayoutManager);
        mRecyclerView.setAdapter(mRoomAdapter);
        mRoomAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                EMChatRoom group = EMClient.getInstance().chatroomManager().getChatRoom(mRoomInfoList.get(position).getHuanxinGroupId());
                if (group == null) {
                    joinRoom(mRoomInfoList.get(position).getHuanxinGroupId(), mRoomInfoList.get(position).getType());
                } else {
                    startActivity(new Intent(getActivity(), ChatActivity.class).putExtra(EaseConstant.EXTRA_CHAT_TYPE, EaseConstant.CHATTYPE_CHATROOM)
                            .putExtra(EaseConstant.EXTRA_USER_ID, mRoomInfoList.get(position).getHuanxinGroupId())
                            .putExtra(Constant.ROOMTYPE, mRoomInfoList.get(position).getType()));
                }
            }
        });
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                getData();
            }
        });
        mRoomAdapter.openLoadAnimation();
        mRoomAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                loadData();
            }
        }, mRecyclerView);
        mRefreshLayout.setEnableLoadmore(false);
        mRefreshLayout.autoRefresh();
    }

    /**
     * EventBus接收消息
     *
     * @param center 获取事件总线信息
     */
    @Override
    protected void onEventComing(EventCenter center) {
    }

    /**
     * 刷新
     */
    public void flushData() {
        getData();
    }

    /**
     * Bundle  传递数据
     *
     * @param extras
     */
    @Override
    protected void getBundleExtras(Bundle extras) {
        type = extras.getInt("type", 1);
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
                    XLog.json(json);
                    List<RoomInfo> list = FastJsonUtil.getList(json, "list", RoomInfo.class);
                    mRoomInfoList.clear();
                    if (list != null && list.size() > 0) {
                        mRoomInfoList.addAll(list);
                        for (RoomInfo roomInfo : list) {
                            EaseGroupInfo easeGroupInfo = MyHelper.getInstance().getGroupById(roomInfo.getHuanxinGroupId());
                            easeGroupInfo.setHead(roomInfo.getRoomImg());
                            easeGroupInfo.setGroupName(roomInfo.getName());
                            easeGroupInfo.setGroupType(roomInfo.getType());
                            MyHelper.getInstance().saveGroup(easeGroupInfo);
                        }
                    }
                    mRoomAdapter.setNewData(mRoomInfoList);
                    mRoomAdapter.notifyDataSetChanged();
                }
            }


            @Override
            public void onFinsh() {
                super.onFinsh();
                if (mRefreshLayout != null) {
                    mRefreshLayout.finishRefresh();
                    mRoomAdapter.loadMoreComplete();
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
     * 加载更多
     */
    private void loadData() {
        page++;
        Map<String, Object> map = new HashMap<>();
        map.put("pageNum", page);
        map.put("pageSize", 20);
        ApiClient.requestNetHandle(getActivity(), AppConfig.getRoomList, "", map, new ResultListener() {
            /**
             * 请求成功
             *
             * @param json
             * @param msg
             */
            @Override
            public void onSuccess(String json, String msg) {
                if (json != null) {
                    XLog.json(json);
                    List<RoomInfo> list = FastJsonUtil.getList(json,"list", RoomInfo.class);
                    if (list != null && list.size() > 0) {
                        mRoomInfoList.addAll(list);
                        for (RoomInfo roomInfo : list) {
                            EaseGroupInfo easeGroupInfo = MyHelper.getInstance().getGroupById(roomInfo.getHuanxinGroupId());
                            easeGroupInfo.setHead(roomInfo.getRoomImg());
                            easeGroupInfo.setGroupName(roomInfo.getName());
                            easeGroupInfo.setGroupType(roomInfo.getType());
                            MyHelper.getInstance().saveGroup(easeGroupInfo);
                        }
                        mRoomAdapter.notifyDataSetChanged();
                        mRoomAdapter.loadMoreComplete();
                    } else {
                        mRoomAdapter.loadMoreEnd(true);
                    }
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
                if (mRoomAdapter != null) {
                    mRoomAdapter.loadMoreFail();
                }
            }
        });
    }


    /**
     * 加入群组
     */
    private void joinRoom(final String groupId, final int type) {
        Map<String, Object> map = new HashMap<>();
        map.put("groupId", groupId);
        ApiClient.requestNetHandle(getActivity(), AppConfig.AddRoom, "正在加入房间...", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                startActivity(new Intent(getActivity(), ChatActivity.class).putExtra(EaseConstant.EXTRA_CHAT_TYPE, EaseConstant.CHATTYPE_GROUP)
                        .putExtra(EaseConstant.EXTRA_USER_ID, groupId)
                        .putExtra(Constant.ROOMTYPE, type));
            }
            @Override
            public void onFailure(String msg) {
                ToastUtil.toast(msg);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

}
