package com.tesu.manicurehouse.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tesu.manicurehouse.R;
import com.tesu.manicurehouse.response.GetKeywordsResponse;

import java.util.List;


/**
 * 类描述：
 * 创建人：许明达
 * 创建时间：2016/04/14 10:49
 * 修改备注：
 */
public class SearchAdapter extends BaseAdapter{

    private String TAG="ClientListAdapter";
    private List<GetKeywordsResponse.KeywordsBean> keywordsBeanList;
    private Context mContext;
    public SearchAdapter(Context context, List<GetKeywordsResponse.KeywordsBean> keywordsBeanList){
        mContext=context;
        this.keywordsBeanList=keywordsBeanList;
    }
    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return keywordsBeanList.get(arg0);
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
            view= LayoutInflater.from(mContext).inflate(R.layout.search_item, null);
            vh=new ViewHolder();
            vh.tv_search_keyword=(TextView)view.findViewById(R.id.tv_search_keyword);
            view.setTag(vh);
        }else{
            vh=(ViewHolder)view.getTag();
        }
        vh.tv_search_keyword.setText(keywordsBeanList.get(pos).search_keyword);
        return view;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return keywordsBeanList.size();
    }


    class ViewHolder{
        TextView tv_search_keyword;
    }
}
