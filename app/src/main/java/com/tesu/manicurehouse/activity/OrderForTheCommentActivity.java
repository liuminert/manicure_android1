package com.tesu.manicurehouse.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.tesu.manicurehouse.R;
import com.tesu.manicurehouse.adapter.OrderGoodsAdapter;
import com.tesu.manicurehouse.adapter.TheOrderGoodsAdapter;
import com.tesu.manicurehouse.base.BaseActivity;
import com.tesu.manicurehouse.base.MyVolley;
import com.tesu.manicurehouse.bean.OrderBean;
import com.tesu.manicurehouse.protocol.GetOrderListProtocol;
import com.tesu.manicurehouse.request.GetOrderListRequest;
import com.tesu.manicurehouse.response.GetOrderListResponse;
import com.tesu.manicurehouse.util.DateUtils;
import com.tesu.manicurehouse.util.DialogUtils;
import com.tesu.manicurehouse.util.LogUtils;
import com.tesu.manicurehouse.util.PictureOption;
import com.tesu.manicurehouse.util.SharedPrefrenceUtils;
import com.tesu.manicurehouse.util.UIUtils;
import com.tesu.manicurehouse.widget.MyListView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 类描述：
 * 创建人：许明达
 * 创建时间：2016/8/16 11:40
 * 待评论页面
 */
public class OrderForTheCommentActivity extends BaseActivity implements View.OnClickListener {

    private ImageView iv_top_back;
    private View rootView;
    private Button btn_goto_comment;
    private OrderBean orderBean;
    private TextView tv_order_num;
    private TextView tv_add_time;
    private TextView tv_user_name;
    private TextView tv_user_tell;
    private TextView tv_address;
    private TextView tv_good_total_price;
    private TextView tv_total_price;
    private TheOrderGoodsAdapter theOrderGoodsAdapter;
    private MyListView lv_order_good;
    private TextView tv_shipping_name;
    private TextView tv_shopping_fee;
    @Override
    protected View initView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        rootView = View.inflate(this, R.layout.activity_order_for_comment, null);
        setContentView(rootView);
        lv_order_good=(MyListView)rootView.findViewById(R.id.lv_order_good);
        iv_top_back = (ImageView) rootView.findViewById(R.id.iv_top_back);
        btn_goto_comment=(Button)rootView.findViewById(R.id.btn_goto_comment);
        tv_order_num = (TextView) rootView.findViewById(R.id.tv_order_num);
        tv_add_time = (TextView) rootView.findViewById(R.id.tv_add_time);
        tv_user_name= (TextView) rootView.findViewById(R.id.tv_user_name);
        tv_user_tell= (TextView) rootView.findViewById(R.id.tv_user_tell);
        tv_total_price= (TextView) rootView.findViewById(R.id.tv_total_price);
        tv_shipping_name= (TextView) rootView.findViewById(R.id.tv_shipping_name);
        tv_good_total_price= (TextView) rootView.findViewById(R.id.tv_good_total_price);
        tv_shopping_fee= (TextView) rootView.findViewById(R.id.tv_shopping_fee);
        tv_address= (TextView) rootView.findViewById(R.id.tv_address);

        initData();
        return null;
    }


    public void initData() {
        if (!ImageLoader.getInstance().isInited()) {
            ImageLoader.getInstance()
                    .init(ImageLoaderConfiguration.createDefault(UIUtils
                            .getContext()));
        }
        orderBean = (OrderBean) getIntent().getSerializableExtra("OrderBean");
        if(orderBean!=null){
            setDate(orderBean);
        }
        btn_goto_comment.setOnClickListener(this);
        iv_top_back.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_goto_comment:
                setFinish();
                Intent intent = new Intent(OrderForTheCommentActivity.this, SubmitCommentActivity.class);
                intent.putExtra("order_id", orderBean.getOrder_id());
                Bundle bundle = new Bundle();
                bundle.putSerializable("GoodsDataBean", (Serializable) orderBean.getGoodsDataList());
                intent.putExtras(bundle);
                UIUtils.startActivityNextAnim(intent);
                break;
            case R.id.iv_top_back:
                finish();
                overridePendingTransition(R.anim.animprv_in, R.anim.animprv_out);
                break;
        }
    }

    public void setDate(OrderBean orderBean) {
        tv_shipping_name.setText(orderBean.getShipping_name());
        tv_order_num.setText("订单编号:" + orderBean.getOrder_sn());
        tv_add_time.setText(DateUtils.milliToSimpleDateTime(Long.parseLong(orderBean.getAdd_time()) * 1000));
        tv_user_name.setText(orderBean.getConsignee());
        tv_user_tell.setText(orderBean.getTel());
        tv_address.setText(orderBean.getAddress());
        tv_shopping_fee.setText("￥" + orderBean.getShipping_fee());
        tv_good_total_price.setText("￥" + orderBean.getGoods_amount());
        tv_total_price.setText("￥" + orderBean.getOrder_amount());
        if(theOrderGoodsAdapter==null){
            theOrderGoodsAdapter=new TheOrderGoodsAdapter(OrderForTheCommentActivity.this,orderBean.getGoodsDataList());
        }
        lv_order_good.setAdapter(theOrderGoodsAdapter);
    }
}
