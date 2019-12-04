package com.haoniu.aixin.activity.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.haoniu.aixin.http.ApiClient;
import com.haoniu.aixin.http.AppConfig;
import com.haoniu.aixin.http.ResultListener;
import com.haoniu.aixin.R;
import com.haoniu.aixin.activity.AddressBookActivity;
import com.haoniu.aixin.activity.ChatActivity;
import com.haoniu.aixin.activity.PlatformAnnouncementActivity;
import com.haoniu.aixin.activity.WebViewActivity;
import com.haoniu.aixin.adapter.ServiceAdapter;
import com.haoniu.aixin.base.Constant;
import com.haoniu.aixin.base.MyHelper;
import com.haoniu.aixin.domain.EaseUser;
import com.haoniu.aixin.entity.EventCenter;
import com.haoniu.aixin.entity.ServiceInfo;
import com.haoniu.aixin.utils.EventUtil;
import com.haoniu.aixin.widget.TitleBar;
import com.zds.base.Toast.ToastUtil;
import com.zds.base.json.FastJsonUtil;
import com.zds.base.util.Preference;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class CustomServiceFragment extends EaseBaseFragment {
    Unbinder unbinder;
    @BindView(R.id.title_bar)
    TitleBar mTitleBar;
    @BindView(R.id.recycle_view)
    RecyclerView mRecycleView;

    ServiceAdapter mServiceAdapter;
    List<ServiceInfo> list;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_custom_service, container, false);
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
        titleBar.setRight_text("通讯录");
        titleBar.setRightLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), AddressBookActivity.class));
            }
        });
        titleBar.setRightLayoutVisibility(View.GONE);
        list = new ArrayList<>();
        list.add(new ServiceInfo(getResources().getString(R.string.xttz), Constant.CUSTOM_TZ));
        list.add(new ServiceInfo("平台公告", Constant.CUSTOM_GG));
        list.add(new ServiceInfo("接龙玩法说明", Constant.CUSTOM_JL));
        list.add(new ServiceInfo("踩雷玩法说明", Constant.CUSTOM_CL));
        mServiceAdapter = new ServiceAdapter(list);
        mRecycleView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecycleView.setAdapter(mServiceAdapter);
        mServiceAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (list.get(position).getType().equals(Constant.CUSTOM_GG)) {
                    startActivity(new Intent(getActivity(), PlatformAnnouncementActivity.class));
                } else if (list.get(position).getType().equals(Constant.CUSTOM_TZ)) {
                    startActivity(new Intent(getActivity(), ChatActivity.class).putExtra(Constant.EXTRA_USER_ID, "admin").putExtra(Constant.EXTRA_CHAT_TYPE, Constant.CHATTYPE_SINGLE));
                } else if (list.get(position).getType().equals(Constant.CUSTOM_KF)) {
                    startActivity(new Intent(getActivity(), ChatActivity.class).putExtra(Constant.EXTRA_USER_ID, list.get(position).getId() + Constant.ID_REDPROJECT).putExtra(Constant.EXTRA_CHAT_TYPE, Constant.CHATTYPE_SINGLE));
                } else if (list.get(position).getType().equals(Constant.CUSTOM_JL)) {
                    startActivity(new Intent(getActivity(), WebViewActivity.class).putExtra("title", "接龙玩法说明").putExtra("url", AppConfig.howToPlay + "1"));
                } else if (list.get(position).getType().equals(Constant.CUSTOM_CL)) {
                    startActivity(new Intent(getActivity(), WebViewActivity.class).putExtra("title", "踩雷玩法说明").putExtra("url", AppConfig.howToPlay + "2"));
                }
            }
        });
        getService();
        getUnReadCount();
    }

    /**
     * EventBus接收消息
     *
     * @param center 获取事件总线信息
     */
    @Override
    protected void onEventComing(EventCenter center) {
        if (center.getEventCode() == EventUtil.FLUSHXTTZ) {
            mServiceAdapter.notifyDataSetChanged();
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
     * 客服列表
     */
    private void getService() {
        Map<String, Object> map = new HashMap<>();
        ApiClient.requestNetHandleByGet(getActivity(), AppConfig.serviceUrl, "", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                List<ServiceInfo> infoList = FastJsonUtil.getList(json, ServiceInfo.class);
                list.clear();
                if (infoList != null && infoList.size() > 0) {
                    for (ServiceInfo serviceInfo : infoList) {
                        serviceInfo.setType(Constant.CUSTOM_KF);
                        EaseUser easeUser = new EaseUser(serviceInfo.getId() + Constant.ID_REDPROJECT);
                        easeUser.setNickname(serviceInfo.getNickname());
                        easeUser.setAvatar(serviceInfo.getAvatarUrl());
                        easeUser.setType(Constant.SERVICE);
                        MyHelper.getInstance().saveUser(easeUser);
                    }
                    list.addAll(infoList);
                }
                list.add(new ServiceInfo("系统通知", Constant.CUSTOM_TZ));
                list.add(new ServiceInfo("平台公告", Constant.CUSTOM_GG));
                list.add(new ServiceInfo("接龙玩法说明", Constant.CUSTOM_JL));
                list.add(new ServiceInfo("踩雷玩法说明", Constant.CUSTOM_CL));
                mServiceAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(String msg) {
                ToastUtil.toast(msg);
            }
        });
    }

    /**
     * 平台公告未读数量
     */
    public void getUnReadCount() {
        Map<String, Object> map = new HashMap<>();
        ApiClient.requestNetHandle(getActivity(), AppConfig.unReadCount, "", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                Preference.saveStringPreferences(getActivity(), Constant.UNREADCOUNT, json);
                EventBus.getDefault().post(new EventCenter(EventUtil.NOTICNUM));
                mServiceAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(String msg) {
                ToastUtil.toast(msg);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
