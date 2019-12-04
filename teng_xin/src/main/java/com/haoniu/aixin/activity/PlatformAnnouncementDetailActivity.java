package com.haoniu.aixin.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.haoniu.aixin.http.ApiClient;
import com.haoniu.aixin.http.AppConfig;
import com.haoniu.aixin.http.ResultListener;
import com.haoniu.aixin.R;
import com.haoniu.aixin.base.BaseActivity;
import com.haoniu.aixin.base.Constant;
import com.haoniu.aixin.base.MyHelper;
import com.haoniu.aixin.entity.EventCenter;
import com.haoniu.aixin.entity.MsgStateInfo;
import com.haoniu.aixin.entity.NoticeInfo;
import com.haoniu.aixin.utils.EventUtil;
import com.haoniu.aixin.utils.StringUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 作   者：赵大帅
 * 描   述: 公告详情
 * 邮   箱: 2510722254@qqq.com
 * 日   期: 2017/12/30 12:14
 * 更新日期: 2017/12/30
 *
 * @author 赵大帅
 */
public class PlatformAnnouncementDetailActivity extends BaseActivity {
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
    @BindView(R.id.tv_time)
    TextView mTvTime;
    @BindView(R.id.tv_content)
    TextView mTvContent;
    private NoticeInfo mInfo;

    /**
     * 替代onCreate的使用
     *
     * @param bundle
     */
    @Override
    protected void initContentView(Bundle bundle) {
        setContentView(R.layout.activity_platform_announcement_detail);
    }

    /**
     * 初始化逻辑
     */
    @Override
    protected void initLogic() {
        if (mInfo != null) {
            setTitle(mInfo.getNoticeTitle());
            mTvTime.setText( StringUtil.formatDateMinute2(mInfo.getCreateTime()));
            mTvContent.setText(mInfo.getNoticeContent());
            changeState(mInfo.getNoticeId() + "");
            MsgStateInfo msgStateInfo = new MsgStateInfo(mInfo.getNoticeId() + "");
            msgStateInfo.setMsg_is_read(Constant.MSG_READ);
            MyHelper.getInstance().saveMsgStateInfo(msgStateInfo);
        } else {
            setTitle("公告");
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
     * 修改消息状态已读
     */
    private void changeState(String id) {
        Map<String, Object> map = new HashMap<>();
        map.put("noticeInfoId", id);
        ApiClient.requestNetHandle(this, AppConfig.updateStatus, "", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                EventBus.getDefault().post(new EventCenter(EventUtil.UNREADCOUNT));
            }

            @Override
            public void onFailure(String msg) {

            }
        });
    }

    /**
     * Bundle  传递数据
     *
     * @param extras
     */
    @Override
    protected void getBundleExtras(Bundle extras) {
        mInfo = extras.getParcelable("notice");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }
}
