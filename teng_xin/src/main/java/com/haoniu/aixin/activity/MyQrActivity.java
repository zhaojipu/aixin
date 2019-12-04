package com.haoniu.aixin.activity;

import android.Manifest;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.dfqin.grantor.PermissionListener;
import com.github.dfqin.grantor.PermissionsUtil;
import com.haoniu.aixin.R;
import com.haoniu.aixin.base.BaseActivity;
import com.haoniu.aixin.base.MyApplication;
import com.haoniu.aixin.base.Storage;
import com.haoniu.aixin.entity.EventCenter;
import com.haoniu.aixin.entity.PZInfo;
import com.haoniu.aixin.http.AppConfig;
import com.haoniu.aixin.utils.BitmapUtil;
import com.haoniu.aixin.widget.CommonDialog;
import com.zds.base.Toast.ToastUtil;
import com.zxy.tiny.Tiny;
import com.zxy.tiny.callback.BitmapCallback;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author Administrator
 * 日期 2018/8/9
 * 描述 我的二维码
 */

public class MyQrActivity extends BaseActivity {


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
    @BindView(R.id.wv_web)
    WebView wvWeb;
    private String yuming;

    @Override
    protected void initContentView(Bundle bundle) {
        setContentView(R.layout.activity_myqr);

    }

    @Override
    protected void initLogic() {
        setTitle("推广海报");
        PZInfo pzInfo = Storage.GetPZ();
        if (pzInfo != null) {
            yuming=pzInfo.getShareUrl();
        }
        toolbarSubtitle.setText("保存");
        toolbarSubtitle.setVisibility(View.VISIBLE);

        wvWeb.loadUrl(AppConfig.share + yuming  + MyApplication.getInstance().getUserInfo().getInviteCode());
        toolbarSubtitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toSelectQishu();
            }
        });
        WebSettings webSettings = wvWeb.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setUseWideViewPort(true); //将图片调整到适合webview的大小
        webSettings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小

        webSettings.setBuiltInZoomControls(true);
        webSettings.setSupportZoom(false);
        webSettings.setDisplayZoomControls(false);
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK); //关闭webview中缓存
        webSettings.setAllowFileAccess(true); //设置可以访问文件
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true); //支持通过JS打开新窗口
//      webSettings.setLoadsImagesAutomatically(true); //支持自动加载图片
        webSettings.setDefaultTextEncodingName("utf-8");//设置编码格式
        wvWeb.setInitialScale(100);  //代表不缩放。
    }

    @Override
    protected void onEventComing(EventCenter center) {

    }

    @Override
    protected void getBundleExtras(Bundle extras) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    CommonDialog commonDialog;

    private void toSelectQishu() {
        if (commonDialog == null) {
            CommonDialog.Builder builder = new CommonDialog.Builder(MyQrActivity.this).fullWidth().fromBottom()
                    .setView(R.layout.dialog_select_savefile);
            builder.setOnClickListener(R.id.tv_cell, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    commonDialog.dismiss();
                }
            });
            builder.setOnClickListener(R.id.tv_save, new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onClick(View v) {
                    commonDialog.dismiss();
                    save();
                }
            });
            commonDialog = builder.create();
            commonDialog.show();
        } else if (!commonDialog.isShowing()) {
            commonDialog.show();
        }
    }

    private void save() {
        if (PermissionsUtil.hasPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            saveFile();
        } else {
            PermissionsUtil.requestPermission(this, new PermissionListener() {
                @Override
                public void permissionGranted(@NonNull String[] permission) {
                    saveFile();
                }

                @Override
                public void permissionDenied(@NonNull String[] permission) {
                    ToastUtil.toast("请打开读写权限");
                }
            }, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE);
        }


    }

    private void saveFile() {
        showLoading("正在保存");
//        Bitmap QeBitmap = EncodingHandler.createQRCode(yuming + AppConfig.QR + MyApplication.getInstance().getUserInfo().getInviteCode(), 800, 800, BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
        Tiny.getInstance().source(createViewBitmap(wvWeb)).asBitmap().compress(new BitmapCallback() {
            @Override
            public void callback(boolean isSuccess, Bitmap bitmap1, Throwable t) {
                if (isSuccess) {
                    BitmapUtil.saveBitmapInFile(MyQrActivity.this, bitmap1);
                    ToastUtil.toast("保存成功");
                } else {
                    ToastUtil.toast("保存失败");
                }
                dismissLoading();
            }
        });
    }

    public Bitmap createViewBitmap(View v) {
        Bitmap bitmap = Bitmap.createBitmap(v.getWidth(), v.getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        v.draw(canvas);
        return bitmap;
    }
}
