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
public class GetAdsResponse {
    /**
     * 服务器响应码
     */
    public String code;
    /**
     * 描述
     */
    public String resultText;

    public List<AdsBean> dataList;

    public class AdsBean {
        public int ad_id;//	广告ID
        public String ad_name;//	该条广告记录的广告名称
        public String ad_link;//	广告链接地址
        public String ad_code;//	广告链接的表现，文字广告就是文字或图片和flash就是它们的地址，代码广告就是代码内容
        public int type_;// 跳转类型0:h5，1:视频，2:商品
    }

}
