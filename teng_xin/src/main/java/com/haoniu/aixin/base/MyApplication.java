/**
 * Copyright (C) 2016 Hyphenate Inc. All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.haoniu.aixin.base;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.multidex.MultiDex;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.haoniu.aixin.R;
import com.haoniu.aixin.activity.LoginActivity;
import com.haoniu.aixin.activity.UserInfoActivity;
import com.haoniu.aixin.domain.EaseAvatarOptions;
import com.haoniu.aixin.domain.EaseUser;
import com.haoniu.aixin.entity.EventCenter;
import com.haoniu.aixin.entity.UserInfo;
import com.haoniu.aixin.entity.VersionInfo;
import com.haoniu.aixin.http.ApiClient;
import com.haoniu.aixin.http.AppConfig;
import com.haoniu.aixin.http.ResultListener;
import com.haoniu.aixin.utils.CretinAutoUpdateUtils;
import com.haoniu.aixin.utils.EaseUserUtils;
import com.haoniu.aixin.utils.EventUtil;
import com.haoniu.aixin.widget.CommonDialog;
import com.haoniu.aixin.widget.EaseImageView;
import com.hyphenate.chat.EMMessage;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.DefaultRefreshFooterCreater;
import com.scwang.smartrefresh.layout.api.DefaultRefreshHeaderCreater;
import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.zds.base.SelfAppContext;
import com.zds.base.Toast.ToastUtil;
import com.zds.base.json.FastJsonUtil;
import com.zds.base.util.StringUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyApplication extends SelfAppContext {

    public static Context applicationContext;
    private static MyApplication instance;

    @Override
    public void onCreate() {
        MultiDex.install(this);
        super.onCreate();
        applicationContext = this;
        instance = this;
        MyHelper.getInstance().init(applicationContext);
        //设置全局的Header构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreater(new DefaultRefreshHeaderCreater() {
            @NonNull
            @Override
            public RefreshHeader createRefreshHeader(Context context, RefreshLayout layout) {
                ClassicsHeader header = new ClassicsHeader(context).setSpinnerStyle(SpinnerStyle.Scale);
                header.setPrimaryColors(ContextCompat.getColor(context, R.color.white), ContextCompat.getColor(context, R.color.colorPrimary));
                return header;//指定为经典Header，默认是 贝塞尔雷达Header
            }
        });
        //设置全局的Footer构建器
        SmartRefreshLayout.setDefaultRefreshFooterCreater(new DefaultRefreshFooterCreater() {
            @NonNull
            @Override
            public RefreshFooter createRefreshFooter(Context context, RefreshLayout layout) {
                layout.setEnableLoadmoreWhenContentNotFull(true);
                ClassicsFooter footer = new ClassicsFooter(context);
                footer.setBackgroundResource(android.R.color.white);
                footer.setSpinnerStyle(SpinnerStyle.Scale);//设置为拉伸模式
                return footer;//指定为经典Footer，默认是 BallPulseFooter
            }
        });
        registerWx();
        CretinAutoUpdateUtils.Builder builder = new CretinAutoUpdateUtils.Builder()
                //设置更新api
                .setBaseUrl(AppConfig.checkVersion)
                //设置是否显示忽略此版本
                .setIgnoreThisVersion(true)
                //设置下载显示形式 对话框或者通知栏显示 二选一
                .setShowType(CretinAutoUpdateUtils.Builder.TYPE_DIALOG_WITH_PROGRESS)
                //设置下载时展示的图标
                .setIconRes(R.mipmap.ic_launcher)
                //设置是否打印log日志
                .showLog(true)
                //设置请求方式
                .setRequestMethod(CretinAutoUpdateUtils.Builder.METHOD_GET)
                //设置下载时展示的应用名称
                .setAppName(getResources().getString(R.string.app_name))
                //设置自定义的Model类
                .setTransition(new VersionInfo())
                .build();
        CretinAutoUpdateUtils.init(builder);
        setUserInfoClass(UserInfoActivity.class);
    }

    private IWXAPI mIWXAPI;

    public IWXAPI registerWx() {
        mIWXAPI = WXAPIFactory.createWXAPI(this, Constant.WXAPPID, true);
        mIWXAPI.registerApp(Constant.WXAPPID);
        return mIWXAPI;
    }

    public static MyApplication getInstance() {
        return instance;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }



    private CommonDialog commonDialog;

    /**
     * 分享
     */
    public void shareDialog(Context context, final View.OnClickListener onClickListener) {
        //分享弹窗
        if (commonDialog != null && commonDialog.isShowing()) {
            commonDialog.dismiss();
        }
        commonDialog = new CommonDialog.Builder(context).setView(R.layout.share_dialog).fromBottom().fullWidth().loadAniamtion()
                .setOnClickListener(R.id.wx_chat, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onClickListener.onClick(view);
                        if (commonDialog != null && commonDialog.isShowing()) {
                            commonDialog.dismiss();
                        }
                    }
                })
                .setOnClickListener(R.id.wx_qun, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onClickListener.onClick(view);
                        if (commonDialog != null && commonDialog.isShowing()) {
                            commonDialog.dismiss();
                        }
                    }
                }).setOnClickListener(R.id.tv_cell, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (commonDialog != null && commonDialog.isShowing()) {
                            commonDialog.dismiss();
                        }
                    }
                }).create();
        commonDialog.show();
    }

    /**
     * 更新用户信息
     */
    public void UpUserInfo() {
        Map<String, Object> map = new HashMap<>();
        ApiClient.requestNetHandle(this, AppConfig.UserInfo, "", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                if (json != null) {
                    UserInfo userInfo = FastJsonUtil.getObject(json, UserInfo.class);
                    if (userInfo != null) {
                      userInfo.setMyPassword(getUserInfo().getMyPassword());
                        saveUserInfo(userInfo);
                        EventBus.getDefault().post(new EventCenter(EventUtil.FLUSHUSERINFO));
                    }
                }
            }

            @Override
            public void onFailure(String msg) {
                //ToastUtil.toast(msg);
            }
        });
    }
    /**
     * 退出房间
     */
    public void layoutRoom(String groupId) {
        Map<String, Object> map = new HashMap<>();
        map.put("groupId",groupId);
        ApiClient.requestNetHandle(this, AppConfig.layoutRoom, "", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {

            }

            @Override
            public void onFailure(String msg) {
                //ToastUtil.toast(msg);
            }
        });
    }

    /**
     * 更新收入
     */
    public void UpIncome() {
        Map<String, Object> map = new HashMap<>();
        ApiClient.requestNetHandleByGet(this, AppConfig.UserInfo, "", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                if (json != null) {
                    UserInfo userInfo = FastJsonUtil.getObject(json, UserInfo.class);
                    if (userInfo != null) {
                        userInfo.setMyPassword(getUserInfo().getMyPassword());
                        saveUserInfo(userInfo);
                        EventBus.getDefault().post(new EventCenter(EventUtil.FLUSHUSERINFO));
                    }
                }
            }

            @Override
            public void onFailure(String msg) {
                ToastUtil.toast(msg);
            }
        });
    }

    /**
     * 设置头像形状
     *
     * @param userAvatarView
     */
    public void setAvatar(EaseImageView userAvatarView) {
        EaseAvatarOptions avatarOptions = EaseUI.getInstance().getAvatarOptions();
        if (avatarOptions != null && userAvatarView instanceof EaseImageView) {
            EaseImageView avatarView = ((EaseImageView) userAvatarView);
            if (avatarOptions.getAvatarShape() != 0) {
                avatarView.setShapeType(avatarOptions.getAvatarShape());
            }
            if (avatarOptions.getAvatarBorderWidth() != 0) {
                avatarView.setBorderWidth(avatarOptions.getAvatarBorderWidth());
            }
            if (avatarOptions.getAvatarBorderColor() != 0) {
                avatarView.setBorderColor(avatarOptions.getAvatarBorderColor());
            }
            if (avatarOptions.getAvatarRadius() != 0) {
                avatarView.setRadius(avatarOptions.getAvatarRadius());
            }
        }
    }
    /**
     * 设置头像形状
     *
     * @param userAvatarView
     */
    public void setAvatars(EaseImageView userAvatarView) {
        EaseAvatarOptions avatarOptions = EaseUI.getInstance().getAvatarOptions();
        if (avatarOptions != null && userAvatarView instanceof EaseImageView) {
            EaseImageView avatarView = ((EaseImageView) userAvatarView);

                avatarView.setRadius(2);
        }
    }

    /**
     * 更新头像昵称
     *
     * @param message
     */
    public void saveUserHeadNick(EMMessage message) {
        EaseUser easeUser = MyHelper.getInstance().getUserById(message.getFrom());
        String avatarurl = message.getStringAttribute(Constant.AVATARURL, "");
        String nickname = message.getStringAttribute(Constant.NICKNAME, "");
        easeUser.setAvatar(avatarurl);
        easeUser.setNickname(nickname);
        MyHelper.getInstance().saveUser(easeUser);
    }

    /**
     * 更新头像昵称
     *
     * @param messages
     */
    public void saveUserHeadNick(List<EMMessage> messages) {
        for (EMMessage emMessage : messages) {
            EaseUser easeUser = MyHelper.getInstance().getUserById(emMessage.getFrom());
            String avatarurl = emMessage.getStringAttribute(Constant.AVATARURL, "");
            String nickname = emMessage.getStringAttribute(Constant.NICKNAME, "");
            easeUser.setAvatar(avatarurl);
            easeUser.setNickname(nickname);
            MyHelper.getInstance().saveUser(easeUser);
            if (emMessage.getStringAttribute(Constant.MSGTYPE, "").equals(Constant.RENAME)) {
                EventBus.getDefault().post(new EventCenter<String>(EventUtil.FLUSHRENAME, emMessage.getStringAttribute("id", "")));
            }
        }
    }



    /**
     * 获取软件版本名称
     *
     * @return
     */
    public String getVersionName() {
        return getPackageInfo().versionName;
    }

    /**
     * 获取软件版本号
     *
     * @return
     */
    public int getVersionCode() {
        return getPackageInfo().versionCode;
    }

    /**
     * 获取App安装包信息
     *
     * @return
     */
    public PackageInfo getPackageInfo() {
        PackageInfo info = null;
        try {
            info = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace(System.err);
        }
        if (info == null) {
            info = new PackageInfo();
        }
        return info;
    }


    /**
     * 同步更新用户好友信息
     */
    public void asyContactsFromServer(){
        Map<String,Object> map =new HashMap<>();
        ApiClient.requestNetHandle(this, AppConfig.friendList, "", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                List<UserInfo> list =FastJsonUtil.getList(json,UserInfo.class);
                List<EaseUser> easeUserList =new ArrayList<>();
                if (list!=null&&list.size()>0){
                    for (UserInfo userInfo:list){
                        EaseUser easeUser = EaseUserUtils.getUserInfo(userInfo.getIdhs());
                        easeUser.setAvatar(userInfo.getHeadImg());
                        easeUser.setNickname(userInfo.getNickName());
                        MyHelper.getInstance().saveUser(easeUser);
                        easeUserList.add(easeUser);
                    }
                }
                MyHelper.getInstance().updateContactList(easeUserList);
                EventBus.getDefault().post(new EventCenter(EventUtil.FLUSHFRIEND));
            }

            @Override
            public void onFailure(String msg) {

            }
        });

    }


    /**
     * 检测网络是否可用
     *
     * @return
     */
    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni != null && ni.isConnectedOrConnecting();
    }

    /**
     * 获取缓存用户信息
     *
     * @return
     */
    public UserInfo getUserInfo() {
        return Storage.GetUserInfo() == null ? new UserInfo() : Storage
                .GetUserInfo();
    }

    /**
     * 获取token
     */
    public String getToken() {
        UserInfo userInfo = getUserInfo();
        if (userInfo == null) {
            return "";
        } else {
            return userInfo.getToken();
        }
    }


    /**
     * 保存缓存用户信息
     *
     * @param user
     */
    public void saveUserInfo(final UserInfo user) {
        if (user != null) {
            Storage.ClearUserInfo();
            Storage.saveUsersInfo(user);
        }
    }

    /**
     * 用户存在是ture 否则是false
     *
     * @return
     */
    public boolean checkUser() {
        if (StringUtil.isEmpty(getUserInfo().getPhone())) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 用户存在是ture 否则是false
     *
     * @return
     */
    public boolean checkUserToLogin(Context context) {
        if (StringUtil.isEmpty(getUserInfo().getPhone())) {
            Intent intent = new Intent(context, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            return false;
        } else {
            return true;
        }
    }

    /**
     * 清除缓存用户信息
     *
     * @param
     */
    public void cleanUserInfo() {
        Storage.ClearUserInfo();
    }
}
