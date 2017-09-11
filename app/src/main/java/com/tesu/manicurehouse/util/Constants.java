package com.tesu.manicurehouse.util;

import android.util.FloatMath;

/**
 * @作者: 许明达
 * @创建时间: 2015-3-22下午14:03:31
 * @版权: 特速版权所有
 * @描述: 全局常量
 */
public interface Constants {
    /**
     * 测试连接超时
     */
//	 String SERVER_URL = "http://192.168.1.200:8020/soda/api/";
    /**
     * 测试地址
     *
     */

//	String SERVER_URL = "http://192.168.0.85:8080/manicure_background/";
    /**
     * 生产环境 正确地址
     */
//	 String SERVER_URL = "http://121.201.66.7:8080/manicure_background/";
//	 String SERVER_URL = "http://192.168.0.85:8080/manicure_background/";
    String SERVER_URL = "http://123.207.242.44:8080/manicure_background/";
    String SHARE_URL = "http://adminnew.ransheng.net:8080/manicure_background/";

    /**
     * 上传文件到服务器url
     */
//    String UPLOADFILE_URL = "http://121.201.66.7:8080/manicure_videocomp/video/uploadFile.do";
    String UPLOADFILE_URL = "http://123.207.242.44:8080/manicure_videocomp/video/uploadFile.do";

    /**
     * 合成音视频url
     */
//    String COMPOSEVEDIO_URL = "http://121.201.66.7:8080/manicure_videocomp/video/synthesis.do";
    String COMPOSEVEDIO_URL = "http://123.207.242.44:8080/manicure_videocomp/video/synthesis.do";
    /**
     * 图片基本访问地址
     */
    // String BASE_IMAGE_URL = SERVER_URL + "image/";
    int PAGE_SIZE = 20;
    // String SERVERIP= "119.147.54.150";
    // String SERVERIP = "app.sodapp.com";

    // 微信APP_ID 替换为你的应用从官方网站申请到的合法appId
    public static final String APP_ID = "wxf66fff0d0fbdd642";
}
