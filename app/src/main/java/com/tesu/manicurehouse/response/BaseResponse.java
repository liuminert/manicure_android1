package com.tesu.manicurehouse.response;

/**
 * Created by Administrator on 2016/9/13 0013.
 */
public class BaseResponse {
    private int code;
    private String resultText;

    @Override
    public String toString() {
        return "BaseResponse{" +
                "code=" + code +
                ", resultText='" + resultText + '\'' +
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
}
