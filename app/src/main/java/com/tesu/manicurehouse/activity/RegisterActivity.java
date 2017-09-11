package com.tesu.manicurehouse.activity;

import android.app.Dialog;
import android.content.Intent;
import android.text.TextUtils;
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
import com.tesu.manicurehouse.protocol.SendVerificationCodeProtocol;
import com.tesu.manicurehouse.request.SendVerificationCodeRequest;
import com.tesu.manicurehouse.response.SendVerificationCodeResponse;
import com.tesu.manicurehouse.util.DialogUtils;
import com.tesu.manicurehouse.util.LogUtils;
import com.tesu.manicurehouse.util.SharedPrefrenceUtils;
import com.tesu.manicurehouse.util.UIUtils;

/**
 * 类描述：
 * 创建人：许明达
 * 创建时间：2016/8/30 10:40
 * 注册页面
 */
public class RegisterActivity extends BaseActivity implements View.OnClickListener {

    private ImageView iv_top_back;
    private View rootView;
    private Button btn_register;
    private EditText et_register_phone;
    private EditText et_pwd;
    private String url;
    private Dialog loadingDialog;
    private SendVerificationCodeResponse sendVerificationCodeResponse;
    private String mobile_phone;
    private String password;
    private boolean isThridLogin=false;
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
        rootView = View.inflate(this, R.layout.activity_register, null);
        setContentView(rootView);
        iv_top_back = (ImageView) rootView.findViewById(R.id.iv_top_back);
        btn_register=(Button)rootView.findViewById(R.id.btn_register);
        et_pwd=(EditText)rootView.findViewById(R.id.et_pwd);
        et_register_phone=(EditText)rootView.findViewById(R.id.et_register_phone);
        initData();
        return null;
    }


    public void initData() {
        Intent intent=getIntent();
        isThridLogin=intent.getBooleanExtra("isThridLogin",false);
        openId=intent.getStringExtra("openId");
        platform=intent.getStringExtra("platform");
        alias=intent.getStringExtra("alias");
        avatar=intent.getStringExtra("avatar");
        loadingDialog = DialogUtils.createLoadDialog(RegisterActivity.this, true);
        btn_register.setOnClickListener(this);
        iv_top_back.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_top_back: {
                finish();
                overridePendingTransition(R.anim.animprv_in, R.anim.animprv_out);
                break;
            }
            case R.id.btn_register: {
                mobile_phone=et_register_phone.getText().toString().trim();
                password=et_pwd.getText().toString().trim();
                if(!TextUtils.isEmpty(mobile_phone)&&!TextUtils.isEmpty(password)){
                    if(mobile_phone.length()==11&&password.length()>5){
                        runAsyncTask();
                    }
                    else{
                        if(mobile_phone.length()<11) {
                            DialogUtils.showAlertDialog(RegisterActivity.this,
                                    "请输入正确的手机号码！");
                        }
                        else if(password.length()<6){
                            DialogUtils.showAlertDialog(RegisterActivity.this,
                                    "请至少输入6位密码！");
                        }
                    }


                }else {
                    if (TextUtils.isEmpty(mobile_phone)) {
                        DialogUtils.showAlertDialog(RegisterActivity.this,
                                "手机号码不能为空！");
                    } else if (TextUtils.isEmpty(password)) {
                        DialogUtils.showAlertDialog(RegisterActivity.this,
                                "密码不能为空！");
                    }
                }
                break;
            }
        }
    }

    public void runAsyncTask() {
        loadingDialog.show();
        SendVerificationCodeProtocol sendVerificationCodeProtocol = new SendVerificationCodeProtocol();
        SendVerificationCodeRequest sendVerificationCodeRequest = new SendVerificationCodeRequest();
        url = sendVerificationCodeProtocol.getApiFun();
        sendVerificationCodeRequest.map.put("mobile_phone", mobile_phone);
        sendVerificationCodeRequest.map.put("password", password);
        sendVerificationCodeRequest.map.put("verification_type", "0");
        MyVolley.uploadNoFile(MyVolley.POST, url, sendVerificationCodeRequest.map, new MyVolley.VolleyCallback() {
            @Override
            public void dealWithJson(String address, String json) {

                Gson gson = new Gson();
                sendVerificationCodeResponse = gson.fromJson(json, SendVerificationCodeResponse.class);
                LogUtils.e("sendVerificationCodeResponse:"+json.toString());
                if (sendVerificationCodeResponse.code.equals("0")) {
                    loadingDialog.dismiss();
                    setFinish();
                    Intent intent=new Intent(RegisterActivity.this,InputGodeActivity.class);
                    intent.putExtra("verification_type",0);
                    intent.putExtra("mobile_phone",mobile_phone);
                    intent.putExtra("isThridLogin", isThridLogin);
                    intent.putExtra("password", password);
                    intent.putExtra("platform",platform);
                    intent.putExtra("openId",openId);
                    intent.putExtra("alias",alias);
                    intent.putExtra("avatar",avatar);
                    UIUtils.startActivityNextAnim(intent);
                } else {
                    loadingDialog.dismiss();
                    DialogUtils.showAlertDialog(RegisterActivity.this,
                            sendVerificationCodeResponse.resultText);
                }


            }

            @Override
            public void dealWithError(String address, String error) {
                loadingDialog.dismiss();
                DialogUtils.showAlertDialog(RegisterActivity.this, error);
            }
        });
    }
}
