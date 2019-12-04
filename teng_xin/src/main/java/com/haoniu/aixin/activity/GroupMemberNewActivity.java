package com.haoniu.aixin.activity;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.haoniu.aixin.R;
import com.haoniu.aixin.adapter.RoomDeatilNewAdapter;
import com.haoniu.aixin.base.BaseActivity;
import com.haoniu.aixin.entity.EventCenter;
import com.haoniu.aixin.entity.RoomInfo;
import com.haoniu.aixin.entity.UserInfo;
import com.haoniu.aixin.http.ApiClient;
import com.haoniu.aixin.http.AppConfig;
import com.haoniu.aixin.http.ResultListener;
import com.zds.base.Toast.ToastUtil;
import com.zds.base.json.FastJsonUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * 更多成员
 */
public class GroupMemberNewActivity extends BaseActivity {
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

    RoomInfo roomInfo;
    RoomDeatilNewAdapter roomDeatilNewAdapter;
    List<UserInfo> list = new ArrayList<>();
    private int page;
    @Override
    protected void initContentView(Bundle bundle) {
        setContentView(R.layout.activity_group_member_new);
    }

    @Override
    protected void initLogic() {
        setTitle("更多成员");

        roomDeatilNewAdapter = new RoomDeatilNewAdapter(list);
        recycleView.setLayoutManager(new GridLayoutManager(this, 4));
        recycleView.setAdapter(roomDeatilNewAdapter);
        roomDeatilNewAdapter.openLoadAnimation();
        roomDeatilNewAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                loadMore();
            }
        },recycleView);
        getData();
    }
    /**
     * 获取群成员
     */
    private void getData() {
        page = 1;
        Map<String, Object> map = new HashMap<>();
        map.put("roomId", roomInfo.getId());
        map.put("pageNum", page);
        map.put("pageSize", 20);
        ApiClient.requestNetHandle(this, AppConfig.memberList, "", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                list.clear();
                List<UserInfo> userInfoList = FastJsonUtil.getList(json, "list", UserInfo.class);
                if (userInfoList != null) {
                    list.addAll(userInfoList);
                }
                roomDeatilNewAdapter.notifyDataSetChanged();
                roomDeatilNewAdapter.loadMoreComplete();
            }

            @Override
            public void onFailure(String msg) {
                ToastUtil.toast(msg);
                roomDeatilNewAdapter.loadMoreFail();
            }
        });
    }
    /**
     * 获取更多群成员
     */
    private void loadMore() {
        page ++;
        Map<String, Object> map = new HashMap<>();
        map.put("roomId", roomInfo.getId());
        map.put("pageNum", page);
        map.put("pageSize", 20);
        ApiClient.requestNetHandle(this, AppConfig.memberList, "", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                List<UserInfo> userInfoList = FastJsonUtil.getList(json, "list", UserInfo.class);
                if (userInfoList != null) {
                    list.addAll(userInfoList);
                    if (userInfoList.size()<20){
                        roomDeatilNewAdapter.loadMoreEnd();
                    }else {
                        roomDeatilNewAdapter.loadMoreComplete();
                    }
                }else {
                    roomDeatilNewAdapter.loadMoreEnd();
                }
                roomDeatilNewAdapter.notifyDataSetChanged();



            }

            @Override
            public void onFailure(String msg) {
                ToastUtil.toast(msg);
                roomDeatilNewAdapter.loadMoreFail();
            }
        });
    }
    @Override
    protected void onEventComing(EventCenter center) {

    }

    @Override
    protected void getBundleExtras(Bundle extras) {
        roomInfo= (RoomInfo) extras.getSerializable("roomInfo");

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }
}
