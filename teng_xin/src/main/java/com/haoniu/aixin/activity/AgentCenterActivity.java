package com.haoniu.aixin.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.haoniu.aixin.http.ApiClient;
import com.haoniu.aixin.http.AppConfig;
import com.haoniu.aixin.http.ResultListener;
import com.haoniu.aixin.R;
import com.haoniu.aixin.adapter.AgentAdapter;
import com.haoniu.aixin.base.BaseActivity;
import com.haoniu.aixin.base.Constant;
import com.haoniu.aixin.base.MyApplication;
import com.haoniu.aixin.entity.EventCenter;
import com.haoniu.aixin.entity.UserInfo;
import com.haoniu.aixin.model.MoneyIncomeInfo;
import com.haoniu.aixin.model.MoneyInfo;
import com.haoniu.aixin.widget.CommonDialog;
import com.haoniu.aixin.widget.EaseImageView;
import com.zds.base.ImageLoad.GlideUtils;
import com.zds.base.Toast.ToastUtil;
import com.zds.base.code.encoding.EncodingHandler;
import com.zds.base.json.FastJsonUtil;
import com.zds.base.util.StringUtil;
import com.zds.base.util.UriUtil;
import com.zxy.tiny.Tiny;
import com.zxy.tiny.callback.FileCallback;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 作   者：赵大帅
 * 描   述: 代理中心
 * 邮   箱: 2510722254@qqq.com
 * 日   期: 2017/11/28 14:10
 * 更新日期: 2017/11/28
 */
public class AgentCenterActivity extends BaseActivity {
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

    private AgentAdapter mAdapter;
    private List<MoneyIncomeInfo> list;
    private CommonDialog.Builder builder;
    private TextView tv_show_name,//头部外显
            tv_code,//推荐码
            tv_save, //显示
            tv_money, //日收入
            tv_money2,//月收入
            tv_content;//收益说明

    /**
     * 替代onCreate的使用
     *
     * @param bundle
     */
    @Override
    protected void initContentView(Bundle bundle) {
        setContentView(R.layout.activity_agent_center);
    }

    /**
     * 初始化逻辑
     */
    @Override
    protected void initLogic() {
        mToolbarSubtitle.setText("我的会员");
        mToolbarSubtitle.setVisibility(View.VISIBLE);
        setTitle("代理中心");
        @SuppressLint("InflateParams")
        View headerView = LayoutInflater.from(this).inflate(R.layout.head_view_agent, null);
        tv_show_name = headerView.findViewById(R.id.tv_show_name);
        tv_code = headerView.findViewById(R.id.tv_code);
        tv_save = headerView.findViewById(R.id.tv_save);
        tv_money = headerView.findViewById(R.id.tv_money);
        tv_money2 = headerView.findViewById(R.id.tv_money2);
        @SuppressLint("InflateParams")
        View footView = LayoutInflater.from(this).inflate(R.layout.foot_view_agent, null);
        TextView tv_shuo_show = footView.findViewById(R.id.tv_shuo_show);
        tv_shuo_show.getPaint().setFakeBoldText(true);
        tv_content = footView.findViewById(R.id.tv_content);
        list = new ArrayList<>();
        mAdapter = new AgentAdapter(list);
        mRecycleView.setLayoutManager(new LinearLayoutManager(this));
        mRecycleView.setAdapter(mAdapter);
        mAdapter.addHeaderView(headerView);
        //mAdapter.addFooterView(footView);
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                startActivity(new Intent(AgentCenterActivity.this, CenterWaterDlActivity.class).putExtra("title", "我的收益"));
            }
        });
        tv_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });
        initUser();
        getMoney();
    }

    /**
     * 初始化用户信息
     */
    private void initUser() {
        UserInfo userInfo = MyApplication.getInstance().getUserInfo();
        tv_show_name.setText("邀请码");
       // tv_code.setText(userInfo.getInviteCode());
        tv_content.setText(getResources().getString(R.string.yijidaili));
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

    @OnClick(R.id.toolbar_subtitle)
    public void onViewClicked() {
        startActivity(MyMemberListActivity.class);
    }


    /**
     * 显示弹窗
     */
    public void showDialog() {
        UserInfo userInfo = MyApplication.getInstance().getUserInfo();
        if (builder != null && builder.isShowing()) {
            return;
        }
        builder = new CommonDialog.Builder(this).setView(R.layout.dialog_qrcode).center().loadAniamtion().setOnClickListener(R.id.tv_to_save, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveFile();
            }
        }).setOnClickListener(R.id.ll_container, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                builder.dismiss();
            }
        }).setText(R.id.tv_name, userInfo.getNickName()).setText(R.id.tv_phone, userInfo.getPhone());
        CommonDialog commonDialog = builder.create();
        ImageView imageView = builder.getView(R.id.img_qr_code);
        try {
            imageView.setImageBitmap(EncodingHandler.createQRCode(AppConfig.QR + MyApplication.getInstance().getUserInfo().getUserId(), 800, 800, BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher)));
        } catch (Exception ex) {
            ex.printStackTrace();
//            GlideUtils.loadImageViewLodingByCircle(AppConfig.checkimg( MyApplication.getInstance().getUserInfo().getQrCode(), imageView, R.mipmap.defult_photo);
        }
        EaseImageView imgHead = builder.getView(R.id.img_head);
        MyApplication.getInstance().setAvatar(imgHead);
        GlideUtils.loadImageViewLodingByCircle(AppConfig.checkimg(MyApplication.getInstance().getUserInfo().getUserImg()), imgHead, R.mipmap.img_default_avatar);
        commonDialog.show();
    }

    /**
     * 初始化收入数据
     *
     * @param moneyInfo
     */
    public void initShowRu(MoneyInfo moneyInfo) {
        tv_money.setText("日收益：" + StringUtil.getFormatValue2(moneyInfo.getDay()));
        tv_money2.setText("月收益：" + StringUtil.getFormatValue2(moneyInfo.getMonth()));
        list.clear();
        list.add(new MoneyIncomeInfo("普通会员收益", "", Constant.REFEREETYPE3));
        mAdapter.notifyDataSetChanged();

    }

    //获取收入
    private void getMoney() {
        Map<String, Object> map = new HashMap<>();
        ApiClient.requestNetHandleByGet(this, AppConfig.getMOney, "加载中...", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                MoneyInfo moneyInfo = FastJsonUtil.getObject(json, MoneyInfo.class);
                if (moneyInfo != null) {
                    initShowRu(moneyInfo);
                }
            }

            @Override
            public void onFinsh() {
                super.onFinsh();
            }

            @Override
            public void onFailure(String msg) {
                ToastUtil.toast(msg);
            }
        });

    }


    /**
     * 保存图片
     */
    private void saveFile() {
        showLoading("正在保存...");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Bitmap QeBitmap = EncodingHandler.createQRCode(AppConfig.QR + MyApplication.getInstance().getUserInfo().getUserId(), 800, 800, BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
                    Tiny.getInstance().source(QeBitmap).asFile().compress(new FileCallback() {
                        @Override
                        public void callback(final boolean isSuccess, final String outfile, Throwable t) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (isSuccess) {
                                        Uri uri = UriUtil.getUri(AgentCenterActivity.this, outfile);
                                        try {
                                            MediaStore.Images.Media.insertImage(getContentResolver(),
                                                    new File(outfile).getAbsolutePath(), new File(outfile).getName(), null);
                                            MediaScannerConnection.scanFile(AgentCenterActivity.this, new String[]{new File(outfile).getAbsolutePath()}, null, null);
                                            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
                                            toast("保存成功");
                                        } catch (FileNotFoundException e) {
                                            e.printStackTrace();
                                            toast("保存失败");
                                        }

                                    } else {
                                        toast("保存失败");
                                    }
                                    dismissLoading();
                                }
                            });

                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            toast("保存失败");
                            dismissLoading();
                        }
                    });
                }

            }
        }).start();


    }
}
