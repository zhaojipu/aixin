package com.haoniu.aixin.base;

import android.content.Context;

import com.haoniu.aixin.db.UserDao;
import com.haoniu.aixin.domain.EaseGroupInfo;
import com.haoniu.aixin.domain.EaseUser;
import com.haoniu.aixin.domain.RobotUser;
import com.haoniu.aixin.entity.ApplyStateInfo;
import com.haoniu.aixin.entity.MsgStateInfo;
import com.haoniu.aixin.model.EaseAtMessageHelper;
import com.haoniu.aixin.utils.PreferenceManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyModel {
    UserDao dao = null;
    protected Context context = null;
    protected Map<Key, Object> valueCache = new HashMap<Key, Object>();

    public MyModel(Context ctx) {
        context = ctx;
        PreferenceManager.init(context);
    }

    public boolean saveContactList(List<EaseUser> contactList) {
        UserDao dao = new UserDao(context);
        dao.saveContactList(contactList);
        return true;
    }

    public Map<String, EaseUser> getContactList() {
        UserDao dao = new UserDao(context);
        return dao.getContactList();
    }

    public void saveContact(EaseUser user) {
        UserDao dao = new UserDao(context);
        dao.saveContact(user);
    }


    public void saveuser(EaseUser user) {
        UserDao dao = new UserDao(context);
        dao.saveuser(user);
    }

    public boolean saveUserList(List<EaseUser> userList) {
        UserDao dao = new UserDao(context);
        dao.saveUserList(userList);
        return true;
    }

    public EaseUser getUser(String id) {
        UserDao dao = new UserDao(context);
        return dao.getUser(id);
    }

    public void delectUser(String id) {
        UserDao dao = new UserDao(context);
        dao.deleteuser(id);
    }

    public void saveGroup(EaseGroupInfo user) {
        UserDao dao = new UserDao(context);
        dao.saveGroup(user);
    }

    public Map<String,EaseGroupInfo> getGroupList(){
        UserDao dao = new UserDao(context);
        return dao.getGroupList();
    }

    public EaseGroupInfo getGroup(String id) {
        UserDao dao = new UserDao(context);
        return dao.getGroup(id);
    }

    public void delectGroup(String id) {
        UserDao dao = new UserDao(context);
        dao.deleteGroup(id);
    }


    /**
     * 获取消息已读状态
     *
     * @param id
     * @return
     */
    public MsgStateInfo getMsgStateInfoById(String id) {
        UserDao dao = new UserDao(context);
        return dao.getMsgStateById(id);
    }

    /**
     * 保存消息已读状态
     *
     * @param msgStateInfo
     */

    public void saveMsgStateInfo(MsgStateInfo msgStateInfo) {
        UserDao dao = new UserDao(context);
        dao.saveMsgStateInfo(msgStateInfo);
    }

    /**
     * 获取审核消息已读状态
     *
     * @param id
     * @return
     */
    public ApplyStateInfo getApplyStateInfoById(String id) {
        UserDao dao = new UserDao(context);
        return dao.getApplyStateById(id);
    }

    /**
     * 保存审核消息已读状态
     *
     * @param applyStateInfo
     */
    public void saveApplyStateInfo(ApplyStateInfo applyStateInfo) {
        UserDao dao = new UserDao(context);
        dao.saveApplyStateInfo(applyStateInfo);
    }

    /**
     * save current username
     *
     * @param username
     */
    public void setCurrentUserName(String username) {
        PreferenceManager.getInstance().setCurrentUserName(username);
    }

    public String getCurrentUsernName() {
        return PreferenceManager.getInstance().getCurrentUsername();
    }

    public Map<String, RobotUser> getRobotList() {
        UserDao dao = new UserDao(context);
        return dao.getRobotUser();
    }

    public boolean saveRobotList(List<RobotUser> robotList) {
        UserDao dao = new UserDao(context);
        dao.saveRobotUser(robotList);
        return true;
    }

    public void setSettingMsgNotification(boolean paramBoolean) {
        PreferenceManager.getInstance().setSettingMsgNotification(paramBoolean);
        valueCache.put(Key.VibrateAndPlayToneOn, paramBoolean);
    }

    public boolean getSettingMsgNotification() {
        Object val = valueCache.get(Key.VibrateAndPlayToneOn);

        if (val == null) {
            val = PreferenceManager.getInstance().getSettingMsgNotification();
            valueCache.put(Key.VibrateAndPlayToneOn, val);
        }

        return (Boolean) (val != null ? val : true);
    }

    public void setSettingMsgSound(boolean paramBoolean) {
        PreferenceManager.getInstance().setSettingMsgSound(paramBoolean);
        valueCache.put(Key.PlayToneOn, paramBoolean);
    }

    public boolean getSettingMsgSound() {
        Object val = valueCache.get(Key.PlayToneOn);

        if (val == null) {
            val = PreferenceManager.getInstance().getSettingMsgSound();
            valueCache.put(Key.PlayToneOn, val);
        }

        return (Boolean) (val != null ? val : true);
    }

    public void setSettingMsgVibrate(boolean paramBoolean) {
        PreferenceManager.getInstance().setSettingMsgVibrate(paramBoolean);
        valueCache.put(Key.VibrateOn, paramBoolean);
    }

    public boolean getSettingMsgVibrate() {
        Object val = valueCache.get(Key.VibrateOn);

        if (val == null) {
            val = PreferenceManager.getInstance().getSettingMsgVibrate();
            valueCache.put(Key.VibrateOn, val);
        }

        return (Boolean) (val != null ? val : true);
    }

    public void setSettingMsgSpeaker(boolean paramBoolean) {
        PreferenceManager.getInstance().setSettingMsgSpeaker(paramBoolean);
        valueCache.put(Key.SpakerOn, paramBoolean);
    }

    public boolean getSettingMsgSpeaker() {
        Object val = valueCache.get(Key.SpakerOn);

        if (val == null) {
            val = PreferenceManager.getInstance().getSettingMsgSpeaker();
            valueCache.put(Key.SpakerOn, val);
        }

        return (Boolean) (val != null ? val : true);
    }


    public void setDisabledGroups(List<String> groups) {
        if (dao == null) {
            dao = new UserDao(context);
        }

        List<String> list = new ArrayList<String>();
        list.addAll(groups);
        for (int i = 0; i < list.size(); i++) {
            if (EaseAtMessageHelper.get().getAtMeGroups().contains(list.get(i))) {
                list.remove(i);
                i--;
            }
        }

        dao.setDisabledGroups(list);
        valueCache.put(Key.DisabledGroups, list);
    }

    public List<String> getDisabledGroups() {
        Object val = valueCache.get(Key.DisabledGroups);

        if (dao == null) {
            dao = new UserDao(context);
        }

        if (val == null) {
            val = dao.getDisabledGroups();
            valueCache.put(Key.DisabledGroups, val);
        }

        //noinspection unchecked
        return (List<String>) val;
    }

    public void setDisabledIds(List<String> ids) {
        if (dao == null) {
            dao = new UserDao(context);
        }

        dao.setDisabledIds(ids);
        valueCache.put(Key.DisabledIds, ids);
    }

    public List<String> getDisabledIds() {
        Object val = valueCache.get(Key.DisabledIds);

        if (dao == null) {
            dao = new UserDao(context);
        }

        if (val == null) {
            val = dao.getDisabledIds();
            valueCache.put(Key.DisabledIds, val);
        }

        //noinspection unchecked
        return (List<String>) val;
    }

    public void setGroupsSynced(boolean synced) {
        PreferenceManager.getInstance().setGroupsSynced(synced);
    }

    public boolean isGroupsSynced() {
        return PreferenceManager.getInstance().isGroupsSynced();
    }

    public void setContactSynced(boolean synced) {
        PreferenceManager.getInstance().setContactSynced(synced);
    }

    public boolean isContactSynced() {
        return PreferenceManager.getInstance().isContactSynced();
    }

    public void setBlacklistSynced(boolean synced) {
        PreferenceManager.getInstance().setBlacklistSynced(synced);
    }

    public boolean isBacklistSynced() {
        return PreferenceManager.getInstance().isBacklistSynced();
    }

    public void allowChatroomOwnerLeave(boolean value) {
        PreferenceManager.getInstance().setSettingAllowChatroomOwnerLeave(value);
    }

    public boolean isChatroomOwnerLeaveAllowed() {
        return PreferenceManager.getInstance().getSettingAllowChatroomOwnerLeave();
    }

    public void setDeleteMessagesAsExitGroup(boolean value) {
        PreferenceManager.getInstance().setDeleteMessagesAsExitGroup(value);
    }

    public boolean isDeleteMessagesAsExitGroup() {
        return PreferenceManager.getInstance().isDeleteMessagesAsExitGroup();
    }

    public void setTransfeFileByUser(boolean value) {
        PreferenceManager.getInstance().setTransferFileByUser(value);
    }

    public boolean isSetTransferFileByUser() {
        return PreferenceManager.getInstance().isSetTransferFileByUser();
    }

    public void setAutodownloadThumbnail(boolean autodownload) {
        PreferenceManager.getInstance().setAudodownloadThumbnail(autodownload);
    }

    public boolean isSetAutodownloadThumbnail() {
        return PreferenceManager.getInstance().isSetAutodownloadThumbnail();
    }

    public void setAutoAcceptGroupInvitation(boolean value) {
        PreferenceManager.getInstance().setAutoAcceptGroupInvitation(value);
    }

    public boolean isAutoAcceptGroupInvitation() {
        return PreferenceManager.getInstance().isAutoAcceptGroupInvitation();
    }


    public void setAdaptiveVideoEncode(boolean value) {
        PreferenceManager.getInstance().setAdaptiveVideoEncode(value);
    }

    public boolean isAdaptiveVideoEncode() {
        return PreferenceManager.getInstance().isAdaptiveVideoEncode();
    }

    public void setPushCall(boolean value) {
        PreferenceManager.getInstance().setPushCall(value);
    }

    public boolean isPushCall() {
        return PreferenceManager.getInstance().isPushCall();
    }

    public void setRestServer(String restServer) {
        PreferenceManager.getInstance().setRestServer(restServer);
    }

    public String getRestServer() {
        return PreferenceManager.getInstance().getRestServer();
    }

    public void setIMServer(String imServer) {
        PreferenceManager.getInstance().setIMServer(imServer);
    }

    public String getIMServer() {
        return PreferenceManager.getInstance().getIMServer();
    }

    public void enableCustomServer(boolean enable) {
        PreferenceManager.getInstance().enableCustomServer(enable);
    }

    public boolean isCustomServerEnable() {
        return PreferenceManager.getInstance().isCustomServerEnable();
    }

    public void enableCustomAppkey(boolean enable) {
        PreferenceManager.getInstance().enableCustomAppkey(enable);
    }

    public boolean isCustomAppkeyEnabled() {
        return PreferenceManager.getInstance().isCustomAppkeyEnabled();
    }

    public void setCustomAppkey(String appkey) {
        PreferenceManager.getInstance().setCustomAppkey(appkey);
    }

    public boolean isMsgRoaming() {
        return PreferenceManager.getInstance().isMsgRoaming();
    }

    public void setMsgRoaming(boolean roaming) {
        PreferenceManager.getInstance().setMsgRoaming(roaming);
    }

    public String getCutomAppkey() {
        return PreferenceManager.getInstance().getCustomAppkey();
    }

    enum Key {
        VibrateAndPlayToneOn,
        VibrateOn,
        PlayToneOn,
        SpakerOn,
        DisabledGroups,
        DisabledIds
    }
}
