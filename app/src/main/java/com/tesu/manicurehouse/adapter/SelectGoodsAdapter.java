package com.tesu.manicurehouse.adapter;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.tesu.manicurehouse.R;
import com.tesu.manicurehouse.bean.CartBean;
import com.tesu.manicurehouse.bean.GoodsAttrBean;
import com.tesu.manicurehouse.callback.OnCallBackDeleteShopingCart;
import com.tesu.manicurehouse.callback.OnCallBackGood;
import com.tesu.manicurehouse.util.LogUtils;
import com.tesu.manicurehouse.util.PictureOption;
import com.tesu.manicurehouse.util.UIUtils;

import java.util.HashMap;
import java.util.List;


public class SelectGoodsAdapter extends BaseAdapter {

    private String TAG = "ClientListAdapter";
    private List<GoodsAttrBean> bitmapList;
    private Context mContext;
    private int count = 0;
    private Button btn_order;
    private OnCallBackGood onCallBackGood;
    private HashMap<GoodsAttrBean, Boolean> isCheckMap = new HashMap<GoodsAttrBean, Boolean>();

    public SelectGoodsAdapter(Context context, List<GoodsAttrBean> bitmapList, Button btn_order, OnCallBackGood onCallBackGood) {
        mContext = context;
        this.onCallBackGood = onCallBackGood;
        this.bitmapList = bitmapList;
        count = bitmapList.size();
        if (!ImageLoader.getInstance().isInited()) {
            ImageLoader.getInstance()
                    .init(ImageLoaderConfiguration.createDefault(UIUtils
                            .getContext()));
        }
        this.btn_order = btn_order;
        for (int i = 0; i < bitmapList.size(); i++) {
            isCheckMap.put(bitmapList.get(i), true);
        }
    }

    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return bitmapList.get(arg0);
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
            view = LayoutInflater.from(mContext).inflate(R.layout.select_good_item, null);
            vh = new ViewHolder();
            vh.ch_good = (CheckBox) view.findViewById(R.id.ch_good);
            vh.iv_good = (ImageView) view.findViewById(R.id.iv_good);
            vh.tv_good_color = (TextView) view.findViewById(R.id.tv_good_color);
            vh.tv_good_name = (TextView) view.findViewById(R.id.tv_good_name);
            vh.tv_good_num = (TextView) view.findViewById(R.id.tv_good_num);
            vh.tv_good_price = (TextView) view.findViewById(R.id.tv_good_price);
            view.setTag(vh);
        } else {
            vh = (ViewHolder) view.getTag();
        }
        ImageLoader.getInstance().displayImage(bitmapList.get(pos).getGoods_thumb(), vh.iv_good, PictureOption.getSimpleOptions());
        vh.tv_good_color.setText(bitmapList.get(pos).getAttr_value());
        vh.tv_good_num.setText("x" + bitmapList.get(pos).getNum());
        vh.tv_good_price.setText("￥" + bitmapList.get(pos).getShop_price());
        vh.tv_good_name.setText(bitmapList.get(pos).getGoods_name());
        vh.ch_good.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    onCallBackGood.addSelectItemCallBack(bitmapList.get(pos));
                    count++;
                } else {
                    onCallBackGood.delSelectItemCallBack(bitmapList.get(pos));
                    count--;
                }
                isCheckMap.put(bitmapList.get(pos), isChecked);
                if (count == 0) {
                    btn_order.getBackground().setAlpha(80);
                    btn_order.setClickable(false);
                } else {
                    btn_order.getBackground().setAlpha(255);
                    btn_order.setClickable(true);
                }
                LogUtils.e("count:" + count);
            }
        });
        if (isCheckMap.size() > 0) {
            if (isCheckMap.get(bitmapList.get(pos))) {
                vh.ch_good.setChecked(true);
            } else {
                vh.ch_good.setChecked(false);
            }
        }
        return view;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return bitmapList.size();
    }


    class ViewHolder {
        ImageView iv_good;
        TextView tv_good_name;
        TextView tv_good_color;
        TextView tv_good_price;
        TextView tv_good_num;
        CheckBox ch_good;
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
