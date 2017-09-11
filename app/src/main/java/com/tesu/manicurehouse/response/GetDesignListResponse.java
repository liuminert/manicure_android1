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
public class GetDesignListResponse {
    /**
     * 服务器响应码
     */
    public int code;
    /**
     * 描述
     */
    public String resultText;

    public List<DesignBean> dataList;
    //设计分成
    public String designFee;
    //  申请提现 ，待打款金额
    public String unPayMoney;
    //   申请提现，已打款金额
    public String payMoney;

    public class DesignBean {
        public int design_id;//	设计分成id
        public int video_id;//视频id
        public int type;//1表示晒视频，2表示晒照片
        public String video_url;//	视频完整的url
        public String pics;//当type=2时有值，图片（PS：可能有多个，暂定为json数组，格式:[”图片url”,’图片url”,”图片url”]）
        public String title;//标题
        public long create_time;//创建时间（秒）
        public int designCnt;//	被采纳次数
        public String sumFee;//合计
        public String cover_img; //视频封面url
    }
}
