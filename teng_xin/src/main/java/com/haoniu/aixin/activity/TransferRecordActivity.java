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
import com.haoniu.aixin.R;
import com.haoniu.aixin.adapter.FundAecordAdapter;
import com.haoniu.aixin.base.BaseActivity;
import com.haoniu.aixin.entity.EventCenter;
import com.haoniu.aixin.entity.TransferRInfo;
import com.haoniu.aixin.http.ApiClient;
import com.haoniu.aixin.http.AppConfig;
import com.haoniu.aixin.http.ResultListener;
import com.zds.base.Toast.ToastUtil;
import com.zds.base.json.FastJsonUtil;
import com.zds.base.log.XLog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

/**
 * 作   者：赵大帅
 * 描   述: 转账记录
 * 邮   箱: 2510722254@qqq.com
 * 日   期: 2017/12/2 11:37
 * 更新日期: 2017/12/2
 */
public class TransferRecordActivity extends BaseActivity {

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

    private FundAecordAdapter mTransferAdapter;
    private List<TransferRInfo> mWaterInfos;
    private int page = 1;

    /**
     * 替代onCreate的使用
     *
     * @param bundle
     */
    @Override
    protected void initContentView(Bundle bundle) {
        setContentView(R.layout.activity_transfer_record);
    }

    /**
     * 初始化逻辑
     */
    @Override
    protected void initLogic() {
        setTitle("转账记录");
        mWaterInfos = new ArrayList<>();
        mTransferAdapter = new FundAecordAdapter(mWaterInfos);
        LinearLayoutManager LinearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(LinearLayoutManager);
        mRecyclerView.setAdapter(mTransferAdapter);
        mTransferAdapter.openLoadAnimation();
        mTransferAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                loadData();
            }
        });
        getData();
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
        map.put("pageNum", page);
        map.put("pageSize", 20);
        ApiClient.requestNetHandle(this, AppConfig.transferRecord, "", map, new ResultListener() {
            /**
             * 请求成功
             * @param json
             * @param msg
             */
            @Override
            public void onSuccess(String json, String msg) {
                if (json != null) {
                    XLog.json(json);
                    List<TransferRInfo> list = FastJsonUtil.getList(json,"list", TransferRInfo.class);
                    if (list != null && list.size() > 0) {
                        mWaterInfos.clear();
                        mWaterInfos.addAll(list);
                    }
                    mTransferAdapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onFinsh() {
                super.onFinsh();
                mTransferAdapter.loadMoreComplete();
            }

            /**
             * 请求失败
             *
             * @param msg
             */
            @Override
            public void onFailure(String msg) {
                ToastUtil.toast(msg);
            }
        });
    }

    //加载更多
    private void loadData() {
        page++;
        Map<String, Object> map = new HashMap<>();
        map.put("pageNum", page);
        map.put("pageSize", 20);
        ApiClient.requestNetHandle(this, AppConfig.transferRecord, "", map, new ResultListener() {
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
                    List<TransferRInfo> list = FastJsonUtil.getList(json,"list", TransferRInfo.class);
                    if (list != null && list.size() > 0) {
                        mWaterInfos.addAll(list);
                        mTransferAdapter.notifyDataSetChanged();
                        mTransferAdapter.loadMoreComplete();
                    } else {
                        mTransferAdapter.loadMoreEnd(true);
                    }
                }

            }

            @Override
            public void onFinsh() {
                super.onFinsh();
            }

            /**
             * 请求失败
             *
             * @param msg
             */
            @Override
            public void onFailure(String msg) {
                ToastUtil.toast(msg);
                mTransferAdapter.loadMoreFail();
            }
        });
    }
}
