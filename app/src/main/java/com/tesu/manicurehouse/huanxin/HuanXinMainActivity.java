package com.tesu.manicurehouse.huanxin;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.easemob.EMEventListener;
import com.easemob.EMNotifierEvent;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMMessage;
import com.tesu.manicurehouse.R;

/**
 * Created by lzan13 on 2015/11/6 21:10.
 * 程序主界面，主要模拟实现商品的列表展示，以及设置界面的跳转等功能
 * 实现消息的监听，在Toolbar上显示有未读消息
 */
public class HuanXinMainActivity extends HuanXinBaseActivity implements View.OnClickListener {

    private PopupListWindow mPopupListWindow;
    private TextView tvCustomer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 判断是否退出登陆，然后关闭Activity，并跳转到登陆界面
        setContentView(R.layout.activity_huanxin_main);

        findViewById(R.id.ib_shop_imageone).setOnClickListener(this);
        findViewById(R.id.ib_shop_imagetwo).setOnClickListener(this);
        findViewById(R.id.ib_shop_imagethree).setOnClickListener(this);
        findViewById(R.id.ib_shop_imagefour).setOnClickListener(this);
        tvCustomer = (TextView) findViewById(R.id.textview_customer);
        tvCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPopupListWindow == null) {
                    mPopupListWindow = new PopupListWindow(HuanXinMainActivity.this);
                }
                mPopupListWindow.showAsDropDown(tvCustomer);
            }
        });
    }



    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setClass(this, HuanXinDetailActivity.class);
        switch (v.getId()) {
            case R.id.ib_shop_imageone:
                intent.putExtra(CustomerConstants.INTENT_CODE_IMG_SELECTED_KEY, CustomerConstants.INTENT_CODE_IMG_SELECTED_1);
                break;
            case R.id.ib_shop_imagetwo:
                intent.putExtra(CustomerConstants.INTENT_CODE_IMG_SELECTED_KEY, CustomerConstants.INTENT_CODE_IMG_SELECTED_2);
                break;
            case R.id.ib_shop_imagethree:
                intent.putExtra(CustomerConstants.INTENT_CODE_IMG_SELECTED_KEY, CustomerConstants.INTENT_CODE_IMG_SELECTED_3);
                break;
            case R.id.ib_shop_imagefour:
                intent.putExtra(CustomerConstants.INTENT_CODE_IMG_SELECTED_KEY, CustomerConstants.INTENT_CODE_IMG_SELECTED_4);
                break;
        }
        startActivity(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPopupListWindow != null && mPopupListWindow.isShowing()) {
            mPopupListWindow.dismiss();
        }
    }
}
