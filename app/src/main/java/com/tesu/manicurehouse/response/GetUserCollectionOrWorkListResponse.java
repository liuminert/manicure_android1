package com.tesu.manicurehouse.response;

import com.tesu.manicurehouse.bean.CollectionItem;

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
public class GetUserCollectionOrWorkListResponse {
    /**
     * 服务器响应码
     */
    public String code;
    /**
     * 描述
     */
    public String resultText;

    public List<CollectionData> dataList;

    public class CollectionData{
        public int video_id	;//视频id，自增长
        public int id_value;	//type=0，填后台登陆管理人的登陆id；type=1，填app会员userid
        public String alias	;//昵称
        public String avatar;	//头像
        public int type;//	1表示晒视频，2表示晒照片
        public String video_url;//	varchar
        public String subtitle_file_url;//	字幕文件url，可以通过4.1.1上传文件接口获取文件路径
        public String pics	;//当type=2时有值，图片（PS：可能有多个，暂定为json数组，格式:[”图片url”,’图片url”,”图片url”]）
        public String title;	//标题
        public int is_fee;//	是否收费，0否，1是
        public String fee;//	当is_fee=1时有值
        public int play_cnt;//	播放次数
        public int collection_cnt;//	收藏次数
        public int forward_cnt	;//转发次数
        public int liked_cnt;//	点赞次数
        public int is_collection;//	会员是否已收藏0否，1是
        public int is_liked;//	会员是否已点赞0否，1是
        public long create_time;
        public String cover_img;  //视频封面url
        public int index_;  //官方视频头像下标
    }
}
