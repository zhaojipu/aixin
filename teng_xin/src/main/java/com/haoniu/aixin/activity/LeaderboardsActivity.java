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
import com.haoniu.aixin.http.ApiClient;
import com.haoniu.aixin.http.AppConfig;
import com.haoniu.aixin.http.ResultListener;
import com.haoniu.aixin.R;
import com.haoniu.aixin.adapter.LeaderboardsAdapter;
import com.haoniu.aixin.base.BaseActivity;
import com.haoniu.aixin.entity.EventCenter;
import com.haoniu.aixin.entity.PhbInfo;
import com.zds.base.ImageLoad.GlideUtils;
import com.zds.base.Toast.ToastUtil;
import com.zds.base.json.FastJsonUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * 作   者：赵大帅
 * 描   述: 排行榜
 * 邮   箱: 2510722254@qqq.com
 * 日   期: 2017/11/29 9:53
 * 更新日期: 2017/11/29
 */

public class LeaderboardsActivity extends BaseActivity {
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
    @BindView(R.id.recycleview)
    RecyclerView mRecycleview;
    @BindView(R.id.tv_no)
    TextView mTvNo;
    @BindView(R.id.img_view)
    CircleImageView mImgView;
    @BindView(R.id.tv_username)
    TextView mTvUsername;
    @BindView(R.id.tv_jignyan)
    TextView mTvJignyan;
    @BindView(R.id.ll_liebiao)
    LinearLayout mLlLiebiao;

    private LeaderboardsAdapter mAdapter;
    private int page = 1;
    private List<PhbInfo> list;

    /**
     * 替代onCreate的使用
     *
     * @param bundle
     */
    @Override
    protected void initContentView(Bundle bundle) {
        setContentView(R.layout.activity_leaderboards);
    }

    /**
     * 初始化逻辑
     */
    @Override
    protected void initLogic() {
        setTitle("排行榜");
        list = new ArrayList<>();
        mAdapter = new LeaderboardsAdapter(list);
        mRecycleview.setLayoutManager(new LinearLayoutManager(this));
        mAdapter.openLoadAnimation();
        mRecycleview.setAdapter(mAdapter);
        mAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                mRecycleview.post(new Runnable() {
                    @Override
                    public void run() {
                        loadData();
                    }
                });
            }
        });
        getData();
        getMineData();
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
     * 加载数据
     */
    private void getData() {
        page = 1;
        Map<String, Object> map = new HashMap<>();
        map.put("page", page);
        ApiClient.requestNetHandleByGet(this, AppConfig.getBank, "正在加载...", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                List<PhbInfo> phbInfoList = FastJsonUtil.getList(json, PhbInfo.class);
                if (phbInfoList != null && phbInfoList.size() > 0) {
                    list.clear();
                    list.addAll(phbInfoList);
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
                ToastUtil.toast(msg);
            }
        });
    }

    /**
     * 加载更多
     */
    private void loadData() {
        page++;
        Map<String, Object> map = new HashMap<>();
        map.put("page", page);
        ApiClient.requestNetHandleByGet(this, AppConfig.getBank, "", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                List<PhbInfo> phbInfoList = FastJsonUtil.getList(json, PhbInfo.class);
                if (phbInfoList != null && phbInfoList.size() > 0) {
                    list.addAll(phbInfoList);
                    mAdapter.notifyDataSetChanged();
                    mAdapter.loadMoreComplete();
                } else {
                    mAdapter.loadMoreEnd(true);
                }
            }


            @Override
            public void onFailure(String msg) {
                ToastUtil.toast(msg);
                mAdapter.loadMoreFail();
            }
        });
    }

    //加在自己的排名
    private void getMineData() {
        Map<String, Object> map = new HashMap<>();
        ApiClient.requestNetHandleByGet(this, AppConfig.getBankMine, "", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                PhbInfo phbInfo = FastJsonUtil.getObject(json, PhbInfo.class);
                if (phbInfo != null) {
                    mTvNo.setText("NO." + phbInfo.getRownum());
                    mTvJignyan.setText("积分：" + phbInfo.getCredit());
                    GlideUtils.loadImageViewLoding(AppConfig.checkimg( phbInfo.getAvatarUrl()), mImgView, R.mipmap.img_default_avatar);

                }
            }

            @Override
            public void onFailure(String msg) {

            }
        });
    }
}
