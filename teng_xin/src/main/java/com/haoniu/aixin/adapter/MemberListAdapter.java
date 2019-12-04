package com.haoniu.aixin.adapter;

import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.haoniu.aixin.http.AppConfig;
import com.haoniu.aixin.R;
import com.haoniu.aixin.entity.UserInfo;
import com.zds.base.ImageLoad.GlideUtils;
import com.zds.base.util.StringUtil;

import java.util.List;

/**
 * 会员
 */
public class MemberListAdapter extends BaseQuickAdapter<UserInfo, BaseViewHolder> {


    public MemberListAdapter(List<UserInfo> list) {
        super(R.layout.adapter_member_list, list);
    }

    @Override
    protected void convert(BaseViewHolder helper, UserInfo item) {
        helper.setText(R.id.tv_name, item.getNickName());
        helper.setText(R.id.tv_phone, StringUtil.formateUserPhone(item.getPhone()));
//        helper.setText(R.id.tv_message, item.getCreateTime() + "注册");
        GlideUtils.loadImageViewLodingByCircle(AppConfig.checkimg( item.getUserImg()), (ImageView) helper.getView(R.id.img_head), R.mipmap.img_default_avatar);

//        helper.setText(R.id.tv_message, item.getCreateTime() + "注册");
        helper.setGone(R.id.ll_container, false);
    }

}