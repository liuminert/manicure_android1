package com.tesu.manicurehouse.response;

import com.tesu.manicurehouse.bean.VideoInfoBean;
import com.tesu.manicurehouse.bean.VideoLableBean;

import java.util.List;

/**
 * Created by Administrator on 2016/9/20 0020.
 */
public class VideoInfoResponse {
    private int code;
    private String resultText;
    private VideoInfoBean data;

    @Override
    public String toString() {
        return "VideoInfoResponse{" +
                "code=" + code +
                ", resultText='" + resultText + '\'' +
                ", data=" + data +
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

    public VideoInfoBean getData() {
        return data;
    }

    public void setData(VideoInfoBean data) {
        this.data = data;
    }
}
