package com.haoniu.aixin.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.haoniu.aixin.R;
import com.haoniu.aixin.entity.RedWaterInfo;

import java.util.List;


/**
 * 红包流水
 */
public class RedWaterAdapter extends BaseQuickAdapter<RedWaterInfo, BaseViewHolder> {

    public RedWaterAdapter(List<RedWaterInfo> list) {
        super(R.layout.red_water_adapter, list);
    }

    /**
     * Implement this method and use the helper to adapt the view to the given item.
     *
     * @param helper A fully initialized helper.
     * @param item   The item that needs to be displayed.
     */
    @Override
    protected void convert(BaseViewHolder helper, RedWaterInfo item) {
//        helper.setText(R.id.name, item.getName());
//        helper.setText(R.id.tv_money, item.getMoney());
//        helper.setText(R.id.tv_time, item.getTime());
//        helper.setGone(R.id.tv_fw_money, false);
//        if (item.getServiceMoney() != null && !item.getServiceMoney().equals("")) {
//            helper.setText(R.id.tv_fw_money, "-" + item.getServiceMoney());
//        }
    }
}