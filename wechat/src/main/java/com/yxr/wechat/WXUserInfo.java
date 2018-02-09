package com.yxr.wechat;

/**
 * 微信用户信息
 * Created by 63062 on 2018/2/9.
 */

public class WXUserInfo {

    private String unionId;
    private String name;
    private String avatar;
    private String sex;
    private String province;
    private String country;
    private String city;

    public WXUserInfo() {

    }

    public WXUserInfo(String unionId, String name, String avatar, String sex, String province, String country, String city) {
        this.unionId = unionId;
        this.name = name;
        this.avatar = avatar;
        this.sex = sex;
        this.province = province;
        this.country = country;
        this.city = city;
    }

    public String getUnionId() {
        return unionId;
    }

    public void setUnionId(String unionId) {
        this.unionId = unionId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
