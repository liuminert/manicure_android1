package com.tesu.manicurehouse.bean;

/**
 * Created by Administrator on 2016/9/9 0009.
 */
public class VideoClassBean {
    private int video_category_id;
    private String category_name;
    private boolean isChecked;

    public int getVideo_category_id() {
        return video_category_id;
    }

    public void setVideo_category_id(int video_category_id) {
        this.video_category_id = video_category_id;
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setIsChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }

    @Override
    public String toString() {
        return "VideoClassBean{" +
                "video_category_id=" + video_category_id +
                ", category_name='" + category_name + '\'' +
                ", isChecked=" + isChecked +
                '}';
    }
}
