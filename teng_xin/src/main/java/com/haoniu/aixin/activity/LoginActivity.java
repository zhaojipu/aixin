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
package com.haoniu.aixin.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.haoniu.aixin.R;
import com.haoniu.aixin.base.BaseActivity;
import com.haoniu.aixin.base.MyApplication;
import com.haoniu.aixin.base.MyHelper;
import com.haoniu.aixin.base.Storage;
import com.haoniu.aixin.db.DemoDBManager;
import com.haoniu.aixin.entity.EventCenter;
import com.haoniu.aixin.entity.UserInfo;
import com.haoniu.aixin.http.ApiClient;
import com.haoniu.aixin.http.AppConfig;
import com.haoniu.aixin.http.ResultListener;
import com.haoniu.aixin.utils.CretinAutoUpdateUtils;
import com.haoniu.aixin.utils.EaseCommonUtils;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.zds.base.Toast.ToastUtil;
import com.zds.base.util.SystemUtil;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 作   者：赵大帅
 * 描   述: 登录
 * 日   期: 2017/11/13 11:46
 * 更新日期: 2017/11/13
 *
 * @author Administrator
 */
public class LoginActivity extends BaseActivity {


    @BindView(R.id.et_phone)
    EditText etPhone;
    @BindView(R.id.et_pwd)
    EditText etPwd;
    @BindView(R.id.tvLogin)
    Button tvLogin;
    @BindView(R.id.tv_submit)
    TextView tvSubmit;
    @BindView(R.id.tv_update_pwd)
    TextView tvUpdatePwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * 替代onCreate的使用
     *
     * @param bundle
     */
    @Override
    protected void initContentView(Bundle bundle) {
        setContentView(R.layout.activity_login);
    }

    /**
     * 初始化逻辑
     */
    @Override
    protected void initLogic() {
        setTransparencyBar();
        setIsDark(true);

        etPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                etPwd.setText(null);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        if (MyHelper.getInstance().getCurrentUsernName() != null) {
            etPhone.setText(MyHelper.getInstance().getCurrentUsernName());
        }
        AppConfig.checkVersion(this, true);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CretinAutoUpdateUtils.getInstance(this).destroy();
    }

    /**
     * 用户登录
     */
    public void login() {
        if (!EaseCommonUtils.isNetWorkConnected(this)) {
            Toast.makeText(this, R.string.network_isnot_available, Toast.LENGTH_SHORT).show();
            return;
        }
        String currentUsername = etPhone.getText().toString().trim();
        final String currentPassword = etPwd.getText().toString().trim();

        if (TextUtils.isEmpty(currentUsername)) {
            ToastUtil.toast(getString(R.string.User_name_cannot_be_empty));
            return;
        }
        if (TextUtils.isEmpty(currentPassword)) {
            ToastUtil.toast(getString(R.string.Password_cannot_be_empty));
            Toast.makeText(this, R.string.Password_cannot_be_empty, Toast.LENGTH_SHORT).show();
            return;
        }
        showLoading(getString(R.string.Is_landing));
        DemoDBManager.getInstance().closeDB();
        MyHelper.getInstance().setCurrentUserName(currentUsername);
        Map<String, Object> map = new HashMap<>();
        map.put("phone", currentUsername);
        map.put("pwd", currentPassword);
        map.put("equipmentId", SystemUtil.getAndroidID());
        ApiClient.requestNetHandle(LoginActivity.this, AppConfig.toLoginUrl, "", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                if (json != null) {
                    UserInfo userInfo = JSON.parseObject(json, UserInfo.class);
                    userInfo.setMyPassword(currentPassword);
                    Storage.saveToken(userInfo.getToken());
                    if (userInfo != null) {
                        MyApplication.getInstance().saveUserInfo(userInfo);
                        HXlogin();
                    }
                }
            }

            @Override
            public void onFailure(String msg) {
                dismissLoading();
                ToastUtil.toast(msg);
            }
        });
    }

    /**
     * 环信登录
     */
    private void HXlogin() {
        final UserInfo userInfo = MyApplication.getInstance().getUserInfo();
        EMClient.getInstance().login(userInfo.getIdh() + "", "123456", new EMCallBack() {
            @Override
            public void onSuccess() {
                // ** manually load all local groups and conversation
                EMClient.getInstance().groupManager().loadAllGroups();
                EMClient.getInstance().chatManager().loadAllConversations();
                // update current user's display name for APNs
                EMClient.getInstance().pushManager().updatePushNickname(userInfo.getNickName());
                // get user's info (this should be get from App's server or 3rd party service)
                MyHelper.getInstance().getUserProfileManager().asyncGetCurrentUserInfo();
                startActivity(MainActivity.class);
                dismissLoading();
                finish();
            }

            @Override
            public void onProgress(int progress, String status) {
            }

            @Override
            public void onError(final int code, final String message) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (code == 200) {
                            EMClient.getInstance().logout(false);
                        }
                        dismissLoading();
                        toast("登录失败" + message);
                    }
                });
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @OnClick({R.id.tv_update_pwd, R.id.tvLogin, R.id.tv_submit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_update_pwd:
                startActivity(new Intent(LoginActivity.this, UpDataPasswordActivity.class));
                break;
            case R.id.tvLogin:
                login();
                break;
            case R.id.tv_submit:
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                break;
            default:
        }
    }
}
