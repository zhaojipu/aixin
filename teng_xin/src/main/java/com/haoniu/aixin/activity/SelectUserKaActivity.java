package com.haoniu.aixin.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.haoniu.aixin.R;
import com.haoniu.aixin.base.BaseActivity;
import com.haoniu.aixin.entity.BankDetailInfo;
import com.haoniu.aixin.entity.EventCenter;
import com.haoniu.aixin.http.ApiClient;
import com.haoniu.aixin.http.AppConfig;
import com.haoniu.aixin.http.ResultListener;
import com.haoniu.aixin.utils.StringUtil;
import com.zds.base.json.FastJsonUtil;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 选择账户
 */
public class SelectUserKaActivity extends BaseActivity {


    @BindView(R.id.bar)
    View bar;
    @BindView(R.id.ll_back)
    LinearLayout llBack;
    @BindView(R.id.toolbar_subtitle)
    TextView toolbarSubtitle;
    @BindView(R.id.img_right)
    ImageView imgRight;
    @BindView(R.id.toolbar_title)
    TextView toolbarTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.llayout_title_1)
    RelativeLayout llayoutTitle1;
    @BindView(R.id.tv_bank)
    TextView tvBank;
    @BindView(R.id.ll_bank)
    LinearLayout llBank;

    BankDetailInfo bankDetailInfo;
    @BindView(R.id.tv_alipay)
    TextView tvAlipay;
    @BindView(R.id.ll_alipay)
    LinearLayout llAlipay;

    @Override
    protected void initContentView(Bundle bundle) {
        setContentView(R.layout.activity_ka);
    }

    @Override
    protected void initLogic() {
        setTitle("选择账户");

    }

    @Override
    protected void onResume() {
        super.onResume();
        getBank();
    }

    /**
     * 获取支付宝
     */
    private void getBank() {

        Map<String, Object> map = new HashMap<>();
        ApiClient.requestNetHandle(this, AppConfig.bankInfo, "", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                bankDetailInfo = FastJsonUtil.getObject(json, BankDetailInfo.class);
                if (bankDetailInfo != null) {
                    if (!StringUtil.isEmpty(bankDetailInfo.getBankNumber())) {
                        llBank.setVisibility(View.VISIBLE);
                        tvBank.setText(bankDetailInfo.getBankName() + "(" + bankDetailInfo.getBankNumber() + ")");
                    }else {
                        tvBank.setText("请添加银行卡");
                        llBank.setVisibility(View.VISIBLE);
                    }
                    if (!StringUtil.isEmpty(bankDetailInfo.getAlipay())) {
                        llAlipay.setVisibility(View.VISIBLE);
                        tvAlipay.setText("支付宝(" + bankDetailInfo.getAlipay() + ")");
                    }else {
                        llAlipay.setVisibility(View.VISIBLE);
                        tvAlipay.setText("请添加支付宝");
                    }

                }

            }

            @Override
            public void onFailure(String msg) {
                toast(msg);
            }
        });
    }

    @Override
    protected void onEventComing(EventCenter center) {

    }

    @Override
    protected void getBundleExtras(Bundle extras) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }


    @OnClick({R.id.ll_alipay, R.id.ll_bank})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_alipay:
                if (!StringUtil.isEmpty(bankDetailInfo.getAlipay())) {
                Intent intent = new Intent();
                intent.putExtra("type",1);
                setResult(999, intent);
                finish();}else {
                    startActivity(new Intent(SelectUserKaActivity.this, AliPayNewActivity.class));
                }
                break;
            case R.id.ll_bank:
                if (!StringUtil.isEmpty(bankDetailInfo.getBankNumber())) {
                Intent intent2 = new Intent();
                intent2.putExtra("type",2);
                setResult(999, intent2);
                finish();}else {
                    startActivity(new Intent(SelectUserKaActivity.this, BankNewActivity.class));
                }
                break;
        }
    }
}
