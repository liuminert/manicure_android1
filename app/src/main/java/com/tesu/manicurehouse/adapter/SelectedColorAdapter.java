package com.tesu.manicurehouse.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.tesu.manicurehouse.R;
import com.tesu.manicurehouse.bean.GoodsAttrBean;
import com.tesu.manicurehouse.response.GetGoodAttrListResponse;
import com.tesu.manicurehouse.support.PercentRelativeLayout;
import com.tesu.manicurehouse.util.LogUtils;
import com.tesu.manicurehouse.util.PictureOption;
import com.tesu.manicurehouse.util.UIUtils;

import java.util.HashMap;
import java.util.List;


/**
 * 类描述：
 * 创建人：许明达
 * 创建时间：2016/04/14 10:49
 * 修改备注：
 */
public class SelectedColorAdapter extends BaseAdapter{

    private String TAG="ClientListAdapter";
    private List<GoodsAttrBean> customerList;
    private Context mContext;
    public SelectedColorAdapter(Context context, List<GoodsAttrBean> customerList){
        mContext=context;
        this.customerList=customerList;
        if (!ImageLoader.getInstance().isInited()) {
            ImageLoader.getInstance()
                    .init(ImageLoaderConfiguration.createDefault(UIUtils
                            .getContext()));
        }
    }
    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return customerList.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(final int pos, View view, ViewGroup arg2) {
        // TODO Auto-generated method stub
        ViewHolder vh;
        if(view==null){
            view= LayoutInflater.from(mContext).inflate(R.layout.selected_color_item, null);
            vh=new ViewHolder();
            vh.iv_selected_color=(ImageView)view.findViewById(R.id.iv_selected_color);
            view.setTag(vh);
        }else{
            vh=(ViewHolder)view.getTag();
        }
        ImageLoader.getInstance().displayImage(customerList.get(pos).getAttr_img(),vh.iv_selected_color, PictureOption.getSimpleOptions());
        return view;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return customerList.size();
    }


    class ViewHolder{
        ImageView iv_selected_color;
    }
}
