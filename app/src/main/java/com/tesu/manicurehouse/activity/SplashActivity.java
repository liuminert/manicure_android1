package com.tesu.manicurehouse.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.tesu.manicurehouse.R;
import com.tesu.manicurehouse.base.BaseActivity;
import com.tesu.manicurehouse.util.DialogUtils;
import com.tesu.manicurehouse.util.SharedPrefrenceUtils;
import com.tesu.manicurehouse.util.UIUtils;

/**
 * @作者: 周岐同
 * @创建时间: 2015-4-3下午8:38:49
 * @版权: 微位科技版权所有
 * @描述: LOGO页面 处理 进入引导页面还是主界面
 * @更新人:
 * @更新时间:
 * @更新内容: TODO
 */
public class SplashActivity extends BaseActivity {
    private boolean isStartNext;
    private long startTime;
    private long endTime;
    private long dTime;
    // 网络未连接
    protected static final int ERROR = 0;
    protected static final int EMPTY = 1;
    protected static final int NETWORK_NOT_OPEN = 4;
    // 请求超时
    protected static final int TIME_OUT = 6;
    private Dialog showAlterDialog;


    @Override
    protected View initView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        View rootView = View.inflate(this, R.layout.activity_splash, null);
        setContentView(rootView);
        startTime = SystemClock.currentThreadTimeMillis();
        initData();
        return rootView;
    }
    public void initData() {
        // 只调用一次
        if (!isStartNext) {
            isStartNext = true;
            endTime = SystemClock.currentThreadTimeMillis();
            dTime = endTime - startTime;
            // LogUtils.e("dTime " + dTime);
            new Thread() {
                public void run() {
                    if (dTime < 1500) {
                        SystemClock.sleep(1500 - dTime);
                    }
                    startNext();
                }

                ;
            }.start();
        }
    }

    private void startNext() {
        if (SharedPrefrenceUtils.getBoolean(SplashActivity.this, "isGuided")) {
            Intent intent = new Intent(UIUtils.getContext(), MainActivity.class);
            UIUtils.startActivity(intent);
            finish();
        } else {
            Intent intent = new Intent(UIUtils.getContext(),
                    GuideActivity.class);
            UIUtils.startActivity(intent);
            finish();
        }
    }



}
