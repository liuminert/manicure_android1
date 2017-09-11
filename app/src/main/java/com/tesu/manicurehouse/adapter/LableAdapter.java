package com.tesu.manicurehouse.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.tesu.manicurehouse.R;

import java.util.List;


/**
 * 类描述：
 * 创建人：许明达
 * 创建时间：2016/04/14 10:49
 * 修改备注：
 */
public class LableAdapter extends BaseAdapter{

    private String TAG="ClientListAdapter";
    private List<String> customerList;
    private Context mContext;
    private CommentImageAdapter commentImageAdapter;
    public LableAdapter(Context context, List<String> customerList){
        mContext=context;
        this.customerList=customerList;
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
    public View getView(int pos, View view, ViewGroup arg2) {
        // TODO Auto-generated method stub
        ViewHolder vh;
        if(view==null){
            view= LayoutInflater.from(mContext).inflate(R.layout.lable_item, null);
            vh=new ViewHolder();
            view.setTag(vh);
        }else{
            vh=(ViewHolder)view.getTag();
        }

        return view;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return customerList.size();
    }


    class ViewHolder{
    }
}
