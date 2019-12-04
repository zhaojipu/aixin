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
import com.haoniu.aixin.base.MyApplication;
import com.haoniu.aixin.base.Storage;
import com.haoniu.aixin.entity.BankDetailInfo;
import com.haoniu.aixin.entity.EventCenter;
import com.haoniu.aixin.entity.PZInfo;
import com.haoniu.aixin.entity.UserInfo;
import com.haoniu.aixin.http.ApiClient;
import com.haoniu.aixin.http.AppConfig;
import com.haoniu.aixin.http.ResultListener;
import com.haoniu.aixin.utils.EventUtil;
import com.zds.base.json.FastJsonUtil;
import com.zds.base.util.StringUtil;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 作   者：赵大帅
 * 描   述: 我的钱包
 * 邮   箱: 2510722254@qqq.com
 * 日   期: 2017/12/27 10:48
 * 更新日期: 2017/12/27
 */
public class MyPacketActivity extends BaseActivity {



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
    @BindView(R.id.tv_money)
    TextView tvMoney;
    @BindView(R.id.ll_cz)
    LinearLayout llCz;
    @BindView(R.id.ll_tx)
    LinearLayout llTx;

    /**
     * 替代onCreate的使用
     *
     * @param bundle
     */
    @Override
    protected void initContentView(Bundle bundle) {
        setContentView(R.layout.activity_my_packet);
    }

    /**
     * 初始化逻辑
     */
    @Override
    protected void initLogic() {
        setTitle("我的钱包");
        initMoney();
        getBank();
        toolbarSubtitle.setVisibility(View.VISIBLE);
        toolbarSubtitle.setText("账单");
    }

    /**
     * 初始化金额
     */

    private void initMoney() {
        UserInfo userInfo = MyApplication.getInstance().getUserInfo();
        if (userInfo != null) {
            tvMoney.setText("￥" + StringUtil.getFormatValue2(userInfo.getMoney()));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        MyApplication.getInstance().UpUserInfo();
    }


    /**
     * EventBus接收消息
     *
     * @param center 获取事件总线信息
     */
    @Override
    protected void onEventComing(EventCenter center) {
        /**
         * 刷新用户信息
         */
        if (center.getEventCode() == EventUtil.FLUSHUSERINFO) {
            initMoney();
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


    @OnClick({R.id.ll_cz, R.id.ll_tx,R.id.ll_bank,R.id.toolbar_subtitle,R.id.ll_alipay})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_cz:
                PZInfo pzInfo = Storage.GetPZ();
                if (pzInfo != null) {
                    startActivity(new Intent(MyPacketActivity.this, WebViewActivity.class).putExtra("title", "充值").putExtra("url", pzInfo.getRechargeUrl()+Storage.getToken()));
                }
                break;
            case R.id.ll_tx:

                break;
            case R.id.ll_bank:
                if (bankDetailInfo != null) {
                    if (StringUtil.isEmpty(bankDetailInfo.getBankNumber())) {

                        startActivity(new Intent(MyPacketActivity.this, BankNewActivity.class));
                    } else {
                        startActivity(new Intent(MyPacketActivity.this, BankNewInfoActivity.class));
                    }
                } else {
                    toast("未获取到信息");
                }
                break;
            case R.id.ll_alipay:

                if (bankDetailInfo != null) {
                    if (StringUtil.isEmpty(bankDetailInfo.getAlipay())) {

                        startActivity(new Intent(MyPacketActivity.this, AliPayNewActivity.class));
                    } else {
                        startActivity(new Intent(MyPacketActivity.this, AlipayNewInfoActivity.class));
                    }
                } else {
                    toast("未获取到信息");
                }
                break;

            //账单
            case R.id.toolbar_subtitle:
            startActivity(new Intent(MyPacketActivity.this, NewMyPacketActivity.class));
            break;
            default:
        }
    }

    BankDetailInfo bankDetailInfo;

    /**
     * 获取银行卡信息
     */
    private void getBank() {
        ApiClient.requestNetHandle(MyPacketActivity.this, AppConfig.bankInfo, "", new HashMap<>(), new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                bankDetailInfo = FastJsonUtil.getObject(json, BankDetailInfo.class);
            }

            @Override
            public void onFailure(String msg) {
            }
        });
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }
}
