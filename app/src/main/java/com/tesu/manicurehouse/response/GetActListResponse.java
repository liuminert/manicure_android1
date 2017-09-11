package com.tesu.manicurehouse.response;

import java.io.Serializable;
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
public class GetActListResponse {
    /**
     * 服务器响应码
     */
    public String code;
    /**描述*/
    public String resultText;

    public List<ActBean> dataList;
    public class ActBean implements Serializable{
        public int act_id;//	活动id，自增长
        public  String act_title;//	活动主题
        public  String act_content	;//活动文字内容(图文)
        public  String act_pic	;//活动缩略图
        public  String create_time	;//创建时间（秒）
    }
}
