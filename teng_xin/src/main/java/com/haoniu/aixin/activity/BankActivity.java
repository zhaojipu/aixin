package com.haoniu.aixin.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.haoniu.aixin.R;
import com.haoniu.aixin.base.BaseActivity;
import com.haoniu.aixin.base.MyApplication;
import com.haoniu.aixin.entity.EventCenter;
import com.haoniu.aixin.entity.UserInfo;
import com.haoniu.aixin.http.ApiClient;
import com.haoniu.aixin.http.AppConfig;
import com.haoniu.aixin.http.ResultListener;
import com.haoniu.aixin.utils.EventUtil;
import com.zds.base.util.StringUtil;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 作   者：赵大帅
 * 描   述: 绑定银行卡
 * 邮   箱: 2510722254@qqq.com
 * 日   期: 2017/12/27 16:44
 * 更新日期: 2017/12/27
 */
public class BankActivity extends BaseActivity {


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
    @BindView(R.id.tv_bank)
    EditText mTvBank;
    @BindView(R.id.tv_bank_pay)
    EditText mTvBankPay;
    @BindView(R.id.tv_bank_pay2)
    EditText mTvBankPay2;
    @BindView(R.id.ll_qr)
    LinearLayout mLlQr;
    @BindView(R.id.tv_name)
    EditText mTvName;
    @BindView(R.id.tv_message)
    TextView mTvMessage;
    @BindView(R.id.ll_submits)
    LinearLayout mLlSubmits;
    private boolean isedit = false;

    /**
     * 替代onCreate的使用
     *
     * @param bundle
     */
    @Override
    protected void initContentView(Bundle bundle) {
        setContentView(R.layout.activity_bank);
    }

    /**
     * 初始化逻辑
     */
    @Override
    protected void initLogic() {
        setTitle("绑定银行卡");
        mToolbarSubtitle.setVisibility(View.VISIBLE);
        mToolbarSubtitle.setText("编辑");
        initUserInfo();
    }

    /**
     * EventBus接收消息
     *
     * @param center 获取事件总线信息
     */
    @Override
    protected void onEventComing(EventCenter center) {
        if (center.getEventCode() == EventUtil.FLUSHUSERINFO) {
            initUserInfo();
        }
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
     * 初始化用户支付宝信息
     */
    private void initUserInfo() {
        UserInfo userInfo = MyApplication.getInstance().getUserInfo();

//        if (userInfo.getBankNumber() == null || userInfo.getBankNumber().equals("")) {
//            isedit = true;
//            if (StringUtil.isEmpty(userInfo.getRealname())) {
//                mTvName.setFocusable(true);
//                mTvName.setFocusableInTouchMode(true);
//
//            } else {
//                mTvName.setFocusable(false);
//                mTvName.setFocusableInTouchMode(false);
//                mTvName.setText(userInfo.getRealname());
//            }
//            mTvBank.setFocusable(true);
//            mTvBank.setFocusableInTouchMode(true);
//            mTvBankPay.setFocusable(true);
//            mTvBankPay.setFocusableInTouchMode(true);
//            mTvBankPay.requestFocus();
//            mLlQr.setVisibility(View.VISIBLE);
//            mToolbarSubtitle.setVisibility(View.GONE);
//            mTvMessage.setVisibility(View.VISIBLE);
//            mTvMessage.setVisibility(View.VISIBLE);
//        } else {
//            mTvName.setText(userInfo.getRealname());
//            mTvBank.setText(userInfo.getBankName());
//            mTvBankPay.setText(userInfo.getBankNumber());
//            mTvBankPay2.setText(userInfo.getBankNumber());
//            if (isedit) {
//                if (StringUtil.isEmpty(userInfo.getRealname())) {
//                    mTvName.setFocusable(true);
//                    mTvName.setFocusableInTouchMode(true);
//                } else {
//                    mTvName.setFocusable(false);
//                    mTvName.setFocusableInTouchMode(false);
//                    mTvName.setText(userInfo.getRealname());
//                }
//                mTvBankPay.setFocusable(true);
//                mTvBankPay.setFocusableInTouchMode(true);
//                mTvBank.setFocusable(true);
//                mTvBank.setFocusableInTouchMode(true);
//                mTvBankPay.requestFocus();
//                mTvBankPay.setSelection(mTvBankPay.getText().length());
//                mToolbarSubtitle.setVisibility(View.VISIBLE);
//                mToolbarSubtitle.setText("取消");
//                mLlQr.setVisibility(View.VISIBLE);
//                mLlSubmits.setVisibility(View.VISIBLE);
//                mTvMessage.setVisibility(View.VISIBLE);
//            } else {
//                mTvName.setFocusable(false);
//                mTvName.setFocusableInTouchMode(false);
//                mTvBank.setFocusable(false);
//                mTvBank.setFocusableInTouchMode(false);
//                mTvBankPay.setFocusable(false);
//                mTvBankPay.setFocusableInTouchMode(false);
//                mLlQr.setVisibility(View.GONE);
//                mTvMessage.setVisibility(View.GONE);
//                mToolbarSubtitle.setVisibility(View.VISIBLE);
//                mToolbarSubtitle.setText("编辑");
//                mLlSubmits.setVisibility(View.GONE);
//            }
//
//        }
    }


    /**
     * 绑定支付宝
     */
    private void apply() {
        if (StringUtil.isEmpty(mTvBank.getText().toString())) {
            toast("请输入银行");
            return;
        } else if (StringUtil.isEmpty(mTvBankPay.getText().toString())) {
            toast("请输入银行卡账号");
            return;
        } else if (StringUtil.isEmpty(mTvBankPay2.getText().toString()) || !mTvBankPay2.getText().toString().equals(mTvBankPay.getText().toString())) {
            toast("银行卡账号输入不一致");
            return;
        } else if (StringUtil.isEmpty(mTvName.getText().toString())) {
            toast("请输入真实姓名");
            return;
        }
        Map<String, Object> map = new HashMap<>();
        //姓名
        map.put("realname", mTvName.getText().toString());
        //银行帐号
        map.put("bankNumber", mTvBankPay.getText().toString());
        //银行
        map.put("bankName", mTvBank.getText().toString());
        ApiClient.requestNetHandle(this, AppConfig.addBank, "正在提交...", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                toast(msg);
                isedit = false;
                MyApplication.getInstance().UpUserInfo();
            }

            @Override
            public void onFailure(String msg) {
                toast(msg);
            }
        });
    }


    @OnClick({R.id.toolbar_subtitle, R.id.ll_submits})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            /**
             * 编辑、取消
             */
            case R.id.toolbar_subtitle:
                if (isedit) {
                    isedit = false;
                } else {
                    isedit = true;
                }
                initUserInfo();
                break;
            case R.id.ll_submits:
                //绑定银行卡
                apply();
                break;
            default:
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }
}
