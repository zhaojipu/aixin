package com.haoniu.aixin.activity;

import android.content.Intent;
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
import com.haoniu.aixin.adapter.ReviewAdapter;
import com.haoniu.aixin.base.BaseActivity;
import com.haoniu.aixin.entity.ApplyRecordInfo;
import com.haoniu.aixin.entity.EventCenter;
import com.haoniu.aixin.utils.EventUtil;
import com.zds.base.json.FastJsonUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

/**
 * 作   者：赵大帅
 * 描   述: 我的审核
 * 邮   箱: 2510722254@qqq.com
 * 日   期: 2017/12/27 12:28
 * 更新日期: 2017/12/27
 */
public class MyReviewActivity extends BaseActivity {


    @BindView(R.id.recycle_view)
    RecyclerView mRecycleView;
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

    private ReviewAdapter mAdapter;
    private int page = 1;
    private List<ApplyRecordInfo> list;


    /**
     * 替代onCreate的使用
     *
     * @param bundle
     */
    @Override
    protected void initContentView(Bundle bundle) {
        setContentView(R.layout.activity_my_review);
    }

    /**
     * 初始化逻辑
     */
    @Override
    protected void initLogic() {
        setTitle("我的审核");
        list = new ArrayList<>();
        mAdapter = new ReviewAdapter(list);
        mRecycleView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter.openLoadAnimation();
        mRecycleView.setAdapter(mAdapter);
        mAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                mRecycleView.post(new Runnable() {
                    @Override
                    public void run() {
                        loadData();
                    }
                });
            }
        });
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                startActivity(new Intent(MyReviewActivity.this, AuditActivity.class).putExtra("ApplyRecordInfo", list.get(position)));
            }
        });
        getData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAdapter.notifyDataSetChanged();
    }

    /**
     * EventBus接收消息
     *
     * @param center 获取事件总线信息
     */
    @Override
    protected void onEventComing(EventCenter center) {
        if (center.getEventCode() == EventUtil.FLUSHAUDIT) {
            getData();
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

    /**
     * 获取审核数据
     */
    private void getData() {
        page = 1;
        Map<String, Object> map = new HashMap<>();
        map.put("type", "audit");
        map.put("page", page);
        ApiClient.requestNetHandleByGet(this, AppConfig.applyRecord, "加载中...", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                List<ApplyRecordInfo> applyRecordInfos = FastJsonUtil.getList(json, ApplyRecordInfo.class);
                list.clear();
                if (applyRecordInfos != null && applyRecordInfos.size() > 0) {
                    list.addAll(applyRecordInfos);
                }
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFinsh() {
                super.onFinsh();
                mAdapter.loadMoreComplete();
            }

            @Override
            public void onFailure(String msg) {
                toast(msg);
            }
        });

    }

    /**
     * 加载更多
     */
    private void loadData() {
        page++;
        Map<String, Object> map = new HashMap<>();
        map.put("type", "audit");
        map.put("page", page);
        ApiClient.requestNetHandleByGet(this, AppConfig.applyRecord, "", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                List<ApplyRecordInfo> applyRecordInfos = FastJsonUtil.getList(json, ApplyRecordInfo.class);
                if (applyRecordInfos != null && applyRecordInfos.size() > 0) {
                    list.addAll(applyRecordInfos);
                    mAdapter.notifyDataSetChanged();
                    mAdapter.loadMoreComplete();
                } else {
                    mAdapter.loadMoreEnd(true);
                }
            }

            @Override
            public void onFailure(String msg) {
                toast(msg);
                mAdapter.loadMoreFail();
            }
        });

    }
}
