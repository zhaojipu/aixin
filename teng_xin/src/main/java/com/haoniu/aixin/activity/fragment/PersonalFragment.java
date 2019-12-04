package com.haoniu.aixin.activity.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.haoniu.aixin.R;
import com.haoniu.aixin.activity.AccountManagementActivity;
import com.haoniu.aixin.activity.CenterWaterDlActivity;
import com.haoniu.aixin.activity.MyPacketActivity;
import com.haoniu.aixin.activity.MyQrActivity;
import com.haoniu.aixin.activity.SetActivity;
import com.haoniu.aixin.activity.UserInfoFileActivity;
import com.haoniu.aixin.activity.WithdrawActivity;
import com.haoniu.aixin.base.MyApplication;
import com.haoniu.aixin.entity.BankDetailInfo;
import com.haoniu.aixin.entity.EventCenter;
import com.haoniu.aixin.entity.UserInfo;
import com.haoniu.aixin.http.ApiClient;
import com.haoniu.aixin.http.AppConfig;
import com.haoniu.aixin.http.ResultListener;
import com.haoniu.aixin.utils.EventUtil;
import com.haoniu.aixin.widget.EaseImageView;
import com.zds.base.ImageLoad.GlideUtils;
import com.zds.base.json.FastJsonUtil;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * 作   者：赵大帅
 * 描   述: 我的
 * 日   期: 2017/12/5 17:09
 * 更新日期: 2017/12/5
 */
public class PersonalFragment extends EaseBaseFragment {

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
    @BindView(R.id.llayout_title_1)
    RelativeLayout llayoutTitle1;
    @BindView(R.id.my_head)
    EaseImageView myHead;
    @BindView(R.id.tv_username)
    TextView tvUsername;
    @BindView(R.id.tv_phone)
    TextView tvPhone;
    @BindView(R.id.ll_users)
    LinearLayout llUsers;
    @BindView(R.id.img_rights)
    ImageView imgRights;
    @BindView(R.id.Rel_user_info)
    RelativeLayout RelUserInfo;
    @BindView(R.id.rel_background)
    RelativeLayout relBackground;
    @BindView(R.id.ll_qianbao)
    LinearLayout llQianbao;
    @BindView(R.id.ll_tixian)
    LinearLayout llTixian;
    @BindView(R.id.ll_zhanghaoguanli)
    LinearLayout llZhanghaoguanli;
    @BindView(R.id.ll_dltd)
    LinearLayout llDltd;
    @BindView(R.id.ll_er_code)
    LinearLayout llErCode;
    @BindView(R.id.ll_set)
    LinearLayout llSet;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_personal_center, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        MyApplication.getInstance().UpUserInfo();
        getBank();
    }

    /**
     * 初始化用户信息
     */
    private void initUserInfo() {
        UserInfo userInfo = MyApplication.getInstance().getUserInfo();
        if (userInfo != null) {
            GlideUtils.loadImageViewLoding(AppConfig.checkimg( userInfo.getUserImg()), myHead, R.mipmap.img_default_avatar);
            tvUsername.setText(userInfo.getNickName());
            tvPhone.setText("用户id："+userInfo.getUserId());
        }
    }

    /**
     * 初始化逻辑
     */
    @Override
    protected void initLogic() {
        llBack.setVisibility(View.GONE);
        toolbarTitle.setText("我");
        MyApplication.getInstance().setAvatars(myHead);
        initUserInfo();
    }

    /**
     * EventBus接收消息
     *
     * @param center 获取事件总线信息
     */
    @Override
    protected void onEventComing(EventCenter center) {
        if (center.getEventCode() == EventUtil.FLUSHUSERINFO) {
            initUserInfo();
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

    @OnClick({R.id.Rel_user_info, R.id.ll_qianbao, R.id.ll_tixian, R.id.ll_zhanghaoguanli, R.id.ll_dltd, R.id.ll_er_code, R.id.ll_set})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            //个人资料
            case R.id.Rel_user_info:
                startActivity(new Intent(getActivity(), UserInfoFileActivity.class));
                break;
                //账号管理
            case R.id.ll_zhanghaoguanli:
                startActivity(new Intent(getActivity(), AccountManagementActivity.class));
                break;
            //推广海报
            case R.id.ll_er_code:
                startActivity(new Intent(getActivity(), MyQrActivity.class));
                break;
            //设置
            case R.id.ll_set:
                startActivity(new Intent(getActivity(), SetActivity.class));
                break;
            //我的推荐
            case R.id.ll_dltd:
                startActivity(new Intent(getActivity(), CenterWaterDlActivity.class));
                break;
                //我的钱包
            case R.id.ll_qianbao:
                startActivity(new Intent(getActivity(), MyPacketActivity.class));
                break;
            //客服
//            case R.id.ll_wode_kefu:
//                startActivity(new Intent(getActivity(), CustomServiceActivity.class));
//                break;
            //            //提现
            case R.id.ll_tixian:
                startActivity(new Intent(getActivity(), WithdrawActivity.class));
                break;
//            //充值
//            case R.id.tv_chongzhi:
//                PZInfo pzInfo = Storage.GetPZ();
//                if (pzInfo != null) {
//                    startActivity(new Intent(getActivity(), WebViewActivity.class).putExtra("title", "充值").putExtra("url", pzInfo.getRechargeUrl() + Storage.getToken()));
//                }
//                break;
//            //转账
//            case R.id.tv_zhuanzhang:
//                EventBus.getDefault().post(new EventCenter(EventUtil.TONGXUNLU));
//                break;
//            case R.id.tv_zj_jl://资金记录
//                startActivity(new Intent(getActivity(), NewMyPacketActivity.class));
//                break;
//            //银行卡
//            case R.id.ll_wode_yinhangka:
//                if (bankDetailInfo != null) {
//                    if (StringUtil.isEmpty(bankDetailInfo.getBankNumber())) {
//
//                        startActivity(new Intent(getActivity(), BankNewActivity.class));
//                    } else {
//                        startActivity(new Intent(getActivity(), BankNewInfoActivity.class));
//                    }
//                } else {
//                    toast("未获取到信息");
//                }
//                break;
//            //支付宝
//            case R.id.ll_wode_alipay:
//
//                if (bankDetailInfo != null) {
//                    if (StringUtil.isEmpty(bankDetailInfo.getAlipay())) {
//
//                        startActivity(new Intent(getActivity(), AliPayNewActivity.class));
//                    } else {
//                        startActivity(new Intent(getActivity(), AlipayNewInfoActivity.class));
//                    }
//                } else {
//                    toast("未获取到信息");
//                }
//                break;
            default:
                break;
        }
    }

    BankDetailInfo bankDetailInfo;

    /**
     * 获取银行卡信息
     */
    private void getBank() {
        ApiClient.requestNetHandle(getActivity(), AppConfig.bankInfo, "", new HashMap<>(), new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                bankDetailInfo = FastJsonUtil.getObject(json, BankDetailInfo.class);
            }

            @Override
            public void onFailure(String msg) {
            }
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


}
