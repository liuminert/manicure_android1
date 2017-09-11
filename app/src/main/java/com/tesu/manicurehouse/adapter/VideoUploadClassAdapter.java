package com.tesu.manicurehouse.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tesu.manicurehouse.R;
import com.tesu.manicurehouse.bean.VideoClassBean;
import com.tesu.manicurehouse.bean.VideoLableBean;
import com.tesu.manicurehouse.support.PercentRelativeLayout;

import java.util.List;


/**
 * 类描述：
 * 创建人：许明达
 * 创建时间：2016/04/14 10:49
 * 修改备注：
 */
public class VideoUploadClassAdapter extends BaseAdapter{

    private String TAG="ClientListAdapter";
    private List<VideoClassBean> videoClassBeanList;
    private Context mContext;
    private CommentImageAdapter commentImageAdapter;
    public VideoUploadClassAdapter(Context context, List<VideoClassBean> videoClassBeanList){
        mContext=context;
        this.videoClassBeanList=videoClassBeanList;
    }
    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return videoClassBeanList.get(arg0);
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
            view= LayoutInflater.from(mContext).inflate(R.layout.video_upload_class_item, parent,false);
            vh=new ViewHolder();
            vh.tv_class = (TextView) view.findViewById(R.id.tv_class);
            view.setTag(vh);
        }else{
            vh=(ViewHolder)view.getTag();
        }
        VideoClassBean videoClassBean = videoClassBeanList.get(pos);
        vh.tv_class.setText(videoClassBean.getCategory_name());
        if(videoClassBean.isChecked()){
            vh.tv_class.setBackgroundResource(R.drawable.bt_bg_editor);
            vh.tv_class.setTextColor(mContext.getResources().getColor(R.color.white));
        }else {
            vh.tv_class.setBackgroundResource(R.drawable.bg_label);
            vh.tv_class.setTextColor(mContext.getResources().getColor(R.color.text_color_black));
        }

        return view;

    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return videoClassBeanList.size();
    }



    class ViewHolder{
        TextView tv_class;
    }
}
