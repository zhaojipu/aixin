package com.haoniu.aixin.entity;

/**
 * 作   者：赵大帅
 * 描   述:
 * 邮   箱: 2510722254@qqq.com
 * 日   期: 2017/12/16 10:55
 * 更新日期: 2017/12/16
 */
public class ServiceInfo {


    /**
     * 头像
     */
    private String avatarUrl;
    /**
     * id
     */
    private int id;
    /**
     * 手机号
     */
    private String phone;
    /**
     * 创建时间
     */
    private String createTime;

    /**
     * 类型 1、客服2、通知3、公告
     */
    private String type;

    /**
     * 昵称
     */
    private String nickname;


    public ServiceInfo(String nickname, String type) {
        this.nickname = nickname;
        this.type = type;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
