package com.haoniu.aixin.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.haoniu.aixin.R;
import com.haoniu.aixin.base.BaseActivity;
import com.haoniu.aixin.base.Constant;
import com.haoniu.aixin.base.MyApplication;
import com.haoniu.aixin.entity.EventCenter;
import com.haoniu.aixin.entity.UserInfo;
import com.haoniu.aixin.http.ApiClient;
import com.haoniu.aixin.http.AppConfig;
import com.haoniu.aixin.http.ResultListener;
import com.haoniu.aixin.widget.EaseImageView;
import com.zds.base.ImageLoad.GlideUtils;
import com.zds.base.json.FastJsonUtil;
import com.zds.base.log.XLog;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 我的上级
 */
public class ChiefActivity extends BaseActivity {
    @BindView(R.id.img_head)
    EaseImageView imgHead;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.ll_shangji)
    LinearLayout llShangji;

    private UserInfo userInfo;
    @Override
    protected void initContentView(Bundle bundle) {
        setContentView(R.layout.activity_chief);

    }

    @Override
    protected void initLogic() {
        setTitle("我的上级");
        MyApplication.getInstance().setAvatars(imgHead);
        getData();
    }

    /**
     * 获取我的上级数据
     */
    private void getData(){
        llShangji.setVisibility(View.GONE);
        Map<String,Object> map =  new HashMap<>();
        ApiClient.requestNetHandle(this, AppConfig.chiefUrl, "", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
               XLog.json(json);
                List<UserInfo> list = FastJsonUtil.getList(json,UserInfo.class);
                if (list!=null&&list.size()>0){
                    llShangji.setVisibility(View.VISIBLE);
                    userInfo=list.get(0);
                    if (userInfo==null||userInfo.getNickName()==null){
                        return;
                    }
                    tvName.setText(userInfo.getNickName());
                    GlideUtils.loadImageViewLoding(AppConfig.checkimg(userInfo.getHeadImg()),imgHead, R.mipmap.img_default_avatar);
                }



            }

            @Override
            public void onFailure(String msg) {
                toast(msg);
                llShangji.setVisibility(View.GONE);
            }
        });
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

    @OnClick(R.id.ll_shangji)
    public void onViewClicked() {
        if (userInfo==null){
            return;
        }
        startActivity(new Intent(ChiefActivity.this, ChatActivity.class).putExtra(Constant.EXTRA_USER_ID, userInfo.getIdhs() ).putExtra(Constant.EXTRA_CHAT_TYPE, Constant.CHATTYPE_SINGLE));

    }
}
