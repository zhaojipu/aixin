package com.haoniu.aixin.adapter;

import android.support.v4.content.ContextCompat;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.haoniu.aixin.R;
import com.haoniu.aixin.base.MyApplication;
import com.haoniu.aixin.entity.RedPacketInfo;
import com.haoniu.aixin.http.AppConfig;
import com.haoniu.aixin.utils.StringUtil;
import com.haoniu.aixin.widget.EaseImageView;
import com.zds.base.ImageLoad.GlideUtils;

import java.util.List;

/**
 * 抢红包的人
 */
public class RedPacketAdapter extends BaseQuickAdapter<RedPacketInfo.RedPacketNumberListBean, BaseViewHolder> {


    /**
     * 最大金额
     */
    private double maxMoney;
    /**
     * 最小金额
     */
    private double minMoney;

    /**
     * 是否结束
     */
    private boolean isfirsh;

    /**
     * 1.接龙2.踩雷3福利
     */
    private int roomType;

    /**
     * 雷点
     */
    private String points;
    /**
     *
     */
    private boolean isHave;

    public RedPacketAdapter(List<RedPacketInfo.RedPacketNumberListBean> list) {
        super(R.layout.adapter_item_redpacket, list);
    }

    @Override
    protected void convert(BaseViewHolder helper, RedPacketInfo.RedPacketNumberListBean item) {
        helper.setText(R.id.tv_name, item.getNickName());
        try {
            helper.setText(R.id.tv_time, StringUtil.formatDateMinute2(item.getRabTime()));
        } catch (Exception e) {
        }
        /**
         * 手气最佳、手气最差
         */
        if (isfirsh) {
            helper.setGone(R.id.ll_shouqu, false);
            if (item.getMoney() == minMoney) {
                helper.setImageResource(R.id.img_shouqi, R.mipmap.img_sqzc);
                helper.setText(R.id.tv_shouqi, "手气最差");
                helper.setTextColor(R.id.tv_shouqi, ContextCompat.getColor(mContext, R.color.sqzc));
            } else if (item.getMoney() == maxMoney) {
                helper.setImageResource(R.id.img_shouqi, R.mipmap.img_sqzj);
                helper.setText(R.id.tv_shouqi, "手气最佳");
                helper.setTextColor(R.id.tv_shouqi, ContextCompat.getColor(mContext, R.color.sqzj));
            } else {
                helper.setGone(R.id.ll_shouqu, false);
            }
        } else {
            helper.setGone(R.id.ll_shouqu, false);
        }
        helper.setGone(R.id.ll_cailei, false);
        if (roomType == 1 || roomType == 2) {
            if (isHave && points != null && points.indexOf(StringUtil.getFormatValue2(item.getMoney()).substring((StringUtil.getFormatValue2(item.getMoney())).length() - 1)) != -1) {
                helper.setGone(R.id.ll_cailei, true);
            }
        }
        if (item.getIsPtUser() == 1) {
            helper.setGone(R.id.ll_shouqu, true);
            helper.setGone(R.id.ll_cailei, false);
            helper.setImageResource(R.id.img_shouqi, R.mipmap.mianfei);
            helper.setText(R.id.tv_shouqi, "免死");
            helper.setTextColor(R.id.tv_shouqi, ContextCompat.getColor(mContext, R.color.sqzj));

            if (!isfirsh) {
                StringBuilder sb = new StringBuilder(StringUtil.getFormatValue2(item.getMoney()));
                sb.replace(sb.length() - 1, sb.length(), "*");
                helper.setText(R.id.tv_money, sb.toString() + mContext.getResources().getString(R.string.glod));
            }else {
                helper.setText(R.id.tv_money, StringUtil.getFormatValue2(item.getMoney()) + mContext.getResources().getString(R.string.glod));

            }

        }else {
            helper.setText(R.id.tv_money, StringUtil.getFormatValue2(item.getMoney()) + mContext.getResources().getString(R.string.glod));
        }

        MyApplication.getInstance().setAvatar((EaseImageView) helper.getView(R.id.avatar_user));
        GlideUtils.loadImageViewLodingByCircle(AppConfig.checkimg( item.getHeadImg()), (EaseImageView) helper.getView(R.id.avatar_user), R.mipmap.img_default_avatar);
    }

    public void setRoomType(int roomType) {
        this.roomType = roomType;
    }

    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
    }

    public boolean isHave() {
        return isHave;
    }

    public void setHave(boolean have) {
        isHave = have;
    }

    public void setMaxMoney(double maxMoney) {
        this.maxMoney = maxMoney;
    }

    public void setMinMoney(double minMoney) {
        this.minMoney = minMoney;
    }

    public void setIsfirsh(boolean isfirsh) {
        this.isfirsh = isfirsh;
    }
}