package com.haoniu.aixin.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.haoniu.aixin.R;
import com.haoniu.aixin.base.MyApplication;
import com.haoniu.aixin.entity.UserInfo;
import com.haoniu.aixin.http.AppConfig;
import com.zds.base.ImageLoad.GlideUtils;

import java.util.List;

/**
 * 人工客服
 */
public class PeopleServiceAdapter extends BaseQuickAdapter<UserInfo, BaseViewHolder> {

    public PeopleServiceAdapter(List<UserInfo> list) {
        super(R.layout.adapter_people_server, list);
    }

    @Override
    protected void convert(BaseViewHolder helper, UserInfo item) {
        helper.setText(R.id.tv_name, item.getNickName());
        MyApplication.getInstance().setAvatars(helper.getView(R.id.img_head));
        GlideUtils.loadImageViewLoding(AppConfig.checkimg( item.getHeadImg()), helper.getView(R.id.img_head), R.mipmap.img_default_avatar);
    }

}