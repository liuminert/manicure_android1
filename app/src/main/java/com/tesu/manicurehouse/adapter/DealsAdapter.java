package com.tesu.manicurehouse.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.tesu.manicurehouse.R;
import com.tesu.manicurehouse.bean.SucDealGoodBean;

import java.util.ArrayList;
import java.util.List;


/**
 * 类描述：
 * 创建人：许明达
 * 创建时间：2016/04/14 10:49
 * 修改备注：
 */
public class DealsAdapter extends BaseAdapter{

    private String TAG="ClientListAdapter";
    private List<SucDealGoodBean> sucDealGoodList;
    private Context mContext;
    private CommentImageAdapter commentImageAdapter;
    public DealsAdapter(Context context, List<SucDealGoodBean> sucDealGoodList){
        mContext=context;
        this.sucDealGoodList=sucDealGoodList;
    }
    public void setDate(List<SucDealGoodBean> sucDealGoodList){
        this.sucDealGoodList=sucDealGoodList;
        notifyDataSetChanged();
    }
    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return sucDealGoodList.get(arg0);
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
            view= LayoutInflater.from(mContext).inflate(R.layout.deal_item, null);
            vh=new ViewHolder();
            vh.tv_tell=(TextView)view.findViewById(R.id.tv_tell);
            vh.tv_time=(TextView)view.findViewById(R.id.tv_time);
            vh.tv_goods_attr=(TextView)view.findViewById(R.id.tv_goods_attr);
            vh.tv_goods_num=(TextView)view.findViewById(R.id.tv_goods_num);
            view.setTag(vh);
        }else{
            vh=(ViewHolder)view.getTag();
        }
        vh.tv_goods_attr.setText(sucDealGoodList.get(pos).getAttr_value());
//        vh.tv_tell.setText(sucDealGoodList.get(pos).getTel().replace(sucDealGoodList.get(pos).getTel().substring(3, 8), "*"));
        vh.tv_tell.setText(sucDealGoodList.get(pos).getTel());
        vh.tv_goods_num.setText(sucDealGoodList.get(pos).getGoods_number());
        vh.tv_time.setText(sucDealGoodList.get(pos).getAdd_time());
        return view;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return sucDealGoodList.size();
    }


    class ViewHolder{
        TextView tv_tell;
        TextView tv_time;
        TextView tv_goods_attr;
        TextView tv_goods_num;
    }
}
