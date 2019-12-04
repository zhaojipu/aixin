package com.haoniu.aixin.widget.my_message;

import android.content.Context;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.haoniu.aixin.R;
import com.haoniu.aixin.base.Constant;
import com.haoniu.aixin.widget.chatrow.EaseChatRow;
import com.hyphenate.chat.EMMessage;

/**
 * 作   者：赵大帅
 * 描   述: 充值
 * 日   期: 2017/11/27 17:26
 * 更新日期: 2017/11/27
 */
public class ChatRechargePacket extends EaseChatRow {

    private TextView tv_message, tv_time;
    private LinearLayout ll_container;

    public ChatRechargePacket(Context context, EMMessage message, int position, BaseAdapter adapter) {
        super(context, message, position, adapter);
    }

    @Override
    protected void onInflateView() {
        if (message.getStringAttribute(Constant.MSGTYPE, "").equals(Constant.RECHARGE)) {
            inflater.inflate(message.direct() == EMMessage.Direct.RECEIVE ?
                    R.layout.chat_recharge : R.layout.chat_recharge, this);
        }
    }

    @Override
    protected void onFindViewById() {
        tv_message = (TextView) findViewById(R.id.tv_money);
        tv_time = (TextView) findViewById(R.id.tv_time);
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
        tv_message.setText(message.getIntAttribute("money", 0) + getResources().getString(R.string.glod));
        tv_time.setText(message.getStringAttribute("time", ""));
    }
}
