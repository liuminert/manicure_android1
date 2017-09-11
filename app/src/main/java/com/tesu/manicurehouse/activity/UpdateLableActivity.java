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
import com.tesu.manicurehouse.protocol.UpdateUserInfoProtocol;
import com.tesu.manicurehouse.request.UpdateUserInfoRequest;
import com.tesu.manicurehouse.response.UpdateUserInfoResponse;
import com.tesu.manicurehouse.util.DialogUtils;
import com.tesu.manicurehouse.util.LogUtils;
import com.tesu.manicurehouse.util.SharedPrefrenceUtils;
import com.tesu.manicurehouse.util.UIUtils;

/**
 * 类描述：
 * 创建人：许明达
 * 创建时间：2016/8/30 10:40
 * 修改个性签名页面
 */
public class UpdateLableActivity extends BaseActivity implements View.OnClickListener {

    private ImageView iv_top_back;
    private View rootView;
    private Button btn_comit;
    private String url;
    private Dialog loadingDialog;
    private UpdateUserInfoResponse updateUserInfoResponse;
    private String signature;
    private EditText et_lable;

    @Override
    protected View initView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        rootView = View.inflate(this, R.layout.activity_update_lable, null);
        setContentView(rootView);
        iv_top_back = (ImageView) rootView.findViewById(R.id.iv_top_back);
        btn_comit=(Button)rootView.findViewById(R.id.btn_comit);
        et_lable=(EditText)rootView.findViewById(R.id.et_lable);
        initData();
        return null;
    }


    public void initData() {
        loadingDialog = DialogUtils.createLoadDialog(UpdateLableActivity.this, true);
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
                signature=et_lable.getText().toString().trim();
                if(!TextUtils.isEmpty(signature)){
                    runAsyncTask();
                }
                else{
                    DialogUtils.showAlertDialog(UpdateLableActivity.this, "个性签名不能为空！");
                }
                break;
            }
        }
    }
    public void runAsyncTask() {
        loadingDialog.show();
        UpdateUserInfoProtocol updateUserInfoProtocol = new UpdateUserInfoProtocol();
        UpdateUserInfoRequest updateUserInfoRequest = new UpdateUserInfoRequest();
        url = updateUserInfoProtocol.getApiFun();
        updateUserInfoRequest.map.put("signature",signature);
        updateUserInfoRequest.map.put("user_id",SharedPrefrenceUtils.getString(UpdateLableActivity.this,"user_id"));


        MyVolley.uploadNoFile(MyVolley.POST, url, updateUserInfoRequest.map, new MyVolley.VolleyCallback() {
            @Override
            public void dealWithJson(String address, String json) {

                Gson gson = new Gson();
                updateUserInfoResponse = gson.fromJson(json, UpdateUserInfoResponse.class);
                LogUtils.e("sendVerificationCodeResponse:" + json.toString());
                if (updateUserInfoResponse.code.equals("0")) {
                    loadingDialog.dismiss();
                    Intent intent = getIntent();
                    intent.putExtra("signature", signature);
                    setResult(1, intent);
                    finish();
                    overridePendingTransition(R.anim.animprv_in, R.anim.animprv_out);
                } else {
                    loadingDialog.dismiss();
                    DialogUtils.showAlertDialog(UpdateLableActivity.this,
                            updateUserInfoResponse.resultText);
                }


            }

            @Override
            public void dealWithError(String address, String error) {
                loadingDialog.dismiss();
                DialogUtils.showAlertDialog(UpdateLableActivity.this, error);
            }
        });
    }
}
