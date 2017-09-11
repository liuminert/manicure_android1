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
public class GetCommonProblemResponse {
    /**
     * 服务器响应码
     */
    public String code;
    /**描述*/
    public String resultText;

    public List<CommonProblemBean> dataList;
    public class CommonProblemBean{
        public int help_id;//	 问题id
        public  String help_que;//	 问题
        public  String help_ans;// 答案
        public  String add_time;// 增加记录的时间
    }
}
