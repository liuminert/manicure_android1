package com.tesu.manicurehouse.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.tesu.manicurehouse.R;
import com.tesu.manicurehouse.adapter.MoreColorAdapter;
import com.tesu.manicurehouse.adapter.SelectedColorAdapter;
import com.tesu.manicurehouse.base.BaseActivity;
import com.tesu.manicurehouse.base.MyVolley;
import com.tesu.manicurehouse.bean.GoodsAttrBean;
import com.tesu.manicurehouse.protocol.GetGoodAttrListProtocol;
import com.tesu.manicurehouse.protocol.GetGoodDescProtocol;
import com.tesu.manicurehouse.request.GetGoodAttrListRequest;
import com.tesu.manicurehouse.request.GetGoodDescRequest;
import com.tesu.manicurehouse.response.GetGoodAttrListResponse;
import com.tesu.manicurehouse.response.GetGoodDescResponse;
import com.tesu.manicurehouse.response.GetGoodsAttrResponse;
import com.tesu.manicurehouse.support.PercentRelativeLayout;
import com.tesu.manicurehouse.util.DialogUtils;
import com.tesu.manicurehouse.util.LogUtils;
import com.tesu.manicurehouse.util.PictureOption;
import com.tesu.manicurehouse.util.SharedPrefrenceUtils;
import com.tesu.manicurehouse.util.UIUtils;
import com.tesu.manicurehouse.widget.HorizontalListView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 类描述：
 * 创建人：许明达
 * 创建时间：2016/8/30 10:40
 * 更多色卡页面
 */
public class MoreColorActivity extends BaseActivity implements View.OnClickListener {

    private ImageView iv_top_back;
    private View rootView;
    private TextView tv_next;
    private GridView gv_color;
    private MoreColorAdapter moreColorAdapter;
    private SelectedColorAdapter selectedColorAdapter;
    private HorizontalListView hl_color;
    private List<GoodsAttrBean> selectcolorlist;
    private int selectpos;
    private PercentRelativeLayout rl_color;
    private View v_bg;
    private TextView tv_cancle;
    private TextView tv_ensure;
    private String url;
    private Dialog loadingDialog;
    private GetGoodAttrListResponse getGoodAttrListResponse;
    private String goods_id;
    private ImageView iv_color;
    private TextView tv_color_name;
    private TextView tv_color_num;
    private EditText et_num;
    private TextView tv_reduce;
    private TextView tv_add;
    //选择的数量
    private String goods_num;
    private int good_storage = 30;
    private String goods_name;
    private String shop_price;
    private String goods_img;
    private int integral;
    @Override
    protected View initView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        rootView = View.inflate(this, R.layout.activity_more_color, null);
        setContentView(rootView);
        iv_top_back = (ImageView) rootView.findViewById(R.id.iv_top_back);
        tv_next = (TextView) rootView.findViewById(R.id.tv_next);
        gv_color = (GridView) rootView.findViewById(R.id.gv_color);
        hl_color = (HorizontalListView) rootView.findViewById(R.id.hl_color);
        rl_color = (PercentRelativeLayout) rootView.findViewById(R.id.rl_color);
        iv_color = (ImageView) rootView.findViewById(R.id.iv_color);
        tv_color_name = (TextView) rootView.findViewById(R.id.tv_color_name);
        tv_color_num= (TextView) rootView.findViewById(R.id.tv_color_num);
        tv_reduce = (TextView) rootView.findViewById(R.id.tv_reduce);
        tv_ensure= (TextView) rootView.findViewById(R.id.tv_ensure);
        tv_add = (TextView) rootView.findViewById(R.id.tv_add);
        et_num = (EditText) rootView.findViewById(R.id.et_num);
        v_bg = (View) rootView.findViewById(R.id.v_bg);
        tv_cancle = (TextView) rootView.findViewById(R.id.tv_cancle);
        initData();
        return null;
    }


    public void initData() {
        Intent intent = getIntent();
        integral=intent.getIntExtra("integral",0);
        goods_id = intent.getStringExtra("goods_id");
        goods_name= intent.getStringExtra("goods_name");
        goods_img= intent.getStringExtra("goods_img");
        shop_price= intent.getStringExtra("shop_price");
        loadingDialog = DialogUtils.createLoadDialog(MoreColorActivity.this, true);
        if (!ImageLoader.getInstance().isInited()) {
            ImageLoader.getInstance()
                    .init(ImageLoaderConfiguration.createDefault(UIUtils
                            .getContext()));
        }
        selectcolorlist = new ArrayList<GoodsAttrBean>();
        if (selectedColorAdapter == null) {
            selectedColorAdapter = new SelectedColorAdapter(MoreColorActivity.this, selectcolorlist);
        }
        hl_color.setAdapter(selectedColorAdapter);
        tv_next.setOnClickListener(this);
        tv_cancle.setOnClickListener(this);
        iv_top_back.setOnClickListener(this);
        tv_add.setOnClickListener(this);
        tv_reduce.setOnClickListener(this);
        tv_ensure.setOnClickListener(this);
        runAsyncTask();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_reduce: {
                int good_num = 1;
                goods_num = et_num.getText().toString();
                if (TextUtils.isEmpty(goods_num)) {
                    et_num.setText("1");
                } else {
                    if(goods_num.equals("0")){
                        et_num.setText("1");
                    }
                    else {
                        good_num = Integer.parseInt(goods_num);
                    }
                }
                good_num = good_num - 1;
                if (good_num == 0) {
                    et_num.setText("" + 1);
                    good_num = 1;
                } else {
                    et_num.setText("" + good_num);
                }
                break;
            }
            case R.id.tv_add: {
                int good_num = 1;
                goods_num = et_num.getText().toString();
                if (TextUtils.isEmpty(goods_num)) {
                    et_num.setText("1");
                } else {
                    good_num = Integer.parseInt(goods_num);
                }

                if (good_num < good_storage) {
                    if (good_num < good_storage) {
                        good_num = good_num + 1;
                    }
                    goods_num = String.valueOf(good_num);
                } else {
                    DialogUtils.showAlertDialog(MoreColorActivity.this,
                            "购买数量不能大于库存" + good_storage + "份");
                    goods_num = "" + good_storage;
                }
                et_num.setText(goods_num);
                break;
            }
            case R.id.tv_ensure:{
                v_bg.setVisibility(View.GONE);
                rl_color.setVisibility(View.GONE);
                if(selectcolorlist.contains(getGoodAttrListResponse.dataList.get(selectpos))){
                    for (int i=0;i<selectcolorlist.size();i++){
                        if(selectcolorlist.get(i).getGoods_attr_id()==getGoodAttrListResponse.dataList.get(selectpos).getGoods_attr_id()){
                            selectcolorlist.get(i).setNum(Integer.parseInt(et_num.getText().toString()));
                        }
                    }
                }
               else{
                    moreColorAdapter.setSelected(selectpos, Integer.parseInt(et_num.getText().toString()));
                }
                et_num.setText("1");
                break;
            }
            case R.id.tv_cancle: {
                v_bg.setVisibility(View.GONE);
                rl_color.setVisibility(View.GONE);
                et_num.setText("1");
                break;
            }
            case R.id.iv_top_back: {
                Intent result=getIntent();
                setResult(1, result);
                finish();
                overridePendingTransition(R.anim.animprv_in, R.anim.animprv_out);
                break;
            }
            case R.id.tv_next: {
                if (selectcolorlist.size() > 0) {
                    Intent intent = new Intent(MoreColorActivity.this, MakesureGoodActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("selectcolorlist", (Serializable) selectcolorlist);
                    intent.putExtras(bundle);
                    intent.putExtra("integral", integral);
                    intent.putExtra("goods_name", goods_name);
                    intent.putExtra("shop_price",shop_price);
                    intent.putExtra("goods_img", goods_img);
                    intent.putExtra("goods_id", goods_id);
                    UIUtils.startActivityForResultNextAnim(intent,1);
                    setFinish();
                    Intent result=getIntent();
                    setResult(2, result);
                } else {
                    DialogUtils.showAlertDialog(MoreColorActivity.this,
                            "请先选择色卡！");
                }
                break;
            }
        }
    }

    public void runAsyncTask() {
        loadingDialog.show();
        GetGoodAttrListProtocol getGoodAttrListProtocol = new GetGoodAttrListProtocol();
        GetGoodAttrListRequest getGoodAttrListRequest = new GetGoodAttrListRequest();
        url = getGoodAttrListProtocol.getApiFun();
        getGoodAttrListRequest.map.put("goods_id", goods_id);
        getGoodAttrListRequest.map.put("pageNo", "1");
        getGoodAttrListRequest.map.put("pageSize", "999");
        MyVolley.uploadNoFile(MyVolley.POST, url, getGoodAttrListRequest.map, new MyVolley.VolleyCallback() {
            @Override
            public void dealWithJson(String address, String json) {

                Gson gson = new Gson();
                getGoodAttrListResponse = gson.fromJson(json, GetGoodAttrListResponse.class);
                if (getGoodAttrListResponse.code == 0) {
                    LogUtils.e("json:" + json);
                    loadingDialog.dismiss();
                    setDate(getGoodAttrListResponse.dataList);
                } else {
                    loadingDialog.dismiss();
                    DialogUtils.showAlertDialog(MoreColorActivity.this,
                            getGoodAttrListResponse.resultText);
                }


            }

            @Override
            public void dealWithError(String address, String error) {
                loadingDialog.dismiss();
                DialogUtils.showAlertDialog(MoreColorActivity.this, error);
            }
        });
    }

    public void setDate(final List<GoodsAttrBean> goodsAttrs) {
        if (moreColorAdapter == null) {
            moreColorAdapter = new MoreColorAdapter(MoreColorActivity.this, goodsAttrs, selectcolorlist, selectedColorAdapter);
        }
        gv_color.setAdapter(moreColorAdapter);
        gv_color.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                selectpos=position;
                v_bg.setVisibility(View.VISIBLE);
                v_bg.getBackground().setAlpha(80);
                rl_color.setVisibility(View.VISIBLE);
                ImageLoader.getInstance().displayImage(goodsAttrs.get(position).getAttr_img(), iv_color, PictureOption.getSimpleOptions());
                tv_color_name.setText(goodsAttrs.get(position).getAttr_value());
                tv_color_num.setText("库存:"+goodsAttrs.get(position).getProduct_number());
                return true;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1){
            if(resultCode==2){
                Intent result=getIntent();
                setResult(3, result);
            }
        }
    }
}
