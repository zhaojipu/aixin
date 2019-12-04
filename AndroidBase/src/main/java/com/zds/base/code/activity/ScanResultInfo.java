package com.zds.base.code.activity;

import java.io.Serializable;

public class ScanResultInfo implements Serializable {


    /**
     * name : 张三
     * phone : 18226655888
     * headImg : http://aixin.fb6614.cn:8055/fileUpload/user/1553063329047.JPEG
     */

    private String name;
    private String phone;
    private String headImg;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getHeadImg() {
        return headImg;
    }

    public void setHeadImg(String headImg) {
        this.headImg = headImg;
    }
}
