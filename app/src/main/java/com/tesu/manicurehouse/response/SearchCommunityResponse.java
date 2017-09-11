package com.tesu.manicurehouse.response;

import com.tesu.manicurehouse.bean.GoodBean;

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
public class SearchCommunityResponse {
    /**
     * 服务器响应码
     */
    public int code;
    /**描述*/
    public String resultText;
    public List<CommunityBean> dataList;
    public class CommunityBean{
        public int type;	//1帖子，2资讯
        public int id;//	type=1帖子id，type=2资讯id，
        public String title	;//标题
        public String pic	;//图片url(PS:如果是type=1,可能有多张)
        public int forward_cnt;//	转发次数
        public int liked_cnt;//	点赞次数
        public int  comment_cnt;//	评论次数
        public String add_time	;//时间(秒)
        public int is_liked;//	是否已经点赞0否，1是
        public String alias;
        public String avatar;
    }
}
