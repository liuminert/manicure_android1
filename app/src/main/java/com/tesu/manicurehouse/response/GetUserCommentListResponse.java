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
public class GetUserCommentListResponse {
    /**
     * 服务器响应码
     */
    public String code;
    /**
     * 描述
     */
    public String resultText;

    public List<DataList> dataList;

    public class DataList {
        public int value_id;    //	Type=0帖子id，type=1视频id，type=2资讯id
        public int type;//0帖子，1视频，2资讯
        public String title;//	标题
        public String value_url;//	图片(多张)或者视频url
        public String comment_time;
        public String reply_content;
        public String alias;//	评论人的昵称
        public String pic_url;
    }
}
