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
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 *  转账记录
 */

public class TransferRecordFragment extends EaseBaseFragment {

    @BindView(R.id.recycle_view)
    RecyclerView mRecycleView;
    Unbinder unbinder;
    @BindView(R.id.tv_nick)
    TextView tvNick;

    private FundAecordAdapter mTransferAdapter;
    private List<TransferRInfo> mWaterInfos;
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
        tvNick.setVisibility(View.VISIBLE);
        mWaterInfos = new ArrayList<>();
        mTransferAdapter = new FundAecordAdapter(mWaterInfos);
        LinearLayoutManager LinearLayoutManager = new LinearLayoutManager(getContext());
        mRecycleView.setLayoutManager(LinearLayoutManager);
        mRecycleView.setAdapter(mTransferAdapter);
        mTransferAdapter.openLoadAnimation();
        mTransferAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                loadData();
            }
        });
        getData();
    }

    //获取数据
    private void getData() {
        page = 1;
        Map<String, Object> map = new HashMap<>();
        map.put("pageNum", page);
        map.put("pageSize", 20);
        ApiClient.requestNetHandle(getContext(), AppConfig.transferRecord, "", map, new ResultListener() {
            /**
             * 请求成功
             * @param json
             * @param msg
             */
            @Override
            public void onSuccess(String json, String msg) {
                if (json != null) {
                    XLog.json(json);
                    List<TransferRInfo> list = FastJsonUtil.getList(json, "list", TransferRInfo.class);
                    mWaterInfos.clear();
                    if (list != null && list.size() > 0) {
                        mWaterInfos.addAll(list);
                        if (list.size()<20){
                            mTransferAdapter.loadMoreEnd(true);
                        }else {
                            mTransferAdapter.loadMoreComplete();
                        }
                    }else {
                        mTransferAdapter.loadMoreEnd(true);
                    }
                    mTransferAdapter.notifyDataSetChanged();
                }

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
        ApiClient.requestNetHandle(getContext(), AppConfig.transferRecord, "", map, new ResultListener() {
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
                    List<TransferRInfo> list = FastJsonUtil.getList(json, "list", TransferRInfo.class);
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
