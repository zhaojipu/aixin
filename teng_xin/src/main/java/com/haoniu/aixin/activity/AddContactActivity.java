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

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.haoniu.aixin.http.ApiClient;
import com.haoniu.aixin.http.AppConfig;
import com.haoniu.aixin.http.ResultListener;
import com.haoniu.aixin.R;
import com.haoniu.aixin.base.BaseActivity;
import com.haoniu.aixin.base.MyHelper;
import com.haoniu.aixin.domain.EaseUser;
import com.haoniu.aixin.entity.EaseUserInfo;
import com.haoniu.aixin.entity.EventCenter;
import com.haoniu.aixin.widget.CommonDialog;
import com.haoniu.aixin.widget.EaseAlertDialog;
import com.haoniu.aixin.widget.EaseImageView;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;
import com.zds.base.ImageLoad.GlideUtils;
import com.zds.base.Toast.ToastUtil;
import com.zds.base.json.FastJsonUtil;
import com.zds.base.util.StringUtil;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

public class AddContactActivity extends BaseActivity {
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
    @BindView(R.id.edit_note)
    EditText mEditNote;
    @BindView(R.id.avatar)
    EaseImageView mAvatar;
    @BindView(R.id.name)
    TextView mName;
    @BindView(R.id.indicator)
    Button mIndicator;
    @BindView(R.id.ll_user)
    RelativeLayout mLlUser;
    private String username;


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
        setContentView(R.layout.em_activity_add_contact);
    }

    /**
     * 初始化逻辑
     */
    @Override
    protected void initLogic() {
        setTitle("添加好友");
        mToolbarSubtitle.setText("查找");
        mToolbarSubtitle.setVisibility(View.VISIBLE);
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

    /**
     * 查询用户
     */
    private void getUserInfo() {
        if (StringUtil.isEmpty(mEditNote.getText().toString())) {
            return;
        }
        Map<String, Object> map = new HashMap<>();
        map.put("phone", mEditNote.getText().toString());
        ApiClient.requestNetHandleByGet(this, AppConfig.getUserByPhone, "", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                // XLog.json(json);
                EaseUserInfo easeUserInfos = FastJsonUtil.getObject(json, EaseUserInfo.class);
                if (easeUserInfos != null) {
                    EaseUser easeUser = new EaseUser(easeUserInfos.getId() + "");
                    easeUser.setAvatar(easeUserInfos.getAvatarUrl());
                    easeUser.setNickname(easeUserInfos.getNickname());
                    MyHelper.getInstance().saveUser(easeUser);
                    username = easeUser.getUsername();
                    mName.setText(easeUser.getNick());
                    GlideUtils.loadImageViewLoding(AppConfig.checkimg(easeUser.getAvatar()), mAvatar, R.mipmap.img_default_avatar);
                    mLlUser.setVisibility(View.VISIBLE);
                } else {
                    mLlUser.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(String msg) {
                ToastUtil.toast(msg);
                mLlUser.setVisibility(View.GONE);
            }
        });


    }

    /**
     * add contact
     */
    public void addContact() {
        if (username == null) {
            toast("获取用户信息失败");
            return;
        }
        if (EMClient.getInstance().getCurrentUser().equals(username)) {
            new EaseAlertDialog(this, R.string.not_add_myself).show();
            return;
        }

        if (MyHelper.getInstance().getContactList().containsKey(username)) {
            new EaseAlertDialog(this, R.string.This_user_is_already_your_friend).show();
            return;
        }
        if (commonDialog != null && commonDialog.isShowing()) {
            commonDialog.dismiss();
        }
        commonDialog = new CommonDialog.Builder(this).setView(R.layout.add_friend_dialog).setOnClickListener(R.id.btn_ok, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                commonDialog.dismiss();
                if (mEditText != null && mEditText.getText().toString() != null && !"".equals(mEditText.getText().toString())) {
                    addFriend(username, mEditText.getText().toString());
                } else {
                    addFriend(username, "加个好友呗！");
                }

            }
        }).setOnClickListener(R.id.btn_cancel, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                commonDialog.dismiss();
            }
        }).setText(R.id.title, "说点啥子吧").create();
        commonDialog.getView(R.id.btn_cancel).setVisibility(View.VISIBLE);
        mEditText = (EditText) commonDialog.getView(R.id.et_message);
        commonDialog.show();
    }

    EditText mEditText;
    CommonDialog commonDialog;

    private void addFriend(final String username, final String reason) {
        showLoading("正在申请添加好友...");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    EMClient.getInstance().contactManager().addContact(username, reason);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            toast("成功发送申请");
                        }
                    });

                } catch (final HyphenateException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            toast(e.getMessage());
                        }
                    });

                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dismissLoading();
                    }
                });
            }
        }).start();
    }

    @Override
    public void back(View v) {
        finish();
    }

    @OnClick({R.id.toolbar_subtitle, R.id.indicator})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.toolbar_subtitle:
                getUserInfo();
                break;
            case R.id.indicator:
                addContact();
                break;
        }
    }
}
