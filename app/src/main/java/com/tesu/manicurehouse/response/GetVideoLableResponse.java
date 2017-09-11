package com.tesu.manicurehouse.response;

import com.tesu.manicurehouse.bean.VideoLableBean;

import java.util.List;

/**
 * Created by Administrator on 2016/9/9 0009.
 */
public class GetVideoLableResponse {
    private int code;
    private String resultText;
    private List<VideoLableBean> dataList;

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

    public List<VideoLableBean> getDataList() {
        return dataList;
    }

    public void setDataList(List<VideoLableBean> dataList) {
        this.dataList = dataList;
    }

    @Override
    public String toString() {
        return "GetLableResponse{" +
                "code=" + code +
                ", resultText='" + resultText + '\'' +
                ", dataList=" + dataList +
                '}';
    }
}
