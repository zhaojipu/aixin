package com.haoniu.aixin.adapter;

import android.view.View;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.haoniu.aixin.R;
import com.haoniu.aixin.entity.RoomInfo;
import com.haoniu.aixin.entity.UserInfo;
import com.haoniu.aixin.http.AppConfig;
import com.zds.base.ImageLoad.GlideUtils;

import java.util.List;

/**
 * 房间成员
 */
public class RoomDeatilNewAdapter extends BaseQuickAdapter<UserInfo, BaseViewHolder> {
    private RoomInfo.RoomBossDTOListBean roomBossDTOListBean;

    public RoomDeatilNewAdapter(List<UserInfo> list) {
        super(R.layout.room_grid, list);
    }
    @Override
    protected void convert(BaseViewHolder helper, UserInfo item) {
        helper.convertView.setVisibility(View.VISIBLE);
        helper.setGone(R.id.tv_room_mine,false);
        if (roomBossDTOListBean!=null){
            if (roomBossDTOListBean.getId()==item.getId()){
                helper.setGone(R.id.tv_room_mine,true);
            }
        }
        helper.setGone(R.id.tv_room_mine, false);
        helper.setText(R.id.tv_name, item.getNickName());
        GlideUtils.loadImageViewLoding(AppConfig.checkimg( item.getHeadImg()), (ImageView) helper.getView(R.id.iv_avatar), R.mipmap.img_default_avatar);
    }


    public RoomInfo.RoomBossDTOListBean getRoomBossDTOListBean() {
        return roomBossDTOListBean;
    }

    public void setRoomBossDTOListBean(RoomInfo.RoomBossDTOListBean roomBossDTOListBean) {
        this.roomBossDTOListBean = roomBossDTOListBean;
    }
}