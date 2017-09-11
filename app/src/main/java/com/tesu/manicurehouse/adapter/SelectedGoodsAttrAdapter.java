package com.tesu.manicurehouse.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.tesu.manicurehouse.R;
import com.tesu.manicurehouse.bean.GoodsAttrBean;
import com.tesu.manicurehouse.util.PictureOption;

import java.util.List;


/**
 * 类描述：
 * 创建人：许明达
 * 创建时间：2016/04/14 10:49
 * 修改备注：
 */
public class SelectedGoodsAttrAdapter extends BaseAdapter{

    private String TAG="SelectedGoodsAttrAdapter";
    private List<GoodsAttrBean> goodsAttrBeanList;
    private Context mContext;

    private LayoutInflater layoutInflater;
    private ImageLoader imageLoader;
    public SelectedGoodsAttrAdapter(Context context, List<GoodsAttrBean> goodsAttrBeanList){
        mContext=context;
        this.goodsAttrBeanList=goodsAttrBeanList;

        layoutInflater = LayoutInflater.from(context);
        imageLoader = ImageLoader.getInstance();
    }
    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return goodsAttrBeanList.get(arg0);
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
            view= LayoutInflater.from(mContext).inflate(R.layout.selected_goods_attr_item, parent,false);
            vh=new ViewHolder();
            vh.goods_attr_iv = (ImageView) view.findViewById(R.id.goods_attr_iv);
            view.setTag(vh);
        }else{
            vh=(ViewHolder)view.getTag();
        }
        GoodsAttrBean goodsAttrBean = goodsAttrBeanList.get(pos);

        //图片加载器的使用代码，就这一句代码即可实现图片的加载。请注意
        //这里的uri地址，因为我们现在实现的是获取本地图片，所以使
        //用"file://"+图片的存储地址。如果要获取网络图片，
        //这里的uri就是图片的网络地址。
        imageLoader.displayImage(goodsAttrBean.getAttr_img(), vh.goods_attr_iv, PictureOption.getSimpleOptions());

        return view;

    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return goodsAttrBeanList.size();
    }



    class ViewHolder{
        ImageView goods_attr_iv;
    }
}
