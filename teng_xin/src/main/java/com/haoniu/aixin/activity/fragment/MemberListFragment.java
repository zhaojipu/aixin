package com.haoniu.aixin.activity.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.haoniu.aixin.R;
import com.haoniu.aixin.activity.MyMemberListActivity;
import com.haoniu.aixin.adapter.ZhiTuiAdapter;
import com.haoniu.aixin.base.MyApplication;
import com.haoniu.aixin.entity.EventCenter;
import com.haoniu.aixin.entity.ZhiTuiNumberInfo;
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
 * 作   者：赵大帅
 * 描   述: 会员列表
 * 日   期: 2017/12/4 16:20
 * 更新日期: 2017/12/4
 *
 * @author Administrator
 */
public class MemberListFragment extends EaseBaseFragment {


    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    Unbinder unbinder;

    private ZhiTuiAdapter mAdapter;
    private List<ZhiTuiNumberInfo> list;
    private int page = 1;
    private int Invite;
    private int id;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_member_list, container, false);
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
        list = new ArrayList<>();
        mAdapter = new ZhiTuiAdapter(list);
        LinearLayoutManager LinearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(LinearLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.openLoadAnimation();
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (Invite <15) {
                    startActivity(new Intent(getActivity(), MyMemberListActivity.class).putExtra("id", list.get(position).getUserId()).putExtra("Invite", Invite + 1));
                }
            }
        });
        mAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                mRecyclerView.post(new Runnable() {
                    @Override
                    public void run() {
                        loadData();
                    }
                });

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
        Invite = extras.getInt("Invite");
        id = extras.getInt("id");
    }


    private void getData() {
        page = 1;
        Map<String, Object> map = new HashMap<>();
        map.put("pageNum", page);
        map.put("pageSize", 20+"");
        if (Invite > 0) {
            map.put("userId", id);
        }else {
            map.put("userId", MyApplication.getInstance().getUserInfo().getUserId());
        }
        ApiClient.requestNetHandle(getActivity(), AppConfig.getDlList, "加载中...", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                List<ZhiTuiNumberInfo> userInfos = FastJsonUtil.getList(json,"list", ZhiTuiNumberInfo.class);
                if (userInfos != null && userInfos.size() > 0) {
                    list.clear();
                    list.addAll(userInfos);
                    mAdapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onFinsh() {
                super.onFinsh();
                mAdapter.loadMoreComplete();
            }

            @Override
            public void onFailure(String msg) {
                ToastUtil.toast(msg);
            }
        });
    }

    private void loadData() {
        page++;
        Map<String, Object> map = new HashMap<>();
        map.put("pageNum", page);
        map.put("pageSize", 20+"");
        if (Invite > 0) {
            map.put("userId", id);
        }else {
            map.put("userId", MyApplication.getInstance().getUserInfo().getUserId());
        }
        ApiClient.requestNetHandle(getActivity(), AppConfig.getDlList, "", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                List<ZhiTuiNumberInfo> userInfos = FastJsonUtil.getList(json,"list", ZhiTuiNumberInfo.class);
                if (userInfos != null && userInfos.size() > 0) {
                    list.addAll(userInfos);
                    mAdapter.notifyDataSetChanged();
                    mAdapter.loadMoreComplete();
                } else {
                    mAdapter.loadMoreEnd(true);
                }
            }

            @Override
            public void onFailure(String msg) {
                ToastUtil.toast(msg);
                if (mAdapter != null) {
                    mAdapter.loadMoreFail();
                }
            }
        });
    }
}
