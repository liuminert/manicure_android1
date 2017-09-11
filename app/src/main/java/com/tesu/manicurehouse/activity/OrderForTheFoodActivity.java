package com.tesu.manicurehouse.activity;

import android.app.Dialog;
import android.content.Intent;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
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
import com.tesu.manicurehouse.support.PercentRelativeLayout;
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
 * 待收货页面
 */
public class OrderForTheFoodActivity extends BaseActivity implements View.OnClickListener {

    private ImageView iv_top_back;
    private View rootView;
    private String url;
    private Dialog loadingDialog;
    private UpdateOrderStatusResponse updateOrderStatusResponse;
    private OrderBean orderBean;
    private TextView tv_order_num;
    private TextView tv_add_time;
    private TextView tv_user_name;
    private TextView tv_user_tell;
    private TextView tv_address;
    private TextView tv_good_total_price;
    private TextView tv_total_price;
    private Button btn_ensure;
    private MyListView lv_order_good;
    private TheOrderGoodsAdapter theOrderGoodsAdapter;
    private TextView tv_shipping_name;
    private TextView tv_shopping_fee;
    private PercentRelativeLayout rl_logistics;
    @Override
    protected View initView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        rootView = View.inflate(this, R.layout.activity_order_for_the_food, null);
        setContentView(rootView);
        rl_logistics= (PercentRelativeLayout) rootView.findViewById(R.id.rl_logistics);
        iv_top_back = (ImageView) rootView.findViewById(R.id.iv_top_back);
        tv_shopping_fee= (TextView) rootView.findViewById(R.id.tv_shopping_fee);
        tv_order_num = (TextView) rootView.findViewById(R.id.tv_order_num);
        tv_add_time = (TextView) rootView.findViewById(R.id.tv_add_time);
        tv_user_name= (TextView) rootView.findViewById(R.id.tv_user_name);
        tv_user_tell= (TextView) rootView.findViewById(R.id.tv_user_tell);
        tv_shipping_name= (TextView) rootView.findViewById(R.id.tv_shipping_name);
        tv_total_price= (TextView) rootView.findViewById(R.id.tv_total_price);
        tv_good_total_price= (TextView) rootView.findViewById(R.id.tv_good_total_price);
        lv_order_good=(MyListView)rootView.findViewById(R.id.lv_order_good);
        tv_address= (TextView) rootView.findViewById(R.id.tv_address);
        btn_ensure=(Button)rootView.findViewById(R.id.btn_ensure);
        iv_top_back.setOnClickListener(this);
        btn_ensure.setOnClickListener(this);
        rl_logistics.setOnClickListener(this);
        initData();
        return null;
    }


    public void initData() {
        loadingDialog = DialogUtils.createLoadDialog(OrderForTheFoodActivity.this, true);
        if (!ImageLoader.getInstance().isInited()) {
            ImageLoader.getInstance()
                    .init(ImageLoaderConfiguration.createDefault(UIUtils
                            .getContext()));
        }
        orderBean = (OrderBean) getIntent().getSerializableExtra("OrderBean");
        if(orderBean!=null){
            setDate(orderBean);
        }
        iv_top_back.setOnClickListener(this);
        btn_ensure.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_logistics:
                Intent intent=new Intent(OrderForTheFoodActivity.this,LogisticsActivity.class);
                intent.putExtra("logistics",orderBean.getLogistics());
                if(orderBean.getShipping_name().equals("宅急送")){
                    intent.putExtra("type",0);
                }
                else{
                    intent.putExtra("type",1);
                }
                UIUtils.startActivityNextAnim(intent);
                break;
            case R.id.btn_ensure:
                runUpdateOrderStatus();
                break;
            case R.id.iv_top_back:
                finish();
                overridePendingTransition(R.anim.animprv_in, R.anim.animprv_out);
                break;
        }
    }

    public void runUpdateOrderStatus() {
        loadingDialog.show();
        UpdateOrderStatusProtocol updateOrderStatusProtocol = new UpdateOrderStatusProtocol();
        UpdateOrderStatusRequest updateOrderStatusRequest = new UpdateOrderStatusRequest();
        url = updateOrderStatusProtocol.getApiFun();
        updateOrderStatusRequest.map.put("user_id", SharedPrefrenceUtils.getString(OrderForTheFoodActivity.this, "user_id"));
        updateOrderStatusRequest.map.put("shipping_status", "2");
        updateOrderStatusRequest.map.put("order_id", orderBean.getOrder_id());

        MyVolley.uploadNoFile(MyVolley.POST, url, updateOrderStatusRequest.map, new MyVolley.VolleyCallback() {
            @Override
            public void dealWithJson(String address, String json) {

                Gson gson = new Gson();
                updateOrderStatusResponse = gson.fromJson(json, UpdateOrderStatusResponse.class);
                LogUtils.e("sendVerificationCodeResponse:" + json.toString());
                if (updateOrderStatusResponse.code==0) {
                    loadingDialog.dismiss();
                    Intent intent=getIntent();
                    intent.putExtra("status", "3");
                    setResult(2, intent);
                    finish();
                    overridePendingTransition(R.anim.animprv_in, R.anim.animprv_out);
                } else {
                    loadingDialog.dismiss();
                    DialogUtils.showAlertDialog(OrderForTheFoodActivity.this,
                            updateOrderStatusResponse.resultText);
                }


            }

            @Override
            public void dealWithError(String address, String error) {
                loadingDialog.dismiss();
                DialogUtils.showAlertDialog(OrderForTheFoodActivity.this, error);
            }
        });
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
           theOrderGoodsAdapter=new TheOrderGoodsAdapter(OrderForTheFoodActivity.this,orderBean.getGoodsDataList());
       }
       lv_order_good.setAdapter(theOrderGoodsAdapter);
    }
}
