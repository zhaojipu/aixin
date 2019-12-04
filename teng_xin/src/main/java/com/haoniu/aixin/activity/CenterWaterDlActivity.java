package com.haoniu.aixin.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.haoniu.aixin.R;
import com.haoniu.aixin.base.BaseActivity;
import com.haoniu.aixin.entity.EventCenter;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 作   者：赵大帅
 * 描   述: 代理收益流水
 * 邮   箱: 2510722254@qqq.com
 * 日   期: 2017/12/2 11:37
 * 更新日期: 2017/12/2
 *
 * @author Administrator
 */
public class CenterWaterDlActivity extends BaseActivity {


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


    /**
     * 替代onCreate的使用
     *
     * @param bundle
     */
    @Override
    protected void initContentView(Bundle bundle) {
        setContentView(R.layout.activity_center_water_list);
    }

    /**
     * 初始化逻辑
     */
    @Override
    protected void initLogic() {
        setTitle("我的收益");
        mToolbarSubtitle.setText("我的团队");
        mToolbarSubtitle.setVisibility(View.VISIBLE);
        CenterWaterDlFragment centerWaterDlFragment1 = new CenterWaterDlFragment();
        centerWaterDlFragment1.setArguments(getIntent().getExtras());
        getSupportFragmentManager().beginTransaction().add(R.id.container, centerWaterDlFragment1).commit();
    }


    @OnClick({R.id.toolbar_subtitle})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.toolbar_subtitle:
                startActivity(new Intent(CenterWaterDlActivity.this,MyMemberListActivity.class).putExtra("Invite",0));
                break;
            default:
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


}
