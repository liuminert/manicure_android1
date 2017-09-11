package com.tesu.manicurehouse.response;

import com.tesu.manicurehouse.bean.GoodsAttrBean;

import java.util.List;

/**
 * Created by Administrator on 2016/9/12 0012.
 */
public class GetGoodsAttrResponse {
    private int code;
    private String resultText;
    private List<GoodsAttrBean> dataList;

    @Override
    public String toString() {
        return "GetGoodsAttrResponse{" +
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

    public List<GoodsAttrBean> getDataList() {
        return dataList;
    }

    public void setDataList(List<GoodsAttrBean> dataList) {
        this.dataList = dataList;
    }
}
