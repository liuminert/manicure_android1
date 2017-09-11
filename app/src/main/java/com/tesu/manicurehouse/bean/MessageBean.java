package com.tesu.manicurehouse.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/10/13 0013.
 */
public class MessageBean implements Serializable{
    private int msg_id;
    private int from_user_id;
    private String from_user_alias;
    private int to_user_id;
    private String to_user_alias;
    private String title;
    private String content;
    private int is_read;   //是否已读    0否1是
    private long create_time;
    private boolean isShow;  //是否显示详情

    @Override
    public String toString() {
        return "MessageBean{" +
                "msg_id=" + msg_id +
                ", from_user_id=" + from_user_id +
                ", from_user_alias='" + from_user_alias + '\'' +
                ", to_user_id=" + to_user_id +
                ", to_user_alias='" + to_user_alias + '\'' +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", is_read=" + is_read +
                ", create_time=" + create_time +
                ", isShow=" + isShow +
                '}';
    }

    public int getMsg_id() {
        return msg_id;
    }

    public void setMsg_id(int msg_id) {
        this.msg_id = msg_id;
    }

    public int getFrom_user_id() {
        return from_user_id;
    }

    public void setFrom_user_id(int from_user_id) {
        this.from_user_id = from_user_id;
    }

    public String getFrom_user_alias() {
        return from_user_alias;
    }

    public void setFrom_user_alias(String from_user_alias) {
        this.from_user_alias = from_user_alias;
    }

    public int getTo_user_id() {
        return to_user_id;
    }

    public void setTo_user_id(int to_user_id) {
        this.to_user_id = to_user_id;
    }

    public String getTo_user_alias() {
        return to_user_alias;
    }

    public void setTo_user_alias(String to_user_alias) {
        this.to_user_alias = to_user_alias;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getIs_read() {
        return is_read;
    }

    public void setIs_read(int is_read) {
        this.is_read = is_read;
    }

    public long getCreate_time() {
        return create_time;
    }

    public void setCreate_time(long create_time) {
        this.create_time = create_time;
    }

    public boolean isShow() {
        return isShow;
    }

    public void setIsShow(boolean isShow) {
        this.isShow = isShow;
    }
}
