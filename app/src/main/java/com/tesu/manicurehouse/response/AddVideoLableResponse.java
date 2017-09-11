package com.tesu.manicurehouse.response;

/**
 * Created by Administrator on 2016/9/9 0009.
 */
public class AddVideoLableResponse {
    private int code;
    private String resultText;
    private int tag_id;

    @Override
    public String toString() {
        return "AddVideoLableResponse{" +
                "code=" + code +
                ", resultText='" + resultText + '\'' +
                ", tag_id=" + tag_id +
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

    public int getTag_id() {
        return tag_id;
    }

    public void setTag_id(int tag_id) {
        this.tag_id = tag_id;
    }
}
