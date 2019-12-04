package com.haoniu.aixin.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.haoniu.aixin.R;
import com.haoniu.aixin.adapter.RedPacketAdapter;
import com.haoniu.aixin.base.BaseActivity;
import com.haoniu.aixin.base.MyApplication;
import com.haoniu.aixin.entity.EventCenter;
import com.haoniu.aixin.entity.RedPacketInfo;
import com.haoniu.aixin.http.ApiClient;
import com.haoniu.aixin.http.AppConfig;
import com.haoniu.aixin.http.ResultListener;
import com.haoniu.aixin.widget.EaseImageView;
import com.zds.base.ImageLoad.GlideUtils;
import com.zds.base.Toast.ToastUtil;
import com.zds.base.json.FastJsonUtil;
import com.zds.base.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

/**
 * 作   者：赵大帅
 * 描   述: 红包详情
 * 邮   箱: 2510722254@qqq.com
 * 日   期: 2017/11/27 14:08
 * 更新日期: 2017/11/27
 *
 * @author Administrator
 */
public class RedPacketDetailActivity extends BaseActivity {

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


    //红包id
    private int rid;

    private RedPacketAdapter mAdapter;
    private List<RedPacketInfo.RedPacketNumberListBean> mList;

    private TextView tv_money, tv_name;
    private EaseImageView img_head;
    //头像
    private String head;
    //昵称
    private String nickname;
    private TextView tv_message_hb;
    private LinearLayout ll_message_hb, ll_user_money;

    /**
     * 替代onCreate的使用
     *
     * @param bundle
     */
    @Override
    protected void initContentView(Bundle bundle) {
        setContentView(R.layout.activity_red_packet);
    }

    /**
     * 初始化逻辑
     */
    @Override
    protected void initLogic() {
        View headView = LayoutInflater.from(this).inflate(R.layout.red_packet_head_view, null);
        tv_money = headView.findViewById(R.id.tv_money);
        ll_message_hb = headView.findViewById(R.id.ll_message_hb);
        ll_user_money = headView.findViewById(R.id.ll_user_money);
        ll_user_money.setVisibility(View.GONE);
        tv_name = headView.findViewById(R.id.tv_name);
        img_head = headView.findViewById(R.id.img_head);
        tv_message_hb = headView.findViewById(R.id.tv_message_hb);
        MyApplication.getInstance().setAvatar(img_head);
        mList = new ArrayList<>();
        mAdapter = new RedPacketAdapter(mList);
        mRecycleView.setLayoutManager(new LinearLayoutManager(this));
        mRecycleView.setAdapter(mAdapter);
        mAdapter.addHeaderView(headView);
        initHeadView();
        getData();
        setTitle("红包详情");
    }

    private void initHeadView() {
        GlideUtils.loadImageViewLodingByCircle(AppConfig.checkimg( head), img_head, R.mipmap.img_default_avatar);
        tv_name.setText(nickname);
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
        rid = extras.getInt("rid", 0);
        head = extras.getString("head", "");
        nickname = extras.getString("nickname", "");
    }

    /**
     * 获取红包数据
     */
    private void getData() {
        Map<String, Object> map = new HashMap<>();
        map.put("redPacketId", rid + "");
        ApiClient.requestNetHandle(this, AppConfig.getRedPacket, "正在加载...", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                if (json != null) {
                    RedPacketInfo redPacketInfo = FastJsonUtil.getObject(json, RedPacketInfo.class);
                    if (redPacketInfo==null){
                        ToastUtil.toast("信息异常");
                        return;
                    }
                    tv_message_hb.setText("已领取" + 0 + "/" + redPacketInfo.getAmount() + "，" + "共"   + StringUtil
                            .getFormatValue2(redPacketInfo.getMoney()) + "    " + (redPacketInfo.getType() == 2 ? ("雷点：" + redPacketInfo.getThunderPoint()) : ""));
                    mAdapter.setRoomType(redPacketInfo.getType());
                    if (redPacketInfo.getRedPacketNumberList() != null && redPacketInfo.getRedPacketNumberList().size() > 0) {
                        mList.addAll(redPacketInfo.getRedPacketNumberList());
                        double money = 0.00;
                        List<Double> listMoney = new ArrayList<>();
                        List<String> listStrMoneyPoint = new ArrayList<>();
                        for (RedPacketInfo.RedPacketNumberListBean numberListBean : mList) {
                            money += numberListBean.getMoney();
                            listMoney.add(numberListBean.getMoney());
                            listStrMoneyPoint.add(StringUtil.getFormatValue2(numberListBean.getMoney()).substring((StringUtil.getFormatValue2(numberListBean.getMoney())).length() - 1));
                            if (numberListBean.getUserId() == MyApplication.getInstance().getUserInfo().getUserId()) {
                                if (numberListBean.getMoney() > 0) {
                                    ll_user_money.setVisibility(View.VISIBLE);
                                }
                                if (tv_money != null) {
                                    tv_money.setText(StringUtil.getFormatValue2(numberListBean.getMoney()));
                                }
                            }
                        }

                        /**
                         * 已领数量
                         */
                        int ylSize = mList.size();
                        /**
                         * 总数量
                         */
                        int allSize = redPacketInfo.getAmount();
                        if (ylSize == allSize && listMoney.size() > 1) {
                            mAdapter.setMaxMoney(Collections.max(listMoney));
                            mAdapter.setMinMoney(Collections.min(listMoney));
                        }
                        boolean isLei = false;
                        String point = redPacketInfo.getThunderPoint();
                        List<String> listPoint = Arrays.asList(point.trim().split(","));
                        for (String pos : listPoint) {

                            if (!listStrMoneyPoint.contains(pos.trim())) {
                                isLei = false;
                                break;
                            } else {
                                isLei = true;
                            }
                        }
                        mAdapter.setHave(isLei);
                        mAdapter.setPoints(point);
                        mAdapter.setIsfirsh(false);
                        tv_message_hb.setText("已领取" + ylSize + "/" + allSize + "，" + "共" + StringUtil
                                .getFormatValue2(redPacketInfo.getMoney()) + "    " + (redPacketInfo.getType() == 2 ? ("雷点：" + redPacketInfo.getThunderPoint()) : ""));

                        if (ylSize == allSize) {
                            mAdapter.setIsfirsh(true);
                           // tv_message_hb.setText("已领取" + ylSize + "/" + allSize + "，" + "已被抢完" + "    " + (redPacketInfo.getType() == 2 ? ("雷点：" + redPacketInfo.getThunderPoint()) : ""));
                        } else {
                            mAdapter.setIsfirsh(false);
                           // tv_message_hb.setText("已领取" + ylSize + "/" + allSize + "，" + "共" + StringUtil
                            //        .getFormatValue2(redPacketInfo.getMoney()) + "    " + (redPacketInfo.getType() == 2 ? ("雷点：" + redPacketInfo.getThunderPoint()) : ""));

//                            if (redPacketInfo.getUserId() == MyApplication.getInstance().getUserInfo().getUserId()) {
//                                tv_message_hb.setText("已领取" + ylSize + "/" + allSize + "，" + "共" + StringUtil.getFormatValue2(money) + "/" + StringUtil
//                                        .getFormatValue2(redPacketInfo.getMoney()) + "    " + (redPacketInfo.getType() == 2 ? ("雷点：" + redPacketInfo.getThunderPoint()) : ""));
//                            } else {
//                                tv_message_hb.setText("已领取" + ylSize + "/" + allSize + "，还剩" + (allSize - ylSize) + "个     " + (redPacketInfo.getType() == 2 ? ("雷点：" + redPacketInfo.getThunderPoint()) : ""));
//
//                            }
                        }
                        mAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onFailure(String msg) {
                ToastUtil.toast(msg);
            }
        });
    }


}
