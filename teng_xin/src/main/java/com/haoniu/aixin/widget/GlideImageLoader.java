package com.haoniu.aixin.widget;

import android.content.Context;
import android.widget.ImageView;

import com.haoniu.aixin.R;
import com.haoniu.aixin.entity.BannerInfo;
import com.haoniu.aixin.http.AppConfig;
import com.youth.banner.loader.ImageLoader;
import com.zds.base.ImageLoad.GlideUtils;

/**
 * banner 图片加载
 *
 * @author Administrator
 */
public class GlideImageLoader extends ImageLoader {
    @Override
    public void displayImage(Context context, Object path, ImageView imageView) {
        GlideUtils.loadRoundCircleImage(AppConfig.checkimg( ((BannerInfo)path).getImgUrl() + ""), imageView, R.color.white, 0);
    }
}