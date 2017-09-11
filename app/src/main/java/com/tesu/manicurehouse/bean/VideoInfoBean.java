package com.tesu.manicurehouse.bean;

import java.util.List;

/**
 * Created by Administrator on 2016/9/20 0020.
 */
public class VideoInfoBean {
    private int id;
    private int type;
    private int id_value;
    private String alias;
    private String avatar;
    private String video_url;
    private String subtitle_file_url;
    private String content;
    private String pics;
    private String title;
    private String tag_ids;
    private int is_fee;
    private double fee;
    private String goodList;
    private int play_cnt;
    private int collection_cnt;
    private int forward_cnt;
    private int liked_cnt;
    private String create_time;
    private int is_collection;
    private int is_liked;
    private int commentCnt;
    private String cover_img;
    private List<VideoLableBean> tagDataLists;

    @Override
    public String toString() {
        return "VideoInfoBean{" +
                "id=" + id +
                ", type=" + type +
                ", id_value=" + id_value +
                ", alias='" + alias + '\'' +
                ", avatar='" + avatar + '\'' +
                ", video_url='" + video_url + '\'' +
                ", subtitle_file_url='" + subtitle_file_url + '\'' +
                ", content='" + content + '\'' +
                ", pics='" + pics + '\'' +
                ", title='" + title + '\'' +
                ", tag_ids='" + tag_ids + '\'' +
                ", is_fee=" + is_fee +
                ", fee=" + fee +
                ", goodList='" + goodList + '\'' +
                ", play_cnt=" + play_cnt +
                ", collection_cnt=" + collection_cnt +
                ", forward_cnt=" + forward_cnt +
                ", liked_cnt=" + liked_cnt +
                ", create_time='" + create_time + '\'' +
                ", is_collection=" + is_collection +
                ", is_liked=" + is_liked +
                ", commentCnt=" + commentCnt +
                ", cover_img='" + cover_img + '\'' +
                ", tagDataLists=" + tagDataLists +
                '}';
    }

    public String getCover_img() {
        return cover_img;
    }

    public void setCover_img(String cover_img) {
        this.cover_img = cover_img;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getVideo_url() {
        return video_url;
    }

    public void setVideo_url(String video_url) {
        this.video_url = video_url;
    }

    public String getSubtitle_file_url() {
        return subtitle_file_url;
    }

    public void setSubtitle_file_url(String subtitle_file_url) {
        this.subtitle_file_url = subtitle_file_url;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPics() {
        return pics;
    }

    public void setPics(String pics) {
        this.pics = pics;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTag_ids() {
        return tag_ids;
    }

    public void setTag_ids(String tag_ids) {
        this.tag_ids = tag_ids;
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

    public String getGoodList() {
        return goodList;
    }

    public void setGoodList(String goodList) {
        this.goodList = goodList;
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

    public int getForward_cnt() {
        return forward_cnt;
    }

    public void setForward_cnt(int forward_cnt) {
        this.forward_cnt = forward_cnt;
    }

    public int getLiked_cnt() {
        return liked_cnt;
    }

    public void setLiked_cnt(int liked_cnt) {
        this.liked_cnt = liked_cnt;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public int getIs_collection() {
        return is_collection;
    }

    public void setIs_collection(int is_collection) {
        this.is_collection = is_collection;
    }

    public int getIs_liked() {
        return is_liked;
    }

    public void setIs_liked(int is_liked) {
        this.is_liked = is_liked;
    }

    public int getCommentCnt() {
        return commentCnt;
    }

    public void setCommentCnt(int commentCnt) {
        this.commentCnt = commentCnt;
    }

    public List<VideoLableBean> getTagDataLists() {
        return tagDataLists;
    }

    public void setTagDataLists(List<VideoLableBean> tagDataLists) {
        this.tagDataLists = tagDataLists;
    }
}
