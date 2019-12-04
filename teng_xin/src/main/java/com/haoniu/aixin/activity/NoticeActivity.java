package com.haoniu.aixin.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.haoniu.aixin.http.AppConfig;
import com.haoniu.aixin.R;
import com.haoniu.aixin.base.BaseActivity;
import com.haoniu.aixin.base.MyApplication;
import com.haoniu.aixin.domain.EaseUser;
import com.haoniu.aixin.entity.EventCenter;
import com.haoniu.aixin.utils.EaseUserUtils;
import com.haoniu.aixin.utils.StringUtil;
import com.haoniu.aixin.widget.EaseImageView;
import com.zds.base.ImageLoad.GlideUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * 作   者：赵大帅
 * 描   述: 公告
 * 日   期: 2017/11/30 15:45
 * 更新日期: 2017/11/30
 */
public class NoticeActivity extends BaseActivity {

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
    @BindView(R.id.img_head)
    EaseImageView mImgHead;
    @BindView(R.id.tv_name)
    TextView mTvName;
    @BindView(R.id.tv_time)
    TextView mTvTime;
    @BindView(R.id.et_notice)
    EditText mEtNotice;
    private String noticeString;
    private Long time;
    private Boolean isMyroom = false;
    private String owner;

    /**
     * 替代onCreate的使用
     *
     * @param bundle
     */
    @Override
    protected void initContentView(Bundle bundle) {
        setContentView(R.layout.activity_notice);
    }

    /**
     * 初始化逻辑
     */
    @Override
    protected void initLogic() {
        setTitle("群公告");
        mToolbarSubtitle.setText("确定");
        mToolbarSubtitle.setVisibility(View.VISIBLE);
        mEtNotice.setText(noticeString);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm");
        mTvTime.setText(sdf.format(new Date(time)));
        MyApplication.getInstance().setAvatar(mImgHead);
        EaseUser easeUser = EaseUserUtils.getUserInfo(owner);
        GlideUtils.loadImageViewLoding(AppConfig.checkimg( easeUser.getAvatar()), mImgHead, R.mipmap.img_default_avatar);
        mTvName.setText(easeUser.getNickname());
        if (owner.equals(MyApplication.getInstance().getUserInfo().getIdh() + "")) {

        } else {
            mEtNotice.setFocusable(false);
            mEtNotice.setEnabled(false);
            mEtNotice.setHint("");
            mToolbarSubtitle.setVisibility(View.GONE);
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
        noticeString = extras.getString("notice");
        owner = extras.getString("owner");
        time = extras.getLong("time", System.currentTimeMillis());
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @OnClick(R.id.toolbar_subtitle)
    public void onViewClicked() {
        if (!StringUtil.isEmpty(mEtNotice.getText().toString())) {
            setResult(RESULT_OK, new Intent().putExtra("notice", mEtNotice.getText().toString()));
        }
        finish();
    }
}
