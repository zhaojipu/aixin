package com.haoniu.aixin.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.haoniu.aixin.R;
import com.haoniu.aixin.base.BaseActivity;
import com.haoniu.aixin.base.MyApplication;
import com.haoniu.aixin.entity.EventCenter;
import com.haoniu.aixin.entity.UserInfo;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 作   者：赵大帅
 * 描   述: 申请代理显示
 * 邮   箱: 2510722254@qqq.com
 * 日   期: 2017/11/15 11:35
 * 更新日期: 2017/11/15
 */
public class ApplySelectActivity extends BaseActivity {
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
    @BindView(R.id.btn_yiji)
    Button mBtnYiji;
    @BindView(R.id.btn_erji)
    Button mBtnErji;

    /**
     * 替代onCreate的使用
     *
     * @param bundle
     */
    @Override
    protected void initContentView(Bundle bundle) {
        setContentView(R.layout.activity_apply_select);
    }

    /**
     * 初始化逻辑
     */
    @Override
    protected void initLogic() {
        setTitle("申请代理");
        mToolbarSubtitle.setVisibility(View.VISIBLE);
        mToolbarSubtitle.setText("申请记录");
        UserInfo userInfo = MyApplication.getInstance().getUserInfo();
        if (userInfo != null) {
//             if (userInfo.getProxy() == 2) {
//                 mBtnErji.setVisibility(View.GONE);
//            }
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

    @OnClick({R.id.toolbar_subtitle, R.id.btn_yiji, R.id.btn_erji})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_yiji:
                startActivity(new Intent(ApplySelectActivity.this, ApplyActivity.class).putExtra("applyProxy", 1));
                break;
            case R.id.btn_erji:
                startActivity(new Intent(ApplySelectActivity.this, ApplyActivity.class).putExtra("applyProxy", 2));
                break;
            case R.id.toolbar_subtitle:
                startActivity(ApplyRecordActivity.class);
                break;
            default:
        }
    }
}
