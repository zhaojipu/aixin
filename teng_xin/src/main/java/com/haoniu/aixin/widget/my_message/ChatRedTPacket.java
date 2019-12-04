package com.haoniu.aixin.widget.my_message;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.haoniu.aixin.R;
import com.haoniu.aixin.base.Constant;
import com.haoniu.aixin.widget.chatrow.EaseChatRow;
import com.hyphenate.chat.EMMessage;

/**
 * 作   者：赵大帅
 * 描   述: 踢人
 * 日   期: 2017/11/30 19:08
 * 更新日期: 2017/11/30
 */
public class ChatRedTPacket extends EaseChatRow {
    private TextView tv_message;
    private LinearLayout ll_container;
    private ImageView img_red;

    public ChatRedTPacket(Context context, EMMessage message, int position, BaseAdapter adapter) {
        super(context, message, position, adapter);
    }

    @Override
    protected void onInflateView() {
        if (message.getStringAttribute(Constant.MSGTYPE, "").equals(Constant.SHOTUSER)) {
            inflater.inflate(message.direct() == EMMessage.Direct.RECEIVE ?
                    R.layout.row_red_packet_ack_message : R.layout.row_red_packet_ack_message, this);
        }
    }

    @Override
    protected void onFindViewById() {
        tv_message = (TextView) findViewById(R.id.tv_money_msg);
        ll_container = (LinearLayout) findViewById(R.id.ll_container);
        img_red = (ImageView) findViewById(R.id.img_red);
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
        ll_container.setVisibility(VISIBLE);
        img_red.setVisibility(GONE);

        if (message.getChatType().equals(EMMessage.ChatType.ChatRoom)) {
            String name = message.getStringAttribute(Constant.NAME, "");
            String messageStr = name + "被房主踢出了房间";
            if (name.length() > 0) {
                final SpannableStringBuilder sp = new SpannableStringBuilder(messageStr);
                sp.setSpan(new ForegroundColorSpan(0xffFA9E3B), 0, name.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); //字体颜色
                tv_message.setText(sp);
            } else {
                tv_message.setText(messageStr);
            }
        }
    }


}
