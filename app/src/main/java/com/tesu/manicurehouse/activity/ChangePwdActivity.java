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
import com.tesu.manicurehouse.protocol.UpdatePwdProtocol;
import com.tesu.manicurehouse.request.UpdatePwdRequest;
import com.tesu.manicurehouse.response.SendVerificationCodeResponse;
import com.tesu.manicurehouse.response.UpdatePwdResponse;
import com.tesu.manicurehouse.util.DialogUtils;
import com.tesu.manicurehouse.util.LogUtils;
import com.tesu.manicurehouse.util.SharedPrefrenceUtils;
import com.tesu.manicurehouse.util.UIUtils;

/**
 * 类描述：
 * 创建人：许明达
 * 创建时间：2016/8/30 10:40
 * 更换密码页面
 */
public class ChangePwdActivity extends BaseActivity implements View.OnClickListener {

    private ImageView iv_top_back;
    private View rootView;
    private Button btn_comit;
    private String url;
    private Dialog loadingDialog;
    private UpdatePwdResponse updatePwdResponse;
    private EditText et_olde_pwd;
    private EditText et_pwd;
    private EditText et_ensure_pwd;
    private String old_password;
    private String new_password;
    private String ensure_password;
    @Override
    protected View initView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        rootView = View.inflate(this, R.layout.activity_change_pwd, null);
        setContentView(rootView);
        iv_top_back = (ImageView) rootView.findViewById(R.id.iv_top_back);
        btn_comit=(Button)rootView.findViewById(R.id.btn_comit);
        et_olde_pwd=(EditText)rootView.findViewById(R.id.et_olde_pwd);
        et_pwd=(EditText)rootView.findViewById(R.id.et_pwd);
        et_ensure_pwd=(EditText)rootView.findViewById(R.id.et_ensure_pwd);
        initData();
        return null;
    }


    public void initData() {
        loadingDialog = DialogUtils.createLoadDialog(ChangePwdActivity.this, false);
        btn_comit.setOnClickListener(this);
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
            case R.id.btn_comit: {
                new_password=et_pwd.getText().toString().trim();
                old_password=et_olde_pwd.getText().toString().trim();
                ensure_password=et_ensure_pwd.getText().toString().trim();
                if(!TextUtils.isEmpty(new_password)&&!TextUtils.isEmpty(old_password)&&!TextUtils.isEmpty(ensure_password)){
                    if(new_password.equals(ensure_password)&&new_password.length()>5&&old_password.length()>5){
                        runAsyncTask();
                    }
                    else{
                        if(!new_password.equals(ensure_password)){
                            DialogUtils.showAlertDialog(ChangePwdActivity.this,
                                    "请输入一样的密码！");
                        }
                        else if(new_password.length()<6||ensure_password.length()<6||old_password.length()<6){
                            DialogUtils.showAlertDialog(ChangePwdActivity.this,
                                    "请至少输入6位数密码！");
                        }
                    }
                }
                else{
                    if(TextUtils.isEmpty(new_password)){
                        DialogUtils.showAlertDialog(ChangePwdActivity.this,
                                "新密码不能为空！");
                    }else if(TextUtils.isEmpty(ensure_password)){
                        DialogUtils.showAlertDialog(ChangePwdActivity.this,
                                "重复密码不能为空！");
                    }
                    else{
                        DialogUtils.showAlertDialog(ChangePwdActivity.this,
                                "旧密码不能为空！");
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
        updatePwdRequest.map.put("type", "0");
        updatePwdRequest.map.put("new_password",new_password);
        updatePwdRequest.map.put("old_password",old_password);
        updatePwdRequest.map.put("user_id",SharedPrefrenceUtils.getString(ChangePwdActivity.this,"user_id"));


        MyVolley.uploadNoFile(MyVolley.POST, url, updatePwdRequest.map, new MyVolley.VolleyCallback() {
            @Override
            public void dealWithJson(String address, String json) {

                Gson gson = new Gson();
                updatePwdResponse = gson.fromJson(json, UpdatePwdResponse.class);
                LogUtils.e("sendVerificationCodeResponse:" + json.toString());
                if (updatePwdResponse.code.equals("0")) {
                    loadingDialog.dismiss();
                    SharedPrefrenceUtils.setBoolean(ChangePwdActivity.this,
                            "isLogin", false);
                    Intent intent=new Intent(ChangePwdActivity.this,LoginActivity.class);
                    UIUtils.startActivityPreAnim(intent);
                    finish();
                    finishActivity();
                } else {
                    loadingDialog.dismiss();
                    DialogUtils.showAlertDialog(ChangePwdActivity.this,
                            updatePwdResponse.resultText);
                }


            }

            @Override
            public void dealWithError(String address, String error) {
                loadingDialog.dismiss();
                DialogUtils.showAlertDialog(ChangePwdActivity.this, error);
            }
        });
    }
}
