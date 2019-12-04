package com.haoniu.aixin.entity;

import java.util.List;

/**
 * 作   者：赵大帅
 * 描   述:
 * 邮   箱: 2510722254@qqq.com
 * 日   期: 2017/11/27 16:13
 * 更新日期: 2017/11/27
 */
public class RedPacketInfo {


    /**
     * amount : 7
     * createTime : 1547805069545
     * delFlag : 0
     * faJieStatus : 0
     * headImg :
     * id : 807
     * isWelfare : 1
     * money : 100
     * nickName : 帅
     * odds : 0
     * punishType : 1
     * redPacketNumberList : [{"createTime":1547805070000,"delFlag":0,"headImg":"","id":744,"isPtUser":1,"isPunish":1,"money":18.94,"nickName":"平台用户","punishMoney":0,"punishStatus":1,"rabTime":null,"redPacketId":807,"status":1,"thunderPoint":0,"userId":1,"welfare":0,"welfareType":""},{"createTime":1547805070000,"delFlag":0,"headImg":"","id":745,"isPtUser":0,"isPunish":1,"money":23.5,"nickName":"帅","punishMoney":0,"punishStatus":0,"rabTime":1547805081000,"redPacketId":807,"status":1,"thunderPoint":0,"userId":11,"welfare":0,"welfareType":""},{"createTime":1547805070000,"delFlag":0,"headImg":"/upload/img/default_avatar.png","id":746,"isPtUser":0,"isPunish":1,"money":15.88,"nickName":"爱信爱信机器人195","punishMoney":0,"punishStatus":0,"rabTime":1547805084000,"redPacketId":807,"status":1,"thunderPoint":0,"userId":100,"welfare":0,"welfareType":""}]
     * remark :
     * roomId : 117
     * status : 0
     * thunderPoint : 2
     * type : 2
     * updateTime : null
     * userId : 11
     * welfareType :
     */

    private int amount;
    private long createTime;
    private int delFlag;
    private int faJieStatus;
    private String headImg;
    private int id;
    private int isWelfare;
    private double money;
    private String nickName;
    private int odds;
    private int punishType;
    private String remark;
    private int roomId;
    private int status;
    private String thunderPoint;
    private int type;
    private Object updateTime;
    private int userId;
    private String welfareType;
    private List<RedPacketNumberListBean> redPacketNumberList;

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public int getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(int delFlag) {
        this.delFlag = delFlag;
    }

    public int getFaJieStatus() {
        return faJieStatus;
    }

    public void setFaJieStatus(int faJieStatus) {
        this.faJieStatus = faJieStatus;
    }

    public String getHeadImg() {
        return headImg;
    }

    public void setHeadImg(String headImg) {
        this.headImg = headImg;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIsWelfare() {
        return isWelfare;
    }

    public void setIsWelfare(int isWelfare) {
        this.isWelfare = isWelfare;
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

    public int getOdds() {
        return odds;
    }

    public void setOdds(int odds) {
        this.odds = odds;
    }

    public int getPunishType() {
        return punishType;
    }

    public void setPunishType(int punishType) {
        this.punishType = punishType;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getThunderPoint() {
        return thunderPoint;
    }

    public void setThunderPoint(String thunderPoint) {
        this.thunderPoint = thunderPoint;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Object getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Object updateTime) {
        this.updateTime = updateTime;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getWelfareType() {
        return welfareType;
    }

    public void setWelfareType(String welfareType) {
        this.welfareType = welfareType;
    }

    public List<RedPacketNumberListBean> getRedPacketNumberList() {
        return redPacketNumberList;
    }

    public void setRedPacketNumberList(List<RedPacketNumberListBean> redPacketNumberList) {
        this.redPacketNumberList = redPacketNumberList;
    }

    public static class RedPacketNumberListBean {
        /**
         * createTime : 1547805070000
         * delFlag : 0
         * headImg :
         * id : 744
         * isPtUser : 1
         * isPunish : 1
         * money : 18.94
         * nickName : 平台用户
         * punishMoney : 0
         * punishStatus : 1
         * rabTime : String
         * redPacketId : 807
         * status : 1
         * thunderPoint : 0
         * userId : 1
         * welfare : 0.0
         * welfareType :
         */

        private long createTime;
        private int delFlag;
        private String headImg;
        private int id;
        private int isPtUser;
        private int isPunish;
        private double money;
        private String nickName;
        private double punishMoney;
        private int punishStatus;
        private long rabTime;
        private int redPacketId;
        private int status;
        private int thunderPoint;
        private int userId;
        private double welfare;
        private String welfareType;

        public long getCreateTime() {
            return createTime;
        }

        public void setCreateTime(long createTime) {
            this.createTime = createTime;
        }

        public int getDelFlag() {
            return delFlag;
        }

        public void setDelFlag(int delFlag) {
            this.delFlag = delFlag;
        }

        public String getHeadImg() {
            return headImg;
        }

        public void setHeadImg(String headImg) {
            this.headImg = headImg;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getIsPtUser() {
            return isPtUser;
        }

        public void setIsPtUser(int isPtUser) {
            this.isPtUser = isPtUser;
        }

        public int getIsPunish() {
            return isPunish;
        }

        public void setIsPunish(int isPunish) {
            this.isPunish = isPunish;
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

        public double getPunishMoney() {
            return punishMoney;
        }

        public void setPunishMoney(double punishMoney) {
            this.punishMoney = punishMoney;
        }

        public int getPunishStatus() {
            return punishStatus;
        }

        public void setPunishStatus(int punishStatus) {
            this.punishStatus = punishStatus;
        }

        public long getRabTime() {
            return rabTime;
        }

        public void setRabTime(long rabTime) {
            this.rabTime = rabTime;
        }

        public int getRedPacketId() {
            return redPacketId;
        }

        public void setRedPacketId(int redPacketId) {
            this.redPacketId = redPacketId;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public int getThunderPoint() {
            return thunderPoint;
        }

        public void setThunderPoint(int thunderPoint) {
            this.thunderPoint = thunderPoint;
        }

        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
        }

        public double getWelfare() {
            return welfare;
        }

        public void setWelfare(double welfare) {
            this.welfare = welfare;
        }

        public String getWelfareType() {
            return welfareType;
        }

        public void setWelfareType(String welfareType) {
            this.welfareType = welfareType;
        }
    }
}
