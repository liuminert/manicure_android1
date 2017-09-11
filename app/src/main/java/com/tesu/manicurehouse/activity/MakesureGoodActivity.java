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
import com.tesu.manicurehouse.adapter.SelectedColorGoodsAdapter;
import com.tesu.manicurehouse.adapter.SelectedGoodsAdapter;
import com.tesu.manicurehouse.base.BaseActivity;
import com.tesu.manicurehouse.base.MyVolley;
import com.tesu.manicurehouse.bean.GoodsAttrBean;
import com.tesu.manicurehouse.bean.OrderGoodBean;
import com.tesu.manicurehouse.callback.OnCallBackSelectColorList;
import com.tesu.manicurehouse.protocol.AddCartProtocol;
import com.tesu.manicurehouse.protocol.GetGoodAttrListProtocol;
import com.tesu.manicurehouse.request.AddCartRequest;
import com.tesu.manicurehouse.request.GetGoodAttrListRequest;
import com.tesu.manicurehouse.response.AddCartResponse;
import com.tesu.manicurehouse.response.GetGoodAttrListResponse;
import com.tesu.manicurehouse.util.DialogUtils;
import com.tesu.manicurehouse.util.LogUtils;
import com.tesu.manicurehouse.util.PictureOption;
import com.tesu.manicurehouse.util.SharedPrefrenceUtils;
import com.tesu.manicurehouse.util.UIUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 类描述：
 * 创建人：许明达
 * 创建时间：2016/8/16 11:40
 * 确认商品
 */
public class MakesureGoodActivity extends BaseActivity implements View.OnClickListener, OnCallBackSelectColorList {

    private ImageView iv_top_back;
    private View rootView;
    private ListView lv_selected_goods;
    private SelectedColorGoodsAdapter selectedColorGoodsAdapter;
    private Button btn_add_shopingcar;
    private Button btn_pay;
    private TextView tv_add;
    private String goods_id;
    private String goods_name;
    private String shop_price;
    private String goods_img;
    private TextView tv_good_name;
    private TextView tv_good_price;
    private ImageView iv_good_image;
    private String url;
    private Dialog loadingDialog;
    private AddCartResponse addCartResponse;
    //已选择的色卡集合
    private List<GoodsAttrBean> selectcolorlist;
    private int integral;
    @Override
    protected View initView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        rootView = View.inflate(this, R.layout.activity_makesure_good, null);
        setContentView(rootView);
        lv_selected_goods = (ListView) rootView.findViewById(R.id.lv_selected_goods);
        iv_top_back = (ImageView) rootView.findViewById(R.id.iv_top_back);
        btn_pay = (Button) rootView.findViewById(R.id.btn_pay);
        btn_add_shopingcar = (Button) rootView.findViewById(R.id.btn_add_shopingcar);
        tv_good_name = (TextView) rootView.findViewById(R.id.tv_good_name);
        tv_good_price = (TextView) rootView.findViewById(R.id.tv_good_price);
        iv_good_image = (ImageView) rootView.findViewById(R.id.iv_good_image);
        tv_add = (TextView) rootView.findViewById(R.id.tv_add);
        iv_top_back.setOnClickListener(this);
        btn_pay.setOnClickListener(this);
        btn_add_shopingcar.setOnClickListener(this);
        initData();
        return null;
    }


    public void initData() {
        loadingDialog = DialogUtils.createLoadDialog(MakesureGoodActivity.this, true);
        if (!ImageLoader.getInstance().isInited()) {
            ImageLoader.getInstance()
                    .init(ImageLoaderConfiguration.createDefault(UIUtils
                            .getContext()));
        }
        Intent intent = getIntent();
        selectcolorlist = (List<GoodsAttrBean>) getIntent().getSerializableExtra("selectcolorlist");
        goods_name = intent.getStringExtra("goods_name");
        goods_id = intent.getStringExtra("goods_id");
        goods_img = intent.getStringExtra("goods_img");
        shop_price = intent.getStringExtra("shop_price");
        integral=intent.getIntExtra("integral",0);
        if (selectedColorGoodsAdapter == null) {
            selectedColorGoodsAdapter = new SelectedColorGoodsAdapter(MakesureGoodActivity.this, selectcolorlist, this);
        }
        lv_selected_goods.setAdapter(selectedColorGoodsAdapter);
        tv_good_name.setText(goods_name);
        tv_good_price.setText(shop_price);
        ImageLoader.getInstance().displayImage(goods_img, iv_good_image, PictureOption.getSimpleOptions());
        tv_add.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_add:
                finish();
                overridePendingTransition(R.anim.animprv_in, R.anim.animprv_out);
                break;
            case R.id.iv_top_back:
                finish();
                overridePendingTransition(R.anim.animprv_in, R.anim.animprv_out);
                break;
            case R.id.btn_pay: {
                Intent intent = new Intent(MakesureGoodActivity.this, MakesureOrderActivity.class);
                List<OrderGoodBean> orderGoodBeanList = new ArrayList<OrderGoodBean>();
                for (int i = 0; i < selectcolorlist.size(); i++) {
                    OrderGoodBean orderGoodBean = new OrderGoodBean();
                    orderGoodBean.setGoods_number(String.valueOf(selectcolorlist.get(i).getNum()));
                    orderGoodBean.setAttr_value(selectcolorlist.get(i).getAttr_value());
                    orderGoodBean.setIntegral(integral);
                    orderGoodBean.setShop_price(Double.parseDouble(shop_price));
                    orderGoodBean.setTotal_price(selectcolorlist.get(i).getNum() * Double.parseDouble(shop_price));
                    orderGoodBean.setGoods_attr_id(String.valueOf(selectcolorlist.get(i).getGoods_attr_id()));
                    orderGoodBean.setGoods_id(String.valueOf(selectcolorlist.get(i).getGoods_id()));
                    orderGoodBean.setGoods_img(goods_img);
                    orderGoodBean.setGoods_name(goods_name);
                    orderGoodBeanList.add(orderGoodBean);
                }
                Bundle bundle = new Bundle();
                bundle.putSerializable("orderGoodBeanList", (Serializable) orderGoodBeanList);
                intent.putExtras(bundle);
                intent.putExtra("fee", 0);
                intent.putExtra("is_normal",0);
                UIUtils.startActivityNextAnim(intent);
                setFinish();
                Intent result = getIntent();
                setResult(2, result);
                break;
            }
            case R.id.btn_add_shopingcar:
                runAddCart();
                break;
        }
    }

    public void runAddCart() {
        loadingDialog.show();
        AddCartProtocol addCartProtocol = new AddCartProtocol();
        AddCartRequest addCartRequest = new AddCartRequest();
        url = addCartProtocol.getApiFun();
        addCartRequest.map.put("goods_id", goods_id);
        addCartRequest.map.put("user_id", SharedPrefrenceUtils.getString(MakesureGoodActivity.this, "user_id"));
        JSONArray jsonArray = new JSONArray();
        try {
            for (int i = 0; i < selectcolorlist.size(); i++) {
                JSONObject json = new JSONObject();
                json.put("goods_attr_id", selectcolorlist.get(i).getGoods_attr_id());
                json.put("goods_attr", selectcolorlist.get(i).getAttr_value());
                json.put("goods_number", selectcolorlist.get(i).getNum());
                jsonArray.put(json);
            }
            addCartRequest.map.put("goodsAttrList", jsonArray.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        LogUtils.e("addCartRequest.map:" + addCartRequest.map.toString());
        MyVolley.uploadNoFile(MyVolley.POST, url, addCartRequest.map, new MyVolley.VolleyCallback() {
            @Override
            public void dealWithJson(String address, String json) {

                Gson gson = new Gson();
                addCartResponse = gson.fromJson(json, AddCartResponse.class);
                if (addCartResponse.code == 0) {
                    LogUtils.e("json:" + json);
                    loadingDialog.dismiss();
                    finishActivity();
                    finish();
                    overridePendingTransition(R.anim.animprv_in, R.anim.animprv_out);
                } else {
                    loadingDialog.dismiss();
                    DialogUtils.showAlertDialog(MakesureGoodActivity.this,
                            addCartResponse.resultText);
                }


            }

            @Override
            public void dealWithError(String address, String error) {
                loadingDialog.dismiss();
                DialogUtils.showAlertDialog(MakesureGoodActivity.this, error);
            }
        });
    }

    @Override
    public void SelectColorListCallBack(List<GoodsAttrBean> selectcolorlist) {
        this.selectcolorlist = selectcolorlist;
    }
}
