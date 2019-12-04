package com.haoniu.aixin.parse;

import android.content.Context;

import com.haoniu.aixin.http.ApiClient;
import com.haoniu.aixin.http.AppConfig;
import com.haoniu.aixin.http.ResultListener;
import com.haoniu.aixin.base.Constant;
import com.haoniu.aixin.base.MyHelper;
import com.haoniu.aixin.base.MyHelper.DataSyncListener;
import com.haoniu.aixin.domain.EaseUser;
import com.haoniu.aixin.entity.EaseUserInfo;
import com.haoniu.aixin.utils.PreferenceManager;
import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMClient;
import com.zds.base.json.FastJsonUtil;
import com.zds.base.util.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserProfileManager {

    /**
     * application context
     */
    protected Context appContext = null;

    /**
     * init flag: test if the sdk has been inited before, we don't need to init
     * again
     */
    private boolean sdkInited = false;

    /**
     * HuanXin sync contact nick and avatar listener
     */
    private List<DataSyncListener> syncContactInfosListeners;

    private boolean isSyncingContactInfosWithServer = false;

    private EaseUser currentUser;

    public UserProfileManager() {
    }

    public synchronized boolean init(Context context) {
        if (sdkInited) {
            return true;
        }
        ParseManager.getInstance().onInit(context);
        syncContactInfosListeners = new ArrayList<DataSyncListener>();
        sdkInited = true;
        return true;
    }

    public void addSyncContactInfoListener(DataSyncListener listener) {
        if (listener == null) {
            return;
        }
        if (!syncContactInfosListeners.contains(listener)) {
            syncContactInfosListeners.add(listener);
        }
    }

    public void removeSyncContactInfoListener(DataSyncListener listener) {
        if (listener == null) {
            return;
        }
        if (syncContactInfosListeners.contains(listener)) {
            syncContactInfosListeners.remove(listener);
        }
    }

    public void asyncFetchContactInfosFromServer(List<String> usernames, final EMValueCallBack<List<EaseUser>> callback) {
        if (isSyncingContactInfosWithServer) {
            return;
        }
        isSyncingContactInfosWithServer = true;
        ParseManager.getInstance().getContactInfos(usernames, new EMValueCallBack<List<EaseUser>>() {

            @Override
            public void onSuccess(List<EaseUser> value) {
                isSyncingContactInfosWithServer = false;
                // in case that logout already before server returns,we should
                // return immediately
                if (!MyHelper.getInstance().isLoggedIn()) {
                    return;
                }
                if (callback != null) {
                    callback.onSuccess(value);
                }
            }

            @Override
            public void onError(int error, String errorMsg) {
                isSyncingContactInfosWithServer = false;
                if (callback != null) {
                    callback.onError(error, errorMsg);
                }
            }

        });

    }

    public void loadFriend(List<String> usernames) {
        String userList = "";
        for (String username : usernames) {
            if (username != null) {
                username = username.replace(Constant.ID_REDPROJECT, "");
            }
            if (username.equals(Constant.ADMIN)) {
                username = "0";
            }
            userList += username+ ",";
        }
        if (userList.length() > 0) {
            userList = userList.substring(0, userList.length() - 1);
        }
        Map<String, Object> map = new HashMap<>();
        map.put("ids", userList);
        ApiClient.requestNetHandleByGet(Utils.getContext(), AppConfig.getUserMessageList, "", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                List<EaseUserInfo> easeUserInfos = FastJsonUtil.getList(json, EaseUserInfo.class);
                if (easeUserInfos != null && easeUserInfos.size() > 0) {
                    for (EaseUserInfo easeUserInfo : easeUserInfos) {
                        EaseUser easeUser = new EaseUser(easeUserInfo.getId().equals("0") ? Constant.ADMIN : (easeUserInfo.getId() + ""));
                        easeUser.setAvatar(easeUserInfo.getAvatarUrl());
                        easeUser.setNickname(easeUserInfo.getNickname());
                        MyHelper.getInstance().saveUser(easeUser);
                    }

                }
            }

            /**
             * 请求失败
             *
             * @param msg
             */
            @Override
            public void onFailure(String msg) {

            }

        });

    }

    public void notifyContactInfosSyncListener(boolean success) {
        for (DataSyncListener listener : syncContactInfosListeners) {
            listener.onSyncComplete(success);
        }
    }

    public boolean isSyncingContactInfoWithServer() {
        return isSyncingContactInfosWithServer;
    }

    public synchronized void reset() {
        isSyncingContactInfosWithServer = false;
        currentUser = null;
        PreferenceManager.getInstance().removeCurrentUserInfo();
    }

    public synchronized EaseUser getCurrentUserInfo() {
        if (currentUser == null) {
            String username = EMClient.getInstance().getCurrentUser();
            currentUser = new EaseUser(username);
            String nick = getCurrentUserNick();
            currentUser.setNick((nick != null) ? nick : username);
            currentUser.setAvatar(getCurrentUserAvatar());
        }
        return currentUser;
    }

    public boolean updateCurrentUserNickName(final String nickname) {
        boolean isSuccess = ParseManager.getInstance().updateParseNickName(nickname);
        if (isSuccess) {
            setCurrentUserNick(nickname);
        }
        return isSuccess;
    }


    public void asyncGetCurrentUserInfo() {
        ParseManager.getInstance().asyncGetCurrentUserInfo(new EMValueCallBack<EaseUser>() {

            @Override
            public void onSuccess(EaseUser value) {
                if (value != null) {
                    setCurrentUserNick(value.getNick());
                    setCurrentUserAvatar(value.getAvatar());
                }
            }

            @Override
            public void onError(int error, String errorMsg) {

            }
        });

    }

    public void asyncGetUserInfo(Context context, final String username, final EMValueCallBack<EaseUser> callback) {
        ParseManager.getInstance().asyncGetUserInfo(context, username, callback);
    }

    private void setCurrentUserNick(String nickname) {
        getCurrentUserInfo().setNick(nickname);
        PreferenceManager.getInstance().setCurrentUserNick(nickname);
    }

    private void setCurrentUserAvatar(String avatar) {
        getCurrentUserInfo().setAvatar(avatar);
        PreferenceManager.getInstance().setCurrentUserAvatar(avatar);
    }

    private String getCurrentUserNick() {
        return PreferenceManager.getInstance().getCurrentUserNick();
    }

    private String getCurrentUserAvatar() {
        return PreferenceManager.getInstance().getCurrentUserAvatar();
    }

}
