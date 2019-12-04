package com.haoniu.aixin.activity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.haoniu.aixin.http.ApiClient;
import com.haoniu.aixin.http.AppConfig;
import com.haoniu.aixin.http.ResultListener;
import com.haoniu.aixin.R;
import com.haoniu.aixin.base.BaseActivity;
import com.haoniu.aixin.base.MyApplication;
import com.haoniu.aixin.entity.EventCenter;
import com.haoniu.aixin.entity.UserInfo;
import com.zds.base.Toast.ToastUtil;
import com.zds.base.aes.AESCipher;
import com.zds.base.json.FastJsonUtil;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * 作   者：赵大帅
 * 描   述:
 * 邮   箱: 2510722254@qqq.com
 * 日   期: 2017/11/17 16:49
 * 更新日期: 2017/11/17
 */
public class ToChangePhoneActivity extends BaseActivity {
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
    @BindView(R.id.et_phone)
    EditText mEtPhone;
    @BindView(R.id.tv_getCode)
    TextView mTvGetCode;
    @BindView(R.id.et_yzm)
    EditText mEtYzm;
    @BindView(R.id.tv_change_phone)
    TextView mTvChangePhone;
    @BindView(R.id.tv_message)
    TextView mTvMessage;

    private CountDownTimer timer;

    /**
     * 替代onCreate的使用
     *
     * @param bundle
     */
    @Override
    protected void initContentView(Bundle bundle) {
        setContentView(R.layout.activity_to_change_phone);
    }

    /**
     * 初始化逻辑
     */
    @Override
    protected void initLogic() {
        setTitle("更换手机号");
        mTvMessage.setText("您当前的手机号为 " + MyApplication.getInstance().getUserInfo().getPhone());
        countDown();
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @OnClick({R.id.tv_getCode, R.id.tv_change_phone})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_getCode:
                getCode();
                break;
            case R.id.tv_change_phone:
                toChangePhone();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
        }

    }

    /**
     * 修改手机号
     */
    private void toChangePhone() {
        final String phone = mEtPhone.getText().toString().trim();
        String code = mEtYzm.getText().toString().trim();
        if (TextUtils.isEmpty(phone)) {
            toast("手机号不能为空");
            mEtPhone.requestFocus();
            return;
        } else if (TextUtils.isEmpty(code)) {
            toast("验证码不能为空");
            mEtYzm.requestFocus();
            return;
        }
        Map<String, Object> map = new HashMap<>();
        map.put("phone", phone);
        map.put("phoneCode", code);
        ApiClient.requestNetHandle(ToChangePhoneActivity.this, AppConfig.upPhone, "正在提交...", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                if (json != null) {
                    toast(msg);
                    UserInfo userInfo = MyApplication.getInstance().getUserInfo();
                    userInfo.setPhone(phone);
                    MyApplication.getInstance().saveUserInfo(userInfo);
                    finish();
                }
            }

            @Override
            public void onFailure(String msg) {
                ToastUtil.toast(msg);
            }
        });
    }

    private void countDown() {
        timer = new CountDownTimer(60 * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTvGetCode.setText(millisUntilFinished / 1000 + "s后重新获取");
                mTvGetCode.setEnabled(false);
                mTvGetCode.setBackgroundResource(R.drawable.shap_gray_5);
            }

            @Override
            public void onFinish() {
                mTvGetCode.setText("获取验证码");
                mTvGetCode.setBackgroundResource(R.drawable.shap_red_5);
                mTvGetCode.setEnabled(true);
            }
        };
    }

    /**
     * 获取验证码
     */
    private void getCode() {
        final String phone = mEtPhone.getText().toString().trim();
        if (TextUtils.isEmpty(phone)) {
            toast("手机号不能为空");
            mEtPhone.requestFocus();
            return;
        }
        Map<String, Object> map = new HashMap<>();
        map.put("type", "1");
        map.put("phone", phone);
        Map<String, Object> map2 = new HashMap<>();
        map2.put("sendPhoneCode",AESCipher.encrypt(FastJsonUtil.toJSONString(map)));
        ApiClient.requestNetHandle(ToChangePhoneActivity.this, AppConfig.getPhoneCodeUrl, "获取验证码...", map2, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                if (json != null) {
                    //  toast(json);
                    toast(msg);
                    timer.start();
                }
            }

            @Override
            public void onFailure(String msg) {
                ToastUtil.toast(msg);
            }
        });
    }
}
