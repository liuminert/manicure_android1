package com.tesu.manicurehouse.huanxin;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.easemob.EMCallBack;
import com.easemob.EMError;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.exceptions.EaseMobException;
import com.tesu.manicurehouse.R;
import com.tesu.manicurehouse.util.SharedPrefrenceUtils;


/**
 * Created by lzan13 on 2015/11/6 21:00.
 * 登陆界面，实现了环信账户的登录
 */
public class HuanXinLoginActivity extends HuanXinBaseActivity {


    private boolean progressShow;
    private ProgressDialog progressDialog;
    private int selectedIndex = CustomerConstants.INTENT_CODE_IMG_SELECTED_DEFAULT;
    private int messageToIndex = CustomerConstants.MESSAGE_TO_DEFAULT;
    private String user_id;
    private String userPwd;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        Intent intent = getIntent();
        selectedIndex = intent.getIntExtra(CustomerConstants.INTENT_CODE_IMG_SELECTED_KEY,
                CustomerConstants.INTENT_CODE_IMG_SELECTED_DEFAULT);
        messageToIndex = intent.getIntExtra(CustomerConstants.MESSAGE_TO_INTENT_EXTRA, CustomerConstants.MESSAGE_TO_DEFAULT);

        user_id = SharedPrefrenceUtils.getString(this, "user_id");
        userPwd = CustomerConstants.DEFAULT_ACCOUNT_PWD;

        checkIsLogin();

    }

    //EMChat.getInstance().isLoggedIn() 可以检测是否已经登录过环信，如果登录过则环信SDK会自动登录，不需要再次调用登录操作
    private void checkIsLogin(){
        if (EMChat.getInstance().isLoggedIn()) {
            progressDialog = getProgressDialog();
            progressDialog.setMessage("Being contact customer service, please wait..");
            progressDialog.show();
            new Thread(new Runnable() {

                @Override
                public void run() {
                    try {
                        //加载本地数据库中的消息到内存中
                        EMChatManager.getInstance().loadAllConversations();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    toChatActivity();
                }
            }).start();
        } else {
            //随机创建一个用户并登录环信服务器
            createRandomAccountAndLoginChatServer();
        }
    }

    private void createRandomAccountAndLoginChatServer() {
        // 使用user_id
//        final String randomAccount = MLSPUtil.getRandomAccount();
        progressDialog = getProgressDialog();
        progressDialog.setMessage("System is automatically registered users for you...");
        progressDialog.show();
        createAccountToServer(user_id, userPwd, new EMCallBack() {

            @Override
            public void onSuccess() {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        //登录环信服务器
                        if(progressDialog.isShowing()){
                            progressDialog.dismiss();
                        }
                        loginHuanxinServer(user_id, userPwd);
                    }
                });
            }

            @Override
            public void onProgress(int progress, String status) {
            }

            @Override
            public void onError(final int errorCode, final String message) {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        if (!HuanXinLoginActivity.this.isFinishing()) {
                            progressDialog.dismiss();
                        }
                        if (errorCode == EMError.NONETWORK_ERROR) {
                            Toast.makeText(getApplicationContext(), "网络不可用", Toast.LENGTH_SHORT).show();
                            finish();
                        } else if (errorCode == EMError.USER_ALREADY_EXISTS) {
//                            if(progressDialog.isShowing()){
//                                progressDialog.dismiss();
//                            }
                            loginHuanxinServer(user_id, userPwd);
//                            Toast.makeText(getApplicationContext(), "用户已存在", Toast.LENGTH_SHORT).show();
                        } else if (errorCode == EMError.UNAUTHORIZED) {
                            Toast.makeText(getApplicationContext(), "无开放注册权限", Toast.LENGTH_SHORT).show();
                            finish();
                        } else if (errorCode == EMError.ILLEGAL_USER_NAME) {
                            Toast.makeText(getApplicationContext(), "用户名非法", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(), "注册失败：" + message, Toast.LENGTH_SHORT).show();
                            finish();
                        }

                    }
                });
            }
        });
    }

    //注册用户
    private void createAccountToServer(final String uname, final String pwd, final EMCallBack callback) {
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    EMChatManager.getInstance().createAccountOnServer(uname, pwd);
                    if (callback != null) {
                        callback.onSuccess();
                    }
                } catch (EaseMobException e) {
                    if (callback != null) {
                        callback.onError(e.getErrorCode(), e.getMessage());
                    }
                }
            }
        });
        thread.start();
    }

    private ProgressDialog getProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(HuanXinLoginActivity.this);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    progressShow = false;
                }
            });
        }
        return progressDialog;
    }

    public void loginHuanxinServer(final String uname, final String upwd) {
        progressShow = true;
        progressDialog = getProgressDialog();
        progressDialog.setMessage("System is automatically login users for you...");
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
        // login huanxin server
        EMChatManager.getInstance().login(uname, upwd, new EMCallBack() {
            @Override
            public void onSuccess() {
                if (!progressShow) {
                    return;
                }
                CustomerHelper.getInstance().setCurrentUserName(uname);
                CustomerHelper.getInstance().setCurrentPassword(upwd);
                try {
                    EMChatManager.getInstance().loadAllConversations();
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
                toChatActivity();

            }

            @Override
            public void onProgress(int progress, String status) {
            }

            @Override
            public void onError(final int code, final String message) {
                if (!progressShow) {
                    return;
                }
                runOnUiThread(new Runnable() {
                    public void run() {
                        progressDialog.dismiss();
                        Toast.makeText(HuanXinLoginActivity.this,
                                "Contact customer service failure:" + message,
                                Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
            }
        });
    }

    private void toChatActivity() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!HuanXinLoginActivity.this.isFinishing())
                    progressDialog.dismiss();
                // 进入主页面
                startActivity(new Intent(HuanXinLoginActivity.this, ChatActivity.class).putExtra(
                        CustomerConstants.INTENT_CODE_IMG_SELECTED_KEY, selectedIndex).putExtra(
                        CustomerConstants.MESSAGE_TO_INTENT_EXTRA, messageToIndex));
                finish();
            }
        });
    }

}

