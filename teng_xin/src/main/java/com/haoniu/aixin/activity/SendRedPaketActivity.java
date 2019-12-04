package com.haoniu.aixin.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.haoniu.aixin.http.ApiClient;
import com.haoniu.aixin.http.AppConfig;
import com.haoniu.aixin.http.ResultListener;
import com.haoniu.aixin.R;
import com.haoniu.aixin.base.BaseActivity;
import com.haoniu.aixin.entity.EventCenter;
import com.zds.base.util.StringUtil;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;


/**
 * 发红包
 *
 * @author Administrator
 */

public class SendRedPaketActivity extends BaseActivity {

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
    @BindView(R.id.tv_money_name)
    TextView mTvMoneyName;
    @BindView(R.id.et_money)
    EditText mEtMoney;
    @BindView(R.id.tv_money_num)
    TextView mTvMoneyNum;
    @BindView(R.id.et_money_num)
    EditText mEtMoneyNum;
    @BindView(R.id.ll_number)
    LinearLayout mLlNumber;
    @BindView(R.id.et_message)
    EditText mEtMessage;
    @BindView(R.id.tv_money)
    TextView mTvMoney;
    @BindView(R.id.tv_submits)
    TextView mTvSubmits;

    private String touserName;
    /**
     * 0个人红包，1群红包
     */
    private int tag = 0;

    /**
     * 替代onCreate的使用
     *
     * @param bundle
     */
    @Override
    protected void initContentView(Bundle bundle) {
        setContentView(R.layout.activity_send_redpacket);
    }

    /**
     * 初始化逻辑
     */
    @Override
    protected void initLogic() {
        setTitle("发红包");
        if (tag == 1) {
            mTvMoneyName.setText("总金额");
            mLlNumber.setVisibility(View.VISIBLE);
        } else {
            mLlNumber.setVisibility(View.GONE);
        }
        mEtMoney.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_CLASS_NUMBER);
        //设置字符过滤
        mEtMoney.setFilters(new InputFilter[]{new InputFilter() {
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
        mEtMoney.addTextChangedListener(watcher);
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
        tag = extras.getInt("tag", 0);
    }

    private TextWatcher watcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // TODO Auto-generated method stub
            mTvMoney.setText(mEtMoney.getText().toString() != null ? "￥" + mEtMoney.getText().toString() : "￥" + "0.00");
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
            // TODO Auto-generated method stub
        }

        @Override
        public void afterTextChanged(Editable s) {
            // TODO Auto-generated method stub

        }
    };


    @OnClick({R.id.ll_back, R.id.tv_submits})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_back:
                finish();
                break;
            case R.id.tv_submits:
                toSendRedPassword();
                break;
            default:
        }
    }

    private void toSendRedPassword() {
        if (StringUtil.isEmpty(mEtMoney.getText().toString())) {
            mEtMoney.requestFocus();
            toast("请输入金额");
            return;
        } else if (StringUtil.isEmpty(mEtMoneyNum.getText().toString())) {
            mEtMoneyNum.requestFocus();
            toast("请输入个数");
            return;
        } else if (Double.valueOf(mEtMoney.getText().toString()) == 0) {
            Toast.makeText(this, "请输入金额", Toast.LENGTH_LONG).show();
            return;
        } else if (Integer.valueOf(mEtMoneyNum.getText().toString()) == 0) {
            Toast.makeText(this, "请输入个数", Toast.LENGTH_LONG).show();
            return;
        } else if (Integer.valueOf(mEtMoneyNum.getText().toString()) * 0.01 > Double.valueOf(mEtMoney.getText().toString())) {
            Toast.makeText(this, "单个红包金额不可低于0.01", Toast.LENGTH_LONG).show();
            return;
        }
        sendRoomRedPerPacket();

    }

    /**
     * 发群随机红包
     */
    private void sendRoomRedPerPacket() {
        Map<String, Object> map = new HashMap<>();
        map.put("huanxinGroupId", touserName);
        map.put("money", mEtMoney.getText().toString());
        map.put("amount", mEtMoneyNum.getText().toString());
        map.put("remark", (mEtMessage.getText().toString() == null || "".equals(mEtMessage.getText().toString())) ? "恭喜发财，大吉大利" : mEtMessage.getText().toString());
        ApiClient.requestNetHandle(this, AppConfig.sendPerRedEnvelope, "正在发送...", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                finish();
            }

            @Override
            public void onFailure(String msg) {
                toast(msg);
            }
        });
    }

}
