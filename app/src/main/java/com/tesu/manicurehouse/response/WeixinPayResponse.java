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
public class WeixinPayResponse {
    /**
     * 服务器响应码
     */
    public int code;
    /**描述*/
    public String resultText;
    public String appid	;//应用ID（String(32)微信开放平台审核通过的应用APPID）
    public String partnerid	;//商户号（微信支付分配的商户号）
    public String prepayid	;//预支付交易会话ID（微信返回的支付交易会话ID）
    public String packageValue;//	扩展字段（暂填写固定值Sign=WXPay）
    public String noncestr;//	随机字符串
    public String timestamp;//	时间戳
    public String sign	;//签名
}
