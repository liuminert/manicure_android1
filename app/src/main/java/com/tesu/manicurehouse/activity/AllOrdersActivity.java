package com.tesu.manicurehouse.activity;

import android.app.Dialog;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.tesu.manicurehouse.R;
import com.tesu.manicurehouse.adapter.OrderGoodsAdapter;
import com.tesu.manicurehouse.base.BaseActivity;
import com.tesu.manicurehouse.base.MyVolley;
import com.tesu.manicurehouse.bean.OrderBean;
import com.tesu.manicurehouse.protocol.GetOrderListProtocol;
import com.tesu.manicurehouse.protocol.GetUserAddressListProtocol;
import com.tesu.manicurehouse.request.GetOrderListRequest;
import com.tesu.manicurehouse.request.GetUserAddressListRequest;
import com.tesu.manicurehouse.response.GetOrderListResponse;
import com.tesu.manicurehouse.response.GetUserAddressListResponse;
import com.tesu.manicurehouse.util.DialogUtils;
import com.tesu.manicurehouse.util.LogUtils;
import com.tesu.manicurehouse.util.SharedPrefrenceUtils;
import com.tesu.manicurehouse.util.UIUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 类描述：
 * 创建人：许明达
 * 创建时间：2016/8/16 11:40
 * 全部订单页面
 */
public class AllOrdersActivity extends BaseActivity implements View.OnClickListener {

    private TextView tv_top_back;
    private View rootView;
    private ListView lv_order;
    private OrderGoodsAdapter orderGoodsAdapter;
    @ViewInject(R.id.rg_content)
    private RadioGroup mRadioGroup;

    private RadioButton rb_all;
    private RadioButton rb_for_the_payment;
    private RadioButton rb_for_the_delivery;
    private RadioButton rb_for_the_good;
    private RadioButton rb_for_the_comment;

    private String url;
    private Dialog loadingDialog;
    private GetOrderListResponse getOrderListResponse;
    //订单状态0：全部，1：待付款，2：待发货，3：待收货，4：待评价
    private String status = "0";


    @Override
    protected View initView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        rootView = View.inflate(this, R.layout.activity_all_orders, null);
        setContentView(rootView);
        rb_all = (RadioButton) rootView.findViewById(R.id.rb_all);
        rb_for_the_payment = (RadioButton) rootView.findViewById(R.id.rb_for_the_payment);
        rb_for_the_delivery = (RadioButton) rootView.findViewById(R.id.rb_for_the_delivery);
        rb_for_the_good = (RadioButton) rootView.findViewById(R.id.rb_for_the_good);
        rb_for_the_comment = (RadioButton) rootView.findViewById(R.id.rb_for_the_comment);

        tv_top_back = (TextView) rootView.findViewById(R.id.tv_top_back);
        lv_order = (ListView) rootView.findViewById(R.id.lv_order);
        tv_top_back.setOnClickListener(this);
        initData();
        return null;
    }


    public void initData() {
        Intent intent=getIntent();
        status=intent.getStringExtra("status");
        loadingDialog = DialogUtils.createLoadDialog(AllOrdersActivity.this, true);
        rb_all.setOnClickListener(this);
        rb_for_the_payment.setOnClickListener(this);
        rb_for_the_comment.setOnClickListener(this);
        rb_for_the_delivery.setOnClickListener(this);
        rb_for_the_good.setOnClickListener(this);
        if(!TextUtils.isEmpty(status)){
            switch (Integer.parseInt(status)){
                case 0:
                    rb_all.setChecked(true);
                    break;
                case 1:
                    rb_for_the_payment.setChecked(true);
                    break;
                case 2:
                    rb_for_the_delivery.setChecked(true);
                    break;
                case 3:
                    rb_for_the_good.setChecked(true);
                    break;
                case 4:
                    rb_for_the_comment.setChecked(true);
                    break;
            }
        }
        runGetOrderList();
    }

    public void setDate(List<OrderBean> dataLists) {
        if (orderGoodsAdapter == null) {
            orderGoodsAdapter = new OrderGoodsAdapter(AllOrdersActivity.this, dataLists, 0);
            lv_order.setAdapter(orderGoodsAdapter);
        }
        else{
            orderGoodsAdapter.setDataLists(dataLists);
            orderGoodsAdapter.notifyDataSetChanged();
        }
        setFinish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_top_back:
                finish();
                overridePendingTransition(R.anim.animprv_in, R.anim.animprv_out);
                break;
            case R.id.rb_all:
                status = "0";
                runGetOrderList();
                break;
            case R.id.rb_for_the_payment:
                status = "1";
                runGetOrderList();
                break;
            case R.id.rb_for_the_comment:
                status = "4";
                runGetOrderList();
                break;
            case R.id.rb_for_the_delivery:
                status = "2";
                runGetOrderList();
                break;
            case R.id.rb_for_the_good:
                status = "3";
                runGetOrderList();
                break;
        }
    }

    public void runGetOrderList() {
        loadingDialog.show();
        GetOrderListProtocol getOrderListProtocol = new GetOrderListProtocol();
        GetOrderListRequest getOrderListRequest = new GetOrderListRequest();
        url = getOrderListProtocol.getApiFun();
        getOrderListRequest.map.put("user_id", SharedPrefrenceUtils.getString(AllOrdersActivity.this, "user_id"));
        getOrderListRequest.map.put("status", status);
        getOrderListRequest.map.put("pageNo", "1");
        getOrderListRequest.map.put("pageSize", "100");


        MyVolley.uploadNoFile(MyVolley.POST, url, getOrderListRequest.map, new MyVolley.VolleyCallback() {
            @Override
            public void dealWithJson(String address, String json) {

                Gson gson = new Gson();
                getOrderListResponse = gson.fromJson(json, GetOrderListResponse.class);
                LogUtils.e("sendVerificationCodeResponse:" + json.toString());
                if (getOrderListResponse.code.equals("0")) {
                    loadingDialog.dismiss();
                    setDate(getOrderListResponse.dataList);
                } else {
                    loadingDialog.dismiss();
                    DialogUtils.showAlertDialog(AllOrdersActivity.this,
                            getOrderListResponse.resultText);
                }


            }

            @Override
            public void dealWithError(String address, String error) {
                loadingDialog.dismiss();
                DialogUtils.showAlertDialog(AllOrdersActivity.this, error);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1){
            if(resultCode==2){
                status=data.getStringExtra("status");
                runGetOrderList();
            }
        }
        else if(requestCode==2){
            if(resultCode==2){
                status=data.getStringExtra("status");
                runGetOrderList();
            }
        }

    }
}
