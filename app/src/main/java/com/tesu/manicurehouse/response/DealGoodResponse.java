package com.tesu.manicurehouse.response;

/**
 * @作者: 许明达
 * @创建时间: 2016-3-23下午15:43:20
 * @版权: 特速版权所有
 * @描述: 封装服务器返回列表的参数
 * @更新人:
 * @更新时间:
 * @更新内容: TODO
 */
public class DealGoodResponse {
    /**
     * 服务器响应码
     */
    public String code;
    /**
     * 描述
     */
    public String resultText;
    //   商家订单号
    public String out_trade_no;

    // 商品描述

    public String body;


    //  商品详情
    public String detail;


    //   实际支付金额
    public String total_fee;

    //订单id
    public String order_id;


}
