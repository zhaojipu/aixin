package com.haoniu.aixin.adapter;

import android.view.View;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.haoniu.aixin.http.AppConfig;
import com.haoniu.aixin.R;
import com.haoniu.aixin.domain.EaseUser;
import com.haoniu.aixin.utils.EaseUserUtils;
import com.zds.base.ImageLoad.GlideUtils;
import com.zds.base.util.StringUtil;

import java.util.List;

/**
 * 房间成员
 */
public class RoomDeatilAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

    public RoomDeatilAdapter(List<String> list) {
        super(R.layout.room_grid, list);
    }

    private int mode = 0;

    @Override
    protected void convert(BaseViewHolder helper, String item) {
        helper.convertView.setVisibility(View.VISIBLE);
        helper.setGone(R.id.tv_room_mine, false);
        EaseUser userInfo = EaseUserUtils.getUserInfo(item);
        if ("-1".equals(item)) {
            helper.setImageResource(R.id.iv_avatar, R.drawable.em_smiley_minus_btn);
            helper.setText(R.id.tv_name, "");
            if (mode != 0) {
                helper.convertView.setVisibility(View.GONE);
            }
            helper.setGone(R.id.badge_delete, false);
        } else if ("-2".equals(item)) {
            helper.setText(R.id.tv_name, "");
            helper.setGone(R.id.badge_delete, false);
            if (mode != 0) {
                helper.convertView.setVisibility(View.GONE);
            }
            helper.setImageResource(R.id.iv_avatar, R.drawable.em_smiley_add_btn);
        } else {
            if (mode == 1) {
                helper.setGone(R.id.badge_delete, true);
            } else {
                helper.setGone(R.id.badge_delete, false);
            }
            helper.setText(R.id.tv_name, userInfo.getNickname());
            if ("admin".equals(item)) {
                helper.setImageResource(R.id.iv_avatar, R.mipmap.ic_launcher);
            } else {
                GlideUtils.loadImageViewLoding(AppConfig.checkimg( userInfo.getAvatar()), (ImageView) helper.getView(R.id.iv_avatar), R.mipmap.img_default_avatar);
            }
            helper.addOnClickListener(R.id.badge_delete);
            if (helper.getPosition() == 0) {
                if (StringUtil.isEmpty(userInfo.getNickname())) {
                    if (userInfo.getUsername().equals("admin")) {

                    }
                    helper.setText(R.id.tv_name, userInfo.getUsername());
                }
                helper.setGone(R.id.tv_room_mine, true);
            }
        }
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }
}