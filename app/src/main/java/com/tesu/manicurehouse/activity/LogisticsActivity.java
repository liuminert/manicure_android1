package com.tesu.manicurehouse.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

import com.tesu.manicurehouse.R;
import com.tesu.manicurehouse.base.BaseActivity;
import com.tesu.manicurehouse.util.Utils;

/**
 * 类描述：
 * 创建人：许明达
 * 创建时间：2016/8/16 11:40
 * 物流页面
 */
public class LogisticsActivity extends BaseActivity implements View.OnClickListener {

    private TextView tv_top_back;
    private View rootView;
    private WebView wv_logistics;
    //css显示图片样式
    private String CSS_STYPE = "<head><style>img{max-width:340px !important;}</style></head>";
    private String logistics;
    //0宅急送，1韵达
    private int type;
    private String title;
    private TextView tv_title;
    @Override
    protected View initView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        rootView = View.inflate(this, R.layout.activity_logistics, null);
        setContentView(rootView);
        tv_title= (TextView) rootView.findViewById(R.id.tv_title);
        tv_top_back = (TextView) rootView.findViewById(R.id.tv_top_back);
        wv_logistics = (WebView) rootView.findViewById(R.id.wv_logistics);
        initData();
        return null;
    }


    public void initData() {
        Intent intent=getIntent();
        logistics=intent.getStringExtra("logistics");
        title=intent.getStringExtra("title");
        type=intent.getIntExtra("type", -1);
        tv_top_back.setOnClickListener(this);
        WebSettings webSettings = wv_logistics.getSettings();
        //设置WebView属性，能够执行Javascript脚本
        webSettings.setJavaScriptEnabled(true);
        //设置可以访问文件
        webSettings.setAllowFileAccess(true);
        //设置支持缩放
        webSettings.setBuiltInZoomControls(true);
        if(!TextUtils.isEmpty(title)){
            tv_title.setText(title);
        }
        //加载需要显示的网页
        if(type==1) {
            wv_logistics.loadUrl(logistics);
        }
        else {
            wv_logistics.loadDataWithBaseURL(null, CSS_STYPE + logistics, "text/html", "utf-8", null);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_top_back: {
                finish();
                overridePendingTransition(R.anim.animprv_in, R.anim.animprv_out);
                break;
            }
        }
    }
}
