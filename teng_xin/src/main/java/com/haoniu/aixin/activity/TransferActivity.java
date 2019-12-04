package com.haoniu.aixin.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.haoniu.aixin.R;
import com.haoniu.aixin.base.BaseActivity;
import com.haoniu.aixin.base.MyApplication;
import com.haoniu.aixin.entity.EventCenter;
import com.haoniu.aixin.http.ApiClient;
import com.haoniu.aixin.http.AppConfig;
import com.haoniu.aixin.http.ResultListener;
import com.haoniu.aixin.utils.EventUtil;
import com.zds.base.Toast.ToastUtil;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 转账
 *
 * @author Administrator
 */

public class TransferActivity extends BaseActivity {


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

    String toUser;
    String id;

    @Override
    protected void initContentView(Bundle bundle) {
        setContentView(R.layout.activity_transfer2);
    }

    @Override
    protected void initLogic() {
        setTitle("转账");
        initUser();
        MyApplication.getInstance().UpUserInfo();
    }

    private void initUser() {
        tvHaveMoney.setText("转给" + toUser);
    }

    @Override
    protected void onEventComing(EventCenter center) {
        if (center.getEventCode() == EventUtil.FLUSHUSERINFO) {
            initUser();
        }
    }

    @Override
    protected void getBundleExtras(Bundle extras) {
        toUser = extras.getString("toUser");
        id = extras.getString("id");
    }

    /**
     * 提现
     */
    private void withdraw() {
        if (etMoney.getText().toString() == null || Integer.valueOf(etMoney.getText().toString()) <= 0) {
            toast("请输入转账金额");
            return;
        }
        Map<String, Object> map = new HashMap<>();
        map.put("money", etMoney.getText().toString());
        map.put("friendUserId", id);
        ApiClient.requestNetHandleByGet(this, AppConfig.transfer, "正在转账...", map, new ResultListener() {
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
                withdraw();
                break;
            default:
        }
    }

}
