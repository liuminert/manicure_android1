package com.tesu.manicurehouse.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.tesu.manicurehouse.R;
import com.tesu.manicurehouse.adapter.SelectGoodsAdapter;
import com.tesu.manicurehouse.base.BaseActivity;
import com.tesu.manicurehouse.bean.CartBean;
import com.tesu.manicurehouse.bean.GoodsAttrBean;
import com.tesu.manicurehouse.bean.OrderGoodBean;
import com.tesu.manicurehouse.callback.OnCallBackGood;
import com.tesu.manicurehouse.util.DialogUtils;
import com.tesu.manicurehouse.util.LogUtils;
import com.tesu.manicurehouse.util.UIUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 类描述：
 * 创建人：许明达
 * 创建时间：2016/8/16 11:40
 * 提及商品
 */
public class IncludeGoodsActivity extends BaseActivity implements View.OnClickListener,OnCallBackGood {

    private ImageView iv_top_back;
    private View rootView;
    private ListView lv_select_goods;
    private Button btn_order;
    private SelectGoodsAdapter selectGoodsAdapter;
    private List<GoodsAttrBean> goodList;
    private TextView tv_totol_num;
    private TextView tv_other_free1;
    private TextView tv_other_free1_title;
    private TextView tv_total_price;
    //设计费
    private double fee;
    //总费用
    private double total_fee=0;
    private int video_id;
    private List<GoodsAttrBean> selectedGoods;
    //购物车上物品数量
    private int num;
    private double total_price;
    @Override
    protected View initView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        rootView = View.inflate(this, R.layout.activity_include_goods, null);
        setContentView(rootView);
        lv_select_goods = (ListView) rootView.findViewById(R.id.lv_select_goods);
        iv_top_back = (ImageView) rootView.findViewById(R.id.iv_top_back);
        btn_order = (Button) rootView.findViewById(R.id.btn_order);
        tv_totol_num=(TextView)rootView.findViewById(R.id.tv_totol_num);
        tv_other_free1=(TextView)rootView.findViewById(R.id.tv_other_free1);
        tv_other_free1_title=(TextView)rootView.findViewById(R.id.tv_other_free1_title);
        tv_total_price=(TextView)rootView.findViewById(R.id.tv_total_price);
        btn_order.setOnClickListener(this);
        iv_top_back.setOnClickListener(this);
        initData();
        return null;
    }


    public void initData() {
        selectedGoods=new ArrayList<>();
        Intent intent= getIntent();
        fee=intent.getDoubleExtra("fee",0);
        goodList = (List<GoodsAttrBean>) intent.getSerializableExtra("goodList");
        video_id=intent.getIntExtra("video_id",0);
        if (selectGoodsAdapter == null) {
            selectGoodsAdapter = new SelectGoodsAdapter(IncludeGoodsActivity.this, goodList, btn_order,this);
        }
        lv_select_goods.setAdapter(selectGoodsAdapter);
        tv_totol_num.setText("共有" + goodList.size() + "个商品");

        if(fee<=0){
            tv_other_free1.setVisibility(View.INVISIBLE);
            tv_other_free1_title.setVisibility(View.INVISIBLE);
        }
        else{
            tv_other_free1.setVisibility(View.VISIBLE);
            tv_other_free1_title.setVisibility(View.VISIBLE);
            tv_other_free1.setText("￥"+fee);
        }
        for(int i=0;i<goodList.size();i++){
            total_fee=goodList.get(i).getShop_price()*goodList.get(i).getNum()+total_fee;
        }
        total_fee=total_fee+fee;
        DecimalFormat df = new DecimalFormat("0.00");
        tv_total_price.setText("合计:￥"+df.format(total_fee));
        selectedGoods.addAll(goodList);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_order:
//                LogUtils.e("selectedGoods:"+selectedGoods.size());
                    Intent intent = new Intent(IncludeGoodsActivity.this, MakesureOrderActivity.class);
                    List<OrderGoodBean> orderGoodBeanList = new ArrayList<OrderGoodBean>();
                    for(int i=0;i<selectedGoods.size();i++){
                        OrderGoodBean orderGoodBean = new OrderGoodBean();
                        orderGoodBean.setIntegral(selectedGoods.get(i).getIntegral());
                        orderGoodBean.setShop_price( selectedGoods.get(i).getShop_price());
                        orderGoodBean.setGoods_number(String.valueOf(selectedGoods.get(i).getNum()));
                        orderGoodBean.setAttr_value(selectedGoods.get(i).getAttr_value());
                        orderGoodBean.setTotal_price(selectedGoods.get(i).getNum() * selectedGoods.get(i).getShop_price());
                        orderGoodBean.setGoods_attr_id(String.valueOf(selectedGoods.get(i).getGoods_attr_id()));
                        orderGoodBean.setGoods_id(String.valueOf(selectedGoods.get(i).getGoods_id()));
                        orderGoodBean.setGoods_img(selectedGoods.get(i).getGoods_thumb());
                        orderGoodBean.setGoods_name(selectedGoods.get(i).getGoods_name());
                        orderGoodBeanList.add(orderGoodBean);
                    }
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("orderGoodBeanList", (Serializable) orderGoodBeanList);
                    intent.putExtras(bundle);
                    intent.putExtra("fee", fee);
                    intent.putExtra("is_normal",1);
                    intent.putExtra("video_id",video_id);
                    UIUtils.startActivityNextAnim(intent);
                break;
            case R.id.iv_top_back:
                finish();
                overridePendingTransition(R.anim.animprv_in, R.anim.animprv_out);
                break;
        }
    }

    @Override
    public void delCallBack(int par, GoodsAttrBean cartBean) {
        num=par;

        if(selectedGoods.contains(cartBean)){
            selectedGoods.remove(cartBean);
            total_price=0;
            for (int i=0;i<selectedGoods.size();i++){
                total_price=total_price+(selectedGoods.get(i).getNum()*selectedGoods.get(i).getShop_price());
            }
            DecimalFormat df = new DecimalFormat("0.00");
            tv_total_price.setText("￥" + df.format(total_price + fee));
        }
    }

    @Override
    public void delSelectItemCallBack(GoodsAttrBean cartBean) {
        total_price=0;
        selectedGoods.remove(cartBean);
        for (int i=0;i<selectedGoods.size();i++){
            total_price=total_price+(selectedGoods.get(i).getNum()*selectedGoods.get(i).getShop_price());
        }
        DecimalFormat df = new DecimalFormat("0.00");
        tv_total_price.setText("￥" + df.format(total_price+fee));
    }

    @Override
    public void addSelectItemCallBack(GoodsAttrBean cartBean) {
        total_price=0;
        if(!selectedGoods.contains(cartBean)){
            selectedGoods.add(cartBean);
        }
        for (int i=0;i<selectedGoods.size();i++){
            total_price=total_price+(selectedGoods.get(i).getNum()*selectedGoods.get(i).getShop_price());
        }
        DecimalFormat df = new DecimalFormat("0.00");
        tv_total_price.setText("￥" + df.format(total_price+fee));
    }

    @Override
    public void changeSelectItemCallBack(List<GoodsAttrBean> cartBeanList) {
        total_price=0;
        for (int i=0;i<selectedGoods.size();i++){
            total_price=total_price+(selectedGoods.get(i).getNum()*selectedGoods.get(i).getShop_price());
        }
        DecimalFormat df = new DecimalFormat("0.00");
        tv_total_price.setText("￥" + df.format(total_price+fee));
    }
}
