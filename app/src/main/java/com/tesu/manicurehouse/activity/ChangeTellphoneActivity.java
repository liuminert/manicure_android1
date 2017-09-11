package com.tesu.manicurehouse.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.CountDownTimer;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
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
import com.tesu.manicurehouse.protocol.SendVerificationCodeProtocol;
import com.tesu.manicurehouse.protocol.ValidatedProtocol;
import com.tesu.manicurehouse.request.SendVerificationCodeRequest;
import com.tesu.manicurehouse.request.ValidatedRequest;
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
 * 更换手机页面
 */
public class ChangeTellphoneActivity extends BaseActivity implements View.OnClickListener {

    private ImageView iv_top_back;
    private View rootView;
    private Button btn_comit;
    private String url;
    private Dialog loadingDialog;
    private SendVerificationCodeResponse sendVerificationCodeResponse;
    private String mobile_phone;
    private String old_mobile_phone;
    private String password;
    private String code;
    private EditText et_olde_tellphone;
    private EditText et_pwd;
    private EditText et_new_tellphone;
    private EditText et_code;
    private TextView tv_getcode;
    int time = 60;
    @Override
    protected View initView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        rootView = View.inflate(this, R.layout.activity_change_tellphone, null);
        setContentView(rootView);
        iv_top_back = (ImageView) rootView.findViewById(R.id.iv_top_back);
        btn_comit=(Button)rootView.findViewById(R.id.btn_comit);
        et_new_tellphone=(EditText)rootView.findViewById(R.id.et_new_tellphone);
        et_olde_tellphone=(EditText)rootView.findViewById(R.id.et_olde_tellphone);
        et_pwd=(EditText)rootView.findViewById(R.id.et_pwd);
        et_code=(EditText)rootView.findViewById(R.id.et_code);
        tv_getcode=(TextView)rootView.findViewById(R.id.tv_getcode);
        initData();
        return null;
    }


    public void initData() {
        loadingDialog = DialogUtils.createLoadDialog(ChangeTellphoneActivity.this, true);
        btn_comit.setOnClickListener(this);
        iv_top_back.setOnClickListener(this);
        tv_getcode.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_getcode:{
                old_mobile_phone=et_olde_tellphone.getText().toString().trim();
                mobile_phone=et_new_tellphone.getText().toString().trim();
                password=et_pwd.getText().toString().trim();
                if(!TextUtils.isEmpty(mobile_phone)&&!TextUtils.isEmpty(old_mobile_phone)&&!TextUtils.isEmpty(password)){
                    runGetCode();
                }
                else{
                    if(TextUtils.isEmpty(mobile_phone)){
                        DialogUtils.showAlertDialog(ChangeTellphoneActivity.this,
                                "新号码不能为空！");
                    }else if(TextUtils.isEmpty(old_mobile_phone)){
                        DialogUtils.showAlertDialog(ChangeTellphoneActivity.this,
                                "原号码不能为空！");
                    }
                    else{
                        DialogUtils.showAlertDialog(ChangeTellphoneActivity.this,
                                "密码不能为空！");
                    }
                }
                break;
            }
            case R.id.iv_top_back: {
                finish();
                overridePendingTransition(R.anim.animprv_in, R.anim.animprv_out);
                break;
            }
            case R.id.btn_comit: {

                code=et_code.getText().toString().trim();
                if(!TextUtils.isEmpty(code)){
                    runAsyncTask();
                }
                break;
            }
        }
    }

    public void runGetCode() {
        loadingDialog.show();
        SendVerificationCodeProtocol sendVerificationCodeProtocol = new SendVerificationCodeProtocol();
        SendVerificationCodeRequest sendVerificationCodeRequest = new SendVerificationCodeRequest();
        url = sendVerificationCodeProtocol.getApiFun();
        sendVerificationCodeRequest.map.put("mobile_phone", mobile_phone);
        sendVerificationCodeRequest.map.put("password", password);
        sendVerificationCodeRequest.map.put("verification_type", "2");
        sendVerificationCodeRequest.map.put("old_mobile_phone",old_mobile_phone);
        sendVerificationCodeRequest.map.put("user_id",SharedPrefrenceUtils.getString(ChangeTellphoneActivity.this,"user_id"));
        MyVolley.uploadNoFile(MyVolley.POST, url, sendVerificationCodeRequest.map, new MyVolley.VolleyCallback() {
            @Override
            public void dealWithJson(String address, String json) {

                Gson gson = new Gson();
                sendVerificationCodeResponse = gson.fromJson(json, SendVerificationCodeResponse.class);
                LogUtils.e("sendVerificationCodeResponse:" + json.toString());
                if (sendVerificationCodeResponse.code.equals("0")) {
                    loadingDialog.dismiss();
                    Countdowmtimer(60000);
                } else {
                    loadingDialog.dismiss();
                    DialogUtils.showAlertDialog(ChangeTellphoneActivity.this,
                            sendVerificationCodeResponse.resultText);
                }


            }

            @Override
            public void dealWithError(String address, String error) {
                loadingDialog.dismiss();
                DialogUtils.showAlertDialog(ChangeTellphoneActivity.this, error);
            }
        });
    }

    public void runAsyncTask() {
        loadingDialog.show();
        ValidatedProtocol validatedProtocol = new ValidatedProtocol();
        ValidatedRequest validatedRequest = new ValidatedRequest();
        url = validatedProtocol.getApiFun();
        validatedRequest.map.put("mobile_phone", mobile_phone);
        validatedRequest.map.put("verification_code", code);
        validatedRequest.map.put("verification_type", String.valueOf(2));
        MyVolley.uploadNoFile(MyVolley.POST, url, validatedRequest.map, new MyVolley.VolleyCallback() {
            @Override
            public void dealWithJson(String address, String json) {

                Gson gson = new Gson();
                ValidatedResponse validatedResponse = gson.fromJson(json, ValidatedResponse.class);
                LogUtils.e("sendVerificationCodeResponse:" + validatedResponse.toString());
                if (validatedResponse.code.equals("0")) {
                    loadingDialog.dismiss();
                    SharedPrefrenceUtils.setBoolean(ChangeTellphoneActivity.this,
                            "isLogin", false);
                    Intent intent=new Intent(ChangeTellphoneActivity.this,LoginActivity.class);
                    UIUtils.startActivityPreAnim(intent);
                    finish();
                    finishActivity();
                } else {
                    loadingDialog.dismiss();
                    DialogUtils.showAlertDialog(ChangeTellphoneActivity.this,
                            validatedResponse.resultText);
                }


            }

            @Override
            public void dealWithError(String address, String error) {
                loadingDialog.dismiss();
                DialogUtils.showAlertDialog(ChangeTellphoneActivity.this, error);
            }
        });
    }

    /** 计时器 */
    public void Countdowmtimer(long dodate) {
        new CountDownTimer(dodate, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                time = time - 1;
                tv_getcode.setText( time+"s");
            }

            @Override
            // 计时结束
            public void onFinish() {
                time = 60;
                tv_getcode.setText( "0s");
            }
        }.start();
    }
}
