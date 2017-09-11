package com.tesu.manicurehouse.response;

import com.tesu.manicurehouse.bean.GoodCommentBean;
import com.tesu.manicurehouse.bean.GoodsAttrBean;

import java.util.List;

/**
 * Created by Administrator on 2016/9/8 0008.
 */
public class GetGoodCommentListResponse {
    public int code;
    public String resultText;
    public List<GoodCommentBean> dataList;
}
