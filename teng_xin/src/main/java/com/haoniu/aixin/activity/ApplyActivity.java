package com.haoniu.aixin.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.haoniu.aixin.http.ApiClient;
import com.haoniu.aixin.http.AppConfig;
import com.haoniu.aixin.http.ResultListener;
import com.haoniu.aixin.R;
import com.haoniu.aixin.base.BaseActivity;
import com.haoniu.aixin.entity.EventCenter;
import com.zds.base.util.StringUtil;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 作   者：赵大帅
 * 描   述: 申请代理
 * 邮   箱: 2510722254@qqq.com
 * 日   期: 2017/12/27 16:44
 * 更新日期: 2017/12/27
 */
public class ApplyActivity extends BaseActivity {
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
    @BindView(R.id.tv_name)
    EditText mTvName;
    @BindView(R.id.tv_phone)
    EditText mTvPhone;
    @BindView(R.id.tv_reason)
    EditText mTvReason;
    @BindView(R.id.tv_submit)
    TextView mTvSubmit;
    @BindView(R.id.tv_wx)
    EditText mTvWx;
    /**
     * 申请代理类型 （1一级代理，2二级代理）
     */
    private int applyProxy;

    /**
     * 替代onCreate的使用
     *
     * @param bundle
     */
    @Override
    protected void initContentView(Bundle bundle) {
        setContentView(R.layout.activity_apply);
    }

    /**
     * 初始化逻辑
     */
    @Override
    protected void initLogic() {
        setTitle("申请代理");
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
        applyProxy = extras.getInt("applyProxy", 1);
    }


    @OnClick(R.id.tv_submit)
    public void onViewClicked() {
        apply();
    }

    /**
     * 代理申请
     */
    private void apply() {
        if (StringUtil.isEmpty(mTvName.getText().toString())) {
            toast("请输入姓名");
            return;
        } else if (StringUtil.isEmpty(mTvPhone.getText().toString())) {
            toast("请输入手机号");
            return;
        } else if (StringUtil.isEmpty(mTvReason.getText().toString())) {
            toast("请输入申请理由");
            return;
        }
        Map<String, Object> map = new HashMap<>();
        //代理类型
        map.put("applyProxy", applyProxy);
        //姓名
        map.put("nickname", mTvName.getText().toString());
        //手机号
        map.put("phone", mTvPhone.getText().toString());
        //申请理由
        map.put("remark", mTvReason.getText().toString());
        //微信号
        map.put("weixinNum", StringUtil.isEmpty(mTvWx.getText().toString()) ? "" : mTvWx.getText().toString());
        ApiClient.requestNetHandle(this, AppConfig.apply, "正在提交...", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                toast(msg);
                finish();
            }

            @Override
            public void onFailure(String msg) {
                toast(msg);
            }
        });
    }

}
