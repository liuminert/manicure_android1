package com.tesu.manicurehouse.response;

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
public class LoginResponse {
    /**
     * 服务器响应码
     */
    public String code;
    /**描述*/
    public String resultText;
    /**
     * 是否分销，0否，1是
     */
    public int is_distribution;
    /**合成视频接口url(配置文件获取)*/
    public String synthesis_url;
    /**上传文件接口url(配置文件获取)*/
    public String uploadfile_url;
    /**会员id*/
    public int user_id;
    /**用户名*/
    public String user_name;
}
