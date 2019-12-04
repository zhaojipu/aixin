package com.haoniu.aixin.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.haoniu.aixin.R;
import com.haoniu.aixin.base.MyApplication;
import com.haoniu.aixin.entity.ZhiTuiNumberInfo;
import com.haoniu.aixin.http.AppConfig;
import com.zds.base.ImageLoad.GlideUtils;
import com.zds.base.util.StringUtil;

import java.util.List;

/**
 * 代理收益
 */
public class FriendAdapter extends BaseQuickAdapter<ZhiTuiNumberInfo, BaseViewHolder> {

    public FriendAdapter(List<ZhiTuiNumberInfo> list) {
        super(R.layout.adapter_friend, list);
    }

    @Override
    protected void convert(BaseViewHolder helper, ZhiTuiNumberInfo item) {
        MyApplication.getInstance().setAvatar(helper.getView(R.id.img_head));
        helper.setText(R.id.tv_name, StringUtil.formateUserPhone(item.getPhone()));
        GlideUtils.loadImageViewLoding(AppConfig.checkimg( item.getUserImg()), helper.getView(R.id.img_head), R.mipmap.img_default_avatar);

    }

}