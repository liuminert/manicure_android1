package com.tesu.manicurehouse.bean;

/**
 * Created by Administrator on 2016/9/14 0014.
 */
public class UploadVideoMessageBean {
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
    public String user_id;
    public int id;
    public String converImageUrl;

    @Override
    public String toString() {
        return "UploadVideoMessageBean{" +
                "video_id=" + video_id +
                ", id_value=" + id_value +
                ", alias='" + alias + '\'' +
                ", avatar='" + avatar + '\'' +
                ", type=" + type +
                ", video_url='" + video_url + '\'' +
                ", subtitle_file_url='" + subtitle_file_url + '\'' +
                ", pics='" + pics + '\'' +
                ", title='" + title + '\'' +
                ", is_fee=" + is_fee +
                ", fee='" + fee + '\'' +
                ", play_cnt=" + play_cnt +
                ", collection_cnt=" + collection_cnt +
                ", forward_cnt=" + forward_cnt +
                ", liked_cnt=" + liked_cnt +
                ", is_collection=" + is_collection +
                ", is_liked=" + is_liked +
                ", create_time=" + create_time +
                ", user_id='" + user_id + '\'' +
                ", id=" + id +
                ", converImageUrl='" + converImageUrl + '\'' +
                '}';
    }
}
