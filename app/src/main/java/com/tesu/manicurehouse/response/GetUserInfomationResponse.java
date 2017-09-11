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
public class GetUserInfomationResponse {
    /**
     * 服务器响应码
     */
    public String code;
    /**
     * 描述
     */
    public String resultText;

    /**
     * 会员id
     */
    public String user_id;
    /**
     * 头像url
     */
    public String avatar;
    /**
     * 城市
     */
    public String city;
    /**
     * 个性签名
     */
    public String signature;
    public String identity_type;//	身份 0：普通用户，1：美甲师，2：美甲店主
    public String user_name;//	用户名
    public String user_money;    //用户现有资金
    public int pay_points;    //消费积分
    public String rank_points;// 会员等级积分
    public String alias;    //昵称
    public String mobile_phone;//手机
}
