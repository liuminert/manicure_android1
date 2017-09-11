package com.tesu.manicurehouse.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.tesu.manicurehouse.R;
import com.tesu.manicurehouse.bean.AudioBean;
import com.tesu.manicurehouse.record.VideoBean;
import com.tesu.manicurehouse.support.PercentRelativeLayout;

import java.util.List;


/**
 * 类描述：
 * 创建人：许明达
 * 创建时间：2016/04/14 10:49
 * 修改备注：
 */
public class VideoAddMusicAdapter extends BaseAdapter{

    private String TAG="ClientListAdapter";
    private List<AudioBean> AudioList;
    private Context mContext;
    private CommentImageAdapter commentImageAdapter;
    public VideoAddMusicAdapter(Context context, List<AudioBean> AudioList){
        mContext=context;
        this.AudioList=AudioList;
    }
    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return AudioList.get(arg0);
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
            view= LayoutInflater.from(mContext).inflate(R.layout.add_music_item, parent,false);
            vh=new ViewHolder();
            vh.music_name_tv = (TextView) view.findViewById(R.id.music_name_tv);
            vh.is_checked_iv = (ImageView) view.findViewById(R.id.is_checked_iv);
            view.setTag(vh);
        }else{
            vh=(ViewHolder)view.getTag();
        }

        AudioBean videoBean = AudioList.get(pos);
        if(videoBean.isChecked()){
            vh.is_checked_iv.setImageResource(R.drawable.ic_checkbox_selected);
        }else {
            vh.is_checked_iv.setImageResource(R.drawable.ic_checkbox_unselected);
        }
        vh.music_name_tv.setText(videoBean.getAudio_name());


        return view;

    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return AudioList.size();
    }



    class ViewHolder{
        TextView music_name_tv;
        ImageView is_checked_iv;
    }
}
