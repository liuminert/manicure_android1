package com.tesu.manicurehouse.response;

import com.tesu.manicurehouse.bean.AudioBean;
import com.tesu.manicurehouse.bean.GoodsAttrBean;

import java.util.List;

/**
 * Created by Administrator on 2016/9/8 0008.
 */
public class GetGoodAttrListResponse {
    public int code;
    public String resultText;
    public List<GoodsAttrBean> dataList;
}
