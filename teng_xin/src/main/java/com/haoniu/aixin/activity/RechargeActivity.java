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

import com.haoniu.aixin.R;
import com.haoniu.aixin.base.BaseActivity;
import com.haoniu.aixin.entity.EventCenter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 作   者：赵大帅
 * 描   述: 充值
 * 邮   箱: 2510722254@qqq.com
 * 日   期: 2017/11/28 16:32
 * 更新日期: 2017/11/28
 */
public class RechargeActivity extends BaseActivity {
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
    @BindView(R.id.tv_jinbi)
    TextView mTvJinbi;
    @BindView(R.id.et_money)
    EditText mEtMoney;
    @BindView(R.id.tv_yuan)
    TextView mTvYuan;
    @BindView(R.id.tv_money)
    TextView mTvMoney;
    @BindView(R.id.img_zhifubao)
    ImageView mImgZhifubao;
    @BindView(R.id.ll_zhifubao)
    LinearLayout mLlZhifubao;
    @BindView(R.id.img_weixin)
    ImageView mImgWeixin;
    @BindView(R.id.ll_weixin)
    LinearLayout mLlWeixin;
    @BindView(R.id.pay)
    TextView mPay;

    /**
     * 替代onCreate的使用
     *
     * @param bundle
     */
    @Override
    protected void initContentView(Bundle bundle) {
        setContentView(R.layout.pay_main);
    }

    /**
     * 初始化逻辑
     */
    @Override
    protected void initLogic() {
        setTitle("充值");
        initbarView();
        mTvJinbi.setText(getResources().getString(R.string.glod));
        mImgWeixin.setImageResource(R.mipmap.img_select);
        mEtMoney.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_CLASS_NUMBER);
        //设置字符过滤
        mEtMoney.setFilters(new InputFilter[]{new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                if (".".equals(source) && dest.toString().length() == 0) {
                    return "0.";
                }
                if (dest.length() == 8) {
                    return "";
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

    private TextWatcher watcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // TODO Auto-generated method stub
            mTvMoney.setText((mEtMoney.getText().toString() != null) && !"".equals(mEtMoney.getText().toString().trim()) ? "￥" + mEtMoney.getText().toString() : "￥" + "0.00");
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

    private void initbarView() {
        mImgWeixin.setImageResource(R.mipmap.img_nselect);
        mImgZhifubao.setImageResource(R.mipmap.img_nselect);
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

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @OnClick({R.id.ll_weixin, R.id.pay})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_weixin:
                initbarView();
                mImgWeixin.setImageResource(R.mipmap.img_select);
                break;
            case R.id.pay:
                recharge();
                break;
        }
    }

    private void recharge() {
       toast("开发中...");
        return;
//        Map<String, Object> map = new HashMap<>();
//        map.put("totalPrice", mEtMoney.getText().toString());
//        ApiClient.requestNetHandle(this, AppConfig.getRecharge, "正在支付...", map, new ResultListener() {
//            @Override
//            public void onSuccess(String json, String msg) {
//                XLog.json(json);
//                ToastUtil.toast(msg);
//            }
//
//            @Override
//            public void onFailure(String msg) {
//                ToastUtil.toast(msg);
//            }
//        });
    }


}
