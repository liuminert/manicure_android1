package com.tesu.manicurehouse.activity;

import android.app.Dialog;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.tesu.manicurehouse.R;
import com.tesu.manicurehouse.base.BaseActivity;
import com.tesu.manicurehouse.base.MyVolley;
import com.tesu.manicurehouse.protocol.SendVerificationCodeProtocol;
import com.tesu.manicurehouse.protocol.UpdatePwdProtocol;
import com.tesu.manicurehouse.request.SendVerificationCodeRequest;
import com.tesu.manicurehouse.request.UpdatePwdRequest;
import com.tesu.manicurehouse.response.SendVerificationCodeResponse;
import com.tesu.manicurehouse.response.UpdatePwdResponse;
import com.tesu.manicurehouse.util.DialogUtils;
import com.tesu.manicurehouse.util.LogUtils;
import com.tesu.manicurehouse.util.UIUtils;

/**
 * 类描述：
 * 创建人：许明达
 * 创建时间：2016/8/30 10:40
 * 设置新密码页面
 */
public class SetNewPwdActivity extends BaseActivity implements View.OnClickListener {

    private ImageView iv_top_back;
    private View rootView;
    private Button btn_finish;
    private String url;
    private Dialog loadingDialog;
    private UpdatePwdResponse updatePwdResponse;
    private EditText et_new_pwd;
    private EditText et_repeat_pwd;
    private String mobile_phone;
    private String new_password;
    private String repeat_pwd;

    @Override
    protected View initView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        rootView = View.inflate(this, R.layout.activity_new_pwd, null);
        setContentView(rootView);
        iv_top_back = (ImageView) rootView.findViewById(R.id.iv_top_back);
        btn_finish=(Button)rootView.findViewById(R.id.btn_finish);
        et_new_pwd=(EditText)rootView.findViewById(R.id.et_new_pwd);
        et_repeat_pwd=(EditText)rootView.findViewById(R.id.et_repeat_pwd);
        initData();
        return null;
    }


    public void initData() {
        Intent intent=getIntent();
        mobile_phone=intent.getStringExtra("mobile_phone");
        loadingDialog = DialogUtils.createLoadDialog(SetNewPwdActivity.this, true);
        btn_finish.setOnClickListener(this);
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
            case R.id.btn_finish: {
                new_password=et_new_pwd.getText().toString().trim();
                repeat_pwd=et_repeat_pwd.getText().toString().trim();
                if(!TextUtils.isEmpty(new_password)&&!TextUtils.isEmpty(repeat_pwd)){
                    if(new_password.equals(repeat_pwd)&&new_password.length()>5){
                        runAsyncTask();
                    }
                    else{
                        if(!new_password.equals(repeat_pwd)){
                            DialogUtils.showAlertDialog(SetNewPwdActivity.this,
                                    "请输入一样的密码！");
                        }
                        else if(new_password.length()<6||repeat_pwd.length()<6){
                            DialogUtils.showAlertDialog(SetNewPwdActivity.this,
                                    "请至少输入6位数密码！");
                        }
                    }
                }
                else{
                    if(TextUtils.isEmpty(new_password)){
                        DialogUtils.showAlertDialog(SetNewPwdActivity.this,
                                "新密码不能为空！");
                    }else if(TextUtils.isEmpty(repeat_pwd)){
                            DialogUtils.showAlertDialog(SetNewPwdActivity.this,
                                    "重复密码不能为空！");
                    }
                }
                break;
            }
        }
    }
    public void runAsyncTask() {
        loadingDialog.show();
        UpdatePwdProtocol updatePwdProtocol = new UpdatePwdProtocol();
        UpdatePwdRequest updatePwdRequest = new UpdatePwdRequest();
        url = updatePwdProtocol.getApiFun();
        updatePwdRequest.map.put("type", "1");
        updatePwdRequest.map.put("mobile_phone",mobile_phone);
        updatePwdRequest.map.put("new_password",new_password);


        MyVolley.uploadNoFile(MyVolley.POST, url, updatePwdRequest.map, new MyVolley.VolleyCallback() {
            @Override
            public void dealWithJson(String address, String json) {

                Gson gson = new Gson();
                updatePwdResponse = gson.fromJson(json, UpdatePwdResponse.class);
                LogUtils.e("sendVerificationCodeResponse:" + json.toString());
                if (updatePwdResponse.code.equals("0")) {
                    loadingDialog.dismiss();
                    setFinish();
                    finishActivity();
                    overridePendingTransition(R.anim.animprv_in, R.anim.animprv_out);
                } else {
                    loadingDialog.dismiss();
                    DialogUtils.showAlertDialog(SetNewPwdActivity.this,
                            updatePwdResponse.resultText);
                }


            }

            @Override
            public void dealWithError(String address, String error) {
                loadingDialog.dismiss();
                DialogUtils.showAlertDialog(SetNewPwdActivity.this, error);
            }
        });
    }
}
