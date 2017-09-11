package com.tesu.manicurehouse.response;

/**
 * Created by Administrator on 2016/9/8 0008.
 */
public class UploadFileResponse {
    private int code;
    private String resultText;
    private String fileName;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getResultText() {
        return resultText;
    }

    public void setResultText(String resultText) {
        this.resultText = resultText;
    }

    @Override
    public String toString() {
        return "UploadFileResponse{" +
                "code=" + code +
                ", resultText='" + resultText + '\'' +
                ", fileName='" + fileName + '\'' +
                '}';
    }
}
