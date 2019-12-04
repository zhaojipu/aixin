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
import com.haoniu.aixin.entity.BankDetailInfo;
import com.haoniu.aixin.entity.EventCenter;
import com.haoniu.aixin.entity.UserInfo;
import com.haoniu.aixin.http.ApiClient;
import com.haoniu.aixin.http.AppConfig;
import com.haoniu.aixin.http.ResultListener;
import com.zds.base.Toast.ToastUtil;
import com.zds.base.json.FastJsonUtil;
import com.zds.base.util.StringUtil;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 银行卡
 */
public class BankNewActivity extends BaseActivity {
    @BindView(R.id.er_name)
    TextView erName;
    @BindView(R.id.er_kahao)
    EditText erKahao;
    @BindView(R.id.er_bank)
    EditText erBank;
    @BindView(R.id.tv_bangding)
    TextView tvBangding;
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
    BankDetailInfo bankDetailInfo;
    @Override
    protected void initContentView(Bundle bundle) {
        setContentView(R.layout.activity_bank_new);
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
                if (bankDetailInfo!=null){
                    erBank.setText(bankDetailInfo.getBankName());
                    erKahao.setText(bankDetailInfo.getBankNumber());
                }
            }

            @Override
            public void onFailure(String msg) {
                toast(msg);
            }
        });
    }

    @Override
    protected void initLogic() {
        setTitle("绑定银行卡");
        UserInfo userInfo =MyApplication.getInstance().getUserInfo();
        getBank();
        if (!StringUtil.isEmpty(userInfo.getRealName())){
            erName.setText(userInfo.getRealName());
        }else {
            startActivityForResult(new Intent(this, RealNameActivity.class).putExtra("realName", ""), 13);
        }

//        erName.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivityForResult(new Intent(BankNewActivity.this, RealNameActivity.class).putExtra("realName", ""), 13);
//            }
//        });
    }



    /**
     * 绑定银行卡
     */
    private void apply() {
        if (StringUtil.isEmpty(erName.getText().toString())) {
            toast("请绑定真实姓名");
            return;
        }
        if (StringUtil.isEmpty(erBank.getText().toString())) {
            toast("请输入银行");
            return;
        } else if (StringUtil.isEmpty(erKahao.getText().toString())) {
            toast("请输入银行卡账号");
            return;
        }
        Map<String, Object> map = new HashMap<>();
        //银行帐号
        map.put("bankNumber", erKahao.getText().toString());
        //银行
        map.put("bankName", erBank.getText().toString());
        //类型
        map.put("type","1");
        ApiClient.requestNetHandle(this, AppConfig.addBank, "正在提交...", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                toast(msg);
                finish();
            }

            @Override
            public void onFailure(String msg) {
                toast(msg);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
       if (requestCode == 13) {
            if (resultCode == 102) {
               String realName=data.getStringExtra("realName");
                upRealName(realName);
        }}
    }

     /**
     * 更新真实姓名
     */
    private void upRealName(final String realName) {
        Map<String, Object> map = new HashMap<>();
        map.put("realName", realName);
        ApiClient.requestNetHandle(this, AppConfig.upDataRealName, "正在更新...", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                UserInfo userInfo = MyApplication.getInstance().getUserInfo();
                userInfo.setRealName(realName);
                MyApplication.getInstance().saveUserInfo(userInfo);
                erName.setText(realName);
                ToastUtil.toast(msg);
            }

            @Override
            public void onFailure(String msg) {
                ToastUtil.toast(msg);
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

    @OnClick(R.id.tv_bangding)
    public void onViewClicked() {
        apply();
    }
}