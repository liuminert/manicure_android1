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
public class GetInformationDescResponse {
    /**
     * 服务器响应码
     */
    public String code;
    /**
     * 描述
     */
    public String resultText;

    public InformationDescBean data;

    public class InformationDescBean {
        public String information_id;//	资讯id
        public String title;//	标题
        public String content;//	详情内容
        public String pic;//	图片url
        public int forward_cnt;//转发次数
        public int liked_cnt;//	点赞次数
        public int comment_cnt;//	评论次数
        public String add_time;//	发帖时间(秒)
        public int is_liked;//	是否已经点赞0否，1是
    }

}
