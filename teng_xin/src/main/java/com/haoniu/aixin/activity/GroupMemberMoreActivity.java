package com.haoniu.aixin.activity;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.haoniu.aixin.adapter.RoomDeatilNewAdapter;
import com.haoniu.aixin.entity.RoomInfo;
import com.haoniu.aixin.entity.UserInfo;
import com.haoniu.aixin.http.ApiClient;
import com.haoniu.aixin.http.AppConfig;
import com.haoniu.aixin.http.ResultListener;
import com.haoniu.aixin.R;
import com.haoniu.aixin.base.BaseActivity;
import com.haoniu.aixin.entity.EventCenter;
import com.zds.base.Toast.ToastUtil;
import com.zds.base.json.FastJsonUtil;
import com.zds.base.log.XLog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 作   者：赵大帅
 * 描   述: 群成员
 * 邮   箱: 2510722254@qqq.com
 * 日   期: 2017/12/11 16:05
 * 更新日期: 2017/12/11
 */
public class GroupMemberMoreActivity extends BaseActivity {
    @BindView(R.id.bar)
    View mBar;
    @BindView(R.id.ll_back)
    LinearLayout mLlBack;
    @BindView(R.id.toolbar_subtitle)
    TextView mToolbarSubtitle;
    @BindView(R.id.img_right)
    ImageView mImgRight;
    @BindView(R.id.toolbar_title)
    TextView mToolbarTitle;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.recycle_view)
    RecyclerView mRecycleView;

    private RoomDeatilNewAdapter adapter;
    private List<UserInfo> list;
    RoomInfo roomInfo;
    private int page;


    /**
     * 替代onCreate的使用
     *
     * @param bundle
     */
    @Override
    protected void initContentView(Bundle bundle) {
        setContentView(R.layout.activity_group_member_more);
    }

    /**
     * 初始化逻辑
     */
    @Override
    protected void initLogic() {
        setTitle("群成员");
        list=new ArrayList<>();
        adapter = new RoomDeatilNewAdapter(list);
        mRecycleView.setLayoutManager(new GridLayoutManager(this, 4));
        mRecycleView.setAdapter(adapter);
        adapter.openLoadAnimation();
        adapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                mRecycleView.post(new Runnable() {
                    @Override
                    public void run() {
                        load();
                    }
                });

            }
        },mRecycleView);
        if (roomInfo.getRoomBossDTOList()!=null&&roomInfo.getRoomBossDTOList().size()>0){
            adapter.setRoomBossDTOListBean(roomInfo.getRoomBossDTOList().get(0));
        }
        mToolbarSubtitle.setVisibility(View.GONE);
        getData();

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
        roomInfo = (RoomInfo) extras.getSerializable("roomInfo");
    }

    /**
     * 获取群成员
     */
    private void getData() {
        page = 1;
        Map<String, Object> map = new HashMap<>();
        map.put("roomId", roomInfo.getId());
        map.put("pageNum", page);
        map.put("pageSize", 60);
        ApiClient.requestNetHandle(this, AppConfig.memberList, "", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                XLog.json(json);
                list.clear();
                List<UserInfo> userInfoList = FastJsonUtil.getList(json, "list", UserInfo.class);
                if (userInfoList != null) {
                    list.addAll(userInfoList);

                }
                adapter.notifyDataSetChanged();
                adapter.loadMoreComplete();
            }

            @Override
            public void onFailure(String msg) {
                ToastUtil.toast(msg);
                adapter.loadMoreFail();
            }
        });
    }
    /**
     * 获取群成员
     */
    private void load() {
        page ++;
        Map<String, Object> map = new HashMap<>();
        map.put("roomId", roomInfo.getId());
        map.put("pageNum", page);
        map.put("pageSize", 60);
        ApiClient.requestNetHandle(this, AppConfig.memberList, "", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                XLog.json(json);
                List<UserInfo> userInfoList = FastJsonUtil.getList(json, "list", UserInfo.class);
                if (userInfoList != null) {
                    list.addAll(userInfoList);

                }
                adapter.notifyDataSetChanged();
                adapter.loadMoreComplete();
            }

            @Override
            public void onFailure(String msg) {
                ToastUtil.toast(msg);
                adapter.loadMoreEnd();
            }
        });
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

}
