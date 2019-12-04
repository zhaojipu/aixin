package com.haoniu.aixin.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
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
import com.haoniu.aixin.utils.EventUtil;
import com.zds.base.util.StringUtil;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 作   者：赵大帅
 * 描   述: 绑定支付宝
 * 邮   箱: 2510722254@qqq.com
 * 日   期: 2017/12/27 16:44
 * 更新日期: 2017/12/27
 */
public class AliPayActivity extends BaseActivity {
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
    @BindView(R.id.tv_alipay)
    EditText tvAlipay;
    @BindView(R.id.tv_name)
    EditText tvName;
    @BindView(R.id.ll_submits)
    LinearLayout tvSubmit;
    @BindView(R.id.tv_alipay2)
    EditText tvAlipay2;
    @BindView(R.id.tv_message)
    TextView tvMessage;
    @BindView(R.id.ll_qr)
    LinearLayout llQr;

    private boolean isedit = false;

    /**
     * 替代onCreate的使用
     *
     * @param bundle
     */
    @Override
    protected void initContentView(Bundle bundle) {
        setContentView(R.layout.activity_alipay);
    }

    /**
     * 初始化逻辑
     */
    @Override
    protected void initLogic() {
        setTitle("绑定支付宝");
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
//        UserInfo userInfo = MyApplication.getInstance().getUserInfo();
//        if (userInfo.getAlipay() == null || userInfo.getAlipay().equals("")) {
//            isedit = true;
//            if (StringUtil.isEmpty(userInfo.getRealname())) {
//                tvName.setFocusable(true);
//                tvName.setFocusableInTouchMode(true);
//            } else {
//                tvName.setFocusable(false);
//                tvName.setFocusableInTouchMode(false);
//                tvName.setText(userInfo.getRealname());
//            }
//            tvAlipay.setFocusable(true);
//            tvAlipay.setFocusableInTouchMode(true);
//            tvAlipay.requestFocus();
//            llQr.setVisibility(View.VISIBLE);
//            mToolbarSubtitle.setVisibility(View.GONE);
//            tvSubmit.setVisibility(View.VISIBLE);
//            tvMessage.setVisibility(View.VISIBLE);
//        } else {
//            tvName.setText(userInfo.getRealname());
//            tvAlipay.setText(userInfo.getAlipay());
//            tvAlipay2.setText(userInfo.getAlipay());
//            if (isedit) {
//                if (StringUtil.isEmpty(userInfo.getRealname())) {
//                    tvName.setFocusable(true);
//                    tvName.setFocusableInTouchMode(true);
//                } else {
//                    tvName.setFocusable(false);
//                    tvName.setFocusableInTouchMode(false);
//                    tvName.setText(userInfo.getRealname());
//                }
//                tvAlipay.setFocusable(true);
//                tvAlipay.setFocusableInTouchMode(true);
//                tvAlipay.requestFocus();
//                tvAlipay.setSelection(tvAlipay.getText().length());
//                mToolbarSubtitle.setVisibility(View.VISIBLE);
//                mToolbarSubtitle.setText("取消");
//                llQr.setVisibility(View.VISIBLE);
//                tvSubmit.setVisibility(View.VISIBLE);
//                tvMessage.setVisibility(View.VISIBLE);
//            } else {
//                tvName.setFocusable(false);
//                tvName.setFocusableInTouchMode(false);
//                tvAlipay.setFocusable(false);
//                tvAlipay.setFocusableInTouchMode(false);
//                llQr.setVisibility(View.GONE);
//                tvMessage.setVisibility(View.GONE);
//                mToolbarSubtitle.setVisibility(View.VISIBLE);
//                mToolbarSubtitle.setText("编辑");
//                tvSubmit.setVisibility(View.GONE);
//            }
//
//        }
    }


    /**
     * 绑定支付宝
     */
    private void apply() {
        if (StringUtil.isEmpty(tvAlipay.getText().toString())) {
            toast("请输入支付宝账号");
            return;
        } else if (StringUtil.isEmpty(tvAlipay2.getText().toString()) || !tvAlipay2.getText().toString().equals(tvAlipay.getText().toString())) {
            toast("支付宝账号输入不一致");
            return;
        } else if (StringUtil.isEmpty(tvName.getText().toString())) {
            toast("请输入真实姓名");
            return;
        }
        Map<String, Object> map = new HashMap<>();
        //姓名
        map.put("realname", tvName.getText().toString());
        //支付宝
        map.put("alipay", tvAlipay.getText().toString());
        ApiClient.requestNetHandle(this, AppConfig.addAliPay, "正在提交...", map, new ResultListener() {
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
                //绑定支付宝
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
