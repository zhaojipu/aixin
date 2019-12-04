package com.haoniu.aixin.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androidkun.xtablayout.XTabLayout;
import com.haoniu.aixin.R;
import com.haoniu.aixin.activity.fragment.MyPacketDetaiTXFragment;
import com.haoniu.aixin.activity.fragment.MyPacketDetailFragment;
import com.haoniu.aixin.activity.fragment.RedWaterFragment;
import com.haoniu.aixin.activity.fragment.TransferRecordFragment;
import com.haoniu.aixin.adapter.ViewPageAdapter;
import com.haoniu.aixin.base.BaseActivity;
import com.haoniu.aixin.base.MyApplication;
import com.haoniu.aixin.entity.EventCenter;
import com.haoniu.aixin.entity.UserInfo;
import com.haoniu.aixin.utils.EventUtil;
import com.haoniu.aixin.widget.MyViewPage;
import com.zds.base.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author 赵大帅
 *         日期 2018/12/26
 *         描述 我的钱包
 */
public class NewMyPacketActivity extends BaseActivity {
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
    @BindView(R.id.tv_cz)
    TextView tvCz;
    @BindView(R.id.tv_zz)
    TextView tvZz;
    @BindView(R.id.tv_tx)
    TextView tvTx;
    @BindView(R.id.tv_hb)
    TextView tvHb;
    @BindView(R.id.llayout_title_1)
    RelativeLayout mLlayoutTitle1;
    @BindView(R.id.tv_money)
    TextView mTvMoney;
    @BindView(R.id.tb_layout)
    XTabLayout mTbLayout;
    @BindView(R.id.my_vp)
    MyViewPage mMyVp;

    private ViewPageAdapter mAdapter;
    private List<Fragment> fgFragment;
    private String[] titel = {"充值记录", "转账记录", "提现记录", "红包记录"};

    @Override
    protected void initContentView(Bundle bundle) {
        setContentView(R.layout.activity_new_my_packet);
    }

    @Override
    protected void initLogic() {
        setTitle("账单");
        initFg();
        MyApplication.getInstance().UpUserInfo();
        UserInfo userInfo = MyApplication.getInstance().getUserInfo();
        if (userInfo != null) {
            mTvMoney.setText("￥" + StringUtil.getFormatValue2(userInfo.getMoney()));
        }
    }

    /**
     * EventBus接收消息
     *
     * @param center 获取事件总线信息
     */
    @Override
    protected void onEventComing(EventCenter center) {
        if (center.getEventCode() == EventUtil.FLUSHUSERINFO) {
            UserInfo userInfo = MyApplication.getInstance().getUserInfo();
            if (userInfo != null) {
                mTvMoney.setText("￥" + StringUtil.getFormatValue2(userInfo.getMoney()));
            }
        }
    }

    /**
     * 初始化
     */
    private void initFg() {
        fgFragment = new ArrayList<>();
        MyPacketDetailFragment fragment = new MyPacketDetailFragment();
        fgFragment.add(fragment);
        fgFragment.add(new TransferRecordFragment());
        MyPacketDetaiTXFragment fragment1 = new MyPacketDetaiTXFragment();
        fgFragment.add(fragment1);
        fgFragment.add(new RedWaterFragment());
        mAdapter = new ViewPageAdapter(getSupportFragmentManager(), fgFragment, titel);
        mMyVp.setAdapter(mAdapter);
        mMyVp.setOffscreenPageLimit(4);
        mTbLayout.setupWithViewPager(mMyVp);
    }

    @Override
    protected void getBundleExtras(Bundle extras) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }


}
