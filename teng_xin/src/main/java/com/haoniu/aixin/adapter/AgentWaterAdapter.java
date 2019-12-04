package com.haoniu.aixin.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.haoniu.aixin.R;
import com.haoniu.aixin.entity.QBLSInfo;
import com.zds.base.util.StringUtil;

import java.util.List;

/**
 * 代理收益
 */
public class AgentWaterAdapter extends BaseQuickAdapter<QBLSInfo, BaseViewHolder> {

    public AgentWaterAdapter(List<QBLSInfo> list) {
        super(R.layout.adapter_agent, list);
    }

    @Override
    protected void convert(BaseViewHolder helper, QBLSInfo item) {
        helper.setText(R.id.tv_name, item.getDescription());
        helper.setText(R.id.tv_time, item.getCreateTime());
        helper.setText(R.id.tv_money, StringUtil.getFormatValue3(item.getMoney()));
    }

}