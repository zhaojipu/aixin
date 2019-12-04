package com.haoniu.aixin.base;

import com.haoniu.aixin.BuildConfig;
import com.haoniu.aixin.utils.NumberFormatUtil;

/**
 * 作   者：赵大帅
 * 描   述: 全局变量
 * 日   期: 2018/1/2 9:36
 * 更新日期: 2018/1/2
 *
 * @author 赵大帅
 */
public class Constant extends EaseConstant {

    /**
     * 环信
     */
    public static final String NEW_FRIENDS_USERNAME = "item_new_friends";
    public static final String GROUP_USERNAME = "item_groups";
    public static final String CHAT_ROOM = "item_chatroom";
    public static final String ACCOUNT_REMOVED = "account_removed";
    public static final String ACCOUNT_CONFLICT = "conflict";
    public static final String ACCOUNT_FORBIDDEN = "user_forbidden";
    public static final String ACCOUNT_KICKED_BY_CHANGE_PASSWORD = "kicked_by_change_password";
    public static final String ACCOUNT_KICKED_BY_OTHER_DEVICE = "kicked_by_another_device";
    public static final String CHAT_ROBOT = "item_robots";
    public static final String ACTION_GROUP_CHANAGED = "action_group_changed";
    public static final String ACTION_CONTACT_CHANAGED = "action_contact_changed";


    /**
     * 加密key
     */
    public static final String SMS_ENCRYPT_KEY = "QTTjJhntSqETavDu";
    /**
     * 标识消息已读
     */
    public static final String MSG_READ = "1";
    /**
     * 群消息免打扰
     */
    public static final String GROUP_MSG_SET = "1";
    /**
     * 群消息正常
     */
    public static final String GROUP_MSG_SET_NO = "0";

    /**
     * 服务大厅 类型 1、客服2、通知3、公告4、接龙玩法说明 5、踩雷玩法说明
     */
    public static final String CUSTOM_KF = "1";
    public static final String CUSTOM_TZ = "2";
    public static final String CUSTOM_GG = "3";
    public static final String CUSTOM_JL = "4";
    public static final String CUSTOM_CL = "5";

    /**
     * 平台公告未读消息数量
     */
    public static final String UNREADCOUNT = "unReadCount";

    /**
     * 环信使用别名
     */
    public static final String ID_REDPROJECT = BuildConfig.HXBIEMING;

    public static String getTitleInv(int Invite) {
        return NumberFormatUtil.formatInteger(Invite+1)+"级推荐";
    }
    // http code
    /**
     * 成功
     */
    public static final int CODESUCCESS = 200;
    /**
     * 请求失败 错误
     */
    public static final int CODEERROR = -1;
    /**
     * token 异常
     */
    public static final int CODETOKENERROR = 401;
    /**
     * 微信授权失败
     */
    public static final int CODEWXERROR = 300;
    /**
     * 请填写注册信息（未注册）
     */
    public static final int CODENORIGISTER = 301;
    //修改房间名称
    public static final String RENAME = "rename";

    /**
     * 自定义消息类型
     */
    public static final String MSGTYPE = "msgType";
    /**
     * 消息类型 抢包
     */
    public static final String RAB = "rab";
    /**
     * 消息类型 发红包
     */
    public static final String REDPACKET = "redpacket";
    /**
     * 红包抢完
     */
    public static final String RABEND = "rabend";
    /**
     * 红包退回
     */
    public static final String RETURNGOLD = "returngold";
    /**
     * 提现通知
     */
    public static final String WITHDRAW = "withdrawal";
    /**
     * 通知
     */
    public static final String NOTICE = "registerMoney";
    /**
     * 转账
     */
    public static final String TURN = "turn";
    /**
     * 加入房间
     */
    public static final String ADDUSER = "adduser";
    /**
     * 惩罚
     */
    public static final String PUNISHMENT = "punishment";
    /**
     * 福利包
     */
    public static final String WELFARE = "welfare";
    /**
     * 红包退回
     */
    public static final String RETURNGO = "returngo";
    /**
     * 退出房间
     */
    public static final String DELUSER = "deluser";
    /**
     * 踢人
     */
    public static final String SHOTUSER = "shotuser";
    /**
     * 多人加入房间
     */
    public static final String ADDUSERS = "addusers";
    /**
     * 创建房间
     */
    public static final String ADDROOM = "addroom";
    /**
     * 房间头像
     */
    public static final String ROOMURL = "roomUrl";
    /**
     * 充值
     */
    public static final String RECHARGE = "recharge";
    /**
     * 晋级代理
     */
    public static final String PROMOTION = "promotion";


    /**
     * 拓展红包消息字段 红包Id
     */
    public static final String REDPACKETID = "redpacketid";

    /**
     * 头像
     */
    public static final String AVATARURL = "avatarurl";
    /**
     * 昵称
     */
    public static final String NICKNAME = "nickname";
    /**
     * 福利包类型
     */
    public static final String WELFARETYPE = "welfareType";


    /**
     * 房间类型
     */
    public static final String ROOMTYPE = "roomType";
    /**
     * 踩雷
     */
    public static final int CAILEI = 2;

    /**
     * 系统用户
     */
    public static final String ADMIN = "admin";
    /**
     * 用户类型
     */
    public static final String SERVICE = "service";
    /**
     * 昵称
     */
    public static final String NAME = "name";
    /**
     * 昵称
     */
    public static final String NICK = "nick";


    /**
     * 微信appId
     */
    public static final String WXAPPID = "wx5d425a3b75a30734";
    /**
     * 微信AppSecret
     */
    public static final String WXAPPSECRET = "64020361b8ec4c99936c0e3999a9f249";

    /**
     * 审核状态
     * 审核中
     */
    public static int AUDITSTATEUNDERREVIEW = 3;
    /**
     * 审核状态
     * 被拒绝
     */
    public static int AUDITSTATEBEREJECTED = 2;
    /**
     * 审核状态
     * 审核通过
     */
    public static int AUDITSTATEPASSED = 1;

    /**
     * 一级代理查询 一级代理下普通会员 手续费
     */
    public static final String REFEREETYPE1 = "1";

    /**
     * 一级代理查询 二级代理及二级代理下普通会员 手续费
     */
    public static final String REFEREETYPE2 = "2";
    /**
     * 二级代理查询 二级代理下普通会员 手续费
     */
    public static final String REFEREETYPE3 = "3";

}
