package com.tesu.manicurehouse.bean;

import java.util.List;

/**
 * Created by Administrator on 2016/9/18 0018.
 */
public class VideoListBean {
    private int id;
    private String title;
    private int type;
    private int id_value;
    private String alias;
    private String avatar;
    private int play_cnt;
    private int collection_cnt;
    private int is_fee;
    private double fee;
    private String pics;
    private String video_url;
    private String cover_img;
    private int index_;

    public int getIndex_() {
        return index_;
    }

    public void setIndex_(int index_) {
        this.index_ = index_;
    }

    @Override
    public String toString() {
        return "VideoListBean{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", type=" + type +
                ", id_value=" + id_value +
                ", alias='" + alias + '\'' +
                ", avatar='" + avatar + '\'' +
                ", play_cnt=" + play_cnt +
                ", collection_cnt=" + collection_cnt +
                ", is_fee=" + is_fee +
                ", fee=" + fee +
                ", pics='" + pics + '\'' +
                ", video_url='" + video_url + '\'' +
                ", cover_img='" + cover_img + '\'' +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getId_value() {
        return id_value;
    }

    public void setId_value(int id_value) {
        this.id_value = id_value;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public int getPlay_cnt() {
        return play_cnt;
    }

    public void setPlay_cnt(int play_cnt) {
        this.play_cnt = play_cnt;
    }

    public int getCollection_cnt() {
        return collection_cnt;
    }

    public void setCollection_cnt(int collection_cnt) {
        this.collection_cnt = collection_cnt;
    }

    public int getIs_fee() {
        return is_fee;
    }

    public void setIs_fee(int is_fee) {
        this.is_fee = is_fee;
    }

    public double getFee() {
        return fee;
    }

    public void setFee(double fee) {
        this.fee = fee;
    }

    public String getPics() {
        return pics;
    }

    public void setPics(String pics) {
        this.pics = pics;
    }

    public String getVideo_url() {
        return video_url;
    }

    public void setVideo_url(String video_url) {
        this.video_url = video_url;
    }

    public String getCover_img() {
        return cover_img;
    }

    public void setCover_img(String cover_img) {
        this.cover_img = cover_img;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VideoListBean that = (VideoListBean) o;

        return id == that.id;

    }

    @Override
    public int hashCode() {
        return id;
    }
}
