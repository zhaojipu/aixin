package com.haoniu.aixin.widget.my_message;

import android.content.Context;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.haoniu.aixin.R;
import com.haoniu.aixin.base.Constant;
import com.haoniu.aixin.widget.chatrow.EaseChatRow;
import com.hyphenate.chat.EMMessage;
import com.zds.base.util.StringUtil;

/**
 * 作   者：赵大帅
 * 描   述: 红包消息
 * 日   期: 2017/11/21 9:12
 * 更新日期: 2017/11/21
 *
 * @author Administrator
 */
public class ChatBasicRedPacket extends EaseChatRow {
    private TextView tv_message, tv_time_message, tv_hongbao;
    private RelativeLayout bubble;

    public ChatBasicRedPacket(Context context, EMMessage message, int position, BaseAdapter adapter) {
        super(context, message, position, adapter);
    }

    @Override
    protected void onInflateView() {
        if (message.getStringAttribute(Constant.MSGTYPE, "").equals(Constant.REDPACKET)) {
            inflater.inflate(message.direct() == EMMessage.Direct.RECEIVE ?
                    R.layout.row_received_red_packet : R.layout.row_send_red_packet, this);
        }
    }

    @Override
    protected void onFindViewById() {
        tv_message = (TextView) findViewById(R.id.message);
        tv_time_message = (TextView) findViewById(R.id.tv_time_message);
        tv_hongbao = (TextView) findViewById(R.id.tv_hongbao);
        bubble= findViewById(R.id.bubble);
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
        String redBagInfo = message.getStringAttribute("redPacketType", "");
        boolean isClick=message.getBooleanAttribute("isClick",false);

        if (isClick){
            if (message.direct() == EMMessage.Direct.RECEIVE ){
                bubble.setBackgroundResource(R.mipmap.img_red_packet2);
            }else {
                bubble.setBackgroundResource(R.mipmap.img_red_packet_right2);
            }

        }else {
            if (message.direct() == EMMessage.Direct.RECEIVE ){
                bubble.setBackgroundResource(R.mipmap.img_red_packet);
            }else {
                bubble.setBackgroundResource(R.mipmap.img_red_packet_right);
            }

        }
        if ("1".equals(redBagInfo)) {
            tv_time_message.setText("");
            tv_message.setText("恭喜发财，大吉大利");
        } else if ("2".equals(redBagInfo)) {
            tv_time_message.setText("");
            tv_hongbao.setText("");
            tv_message.setText(StringUtil.getFormatValue(Double.valueOf(message.getStringAttribute("money", ""))) + "/" + message.getStringAttribute("thunderPoint", "").replaceAll(",", ""));
        } else if ("3".equals(redBagInfo)) {
            tv_time_message.setText("24小时有效");
            tv_hongbao.setText("");
            tv_message.setText(message.getStringAttribute("remark", "恭喜发财，大吉大利"));
        }
    }


}
