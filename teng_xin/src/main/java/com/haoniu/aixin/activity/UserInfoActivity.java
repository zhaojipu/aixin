package com.haoniu.aixin.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.haoniu.aixin.http.ApiClient;
import com.haoniu.aixin.http.AppConfig;
import com.haoniu.aixin.http.ResultListener;
import com.haoniu.aixin.R;
import com.haoniu.aixin.base.BaseActivity;
import com.haoniu.aixin.base.MyApplication;
import com.haoniu.aixin.entity.EventCenter;
import com.haoniu.aixin.widget.EaseAlertDialog;
import com.zds.base.code.activity.ScanResultInfo;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UserInfoActivity extends BaseActivity {


    ScanResultInfo username;

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
    @BindView(R.id.my_head)
    ImageView mMyHead;
    @BindView(R.id.tv_username)
    TextView mTvUsername;
    @BindView(R.id.tv_nick)
    TextView mTvNick;
    @BindView(R.id.rel_userinfo)
    RelativeLayout mRelUserinfo;
    @BindView(R.id.tv_phone)
    TextView mTvPhone;
    @BindView(R.id.rel_background)
    RelativeLayout mRelBackground;
    @BindView(R.id.tv_to_firend)
    TextView mTvToFirend;
    @BindView(R.id.tv_to_chat)
    TextView mTvToChat;

    /**
     * 替代onCreate的使用
     *
     * @param bundle
     */
    @Override
    protected void initContentView(Bundle bundle) {
        setContentView(R.layout.activity_user_info);
    }

    /**
     * 初始化逻辑
     */
    @Override
    protected void initLogic() {
        setTitle("详细资料");
        if (username==null){
            mTvToFirend.setVisibility(View.VISIBLE);
            mTvToChat.setVisibility(View.GONE);
            return;

        }
        mTvNick.setText("爱聊号："+username.getPhone());
        mTvUsername.setText(username.getName());
        mTvToFirend.setVisibility(View.VISIBLE);
        mTvToChat.setVisibility(View.GONE);


        if (username != null) {
            if (username.getPhone().equals(MyApplication.getInstance().getUserInfo().getPhone())) {
                mTvToFirend.setVisibility(View.GONE);
                mTvToChat.setVisibility(View.GONE);
            }
        }
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
        username = (ScanResultInfo) extras.getSerializable("username");
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @OnClick({R.id.tv_to_firend, R.id.tv_to_chat})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_to_firend:
                 addContact();
                break;
            case R.id.tv_to_chat:
                // startActivity(new Intent(UserInfoActivity.this, ChatActivity.class).putExtra("userId", username));
                break;
            default:
        }
    }

    /**
     * add contact
     */
    public void addContact() {
        if (username == null) {
            toast("获取用户信息失败");
            return;
        }
        if (username.getPhone().equals(MyApplication.getInstance().getUserInfo().getPhone())){
            new EaseAlertDialog(this, R.string.not_add_myself).show();
            return;
        }
        addFriend();
    }
    /**
     * 加好友
     */
    public void  addFriend(){
        Map<String,Object> map =new HashMap<>();
        map.put("phone",username.getPhone());
        ApiClient.requestNetHandle(this, AppConfig.addFriendUrl, "", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                toast(msg);
            }

            @Override
            public void onFailure(String msg) {
                toast(msg);
            }
        });
    }



}
