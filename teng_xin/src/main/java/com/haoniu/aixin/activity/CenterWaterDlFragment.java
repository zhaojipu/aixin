package com.haoniu.aixin.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.haoniu.aixin.R;
import com.haoniu.aixin.activity.fragment.EaseBaseFragment;
import com.haoniu.aixin.adapter.DlAgentWaterAdapter;
import com.haoniu.aixin.entity.AllShouYiInfo;
import com.haoniu.aixin.entity.EventCenter;
import com.haoniu.aixin.entity.ShouyiInfo;
import com.haoniu.aixin.http.ApiClient;
import com.haoniu.aixin.http.AppConfig;
import com.haoniu.aixin.http.ResultListener;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.zds.base.Toast.ToastUtil;
import com.zds.base.json.FastJsonUtil;
import com.zds.base.util.StringUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * 作   者：赵大帅
 * 描   述: 代理收益
 * 日   期: 2017/12/4 16:20
 * 更新日期: 2017/12/4
 *
 * @author Administrator
 */
public class CenterWaterDlFragment extends EaseBaseFragment {


    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    Unbinder unbinder;
    @BindView(R.id.ll_no_data)
    LinearLayout mLlNoData;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout mRefreshLayout;
    @BindView(R.id.tv_money)
    TextView tvMoney;
    @BindView(R.id.tv_titles)
    TextView tvTitles;
    @BindView(R.id.tv_yue_money)
    TextView tvYueMoney;
    @BindView(R.id.tv_member)
    TextView tvMember;
    @BindView(R.id.ll_member_connt)
    LinearLayout llMemberConnt;

    private DlAgentWaterAdapter mAgentWaterAdapter;
    private List<ShouyiInfo> mQBLSInfoList;
    private int page = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_center_water_dl, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    /**
     * 初始化逻辑
     */
    @Override
    protected void initLogic() {
        mQBLSInfoList = new ArrayList<>();
        mAgentWaterAdapter = new DlAgentWaterAdapter(mQBLSInfoList);
        LinearLayoutManager LinearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(LinearLayoutManager);
        mRecyclerView.setAdapter(mAgentWaterAdapter);
        mAgentWaterAdapter.openLoadAnimation();
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                getData();
            }
        });
        mAgentWaterAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                loadData();
            }
        }, mRecyclerView);
        mRefreshLayout.setEnableLoadmore(false);
        mRefreshLayout.autoRefresh();
        getAllmoney();
        getMemberConnt();
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
        map.put("pageSize", 20 + "");
        ApiClient.requestNetHandle(getActivity(), AppConfig.dlls, "", map, new ResultListener() {
            /**
             * 请求成功
             *
             * @param json
             * @param msg
             */
            @Override
            public void onSuccess(String json, String msg) {
                if (json != null) {
                    List<ShouyiInfo> list = FastJsonUtil.getList(json, "list", ShouyiInfo.class);
                    if (list != null && list.size() > 0) {
                        mQBLSInfoList.clear();
                        mQBLSInfoList.addAll(list);
                        mLlNoData.setVisibility(View.GONE);
                    } else {
                        mLlNoData.setVisibility(View.VISIBLE);
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
                ToastUtil.toast(msg);
                if (mLlNoData != null) {
                    mLlNoData.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    //月总收益
    private void getAllmoney() {
        Map<String, Object> map = new HashMap<>();
        ApiClient.requestNetHandle(getActivity(), AppConfig.monthMoney, "", map, new ResultListener() {
            /**
             * 请求成功
             * @param json
             * @param msg
             */
            @Override
            public void onSuccess(String json, String msg) {
                if (json != null) {
                    AllShouYiInfo allShouYiInfo = FastJsonUtil.getObject(json, AllShouYiInfo.class);
                    try {
                        tvYueMoney.setText(StringUtil.getFormatValue2(allShouYiInfo.getMonthAward()));
                        tvMoney.setText(StringUtil.getFormatValue2(allShouYiInfo.getTodayAward()));
                    } catch (Exception e) {
                        tvYueMoney.setText(allShouYiInfo.getMonthAward() + "");
                        tvMoney.setText(allShouYiInfo.getTodayAward() + "");
                    }

                }

            }

            /**
             * 请求失败
             *
             * @param msg
             */
            @Override
            public void onFailure(String msg) {
            }
        });
    }

    //获取团队总人数
    private void getMemberConnt() {
        Map<String, Object> map = new HashMap<>();
        ApiClient.requestNetHandle(getActivity(), AppConfig.listCountTotal, "", map, new ResultListener() {
            /**
             * 请求成功
             * @param json
             * @param msg
             */
            @Override
            public void onSuccess(String json, String msg) {
                if (json != null) {
                    try {
                        tvMember.setText(Double.valueOf(json).intValue()+"");
                    }catch (Exception e){
                        tvMember.setText(json);
                    }

                }

            }

            /**
             * 请求失败
             *
             * @param msg
             */
            @Override
            public void onFailure(String msg) {
            }
        });
    }




    //加载更多
    private void loadData() {
        page++;
        Map<String, Object> map = new HashMap<>();
        map.put("pageNum", page);
        map.put("pageSize", 20 + "");
        ApiClient.requestNetHandle(getActivity(), AppConfig.dlls, "", map, new ResultListener() {
            /**
             * 请求成功
             *
             * @param json
             * @param msg
             */
            @Override
            public void onSuccess(String json, String msg) {
                if (json != null) {
                    List<ShouyiInfo> list = FastJsonUtil.getList(json, "list", ShouyiInfo.class);
                    if (list != null && list.size() > 0) {
                        mQBLSInfoList.addAll(list);
                        mAgentWaterAdapter.notifyDataSetChanged();
                        mAgentWaterAdapter.loadMoreComplete();
                    } else {
                        mAgentWaterAdapter.loadMoreEnd(true);
                    }
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
                mAgentWaterAdapter.loadMoreFail();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.ll_no_data)
    public void onViewClicked() {
        getData();
    }
}
