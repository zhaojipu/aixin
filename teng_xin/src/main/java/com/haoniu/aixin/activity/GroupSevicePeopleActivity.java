package com.haoniu.aixin.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.haoniu.aixin.R;
import com.haoniu.aixin.adapter.PeopleServiceAdapter;
import com.haoniu.aixin.base.BaseActivity;
import com.haoniu.aixin.base.Constant;
import com.haoniu.aixin.base.MyHelper;
import com.haoniu.aixin.domain.EaseUser;
import com.haoniu.aixin.entity.EventCenter;
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
 * 群主
 */
public class GroupSevicePeopleActivity extends BaseActivity {

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

    private PeopleServiceAdapter peopleServiceAdapter;
    private List<UserInfo> list =new ArrayList<>();


    @Override
    protected void initContentView(Bundle bundle) {
        setContentView(R.layout.activity_sevice_people);
    }

    @Override
    protected void initLogic() {
        setTitle("群主");
        peopleServiceAdapter =new PeopleServiceAdapter(list);
        recycleView.setLayoutManager(new LinearLayoutManager(this));
        recycleView.setAdapter(peopleServiceAdapter);
        peopleServiceAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                startActivity(new Intent(GroupSevicePeopleActivity.this, ChatActivity.class).putExtra(Constant.EXTRA_USER_ID, list.get(position).getIdh() ).putExtra(Constant.EXTRA_CHAT_TYPE, Constant.CHATTYPE_SINGLE));
            }
        });
        getData();
    }

    /**
     *  获取数据
     */
    private void getData(){
        String url=AppConfig.rengongkefu;
        Map<String,Object> map =new HashMap<>();
        ApiClient.requestNetHandle(this,url, "", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                List<UserInfo> userInfoList= FastJsonUtil.getList(json,UserInfo.class);
                if (userInfoList!=null){
                    list.addAll(userInfoList);
                    for (UserInfo userInfo:list){
                            EaseUser easeUser = new EaseUser(userInfo.getIdh() );
                            easeUser.setNickname(userInfo.getNickName());
                            easeUser.setAvatar(userInfo.getHeadImg());
                            MyHelper.getInstance().saveUser(easeUser);
                    }
                }
                peopleServiceAdapter.notifyDataSetChanged();
            }
            @Override
            public void onFailure(String msg) {
                ToastUtil.toast(msg);
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }
}
