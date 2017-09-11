package com.tesu.manicurehouse.huanxin;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.view.View;

import com.easemob.easeui.ui.EaseBaseActivity;

/**
 * Created by lzan13 on 2015/11/6 20:38.
 * 自定义基类，所有Activity都继承自此类，可以定义一些公用方法和变量
 */
public class HuanXinBaseActivity extends EaseBaseActivity {

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //umeng
//        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //umeng
//        MobclickAgent.onPause(this);
    }

    /**
     * 通过xml查找相应的ID，通用方法
     * @param id
     * @param <T>
     * @return
     */
    protected <T extends View> T $(@IdRes int id) {
        return (T) findViewById(id);
    }
}
