package com.tesu.manicurehouse.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.tesu.manicurehouse.R;
import com.tesu.manicurehouse.adapter.OrderGoodsAdapter;
import com.tesu.manicurehouse.adapter.SearchAdapter;
import com.tesu.manicurehouse.adapter.ShoppingcarAdapter;
import com.tesu.manicurehouse.base.BaseActivity;
import com.tesu.manicurehouse.base.MyVolley;
import com.tesu.manicurehouse.bean.CartBean;
import com.tesu.manicurehouse.bean.OrderGoodBean;
import com.tesu.manicurehouse.callback.OnCallBackDeleteShopingCart;
import com.tesu.manicurehouse.protocol.GetCartListProtocol;
import com.tesu.manicurehouse.protocol.GetOrderListProtocol;
import com.tesu.manicurehouse.request.GetCartListRequest;
import com.tesu.manicurehouse.request.GetOrderListRequest;
import com.tesu.manicurehouse.response.GetCartListResponse;
import com.tesu.manicurehouse.response.GetOrderListResponse;
import com.tesu.manicurehouse.util.DialogUtils;
import com.tesu.manicurehouse.util.LogUtils;
import com.tesu.manicurehouse.util.SharedPrefrenceUtils;
import com.tesu.manicurehouse.util.UIUtils;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 类描述：
 * 创建人：许明达
 * 创建时间：2016/8/16 11:40
 * 购物车页面
 */
public class ShoppingCarActivity extends BaseActivity implements View.OnClickListener ,OnCallBackDeleteShopingCart {

    private ImageView iv_top_back;
    private View rootView;
    private ShoppingcarAdapter shoppingcarAdapter;
    private ListView lv_shopping_goods;
    private TextView tv_total_price;
    private Button btn_order;
    private CheckBox cb_all_good;
    private String url;
    private Dialog loadingDialog;
    private GetCartListResponse getCartListResponse;
    private double total_price;
    //购物车上物品数量
    private int num;
    private List<CartBean> selectedGoods;
    @Override
    protected View initView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        rootView = View.inflate(this, R.layout.activity_shoppingcar, null);
        setContentView(rootView);
        lv_shopping_goods=(ListView)rootView.findViewById(R.id.lv_shopping_goods);
        tv_total_price=(TextView)rootView.findViewById(R.id.tv_total_price);
        iv_top_back = (ImageView) rootView.findViewById(R.id.iv_top_back);
        btn_order=(Button)rootView.findViewById(R.id.btn_order);
        cb_all_good=(CheckBox)rootView.findViewById(R.id.cb_all_good);
        iv_top_back.setOnClickListener(this);
        btn_order.setOnClickListener(this);
        initData();
        return null;
    }


    public void initData() {
        selectedGoods=new ArrayList<CartBean>();
        loadingDialog = DialogUtils.createLoadDialog(ShoppingCarActivity.this, true);
        runGetCartList();
        cb_all_good.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isChecked=cb_all_good.isChecked();
                if (shoppingcarAdapter != null) {
                    shoppingcarAdapter.setAllChecked(isChecked);
                }
                LogUtils.e("pos isChecked:" + isChecked);
            }
        });
//        cb_all_good.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (shoppingcarAdapter != null) {
//                    shoppingcarAdapter.setAllChecked(isChecked);
//                }
//
//            }
//        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_top_back: {
                Intent intent = getIntent();
                intent.putExtra("num",num);
                setResult(1,intent);
                finish();
                overridePendingTransition(R.anim.animprv_in, R.anim.animprv_out);
                break;
            }
            case R.id.btn_order:
                Intent intent=new Intent(ShoppingCarActivity.this,MakesureOrderActivity.class);
                List<OrderGoodBean> orderGoodBeanList = new ArrayList<OrderGoodBean>();
                for(int i=0;i<selectedGoods.size();i++){
                    OrderGoodBean orderGoodBean = new OrderGoodBean();
                    orderGoodBean.setRec_id(selectedGoods.get(i).getRec_id());
                    orderGoodBean.setGoods_number(selectedGoods.get(i).getGoods_number());
                    orderGoodBean.setAttr_value(selectedGoods.get(i).getAttr_value());
                    orderGoodBean.setTotal_price(Integer.parseInt(selectedGoods.get(i).getGoods_number()) * Double.parseDouble(selectedGoods.get(i).getGoods_price()));
                    orderGoodBean.setGoods_attr_id(selectedGoods.get(i).getGoods_attr_id());
                    orderGoodBean.setGoods_id(String.valueOf(selectedGoods.get(i).getGoods_id()));
                    orderGoodBean.setGoods_img(selectedGoods.get(i).getGoods_img());
                    orderGoodBean.setGoods_name(selectedGoods.get(i).getGoods_name());
                    orderGoodBean.setIntegral(selectedGoods.get(i).getIntegral());
                    orderGoodBean.setShop_price(Double.valueOf(selectedGoods.get(i).getGoods_price()));
                    orderGoodBeanList.add(orderGoodBean);
                }
                if(orderGoodBeanList.size()==0){
                    Toast.makeText(ShoppingCarActivity.this,"购物车里面没有商品",Toast.LENGTH_LONG).show();
                    return;
                }
                Bundle bundle = new Bundle();
                bundle.putSerializable("orderGoodBeanList", (Serializable) orderGoodBeanList);
                intent.putExtras(bundle);
                intent.putExtra("fee", 0);
                intent.putExtra("is_normal",0);
                UIUtils.startActivityNextAnim(intent);
                setFinish();
                break;
        }
    }
    public void runGetCartList() {
        loadingDialog.show();
        GetCartListProtocol getCartListProtocol = new GetCartListProtocol();
        GetCartListRequest getCartListRequest = new GetCartListRequest();
        url = getCartListProtocol.getApiFun();
        getCartListRequest.map.put("user_id", SharedPrefrenceUtils.getString(ShoppingCarActivity.this, "user_id"));
        getCartListRequest.map.put("pageNo", "1");
        getCartListRequest.map.put("pageSize", "100");

        MyVolley.uploadNoFile(MyVolley.POST, url, getCartListRequest.map, new MyVolley.VolleyCallback() {
            @Override
            public void dealWithJson(String address, String json) {

                Gson gson = new Gson();
                getCartListResponse = gson.fromJson(json, GetCartListResponse.class);
                LogUtils.e("sendVerificationCodeResponse:" + json.toString());
                if (getCartListResponse.code.equals("0")) {
                    num=getCartListResponse.dataList.size();
                    loadingDialog.dismiss();
                    setDate(getCartListResponse.dataList);
                } else {
                    loadingDialog.dismiss();
                    DialogUtils.showAlertDialog(ShoppingCarActivity.this,
                            getCartListResponse.resultText);
                }


            }

            @Override
            public void dealWithError(String address, String error) {
                loadingDialog.dismiss();
                DialogUtils.showAlertDialog(ShoppingCarActivity.this, error);
            }
        });
    }
    public void setDate(List<CartBean> dataLists) {
        for (int i=0;i<dataLists.size();i++){
            dataLists.get(i).setSelected(true);
            if( Integer.parseInt(dataLists.get(i).getGoods_number())> dataLists.get(i).getProduct_number()){
                dataLists.get(i).setGoods_number(String.valueOf(dataLists.get(i).getProduct_number()));
            }
        }
        selectedGoods.clear();
        selectedGoods.addAll(dataLists);
        if(shoppingcarAdapter==null){
            shoppingcarAdapter=new ShoppingcarAdapter(ShoppingCarActivity.this,dataLists,btn_order,cb_all_good,loadingDialog,this);
        }
        lv_shopping_goods.setAdapter(shoppingcarAdapter);
        total_price = 0;
        for (int i=0;i<dataLists.size();i++){
            total_price=total_price+(Double.parseDouble(dataLists.get(i).getGoods_number())*Double.parseDouble(dataLists.get(i).getGoods_price()));
        }
        DecimalFormat df = new DecimalFormat("0.00");
        tv_total_price.setText("￥" + df.format(total_price));
        if(dataLists.size()==0){
            btn_order.getBackground().setAlpha(80);
        }
//        tv_total_price.setText("￥" + total_price);
    }

    @Override
    public void delCallBack(int par,CartBean cartBean) {
        num=par;

        if(selectedGoods.contains(cartBean)){
            selectedGoods.remove(cartBean);
            total_price=0;
            for (int i=0;i<selectedGoods.size();i++){
                total_price=total_price+(Double.parseDouble(selectedGoods.get(i).getGoods_number())*Double.parseDouble(selectedGoods.get(i).getGoods_price()));
            }
            DecimalFormat df = new DecimalFormat("0.00");
            tv_total_price.setText("￥" + df.format(total_price));
        }
        if(selectedGoods.size()==getCartListResponse.dataList.size()){
            cb_all_good.setChecked(true);
        }else {
            cb_all_good.setChecked(false);
        }

    }

    @Override
    public void delSelectItemCallBack(CartBean cartBea) {
        total_price=0;
        selectedGoods.remove(cartBea);
        if(selectedGoods.size()==getCartListResponse.dataList.size()){
            cb_all_good.setChecked(true);
        }else {
            cb_all_good.setChecked(false);
        }
        for (int i=0;i<selectedGoods.size();i++){
            total_price=total_price+(Double.parseDouble(selectedGoods.get(i).getGoods_number())*Double.parseDouble(selectedGoods.get(i).getGoods_price()));
        }
        DecimalFormat df = new DecimalFormat("0.00");
        tv_total_price.setText("￥" + df.format(total_price));
//        tv_total_price.setText("￥" + total_price);
    }
    @Override
    public void addSelectItemCallBack(CartBean cartBea) {
        total_price=0;
        if(!selectedGoods.contains(cartBea)){
            selectedGoods.add(cartBea);
        }
        if(selectedGoods.size()==getCartListResponse.dataList.size()){
            cb_all_good.setChecked(true);
        }else {
            cb_all_good.setChecked(false);
        }
        for (int i=0;i<selectedGoods.size();i++){
            total_price=total_price+(Double.parseDouble(selectedGoods.get(i).getGoods_number())*Double.parseDouble(selectedGoods.get(i).getGoods_price()));
        }
        DecimalFormat df = new DecimalFormat("0.00");
        tv_total_price.setText("￥" + df.format(total_price));
//        tv_total_price.setText("￥" + total_price);
    }

    @Override
    public void changeSelectItemCallBack(List<CartBean> cartBeanList) {
        total_price=0;
        for (int i=0;i<cartBeanList.size();i++){
            if(cartBeanList.get(i).isSelected()) {
                total_price = total_price + (Double.parseDouble(cartBeanList.get(i).getGoods_number()) * Double.parseDouble(cartBeanList.get(i).getGoods_price()));
            }
        }
        DecimalFormat df = new DecimalFormat("0.00");
        tv_total_price.setText("￥" + df.format(total_price));
    }
}
