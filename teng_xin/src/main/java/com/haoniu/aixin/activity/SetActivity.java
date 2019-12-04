package com.haoniu.aixin.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.haoniu.aixin.R;
import com.haoniu.aixin.base.BaseActivity;
import com.haoniu.aixin.base.MyHelper;
import com.haoniu.aixin.base.MyModel;
import com.haoniu.aixin.entity.EventCenter;
import com.haoniu.aixin.utils.EventUtil;
import com.haoniu.aixin.widget.EaseSwitchButton;
import com.hyphenate.EMCallBack;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * 设置
 *
 * @author Administrator
 */

public class SetActivity extends BaseActivity {


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
    @BindView(R.id.ll_anquan)
    LinearLayout llAnquan;
    @BindView(R.id.ll_tongyong)
    LinearLayout llTongyong;
    @BindView(R.id.ll_about)
    LinearLayout llAbout;
    @BindView(R.id.ll_logout)
    LinearLayout logout;
    @BindView(R.id.switch_btn)
    EaseSwitchButton switchBtn;
    @BindView(R.id.ll_notices)
    LinearLayout llNotices;

    private MyModel settingsModel;

    /**
     * 替代onCreate的使用
     *
     * @param bundle
     */
    @Override
    protected void initContentView(Bundle bundle) {
        setContentView(R.layout.activity_set);
    }


    /**
     * 初始化逻辑
     */
    @Override
    protected void initLogic() {
        setTitle("设置");
        settingsModel = MyHelper.getInstance().getModel();
        if (settingsModel.getSettingMsgNotification()) {
            switchBtn.openSwitch();
        } else {
            switchBtn.closeSwitch();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
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
    protected void onResume() {
        super.onResume();
    }

    private void logout() {
        String st = getResources().getString(R.string.Are_logged_out);
        showLoading(st);
        MyHelper.getInstance().logout(false, new EMCallBack() {

            @Override
            public void onSuccess() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dismissLoading();
                        // show login screen
                        SetActivity.this.finish();
                        EventBus.getDefault().post(new EventCenter(EventUtil.LOSETOKEN, "关闭"));
                    }
                });
            }

            @Override
            public void onProgress(int progress, String status) {

            }

            @Override
            public void onError(int code, String message) {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        dismissLoading();
                        Toast.makeText(SetActivity.this, "退出失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @OnClick({R.id.ll_anquan, R.id.ll_tongyong, R.id.ll_logout,R.id.switch_btn, R.id.ll_about})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_anquan:
                startActivity(AccountSecurityActivity.class);
                break;
            case R.id.ll_tongyong:
                startActivity(CurrencyActivity.class);
                break;
            case R.id.ll_about:
                break;
            //声音
            case R.id.switch_btn:
                if (switchBtn.isSwitchOpen()) {
                    switchBtn.closeSwitch();
                    settingsModel.setSettingMsgNotification(false);
                    settingsModel.setSettingMsgVibrate(false);
                    settingsModel.setSettingMsgSound(false);
                } else {
                    switchBtn.openSwitch();
                    settingsModel.setSettingMsgNotification(true);
                    settingsModel.setSettingMsgNotification(true);
                    settingsModel.setSettingMsgVibrate(true);
                    settingsModel.setSettingMsgSound(true);
                }
                break;
            case R.id.ll_logout:
                logout();
                break;
            default:
                break;
        }
    }
}
