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
package com.haoniu.aixin.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.haoniu.aixin.http.ApiClient;
import com.haoniu.aixin.http.AppConfig;
import com.haoniu.aixin.http.ResultListener;
import com.haoniu.aixin.R;
import com.haoniu.aixin.adapter.RoomDeatilAdapter;
import com.haoniu.aixin.base.BaseActivity;
import com.haoniu.aixin.base.Constant;
import com.haoniu.aixin.base.MyApplication;
import com.haoniu.aixin.base.MyHelper;
import com.haoniu.aixin.domain.EaseGroupInfo;
import com.haoniu.aixin.domain.EaseUser;
import com.haoniu.aixin.entity.EventCenter;
import com.haoniu.aixin.parse.ParseManager;
import com.haoniu.aixin.utils.EventUtil;
import com.haoniu.aixin.utils.payUtil.ShareUtil;
import com.haoniu.aixin.widget.EaseSwitchButton;
import com.hyphenate.EMGroupChangeListener;
import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.chat.EMMucSharedFile;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.zds.base.ImageLoad.GlideUtils;
import com.zds.base.Toast.ToastUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 作   者：赵大帅
 * 描   述: 群详情
 * 日   期: 2017/11/21 14:51
 * 更新日期: 2017/11/21
 *
 * @author Administrator
 */
public class GroupDetailsActivity extends BaseActivity implements EMGroupChangeListener {
    @BindView(R.id.bar)
    View mBar;
    @BindView(R.id.ll_back)
    LinearLayout mLlBack;
    @BindView(R.id.toolbar_subtitle)
    TextView mToolbarSubtitle;
    @BindView(R.id.img_right)
    ImageView mImgRight;
    @BindView(R.id.toolbar_title)
    TextView mToolbarTitle;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.head_group)
    ImageView mHeadGroup;
    @BindView(R.id.tv_group_notice)
    TextView mTvGroupNotice;
    @BindView(R.id.rl_group_notice)
    RelativeLayout mRlGroupNotice;
    @BindView(R.id.group_num)
    TextView mGroupNum;
    @BindView(R.id.tv_room_id)
    TextView mTvRoomId;
    @BindView(R.id.rl_room_id)
    RelativeLayout mRlRoomId;
    @BindView(R.id.tv_group_id)
    TextView mTvGroupId;
    @BindView(R.id.rl_group_id)
    RelativeLayout mRlGroupId;
    @BindView(R.id.group_ewm)
    RelativeLayout mGroupEwm;
    @BindView(R.id.group_set)
    RelativeLayout mGroupSet;
    @BindView(R.id.tv_exit)
    TextView mTvExit;
    @BindView(R.id.rel_exit)
    RelativeLayout mRelExit;
    @BindView(R.id.recycle_view)
    RecyclerView mRecycleView;
    @BindView(R.id.rel_set)
    RelativeLayout mRelSet;
    @BindView(R.id.ll_more)
    LinearLayout mLlMore;
    @BindView(R.id.rel_shuoming)
    RelativeLayout mRelShuoming;
    @BindView(R.id.rel_room_set)
    RelativeLayout mRelRoomSet;
    @BindView(R.id.tv_group_tjm)
    TextView mTvGroupTjm;
    @BindView(R.id.rl_group_tjm)
    RelativeLayout mRlGroupTjm;
    @BindView(R.id.switch_btn)
    EaseSwitchButton mSwitchBtn;
    @BindView(R.id.img_to_right_2)
    ImageView mImgToRight2;
    @BindView(R.id.ll_notices)
    LinearLayout mLlNotices;

    private String groupId;
    private EMGroup mEMGroup;
    private String roomType;
    private String notice;
    private String code;

    private RoomDeatilAdapter adapter;
    private List<String> memberList;


    /**
     * 替代onCreate的使用
     *
     * @param bundle
     */
    @Override
    protected void initContentView(Bundle bundle) {
        setContentView(R.layout.activity_group_details);
    }

    /**
     * 初始化逻辑
     */
    @Override
    protected void initLogic() {
        mEMGroup = EMClient.getInstance().groupManager().getGroup(groupId);
        mTvGroupId.setText(mEMGroup.getGroupName());
        mTvGroupTjm.setText(code);
        setTitle(mEMGroup.getGroupName() + "(" + mEMGroup.getMemberCount() + "人)");
        mGroupNum.setText("共" + mEMGroup.getMemberCount() + "人");
        EaseGroupInfo easeGroupInfo = MyHelper.getInstance().getGroupById(groupId);
        if (easeGroupInfo.getType() != null && easeGroupInfo.getType().equals(Constant.GROUP_MSG_SET)) {
            mSwitchBtn.openSwitch();
        } else {
            mSwitchBtn.closeSwitch();
        }
        if (mEMGroup.getMemberCount() > 100) {
            mLlMore.setVisibility(View.VISIBLE);
        } else {
            mLlMore.setVisibility(View.GONE);
        }
        mTvGroupNotice.setText(notice);
        memberList = new ArrayList<>();
        adapter = new RoomDeatilAdapter(memberList);
        mRecycleView.setHasFixedSize(true);
        mRecycleView.setNestedScrollingEnabled(false);
        mRecycleView.setLayoutManager(new GridLayoutManager(this, 4));
        mRecycleView.setAdapter(adapter);
        adapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapters, View view, int position) {
                if (adapter.getMode() == 1) {
                    deleteMembersFromGroup(memberList.get(position));
                }
            }
        });
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapters, View view, int position) {
                if ("-1".equals(memberList.get(position))) {
                    adapter.setMode(1);
                    adapter.notifyDataSetChanged();
                } else if ("-2".equals(memberList.get(position))) {
                    startActivityForResult((new Intent(GroupDetailsActivity.this, GroupPickContactsActivity.class).putExtra("roomId", groupId)),
                            211);
                } else {
                    //startActivity(new Intent(GroupDetailsActivity.this, UserInfoActivity.class).putExtra("username", memberList.get(position)));
                }
            }
        });
        if (mEMGroup != null) {
            List<String> list = mEMGroup.getMembers();
            for (int i = 0; i < list.size(); i++) {
                if (mEMGroup != null && !mEMGroup.getOwner().equals(list.get(i))) {
                    memberList.add(list.get(i));
                }
            }
            memberList.add(0, mEMGroup.getOwner());
            if ((MyApplication.getInstance().getUserInfo().getIdh() + "").equals(mEMGroup.getOwner())) {
                //  mRelSet.setVisibility(View.VISIBLE);
                if (memberList.size() > 1) {
                    memberList.add("-1");
                }
                mTvExit.setText("解散群组");
            } else {
                //   mRelSet.setVisibility(View.GONE);
                mTvExit.setText("退出群组");
            }
            adapter.notifyDataSetChanged();
        }
        mTvGroupNotice.setText(notice);
        GlideUtils.loadImageViewLoding(AppConfig.checkimg( MyHelper.getInstance().getGroupById(groupId).getHead()), mHeadGroup, R.mipmap.defult_group_icon);
        updateRoom();
        EMClient.getInstance().groupManager().addGroupChangeListener(this);

        /**
         * 分享
         */
        mImgRight.setImageResource(R.mipmap.img_share_w);
        mImgRight.setVisibility(View.GONE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    EMClient.getInstance().groupManager().unblockGroupMessage(groupId);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 更新房间成员
     */
    protected void updateRoom() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mEMGroup = EMClient.getInstance().groupManager().getGroupFromServer(groupId, true);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mTvGroupId.setText(mEMGroup.getGroupName());
                            setTitle(mEMGroup.getGroupName() + "(" + mEMGroup.getMemberCount() + "人)");
                            mGroupNum.setText("共" + mEMGroup.getMemberCount() + "人");
                            if (mEMGroup.getMemberCount() > 100) {
                                mLlMore.setVisibility(View.VISIBLE);
                            } else {
                                mLlMore.setVisibility(View.GONE);
                            }
                            GlideUtils.loadImageViewLoding(AppConfig.checkimg( MyHelper.getInstance().getGroupById(groupId).getHead()), mHeadGroup, R.mipmap.defult_group_icon);
                            refreshMembers();
                            List<String> mem = mEMGroup.getMembers();
                            mem.add(mEMGroup.getOwner());
                            initHead(mem);
                        }
                    });
                } catch (Exception e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                        }
                    });
                }
            }
        }).start();
    }

    /**
     * 刷新
     */
    private void refreshMembers() {
        memberList.clear();
        List<String> list = mEMGroup.getMembers();
        for (int i = 0; i < list.size(); i++) {
            if (mEMGroup != null && !mEMGroup.getOwner().equals(list.get(i))) {
                memberList.add(list.get(i));
            }
        }
        memberList.add(0, mEMGroup.getOwner());
        if ((MyApplication.getInstance().getUserInfo().getIdh() + "").equals(mEMGroup.getOwner())) {
            if (memberList.size() > 1) {
                memberList.add("-1");
            }
        }
        adapter.notifyDataSetChanged();

    }

    private void initHead(List<String> usernames) {
        ParseManager.getInstance().getContactInfos(usernames, new EMValueCallBack<List<EaseUser>>() {

            @Override
            public void onSuccess(List<EaseUser> value) {
                if (value != null && value.size() > 0) {
                    MyHelper.getInstance().saveUserList(value);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onError(int error, String errorMsg) {

            }

        });
    }

    /**
     * EventBus接收消息
     *
     * @param center 获取事件总线信息
     */
    @Override
    protected void onEventComing(EventCenter center) {
        if (center.getEventCode() == EventUtil.FLUSHRENAME) {
            if (center.getData().toString().equals(groupId)) {
                updateRoom();
            }
        }
    }

    /**
     * Bundle  传递数据
     *
     * @param extras
     */
    @Override
    protected void getBundleExtras(Bundle extras) {
        groupId = extras.getString("groupId", "0");
        roomType = extras.getString("roomType", "");
        notice = extras.getString("notice", "");
        code = extras.getString("code", "");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 211) {
                final String[] newmembers = data.getStringArrayExtra("newmembers");
                if (newmembers == null || newmembers.length == 0) {
                    return;
                }
                addMembersToRoom(newmembers);
            }
        }
    }


    @Override
    public void onInvitationReceived(String s, String s1, String s2, String s3) {

    }

    @Override
    public void onRequestToJoinReceived(String s, String s1, String s2, String s3) {

    }

    @Override
    public void onRequestToJoinAccepted(String s, String s1, String s2) {

    }

    @Override
    public void onRequestToJoinDeclined(String s, String s1, String s2, String s3) {

    }

    @Override
    public void onInvitationAccepted(String groupId, String inviter, String reason) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateRoom();
            }
        });
    }

    @Override
    public void onInvitationDeclined(String s, String s1, String s2) {

    }

    @Override
    public void onUserRemoved(String groupId, String groupName) {
        finish();
    }

    @Override
    public void onGroupDestroyed(String groupId, String groupName) {
        finish();
    }

    @Override
    public void onAutoAcceptInvitationFromGroup(String s, String s1, String s2) {

    }

    @Override
    public void onMuteListAdded(String groupId, final List<String> mutes, final long muteExpire) {

    }

    @Override
    public void onMuteListRemoved(String groupId, final List<String> mutes) {

    }

    @Override
    public void onAdminAdded(String groupId, String administrator) {

    }

    @Override
    public void onAdminRemoved(String groupId, String administrator) {

    }

    @Override
    public void onOwnerChanged(String groupId, String newOwner, String oldOwner) {

    }

    @Override
    public void onMemberJoined(String groupId, String member) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateRoom();
            }
        });
    }

    @Override
    public void onMemberExited(String groupId, String member) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateRoom();
            }
        });
    }

    @Override
    public void onAnnouncementChanged(String groupId, final String announcement) {
    }

    @Override
    public void onSharedFileAdded(String groupId, final EMMucSharedFile sharedFile) {
    }

    @Override
    public void onSharedFileDeleted(String groupId, String fileId) {
    }


    @Override
    protected void onDestroy() {
        EMClient.getInstance().groupManager().removeGroupChangeListener(this);
        super.onDestroy();
    }


    //邀请加入房间
    private void addMembersToRoom(String[] newmembers) {
        if (newmembers == null || newmembers.length == 0) {
            return;
        }
        String strUser = "";
        for (String s : newmembers) {
            strUser += s + ",";
        }
        Map<String, Object> map = new HashMap<>();
        map.put("groupId", groupId);
        map.put("userIdStr", strUser);
        ApiClient.requestNetHandle(this, AppConfig.addGroupPeople, "正在添加...", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                updateRoom();
            }

            @Override
            public void onFailure(String msg) {
                ToastUtil.toast(msg);
            }
        });
    }


    /**
     * 删除群成员
     *
     * @param username
     */
    protected void deleteMembersFromGroup(final String username) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("groupId", groupId);
        map.put("userId", username);
        ApiClient.requestNetHandle(GroupDetailsActivity.this, AppConfig.RemoveRoom, "正在删除...", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                toast(msg);
            }

            @Override
            public void onFinsh() {
                super.onFinsh();
                adapter.setMode(0);
            }

            @Override
            public void onFailure(String msg) {
                ToastUtil.toast(msg);
            }
        });
    }

    /**
     * 退出房间
     */
    private void layout() {
        Map<String, Object> map = new HashMap<>();
        map.put("groupId", groupId);
        ApiClient.requestNetHandle(this, AppConfig.layoutRoom, "正在退出...", map, new ResultListener() {
            /**
             * 请求成功
             *
             * @param json
             * @param msg
             */
            @Override
            public void onSuccess(String json, String msg) {
                toast(msg);
                finish();
            }

            /**
             * 请求失败
             *
             * @param msg
             */
            @Override
            public void onFailure(String msg) {
                ToastUtil.toast(msg);
            }

        });
    }

    /**
     * 解散房间
     */
    private void layoutRoom() {
        Map<String, Object> map = new HashMap<>();
        map.put("groupId", groupId);
        ApiClient.requestNetHandle(this, AppConfig.deleteGroup, "正在退出...", map, new ResultListener() {
            /**
             * 请求成功
             *
             * @param json
             * @param msg
             */
            @Override
            public void onSuccess(String json, String msg) {
                finish();
            }

            /**
             * 请求失败
             *
             * @param msg
             */
            @Override
            public void onFailure(String msg) {
                ToastUtil.toast(msg);
            }

        });
    }

    @OnClick({R.id.img_right, R.id.rel_shuoming, R.id.group_set, R.id.rl_group_notice, R.id.switch_btn, R.id.rel_exit, R.id.ll_more})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            /**
             * 更多
             */
            case R.id.ll_more:
                if (mEMGroup.getOwner().equals(MyApplication.getInstance().getUserInfo().getIdh() + "")) {
                    startActivity(new Intent(GroupDetailsActivity.this, GroupMemberMoreActivity.class).putExtra("groupId", groupId).putExtra("isOwer", true).putExtra("ower", mEMGroup.getOwner()));
                } else {
                    startActivity(new Intent(GroupDetailsActivity.this, GroupMemberMoreActivity.class).putExtra("groupId", groupId).putExtra("isOwer", false).putExtra("ower", mEMGroup.getOwner()));
                }
                break;
            /**
             * 屏蔽群消息
             */
            case R.id.switch_btn:
                toggleBlockGroup();
                break;
            /**
             * 房间设置
             */
            case R.id.group_set:
                break;
            /**
             * 退出
             */
            case R.id.rel_exit:
                if (mEMGroup.getOwner().equals(MyApplication.getInstance().getUserInfo().getIdh() + "")) {
                    layoutRoom();
                } else {
                    layout();
                }
                break;
            case R.id.rel_shuoming:
                startActivity(new Intent(GroupDetailsActivity.this, WebViewActivity.class).putExtra("title", "玩法说明").putExtra("url", AppConfig.howToPlay + roomType));
                break;
            case R.id.img_right:
                share();
                break;
            default:
        }
    }

    /**
     * 分享
     */
    private void share() {
        MyApplication.getInstance().shareDialog(GroupDetailsActivity.this, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    /**
                     * 微信好友
                     */
                    case R.id.wx_chat:
                        ShareUtil.shareUrl(GroupDetailsActivity.this, AppConfig.downloadUrl, getResources().getString(R.string.app_name), "房间：" + mEMGroup.getGroupName() + "，房间号：" + code + "，欢迎加入！", R.mipmap.ic_launcher, SendMessageToWX.Req.WXSceneSession);
                        break;
                    /**
                     * 微信朋友圈
                     */
                    case R.id.wx_qun:
                        ShareUtil.shareUrl(GroupDetailsActivity.this, AppConfig.downloadUrl, getResources().getString(R.string.app_name), "房间：" + mEMGroup.getGroupName() + "，房间号：" + code + "，欢迎加入！", R.mipmap.ic_launcher, SendMessageToWX.Req.WXSceneTimeline);
                        break;
                    default:
                }
            }
        });
    }

    /**
     * 屏蔽群消息
     */
    private void toggleBlockGroup() {
        if (mSwitchBtn.isSwitchOpen()) {
            //status true number
            //   状态（0屏蔽消息，1接收消息）
            Map<String, Object> map = new HashMap<>();
            map.put("huanxinGroupId", groupId);
            map.put("status", 1);
            ApiClient.requestNetHandle(this, AppConfig.upMessageSet, "正在更新..", map, new ResultListener() {
                @Override
                public void onSuccess(String json, String msg) {
                    EaseGroupInfo easeGroupInfo = MyHelper.getInstance().getGroupById(groupId);
                    easeGroupInfo.setType(Constant.GROUP_MSG_SET_NO);
                    MyHelper.getInstance().saveGroup(easeGroupInfo);
                    mSwitchBtn.closeSwitch();
                }

                @Override
                public void onFailure(String msg) {
                    ToastUtil.toast(msg);
                }
            });

        } else {
            Map<String, Object> map = new HashMap<>();
            map.put("huanxinGroupId", groupId);
            map.put("status", 0);
            ApiClient.requestNetHandle(this, AppConfig.upMessageSet, "正在更新..", map, new ResultListener() {
                @Override
                public void onSuccess(String json, String msg) {
                    EaseGroupInfo easeGroupInfo = MyHelper.getInstance().getGroupById(groupId);
                    easeGroupInfo.setType(Constant.GROUP_MSG_SET);
                    MyHelper.getInstance().saveGroup(easeGroupInfo);
                    mSwitchBtn.openSwitch();
                }

                @Override
                public void onFailure(String msg) {
                    ToastUtil.toast(msg);
                }
            });
        }
    }

}
