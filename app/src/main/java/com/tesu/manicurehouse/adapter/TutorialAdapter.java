package com.tesu.manicurehouse.adapter;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.tesu.manicurehouse.R;
import com.tesu.manicurehouse.bean.ShowPhotoContentBean;
import com.tesu.manicurehouse.response.GetDesignListResponse;
import com.tesu.manicurehouse.util.DateUtils;
import com.tesu.manicurehouse.util.MyVideoThumbLoader;
import com.tesu.manicurehouse.util.PictureOption;
import com.tesu.manicurehouse.util.StringUtils;
import com.tesu.manicurehouse.util.UIUtils;

import java.util.List;

//教程，包括到账和未到账的
public class TutorialAdapter extends BaseAdapter {

    private String TAG = "ClientListAdapter";
    private List<GetDesignListResponse.DesignBean> dataList;
    private Context mContext;
    private MyVideoThumbLoader mVideoThumbLoader;
    private Gson gson;
    public TutorialAdapter(Context context, List<GetDesignListResponse.DesignBean> dataList) {
        mContext = context;
        this.dataList = dataList;
        if (!ImageLoader.getInstance().isInited()) {
            ImageLoader.getInstance()
                    .init(ImageLoaderConfiguration.createDefault(UIUtils
                            .getContext()));
        }
        gson=new Gson();
        mVideoThumbLoader = new MyVideoThumbLoader();// 初始化缩略图载入方法
    }

    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return dataList.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return 0;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public View getView(int pos, View view, ViewGroup arg2) {
        // TODO Auto-generated method stub
        ViewHolder vh;
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.tutorial_item, null);
            vh = new ViewHolder();
            vh.tv_tutorial_name=(TextView)view.findViewById(R.id.tv_tutorial_name);
            vh.tv_tutorial_like_num=(TextView)view.findViewById(R.id.tv_tutorial_like_num);
            vh.iv_tutorial=(ImageView)view.findViewById(R.id.iv_tutorial);
            vh.tv_tutorial_time=(TextView)view.findViewById(R.id.tv_tutorial_time);
            view.setTag(vh);
        } else {
            vh = (ViewHolder) view.getTag();
        }
        vh.tv_tutorial_name.setText(dataList.get(pos).title);
        vh.tv_tutorial_like_num.setText("被采纳" + dataList.get(pos).designCnt + "次");

        if(dataList.get(pos).type==1){
            if(!TextUtils.isEmpty(dataList.get(pos).cover_img)){
                ImageLoader.getInstance().displayImage(dataList.get(pos).cover_img,vh.iv_tutorial, PictureOption.getSimpleOptions());
            }else{
                String url = dataList.get(pos).video_url;
                if(!TextUtils.isEmpty(url)){
                    vh.iv_tutorial.setTag(url);
                    mVideoThumbLoader.showThumbByAsynctack(url, vh.iv_tutorial);
                }
            }
        }
        else{
            List<ShowPhotoContentBean> pics=gson.fromJson(dataList.get(pos).pics, new TypeToken<List<ShowPhotoContentBean>>(){}.getType());
            if(pics.size()>0){
                ImageLoader.getInstance().displayImage(pics.get(0).getPic1(),vh.iv_tutorial, PictureOption.getSimpleOptions());
            }
            else {
                vh.iv_tutorial.setBackgroundColor(mContext.getResources().getColor(R.color.text_color_black));
            }
        }
        vh.tv_tutorial_time.setText(DateUtils.milliToSimpleDateTime1(dataList.get(pos).create_time*1000));
        return view;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return dataList.size();
    }


    class ViewHolder {
        TextView tv_tutorial_name;
        TextView tv_tutorial_like_num;
        TextView tv_tutorial_time;
        ImageView iv_tutorial;
    }
    /**
     * 点击事件
     */
    @SuppressLint("NewApi")
    private class ImageViewListener implements View.OnClickListener {

        /**
         * 选择的某项
         */
        public int position = 0;
        public ImageViewListener(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
        }
    }

}
