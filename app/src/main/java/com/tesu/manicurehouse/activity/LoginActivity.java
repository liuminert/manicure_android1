package com.tesu.manicurehouse.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.tesu.manicurehouse.R;
import com.tesu.manicurehouse.adapter.ProblemAdapter;
import com.tesu.manicurehouse.base.BaseActivity;
import com.tesu.manicurehouse.base.MyVolley;
import com.tesu.manicurehouse.fragment.ControlTabFragment;
import com.tesu.manicurehouse.protocol.LoginProtocol;
import com.tesu.manicurehouse.protocol.PartyLandingProtocol;
import com.tesu.manicurehouse.protocol.SendVerificationCodeProtocol;
import com.tesu.manicurehouse.request.LoginRequest;
import com.tesu.manicurehouse.request.PartyLandingRequest;
import com.tesu.manicurehouse.request.SendVerificationCodeRequest;
import com.tesu.manicurehouse.response.LoginResponse;
import com.tesu.manicurehouse.response.PartyLandingResponse;
import com.tesu.manicurehouse.response.SendVerificationCodeResponse;
import com.tesu.manicurehouse.util.DialogUtils;
import com.tesu.manicurehouse.util.LogUtils;
import com.tesu.manicurehouse.util.LoginUtils;
import com.tesu.manicurehouse.util.SharedPrefrenceUtils;
import com.tesu.manicurehouse.util.UIUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;

/**
 * 类描述：
 * 创建人：许明达
 * 创建时间：2016/8/16 11:40
 * 登录页面
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener,
        PlatformActionListener {

    private TextView tv_top_back;
    private View rootView;
    private ControlTabFragment ctf;
    private Button btn_login;
    private TextView tv_register;
    private TextView tv_find_pwd;
    private ImageView iv_qq;
    private ImageView iv_sina;
    private ImageView iv_weixin;
    private static final int MSG_AUTH_CANCEL = 0;
    private static final int MSG_AUTH_ERROR = 1;
    private static final int MSG_AUTH_COMPLETE = 2;
    HashMap<String, Object> res;

    private String url;
    //接口请求菊花
    private Dialog loadingDialog;
    private LoginResponse loginResponse;
    private String mobile_phone;
    private String password;
    private EditText et_login_phone;
    private EditText et_pwd;
    /**
     * 第三方登录成功后的openid
     */
    private String openId;
    //1:qq，2:微信，3:新浪
    private int platform;
    //头像url
    private String avatar;
    //昵称
    private String  alias	;
    private String verification_code;
    //0首页,1商城,2晒美甲,3社区,4我的
    private int type;
    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case MSG_AUTH_CANCEL:
                    // 取消授权
                    Toast.makeText(LoginActivity.this, R.string.auth_cancel,
                            Toast.LENGTH_SHORT).show();

                    break;
                case MSG_AUTH_ERROR:
                    // 授权失败
                    Toast.makeText(LoginActivity.this, R.string.auth_error,
                            Toast.LENGTH_SHORT).show();

                    break;
                case MSG_AUTH_COMPLETE:
                    LogUtils.e("进来");
                    // 授权成功
                    Toast.makeText(LoginActivity.this, R.string.auth_complete,
                            Toast.LENGTH_SHORT).show();
                    Object[] objs = (Object[]) msg.obj;
                    String platform = (String) objs[0];
                    res = (HashMap<String, Object>) objs[1];
                    runThridLogin();
                    break;

            }
        }

        ;
    };

    @Override
    protected View initView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        rootView = View.inflate(this, R.layout.activity_login, null);
        setContentView(rootView);
        et_pwd= (EditText) rootView.findViewById(R.id.et_pwd);
        et_login_phone= (EditText) rootView.findViewById(R.id.et_login_phone);
        tv_top_back = (TextView) rootView.findViewById(R.id.tv_top_back);
        btn_login = (Button) rootView.findViewById(R.id.btn_login);
        tv_register = (TextView) rootView.findViewById(R.id.tv_register);
        tv_find_pwd = (TextView) rootView.findViewById(R.id.tv_find_pwd);
        iv_qq = (ImageView) rootView.findViewById(R.id.iv_qq);
        iv_sina = (ImageView) rootView.findViewById(R.id.iv_sina);
        iv_weixin = (ImageView) rootView.findViewById(R.id.iv_weixin);
        if (ctf == null) {
            ctf = MainActivity.getCtf();
        }
        initData();
        return null;
    }


    public void initData() {
        type=getIntent().getIntExtra("type",-1);
        loadingDialog = DialogUtils.createLoadDialog(LoginActivity.this, true);
        ShareSDK.initSDK(LoginActivity.this);
        tv_register.setOnClickListener(this);
        btn_login.setOnClickListener(this);
        tv_top_back.setOnClickListener(this);
        tv_find_pwd.setOnClickListener(this);
        iv_qq.setOnClickListener(this);
        iv_sina.setOnClickListener(this);
        iv_weixin.setOnClickListener(this);

    }

    // 执行授权,获取用户信息
    // 文档：http://wiki.mob.com/Android_%E8%8E%B7%E5%8F%96%E7%94%A8%E6%88%B7%E8%B5%84%E6%96%99
    private void authorize(Platform plat) {
        if (plat == null) {
            return;
        }

        plat.setPlatformActionListener(this);
        // 关闭SSO授权
        plat.SSOSetting(false);
        plat.showUser(null);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_qq: {
                platform=1;
                Platform qq = ShareSDK.getPlatform(QQ.NAME);
                // 取消授权，每次都需要重新授权
                qq.removeAccount();
                ShareSDK.removeCookieOnAuthorize(true);
                // 授权操作
                authorize(qq);
                break;
            }
            case R.id.iv_weixin: {
                LogUtils.e("微信");
                platform=2;
                Platform wechat = ShareSDK.getPlatform(Wechat.NAME);
                // 取消授权，每次都需要重新授权
                wechat.removeAccount();
                ShareSDK.removeCookieOnAuthorize(true);
                // 授权操作
                authorize(wechat);
                break;
            }
            case R.id.iv_sina: {
                platform=3;
                Platform sina = ShareSDK.getPlatform(SinaWeibo.NAME);
                // 取消授权，每次都需要重新授权
                sina.removeAccount();
                ShareSDK.removeCookieOnAuthorize(true);
                // 授权操作
                authorize(sina);
                break;
            }
            case R.id.tv_find_pwd: {
                Intent intent = new Intent(LoginActivity.this, FindPwdActivity.class);
                UIUtils.startActivityNextAnim(intent);
                break;
            }
            case R.id.tv_register: {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                UIUtils.startActivityNextAnim(intent);
                setFinish();
                break;
            }
            case R.id.tv_top_back: {
                ctf.setChecked(ctf.beforeIndex);
                ctf.mCurrentIndex = ctf.beforeIndex;
                finish();
                overridePendingTransition(R.anim.animprv_in, R.anim.animprv_out);
                break;
            }
            case R.id.btn_login: {
                mobile_phone = et_login_phone.getText().toString().trim();
                password = et_pwd.getText().toString().trim();

                if (!TextUtils.isEmpty(mobile_phone)
                        && !TextUtils.isEmpty(password)) {
                    runAsyncTask();
                } else {
                    if (TextUtils.isEmpty(mobile_phone)) {
                        DialogUtils.showAlertDialog(LoginActivity.this,
                                "手机号码不能为空！");
                    } else if (TextUtils.isEmpty(password)) {
                        DialogUtils.showAlertDialog(LoginActivity.this,
                                "密码不能为空！");
                    }
                }
                break;
            }
        }
    }

    public void onComplete(Platform platform, int action,
                           HashMap<String, Object> res) {
        if (action == Platform.ACTION_USER_INFOR) {

            avatar=platform.getDb().getUserIcon();
            openId = platform.getDb().getUserIcon();
            alias=platform.getDb().getUserName();

            LogUtils.e("成功："+res.toString());
            Message msg = new Message();
            msg.what = MSG_AUTH_COMPLETE;
            msg.obj = new Object[]{platform.getName(), res};
            handler.sendMessage(msg);
        }
    }

    public void onError(Platform platform, int action, Throwable t) {
        if (action == Platform.ACTION_USER_INFOR) {
            handler.sendEmptyMessage(MSG_AUTH_ERROR);
        }
        t.printStackTrace();
    }

    public void onCancel(Platform platform, int action) {
        if (action == Platform.ACTION_USER_INFOR) {
            handler.sendEmptyMessage(MSG_AUTH_CANCEL);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == 4) {
            onKeyDownMethod(keyCode, event);
            ctf.setChecked(MainActivity.getCtf().beforeIndex);
            ctf.mCurrentIndex = ctf.beforeIndex;
            finish();
            overridePendingTransition(R.anim.animprv_in, R.anim.animprv_out);
        }
        return super.onKeyDown(keyCode, event);
    }

    public void runAsyncTask() {
        loadingDialog.show();
        LoginProtocol loginProtocol = new LoginProtocol();
        LoginRequest loginRequest = new LoginRequest();
        url = loginProtocol.getApiFun();
        loginRequest.map.put("mobile_phone", mobile_phone);
        loginRequest.map.put("password", password);
        MyVolley.uploadNoFile(MyVolley.POST, url, loginRequest.map, new MyVolley.VolleyCallback() {
            @Override
            public void dealWithJson(String address, String json) {

                Gson gson = new Gson();
                loginResponse = gson.fromJson(json, LoginResponse.class);
                LogUtils.e("sendVerificationCodeResponse:" + json.toString());
                if (loginResponse.code.equals("0")) {
                    loadingDialog.dismiss();
                    SharedPrefrenceUtils.setBoolean(LoginActivity.this,
                            "isLogin", true);
                    SharedPrefrenceUtils.setString(LoginActivity.this,
                            "user_id", String.valueOf(loginResponse.user_id));
                    SharedPrefrenceUtils.setString(LoginActivity.this,
                            "mobile_phone", mobile_phone);
                    SharedPrefrenceUtils.setString(LoginActivity.this,
                            "synthesis_url", loginResponse.synthesis_url);
                    SharedPrefrenceUtils.setString(LoginActivity.this,
                            "uploadfile_url", loginResponse.uploadfile_url);
                    SharedPrefrenceUtils.setString(LoginActivity.this,
                            "user_name", loginResponse.user_name);
                    if(type>-1){
                        ctf.setChecked(type);
                        ctf.mCurrentIndex = type;
                        ctf.switchCurrentPage();
                    }
                    else{
                        ctf.setChecked(4);
                        ctf.mCurrentIndex = 4;
                        ctf.switchCurrentPage();
                    }
                    finish();
                    overridePendingTransition(R.anim.animprv_in, R.anim.animprv_out);
                } else {
                    loadingDialog.dismiss();
                    DialogUtils.showAlertDialog(LoginActivity.this,
                            loginResponse.resultText);

                }


            }

            @Override
            public void dealWithError(String address, String error) {
                loadingDialog.dismiss();
                DialogUtils.showAlertDialog(LoginActivity.this, error);
            }
        });
    }

    public void runThridLogin() {
        loadingDialog.show();
        PartyLandingProtocol partyLandingProtocol = new PartyLandingProtocol();
        PartyLandingRequest partyLandingRequest = new PartyLandingRequest();
        url = partyLandingProtocol.getApiFun();
        partyLandingRequest.map.put("platform", String.valueOf(platform));
        partyLandingRequest.map.put("unique_id", openId);
        partyLandingRequest.map.put("alias", alias);
        partyLandingRequest.map.put("avatar", avatar);
        partyLandingRequest.map.put("mobile_phone", mobile_phone);
        partyLandingRequest.map.put("verification_code", verification_code);
        LogUtils.e("partyLandingRequest.map:"+partyLandingRequest.map.toString());
        MyVolley.uploadNoFile(MyVolley.POST, url, partyLandingRequest.map, new MyVolley.VolleyCallback() {
            @Override
            public void dealWithJson(String address, String json) {

                Gson gson = new Gson();
                PartyLandingResponse partyLandingResponse = gson.fromJson(json, PartyLandingResponse.class);
                LogUtils.e("sendVerificationCodeResponse:" + json.toString());
                if (partyLandingResponse.code.equals("0")) {
                    loadingDialog.dismiss();
                    SharedPrefrenceUtils.setBoolean(LoginActivity.this,
                            "isLogin", true);
                    SharedPrefrenceUtils.setString(LoginActivity.this,
                            "user_id", String.valueOf(partyLandingResponse.user_id));
                    SharedPrefrenceUtils.setString(LoginActivity.this,
                            "mobile_phone", mobile_phone);
                    SharedPrefrenceUtils.setString(LoginActivity.this,
                            "synthesis_url", partyLandingResponse.synthesis_url);
                    SharedPrefrenceUtils.setString(LoginActivity.this,
                            "uploadfile_url", partyLandingResponse.uploadfile_url);
                    SharedPrefrenceUtils.setString(LoginActivity.this,
                            "user_name", partyLandingResponse.user_name);
                    if(type>-1){
                        ctf.setChecked(type);
                        ctf.mCurrentIndex = type;
                        ctf.switchCurrentPage();
                    }
                    else{
                        ctf.setChecked(4);
                        ctf.mCurrentIndex = 4;
                        ctf.switchCurrentPage();
                    }

                    finish();
                    overridePendingTransition(R.anim.animprv_in, R.anim.animprv_out);
                } else {
                    Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                    intent.putExtra("isThridLogin",true);
                    intent.putExtra("platform",String.valueOf(platform));
                    intent.putExtra("openId",openId);
                    intent.putExtra("alias",alias);
                    intent.putExtra("avatar",avatar);
                    UIUtils.startActivityNextAnim(intent);
                    setFinish();
                    loadingDialog.dismiss();
//                    DialogUtils.showAlertDialog(LoginActivity.this,
//                            partyLandingResponse.resultText);
                }


            }

            @Override
            public void dealWithError(String address, String error) {
                loadingDialog.dismiss();
                DialogUtils.showAlertDialog(LoginActivity.this, error);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1){
            if(resultCode==2){
                verification_code=data.getStringExtra("code");
                mobile_phone=data.getStringExtra("mobile_phone");
                runThridLogin();
                LogUtils.e("code"+data.getStringExtra("code"));
            }
        }
    }
}
