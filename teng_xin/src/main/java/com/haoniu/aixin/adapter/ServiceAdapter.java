package com.haoniu.aixin.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.haoniu.aixin.http.AppConfig;
import com.haoniu.aixin.R;
import com.haoniu.aixin.base.Constant;
import com.haoniu.aixin.base.MyApplication;
import com.haoniu.aixin.entity.ServiceInfo;
import com.haoniu.aixin.widget.EaseImageView;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.zds.base.ImageLoad.GlideUtils;
import com.zds.base.util.Preference;

import java.util.List;

/**
 * 客服中心
 *
 * @author Administrator
 */
public class ServiceAdapter extends BaseQuickAdapter<ServiceInfo, BaseViewHolder> {

    public ServiceAdapter(List<ServiceInfo> list) {
        super(R.layout.adapter_service_list, list);
    }

    @Override
    protected void convert(BaseViewHolder helper, ServiceInfo item) {
        MyApplication.getInstance().setAvatar((EaseImageView) helper.getView(R.id.img_head));
        helper.setText(R.id.tv_title, item.getNickname());
        //普通客服
        if (item.getType() == null || item.getType().equals(Constant.CUSTOM_KF)) {
            helper.setGone(R.id.tv_number, false);
            GlideUtils.loadImageViewLoding(AppConfig.checkimg( item.getAvatarUrl()), (EaseImageView) helper.getView(R.id.img_head), R.mipmap.ic_launcher);
        } else
            //系统通知
            if (item.getType().equals(Constant.CUSTOM_TZ)) {
                helper.setImageResource(R.id.img_head, R.mipmap.xit);
                EMConversation conversation = EMClient.getInstance().chatManager().getConversation(Constant.ADMIN);
                if (conversation != null && conversation.getUnreadMsgCount() > 0) {
                    helper.setText(R.id.tv_number, conversation.getUnreadMsgCount() + "");
                    helper.setGone(R.id.tv_number, true);
                } else {
                    helper.setGone(R.id.tv_number, false);
                }
            } else if (item.getType().equals(Constant.CUSTOM_GG))
            //平台公告
            {
                helper.setImageResource(R.id.img_head, R.mipmap.gobgg);
                int count = Double.valueOf(Preference.getStringPreferences(mContext, Constant.UNREADCOUNT, "0")).intValue();
                if (count > 0) {
                    helper.setText(R.id.tv_number, count + "");
                    helper.setGone(R.id.tv_number, true);
                } else {
                    helper.setGone(R.id.tv_number, false);
                }
            } else if (item.getType().equals(Constant.CUSTOM_JL)) {
                helper.setGone(R.id.tv_number, false);
                helper.setImageResource(R.id.img_head, R.mipmap.jielong);
            } else if (item.getType().equals(Constant.CUSTOM_CL)) {
                helper.setGone(R.id.tv_number, false);
                helper.setImageResource(R.id.img_head, R.mipmap.cailei);
            }
    }
}