package com.haoniu.aixin.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
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
import com.zds.base.Toast.ToastUtil;
import com.zds.base.util.StringUtil;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 作   者：赵大帅
 * 描   述:
 * 邮   箱: 2510722254@qqq.com
 * 日   期: 2017/12/29 9:51
 * 更新日期: 2017/12/29
 */
public class ComplaintsActivity extends BaseActivity {
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
    @BindView(R.id.name)
    EditText mName;
    @BindView(R.id.phone)
    EditText mPhone;
    @BindView(R.id.content)
    EditText mContent;
    @BindView(R.id.submit)
    Button mSubmit;

    /**
     * 替代onCreate的使用
     *
     * @param bundle
     */
    @Override
    protected void initContentView(Bundle bundle) {
        setContentView(R.layout.activity_complaints);
    }

    /**
     * 初始化逻辑
     */
    @Override
    protected void initLogic() {
        setTitle("投诉举报");
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


    @OnClick(R.id.submit)
    public void onViewClicked() {
        submit();
    }

    /**
     * 提交投诉意见
     */
    private void submit() {
        if (StringUtil.isEmpty(mName.getText().toString())) {
            toast("姓名不能为空");
            return;
        } else if (StringUtil.isEmpty(mPhone.getText().toString())) {
            toast("手机号不能为空");
            return;
        } else if (StringUtil.isEmpty(mContent.getText().toString())) {
            toast("意见不能为空");
            return;
        }
        Map<String, Object> map = new HashMap<>();
        map.put("phone", mPhone.getText().toString());
        map.put("content", mContent.getText().toString());
        map.put("username", mName.getText().toString());
        ApiClient.requestNetHandle(this, AppConfig.complaints, "正在提交...", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                toast(msg);
                finish();
            }

            @Override
            public void onFailure(String msg) {
                ToastUtil.toast(msg);
            }
        });
    }
}
