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
import com.haoniu.aixin.adapter.AgentWaterAdapter;
import com.haoniu.aixin.base.BaseActivity;
import com.haoniu.aixin.entity.EventCenter;
import com.haoniu.aixin.entity.QBLSInfo;
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

/**
 * 作   者：赵大帅
 * 描   述: 收益流水
 * 邮   箱: 2510722254@qqq.com
 * 日   期: 2017/12/2 11:37
 * 更新日期: 2017/12/2
 */
public class CenterWaterActivity extends BaseActivity {

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


    private AgentWaterAdapter mAgentWaterAdapter;
    private List<QBLSInfo> mQBLSInfoList;
    private int page = 1;
    private String title;
    private String objectTypeStr;

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
        setTitle(title);
        mQBLSInfoList = new ArrayList<>();
        mAgentWaterAdapter = new AgentWaterAdapter(mQBLSInfoList);
        LinearLayoutManager LinearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(LinearLayoutManager);
        mRecyclerView.setAdapter(mAgentWaterAdapter);
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
        title = extras.getString("title", "");
        objectTypeStr = extras.getString("type", "");
    }

    //获取数据
    private void getData() {
        page = 1;
        Map<String, Object> map = new HashMap<>();
        map.put("pages", page);
        map.put("objectTypeStr", objectTypeStr);
        ApiClient.requestNetHandleByGet(this, AppConfig.getQBLS, "", map, new ResultListener() {
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
                    List<QBLSInfo> list = FastJsonUtil.getList(json, "page", QBLSInfo.class);
                    if (list != null && list.size() > 0) {
                        mQBLSInfoList.clear();
                        mQBLSInfoList.addAll(list);
                    }
                    mAgentWaterAdapter.notifyDataSetChanged();
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
            }
        });
    }

    //加载更多
    private void loadData() {
        page++;
        Map<String, Object> map = new HashMap<>();
        map.put("page", page);
        map.put("objectTypeStr", objectTypeStr);
        ApiClient.requestNetHandleByGet(this, AppConfig.getQBLS, "", map, new ResultListener() {
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
                    List<QBLSInfo> list = FastJsonUtil.getList(json, "page", QBLSInfo.class);
                    if (list != null && list.size() > 0) {
                        mQBLSInfoList.addAll(list);
                    } else {
                        mRefreshLayout.setLoadmoreFinished(true);
                    }
                    mAgentWaterAdapter.notifyDataSetChanged();
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
}
