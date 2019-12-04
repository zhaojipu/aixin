package com.haoniu.aixin.parse;

import android.content.Context;

import com.haoniu.aixin.http.ApiClient;
import com.haoniu.aixin.http.AppConfig;
import com.haoniu.aixin.http.ResultListener;
import com.haoniu.aixin.base.Constant;
import com.haoniu.aixin.base.MyApplication;
import com.haoniu.aixin.domain.EaseUser;
import com.haoniu.aixin.entity.EaseUserInfo;
import com.haoniu.aixin.entity.UserInfo;
import com.hyphenate.EMValueCallBack;
import com.zds.base.json.FastJsonUtil;
import com.zds.base.util.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParseManager {

    private static final String TAG = ParseManager.class.getSimpleName();

    private static final String ParseAppID = "UUL8TxlHwKj7ZXEUr2brF3ydOxirCXdIj9LscvJs";
    private static final String ParseClientKey = "B1jH9bmxuYyTcpoFfpeVslhmLYsytWTxqYqKQhBJ";

//	private static final String ParseAppID = "task";
//	private static final String ParseClientKey = "123456789";

    private static final String CONFIG_TABLE_NAME = "hxuser";
    private static final String CONFIG_USERNAME = "username";
    private static final String CONFIG_NICK = "nickname";
    private static final String CONFIG_AVATAR = "avatar";

    private static final String parseServer = "http://parse.easemob.com/parse/";

    private static ParseManager instance = new ParseManager();


    private ParseManager() {
    }

    public static ParseManager getInstance() {
        return instance;
    }

    public void onInit(Context context) {
        Context appContext = context.getApplicationContext();
    }

    public boolean updateParseNickName(final String nickname) {
//        String username = EMClient.getInstance().getCurrentUser();
//        ParseQuery<ParseObject> pQuery = ParseQuery.getQuery(CONFIG_TABLE_NAME);
//        pQuery.whereEqualTo(CONFIG_USERNAME, username);
//        ParseObject pUser = null;
//        try {
//            pUser = pQuery.getFirst();
//            if (pUser == null) {
//                return false;
//            }
//            pUser.put(CONFIG_NICK, nickname);
//            pUser.save();
//            return true;
//        } catch (ParseException e) {
//            if (e.getCode() == ParseException.OBJECT_NOT_FOUND) {
//                pUser = new ParseObject(CONFIG_TABLE_NAME);
//                pUser.put(CONFIG_USERNAME, username);
//                pUser.put(CONFIG_NICK, nickname);
//                try {
//                    pUser.save();
//                    return true;
//                } catch (ParseException e1) {
//                    e1.printStackTrace();
//                    EMLog.e(TAG, "parse error " + e1.getMessage());
//                }
//
//            }
//            e.printStackTrace();
//            EMLog.e(TAG, "parse error " + e.getMessage());
//        } catch (Exception e) {
//            EMLog.e(TAG, "updateParseNickName error");
//            e.printStackTrace();
//        }
        return false;
    }

    public void getContactInfos(List<String> usernames, final EMValueCallBack<List<EaseUser>> callback) {
        String userList = "";
        for (String username : usernames) {
            if (username != null) {
                username = username.replace(Constant.ID_REDPROJECT, "");
            }
            if (username.equals(Constant.ADMIN)) {
                username = "0";
            }
            userList += username + ",";
        }
        if (userList.length() > 0) {
            userList = userList.substring(0, userList.length() - 1);
        }
        Map<String, Object> map = new HashMap<>();
        map.put("ids", userList);
        ApiClient.requestNetHandleByGet(Utils.getContext(), AppConfig.getUserMessageList, "", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                // XLog.json(json);
                List<EaseUserInfo> easeUserInfos = FastJsonUtil.getList(json, EaseUserInfo.class);
                List<EaseUser> list = new ArrayList<>();
                if (easeUserInfos != null && easeUserInfos.size() > 0) {
                    for (EaseUserInfo easeUserInfo : easeUserInfos) {
                        EaseUser easeUser = new EaseUser(easeUserInfo.getId().equals("0") ? Constant.ADMIN : (easeUserInfo.getId() + ""));
                        easeUser.setAvatar(easeUserInfo.getAvatarUrl());
                        easeUser.setNickname(easeUserInfo.getNickname());
                        list.add(easeUser);
                    }

                }
                callback.onSuccess(list);
            }

            @Override
            public void onFailure(String msg) {
                callback.onError(0, msg);
            }
        });

    }


    public void asyncGetCurrentUserInfo(final EMValueCallBack<EaseUser> callback) {
        if (MyApplication.getInstance().checkUser()) {
            UserInfo userInfo = MyApplication.getInstance().getUserInfo();
            EaseUser user = new EaseUser(userInfo.getIdh() + "");
            user.setNickname(userInfo.getNickName());
            user.setAvatar(userInfo.getUserImg());
            callback.onSuccess(user);
        } else {
            callback.onError(0, "未登录");
        }
    }

    public void asyncGetUserInfo(Context context, final String username, final EMValueCallBack<EaseUser> callback) {
        Map<String, Object> map = new HashMap<>();
        String usernames = "";
        if (username != null) {
            usernames = username.replace(Constant.ID_REDPROJECT, "");
        }
        map.put("ids", username.equals(Constant.ADMIN) ? "0" : usernames);
        ApiClient.requestNetHandleByGet(context, AppConfig.getUserMessageList, "", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                // XLog.json(json);
                List<EaseUserInfo> easeUserInfos = FastJsonUtil.getList(json, EaseUserInfo.class);
                EaseUser easeUser = new EaseUser(username);
                if (easeUserInfos != null && easeUserInfos.size() > 0) {
                    easeUser.setAvatar(easeUserInfos.get(0).getAvatarUrl());
                    easeUser.setNickname(easeUserInfos.get(0).getNickname());
                }
                callback.onSuccess(easeUser);
            }

            @Override
            public void onFailure(String msg) {
                callback.onError(0, msg);
            }
        });

    }

}
