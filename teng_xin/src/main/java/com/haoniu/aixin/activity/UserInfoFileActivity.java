package com.haoniu.aixin.activity;

import android.content.Intent;
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
import com.haoniu.aixin.http.ApiClient;
import com.haoniu.aixin.http.AppConfig;
import com.haoniu.aixin.http.ResultListener;
import com.haoniu.aixin.widget.CommonDialog;
import com.haoniu.aixin.widget.EaseImageView;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.zds.base.ImageLoad.GlideUtils;
import com.zds.base.Toast.ToastUtil;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * 设置
 *
 * @author Administrator
 */


public class UserInfoFileActivity extends BaseActivity {


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
    @BindView(R.id.head_img)
    EaseImageView headImg;
    @BindView(R.id.ll_head)
    LinearLayout llHead;
    @BindView(R.id.nickName)
    TextView nickName;
    @BindView(R.id.ll_nickName)
    LinearLayout llNickName;
    @BindView(R.id.ll_qr)
    LinearLayout llQr;
    private String img_path;


    /**
     * 替代onCreate的使用
     *
     * @param bundle
     */
    @Override
    protected void initContentView(Bundle bundle) {
        setContentView(R.layout.activity_user_file);
    }


    /**
     * 初始化逻辑
     */
    @Override
    protected void initLogic() {
        setTitle("个人资料");
        initUserInfo();
    }

    /**
     * 初始化用户信息
     */
    private void initUserInfo() {

        UserInfo userInfo = MyApplication.getInstance().getUserInfo();
        MyApplication.getInstance().setAvatars(headImg);
        if (userInfo != null) {
            GlideUtils.loadImageViewLodingByCircle(AppConfig.checkimg( userInfo.getUserImg()), headImg, R.mipmap.img_default_avatar);
            nickName.setText(userInfo.getNickName());

        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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


    @Override
    protected void onResume() {
        super.onResume();
        // initUserInfo();
    }

    @OnClick({R.id.ll_nickName, R.id.ll_head,R.id.ll_qr})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_nickName:
                //昵称
                startActivityForResult(new Intent(UserInfoFileActivity.this, NickNameActivity.class).putExtra("nickname", nickName.getText().toString() == null ? "" : nickName.getText().toString()), 12);
                break;
            case R.id.ll_head:
                //头像
                toSelectPic();
                break;
                //我的二维码
            case R.id.ll_qr:
                startActivity(PersonQrCodeActivity.class);
                break;
            default:
        }
    }

    /**
     * 选择图片上传
     */
    private void toSelectPic() {
        final CommonDialog.Builder builder = new CommonDialog.Builder(this).fullWidth().fromBottom()
                .setView(R.layout.dialog_select_head);
        builder.setOnClickListener(R.id.tv_cell, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.dismiss();
            }
        });
        builder.setOnClickListener(R.id.tv_xiangji, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.dismiss();
                PictureSelector.create(UserInfoFileActivity.this)
                        .openCamera(PictureMimeType.ofImage())
                        .selectionMode(PictureConfig.SINGLE)
                        .withAspectRatio(1, 1)
                        .enableCrop(true)
                        .showCropFrame(false)
                        .showCropGrid(false)
                        .freeStyleCropEnabled(true)
                        .circleDimmedLayer(true)
                        .forResult(PictureConfig.CHOOSE_REQUEST);
            }
        });
        builder.setOnClickListener(R.id.tv_xiangce, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.dismiss();
                PictureSelector.create(UserInfoFileActivity.this)
                        .openGallery(PictureMimeType.ofImage())
                        .selectionMode(PictureConfig.SINGLE)
                        .withAspectRatio(1, 1)
                        .enableCrop(true)
                        .showCropFrame(false)
                        .showCropGrid(false)
                        .freeStyleCropEnabled(true)
                        .circleDimmedLayer(true)
                        .forResult(PictureConfig.CHOOSE_REQUEST);

            }
        });
        builder.create().show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PictureConfig.CHOOSE_REQUEST) {
            List<LocalMedia> selectList = PictureSelector.obtainMultipleResult(data);
            if (selectList.size() > 0) {
                if (selectList.get(0).isCut()) {
                    img_path = selectList.get(0).getCutPath();
                } else {
                    img_path = selectList.get(0).getPath();
                }
                GlideUtils.loadImageViewFile(img_path, headImg, R.mipmap.img_default_avatar);
                saveHead(img_path);
            }
        } else if (requestCode == 12) {
            if (resultCode == 101) {
                nickName.setText(data.getStringExtra("nickName"));
                upNickName();
            }
        } else if (requestCode == 13) {
//            if (resultCode == 102) {
//                realName.setText(data.getStringExtra("realName"));
//                upRealName();
//            }
        }
    }

    /**
     * 更新头像
     *
     * @param filePath
     */
    private void saveHead(String filePath) {
        ApiClient.requestNetHandleFile(UserInfoFileActivity.this, AppConfig.upHead, "正在上传...", new File(filePath), new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                ToastUtil.toast(msg);
                UserInfo userInfo = MyApplication.getInstance().getUserInfo();
                userInfo.setUserImg(json);
                MyApplication.getInstance().saveUserInfo(userInfo);
            }

            @Override
            public void onFailure(String msg) {
                ToastUtil.toast(msg);
            }
        });
    }

    /**
     * 更新昵称
     */
    private void upNickName() {
        Map<String, Object> map = new HashMap<>();
        map.put("nickName", nickName.getText().toString());
        ApiClient.requestNetHandle(this, AppConfig.upDataNickName, "正在更新...", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                UserInfo userInfo = MyApplication.getInstance().getUserInfo();
                userInfo.setNickName(nickName.getText().toString());
                MyApplication.getInstance().saveUserInfo(userInfo);
                ToastUtil.toast(msg);
            }

            @Override
            public void onFailure(String msg) {
                nickName.setText(MyApplication.getInstance().getUserInfo().getNickName());
                ToastUtil.toast(msg);
            }
        });
    }

//    /**
//     * 更新真实姓名
//     */
//    private void upRealName() {
//        Map<String, Object> map = new HashMap<>();
//        map.put("realName", realName.getText().toString());
//        ApiClient.requestNetHandle(this, AppConfig.upDataRealName, "正在更新...", map, new ResultListener() {
//            @Override
//            public void onSuccess(String json, String msg) {
//                UserInfo userInfo = MyApplication.getInstance().getUserInfo();
//                userInfo.setRealName(realName.getText().toString());
//                MyApplication.getInstance().saveUserInfo(userInfo);
//                ToastUtil.toast(msg);
//            }
//
//            @Override
//            public void onFailure(String msg) {
//                if (!StringUtil.isEmpty(MyApplication.getInstance().getUserInfo().getRealName())) {
//                    realName.setText(MyApplication.getInstance().getUserInfo().getRealName());
//                } else {
//                    realName.setText("未认证");
//                }
//
//                ToastUtil.toast(msg);
//            }
//        });
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);

    }
}
