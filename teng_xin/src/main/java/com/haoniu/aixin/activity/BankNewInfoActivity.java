package com.haoniu.aixin.activity;

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
import com.zds.base.json.FastJsonUtil;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 银行卡详情
 */
public class BankNewInfoActivity extends BaseActivity {


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
    @Override
    protected void initContentView(Bundle bundle) {
        setContentView(R.layout.activity_bank_new_info);
    }

    @Override
    protected void initLogic() {
        setTitle("银行卡");

    }

    @Override
    protected void onResume() {
        super.onResume();
        getBank();
    }

    /**
     * 绑定银行卡
     */
    private void getBank() {

        Map<String, Object> map = new HashMap<>();

        ApiClient.requestNetHandle(this, AppConfig.bankInfo, "", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                bankDetailInfo=FastJsonUtil.getObject(json,BankDetailInfo.class);
                if (bankDetailInfo!=null){
                    tvBank.setText(bankDetailInfo.getBankName()+"("+bankDetailInfo.getBankNumber()+")");
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

    @OnClick(R.id.ll_bank)
    public void onViewClicked() {
        startActivity(BankNewActivity.class);
    }
}
