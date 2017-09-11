package com.tesu.manicurehouse.adapter;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.tesu.manicurehouse.R;
import com.tesu.manicurehouse.bean.MessageBean;
import com.tesu.manicurehouse.response.GetKeywordsResponse;

import java.util.List;


/**
 * 类描述：
 * 创建人：许明达
 * 创建时间：2016/04/14 10:49
 * 修改备注：
 */
public class MessageAdapter extends BaseAdapter{

    private String TAG="MessageAdapter";
    private List<MessageBean> messageBeanList;
    private Context mContext;
    public MessageAdapter(Context context, List<MessageBean> messageBeanList){
        mContext=context;
        this.messageBeanList=messageBeanList;
    }
    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return messageBeanList.get(arg0);
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
            view= LayoutInflater.from(mContext).inflate(R.layout.message_item, null);
            vh=new ViewHolder();
            vh.tv_title=(TextView)view.findViewById(R.id.tv_title);
            vh.tv_content=(TextView)view.findViewById(R.id.tv_content);
            vh.iv_down_notice= (ImageView) view.findViewById(R.id.iv_down_notice);
            view.setTag(vh);
        }else{
            vh=(ViewHolder)view.getTag();
        }
        vh.tv_title.setText(messageBeanList.get(pos).getTitle());
//        vh.tv_content.setText(messageBeanList.get(pos).getContent());
        vh.tv_content.setText(Html.fromHtml(messageBeanList.get(pos).getContent()));
        if(messageBeanList.get(pos).getIs_read() == 0){
            vh.iv_down_notice.setVisibility(View.VISIBLE);
        }else {
            vh.iv_down_notice.setVisibility(View.INVISIBLE);
        }
        if(messageBeanList.get(pos).isShow()){
            vh.tv_content.setVisibility(View.VISIBLE);
        }else {
            vh.tv_content.setVisibility(View.GONE);
        }
        return view;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return messageBeanList.size();
    }

    class ViewHolder{
        TextView tv_title;
        TextView tv_content;
        ImageView iv_down_notice;
    }
}
