package com.haoniu.aixin.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.haoniu.aixin.http.ApiClient;
import com.haoniu.aixin.http.AppConfig;
import com.haoniu.aixin.http.ResultListener;
import com.haoniu.aixin.R;
import com.haoniu.aixin.adapter.RoomAdapter;
import com.haoniu.aixin.base.BaseActivity;
import com.haoniu.aixin.base.Constant;
import com.haoniu.aixin.base.EaseConstant;
import com.haoniu.aixin.entity.EventCenter;
import com.haoniu.aixin.entity.RoomInfo;
import com.haoniu.aixin.widget.CommonDialog;
import com.zds.base.json.FastJsonUtil;
import com.zds.base.util.Preference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 作   者：赵大帅
 * 描   述:
 * 邮   箱: 2510722254@qqq.com
 * 日   期: 2017/12/30 10:10
 * 更新日期: 2017/12/30
 *
 * @author 赵大帅
 */
public class SearchRoomActivity extends BaseActivity {
    @BindView(R.id.bar)
    View mBar;
    @BindView(R.id.et_main_search)
    EditText mEtMainSearch;
    @BindView(R.id.tv_message)
    TextView mTvMessage;
    @BindView(R.id.recycleview)
    RecyclerView mRecycleview;
    @BindView(R.id.tv_cell)
    TextView mTvCell;


    private RoomAdapter mRoomAdapter;
    private List<RoomInfo> mList;

    /**
     * 替代onCreate的使用
     *
     * @param bundle
     */
    @Override
    protected void initContentView(Bundle bundle) {
        setContentView(R.layout.activity_search);
    }

    /**
     * 初始化逻辑
     */
    @Override
    protected void initLogic() {
        mList = new ArrayList<>();
        mRecycleview.setLayoutManager(new LinearLayoutManager(this));
        mRoomAdapter = new RoomAdapter(mList);
        mRecycleview.setAdapter(mRoomAdapter);
        mRoomAdapter.openLoadAnimation();
        mEtMainSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    search();
                    return false;
                }
                return false;
            }
        });
        mRoomAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
//                EMGroup group = EMClient.getInstance().groupManager().getGroup(mList.get(position).getHuanxinGroupId());
//                if (group == null) {
//                    joinRoomPassword(mList.get(position).getPassword() != null && !mList.get(position).getPassword().equals(""), mList.get(position).getHuanxinGroupId(), mList.get(position).getType());
//                } else {
//                    startActivity(new Intent(SearchRoomActivity.this, ChatActivity.class).putExtra(EaseConstant.EXTRA_CHAT_TYPE, EaseConstant.CHATTYPE_GROUP)
//                            .putExtra(EaseConstant.EXTRA_USER_ID, mList.get(position).getHuanxinGroupId())
//                            .putExtra(Constant.ROOMTYPE, mList.get(position).getType()));
//                }
            }
        });
        if (!Preference.getStringPreferences(this, "roomId", "").equals("")) {
            mEtMainSearch.setText(Preference.getStringPreferences(this, "roomId", ""));
            search();
            Preference.saveStringPreferences(this, "roomId", "");
        }

    }

    /**
     * 搜索
     */
    private void search() {
        if (mEtMainSearch.getText().toString() == null || mEtMainSearch.getText().toString().equals("")) {
            toast("房间号不能为空");
            return;
        }
        Map<String, Object> map = new HashMap<>();
        map.put("roomId", mEtMainSearch.getText().toString());
        ApiClient.requestNetHandle(this, AppConfig.search, "加载中", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                if (json != null) {
                    RoomInfo roomInfo = FastJsonUtil.getObject(json, RoomInfo.class);
                    mList.clear();
                    mTvMessage.setVisibility(View.VISIBLE);
                    mRecycleview.setVisibility(View.GONE);
                    if (roomInfo != null) {
                        mList.add(roomInfo);
                        mTvMessage.setVisibility(View.GONE);
                        mRecycleview.setVisibility(View.VISIBLE);
                    } else {
                        mTvMessage.setText("未搜索到房间");
                    }
                    mRoomAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFinsh() {
                super.onFinsh();
                mRoomAdapter.setEnableLoadMore(true);
                //  hideSoftInput();
                mEtMainSearch.clearFocus();
            }

            @Override
            public void onFailure(String msg) {
                mList.clear();
                mRoomAdapter.notifyDataSetChanged();
                mTvCell.setVisibility(View.VISIBLE);
                mRecycleview.setVisibility(View.GONE);
                mTvMessage.setText("未搜索到房间");
                toast(msg);
            }
        });
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
     * Bundle  传递数据
     *
     * @param extras
     */
    @Override
    protected void getBundleExtras(Bundle extras) {

    }

    CommonDialog commonDialog;
    EditText password;

    /**
     * 加入房间 （密码窗）
     */
    private void joinRoomPassword(boolean isHavaPassword, final String groupId, final String type) {
        if (isHavaPassword) {
            if (commonDialog != null && commonDialog.isShowing()) {
                commonDialog.dismiss();
            }
            commonDialog = new CommonDialog.Builder(SearchRoomActivity.this).setView(R.layout.dialog_password).center().loadAniamtion()
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

    /**
     * 加入群组
     */
    private void joinRoom(String password, final String groupId, final String type) {
        Map<String, Object> map = new HashMap<>();
        map.put("groupId", groupId);
//        map.put("password", password);
        ApiClient.requestNetHandle(SearchRoomActivity.this, AppConfig.AddRoom, "正在加入房间...", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                startActivity(new Intent(SearchRoomActivity.this, ChatActivity.class).putExtra(EaseConstant.EXTRA_CHAT_TYPE, EaseConstant.CHATTYPE_GROUP)
                        .putExtra(EaseConstant.EXTRA_USER_ID, groupId)
                        .putExtra(Constant.ROOMTYPE, type));
                if (commonDialog != null && commonDialog.isShowing()) {
                    commonDialog.dismiss();
                }
            }

            @Override
            public void onFailure(String msg) {
                toast(msg);
            }
        });
    }

    @OnClick(R.id.tv_cell)
    public void onViewClicked() {
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }
}
