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

public class SendRedPaketLeiActivity extends BaseActivity {


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
    @BindView(R.id.rel_1)
    RelativeLayout rel1;
    @BindView(R.id.rel_2)
    RelativeLayout rel2;
    @BindView(R.id.rel_3)
    RelativeLayout rel3;
    @BindView(R.id.rel_4)
    RelativeLayout rel4;
    @BindView(R.id.rel_5)
    RelativeLayout rel5;
    @BindView(R.id.rel_6)
    RelativeLayout rel6;
    @BindView(R.id.rel_7)
    RelativeLayout rel7;
    @BindView(R.id.rel_8)
    RelativeLayout rel8;
    @BindView(R.id.rel_9)
    RelativeLayout rel9;
    @BindView(R.id.rel_0)
    RelativeLayout rel0;
    private String touserName;
    private RoomInfo roomInfo;

    /**
     * 替代onCreate的使用
     *
     * @param bundle
     */
    @Override
    protected void initContentView(Bundle bundle) {
        setContentView(R.layout.activity_send_lei_redpacket);
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
                    tvNumber.setText(roomLeiListBean.getAmount() + "个");
                }
            }
        }
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
    }


    /**
     * 发红包
     */
    private void checkPassword(int point) {
        if (point == -1) {
            return;
        }
        if (StringUtil.isEmpty(etMoney.getText().toString())) {
            etMoney.requestFocus();
            toast("请输入金额");
            return;
        } else if (Double.valueOf(etMoney.getText().toString()) == 0) {
            toast("请输入金额");
            return;
        }
        sendCLHB(point + "");
    }

    /**
     * 发踩雷红包
     */
    public void sendCLHB(String point) {
        Map<String, Object> map = new HashMap<>();
        map.put("huanXinGroupId", touserName);
        map.put("money", etMoney.getText().toString());
//        map.put("payPassword", password);
        map.put("thunderPoint", point);
        map.put("type", 1);
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

    /**
     * 支付密码
     */
    private void PayPassword(String point) {

        UserInfo userInfo = MyApplication.getInstance().getUserInfo();
        if (userInfo.getIsPayPsd() == 0) {
            startActivity(new Intent(SendRedPaketLeiActivity.this, InputPasswordActivity.class));
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
                    startActivity(new Intent(SendRedPaketLeiActivity.this, VerifyingPayPasswordPhoneNumberActivity.class));
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
             //   sendCLHB(password, point);
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

    @OnClick({R.id.rel_1, R.id.rel_2, R.id.rel_3, R.id.rel_4, R.id.rel_5, R.id.rel_6, R.id.rel_7, R.id.rel_8, R.id.rel_9, R.id.rel_0})
    public void onViewClicked(View view) {
        int number = -1;
        switch (view.getId()) {
            case R.id.rel_1:
                number = 1;
                break;
            case R.id.rel_2:
                number = 2;
                break;
            case R.id.rel_3:
                number = 3;
                break;
            case R.id.rel_4:
                number = 4;
                break;
            case R.id.rel_5:
                number = 5;
                break;
            case R.id.rel_6:
                number = 6;
                break;
            case R.id.rel_7:
                number = 7;
                break;
            case R.id.rel_8:
                number = 8;
                break;
            case R.id.rel_9:
                number = 9;
                break;
            case R.id.rel_0:
                number = 0;
                break;
            default:
        }
        checkPassword(number);
    }
}
