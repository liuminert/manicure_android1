package com.tesu.manicurehouse.util;

import com.google.gson.Gson;
import com.upyun.library.common.Params;
import com.upyun.library.common.UploadManager;
import com.upyun.library.listener.SignatureListener;
import com.upyun.library.listener.UpCompleteListener;
import com.upyun.library.listener.UpProgressListener;
import com.upyun.library.utils.UpYunUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/9/12 0012.
 */
public class MyUpyunUtils {
    private static final String TAG = "MyUpyunUtils";
    public static String KEY = "JE9mfhBCWXk5oqJ66GlH3cH6ZH0=";
    public static String SPACE = "meijiawu";
    public static String savePath = "/uploads/{year}{mon}{day}/{random32}{.suffix}";
    public static String UpyunBaseUrl = "http://meijiawu.b0.upaiyun.com";

    public static void upload(String path,UpCompleteListener completeListener){

        File temp = null;
        try {
            temp = new File(path);
        } catch (Exception e) {
            e.printStackTrace();
        }

        final Map<String, Object> paramsMap = new HashMap<>();
        //上传空间
        paramsMap.put(Params.BUCKET, SPACE);
        //保存路径，任选其中一个
        paramsMap.put(Params.SAVE_KEY, savePath);
//        paramsMap.put(Params.PATH, savePath);
        //可选参数（详情见api文档介绍）
        paramsMap.put(Params.RETURN_URL, "httpbin.org/post");
        //进度回调，可为空
        UpProgressListener progressListener = new UpProgressListener() {
            @Override
            public void onRequestProgress(final long bytesWrite, final long contentLength) {
//                uploadProgress.setProgress((int) ((100 * bytesWrite) / contentLength));
//                textView.setText((100 * bytesWrite) / contentLength + "%");
//                Log.e(TAG, (100 * bytesWrite) / contentLength + "%");
            }
        };

        SignatureListener signatureListener = new SignatureListener() {
            @Override
            public String getSignature(String raw) {
                return UpYunUtils.md5(raw + KEY);
            }
        };

        UploadManager.getInstance().formUpload(temp, paramsMap, signatureListener, completeListener, progressListener);

    }
}
