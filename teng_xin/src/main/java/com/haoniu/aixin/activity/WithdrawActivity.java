package com.haoniu.aixin.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
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
import com.haoniu.aixin.http.ApiClient;
import com.haoniu.aixin.http.AppConfig;
import com.haoniu.aixin.http.ResultListener;
import com.haoniu.aixin.utils.EventUtil;
import com.haoniu.aixin.widget.CommonDialog;
import com.haoniu.aixin.widget.CustomerKeyboard;
import com.haoniu.aixin.widget.PasswordEditText;
import com.zds.base.Toast.ToastUtil;
import com.zds.base.json.FastJsonUtil;
import com.zds.base.util.StringUtil;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 提现
 *
 * @author Administrator
 */

public class WithdrawActivity extends BaseActivity {


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
    @BindView(R.id.et_money)
    EditText etMoney;
    @BindView(R.id.tv_submit)
    TextView tvSubmit;
    @BindView(R.id.tv_haveMoney)
    TextView tvHaveMoney;
    @BindView(R.id.llayout_title_1)
    RelativeLayout llayoutTitle1;
    @BindView(R.id.tv_lv)
    TextView tvLv;
    @BindView(R.id.tv_time)
    TextView tvTime;
    @BindView(R.id.tv_zhanghu)
    TextView tvZhanghu;
    @BindView(R.id.ll_zhanghu)
    LinearLayout llZhanghu;

    BankDetailInfo bankDetailInfo;
    private int type=-1;

    @Override
    protected void initContentView(Bundle bundle) {
        setContentView(R.layout.activity_withdraw);
    }

    @Override
    protected void initLogic() {
        setTitle("提现");
        initUser();
        MyApplication.getInstance().UpUserInfo();
        getBank();
    }

    private void initUser() {
        getPZ();
        tvHaveMoney.setText("我的余额：" + StringUtil.getFormatValue2(MyApplication.getInstance().getUserInfo().getMoney()) + getResources().getString(R.string.glod));
    }

    @Override
    protected void onEventComing(EventCenter center) {
        if (center.getEventCode() == EventUtil.FLUSHUSERINFO) {
            initUser();
        }
    }

    @Override
    protected void getBundleExtras(Bundle extras) {

    }
    /**
     * 绑定银行卡
     */
    private void getBank() {

        Map<String, Object> map = new HashMap<>();

        ApiClient.requestNetHandle(this, AppConfig.bankInfo, "正在提交...", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                bankDetailInfo=FastJsonUtil.getObject(json,BankDetailInfo.class);
                if (bankDetailInfo!=null) {
                    if (type == -1) {
                    if (!com.haoniu.aixin.utils.StringUtil.isEmpty(bankDetailInfo.getAlipay())) {
                        type = 1;
                        tvZhanghu.setText("支付宝（" + bankDetailInfo.getAlipay() + "）");
                    } else if (!com.haoniu.aixin.utils.StringUtil.isEmpty(bankDetailInfo.getBankNumber())) {
                        type = 2;
                        tvZhanghu.setText(bankDetailInfo.getBankName() + "（" + bankDetailInfo.getBankNumber() + "）");
                    }
                }else {
                        initType();
                    }
                }

            }

            @Override
            public void onFailure(String msg) {
                toast(msg);
            }
        });
    }
    private void initType(){
        if (bankDetailInfo!=null){
            if (type==1){
                if (bankDetailInfo.getAlipay()!=null){
                    tvZhanghu.setText("支付宝（"+bankDetailInfo.getAlipay()+"）");
                }
            }else if (type==2) {

                if (bankDetailInfo.getBankNumber() != null) {
                    tvZhanghu.setText(bankDetailInfo.getBankName() + "（" + bankDetailInfo.getBankNumber() + "）");
                }
            }
        }


    }

    /**
     * 提现
     */
    private void withdraw(String password) {
        if (type<0){
            toast("请添加提现账户");
            return;
        }
        if (StringUtil.isEmpty(etMoney.getText().toString().trim())) {
            toast("请输入提现金额");
            return;
        }
        Map<String, Object> map = new HashMap<>();
        map.put("money", etMoney.getText().toString());
        //提现方式 1.支付宝 2.银行卡
        map.put("way", type+"");
        map.put("payPassword", password);
        ApiClient.requestNetHandle(this, AppConfig.withdraw, "正在提交", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                toast(msg);
                finish();
            }

            @Override
            public void onFailure(String msg) {
                ToastUtil.toast(msg);
            }
        });
    }

    /**
     * 获取配置
     */
    private void getPZ() {
        PZInfo pzInfo = Storage.GetPZ();
        if (pzInfo != null) {
            if (pzInfo.getWithdrawHandRate() > 0) {
                tvLv.setVisibility(View.VISIBLE);
                DecimalFormat df = new DecimalFormat("0.00%");
                tvLv.setText("（当前手续费率为" + df.format(pzInfo.getWithdrawHandRate()) + "" + "）");
            } else {
                tvLv.setVisibility(View.GONE);
            }
            tvTime.setText("提现时间" + pzInfo.getWithdrawTime());
            tvHaveMoney.setText("零钱剩余" + StringUtil.getFormatValue2(MyApplication.getInstance().getUserInfo().getMoney()) + getResources().getString(R.string.glod) + "，最低" + pzInfo.getMinWithdrawMoney() + getResources().getString(R.string.glod) + "起提");
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 990) {
            if (resultCode == 999) {
                 type = data.getIntExtra("type",-1);
                 initType();
                getBank();
            }
        }
    }
    /**
     * 支付密码
     */
    private void PayPassword() {

        if (MyApplication.getInstance().getUserInfo().getIsPayPsd() == 0) {
            toast("请设置支付密码");
            startActivity(new Intent(WithdrawActivity.this, InputPasswordActivity.class));
            return;

        }
        final CommonDialog.Builder builder = new CommonDialog.Builder(this).fullWidth().fromBottom()
                .setView(R.layout.dialog_customer_keyboard);
        builder.setOnClickListener(R.id.delete_dialog, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.dismiss();
            }
        });
        builder.create().show();
        final CustomerKeyboard mCustomerKeyboard = builder.getView(R.id.custom_key_board);
        final PasswordEditText mPasswordEditText = builder.getView(R.id.password_edit_text);
        mCustomerKeyboard.setOnCustomerKeyboardClickListener(new CustomerKeyboard.CustomerKeyboardClickListener() {
            @Override
            public void click(String number) {
                if ("返回".equals(number)) {
                    builder.dismiss();
                } else if ("忘记密码？".equals(number)) {
                    startActivity(new Intent(WithdrawActivity.this, VerifyingPayPasswordPhoneNumberActivity.class));
                } else {
                    mPasswordEditText.addPassword(number);
                }
            }

            @Override
            public void delete() {
                mPasswordEditText.deleteLastPassword();
            }
        });

        mPasswordEditText.setOnPasswordFullListener(new PasswordEditText.PasswordFullListener() {
            @Override
            public void passwordFull(String password) {
                withdraw(password);
                builder.dismiss();
            }
        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @OnClick({R.id.tv_submit,R.id.ll_zhanghu})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_submit:
                PayPassword();
                break;
            case R.id.ll_zhanghu:
                startActivityForResult(new Intent(WithdrawActivity.this,SelectUserKaActivity.class),990);
                break;
            default:
        }
    }

}
