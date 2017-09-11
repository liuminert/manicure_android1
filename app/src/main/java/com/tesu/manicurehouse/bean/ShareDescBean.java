package com.tesu.manicurehouse.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/10/11 0011.
 */
public class ShareDescBean implements Serializable{
    public  String create_time;//	  邀请的时间
    public  String user_name	;//店主用户名

    public  String mobile_phone;//	电话search_type=0有值
    public  String time	;//获取积分时间search_type=1有值
    public  String type;//	String获取类型search_type=1有值
    public  int reward;//	获得积分search_type=1有值

    public String getMobile_phone() {
        return mobile_phone;
    }

    public void setMobile_phone(String mobile_phone) {
        this.mobile_phone = mobile_phone;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getReward() {
        return reward;
    }

    public void setReward(int reward) {
        this.reward = reward;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }
}
