package com.tesu.manicurehouse.response;

import com.tesu.manicurehouse.bean.VideoListBean;

import java.util.List;

/**
 * Created by Administrator on 2016/9/18 0018.
 */
public class VideoListResponse {
    private int code;
    private String resultText;
    private List<VideoListBean> dataList;

    @Override
    public String toString() {
        return "VideoListResponse{" +
                "code=" + code +
                ", resultText='" + resultText + '\'' +
                ", dataList=" + dataList +
                '}';
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getResultText() {
        return resultText;
    }

    public void setResultText(String resultText) {
        this.resultText = resultText;
    }

    public List<VideoListBean> getDataList() {
        return dataList;
    }

    public void setDataList(List<VideoListBean> dataList) {
        this.dataList = dataList;
    }
}
