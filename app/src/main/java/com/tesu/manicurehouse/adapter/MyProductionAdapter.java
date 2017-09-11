package com.tesu.manicurehouse.adapter;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.tesu.manicurehouse.R;
import com.tesu.manicurehouse.bean.CollectionItem;
import com.tesu.manicurehouse.bean.MessageItem;
import com.tesu.manicurehouse.bean.ShowPhotoContentBean;
import com.tesu.manicurehouse.response.GetUserCollectionOrWorkListResponse;
import com.tesu.manicurehouse.util.LoginUtils;
import com.tesu.manicurehouse.util.MyVideoThumbLoader;
import com.tesu.manicurehouse.util.PictureOption;
import com.tesu.manicurehouse.util.StringUtils;
import com.tesu.manicurehouse.widget.SlideView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2016/8/28 0028.
 */
public class MyProductionAdapter extends BaseAdapter{
    private Context mContext;
    List<CollectionItem> collectionItemList;

    private ImageLoader imageLoader;
    private MyVideoThumbLoader mVideoThumbLoader;
    private Gson gson;
    private String [] names={"小达","小红","小名","露丝","卡卡","罗特","蜜语","小桃","果果"};
    private int [] images={R.mipmap.home_logo0,R.mipmap.home_logo1,R.mipmap.home_logo2,R.mipmap.home_logo3,R.mipmap.home_logo4,R.mipmap.home_logo5,R.mipmap.home_logo6,R.mipmap.home_logo7,R.mipmap.home_logo8};

    public MyProductionAdapter(Context context,List<CollectionItem> collectionItemList, SlideView mLastSlideViewWithStatusOn){
        mContext=context;
        this.collectionItemList=collectionItemList;
        imageLoader = ImageLoader.getInstance();
        imageLoader.init((ImageLoaderConfiguration.createDefault(mContext)));
        // 使用DisplayImageOption.Builder()创建DisplayImageOptions

        mVideoThumbLoader = new MyVideoThumbLoader();// 初始化缩略图载入方法
        gson = new Gson();
    }
    @Override
    public int getCount() {
        return collectionItemList.size();
    }

    @Override
    public Object getItem(int position) {
        return collectionItemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView =LayoutInflater.from(mContext).inflate(R.layout.myproduction_list_item, null);
            holder = new ViewHolder();
            holder.iv_uesr_img=(ImageView)convertView.findViewById(R.id.iv_uesr_img);
            holder.iv_big_video=(ImageView)convertView.findViewById(R.id.iv_big_video);
            holder.tv_user_name = (TextView) convertView.findViewById(R.id.tv_user_name);
            holder.tv_date = (TextView) convertView.findViewById(R.id.tv_date);
            holder.iv_vdieo_image = (ImageView) convertView.findViewById(R.id.iv_vdieo_image);
            holder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
            holder.user_like_num = (TextView) convertView.findViewById(R.id.user_like_num);
            holder.user_collection_video = (TextView) convertView.findViewById(R.id.user_collection_video);
            holder.user_share_video = (TextView) convertView.findViewById(R.id.user_share_video);
            holder.iv_like = (ImageView) convertView.findViewById(R.id.iv_like);
            holder.iv_collect = (ImageView) convertView.findViewById(R.id.iv_collect);
            holder.iv_video=(ImageView)convertView.findViewById(R.id.iv_video);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        CollectionItem item = collectionItemList.get(position);

        SimpleDateFormat time = new SimpleDateFormat("yyyy.MM.dd");
        String timeStr = time.format(new Date(item.create_time * 1000));
        holder.tv_date.setText(timeStr);
        if(item.type==2){
            holder.iv_big_video.setVisibility(View.INVISIBLE);
            holder.iv_video.setImageDrawable(mContext.getResources().getDrawable(R.drawable.img_fouce));
            if(!StringUtils.isEmpty(item.pics)){
                List<ShowPhotoContentBean> showPhotoContentBeans = gson.fromJson(item.pics,new TypeToken<List<ShowPhotoContentBean>>(){}.getType());
                if(showPhotoContentBeans != null){
                    imageLoader.displayImage(showPhotoContentBeans.get(0).getPic1(), holder.iv_vdieo_image, PictureOption.getSimpleOptions());
                }
            }
        }else {
            holder.iv_video.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.bt_small_video));
            holder.iv_big_video.setVisibility(View.VISIBLE);
            if(!TextUtils.isEmpty(item.cover_img)){
                imageLoader.displayImage(item.cover_img, holder.iv_vdieo_image, PictureOption.getSimpleOptions());
            }else {
                if(!StringUtils.isEmpty(item.video_url)){
                    holder.iv_vdieo_image.setTag(item.video_url);
                    mVideoThumbLoader.showThumbByAsynctack(item.video_url, holder.iv_vdieo_image);
                }
            }
        }
        if(!StringUtils.isEmpty(item.title)){
            holder.tv_title.setText(item.title);
        }
        holder.user_like_num.setText(item.liked_cnt + "");
        holder.user_collection_video.setText(item.collection_cnt+"");
        holder.user_share_video.setText(item.forward_cnt + "");
        if(item.is_collection==0){
            holder.iv_collect.setImageResource(R.mipmap.bt_collect_noselectable);
        }else {
            holder.iv_collect.setImageResource(R.mipmap.bt_collect_selectable);
        }
        if(item.is_liked==0){
            holder.iv_like.setImageResource(R.mipmap.bt_love_noselectable);
        }else {
            holder.iv_like.setImageResource(R.mipmap.bt_love_selectable);
        }
        if(item.type==0){
            holder.tv_user_name.setText(names[item.index_]);
            holder.iv_uesr_img.setImageResource(images[item.index_]);
        }else{
            if(!StringUtils.isEmpty(item.alias)){
                holder.tv_user_name.setText(item.alias);
            }
            imageLoader.displayImage(item.avatar,holder.iv_uesr_img,PictureOption.getSimpleOptions());
        }
        return convertView;
    }


    private static class ViewHolder {
        ImageView iv_uesr_img;
        public TextView tv_user_name;
        public TextView tv_date;
        public ImageView iv_vdieo_image;
        public TextView tv_title;
        public TextView user_like_num;
        public TextView user_collection_video;
        public TextView user_share_video;
        public ImageView iv_like;
        public ImageView iv_collect;
        public ImageView iv_big_video;
        public ImageView iv_video;
    }
}
