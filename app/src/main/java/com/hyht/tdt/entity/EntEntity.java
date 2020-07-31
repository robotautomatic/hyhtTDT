package com.hyht.tdt;


import java.io.Serializable;
import java.util.Date;

/**
 * Project Name: app
 * Package Name: com.hyth.app.entity
 * Created by Hello on 2020/06/16 下午 02:46
 * Version: 1.0
 * Copyright (c) 2018-2028 张维麟 All Rights Reserved
 */

public class EntEntity implements Serializable {

    private Integer id;

    private Integer entType;

    private String entCode;

    private String entName;

    private String entAttribute;

    private String entAddress;

    private String entOwner;

    private String entProperty;

    private String entImage;

    private String entAddition;

    private Integer coorNum;

    private String coorList;

    private Date createTime;

    @Override
    public String toString() {
        return "EntEntity{" +
                "id=" + id +
                ", entType=" + entType +
                ", entCode='" + entCode + '\'' +
                ", entName='" + entName + '\'' +
                ", entAttribute='" + entAttribute + '\'' +
                ", entAddress='" + entAddress + '\'' +
                ", entOwner='" + entOwner + '\'' +
                ", entProperty='" + entProperty + '\'' +
                ", entImage='" + entImage + '\'' +
                ", entAddition='" + entAddition + '\'' +
                ", coorNum=" + coorNum +
                ", coorList='" + coorList + '\'' +
                ", createTime=" + createTime +
                '}';
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getEntType() {
        return entType;
    }

    public void setEntType(Integer entType) {
        this.entType = entType;
    }

    public String getEntCode() {
        return entCode;
    }

    public void setEntCode(String entCode) {
        this.entCode = entCode;
    }

    public String getEntName() {
        return entName;
    }

    public void setEntName(String entName) {
        this.entName = entName;
    }

    public String getEntAttribute() {
        return entAttribute;
    }

    public void setEntAttribute(String entAttribute) {
        this.entAttribute = entAttribute;
    }

    public String getEntAddress() {
        return entAddress;
    }

    public void setEntAddress(String entAddress) {
        this.entAddress = entAddress;
    }

    public String getEntOwner() {
        return entOwner;
    }

    public void setEntOwner(String entOwner) {
        this.entOwner = entOwner;
    }

    public String getEntProperty() {
        return entProperty;
    }

    public void setEntProperty(String entProperty) {
        this.entProperty = entProperty;
    }

    public String getEntImage() {
        return entImage;
    }

    public void setEntImage(String entImage) {
        this.entImage = entImage;
    }

    public String getEntAddition() {
        return entAddition;
    }

    public void setEntAddition(String entAddition) {
        this.entAddition = entAddition;
    }

    public Integer getCoorNum() {
        return coorNum;
    }

    public void setCoorNum(Integer coorNum) {
        this.coorNum = coorNum;
    }

    public String getCoorList() {
        return coorList;
    }

    public void setCoorList(String coorList) {
        this.coorList = coorList;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public EntEntity() {
    }

    public EntEntity(Integer id, Integer entType, String entCode, String entName, String entAttribute, String entAddress, String entOwner, String entProperty, String entImage, String entAddition, Integer coorNum, String coorList, Date createTime) {
        this.id = id;
        this.entType = entType;
        this.entCode = entCode;
        this.entName = entName;
        this.entAttribute = entAttribute;
        this.entAddress = entAddress;
        this.entOwner = entOwner;
        this.entProperty = entProperty;
        this.entImage = entImage;
        this.entAddition = entAddition;
        this.coorNum = coorNum;
        this.coorList = coorList;
        this.createTime = createTime;
    }
}
