package com.tesu.manicurehouse.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/10/11 0011.
 */
public class RecommendDescBean implements Serializable{
    public int reward;//	积分
    public  String create_time;//	  邀请的时间
    public  String user_name	;//店主用户名

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
