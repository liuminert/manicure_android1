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
public class GetUserMsgResponse {
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
        public String msg_id;//	信息自增id
        public long msg_time;//	时间
        public String msg_title;//	标题
        public String msg_content;//	内容
        public String reply_content	;//官方回复内容
    }
}
