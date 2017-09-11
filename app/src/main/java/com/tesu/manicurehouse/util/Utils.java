package com.tesu.manicurehouse.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

/**
 * Created by Administrator on 2016/10/12 0012.
 */
public class Utils {
    /**
     * 获取应用版本号versionName
     *
     * @param context
     * @return
     */
    public static String getVersionName(Context context) {
        String versionName = "1.0";
        try {
            PackageInfo mPackageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            versionName = mPackageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }

    /**
     * 获取应用版本号versionCode
     *
     * @param context
     * @return
     */
    public static int getVersionCode(Context context) {
        int versionCode = 0;
        try {
            PackageInfo mPackageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            versionCode = mPackageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }
}
