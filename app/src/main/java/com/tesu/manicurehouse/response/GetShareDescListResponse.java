package com.tesu.manicurehouse.response;

import com.tesu.manicurehouse.bean.RecommendDescBean;
import com.tesu.manicurehouse.bean.ShareDescBean;

import java.util.List;

/**
 * @作者: 许明达
 * @创建时间: 2016-3-23下午15:43:20
 * @版权: 特速版权所有
 * @描述: 封装服务器返回列表的参数
 * @更新人:
 * @更新时间:
 * @更新内容: TODO
 */
public class GetShareDescListResponse {
    /**
     * 服务器响应码
     */
    public int code;
    /**描述*/
    public String resultText;
    //人数
    public int recommendCnt;
    //积分
    public int recommendScore;
    public List<ShareDescBean> dataList;
}
