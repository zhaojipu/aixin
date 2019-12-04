package com.haoniu.aixin.entity;

import com.haoniu.aixin.base.Constant;
import com.hyphenate.chat.EMConversation;

import java.io.Serializable;
import java.util.List;

/**
 * 作   者：赵大帅
 * 描   述: 房间
 * 日   期: 2017/11/15 10:45
 * 更新日期: 2017/11/15
 *
 * @author Administrator
 */

public class RoomInfo implements Serializable {


    /**
     * amount : 0
     * createTime : 1548659691000
     * delFlag : 0
     * huanxinGroupId : 72625726488577
     * id : 131
     * isWelfare : 0
     * name : 多雷房间
     * notice :
     * odds : 0
     * personal : 0
     * roomImg : /room/1548665695935.jpg
     * roomLeiList : [{"amount":7,"createTime":1548659691000,"delFlag":0,"eightLei":0,"fiveLei":0,"fourLei":1.02,"id":10,"moneyMax":20,"moneyMin":10,"nineLei":0,"oneLei":1.6,"roomId":131,"sevenLei":0,"sixLei":0,"threeLei":0,"twoLei":0,"type":"1","updateTime":1548659996000},{"amount":9,"createTime":1548659691000,"delFlag":0,"eightLei":2.2,"fiveLei":2.2,"fourLei":1.7,"id":11,"moneyMax":20,"moneyMin":10,"nineLei":2.2,"oneLei":1.6,"roomId":131,"sevenLei":2.2,"sixLei":2.2,"threeLei":1.25,"twoLei":1,"type":"2","updateTime":1548659998000}]
     * roomUserNum : 14
     * type : 2
     * updateTime : 1548659987000
     * userId : 0
     * welfareType :
     */

    private int isPassword;

    public int getIsPassword() {
        return isPassword;
    }

    public void setIsPassword(int isPassword) {
        this.isPassword = isPassword;
    }
    private int amount;
    private long createTime;
    private int delFlag;
    private String huanxinGroupId;
    private int id;
    private int isWelfare;
    private String name;
    private String notice;
    private int odds;
    private int personal;
    private String roomImg;
    private int roomUserNum;
    private int type;
    private long updateTime;
    private int userId;
    private String welfareType;
    private List<RoomLeiListBean> roomLeiList;
    private EMConversation emConversation;
    private List<RoomBossDTOListBean> roomBossDTOList;

    public EMConversation getEmConversation() {
        return emConversation;
    }

    public void setEmConversation(EMConversation emConversation) {
        this.emConversation = emConversation;
    }

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

    public String getHuanxinGroupId() {
        return huanxinGroupId;
    }

    public void setHuanxinGroupId(String huanxinGroupId) {
        this.huanxinGroupId = huanxinGroupId;
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

    public String getName() {
        return name==null?"":name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNotice() {
        return notice;
    }

    public void setNotice(String notice) {
        this.notice = notice;
    }

    public int getOdds() {
        return odds;
    }

    public void setOdds(int odds) {
        this.odds = odds;
    }

    public int getPersonal() {
        return personal;
    }

    public void setPersonal(int personal) {
        this.personal = personal;
    }

    public String getRoomImg() {
        return roomImg;
    }

    public void setRoomImg(String roomImg) {
        this.roomImg = roomImg;
    }

    public int getRoomUserNum() {
        return roomUserNum;
    }

    public void setRoomUserNum(int roomUserNum) {
        this.roomUserNum = roomUserNum;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
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

    public List<RoomLeiListBean> getRoomLeiList() {
        return roomLeiList;
    }

    public void setRoomLeiList(List<RoomLeiListBean> roomLeiList) {
        this.roomLeiList = roomLeiList;
    }

    public List<RoomBossDTOListBean> getRoomBossDTOList() {
        return roomBossDTOList;
    }

    public void setRoomBossDTOList(List<RoomBossDTOListBean> roomBossDTOList) {
        this.roomBossDTOList = roomBossDTOList;
    }



    public static class RoomLeiListBean implements Serializable{
        /**
         * amount : 7
         * createTime : 1548659691000
         * delFlag : 0
         * eightLei : 0.0
         * fiveLei : 0.0
         * fourLei : 1.02
         * id : 10
         * moneyMax : 20.0
         * moneyMin : 10.0
         * nineLei : 0.0
         * oneLei : 1.6
         * roomId : 131
         * sevenLei : 0.0
         * sixLei : 0.0
         * threeLei : 0.0
         * twoLei : 0.0
         * type : 1
         * updateTime : 1548659996000
         */

        private int amount1 = 9;
        private int amount;
        private long createTime;
        private int delFlag;
        private double eightLei;
        private double fiveLei;
        private double fourLei;
        private int id;
        private double moneyMax;
        private double moneyMin;
        private double nineLei;
        private double oneLei;
        private int roomId;
        private double sevenLei;
        private double sixLei;
        private double threeLei;
        private double twoLei;
        private String type;
        private long updateTime;

        public int getAmount1() {
            return amount1;
        }

        public void setAmount1(int amount1) {
            this.amount1 = amount1;
        }

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

        public double getEightLei() {
            return eightLei;
        }

        public void setEightLei(double eightLei) {
            this.eightLei = eightLei;
        }

        public double getFiveLei() {
            return fiveLei;
        }

        public void setFiveLei(double fiveLei) {
            this.fiveLei = fiveLei;
        }

        public double getFourLei() {
            return fourLei;
        }

        public void setFourLei(double fourLei) {
            this.fourLei = fourLei;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public double getMoneyMax() {
            return moneyMax;
        }

        public void setMoneyMax(double moneyMax) {
            this.moneyMax = moneyMax;
        }

        public double getMoneyMin() {
            return moneyMin;
        }

        public void setMoneyMin(double moneyMin) {
            this.moneyMin = moneyMin;
        }

        public double getNineLei() {
            return nineLei;
        }

        public void setNineLei(double nineLei) {
            this.nineLei = nineLei;
        }

        public double getOneLei() {
            return oneLei;
        }

        public void setOneLei(double oneLei) {
            this.oneLei = oneLei;
        }

        public int getRoomId() {
            return roomId;
        }

        public void setRoomId(int roomId) {
            this.roomId = roomId;
        }

        public double getSevenLei() {
            return sevenLei;
        }

        public void setSevenLei(double sevenLei) {
            this.sevenLei = sevenLei;
        }

        public double getSixLei() {
            return sixLei;
        }

        public void setSixLei(double sixLei) {
            this.sixLei = sixLei;
        }

        public double getThreeLei() {
            return threeLei;
        }

        public void setThreeLei(double threeLei) {
            this.threeLei = threeLei;
        }

        public double getTwoLei() {
            return twoLei;
        }

        public void setTwoLei(double twoLei) {
            this.twoLei = twoLei;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public long getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(long updateTime) {
            this.updateTime = updateTime;
        }
    }

    public static class RoomBossDTOListBean implements Serializable {
        /**
         * headImg : /upload/img/default_avatar.png
         * id : 7924
         * name : 30-100房间
         * nickName : 群主
         * phone : 17722233432
         */

        private String headImg;
        private int id;
        private String name;
        private String nickName;
        private String phone;


        public String getIdh() {
            return id + Constant.ID_REDPROJECT;
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

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
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
    }

}
