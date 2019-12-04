package com.haoniu.aixin.activity.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.haoniu.aixin.R;
import com.haoniu.aixin.adapter.FundAecordAdapter;
import com.haoniu.aixin.entity.EventCenter;
import com.haoniu.aixin.entity.RedWaterInfo;
import com.haoniu.aixin.http.ApiClient;
import com.haoniu.aixin.http.AppConfig;
import com.haoniu.aixin.http.ResultListener;
import com.zds.base.json.FastJsonUtil;
import com.zds.base.log.XLog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by zzx on 2019/01/16/下午 12:20
 */

public class RedWaterFragment extends EaseBaseFragment {

    @BindView(R.id.recycle_view)
    RecyclerView mRecycleView;
    Unbinder unbinder;
    @BindView(R.id.tv_nick)
    TextView tvNick;

    private FundAecordAdapter mWaterAdapter;
    private List<RedWaterInfo> mWaterInfos;
    private int page = 1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_transfer, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void initLogic() {
        tvNick.setVisibility(View.GONE);
        mWaterInfos = new ArrayList<>();
        mWaterAdapter = new FundAecordAdapter(mWaterInfos);
        LinearLayoutManager LinearLayoutManager = new LinearLayoutManager(getContext());
        mRecycleView.setLayoutManager(LinearLayoutManager);
        mRecycleView.setAdapter(mWaterAdapter);
        mWaterAdapter.openLoadAnimation();
        mWaterAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                loadData();
            }
        }, mRecycleView);
        getData();
    }

    //获取数据
    private void getData() {
        page = 1;
        Map<String, Object> map = new HashMap<>();
        map.put("pageNum", page);
        map.put("pageSize", 20);
        ApiClient.requestNetHandle(getContext(), AppConfig.qhbls, "", map, new ResultListener() {
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
                    List<RedWaterInfo> list = FastJsonUtil.getList(json,"list", RedWaterInfo.class);
                    if (list != null && list.size() > 0) {
                        mWaterInfos.clear();
                        mWaterInfos.addAll(list);
                        mWaterAdapter.loadMoreComplete();
                    }
                    mWaterAdapter.notifyDataSetChanged();
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
                toast(msg);
                mWaterAdapter.loadMoreFail();
            }
        });
    }

    //加载更多
    private void loadData() {
        page++;
        Map<String, Object> map = new HashMap<>();
        map.put("pageNum", page);
        map.put("pageSize", 20);
        ApiClient.requestNetHandle(getContext(), AppConfig.qhbls, "", map, new ResultListener() {
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
                    List<RedWaterInfo> list = FastJsonUtil.getList(json, "list",RedWaterInfo.class);
                    if (list != null && list.size() > 0) {
                        mWaterInfos.addAll(list);
                        mWaterAdapter.loadMoreComplete();
                    } else {
                        mWaterAdapter.loadMoreEnd(true);
                    }
                    mWaterAdapter.notifyDataSetChanged();
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
                mWaterAdapter.loadMoreFail();
                toast(msg);
            }
        });
    }

    @Override
    protected void onEventComing(EventCenter center) {

    }

    @Override
    protected void getBundleExtras(Bundle extras) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
