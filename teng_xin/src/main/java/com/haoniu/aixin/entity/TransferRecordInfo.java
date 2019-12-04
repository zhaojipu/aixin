package com.haoniu.aixin.entity;

/**
 * 作   者：赵大帅
 * 描   述:
 * 邮   箱: 2510722254@qqq.com
 * 日   期: 2017/12/13 16:42
 * 更新日期: 2017/12/13
 */
public class TransferRecordInfo {


    /**
     * avatarUrl : /uploadFile/avatar/[2017-12-08-17-44-54]_0.16113347338726813.jpg
     * createTime : 2017-12-11 15:38:24
     * friendAvatarUrl : /uploadFile/avatar/[2017-12-11-15-41-15]_0.11610638652489269.jpg
     * friendName : 会员6772
     * friendUserId : 45
     * gatherMoney : 1000
     * id : 15
     * money : 1000
     * proxyOne : 0
     * userId : 7
     * userName : 帅帅
     * withdrawCash : 0
     * withdrawCashOne : 0
     * withdrawCashSys : 0
     */

    private String avatarUrl;
    private String createTime;
    private String friendAvatarUrl;
    private String friendName;
    private int friendUserId;
    private double gatherMoney;
    private int id;
    private double money;
    private int proxyOne;
    private int userId;
    private String userName;
    private double withdrawCash;
    private double withdrawCashOne;
    private double withdrawCashSys;

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getFriendAvatarUrl() {
        return friendAvatarUrl;
    }

    public void setFriendAvatarUrl(String friendAvatarUrl) {
        this.friendAvatarUrl = friendAvatarUrl;
    }

    public String getFriendName() {
        return friendName;
    }

    public void setFriendName(String friendName) {
        this.friendName = friendName;
    }

    public int getFriendUserId() {
        return friendUserId;
    }

    public void setFriendUserId(int friendUserId) {
        this.friendUserId = friendUserId;
    }

    public double getGatherMoney() {
        return gatherMoney;
    }

    public void setGatherMoney(double gatherMoney) {
        this.gatherMoney = gatherMoney;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public int getProxyOne() {
        return proxyOne;
    }

    public void setProxyOne(int proxyOne) {
        this.proxyOne = proxyOne;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public double getWithdrawCash() {
        return withdrawCash;
    }

    public void setWithdrawCash(double withdrawCash) {
        this.withdrawCash = withdrawCash;
    }

    public double getWithdrawCashOne() {
        return withdrawCashOne;
    }

    public void setWithdrawCashOne(double withdrawCashOne) {
        this.withdrawCashOne = withdrawCashOne;
    }

    public double getWithdrawCashSys() {
        return withdrawCashSys;
    }

    public void setWithdrawCashSys(double withdrawCashSys) {
        this.withdrawCashSys = withdrawCashSys;
    }
}
