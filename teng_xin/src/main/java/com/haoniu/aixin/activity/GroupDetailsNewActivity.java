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

import com.haoniu.aixin.R;
import com.haoniu.aixin.adapter.RoomDeatilNewAdapter;
import com.haoniu.aixin.base.BaseActivity;
import com.haoniu.aixin.base.MyHelper;
import com.haoniu.aixin.domain.EaseGroupInfo;
import com.haoniu.aixin.entity.EventCenter;
import com.haoniu.aixin.entity.RoomInfo;
import com.haoniu.aixin.entity.UserInfo;
import com.haoniu.aixin.http.ApiClient;
import com.haoniu.aixin.http.AppConfig;
import com.haoniu.aixin.http.ResultListener;
import com.zds.base.Toast.ToastUtil;
import com.zds.base.json.FastJsonUtil;
import com.zds.base.log.XLog;
import com.zds.base.util.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 作   者：赵大帅
 * 描   述: 群详情
 * 日   期: 2017/11/21 14:51
 * 更新日期: 2017/11/21
 *
 * @author Administrator
 */
public class GroupDetailsNewActivity extends BaseActivity {


    @BindView(R.id.bar)
    View bar;
    @BindView(R.id.ll_back)
    LinearLayout llBack;
    @BindView(R.id.toolbar_subtitle)
    TextView toolbarSubtitle;
    @BindView(R.id.img_right)
    ImageView imgRight;
    @BindView(R.id.toolbar_title)
    TextView toolbarTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.llayout_title_1)
    RelativeLayout llayoutTitle1;
    @BindView(R.id.recycle_view)
    RecyclerView recycleView;
    @BindView(R.id.ll_more_member)
    LinearLayout llMoreMember;
    @BindView(R.id.tv_group_name)
    TextView tvGroupName;
    @BindView(R.id.ll_tuichu)
    LinearLayout llTuichu;

    private RoomInfo roomInfo;
    RoomDeatilNewAdapter roomDeatilNewAdapter;
    List<UserInfo> list = new ArrayList<>();
    private int page;

    @Override
    protected void initContentView(Bundle bundle) {
        setContentView(R.layout.activity_new_group_detail);
    }

    @Override
    protected void initLogic() {
        if (roomInfo==null){
            return;
        }

        toolbarTitle.setText(roomInfo.getName() + "(" + roomInfo.getRoomUserNum() + ")");
        tvGroupName.setText(roomInfo.getName());
        roomDeatilNewAdapter = new RoomDeatilNewAdapter(list);
        recycleView.setLayoutManager(new GridLayoutManager(this, 4));
        recycleView.setAdapter(roomDeatilNewAdapter);
        llMoreMember.setVisibility(View.GONE);
        if (roomInfo.getRoomBossDTOList()!=null&&roomInfo.getRoomBossDTOList().size()>0){

        roomDeatilNewAdapter.setRoomBossDTOListBean(roomInfo.getRoomBossDTOList().get(0));
        }
        getData();
        getRoomDeatil();
    }

    /**
     * 房间详情
     */
    protected void getRoomDeatil() {
        final Map<String, Object> map = new HashMap<>();
        map.put("groupId", roomInfo.getHuanxinGroupId());
        ApiClient.requestNetHandle(this, AppConfig.getRoomDetail, "", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                roomInfo = FastJsonUtil.getObject(json, RoomInfo.class);
                if (roomInfo != null) {
                    EaseGroupInfo easeGroupInfo = MyHelper.getInstance().getGroupById(roomInfo.getHuanxinGroupId());
                    easeGroupInfo.setHead(roomInfo.getRoomImg());
                    easeGroupInfo.setGroupName(roomInfo.getName());
                    easeGroupInfo.setGroupType(roomInfo.getType());
                    MyHelper.getInstance().saveGroup(easeGroupInfo);
                    toolbarTitle.setText(roomInfo.getName() + "(" + roomInfo.getRoomUserNum() + ")");
                    tvGroupName.setText(roomInfo.getName());
                    if (roomInfo.getRoomBossDTOList()!=null&&roomInfo.getRoomBossDTOList().size()>0){

                        roomDeatilNewAdapter.setRoomBossDTOListBean(roomInfo.getRoomBossDTOList().get(0));
                    }
                }
            }

            @Override
            public void onFailure(String msg) {
                ToastUtil.toast(msg);
            }
        });
    }

    /**
     * 获取群成员
     */
    private void getData() {
        page = 1;
        Map<String, Object> map = new HashMap<>();
        map.put("roomId", roomInfo.getId());
        map.put("pageNum", page);
        map.put("pageSize", 20);
        ApiClient.requestNetHandle(this, AppConfig.memberList, "", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                XLog.json(json);
                list.clear();
                List<UserInfo> userInfoList = FastJsonUtil.getList(json, "list", UserInfo.class);
                if (userInfoList != null) {
                    list.addAll(userInfoList);
                    if (list.size() > 16) {
                        list = Utils.getSubListPage(list, 0, 16);
                        llMoreMember.setVisibility(View.VISIBLE);
                    } else {
                        llMoreMember.setVisibility(View.GONE);
                    }
                } else {
                    llMoreMember.setVisibility(View.GONE);
                }
                roomDeatilNewAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(String msg) {
                ToastUtil.toast(msg);
            }
        });
    }

    @Override
    protected void onEventComing(EventCenter center) {
    }

    @Override
    protected void getBundleExtras(Bundle extras) {
        roomInfo = (RoomInfo) extras.getSerializable("roomInfo");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @OnClick({R.id.ll_more_member, R.id.ll_tuichu})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_more_member:
                startActivity(new Intent(GroupDetailsNewActivity.this,GroupMemberMoreActivity.class).putExtra("roomInfo",roomInfo));
                break;
            case R.id.ll_tuichu:
                layoutRoom(roomInfo.getHuanxinGroupId());
                break;
        }
    }

    /**
     * 退出房间
     */
    public void layoutRoom(String groupId) {
        Map<String, Object> map = new HashMap<>();
        map.put("groupId",groupId);
        ApiClient.requestNetHandle(this, AppConfig.layoutRoom, "加载中...", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                finish();
            }

            @Override
            public void onFailure(String msg) {
                ToastUtil.toast(msg);
            }
        });
    }
}
