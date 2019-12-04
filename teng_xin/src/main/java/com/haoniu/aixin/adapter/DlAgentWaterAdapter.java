package com.haoniu.aixin.adapter;


import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.haoniu.aixin.R;
import com.haoniu.aixin.entity.ShouyiInfo;
import com.zds.base.util.StringUtil;

import java.util.List;

/**
 * 代理收益流水
 *
 * @author Administrator
 */
public class DlAgentWaterAdapter extends BaseQuickAdapter<ShouyiInfo, BaseViewHolder> {

    public DlAgentWaterAdapter(List<ShouyiInfo> list) {
        super(R.layout.adapter_dl, list);
    }

    @Override
    protected void convert(BaseViewHolder helper, ShouyiInfo item) {
        if (helper.getAdapterPosition() == 0) {
            helper.setGone(R.id.ll_tiltes, true);
        } else {
            helper.setGone(R.id.ll_tiltes, false);
        }
        helper.setText(R.id.tv_nick, item.getFromUserName());
        String typeAward="" ;
        if (item.getAwardType()==1){
            typeAward="(发)";
        }else if (item.getAwardType()==2){
            typeAward="(抢)";
        }

        helper.setText(R.id.tv_money, StringUtil.getFormatValue4(item.getAwardMoney()) + typeAward);
        helper.setText(R.id.tv_time, com.haoniu.aixin.utils.StringUtil.formatDateMinute2(item.getCreateTime()).replace(" ","\n"));
        helper.setText(R.id.tv_lv, item.getLevel() + "");
    }

}