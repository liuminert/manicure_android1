package com.tesu.manicurehouse.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tesu.manicurehouse.R;
import com.tesu.manicurehouse.bean.VideoLableBean;
import com.tesu.manicurehouse.support.PercentRelativeLayout;

import java.util.List;


/**
 * 类描述：
 * 创建人：许明达
 * 创建时间：2016/04/14 10:49
 * 修改备注：
 */
public class VideoLableAdapter extends BaseAdapter{

    private String TAG="ClientListAdapter";
    private List<VideoLableBean> lableResponseList;
    private Context mContext;
    private CommentImageAdapter commentImageAdapter;
    public VideoLableAdapter(Context context, List<VideoLableBean> lableResponseList){
        mContext=context;
        this.lableResponseList=lableResponseList;
    }
    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return lableResponseList.get(arg0);
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
            view= LayoutInflater.from(mContext).inflate(R.layout.video_lable_item, parent,false);
            vh=new ViewHolder();
            vh.tv_lable = (TextView) view.findViewById(R.id.tv_lable);
            view.setTag(vh);
        }else{
            vh=(ViewHolder)view.getTag();
        }
        VideoLableBean videoLableBean = lableResponseList.get(pos);
        vh.tv_lable.setText(videoLableBean.getTag_name());
//        if(videoLableBean.ischecked()){
//            vh.tv_lable.setBackgroundResource(R.drawable.bt_bg_editor);
//            vh.tv_lable.setTextColor(mContext.getResources().getColor(R.color.white));
//        }else{
//            vh.tv_lable.setBackgroundResource(R.drawable.bg_label);
//            vh.tv_lable.setTextColor(mContext.getResources().getColor(R.color.text_color_black));
//        }

        return view;

    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return lableResponseList.size();
    }



    class ViewHolder{
        TextView tv_lable;
    }
}
