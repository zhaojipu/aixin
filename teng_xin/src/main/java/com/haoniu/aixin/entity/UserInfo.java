package com.haoniu.aixin.entity;


import com.haoniu.aixin.base.Constant;

import java.io.Serializable;


/**
 * 用户信息
 *
 * @author Administrator
 */

public class UserInfo implements Serializable {


    /**
     * freezeMoney : 0.0
     * isPayPsd : 0
     * money : 0.0
     * nickName : 帅
     * phone : 15385136772
     * token : 388AA363495D954CA7138A93AB874F89
     * userId : 11
     * userImg :
     */

    private double freezeMoney;//冻结金额
    private int isPayPsd;//是否有支付密码 0没有 1 有
    private double money;//余额
    private String nickName;
    private String phone;
    private String token;
    private int userId;
    private String userImg;
    private String myPassword;
    private String realName;
    private String inviteCode;
    private String headImg;
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getHeadImg() {
        return headImg;
    }

    public void setHeadImg(String headImg) {
        this.headImg = headImg;
    }

    public String getInviteCode() {
        return inviteCode;
    }

    public void setInviteCode(String inviteCode) {
        this.inviteCode = inviteCode;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getIdh() {
        return userId + Constant.ID_REDPROJECT;
    }
    public String getIdhs() {
        return id + Constant.ID_REDPROJECT;
    }

    public String getMyPassword() {
        return myPassword;
    }

    public void setMyPassword(String myPassword) {
        this.myPassword = myPassword;
    }

    public double getFreezeMoney() {
        return freezeMoney;
    }

    public void setFreezeMoney(double freezeMoney) {
        this.freezeMoney = freezeMoney;
    }

    public int getIsPayPsd() {
        return isPayPsd;
    }

    public void setIsPayPsd(int isPayPsd) {
        this.isPayPsd = isPayPsd;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserImg() {
        return userImg;
    }

    public void setUserImg(String userImg) {
        this.userImg = userImg;
    }
}
