package com.tesu.manicurehouse.activity;

import android.view.View;
import android.view.Window;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tesu.manicurehouse.R;
import com.tesu.manicurehouse.adapter.VideoShowPhotoAdapter;
import com.tesu.manicurehouse.base.BaseActivity;
import com.tesu.manicurehouse.bean.ShowPhotoContentBean;
import com.tesu.manicurehouse.protocol.GetSucDealGoodListProtocol;
import com.tesu.manicurehouse.util.StringUtils;
import com.tesu.manicurehouse.widget.MyListView;

import java.util.List;

/**
 * 类描述：
 * 创建人：许明达
 * 创建时间：2016/8/16 11:40
 * 关于我们页面
 */
public class ShowLocalPhotoMessageActivity extends BaseActivity implements View.OnClickListener {

    private ListView lv_show_photo;
    private View rootView;
    private VideoShowPhotoAdapter videoShowPhotoAdapter;
    private List<ShowPhotoContentBean> showPhotoContentBeanList;
    private Gson gson;
    private TextView tv_top_back;

    @Override
    protected View initView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        rootView = View.inflate(this, R.layout.activity_show_local_message, null);
        setContentView(rootView);
        lv_show_photo = (ListView) rootView.findViewById(R.id.lv_show_photo);
        tv_top_back = (TextView) rootView.findViewById(R.id.tv_top_back);

        tv_top_back.setOnClickListener(this);
        initData();
        return null;
    }


    public void initData() {
        String pics = getIntent().getStringExtra("pics");
        gson = new Gson();
        if(!StringUtils.isEmpty(pics)){
            showPhotoContentBeanList = gson.fromJson(pics,new TypeToken<List<ShowPhotoContentBean>>(){}.getType());
            if(showPhotoContentBeanList != null){
                videoShowPhotoAdapter = new VideoShowPhotoAdapter(ShowLocalPhotoMessageActivity.this,showPhotoContentBeanList);
                lv_show_photo.setAdapter(videoShowPhotoAdapter);
            }
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
