package com.haoniu.aixin.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.haoniu.aixin.base.Constant;
import com.haoniu.aixin.base.MyApplication;
import com.haoniu.aixin.domain.EaseGroupInfo;
import com.haoniu.aixin.domain.EaseUser;
import com.haoniu.aixin.domain.InviteMessage;
import com.haoniu.aixin.domain.InviteMessage.InviteMessageStatus;
import com.haoniu.aixin.domain.RobotUser;
import com.haoniu.aixin.entity.ApplyStateInfo;
import com.haoniu.aixin.entity.MsgStateInfo;
import com.haoniu.aixin.utils.EaseCommonUtils;
import com.hyphenate.util.HanziToPinyin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class DemoDBManager {
    static private DemoDBManager dbMgr = new DemoDBManager();
    private DbOpenHelper dbHelper;

    private DemoDBManager() {
        dbHelper = DbOpenHelper.getInstance(MyApplication.getInstance().getApplicationContext());
    }

    public static synchronized DemoDBManager getInstance() {
        if (dbMgr == null) {
            dbMgr = new DemoDBManager();
        }
        return dbMgr;
    }

    /**
     * save contact list
     *
     * @param contactList
     */
    synchronized public void saveContactList(List<EaseUser> contactList) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db.isOpen()) {
            db.delete(UserDao.TABLE_NAME, null, null);
            for (EaseUser user : contactList) {
                ContentValues values = new ContentValues();
                values.put(UserDao.COLUMN_NAME_ID, user.getUsername());
                if (user.getNick() != null) {
                    values.put(UserDao.COLUMN_NAME_NICK, user.getNick());
                }
                if (user.getAvatar() != null) {
                    values.put(UserDao.COLUMN_NAME_AVATAR, user.getAvatar());
                }
                db.replace(UserDao.TABLE_NAME, null, values);
            }
        }
    }

    /**
     * get contact list
     *
     * @return
     */
    synchronized public Map<String, EaseUser> getContactList() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Map<String, EaseUser> users = new Hashtable<String, EaseUser>();
        if (db.isOpen()) {
            Cursor cursor = db.rawQuery("select * from " + UserDao.TABLE_NAME, null);
            while (cursor.moveToNext()) {
                String username = cursor.getString(cursor.getColumnIndex(UserDao.COLUMN_NAME_ID));
                String nick = cursor.getString(cursor.getColumnIndex(UserDao.COLUMN_NAME_NICK));
                String avatar = cursor.getString(cursor.getColumnIndex(UserDao.COLUMN_NAME_AVATAR));
                EaseUser user = new EaseUser(username);
                user.setNick(nick);
                user.setAvatar(avatar);
                if (username.equals(Constant.NEW_FRIENDS_USERNAME) || username.equals(Constant.GROUP_USERNAME)
                        || username.equals(Constant.CHAT_ROOM) || username.equals(Constant.CHAT_ROBOT)) {
                    user.setInitialLetter("");
                } else {
                    EaseCommonUtils.setUserInitialLetter(user);
                }
                users.put(username, user);
            }
            cursor.close();
        }
        return users;
    }

    /**
     * get  user
     *
     * @return
     */
    synchronized public EaseUser getUserById(String id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        EaseUser users = new EaseUser(id);
        if (db.isOpen()) {
            Cursor cursor = db.rawQuery(String.format("select * from %s where %s = ?", UserDao.ALL_USERS_TABLE_NAME, UserDao.COLUMN_NAME_ID), new String[]{id});
            while (cursor.moveToNext()) {
                String username = cursor.getString(cursor.getColumnIndex(UserDao.COLUMN_NAME_ID));
                String nick = cursor.getString(cursor.getColumnIndex(UserDao.COLUMN_NAME_NICK));
                String avatar = cursor.getString(cursor.getColumnIndex(UserDao.COLUMN_NAME_AVATAR));
                String type = cursor.getString(cursor.getColumnIndex(UserDao.COLUMN_NAME_TYPE));
                EaseUser user = new EaseUser(username);
                user.setNick(nick);
                user.setType(type);
                user.setAvatar(avatar);
                if (username.equals(Constant.NEW_FRIENDS_USERNAME) || username.equals(Constant.GROUP_USERNAME)
                        || username.equals(Constant.CHAT_ROOM) || username.equals(Constant.CHAT_ROBOT)) {
                    user.setInitialLetter("");
                } else {
                    EaseCommonUtils.setUserInitialLetter(user);
                }
                users = user;
            }
            cursor.close();
        }
        return users;
    }

    /**
     * 获取 MsgStateInfo
     *
     * @return
     */
    synchronized public MsgStateInfo getMsgStateById(String id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        MsgStateInfo msgStateInfo = new MsgStateInfo(id);
        if (db.isOpen()) {
            Cursor cursor = db.rawQuery(String.format("select * from %s where %s = ?", UserDao.TABLE_NAME_MSG, UserDao.MSG_ID), new String[]{id});
            while (cursor.moveToNext()) {
                String ids = cursor.getString(cursor.getColumnIndex(UserDao.MSG_ID));
                String state = cursor.getString(cursor.getColumnIndex(UserDao.MSG_ISREAD));
                String type = cursor.getString(cursor.getColumnIndex(UserDao.MSG_TYPE));
                MsgStateInfo msgInfo = new MsgStateInfo(ids);
                msgInfo.setMsg_is_read(state);
                msgInfo.setMsg_type(type);
                msgStateInfo = msgInfo;
            }
            cursor.close();
        }
        return msgStateInfo;
    }

    /**
     * 保存 MsgStateInfo
     *
     * @param msgStateInfo
     */
    synchronized public void saveMsgState(MsgStateInfo msgStateInfo) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(UserDao.MSG_ID, msgStateInfo.getMsg_id());
        if (msgStateInfo.getMsg_type() != null) {
            values.put(UserDao.MSG_TYPE, msgStateInfo.getMsg_type());
        }
        if (msgStateInfo.getMsg_is_read() != null) {
            values.put(UserDao.MSG_ISREAD, msgStateInfo.getMsg_is_read());
        }
        if (db.isOpen()) {
            db.replace(UserDao.TABLE_NAME_MSG, null, values);
        }
    }

    /**
     * 获取 ApplyStateInfo
     *
     * @return
     */
    synchronized public ApplyStateInfo getApplyStateById(String id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        ApplyStateInfo applyStateInfo = new ApplyStateInfo(id);
        if (db.isOpen()) {
            Cursor cursor = db.rawQuery(String.format("select * from %s where %s = ?", UserDao.TABLE_NAME_APPLY, UserDao.APPLY_ID), new String[]{id});
            while (cursor.moveToNext()) {
                String ids = cursor.getString(cursor.getColumnIndex(UserDao.APPLY_ID));
                String state = cursor.getString(cursor.getColumnIndex(UserDao.APPLY_ISREAD));
                String type = cursor.getString(cursor.getColumnIndex(UserDao.APPLY_TYPE));
                ApplyStateInfo applyInfo = new ApplyStateInfo(ids);
                applyInfo.setApply_is_read(state);
                applyInfo.setApply_type(type);
                applyStateInfo = applyInfo;
            }
            cursor.close();
        }
        return applyStateInfo;
    }

    /**
     * 保存 ApplyStateInfo
     *
     * @param applyStateInfo
     */
    synchronized public void saveApplyState(ApplyStateInfo applyStateInfo) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(UserDao.APPLY_ID, applyStateInfo.getApply_id());
        if (applyStateInfo.getApply_type() != null) {
            values.put(UserDao.APPLY_TYPE, applyStateInfo.getApply_type());
        }
        if (applyStateInfo.getApply_is_read() != null) {
            values.put(UserDao.APPLY_ISREAD, applyStateInfo.getApply_is_read());
        }
        if (db.isOpen()) {
            db.replace(UserDao.TABLE_NAME_APPLY, null, values);
        }
    }

    /**
     * get a Group
     *
     * @return
     */
    synchronized public EaseGroupInfo getGroupById(String id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        EaseGroupInfo groupInfos = new EaseGroupInfo(id);
        if (db.isOpen()) {
            Cursor cursor = db.rawQuery(String.format("select * from %s where %s = ?", UserDao.GROUPS_TABLE_NAME, UserDao.COLUMN_NAME_ID), new String[]{id});
            while (cursor.moveToNext()) {
                String username = cursor.getString(cursor.getColumnIndex(UserDao.COLUMN_NAME_ID));
                String nick = cursor.getString(cursor.getColumnIndex(UserDao.COLUMN_NAME_NICK));
                String avatar = cursor.getString(cursor.getColumnIndex(UserDao.COLUMN_NAME_AVATAR));
                String type = cursor.getString(cursor.getColumnIndex(UserDao.COLUMN_NAME_TYPE));
                int groupType = cursor.getInt(cursor.getColumnIndex(UserDao.COLUMN_NAME_GROUP_TYPE));
                EaseGroupInfo groupInfo = new EaseGroupInfo(username);
                groupInfo.setGroupName(nick);
                groupInfo.setHead(avatar);
                groupInfo.setType(type);
                groupInfo.setGroupType(groupType);
                groupInfos = groupInfo;
            }
            cursor.close();
        }
        return groupInfos;
    }

    /**
     * get a Group
     *
     * @return
     */
    synchronized public Map<String, EaseGroupInfo> getGroupList() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Map<String, EaseGroupInfo> groupInfoMap = new HashMap<>();
        if (db.isOpen()) {
            Cursor cursor = db.rawQuery("select * from " + UserDao.GROUPS_TABLE_NAME, null);
            while (cursor.moveToNext()) {
                String username = cursor.getString(cursor.getColumnIndex(UserDao.COLUMN_NAME_ID));
                String nick = cursor.getString(cursor.getColumnIndex(UserDao.COLUMN_NAME_NICK));
                String avatar = cursor.getString(cursor.getColumnIndex(UserDao.COLUMN_NAME_AVATAR));
                String type = cursor.getString(cursor.getColumnIndex(UserDao.COLUMN_NAME_TYPE));
                int groupType = cursor.getInt(cursor.getColumnIndex(UserDao.COLUMN_NAME_GROUP_TYPE));
                EaseGroupInfo groupInfo = new EaseGroupInfo(username);
                groupInfo.setGroupName(nick);
                groupInfo.setHead(avatar);
                groupInfo.setType(type);
                groupInfo.setGroupType(groupType);
                groupInfoMap.put(groupInfo.getUsername(), groupInfo);
            }
            cursor.close();
        }
        return groupInfoMap;
    }

    /**
     * delete a contact
     *
     * @param username
     */
    synchronized public void deleteContact(String username) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db.isOpen()) {
            db.delete(UserDao.TABLE_NAME, UserDao.COLUMN_NAME_ID + " = ?", new String[]{username});
        }
    }

    /**
     * delete a user
     *
     * @param username
     */
    synchronized public void deleteuser(String username) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db.isOpen()) {
            db.delete(UserDao.ALL_USERS_TABLE_NAME, UserDao.COLUMN_NAME_ID + " = ?", new String[]{username});
        }
    }

    /**
     * delete a group
     *
     * @param username
     */
    synchronized public void deleteGroup(String username) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db.isOpen()) {
            db.delete(UserDao.GROUPS_TABLE_NAME, UserDao.COLUMN_NAME_ID + " = ?", new String[]{username});
        }
    }

    /**
     * save user list
     *
     * @param contactList
     */
    synchronized public void saveUserList(List<EaseUser> contactList) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db.isOpen()) {
            db.delete(UserDao.TABLE_NAME, null, null);
            for (EaseUser user : contactList) {
                ContentValues values = new ContentValues();
                values.put(UserDao.COLUMN_NAME_ID, user.getUsername());
                if (user.getNick() != null) {
                    values.put(UserDao.COLUMN_NAME_NICK, user.getNick());
                }
                if (user.getAvatar() != null) {
                    values.put(UserDao.COLUMN_NAME_AVATAR, user.getAvatar());
                }
                if (user.getType() != null) {
                    values.put(UserDao.COLUMN_NAME_TYPE, user.getType());
                }
                db.replace(UserDao.ALL_USERS_TABLE_NAME, null, values);
            }
        }
    }

    /**
     * save a contact
     *
     * @param user
     */
    synchronized public void saveContact(EaseUser user) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(UserDao.COLUMN_NAME_ID, user.getUsername());
        if (user.getNick() != null) {
            values.put(UserDao.COLUMN_NAME_NICK, user.getNick());
        }
        if (user.getAvatar() != null) {
            values.put(UserDao.COLUMN_NAME_AVATAR, user.getAvatar());
        }
        if (db.isOpen()) {
            db.replace(UserDao.TABLE_NAME, null, values);
        }
    }

    /**
     * save a user
     *
     * @param user
     */
    synchronized public void saveuser(EaseUser user) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(UserDao.COLUMN_NAME_ID, user.getUsername());
        if (user.getNick() != null) {
            values.put(UserDao.COLUMN_NAME_NICK, user.getNick());
        }
        if (user.getAvatar() != null) {
            values.put(UserDao.COLUMN_NAME_AVATAR, user.getAvatar());
        }
        if (user.getType() != null) {
            values.put(UserDao.COLUMN_NAME_TYPE, user.getType());
        }
        if (db.isOpen()) {
            db.replace(UserDao.ALL_USERS_TABLE_NAME, null, values);
        }
    }

    /**
     * save a group
     *
     * @param groupInfo
     */
    synchronized public void saveGroup(EaseGroupInfo groupInfo) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(UserDao.COLUMN_NAME_ID, groupInfo.getUsername());
        if (groupInfo.getGroupName() != null) {
            values.put(UserDao.COLUMN_NAME_NICK, groupInfo.getGroupName());
        }
        if (groupInfo.getType() != null) {
            values.put(UserDao.COLUMN_NAME_TYPE, groupInfo.getType());
        }
        if (groupInfo.getHead() != null) {
            values.put(UserDao.COLUMN_NAME_AVATAR, groupInfo.getHead());
        }
        values.put(UserDao.COLUMN_NAME_GROUP_TYPE, groupInfo.getGroupType());

        if (db.isOpen()) {
            db.replace(UserDao.GROUPS_TABLE_NAME, null, values);
        }
    }


    public void setDisabledGroups(List<String> groups) {
        setList(UserDao.COLUMN_NAME_DISABLED_GROUPS, groups);
    }

    public List<String> getDisabledGroups() {
        return getList(UserDao.COLUMN_NAME_DISABLED_GROUPS);
    }

    public void setDisabledIds(List<String> ids) {
        setList(UserDao.COLUMN_NAME_DISABLED_IDS, ids);
    }

    public List<String> getDisabledIds() {
        return getList(UserDao.COLUMN_NAME_DISABLED_IDS);
    }

    synchronized private void setList(String column, List<String> strList) {
        StringBuilder strBuilder = new StringBuilder();

        for (String hxid : strList) {
            strBuilder.append(hxid).append("$");
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db.isOpen()) {
            ContentValues values = new ContentValues();
            values.put(column, strBuilder.toString());

            db.update(UserDao.PREF_TABLE_NAME, values, null, null);
        }
    }

    synchronized private List<String> getList(String column) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select " + column + " from " + UserDao.PREF_TABLE_NAME, null);
        if (!cursor.moveToFirst()) {
            cursor.close();
            return null;
        }

        String strVal = cursor.getString(0);
        if (strVal == null || "".equals(strVal)) {
            return null;
        }

        cursor.close();

        String[] array = strVal.split("$");

        if (array.length > 0) {
            List<String> list = new ArrayList<String>();
            Collections.addAll(list, array);
            return list;
        }

        return null;
    }

    /**
     * save a message
     *
     * @param message
     * @return return cursor of the message
     */
    public synchronized Integer saveMessage(InviteMessage message) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int id = -1;
        if (db.isOpen()) {
            ContentValues values = new ContentValues();
            values.put(InviteMessgeDao.COLUMN_NAME_FROM, message.getFrom());
            values.put(InviteMessgeDao.COLUMN_NAME_GROUP_ID, message.getGroupId());
            values.put(InviteMessgeDao.COLUMN_NAME_GROUP_Name, message.getGroupName());
            values.put(InviteMessgeDao.COLUMN_NAME_REASON, message.getReason());
            values.put(InviteMessgeDao.COLUMN_NAME_TIME, message.getTime());
            values.put(InviteMessgeDao.COLUMN_NAME_STATUS, message.getStatus().ordinal());
            values.put(InviteMessgeDao.COLUMN_NAME_GROUPINVITER, message.getGroupInviter());
            db.insert(InviteMessgeDao.TABLE_NAME, null, values);

            Cursor cursor = db.rawQuery("select last_insert_rowid() from " + InviteMessgeDao.TABLE_NAME, null);
            if (cursor.moveToFirst()) {
                id = cursor.getInt(0);
            }

            cursor.close();
        }
        return id;
    }

    /**
     * update message
     *
     * @param msgId
     * @param values
     */
    synchronized public void updateMessage(int msgId, ContentValues values) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db.isOpen()) {
            db.update(InviteMessgeDao.TABLE_NAME, values, InviteMessgeDao.COLUMN_NAME_ID + " = ?", new String[]{String.valueOf(msgId)});
        }
    }

    /**
     * get messges
     *
     * @return
     */
    synchronized public List<InviteMessage> getMessagesList() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<InviteMessage> msgs = new ArrayList<InviteMessage>();
        if (db.isOpen()) {
            Cursor cursor = db.rawQuery("select * from " + InviteMessgeDao.TABLE_NAME + " desc ", null);
            while (cursor.moveToNext()) {
                InviteMessage msg = new InviteMessage();
                int id = cursor.getInt(cursor.getColumnIndex(InviteMessgeDao.COLUMN_NAME_ID));
                String from = cursor.getString(cursor.getColumnIndex(InviteMessgeDao.COLUMN_NAME_FROM));
                String groupid = cursor.getString(cursor.getColumnIndex(InviteMessgeDao.COLUMN_NAME_GROUP_ID));
                String groupname = cursor.getString(cursor.getColumnIndex(InviteMessgeDao.COLUMN_NAME_GROUP_Name));
                String reason = cursor.getString(cursor.getColumnIndex(InviteMessgeDao.COLUMN_NAME_REASON));
                long time = cursor.getLong(cursor.getColumnIndex(InviteMessgeDao.COLUMN_NAME_TIME));
                int status = cursor.getInt(cursor.getColumnIndex(InviteMessgeDao.COLUMN_NAME_STATUS));
                String groupInviter = cursor.getString(cursor.getColumnIndex(InviteMessgeDao.COLUMN_NAME_GROUPINVITER));

                msg.setId(id);
                msg.setFrom(from);
                msg.setGroupId(groupid);
                msg.setGroupName(groupname);
                msg.setReason(reason);
                msg.setTime(time);
                msg.setGroupInviter(groupInviter);
                msg.setStatus(InviteMessageStatus.values()[status]);
                msgs.add(msg);
            }
            cursor.close();
        }
        return msgs;
    }

    /**
     * delete invitation message
     *
     * @param from
     */
    synchronized public void deleteMessage(String from) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db.isOpen()) {
            db.delete(InviteMessgeDao.TABLE_NAME, InviteMessgeDao.COLUMN_NAME_FROM + " = ?", new String[]{from});
        }
    }

    /**
     * delete invitation message
     *
     * @param groupId
     */
    synchronized public void deleteGroupMessage(String groupId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db.isOpen()) {
            db.delete(InviteMessgeDao.TABLE_NAME, InviteMessgeDao.COLUMN_NAME_GROUP_ID + " = ?", new String[]{groupId});
        }
    }

    /**
     * delete invitation message
     *
     * @param groupId
     */
    synchronized public void deleteGroupMessage(String groupId, String from) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db.isOpen()) {
            db.delete(InviteMessgeDao.TABLE_NAME, InviteMessgeDao.COLUMN_NAME_GROUP_ID + " = ? AND " + InviteMessgeDao.COLUMN_NAME_FROM + " = ? ",
                    new String[]{groupId, from});
        }
    }

    synchronized int getUnreadNotifyCount() {
        int count = 0;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        if (db.isOpen()) {
            Cursor cursor = db.rawQuery("select " + InviteMessgeDao.COLUMN_NAME_UNREAD_MSG_COUNT + " from " + InviteMessgeDao.TABLE_NAME, null);
            if (cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
            cursor.close();
        }
        return count;
    }

    synchronized void setUnreadNotifyCount(int count) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db.isOpen()) {
            ContentValues values = new ContentValues();
            values.put(InviteMessgeDao.COLUMN_NAME_UNREAD_MSG_COUNT, count);

            db.update(InviteMessgeDao.TABLE_NAME, values, null, null);
        }
    }

    synchronized public void closeDB() {
        if (dbHelper != null) {
            dbHelper.closeDB();
        }
        dbMgr = null;
    }


    /**
     * Save Robot list
     */
    synchronized public void saveRobotList(List<RobotUser> robotList) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db.isOpen()) {
            db.delete(UserDao.ROBOT_TABLE_NAME, null, null);
            for (RobotUser item : robotList) {
                ContentValues values = new ContentValues();
                values.put(UserDao.ROBOT_COLUMN_NAME_ID, item.getUsername());
                if (item.getNick() != null) {
                    values.put(UserDao.ROBOT_COLUMN_NAME_NICK, item.getNick());
                }
                if (item.getAvatar() != null) {
                    values.put(UserDao.ROBOT_COLUMN_NAME_AVATAR, item.getAvatar());
                }
                db.replace(UserDao.ROBOT_TABLE_NAME, null, values);
            }
        }
    }

    /**
     * load robot list
     */
    synchronized public Map<String, RobotUser> getRobotList() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Map<String, RobotUser> users = null;
        if (db.isOpen()) {
            Cursor cursor = db.rawQuery("select * from " + UserDao.ROBOT_TABLE_NAME, null);
            if (cursor.getCount() > 0) {
                users = new Hashtable<String, RobotUser>();
            }
            while (cursor.moveToNext()) {
                String username = cursor.getString(cursor.getColumnIndex(UserDao.ROBOT_COLUMN_NAME_ID));
                String nick = cursor.getString(cursor.getColumnIndex(UserDao.ROBOT_COLUMN_NAME_NICK));
                String avatar = cursor.getString(cursor.getColumnIndex(UserDao.ROBOT_COLUMN_NAME_AVATAR));
                RobotUser user = new RobotUser(username);
                user.setNick(nick);
                user.setAvatar(avatar);
                String headerName = null;
                if (!TextUtils.isEmpty(user.getNick())) {
                    headerName = user.getNick();
                } else {
                    headerName = user.getUsername();
                }
                if (Character.isDigit(headerName.charAt(0))) {
                    user.setInitialLetter("#");
                } else {
                    user.setInitialLetter(HanziToPinyin.getInstance().get(headerName.substring(0, 1)).get(0).target
                            .substring(0, 1).toUpperCase());
                    char header = user.getInitialLetter().toLowerCase().charAt(0);
                    if (header < 'a' || header > 'z') {
                        user.setInitialLetter("#");
                    }
                }

                try {
                    users.put(username, user);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            cursor.close();
        }
        return users;
    }
}
