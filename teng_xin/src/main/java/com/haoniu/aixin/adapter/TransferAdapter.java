package com.haoniu.aixin.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.haoniu.aixin.http.AppConfig;
import com.haoniu.aixin.R;
import com.haoniu.aixin.base.MyApplication;
import com.haoniu.aixin.entity.TransferRecordInfo;
import com.haoniu.aixin.widget.EaseImageView;
import com.zds.base.ImageLoad.GlideUtils;
import com.zds.base.util.StringUtil;

import java.util.List;

/**
 * 转账记录
 */
public class TransferAdapter extends BaseQuickAdapter<TransferRecordInfo, BaseViewHolder> {

    public TransferAdapter(List<TransferRecordInfo> list) {
        super(R.layout.adapter_transfer_record, list);
    }

    /**
     * Implement this method and use the helper to adapt the view to the given item.
     *
     * @param helper A fully initialized helper.
     * @param item   The item that needs to be displayed.
     */
    @Override
    protected void convert(BaseViewHolder helper, TransferRecordInfo item) {
        MyApplication.getInstance().setAvatar((EaseImageView) helper.getView(R.id.img_head));
        if (item.getWithdrawCash() == 0) {
            helper.setGone(R.id.tv_money_fee, false);
        } else {
            helper.setGone(R.id.tv_money_fee, true);
        }
        helper.setText(R.id.tv_time, item.getCreateTime());
        if (item.getUserId() == MyApplication.getInstance().getUserInfo().getUserId()) {
            GlideUtils.loadImageViewLoding(AppConfig.checkimg( item.getFriendAvatarUrl()), (EaseImageView) helper.getView(R.id.img_head), R.mipmap.img_default_avatar);
            helper.setText(R.id.tv_name, "转账给 (" + item.getFriendName() + ")");
            helper.setText(R.id.tv_money, "-" + StringUtil.getFormatValue2(item.getMoney()) + mContext.getResources().getString(R.string.glod));
            helper.setText(R.id.tv_money_fee, "手续费 " + item.getWithdrawCash() + mContext.getResources().getString(R.string.glod));
        } else {
            GlideUtils.loadImageViewLoding(AppConfig.checkimg( item.getAvatarUrl()), (EaseImageView) helper.getView(R.id.img_head), R.mipmap.img_default_avatar);
            helper.setText(R.id.tv_name, item.getUserName() + "给你转账");
            helper.setText(R.id.tv_money, "+" + StringUtil.getFormatValue2(item.getMoney()) + mContext.getResources().getString(R.string.glod));
            helper.setText(R.id.tv_money_fee, "手续费 " + item.getWithdrawCash() + mContext.getResources().getString(R.string.glod));
        }

    }


}