package com.haoniu.aixin.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.haoniu.aixin.R;
import com.haoniu.aixin.base.Constant;
import com.haoniu.aixin.base.MyHelper;
import com.haoniu.aixin.entity.ApplyRecordInfo;
import com.haoniu.aixin.entity.ApplyStateInfo;

import java.util.List;

/**
 * 我的审列表
 */
public class ReviewAdapter extends BaseQuickAdapter<ApplyRecordInfo, BaseViewHolder> {

    public ReviewAdapter(List<ApplyRecordInfo> list) {
        super(R.layout.adapter_review, list);
    }

    @Override
    protected void convert(BaseViewHolder helper, ApplyRecordInfo item) {
        helper.setText(R.id.tv_title, "审核通知");
        helper.setText(R.id.tv_time, item.getCreateTime());
        helper.setText(R.id.tv_content, item.getNickname() + "发来一条审核通知，点击查看详情");
        // 审核通过
        if (item.getStatus() == Constant.AUDITSTATEPASSED) {
            helper.setImageResource(R.id.img_reject_or_passed, R.mipmap.img_passed);
            helper.setGone(R.id.img_reject_or_passed, true);
            //拒绝
        } else if (item.getStatus() == Constant.AUDITSTATEBEREJECTED) {
            helper.setGone(R.id.img_reject_or_passed, true);
            helper.setImageResource(R.id.img_reject_or_passed, R.mipmap.img_rejected);
            //审核中
        } else {
            helper.setGone(R.id.img_reject_or_passed, false);
        }
        ApplyStateInfo applyStateInfo = MyHelper.getInstance().getApplyStateInfoById(item.getId() + "");
        if (applyStateInfo.getApply_is_read() != null && applyStateInfo.getApply_is_read().equals(Constant.MSG_READ)) {
            helper.setGone(R.id.img_new, false);
        } else {
            helper.setGone(R.id.img_new, true);
        }
    }
}