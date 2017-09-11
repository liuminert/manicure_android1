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
import com.tesu.manicurehouse.bean.OrderBean;
import com.tesu.manicurehouse.response.GetOrderListResponse;
import com.tesu.manicurehouse.util.DateUtils;
import com.tesu.manicurehouse.util.PictureOption;
import com.tesu.manicurehouse.util.UIUtils;

import java.io.Serializable;
import java.util.List;


public class OrderGoodsAdapter extends BaseAdapter {

    private List<OrderBean> dataLists;
    private Context mContext;
    //0显示按钮，1不显示
    private int type;

    public OrderGoodsAdapter(Context context, List<OrderBean> dataLists, int type) {
        mContext = context;
        this.type = type;
        this.dataLists = dataLists;
        if (!ImageLoader.getInstance().isInited()) {
            ImageLoader.getInstance()
                    .init(ImageLoaderConfiguration.createDefault(UIUtils
                            .getContext()));
        }
    }

    public void setDataLists(List<OrderBean> dataLists) {
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
            view = LayoutInflater.from(mContext).inflate(R.layout.order_good_item, null);
            vh = new ViewHolder();
            vh.btn_wait_pay = (Button) view.findViewById(R.id.btn_wait_pay);
            vh.tv_good_name = (TextView) view.findViewById(R.id.tv_good_name);
            vh.tv_good_num = (TextView) view.findViewById(R.id.tv_good_num);
            vh.tv_order_num = (TextView) view.findViewById(R.id.tv_order_num);
            vh.tv_order_time = (TextView) view.findViewById(R.id.tv_order_time);
            vh.tv_total_price = (TextView) view.findViewById(R.id.tv_total_price);
            vh.iv_good = (ImageView) view.findViewById(R.id.iv_good);
            view.setTag(vh);
        } else {
            vh = (ViewHolder) view.getTag();
        }
        vh.btn_wait_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dataLists.get(pos).getOrder_status() != 2) {
                    if (dataLists.get(pos).getPay_status() == 0) {
                        Intent intent = new Intent(mContext, OrderForThePaymentActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("OrderBean", (Serializable) dataLists.get(pos));
                        intent.putExtras(bundle);
                        UIUtils.startActivityForResultNextAnim(intent,1);
                    } else {
                        if (dataLists.get(pos).getShipping_status() == 0) {
                            Intent intent = new Intent(mContext, OrderForTheDeliveryActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("OrderBean", (Serializable) dataLists.get(pos));
                            intent.putExtras(bundle);
                            UIUtils.startActivityNextAnim(intent);
                        } else if (dataLists.get(pos).getShipping_status() == 1) {
                            Intent intent = new Intent(mContext, OrderForTheFoodActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("OrderBean", (Serializable) dataLists.get(pos));
                            intent.putExtras(bundle);
                            UIUtils.startActivityForResultNextAnim(intent, 2);
                        } else if (dataLists.get(pos).getIs_comment() == 0) {
                            Intent intent = new Intent(mContext, OrderForTheCommentActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("OrderBean", (Serializable) dataLists.get(pos));
                            intent.putExtras(bundle);
                            UIUtils.startActivityNextAnim(intent);
                        }
                    }
                }
            }
        });
        if (type == 1) {
            vh.btn_wait_pay.setVisibility(View.INVISIBLE);
        } else {
            vh.btn_wait_pay.setVisibility(View.VISIBLE);
        }

        if (dataLists.get(pos).getOrder_status() == 2) {
            vh.btn_wait_pay.setText("已取消");
        } else {
            if (dataLists.get(pos).getPay_status() == 0) {
                vh.btn_wait_pay.setText("待付款");
            } else {
                if (dataLists.get(pos).getShipping_status() == 0) {
                    vh.btn_wait_pay.setText("待发货");
                } else if (dataLists.get(pos).getShipping_status() == 1) {
                    vh.btn_wait_pay.setText("待收货");
                } else if (dataLists.get(pos).getShipping_status() == 2) {
                    if (dataLists.get(pos).getIs_comment() == 0) {
                        vh.btn_wait_pay.setText("待评论");
                    }
                }
            }
        }

        vh.tv_order_num.setText("订单编号:" + dataLists.get(pos).getOrder_sn());
        vh.tv_order_time.setText(DateUtils.milliToSimpleDateTime1(Long.parseLong(dataLists.get(pos).getAdd_time()) * 1000));
        if (dataLists.get(pos).getGoodsDataList().size() > 0) {
            ImageLoader.getInstance().displayImage(dataLists.get(pos).getGoodsDataList().get(0).getGoods_img(), vh.iv_good, PictureOption.getSimpleOptions());
            vh.tv_good_name.setText(dataLists.get(pos).getGoodsDataList().get(0).getGoods_name());
            vh.tv_good_num.setText("x" + dataLists.get(pos).getGoodsDataList().get(0).getGoods_number());
        } else {
            ImageLoader.getInstance().displayImage(null, vh.iv_good, PictureOption.getSimpleOptions());
            vh.tv_good_name.setText("没有商品信息");
            vh.tv_good_num.setText("x0");
        }
        vh.tv_total_price.setText("￥" + dataLists.get(pos).getOrder_amount());
        return view;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return dataLists.size();
    }


    class ViewHolder {
        Button btn_wait_pay;
        TextView tv_order_num;
        ImageView iv_good;
        TextView tv_good_name;
        TextView tv_good_num;
        TextView tv_order_time;
        TextView tv_total_price;
    }


}
