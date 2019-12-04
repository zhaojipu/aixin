/**
 * Copyright (C) 2016 Hyphenate Inc. All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.haoniu.aixin.db;

import android.content.Context;

import com.haoniu.aixin.domain.EaseGroupInfo;
import com.haoniu.aixin.domain.EaseUser;
import com.haoniu.aixin.domain.RobotUser;
import com.haoniu.aixin.entity.ApplyStateInfo;
import com.haoniu.aixin.entity.MsgStateInfo;

import java.util.List;
import java.util.Map;

public class UserDao {
    public static final String TABLE_NAME = "uers";
    public static final String COLUMN_NAME_ID = "username";
    public static final String COLUMN_NAME_NICK = "nick";
    public static final String COLUMN_NAME_AVATAR = "avatar";
    public static final String COLUMN_NAME_TYPE = "type";
    public static final String COLUMN_NAME_GROUP_TYPE="groupType";

    public static final String TABLE_NAME_MSG = "msg_read_type";
    public static final String MSG_ISREAD = "msg_is_read";
    public static final String MSG_TYPE = "msg_type";
    public static final String MSG_ID = "msg_id";

    public static final String TABLE_NAME_APPLY = "apply_read_type";
    public static final String APPLY_ISREAD = "apply_is_read";
    public static final String APPLY_TYPE = "apply_type";
    public static final String APPLY_ID = "apply_id";

    public static final String ALL_USERS_TABLE_NAME = "alluers";
    public static final String GROUPS_TABLE_NAME = "groups";

    public static final String PREF_TABLE_NAME = "pref";
    public static final String COLUMN_NAME_DISABLED_GROUPS = "disabled_groups";
    public static final String COLUMN_NAME_DISABLED_IDS = "disabled_ids";

    public static final String ROBOT_TABLE_NAME = "robots";
    public static final String ROBOT_COLUMN_NAME_ID = "username";
    public static final String ROBOT_COLUMN_NAME_NICK = "nick";
    public static final String ROBOT_COLUMN_NAME_AVATAR = "avatar";


    public UserDao(Context context) {
    }

    /**
     * save contact list
     *
     * @param contactList
     */
    public void saveContactList(List<EaseUser> contactList) {
        DemoDBManager.getInstance().saveContactList(contactList);
    }

    /**
     * save user list
     *
     * @param contactList
     */
    public void saveUserList(List<EaseUser> contactList) {
        DemoDBManager.getInstance().saveUserList(contactList);
    }

    /**
     * get contact list
     *
     * @return
     */
    public Map<String, EaseUser> getContactList() {

        return DemoDBManager.getInstance().getContactList();
    }
    /**
     * get List groups
     *
     * @return
     */
    public Map<String,EaseGroupInfo> getGroupList() {
        return DemoDBManager.getInstance().getGroupList();
    }

    /**
     * get group
     *
     * @return
     */
    public EaseGroupInfo getGroup(String id) {
        return DemoDBManager.getInstance().getGroupById(id);
    }

    /**
     * get MsgStateInfo
     *
     * @return
     */
    public MsgStateInfo getMsgStateById(String id) {
        return DemoDBManager.getInstance().getMsgStateById(id);
    }

    /**
     * save MsgStateInfo
     *
     * @return
     */
    public void saveMsgStateInfo(MsgStateInfo msgStateInfo) {
        DemoDBManager.getInstance().saveMsgState(msgStateInfo);
    }

    /**
     * get MsgStateInfo
     *
     * @return
     */
    public ApplyStateInfo getApplyStateById(String id) {
        return DemoDBManager.getInstance().getApplyStateById(id);
    }

    /**
     * save MsgStateInfo
     *
     * @return
     */
    public void saveApplyStateInfo(ApplyStateInfo applyStateInfo) {
        DemoDBManager.getInstance().saveApplyState(applyStateInfo);
    }

    /**
     * get user
     *
     * @return
     */
    public EaseUser getUser(String id) {
        return DemoDBManager.getInstance().getUserById(id);
    }

    /**
     * delete a contact
     *
     * @param username
     */
    public void deleteContact(String username) {
        DemoDBManager.getInstance().deleteContact(username);
    }

    /**
     * delete a user
     *
     * @param username
     */
    public void deleteuser(String username) {
        DemoDBManager.getInstance().deleteuser(username);
    }

    /**
     * delete a group
     *
     * @param username
     */
    public void deleteGroup(String username) {
        DemoDBManager.getInstance().deleteGroup(username);
    }

    /**
     * save a contact
     *
     * @param user
     */
    public void saveContact(EaseUser user) {
        DemoDBManager.getInstance().saveContact(user);
    }

    /**
     * save a user
     *
     * @param user
     */
    public void saveuser(EaseUser user) {
        DemoDBManager.getInstance().saveuser(user);
    }

    /**
     * save a group
     *
     * @param user
     */
    public void saveGroup(EaseGroupInfo user) {
        DemoDBManager.getInstance().saveGroup(user);
    }

    public void setDisabledGroups(List<String> groups) {
        DemoDBManager.getInstance().setDisabledGroups(groups);
    }

    public List<String> getDisabledGroups() {
        return DemoDBManager.getInstance().getDisabledGroups();
    }

    public void setDisabledIds(List<String> ids) {
        DemoDBManager.getInstance().setDisabledIds(ids);
    }

    public List<String> getDisabledIds() {
        return DemoDBManager.getInstance().getDisabledIds();
    }

    public Map<String, RobotUser> getRobotUser() {
        return DemoDBManager.getInstance().getRobotList();
    }

    public void saveRobotUser(List<RobotUser> robotList) {
        DemoDBManager.getInstance().saveRobotList(robotList);
    }
}
