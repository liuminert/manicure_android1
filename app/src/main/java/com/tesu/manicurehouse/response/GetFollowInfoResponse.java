package com.tesu.manicurehouse.response;

import com.tesu.manicurehouse.bean.AudioBean;

import java.util.List;

/**
 * Created by Administrator on 2016/9/8 0008.
 */
public class GetFollowInfoResponse {
    public int code;
    public String resultText;
    public Data data;

    public class Data {
        public String city;
        public int is_follow;//是否已经关注，0否，1是
        public String user_id;//	会员id
        public String alias;//昵称
        public String avatar;//	头像
        public int followedCnt;//	粉丝数
        public int followCnt;//关注数
        public int likedCnt;//	被赞数
        public String signature;//个性签名
        public int identity_type;//	0：普通用户，1：美甲师，2：美甲店主
    }

}
