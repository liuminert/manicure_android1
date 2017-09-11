package com.tesu.manicurehouse.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tesu.manicurehouse.R;
import com.tesu.manicurehouse.bean.GroupProductBean;
import com.tesu.manicurehouse.bean.ProductBean;
import com.tesu.manicurehouse.bean.VideoClassBean;
import com.tesu.manicurehouse.widget.NoScrollGridView;

import java.util.List;


/**
 * 类描述：
 * 创建人：许明达
 * 创建时间：2016/04/14 10:49
 * 修改备注：
 */
public class GroupProductAdapter extends BaseAdapter{

    private String TAG="GroupProductAdapter";
    private List<GroupProductBean> groupProductBeanList;
    private Context mContext;
    private IProductCallBack iProductCallBack;
    public GroupProductAdapter(Context context, List<GroupProductBean> groupProductBeanList){
        mContext=context;
        this.groupProductBeanList=groupProductBeanList;
    }
    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return groupProductBeanList.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(final int pos, View view, ViewGroup parent) {
        // TODO Auto-generated method stub
        ViewHolder vh;
        if(view==null){
            view= LayoutInflater.from(mContext).inflate(R.layout.group_product_item, parent,false);
            vh=new ViewHolder();
            vh.group_product_name_tv = (TextView) view.findViewById(R.id.group_product_name_tv);
            vh.product_gv = (NoScrollGridView) view.findViewById(R.id.product_gv);
            view.setTag(vh);
        }else{
            vh=(ViewHolder)view.getTag();
        }
        final GroupProductBean groupProductBean = groupProductBeanList.get(pos);
        vh.group_product_name_tv.setText(groupProductBean.getGoods_type_name());
        ProductAdapter productAdapter = new ProductAdapter(mContext,groupProductBean.getGoodsList());
        vh.product_gv.setAdapter(productAdapter);

        vh.product_gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ProductBean productBean = groupProductBean.getGoodsList().get(position);
                if(productBean.isSelected()){
                    productBean.setIsSelected(false);
                    unChooseProduct(pos,position);
                }else {
                    productBean.setIsSelected(true);
                    chooseProduct(pos,position);
                }

                notifyDataSetChanged();

            }
        });

        return view;

    }

    private void chooseProduct(int pos,int position) {
        iProductCallBack.chooseProduct(pos,position);
    }

    private void unChooseProduct(int pos,int position){
        iProductCallBack.unChooseProduct(pos,position);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return groupProductBeanList.size();
    }



    class ViewHolder{
        TextView group_product_name_tv;
        NoScrollGridView product_gv;
    }

    public void setiProductCallBack(IProductCallBack iProductCallBack){
        this.iProductCallBack = iProductCallBack;
    }

    public interface IProductCallBack{
        void chooseProduct(int pos,int position);
        void unChooseProduct(int pos,int position);
    }
}
