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
import com.haoniu.aixin.entity.ChongzhiInfo;
import com.haoniu.aixin.entity.EventCenter;
import com.haoniu.aixin.http.ApiClient;
import com.haoniu.aixin.http.AppConfig;
import com.haoniu.aixin.http.ResultListener;
import com.zds.base.Toast.ToastUtil;
import com.zds.base.json.FastJsonUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 充值
 */

public class MyPacketDetailFragment extends EaseBaseFragment {

    @BindView(R.id.recycle_view)
    RecyclerView mRecycleView;
    Unbinder unbinder;

    FundAecordAdapter expenditureDetailsAdapter;
    List<ChongzhiInfo> list;
    int page = 1;
    @BindView(R.id.tv_nick)
    TextView tvNick;

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
        list = new ArrayList<>();
        expenditureDetailsAdapter = new FundAecordAdapter(list);
        LinearLayoutManager LinearLayoutManager = new LinearLayoutManager(getContext());
        mRecycleView.setLayoutManager(LinearLayoutManager);
        mRecycleView.setAdapter(expenditureDetailsAdapter);
        expenditureDetailsAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

            }
        });
        expenditureDetailsAdapter.openLoadAnimation();
        expenditureDetailsAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                load();
            }
        }, mRecycleView);
        getDate();
    }


    @Override
    public void onResume() {
        super.onResume();
        getDate();
    }

    /**
     * 收支明细
     */
    private void getDate() {
        page = 1;
        Map<String, Object> map = new HashMap<>();
        map.put("pageNum", page);
        map.put("pageSize", 20);
        ApiClient.requestNetHandle(getContext(), AppConfig.BalancePayments, "", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                List<ChongzhiInfo> chongzhiInfos = FastJsonUtil.getList(json, "list", ChongzhiInfo.class);
                list.clear();
                if (chongzhiInfos != null && chongzhiInfos.size() > 0) {
                    list.addAll(chongzhiInfos);
                    if (chongzhiInfos.size()<20){
                        expenditureDetailsAdapter.loadMoreEnd(true);
                    }else {
                        expenditureDetailsAdapter.loadMoreComplete();
                    }
                }else {
                    expenditureDetailsAdapter.loadMoreEnd(true);
                }
                expenditureDetailsAdapter.notifyDataSetChanged();
            }


            @Override
            public void onFailure(String msg) {
                ToastUtil.toast(msg);
            }
        });
    }

    /**
     * 收支明细
     */
    private void load() {
        page++;
        Map<String, Object> map = new HashMap<>();
        map.put("pageNum", page);
        map.put("pageSize", 20);
        ApiClient.requestNetHandle(getContext(), AppConfig.BalancePayments, "", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                List<ChongzhiInfo> chongzhiInfos = FastJsonUtil.getList(json, "list", ChongzhiInfo.class);
                if (chongzhiInfos != null && chongzhiInfos.size() > 0) {
                    list.addAll(chongzhiInfos);
                    expenditureDetailsAdapter.notifyDataSetChanged();
                    expenditureDetailsAdapter.loadMoreComplete();
                } else {
                    expenditureDetailsAdapter.loadMoreEnd(true);
                }
            }

            @Override
            public void onFailure(String msg) {
                toast(msg);
                expenditureDetailsAdapter.loadMoreFail();
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
