package com.tesu.manicurehouse.response;

import com.tesu.manicurehouse.bean.GoodsAttrBean;

import java.util.List;

/**
 * Created by Administrator on 2016/9/12 0012.
 */
public class GetActDescByIdResponse {
    private int code;
    private String resultText;
    private GetActListResponse.ActBean data;

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

    public GetActListResponse.ActBean getData() {
        return data;
    }

    public void setData(GetActListResponse.ActBean data) {
        this.data = data;
    }
}
