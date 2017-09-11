package com.tesu.manicurehouse.response;

import java.util.List;

/**
 * Created by Administrator on 2016/9/8 0008.
 */
public class GetInformationListResponse {
    public int code;
    public String resultText;
    public List<InformationBean> dataList;

    public class InformationBean {
        public String information_id;//	资讯id
        public String title;//标题
        public String content;//	详情内容
        public int forward_cnt;//转发次数
        public int comment_cnt;//评论次数
        public int liked_cnt;//	点赞次数
        public String pic;//图片url
        public int is_liked;//	是否已经点赞0否，1是
        public String add_time;//发帖时间(秒)
    }

}
