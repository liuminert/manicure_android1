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
public class GetPostListResponse {
    /**
     * 服务器响应码
     */
    public String code;
    /**
     * 描述
     */
    public String resultText;

    public List<PostBean> dataList;

    public List<PostBean> getDataList() {
        return dataList;
    }

    public void setDataList(List<PostBean> dataList) {
        this.dataList = dataList;
    }

    public class PostBean {
        public int post_id;//	帖子id
        public int user_id	;//会员id
        public String avatar;//	头像
        public String alias;//	昵称
        public int is_liked;//	是否已经点赞0否，1是
        public String title;//	标题
        public String content;//	内容
        public String pics	;//可能有多个，暂定为json数组，格式:[”图片url”,’图片url”,”图片url”]
        public int forward_cnt;//	转发次数
        public int liked_cnt;//	点赞次数
        public int comment_cnt;//	评论次数
        public String add_time;//	发帖时间(秒)
        public int set_top;//	是否置顶0否，1是
        public int set_essence	;//是否加精0否，1是
    }

}
