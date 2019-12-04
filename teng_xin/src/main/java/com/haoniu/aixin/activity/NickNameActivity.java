package com.haoniu.aixin.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.haoniu.aixin.R;
import com.haoniu.aixin.base.BaseActivity;
import com.haoniu.aixin.entity.EventCenter;
import com.zds.base.util.StringUtil;

import butterknife.BindView;
import butterknife.OnClick;


/**
 * 修改昵称
 */

public class NickNameActivity extends BaseActivity {

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
    @BindView(R.id.et_nickname)
    EditText mEtNickname;
    private String nickname;


    @Override
    protected void initContentView(Bundle bundle) {
        setContentView(R.layout.activity_nickname);
    }

    /**
     * 初始化逻辑
     */
    @Override
    protected void initLogic() {
        setTitle("名字修改");
        mToolbarSubtitle.setText("完成");
        mToolbarSubtitle.setVisibility(View.VISIBLE);
        nickname = getIntent().getStringExtra("nickname");
        mEtNickname.setText(nickname);
        if (nickname != null && nickname.length() > 0) {
            mEtNickname.setSelection(mEtNickname.getText().length());
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


    @OnClick({R.id.toolbar_subtitle})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.toolbar_subtitle:
                if (!StringUtil.isEmpty(mEtNickname.getText().toString())) {
                    Intent intent = new Intent();
                    intent.putExtra("nickName", mEtNickname.getText().toString());
                    setResult(101, intent);
                    finish();
                } else {
                    toast("名字不能为空");
                }
                break;
        }
    }


}
