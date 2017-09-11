package com.tesu.manicurehouse.activity;

import android.app.Dialog;
import android.content.Intent;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.tesu.manicurehouse.R;
import com.tesu.manicurehouse.adapter.RecommendAdapter;
import com.tesu.manicurehouse.adapter.ShopAdapter;
import com.tesu.manicurehouse.base.BaseActivity;
import com.tesu.manicurehouse.base.MyVolley;
import com.tesu.manicurehouse.bean.GoodsAttrBean;
import com.tesu.manicurehouse.bean.RecommendDescBean;
import com.tesu.manicurehouse.protocol.GetRecommendDescListProtocol;
import com.tesu.manicurehouse.request.GetRecommendDescListRequest;
import com.tesu.manicurehouse.response.GetFollowInfoResponse;
import com.tesu.manicurehouse.response.GetRecommendDescListResponse;
import com.tesu.manicurehouse.util.DialogUtils;
import com.tesu.manicurehouse.util.LogUtils;
import com.tesu.manicurehouse.util.SharedPrefrenceUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 类描述：
 * 创建人：许明达
 * 创建时间：2016/8/16 11:40
 * 店主详情页面
 */
public class ShopOwnerInfoActivity extends BaseActivity implements View.OnClickListener {
    private TextView tv_top_back;
    private View rootView;
    private PullToRefreshListView lv_recommend;
    private RecommendAdapter recommendAdapter;
    private List<RecommendDescBean> recommendDescBeanList;
    private TextView tv_owner_num;
    private TextView tv2;
    //积分
    private int recommendScore;
    //好友数
    private int recommendCnt;
    @Override
    protected View initView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        rootView = View.inflate(this, R.layout.activity_shop_owner_info, null);
        setContentView(rootView);
        tv_top_back = (TextView) rootView.findViewById(R.id.tv_top_back);
        lv_recommend= (PullToRefreshListView) rootView.findViewById(R.id.lv_recommend);
        tv_owner_num = (TextView) rootView.findViewById(R.id.tv_owner_num);
        tv2 = (TextView) rootView.findViewById(R.id.tv2);
        initData();
        return null;
    }


    public void initData() {
        Intent intent = getIntent();
        recommendDescBeanList = (List<RecommendDescBean>) intent.getSerializableExtra("recommendDescBeanList");
        recommendScore = intent.getIntExtra("recommendScore", 0);
        recommendCnt = intent.getIntExtra("recommendCnt", 0);
        tv_top_back.setOnClickListener(this);
        if (recommendAdapter == null) {
            recommendAdapter = new RecommendAdapter(ShopOwnerInfoActivity.this, recommendDescBeanList);
        }
        lv_recommend.setAdapter(recommendAdapter);
        tv_owner_num.setText(recommendCnt + "位");
        tv2.setText("共获得" + recommendScore + "积分");
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_top_back:
                finish();
                overridePendingTransition(R.anim.animprv_in, R.anim.animprv_out);
                break;
        }
    }



}
