package com.tesu.manicurehouse.activity;

import android.content.Intent;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tesu.manicurehouse.R;
import com.tesu.manicurehouse.base.BaseActivity;
import com.tesu.manicurehouse.util.SharedPrefrenceUtils;
import com.tesu.manicurehouse.util.UIUtils;

/**
 * 类描述：
 * 创建人：许明达
 * 创建时间：2016/8/16 11:40
 * 账户安全页面
 */
public class AccountSafeActivity extends BaseActivity implements View.OnClickListener {

    private TextView tv_top_back;
    private View rootView;
    private RelativeLayout rl_change_tellphone;
    private RelativeLayout rl_change_pwd;
    private TextView tv_mobile;

    @Override
    protected View initView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        rootView = View.inflate(this, R.layout.activity_account_security, null);
        setContentView(rootView);
        tv_top_back = (TextView) rootView.findViewById(R.id.tv_top_back);
        rl_change_tellphone=(RelativeLayout)rootView.findViewById(R.id.rl_change_tellphone);
        rl_change_pwd=(RelativeLayout)rootView.findViewById(R.id.rl_change_pwd);
        tv_mobile=(TextView)rootView.findViewById(R.id.tv_mobile);
        initData();
        return null;
    }


    public void initData() {
        tv_top_back.setOnClickListener(this);
        rl_change_tellphone.setOnClickListener(this);
        rl_change_pwd.setOnClickListener(this);
        tv_mobile.setText(SharedPrefrenceUtils.getString(AccountSafeActivity.this,"mobile_phone"));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_change_pwd: {
                setFinish();
                Intent intent=new Intent(AccountSafeActivity.this,ChangePwdActivity.class);
                UIUtils.startActivityNextAnim(intent);
                break;
            }
            case R.id.rl_change_tellphone: {
                setFinish();
                Intent intent=new Intent(AccountSafeActivity.this,ChangeTellphoneActivity.class);
                UIUtils.startActivityNextAnim(intent);
                break;
            }
            case R.id.tv_top_back: {
                finish();
                overridePendingTransition(R.anim.animprv_in, R.anim.animprv_out);
                break;
            }
        }
    }
}
