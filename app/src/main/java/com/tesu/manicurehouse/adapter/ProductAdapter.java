package com.tesu.manicurehouse.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.tesu.manicurehouse.R;
import com.tesu.manicurehouse.bean.GroupProductBean;
import com.tesu.manicurehouse.bean.ProductBean;
import com.tesu.manicurehouse.util.PictureOption;
import com.tesu.manicurehouse.widget.NoScrollGridView;

import java.util.List;


/**
 * 类描述：
 * 创建人：许明达
 * 创建时间：2016/04/14 10:49
 * 修改备注：
 */
public class ProductAdapter extends BaseAdapter{

    private String TAG="ProductAdapter";
    private List<ProductBean> productBeanList;
    private Context mContext;

    private LayoutInflater layoutInflater;
    private ImageLoader imageLoader;
    public ProductAdapter(Context context, List<ProductBean> productBeanList){
        mContext=context;
        this.productBeanList=productBeanList;

        layoutInflater = LayoutInflater.from(context);
        imageLoader = ImageLoader.getInstance();
        imageLoader.init((ImageLoaderConfiguration.createDefault(mContext)));
    }
    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return productBeanList.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(int pos, View view, ViewGroup parent) {
        // TODO Auto-generated method stub
        ViewHolder vh;
        if(view==null){
            view= LayoutInflater.from(mContext).inflate(R.layout.product_item, parent,false);
            vh=new ViewHolder();
            vh.product_iv = (ImageView) view.findViewById(R.id.product_iv);
            vh.product_cb = (CheckBox) view.findViewById(R.id.product_cb);
            vh.tv_product_name=(TextView)view.findViewById(R.id.tv_product_name);
            view.setTag(vh);
        }else{
            vh=(ViewHolder)view.getTag();
        }
        ProductBean productBean = productBeanList.get(pos);

        //图片加载器的使用代码，就这一句代码即可实现图片的加载。请注意
        //这里的uri地址，因为我们现在实现的是获取本地图片，所以使
        //用"file://"+图片的存储地址。如果要获取网络图片，
        //这里的uri就是图片的网络地址。
        imageLoader.displayImage(productBean.getGoods_thumb(), vh.product_iv, PictureOption.getSimpleOptions());
        vh.product_cb.setChecked(productBean.isSelected());
        vh.tv_product_name.setText(productBean.getGoods_name());
        return view;

    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return productBeanList.size();
    }



    class ViewHolder{
        ImageView product_iv;
        CheckBox product_cb;
        TextView tv_product_name;
    }
}
