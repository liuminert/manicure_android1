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
import com.tesu.manicurehouse.request.SendVerificationCodeRequest;
import com.tesu.manicurehouse.response.SendVerificationCodeResponse;
import com.tesu.manicurehouse.util.DialogUtils;
import com.tesu.manicurehouse.util.LogUtils;
import com.tesu.manicurehouse.util.UIUtils;

/**
 * 类描述：
 * 创建人：许明达
 * 创建时间：2016/8/30 10:40
 * 注册页面
 */
public class FindPwdActivity extends BaseActivity implements View.OnClickListener {

    private ImageView iv_top_back;
    private View rootView;
    private Button btn_next;
    private String url;
    private Dialog loadingDialog;
    private SendVerificationCodeResponse sendVerificationCodeResponse;
    private String mobile_phone;
    private EditText et_phone;

    @Override
    protected View initView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        rootView = View.inflate(this, R.layout.activity_find_pwd, null);
        setContentView(rootView);
        et_phone=(EditText)rootView.findViewById(R.id.et_phone);
        iv_top_back = (ImageView) rootView.findViewById(R.id.iv_top_back);
        btn_next=(Button)rootView.findViewById(R.id.btn_next);
        initData();
        return null;
    }


    public void initData() {
        loadingDialog = DialogUtils.createLoadDialog(FindPwdActivity.this, true);
        btn_next.setOnClickListener(this);
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
            case R.id.btn_next: {
                mobile_phone=et_phone.getText().toString().trim();
                if(!TextUtils.isEmpty(mobile_phone)){
                    runAsyncTask();
                }else {
                    if (TextUtils.isEmpty(mobile_phone)) {
                        DialogUtils.showAlertDialog(FindPwdActivity.this,
                                "手机号码不能为空！");
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
        sendVerificationCodeRequest.map.put("verification_type", "1");
        MyVolley.uploadNoFile(MyVolley.POST, url, sendVerificationCodeRequest.map, new MyVolley.VolleyCallback() {
            @Override
            public void dealWithJson(String address, String json) {

                Gson gson = new Gson();
                sendVerificationCodeResponse = gson.fromJson(json, SendVerificationCodeResponse.class);
                LogUtils.e("sendVerificationCodeResponse:" + json.toString());
                if (sendVerificationCodeResponse.code.equals("0")) {
                    loadingDialog.dismiss();
                    setFinish();
                    Intent intent=new Intent(FindPwdActivity.this,InputGodeActivity.class);
                    intent.putExtra("verification_type",1);
                    intent.putExtra("mobile_phone",mobile_phone);
                    UIUtils.startActivityNextAnim(intent);
                } else {
                    loadingDialog.dismiss();
                    DialogUtils.showAlertDialog(FindPwdActivity.this,
                            sendVerificationCodeResponse.resultText);
                }


            }

            @Override
            public void dealWithError(String address, String error) {
                loadingDialog.dismiss();
                DialogUtils.showAlertDialog(FindPwdActivity.this, error);
            }
        });
    }
}
