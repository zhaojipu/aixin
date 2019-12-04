package com.haoniu.aixin.base;


import com.haoniu.aixin.entity.PZInfo;
import com.haoniu.aixin.entity.UserInfo;
import com.zds.base.log.XLog;
import com.zds.base.util.Preference;
import com.zds.base.util.Utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.io.StreamCorruptedException;

public class Storage {
    /**
     * 保存token
     *
     * @param token
     */
    public static void saveToken(String token) {
        if (token == null || "".equals(token)) {
            return;
        }
        Preference.saveStringPreferences(Utils.getContext(), "token", token);
    }

    /**
     * 获取token
     */
    public static String getToken() {
        XLog.d("token", Preference.getStringPreferences(Utils.getContext(), "token", ""));
        return Preference.getStringPreferences(Utils.getContext(), "token", "");
    }

    /**
     * 清除缓存用户信息数据
     */
    public static void ClearUserInfo() {
        new File(MyApplication.getInstance().getCacheDir().getPath() + "/"
                + "feiyu_userinfo.bean").delete();
    }

    /**
     * 保存用户信息至缓存
     *
     * @param userInfo
     */
    public static void saveUsersInfo(UserInfo userInfo) {
        try {
            new ObjectOutputStream(new FileOutputStream(new File(MyApplication
                    .getInstance().getCacheDir().getPath()
                    + "/" + "feiyu_userinfo.bean"))).writeObject(userInfo);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 读取缓存数据
     *
     * @return
     */
    public static UserInfo GetUserInfo() {
        File file = new File(MyApplication.getInstance().getCacheDir().getPath()
                + "/" + "feiyu_userinfo.bean");
        if (!file.exists()) {
            return null;
        }

        if (file.isDirectory()) {
            return null;
        }

        if (!file.canRead()) {
            return null;
        }

        try {
            @SuppressWarnings("resource")
            UserInfo userInfo = (UserInfo) new ObjectInputStream(
                    new BufferedInputStream(new FileInputStream(file)))
                    .readObject();
            return userInfo;
        } catch (OptionalDataException e) {
            e.printStackTrace();
        } catch (StreamCorruptedException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 保存配置信息至缓存
     *
     * @param pzInfoMap
     */
    public static void savePZ(PZInfo pzInfoMap) {
        try {
            new ObjectOutputStream(new FileOutputStream(new File(MyApplication
                    .getInstance().getCacheDir().getPath()
                    + "/" + "feiyu_PZ.bean"))).writeObject(pzInfoMap);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 读取缓存数据配置信息
     *
     * @return
     */
    public static PZInfo GetPZ() {
        File file = new File(MyApplication.getInstance().getCacheDir().getPath()
                + "/" + "feiyu_PZ.bean");
        if (!file.exists()) {
            return null;
        }

        if (file.isDirectory()) {
            return null;
        }

        if (!file.canRead()) {
            return null;
        }

        try {
            @SuppressWarnings("resource")
                    PZInfo pzInfoMap = (PZInfo) new ObjectInputStream(
                    new BufferedInputStream(new FileInputStream(file)))
                    .readObject();
            return pzInfoMap;
        } catch (OptionalDataException e) {
            e.printStackTrace();
        } catch (StreamCorruptedException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 保存 公告
     *
     * @param id
     */
    public static void saveGG(int id) {
        Preference.saveIntPreferences(Utils.getContext(), "ggid", id);
    }

    /**
     * 获取域名
     */
    public static int getGGId() {
        return Preference.getIntPreferences(Utils.getContext(), "ggid", 0);
    }

}
