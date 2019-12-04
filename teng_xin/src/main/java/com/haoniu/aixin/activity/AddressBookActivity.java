package com.haoniu.aixin.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.haoniu.aixin.R;
import com.haoniu.aixin.activity.fragment.ContactListFragment;
import com.haoniu.aixin.activity.fragment.EaseContactListFragment;
import com.haoniu.aixin.base.BaseActivity;
import com.haoniu.aixin.entity.EventCenter;
import com.haoniu.aixin.runtimepermissions.PermissionsManager;

/**
 * 作   者：赵大帅
 * 描   述: 聊天
 * 日   期: 2017/11/17 18:07
 * 更新日期: 2017/11/17
 *
 * @author Administrator
 */
@SuppressLint("Registered")
public class AddressBookActivity extends BaseActivity {
    public static AddressBookActivity activityInstance;
    private EaseContactListFragment mContactListFragment;

    /**
     * 替代onCreate的使用
     *
     * @param bundle
     */
    @Override
    protected void initContentView(Bundle bundle) {
        setContentView(R.layout.activity_chat);
    }

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
    }

    /**
     * 初始化逻辑
     */
    @Override
    protected void initLogic() {
        activityInstance = this;
        //use EaseChatFratFragment
        mContactListFragment = new ContactListFragment();
        //pass parameters to chat fragment
        mContactListFragment.setArguments(getIntent().getExtras());
        getSupportFragmentManager().beginTransaction().add(R.id.container, mContactListFragment).commit();
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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        activityInstance = null;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        // make sure only one chat activity is opened
    }

//    @Override
//    public void onBackPressed() {
//        if (EasyUtils.isSingleActivity(this)) {
//            Intent intent = new Intent(this, MainActivity.class);
//            startActivity(intent);
//        }
//    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        PermissionsManager.getInstance().notifyPermissionsChange(permissions, grantResults);
    }
}
