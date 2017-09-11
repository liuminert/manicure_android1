package com.tesu.manicurehouse.activity;

import android.app.Dialog;
import android.content.Intent;
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
import com.tesu.manicurehouse.protocol.UpdateOrderStatusProtocol;
import com.tesu.manicurehouse.request.GetOrderListRequest;
import com.tesu.manicurehouse.request.UpdateOrderStatusRequest;
import com.tesu.manicurehouse.response.GetOrderListResponse;
import com.tesu.manicurehouse.response.UpdateOrderStatusResponse;
import com.tesu.manicurehouse.util.DateUtils;
import com.tesu.manicurehouse.util.DialogUtils;
import com.tesu.manicurehouse.util.LogUtils;
import com.tesu.manicurehouse.util.PictureOption;
import com.tesu.manicurehouse.util.SharedPrefrenceUtils;
import com.tesu.manicurehouse.util.UIUtils;
import com.tesu.manicurehouse.widget.MyListView;

import java.util.ArrayList;
import java.util.List;

/**
 * 类描述：
 * 创建人：许明达
 * 创建时间：2016/8/16 11:40
 * 待付款页面
 */
public class OrderForThePaymentActivity extends BaseActivity implements View.OnClickListener {

    private ImageView iv_top_back;
    private View rootView;
    private String url;
    private Dialog loadingDialog;
    private OrderBean orderBean;
    private TextView tv_order_num;
    private TextView tv_add_time;
    private TextView tv_user_name;
    private TextView tv_user_tell;
    private TextView tv_address;
    private TextView tv_good_total_price;
    private TextView tv_total_price;
    private TextView tv_courier;
    private TextView tv_shopping_fee;
    private Button btn_fast_payment;
    private Button btn_cancle_order;
    private MyListView  lv_order_good;
    private TheOrderGoodsAdapter theOrderGoodsAdapter;
    //总可以抵扣的积分
    private int total_integral = 0;
    @Override
    protected View initView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        rootView = View.inflate(this, R.layout.activity_order_for_the_payment, null);
        setContentView(rootView);
        iv_top_back = (ImageView) rootView.findViewById(R.id.iv_top_back);
        tv_order_num = (TextView) rootView.findViewById(R.id.tv_order_num);
        tv_add_time = (TextView) rootView.findViewById(R.id.tv_add_time);
        tv_user_name= (TextView) rootView.findViewById(R.id.tv_user_name);
        tv_courier = (TextView) rootView.findViewById(R.id.tv_courier);
        tv_shopping_fee= (TextView) rootView.findViewById(R.id.tv_shopping_fee);
        tv_user_tell= (TextView) rootView.findViewById(R.id.tv_user_tell);
        tv_total_price= (TextView) rootView.findViewById(R.id.tv_total_price);
        tv_good_total_price= (TextView) rootView.findViewById(R.id.tv_good_total_price);
        tv_address= (TextView) rootView.findViewById(R.id.tv_address);
        btn_fast_payment=(Button)rootView.findViewById(R.id.btn_fast_payment);
        lv_order_good=(MyListView)rootView.findViewById(R.id.lv_order_good);
        btn_cancle_order=(Button)rootView.findViewById(R.id.btn_cancle_order);
        iv_top_back.setOnClickListener(this);
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
        loadingDialog = DialogUtils.createLoadDialog(OrderForThePaymentActivity.this, true);
        if(orderBean!=null){
            setDate(orderBean);
        }
        btn_fast_payment.setOnClickListener(this);
        btn_cancle_order.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_fast_payment:
                Intent intent=new Intent(OrderForThePaymentActivity.this,PayOrderActivity.class);
                intent.putExtra("order_id",orderBean.getOrder_id());
                intent.putExtra("total_integral", total_integral);
                intent.putExtra("total_price", (Double.parseDouble(orderBean.getGoods_amount())+Double.parseDouble(orderBean.getShipping_fee())));
                intent.putExtra("total_fee",String.valueOf(Double.parseDouble(orderBean.getGoods_amount()) + Double.parseDouble(orderBean.getShipping_fee())));
                intent.putExtra("out_trade_no", orderBean.getOrder_sn());
                UIUtils.startActivityNextAnim(intent);
                break;
            case R.id.btn_cancle_order:
                runUpdateOrderStatus();
                break;
            case R.id.iv_top_back:
                finish();
                overridePendingTransition(R.anim.animprv_in, R.anim.animprv_out);
                break;
        }
    }

    public void setDate(OrderBean orderBean) {

        tv_order_num.setText("订单编号:" + orderBean.getOrder_sn());
        tv_add_time.setText(DateUtils.milliToSimpleDateTime(Long.parseLong(orderBean.getAdd_time()) * 1000));
        tv_user_name.setText(orderBean.getConsignee());
        tv_user_tell.setText(orderBean.getTel());
        tv_address.setText(orderBean.getAddress());
        tv_shopping_fee.setText("￥" +orderBean.getShipping_fee());
        tv_courier.setText(orderBean.getShipping_name());
        tv_good_total_price.setText("￥" +orderBean.getGoods_amount());
        tv_total_price.setText("￥" +orderBean.getOrder_amount());
        for (int i = 0; i < orderBean.getGoodsDataList().size(); i++) {
            LogUtils.e("订单total_integral"+orderBean.getGoodsDataList().get(i).getIntegral()+"   "+orderBean.getGoodsDataList().get(i).getGoods_number());
            if(orderBean.getGoodsDataList().get(i).getIntegral()==-1){
                total_integral=(int) Double.parseDouble(orderBean.getGoodsDataList().get(i).getShop_price())*10*Integer.parseInt(orderBean.getGoodsDataList().get(i).getGoods_number())+total_integral;
                LogUtils.e("有负1："+total_integral);
            }
            else{
                total_integral= orderBean.getGoodsDataList().get(i).getIntegral()*Integer.parseInt(orderBean.getGoodsDataList().get(i).getGoods_number())+total_integral;
            }
        }
        if(theOrderGoodsAdapter==null){
            theOrderGoodsAdapter=new TheOrderGoodsAdapter(OrderForThePaymentActivity.this,orderBean.getGoodsDataList());
        }
        lv_order_good.setAdapter(theOrderGoodsAdapter);
    }

    public void runUpdateOrderStatus() {
        loadingDialog.show();
        UpdateOrderStatusProtocol updateOrderStatusProtocol = new UpdateOrderStatusProtocol();
        UpdateOrderStatusRequest updateOrderStatusRequest = new UpdateOrderStatusRequest();
        url = updateOrderStatusProtocol.getApiFun();
        updateOrderStatusRequest.map.put("user_id", SharedPrefrenceUtils.getString(OrderForThePaymentActivity.this, "user_id"));
        updateOrderStatusRequest.map.put("order_status", "2");
        updateOrderStatusRequest.map.put("order_id", orderBean.getOrder_id());

        MyVolley.uploadNoFile(MyVolley.POST, url, updateOrderStatusRequest.map, new MyVolley.VolleyCallback() {
            @Override
            public void dealWithJson(String address, String json) {

                Gson gson = new Gson();
                UpdateOrderStatusResponse updateOrderStatusResponse = gson.fromJson(json, UpdateOrderStatusResponse.class);
                LogUtils.e("sendVerificationCodeResponse:" + json.toString());
                if (updateOrderStatusResponse.code == 0) {
                    loadingDialog.dismiss();
                    Intent intent=getIntent();
                    intent.putExtra("status","1");
                    setResult(2,intent);
                    finish();
                    overridePendingTransition(R.anim.animprv_in, R.anim.animprv_out);
                } else {
                    loadingDialog.dismiss();
                    DialogUtils.showAlertDialog(OrderForThePaymentActivity.this,
                            updateOrderStatusResponse.resultText);
                }


            }

            @Override
            public void dealWithError(String address, String error) {
                loadingDialog.dismiss();
                DialogUtils.showAlertDialog(OrderForThePaymentActivity.this, error);
            }
        });
    }
}
