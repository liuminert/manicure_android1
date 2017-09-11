package com.tesu.manicurehouse.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tesu.manicurehouse.R;

import java.util.List;


/**
 * 类描述：
 * 创建人：许明达
 * 创建时间：2016/04/14 10:49
 * 修改备注：
 */
public class HistoryAdapter extends BaseAdapter{

    private String TAG="ClientListAdapter";
    private List<String> customerList;
    private Context mContext;
    private CommentImageAdapter commentImageAdapter;
    public HistoryAdapter(Context context, List<String> customerList){
        mContext=context;
        this.customerList=customerList;
    }
    public void setDate(List<String> customerList){
        this.customerList=customerList;
        notifyDataSetChanged();
    }
    public void clearDate(){
        this.customerList.clear();
        notifyDataSetChanged();
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
            view= LayoutInflater.from(mContext).inflate(R.layout.history_item, null);
            vh=new ViewHolder();
            vh.tv_history_keyword=(TextView)view.findViewById(R.id.tv_history_keyword);
            view.setTag(vh);
        }else{
            vh=(ViewHolder)view.getTag();
        }
        vh.tv_history_keyword.setText(customerList.get(pos));
        return view;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return customerList.size();
    }


    class ViewHolder{
        TextView tv_history_keyword;
    }
}
