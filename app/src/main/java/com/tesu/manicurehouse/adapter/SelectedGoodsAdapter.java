package com.tesu.manicurehouse.adapter;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.tesu.manicurehouse.R;
import com.tesu.manicurehouse.bean.OrderGoodBean;
import com.tesu.manicurehouse.util.PictureOption;
import com.tesu.manicurehouse.util.UIUtils;

import java.text.DecimalFormat;
import java.util.List;


public class SelectedGoodsAdapter extends BaseAdapter {

    private String TAG = "ClientListAdapter";
    private List<OrderGoodBean> orderGoodBeanList;
    private Context mContext;

    public SelectedGoodsAdapter(Context context, List<OrderGoodBean> orderGoodBeanList) {
        mContext = context;
        this.orderGoodBeanList = orderGoodBeanList;
        if (!ImageLoader.getInstance().isInited()) {
            ImageLoader.getInstance()
                    .init(ImageLoaderConfiguration.createDefault(UIUtils
                            .getContext()));
        }
    }

    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return orderGoodBeanList.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return 0;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public View getView(int pos, View view, ViewGroup arg2) {
        // TODO Auto-generated method stub
        ViewHolder vh;
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.selected_good_item, null);
            vh = new ViewHolder();
            vh.iv_good=(ImageView)view.findViewById(R.id.iv_good);
            vh.tv_good_name=(TextView)view.findViewById(R.id.tv_good_name);
            vh.tv_total_price=(TextView)view.findViewById(R.id.tv_total_price);
            vh.tv_good_num=(TextView)view.findViewById(R.id.tv_good_num);
            view.setTag(vh);
        } else {
            vh = (ViewHolder) view.getTag();
        }
        ImageLoader.getInstance().displayImage(orderGoodBeanList.get(pos).getGoods_img(), vh.iv_good, PictureOption.getSimpleOptions());
        vh.tv_good_name.setText(orderGoodBeanList.get(pos).getGoods_name());
        vh.tv_good_num.setText("x" + orderGoodBeanList.get(pos).getGoods_number());
        DecimalFormat df = new DecimalFormat("0.00");
        vh.tv_total_price.setText(""+df.format(orderGoodBeanList.get(pos).getTotal_price()));
        return view;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return orderGoodBeanList.size();
    }


    class ViewHolder {
        ImageView iv_good;
        TextView tv_good_name;
        TextView tv_total_price;
        TextView tv_good_num;
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
