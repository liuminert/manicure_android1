package com.tesu.manicurehouse.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.tesu.manicurehouse.R;
import com.tesu.manicurehouse.activity.GoodInfoActivity;
import com.tesu.manicurehouse.bean.GoodBean;
import com.tesu.manicurehouse.util.PictureOption;
import com.tesu.manicurehouse.util.UIUtils;

import java.util.List;


/**
 * 类描述：
 * 创建人：许明达
 * 创建时间：2016/04/14 10:49
 * 修改备注：
 */
public class ShopAdapter extends BaseAdapter{

    private String TAG="ClientListAdapter";
    private List<GoodBean> goodBeans;
    private Context mContext;
    public ShopAdapter(Context context, List<GoodBean> goodBeans){
        mContext=context;
        this.goodBeans=goodBeans;
        if (!ImageLoader.getInstance().isInited()) {
            ImageLoader.getInstance()
                    .init(ImageLoaderConfiguration.createDefault(UIUtils
                            .getContext()));
        }
    }
    public void setDate(List<GoodBean> goodBeans){
        this.goodBeans=goodBeans;
        notifyDataSetChanged();
    }
    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return goodBeans.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(int pos, View view, ViewGroup arg2) {
        // TODO Auto-generated method stub
        ViewHolder vh;
        if(view==null){
            view= LayoutInflater.from(mContext).inflate(R.layout.shop_item, null);
            vh=new ViewHolder();
            vh.tv_good_name_one=(TextView)view.findViewById(R.id.tv_good_name_one);
            vh.tv_good_name_two=(TextView)view.findViewById(R.id.tv_good_name_two);
            vh.rl_right=(RelativeLayout)view.findViewById(R.id.rl_rihgt);
            vh.rl_lift=(RelativeLayout)view.findViewById(R.id.rl_lift);
            vh.tv_olde_price=(TextView)view.findViewById(R.id.tv_olde_price);
            vh.tv_olde_price_two=(TextView)view.findViewById(R.id.tv_olde_price_two);
            vh.iv_one=(ImageView)view.findViewById(R.id.iv_one);
            vh.iv_two=(ImageView)view.findViewById(R.id.iv_two);
            vh.tv_good_price_one=(TextView)view.findViewById(R.id.tv_good_price_one);
            vh.tv_good_price_two=(TextView)view.findViewById(R.id.tv_good_price_two);
            view.setTag(vh);
        }else{
            vh=(ViewHolder)view.getTag();
        }
        if(goodBeans.size()%2==0) {
            vh.tv_good_name_one.setText( goodBeans.get(pos * 2 + 0).getGoods_name());
            vh.tv_good_name_two.setText(goodBeans.get(pos * 2 + 1).getGoods_name());
            ImageLoader.getInstance().displayImage(goodBeans.get(pos * 2 + 0).getGoods_img(), vh.iv_one, PictureOption.getSimpleOptions());
            ImageLoader.getInstance().displayImage(goodBeans.get(pos * 2 + 1).getGoods_img(), vh.iv_two, PictureOption.getSimpleOptions());
            ClickListener clickListener=new ClickListener(pos * 2 + 0);
            ClickListener clickListener1=new ClickListener(pos * 2 + 1);
            vh.rl_lift.setOnClickListener(clickListener);
            vh.rl_right.setOnClickListener(clickListener1);
            vh.tv_olde_price.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            vh.tv_olde_price_two.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            vh.tv_good_price_one.setText("￥" + goodBeans.get(pos * 2 + 0).getShop_price());
            vh.tv_good_price_two.setText("￥"+goodBeans.get(pos * 2 + 1).getShop_price());
            vh.tv_olde_price.setText(goodBeans.get(pos * 2 + 0).getShop_price());
            vh.tv_olde_price_two.setText(goodBeans.get(pos * 2 + 1).getShop_price());
            vh.rl_right.setVisibility(View.VISIBLE);
        }

        else{
            if(pos*2==goodBeans.size()-1){
                vh.tv_good_name_one.setText(goodBeans.get(pos * 2 + 0).getGoods_name());
                ImageLoader.getInstance().displayImage(goodBeans.get(pos * 2 + 0).getGoods_img(), vh.iv_one, PictureOption.getSimpleOptions());
                vh.rl_right.setVisibility(View.INVISIBLE);
                ClickListener clickListener=new ClickListener(pos * 2 + 0);
                vh.rl_lift.setOnClickListener(clickListener);
                vh.tv_olde_price.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                vh.tv_good_price_one.setText("￥" + goodBeans.get(pos * 2 + 0).getShop_price());
                vh.tv_olde_price.setText(goodBeans.get(pos * 2 + 0).getShop_price());
            }
            else{
                vh.rl_right.setVisibility(View.VISIBLE);
                vh.tv_good_name_one.setText( goodBeans.get(pos * 2 + 0).getGoods_name());
                vh.tv_good_name_two.setText(goodBeans.get(pos * 2 + 1).getGoods_name());
                ImageLoader.getInstance().displayImage(goodBeans.get(pos * 2 + 0).getGoods_img(), vh.iv_one, PictureOption.getSimpleOptions());
                ImageLoader.getInstance().displayImage(goodBeans.get(pos * 2 + 1).getGoods_img(), vh.iv_two, PictureOption.getSimpleOptions());
                ClickListener clickListener=new ClickListener(pos * 2 + 0);
                ClickListener clickListener1=new ClickListener(pos * 2 + 1);
                vh.rl_lift.setOnClickListener(clickListener);
                vh.rl_right.setOnClickListener(clickListener1);
                vh.tv_olde_price.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                vh.tv_olde_price_two.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                vh.tv_good_price_one.setText("￥" + goodBeans.get(pos * 2 + 0).getShop_price());
                vh.tv_good_price_two.setText("￥" + goodBeans.get(pos * 2 + 1).getShop_price());
                vh.tv_olde_price.setText(goodBeans.get(pos * 2 + 0).getShop_price());
                vh.tv_olde_price_two.setText(goodBeans.get(pos * 2 + 1).getShop_price());
            }
        }

        return view;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return goodBeans.size()/2+goodBeans.size()%2;
    }


    class ViewHolder{
        TextView tv_good_name_one;
        TextView tv_good_name_two;
        RelativeLayout rl_right;
        TextView tv_olde_price;
        TextView tv_olde_price_two;
        RelativeLayout rl_lift;
        ImageView iv_one;
        ImageView iv_two;
        TextView tv_good_price_one;
        TextView tv_good_price_two;
    }

    /**
     * 点击事件
     */
    @SuppressLint("NewApi")
    private class ClickListener implements View.OnClickListener {

        /**
         * 选择的某项
         */
        public int position = 0;

        public ClickListener(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.rl_lift: {
                    Intent intent = new Intent(mContext, GoodInfoActivity.class);
                    intent.putExtra("goods_id",goodBeans.get(position).getGoods_id());
                    UIUtils.startActivityForResultNextAnim(intent,3);
                    break;
                }
                case R.id.rl_rihgt: {
                    Intent intent = new Intent(mContext, GoodInfoActivity.class);
                    intent.putExtra("goods_id",goodBeans.get(position).getGoods_id());
                    UIUtils.startActivityForResultNextAnim(intent, 3);
                    break;
                }
            }

        }
    }
}
