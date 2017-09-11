package com.tesu.manicurehouse.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
public class MoreColorAdapter extends BaseAdapter {

    private String TAG = "ClientListAdapter";
    List<GoodsAttrBean> goodsAttrs;
    private Context mContext;
    private CommentImageAdapter commentImageAdapter;
    private HashMap<Integer, Boolean> hashMap = new HashMap<Integer, Boolean>();
    private List<GoodsAttrBean> selectcolorlist;
    private SelectedColorAdapter selectedColorAdapter;

    public MoreColorAdapter(Context context, List<GoodsAttrBean> goodsAttrs, List<GoodsAttrBean> selectcolorlist, SelectedColorAdapter selectedColorAdapter) {
        mContext = context;
        this.goodsAttrs = goodsAttrs;
        this.selectcolorlist = selectcolorlist;
        this.selectedColorAdapter = selectedColorAdapter;
        if (!ImageLoader.getInstance().isInited()) {
            ImageLoader.getInstance()
                    .init(ImageLoaderConfiguration.createDefault(UIUtils
                            .getContext()));
        }
    }

    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return goodsAttrs.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return 0;
    }

    public void setSelected(int pos,int num) {
//        if(!selectcolorlist.contains(goodsAttrs.get(pos))){
            goodsAttrs.get(pos).setNum(num);
            selectcolorlist.add(goodsAttrs.get(pos));
//        }
        hashMap.put(pos, true);
        notifyDataSetChanged();
    }

    @Override
    public View getView(final int pos, View view, ViewGroup arg2) {
        // TODO Auto-generated method stub
        ViewHolder vh;
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.more_color_item, null);
            vh = new ViewHolder();
            vh.rl_color = (PercentRelativeLayout) view.findViewById(R.id.rl_color);
            vh.iv_choose = (ImageView) view.findViewById(R.id.iv_choose);
            vh.iv_image_color = (ImageView) view.findViewById(R.id.iv_image_color);
            vh.tv_image_name = (TextView) view.findViewById(R.id.tv_image_name);
            view.setTag(vh);
        } else {
            vh = (ViewHolder) view.getTag();
        }
        if (hashMap.get(pos) != null) {
            if (hashMap.get(pos)) {
                vh.iv_choose.setVisibility(View.VISIBLE);
            } else {
                vh.iv_choose.setVisibility(View.INVISIBLE);
            }
        } else {
            vh.iv_choose.setVisibility(View.INVISIBLE);
        }
        vh.rl_color.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtils.e("goodsAttrs:" + goodsAttrs.get(pos).getProduct_number());
                if(goodsAttrs.get(pos).getProduct_number()>0){
                    if (hashMap.get(pos) != null) {
                        if (hashMap.get(pos)) {
                            selectcolorlist.remove(goodsAttrs.get(pos));
                            selectedColorAdapter.notifyDataSetChanged();
                            hashMap.put(pos, false);
                        } else {
                            goodsAttrs.get(pos).setNum(1);
                            selectcolorlist.add(goodsAttrs.get(pos));
                            hashMap.put(pos, true);
                        }
                    } else {
                        goodsAttrs.get(pos).setNum(1);
                        selectcolorlist.add(goodsAttrs.get(pos));
                        hashMap.put(pos, true);
                    }
                    notifyDataSetChanged();
                }
                else{
                    Toast.makeText(mContext,"此色卡库存为0，不能购买！",Toast.LENGTH_SHORT).show();
                }
            }
        });
        vh.rl_color.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                LogUtils.e("Long");
                return false;
            }
        });
        vh.tv_image_name.setText(goodsAttrs.get(pos).getAttr_value());
        ImageLoader.getInstance().displayImage(goodsAttrs.get(pos).getAttr_img(), vh.iv_image_color, PictureOption.getSimpleOptions());
        return view;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return goodsAttrs.size();
    }


    class ViewHolder {
        PercentRelativeLayout rl_color;
        ImageView iv_choose;
        TextView tv_image_name;
        ImageView iv_image_color;
    }
}
