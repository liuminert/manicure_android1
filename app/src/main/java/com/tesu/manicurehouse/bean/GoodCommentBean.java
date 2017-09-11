package com.tesu.manicurehouse.bean;

import java.util.List;

/**
 * Created by Administrator on 2016/9/20 0020.
 */
public class GoodCommentBean {
    private String comment_id;//	评论的id
    private String user_name;//	评论该商品的人的名称
    private String content;//评论的内容
    private String add_time;//评论的时间
    private String pics;//	[”图片url”,”图片url”,”图片url”]。（请调用4.1.1上传文件接口获取图片url）
    private String avatar;

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getComment_id() {
        return comment_id;
    }

    public void setComment_id(String comment_id) {
        this.comment_id = comment_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAdd_time() {
        return add_time;
    }

    public void setAdd_time(String add_time) {
        this.add_time = add_time;
    }

    public String getPics() {
        return pics;
    }

    public void setPics(String  pics) {
        this.pics = pics;
    }
}
