package com.tesu.manicurehouse.bean;

/**
 * Created by Administrator on 2016/9/10 0010.
 */
public class ShowPhotoContentBean {
    private String pic1;
    private String pic2;
    private String content;
    private int picCnt;

    @Override
    public String toString() {
        return "ShowPhotoContentBean{" +
                "pic1='" + pic1 + '\'' +
                ", pic2='" + pic2 + '\'' +
                ", content='" + content + '\'' +
                ", picCnt=" + picCnt +
                '}';
    }

    public String getPic1() {
        return pic1;
    }

    public void setPic1(String pic1) {
        this.pic1 = pic1;
    }

    public String getPic2() {
        return pic2;
    }

    public void setPic2(String pic2) {
        this.pic2 = pic2;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getPicCnt() {
        return picCnt;
    }

    public void setPicCnt(int picCnt) {
        this.picCnt = picCnt;
    }
}
