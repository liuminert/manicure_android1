package com.tesu.manicurehouse.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.os.Build;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.tesu.manicurehouse.R;
import com.tesu.manicurehouse.activity.SurfaceViewTestActivity;
import com.tesu.manicurehouse.activity.VideoInfoActivity;
import com.tesu.manicurehouse.activity.VideoShowInfoActivity;
import com.tesu.manicurehouse.activity.VideoShowPhotoInfoActivity;
import com.tesu.manicurehouse.bean.ShowPhotoContentBean;
import com.tesu.manicurehouse.bean.VideoListBean;
import com.tesu.manicurehouse.support.PercentRelativeLayout;
import com.tesu.manicurehouse.util.MyVideoThumbLoader;
import com.tesu.manicurehouse.util.PictureOption;
import com.tesu.manicurehouse.util.StringUtils;
import com.tesu.manicurehouse.util.UIUtils;
import com.tesu.manicurehouse.widget.CircleImageView;

import java.util.HashMap;
import java.util.List;


/**
 * 类描述：
 * 创建人：许明达
 * 创建时间：2016/04/14 10:49
 * 修改备注：
 */
public class ShowAdapter extends BaseAdapter{

    private String TAG="ShowAdapter";
    private List<VideoListBean> videoListBeanList;
    private Context mContext;
    private Animation animation;
    private ImageLoader imageLoader;
    private int width;
    private MyVideoThumbLoader mVideoThumbLoader;
    private Gson gson;
    public ShowAdapter(int width,Context context, List<VideoListBean> videoListBeanList){
        mContext=context;
        this.videoListBeanList=videoListBeanList;
        animation = AnimationUtils.loadAnimation(mContext, R.anim.anim_scale);

        imageLoader = ImageLoader.getInstance();
        imageLoader.init((ImageLoaderConfiguration.createDefault(mContext)));

        this.width = width;
        mVideoThumbLoader = new MyVideoThumbLoader();// 初始化缩略图载入方法
        gson = new Gson();
    }

    public void setDate(List<VideoListBean> videoListBeanList){
        this.videoListBeanList=videoListBeanList;
    }
    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return videoListBeanList.get(arg0);
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
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.show_item, null);
            vh = new ViewHolder();
            vh.video_right = (ImageView) view.findViewById(R.id.video_right);
            vh.video_left = (ImageView) view.findViewById(R.id.video_left);
            vh.rl_rihgt = (PercentRelativeLayout) view.findViewById(R.id.rl_rihgt);
            vh.tv_title_left = (TextView) view.findViewById(R.id.tv_title_left);
            vh.tv_play_count_left = (TextView) view.findViewById(R.id.tv_play_count_left);
            vh.tv_collection_count_left = (TextView) view.findViewById(R.id.tv_collection_count_left);
            vh.tv_title_right = (TextView) view.findViewById(R.id.tv_title_right);
            vh.tv_play_count_right = (TextView) view.findViewById(R.id.tv_play_count_right);
            vh.tv_collection_count_right = (TextView) view.findViewById(R.id.tv_collection_count_right);
            vh.iv_vido_left = (CircleImageView) view.findViewById(R.id.iv_vido);
            vh.iv_vido_right = (CircleImageView) view.findViewById(R.id.iv_vido_right);
            vh.tv_isfee_left = (TextView) view.findViewById(R.id.tv_isfee_left);
            vh.tv_isfee_right = (TextView) view.findViewById(R.id.tv_isfee_right);
            vh.iv_video_play_left = (ImageView) view.findViewById(R.id.iv_video_play_left);
            vh.iv_video_play_right = (ImageView) view.findViewById(R.id.iv_video_play_right);
            vh.iv_is_free_left = (ImageView) view.findViewById(R.id.iv_is_free_left);
            vh.iv_is_free_right = (ImageView) view.findViewById(R.id.iv_is_free_right);
            view.setTag(vh);
        } else {
            vh = (ViewHolder) view.getTag();
        }
        if (videoListBeanList.size() % 2 == 0) {
            ClickListener clickListener1 = new ClickListener(pos * 2 + 0);
            ClickListener clickListener = new ClickListener(pos * 2 + 1);
            vh.video_right.setOnClickListener(clickListener);
            vh.video_left.setOnClickListener(clickListener1);
        } else {
            if (pos * 2 == videoListBeanList.size() - 1) {
                vh.rl_rihgt.setVisibility(View.INVISIBLE);
                ClickListener clickListener = new ClickListener(pos * 2 + 0);
                vh.video_left.setOnClickListener(clickListener);
            } else {
                ClickListener clickListener = new ClickListener(pos * 2 + 1);
                ClickListener clickListener1 = new ClickListener(pos * 2 + 0);
                vh.video_right.setOnClickListener(clickListener);
                vh.video_left.setOnClickListener(clickListener1);
                vh.rl_rihgt.setVisibility(View.VISIBLE);
            }
        }
//
        VideoListBean videoListBeanLeft = videoListBeanList.get(pos *2 +0);
        String pics = videoListBeanLeft.getPics();
        List<ShowPhotoContentBean> showPhotoContentBeans = null;
        try{
            if(!StringUtils.isEmpty(pics)){
                showPhotoContentBeans = gson.fromJson(pics,new TypeToken<List<ShowPhotoContentBean>>(){}.getType());
            }
        }
        catch (Exception e){

        }

        if(videoListBeanLeft != null){
            vh.tv_title_left.setText(videoListBeanLeft.getTitle());
            vh.tv_play_count_left.setText(videoListBeanLeft.getPlay_cnt()+"");
            vh.tv_collection_count_left.setText(videoListBeanLeft.getCollection_cnt()+"");
            if(videoListBeanLeft.getType()==1){
                if(!StringUtils.isEmpty(videoListBeanLeft.getCover_img())) {
                    imageLoader.displayImage(videoListBeanLeft.getCover_img(), vh.video_left, PictureOption.getSimpleOptions());
                }else {
                    String url = videoListBeanLeft.getVideo_url();
                    if(!StringUtils.isEmpty(url)){
                        vh.video_left.setTag(url);
                        mVideoThumbLoader.showThumbByAsynctack(url, vh.video_left);
                    }
                }
//                vh.iv_vido_left.setImageResource(R.mipmap.but_video);
                vh.iv_video_play_left.setVisibility(View.VISIBLE);

            }else if(videoListBeanLeft.getType()==2 && showPhotoContentBeans != null && !StringUtils.isEmpty(showPhotoContentBeans.get(0).getPic1())){
                imageLoader.displayImage(showPhotoContentBeans.get(0).getPic1(), vh.video_left, PictureOption.getSimpleOptions());
//                vh.iv_vido_left.setImageResource(R.drawable.img_fouce);
                vh.iv_video_play_left.setVisibility(View.GONE);
            }

//            if(!StringUtils.isEmpty(videoListBeanLeft.getAvatar())){
                imageLoader.displayImage(videoListBeanLeft.getAvatar(), vh.iv_vido_left, PictureOption.getSimpleOptions());
//            }
            if(videoListBeanLeft.getIs_fee()==0){
                vh.tv_isfee_left.setText("免费");
                vh.iv_is_free_left.setImageResource(R.drawable.free);
            }else {
                vh.tv_isfee_left.setText("收费");
                vh.iv_is_free_left.setImageResource(R.drawable.unfree);
            }
        }
        if(pos*2 +1 < videoListBeanList.size()){
            VideoListBean videoListBeanRight = videoListBeanList.get(pos *2 +1);

            if(videoListBeanRight != null){
                pics = videoListBeanRight.getPics();
                List<ShowPhotoContentBean> showPhotoContentBeans1 = null;
                if(!StringUtils.isEmpty(pics)){
                    showPhotoContentBeans1 = gson.fromJson(pics,new TypeToken<List<ShowPhotoContentBean>>(){}.getType());
                }
                vh.tv_title_right.setText(videoListBeanRight.getTitle());
                vh.tv_play_count_right.setText(videoListBeanRight.getPlay_cnt()+"");
                vh.tv_collection_count_right.setText(videoListBeanRight.getCollection_cnt()+"");

                if(videoListBeanRight.getType()==1){
                    if(!StringUtils.isEmpty(videoListBeanRight.getCover_img())){
                        imageLoader.displayImage(videoListBeanRight.getCover_img(), vh.video_right, PictureOption.getSimpleOptions());
                    }else {
                        String url = videoListBeanRight.getVideo_url();
                        if(!StringUtils.isEmpty(url)){
                            vh.video_right.setTag(url);
                            mVideoThumbLoader.showThumbByAsynctack(url, vh.video_right);
                        }
                    }
//                    vh.iv_vido_right.setImageResource(R.mipmap.but_video);
                    vh.iv_video_play_right.setVisibility(View.VISIBLE);
                }else if(videoListBeanRight.getType()==2 && showPhotoContentBeans1 != null && !StringUtils.isEmpty(showPhotoContentBeans1.get(0).getPic1())){
                    imageLoader.displayImage(showPhotoContentBeans1.get(0).getPic1(), vh.video_right, PictureOption.getSimpleOptions());
//                    vh.iv_vido_right.setImageResource(R.drawable.img_fouce);
                    vh.iv_video_play_right.setVisibility(View.GONE);
                }

//                if(!StringUtils.isEmpty(videoListBeanRight.getAvatar())){
                    imageLoader.displayImage(videoListBeanRight.getAvatar(), vh.iv_vido_right, PictureOption.getSimpleOptions());
//                }

                if(videoListBeanRight.getIs_fee()==0){
                    vh.tv_isfee_right.setText("免费");
                    vh.iv_is_free_right.setImageResource(R.drawable.free);
                }else {
                    vh.tv_isfee_right.setText("收费");
                    vh.iv_is_free_right.setImageResource(R.drawable.unfree);
                }
            }
        }
//        vh.video_right.setAnimation(animation);
//        vh.video_left.setAnimation(animation);
//        animation.start();
        return view;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return videoListBeanList.size() / 2 + videoListBeanList.size() % 2;
    }


    class ViewHolder {
        ImageView video_right;
        ImageView video_left;
        PercentRelativeLayout rl_rihgt;
        TextView tv_title_left;
        TextView tv_play_count_left;
        TextView tv_collection_count_left;
        TextView tv_title_right;
        TextView tv_play_count_right;
        TextView tv_collection_count_right;
        CircleImageView iv_vido_left;
        CircleImageView iv_vido_right;
        TextView tv_isfee_left;
        TextView tv_isfee_right;
        ImageView iv_video_play_left;
        ImageView iv_video_play_right;
        ImageView iv_is_free_left;
        ImageView iv_is_free_right;
    }


    /**
     * 点击事件
     */
    @SuppressLint("NewApi")
    private class ClickListener implements View.OnClickListener {

        /**
         * 选择的某项
         */
        public int position = 0;

        public ClickListener(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            VideoListBean videoListBean = videoListBeanList.get(position);
            switch (v.getId()) {
                case R.id.video_right: {
                    Intent intent = null;
                    if(videoListBean.getType()==2){
                        intent = new Intent(mContext, VideoShowPhotoInfoActivity.class);
//                        intent = new Intent(mContext, SurfaceViewTestActivity.class);
                    }else {
                        intent = new Intent(mContext, VideoShowInfoActivity.class);
                    }
                    intent.putExtra("video_id",videoListBean.getId());
                    intent.putExtra("cover_img", videoListBean.getCover_img());
                    intent.putExtra("video_url", videoListBean.getVideo_url());
                    UIUtils.startActivityNextAnim(intent);
                    break;
                }
                case R.id.video_left: {
                    Intent intent = null;
                    if(videoListBean.getType()==2){
                        intent = new Intent(mContext, VideoShowPhotoInfoActivity.class);
                    }else {
                        intent = new Intent(mContext, VideoShowInfoActivity.class);
                    }
                    intent.putExtra("video_id",videoListBean.getId());
                    intent.putExtra("cover_img", videoListBean.getCover_img());
                    intent.putExtra("video_url", videoListBean.getVideo_url());
                    UIUtils.startActivityNextAnim(intent);
                    break;
                }
            }

        }
    }
}
