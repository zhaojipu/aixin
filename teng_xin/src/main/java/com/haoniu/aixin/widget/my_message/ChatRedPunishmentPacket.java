package com.haoniu.aixin.widget.my_message;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.haoniu.aixin.R;
import com.haoniu.aixin.base.Constant;
import com.haoniu.aixin.base.MyApplication;
import com.haoniu.aixin.entity.RoomInfo;
import com.haoniu.aixin.widget.chatrow.EaseChatRow;
import com.hyphenate.chat.EMMessage;
import com.zds.base.log.XLog;

public class ChatRedPunishmentPacket extends EaseChatRow {
    private TextView tv_message;
    private LinearLayout ll_container;
    private RoomInfo mRoomInfo;

    public ChatRedPunishmentPacket(Context context, EMMessage message, int position, BaseAdapter adapter, RoomInfo mRoomInfo) {
        super(context, message, position, adapter);
        this.mRoomInfo = mRoomInfo;
    }

    @Override
    protected void onInflateView() {
        if (message.getStringAttribute(Constant.MSGTYPE, "").equals(Constant.PUNISHMENT)) {
            inflater.inflate(message.direct() == EMMessage.Direct.RECEIVE ?
                    R.layout.row_red_packet_ack_message : R.layout.row_red_packet_ack_message, this);
        }
    }

    @Override
    protected void onFindViewById() {
        tv_message = (TextView) findViewById(R.id.tv_money_msg);
        ll_container = (LinearLayout) findViewById(R.id.ll_container);
    }

    /**
     * refresh view when message status change
     *
     * @param msg
     */
    @Override
    protected void onViewUpdate(EMMessage msg) {
    }

    @Override
    protected void onSetUpView() {
        XLog.d("messages", message.ext().toString());
        ll_container.setVisibility(VISIBLE);
        if (message.getChatType().equals(EMMessage.ChatType.ChatRoom)) {
            String name = message.getStringAttribute(Constant.NICK, "");
            String punishMsg = message.getStringAttribute("punishMsg", "");//成发消息内容
            if (message.getStringAttribute("id", "").equals(MyApplication.getInstance().getUserInfo().getUserId() + "")) {
                name = "你";
            }
            String messageStr = name + punishMsg;
            if (name.length() > 0 && messageStr.length() > 0) {
                final SpannableStringBuilder sp = new SpannableStringBuilder(messageStr);
                sp.setSpan(new ForegroundColorSpan(0xffFA9E3B), 0, name.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); //字体颜色
                tv_message.setText(sp);
            } else {
                tv_message.setText(messageStr);
            }
        }
    }

}
