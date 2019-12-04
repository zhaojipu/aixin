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
import com.haoniu.aixin.entity.EventCenter;
import com.zds.base.util.StringUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * 修改昵称
 */

public class RealNameActivity extends BaseActivity {

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
    @BindView(R.id.llayout_title_1)
    RelativeLayout llayoutTitle1;
    @BindView(R.id.et_real_name)
    EditText etRealName;
    @BindView(R.id.tv_submits)
    TextView tvSubmits;
    private String nickname;


    @Override
    protected void initContentView(Bundle bundle) {
        setContentView(R.layout.activity_zhenshixingming);
    }

    /**
     * 初始化逻辑
     */
    @Override
    protected void initLogic() {
        setTitle("真实姓名");
        mToolbarSubtitle.setVisibility(View.VISIBLE);
        nickname = getIntent().getStringExtra("realName");
        etRealName.setText(nickname);
        if (nickname != null && nickname.length() > 0) {
            etRealName.setSelection(etRealName.getText().length());
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

    }


    @OnClick({R.id.tv_submits})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_submits:
                if (!StringUtil.isEmpty(etRealName.getText().toString())) {
                    Intent intent = new Intent();
                    intent.putExtra("realName", etRealName.getText().toString());
                    setResult(102, intent);
                    finish();
                } else {
                    toast("姓名不能为空");
                }
                break;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }
}
