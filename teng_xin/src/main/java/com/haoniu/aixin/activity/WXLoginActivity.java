//package com.haoniu.aixin.activity;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.ImageButton;
//import android.widget.TextView;
//
//import com.haoniu.aixin.http.ApiClient;
//import com.haoniu.aixin.http.AppConfig;
//import com.haoniu.aixin.http.ResultListener;
//import com.haoniu.aixin.R;
//import com.haoniu.aixin.base.BaseActivity;
//import com.haoniu.aixin.base.MyApplication;
//import com.haoniu.aixin.base.MyHelper;
//import com.haoniu.aixin.base.Storage;
//import com.haoniu.aixin.db.DemoDBManager;
//import com.haoniu.aixin.entity.EventCenter;
//import com.haoniu.aixin.entity.UserInfo;
//import com.haoniu.aixin.utils.EventUtil;
//import com.hyphenate.EMCallBack;
//import com.hyphenate.chat.EMClient;
//import com.tencent.mm.opensdk.modelmsg.SendAuth;
//import com.zds.base.Toast.ToastUtil;
//import com.zds.base.json.FastJsonUtil;
//
//import java.util.HashMap;
//import java.util.Map;
//
//import butterknife.BindView;
//import butterknife.ButterKnife;
//import butterknife.OnClick;
//
///**
// * 作   者：赵大帅
// * 描   述: 微信登录
// * 邮   箱: 2510722254@qqq.com
// * 日   期: 2017/12/20 16:42
// * 更新日期: 2017/12/20
// */
//public class WXLoginActivity extends BaseActivity {
//
//    @BindView(R.id.wxlogin)
//    ImageButton mWxlogin;
//    @BindView(R.id.tv_login)
//    TextView mTvLogin;
//    private String wxCode;
//
//    /**
//     * 替代onCreate的使用
//     *
//     * @param bundle
//     */
//    @Override
//    protected void initContentView(Bundle bundle) {
//        setContentView(R.layout.activity_wx_login);
//    }
//
//    /**
//     * 初始化逻辑
//     */
//    @Override
//    protected void initLogic() {
//        if (MyHelper.getInstance().isLoggedIn() && MyApplication.getInstance().checkUser()) {
//            startActivity(MainActivity.class);
//            return;
//        } else {
//            MyHelper.getInstance().logout(true, null);
//        }
//    }
//
//    /**
//     * EventBus接收消息
//     *
//     * @param center 获取事件总线信息
//     */
//    @Override
//    protected void onEventComing(EventCenter center) {
//        if (center.getEventCode() == EventUtil.WXLOGOINSUCCESS) {
//            wxCode = center.getData().toString();
//            dismissLoading();
//            WXlogin(center.getData().toString());
//        } else if (center.getEventCode() == EventUtil.WXLOGOINERROR) {
//            dismissLoading();
//        } else if (center.getEventCode() == EventUtil.TOREGISTER) {
//            dismissLoading();
//            startActivity(new Intent(this, RegisterActivity.class).putExtra("code", wxCode));
//        } else if (center.getEventCode() == EventUtil.CLOSELOGIN) {
//            finish();
//        }
//    }
//
//    /**
//     * Bundle  传递数据
//     *
//     * @param extras
//     */
//    @Override
//    protected void getBundleExtras(Bundle extras) {
//
//    }
//
//
//    /**
//     * 吊起微信 获取授权临时票据（code）
//     */
//    private void makeWX() {
//        showLoading("正在打开微信...");
//        final SendAuth.Req req = new SendAuth.Req();
//        req.scope = "snsapi_userinfo";
//        req.state = "weixinlogin_weiliao";
//        boolean issend = MyApplication.getInstance().registerWx().sendReq(req);
//        if (!issend) {
//            toast("打开微信失败，请安装微信最新版本");
//            dismissLoading();
//        }
//    }
//
//    //微信登录
//    private void WXlogin(final String code) {
//        Map<String, Object> map = new HashMap<>();
//        map.put("code", code);
//        showLoading("正在登录...");
//        ApiClient.requestNetHandle(this, AppConfig.WXLogin, "", map, new ResultListener() {
//            @Override
//            public void onSuccess(String json, String msg) {
//                if (json != null) {
//                    UserInfo userInfo = FastJsonUtil.getObject(json, UserInfo.class);
//                    userInfo.setMypassword(code);
//                    Storage.saveToken(userInfo.getToken());
//                    if (userInfo != null) {
//                        MyApplication.getInstance().saveUserInfo(userInfo);
//                    }
//                    HXlogin();
//                }
//            }
//
//            @Override
//            public void onFailure(String msg) {
//                MyApplication.getInstance().cleanUserInfo();
//                ToastUtil.toast(msg);
//                dismissLoading();
//            }
//        });
//    }
//
//
//    /**
//     * 环信登录
//     */
//    private void HXlogin() {
//        DemoDBManager.getInstance().closeDB();
//        final UserInfo userInfo = MyApplication.getInstance().getUserInfo();
//        EMClient.getInstance().login(userInfo.getId() + "", "123456", new EMCallBack() {
//
//            @Override
//            public void onSuccess() {
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        EMClient.getInstance().groupManager().loadAllGroups();
//                        EMClient.getInstance().chatManager().loadAllConversations();
//                        // update current user's display name for APNs
//                        EMClient.getInstance().pushManager().updatePushNickname(userInfo.getNickname());
//                        // get user's info (this should be get from App's server or 3rd party service)
//                        MyHelper.getInstance().getUserProfileManager().asyncGetCurrentUserInfo();
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                startActivity(MainActivity.class);
//                                dismissLoading();
//                                finish();
//                            }
//                        });
//                    }
//                }).start();
//            }
//
//            @Override
//            public void onProgress(int progress, String status) {
//            }
//
//            @Override
//            public void onError(final int code, final String message) {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        dismissLoading();
//                        ToastUtil.toast("登录失败");
//                        if (code == 200) {
//                            EMClient.getInstance().logout(true);
//                        }
//                    }
//                });
//            }
//        });
//    }
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        // TODO: add setContentView(...) invocation
//        ButterKnife.bind(this);
//    }
//
//    @OnClick({R.id.wxlogin, R.id.tv_login})
//    public void onViewClicked(View view) {
//        switch (view.getId()) {
//            case R.id.wxlogin:
//                makeWX();
////                startActivity(RegisterActivity.class);
//                break;
//            case R.id.tv_login:
//                startActivity(LoginActivity.class);
//                finish();
//                break;
//        }
//    }
//}
