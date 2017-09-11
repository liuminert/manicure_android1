package com.tesu.manicurehouse.adapter;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.tesu.manicurehouse.R;
import com.tesu.manicurehouse.activity.ShoppingCarActivity;
import com.tesu.manicurehouse.base.MyVolley;
import com.tesu.manicurehouse.bean.CartBean;
import com.tesu.manicurehouse.callback.OnCallBackDeleteShopingCart;
import com.tesu.manicurehouse.protocol.DeleteCartProtocol;
import com.tesu.manicurehouse.protocol.GetCartListProtocol;
import com.tesu.manicurehouse.protocol.UpdateCartGoodsNumberProtocol;
import com.tesu.manicurehouse.request.DeleteCartRequest;
import com.tesu.manicurehouse.request.GetCartListRequest;
import com.tesu.manicurehouse.request.UpdateCartGoodsNumberRequest;
import com.tesu.manicurehouse.response.DeleteCartResponse;
import com.tesu.manicurehouse.response.GetCartListResponse;
import com.tesu.manicurehouse.response.UpdateCartGoodsNumberResponse;
import com.tesu.manicurehouse.util.DateUtils;
import com.tesu.manicurehouse.util.DialogUtils;
import com.tesu.manicurehouse.util.LogUtils;
import com.tesu.manicurehouse.util.PictureOption;
import com.tesu.manicurehouse.util.SharedPrefrenceUtils;
import com.tesu.manicurehouse.util.UIUtils;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;


public class ShoppingcarAdapter extends BaseAdapter implements View.OnClickListener {

    private String TAG = "ClientListAdapter";
    private List<CartBean> dataLists;
    private Context mContext;
    private HashMap<Integer, Boolean> hashMap = new HashMap<Integer, Boolean>();
    private int count = 0;
    private Button btn_order;
    private boolean allChecked = true;
    private CheckBox cb_all_good;
    private String url;
    private Dialog loadingDialog;
    private DeleteCartResponse deleteCartResponse;
    private String rec_id;
    private int del_item = 0;
    private OnCallBackDeleteShopingCart onCallBackDeleteShopingCart;
    private HashMap<CartBean, Boolean> isCheckMap = new HashMap<CartBean, Boolean>();
    //选择的数量
    private String goods_num;
    private int good_storage;
    private AlertDialog alertDialog;
    private int goods_id;
    private String goods_attr_id;
    private String goods_number;

    public ShoppingcarAdapter(Context context, List<CartBean> dataLists, Button btn_order, CheckBox cb_all_good, Dialog loadingDialog, OnCallBackDeleteShopingCart onCallBackDeleteShopingCart) {
        mContext = context;
        this.dataLists = dataLists;
        this.loadingDialog = loadingDialog;
        count = dataLists.size();
        this.btn_order = btn_order;
        this.cb_all_good = cb_all_good;
        this.onCallBackDeleteShopingCart = onCallBackDeleteShopingCart;
        if (!ImageLoader.getInstance().isInited()) {
            ImageLoader.getInstance()
                    .init(ImageLoaderConfiguration.createDefault(UIUtils
                            .getContext()));
        }

        for (int i = 0; i < dataLists.size(); i++) {
            isCheckMap.put(dataLists.get(i), true);
        }
    }

    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return dataLists.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return 0;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public View getView(final int pos, View view, ViewGroup arg2) {
        // TODO Auto-generated method stub
        ViewHolder vh;
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.shoppingcar_item, null);
            vh = new ViewHolder();
            vh.tv_editor = (TextView) view.findViewById(R.id.tv_editor);
            vh.tv_del = (TextView) view.findViewById(R.id.tv_del);
            vh.ch_good = (CheckBox) view.findViewById(R.id.ch_good);
            vh.tv_time = (TextView) view.findViewById(R.id.tv_time);
            vh.tv_good_name = (TextView) view.findViewById(R.id.tv_good_name);
            vh.tv_good_color = (TextView) view.findViewById(R.id.tv_good_color);
            vh.tv_good_total_price = (TextView) view.findViewById(R.id.tv_good_total_price);
            vh.tv_good_num = (TextView) view.findViewById(R.id.tv_good_num);
            vh.iv_good = (ImageView) view.findViewById(R.id.iv_good);
            vh.et_num = (EditText) view.findViewById(R.id.et_num);
            vh.tv_add = (TextView) view.findViewById(R.id.tv_add);
            vh.tv_reduce = (TextView) view.findViewById(R.id.tv_reduce);
            view.setTag(vh);
        } else {
            vh = (ViewHolder) view.getTag();
        }
        EditextListener editextListener=new EditextListener(pos, vh);
        ViewListener viewListener = new ViewListener(pos, vh);
        vh.tv_editor.setOnClickListener(viewListener);
        if (hashMap.get(pos) != null) {
            if (hashMap.get(pos)) {
                vh.tv_editor.setText("编辑");
                vh.tv_del.setVisibility(View.INVISIBLE);
                vh.et_num.setVisibility(View.INVISIBLE);
                vh.tv_add.setVisibility(View.INVISIBLE);
                vh.tv_reduce.setVisibility(View.INVISIBLE);
            } else {
                vh.tv_editor.setText("完成");
                vh.tv_del.setVisibility(View.VISIBLE);
                vh.et_num.setVisibility(View.VISIBLE);
                vh.tv_add.setVisibility(View.VISIBLE);
                vh.tv_reduce.setVisibility(View.VISIBLE);
            }
        }
        vh.tv_del.setOnClickListener(viewListener);
        vh.ch_good.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                LogUtils.e("pos:" + pos + "   :" + isChecked);
                dataLists.get(pos).setSelected(isChecked);
                if (isChecked) {
                    onCallBackDeleteShopingCart.addSelectItemCallBack(dataLists.get(pos));
                    count++;
                } else {
                    onCallBackDeleteShopingCart.delSelectItemCallBack(dataLists.get(pos));
                    count--;
                }
                isCheckMap.put(dataLists.get(pos), isChecked);
                if (count == 0) {
                    btn_order.getBackground().setAlpha(80);
                } else {
                    btn_order.getBackground().setAlpha(255);
                }
                if (count == dataLists.size()) {
                    cb_all_good.setChecked(true);
                } else if (count == 0) {
                    cb_all_good.setChecked(false);
                }
                LogUtils.e("count:" + count);
            }
        });
//        if (allChecked) {
//            vh.ch_good.setChecked(true);
//        } else {
//            vh.ch_good.setChecked(false);
//        }
        if (isCheckMap.size() > 0) {
            if (isCheckMap.get(dataLists.get(pos))) {
                vh.ch_good.setChecked(true);
            } else {
                vh.ch_good.setChecked(false);
            }
        }


        vh.tv_good_color.setText(dataLists.get(pos).getAttr_value());
        vh.tv_time.setText(dataLists.get(pos).getAdd_time());
        vh.tv_good_name.setText(dataLists.get(pos).getGoods_name());
        vh.tv_good_num.setText("x" + dataLists.get(pos).getGoods_number());
        DecimalFormat df = new DecimalFormat("0.00");
        vh.tv_good_total_price.setText("￥" + df.format(Double.parseDouble(dataLists.get(pos).getGoods_price()) * Integer.parseInt(dataLists.get(pos).getGoods_number())));
        ImageLoader.getInstance().displayImage(dataLists.get(pos).getGoods_img(), vh.iv_good, PictureOption.getSimpleOptions());
        vh.et_num.setText("" + dataLists.get(pos).getGoods_number());
        vh.tv_add.setOnClickListener(viewListener);
        vh.tv_reduce.setOnClickListener(viewListener);
        //搜索输入监听
        vh.et_num.setOnKeyListener(editextListener);
        return view;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return dataLists.size();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_ensure:
                rec_id = dataLists.get(del_item).getRec_id();
                alertDialog.dismiss();
                runDeleteCart();
                break;
            case R.id.tv_cancle:
                alertDialog.dismiss();
                break;
        }
    }


    class ViewHolder {
        TextView tv_editor;
        TextView tv_del;
        CheckBox ch_good;
        TextView tv_time;
        TextView tv_good_name;
        TextView tv_good_color;
        TextView tv_good_total_price;
        TextView tv_good_num;
        ImageView iv_good;
        EditText et_num;
        TextView tv_reduce;
        TextView tv_add;
    }

    /**
     * 点击事件
     */
    @SuppressLint("NewApi")
    private class ViewListener implements View.OnClickListener {

        /**
         * 选择的某项
         */
        public int position = 0;
        public ViewHolder vh;

        public ViewListener(int position, ViewHolder vh) {
            this.position = position;
            this.vh = vh;
        }

        @Override
        public void onClick(View v) {
            good_storage = dataLists.get(position).getAttr_number();
            switch (v.getId()) {
                case R.id.tv_del: {
                    rec_id = dataLists.get(position).getRec_id();
                    del_item = position;
                    runDeleteCart();
                    vh.tv_editor.setText("编辑");
                    vh.tv_del.setVisibility(View.INVISIBLE);
                    break;
                }
                case R.id.tv_editor: {
                    if (hashMap.get(position) != null) {
                        if (hashMap.get(position)) {
                            hashMap.put(position, false);
                        } else {
                            hashMap.put(position, true);
                            goods_id=dataLists.get(position).getGoods_id();
                            goods_attr_id=dataLists.get(position).getGoods_attr_id();
                            goods_number=dataLists.get(position).getGoods_number();
                            UpdateCartGoodsNumber();
                        }
                    } else {
                        hashMap.put(position, false);
                    }
                    notifyDataSetChanged();
                    break;
                }
                case R.id.tv_add: {
                    int good_num = 1;
                    goods_num = vh.et_num.getText().toString();
                    if (TextUtils.isEmpty(goods_num)) {
                        vh.et_num.setText("1");
                    } else {
                        good_num = Integer.parseInt(goods_num);
                    }

                    if (good_num < good_storage) {
                        if (good_num < good_storage) {
                            good_num = good_num + 1;
                        }
                        goods_num = String.valueOf(good_num);
                    } else {
                        DialogUtils.showAlertDialog(mContext,
                                "购买数量不能大于库存" + good_storage + "份");
                        goods_num = "" + good_storage;
                    }
                    dataLists.get(position).setGoods_number(goods_num);
                    notifyDataSetChanged();
                    onCallBackDeleteShopingCart.changeSelectItemCallBack(dataLists);
                    break;
                }
                case R.id.tv_reduce: {

                    int good_num = 1;
                    goods_num = vh.et_num.getText().toString();
                    LogUtils.e("减" + goods_num);
                    if (TextUtils.isEmpty(goods_num)) {
                        vh.et_num.setText("1");
                    } else {
                        good_num = Integer.parseInt(goods_num);
                    }
                    good_num = good_num - 1;
                    if (good_num <= 0) {
                        del_item = position;
                        alertDialog = DialogUtils.showAlertDoubleBtnDialog(mContext,
                                "确定要删除这个商品吗？", ShoppingcarAdapter.this);
                    } else {
                        dataLists.get(position).setGoods_number(String.valueOf(good_num));
                        notifyDataSetChanged();
                        onCallBackDeleteShopingCart.changeSelectItemCallBack(dataLists);
                    }
                    break;
                }
            }
        }
    }

    private class EditextListener implements View.OnKeyListener {

        /**
         * 选择的某项
         */
        public int position = 0;
        public ViewHolder vh;

        public EditextListener(int position, ViewHolder vh) {
            this.position = position;
            this.vh = vh;
        }

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (KeyEvent.KEYCODE_ENTER == keyCode && event.getAction() == KeyEvent.ACTION_DOWN) {
                int good_num=Integer.parseInt(vh.et_num.getText().toString());
                if (good_num <=good_storage) {
                    dataLists.get(position).setGoods_number(vh.et_num.getText().toString());
                    vh.et_num.setText(vh.et_num.getText().toString());
                } else {
                    DialogUtils.showAlertDialog(mContext,
                            "购买数量不能大于库存" + good_storage + "份");
                    dataLists.get(position).setGoods_number(String.valueOf(good_storage));
                    vh.et_num.setText(String.valueOf(good_storage));
                }

                return true;
            }
            return false;
        }
    }

    public void setAllChecked(boolean allChecked) {
        this.allChecked = allChecked;
        for (int i = 0; i < dataLists.size(); i++) {
            isCheckMap.put(dataLists.get(i), allChecked);
        }
        notifyDataSetChanged();
    }

    public void runDeleteCart() {
        loadingDialog.show();
        DeleteCartProtocol deleteCartProtocol = new DeleteCartProtocol();
        DeleteCartRequest deleteCartRequest = new DeleteCartRequest();
        url = deleteCartProtocol.getApiFun();
        deleteCartRequest.map.put("rec_id", rec_id);

        MyVolley.uploadNoFile(MyVolley.POST, url, deleteCartRequest.map, new MyVolley.VolleyCallback() {
            @Override
            public void dealWithJson(String address, String json) {

                Gson gson = new Gson();
                deleteCartResponse = gson.fromJson(json, DeleteCartResponse.class);
                LogUtils.e("sendVerificationCodeResponse:" + json.toString());
                if (deleteCartResponse.code.equals("0")) {
                    CartBean cartBean = dataLists.get(del_item);
                    dataLists.remove(del_item);
                    count--;
                    onCallBackDeleteShopingCart.delCallBack(dataLists.size(), cartBean);
                    loadingDialog.dismiss();
                    notifyDataSetChanged();

                } else {
                    loadingDialog.dismiss();
                    DialogUtils.showAlertDialog(mContext,
                            deleteCartResponse.resultText);
                }

            }

            @Override
            public void dealWithError(String address, String error) {
                loadingDialog.dismiss();
                DialogUtils.showAlertDialog(mContext, error);
            }
        });
    }

    public void UpdateCartGoodsNumber() {
        loadingDialog.show();
        UpdateCartGoodsNumberProtocol updateCartGoodsNumberProtocol = new UpdateCartGoodsNumberProtocol();
        UpdateCartGoodsNumberRequest updateCartGoodsNumberRequest = new UpdateCartGoodsNumberRequest();
        url = updateCartGoodsNumberProtocol.getApiFun();
        updateCartGoodsNumberRequest.map.put("user_id", SharedPrefrenceUtils.getString(mContext,"user_id"));
        updateCartGoodsNumberRequest.map.put("goods_id", String.valueOf(goods_id));
        updateCartGoodsNumberRequest.map.put("goods_attr_id", goods_attr_id);
        updateCartGoodsNumberRequest.map.put("goods_number", goods_number);

        MyVolley.uploadNoFile(MyVolley.POST, url, updateCartGoodsNumberRequest.map, new MyVolley.VolleyCallback() {
            @Override
            public void dealWithJson(String address, String json) {

                Gson gson = new Gson();
                UpdateCartGoodsNumberResponse updateCartGoodsNumberResponse = gson.fromJson(json, UpdateCartGoodsNumberResponse.class);
                LogUtils.e("sendVerificationCodeResponse:" + json.toString());
                if (updateCartGoodsNumberResponse.code==0) {
                    loadingDialog.dismiss();
                    notifyDataSetChanged();

                } else {
                    loadingDialog.dismiss();
                    DialogUtils.showAlertDialog(mContext,
                            deleteCartResponse.resultText);
                }

            }

            @Override
            public void dealWithError(String address, String error) {
                loadingDialog.dismiss();
                DialogUtils.showAlertDialog(mContext, error);
            }
        });
    }
}
