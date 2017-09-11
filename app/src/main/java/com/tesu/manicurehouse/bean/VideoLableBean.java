package com.tesu.manicurehouse.bean;

/**
 * Created by Administrator on 2016/9/9 0009.
 */
public class VideoLableBean {
    private int tag_id;
    private String tag_name;
    private boolean ischecked;

    @Override
    public String toString() {
        return "VideoLableBean{" +
                "tag_id=" + tag_id +
                ", tag_name='" + tag_name + '\'' +
                ", ischecked=" + ischecked +
                '}';
    }

    public int getTag_id() {
        return tag_id;
    }

    public void setTag_id(int tag_id) {
        this.tag_id = tag_id;
    }

    public String getTag_name() {
        return tag_name;
    }

    public void setTag_name(String tag_name) {
        this.tag_name = tag_name;
    }

    public boolean ischecked() {
        return ischecked;
    }

    public void setIschecked(boolean ischecked) {
        this.ischecked = ischecked;
    }
}
