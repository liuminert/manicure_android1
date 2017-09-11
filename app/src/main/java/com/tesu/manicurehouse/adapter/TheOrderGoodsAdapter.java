package com.tesu.manicurehouse.adapter;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.tesu.manicurehouse.R;
import com.tesu.manicurehouse.activity.OrderForTheCommentActivity;
import com.tesu.manicurehouse.activity.OrderForTheDeliveryActivity;
import com.tesu.manicurehouse.activity.OrderForTheFoodActivity;
import com.tesu.manicurehouse.activity.OrderForThePaymentActivity;
import com.tesu.manicurehouse.bean.GoodsDataBean;
import com.tesu.manicurehouse.bean.OrderBean;
import com.tesu.manicurehouse.util.DateUtils;
import com.tesu.manicurehouse.util.LogUtils;
import com.tesu.manicurehouse.util.PictureOption;
import com.tesu.manicurehouse.util.UIUtils;

import java.io.Serializable;
import java.util.List;


public class TheOrderGoodsAdapter extends BaseAdapter {

    private List<GoodsDataBean> dataLists;
    private Context mContext;

    public TheOrderGoodsAdapter(Context context, List<GoodsDataBean> dataLists) {
        mContext = context;
        this.dataLists = dataLists;
        if (!ImageLoader.getInstance().isInited()) {
            ImageLoader.getInstance()
                    .init(ImageLoaderConfiguration.createDefault(UIUtils
                            .getContext()));
        }
    }
    public void setDataLists( List<GoodsDataBean> dataLists){
        this.dataLists = dataLists;
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
            view = LayoutInflater.from(mContext).inflate(R.layout.order_goods_item, null);
            vh = new ViewHolder();
            vh.tv_good_name=(TextView)view.findViewById(R.id.tv_good_name);
            vh.tv_good_num=(TextView)view.findViewById(R.id.tv_good_num);
            vh.tv_goods_amount=(TextView)view.findViewById(R.id.tv_goods_amount);
            vh.iv_good=(ImageView)view.findViewById(R.id.iv_good);
            view.setTag(vh);
        } else {
            vh = (ViewHolder) view.getTag();
        }
        vh.tv_good_name.setText(dataLists.get(pos).getGoods_name());
        vh.tv_good_num.setText("x" + dataLists.get(pos).getGoods_number());
        vh.tv_goods_amount.setText("￥" + (Integer.parseInt(dataLists.get(pos).getGoods_number()) * Double.parseDouble(dataLists.get(pos).getShop_price())));
        ImageLoader.getInstance().displayImage(dataLists.get(pos).getGoods_img(),vh.iv_good, PictureOption.getSimpleOptions());
        return view;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return dataLists.size();
    }


    class ViewHolder {
        ImageView iv_good;
        TextView tv_good_name;
        TextView tv_good_num;
        TextView tv_goods_amount;
    }
    /**
     * 点击事件
     */
    @SuppressLint("NewApi")
    private class ImageViewListener implements View.OnClickListener {

        /**
         * 选择的某项
         */
        public int position = 0;
        public ImageViewListener(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
        }
    }

}
