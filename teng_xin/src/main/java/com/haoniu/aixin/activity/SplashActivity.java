package com.haoniu.aixin.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.haoniu.aixin.R;
import com.haoniu.aixin.base.BaseActivity;
import com.haoniu.aixin.base.MyApplication;
import com.haoniu.aixin.base.MyHelper;
import com.haoniu.aixin.base.Storage;
import com.haoniu.aixin.entity.EventCenter;
import com.haoniu.aixin.entity.UserInfo;

import com.haoniu.aixin.http.ApiClient;
import com.haoniu.aixin.http.AppConfig;
import com.haoniu.aixin.http.ResultListener;
import com.hyphenate.chat.EMClient;
import com.zds.base.json.FastJsonUtil;
import com.zds.base.util.SystemUtil;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 作   者：赵大帅
 * 描   述: 启动页
 * 日   期: 2017/11/13 9:57
 * 更新日期: 2017/11/13
 *
 * @author Administrator
 */
public class SplashActivity extends BaseActivity {

    private static final int sleepTime = 2000;
    @BindView(R.id.img_logo)
    ImageView mImgLogo;
    @BindView(R.id.splash_root)
    RelativeLayout mSplashRoot;

    @Override
    protected void initContentView(Bundle bundle) {
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);
    }

    /**
     * 初始化逻辑
     */
    @Override
    protected void initLogic() {
        MyHelper.getInstance().initHandler(this.getMainLooper());
        setTransparencyBar();
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

    long start;

    @Override
    protected void onStart() {
        super.onStart();
        toApp();
    }


    private void toApp() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (MyHelper.getInstance().isLoggedIn() && MyApplication.getInstance().checkUser()) {
                    start = System.currentTimeMillis();
                    EMClient.getInstance().chatManager().loadAllConversations();
                    EMClient.getInstance().groupManager().loadAllGroups();
                    yzLogin();
                } else {
                    handler.sendEmptyMessageDelayed(0, sleepTime);

                }
            }
        }).start();
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            EMClient.getInstance().logout(true);
            startActivity(LoginActivity.class);
            finish();
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void yzLogin() {
        final UserInfo userInfos = MyApplication.getInstance().getUserInfo();
        Map<String, Object> map = new HashMap<>();
        map.put("phone", userInfos.getPhone());
        map.put("pwd", userInfos.getMyPassword());
        map.put("equipmentId", SystemUtil.getAndroidID());
        ApiClient.requestNetHandle(SplashActivity.this, AppConfig.toLoginUrl, "", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                if (json != null) {
                    UserInfo userInfo = FastJsonUtil.getObject(json, UserInfo.class);
                    userInfo.setMyPassword(userInfos.getMyPassword());
                    Storage.saveToken(userInfo.getToken());
                    if (userInfo != null) {
                        MyApplication.getInstance().saveUserInfo(userInfo);
                        //wait
                        startActivity(MainActivity.class);
                        finish();
                    } else {
                        startActivity(LoginActivity.class);
                        finish();
                    }
                }
            }

            @Override
            public void onFailure(String msg) {
                dismissLoading();
                EMClient.getInstance().logout(true);
                MyApplication.getInstance().cleanUserInfo();
                startActivity(LoginActivity.class);
                finish();
            }
        });
    }
}
