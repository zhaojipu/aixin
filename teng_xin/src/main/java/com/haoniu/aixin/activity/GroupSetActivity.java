package com.haoniu.aixin.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.haoniu.aixin.R;
import com.haoniu.aixin.base.BaseActivity;
import com.haoniu.aixin.entity.EventCenter;
import com.haoniu.aixin.widget.EaseSwitchButton;
import com.hyphenate.chat.EMClient;

import butterknife.BindView;
import butterknife.OnClick;


/**
 * 作   者：赵大帅
 * 描   述: 房间设置
 * 日   期: 2017/12/11 15:00
 * 更新日期: 2017/12/11
 */
public class GroupSetActivity extends BaseActivity {


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
    @BindView(R.id.switch_btn)
    EaseSwitchButton mSwitchBtn;
    @BindView(R.id.img_to_right_2)
    ImageView mImgToRight2;
    @BindView(R.id.ll_notices)
    LinearLayout mLlNotices;
    private String groupId;


    /**
     * 替代onCreate的使用
     *
     * @param bundle
     */
    @Override
    protected void initContentView(Bundle bundle) {
        setContentView(R.layout.activity_group_set);
    }

    /**
     * 初始化逻辑
     */
    @Override
    protected void initLogic() {
        setTitle("群设置");
        if (EMClient.getInstance().groupManager().getGroup(groupId).isMsgBlocked()) {
            mSwitchBtn.openSwitch();
        } else {
            mSwitchBtn.closeSwitch();
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
        groupId = getIntent().getStringExtra("id");
    }


    @OnClick({R.id.switch_btn})
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.switch_btn:
                toggleBlockGroup();
                break;
        }
    }


    private void toggleBlockGroup() {
        if (mSwitchBtn.isSwitchOpen()) {
            showLoading(getResources().getString(R.string.Is_unblock));
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        EMClient.getInstance().groupManager().unblockGroupMessage(groupId);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mSwitchBtn.closeSwitch();
                                dismissLoading();
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dismissLoading();
                                toast("解除群屏蔽失败，请检查网络或稍后重试");
                            }
                        });

                    }
                }
            }).start();

        } else {
            final String st9 = getResources().getString(R.string.group_of_shielding);
            showLoading(getResources().getString(R.string.group_is_blocked));
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        EMClient.getInstance().groupManager().blockGroupMessage(groupId);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mSwitchBtn.openSwitch();
                                dismissLoading();
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dismissLoading();
                                toast(st9);
                            }
                        });
                    }

                }
            }).start();
        }
    }
}
