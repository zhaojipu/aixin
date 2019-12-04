package com.haoniu.aixin.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.haoniu.aixin.R;
import com.haoniu.aixin.base.BaseActivity;
import com.haoniu.aixin.base.Constant;
import com.haoniu.aixin.base.MyApplication;
import com.haoniu.aixin.domain.EaseUser;
import com.haoniu.aixin.entity.EventCenter;
import com.haoniu.aixin.http.ApiClient;
import com.haoniu.aixin.http.AppConfig;
import com.haoniu.aixin.http.ResultListener;
import com.haoniu.aixin.utils.EaseUserUtils;
import com.haoniu.aixin.widget.CommonDialog;
import com.haoniu.aixin.widget.CustomerKeyboard;
import com.haoniu.aixin.widget.EaseImageView;
import com.haoniu.aixin.widget.PasswordEditText;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.zds.base.ImageLoad.GlideUtils;
import com.zds.base.Toast.ToastUtil;
import com.zds.base.util.StringUtil;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 作   者：赵大帅
 * 描   述: 转账
 * 邮   箱: 2510722254@qqq.com
 * 日   期: 2017/11/29 16:46
 * 更新日期: 2017/11/29
 *
 * @author Administrator
 */
public class TransferfActivity extends BaseActivity {


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
    @BindView(R.id.img_head)
    EaseImageView imgHead;
    @BindView(R.id.nickName)
    TextView nickName;
    @BindView(R.id.ll_userMessage)
    LinearLayout llUserMessage;
    @BindView(R.id.textView2)
    TextView textView2;
    @BindView(R.id.et_money)
    EditText etMoney;
    @BindView(R.id.tv_tosubmit)
    TextView tvTosubmit;
    private String userId;
    private String head;
    private String nick;
    private int type;

    /**
     * 替代onCreate的使用
     *
     * @param bundle
     */
    @Override
    protected void initContentView(Bundle bundle) {
        setContentView(R.layout.activity_transfer);
    }

    /**
     * 初始化逻辑
     */
    @Override
    protected void initLogic() {
        setTitle("转账");
        MyApplication.getInstance().setAvatar(imgHead);
        if (!com.haoniu.aixin.utils.StringUtil.isEmpty(head)){
            GlideUtils.loadImageViewLoding(AppConfig.checkimg( head), imgHead, R.mipmap.img_default_avatar);
            nickName.setText(nick);
        }else {
            EaseUser user = EaseUserUtils.getUserInfo(userId);
            if (user != null) {
                GlideUtils.loadImageViewLoding(AppConfig.checkimg( user.getAvatar()), imgHead, R.mipmap.img_default_avatar);
                nickName.setText(user.getNickname());
                nick=user.getNickname();
            }
        }

        etMoney.setFilters(new InputFilter[]{new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                if (".".equals(source) && dest.toString().length() == 0) {
                    return "0.";
                }
                if (dest.toString().contains(".")) {
                    int index = dest.toString().indexOf(".");
                    int mlength = dest.toString().substring(index).length();
                    if (mlength == 3) {
                        return "";
                    }
                }
                return null;
            }
        }});
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
        userId = extras.getString("userId", "");
        head = extras.getString("head", "");
        nick = extras.getString("nick", "");
        type=extras.getInt("type",0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @OnClick({R.id.tv_tosubmit, R.id.toolbar_subtitle})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            //转账记录
            case R.id.toolbar_subtitle:
                startActivity(TransferRecordActivity.class);
                break;
            //转账
            case R.id.tv_tosubmit:
                if (com.haoniu.aixin.utils.StringUtil.isEmpty(etMoney.getText().toString()) || Double.valueOf(etMoney.getText().toString()) == 0) {
                    toast("请输入金额");
                    etMoney.requestFocus();
                    return;
                }
                transferTopassword();
                break;
            default:
        }
    }

    //转账
    protected void transferTopassword() {
        if (MyApplication.getInstance().getUserInfo().getIsPayPsd() == 0) {
            startActivity(new Intent(TransferfActivity.this, InputPasswordActivity.class));
            return;
        }
        final CommonDialog.Builder builder = new CommonDialog.Builder(TransferfActivity.this).fullWidth().fromBottom()
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
                if (number.equals("返回")) {
                    builder.dismiss();
                } else if (number.equals("忘记密码？")) {
                    startActivity(new Intent(TransferfActivity.this, VerifyingPayPasswordPhoneNumberActivity.class));
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
                transfer(password);
                builder.dismiss();
            }
        });

    }

    /**
     * 转账
     */
    private void transfer(String password) {
        Map<String, Object> map = new HashMap<>();
        map.put("money", etMoney.getText().toString());
        if (!StringUtil.isEmpty(userId)) {
            map.put("toUserId", userId.replace(Constant.ID_REDPROJECT, ""));
        }
        map.put("payPwd", password);
        ApiClient.requestNetHandle(this, AppConfig.transfer, "正在转账...", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                if (type!=1){
                    EMMessage message = EMMessage.createTxtSendMessage("转账", userId);
                    message.setAttribute(Constant.MSGTYPE, Constant.TURN);
                    message.setAttribute("money",etMoney.getText().toString());
                    message.setAttribute("nickname", nick==null?"":nick);
                    message.setAttribute(Constant.AVATARURL, MyApplication.getInstance().getUserInfo().getUserImg());
                    message.setAttribute(Constant.NICKNAME, MyApplication.getInstance().getUserInfo().getNickName());
                    //Add to conversation
                    EMClient.getInstance().chatManager().sendMessage(message);
                    EMClient.getInstance().chatManager().saveMessage(message);
                }
                toast(msg);
                finish();
            }

            @Override
            public void onFailure(String msg) {
                ToastUtil.toast(msg);
            }
        });
    }
}
