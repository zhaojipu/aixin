package com.haoniu.aixin.utils;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.haoniu.aixin.http.AppConfig;
import com.haoniu.aixin.R;
import com.haoniu.aixin.base.EaseUI;
import com.haoniu.aixin.base.EaseUI.EaseUserProfileProvider;
import com.haoniu.aixin.domain.EaseUser;
import com.zds.base.ImageLoad.GlideUtils;

public class EaseUserUtils {

    static EaseUserProfileProvider userProvider;

    static {
        userProvider = EaseUI.getInstance().getUserProfileProvider();
    }

    /**
     * get EaseUser according username
     *
     * @param username
     * @return
     */
    public static EaseUser getUserInfo(String username) {
        if (userProvider != null) {
            return userProvider.getUser(username);
        }

        return null;
    }

    /**
     * set user avatar
     *
     * @param username
     */
    public static void setUserAvatar(Context context, String username, ImageView imageView) {
        EaseUser user = getUserInfo(username);
        if (user != null && user.getAvatar() != null) {
            try {
                int avatarResId = Integer.parseInt(user.getAvatar());
                GlideUtils.loadImageView(avatarResId, imageView);
            } catch (Exception e) {
                //use default avatar
                GlideUtils.loadImageViewLoding(AppConfig.checkimg( user.getAvatar()), imageView, R.mipmap.img_default_avatar);
            }
        } else {
            Glide.with(context).load(R.mipmap.img_default_avatar).into(imageView);
        }
    }

    /**
     * set user's nickname
     */
    public static void setUserNick(String username, TextView textView) {
        if (textView != null) {
            EaseUser user = getUserInfo(username);
            if (user != null && user.getNick() != null) {
                textView.setText(user.getNick());
            } else {
                textView.setText(username);
            }
        }
    }

}
