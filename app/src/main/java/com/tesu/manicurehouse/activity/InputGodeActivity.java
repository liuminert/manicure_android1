package com.tesu.manicurehouse.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.tesu.manicurehouse.R;
import com.tesu.manicurehouse.base.BaseActivity;
import com.tesu.manicurehouse.base.MyVolley;
import com.tesu.manicurehouse.fragment.ControlTabFragment;
import com.tesu.manicurehouse.protocol.LoginProtocol;
import com.tesu.manicurehouse.protocol.PartyLandingProtocol;
import com.tesu.manicurehouse.protocol.SendVerificationCodeProtocol;
import com.tesu.manicurehouse.protocol.ValidatedProtocol;
import com.tesu.manicurehouse.request.LoginRequest;
import com.tesu.manicurehouse.request.PartyLandingRequest;
import com.tesu.manicurehouse.request.SendVerificationCodeRequest;
import com.tesu.manicurehouse.request.ValidatedRequest;
import com.tesu.manicurehouse.response.LoginResponse;
import com.tesu.manicurehouse.response.PartyLandingResponse;
import com.tesu.manicurehouse.response.SendVerificationCodeResponse;
import com.tesu.manicurehouse.response.ValidatedResponse;
import com.tesu.manicurehouse.util.DialogUtils;
import com.tesu.manicurehouse.util.LogUtils;
import com.tesu.manicurehouse.util.SharedPrefrenceUtils;
import com.tesu.manicurehouse.util.UIUtils;

/**
 * 类描述：
 * 创建人：许明达
 * 创建时间：2016/8/30 10:40
 * 填写验证码页面
 */
public class InputGodeActivity extends BaseActivity implements View.OnClickListener {

    private ImageView iv_top_back;
    private View rootView;
    private Button btn_next;
    //0表示注册，1表示找回密码, 2表示修改手机号码
    private  int verification_type=-1;
    private TextView tv_time;
    int time = 60;
    private String url;
    private Dialog loadingDialog;
    private ValidatedResponse validatedResponse;
    private String mobile_phone;
    private String verification_code;
    private EditText et_code;
    private TextView tv_title;
    private String password;
    private LoginResponse loginResponse;
    private ControlTabFragment ctf;
    private boolean isThridLogin;
    /**
     * 第三方登录成功后的openid
     */
    private String openId;
    //1:qq，2:微信，3:新浪
    private String platform;
    //头像url
    private String avatar;
    //昵称
    private String  alias;
    @Override
    protected View initView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        rootView = View.inflate(this, R.layout.activity_input_code, null);
        setContentView(rootView);
        et_code=(EditText)rootView.findViewById(R.id.et_code);
        iv_top_back = (ImageView) rootView.findViewById(R.id.iv_top_back);
        btn_next=(Button)rootView.findViewById(R.id.btn_next);
        tv_title=(TextView)rootView.findViewById(R.id.tv_title);
        tv_time=(TextView)rootView.findViewById(R.id.tv_time);

        if (ctf == null) {
            ctf = MainActivity.getCtf();
        }

        initData();
        return null;
    }


    public void initData() {
        loadingDialog = DialogUtils.createLoadDialog(InputGodeActivity.this, true);
        Intent intent=getIntent();
        openId=intent.getStringExtra("openId");
        platform=intent.getStringExtra("platform");
        alias=intent.getStringExtra("alias");
        avatar=intent.getStringExtra("avatar");
        mobile_phone=intent.getStringExtra("mobile_phone");
        password=intent.getStringExtra("password");
        isThridLogin=intent.getBooleanExtra("isThridLogin",false);
        verification_type=intent.getIntExtra("verification_type", -1);
        tv_title.setText("已向"+mobile_phone+"发送短信验证码");
        if(verification_type==0){
            btn_next.setText("完成");
        }
        else if(verification_type==1){
            btn_next.setText("下一步");
        }
        btn_next.setOnClickListener(this);
        iv_top_back.setOnClickListener(this);
        Countdowmtimer(60000);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_top_back: {
                finish();
                overridePendingTransition(R.anim.animprv_in, R.anim.animprv_out);
                break;
            }
            case R.id.btn_next: {
                verification_code=et_code.getText().toString().trim();
                if(!TextUtils.isEmpty(verification_code)){
                    if (isThridLogin) {
                        runThridLogin();
                    } else {
                        runAsyncTask();
                    }
                }
                else{
                    DialogUtils.showAlertDialog(InputGodeActivity.this, "请输入验证码！");
                }
                break;
            }
        }
    }
    /** 计时器 */
    public void Countdowmtimer(long dodate) {
        new CountDownTimer(dodate, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                time = time - 1;
                tv_time.setText("收到短信大约需要" + time + "秒");
                SpannableStringBuilder style=new SpannableStringBuilder(tv_time.getText());
                if(time>9){
                    style.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.pink_text)), 8, 10, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                    tv_time.setText(style);
                }
                else{
                    style.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.pink_text)), 8, 9, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                    tv_time.setText(style);
                }
            }

            @Override
            // 计时结束
            public void onFinish() {
                time = 60;
                tv_time.setText("验证码已过期，请重新获取！");
//                SpannableStringBuilder style=new SpannableStringBuilder(tv_time.getText());
//                style.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.pink_text)), 8, 9, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
//                tv_time.setText(style);
            }
        }.start();
    }

    public void runAsyncTask() {
        loadingDialog.show();
        ValidatedProtocol validatedProtocol = new ValidatedProtocol();
        ValidatedRequest validatedRequest = new ValidatedRequest();
        url = validatedProtocol.getApiFun();
        validatedRequest.map.put("mobile_phone", mobile_phone);
        validatedRequest.map.put("verification_code", verification_code);
        validatedRequest.map.put("verification_type", String.valueOf(verification_type));
        LogUtils.e("verification_type:"+verification_type);
        MyVolley.uploadNoFile(MyVolley.POST, url, validatedRequest.map, new MyVolley.VolleyCallback() {
            @Override
            public void dealWithJson(String address, String json) {

                Gson gson = new Gson();
                validatedResponse = gson.fromJson(json, ValidatedResponse.class);
                LogUtils.e("sendVerificationCodeResponse:" + validatedResponse.toString());
                if (validatedResponse.code.equals("0")) {
                    loadingDialog.dismiss();

                        if (verification_type == 0) {
                            logAsyncTask();
                        } else if (verification_type == 1) {
                            setFinish();
                            Intent intent = new Intent(InputGodeActivity.this, SetNewPwdActivity.class);
                            intent.putExtra("mobile_phone", mobile_phone);
                            UIUtils.startActivityNextAnim(intent);
                        }

                } else {
                    loadingDialog.dismiss();
                    DialogUtils.showAlertDialog(InputGodeActivity.this,
                            validatedResponse.resultText);
                }


            }

            @Override
            public void dealWithError(String address, String error) {
                loadingDialog.dismiss();
                DialogUtils.showAlertDialog(InputGodeActivity.this, error);
            }
        });
    }

    public void logAsyncTask() {
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
                    SharedPrefrenceUtils.setBoolean(InputGodeActivity.this,
                            "isLogin", true);
                    SharedPrefrenceUtils.setString(InputGodeActivity.this,
                            "user_id", String.valueOf(loginResponse.user_id));
                    SharedPrefrenceUtils.setString(InputGodeActivity.this,
                            "mobile_phone", mobile_phone);
                    ctf.setChecked(4);
                    ctf.mCurrentIndex = 4;
                    ctf.switchCurrentPage();
                    setFinish();
                    finishActivity();
                    overridePendingTransition(R.anim.animprv_in, R.anim.animprv_out);
                } else {
                    loadingDialog.dismiss();
                    DialogUtils.showAlertDialog(InputGodeActivity.this,
                            loginResponse.resultText);
                }


            }

            @Override
            public void dealWithError(String address, String error) {
                loadingDialog.dismiss();
                DialogUtils.showAlertDialog(InputGodeActivity.this, error);
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
        MyVolley.uploadNoFile(MyVolley.POST, url, partyLandingRequest.map, new MyVolley.VolleyCallback() {
            @Override
            public void dealWithJson(String address, String json) {

                Gson gson = new Gson();
                PartyLandingResponse partyLandingResponse = gson.fromJson(json, PartyLandingResponse.class);
                LogUtils.e("sendVerificationCodeResponse第三方:" + json.toString());
                if (partyLandingResponse.code.equals("0")) {
                    loadingDialog.dismiss();
                    SharedPrefrenceUtils.setBoolean(InputGodeActivity.this,
                            "isLogin", true);
                    SharedPrefrenceUtils.setString(InputGodeActivity.this,
                            "user_id", String.valueOf(partyLandingResponse.user_id));
                    SharedPrefrenceUtils.setString(InputGodeActivity.this,
                            "mobile_phone", mobile_phone);
                    SharedPrefrenceUtils.setString(InputGodeActivity.this,
                            "synthesis_url", partyLandingResponse.synthesis_url);
                    SharedPrefrenceUtils.setString(InputGodeActivity.this,
                            "uploadfile_url", partyLandingResponse.uploadfile_url);
                    ctf.setChecked(4);
                    ctf.mCurrentIndex = 4;
                    ctf.switchCurrentPage();
                    finishActivity();
                    finish();
                    overridePendingTransition(R.anim.animprv_in, R.anim.animprv_out);
                } else {
                    loadingDialog.dismiss();
                    DialogUtils.showAlertDialog(InputGodeActivity.this,
                            partyLandingResponse.resultText);
                }


            }

            @Override
            public void dealWithError(String address, String error) {
                loadingDialog.dismiss();
                DialogUtils.showAlertDialog(InputGodeActivity.this, error);
            }
        });
    }
}
