package com.tesu.manicurehouse.response;

import com.tesu.manicurehouse.bean.AudioBean;
import com.tesu.manicurehouse.bean.VideoListBean;

import java.util.List;

/**
 * Created by Administrator on 2016/9/8 0008.
 */
public class GetVideoListResponse {
    public int code;
    public String resultText;
    public List<VideoListBean> dataList;
}
