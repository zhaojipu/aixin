package com.haoniu.aixin.base;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Looper;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.haoniu.aixin.R;
import com.haoniu.aixin.activity.ChatActivity;
import com.haoniu.aixin.activity.MainActivity;
import com.haoniu.aixin.db.DemoDBManager;
import com.haoniu.aixin.db.InviteMessgeDao;
import com.haoniu.aixin.db.UserDao;
import com.haoniu.aixin.domain.EaseAvatarOptions;
import com.haoniu.aixin.domain.EaseEmojicon;
import com.haoniu.aixin.domain.EaseEmojiconGroupEntity;
import com.haoniu.aixin.domain.EaseGroupInfo;
import com.haoniu.aixin.domain.EaseUser;
import com.haoniu.aixin.domain.EmojiconExampleGroupData;
import com.haoniu.aixin.domain.InviteMessage;
import com.haoniu.aixin.domain.InviteMessage.InviteMessageStatus;
import com.haoniu.aixin.domain.RobotUser;
import com.haoniu.aixin.entity.ApplyStateInfo;
import com.haoniu.aixin.entity.MsgStateInfo;
import com.haoniu.aixin.model.EaseAtMessageHelper;
import com.haoniu.aixin.model.EaseNotifier;
import com.haoniu.aixin.parse.UserProfileManager;
import com.haoniu.aixin.receiver.CallReceiver;
import com.haoniu.aixin.utils.EaseCommonUtils;
import com.haoniu.aixin.utils.PreferenceManager;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMConnectionListener;
import com.hyphenate.EMContactListener;
import com.hyphenate.EMError;
import com.hyphenate.EMGroupChangeListener;
import com.hyphenate.EMMessageListener;
import com.hyphenate.EMMultiDeviceListener;
import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMMessage.ChatType;
import com.hyphenate.chat.EMMessage.Status;
import com.hyphenate.chat.EMMessage.Type;
import com.hyphenate.chat.EMMucSharedFile;
import com.hyphenate.chat.EMOptions;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.exceptions.HyphenateException;
import com.hyphenate.util.EMLog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MyHelper {
    /**
     * data sync listener
     */
    public interface DataSyncListener {
        /**
         * sync complete
         *
         * @param success true：data sync successful，false: failed to sync data
         */
        void onSyncComplete(boolean success);
    }

    protected static final String TAG = "MyHelper";

    private EaseUI easeUI;

    /**
     * EMEventListener
     */
    protected EMMessageListener messageListener = null;

    private Map<String, EaseUser> contactList;

    private Map<String, RobotUser> robotList;

    private UserProfileManager userProManager;

    private static MyHelper instance = null;

    private MyModel demoModel = null;

    /**
     * sync groups status listener
     */
    private List<DataSyncListener> syncGroupsListeners;
    /**
     * sync contacts status listener
     */
    private List<DataSyncListener> syncContactsListeners;
    /**
     * sync blacklist status listener
     */
    private List<DataSyncListener> syncBlackListListeners;

    private boolean isSyncingGroupsWithServer = false;
    private boolean isSyncingContactsWithServer = false;
    private boolean isSyncingBlackListWithServer = false;
    private boolean isGroupsSyncedWithServer = false;
    private boolean isContactsSyncedWithServer = false;
    private boolean isBlackListSyncedWithServer = false;

    public boolean isVoiceCalling;
    public boolean isVideoCalling;

    private String username;

    private Context appContext;

    private CallReceiver callReceiver;

    private InviteMessgeDao inviteMessgeDao;
    private UserDao userDao;

    private LocalBroadcastManager broadcastManager;

    private boolean isGroupAndContactListenerRegisted;

    private ExecutorService executor;

    protected android.os.Handler handler;

    Queue<String> msgQueue = new ConcurrentLinkedQueue<>();

    private MyHelper() {
        executor = Executors.newCachedThreadPool();
    }

    public synchronized static MyHelper getInstance() {
        if (instance == null) {
            instance = new MyHelper();
        }
        return instance;
    }

    public void execute(Runnable runnable) {
        executor.execute(runnable);
    }

    /**
     * init helper
     *
     * @param context application context
     */
    public void init(Context context) {
        demoModel = new MyModel(context);
        EMOptions options = initChatOptions();

        //use default options if options is null
        if (EaseUI.getInstance().init(context, options)) {
            appContext = context;

            //debug mode, you'd better set it to false, if you want release your App officially.
            EMClient.getInstance().setDebugMode(true);
            //get easeui instance
            easeUI = EaseUI.getInstance();
            //to set user's profile and avatar
            setEaseUIProviders();
            //initialize preference manager
            PreferenceManager.init(context);
            //initialize profile manager
            getUserProfileManager().init(context);
            //set Call options
            setCallOptions();


            /**
             * This function is only meaningful when your app need recording
             * If not, remove it.
             * This function need be called before the video stream started, so we set it in onCreate function.
             * This method will set the preferred video record encoding codec.
             * Using default encoding format, recorded file may not be played by mobile player.
             */

            // resolution
            String resolution = PreferenceManager.getInstance().getCallBackCameraResolution();
            if ("".equals(resolution)) {
                resolution = PreferenceManager.getInstance().getCallFrontCameraResolution();
            }
            String[] wh = resolution.split("x");
            if (wh.length == 2) {
                try {
                    //   EMClient.getInstance().callManager().getCallOptions().setVideoResolution(new Integer(wh[0]).intValue(), new Integer(wh[1]).intValue());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            // enabled fixed sample rate
            boolean enableFixSampleRate = PreferenceManager.getInstance().isCallFixedVideoResolution();
            //   EMClient.getInstance().callManager().getCallOptions().enableFixedVideoResolution(enableFixSampleRate);

            // Offline call push
            //   EMClient.getInstance().callManager().getCallOptions().setIsSendPushIfOffline(getModel().isPushCall());

            setGlobalListeners();
            broadcastManager = LocalBroadcastManager.getInstance(appContext);
            initDbDao();
        }
    }


    private EMOptions initChatOptions() {
        Log.d(TAG, "init HuanXin Options");

        EMOptions options = new EMOptions();
        // set if accept the invitation automatically
        options.setAcceptInvitationAlways(false);
        // set if you need read ack
        options.setRequireAck(true);
        options.setAutoLogin(true);
        // set if you need delivery ack
        options.setRequireDeliveryAck(false);

        /**
         * NOTE:你需要设置自己申请的Sender ID来使用Google推送功能，详见集成文档
         */
        options.setFCMNumber("921300338324");
        //you need apply & set your own id if you want to use Mi push notification
        options.setMipushConfig("2882303761517709014", "5621770995014");

        //set custom servers, commonly used in private deployment
        if (demoModel.isCustomServerEnable() && demoModel.getRestServer() != null && demoModel.getIMServer() != null) {
            options.setRestServer(demoModel.getRestServer());
            options.setIMServer(demoModel.getIMServer());
            if (demoModel.getIMServer().contains(":")) {
                options.setIMServer(demoModel.getIMServer().split(":")[0]);
                options.setImPort(Integer.valueOf(demoModel.getIMServer().split(":")[1]));
            }
        }

        if (demoModel.isCustomAppkeyEnabled() && demoModel.getCutomAppkey() != null && !demoModel.getCutomAppkey().isEmpty()) {
            options.setAppKey(demoModel.getCutomAppkey());
        }
        options.allowChatroomOwnerLeave(getModel().isChatroomOwnerLeaveAllowed());
        options.setDeleteMessagesAsExitGroup(getModel().isDeleteMessagesAsExitGroup());
        options.setAutoAcceptGroupInvitation(getModel().isAutoAcceptGroupInvitation());
        // Whether the message attachment is automatically uploaded to the Hyphenate server,
        options.setAutoTransferMessageAttachments(getModel().isSetTransferFileByUser());
        // Set Whether auto download thumbnail, default value is true.
        options.setAutoDownloadThumbnail(getModel().isSetAutodownloadThumbnail());
        return options;
    }

    private void setCallOptions() {
        // min video kbps
//        int minBitRate = PreferenceManager.getInstance().getCallMinVideoKbps();
//        if (minBitRate != -1) {
//            EMClient.getInstance().callManager().getCallOptions().setMinVideoKbps(minBitRate);
//        }
//
//        // max video kbps
//        int maxBitRate = PreferenceManager.getInstance().getCallMaxVideoKbps();
//        if (maxBitRate != -1) {
//            EMClient.getInstance().callManager().getCallOptions().setMaxVideoKbps(maxBitRate);
//        }
//
//        // max frame rate
//        int maxFrameRate = PreferenceManager.getInstance().getCallMaxFrameRate();
//        if (maxFrameRate != -1) {
//            EMClient.getInstance().callManager().getCallOptions().setMaxVideoFrameRate(maxFrameRate);
//        }
//
//        // audio sample rate
//        int audioSampleRate = PreferenceManager.getInstance().getCallAudioSampleRate();
//        if (audioSampleRate != -1) {
//            EMClient.getInstance().callManager().getCallOptions().setAudioSampleRate(audioSampleRate);
//        }
//
//        /**
//         * This function is only meaningful when your app need recording
//         * If not, remove it.
//         * This function need be called before the video stream started, so we set it in onCreate function.
//         * This method will set the preferred video record encoding codec.
//         * Using default encoding format, recorded file may not be played by mobile player.
//         */
//        //EMClient.getInstance().callManager().getVideoCallHelper().setPreferMovFormatEnable(true);
//
//        // resolution
//        String resolution = PreferenceManager.getInstance().getCallBackCameraResolution();
//        if (resolution.equals("")) {
//            resolution = PreferenceManager.getInstance().getCallFrontCameraResolution();
//        }
//        String[] wh = resolution.split("x");
//        if (wh.length == 2) {
//            try {
//                EMClient.getInstance().callManager().getCallOptions().setVideoResolution(new Integer(wh[0]).intValue(), new Integer(wh[1]).intValue());
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//
//        // enabled fixed sample rate
//        boolean enableFixSampleRate = PreferenceManager.getInstance().isCallFixedVideoResolution();
//        EMClient.getInstance().callManager().getCallOptions().enableFixedVideoResolution(enableFixSampleRate);
//
//        // Offline call push
//        EMClient.getInstance().callManager().getCallOptions().setIsSendPushIfOffline(getModel().isPushCall());
    }

    protected void setEaseUIProviders() {
        //set user avatar to circle shape
        EaseAvatarOptions avatarOptions = new EaseAvatarOptions();
        avatarOptions.setAvatarShape(1);
        avatarOptions.setAvatarRadius(10);
        easeUI.setAvatarOptions(avatarOptions);

        // set profile provider if you want easeUI to handle avatar and nickname
        easeUI.setUserProfileProvider(new EaseUI.EaseUserProfileProvider() {

            @Override
            public EaseUser getUser(String username) {
                return getUserInfo(username);
            }
        });

        //set options 
        easeUI.setSettingsProvider(new EaseUI.EaseSettingsProvider() {

            @Override
            public boolean isSpeakerOpened() {
                return demoModel.getSettingMsgSpeaker();
            }

            @Override
            public boolean isMsgVibrateAllowed(EMMessage message) {
                EaseGroupInfo groupInfo=MyHelper.getInstance().getGroupById(message.getTo());
                if (groupInfo.getType()!=null&&groupInfo.getType().equals(Constant.GROUP_MSG_SET)){
                    return false;
                }
                if (message.getStringAttribute(Constant.MSGTYPE, "").equals(Constant.RAB)||
                        message.getStringAttribute(Constant.MSGTYPE, "").equals(Constant.WELFARE)||
                        message.getStringAttribute(Constant.MSGTYPE, "").equals(Constant.PUNISHMENT)||
                        message.getStringAttribute(Constant.MSGTYPE, "").equals(Constant.RABEND)  ||
                        message.getStringAttribute(Constant.MSGTYPE, "").equals(Constant.ADDUSER)||
                        message.getStringAttribute(Constant.MSGTYPE, "").equals(Constant.DELUSER)||
                        message.getStringAttribute(Constant.MSGTYPE, "").equals(Constant.SHOTUSER)||
                        message.getStringAttribute(Constant.MSGTYPE, "").equals(Constant.ADDUSERS)||
                        "系统管理员".equals(message.getFrom())) {
                    return false;
                }
//                if (message.getStringAttribute(Constant.MSGTYPE, "").equals(Constant.RAB)) {
//                    String currentUser = MyApplication.getInstance().getUserInfo().getUserId() + "";
//                    String loginName = message.getStringAttribute("id", "");//id
//                    String sendLoginName = message.getStringAttribute("sendid", "");//发红包的id
//                    if (currentUser.equals(loginName)||currentUser.equals(sendLoginName)){
//                        return true;
//                    }else {
//                        return false;
//                    }
//                }
                return demoModel.getSettingMsgVibrate();
            }

            @Override
            public boolean isMsgSoundAllowed(EMMessage message) {
                EaseGroupInfo groupInfo=MyHelper.getInstance().getGroupById(message.getTo());
                if (groupInfo.getType()!=null&&groupInfo.getType().equals(Constant.GROUP_MSG_SET)){
                    return false;
                }
                if (message.getStringAttribute(Constant.MSGTYPE, "").equals(Constant.RAB)||
                        message.getStringAttribute(Constant.MSGTYPE, "").equals(Constant.WELFARE)||
                        message.getStringAttribute(Constant.MSGTYPE, "").equals(Constant.PUNISHMENT)||
                        message.getStringAttribute(Constant.MSGTYPE, "").equals(Constant.RABEND)  ||
                        message.getStringAttribute(Constant.MSGTYPE, "").equals(Constant.ADDUSER)||
                        message.getStringAttribute(Constant.MSGTYPE, "").equals(Constant.DELUSER)||
                        message.getStringAttribute(Constant.MSGTYPE, "").equals(Constant.SHOTUSER)||
                        message.getStringAttribute(Constant.MSGTYPE, "").equals(Constant.ADDUSERS)||
                        "系统管理员".equals(message.getFrom())) {
                    return false;
                }
                return demoModel.getSettingMsgSound();
            }

            @Override
            public boolean isMsgNotifyAllowed(EMMessage message) {
                if (message == null) {
                    return demoModel.getSettingMsgNotification();
                }
                EaseGroupInfo groupInfo=MyHelper.getInstance().getGroupById(message.getTo());
                if (groupInfo.getType()!=null&&groupInfo.getType().equals(Constant.GROUP_MSG_SET)){
                    return false;
                }
                if (message.getStringAttribute(Constant.MSGTYPE, "").equals(Constant.RAB)||
                        message.getStringAttribute(Constant.MSGTYPE, "").equals(Constant.WELFARE)||
                        message.getStringAttribute(Constant.MSGTYPE, "").equals(Constant.PUNISHMENT)||
                        message.getStringAttribute(Constant.MSGTYPE, "").equals(Constant.RABEND)  ||
                        message.getStringAttribute(Constant.MSGTYPE, "").equals(Constant.ADDUSER)||
                        message.getStringAttribute(Constant.MSGTYPE, "").equals(Constant.DELUSER)||
                        message.getStringAttribute(Constant.MSGTYPE, "").equals(Constant.SHOTUSER)||
                        message.getStringAttribute(Constant.MSGTYPE, "").equals(Constant.ADDUSERS)||
                        "系统管理员".equals(message.getFrom())) {
                    return false;
                }

                if (!demoModel.getSettingMsgNotification()) {
                    return false;
                } else {
                    String chatUsename = null;
                    List<String> notNotifyIds = null;
                    // get user or group id which was blocked to show message notifications
                    if (message.getChatType() == ChatType.Chat) {
                        chatUsename = message.getFrom();
                        notNotifyIds = demoModel.getDisabledIds();
                    } else {
                        chatUsename = message.getTo();
                        notNotifyIds = demoModel.getDisabledGroups();
                    }

                    if (notNotifyIds == null || !notNotifyIds.contains(chatUsename)) {
                        return true;
                    } else {
                        return false;
                    }
                }
            }
        });
        //set emoji icon provider
        easeUI.setEmojiconInfoProvider(new EaseUI.EaseEmojiconInfoProvider() {

            @Override
            public EaseEmojicon getEmojiconInfo(String emojiconIdentityCode) {
                EaseEmojiconGroupEntity data = EmojiconExampleGroupData.getData();
                for (EaseEmojicon emojicon : data.getEmojiconList()) {
                    if (emojicon.getIdentityCode().equals(emojiconIdentityCode)) {
                        return emojicon;
                    }
                }
                return null;
            }

            @Override
            public Map<String, Object> getTextEmojiconMapping() {
                return null;
            }
        });

        //set notification options, will use default if you don't set it
        easeUI.getNotifier().setNotificationInfoProvider(new EaseNotifier.EaseNotificationInfoProvider() {

            @Override
            public String getTitle(EMMessage message) {
                //you can update title here
                return null;
            }

            @Override
            public int getSmallIcon(EMMessage message) {
                //you can update icon here
                return 0;
            }

            @Override
            public String getDisplayedText(EMMessage message) {
                // be used on notification bar, different text according the message type.
                String ticker = EaseCommonUtils.getMessageDigest(message, appContext);
                if (message.getType() == Type.TXT) {
                    ticker = ticker.replaceAll("\\[.{2,3}\\]", "[表情]");
                }
                EaseUser user = getUserInfo(message.getFrom());
                if (user != null) {
                    if (EaseAtMessageHelper.get().isAtMeMsg(message)) {
                        return String.format(appContext.getString(R.string.at_your_in_group), user.getNick());
                    }
                    return user.getNick() + ": " + ticker;
                } else {
                    if (EaseAtMessageHelper.get().isAtMeMsg(message)) {
                        return String.format(appContext.getString(R.string.at_your_in_group), message.getFrom());
                    }
                    return message.getFrom() + ": " + ticker;
                }
            }

            @Override
            public String getLatestText(EMMessage message, int fromUsersNum, int messageNum) {
                // here you can customize the text.
                // return fromUsersNum + "contacts send " + messageNum + "messages to you";
                return null;
            }

            @Override
            public Intent getLaunchIntent(EMMessage message) {
                // you can set what activity you want display when user click the notification
                Intent intent = new Intent(appContext, ChatActivity.class);
                // open calling activity if there is call
//                if (isVideoCalling) {
//                    intent = new Intent(appContext, VideoCallActivity.class);
//                } else if (isVoiceCalling) {
//                    intent = new Intent(appContext, VoiceCallActivity.class);
//                } else {
                ChatType chatType = message.getChatType();
                if (chatType == ChatType.Chat) { // single chat message
                    intent.putExtra("userId", message.getFrom());
                    intent.putExtra("chatType", Constant.CHATTYPE_SINGLE);
                } else { // group chat message
                    // message.getTo() is the group id
                    intent.putExtra("userId", message.getTo());
                    if (chatType == ChatType.GroupChat) {
                        intent.putExtra("chatType", Constant.CHATTYPE_GROUP);
                    } else {
                        intent.putExtra("chatType", Constant.CHATTYPE_CHATROOM);
                    }

                }
//                }
                return intent;
            }
        });
    }



    EMConnectionListener connectionListener;

    /**
     * set global listener
     */
    protected void setGlobalListeners() {
        syncGroupsListeners = new ArrayList<>();
        syncContactsListeners = new ArrayList<>();
        syncBlackListListeners = new ArrayList<>();

        isGroupsSyncedWithServer = demoModel.isGroupsSynced();
        isContactsSyncedWithServer = demoModel.isContactSynced();
        isBlackListSyncedWithServer = demoModel.isBacklistSynced();

        // create the global connection listener
        connectionListener = new EMConnectionListener() {
            @Override
            public void onDisconnected(int error) {
                EMLog.d("global listener", "onDisconnect" + error);
                if (error == EMError.USER_REMOVED) {
                    onUserException(Constant.ACCOUNT_REMOVED);
                } else if (error == EMError.USER_LOGIN_ANOTHER_DEVICE) {
                    onUserException(Constant.ACCOUNT_CONFLICT);
                } else if (error == EMError.SERVER_SERVICE_RESTRICTED) {
                    onUserException(Constant.ACCOUNT_FORBIDDEN);
                } else if (error == EMError.USER_KICKED_BY_CHANGE_PASSWORD) {
                    onUserException(Constant.ACCOUNT_KICKED_BY_CHANGE_PASSWORD);
                } else if (error == EMError.USER_KICKED_BY_OTHER_DEVICE) {
                    onUserException(Constant.ACCOUNT_KICKED_BY_OTHER_DEVICE);
                }
            }

            @Override
            public void onConnected() {
                // in case group and contact were already synced, we supposed to notify sdk we are ready to receive the events
                if (isGroupsSyncedWithServer && isContactsSyncedWithServer) {
                    EMLog.d(TAG, "group and contact already synced with servre");
                } else {
                    if (!isGroupsSyncedWithServer) {
                        asyncFetchGroupsFromServer(null);
                    }

                    if (!isContactsSyncedWithServer) {
                        asyncFetchContactsFromServer(null);
                    }

                    if (!isBlackListSyncedWithServer) {
                        asyncFetchBlackListFromServer(null);
                    }
                }
            }
        };

        //  IntentFilter callFilter = new IntentFilter(EMClient.getInstance().callManager().getIncomingCallBroadcastAction());
        if (callReceiver == null) {
            callReceiver = new CallReceiver();
        }
//        EMClient.getInstance().conferenceManager().addConferenceListener(new EMConferenceListener() {
//            @Override
//            public void onMemberJoined(String username) {
//                EMLog.i(TAG, String.format("member joined username: %s, member: %d", username,
//                        EMClient.getInstance().conferenceManager().getConferenceMemberList().size()));
//            }
//
//            @Override
//            public void onMemberExited(String username) {
//                EMLog.i(TAG, String.format("member exited username: %s, member size: %d", username,
//                        EMClient.getInstance().conferenceManager().getConferenceMemberList().size()));
//            }
//
//            @Override
//            public void onStreamAdded(EMConferenceStream stream) {
//                EMLog.i(TAG, String.format("Stream added streamId: %s, streamName: %s, memberName: %s, username: %s, extension: %s, videoOff: %b, mute: %b",
//                        stream.getStreamId(), stream.getStreamName(), stream.getMemberName(), stream.getUsername(),
//                        stream.getExtension(), stream.isVideoOff(), stream.isAudioOff()));
//                EMLog.i(TAG, String.format("Conference stream subscribable: %d, subscribed: %d",
//                        EMClient.getInstance().conferenceManager().getAvailableStreamMap().size(),
//                        EMClient.getInstance().conferenceManager().getSubscribedStreamMap().size()));
//            }
//
//            @Override
//            public void onStreamRemoved(EMConferenceStream stream) {
//                EMLog.i(TAG, String.format("Stream removed streamId: %s, streamName: %s, memberName: %s, username: %s, extension: %s, videoOff: %b, mute: %b",
//                        stream.getStreamId(), stream.getStreamName(), stream.getMemberName(), stream.getUsername(),
//                        stream.getExtension(), stream.isVideoOff(), stream.isAudioOff()));
//                EMLog.i(TAG, String.format("Conference stream subscribable: %d, subscribed: %d",
//                        EMClient.getInstance().conferenceManager().getAvailableStreamMap().size(),
//                        EMClient.getInstance().conferenceManager().getSubscribedStreamMap().size()));
//            }
//
//            @Override
//            public void onStreamUpdate(EMConferenceStream stream) {
//                EMLog.i(TAG, String.format("Stream added streamId: %s, streamName: %s, memberName: %s, username: %s, extension: %s, videoOff: %b, mute: %b",
//                        stream.getStreamId(), stream.getStreamName(), stream.getMemberName(), stream.getUsername(),
//                        stream.getExtension(), stream.isVideoOff(), stream.isAudioOff()));
//                EMLog.i(TAG, String.format("Conference stream subscribable: %d, subscribed: %d",
//                        EMClient.getInstance().conferenceManager().getAvailableStreamMap().size(),
//                        EMClient.getInstance().conferenceManager().getSubscribedStreamMap().size()));
//            }
//
//            @Override
//            public void onPassiveLeave(int error, String message) {
//                EMLog.i(TAG, String.format("passive leave code: %d, message: %s", error, message));
//            }
//
//            @Override
//            public void onConferenceState(ConferenceState state) {
//                EMLog.i(TAG, String.format("State code=%d", state.ordinal()));
//            }
//
//            @Override
//            public void onStreamSetup(String streamId) {
//                EMLog.i(TAG, String.format("Stream id - %s", streamId));
//            }
//
//            @Override
//            public void onReceiveInvite(String confId, String password, String extension) {
//                EMLog.i(TAG, String.format("Receive conference invite confId: %s, password: %s, extension: %s", confId, password, extension));
//                if (easeUI.getTopActivity().getClass().getSimpleName().equals("ConferenceActivity")) {
//                    return;
//                }
//                Intent conferenceIntent = new Intent(appContext, ConferenceActivity.class);
//                conferenceIntent.putExtra(Constant.EXTRA_CONFERENCE_ID, confId);
//                conferenceIntent.putExtra(Constant.EXTRA_CONFERENCE_PASS, password);
//                conferenceIntent.putExtra(Constant.EXTRA_CONFERENCE_IS_CREATOR, false);
//                conferenceIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                appContext.startActivity(conferenceIntent);
//            }
//        });
        //register incoming call receiver
        // appContext.registerReceiver(callReceiver, callFilter);
        //register connection listener
        EMClient.getInstance().addConnectionListener(connectionListener);
        //register group and contact event listener
        registerGroupAndContactListener();
        //register message event listener
        registerMessageListener();

    }

    private void initDbDao() {
        inviteMessgeDao = new InviteMessgeDao(appContext);
        userDao = new UserDao(appContext);
    }

    /**
     * register group and contact listener, you need register when login
     */
    public void registerGroupAndContactListener() {
        if (!isGroupAndContactListenerRegisted) {
            EMClient.getInstance().groupManager().addGroupChangeListener(new MyGroupChangeListener());
            EMClient.getInstance().contactManager().setContactListener(new MyContactListener());
            EMClient.getInstance().addMultiDeviceListener(new MyMultiDeviceListener());
            isGroupAndContactListenerRegisted = true;
        }

    }

    /**
     * group change listener
     */
    class MyGroupChangeListener implements EMGroupChangeListener {

        @Override
        public void onInvitationReceived(String groupId, String groupName, String inviter, String reason) {

            new InviteMessgeDao(appContext).deleteMessage(groupId);

            // user invite you to join group
            InviteMessage msg = new InviteMessage();
            msg.setFrom(groupId);
            msg.setTime(System.currentTimeMillis());
            msg.setGroupId(groupId);
            msg.setGroupName(groupName);
            msg.setReason(reason);
            msg.setGroupInviter(inviter);
            //   showToast("receive invitation to join the group：" + groupName);
            msg.setStatus(InviteMessageStatus.GROUPINVITATION);
            notifyNewInviteMessage(msg);
            broadcastManager.sendBroadcast(new Intent(Constant.ACTION_GROUP_CHANAGED));
        }

        @Override
        public void onInvitationAccepted(String groupId, String invitee, String reason) {
            new InviteMessgeDao(appContext).deleteMessage(groupId);
            //user accept your invitation
            boolean hasGroup = false;
            EMGroup _group = null;
            for (EMGroup group : EMClient.getInstance().groupManager().getAllGroups()) {
                if (group.getGroupId().equals(groupId)) {
                    hasGroup = true;
                    _group = group;
                    break;
                }
            }
            if (!hasGroup) {
                return;
            }
            InviteMessage msg = new InviteMessage();
            msg.setFrom(groupId);
            msg.setTime(System.currentTimeMillis());
            msg.setGroupId(groupId);
            msg.setGroupName(_group == null ? groupId : _group.getGroupName());
            msg.setReason(reason);
            msg.setGroupInviter(invitee);
            //showToast(invitee + "Accept to join the group：" + _group == null ? groupId : _group.getGroupName());
            msg.setStatus(InviteMessageStatus.GROUPINVITATION_ACCEPTED);
            notifyNewInviteMessage(msg);
            broadcastManager.sendBroadcast(new Intent(Constant.ACTION_GROUP_CHANAGED));
        }

        @Override
        public void onInvitationDeclined(String groupId, String invitee, String reason) {

            new InviteMessgeDao(appContext).deleteMessage(groupId);

            //user declined your invitation
            EMGroup group = null;
            for (EMGroup _group : EMClient.getInstance().groupManager().getAllGroups()) {
                if (_group.getGroupId().equals(groupId)) {
                    group = _group;
                    break;
                }
            }
            if (group == null) {
                return;
            }
            InviteMessage msg = new InviteMessage();
            msg.setFrom(groupId);
            msg.setTime(System.currentTimeMillis());
            msg.setGroupId(groupId);
            msg.setGroupName(group.getGroupName());
            msg.setReason(reason);
            msg.setGroupInviter(invitee);
            showToast(invitee + "Declined to join the group：" + group.getGroupName());
            msg.setStatus(InviteMessageStatus.GROUPINVITATION_DECLINED);
            notifyNewInviteMessage(msg);
            broadcastManager.sendBroadcast(new Intent(Constant.ACTION_GROUP_CHANAGED));
        }

        @Override
        public void onUserRemoved(String groupId, String groupName) {
            //user is removed from group
            broadcastManager.sendBroadcast(new Intent(Constant.ACTION_GROUP_CHANAGED));
            // showToast("current user removed, groupId:" + groupId);
        }

        @Override
        public void onGroupDestroyed(String groupId, String groupName) {
            // group is dismissed,
            broadcastManager.sendBroadcast(new Intent(Constant.ACTION_GROUP_CHANAGED));
            //  showToast("group destroyed, groupId:" + groupId);
        }

        @Override
        public void onRequestToJoinReceived(String groupId, String groupName, String applyer, String reason) {

            // user apply to join group
            InviteMessage msg = new InviteMessage();
            msg.setFrom(applyer);
            msg.setTime(System.currentTimeMillis());
            msg.setGroupId(groupId);
            msg.setGroupName(groupName);
            msg.setReason(reason);
            showToast(applyer + " ApplyActivity to join group：" + groupId);
            msg.setStatus(InviteMessageStatus.BEAPPLYED);
            notifyNewInviteMessage(msg);
            broadcastManager.sendBroadcast(new Intent(Constant.ACTION_GROUP_CHANAGED));
        }

        @Override
        public void onRequestToJoinAccepted(String groupId, String groupName, String accepter) {

            String st4 = appContext.getString(R.string.Agreed_to_your_group_chat_application);
            // your application was accepted
            EMMessage msg = EMMessage.createReceiveMessage(Type.TXT);
            msg.setChatType(ChatType.GroupChat);
            msg.setFrom(accepter);
            msg.setTo(groupId);
            msg.setMsgId(UUID.randomUUID().toString());
            msg.addBody(new EMTextMessageBody(accepter + " " + st4));
            msg.setStatus(Status.SUCCESS);
            // save accept message
            EMClient.getInstance().chatManager().saveMessage(msg);
            // notify the accept message
            getNotifier().vibrateAndPlayTone(msg);

            //  showToast("request to join accepted, groupId:" + groupId);
            broadcastManager.sendBroadcast(new Intent(Constant.ACTION_GROUP_CHANAGED));
        }

        @Override
        public void onRequestToJoinDeclined(String groupId, String groupName, String decliner, String reason) {
            // your application was declined, we do nothing here in demo
            //  showToast("request to join declined, groupId:" + groupId);
        }

        @Override
        public void onAutoAcceptInvitationFromGroup(String groupId, String inviter, String inviteMessage) {
            // got an invitation
            String st3 = appContext.getString(R.string.Invite_you_to_join_a_group_chat);
            EMMessage msg = EMMessage.createReceiveMessage(Type.TXT);
            msg.setChatType(ChatType.GroupChat);
            msg.setFrom(inviter);
            msg.setTo(groupId);
            msg.setMsgId(UUID.randomUUID().toString());
            msg.addBody(new EMTextMessageBody(inviter + " " + st3));
            msg.setStatus(Status.SUCCESS);
            // save invitation as messages
            EMClient.getInstance().chatManager().saveMessage(msg);
            // notify invitation message
            getNotifier().vibrateAndPlayTone(msg);
            // showToast("auto accept invitation from groupId:" + groupId);
            broadcastManager.sendBroadcast(new Intent(Constant.ACTION_GROUP_CHANAGED));
        }

        // ============================= group_reform new add api begin
        @Override
        public void onMuteListAdded(String groupId, final List<String> mutes, final long muteExpire) {
            StringBuilder sb = new StringBuilder();
            for (String member : mutes) {
                sb.append(member).append(",");
            }
            // showToast("onMuterListAdded: " + sb.toString());
        }


        @Override
        public void onMuteListRemoved(String groupId, final List<String> mutes) {
            StringBuilder sb = new StringBuilder();
            for (String member : mutes) {
                sb.append(member).append(",");
            }
            //  showToast("onMuterListRemoved: " + sb.toString());
        }


        @Override
        public void onAdminAdded(String groupId, String administrator) {
            //   showToast("onAdminAdded: " + administrator);
        }

        @Override
        public void onAdminRemoved(String groupId, String administrator) {
            //  showToast("onAdminRemoved: " + administrator);
        }

        @Override
        public void onOwnerChanged(String groupId, String newOwner, String oldOwner) {
            //  showToast("onOwnerChanged new:" + newOwner + " old:" + oldOwner);
        }

        @Override
        public void onMemberJoined(String groupId, String member) {
            // showToast("onMemberJoined: " + member);
        }

        @Override
        public void onMemberExited(String groupId, String member) {
            //  showToast("onMemberExited: " + member);
        }

        @Override
        public void onAnnouncementChanged(String groupId, String announcement) {
            //   showToast("onAnnouncementChanged, groupId" + groupId);
        }

        @Override
        public void onSharedFileAdded(String groupId, EMMucSharedFile sharedFile) {
            // showToast("onSharedFileAdded, groupId" + groupId);
        }

        @Override
        public void onSharedFileDeleted(String groupId, String fileId) {
            //  showToast("onSharedFileDeleted, groupId" + groupId);
        }
        // ============================= group_reform new add api end
    }

    void showToast(final String message) {
        Log.d(TAG, "receive invitation to join the group：" + message);
        if (handler != null) {
            Message msg = Message.obtain(handler, 0, message);
            handler.sendMessage(msg);
        } else {
            msgQueue.add(message);
        }
    }

    public void initHandler(Looper looper) {
        handler = new android.os.Handler(looper) {
            @Override
            public void handleMessage(Message msg) {
                String str = (String) msg.obj;
                Toast.makeText(appContext, str, Toast.LENGTH_LONG).show();
            }
        };
        while (!msgQueue.isEmpty()) {
            showToast(msgQueue.remove());
        }
    }

    /***
     * 好友变化listener
     *
     */
    public class MyContactListener implements EMContactListener {

        @Override
        public void onContactAdded(String username) {
            // save contact
            final Map<String, EaseUser> localUsers = getContactList();
            final Map<String, EaseUser> toAddUsers = new HashMap<String, EaseUser>();
            final EaseUser user = new EaseUser(username);

            if (!localUsers.containsKey(username)) {
                userDao.saveContact(user);
            }
            toAddUsers.put(username, user);
            localUsers.putAll(toAddUsers);
            List<String> list = new ArrayList<>();
            list.add(username);
            getUserProfileManager().asyncFetchContactInfosFromServer(list, new EMValueCallBack<List<EaseUser>>() {

                @Override
                public void onSuccess(List<EaseUser> uList) {
                    if (uList != null && uList.size() > 0) {
                        userDao.saveContact(uList.get(0));
                        userDao.saveuser(uList.get(0));
                        getContactList().put(uList.get(0).getUsername(), uList.get(0));
                        broadcastManager.sendBroadcast(new Intent(Constant.ACTION_CONTACT_CHANAGED));
                    }
                }

                @Override
                public void onError(int error, String errorMsg) {
                }
            });
            // showToast("onContactAdded:" + username);
        }

        @Override
        public void onContactDeleted(String username) {
            Map<String, EaseUser> localUsers = MyHelper.getInstance().getContactList();
            localUsers.remove(username);
            userDao.deleteContact(username);
            inviteMessgeDao.deleteMessage(username);

            EMClient.getInstance().chatManager().deleteConversation(username, false);

            broadcastManager.sendBroadcast(new Intent(Constant.ACTION_CONTACT_CHANAGED));
            // showToast("onContactDeleted:" + username);
        }

        @Override
        public void onContactInvited(String username, String reason) {
            List<InviteMessage> msgs = inviteMessgeDao.getMessagesList();

            for (InviteMessage inviteMessage : msgs) {
                if (inviteMessage.getGroupId() == null && inviteMessage.getFrom().equals(username)) {
                    inviteMessgeDao.deleteMessage(username);
                }
            }
            // save invitation as message
            InviteMessage msg = new InviteMessage();
            msg.setFrom(username);
            msg.setTime(System.currentTimeMillis());
            msg.setReason(reason);
            List<String> list = new ArrayList<>();
            list.add(username);
            getUserProfileManager().loadFriend(list);
            //showToast(username + "apply to be your friend,reason: " + reason);
            // set invitation status
            msg.setStatus(InviteMessageStatus.BEINVITEED);
            notifyNewInviteMessage(msg);
            broadcastManager.sendBroadcast(new Intent(Constant.ACTION_CONTACT_CHANAGED));
        }

        @Override
        public void onFriendRequestAccepted(String username) {
            List<InviteMessage> msgs = inviteMessgeDao.getMessagesList();
            for (InviteMessage inviteMessage : msgs) {
                if (inviteMessage.getFrom().equals(username)) {
                    return;
                }
            }
            // save invitation as message
            InviteMessage msg = new InviteMessage();
            msg.setFrom(username);
            msg.setTime(System.currentTimeMillis());
            // showToast(username + " accept your to be friend");
            msg.setStatus(InviteMessageStatus.BEAGREED);
            notifyNewInviteMessage(msg);
            broadcastManager.sendBroadcast(new Intent(Constant.ACTION_CONTACT_CHANAGED));
        }

        @Override
        public void onFriendRequestDeclined(String username) {
            // your request was refused
            //  showToast(username + " refused to be your friend");
        }
    }

    public class MyMultiDeviceListener implements EMMultiDeviceListener {

        @Override
        public void onContactEvent(int event, String target, String ext) {
            switch (event) {
                case EMMultiDeviceListener.CONTACT_REMOVE: {
                    Map<String, EaseUser> localUsers = getContactList();
                    localUsers.remove(target);
                    userDao.deleteContact(target);
                    inviteMessgeDao.deleteMessage(target);

                    EMClient.getInstance().chatManager().deleteConversation(username, false);
                    broadcastManager.sendBroadcast(new Intent(Constant.ACTION_CONTACT_CHANAGED));
                    //  showToast("CONTACT_REMOVE");
                }
                break;
                case EMMultiDeviceListener.CONTACT_ACCEPT: {
                    Map<String, EaseUser> localUsers = getContactList();
                    EaseUser user = new EaseUser(target);
                    if (!localUsers.containsKey(target)) {
                        userDao.saveContact(user);
                    }
                    localUsers.put(target, user);
                    updateContactNotificationStatus(target, "", InviteMessageStatus.MULTI_DEVICE_CONTACT_ACCEPT);
                    broadcastManager.sendBroadcast(new Intent(Constant.ACTION_CONTACT_CHANAGED));
                    //  showToast("CONTACT_ACCEPT");
                }
                break;
                case EMMultiDeviceListener.CONTACT_DECLINE:
                    updateContactNotificationStatus(target, "", InviteMessageStatus.MULTI_DEVICE_CONTACT_DECLINE);
                    showToast("CONTACT_DECLINE");
                    break;
//                case CONTACT_ADD:
//                    updateContactNotificationStatus(target, "", InviteMessageStatus.MULTI_DEVICE_CONTACT_ADD);
//                    showToast("CONTACT_ADD");
//                break;
                case CONTACT_BAN:
                    updateContactNotificationStatus(target, "", InviteMessageStatus.MULTI_DEVICE_CONTACT_BAN);
                    //  showToast("CONTACT_BAN");

                    Map<String, EaseUser> localUsers = MyHelper.getInstance().getContactList();
                    localUsers.remove(username);
                    userDao.deleteContact(username);
                    inviteMessgeDao.deleteMessage(username);
                    EMClient.getInstance().chatManager().deleteConversation(username, false);
                    broadcastManager.sendBroadcast(new Intent(Constant.ACTION_CONTACT_CHANAGED));
                    break;
                case CONTACT_ALLOW:
                    updateContactNotificationStatus(target, "", InviteMessageStatus.MULTI_DEVICE_CONTACT_ALLOW);
                    //  showToast("CONTACT_ALLOW");
                    break;
                default:
                    break;
            }
        }

        private void updateContactNotificationStatus(String from, String reason, InviteMessageStatus status) {
            InviteMessage msg = null;
            for (InviteMessage _msg : inviteMessgeDao.getMessagesList()) {
                if (_msg.getFrom().equals(from)) {
                    msg = _msg;
                    break;
                }
            }
            if (msg != null) {
                ContentValues values = new ContentValues();
                msg.setStatus(status);
                values.put(InviteMessgeDao.COLUMN_NAME_STATUS, msg.getStatus().ordinal());
                inviteMessgeDao.updateMessage(msg.getId(), values);
            } else {
                // save invitation as message
                msg = new InviteMessage();
                msg.setFrom(username);
                msg.setTime(System.currentTimeMillis());
                msg.setReason(reason);
                msg.setStatus(status);
                notifyNewInviteMessage(msg);
            }
        }

        @Override
        public void onGroupEvent(final int event, final String target, final List<String> usernames) {
            execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        String groupId = target;
                        switch (event) {
                            case GROUP_CREATE:
                                // showToast("GROUP_CREATE");
                                saveGroupNotification(groupId, /*groupName*/"",  /*person*/"", /*reason*/"", InviteMessageStatus.MULTI_DEVICE_GROUP_CREATE);
                                break;
                            case GROUP_DESTROY:
                                //  showToast("GROUP_DESTROY");
                                inviteMessgeDao.deleteGroupMessage(groupId);
                                saveGroupNotification(groupId, /*groupName*/"",  /*person*/"", /*reason*/"", InviteMessageStatus.MULTI_DEVICE_GROUP_DESTROY);
                                broadcastManager.sendBroadcast(new Intent(Constant.ACTION_GROUP_CHANAGED));
                                break;
                            case GROUP_JOIN:
                                //   showToast("GROUP_JOIN");
                                broadcastManager.sendBroadcast(new Intent(Constant.ACTION_GROUP_CHANAGED));
                                saveGroupNotification(groupId, /*groupName*/"",  /*person*/"", /*reason*/"", InviteMessageStatus.MULTI_DEVICE_GROUP_JOIN);
                                break;
                            case GROUP_LEAVE:
                                //   showToast("GROUP_LEAVE");
                                inviteMessgeDao.deleteGroupMessage(groupId);
                                saveGroupNotification(groupId, /*groupName*/"",  /*person*/"", /*reason*/"", InviteMessageStatus.MULTI_DEVICE_GROUP_LEAVE);
                                broadcastManager.sendBroadcast(new Intent(Constant.ACTION_GROUP_CHANAGED));
                                break;
                            case GROUP_APPLY:
                                //  showToast("GROUP_APPLY");
                                inviteMessgeDao.deleteGroupMessage(groupId);
                                saveGroupNotification(groupId, /*groupName*/"",  /*person*/"", /*reason*/"", InviteMessageStatus.MULTI_DEVICE_GROUP_APPLY);
                                break;
                            case GROUP_APPLY_ACCEPT:
                                //  showToast("GROUP_ACCEPT");
                                inviteMessgeDao.deleteGroupMessage(groupId, usernames.get(0));
                                // TODO: person, reason from ext
                                saveGroupNotification(groupId, /*groupName*/"",  /*person*/usernames.get(0), /*reason*/"", InviteMessageStatus.MULTI_DEVICE_GROUP_APPLY_ACCEPT);
                                break;
                            case GROUP_APPLY_DECLINE:
                                //   showToast("GROUP_APPLY_DECLINE");
                                inviteMessgeDao.deleteGroupMessage(groupId, usernames.get(0));
                                // TODO: person, reason from ext
                                saveGroupNotification(groupId, /*groupName*/"",  /*person*/usernames.get(0), /*reason*/"", InviteMessageStatus.MULTI_DEVICE_GROUP_APPLY_DECLINE);
                                break;
                            case GROUP_INVITE:
                                //    showToast("GROUP_INVITE");
                                // TODO: person, reason from ext
                                saveGroupNotification(groupId, /*groupName*/"",  /*person*/usernames.get(0), /*reason*/"", InviteMessageStatus.MULTI_DEVICE_GROUP_INVITE);
                                break;
                            case GROUP_INVITE_ACCEPT:
                                showToast("GROUP_INVITE_ACCEPT");
                                String st3 = appContext.getString(R.string.Invite_you_to_join_a_group_chat);
                                EMMessage msg = EMMessage.createReceiveMessage(Type.TXT);
                                msg.setChatType(ChatType.GroupChat);
                                // TODO: person, reason from ext
                                String from = "";
                                if (usernames != null && usernames.size() > 0) {
                                    msg.setFrom(usernames.get(0));
                                }
                                msg.setTo(groupId);
                                msg.setMsgId(UUID.randomUUID().toString());
                                msg.addBody(new EMTextMessageBody(msg.getFrom() + " " + st3));
                                msg.setStatus(Status.SUCCESS);
                                // save invitation as messages
                                EMClient.getInstance().chatManager().saveMessage(msg);

                                inviteMessgeDao.deleteMessage(groupId);
                                // TODO: person, reason from ext
                                saveGroupNotification(groupId, /*groupName*/"",  /*person*/"", /*reason*/"", InviteMessageStatus.MULTI_DEVICE_GROUP_INVITE_ACCEPT);
                                broadcastManager.sendBroadcast(new Intent(Constant.ACTION_GROUP_CHANAGED));
                                break;
                            case GROUP_INVITE_DECLINE:
                                //   showToast("GROUP_INVITE_DECLINE");
                                inviteMessgeDao.deleteMessage(groupId);
                                // TODO: person, reason from ext
                                saveGroupNotification(groupId, /*groupName*/"",  /*person*/usernames.get(0), /*reason*/"", InviteMessageStatus.MULTI_DEVICE_GROUP_INVITE_DECLINE);
                                break;
                            case GROUP_KICK:
                                //showToast("GROUP_KICK");
                                //  // TODO: person, reason from ext
                                saveGroupNotification(groupId, /*groupName*/"",  /*person*/usernames.get(0), /*reason*/"", InviteMessageStatus.MULTI_DEVICE_GROUP_INVITE_DECLINE);
                                break;
                            case GROUP_BAN:
                                //   showToast("GROUP_BAN");
                                // TODO: person from ext
                                saveGroupNotification(groupId, /*groupName*/"",  /*person*/usernames.get(0), /*reason*/"", InviteMessageStatus.MULTI_DEVICE_GROUP_BAN);
                                break;
                            case GROUP_ALLOW:
                                showToast("GROUP_ALLOW");
                                // TODO: person from ext
                                saveGroupNotification(groupId, /*groupName*/"",  /*person*/usernames.get(0), /*reason*/"", InviteMessageStatus.MULTI_DEVICE_GROUP_ALLOW);
                                break;
                            case GROUP_BLOCK:
                                // showToast("GROUP_BLOCK");
                                saveGroupNotification(groupId, /*groupName*/"",  /*person*/"", /*reason*/"", InviteMessageStatus.MULTI_DEVICE_GROUP_BLOCK);
                                break;
                            case GROUP_UNBLOCK:
                                //   showToast("GROUP_UNBLOCK");
                                // TODO: person from ext
                                saveGroupNotification(groupId, /*groupName*/"",  /*person*/"", /*reason*/"", InviteMessageStatus.MULTI_DEVICE_GROUP_UNBLOCK);
                                break;
                            case GROUP_ASSIGN_OWNER:
                                //   showToast("GROUP_ASSIGN_OWNER");
                                // TODO: person from ext
                                saveGroupNotification(groupId, /*groupName*/"",  /*person*/usernames.get(0), /*reason*/"", InviteMessageStatus.MULTI_DEVICE_GROUP_ASSIGN_OWNER);
                                break;
                            case GROUP_ADD_ADMIN:
                                //    showToast("GROUP_ADD_ADMIN");
                                // TODO: person from ext
                                saveGroupNotification(groupId, /*groupName*/"",  /*person*/usernames.get(0), /*reason*/"", InviteMessageStatus.MULTI_DEVICE_GROUP_ADD_ADMIN);
                                break;
                            case GROUP_REMOVE_ADMIN:
                                //     showToast("GROUP_REMOVE_ADMIN");
                                // TODO: person from ext
                                saveGroupNotification(groupId, /*groupName*/"",  /*person*/usernames.get(0), /*reason*/"", InviteMessageStatus.MULTI_DEVICE_GROUP_REMOVE_ADMIN);
                                break;
                            case GROUP_ADD_MUTE:
                                //   showToast("GROUP_ADD_MUTE");
                                // TODO: person from ext
                                saveGroupNotification(groupId, /*groupName*/"",  /*person*/usernames.get(0), /*reason*/"", InviteMessageStatus.MULTI_DEVICE_GROUP_ADD_MUTE);
                                break;
                            case GROUP_REMOVE_MUTE:
                                //   showToast("GROUP_REMOVE_MUTE");
                                // TODO: person from ext
                                saveGroupNotification(groupId, /*groupName*/"",  /*person*/usernames.get(0), /*reason*/"", InviteMessageStatus.MULTI_DEVICE_GROUP_REMOVE_MUTE);
                                break;
                            default:
                                break;
                        }

                        if (false) { // keep the try catch structure
                            throw new HyphenateException("");
                        }
                    } catch (HyphenateException e) {
                        e.printStackTrace();
                    }

                }
            });
        }

        private void saveGroupNotification(String groupId, String groupName, String inviter, String reason, InviteMessageStatus status) {
            InviteMessage msg = new InviteMessage();
            msg.setFrom(groupId);
            msg.setTime(System.currentTimeMillis());
            msg.setGroupId(groupId);
            msg.setGroupName(groupName);
            msg.setReason(reason);
            msg.setGroupInviter(inviter);
            Log.d(TAG, "receive invitation to join the group：" + groupName);
            msg.setStatus(status);
            notifyNewInviteMessage(msg);
        }

        private void updateGroupNotificationStatus(String groupId, String groupName, String inviter, String reason, InviteMessageStatus status) {
            InviteMessage msg = null;
            for (InviteMessage _msg : inviteMessgeDao.getMessagesList()) {
                if (_msg.getGroupId().equals(groupId)) {
                    msg = _msg;
                    break;
                }
            }
            if (msg != null) {
                ContentValues values = new ContentValues();
                msg.setStatus(status);
                values.put(InviteMessgeDao.COLUMN_NAME_STATUS, msg.getStatus().ordinal());
                inviteMessgeDao.updateMessage(msg.getId(), values);
            }
        }
    }

    /**
     * save and notify invitation message
     *
     * @param msg
     */
    private void notifyNewInviteMessage(InviteMessage msg) {
        if (inviteMessgeDao == null) {
            inviteMessgeDao = new InviteMessgeDao(appContext);
        }
        inviteMessgeDao.saveMessage(msg);
        //increase the unread message count
        inviteMessgeDao.saveUnreadMessageCount(1);
        // notify there is new message
        getNotifier().vibrateAndPlayTone(null);
    }

    /**
     * user met some exception: conflict, removed or forbidden
     */
    protected void onUserException(String exception) {
        EMLog.e(TAG, "onUserException: " + exception);
        Intent intent = new Intent(appContext, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        intent.putExtra(exception, true);
        appContext.startActivity(intent);
        //  showToast(exception);
    }

    private EaseUser getUserInfo(String username) {
        // To get instance of EaseUser, here we get it from the user list in memory
        // You'd better cache it if you get it from your server
        EaseUser user = null;
        if (username.equals(EMClient.getInstance().getCurrentUser())) {
            return getUserProfileManager().getCurrentUserInfo();
        }
//        user = getContactList().get(username);

        if (user == null) {
            user = getUserById(username);
        }

        // if user is not in your contacts, set inital letter for him/her
        if (user == null) {
            user = new EaseUser(username);
            EaseCommonUtils.setUserInitialLetter(user);
        }
        return user;
    }

    /**
     * Global listener
     * If this event already handled by an activity, you don't need handle it again
     * activityList.size() <= 0 means all activities already in background or not in Activity Stack
     */
    protected void registerMessageListener() {
        messageListener = new EMMessageListener() {
            private BroadcastReceiver broadCastReceiver = null;

            @Override
            public void onMessageReceived(List<EMMessage> messages) {
                MyApplication.getInstance().saveUserHeadNick(messages);

                for (EMMessage message : messages) {
                    EaseCommonUtils.initMessage(message);
                    EMLog.d(TAG, "onMessageReceived id : " + message.getMsgId());
                    // in background, do not refresh UI, notify it in notification bar
                    if (!easeUI.hasForegroundActivies()) {
                        getNotifier().onNewMsg(message);
                    }
                }
            }

            @Override
            public void onCmdMessageReceived(List<EMMessage> messages) {
                for (EMMessage emMessage : messages) {
                    if (emMessage.getStringAttribute(Constant.MSGTYPE, "").equals(Constant.RAB)){
                        String currentUser = MyApplication.getInstance().getUserInfo().getUserId() + "";
                        String loginName = emMessage.getStringAttribute("id", "");//id
                        String sendLoginName = emMessage.getStringAttribute("sendid", "");//发红包的id
                        if (!loginName.equals(currentUser)&&!sendLoginName.equals(currentUser)) {
                            continue;
                        }
                    }
                    EaseCommonUtils.initMessage(emMessage);
                    EMMessage msg = EMMessage.createTxtSendMessage(emMessage.getStringAttribute("message",""), emMessage.getTo());
                    msg.setChatType(EMMessage.ChatType.GroupChat);
                    msg.setFrom(emMessage.getFrom());
                    msg.setTo(emMessage.getTo());
                    msg.setMsgId(emMessage.getMsgId());
                    msg.setMsgTime(emMessage.getMsgTime());
                    msg.setDirection(EMMessage.Direct.RECEIVE);
                    msg.setUnread(false);
                    msg.setAttribute("cmd",true);
                    for (String key : emMessage.ext().keySet()) {
                        if (emMessage.ext().get(key) instanceof Integer) {
                            try {
                                msg.setAttribute(key, Integer.valueOf((Integer) emMessage.ext().get(key)));
                            } catch (Exception e) {
                            }
                        } else if (emMessage.ext().get(key) instanceof String) {
                            try {
                                msg.setAttribute(key, emMessage.ext().get(key).toString());
                            } catch (Exception e) {
                            }
                        } else if (emMessage.ext().get(key) instanceof Boolean) {
                            try {
                                msg.setAttribute(key, emMessage.ext().get(key).toString());
                            } catch (Exception e) {
                            }
                        } else if (emMessage.ext().get(key) instanceof Boolean) {
                            try {
                                msg.setAttribute(key, (Boolean) emMessage.ext().get(key));
                            } catch (Exception e) {
                            }
                        } else if (emMessage.ext().get(key) instanceof Long) {
                            try {
                                msg.setAttribute(key, (Long) emMessage.ext().get(key));
                            } catch (Exception e) {
                            }
                        }
                    }
                    //保存消息
                    EMClient.getInstance().chatManager().saveMessage(msg);
                }
            }

            @Override
            public void onMessageRead(List<EMMessage> messages) {
            }

            @Override
            public void onMessageDelivered(List<EMMessage> message) {
            }

            @Override
            public void onMessageRecalled(List<EMMessage> messages) {
                for (EMMessage msg : messages) {
                    if (msg.getChatType() == ChatType.GroupChat && EaseAtMessageHelper.get().isAtMeMsg(msg)) {
                        EaseAtMessageHelper.get().removeAtMeGroup(msg.getTo());
                    }
                    EMMessage msgNotification = EMMessage.createReceiveMessage(Type.TXT);
                    EMTextMessageBody txtBody = new EMTextMessageBody(String.format(appContext.getString(R.string.msg_recall_by_user), msg.getFrom()));
                    msgNotification.addBody(txtBody);
                    msgNotification.setFrom(msg.getFrom());
                    msgNotification.setTo(msg.getTo());
                    msgNotification.setUnread(false);
                    msgNotification.setMsgTime(msg.getMsgTime());
                    msgNotification.setLocalTime(msg.getMsgTime());
                    msgNotification.setChatType(msg.getChatType());
                    msgNotification.setAttribute(Constant.MESSAGE_TYPE_RECALL, true);
                    EMClient.getInstance().chatManager().saveMessage(msgNotification);
                }
            }

            @Override
            public void onMessageChanged(EMMessage message, Object change) {
                EMLog.d(TAG, "change:");
                EMLog.d(TAG, "change:" + change);
            }
        };

        EMClient.getInstance().chatManager().addMessageListener(messageListener);
    }

    /**
     * if ever logged in
     *
     * @return
     */
    public boolean isLoggedIn() {
        return EMClient.getInstance().isLoggedInBefore();
    }

    /**
     * logout
     *
     * @param unbindDeviceToken whether you need unbind your device token
     * @param callback          callback
     */
    public void logout(boolean unbindDeviceToken, final EMCallBack callback) {
        endCall();
        Log.d(TAG, "logout: " + unbindDeviceToken);
        EMClient.getInstance().logout(unbindDeviceToken, new EMCallBack() {

            @Override
            public void onSuccess() {
                Log.d(TAG, "logout: onSuccess");
                reset();
                if (callback != null) {
                    callback.onSuccess();
                }

            }

            @Override
            public void onProgress(int progress, String status) {
                if (callback != null) {
                    callback.onProgress(progress, status);
                }
            }

            @Override
            public void onError(int code, String error) {
                Log.d(TAG, "logout: onSuccess");
                reset();
                if (callback != null) {
                    callback.onError(code, error);
                }
            }
        });
    }

    /**
     * get instance of EaseNotifier
     *
     * @return
     */
    public EaseNotifier getNotifier() {
        return easeUI.getNotifier();
    }

    public MyModel getModel() {
        return (MyModel) demoModel;
    }

    /**
     * update contact list
     *
     * @param aContactList
     */
    public void setContactList(Map<String, EaseUser> aContactList) {
        if (aContactList == null) {
            if (contactList != null) {
                contactList.clear();
            }
            return;
        }

        contactList = aContactList;
    }

    /**
     * save user list
     *
     * @param list
     * @return
     */
    public boolean saveUserList(List<EaseUser> list) {
        return demoModel.saveUserList(list);
    }

    /**
     * save single contact
     */
    public void saveContact(EaseUser user) {
        contactList.put(user.getUsername(), user);
        demoModel.saveContact(user);
    }

    /**
     * save single user
     */
    public void saveUser(EaseUser user) {
        demoModel.saveuser(user);
    }

    /**
     * delect User
     */
    public void delectUser(String id) {
        demoModel.delectUser(id);
    }

    /**
     * get USer
     */
    public EaseUser getUserById(String id) {
        return demoModel.getUser(id);
    }

    /**
     * save group
     */
    public void saveGroup(EaseGroupInfo user) {
        demoModel.saveGroup(user);
    }

    /**
     * delect group
     */
    public void delectGroup(String id) {
        demoModel.delectGroup(id);
    }

    /**
     * get group
     */
    public EaseGroupInfo getGroupById(String id) {
        return demoModel.getGroup(id);
    }
    /**
     * get group
     */
    public void getGroupReset() {
        Map<String,EaseGroupInfo> map= getGroupList();
       for (String gruopId :map.keySet()){
           EaseGroupInfo groupInfo= map.get(gruopId);
           groupInfo.setType(Constant.GROUP_MSG_SET_NO);
           saveGroup(groupInfo);
       }
    }

    /**
     * get group list
     * @return
     */
    public Map<String,EaseGroupInfo> getGroupList(){
        return demoModel.getGroupList();
    }

    /**
     * 获取消息已读状态
     *
     * @param id
     * @return
     */
    public MsgStateInfo getMsgStateInfoById(String id) {
        return demoModel.getMsgStateInfoById(id);
    }

    /**
     * 保存消息已读状态
     *
     * @param msgStateInfo
     */
    public void saveMsgStateInfo(MsgStateInfo msgStateInfo) {
        demoModel.saveMsgStateInfo(msgStateInfo);
    }

    /**
     * 获取审核消息已读状态
     *
     * @param id
     * @return
     */
    public ApplyStateInfo getApplyStateInfoById(String id) {
        return demoModel.getApplyStateInfoById(id);
    }

    /**
     * 保存审核消息已读状态
     *
     * @param applyStateInfo
     */
    public void saveApplyStateInfo(ApplyStateInfo applyStateInfo) {
        demoModel.saveApplyStateInfo(applyStateInfo);
    }

    /**
     * get contact list
     *
     * @return
     */
    public Map<String, EaseUser> getContactList() {
        if (isLoggedIn() && contactList == null) {
            contactList = demoModel.getContactList();
        }
        // return a empty non-null object to avoid app crash
        if (contactList == null) {
            return new Hashtable<String, EaseUser>();
        }

        return contactList;
    }


    /**
     * set current username
     *
     * @param username
     */
    public void setCurrentUserName(String username) {
        this.username = username;
        demoModel.setCurrentUserName(username);
    }

    /**
     * get current user's id
     */
    public String getCurrentUsernName() {
        if (username == null) {
            username = demoModel.getCurrentUsernName();
        }
        return username;
    }

    public void setRobotList(Map<String, RobotUser> robotList) {
        this.robotList = robotList;
    }

    public Map<String, RobotUser> getRobotList() {
        if (isLoggedIn() && robotList == null) {
            robotList = demoModel.getRobotList();
        }
        return robotList;
    }

    /**
     * update user list to cache and database
     *
     * @param contactInfoList
     */
    public void updateContactList(List<EaseUser> contactInfoList) {
        for (EaseUser u : contactInfoList) {
            EaseCommonUtils.setUserInitialLetter(u);
            demoModel.saveuser(u);
            contactList.put(u.getUsername(), u);
        }
        ArrayList<EaseUser> mList = new ArrayList<EaseUser>();
        mList.addAll(contactList.values());
        demoModel.saveContactList(mList);
    }

    public UserProfileManager getUserProfileManager() {
        if (userProManager == null) {
            userProManager = new UserProfileManager();
        }
        return userProManager;
    }

    void endCall() {
        try {
            //  EMClient.getInstance().callManager().endCall();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addSyncGroupListener(DataSyncListener listener) {
        if (listener == null) {
            return;
        }
        if (!syncGroupsListeners.contains(listener)) {
            syncGroupsListeners.add(listener);
        }
    }

    public void removeSyncGroupListener(DataSyncListener listener) {
        if (listener == null) {
            return;
        }
        if (syncGroupsListeners.contains(listener)) {
            syncGroupsListeners.remove(listener);
        }
    }

    public void addSyncContactListener(DataSyncListener listener) {
        if (listener == null) {
            return;
        }
        if (!syncContactsListeners.contains(listener)) {
            syncContactsListeners.add(listener);
        }
    }

    public void removeSyncContactListener(DataSyncListener listener) {
        if (listener == null) {
            return;
        }
        if (syncContactsListeners.contains(listener)) {
            syncContactsListeners.remove(listener);
        }
    }

    public void addSyncBlackListListener(DataSyncListener listener) {
        if (listener == null) {
            return;
        }
        if (!syncBlackListListeners.contains(listener)) {
            syncBlackListListeners.add(listener);
        }
    }

    public void removeSyncBlackListListener(DataSyncListener listener) {
        if (listener == null) {
            return;
        }
        if (syncBlackListListeners.contains(listener)) {
            syncBlackListListeners.remove(listener);
        }
    }

    /**
     * Get group list from server
     * This method will save the sync state
     *
     * @throws HyphenateException
     */
    public synchronized void asyncFetchGroupsFromServer(final EMCallBack callback) {
        if (isSyncingGroupsWithServer) {
            return;
        }

        isSyncingGroupsWithServer = true;

        new Thread() {
            @Override
            public void run() {
                try {
                    List<EMGroup> groups = EMClient.getInstance().groupManager().getJoinedGroupsFromServer();

                    // in case that logout already before server returns, we should return immediately
                    if (!isLoggedIn()) {
                        isGroupsSyncedWithServer = false;
                        isSyncingGroupsWithServer = false;
                        noitifyGroupSyncListeners(false);
                        return;
                    }

                    demoModel.setGroupsSynced(true);

                    isGroupsSyncedWithServer = true;
                    isSyncingGroupsWithServer = false;

                    //notify sync group list success
                    noitifyGroupSyncListeners(true);

                    if (callback != null) {
                        callback.onSuccess();
                    }
                } catch (HyphenateException e) {
                    demoModel.setGroupsSynced(false);
                    isGroupsSyncedWithServer = false;
                    isSyncingGroupsWithServer = false;
                    noitifyGroupSyncListeners(false);
                    if (callback != null) {
                        callback.onError(e.getErrorCode(), e.toString());
                    }
                }

            }
        }.start();
    }

    public void noitifyGroupSyncListeners(boolean success) {
        for (DataSyncListener listener : syncGroupsListeners) {
            listener.onSyncComplete(success);
        }
    }

    public void asyncFetchContactsFromServer(final EMValueCallBack<List<String>> callback) {
//        if (isSyncingContactsWithServer) {
//            return;
//        }
//        isSyncingContactsWithServer=true;
//        if (!isLoggedIn()) {
//            isSyncingContactsWithServer = false;
//            return;
//        }
        MyApplication.getInstance().asyContactsFromServer();
//        new Thread() {
//            @Override
//            public void run() {
//                List<String> usernames = null;
//                List<String> selfIds = null;
//                try {
//                    usernames = EMClient.getInstance().contactManager().getAllContactsFromServer();
//                    selfIds = EMClient.getInstance().contactManager().getSelfIdsOnOtherPlatform();
//                    // in case that logout already before server returns, we should return immediately
//                    if (!isLoggedIn()) {
//                        isContactsSyncedWithServer = false;
//                        isSyncingContactsWithServer = false;
//                        notifyContactsSyncListener(false);
//                        return;
//                    }
//                    if (selfIds.size() > 0) {
//                        usernames.addAll(selfIds);
//                    }
//                    Map<String, EaseUser> userlist = new HashMap<String, EaseUser>();
//                    for (String username : usernames) {
//                        EaseUser user = new EaseUser(username);
//                        EaseCommonUtils.setUserInitialLetter(user);
//                        userlist.put(username, user);
//                    }
//                    // save the contact list to cache
//                    getContactList().clear();
//                    getContactList().putAll(userlist);
//                    // save the contact list to database
//                    UserDao dao = new UserDao(appContext);
//                    List<EaseUser> users = new ArrayList<EaseUser>(userlist.values());
//                    dao.saveContactList(users);
//
//                    demoModel.setContactSynced(true);
//                    EMLog.d(TAG, "set contact syn status to true");
//
//                    isContactsSyncedWithServer = true;
//                    isSyncingContactsWithServer = false;
//
//                    //notify sync success
//                    notifyContactsSyncListener(true);
//
//                    getUserProfileManager().asyncFetchContactInfosFromServer(usernames, new EMValueCallBack<List<EaseUser>>() {
//
//                        @Override
//                        public void onSuccess(List<EaseUser> uList) {
//                            updateContactList(uList);
//                            getUserProfileManager().notifyContactInfosSyncListener(true);
//                        }
//
//                        @Override
//                        public void onError(int error, String errorMsg) {
//                        }
//                    });
//                    if (callback != null) {
//                        callback.onSuccess(usernames);
//                    }
//                } catch (HyphenateException e) {
//                    demoModel.setContactSynced(false);
//                    isContactsSyncedWithServer = false;
//                    isSyncingContactsWithServer = false;
//                    notifyContactsSyncListener(false);
//                    e.printStackTrace();
//                    if (callback != null) {
//                        callback.onError(e.getErrorCode(), e.toString());
//                    }
//                }
//
//            }
//        }.start();
    }

    public void notifyContactsSyncListener(boolean success) {
        for (DataSyncListener listener : syncContactsListeners) {
            listener.onSyncComplete(success);
        }
    }

    public void asyncFetchBlackListFromServer(final EMValueCallBack<List<String>> callback) {

        if (isSyncingBlackListWithServer) {
            return;
        }

        isSyncingBlackListWithServer = true;

        new Thread() {
            @Override
            public void run() {
                try {
                    List<String> usernames = EMClient.getInstance().contactManager().getBlackListFromServer();

                    // in case that logout already before server returns, we should return immediately
                    if (!isLoggedIn()) {
                        isBlackListSyncedWithServer = false;
                        isSyncingBlackListWithServer = false;
                        notifyBlackListSyncListener(false);
                        return;
                    }

                    demoModel.setBlacklistSynced(true);

                    isBlackListSyncedWithServer = true;
                    isSyncingBlackListWithServer = false;

                    notifyBlackListSyncListener(true);
                    if (callback != null) {
                        callback.onSuccess(usernames);
                    }
                } catch (HyphenateException e) {
                    demoModel.setBlacklistSynced(false);

                    isBlackListSyncedWithServer = false;
                    isSyncingBlackListWithServer = true;
                    e.printStackTrace();

                    if (callback != null) {
                        callback.onError(e.getErrorCode(), e.toString());
                    }
                }

            }
        }.start();
    }

    public void notifyBlackListSyncListener(boolean success) {
        for (DataSyncListener listener : syncBlackListListeners) {
            listener.onSyncComplete(success);
        }
    }

    public boolean isSyncingGroupsWithServer() {
        return isSyncingGroupsWithServer;
    }

    public boolean isSyncingContactsWithServer() {
        return isSyncingContactsWithServer;
    }

    public boolean isSyncingBlackListWithServer() {
        return isSyncingBlackListWithServer;
    }

    public boolean isGroupsSyncedWithServer() {
        return isGroupsSyncedWithServer;
    }

    public boolean isContactsSyncedWithServer() {
        return isContactsSyncedWithServer;
    }

    public boolean isBlackListSyncedWithServer() {
        return isBlackListSyncedWithServer;
    }

    synchronized void reset() {
        isSyncingGroupsWithServer = false;
        isSyncingContactsWithServer = false;
        isSyncingBlackListWithServer = false;

        demoModel.setGroupsSynced(false);
        demoModel.setContactSynced(false);
        demoModel.setBlacklistSynced(false);

        isGroupsSyncedWithServer = false;
        isContactsSyncedWithServer = false;
        isBlackListSyncedWithServer = false;

        isGroupAndContactListenerRegisted = false;

        setContactList(null);
        setRobotList(null);
        getUserProfileManager().reset();
        DemoDBManager.getInstance().closeDB();
    }

    public void pushActivity(Activity activity) {
        easeUI.pushActivity(activity);
    }

    public void popActivity(Activity activity) {
        easeUI.popActivity(activity);
    }

}