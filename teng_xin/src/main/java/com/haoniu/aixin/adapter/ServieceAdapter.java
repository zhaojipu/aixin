package com.haoniu.aixin.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.haoniu.aixin.R;
import com.haoniu.aixin.base.MyApplication;
import com.haoniu.aixin.entity.ServersInfo;
import com.haoniu.aixin.http.AppConfig;
import com.haoniu.aixin.widget.EaseImageView;
import com.zds.base.ImageLoad.GlideUtils;

import java.util.List;

/**
 * 客服
 */
public class ServieceAdapter extends BaseQuickAdapter<ServersInfo, BaseViewHolder> {

    public ServieceAdapter(List<ServersInfo> list) {
        super(R.layout.adapter_service, list);
    }

    @Override
    protected void convert(BaseViewHolder helper, ServersInfo item) {
        helper.setText(R.id.tv_name, item.getNickName());
        MyApplication.getInstance().setAvatar((EaseImageView) helper.getView(R.id.img_head));
        GlideUtils.loadImageViewLoding(AppConfig.checkimg( item.getHeadImg()), helper.getView(R.id.img_head), R.mipmap.img_default_avatar);

    }


}