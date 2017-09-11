package com.tesu.manicurehouse.response;

/**
 * Created by Administrator on 2016/9/8 0008.
 */
public class ComposeVideoResponse {
    private int code;
    private String resultText;
    private String file_url;

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

    public String getFile_url() {
        return file_url;
    }

    public void setFile_url(String file_url) {
        this.file_url = file_url;
    }

    @Override
    public String toString() {
        return "ComposeVideoResponse{" +
                "code=" + code +
                ", resultText='" + resultText + '\'' +
                ", file_url='" + file_url + '\'' +
                '}';
    }
}
