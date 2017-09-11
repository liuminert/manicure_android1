package com.tesu.manicurehouse.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.tesu.manicurehouse.R;
import com.tesu.manicurehouse.response.GetUserCommentListResponse;
import com.tesu.manicurehouse.util.MyVideoThumbLoader;
import com.tesu.manicurehouse.util.PictureOption;
import com.tesu.manicurehouse.util.StringUtils;
import com.tesu.manicurehouse.util.UIUtils;
import com.tesu.manicurehouse.util.Utility;
import com.tesu.manicurehouse.widget.CollapsibleTextView;
import com.tesu.manicurehouse.widget.MyListView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * 类描述：
 * 创建人：许明达
 * 创建时间：2016/04/14 10:49
 * 修改备注：
 */
public class MyCommentAdapter extends BaseAdapter{

    private String TAG="ClientListAdapter";
    private List<GetUserCommentListResponse.DataList> dataLists;
    private Context mContext;
    private MyVideoThumbLoader mVideoThumbLoader;
    private Gson gson= new Gson();
    public MyCommentAdapter(Context context,List<GetUserCommentListResponse.DataList> dataLists){
        mContext=context;
        this.dataLists=dataLists;
        if (!ImageLoader.getInstance().isInited()) {
            ImageLoader.getInstance()
                    .init(ImageLoaderConfiguration.createDefault(UIUtils
                            .getContext()));
        }
        mVideoThumbLoader = new MyVideoThumbLoader();// 初始化缩略图载入方法
    }
    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return dataLists.get(arg0);
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
            view= LayoutInflater.from(mContext).inflate(R.layout.my_comment_item, null);
            vh=new ViewHolder();
            vh.tv_user_comment=(CollapsibleTextView)view.findViewById(R.id.tv_user_comment);
            vh.iv_value_url=(ImageView)view.findViewById(R.id.iv_value_url);
            vh.tv_comment_time=(TextView)view.findViewById(R.id.tv_comment_time);
            vh.tv_title=(TextView)view.findViewById(R.id.tv_title);
            vh.tv_user_name=(TextView)view.findViewById(R.id.tv_user_name);
            view.setTag(vh);
        }else{
            vh=(ViewHolder)view.getTag();
        }
        vh.tv_user_comment.setDesc("评价内容:" + dataLists.get(pos).reply_content, TextView.BufferType.NORMAL);
//        switch (dataLists.get(pos).type){
//            case 1:
//                if(!StringUtils.isEmpty(dataLists.get(pos).value_url)){
////                    vh.iv_value_url.setTag(dataLists.get(pos).value_url);
////                    mVideoThumbLoader.showThumbByAsynctack(dataLists.get(pos).value_url, vh.iv_value_url);
//                    ImageLoader.getInstance().displayImage(dataLists.get(pos).value_url, vh.iv_value_url, PictureOption.getSimpleOptions());
//                }else {
//                    vh.iv_value_url.setImageResource(R.mipmap.pic_video);
//                }
//                break;
//            case 2:
//                ImageLoader.getInstance().displayImage(dataLists.get(pos).value_url, vh.iv_value_url, PictureOption.getSimpleOptions());
//                break;
//            case 0:
//                String urls = dataLists.get(pos).value_url;
//                if(!TextUtils.isEmpty(urls)){
//                    List<String> strs = gson.fromJson(urls,new TypeToken<List<String>>(){}.getType());
//                    if(strs != null && strs.size()>0){
//                        ImageLoader.getInstance().displayImage(strs.get(0), vh.iv_value_url, PictureOption.getSimpleOptions());
//                    }
//                }
//                else {
//                    vh.iv_value_url.setImageResource(R.mipmap.pic_video);
//                }
//                break;
//        }
        String urls = dataLists.get(pos).pic_url;
        if(!TextUtils.isEmpty(urls)){
                ImageLoader.getInstance().displayImage(urls, vh.iv_value_url, PictureOption.getSimpleOptions());
        }
        else {
            vh.iv_value_url.setImageResource(R.mipmap.pic_video);
        }
        vh. tv_user_name.setText(dataLists.get(pos).alias);
        String timeStr = dataLists.get(pos).comment_time;
        SimpleDateFormat format=new SimpleDateFormat("yyyy MM dd HH:mm:ss");
        try {
            long time = Long.valueOf(timeStr);
            timeStr = format.format(new Date(time*1000));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        vh.tv_comment_time.setText(timeStr);
        vh.tv_title.setText(dataLists.get(pos).title);
        return view;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return dataLists.size();
    }


    class ViewHolder{
        CollapsibleTextView tv_user_comment;
        ImageView iv_value_url;
        TextView tv_comment_time;
        TextView tv_title;
        TextView tv_user_name;
    }
}
