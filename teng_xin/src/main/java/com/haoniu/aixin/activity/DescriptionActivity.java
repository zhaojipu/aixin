package com.haoniu.aixin.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.haoniu.aixin.R;
import com.haoniu.aixin.base.BaseActivity;
import com.haoniu.aixin.base.Constant;
import com.haoniu.aixin.entity.EventCenter;

import butterknife.BindView;

/**
 * 作   者：赵大帅
 * 描   述: 说明
 * 邮   箱: 2510722254@qqq.com
 * 日   期: 2017/12/15 16:18
 * 更新日期: 2017/12/15
 */
public class DescriptionActivity extends BaseActivity {
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
    @BindView(R.id.tv_show)
    TextView mTvShow;
    @BindView(R.id.tv_detail_show)
    TextView mTvDetailShow;

    private String type;

    /**
     * 替代onCreate的使用
     *
     * @param bundle
     */
    @Override
    protected void initContentView(Bundle bundle) {
        setContentView(R.layout.activity_description);
    }

    /**
     * 初始化逻辑
     */
    @Override
    protected void initLogic() {
        setTitle("玩法说明");
        if (type.equals(Constant.CAILEI)) {
            mTvShow.setText("红包踩雷描述：");
            mTvDetailShow.setText(R.string.clsm);
        } else {
            mTvShow.setText("红包接龙描述：");
            mTvDetailShow.setText(R.string.dzsm);
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
        type = extras.getString("type", "");
    }

}
