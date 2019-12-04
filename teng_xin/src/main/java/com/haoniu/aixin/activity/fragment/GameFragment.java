package com.haoniu.aixin.activity.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.haoniu.aixin.R;
import com.haoniu.aixin.WebViewActivity2;
import com.haoniu.aixin.activity.CustomServiceActivity;
import com.haoniu.aixin.activity.SaoleiActivity;
import com.haoniu.aixin.base.Storage;
import com.haoniu.aixin.entity.BannerInfo;
import com.haoniu.aixin.entity.EventCenter;
import com.haoniu.aixin.entity.NoticeInfo;
import com.haoniu.aixin.http.ApiClient;
import com.haoniu.aixin.http.AppConfig;
import com.haoniu.aixin.http.ResultListener;
import com.haoniu.aixin.widget.BannerHeadView;
import com.haoniu.aixin.widget.CommonDialog;
import com.youth.banner.listener.OnBannerListener;
import com.zds.base.Toast.ToastUtil;
import com.zds.base.json.FastJsonUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * 游戏
 *
 * @author Administrator
 */
public class GameFragment extends EaseBaseFragment {
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
    @BindView(R.id.tv_gonggao)
    TextView tvGonggao;
    @BindView(R.id.bannerview)
    BannerHeadView mBannerview;
    @BindView(R.id.ll_saolei_gz)
    LinearLayout llSaoleiGz;
    @BindView(R.id.tv_sl)
    TextView tvSl;
    @BindView(R.id.ll_saolei)
    LinearLayout llSaolei;
    @BindView(R.id.ll_jinqiang_gz)
    LinearLayout llJinqiangGz;
    @BindView(R.id.tv_jinqiang)
    TextView tvJinqiang;
    @BindView(R.id.ll_jinqiang)
    LinearLayout llJinqiang;
    @BindView(R.id.ll_niuniu_gz)
    LinearLayout llNiuniuGz;
    @BindView(R.id.tv_niuniu)
    TextView tvNiuniu;
    @BindView(R.id.ll_niuniu)
    LinearLayout llNiuniu;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_game, container, false);
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
        toolbarTitle.setText("游戏");
//        initBar();
        mBannerview.getBanner().setOnBannerListener(new OnBannerListener() {
            @Override
            public void OnBannerClick(int position) {
                startActivity(new Intent(getActivity(),CustomServiceActivity.class));
            }
        });
        getPlatformAnnouncement();
        getBanner();
    }

    private void getBanner(){
        Map<String, Object> map = new HashMap<>();
        ApiClient.requestNetHandle(getActivity(), AppConfig.getBannerUrl, "", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                List<BannerInfo> list =FastJsonUtil.getList(json,BannerInfo.class);
                mBannerview.upData(list);
            }

            @Override
            public void onFailure(String msg) {
            }
        });
    }


    /**
     * 获取平台公告数据
     */
    private void getPlatformAnnouncement() {
        Map<String, Object> map = new HashMap<>();
        map.put("pageNum", 1);
        map.put("pageSize", 1);
        ApiClient.requestNetHandle(getActivity(), AppConfig.PlatformAnnouncement, "", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                List<NoticeInfo> applyRecordInfos = FastJsonUtil.getList(json, "list", NoticeInfo.class);
                if (applyRecordInfos != null && applyRecordInfos.size() > 0) {
                    tvGonggao.setText(applyRecordInfos.get(0).getNoticeContent());
                    tvGonggao.setSelected(true);
                    if (applyRecordInfos.get(0).getNoticeId() != Storage.getGGId()) {
                        setGG(applyRecordInfos.get(0));
                    }
                }
            }

            @Override
            public void onFailure(String msg) {
            }
        });

    }


    CommonDialog commonDialog;
    TextView tvTitle;
    TextView tvContent;

    /**
     * 公告r
     */
    private void setGG(NoticeInfo noticeInfo) {
        if (commonDialog != null && commonDialog.isShowing()) {
            commonDialog.dismiss();
        }
        commonDialog = new CommonDialog.Builder(getActivity()).setView(R.layout.dialog_gg).center().loadAniamtion()
                .setOnClickListener(R.id.img_close, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        commonDialog.dismiss();
                    }
                }).create();
        tvTitle = commonDialog.getView(R.id.tv_title);
        tvContent = commonDialog.getView(R.id.tv_content);
        if (noticeInfo != null) {
            tvTitle.setText("通知");
            tvContent.setText(noticeInfo.getNoticeContent());
            Storage.saveGG(noticeInfo.getNoticeId());
        }
        commonDialog.setCancelable(false);
        commonDialog.show();
    }


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


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.tv_sl,R.id.ll_jinqiang, R.id.ll_saolei_gz, R.id.ll_jinqiang_gz, R.id.ll_saolei, R.id.ll_niuniu, R.id.ll_niuniu_gz})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_saolei_gz:
                // 扫雷
               // startActivity(new Intent(getActivity(),WebViewActivity.class));
                startActivity(new Intent(getActivity(),WebViewActivity2.class).putExtra("title","游戏规则").putExtra("url",AppConfig.guize));
                break;
            case R.id.tv_sl:
                startActivity(new Intent(getActivity(), SaoleiActivity.class).putExtra("type", 2));
//
                break;
            case R.id.ll_jinqiang:
                ToastUtil.toast("功能暂未开放，敬请期待！");
                break;
            case R.id.ll_jinqiang_gz:
                // 禁抢
                ToastUtil.toast("功能暂未开放，敬请期待！");
                break;
            case R.id.ll_niuniu:
                ToastUtil.toast("功能暂未开放，敬请期待！");
                break;
            case R.id.ll_niuniu_gz:
                ToastUtil.toast("功能暂未开放，敬请期待！");
                break;
            default:
                break;
        }
    }
}
