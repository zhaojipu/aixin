package com.haoniu.aixin.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.haoniu.aixin.R;
import com.haoniu.aixin.adapter.EaseContactAdapter;
import com.haoniu.aixin.base.BaseActivity;
import com.haoniu.aixin.domain.EaseUser;
import com.haoniu.aixin.entity.EventCenter;
import com.haoniu.aixin.utils.EaseUserUtils;
import com.haoniu.aixin.widget.EaseSidebar;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.zds.base.util.BarUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PickAtUserActivity extends BaseActivity {
    ListView listView;
    View headerView;

    String groupId;
    EMGroup group;


    /**
     * 替代onCreate的使用
     *
     * @param bundle
     */
    @Override
    protected void initContentView(Bundle bundle) {
        setContentView(R.layout.activity_pick_at_user);
    }

    /**
     * 初始化逻辑
     */
    @Override
    protected void initLogic() {
        initBar();
        group = EMClient.getInstance().groupManager().getGroup(groupId);
        EaseSidebar sidebar = (EaseSidebar) findViewById(R.id.sidebar);
        listView = (ListView) findViewById(R.id.list);
        sidebar.setListView(listView);
        updateList();
        updateGroupData();

    }

    protected void initBar() {
        View bar = findViewById(R.id.bar);
        if (bar != null) {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) bar.getLayoutParams();
            params.height = BarUtils.getStatusBarHeight(this);
            bar.setLayoutParams(params);
        }
    }

    /**
     * EventBus接收消息
     *
     * @param center 获取事件总线信息
     */
    @Override
    protected void onEventComing(EventCenter center) {

    }

    /**
     * Bundle  传递数据
     *
     * @param extras
     */
    @Override
    protected void getBundleExtras(Bundle extras) {
        groupId = extras.getString("groupId", "");
    }

    void updateGroupData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    group = EMClient.getInstance().groupManager().getGroupFromServer(groupId);
                    EMClient.getInstance().groupManager().fetchGroupMembers(groupId, "", 200);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateList();
                    }
                });
            }
        }).start();
    }

    void updateList() {
        List<String> members = group.getMembers();
        List<EaseUser> userList = new ArrayList<EaseUser>();
        members.addAll(group.getAdminList());
        members.add(group.getOwner());
        for (String username : members) {
            EaseUser user = EaseUserUtils.getUserInfo(username);
            userList.add(user);
        }

        Collections.sort(userList, new Comparator<EaseUser>() {

            @Override
            public int compare(EaseUser lhs, EaseUser rhs) {
                if (lhs.getInitialLetter().equals(rhs.getInitialLetter())) {
                    return lhs.getNick().compareTo(rhs.getNick());
                } else {
                    if ("#".equals(lhs.getInitialLetter())) {
                        return 1;
                    } else if ("#".equals(rhs.getInitialLetter())) {
                        return -1;
                    }
                    return lhs.getInitialLetter().compareTo(rhs.getInitialLetter());
                }

            }
        });
        final boolean isOwner = EMClient.getInstance().getCurrentUser().equals(group.getOwner());
        if (isOwner) {
            addHeadView();
        } else {
            if (headerView != null) {
                listView.removeHeaderView(headerView);
                headerView = null;
            }
        }
        listView.setAdapter(new PickUserAdapter(this, 0, userList));
        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (isOwner) {
                    if (position != 0) {
                        EaseUser user = (EaseUser) listView.getItemAtPosition(position);
                        if (EMClient.getInstance().getCurrentUser().equals(user.getUsername())) {
                            return;
                        }
                        setResult(RESULT_OK, new Intent().putExtra("username", user.getUsername()));
                    } else {
                        setResult(RESULT_OK, new Intent().putExtra("username", getString(R.string.all_members)));
                    }
                } else {
                    EaseUser user = (EaseUser) listView.getItemAtPosition(position);
                    if (EMClient.getInstance().getCurrentUser().equals(user.getUsername())) {
                        return;
                    }
                    setResult(RESULT_OK, new Intent().putExtra("username", user.getUsername()));
                }

                finish();
            }
        });
    }

    private void addHeadView() {
        if (listView.getHeaderViewsCount() == 0) {
            View view = LayoutInflater.from(this).inflate(R.layout.ease_row_contact, listView, false);
            ImageView avatarView = (ImageView) view.findViewById(R.id.avatar);
            TextView textView = (TextView) view.findViewById(R.id.name);
            textView.setText(getString(R.string.all_members));
            avatarView.setImageResource(R.drawable.ease_groups_icon);
            listView.addHeaderView(view);
            headerView = view;
        }
    }

    @Override
    public void back(View view) {
        finish();
    }

    private class PickUserAdapter extends EaseContactAdapter {

        public PickUserAdapter(Context context, int resource, List<EaseUser> objects) {
            super(context, resource, objects);
        }
    }
}
