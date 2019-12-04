package com.haoniu.aixin.base;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.haoniu.aixin.R;
import com.haoniu.aixin.entity.EventCenter;
import com.haoniu.aixin.utils.StatusBarUtil;
import com.haoniu.aixin.widget.Loading_view;
import com.lzy.okgo.OkGo;
import com.zds.base.util.BarUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.lang.ref.WeakReference;

import butterknife.ButterKnife;

@SuppressLint({"NewApi", "Registered"})
public abstract class EaseBaseActivity extends AppCompatActivity {

    protected InputMethodManager inputMethodManager;


    protected Toolbar mToolbar;
    protected TextView title;
    protected ImageView img_right;
    protected TextView toolbar_subtitle;
    private LinearLayout ll_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!isTaskRoot()) {
            Intent intent = getIntent();
            String action = intent.getAction();
            if (intent.hasCategory(Intent.CATEGORY_LAUNCHER) && action.equals(Intent.ACTION_MAIN)) {
                finish();
                return;
            }
        }
        Bundle extras = getIntent().getExtras();
        if (null != extras) {
            getBundleExtras(extras);
        }
        EventBus.getDefault().register(this);
        initContentView(savedInstanceState);
        initToolBar();
        initLogic();
        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        ActivityStackManager.getInstance().addActivity(new WeakReference<Activity>(this));

    }

    /**
     *  状态栏是否深色
     * @param isDark
     */
    protected void setIsDark(boolean isDark) {
        if (isDark){
            if (!StatusBarUtil.setStatusBarDarkTheme(this, true)) {
                //如果不支持设置深色风格 为了兼容总不能让状态栏白白的看不清, 于是设置一个状态栏颜色为半透明,
                //这样半透明+白=灰, 状态栏的文字能看得清
                StatusBarUtil.setStatusBarColor(this,0x55000000);
            }
        }

    }

    /**
     * 设置沉浸式
     */
    public void setTransparencyBar(){
        StatusBarUtil.setTranslucentStatus(this);
        setBar();
    }

    @Override
    protected void onResume() {
        super.onResume();
        EaseUI.getInstance().getNotifier().reset();
    }

    /**
     * 初始化Toolbar
     */
    protected void initToolBar() {
        try {
            mToolbar = (Toolbar) findViewById(R.id.toolbar);
            if (null != mToolbar) {
                setSupportActionBar(mToolbar);
                getSupportActionBar().setDisplayShowTitleEnabled(false);
                title = (TextView) findViewById(R.id.toolbar_title);
                img_right = (ImageView) findViewById(R.id.img_right);
                toolbar_subtitle = (TextView) findViewById(R.id.toolbar_subtitle);
                mToolbar.setNavigationIcon(null);
            }
            ll_back = (LinearLayout) findViewById(R.id.ll_back);
            if (ll_back != null) {
                ll_back.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        finish();
                    }
                });
            }
        } catch (Exception e) {

        }
    }

    public void setBar() {
        try {
            View bar = findViewById(R.id.bar);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) bar.getLayoutParams();
            params.height = BarUtils.getStatusBarHeight(this);
            bar.setLayoutParams(params);
        } catch (Exception e) {

        }
    }

    public void setBarGray() {
        try {
            View bar = findViewById(R.id.bar);
            bar.setBackgroundColor(ContextCompat.getColor(this,R.color.tranGray));
        } catch (Exception e) {

        }
    }
    /**
     * 设置 title
     *
     * @param string
     */
    public void setTitle(String string) {
        if (null != title) {
            title.setText(string);
        }
    }

    /**
     * 设置 title
     *
     * @param id
     */
    @Override
    public void setTitle(int id) {
        if (null != title) {
            title.setText(id);
        }
    }


    /**
     * 替代onCreate的使用
     */
    protected abstract void initContentView(Bundle bundle);


    /**
     * 初始化逻辑
     */
    protected abstract void initLogic();

    /**
     * EventBus接收消息
     *
     * @param center 获取事件总线信息
     */
    protected abstract void onEventComing(EventCenter center);

    /**
     * EventBus接收消息
     *
     * @param center 消息接收
     */
    @Subscribe
    public void onEventMainThread(EventCenter center) {
        if (null != center) {
            onEventComing(center);
        }

    }

    /**
     * Bundle  传递数据
     *
     * @param extras
     */
    protected abstract void getBundleExtras(Bundle extras);


    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        ButterKnife.bind(this);
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        ButterKnife.bind(this);
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        super.setContentView(view, params);
        ButterKnife.bind(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:// 点击返回图标事件
                finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        ActivityStackManager.getInstance().removeActivity(new WeakReference<Activity>(this));
        OkGo.getInstance().cancelTag(this);
        super.onDestroy();
    }

    /**
     * 吐司
     *
     * @param msg
     */
    public void toast(String msg) {
        if (msg != null && !"".equals(msg)) {
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        }
    }

    Loading_view dialog;

    public void showLoading() {
        if (dialog != null && dialog.isShowing()) {
            return;
        }
        dialog = new Loading_view(this);
        // dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false);
        // dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("请求网络中...");
        dialog.show();
    }

    public void showLoading(String message) {
        if (dialog != null && dialog.isShowing()) {
            return;
        }
        dialog = new Loading_view(this);
        // dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false);
        //  dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage(message);
        dialog.show();
    }

    public void dismissLoading() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    protected void showSoftInput(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
    }

    protected void hideSoftInput(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    protected void hideSoftKeyboard() {
        if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getCurrentFocus() != null) {
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }
}


