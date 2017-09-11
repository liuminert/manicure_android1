package com.tesu.manicurehouse.response;

import com.tesu.manicurehouse.bean.AudioBean;

import java.util.List;

/**
 * Created by Administrator on 2016/9/8 0008.
 */
public class GetAudioListResponse {
    private int code;
    private String resultText;
    private List<AudioBean> dataList;

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

    public List<AudioBean> getDataList() {
        return dataList;
    }

    public void setDataList(List<AudioBean> dataList) {
        this.dataList = dataList;
    }

    @Override
    public String toString() {
        return "getAudioListResponse{" +
                "code=" + code +
                ", resultText='" + resultText + '\'' +
                ", dataList=" + dataList +
                '}';
    }
}
