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
import com.haoniu.aixin.R;
import com.haoniu.aixin.adapter.ServieceAdapter;
import com.haoniu.aixin.base.BaseActivity;
import com.haoniu.aixin.base.Constant;
import com.haoniu.aixin.base.MyHelper;
import com.haoniu.aixin.domain.EaseUser;
import com.haoniu.aixin.entity.EventCenter;
import com.haoniu.aixin.entity.ServersInfo;
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

/**
 * 作   者：赵大帅
 * 描   述: 客服
 * 邮   箱: 2510722254@qqq.com
 * 日   期: 2017/12/6 17:15
 * 更新日期: 2017/12/6
 *
 * @author Administrator
 */
public class CustomServiceActivity extends BaseActivity {
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
    @BindView(R.id.recycle_view)
    RecyclerView mRecycleView;

    ServieceAdapter mServieceAdapter;
    List<ServersInfo> list;

    /**
     * 替代onCreate的使用
     *
     * @param bundle
     */
    @Override
    protected void initContentView(Bundle bundle) {
        setContentView(R.layout.activity_custom_service);
    }

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
    }

    /**
     * 初始化逻辑
     */
    @Override
    protected void initLogic() {
        setTitle("客服");
        list = new ArrayList<>();
        mServieceAdapter = new ServieceAdapter(list);
        mRecycleView.setLayoutManager(new LinearLayoutManager(this));
        mRecycleView.setAdapter(mServieceAdapter);
        mServieceAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                startActivity(new Intent(CustomServiceActivity.this, ChatActivity.class).putExtra("userId", list.get(position).getUserId() + Constant.ID_REDPROJECT));
            }
        });
        getService();
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

    /**
     * 客服列表
     */
    private void getService() {
        Map<String, Object> map = new HashMap<>();
        ApiClient.requestNetHandle(this, AppConfig.serviceUrl, "", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                List<ServersInfo> infoList = FastJsonUtil.getList(json, ServersInfo.class);
                list.clear();
                if (infoList != null && infoList.size() > 0) {
                    list.addAll(infoList);
                    for (ServersInfo serviceInfo : infoList) {
                        EaseUser easeUser = new EaseUser(serviceInfo.getUserId() + Constant.ID_REDPROJECT);
                        easeUser.setNickname(serviceInfo.getNickName());
                        easeUser.setAvatar(serviceInfo.getHeadImg());
                        easeUser.setType(Constant.SERVICE);
                        MyHelper.getInstance().saveUser(easeUser);
                    }
                }
                mServieceAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(String msg) {
                ToastUtil.toast(msg);
            }
        });
    }

}
