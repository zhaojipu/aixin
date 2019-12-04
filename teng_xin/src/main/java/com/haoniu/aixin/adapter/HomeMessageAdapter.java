package com.haoniu.aixin.adapter;

import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.haoniu.aixin.R;
import com.haoniu.aixin.base.Constant;
import com.haoniu.aixin.base.MyApplication;
import com.haoniu.aixin.base.MyHelper;
import com.haoniu.aixin.entity.RoomInfo;
import com.haoniu.aixin.http.AppConfig;
import com.haoniu.aixin.model.EaseAtMessageHelper;
import com.haoniu.aixin.utils.EaseCommonUtils;
import com.haoniu.aixin.utils.EaseSmileUtils;
import com.haoniu.aixin.utils.EaseUserUtils;
import com.haoniu.aixin.widget.EaseConversationList;
import com.haoniu.aixin.widget.EaseImageView;
import com.hyphenate.chat.EMChatRoom;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.util.DateUtils;
import com.zds.base.ImageLoad.GlideUtils;

import java.util.Date;
import java.util.List;

/**
 * 消息
 */
public class HomeMessageAdapter extends BaseQuickAdapter<RoomInfo, BaseViewHolder> {

    public HomeMessageAdapter(List<RoomInfo> list) {
        super(R.layout.ease_row_chat_history, list);
    }

    private EaseConversationList.EaseConversationListHelper cvsListHelper;

    public void setCvsListHelper(EaseConversationList.EaseConversationListHelper cvsListHelper) {
        this.cvsListHelper = cvsListHelper;
    }

    @Override
    protected void convert(BaseViewHolder helper, RoomInfo item) {

        helper.getView(R.id.list_itease_layout).setBackgroundResource(R.drawable.ease_mm_listitem);

        // 环信消息
        EMConversation conversation = item.getEmConversation();
        if (conversation!=null) {
            //消息ID
            String username = conversation.conversationId();

            if (conversation.getType() == EMConversation.EMConversationType.ChatRoom) {
                String groupId = conversation.conversationId();
                if (EaseAtMessageHelper.get().hasAtMeMsg(groupId)) {
                    helper.setGone(R.id.mentioned,true);
                } else {
                    helper.setGone(R.id.mentioned,false);
                }
                // group message, show group avatar
                EMChatRoom group = EMClient.getInstance().chatroomManager().getChatRoom(username);
                GlideUtils.loadImageViewLoding(AppConfig.checkimg( MyHelper.getInstance().getGroupById(groupId).getHead()), helper.getView(R.id.avatar), R.mipmap.ic_launcher);
               if (item.getName()==null){
                   helper.setText(R.id.name,item.getName());
               }else {
                   helper.setText(R.id.name,group != null ? group.getName() : username);
               }
            } else {
                if (username.equals(Constant.ADMIN)) {
                    helper.setText(R.id.name,mContext.getResources().getString(R.string.xttz));
                    GlideUtils.loadImageView(R.mipmap.ic_launcher, (ImageView) helper.getView(R.id.avatar));
                } else {
                    EaseUserUtils.setUserAvatar(mContext, username, (ImageView) helper.getView(R.id.avatar));
                    EaseUserUtils.setUserNick(username,(TextView) helper.getView(R.id.name));
                }
                helper.setGone(R.id.mentioned,false);
            }
            if (helper.getView(R.id.avatar) instanceof EaseImageView) {
                MyApplication.getInstance().setAvatars((EaseImageView) helper.getView(R.id.avatar));
            }
            if (conversation.getUnreadMsgCount() > 0) {
                // show unread message count
                helper.setText(R.id.unread_msg_number,String.valueOf(conversation.getUnreadMsgCount()));
                helper.setVisible(R.id.unread_msg_number,true);
            } else {
                helper.setGone(R.id.unread_msg_number,true);
                helper.setVisible(R.id.unread_msg_number,false);
            }
            if (conversation.getAllMsgCount() != 0) {
                // show the content of latest message
                EMMessage lastMessage = conversation.getLastMessage();
                if (lastMessage.getBooleanAttribute("cmd", false)) {
                    List<EMMessage> messages = conversation.getAllMessages();
                    int lent = messages.size() - 1;
                    for (int i = lent; i > 0; i--) {
                        if (messages.get(i).getBooleanAttribute("cmd", false)) {

                        } else {
                            lastMessage = messages.get(i);
                            break;
                        }
                    }
                }
                String content = null;
                if (cvsListHelper != null) {
                    content = cvsListHelper.onSetItemSecondaryText(lastMessage);
                }
                ((TextView)helper.getView(R.id.message)).setText(EaseSmileUtils.getSmiledText(mContext, EaseCommonUtils.getMessageDigest(lastMessage, mContext)),
                        TextView.BufferType.SPANNABLE);
                if (content != null) {
                    helper.setText(R.id.message,content);
                }
                helper.setText(R.id.time,DateUtils.getTimestampString(new Date(lastMessage.getMsgTime())));
                if (lastMessage.direct() == EMMessage.Direct.SEND && lastMessage.status() == EMMessage.Status.FAIL) {
                    helper.setGone(R.id.msg_state,true);
                } else {
                    helper.setGone(R.id.msg_state,false);
                }
            }

        }else {
            helper.setGone(R.id.mentioned,false);
            helper.setGone(R.id.msg_state,false);
            helper.setText(R.id.name,item.getName());
            GlideUtils.loadImageViewLoding(AppConfig.checkimg(item.getRoomImg()), helper.getView(R.id.avatar), R.mipmap.ic_launcher);
            helper.setText(R.id.message,"本群为扫雷游戏群，快来赚取大量金币！");
            helper.setVisible(R.id.unread_msg_number,false);
            helper.setText(R.id.time,DateUtils.getTimestampString(new Date()));

        }
    }

}

