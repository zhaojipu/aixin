package com.haoniu.aixin.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.haoniu.aixin.R;
import com.haoniu.aixin.base.BaseActivity;
import com.haoniu.aixin.base.MyApplication;
import com.haoniu.aixin.entity.EventCenter;
import com.haoniu.aixin.entity.UserInfo;
import com.haoniu.aixin.http.AppConfig;
import com.zds.base.ImageLoad.GlideUtils;
import com.zds.base.aes.AESCipher;
import com.zds.base.code.WriterException;
import com.zds.base.code.activity.ScanResultInfo;
import com.zds.base.code.encoding.EncodingHandler;
import com.zds.base.json.FastJsonUtil;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * 我的二维码
 */
public class PersonQrCodeActivity extends BaseActivity {
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
    @BindView(R.id.img_head)
    ImageView imgHead;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.img_code)
    ImageView imgCode;
    @BindView(R.id.ll_qrcode)
    RelativeLayout llQrcode;
    @BindView(R.id.tv_phone)
    TextView tvPhone;

    @Override
    protected void initContentView(Bundle bundle) {
        setContentView(R.layout.activity_person_qrcode);

    }

    @Override
    protected void initLogic() {
        setTitle("我的二维码");
        UserInfo userInfo = MyApplication.getInstance().getUserInfo();
        ScanResultInfo scanResultInfo = new ScanResultInfo();
        scanResultInfo.setPhone(userInfo.getPhone());
        scanResultInfo.setHeadImg(userInfo.getHeadImg());
        scanResultInfo.setName(userInfo.getNickName());
        if (userInfo != null) {
            Bitmap QeBitmap = null;
            try {

                QeBitmap = EncodingHandler.createQRCode(AESCipher.encrypt(FastJsonUtil.toJSONString(scanResultInfo)), 1000);
            } catch (WriterException e) {
                e.printStackTrace();
            }
            if (QeBitmap != null) {
                imgCode.setImageBitmap(QeBitmap);
            }
            tvName.setText(userInfo.getNickName());
            tvPhone.setText("我的爱聊号："+userInfo.getPhone());
            GlideUtils.loadImageViewLodingByCircle(AppConfig.checkimg(userInfo.getUserImg()), imgHead, R.mipmap.img_default_avatar);

        }


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
}
