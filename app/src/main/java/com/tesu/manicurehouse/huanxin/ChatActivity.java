package com.tesu.manicurehouse.huanxin;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.easemob.easeui.EaseConstant;
import com.tesu.manicurehouse.R;

/**
 * Created by lzan13 on 2015/11/6 21:30.
 * 聊天界面，这里只是个容器，真正实现聊天是在ChatFragment类里{@link ChatFragment}
 */
public class ChatActivity extends HuanXinBaseActivity {

    public static ChatActivity activityInstance;
    private ChatFragment chatFragment;
    String toChatUsername;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_chat);
        activityInstance = this;
        // 聊天人或群id
        toChatUsername = HelpDeskPreferenceUtils.getInstance(this).getSettingCustomerAccount();
        // 可以直接new EaseChatFratFragment使用
        chatFragment = new ChatFragment();
        Intent intent = getIntent();
        intent.putExtra(CustomerConstants.EXTRA_USER_ID, toChatUsername);
        intent.putExtra(CustomerConstants.EXTRA_SHOW_USERNICK, true);
        // 传入参数
        chatFragment.setArguments(intent.getExtras());
        getSupportFragmentManager().beginTransaction().add(R.id.container, chatFragment).commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        activityInstance = null;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        // 点击notification bar进入聊天页面，保证只有一个聊天页面
        String username = intent.getStringExtra("userId");
        if (toChatUsername.equals(username))
            super.onNewIntent(intent);
        else {
            finish();
            startActivity(intent);
        }

    }

    @Override
    public void onBackPressed() {
        chatFragment.onBackPressed();
    }

    public void sendRobotMessage(String txtContent, String menuId) {
        chatFragment.sendRobotMessage(txtContent, menuId);
    }

}
