/**
 * 作   者：赵大帅
 * 描   述: 好友
 * 日   期: 2017/11/27 9:39
 * 更新日期: 2017/11/27
 */
package com.haoniu.aixin.activity.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.haoniu.aixin.activity.ChatActivity;
import com.haoniu.aixin.activity.ChiefActivity;
import com.haoniu.aixin.activity.SevicePeopleActivity;
import com.haoniu.aixin.base.Constant;
import com.haoniu.aixin.R;
import com.haoniu.aixin.base.MyHelper;
import com.haoniu.aixin.domain.EaseUser;

import java.util.Hashtable;
import java.util.Map;

/**
 * 好友列表
 */
public class ContactListFragment extends EaseContactListFragment {

    private View loadingView;
    @SuppressLint("InflateParams")
    @Override
    protected void initLogic() {
        super.initLogic();
        @SuppressLint("InflateParams") View headerView = LayoutInflater.from(getActivity()).inflate(R.layout.em_contacts_header, null);
        HeaderItemClickListener clickListener = new HeaderItemClickListener();
        headerView.findViewById(R.id.item_1).setOnClickListener(clickListener);
        headerView.findViewById(R.id.item_2).setOnClickListener(clickListener);
        headerView.findViewById(R.id.item_3).setOnClickListener(clickListener);
        headerView.findViewById(R.id.item_4).setOnClickListener(clickListener);
        listView.addHeaderView(headerView);
        //add loading view
        loadingView = LayoutInflater.from(getActivity()).inflate(R.layout.em_layout_loading_data, null);
        contentContainer.addView(loadingView);
        registerForContextMenu(listView);
        //设置联系人数据
        Map<String, EaseUser> m = MyHelper.getInstance().getContactList();
        if (m instanceof Hashtable<?, ?>) {
            m = (Map<String, EaseUser>) ((Hashtable<String, EaseUser>) m).clone();
        }
        setContactsMap(m);
        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                EaseUser user = (EaseUser) listView.getItemAtPosition(position);
                if (user != null) {
                    String username = user.getUsername();
                    startActivity(new Intent(getActivity(), ChatActivity.class).putExtra("chatType",Constant.CHATTYPE_SINGLE).putExtra("userId", username));
                }
            }
        });
    }

    protected class HeaderItemClickListener implements OnClickListener{

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.item_1:
                    /**
                     * 我的上级
                     */
                    startActivity(new Intent(getActivity(), ChiefActivity.class));
                    break;
                case R.id.item_2:
                    /**
                     * 群主
                     */
                    startActivity(new Intent(getActivity(), SevicePeopleActivity.class).putExtra("type",3).putExtra("title","群主"));
                    break;
                case R.id.item_3:
                    /**
                     *  人工充值客服
                     */
                    startActivity(new Intent(getActivity(), SevicePeopleActivity.class).putExtra("type",1).putExtra("title","人工充值客服"));
                    break;
                case R.id.item_4:
                    /**
                     * 游戏客服问题
                     */
                    startActivity(new Intent(getActivity(), SevicePeopleActivity.class).putExtra("type",2).putExtra("title","游戏客服问题"));

                    break;
                default:
                    break;
            }
        }

    }
    @Override
    public void refresh() {
        Map<String, EaseUser> m = MyHelper.getInstance().getContactList();
        if (m instanceof Hashtable<?, ?>) {
            //noinspection unchecked
            m = (Map<String, EaseUser>) ((Hashtable<String, EaseUser>) m).clone();
        }
        setContactsMap(m);
        super.refresh();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        return super.onContextItemSelected(item);
    }

}
