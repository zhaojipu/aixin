package com.haoniu.aixin.activity.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.haoniu.aixin.R;
import com.haoniu.aixin.activity.MainActivity;
import com.haoniu.aixin.base.Constant;
import com.haoniu.aixin.base.EaseConstant;
import com.haoniu.aixin.base.EaseUI;
import com.haoniu.aixin.base.MyApplication;
import com.haoniu.aixin.base.MyHelper;
import com.haoniu.aixin.base.Storage;
import com.haoniu.aixin.domain.EaseEmojicon;
import com.haoniu.aixin.domain.EaseGroupInfo;
import com.haoniu.aixin.domain.EaseUser;
import com.haoniu.aixin.entity.EventCenter;
import com.haoniu.aixin.entity.NoticeInfo;
import com.haoniu.aixin.entity.RoomInfo;
import com.haoniu.aixin.http.ApiClient;
import com.haoniu.aixin.http.AppConfig;
import com.haoniu.aixin.http.ResultListener;
import com.haoniu.aixin.model.EaseAtMessageHelper;
import com.haoniu.aixin.utils.EaseCommonUtils;
import com.haoniu.aixin.utils.EaseUserUtils;
import com.haoniu.aixin.utils.EventUtil;
import com.haoniu.aixin.widget.EaseAlertDialog;
import com.haoniu.aixin.widget.EaseAlertDialog.AlertDialogUser;
import com.haoniu.aixin.widget.EaseChatExtendMenu;
import com.haoniu.aixin.widget.EaseChatInputMenu;
import com.haoniu.aixin.widget.EaseChatInputMenu.ChatInputMenuListener;
import com.haoniu.aixin.widget.EaseChatMessageList;
import com.haoniu.aixin.widget.EaseVoiceRecorderView;
import com.haoniu.aixin.widget.EaseVoiceRecorderView.EaseVoiceRecorderCallback;
import com.haoniu.aixin.widget.chatrow.EaseCustomChatRowProvider;
import com.hyphenate.EMChatRoomChangeListener;
import com.hyphenate.EMMessageListener;
import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMChatRoom;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.chat.EMImageMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMMessage.ChatType;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.exceptions.HyphenateException;
import com.hyphenate.util.EasyUtils;
import com.hyphenate.util.PathUtil;
import com.zds.base.Toast.ToastUtil;
import com.zds.base.json.FastJsonUtil;
import com.zds.base.log.XLog;
import com.zds.base.util.Preference;
import com.zds.base.util.UriUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * you can new an BaseChatFragment to use or you can inherit it to expand.
 * You need call setArguments to pass chatType and userId
 * <br/>
 * <br/>
 * you can see ChatActivity in demo for your reference
 */
public class BaseChatFragment extends EaseBaseFragment implements EMMessageListener {
    protected static final int REQUEST_CODE_MAP = 1;
    protected static final int REQUEST_CODE_CAMERA = 2;
    protected static final int REQUEST_CODE_LOCAL = 3;
    /**
     * params to fragment
     */
    protected int chatType;
    protected String toChatUsername;

    protected int roomType;

    protected EMConversation conversation;

    protected InputMethodManager inputManager;
    protected ClipboardManager clipboard;

    protected Handler handler = new Handler();
    protected File cameraFile;
    protected SwipeRefreshLayout swipeRefreshLayout;
    protected ListView listView;

    protected boolean isloading;
    protected boolean haveMoreData = true;
    protected int pagesize = 40;
    protected GroupListener groupListener;
    protected EMMessage contextMenuMessage;
    protected EMChatRoom group;

    static final int ITEM_TAKE_PICTURE = 1;
    static final int ITEM_PICTURE = 2;
    static final int ITEM_LOCATION = 3;

    protected int[] itemStrings = {R.string.attach_take_pic, R.string.attach_picture};
    protected int[] itemdrawables = {R.mipmap.img_new_pais, R.mipmap.img_new_zhaop
    };
    protected int[] itemIds = {ITEM_TAKE_PICTURE, ITEM_PICTURE};

    @BindView(R.id.message_list)
    EaseChatMessageList mMessageList;
    @BindView(R.id.voice_recorder)
    EaseVoiceRecorderView mVoiceRecorder;
    @BindView(R.id.input_menu)
    EaseChatInputMenu mInputMenu;
    Unbinder unbinder;
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
    @BindView(R.id.tv_gonggao)
    TextView tvGonggao;
    @BindView(R.id.ll_gg)
    LinearLayout llGg;

    private boolean isMessageListInited;
    protected MyItemClickListener extendMenuItemClickListener;
    protected boolean isRoaming = false;
    private ExecutorService fetchQueue;
    protected RoomInfo roomInfo;


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState, boolean roaming) {
        isRoaming = roaming;
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }


    /**
     * 初始化逻辑
     */
    @Override
    protected void initLogic() {
        // message list layout
        if (chatType != EaseConstant.CHATTYPE_SINGLE) {
            mMessageList.setShowUserNick(true);
            llGg.setVisibility(View.VISIBLE);
            getPlatformAnnouncement();
        }else {
            llGg.setVisibility(View.GONE);
        }
//      messageList.setAvatarShape(1);
        listView = mMessageList.getListView();
        extendMenuItemClickListener = new MyItemClickListener();
        registerExtendMenuItem();
        // init input menu
        mInputMenu.init(null);
        mInputMenu.setChatInputMenuListener(new ChatInputMenuListener() {

            @Override
            public void onSendMessage(String content) {
                sendTextMessage(content);
            }

            @Override
            public boolean onPressToSpeakBtnTouch(View v, MotionEvent event) {
                return mVoiceRecorder.onPressToSpeakBtnTouch(v, event, new EaseVoiceRecorderCallback() {

                    @Override
                    public void onVoiceRecordComplete(String voiceFilePath, int voiceTimeLength) {
                        sendVoiceMessage(voiceFilePath, voiceTimeLength);
                    }
                });
            }

            @Override
            public void onBigExpressionClicked(EaseEmojicon emojicon) {
                sendBigExpressionMessage(emojicon.getName(), emojicon.getIdentityCode());
            }
        });

        swipeRefreshLayout = mMessageList.getSwipeRefreshLayout();
        swipeRefreshLayout.setColorSchemeResources(R.color.holo_blue_bright, R.color.holo_green_light,
                R.color.holo_orange_light, R.color.holo_red_light);

        inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        if (isRoaming) {
            fetchQueue = Executors.newSingleThreadExecutor();
        }
        toolbarTitle.setText(toChatUsername);
        if (chatType == EaseConstant.CHATTYPE_SINGLE) {
            // set title
            if (EaseUserUtils.getUserInfo(toChatUsername) != null) {
                EaseUser user = EaseUserUtils.getUserInfo(toChatUsername);
                if (user != null) {
                    if (Constant.ADMIN.equals(user.getUsername())) {
                        toolbarTitle.setText(getResources().getString(R.string.xttz));
                        mInputMenu.setVisibility(View.GONE);
                    } else {
                        toolbarTitle.setText(user.getNickname());
                        mInputMenu.setVisibility(View.VISIBLE);
                    }
                }
            }
            imgRight.setVisibility(View.VISIBLE);
            imgRight.setImageResource(R.drawable.ic_winstyle_delete);
            onConversationInit();
            onMessageListInit();
        } else {
            if (chatType == EaseConstant.CHATTYPE_CHATROOM) {
                imgRight.setImageResource(R.mipmap.img_new_group_detail);
                imgRight.setVisibility(View.VISIBLE);
                group = EMClient.getInstance().chatroomManager().getChatRoom(toChatUsername);
                groupListener = new GroupListener();
                EMClient.getInstance().chatroomManager().addChatRoomChangeListener(groupListener);
                if (group != null) {
                    onConversationInit();
                    onMessageListInit();
                    if (roomType <= 0) {
                        if (roomInfo != null) {
                            roomType = roomInfo.getType();
                        } else {
                            EaseGroupInfo easeGroupInfo = MyHelper.getInstance().getGroupById(toChatUsername);
                            roomType = easeGroupInfo.getGroupType();
                        }
                    }
                }else {
                    onChatRoomViewCreation();
                }
                getRoomDeatil();
            }
        }
        llBack.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (EasyUtils.isSingleActivity(getActivity())) {
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    startActivity(intent);
                }
                onBackPressed();
            }
        });
        imgRight.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (chatType == EaseConstant.CHATTYPE_SINGLE) {
                    emptyHistory();
                } else {
                    toGroupDetails();
                }
            }
        });
        setRefreshLayoutListener();
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (mMessageList != null) {
                    if (firstVisibleItem > oldVisibleItem) {
                        // 向上滑动
                        mMessageList.setCanHd(false);
                    }
                    if (firstVisibleItem < oldVisibleItem && listView.getLastVisiblePosition() >= (totalItemCount - 2)) {
                        mMessageList.setCanHd(true);
                        // 向下滑动
                    }
                }
            }
        });
    }


    /**
     * 获取平台公告数据
     */
    private void getPlatformAnnouncement() {
        Map<String, Object> map = new HashMap<>();
        map.put("pageNum", 1);
        map.put("pageSize", 1);
        ApiClient.requestNetHandle(getActivity(), AppConfig.PlatformAnnouncement, "", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                List<NoticeInfo> applyRecordInfos = FastJsonUtil.getList(json, "list", NoticeInfo.class);
                if (applyRecordInfos != null && applyRecordInfos.size() > 0) {
                    tvGonggao.setText(applyRecordInfos.get(0).getNoticeContent());
                    tvGonggao.setSelected(true);
                    if (applyRecordInfos.get(0).getNoticeId() != Storage.getGGId()) {
                       // setGG(applyRecordInfos.get(0));
                    }
                }
            }

            @Override
            public void onFailure(String msg) {
            }
        });

    }

    private int oldVisibleItem = 0;


    /**
     * 房间详情
     */
    protected void getRoomDeatil() {
        final Map<String, Object> map = new HashMap<>();
        map.put("groupId", toChatUsername);
        ApiClient.requestNetHandle(getActivity(), AppConfig.getRoomDetail, "", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                roomInfo = FastJsonUtil.getObject(json, RoomInfo.class);
                if (roomInfo != null) {
                    EaseGroupInfo easeGroupInfo = MyHelper.getInstance().getGroupById(roomInfo.getHuanxinGroupId());
                    easeGroupInfo.setHead(roomInfo.getRoomImg());
                    easeGroupInfo.setGroupName(roomInfo.getName());
                    easeGroupInfo.setGroupType(roomInfo.getType());
                    MyHelper.getInstance().saveGroup(easeGroupInfo);
                    roomType = roomInfo.getType();
                    toolbarTitle.setText(roomInfo.getName() + "(" + roomInfo.getRoomUserNum() + ")");
                    if (roomType > 0) {
                        EventBus.getDefault().post(new EventCenter(EventUtil.REGISTERBUTTON));
                    }
                    if (roomInfo!=null&&roomInfo.getRoomBossDTOList()!=null&&roomInfo.getRoomBossDTOList().size()>0&&roomInfo.getRoomBossDTOList().get(0).getId()==MyApplication.getInstance().getUserInfo().getUserId()) {
                        mInputMenu.getPrimaryMenu().setEditfouse(false);
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
     * 红包开声效
     */
    protected void playSound() {
        MediaPlayer player = MediaPlayer.create(getActivity(), R.raw.red_sound);
        player.start();
    }

    /**
     * EventBus接收消息
     *
     * @param center 获取事件总线信息
     */
    @Override
    protected void onEventComing(EventCenter center) {
        if (center.getEventCode() == EventUtil.FLUSHUSERINFO) {

        } else if (center.getEventCode() == EventUtil.FLUSHNOTICE) {
            getRoomDeatil();
        } else if (center.getEventCode() == EventUtil.FLUSHRENAME) {
            if (center.getData().toString().equals(toChatUsername)) {
                getRoomDeatil();
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
        chatType = extras.getInt(EaseConstant.EXTRA_CHAT_TYPE, EaseConstant.CHATTYPE_SINGLE);
        // userId you are chat with or group id
        toChatUsername = extras.getString(EaseConstant.EXTRA_USER_ID);
        roomType = extras.getInt(Constant.ROOMTYPE);
    }

    /**
     * register extend menu, item id need > 3 if you override this method and keep exist item
     */
    protected void registerExtendMenuItem() {
        for (int i = 0; i < itemStrings.length; i++) {
            mInputMenu.registerExtendMenuItem(itemStrings[i], itemdrawables[i], itemIds[i], extendMenuItemClickListener);
        }
    }

    protected void onConversationInit() {
        conversation = EMClient.getInstance().chatManager().getConversation(toChatUsername, EaseCommonUtils.getConversationType(chatType), true);
        conversation.markAllMessagesAsRead();
        // the number of messages loaded into conversation is getChatOptions().getNumberOfMessagesLoaded
        // you can change this number
        if (!isRoaming) {
            final List<EMMessage> msgs = conversation.getAllMessages();
            int msgCount = msgs != null ? msgs.size() : 0;
            if (msgCount < conversation.getAllMsgCount() && msgCount < pagesize) {
                String msgId = null;
                if (msgs != null && msgs.size() > 0) {
                    msgId = msgs.get(0).getMsgId();
                }
                conversation.loadMoreMsgFromDB(msgId, pagesize - msgCount);
            }
        } else {
            fetchQueue.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        EMClient.getInstance().chatManager().fetchHistoryMessages(
                                toChatUsername, EaseCommonUtils.getConversationType(chatType), pagesize, "");
                        final List<EMMessage> msgs = conversation.getAllMessages();
                        int msgCount = msgs != null ? msgs.size() : 0;
                        if (msgCount < conversation.getAllMsgCount() && msgCount < pagesize) {
                            String msgId = null;
                            if (msgs != null && msgs.size() > 0) {
                                msgId = msgs.get(0).getMsgId();
                            }
                            conversation.loadMoreMsgFromDB(msgId, pagesize - msgCount);
                        }
                        mMessageList.refreshSelectLast();
                    } catch (HyphenateException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    protected void onMessageListInit() {
        mMessageList.init(toChatUsername, chatType, chatFragmentHelper != null ?
                chatFragmentHelper.onSetCustomChatRowProvider() : null);
        setListItemClickListener();
        mMessageList.getListView().setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideKeyboard();
                if (mInputMenu != null) {
                    mInputMenu.hideExtendMenuContainer();
                }
                return false;
            }
        });
        isMessageListInited = true;
    }

    protected void setListItemClickListener() {
        mMessageList.setItemClickListener(new EaseChatMessageList.MessageListItemClickListener() {

            @Override
            public void onUserAvatarClick(String username) {
                if (chatFragmentHelper != null) {
                    chatFragmentHelper.onAvatarClick(username);
                }
            }

            @Override
            public void onUserAvatarLongClick(String username) {
                if (chatFragmentHelper != null) {
                    chatFragmentHelper.onAvatarLongClick(username);
                }
            }

            @Override
            public void onBubbleLongClick(EMMessage message) {
                contextMenuMessage = message;
                if (chatFragmentHelper != null) {
                    chatFragmentHelper.onMessageBubbleLongClick(message);
                }
            }

            @Override
            public boolean onBubbleClick(EMMessage message) {
                if (chatFragmentHelper == null) {
                    return false;
                }
                return chatFragmentHelper.onMessageBubbleClick(message);
            }

        });
    }

    protected void setRefreshLayoutListener() {
        swipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {

            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        if (!isRoaming) {
                            loadMoreLocalMessage();
                        } else {
                            loadMoreRoamingMessages();
                        }
                    }
                }, 600);
            }
        });
    }

    private void loadMoreLocalMessage() {
        if (listView.getFirstVisiblePosition() == 0 && !isloading && haveMoreData) {
            List<EMMessage> messages;
            try {
                messages = conversation.loadMoreMsgFromDB(conversation.getAllMessages().size() == 0 ? "" : conversation.getAllMessages().get(0).getMsgId(),
                        pagesize);
            } catch (Exception e1) {
                swipeRefreshLayout.setRefreshing(false);
                return;
            }
            if (messages.size() > 0) {
                mMessageList.refreshSeekTo(messages.size() - 1);
                if (messages.size() != pagesize) {
                    haveMoreData = false;
                }
            } else {
                haveMoreData = false;
            }

            isloading = false;
        } else {
            toast(getResources().getString(R.string.no_more_messages));
        }
        swipeRefreshLayout.setRefreshing(false);
    }

    private void loadMoreRoamingMessages() {
        if (!haveMoreData) {
            toast(getResources().getString(R.string.no_more_messages));
            swipeRefreshLayout.setRefreshing(false);
            return;
        }
        if (fetchQueue != null) {
            fetchQueue.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        List<EMMessage> messages = conversation.getAllMessages();
                        EMClient.getInstance().chatManager().fetchHistoryMessages(
                                toChatUsername, EaseCommonUtils.getConversationType(chatType), pagesize,
                                (messages != null && messages.size() > 0) ? messages.get(0).getMsgId() : "");
                    } catch (HyphenateException e) {
                        e.printStackTrace();
                    } finally {
                        Activity activity = getActivity();
                        if (activity != null) {
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    loadMoreLocalMessage();
                                }
                            });
                        }
                    }
                }
            });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            // capture new image
            if (requestCode == REQUEST_CODE_CAMERA) {
                if (cameraFile != null && cameraFile.exists()) {
                    sendImageMessage(cameraFile.getAbsolutePath());
                }
            } else
                // send local image
                if (requestCode == REQUEST_CODE_LOCAL) {
                    if (data != null) {
                        Uri selectedImage = data.getData();
                        if (selectedImage != null) {
                            sendPicByUri(selectedImage);
                        }
                    }
                } else
                    // location
                    if (requestCode == REQUEST_CODE_MAP) {
                        double latitude = data.getDoubleExtra("latitude", 0);
                        double longitude = data.getDoubleExtra("longitude", 0);
                        String locationAddress = data.getStringExtra("address");
                        if (locationAddress != null && !"".equals(locationAddress)) {
                            sendLocationMessage(latitude, longitude, locationAddress);
                        } else {
                            Toast.makeText(getActivity(), R.string.unable_to_get_loaction, Toast.LENGTH_SHORT).show();
                        }

                    }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isMessageListInited) {
            mMessageList.refresh();
        }
        EaseUI.getInstance().pushActivity(getActivity());
        // register the event listener when enter the foreground
        EMClient.getInstance().chatManager().addMessageListener(this);
        if (chatType == EaseConstant.CHATTYPE_GROUP) {
            MyApplication.getInstance().UpUserInfo();
            EaseAtMessageHelper.get().removeAtMeGroup(toChatUsername);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        // unregister this event listener when this activity enters the
        // background
        EMClient.getInstance().chatManager().removeMessageListener(this);

        // remove activity from foreground activity list
        EaseUI.getInstance().popActivity(getActivity());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mHandler.removeMessages(0);
        if (groupListener != null) {
            EMClient.getInstance().chatroomManager().removeChatRoomListener(groupListener);
        }
    }

    public void onBackPressed() {
        if (mInputMenu.onBackPressed()) {
            getActivity().finish();
            if (chatType == EaseConstant.CHATTYPE_GROUP) {
                EaseAtMessageHelper.get().removeAtMeGroup(toChatUsername);
                EaseAtMessageHelper.get().cleanToAtUserList();
            }
        }
    }


    /**
     * implement methods in EMMessageListener
     *
     * @param messages
     */
    @Override
    public void onMessageReceived(List<EMMessage> messages) {
        MyApplication.getInstance().saveUserHeadNick(messages);
        getPlatformAnnouncement();
        for (EMMessage message : messages) {
            String username = null;
            EaseCommonUtils.initMessage(message);
            // group message
            if (message.getChatType() == ChatType.GroupChat || message.getChatType() == ChatType.ChatRoom) {
                username = message.getTo();
            } else {
                // single chat message
                username = message.getFrom();
            }
            if (message.getStringAttribute(Constant.MSGTYPE, "").equals(Constant.WELFARE) || message.getStringAttribute(Constant.MSGTYPE, "").equals(Constant.PUNISHMENT) || message.getStringAttribute(Constant.MSGTYPE, "").equals(Constant.RECHARGE) || message.getStringAttribute(Constant.MSGTYPE, "").equals(Constant.REDPACKET) || message.getStringAttribute(Constant.MSGTYPE, "").equals(Constant.RETURNGOLD) || message.getStringAttribute(Constant.MSGTYPE, "").equals(Constant.RABEND) || message.getStringAttribute(Constant.MSGTYPE, "").equals(Constant.RAB)) {
                MyApplication.getInstance().UpUserInfo();
            }
            // if the message is for current conversation
            if (username.equals(toChatUsername) || message.getTo().equals(toChatUsername) || message.conversationId().equals(toChatUsername)) {
                EaseUI.getInstance().getNotifier().vibrateAndPlayTone(message);
                conversation.markMessageAsRead(message.getMsgId());
                mMessageList.refreshSelectLast();
            } else {
                EaseUI.getInstance().getNotifier().onNewMsg(message);
            }

        }
    }
    protected void onChatRoomViewCreation() {
        final ProgressDialog pd = ProgressDialog.show(getActivity(), "", "加载中......");
        EMClient.getInstance().chatroomManager().joinChatRoom(toChatUsername, new EMValueCallBack<EMChatRoom>() {

            @Override
            public void onSuccess(final EMChatRoom value) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(getActivity().isFinishing() || !toChatUsername.equals(value.getId()))
                            return;
                        pd.dismiss();
                        group = EMClient.getInstance().chatroomManager().getChatRoom(toChatUsername);

                        onConversationInit();
                        onMessageListInit();

                        // Dismiss the click-to-rejoin layout.
                    }
                });
            }

            @Override
            public void onError(final int error, String errorMsg) {
                // TODO Auto-generated method stub
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pd.dismiss();
                    }
                });
                getActivity().finish();
            }
        });
    }

    @Override
    public void onCmdMessageReceived(List<EMMessage> messages) {
        for (EMMessage emMessage : messages) {
            if (emMessage.getStringAttribute(Constant.MSGTYPE, "").equals(Constant.RAB)) {
                String currentUser = MyApplication.getInstance().getUserInfo().getUserId() + "";
                String loginName = emMessage.getStringAttribute("id", "");//id
                String sendLoginName = emMessage.getStringAttribute("sendid", "");//发红包的id
                if (!loginName.equals(currentUser) && !sendLoginName.equals(currentUser)) {
                    continue;
                }
            }
            EaseCommonUtils.initMessage(emMessage);
            EMMessage msg = EMMessage.createTxtSendMessage(emMessage.getStringAttribute("message", ""), emMessage.getTo());
            msg.setChatType(ChatType.ChatRoom);
            msg.setFrom(emMessage.getFrom());
            msg.setTo(emMessage.getTo());
            msg.setMsgId(emMessage.getMsgId());
            msg.setMsgTime(emMessage.getMsgTime());
            msg.setDirection(EMMessage.Direct.RECEIVE);
            msg.setUnread(false);
            msg.setAttribute("cmd", true);
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
            mMessageList.refreshSelectLast();
        }
    }

    @Override
    public void onMessageRead(List<EMMessage> messages) {
        if (isMessageListInited) {
            mMessageList.refresh();
        }
    }

    @Override
    public void onMessageDelivered(List<EMMessage> messages) {
        if (isMessageListInited) {
            mMessageList.refresh();
        }
    }

    @Override
    public void onMessageRecalled(List<EMMessage> messages) {
        if (isMessageListInited) {
            mMessageList.refresh();
        }
    }

    @Override
    public void onMessageChanged(EMMessage emMessage, Object change) {
        if (isMessageListInited) {
            mMessageList.refresh();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    /**
     * handle the click event for extend menu
     */
    class MyItemClickListener implements EaseChatExtendMenu.EaseChatExtendMenuItemClickListener {

        @Override
        public void onClick(int itemId, View view) {
            if (chatFragmentHelper != null) {
                if (chatFragmentHelper.onExtendMenuItemClick(itemId, view)) {
                    return;
                }
            }
            switch (itemId) {
                case ITEM_TAKE_PICTURE:
                    selectPicFromCamera();
                    break;
                case ITEM_PICTURE:
                    selectPicFromLocal();
                    break;
                case ITEM_LOCATION:
                    //  startActivityForResult(new Intent(getActivity(), EaseBaiduMapActivity.class), REQUEST_CODE_MAP);
                    break;
                default:
                    break;
            }
        }

    }

    /**
     * input @
     *
     * @param username
     */
    protected void inputAtUsername(String username, boolean autoAddAtSymbol) {
        if (EMClient.getInstance().getCurrentUser().equals(username) ||
                chatType != EaseConstant.CHATTYPE_GROUP) {
            return;
        }
        EaseAtMessageHelper.get().addAtUser(username);
        EaseUser user = EaseUserUtils.getUserInfo(username);
        if (user != null) {
            username = user.getNick();
        }
        if (autoAddAtSymbol) {
            mInputMenu.insertText("@" + username + " ");
        } else {
            mInputMenu.insertText(username + " ");
        }
    }


    /**
     * input @
     *
     * @param username
     */
    protected void inputAtUsername(String username) {
        inputAtUsername(username, true);
    }


    //send message
    protected void sendTextMessage(String content) {
        if (EaseAtMessageHelper.get().containsAtUsername(content)) {
            sendAtMessage(content);
        } else {
            EMMessage message = EMMessage.createTxtSendMessage(content, toChatUsername);
            sendMessage(message);
        }
    }

    /**
     * send @ message, only support group chat message
     *
     * @param content
     */
    @SuppressWarnings("ConstantConditions")
    private void sendAtMessage(String content) {
        if (chatType != EaseConstant.CHATTYPE_GROUP) {
            XLog.d("only support group chat message");
            return;
        }
        EMMessage message = EMMessage.createTxtSendMessage(content, toChatUsername);
        EMGroup group = EMClient.getInstance().groupManager().getGroup(toChatUsername);
        if (EMClient.getInstance().getCurrentUser().equals(group.getOwner()) && EaseAtMessageHelper.get().containsAtAll(content)) {
            message.setAttribute(EaseConstant.MESSAGE_ATTR_AT_MSG, EaseConstant.MESSAGE_ATTR_VALUE_AT_MSG_ALL);
        } else {
            message.setAttribute(EaseConstant.MESSAGE_ATTR_AT_MSG,
                    EaseAtMessageHelper.get().atListToJsonArray(EaseAtMessageHelper.get().getAtMessageUsernames(content)));
        }
        sendMessage(message);

    }


    protected void sendBigExpressionMessage(String name, String identityCode) {
        EMMessage message = EaseCommonUtils.createExpressionMessage(toChatUsername, name, identityCode);
        sendMessage(message);
    }

    protected void sendVoiceMessage(String filePath, int length) {
        EMMessage message = EMMessage.createVoiceSendMessage(filePath, length, toChatUsername);
        sendMessage(message);
    }

    protected void sendImageMessage(String imagePath) {
        EMMessage message = EMMessage.createImageSendMessage(imagePath, false, toChatUsername);
        sendMessage(message);
    }

    protected void sendLocationMessage(double latitude, double longitude, String locationAddress) {
        EMMessage message = EMMessage.createLocationSendMessage(latitude, longitude, locationAddress, toChatUsername);
        sendMessage(message);
    }

    protected void sendVideoMessage(String videoPath, String thumbPath, int videoLength) {
        EMMessage message = EMMessage.createVideoSendMessage(videoPath, thumbPath, videoLength, toChatUsername);
        sendMessage(message);
    }

    protected void sendFileMessage(String filePath) {
        EMMessage message = EMMessage.createFileSendMessage(filePath, toChatUsername);
        sendMessage(message);
    }

    protected void sendMessage(EMMessage message) {
        if (message == null) {
            return;
        }
        if (chatFragmentHelper != null) {
            //set extension
            chatFragmentHelper.onSetMessageAttributes(message);
        }
        if (chatType == EaseConstant.CHATTYPE_CHATROOM) {
            message.setChatType(ChatType.ChatRoom);
            if (roomInfo!=null&&roomInfo.getRoomBossDTOList()!=null&&roomInfo.getRoomBossDTOList().size()>0&&roomInfo.getRoomBossDTOList().get(0).getId()==MyApplication.getInstance().getUserInfo().getUserId()){
            } else {
                ToastUtil.toast("群禁言");
                return;
            }
        }
        message.setAttribute(Constant.AVATARURL, MyApplication.getInstance().getUserInfo().getUserImg());
        message.setAttribute(Constant.NICKNAME, MyApplication.getInstance().getUserInfo().getNickName());
        //Add to conversation
        EMClient.getInstance().chatManager().sendMessage(message);
        EMClient.getInstance().chatManager().saveMessage(message);
        //refresh ui
        if (isMessageListInited) {
            mMessageList.refreshSelectLast();
        }
        //保存发送时间
        Preference.saveLongPreferences(getActivity(), toChatUsername, System.currentTimeMillis() / 1000);
    }


    //===================================================================================


    /**
     * send image
     *
     * @param selectedImage
     */
    protected void sendPicByUri(Uri selectedImage) {
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = getActivity().getContentResolver().query(selectedImage, filePathColumn, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            cursor = null;

            if (picturePath == null || "null".equals(picturePath)) {
                Toast toast = Toast.makeText(getActivity(), R.string.cant_find_pictures, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;
            }
            sendImageMessage(picturePath);
        } else {
            File file = new File(selectedImage.getPath());
            if (!file.exists()) {
                Toast toast = Toast.makeText(getActivity(), R.string.cant_find_pictures, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;

            }
            sendImageMessage(file.getAbsolutePath());
        }

    }

    /**
     * send file
     *
     * @param uri
     */
    protected void sendFileByUri(Uri uri) {
        String filePath = null;
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = null;
            try {
                cursor = getActivity().getContentResolver().query(uri, filePathColumn, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow("_data");
                if (cursor.moveToFirst()) {
                    filePath = cursor.getString(column_index);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            filePath = uri.getPath();
        }
        if (filePath == null) {
            return;
        }
        File file = new File(filePath);
        if (!file.exists()) {
            Toast.makeText(getActivity(), R.string.File_does_not_exist, Toast.LENGTH_SHORT).show();
            return;
        }
        sendFileMessage(filePath);
    }

    /**
     * capture new image
     */
    protected void selectPicFromCamera() {
        if (!EaseCommonUtils.isSdcardExist()) {
            Toast.makeText(getActivity(), R.string.sd_card_does_not_exist, Toast.LENGTH_SHORT).show();
            return;
        }

        cameraFile = new File(PathUtil.getInstance().getImagePath(), EMClient.getInstance().getCurrentUser()
                + System.currentTimeMillis() + ".jpg");
        //noinspection ResultOfMethodCallIgnored
        cameraFile.getParentFile().mkdirs();

        startActivityForResult(
                new Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(MediaStore.EXTRA_OUTPUT, UriUtil.getUri(getActivity(), cameraFile)),
                REQUEST_CODE_CAMERA);
    }

    /**
     * select local image
     */
    protected void selectPicFromLocal() {
        Intent intent;
        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");

        } else {
            intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        }
        startActivityForResult(intent, REQUEST_CODE_LOCAL);
    }


    /**
     * clear the conversation history
     */
    protected void emptyHistory() {
        if (conversation.getAllMsgCount() == 0) {
            toast("消息已清空");
            return;
        }
        String msg = getResources().getString(R.string.Whether_to_empty_all_chats);
        new EaseAlertDialog(getActivity(), null, msg, null, new AlertDialogUser() {

            @Override
            public void onResult(boolean confirmed, Bundle bundle) {
                if (confirmed) {
                    if (conversation != null) {
                        conversation.clearAllMessages();
                    }
                    mMessageList.refresh();
                    haveMoreData = true;
                }
            }
        }, true).show();
    }

    /**
     * open group detail
     */
    protected void toGroupDetails() {
        if (chatType == EaseConstant.CHATTYPE_CHATROOM) {
//            EMGroup group = EMClient.getInstance().groupManager().getGroup(toChatUsername);
//            if (group == null) {
//                Toast.makeText(getActivity(), R.string.gorup_not_found, Toast.LENGTH_SHORT).show();
//                return;
//            }
            if (chatFragmentHelper != null) {
                chatFragmentHelper.onEnterToChatDetails();
            }
        }
    }

    /**
     * hide
     */
    protected void hideKeyboard() {
        if (getActivity().getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getActivity().getCurrentFocus() != null) {
                inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    /**
     * forward message
     *
     * @param forward_msg_id
     */
    protected void forwardMessage(String forward_msg_id) {
        final EMMessage forward_msg = EMClient.getInstance().chatManager().getMessage(forward_msg_id);
        EMMessage.Type type = forward_msg.getType();
        switch (type) {
            case TXT:
                if (forward_msg.getBooleanAttribute(EaseConstant.MESSAGE_ATTR_IS_BIG_EXPRESSION, false)) {
                    sendBigExpressionMessage(((EMTextMessageBody) forward_msg.getBody()).getMessage(),
                            forward_msg.getStringAttribute(EaseConstant.MESSAGE_ATTR_EXPRESSION_ID, null));
                } else {
                    // get the content and send it
                    String content = ((EMTextMessageBody) forward_msg.getBody()).getMessage();
                    sendTextMessage(content);
                }
                break;
            case IMAGE:
                // send image
                String filePath = ((EMImageMessageBody) forward_msg.getBody()).getLocalUrl();
                if (filePath != null) {
                    File file = new File(filePath);
                    if (!file.exists()) {
                        // send thumb nail if original image does not exist
                        filePath = ((EMImageMessageBody) forward_msg.getBody()).thumbnailLocalPath();
                    }
                    sendImageMessage(filePath);
                }
                break;
            default:
                break;
        }

//        if (forward_msg.getChatType() == ChatType.ChatRoom) {
//            EMClient.getInstance().chatroomManager().leaveChatRoom(forward_msg.getTo());
//        }
    }

    /**
     * listen the group event
     */
    class GroupListener implements EMChatRoomChangeListener {



        @Override
        public void onChatRoomDestroyed(String s, String s1) {
            Activity activity = getActivity();
            if (activity != null) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (toChatUsername.equals(s)) {
                            toast("房间已被群主解散");
                            Activity activity = getActivity();
                            if (activity != null && !activity.isFinishing()) {
                                activity.finish();
                            }
                        }
                    }
                });
            }
        }

        @Override
        public void onMemberJoined(String s, String s1) {
            if (s.contains(MyApplication.getInstance().getUserInfo().getIdhs())){
                onConversationInit();
                onMessageListInit();
            }
            getRoomDeatil();
        }

        @Override
        public void onMemberExited(String s, String s1, String s2) {
//            Activity activity = getActivity();
//            if (activity != null) {
//                activity.runOnUiThread(new Runnable() {
//
//                    @Override
//                    public void run() {
//                        if (toChatUsername.equals(s)) {
//                            Activity activity = getActivity();
//                            if (activity != null && !activity.isFinishing()) {
//                                activity.finish();
//                            }
//                        }
//                    }
//                });
//            }
        }

        @Override
        public void onRemovedFromChatRoom(String s, String s1, String s2) {
//            Activity activity = getActivity();
//            if (activity != null) {
//                activity.runOnUiThread(new Runnable() {
//
//                    @Override
//                    public void run() {
//                        if (toChatUsername.equals(s)) {
//                            toast("您离开了了房间");
//                            Activity activity = getActivity();
//                            if (activity != null && !activity.isFinishing()) {
//                                activity.finish();
//                            }
//                        }
//                    }
//                });
//            }
        }

        @Override
        public void onMuteListAdded(String s, List<String> list, long l) {

        }

        @Override
        public void onMuteListRemoved(String s, List<String> list) {

        }

        @Override
        public void onAdminAdded(String s, String s1) {

        }

        @Override
        public void onAdminRemoved(String s, String s1) {

        }

        @Override
        public void onOwnerChanged(String s, String s1, String s2) {

        }

        @Override
        public void onAnnouncementChanged(String s, String s1) {

        }

    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            EMGroup group = EMClient.getInstance().groupManager().getGroup(toChatUsername);
            if (group != null) {
                onConversationInit();
                onMessageListInit();
            }
        }
    };


    protected EaseChatFragmentHelper chatFragmentHelper;

    public void setChatFragmentHelper(EaseChatFragmentHelper chatFragmentHelper) {
        this.chatFragmentHelper = chatFragmentHelper;
    }

    public interface EaseChatFragmentHelper {
        /**
         * set message attribute
         */
        void onSetMessageAttributes(EMMessage message);

        /**
         * enter to chat detail
         */
        void onEnterToChatDetails();

        /**
         * on avatar clicked
         *
         * @param username
         */
        void onAvatarClick(String username);

        /**
         * on avatar long pressed
         *
         * @param username
         */
        void onAvatarLongClick(String username);

        /**
         * on message bubble clicked
         */
        boolean onMessageBubbleClick(EMMessage message);

        /**
         * on message bubble long pressed
         */
        void onMessageBubbleLongClick(EMMessage message);

        /**
         * on extend menu item clicked, return true if you want to override
         *
         * @param view
         * @param itemId
         * @return
         */
        boolean onExtendMenuItemClick(int itemId, View view);

        /**
         * on set custom chat row provider
         *
         * @return
         */
        EaseCustomChatRowProvider onSetCustomChatRowProvider();
    }

}
