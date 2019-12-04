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
import com.haoniu.aixin.widget.chatrow.EaseChatRow;
import com.hyphenate.chat.EMMessage;

public class ChatRedAckPacket extends EaseChatRow {
    private TextView tv_message;
    private LinearLayout ll_container;

    public ChatRedAckPacket(Context context, EMMessage message, int position, BaseAdapter adapter) {
        super(context, message, position, adapter);
    }

    @Override
    protected void onInflateView() {
        if (message.getStringAttribute(Constant.MSGTYPE, "").equals(Constant.RAB)) {
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
        String currentUser = MyApplication.getInstance().getUserInfo().getUserId() + "";
        String loginName = message.getStringAttribute("id", "");//id
        String loginNickname = message.getStringAttribute(Constant.NICKNAME, "");//昵称
        String sendLoginName = message.getStringAttribute("sendid", "");//发红包的id
        String sendLoginNickName = message.getStringAttribute("sendnickname", "");//发红包的昵称
        ll_container.setVisibility(VISIBLE);
        if (message.getChatType().equals(EMMessage.ChatType.ChatRoom)) {
            if (loginName.equals(currentUser)) {
                String message = "";
                if (loginName.equals(sendLoginName)) {
                    message = getResources().getString(R.string.msg_take_red_packet);
                } else {
                    message = String.format(getResources().getString(R.string.msg_take_someone_red_packet), sendLoginNickName);
                }
                final SpannableStringBuilder sp = new SpannableStringBuilder(message);
                sp.setSpan(new ForegroundColorSpan(0xffFA9E3B), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); //字体颜色
                sp.setSpan(new ForegroundColorSpan(0xffFA9E3B), 4, message.length() - 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); //字体颜色
                tv_message.setText(sp);
            } else if (sendLoginName.equals(currentUser)) {
                String message = String.format(getResources().getString(R.string.msg_someone_take_red_packet), loginNickname, "你");
                final SpannableStringBuilder sp = new SpannableStringBuilder(message);
                sp.setSpan(new ForegroundColorSpan(0xffFA9E3B), 0, loginNickname.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); //字体颜色
                sp.setSpan(new ForegroundColorSpan(0xffFA9E3B), loginNickname.length() + 3, loginNickname.length() + 3 + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); //字体颜色
                tv_message.setText(sp);
            } else {
                String message = String.format(getResources().getString(R.string.msg_someone_take_red_packet), loginNickname, sendLoginNickName);
                final SpannableStringBuilder sp = new SpannableStringBuilder(message);
                sp.setSpan(new ForegroundColorSpan(0xffFA9E3B), 0, loginNickname.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); //字体颜色
                sp.setSpan(new ForegroundColorSpan(0xffFA9E3B), loginNickname.length() + 3, loginNickname.length() + 3 + sendLoginNickName.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); //字体颜色
                tv_message.setText(sp);
                ll_container.setVisibility(GONE);
            }
        } else {
            String message = String.format(getResources().getString(R.string.msg_someone_take_red_packet), loginNickname, sendLoginNickName);
            final SpannableStringBuilder sp = new SpannableStringBuilder(message);
            sp.setSpan(new ForegroundColorSpan(0xffFA9E3B), 0, loginNickname.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); //字体颜色
            sp.setSpan(new ForegroundColorSpan(0xffFA9E3B), loginNickname.length() + 3, loginNickname.length() + 3 + sendLoginNickName.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); //字体颜色
            tv_message.setText(sp);
        }
    }


}
