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
import com.haoniu.aixin.base.Constant;
import com.haoniu.aixin.base.MyHelper;
import com.haoniu.aixin.entity.ApplyRecordInfo;
import com.haoniu.aixin.entity.ApplyStateInfo;
import com.haoniu.aixin.entity.EventCenter;
import com.haoniu.aixin.utils.EventUtil;
import com.haoniu.aixin.widget.CommonDialog;
import com.zds.base.Toast.ToastUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 作   者：赵大帅
 * 描   述: 审核
 * 邮   箱: 2510722254@qqq.com
 * 日   期: 2017/12/28 16:04
 * 更新日期: 2017/12/28
 */
public class AuditActivity extends BaseActivity {
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
    TextView mTvName;
    @BindView(R.id.tv_phone)
    TextView mTvPhone;
    @BindView(R.id.tv_reason)
    TextView mTvReason;
    @BindView(R.id.tv_tongguo)
    Button mTvTongguo;
    @BindView(R.id.tv_jujue)
    Button mTvJujue;
    @BindView(R.id.tv_state)
    TextView mTvState;
    @BindView(R.id.ll_result)
    LinearLayout mLlResult;
    @BindView(R.id.ll_btn)
    LinearLayout mLlBtn;
    @BindView(R.id.tv_wx)
    TextView tvWx;
    /**
     * 审核信息
     */
    private ApplyRecordInfo mApplyRecordInfo;
    CommonDialog.Builder builder;
    EditText editText;

    /**
     * 替代onCreate的使用
     *
     * @param bundle
     */
    @Override
    protected void initContentView(Bundle bundle) {
        setContentView(R.layout.activity_audit);
    }

    /**
     * 初始化逻辑
     */
    @Override
    protected void initLogic() {
        setTitle("审核详情");
        if (mApplyRecordInfo != null) {
            mTvName.setText(mApplyRecordInfo.getNickname());
            mTvPhone.setText(mApplyRecordInfo.getPhone());
            mTvReason.setText(mApplyRecordInfo.getRemark());
            tvWx.setText(mApplyRecordInfo.getWeixinNum());
            // 审核通过
            if (mApplyRecordInfo.getStatus() == Constant.AUDITSTATEPASSED) {
                mLlBtn.setVisibility(View.GONE);
                mLlResult.setVisibility(View.VISIBLE);
                mTvState.setText("通过");
                //拒绝
            } else if (mApplyRecordInfo.getStatus() == Constant.AUDITSTATEBEREJECTED) {
                mLlBtn.setVisibility(View.GONE);
                mLlResult.setVisibility(View.VISIBLE);
                mTvState.setText("被拒绝");
                //审核中
            } else {
                mLlBtn.setVisibility(View.VISIBLE);
                mLlResult.setVisibility(View.GONE);
            }
            ApplyStateInfo applyStateInfo = new ApplyStateInfo(mApplyRecordInfo.getId() + "");
            applyStateInfo.setApply_is_read(Constant.MSG_READ);
            MyHelper.getInstance().saveApplyStateInfo(applyStateInfo);

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
        mApplyRecordInfo = (ApplyRecordInfo) extras.getParcelable("ApplyRecordInfo");
    }


    @OnClick({R.id.tv_tongguo, R.id.tv_jujue})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_tongguo:
                agreeOrRefuse("", 1);
                break;
            case R.id.tv_jujue:
                refuseAlert();
                break;
            default:
        }
    }

    /**
     * 同意申请 or 拒绝  state 1通过审核 2审核失败
     */
    private void agreeOrRefuse(String reason, int status) {
        if (mApplyRecordInfo == null) {
            toast("获取审核信息失败");
            return;
        }
        Map<String, Object> map = new HashMap<>();
        map.put("status", status);
        map.put("content", reason);
        map.put("userProxyDetailId", mApplyRecordInfo.getId());
        ApiClient.requestNetHandle(this, AppConfig.review, "正在提交...", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                toast(msg);
                finish();
                EventBus.getDefault().post(new EventCenter(EventUtil.FLUSHAUDIT));
            }

            @Override
            public void onFailure(String msg) {
                ToastUtil.toast(msg);
            }
        });
    }

    /**
     * 拒绝弹窗
     */
    private void refuseAlert() {
        if (builder != null && builder.isShowing()) {
            builder.dismiss();
        }
        builder = new CommonDialog.Builder(this)
                .setView(R.layout.dialog_audit_refuse).center().loadAniamtion()
                .setOnClickListener(R.id.tv_submit, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (editText != null && editText.getText().toString() != null && !editText.getText().toString().equals("")) {
                            if (builder != null && builder.isShowing()) {
                                builder.dismiss();
                            }
                            agreeOrRefuse(editText.getText().toString(), 2);
                        } else {
                            toast("请输入拒绝理由");
                            return;
                        }
                    }
                }).setOnClickListener(R.id.img_close, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (builder != null && builder.isShowing()) {
                            builder.dismiss();
                        }
                    }
                });
        CommonDialog commonDialog = builder.create();
        editText = commonDialog.getView(R.id.et_content);
        commonDialog.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }
}
