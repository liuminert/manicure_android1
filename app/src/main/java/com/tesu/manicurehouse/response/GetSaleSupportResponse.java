package com.tesu.manicurehouse.response;

/**
 * Created by Administrator on 2016/10/14 0014.
 */
public class GetSaleSupportResponse {
    private int code;
    private String resultText;
    private String content;

    @Override
    public String toString() {
        return "GetSaleSupportResponse{" +
                "code=" + code +
                ", resultText='" + resultText + '\'' +
                ", conetent='" + content + '\'' +
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
