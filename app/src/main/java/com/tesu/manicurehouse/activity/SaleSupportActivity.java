package com.tesu.manicurehouse.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.tesu.manicurehouse.R;
import com.tesu.manicurehouse.base.MyVolley;
import com.tesu.manicurehouse.huanxin.HuanXinLoginActivity;
import com.tesu.manicurehouse.response.GetSaleSupportResponse;
import com.tesu.manicurehouse.support.PercentLinearLayout;
import com.tesu.manicurehouse.support.PercentRelativeLayout;
import com.tesu.manicurehouse.util.DialogUtils;
import com.tesu.manicurehouse.util.LogUtils;
import com.tesu.manicurehouse.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class SaleSupportActivity extends Activity {
    private WebView wv_support;
    private Dialog loadingDialog;
    private String url;
    private GetSaleSupportResponse getSaleSupportResponse;
    private Gson gson;
    //css显示图片样式
    private String CSS_STYPE = "<head><style>img{max-width:340px !important;}</style></head>";
    private PercentLinearLayout ll_online_customer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale_support);

        wv_support = (WebView) findViewById(R.id.wv_support);
        ll_online_customer= (PercentLinearLayout) findViewById(R.id.ll_online_customer);

        loadingDialog = DialogUtils.createLoadDialog(SaleSupportActivity.this, true);
        gson = new Gson();

        getSupportMessage();

        ll_online_customer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SaleSupportActivity.this, HuanXinLoginActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * 获取售后反馈
     */
    private void getSupportMessage() {
        loadingDialog.show();
        url = "app/getSaleSupport.do";
        Map<String, String> params = new HashMap<String, String>();
        MyVolley.uploadNoFile(MyVolley.POST, url, params, new MyVolley.VolleyCallback() {

            @Override
            public void dealWithJson(String address, String json) {
                loadingDialog.dismiss();
                LogUtils.e("获取售后服务:" + json);
                getSaleSupportResponse = gson.fromJson(json,GetSaleSupportResponse.class);
                if(getSaleSupportResponse.getCode()==0){
                    String htmlStr = getSaleSupportResponse.getContent();
                    LogUtils.e("htmlStr" + htmlStr);
                    if(!TextUtils.isEmpty(htmlStr)){
                        wv_support.loadDataWithBaseURL(null, CSS_STYPE + htmlStr, "text/html", "utf-8", null);
                    }
                }else {
                    DialogUtils.showAlertDialog(SaleSupportActivity.this,getSaleSupportResponse.getResultText());
                }

            }

            @Override
            public void dealWithError(String address, String error) {
                loadingDialog.dismiss();
                LogUtils.e("获取售后服务错误:" + error);
                DialogUtils.showAlertDialog(SaleSupportActivity.this, error);
            }
        });

        }
    }
