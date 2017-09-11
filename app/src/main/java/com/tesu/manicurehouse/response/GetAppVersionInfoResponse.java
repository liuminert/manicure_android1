package com.tesu.manicurehouse.response;

/**
 * @作者: 许明达
 * @创建时间: 2016-3-23下午15:43:20
 * @版权: 特速版权所有
 * @描述: 封装服务器返回列表的参数
 * @更新人:
 * @更新时间:
 * @更新内容: TODO
 */
public class GetAppVersionInfoResponse {

    /**
     * 服务器响应码
     */
    public int code;
    public String msg;
    /**描述*/
    public AppVersionInfo data;
    public class AppVersionInfo{
        //最新版本号1.10
        public String newest_version;
        //更新内容
        public  String update_content;
        //下载链接地址
        public  String download_url;
        // 是否强制更新0否1是
        public int force_update;
    }

}
