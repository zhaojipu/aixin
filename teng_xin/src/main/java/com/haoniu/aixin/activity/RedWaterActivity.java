package com.haoniu.aixin.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.haoniu.aixin.http.ApiClient;
import com.haoniu.aixin.http.AppConfig;
import com.haoniu.aixin.http.ResultListener;
import com.haoniu.aixin.R;
import com.haoniu.aixin.adapter.RedWaterAdapter;
import com.haoniu.aixin.base.BaseActivity;
import com.haoniu.aixin.entity.EventCenter;
import com.haoniu.aixin.entity.RedWaterInfo;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.zds.base.json.FastJsonUtil;
import com.zds.base.log.XLog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 作   者：赵大帅
 * 描   述: 红包流水
 * 邮   箱: 2510722254@qqq.com
 * 日   期: 2017/12/2 11:37
 * 更新日期: 2017/12/2
 *
 * @author Administrator
 */
public class RedWaterActivity extends BaseActivity {

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
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout mRefreshLayout;
    @BindView(R.id.ll_no_data)
    LinearLayout mLlNoData;


    private RedWaterAdapter mWaterAdapter;
    private List<RedWaterInfo> mWaterInfos;
    private int page = 1;

    /**
     * 替代onCreate的使用
     *
     * @param bundle
     */
    @Override
    protected void initContentView(Bundle bundle) {
        setContentView(R.layout.activity_red_water);
    }

    /**
     * 初始化逻辑
     */
    @Override
    protected void initLogic() {
        setTitle("红包记录");
        mWaterInfos = new ArrayList<>();
        mWaterAdapter = new RedWaterAdapter(mWaterInfos);
        LinearLayoutManager LinearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(LinearLayoutManager);
        mRecyclerView.setAdapter(mWaterAdapter);
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                getData();
            }
        });
        mRefreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                loadData();
            }
        });
        mRefreshLayout.autoRefresh();
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

    //获取数据
    private void getData() {
        page = 1;
        Map<String, Object> map = new HashMap<>();
        map.put("page", page);
        ApiClient.requestNetHandleByGet(this, AppConfig.qhbls, "", map, new ResultListener() {
            /**
             * 请求成功
             *
             * @param json
             * @param msg
             */
            @Override
            public void onSuccess(String json, String msg) {
                if (json != null) {
                    XLog.json(json);
                    List<RedWaterInfo> list = FastJsonUtil.getList(json, RedWaterInfo.class);
                    mLlNoData.setVisibility(View.VISIBLE);
                    if (list != null && list.size() > 0) {
                        mWaterInfos.clear();
                        mWaterInfos.addAll(list);
                        mLlNoData.setVisibility(View.GONE);
                    }
                    mWaterAdapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onFinsh() {
                super.onFinsh();
                mRefreshLayout.finishRefresh();
                mRefreshLayout.setLoadmoreFinished(false);
            }

            /**
             * 请求失败
             *
             * @param msg
             */
            @Override
            public void onFailure(String msg) {
                toast(msg);
                mLlNoData.setVisibility(View.VISIBLE);
            }
        });
    }

    //加载更多
    private void loadData() {
        page++;
        Map<String, Object> map = new HashMap<>();
        map.put("page", page);
        ApiClient.requestNetHandleByGet(this, AppConfig.qhbls, "", map, new ResultListener() {
            /**
             * 请求成功
             *
             * @param json
             * @param msg
             */
            @Override
            public void onSuccess(String json, String msg) {
                if (json != null) {
                    XLog.json(json);
                    List<RedWaterInfo> list = FastJsonUtil.getList(json, RedWaterInfo.class);
                    if (list != null && list.size() > 0) {
                        mWaterInfos.addAll(list);
                    } else {
                        mRefreshLayout.setLoadmoreFinished(true);
                    }
                    mWaterAdapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onFinsh() {
                super.onFinsh();
                mRefreshLayout.finishLoadmore();
            }

            /**
             * 请求失败
             *
             * @param msg
             */
            @Override
            public void onFailure(String msg) {
                toast(msg);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @OnClick(R.id.ll_no_data)
    public void onViewClicked() {
        getData();
    }
}
