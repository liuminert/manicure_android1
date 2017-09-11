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
public class GetPostCommentListResponse {
    /**
     * 服务器响应码
     */
    public String code;
    /**
     * 描述
     */
    public String resultText;

    public List<InformationCommentBean> dataList;
    public int commentCnt;

    public class InformationCommentBean {
        public String comment_id;//	主贴id
        public String post_id;//	帖子id
        public String user_id;//	评论人的会员id
        public String alias	;//昵称
        public String avatar;//	头像
        public int liked_cnt;//	点赞次数
        public String content;//	评论内容
        public String pics	;//图片（PS：可能有多个，暂定为json数组，格式:[”图片url”,’图片url”,”图片url”]）
        public String comment_time;//	评论时间(秒)
        public List<ReplyData> replyDataList;//	回复评论列表，默认查询第一页的回复数据，见下面详情
        public int is_liked;//	会员是否已点赞0否，1是
    }

    public class ReplyData{
        public String reply_id;//	跟帖id
        public String comment_id;//	主贴id
        public String parent_id	;//父id
        public String user_id	;//回复人的会员id
        public String alias;//	昵称
        public String avatar;//	头像
        public String content	;//回复内容
        public String reply_time	;//回复时间(秒)
    }
}
