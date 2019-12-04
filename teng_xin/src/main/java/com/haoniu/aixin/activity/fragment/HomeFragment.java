package com.haoniu.aixin.activity.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.haoniu.aixin.R;
import com.haoniu.aixin.activity.SearchRoomActivity;
import com.haoniu.aixin.entity.EventCenter;
import com.haoniu.aixin.utils.EventUtil;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * @author Administrator
 */
public class HomeFragment extends EaseBaseFragment {
    Unbinder unbinder;
    @BindView(R.id.tv_search)
    TextView mTvSearch;
    @BindView(R.id.ll_search)
    LinearLayout mLlSearch;
    @BindView(R.id.vp_2)
    ViewPager mVp2;
    @BindView(R.id.bar)
    View mBar;
    @BindView(R.id.tv_cl)
    TextView mTvCl;
    @BindView(R.id.tv_jl)
    TextView mTvJl;
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
    @BindView(R.id.llayout_title_1)
    RelativeLayout llayoutTitle1;
    @BindView(R.id.tv_edit_search)
    TextView tvEditSearch;
    private ArrayList<RoomListFragment> mFragments = new ArrayList<>();
    private final String[] mTitles = {"接龙红包", "踩雷红包"};
    private int currentIndex;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
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
        toolbarTitle.setText("爱聊");
        llBack.setVisibility(View.GONE);
        RoomListFragment roomListFragment1 = new RoomListFragment();
        Bundle bundle1 = new Bundle();
        bundle1.putInt("type", 2);
        roomListFragment1.setArguments(bundle1);
        RoomListFragment roomListFragment2 = new RoomListFragment();
        Bundle bundle2 = new Bundle();
        bundle2.putInt("type", 1);
        roomListFragment2.setArguments(bundle2);
//      mFragments.add(roomListFragment1);
        mFragments.add(roomListFragment2);
        mVp2.setOffscreenPageLimit(2);
        mVp2.setAdapter(new MyPagerAdapter(getActivity().getSupportFragmentManager()));
        mVp2.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currentIndex = position;
                initBar(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mVp2.setCurrentItem(0);
    }

    /**
     * 初始化头部
     */
    public void initBar(int position) {
        mTvCl.setTextColor(getResources().getColor(R.color.colorPrimary));
        mTvCl.setBackgroundColor(getResources().getColor(R.color.transparent));
        mTvJl.setTextColor(getResources().getColor(R.color.colorPrimary));
        mTvJl.setBackgroundColor(getResources().getColor(R.color.transparent));
        if (position == 0) {
            mTvCl.setTextColor(getResources().getColor(R.color.white));
            mTvCl.setBackgroundResource(R.drawable.bor_main_bar);
        } else {
            mTvJl.setTextColor(getResources().getColor(R.color.white));
            mTvJl.setBackgroundResource(R.drawable.bor_main_bar);
        }
    }

    /**
     * EventBus接收消息
     *
     * @param center 获取事件总线信息
     */
    @Override
    protected void onEventComing(EventCenter center) {
        if (center.getEventCode() == EventUtil.FLUSHGROUP) {
            mFragments.get(currentIndex).flushData();
        } else if (center.getEventCode() == EventUtil.FLUSHRENAME) {
            mFragments.get(currentIndex).flushData();
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


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.tv_edit_search, R.id.tv_cl, R.id.tv_jl, R.id.tv_search})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_cl:
                mVp2.setCurrentItem(0);
                break;
            case R.id.tv_jl:
                mVp2.setCurrentItem(1);
                break;
            case R.id.tv_edit_search:
            case R.id.tv_search:
                Intent intent = new Intent();
                intent.setClass(getActivity(), SearchRoomActivity.class);
                startActivityForResult(intent, 100);
                getActivity().overridePendingTransition(R.anim.animation_2, R.anim.animation_1);
                break;
            default:
        }
    }

    private class MyPagerAdapter extends FragmentPagerAdapter {
        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTitles[position];
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }
    }
}
