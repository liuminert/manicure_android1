package com.tesu.manicurehouse.activity;

import android.app.Dialog;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.tesu.manicurehouse.R;
import com.tesu.manicurehouse.adapter.SelectGoodsAdapter;
import com.tesu.manicurehouse.adapter.SelectedGoodsAdapter;
import com.tesu.manicurehouse.base.BaseActivity;
import com.tesu.manicurehouse.base.MyVolley;
import com.tesu.manicurehouse.bean.AddressBean;
import com.tesu.manicurehouse.bean.GoodsAttrBean;
import com.tesu.manicurehouse.bean.OrderGoodBean;
import com.tesu.manicurehouse.protocol.CalculatePostageProtocol;
import com.tesu.manicurehouse.protocol.DealGoodProtocol;
import com.tesu.manicurehouse.protocol.GetUserAddressListProtocol;
import com.tesu.manicurehouse.protocol.GetUserInfomationProtocol;
import com.tesu.manicurehouse.request.CalculatePostageRequest;
import com.tesu.manicurehouse.request.DealGoodRequest;
import com.tesu.manicurehouse.request.GetUserAddressListRequest;
import com.tesu.manicurehouse.request.GetUserInfomationRequest;
import com.tesu.manicurehouse.response.AddCartResponse;
import com.tesu.manicurehouse.response.CalculatePostageResponse;
import com.tesu.manicurehouse.response.DealGoodResponse;
import com.tesu.manicurehouse.response.GetUserAddressListResponse;
import com.tesu.manicurehouse.response.GetUserInfomationResponse;
import com.tesu.manicurehouse.util.DialogUtils;
import com.tesu.manicurehouse.util.LogUtils;
import com.tesu.manicurehouse.util.SharedPrefrenceUtils;
import com.tesu.manicurehouse.util.UIUtils;
import com.tesu.manicurehouse.widget.MyListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 类描述：
 * 创建人：许明达
 * 创建时间：2016/8/16 11:40
 * 确认订单
 */
public class MakesureOrderActivity extends BaseActivity implements View.OnClickListener {

    private ImageView iv_top_back;
    private View rootView;
    private MyListView lv_selected_goods;
    private SelectedGoodsAdapter selectedGoodsAdapter;
    private Button btn_order;
    private String url;
    private Dialog loadingDialog;
    private GetUserAddressListResponse getUserAddressListResponse;
    private DealGoodResponse dealGoodResponse;
    private TextView tv_user_name;
    private TextView tv_user_tell;
    private TextView tv_user_address;
    private TextView tv_change_address;
    private AddressBean selected_address;
    private double goods_amount;//	商品总金额
    private double shipping_fee = 0;//	配送费用
    private String shipping_name;
    private int integral;//	积分（int）
    private List<OrderGoodBean> orderGoodBeanList;
    private TextView tv_good_total_price;
    private TextView tv_shipping_fee;
    private TextView tv_degsin_total_price;
    private TextView tv_total_price;
    private double total_price = 0;
    //总可以抵扣的积分
    private int total_integral = 0;
    private String ricIdList;
    private double fee;
    private int is_normal;
    private int video_id;
    private LinearLayout ll_address;
    private LinearLayout ll_empty;
    private TextView tv_courier;
    private RelativeLayout rl_shopping;
    private RelativeLayout rl_distribution;
    private CalculatePostageResponse calculatePostageResponse;
    private RelativeLayout rl;
    private RelativeLayout rl_two;
    private TextView tv_shopping_name_one;
    private TextView tv_shopping_fee_one;
    private TextView tv_shopping_name_two;
    private TextView tv_shopping_fee_two;
    private Button btn_cancel;

    @Override
    protected View initView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        rootView = View.inflate(this, R.layout.activity_makesure_order, null);
        setContentView(rootView);
        btn_cancel=(Button)rootView.findViewById(R.id.btn_cancel);
        tv_shopping_name_one = (TextView) rootView.findViewById(R.id.tv_shopping_name_one);
        tv_shopping_fee_one = (TextView) rootView.findViewById(R.id.tv_shopping_fee_one);
        tv_shopping_name_two = (TextView) rootView.findViewById(R.id.tv_shopping_name_two);
        tv_shopping_fee_two = (TextView) rootView.findViewById(R.id.tv_shopping_fee_two);
        rl = (RelativeLayout) rootView.findViewById(R.id.rl);
        rl_two = (RelativeLayout) rootView.findViewById(R.id.rl_two);
        rl_distribution = (RelativeLayout) rootView.findViewById(R.id.rl_distribution);
        rl_shopping = (RelativeLayout) rootView.findViewById(R.id.rl_shopping);
        tv_courier = (TextView) rootView.findViewById(R.id.tv_courier);
        ll_address = (LinearLayout) rootView.findViewById(R.id.ll_address);
        ll_empty = (LinearLayout) rootView.findViewById(R.id.ll_empty);
        lv_selected_goods = (MyListView) rootView.findViewById(R.id.lv_selected_goods);
        iv_top_back = (ImageView) rootView.findViewById(R.id.iv_top_back);
        btn_order = (Button) rootView.findViewById(R.id.btn_order);
        tv_user_name = (TextView) rootView.findViewById(R.id.tv_user_name);
        tv_user_tell = (TextView) rootView.findViewById(R.id.tv_user_tell);
        tv_user_address = (TextView) rootView.findViewById(R.id.tv_user_address);
        tv_change_address = (TextView) rootView.findViewById(R.id.tv_change_address);
        tv_good_total_price = (TextView) rootView.findViewById(R.id.tv_good_total_price);
        tv_shipping_fee = (TextView) rootView.findViewById(R.id.tv_shipping_fee);
        tv_degsin_total_price = (TextView) rootView.findViewById(R.id.tv_degsin_total_price);
        tv_total_price = (TextView) rootView.findViewById(R.id.tv_total_price);
        initData();
        return null;
    }


    public void initData() {
        Intent intent = getIntent();
        orderGoodBeanList = (List<OrderGoodBean>) intent.getSerializableExtra("orderGoodBeanList");
        fee = intent.getDoubleExtra("fee", 0);
        video_id = intent.getIntExtra("video_id", -1);
        is_normal = intent.getIntExtra("is_normal", 0);
        loadingDialog = DialogUtils.createLoadDialog(MakesureOrderActivity.this, false);
        runGetUserAddressList();
        rl_shopping.getBackground().setAlpha(100);
//        runGetUserInfo();
        if (orderGoodBeanList != null) {
            if (selectedGoodsAdapter == null) {
                selectedGoodsAdapter = new SelectedGoodsAdapter(MakesureOrderActivity.this, orderGoodBeanList);
            }
            lv_selected_goods.setAdapter(selectedGoodsAdapter);
            for (int i = 0; i < orderGoodBeanList.size(); i++) {
                if(orderGoodBeanList.get(i).getIntegral()==-1){

                    total_integral=(int) orderGoodBeanList.get(i).getShop_price()*10*Integer.parseInt(orderGoodBeanList.get(i).getGoods_number())+total_integral;
                    LogUtils.e("total_integral有负1："+total_integral);
                }
                else{
                    total_integral= orderGoodBeanList.get(i).getIntegral()*Integer.parseInt(orderGoodBeanList.get(i).getGoods_number())+total_integral;
                }
                LogUtils.e("total_integral："+total_integral);
                total_price = orderGoodBeanList.get(i).getTotal_price() + total_price;
                if (ricIdList != null) {
                    ricIdList = ricIdList + "," + orderGoodBeanList.get(i).getRec_id();
                } else {
                    ricIdList = orderGoodBeanList.get(i).getRec_id();
                }
            }
        }
        tv_degsin_total_price.setText("￥" + fee);
        goods_amount = total_price;
        DecimalFormat df = new DecimalFormat("0.00");
        tv_good_total_price.setText("￥" + df.format(total_price));
        tv_total_price.setText("￥" + df.format(total_price + fee));
        iv_top_back.setOnClickListener(this);
        btn_order.setOnClickListener(this);
        tv_change_address.setOnClickListener(this);
        rl_distribution.setOnClickListener(this);
        rl.setOnClickListener(this);
        rl_two.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_cancel:{
                rl_shopping.setVisibility(View.GONE);
                break;
            }
            case R.id.rl_two: {
                shipping_name = calculatePostageResponse.dataList.get(1).dispatch_name;
                shipping_fee = calculatePostageResponse.dataList.get(1).dispatch_price;
                tv_courier.setText(calculatePostageResponse.dataList.get(1).dispatch_name);
                tv_shipping_fee.setText("￥" + calculatePostageResponse.dataList.get(1).dispatch_price);
                DecimalFormat df = new DecimalFormat("0.00");
                tv_total_price.setText("￥" + df.format(total_price + fee + calculatePostageResponse.dataList.get(1).dispatch_price));
                rl_shopping.setVisibility(View.GONE);
                break;
            }
            case R.id.rl: {
                if (calculatePostageResponse.dataList.size() > 0) {
                    shipping_name = calculatePostageResponse.dataList.get(0).dispatch_name;
                    shipping_fee = calculatePostageResponse.dataList.get(0).dispatch_price;
                    tv_courier.setText(calculatePostageResponse.dataList.get(0).dispatch_name);
                    tv_shipping_fee.setText("￥" + calculatePostageResponse.dataList.get(0).dispatch_price);
                    DecimalFormat df = new DecimalFormat("0.00");
                    tv_total_price.setText("￥" + df.format(total_price + fee + calculatePostageResponse.dataList.get(0).dispatch_price));

                }
                rl_shopping.setVisibility(View.GONE);
                break;
            }
            case R.id.rl_distribution: {
                if (selected_address == null) {
                    DialogUtils.showAlertDialog(MakesureOrderActivity.this,
                            "请先添加一个收货地址！");
                    return;
                }
                rl_shopping.setVisibility(View.VISIBLE);
                if (calculatePostageResponse.dataList.size() > 0) {
                    switch (calculatePostageResponse.dataList.size()) {
                        case 1: {
                            rl.setVisibility(View.VISIBLE);
                            rl_two.setVisibility(View.GONE);
                            tv_shopping_name_one.setText(calculatePostageResponse.dataList.get(0).dispatch_name);
                            tv_shopping_fee_one.setText("￥" + calculatePostageResponse.dataList.get(0).dispatch_price);
                            break;
                        }
                        case 2: {
                            rl.setVisibility(View.VISIBLE);
                            rl_two.setVisibility(View.VISIBLE);
                            tv_shopping_name_one.setText(calculatePostageResponse.dataList.get(0).dispatch_name);
                            tv_shopping_fee_one.setText("￥" + calculatePostageResponse.dataList.get(0).dispatch_price);
                            tv_shopping_name_two.setText(calculatePostageResponse.dataList.get(1).dispatch_name);
                            tv_shopping_fee_two.setText("￥" + calculatePostageResponse.dataList.get(1).dispatch_price);
                            break;
                        }
                    }

                } else {
                    rl.setVisibility(View.VISIBLE);
                    rl_two.setVisibility(View.GONE);
                    tv_shopping_name_one.setText("免费包邮");
                    tv_shopping_fee_one.setText("￥" + 0.0);
                }
                break;
            }
            case R.id.iv_top_back:
                finish();
                overridePendingTransition(R.anim.animprv_in, R.anim.animprv_out);
                break;
            case R.id.btn_order: {
                if (selected_address == null) {
                    DialogUtils.showAlertDialog(MakesureOrderActivity.this,
                            "请先添加一个收货地址！");
                } else {
                    if(TextUtils.isEmpty(shipping_name)){
                        DialogUtils.showAlertDialog(MakesureOrderActivity.this,
                                "请选择配送方式");
                        return;
                    }
                    runDealGood();
                }

                break;
            }
            case R.id.tv_change_address: {
                Intent intent = new Intent(MakesureOrderActivity.this, MyAddressActivity.class);
                intent.putExtra("checkable", true);
                UIUtils.startActivityForResultNextAnim(intent, 1);
                break;
            }
        }
    }

    public void runGetUserAddressList() {
        loadingDialog.show();
        GetUserAddressListProtocol getUserAddressListProtocol = new GetUserAddressListProtocol();
        GetUserAddressListRequest getUserAddressListRequest = new GetUserAddressListRequest();
        url = getUserAddressListProtocol.getApiFun();
        getUserAddressListRequest.map.put("user_id", SharedPrefrenceUtils.getString(MakesureOrderActivity.this, "user_id"));


        MyVolley.uploadNoFile(MyVolley.POST, url, getUserAddressListRequest.map, new MyVolley.VolleyCallback() {
            @Override
            public void dealWithJson(String address, String json) {

                Gson gson = new Gson();
                getUserAddressListResponse = gson.fromJson(json, GetUserAddressListResponse.class);
                LogUtils.e("sendVerificationCodeResponse:" + json.toString());
                if (getUserAddressListResponse.code.equals("0")) {
                    loadingDialog.dismiss();
                    if (getUserAddressListResponse.dataList.size() > 0) {
                        selected_address = getUserAddressListResponse.dataList.get(0);
                        setAddress(getUserAddressListResponse.dataList.get(0));
                        ll_address.setVisibility(View.VISIBLE);
                        ll_empty.setVisibility(View.GONE);
                        tv_change_address.setText("修改");
                    } else {
                        loadingDialog.dismiss();
                        ll_address.setVisibility(View.GONE);
                        ll_empty.setVisibility(View.VISIBLE);
                        tv_change_address.setText("添加");
                    }
                } else {
                    loadingDialog.dismiss();
                    DialogUtils.showAlertDialog(MakesureOrderActivity.this,
                            getUserAddressListResponse.resultText);
                }


            }

            @Override
            public void dealWithError(String address, String error) {
                loadingDialog.dismiss();
                DialogUtils.showAlertDialog(MakesureOrderActivity.this, error);
            }
        });
    }

    //下单
    public void runDealGood() {
        loadingDialog.show();
        DealGoodProtocol dealGoodProtocol = new DealGoodProtocol();
        DealGoodRequest dealGoodRequest = new DealGoodRequest();
        url = dealGoodProtocol.getApiFun();
        dealGoodRequest.map.put("user_id", SharedPrefrenceUtils.getString(MakesureOrderActivity.this, "user_id"));
        dealGoodRequest.map.put("address_id", String.valueOf(selected_address.getAddress_id()));
        dealGoodRequest.map.put("goods_amount", String.valueOf(goods_amount));
        dealGoodRequest.map.put("shipping_fee", String.valueOf(shipping_fee));
        dealGoodRequest.map.put("shipping_name", shipping_name);
        //是否是一键下单，0：否，1：是
        dealGoodRequest.map.put("is_normal", String.valueOf(is_normal));
        dealGoodRequest.map.put("fee", String.valueOf(fee));
        dealGoodRequest.map.put("recIdList", ricIdList);
        if (video_id > 0) {
            dealGoodRequest.map.put("video_id", String.valueOf(video_id));
        }
        LogUtils.e("ricIdList:" + ricIdList);
        try {
            JSONArray jsonArray = new JSONArray();
            for (int i = 0; i < orderGoodBeanList.size(); i++) {
                JSONObject json = new JSONObject();
                json.put("goods_attr_id", orderGoodBeanList.get(i).getGoods_attr_id());
                json.put("attr_value", orderGoodBeanList.get(i).getAttr_value());
                json.put("goods_id", orderGoodBeanList.get(i).getGoods_id());
                json.put("goods_number", orderGoodBeanList.get(i).getGoods_number());
                jsonArray.put(json);
            }
            dealGoodRequest.map.put("goodList", jsonArray.toString());
            MyVolley.uploadNoFile(MyVolley.POST, url, dealGoodRequest.map, new MyVolley.VolleyCallback() {
                @Override
                public void dealWithJson(String address, String json) {

                    Gson gson = new Gson();
                    dealGoodResponse = gson.fromJson(json, DealGoodResponse.class);
                    LogUtils.e("sendVerificationCodeResponse:" + json.toString());
                    if (dealGoodResponse.code.equals("0")) {
                        loadingDialog.dismiss();
                        Intent intent = new Intent(MakesureOrderActivity.this, PayOrderActivity.class);
                        intent.putExtra("total_price", total_price);
                        intent.putExtra("total_integral",total_integral);
                        intent.putExtra("total_fee", dealGoodResponse.total_fee);
                        intent.putExtra("detail", dealGoodResponse.detail);
                        intent.putExtra("body", dealGoodResponse.body);
                        intent.putExtra("out_trade_no", dealGoodResponse.out_trade_no);
                        intent.putExtra("order_id", dealGoodResponse.order_id);
                        UIUtils.startActivityNextAnim(intent);
                        setFinish();
                    } else {
                        loadingDialog.dismiss();
                        DialogUtils.showAlertDialog(MakesureOrderActivity.this,
                                dealGoodResponse.resultText);
                    }


                }

                @Override
                public void dealWithError(String address, String error) {
                    loadingDialog.dismiss();
                    DialogUtils.showAlertDialog(MakesureOrderActivity.this, error);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    public void setAddress(AddressBean addressBean) {
        ll_address.setVisibility(View.VISIBLE);
        ll_empty.setVisibility(View.GONE);
        tv_change_address.setText("修改");
        tv_user_name.setText(addressBean.getConsignee());
        tv_user_tell.setText(addressBean.getTel());
        tv_user_address.setText(addressBean.getProvince_name() + addressBean.getCity_name() + addressBean.getDistrict_name() + addressBean.getAddress());
        runGetCalculatePostage();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == 1 && data != null) {
                selected_address = (AddressBean) data.getSerializableExtra("selected_address");
                setAddress(selected_address);
            }
        }

    }

    public void runGetCalculatePostage() {
        CalculatePostageProtocol calculatePostageProtocol = new CalculatePostageProtocol();
        CalculatePostageRequest calculatePostageRequest = new CalculatePostageRequest();
        url = calculatePostageProtocol.getApiFun();
        calculatePostageRequest.map.put("province", selected_address.getProvince());
        calculatePostageRequest.map.put("city", selected_address.getCity());
        calculatePostageRequest.map.put("district", selected_address.getDistrict());
        DecimalFormat df = new DecimalFormat("0.00");
        calculatePostageRequest.map.put("sum_goods_prices", df.format(total_price + fee));
        try {
            JSONArray jsonArray = new JSONArray();
            for (int i = 0; i < orderGoodBeanList.size(); i++) {
                JSONObject json = new JSONObject();
                json.put("goods_id", orderGoodBeanList.get(i).getGoods_id());
                json.put("goods_number", orderGoodBeanList.get(i).getGoods_number());
                jsonArray.put(json);
            }
            calculatePostageRequest.map.put("goodsList", jsonArray.toString());
            MyVolley.uploadNoFile(MyVolley.POST, url, calculatePostageRequest.map, new MyVolley.VolleyCallback() {
                @Override
                public void dealWithJson(String address, String json) {

                    Gson gson = new Gson();
                    calculatePostageResponse = gson.fromJson(json, CalculatePostageResponse.class);
                    LogUtils.e("运费:" + json.toString());
                    if (calculatePostageResponse.code == 0) {
                        if (calculatePostageResponse.dataList.size() > 0) {
                            shipping_name = calculatePostageResponse.dataList.get(0).dispatch_name;
                            shipping_fee = calculatePostageResponse.dataList.get(0).dispatch_price;
                            tv_courier.setText(calculatePostageResponse.dataList.get(0).dispatch_name);
                            tv_shipping_fee.setText("￥" + calculatePostageResponse.dataList.get(0).dispatch_price);
                            DecimalFormat df = new DecimalFormat("0.00");
                            tv_total_price.setText("￥" + df.format(total_price + fee + calculatePostageResponse.dataList.get(0).dispatch_price));
                        }
                    } else {
                        DialogUtils.showAlertDialog(MakesureOrderActivity.this,
                                calculatePostageResponse.resultText);
                    }


                }

                @Override
                public void dealWithError(String address, String error) {
                    DialogUtils.showAlertDialog(MakesureOrderActivity.this, error);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
