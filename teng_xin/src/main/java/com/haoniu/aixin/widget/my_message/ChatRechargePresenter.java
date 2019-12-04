package com.haoniu.aixin.widget.my_message;

import android.content.Context;
import android.widget.BaseAdapter;

import com.haoniu.aixin.widget.chatrow.EaseChatRow;
import com.haoniu.aixin.widget.presenter.EaseChatRowPresenter;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.exceptions.HyphenateException;

/**
 * 作   者：赵大帅
 * 描   述: 充值
 * 日   期: 2017/11/21 9:12
 * 更新日期: 2017/11/21
 */

public class ChatRechargePresenter extends EaseChatRowPresenter {

    @Override
    protected EaseChatRow onCreateChatRow(Context cxt, EMMessage message, int position, BaseAdapter adapter) {
        return new ChatRechargePacket(cxt, message, position, adapter);
    }

    @Override
    protected void handleReceiveMessage(EMMessage message) {
        if (!message.isAcked() && message.getChatType() == EMMessage.ChatType.Chat) {
            try {
                EMClient.getInstance().chatManager().ackMessageRead(message.getFrom(), message.getMsgId());
            } catch (HyphenateException e) {
                e.printStackTrace();
            }
        }
    }
}
