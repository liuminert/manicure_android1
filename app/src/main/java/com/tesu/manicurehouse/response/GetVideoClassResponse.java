package com.tesu.manicurehouse.response;

import com.tesu.manicurehouse.bean.VideoClassBean;

import java.util.List;

/**
 * Created by Administrator on 2016/9/9 0009.
 */
public class GetVideoClassResponse {
    private int code;
    private String resultText;
    private List<VideoClassBean> dataList;

    @Override
    public String toString() {
        return "GetVideoClassResponse{" +
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

    public List<VideoClassBean> getDataList() {
        return dataList;
    }

    public void setDataList(List<VideoClassBean> dataList) {
        this.dataList = dataList;
    }
}
