package com.haoniu.aixin.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.WindowManager;

import com.haoniu.aixin.R;
import com.haoniu.aixin.activity.fragment.BaseChatFragment;
import com.haoniu.aixin.activity.fragment.ChatFragment;
import com.haoniu.aixin.base.BaseActivity;
import com.haoniu.aixin.base.EaseConstant;
import com.haoniu.aixin.entity.EventCenter;
import com.haoniu.aixin.runtimepermissions.PermissionsManager;
import com.hyphenate.util.EasyUtils;

/**
 * 作   者：赵大帅
 * 描   述: 聊天
 * 日   期: 2017/11/17 18:07
 * 更新日期: 2017/11/17
 */
public class ChatActivity extends BaseActivity {
    public static ChatActivity activityInstance;
    private BaseChatFragment chatFragment;
    String toChatUsername;

    /**
     * 替代onCreate的使用
     *
     * @param bundle
     */
    @Override
    protected void initContentView(Bundle bundle) {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
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
        chatFragment = new ChatFragment();
        //pass parameters to chat fragment
        chatFragment.setArguments(getIntent().getExtras());
        getSupportFragmentManager().beginTransaction().add(R.id.container, chatFragment).commit();
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
        toChatUsername = extras.getString("userId");

    }


    @Override
    protected void onDestroy() {
        try {
            int chatType = getIntent().getExtras().getInt(EaseConstant.EXTRA_CHAT_TYPE, EaseConstant.CHATTYPE_SINGLE);
            if (chatType == EaseConstant.CHATTYPE_GROUP) {

                //MyApplication.getInstance().layoutRoom(toChatUsername);
            }
        }catch (Exception e){
            e.getStackTrace();
        }
        super.onDestroy();
        activityInstance = null;


    }


    @Override
    protected void onNewIntent(Intent intent) {
        // make sure only one chat activity is opened
        String username = intent.getStringExtra("userId");
        if (toChatUsername.equals(username)) {
            super.onNewIntent(intent);
        } else {
            finish();
            startActivity(intent);
        }

    }

    @Override
    public void onBackPressed() {
        chatFragment.onBackPressed();
        if (EasyUtils.isSingleActivity(this)) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }

    public String getToChatUsername() {
        return toChatUsername;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        PermissionsManager.getInstance().notifyPermissionsChange(permissions, grantResults);
    }
}
