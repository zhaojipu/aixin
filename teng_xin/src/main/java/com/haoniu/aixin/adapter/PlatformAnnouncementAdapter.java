package com.haoniu.aixin.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.haoniu.aixin.R;
import com.haoniu.aixin.base.Constant;
import com.haoniu.aixin.base.MyHelper;
import com.haoniu.aixin.entity.MsgStateInfo;
import com.haoniu.aixin.entity.NoticeInfo;
import com.haoniu.aixin.utils.StringUtil;

import java.util.List;

/**
 * 平台公告
 *
 * @author 赵大帅
 */
public class PlatformAnnouncementAdapter extends BaseQuickAdapter<NoticeInfo, BaseViewHolder> {

    public PlatformAnnouncementAdapter(List<NoticeInfo> list) {
        super(R.layout.adapter_platform_announcement, list);
    }

    @Override
    protected void convert(BaseViewHolder helper, NoticeInfo item) {
        helper.setText(R.id.tv_title, item.getNoticeTitle());
        helper.setText(R.id.tv_time, StringUtil.formatDateMinute2(item.getCreateTime()));
        helper.setText(R.id.tv_content, item.getNoticeContent());
        MsgStateInfo msgStateInfo = MyHelper.getInstance().getMsgStateInfoById(item.getNoticeId() + "");
        if (msgStateInfo.getMsg_is_read() != null && msgStateInfo.getMsg_is_read().equals(Constant.MSG_READ)) {
            helper.setGone(R.id.img_new, false);
        } else {
//            if (item.getStatus() == 0) {
//                helper.setGone(R.id.img_new, true);
//            } else {
                helper.setGone(R.id.img_new, false);
//            }
        }
    }
}