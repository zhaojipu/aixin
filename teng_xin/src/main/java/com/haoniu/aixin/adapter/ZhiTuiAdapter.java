package com.haoniu.aixin.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.haoniu.aixin.R;
import com.haoniu.aixin.base.MyApplication;
import com.haoniu.aixin.entity.ZhiTuiNumberInfo;
import com.haoniu.aixin.http.AppConfig;
import com.haoniu.aixin.utils.StringUtil;
import com.zds.base.ImageLoad.GlideUtils;

import java.util.List;

/**
 * zhitui
 */
public class ZhiTuiAdapter extends BaseQuickAdapter<ZhiTuiNumberInfo, BaseViewHolder> {

    public ZhiTuiAdapter(List<ZhiTuiNumberInfo> list) {
        super(R.layout.adapter_invers, list);
    }

    @Override
    protected void convert(BaseViewHolder helper, ZhiTuiNumberInfo item) {
        MyApplication.getInstance().setAvatar(helper.getView(R.id.img_head));
        helper.setText(R.id.tv_name, StringUtil.formateUserPhone(item.getPhone()));
        GlideUtils.loadImageViewLoding(AppConfig.checkimg( item.getUserImg()), helper.getView(R.id.img_head), R.mipmap.img_default_avatar);
        helper.setText(R.id.tv_zhitui_number, item.getOneLevelNum() + "");
    }
}