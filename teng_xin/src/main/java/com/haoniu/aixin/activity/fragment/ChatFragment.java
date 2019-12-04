package com.haoniu.aixin.activity.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Toast;

import com.haoniu.aixin.R;
import com.haoniu.aixin.activity.ChatActivity;
import com.haoniu.aixin.activity.CustomServiceActivity;
import com.haoniu.aixin.activity.GroupDetailsNewActivity;
import com.haoniu.aixin.activity.NewMyPacketActivity;
import com.haoniu.aixin.activity.RedPacketDetailActivity;
import com.haoniu.aixin.activity.SendRedPaketActivity;
import com.haoniu.aixin.activity.SendRedPaketLei2Activity;
import com.haoniu.aixin.activity.SendRedPaketLei4Activity;
import com.haoniu.aixin.activity.SevicePeopleActivity;
import com.haoniu.aixin.activity.TransferRecordActivity;
import com.haoniu.aixin.activity.TransferfActivity;
import com.haoniu.aixin.activity.WebViewActivity;
import com.haoniu.aixin.activity.WithdrawActivity;
import com.haoniu.aixin.base.Constant;
import com.haoniu.aixin.base.EaseConstant;
import com.haoniu.aixin.base.MyApplication;
import com.haoniu.aixin.base.MyHelper;
import com.haoniu.aixin.base.Storage;
import com.haoniu.aixin.domain.EaseUser;
import com.haoniu.aixin.entity.EventCenter;
import com.haoniu.aixin.entity.PZInfo;
import com.haoniu.aixin.entity.RoomInfo;
import com.haoniu.aixin.http.ApiClient;
import com.haoniu.aixin.http.AppConfig;
import com.haoniu.aixin.http.ResultListener;
import com.haoniu.aixin.utils.EventUtil;
import com.haoniu.aixin.utils.MyAnimation;
import com.haoniu.aixin.widget.CommonDialog;
import com.haoniu.aixin.widget.EaseChatRecallPresenter;
import com.haoniu.aixin.widget.EaseChatVoiceCallPresenter;
import com.haoniu.aixin.widget.EaseImageView;
import com.haoniu.aixin.widget.chatrow.EaseCustomChatRowProvider;
import com.haoniu.aixin.widget.my_message.ChatNoticePresenter;
import com.haoniu.aixin.widget.my_message.ChatPromotionPresenter;
import com.haoniu.aixin.widget.my_message.ChatRechargePresenter;
import com.haoniu.aixin.widget.my_message.ChatRedPacketAckPresenter;
import com.haoniu.aixin.widget.my_message.ChatRedPacketAddAllPresenter;
import com.haoniu.aixin.widget.my_message.ChatRedPacketAddPresenter;
import com.haoniu.aixin.widget.my_message.ChatRedPacketAddroomPresenter;
import com.haoniu.aixin.widget.my_message.ChatRedPacketFinalPresenter;
import com.haoniu.aixin.widget.my_message.ChatRedPacketGoReturnPresenter;
import com.haoniu.aixin.widget.my_message.ChatRedPacketLeftPresenter;
import com.haoniu.aixin.widget.my_message.ChatRedPacketPresenter;
import com.haoniu.aixin.widget.my_message.ChatRedPacketPunishmentPresenter;
import com.haoniu.aixin.widget.my_message.ChatRedPacketReturnPresenter;
import com.haoniu.aixin.widget.my_message.ChatRedPacketTPresenter;
import com.haoniu.aixin.widget.my_message.ChatRedPacketUpRoomNamePresenter;
import com.haoniu.aixin.widget.my_message.ChatRedPacketWelfarePresenter;
import com.haoniu.aixin.widget.my_message.ChatRedPacketturnPresenter;
import com.haoniu.aixin.widget.my_message.ChatWithdrawPresenter;
import com.haoniu.aixin.widget.presenter.EaseChatRowPresenter;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.util.PathUtil;
import com.zds.base.ImageLoad.GlideUtils;
import com.zds.base.Toast.ToastUtil;
import com.zds.base.log.XLog;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Administrator
 */
public class ChatFragment extends BaseChatFragment implements BaseChatFragment.EaseChatFragmentHelper {

    /**
     * constant start from 11 to avoid conflict with constant in base class
     */
    private static final int ITEM_VOICE_CALL = 13;
    private static final int ITEM_VIDEO_CALL = 14;
    private static final int REQUEST_CODE_SELECT_VIDEO = 11;
    private static final int REQUEST_CODE_SELECT_FILE = 12;
    private static final int REQUEST_CODE_GROUP_DETAIL = 13;
    private static final int REQUEST_CODE_CONTEXT_MENU = 14;
    private static final int REQUEST_CODE_SELECT_AT_USER = 15;
    private static final int MESSAGE_TYPE_SENT_VOICE_CALL = 1;
    private static final int MESSAGE_TYPE_RECV_VOICE_CALL = 2;
    private static final int MESSAGE_TYPE_SENT_VIDEO_CALL = 3;
    private static final int MESSAGE_TYPE_RECV_VIDEO_CALL = 4;
    private static final int MESSAGE_TYPE_RECALL = 9;
    /**
     * red packet code : 红包功能使用的常量
     */
    private static final int MESSAGE_TYPE_RECV_RED_PACKET = 5;
    private static final int MESSAGE_TYPE_SEND_RED_PACKET = 6;
    private static final int MESSAGE_TYPE_SEND_RED_PACKET_ACK = 7;
    private static final int MESSAGE_TYPE_RECV_RED_PACKET_ACK = 8;
    private static final int MESSAGE_TYPE_SEND_RED_PACKET_FIN = 9;
    private static final int MESSAGE_TYPE_RECV_RED_PACKET_FIN = 10;
    private static final int MESSAGE_TYPE_SEND_RED_PACKET_RETURN = 11;
    private static final int MESSAGE_TYPE_RECV_RED_PACKET_RETURN = 12;
    private static final int MESSAGE_TYPE_SEND_RED_PACKET_TURN = 13;
    private static final int MESSAGE_TYPE_RECV_RED_PACKET_TURN = 14;
    private static final int MESSAGE_TYPE_SEND_RED_PACKET_ADD = 15;
    private static final int MESSAGE_TYPE_RECV_RED_PACKET_ADD = 16;
    private static final int MESSAGE_TYPE_SEND_RED_PACKET_DEL = 17;
    private static final int MESSAGE_TYPE_RECV_RED_PACKET_DEL = 18;
    private static final int MESSAGE_TYPE_SEND_RED_PACKET_T = 19;
    private static final int MESSAGE_TYPE_RECV_RED_PACKET_T = 20;
    private static final int MESSAGE_TYPE_SEND_RED_PACKET_ADDROOM = 21;
    private static final int MESSAGE_TYPE_RECV_RED_PACKET_ADDROOM = 22;
    private static final int MESSAGE_TYPE_SEND_RED_PACKET_ADDS = 23;
    private static final int MESSAGE_TYPE_RECV_RED_PACKET_ADDS = 24;
    private static final int MESSAGE_TYPE_SEND_RED_PACKET_RECHARGE = 25;
    private static final int MESSAGE_TYPE_RECV_RED_PACKET_RECHARGE = 26;
    private static final int MESSAGE_TYPE_SEND_RED_PACKET_PUNISHMENT = 27;
    private static final int MESSAGE_TYPE_RECV_RED_PACKET_PUNISHMENT = 28;
    private static final int MESSAGE_TYPE_SEND_RED_PACKET_FL = 29;
    private static final int MESSAGE_TYPE_RECV_RED_PACKET_FL = 30;
    private static final int MESSAGE_TYPE_SEND_RED_PACKET_RTH = 31;
    private static final int MESSAGE_TYPE_RECV_RED_PACKET_RTH = 32;
    private static final int MESSAGE_TYPE_SEND_RED_PACKET_PROMOTION = 33;
    private static final int MESSAGE_TYPE_RECV_RED_PACKET_PROMOTION = 34;
    private static final int MESSAGE_TYPE_SEND_RED_PACKET_RENAME = 36;
    private static final int MESSAGE_TYPE_RECV_RED_PACKET_RENAME = 35;
    private static final int MESSAGE_TYPE_SEND_WITHDRAW = 38;
    private static final int MESSAGE_TYPE_RECV_WITHDRAW = 37;
    private static final int MESSAGE_NOTICE_SEND = 40;
    private static final int MESSAGE_NOTICE_RECV = 39;
    private static final int ITEM_RED_PACKET = 16;
    private static final int ITEM_RED_PACKET1 = 27;
    private static final int ITEM_RED_PACKET2 = 23;
    private static final int ITEM_RED_PACKET21 = 28;
    private static final int ITEM_TRANSFER = 17;
    private static final int ITEM_RECORD = 19;
    private static final int ITEM_MY_RED_PACKET = 18;
    private static final int ITEM_MY_CHONGZHI = 20;
    private static final int ITEM_MY_tixian = 21;
    private static final int ITEM_MY_kefu = 22;
    private static final int ITEM_MY_rengongchongzhi = 24;
    private static final int ITEM_MY_qunzhu = 25;
    private static final int ITEM_MY_zhangdan = 26;
    //end of red packet code
    /**
     * if it is chatBot
     */
    private boolean isRobot;

    /**
     * 旋转动画
     */
    protected MyAnimation operatingAnim;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState, MyHelper.getInstance().getModel().isMsgRoaming() && (chatType != EaseConstant.CHATTYPE_CHATROOM));
    }

    @Override
    protected void initLogic() {
        operatingAnim = new MyAnimation();
        setChatFragmentHelper(this);
        super.initLogic();
        if (chatType == Constant.CHATTYPE_SINGLE) {
        } else if (chatType == EaseConstant.CHATTYPE_CHATROOM) {
            mInputMenu.getPrimaryMenu().setEditfouse(true);
        }
        setChatFragmentHelper(this);
    }

    private boolean isregister;

    @Override
    protected void registerExtendMenuItem() {
        //use the menu in base class
        super.registerExtendMenuItem();
        if (chatType == Constant.CHATTYPE_SINGLE) {
            mInputMenu.registerExtendMenuItem("转账", R.mipmap.img_transfer, ITEM_TRANSFER, extendMenuItemClickListener);
            mInputMenu.registerExtendMenuItem("转账记录", R.mipmap.img_zhuangzhangjilu, ITEM_RECORD, extendMenuItemClickListener);
        } else if (chatType == Constant.CHATTYPE_CHATROOM) {
            if (roomType > 0 && roomInfo != null && !isregister) {
                isregister = true;
                registerMenu();
            }
        }
    }
    private int amount1;
    private int amount2;
    private void registerMenu() {
        for (RoomInfo.RoomLeiListBean roomLeiListBean : roomInfo.getRoomLeiList()) {
            if (roomLeiListBean.getType() != null && roomLeiListBean.getType().equals("1")) {
                mInputMenu.registerMenuItemNumber("发红包", roomLeiListBean.getAmount(), ITEM_RED_PACKET, extendMenuItemClickListener);
                if (roomLeiListBean.getAmount1()>0){
                    amount1 = roomLeiListBean.getAmount1();
                    mInputMenu.registerMenuItemNumber("发红包", amount1, ITEM_RED_PACKET1, extendMenuItemClickListener);
                }
            } else if (roomLeiListBean.getType() != null && roomLeiListBean.getType().equals("2")) {
                mInputMenu.registerMenuItemNumber("发红包", roomLeiListBean.getAmount(), ITEM_RED_PACKET2, extendMenuItemClickListener);
                if (roomLeiListBean.getAmount1()>0){
                    amount2 = roomLeiListBean.getAmount1();
                    mInputMenu.registerMenuItemNumber("发红包", amount2, ITEM_RED_PACKET21, extendMenuItemClickListener);
                }
            }
        }
        mInputMenu.registerExtendMenuItem("充值", R.mipmap.img_new_chongzhi, ITEM_MY_CHONGZHI, extendMenuItemClickListener);
        mInputMenu.registerExtendMenuItem("提现", R.mipmap.img_new_tixian, ITEM_MY_tixian, extendMenuItemClickListener);
        mInputMenu.registerExtendMenuItem("账单", R.mipmap.img_new_zhangdan, ITEM_MY_zhangdan, extendMenuItemClickListener);
        mInputMenu.registerExtendMenuItem("人工充值", R.mipmap.img_new_rengcz, ITEM_MY_rengongchongzhi, extendMenuItemClickListener);
        mInputMenu.registerExtendMenuItem("联系群主", R.mipmap.img_new_lianxiquanzhu, ITEM_MY_qunzhu, extendMenuItemClickListener);
       // mInputMenu.registerExtendMenuItem("客服", R.mipmap.kefu, ITEM_MY_kefu, extendMenuItemClickListener);

    }

    @Override
    protected void onEventComing(EventCenter center) {//E1E1E3
        super.onEventComing(center);
        if (center.getEventCode() == EventUtil.REGISTERBUTTON) {
            if (roomType > 0 && !isregister) {
                if (chatType == Constant.CHATTYPE_CHATROOM) {
                    if (roomType > 0 && roomInfo != null && !isregister) {
                        isregister = true;
                        registerMenu();
                    }
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                //send the video
                case REQUEST_CODE_SELECT_VIDEO:
                    if (data != null) {
                        int duration = data.getIntExtra("dur", 0);
                        String videoPath = data.getStringExtra("path");
                        File file = new File(PathUtil.getInstance().getImagePath(), "thvideo" + System.currentTimeMillis());
                        try {
                            FileOutputStream fos = new FileOutputStream(file);
                            Bitmap ThumbBitmap = ThumbnailUtils.createVideoThumbnail(videoPath, 3);
                            ThumbBitmap.compress(CompressFormat.JPEG, 100, fos);
                            fos.close();
                            sendVideoMessage(videoPath, file.getAbsolutePath(), duration);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                //send the file
                case REQUEST_CODE_SELECT_FILE:
                    if (data != null) {
                        Uri uri = data.getData();
                        if (uri != null) {
                            sendFileByUri(uri);
                        }
                    }
                    break;
                case REQUEST_CODE_SELECT_AT_USER:
                    if (data != null) {
                        String username = data.getStringExtra("username");
                        inputAtUsername(username, false);
                    }
                    break;
                default:
                    break;
            }
        }

    }

    @Override
    public void onSetMessageAttributes(EMMessage message) {
        if (isRobot) {
            //set message extension
            message.setAttribute("em_robot_message", isRobot);
        }
    }

    @Override
    public EaseCustomChatRowProvider onSetCustomChatRowProvider() {
        return new CustomChatRowProvider();
    }


    @Override
    public void onEnterToChatDetails() {
        if (chatType == Constant.CHATTYPE_CHATROOM) {
//            EMGroup group = EMClient.getInstance().groupManager().getGroup(toChatUsername);
            if (group != null && roomInfo != null && roomType > 0) {
                startActivityForResult((new Intent(getActivity(), GroupDetailsNewActivity.class).putExtra("roomInfo", roomInfo)),
                        REQUEST_CODE_GROUP_DETAIL);
            } else {
                toast("未获取群组信息");
                return;
            }

        }
    }

    @Override
    public void onAvatarClick(String username) {
        //handling when user click avatar
//        Intent intent = new Intent(getActivity(), UserInfoActivity.class);
//        intent.putExtra("username", username);
//        startActivity(intent);
    }

    @Override
    public void onAvatarLongClick(String username) {
        // inputAtUsername(username);
    }



    @Override
    public boolean onMessageBubbleClick(EMMessage message) {
        //消息框点击事件，demo这里不做覆盖，如需覆盖，return true
        if (message.getStringAttribute(Constant.MSGTYPE, "").equals(Constant.REDPACKET)) {
            XLog.d(message.ext().toString());
            message.setAttribute("isClick",true);
            EMClient.getInstance().chatManager().saveMessage(message);
            mMessageList.refreshSelectLast();
            checkRedPacket(message);
            return true;
        }
        return false;
    }

    @Override
    public void onCmdMessageReceived(List<EMMessage> messages) {
//        for (EMMessage emMessage:messages){
//            EMClient.getInstance().chatManager().saveMessage(emMessage);
//        }
    }

    @Override
    public void onMessageBubbleLongClick(EMMessage message) {
    }

    @Override
    public boolean onExtendMenuItemClick(int itemId, View view) {
        switch (itemId) {
            case ITEM_VOICE_CALL:
                startVoiceCall();
                break;
            case ITEM_VIDEO_CALL:
                startVideoCall();
                break;
            case ITEM_RED_PACKET:
                //发包
                if (roomType == Constant.CAILEI && roomInfo != null) {
                    startActivity(new Intent(getActivity(), SendRedPaketLei4Activity.class).putExtra("username", toChatUsername).putExtra("detail", roomInfo));
                }
                break;
            case ITEM_RED_PACKET1:
                //发包
                if (roomType == Constant.CAILEI && roomInfo != null) {
                    startActivity(new Intent(getActivity(), SendRedPaketLei4Activity.class)
                            .putExtra("username", toChatUsername)
                            .putExtra("detail", roomInfo)
                            .putExtra("amount",amount1));
                }
                break;
            case ITEM_RED_PACKET2:
                //发包
                if (roomType == Constant.CAILEI && roomInfo != null) {
                    startActivity(new Intent(getActivity(), SendRedPaketLei2Activity.class).putExtra("username", toChatUsername).putExtra("detail", roomInfo));
                }
                break;
            case ITEM_RED_PACKET21:
                //发包
                if (roomType == Constant.CAILEI && roomInfo != null) {
                    startActivity(new Intent(getActivity(), SendRedPaketLei2Activity.class)
                            .putExtra("username", toChatUsername)
                            .putExtra("detail", roomInfo)
                            .putExtra("amount",amount2));
                }
                break;
            case ITEM_MY_RED_PACKET:
                //个人红包
                if (chatType == EaseConstant.CHATTYPE_CHATROOM) {
                    startActivity(new Intent(getActivity(), SendRedPaketActivity.class).putExtra("username", toChatUsername).putExtra("tag", 1));
                } else if (chatType == EaseConstant.CHATTYPE_SINGLE) {
                    startActivity(new Intent(getActivity(), SendRedPaketActivity.class).putExtra("username", toChatUsername).putExtra("tag", 0));
                }
                break;
            case ITEM_TRANSFER:
                //转账
                startActivity(new Intent(getActivity(), TransferfActivity.class).putExtra("userId" +
                        "", toChatUsername));
                break;
            case ITEM_RECORD:
                //转账记录
                startActivity(new Intent(getActivity(), TransferRecordActivity.class));
                break;
            case ITEM_MY_rengongchongzhi:
                //人工充值
                /**
                 *  人工充值客服
                 */
                startActivity(new Intent(getActivity(), SevicePeopleActivity.class).putExtra("type",1).putExtra("title","人工充值客服"));
                break;
            case ITEM_MY_zhangdan:
                //账单
                startActivity(new Intent(getActivity(), NewMyPacketActivity.class));
                break;
            case ITEM_MY_qunzhu:
                if (roomInfo!=null&&roomInfo.getRoomBossDTOList()!=null&&roomInfo.getRoomBossDTOList().size()>0&&roomInfo.getRoomBossDTOList().get(0).getId()==MyApplication.getInstance().getUserInfo().getUserId()) {
                  toast("不能跟自己聊天");
                }else {
                    //联系群主
                if (roomInfo!=null&&roomInfo.getRoomBossDTOList()!=null&&roomInfo.getRoomBossDTOList().size()>0){
                    EaseUser easeUser = new EaseUser(roomInfo.getRoomBossDTOList().get(0).getIdh());
                    easeUser.setAvatar(roomInfo.getRoomBossDTOList().get(0).getHeadImg());
                    easeUser.setNickname(roomInfo.getRoomBossDTOList().get(0).getNickName());
                    MyHelper.getInstance().saveUser(easeUser);
                    startActivity(new Intent(getActivity(), ChatActivity.class).putExtra(Constant.EXTRA_USER_ID, roomInfo.getRoomBossDTOList().get(0).getId()+Constant.ID_REDPROJECT ).putExtra(Constant.EXTRA_CHAT_TYPE, Constant.CHATTYPE_SINGLE));
                }else {toast("获取群主信息失败！");}}
                break;

            case ITEM_MY_kefu:
                //客服
                startActivity(new Intent(getActivity(),CustomServiceActivity.class));
                break;
            case ITEM_MY_CHONGZHI:
                //充值
                PZInfo pzInfo = Storage.GetPZ();
                if (pzInfo != null) {
                    startActivity(new Intent(getActivity(), WebViewActivity.class).putExtra("title", "充值").putExtra("url", pzInfo.getRechargeUrl()+Storage.getToken()));
                }
                //startActivity(new Intent(getActivity(), WebViewActivity.class).putExtra("title", "充值").putExtra("url", AppConfig.getRecharge));
                break;
            case ITEM_MY_tixian:
                //提现
                startActivity(new Intent(getActivity(), WithdrawActivity.class));
                break;
            default:
                break;
        }
        return false;
    }

    /**
     * 检测红包
     */
    private void checkRedPacket(final EMMessage emMessage) {
        final String head = emMessage.getStringAttribute(Constant.AVATARURL, "");
        final String nickname = emMessage.getStringAttribute(Constant.NICKNAME, "");
        final int id = emMessage.getIntAttribute(Constant.REDPACKETID, 0);
        Map<String, Object> map = new HashMap<>();
        map.put("redPacketId", emMessage.getIntAttribute(Constant.REDPACKETID, 0));
        ApiClient.requestNetHandle(getActivity(), AppConfig.getRedEnvelopeState, "正在加载", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                XLog.json(json);
                if (json != null) {
                    double state;
                    try {
                        state = Double.valueOf(json);
                    } catch (Exception e) {
                        return;
                    }
                    //查看红包详情 20 红包未过期 已抢完 参与  10 红包未过期 未抢完 参与
                    if (state == 20 || state == 10) {
                        startActivity(new Intent(getActivity(), RedPacketDetailActivity.class).putExtra("rid", id).putExtra("head", head).putExtra("nickname", nickname));
                    } else {
                        showRedPacket(state, emMessage);
                    }
                }
            }

            @Override
            public void onFailure(String msg) {
                ToastUtil.toast(msg);
            }
        });
    }

    CommonDialog.Builder builder;

    private boolean isRabShow = false;

    /**
     * 显示红包
     */
    private void showRedPacket(double state, final EMMessage emMessage) {
        XLog.d("redPacket", emMessage.ext().toString());
        final String head = emMessage.getStringAttribute(Constant.AVATARURL, "");
        final String nickname = emMessage.getStringAttribute(Constant.NICKNAME, "");
        String remark = emMessage.getStringAttribute("remark", "恭喜发财，大吉大利");
        final int id = emMessage.getIntAttribute(Constant.REDPACKETID, 0);
        if (builder != null) {
            builder.dismiss();
        }
        builder = new CommonDialog.Builder(getActivity()).center().loadAniamtion()
                .setView(R.layout.dialog_red_packet);
        builder.setOnClickListener(R.id.ll_closes, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.dismiss();
            }
        });
        builder.setText(R.id.tv_nick, nickname);
        builder.setOnClickListener(R.id.img_open, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isRabShow) {
                    toast("抢红包中，请勿重复提交！");
                    return;
                }
                openAnimation(v);
                rabRedPacket(v, emMessage);
            }
        });
        builder.setOnClickListener(R.id.tv_red_detail, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (builder != null) {
                    builder.dismiss();
                }
                startActivity(new Intent(getActivity(), RedPacketDetailActivity.class).putExtra("rid", id).putExtra("head", head).putExtra("nickname", nickname));
            }
        });
        CommonDialog commonDialog = builder.create();
        builder.getView(R.id.tv_red_detail).setVisibility(View.GONE);
        if (state == 11) {//红包未抢完 未过期 未参与
            builder.setText(R.id.tv_message, (remark.equals("null") || remark.equals("")) ? "恭喜发财，大吉大利" : remark);
            builder.getView(R.id.rel_open).setVisibility(View.VISIBLE);
            if (emMessage.getFrom().equals(String.valueOf(MyApplication.getInstance().getUserInfo().getIdh()))) {
                builder.getView(R.id.tv_red_detail).setVisibility(View.VISIBLE);
            }
        } else if (state == 10) {//红包未抢完 未过期 已参与
            builder.setText(R.id.tv_message, "您已抢过红包！不能重复参与");
            builder.getView(R.id.rel_open).setVisibility(View.GONE);
            builder.getView(R.id.tv_red_detail).setVisibility(View.VISIBLE);
        } else if (state == 21) {//红包未过期 已抢完 未参与
            builder.setText(R.id.tv_message, "红包已抢完");
            builder.getView(R.id.rel_open).setVisibility(View.INVISIBLE);
            builder.getView(R.id.tv_red_detail).setVisibility(View.VISIBLE);
        } else if (state == -1) {//红包已过期
            builder.setText(R.id.tv_message, "红包过期");
            builder.getView(R.id.rel_open).setVisibility(View.INVISIBLE);
        }
        EaseImageView imageView = (EaseImageView) builder.getView(R.id.img_head);
        MyApplication.getInstance().setAvatar(imageView);
        GlideUtils.loadImageViewLodingByCircle(AppConfig.checkimg( head), imageView, R.mipmap.img_default_avatar);
        commonDialog.show();
    }

    /**
     * 抢红包
     */
    private void rabRedPacket(final View view, final EMMessage emMessage) {
        final String head = emMessage.getStringAttribute(Constant.AVATARURL, "");
        final String nickname = emMessage.getStringAttribute(Constant.NICKNAME, "");
        final int id = emMessage.getIntAttribute(Constant.REDPACKETID, 0);
        isRabShow = true;
        Map<String, Object> map = new HashMap<>();
        map.put("redPacketId", id + "");
        ApiClient.requestNetHandle(getActivity(), AppConfig.grabRedEnvelope, "", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                isRabShow = false;
                closeAnimation(view);
                playSound();
                builder.dismiss();
                startActivity(new Intent(getActivity(), RedPacketDetailActivity.class).putExtra("rid", id).putExtra("head", head).putExtra("nickname", nickname));
            }

            @Override
            public void onFailure(String msg) {
                ToastUtil.toast(msg);
                isRabShow = false;
                closeAnimation(view);
                builder.dismiss();
            }
        });
    }

    /**
     * 开始动画
     */
    protected void openAnimation(View view) {
        if (operatingAnim != null) {
            view.startAnimation(operatingAnim);
        }
    }

    /**
     * 关闭动画
     */
    protected void closeAnimation(View view) {
        view.clearAnimation();
    }

    /**
     * select file
     */
    protected void selectFileFromLocal() {
        Intent intent = null;
        //api 19 and later, we can't use this way, demo just select from images
        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
        } else {
            intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        }
        startActivityForResult(intent, REQUEST_CODE_SELECT_FILE);
    }

    /**
     * make a voice call
     */
    protected void startVoiceCall() {

        if (!EMClient.getInstance().isConnected()) {
            Toast.makeText(getActivity(), R.string.not_connect_to_server, Toast.LENGTH_SHORT).show();
        } else {
//            startActivity(new Intent(getActivity(), VoiceCallActivity.class).putExtra("username", toChatUsername)
//                    .putExtra("isComingCall", false));
            // voiceCallBtn.setEnabled(false);
            mInputMenu.hideExtendMenuContainer();
        }
    }

    /**
     * make a video call
     */
    protected void startVideoCall() {
        if (!EMClient.getInstance().isConnected()) {
            Toast.makeText(getActivity(), R.string.not_connect_to_server, Toast.LENGTH_SHORT).show();
        } else {
//            startActivity(new Intent(getActivity(), VideoCallActivity.class).putExtra("username", toChatUsername)
//                    .putExtra("isComingCall", false));
            // videoCallBtn.setEnabled(false);
            mInputMenu.hideExtendMenuContainer();
        }
    }

    /**
     * chat row provider
     */
    private final class CustomChatRowProvider implements EaseCustomChatRowProvider {
        @Override
        public int getCustomChatRowTypeCount() {
            //here the number is the message type in EMMessage::Type
            //which is used to count the number of different chat row
            return 54;
        }

        @Override
        public int getCustomChatRowType(EMMessage message) {
            if (message.getType() == EMMessage.Type.TXT) {
                //voice call
                if (message.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_VOICE_CALL, false)) {
                    return message.direct() == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_VOICE_CALL : MESSAGE_TYPE_SENT_VOICE_CALL;
                } else if (message.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_VIDEO_CALL, false)) {
                    //video call
                    return message.direct() == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_VIDEO_CALL : MESSAGE_TYPE_SENT_VIDEO_CALL;
                } else if (message.getBooleanAttribute(Constant.MESSAGE_TYPE_RECALL, false)) {
                    return MESSAGE_TYPE_RECALL;
                } else if (message.getStringAttribute(Constant.MSGTYPE, "").equals(Constant.REDPACKET)) {
                    //红包
                    return message.direct() == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_RED_PACKET : MESSAGE_TYPE_SEND_RED_PACKET;
                } else if (message.getStringAttribute(Constant.MSGTYPE, "").equals(Constant.RAB)) {
                    //抢红包
                    return message.direct() == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_RED_PACKET_ACK : MESSAGE_TYPE_SEND_RED_PACKET_ACK;
                } else if (message.getStringAttribute(Constant.MSGTYPE, "").equals(Constant.RABEND)) {
                    //红包抢完
                    return message.direct() == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_RED_PACKET_FIN : MESSAGE_TYPE_SEND_RED_PACKET_FIN;
                } else if (message.getStringAttribute(Constant.MSGTYPE, "").equals(Constant.RETURNGOLD)) {
                    //红包退还
                    return message.direct() == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_RED_PACKET_RETURN : MESSAGE_TYPE_SEND_RED_PACKET_RETURN;
                } else if (message.getStringAttribute(Constant.MSGTYPE, "").equals(Constant.TURN)) {
                    //转账
                    return message.direct() == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_RED_PACKET_TURN : MESSAGE_TYPE_SEND_RED_PACKET_TURN;
                } else if (message.getStringAttribute(Constant.MSGTYPE, "").equals(Constant.ADDUSER)) {
                    //加入房间
                    return message.direct() == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_RED_PACKET_ADD : MESSAGE_TYPE_SEND_RED_PACKET_ADD;
                } else if (message.getStringAttribute(Constant.MSGTYPE, "").equals(Constant.DELUSER)) {
                    //退出房间
                    return message.direct() == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_RED_PACKET_DEL : MESSAGE_TYPE_SEND_RED_PACKET_DEL;
                } else if (message.getStringAttribute(Constant.MSGTYPE, "").equals(Constant.SHOTUSER)) {
                    //踢人
                    return message.direct() == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_RED_PACKET_T : MESSAGE_TYPE_SEND_RED_PACKET_T;
                } else if (message.getStringAttribute(Constant.MSGTYPE, "").equals(Constant.ADDROOM)) {
                    //创建房间
                    return message.direct() == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_RED_PACKET_ADDROOM : MESSAGE_TYPE_SEND_RED_PACKET_ADDROOM;
                } else if (message.getStringAttribute(Constant.MSGTYPE, "").equals(Constant.ADDUSERS)) {
                    //创建房间
                    return message.direct() == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_RED_PACKET_ADDS : MESSAGE_TYPE_SEND_RED_PACKET_ADDS;
                } else if (message.getStringAttribute(Constant.MSGTYPE, "").equals(Constant.RECHARGE)) {
                    //转账
                    return message.direct() == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_RED_PACKET_RECHARGE : MESSAGE_TYPE_SEND_RED_PACKET_RECHARGE;
                } else if (message.getStringAttribute(Constant.MSGTYPE, "").equals(Constant.PUNISHMENT)) {
                    //惩罚
                    return message.direct() == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_RED_PACKET_PUNISHMENT : MESSAGE_TYPE_SEND_RED_PACKET_PUNISHMENT;
                } else if (message.getStringAttribute(Constant.MSGTYPE, "").equals(Constant.WELFARE)) {
                    //福利
                    return message.direct() == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_RED_PACKET_FL : MESSAGE_TYPE_SEND_RED_PACKET_FL;
                } else if (message.getStringAttribute(Constant.MSGTYPE, "").equals(Constant.RETURNGO)) {
                    //福利
                    return message.direct() == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_RED_PACKET_RTH : MESSAGE_TYPE_SEND_RED_PACKET_RTH;
                } else if (message.getStringAttribute(Constant.MSGTYPE, "").equals(Constant.PROMOTION)) {
                    //晋级代理
                    return message.direct() == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_RED_PACKET_PROMOTION : MESSAGE_TYPE_SEND_RED_PACKET_PROMOTION;
                } else if (message.getStringAttribute(Constant.MSGTYPE, "").equals(Constant.RENAME)) {
                    //修改房间名称
                    return message.direct() == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_RED_PACKET_RENAME : MESSAGE_TYPE_SEND_RED_PACKET_RENAME;
                } else if (message.getStringAttribute(Constant.MSGTYPE, "").equals(Constant.WITHDRAW)) {
                    //提现审核通知
                    return message.direct() == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_WITHDRAW : MESSAGE_TYPE_SEND_WITHDRAW;
                } else if (message.getStringAttribute(Constant.MSGTYPE, "").equals(Constant.NOTICE)) {
                    //通知
                    return message.direct() == EMMessage.Direct.RECEIVE ? MESSAGE_NOTICE_RECV : MESSAGE_NOTICE_SEND;
                }

            }
            return 0;
        }

        @Override
        public EaseChatRowPresenter getCustomChatRow(EMMessage message,
                                                     int position, BaseAdapter adapter) {
            XLog.d("messageExt", message.ext().toString());
            if (message.getType() == EMMessage.Type.TXT) {
                // voice call or video call
                if (message.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_VOICE_CALL, false) ||
                        message.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_VIDEO_CALL, false)) {
                    EaseChatRowPresenter presenter = new EaseChatVoiceCallPresenter();
                    return presenter;
                } else if (message.getBooleanAttribute(Constant.MESSAGE_TYPE_RECALL, false)) {
                    EaseChatRowPresenter presenter = new EaseChatRecallPresenter();
                    return presenter;
                    //红包消息
                } else if (message.getStringAttribute(Constant.MSGTYPE, "").equals(Constant.REDPACKET)) {
                    EaseChatRowPresenter presenter = new ChatRedPacketPresenter();
                    return presenter;
                    //红包回执消息
                } else if (message.getStringAttribute(Constant.MSGTYPE, "").equals(Constant.RAB)) {
                    EaseChatRowPresenter presenter = new ChatRedPacketAckPresenter();
                    return presenter;
                    //红包抢完
                } else if (message.getStringAttribute(Constant.MSGTYPE, "").equals(Constant.RABEND)) {
                    EaseChatRowPresenter presenter = new ChatRedPacketFinalPresenter();
                    return presenter;
                    //金币退回
                } else if (message.getStringAttribute(Constant.MSGTYPE, "").equals(Constant.RETURNGOLD)) {
                    EaseChatRowPresenter presenter = new ChatRedPacketReturnPresenter();
                    return presenter;
                    //转账
                } else if (message.getStringAttribute(Constant.MSGTYPE, "").equals(Constant.TURN)) {
                    EaseChatRowPresenter presenter = new ChatRedPacketturnPresenter();
                    return presenter;
                    //加入房间
                } else if (message.getStringAttribute(Constant.MSGTYPE, "").equals(Constant.ADDUSER)) {
                    EaseChatRowPresenter presenter = new ChatRedPacketAddPresenter();
                    return presenter;
                    //惩罚
                } else if (message.getStringAttribute(Constant.MSGTYPE, "").equals(Constant.PUNISHMENT)) {
                    ChatRedPacketPunishmentPresenter presenter = new ChatRedPacketPunishmentPresenter();
                    presenter.setRoomInfo(roomInfo);
                    return presenter;
                    //福利包
                } else if (message.getStringAttribute(Constant.MSGTYPE, "").equals(Constant.WELFARE)) {
                    EaseChatRowPresenter presenter = new ChatRedPacketWelfarePresenter();
                    return presenter;
                    //红包退回
                } else if (message.getStringAttribute(Constant.MSGTYPE, "").equals(Constant.RETURNGO)) {
                    EaseChatRowPresenter presenter = new ChatRedPacketGoReturnPresenter();
                    return presenter;
                    //离开房间
                } else if (message.getStringAttribute(Constant.MSGTYPE, "").equals(Constant.DELUSER)) {
                    EaseChatRowPresenter presenter = new ChatRedPacketLeftPresenter();
                    return presenter;
                    //踢人
                } else if (message.getStringAttribute(Constant.MSGTYPE, "").equals(Constant.SHOTUSER)) {
                    EaseChatRowPresenter presenter = new ChatRedPacketTPresenter();
                    return presenter;
                    //创建房间
                } else if (message.getStringAttribute(Constant.MSGTYPE, "").equals(Constant.ADDROOM)) {
                    EaseChatRowPresenter presenter = new ChatRedPacketAddroomPresenter();
                    return presenter;
                    //多人加入房间
                } else if (message.getStringAttribute(Constant.MSGTYPE, "").equals(Constant.ADDUSERS)) {
                    EaseChatRowPresenter presenter = new ChatRedPacketAddAllPresenter();
                    return presenter;
                    //充值
                } else if (message.getStringAttribute(Constant.MSGTYPE, "").equals(Constant.RECHARGE)) {
                    EaseChatRowPresenter presenter = new ChatRechargePresenter();
                    return presenter;
                    //晋级代理
                } else if (message.getStringAttribute(Constant.MSGTYPE, "").equals(Constant.PROMOTION)) {
                    EaseChatRowPresenter presenter = new ChatPromotionPresenter();
                    return presenter;
                    //修改房间名称
                } else if (message.getStringAttribute(Constant.MSGTYPE, "").equals(Constant.RENAME)) {
                    EaseChatRowPresenter presenter = new ChatRedPacketUpRoomNamePresenter();
                    return presenter;
                    //提现审核通知
                } else if (message.getStringAttribute(Constant.MSGTYPE, "").equals(Constant.WITHDRAW)) {
                    EaseChatRowPresenter presenter = new ChatWithdrawPresenter();
                    return presenter;
                    //通知
                } else if (message.getStringAttribute(Constant.MSGTYPE, "").equals(Constant.NOTICE)) {
                    EaseChatRowPresenter presenter = new ChatNoticePresenter();
                    return presenter;
                }
            }
            return null;
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
