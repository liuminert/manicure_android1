package com.tesu.manicurehouse.activity;

import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.tesu.manicurehouse.R;
import com.tesu.manicurehouse.adapter.ProblemAdapter;
import com.tesu.manicurehouse.base.BaseActivity;
import com.tesu.manicurehouse.util.StartCustomTextView;
import com.tesu.manicurehouse.util.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * 类描述：
 * 创建人：许明达
 * 创建时间：2016/8/16 11:40
 * 关于我们页面
 */
public class AboutActivity extends BaseActivity implements View.OnClickListener {

    private TextView tv_top_back;
    private View rootView;
    private TextView tv_version;
    private StartCustomTextView tv_content;

    @Override
    protected View initView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        rootView = View.inflate(this, R.layout.activity_about, null);
        setContentView(rootView);
        tv_top_back = (TextView) rootView.findViewById(R.id.tv_top_back);
        tv_version = (TextView) rootView.findViewById(R.id.tv_version);
        tv_content=(StartCustomTextView)rootView.findViewById(R.id.tv_content);
        initData();
        return null;
    }


    public void initData() {
        tv_version.setText("版本 v "+ Utils.getVersionName(this));
        tv_top_back.setOnClickListener(this);
        tv_content.setMText("\u3000\u3000河源市密特欧网络科技有限公司成立于2013年，致力于网络技术开发；软件开发和销售；胶水、美甲产品、化妆品销售（含互联网销售）。我们以最真挚的心，推出让用户满意的产品，做出让用户舒心的服务，打造更好的用户体验。");
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
