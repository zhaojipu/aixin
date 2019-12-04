package com.haoniu.aixin.activity.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.haoniu.aixin.R;
import com.haoniu.aixin.activity.TransferfActivity;
import com.haoniu.aixin.adapter.FriendAdapter;
import com.haoniu.aixin.base.MyApplication;
import com.haoniu.aixin.entity.EventCenter;
import com.haoniu.aixin.entity.ZhiTuiNumberInfo;
import com.haoniu.aixin.http.ApiClient;
import com.haoniu.aixin.http.AppConfig;
import com.haoniu.aixin.http.ResultListener;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.zds.base.json.FastJsonUtil;
import com.zds.base.log.XLog;
import com.zds.base.util.StringUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 通讯录
 *
 * @author Administrator
 */
public class FriendListFragment extends EaseBaseFragment {
    Unbinder unbinder;
    @BindView(R.id.bar)
    View bar;
    @BindView(R.id.ll_back)
    LinearLayout llBack;
    @BindView(R.id.toolbar_subtitle)
    TextView toolbarSubtitle;
    @BindView(R.id.img_right)
    ImageView imgRight;
    @BindView(R.id.toolbar_title)
    TextView toolbarTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.et_search)
    EditText etSearch;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    FriendAdapter friendAdapter;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    private List<ZhiTuiNumberInfo> list;
    private List<ZhiTuiNumberInfo> listlist;
    private int page = 1;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friend_list, container, false);
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
        llBack.setVisibility(View.GONE);
        toolbarTitle.setText("通讯录");
//        initBar();
        list = new ArrayList<>();
        listlist = new ArrayList<>();
        friendAdapter = new FriendAdapter(listlist);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(friendAdapter);
        friendAdapter.openLoadAnimation();
        friendAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (listlist.size() > position) {
                    startActivity(new Intent(getActivity(), TransferfActivity.class).putExtra("type",1).putExtra("nick",list.get(position).getNickName()).putExtra("userId", listlist.get(position).getUserId()+"").putExtra("head", listlist.get(position).getUserImg() + ""));
                }
            }
        });
        friendAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                recyclerView.post(new Runnable() {
                    @Override
                    public void run() {
                        loadData();
                    }
                });
            }
        });
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                getData();
            }
        });
        refreshLayout.setEnableLoadmore(false);
        refreshLayout.autoRefresh();
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                getData();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                getData();
            }
        });
        etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView arg0, int arg1, KeyEvent arg2) {
                // TODO Auto-generated method stub
                if (arg1 == EditorInfo.IME_ACTION_SEARCH) {
                    getData();
                }
                return false;
            }

        });

    }

    private void chang() {
        if (!StringUtil.isEmpty(etSearch)) {
            listlist.clear();
            for (ZhiTuiNumberInfo userInfo : list) {
                XLog.d(etSearch.getText().toString());
                if (userInfo.getPhone().contains(etSearch.getText().toString())) {
                    listlist.add(userInfo);
                }
            }
        } else {
            listlist.clear();
            listlist.addAll(list);
        }
        friendAdapter.notifyDataSetChanged();

    }

    @Override
    protected void onEventComing(EventCenter center) {

    }




    private void getData() {
        page = 1;
        Map<String, Object> map = new HashMap<>();
        map.put("pageNum", page);
        map.put("pageSize",20+"");
        map.put("userId",MyApplication.getInstance().getUserInfo().getUserId());
        map.put("phone",etSearch.getText().toString());
        ApiClient.requestNetHandle(getActivity(), AppConfig.xiaji, "", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                List<ZhiTuiNumberInfo> userInfos = FastJsonUtil.getList(json,"list", ZhiTuiNumberInfo.class);
                list.clear();
                if (userInfos != null && userInfos.size() > 0) {
                    list.addAll(userInfos);
                }
                chang();
                friendAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFinsh() {
                super.onFinsh();
                if (getActivity() != null) {
                    refreshLayout.finishRefresh();
                    refreshLayout.setLoadmoreFinished(false);
                    friendAdapter.loadMoreComplete();
                }

            }

            @Override
            public void onFailure(String msg) {

            }
        });
    }

    private void loadData() {
        page++;
        Map<String, Object> map = new HashMap<>();
        map.put("pageNum", page);
        map.put("pageSize",20+"");
        map.put("userId",MyApplication.getInstance().getUserInfo().getUserId());
        map.put("phone",etSearch.getText().toString());
        ApiClient.requestNetHandle(getActivity(), AppConfig.xiaji, "", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                List<ZhiTuiNumberInfo> userInfos = FastJsonUtil.getList(json,"list", ZhiTuiNumberInfo.class);
                if (userInfos != null && userInfos.size() > 0) {
                    list.addAll(userInfos);
                    chang();
                    friendAdapter.notifyDataSetChanged();
                    friendAdapter.loadMoreComplete();
                } else {
                    friendAdapter.loadMoreEnd(true);
                }
            }

            @Override
            public void onFailure(String msg) {
                // ToastUtil.toast(msg);
                if (friendAdapter != null) {
                    friendAdapter.loadMoreFail();
                }
            }
        });
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
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

}
