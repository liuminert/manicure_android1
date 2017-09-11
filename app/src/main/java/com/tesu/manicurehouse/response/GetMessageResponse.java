package com.tesu.manicurehouse.response;

import com.tesu.manicurehouse.bean.MessageBean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/10/13 0013.
 */
public class GetMessageResponse{
    private int code;
    private String resultText;
    private int unReadCnt;
    private ArrayList<MessageBean> dataList;

    @Override
    public String toString() {
        return "GetMessageResponse{" +
                "code=" + code +
                ", resultText='" + resultText + '\'' +
                ", unReadCnt=" + unReadCnt +
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

    public int getUnReadCnt() {
        return unReadCnt;
    }

    public void setUnReadCnt(int unReadCnt) {
        this.unReadCnt = unReadCnt;
    }

    public ArrayList<MessageBean> getDataList() {
        return dataList;
    }

    public void setDataList(ArrayList<MessageBean> dataList) {
        this.dataList = dataList;
    }
}
