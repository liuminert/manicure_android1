package com.tesu.manicurehouse.activity;

import android.content.Intent;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.tesu.manicurehouse.R;
import com.tesu.manicurehouse.base.BaseActivity;
import com.tesu.manicurehouse.util.PictureOption;
import com.tesu.manicurehouse.util.UIUtils;

/**
 * 类描述：
 * 创建人：许明达
 * 创建时间：2016/8/16 11:40
 * 别人的个人页面
 */
public class OthrersInfoActivity extends BaseActivity implements View.OnClickListener {

    private TextView tv_top_back;
    private View rootView;
    private int identity_type;
    private String avatar;
    private String alias;
    private String signature;
    private String city;
    private ImageView iv_image;
    private TextView tv_name;
    private TextView tv_membership_grade;
    private TextView tv_signature;
    private TextView tv_city;

    @Override
    protected View initView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        rootView = View.inflate(this, R.layout.others_personal_layout, null);
        setContentView(rootView);
        tv_top_back = (TextView) rootView.findViewById(R.id.tv_top_back);
        iv_image=(ImageView)rootView.findViewById(R.id.iv_image);
        tv_name = (TextView) rootView.findViewById(R.id.tv_name);
        tv_membership_grade = (TextView) rootView.findViewById(R.id.tv_membership_grade);
        tv_signature = (TextView) rootView.findViewById(R.id.tv_signature);
        tv_city = (TextView) rootView.findViewById(R.id.tv_city);
        initData();
        return null;
    }


    public void initData() {
        Intent intent = getIntent();
        identity_type = intent.getIntExtra("identity_type", 0);
        avatar = intent.getStringExtra("avatar");
        alias = intent.getStringExtra("alias");
        signature = intent.getStringExtra("signature");
        city = intent.getStringExtra("city");
        tv_top_back.setOnClickListener(this);
        if (!ImageLoader.getInstance().isInited()) {
            ImageLoader.getInstance()
                    .init(ImageLoaderConfiguration.createDefault(UIUtils
                            .getContext()));
        }
        ImageLoader.getInstance().displayImage(avatar, iv_image, PictureOption.getSimpleOptions());
        tv_city.setText(city);
        tv_signature.setText(signature);
        tv_name.setText(alias);
        if(identity_type==0){
            tv_membership_grade.setText("普通用户");
        }
        else if(identity_type==1){
            tv_membership_grade.setText("美甲师");
        }
        else if(identity_type==3){
            tv_membership_grade.setText("美甲店主");
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
