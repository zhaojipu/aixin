package com.haoniu.aixin.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.haoniu.aixin.R;
import com.haoniu.aixin.base.BaseActivity;
import com.haoniu.aixin.base.MyApplication;
import com.haoniu.aixin.entity.EventCenter;
import com.haoniu.aixin.entity.RoomInfo;
import com.haoniu.aixin.entity.UserInfo;
import com.haoniu.aixin.http.ApiClient;
import com.haoniu.aixin.http.AppConfig;
import com.haoniu.aixin.http.ResultListener;
import com.haoniu.aixin.widget.CommonDialog;
import com.haoniu.aixin.widget.CustomerKeyboard;
import com.haoniu.aixin.widget.PasswordEditText;
import com.zds.base.Toast.ToastUtil;
import com.zds.base.util.StringUtil;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * 发踩雷红包
 *
 * @author Administrator
 */

public class SendRedPaketLei4Activity extends BaseActivity {


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
    @BindView(R.id.tv_money_num)
    TextView tvMoneyNum;
    @BindView(R.id.tv_number)
    TextView tvNumber;
    @BindView(R.id.tv_lv)
    TextView tvLv;
    @BindView(R.id.tv_money_name)
    TextView tvMoneyName;
    @BindView(R.id.et_money)
    EditText etMoney;
    @BindView(R.id.tv_fenwei)
    TextView tvFenwei;
    @BindView(R.id.tv_money_lei)
    TextView tvMoneyLei;
    @BindView(R.id.et_lei)
    EditText etLei;
    @BindView(R.id.tv_submit)
    TextView tvSubmit;
    private String touserName;
    private RoomInfo roomInfo;
    private int amount;
    private RoomInfo.RoomLeiListBean roomLeiListBean;
    /**
     * 替代onCreate的使用
     *
     * @param bundle
     */
    @Override
    protected void initContentView(Bundle bundle) {
        setContentView(R.layout.activity_send_lei_redpacket4);
    }

    /**
     * 初始化逻辑
     */
    @Override
    protected void initLogic() {
        setTitle("发红包");
        if (roomInfo != null) {
            for (RoomInfo.RoomLeiListBean roomLeiListBean : roomInfo.getRoomLeiList()) {
                if (roomLeiListBean.getType() != null && roomLeiListBean.getType().equals("1")) {
                    tvFenwei.setText("红包发布范围：" + roomLeiListBean.getMoneyMin() + "-" + roomLeiListBean.getMoneyMax() + "元");
                    tvLv.setText("游戏倍数：" + roomLeiListBean.getOneLei());
                    tvNumber.setText((amount==0 ? roomLeiListBean.getAmount():amount) + "个");
                    this.roomLeiListBean = roomLeiListBean;
                }
            }
        }
    }


    /**
     * 支付密码
     */
    private void PayPassword(String point) {

        UserInfo userInfo = MyApplication.getInstance().getUserInfo();
        if (userInfo.getIsPayPsd() == 0) {
            startActivity(new Intent(SendRedPaketLei4Activity.this, InputPasswordActivity.class));
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
                    startActivity(new Intent(SendRedPaketLei4Activity.this, VerifyingPayPasswordPhoneNumberActivity.class));
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
                   sendCLHB(point,password);
                builder.dismiss();
            }
        });

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
        touserName = extras.getString("username");
        roomInfo = (RoomInfo) extras.getSerializable("detail");
        amount = extras.getInt("amount");
    }


    /**
     * 发红包
     */
    private void toSendRed() {
        if (StringUtil.isEmpty(etLei.getText().toString())) {
            etLei.requestFocus();
            toast("请输入雷点");
            return;
        } else if (StringUtil.isEmpty(etMoney.getText().toString())) {
            etMoney.requestFocus();
            toast("请输入金额");
            return;
        } else if (Double.valueOf(etMoney.getText().toString()) == 0) {
            Toast.makeText(this, "请输入金额", Toast.LENGTH_LONG).show();
            return;
        }
        PayPassword(etLei.getText().toString());
    }

    /**
     * 发踩雷红包
     */
    /**
     * 发踩雷红包
     */
    public void sendCLHB(String point,String password) {
        Map<String, Object> map = new HashMap<>();
        map.put("huanXinGroupId", touserName);
        map.put("money", etMoney.getText().toString());
        map.put("payPassword", password);
        map.put("thunderPoint", point);
        map.put("type", 1);
        map.put("amount", amount==0 ? this.roomLeiListBean.getAmount():amount);
        ApiClient.requestNetHandle(this, AppConfig.sendCLRedEnvelope, "正在发红包...", map, new ResultListener() {
            /**
             * 请求成功
             *
             * @param json
             * @param msg
             */
            @Override
            public void onSuccess(String json, String msg) {
                toast(msg);
                finish();
            }

            /**
             * 请求失败
             *
             * @param msg
             */
            @Override
            public void onFailure(String msg) {
                ToastUtil.toast(msg);
            }
        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @OnClick({R.id.tv_submit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_submit:
                toSendRed();
                break;
            default:
        }

    }
}
