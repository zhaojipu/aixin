package com.haoniu.aixin.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.haoniu.aixin.http.ApiClient;
import com.haoniu.aixin.http.AppConfig;
import com.haoniu.aixin.http.ResultListener;
import com.haoniu.aixin.R;
import com.haoniu.aixin.adapter.RedeemAdapter;
import com.haoniu.aixin.base.BaseActivity;
import com.haoniu.aixin.base.MyApplication;
import com.haoniu.aixin.entity.EventCenter;
import com.haoniu.aixin.entity.UserInfo;
import com.haoniu.aixin.model.CreditInfo;
import com.haoniu.aixin.utils.EventUtil;
import com.zds.base.Toast.ToastUtil;
import com.zds.base.json.FastJsonUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 作   者：赵大帅
 * 描   述: 积分兑换
 * 邮   箱: 2510722254@qqq.com
 * 日   期: 2017/11/28 12:51
 * 更新日期: 2017/11/28
 */
public class RedeemActivity extends BaseActivity {
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
    @BindView(R.id.recycle_view)
    RecyclerView mRecycleView;
    @BindView(R.id.tv_user_money)
    TextView mTvUserMoney;
    RedeemAdapter mAdapter;
    private List<CreditInfo> mList;

    /**
     * 替代onCreate的使用
     *
     * @param bundle
     */
    @Override
    protected void initContentView(Bundle bundle) {
        setContentView(R.layout.activity_redeem);
    }

    /**
     * 初始化逻辑
     */
    @Override
    protected void initLogic() {
        setTitle("积分兑换");
        mToolbarSubtitle.setText("兑换记录");
        mToolbarSubtitle.setVisibility(View.VISIBLE);
        mList = new ArrayList<>();
        mRecycleView.setHasFixedSize(true);
        mRecycleView.setNestedScrollingEnabled(false);
        mRecycleView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new RedeemAdapter(mList);
        mRecycleView.setAdapter(mAdapter);
        getData();
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                duihuan(mList.get(position).getId());
            }
        });
        initUserInfo();
    }


    /**
     * 趣币积分
     */
    private void initUserInfo() {
        UserInfo userInfo = MyApplication.getInstance().getUserInfo();
        if (userInfo != null) {
          //  mTvUserMoney.setText(getResources().getString(R.string.glod) + "： " + StringUtil.getFormatValue2(userInfo.getMoney()) + "    积分： " + userInfo.getCredit());
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
            initUserInfo();
        }

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    /**
     * 获取数据
     */
    private void getData() {
        Map<String, Object> map = new HashMap<>();
        map.put("rows", 1000);
        ApiClient.requestNetHandleByGet(this, AppConfig.scoreList, "正在加载...", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                List<CreditInfo> list = FastJsonUtil.getList(json, CreditInfo.class);
                if (list != null && list.size() > 0) {
                    mList.clear();
                    mList.addAll(list);
                    mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(String msg) {
                ToastUtil.toast(msg);
            }
        });
    }

    private void duihuan(int credit) {
        Map<String, Object> map = new HashMap<>();
        map.put("exchangeId", credit);
        ApiClient.requestNetHandle(this, AppConfig.creditChangeMoney, "兑换中...", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                toast(msg);
                MyApplication.getInstance().UpUserInfo();
            }

            @Override
            public void onFailure(String msg) {
                ToastUtil.toast(msg);
            }
        });
    }

    @OnClick(R.id.toolbar_subtitle)
    public void onViewClicked() {
        startActivity(ExchangeRecordsActivity.class);
    }
}
