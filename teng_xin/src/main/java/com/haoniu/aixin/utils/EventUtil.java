package com.haoniu.aixin.utils;

/**
 * 作   者：赵大帅
 * 描   述:
 * 邮   箱: 2510722254@qqq.com
 * 日   期: 2017/11/15 15:52
 * 更新日期: 2017/11/15
 */
public class EventUtil {

    //token 失效退出登录
    public static final int LOSETOKEN = 1;

    //支付结果
    public static final int PAYSUCCESS = 2;

    //转账
    public static final int TRANSFERMESSAGE = 3;

    //验证密码修改
    public static final int CLOSE1 = 4;
    //验证密码忘记
    public static final int CLOSE2 = 5;
    //刷新群组
    public static final int FLUSHGROUP = 6;

    //刷新群组
    public static final int FLUSHBANNER = 7;

    //注册 chat
    public static final int REGISTERBUTTON = 8;
    //刷新用户信息
    public static final int FLUSHUSERINFO = 9;
    //刷新公告
    public static final int FLUSHNOTICE = 10;

    /**
     * 授权登成功
     */
    public static final int WXLOGOINSUCCESS = 11;
    /**
     * 授权登录失败
     */
    public static final int WXLOGOINERROR = 12;

    //to rigister
    public static final int TOREGISTER = 13;

    //关闭登录
    public static final int CLOSELOGIN = 14;

    /**
     * 刷新审核列表信息
     */
    public static final int FLUSHAUDIT = 14;

    /**
     * 刷新消息数量
     */
    public static final int UNREADCOUNT = 15;

    /**
     * 刷新系统通知
     */
    public static final int FLUSHXTTZ = 16;
    /**
     * 修改房间名称
     */
    public static final int FLUSHRENAME = 17;

    /**
     * 刷新平台消息数量
     */
    public static final int NOTICNUM = 18;
    /**
     * 刷新审核未读数量
     */
    public static final int APPLYNUMER = 19;

    /**
     * 客服
     */
    public static final int KEFU = 20;

    /**
     *  转账
     */
    public static final int TONGXUNLU=21;


    /**
     * 刷新好友信息
     */
    public static final int FLUSHFRIEND=22;

    /**
     * 刷新信息
     */
    public static final int FLUSHMESSAGE=23;

}
