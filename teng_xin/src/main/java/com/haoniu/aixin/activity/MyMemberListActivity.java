package com.haoniu.aixin.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.haoniu.aixin.R;
import com.haoniu.aixin.activity.fragment.MemberListFragment;
import com.haoniu.aixin.base.BaseActivity;
import com.haoniu.aixin.base.Constant;
import com.haoniu.aixin.entity.EventCenter;
import com.haoniu.aixin.entity.MemberCountInfo;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 作   者：赵大帅
 * 描   述: 我的会员
 * 邮   箱: 2510722254@qqq.com
 * 日   期: 2017/11/28 14:59
 * 更新日期: 2017/11/28
 */
public class MyMemberListActivity extends BaseActivity {

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
    @BindView(R.id.container)
    RelativeLayout mContainer;
    private int Invite;

    private int id;

    /**
     * 替代onCreate的使用
     *
     * @param bundle
     */
    @Override
    protected void initContentView(Bundle bundle) {
        setContentView(R.layout.activity_my_member_list);
    }

    /**
     * 初始化逻辑
     */
    @Override
    protected void initLogic() {
        setTitle(Constant.getTitleInv(Invite));
        MemberListFragment memberListFragment1 = new MemberListFragment();
        memberListFragment1.setArguments(getIntent().getExtras());
        getSupportFragmentManager().beginTransaction().add(R.id.container, memberListFragment1).commit();
//        getPenperNum();
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
        id = extras.getInt("id", 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }


//    /**
//     * 获取人数
//     */
//    private void getPenperNum() {
//        Map<String, Object> map = new HashMap<>();
//        map.put("userId", id);
//        ApiClient.requestNetHandleByGet(this, AppConfig.MY_TEAM_PENPER_NUM, "", map, new ResultListener() {
//            @Override
//            public void onSuccess(String json, String msg) {
//                int countOne = FastJsonUtil.getInt(json, "countOne");
//                int teamCountNum = FastJsonUtil.getInt(json, "teamCountNum");
//                mToolbarSubtitle.setText(teamCountNum + "人");
//                mToolbarSubtitle.setTextColor(getResources().getColor(R.color.black_text));
//                mToolbarSubtitle.setVisibility(View.VISIBLE);
//            }
//
//            @Override
//            public void onFailure(String msg) {
//                toast(msg);
//            }
//        });
//    }

    //初始化数量
    private void initTitleBar(MemberCountInfo memberCountInfo) {
        mToolbarSubtitle.setText(memberCountInfo.getCommon() + "人");
        mToolbarSubtitle.setTextColor(getResources().getColor(R.color.black_text));
        mToolbarSubtitle.setVisibility(View.VISIBLE);
    }

}


