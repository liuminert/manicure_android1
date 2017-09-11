package com.tesu.manicurehouse.base;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.tesu.manicurehouse.R;
import com.tesu.manicurehouse.activity.MainActivity;
import com.tesu.manicurehouse.util.LogUtils;
import com.tesu.manicurehouse.util.LoginUtils;
import com.tesu.manicurehouse.util.SharedPrefrenceUtils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @作者: 许明达
 * @创建时间: 2015-4-3下午8:39:58
 * @版权: 特俗版权所有
 * @描述: Activity的基类, 处理activity的生命周期
 * @更新人:
 * @更新时间:
 * @更新内容: TODO
 */
public abstract class BaseActivity extends FragmentActivity {
    /**
     * 记录处于前台的Activity
     */
    private static BaseActivity mForegroundActivity = null;
    /**
     * 记录所有活动的Activity
     */
    private static final List<BaseActivity> mActivities = new LinkedList<BaseActivity>();

    private long exitTime;
    protected boolean isActivityRun = true;


    // 网络未连接
    protected static final int ERROR = 0;
    // 数据为空
    protected static final int EMPTY = 1;
    protected static final int NETWORK_NOT_OPEN = 2;
    protected static final int TIME_OUT = 3;
    // 成功
    protected static final int SUCCESS = 4;
    protected static final int CLIENTCONFIGSUCCESS = 5;
    protected static final int CLIENTCONFIGERROR = 6;

    protected MyHandler handler = new MyHandler();
    /**
     * 记录我要删除的activity
     */
    private static final List<BaseActivity> delActivities = new LinkedList<BaseActivity>();
    static class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case ERROR:

                    break;
                case EMPTY:

                    break;
                case NETWORK_NOT_OPEN:

                    break;
                case TIME_OUT:

                    break;
                case SUCCESS:

                    break;
            }
        }
    }
    //权限状态
    private int mPermissionState=101;

    public void getCheckPermission(){
        //如果版本是6.0
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            //获取权限
            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED||ActivityCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
                //申请多个（或者多个）权限，并提供用于回调返回的获取码
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE},mPermissionState);
            }
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        synchronized (mActivities) {
            mActivities.add(this);
        }
        init();
        initView();
        getCheckPermission();
    }

    @Override
    protected void onResume() {
        mForegroundActivity = this;
        super.onResume();
        // 友盟统计启动退出
//        MobclickAgent.onPageStart("SplashScreen");
//        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        mForegroundActivity = null;
        super.onPause();
        // 友盟统计启动退出
//        MobclickAgent.onPageEnd("SplashScreen");
//        MobclickAgent.onPause(this);
    }

    protected void init() {
    }

    protected abstract View initView();

    /**
     * 关闭所有Activity
     */
    public static void finishAll() {
        List<BaseActivity> copy;
        synchronized (mActivities) {
            copy = new ArrayList<BaseActivity>(mActivities);
        }
        for (BaseActivity activity : copy) {
            activity.finish();
        }
    }

    /**
     * 关闭所有Activity，除了参数传递的Activity
     */
    public static void finishAll(BaseActivity except) {
        List<BaseActivity> copy;
        synchronized (mActivities) {
            copy = new ArrayList<BaseActivity>(mActivities);
        }
        for (BaseActivity activity : copy) {
            if (activity != except)
                activity.finish();
        }
    }

    /**
     * 是否有启动的Activity
     */
    public static boolean hasActivity() {
        return mActivities.size() > 0;
    }

    /**
     * 获取当前处于前台的activity
     */
    public static BaseActivity getForegroundActivity() {
        return mForegroundActivity;
    }

    /**
     * 获取当前处于栈顶的activity，无论其是否处于前台
     */
    public static BaseActivity getCurrentActivity() {
        List<BaseActivity> copy;
        synchronized (mActivities) {
            copy = new ArrayList<BaseActivity>(mActivities);
        }
        if (copy.size() > 0) {
            return copy.get(copy.size() - 1);
        }
        return null;
    }

    /**
     * 推出应用
     */
    public void exitApp() {
        if (System.currentTimeMillis() - exitTime > 2000) {
            // 点击间隔大于两秒，做出提示
//            Toast.makeText(BaseActivity.this, "再按一次返回键退出程序！", Toast.LENGTH_SHORT).show();
//            showTextToast("再按一次返回键退出程序！");
            exitTime = System.currentTimeMillis();
        } else {
            SharedPrefrenceUtils.setString(this, "currentIndex", "0");
            // 连续点击量两次，进行应用退出的处理
            finishAll();
            // 友盟保存统计数据
//            MobclickAgent.onKillProcess(this);
//            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(0);

        }

    }

    /**
     * 强制退出
     */
    public void exit() {
        finishAll();
        // 友盟保存统计数据
//        MobclickAgent.onKillProcess(this);
        System.exit(0);
//        android.os.Process.killProcess(android.os.Process.myPid());
    }

    /**
     * 手机按键点击返回键，退出App
     */
    protected void onKeyDownBack() {
        exitApp();
    }

    /**
     * 此方法表示手机按键点击方法
     * <p/>
     * <b>[不用再重写官方的onKeyDown方法]</b>
     * <p/>
     * <b>[onKeyDownBack监听返回键方法]</b>
     *
     * @param keyCode 官方的onKeyDown的keyCode
     * @param event   官方的onKeyDown的event
     */
    protected void onKeyDownMethod(int keyCode, KeyEvent event) {
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == 4) {
            onKeyDownMethod(keyCode, event);
//			if(!TextUtils.isEmpty(LoginUtils.getOthersId())){
//				LoginUtils.othersId = "";
//				OthersActivity.page = 6;
//			}
//			else if(!TextUtils.isEmpty(LoginUtils.getFansId())){
//				LoginUtils.fansId="";
//				MainActivity.getCtf().switchCurrentPage();
//			}
            finish();
            overridePendingTransition(R.anim.animprv_in, R.anim.animprv_out);
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 返回按钮点击事件
     */
    protected OnClickListener onBackClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();
//            overridePendingTransition(R.anim.animprv_in, R.anim.animprv_out);
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isActivityRun = false;

    }
    protected void setFinish() {
        synchronized (delActivities) {
            delActivities.add(this);
        }
    }

    protected void finishActivity() {
        List<BaseActivity> copy;
        synchronized (delActivities) {
            copy = new ArrayList<BaseActivity>(delActivities);
        }
        for (BaseActivity activity : copy) {
            activity.finish();
        }
    }
}
