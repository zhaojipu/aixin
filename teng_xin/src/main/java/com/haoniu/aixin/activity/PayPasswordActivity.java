package com.haoniu.aixin.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.haoniu.aixin.R;
import com.haoniu.aixin.base.BaseActivity;
import com.haoniu.aixin.base.MyApplication;
import com.haoniu.aixin.entity.EventCenter;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 作   者：赵大帅
 * 描   述: 支付密码
 * 邮   箱: 2510722254@qqq.com
 * 日   期: 2017/11/17 9:48
 * 更新日期: 2017/11/17
 */
public class PayPasswordActivity extends BaseActivity {
    @BindView(R.id.ll_up_password)
    LinearLayout mLlUpPassword;
    @BindView(R.id.ll_forget_password)
    LinearLayout mLlForgetPassword;

    /**
     * 替代onCreate的使用
     *
     * @param bundle
     */
    @Override
    protected void initContentView(Bundle bundle) {
        setContentView(R.layout.activity_pay_password);
    }

    /**
     * 初始化逻辑
     */
    @Override
    protected void initLogic() {
        setTitle("支付密码");
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


    @OnClick({R.id.ll_up_password, R.id.ll_forget_password})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_up_password:
                if (MyApplication.getInstance().getUserInfo().getIsPayPsd() == 0) {
                    startActivity(InputPasswordActivity.class);

                } else {
                    startActivity(VerifyingPayPasswordActivity.class);
                }
                break;
            case R.id.ll_forget_password:
                startActivity(VerifyingPayPasswordPhoneNumberActivity.class);
                break;
            default:
        }
    }
}
