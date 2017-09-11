package com.tesu.manicurehouse.activity;

import android.content.Intent;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.tesu.manicurehouse.R;
import com.tesu.manicurehouse.base.BaseActivity;
import com.tesu.manicurehouse.util.UIUtils;

/**
 * 类描述：
 * 创建人：许明达
 * 创建时间：2016/8/16 11:40
 * 支付成功
 */
public class PaySuccessActivity extends BaseActivity implements View.OnClickListener{

    private ImageView iv_top_back;
    private View rootView;
    private TextView tv_goto_order;
    @Override
    protected View initView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        rootView = View.inflate(this, R.layout.activity_pay_success, null);
        setContentView(rootView);
        iv_top_back=(ImageView)rootView.findViewById(R.id.iv_top_back);
        tv_goto_order=(TextView)rootView.findViewById(R.id.tv_goto_order);
        initData();
        return null;
    }



    public void initData(){
        iv_top_back.setOnClickListener(this);
        tv_goto_order.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_goto_order:
                Intent intent=new Intent(PaySuccessActivity.this,AllOrdersActivity.class);
                intent.putExtra("status","0");
                UIUtils.startActivityNextAnim(intent);
                finishActivity();
                finish();
                break;
            case R.id.iv_top_back:
                finishActivity();
                finish();
                overridePendingTransition(R.anim.animprv_in, R.anim.animprv_out);
                break;
        }
    }
}
