package com.tesu.manicurehouse.adapter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.tesu.manicurehouse.R;
import com.tesu.manicurehouse.activity.ClassifyActivity;
import com.tesu.manicurehouse.activity.GoodInfoActivity;
import com.tesu.manicurehouse.activity.LogisticsActivity;
import com.tesu.manicurehouse.activity.OfficialEventsActivity;
import com.tesu.manicurehouse.activity.OfficialEventsInfoActivity;
import com.tesu.manicurehouse.activity.SearchActivity;
import com.tesu.manicurehouse.activity.VideoInfoActivity;
import com.tesu.manicurehouse.activity.VideoShowInfoActivity;
import com.tesu.manicurehouse.activity.VideoShowPhotoInfoActivity;
import com.tesu.manicurehouse.base.MyVolley;
import com.tesu.manicurehouse.base.ViewTabBasePager;
import com.tesu.manicurehouse.bean.GoodsAttrBean;
import com.tesu.manicurehouse.bean.ShareDateBean;
import com.tesu.manicurehouse.bean.ShowPhotoContentBean;
import com.tesu.manicurehouse.bean.VideoInfoBean;
import com.tesu.manicurehouse.bean.VideoListBean;
import com.tesu.manicurehouse.protocol.GetAdsProtocol;
import com.tesu.manicurehouse.request.GetAdsRequest;
import com.tesu.manicurehouse.response.GetAdsResponse;
import com.tesu.manicurehouse.response.VideoInfoResponse;
import com.tesu.manicurehouse.response.VideoListResponse;
import com.tesu.manicurehouse.support.PercentRelativeLayout;
import com.tesu.manicurehouse.tabpager.TabHomePager;
import com.tesu.manicurehouse.util.Constants;
import com.tesu.manicurehouse.util.DialogUtils;
import com.tesu.manicurehouse.util.LogUtils;
import com.tesu.manicurehouse.util.MyVideoThumbLoader;
import com.tesu.manicurehouse.util.NetWorkStatusUtil;
import com.tesu.manicurehouse.util.PictureOption;
import com.tesu.manicurehouse.util.SharedPrefrenceUtils;
import com.tesu.manicurehouse.util.StringUtils;
import com.tesu.manicurehouse.util.UIUtils;
import com.tesu.manicurehouse.widget.NoScrollViewPager;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


/**
 * 类描述：
 * 创建人：许明达
 * 创建时间：2016/04/14 10:49
 * 修改备注：
 */
public class HomeAdapter extends BaseAdapter implements View.OnClickListener {

    private Dialog alertDialog;
    private String TAG = "HomeAdapter";
    private List<VideoListBean> videoListBeanList;
    private Context mContext;
    private Animation animation;
    private ImageLoader imageLoader;
    private MyVideoThumbLoader mVideoThumbLoader;
    //布局类型
    private final int TYPE_ONE = 0, TYPE_TWO = 1, TYPE_COUNT = 2;
    private List<View> dots; // 图片标题正文的那些点
    private int oldPosition = 0;
    private ScheduledExecutorService scheduledExecutorService;
    private int currentItem = 0; // 当前图片的索引号
    private List<ImageView> imageViews; // 滑动的图片集合
    private String url;
    private Dialog loadingDialog;
    private GetAdsResponse getAdsResponse;
    private boolean isloaded;
    private ImageView imageView;
    private VideoListResponse videoListResponse;
    public int type = 1;
    boolean isopen = false;
    private VideoInfoResponse videoInfoResponse;
    private boolean isHome;
    private Gson gson;
    private int[] images = {R.mipmap.home_logo0, R.mipmap.home_logo1, R.mipmap.home_logo2, R.mipmap.home_logo3, R.mipmap.home_logo4, R.mipmap.home_logo5, R.mipmap.home_logo6, R.mipmap.home_logo7, R.mipmap.home_logo8};
    private int int_type;
    private ChangePageNo changePageNo;

    public HomeAdapter(Context context, List<VideoListBean> videoListBeanList, Dialog loadingDialog, boolean isHome, int int_type) {
        mContext = context;
        this.loadingDialog = loadingDialog;
        this.videoListBeanList = videoListBeanList;
        this.isHome = isHome;
        animation = AnimationUtils.loadAnimation(mContext, R.anim.anim_scale);

        imageLoader = ImageLoader.getInstance();
        imageLoader.init((ImageLoaderConfiguration.createDefault(mContext)));

        mVideoThumbLoader = new MyVideoThumbLoader();// 初始化缩略图载入方法
        this.int_type = int_type;
        gson = new Gson();
    }

    public void setLoading(boolean isloaded) {
        this.isloaded = isloaded;
    }

    public void setDate(List<VideoListBean> videoListBeanList) {
        this.videoListBeanList = videoListBeanList;
        notifyDataSetChanged();
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

    ViewHolder vh;
    HomeHeadHolder homeHeadHolder;

    @Override
    public View getView(int pos, View view, ViewGroup arg2) {
        // TODO Auto-generated method stub

        // 获取集合内的对象
        int type = getItemViewType(pos);
        if (view == null) {
            switch (type) {
                case TYPE_ONE:
                    view = View.inflate(UIUtils.getContext(),
                            R.layout.home_head_layout, null);
                    homeHeadHolder = new HomeHeadHolder();
                    homeHeadHolder.vp = (ViewPager) view.findViewById(R.id.vp);
                    homeHeadHolder.v_dot0 = (View) view.findViewById(R.id.v_dot0);
                    homeHeadHolder.v_dot1 = (View) view.findViewById(R.id.v_dot1);
                    homeHeadHolder.v_dot2 = (View) view.findViewById(R.id.v_dot2);
                    homeHeadHolder.v_dot3 = (View) view.findViewById(R.id.v_dot3);
                    homeHeadHolder.v_dot4 = (View) view.findViewById(R.id.v_dot4);
                    homeHeadHolder.ll_pager = (LinearLayout) view.findViewById(R.id.ll_pager);
                    homeHeadHolder.tv_search = (TextView) view.findViewById(R.id.tv_search);
                    homeHeadHolder.tv_new = (TextView) view.findViewById(R.id.tv_new);
                    homeHeadHolder.tv_hot = (TextView) view.findViewById(R.id.tv_hot);
                    homeHeadHolder.tv_classify = (TextView) view.findViewById(R.id.tv_classify);
                    view.setTag(homeHeadHolder);
                    break;
                case TYPE_TWO:
                    view = LayoutInflater.from(mContext).inflate(R.layout.home_item, null);
                    vh = new ViewHolder();
                    vh.video_right = (ImageView) view.findViewById(R.id.video_right);
                    vh.video_left = (ImageView) view.findViewById(R.id.video_left);
                    vh.iv_user_image_right = (ImageView) view.findViewById(R.id.iv_user_image_right);
                    vh.iv_user_image_left = (ImageView) view.findViewById(R.id.iv_user_image);
                    vh.rl_rihgt = (PercentRelativeLayout) view.findViewById(R.id.rl_rihgt);
                    vh.tv_title_left = (TextView) view.findViewById(R.id.tv_title_left);
                    vh.tv_play_count_left = (TextView) view.findViewById(R.id.tv_play_count_left);
                    vh.tv_collection_count_left = (TextView) view.findViewById(R.id.tv_collection_count_left);
                    vh.tv_title_right = (TextView) view.findViewById(R.id.tv_title_right);
                    vh.tv_play_count_right = (TextView) view.findViewById(R.id.tv_play_count_right);
                    vh.tv_collection_count_right = (TextView) view.findViewById(R.id.tv_collection_count_right);
                    vh.iv_vido_left = (ImageView) view.findViewById(R.id.iv_vido);
                    vh.iv_vido_right = (ImageView) view.findViewById(R.id.iv_vido_right);
                    view.setTag(vh);
                    break;
            }

        } else {
            switch (type) {
                case TYPE_ONE:
                    homeHeadHolder = (HomeHeadHolder) view.getTag();
                    break;
                case TYPE_TWO:
                    vh = (ViewHolder) view.getTag();
                    break;
            }
        }
        switch (type) {
            case TYPE_ONE:
                if (!isloaded) {
                    runAsyncTask();
                }
                homeHeadHolder.tv_search.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, SearchActivity.class);
                        intent.putExtra("int_type", 0);
                        intent.putExtra("type", 1);
                        intent.putExtra("position", 2);
                        UIUtils.startActivityNextAnim(intent);
                    }
                });
                homeHeadHolder.tv_new.setOnClickListener(this);
                homeHeadHolder.tv_classify.setOnClickListener(this);
                homeHeadHolder.tv_hot.setOnClickListener(this);
                break;
            case TYPE_TWO:
                if (videoListBeanList.size() % 2 == 0) {
                    int left = random();
                    int right = random();
                    vh.rl_rihgt.setVisibility(View.VISIBLE);
                    ClickListener clickListener1 = new ClickListener(pos * 2 + 0, left);
                    ClickListener clickListener = new ClickListener(pos * 2 + 1, right);
                    vh.video_right.setOnClickListener(clickListener);
                    vh.video_left.setOnClickListener(clickListener1);
                    if(!TextUtils.isEmpty(videoListBeanList.get(pos * 2 + 0).getAvatar())){
                        imageLoader.displayImage(videoListBeanList.get(pos * 2 + 0).getAvatar(), vh.iv_user_image_left, PictureOption.getSimpleOptions());
                    }else {
                        vh.iv_user_image_left.setImageResource(images[videoListBeanList.get(pos * 2 + 0).getIndex_()]);
                    }
                    if(!TextUtils.isEmpty(videoListBeanList.get(pos * 2 + 1).getAvatar())){
                        imageLoader.displayImage(videoListBeanList.get(pos * 2 + 1).getAvatar(), vh.iv_user_image_right, PictureOption.getSimpleOptions());
                    }else {
                        vh.iv_user_image_right.setImageResource(images[videoListBeanList.get(pos * 2 + 1).getIndex_()]);
                    }
                } else {
                    if (pos * 2 == videoListBeanList.size() - 1) {
                        int left = random();
                        vh.rl_rihgt.setVisibility(View.INVISIBLE);
                        ClickListener clickListener = new ClickListener(pos * 2 + 0, left);
                        vh.video_left.setOnClickListener(clickListener);
                        if(!TextUtils.isEmpty(videoListBeanList.get(pos * 2 + 0).getAvatar())){
                            imageLoader.displayImage(videoListBeanList.get(pos * 2 + 0).getAvatar(), vh.iv_user_image_left, PictureOption.getSimpleOptions());
                        }else {
                            vh.iv_user_image_left.setImageResource(images[videoListBeanList.get(pos * 2 + 0).getIndex_()]);
                        }
                    } else {
                        int left = random();
                        int right = random();
                        ClickListener clickListener = new ClickListener(pos * 2 + 1, right);
                        ClickListener clickListener1 = new ClickListener(pos * 2 + 0, left);
                        vh.video_right.setOnClickListener(clickListener);
                        vh.video_left.setOnClickListener(clickListener1);
                        vh.rl_rihgt.setVisibility(View.VISIBLE);
                        if(!TextUtils.isEmpty(videoListBeanList.get(pos * 2 + 0).getAvatar())){
                            imageLoader.displayImage(videoListBeanList.get(pos * 2 + 0).getAvatar(), vh.iv_user_image_left, PictureOption.getSimpleOptions());
                        }else {
                            vh.iv_user_image_left.setImageResource(images[videoListBeanList.get(pos * 2 + 0).getIndex_()]);
                        }
                        if(!TextUtils.isEmpty(videoListBeanList.get(pos * 2 + 1).getAvatar())){
                            imageLoader.displayImage(videoListBeanList.get(pos * 2 + 1).getAvatar(), vh.iv_user_image_right, PictureOption.getSimpleOptions());
                        }else {
                            vh.iv_user_image_right.setImageResource(images[videoListBeanList.get(pos * 2 + 1).getIndex_()]);
                        }
                    }
                }
                VideoListBean videoListBeanLeft = videoListBeanList.get(pos * 2 + 0);
                if (videoListBeanLeft != null) {
                    vh.tv_title_left.setText(videoListBeanLeft.getTitle());
                    vh.tv_play_count_left.setText(videoListBeanLeft.getPlay_cnt() + "");
                    vh.tv_collection_count_left.setText(videoListBeanLeft.getCollection_cnt() + "");
                    if (videoListBeanLeft.getType() == 0) {
                        if (!StringUtils.isEmpty(videoListBeanLeft.getCover_img())) {
                            imageLoader.displayImage(videoListBeanLeft.getCover_img(), vh.video_left, PictureOption.getSimpleOptions());
                        } else {
                            String url = videoListBeanLeft.getVideo_url();
                            if (!StringUtils.isEmpty(url)) {
                                vh.video_left.setTag(url);
                                mVideoThumbLoader.showThumbByAsynctack(url, vh.video_left);
                            }
                        }
                    } else if (videoListBeanLeft.getType() == 1) {
                        if (!StringUtils.isEmpty(videoListBeanLeft.getCover_img())) {
                            imageLoader.displayImage(videoListBeanLeft.getCover_img(), vh.video_left, PictureOption.getSimpleOptions());
                        } else {
                            String url = videoListBeanLeft.getVideo_url();
                            if (!StringUtils.isEmpty(url)) {
                                vh.video_left.setTag(url);
                                mVideoThumbLoader.showThumbByAsynctack(url, vh.video_left);
                            }
                        }
                    } else if (videoListBeanLeft.getType() == 2 && !StringUtils.isEmpty(videoListBeanLeft.getPics())) {
                        List<ShowPhotoContentBean> showPhotoContentBeans = gson.fromJson(videoListBeanLeft.getPics(), new TypeToken<List<ShowPhotoContentBean>>() {
                        }.getType());
                        imageLoader.displayImage(showPhotoContentBeans.get(0).getPic1(), vh.video_left, PictureOption.getSimpleOptions());
                    }
                }
                if (pos * 2 + 1 < videoListBeanList.size()) {
                    VideoListBean videoListBeanRight = videoListBeanList.get(pos * 2 + 1);
                    if (videoListBeanRight.getType() == 0) {
                        if (videoListBeanRight != null) {
                            vh.tv_title_right.setText(videoListBeanRight.getTitle());
                            vh.tv_play_count_right.setText(videoListBeanRight.getPlay_cnt() + "");
                            vh.tv_collection_count_right.setText(videoListBeanRight.getCollection_cnt() + "");
                            if (!StringUtils.isEmpty(videoListBeanRight.getCover_img())) {
                                imageLoader.displayImage(videoListBeanRight.getCover_img(), vh.video_right, PictureOption.getSimpleOptions());
                            } else {
                                String url = videoListBeanRight.getVideo_url();
                                if (!StringUtils.isEmpty(url)) {
                                    vh.video_right.setTag(url);
                                    mVideoThumbLoader.showThumbByAsynctack(url, vh.video_right);
                                }
                            }
                        }
                    } else if (videoListBeanRight.getType() == 1) {
                        if (!StringUtils.isEmpty(videoListBeanRight.getCover_img())) {
                            imageLoader.displayImage(videoListBeanRight.getCover_img(), vh.video_left, PictureOption.getSimpleOptions());
                        } else {
                            String url = videoListBeanRight.getVideo_url();
                            if (!StringUtils.isEmpty(url)) {
                                vh.video_left.setTag(url);
                                mVideoThumbLoader.showThumbByAsynctack(url, vh.video_left);
                            }
                        }
                    } else if (videoListBeanRight.getType() == 2 && !StringUtils.isEmpty(videoListBeanRight.getPics())) {
                        List<ShowPhotoContentBean> showPhotoContentBeans = gson.fromJson(videoListBeanRight.getPics(), new TypeToken<List<ShowPhotoContentBean>>() {
                        }.getType());
                        imageLoader.displayImage(showPhotoContentBeans.get(0).getPic1(), vh.video_right, PictureOption.getSimpleOptions());
                    }
                }
//                vh.video_right.setAnimation(animation);
//                vh.video_left.setAnimation(animation);
//                animation.start();
                break;
        }
        return view;
    }

    @Override
    public int getItemViewType(int position) {
        if (isHome) {
            if (position == 0) {
                return TYPE_ONE;
            } else {
                return TYPE_TWO;
            }
        } else {
            return TYPE_TWO;
        }
    }

    @Override
    public int getViewTypeCount() {
        return TYPE_COUNT;
    }

    @Override
    public void onClick(View v) {
        //渐变色
        Shader shader = new LinearGradient(0, 0, 0, 60, Color.rgb(255, 72, 106
        ), Color.rgb(254, 211, 70), Shader.TileMode.CLAMP);
        Shader shader1 = new LinearGradient(0, 0, 0, 0, Color.BLACK, Color.BLACK, Shader.TileMode.CLAMP);
        switch (v.getId()) {

            case R.id.tv_classify:
                Intent intent = new Intent(mContext, ClassifyActivity.class);
                intent.putExtra("postion", 0);
                UIUtils.startActivityNextAnim(intent);
                break;
            case R.id.tv_new: {
                type = 1;
                homeHeadHolder.tv_new.getPaint().setShader(shader);
                homeHeadHolder.tv_new.postInvalidate();
//                homeHeadHolder.tv_classify.getPaint().setShader(shader1);
//                homeHeadHolder.tv_classify.postInvalidate();
                homeHeadHolder.tv_hot.getPaint().setShader(shader1);
                homeHeadHolder.tv_hot.postInvalidate();
                changePageNo.changeNo();
                getViedeoList();
                break;
            }
            case R.id.tv_hot: {
                type = 2;
                homeHeadHolder.tv_hot.getPaint().setShader(shader);
                homeHeadHolder.tv_hot.postInvalidate();
//                homeHeadHolder.tv_classify.getPaint().setShader(shader1);
//                homeHeadHolder.tv_classify.postInvalidate();
                homeHeadHolder.tv_new.getPaint().setShader(shader1);
                homeHeadHolder.tv_new.postInvalidate();
                changePageNo.changeNo();
                getViedeoList();
                break;
            }
        }
    }

    public class HomeHeadHolder {
        ViewPager vp;
        LinearLayout ll_pager;
        View v_dot0;
        View v_dot1;
        View v_dot2;
        View v_dot3;
        View v_dot4;
        TextView tv_search;
        TextView tv_new;
        TextView tv_classify;
        TextView tv_hot;
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
        ImageView iv_vido_left;
        ImageView iv_vido_right;
        ImageView iv_user_image_left;
        ImageView iv_user_image_right;
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
        public int rondom = 0;

        public ClickListener(int position, int rondom) {
            this.position = position;
            this.rondom = rondom;
        }

        @Override
        public void onClick(View v) {
            VideoListBean videoListBean = videoListBeanList.get(position);
            switch (v.getId()) {
                case R.id.video_right: {
                    Intent intent = null;
                    if (videoListBean.getType() == 0) {
                        intent = new Intent(mContext, VideoInfoActivity.class);
                    } else if (videoListBean.getType() == 1) {
                        intent = new Intent(mContext, VideoShowInfoActivity.class);
                    } else {
                        intent = new Intent(mContext, VideoShowPhotoInfoActivity.class);
                    }

                    intent.putExtra("video_id", videoListBean.getId());
                    intent.putExtra("rondom", videoListBeanList.get(position).getIndex_());
                    intent.putExtra("video_name", videoListBean.getTitle());
                    intent.putExtra("cover_img", videoListBean.getCover_img());
                    intent.putExtra("video_url", videoListBean.getVideo_url());
                    UIUtils.startActivityNextAnim(intent);
                    break;
                }
                case R.id.video_left: {
                    Intent intent = null;
                    if (videoListBean.getType() == 0) {
                        intent = new Intent(mContext, VideoInfoActivity.class);
                    } else if (videoListBean.getType() == 1) {
                        intent = new Intent(mContext, VideoShowInfoActivity.class);
                    } else {
                        intent = new Intent(mContext, VideoShowPhotoInfoActivity.class);
                    }
                    intent.putExtra("rondom", videoListBeanList.get(position).getIndex_());
                    intent.putExtra("video_id", videoListBean.getId());
                    intent.putExtra("video_name", videoListBean.getTitle());
                    intent.putExtra("cover_img", videoListBean.getCover_img());
                    intent.putExtra("video_url", videoListBean.getVideo_url());
                    UIUtils.startActivityNextAnim(intent);
                    break;
                }
            }

        }
    }

    /**
     * 填充ViewPager页面的适配器
     *
     * @author Administrator
     */
    private class MyAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            if (getAdsResponse.dataList.size() > 5) {
                return 5;
            } else {
                return getAdsResponse.dataList.size();
            }
        }

        @Override
        public Object instantiateItem(View arg0, int arg1) {
            ((ViewPager) arg0).addView(imageViews.get(arg1));
            return imageViews.get(arg1);
        }

        @Override
        public void destroyItem(View arg0, int arg1, Object arg2) {
            ((ViewPager) arg0).removeView((View) arg2);
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public void restoreState(Parcelable arg0, ClassLoader arg1) {

        }

        @Override
        public Parcelable saveState() {
            return null;
        }

        @Override
        public void startUpdate(View arg0) {

        }

        @Override
        public void finishUpdate(View arg0) {

        }
    }

    private class MyPageChangeListener implements ViewPager.OnPageChangeListener {
        /**
         * This method will be invoked when a new page becomes selected.
         * position: Position index of the new selected page.
         */
        public void onPageSelected(int position) {
//            currentItem = position;
//            dots.get(oldPosition).setBackgroundResource(R.drawable.dot_normal);
//            dots.get(position).setBackgroundResource(R.drawable.dot_focused);
//            oldPosition = position;
            LogUtils.e("dots.size()" + dots.size());
            if (dots.size() > 0) {
                currentItem = position % imageViews.size();
                dots.get(oldPosition).setBackgroundResource(R.drawable.dot_normal);
                dots.get(position % imageViews.size()).setBackgroundResource(R.drawable.dot_focused);
                oldPosition = position % imageViews.size();
            }

        }

        public void onPageScrollStateChanged(int state) {
        }

        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }
    }

    /**
     * 换行切换任务
     */
    private class ScrollTask implements Runnable {

        public void run() {
            synchronized (homeHeadHolder.vp) {
                currentItem = (currentItem + 1) % imageViews.size();
                handler.obtainMessage().sendToTarget(); // 通过Handler切换图片
            }
        }

    }

    // 切换当前显示的图片
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            homeHeadHolder.vp.setCurrentItem(currentItem);// 切换当前显示的图片
        }
    };

    public void startScrollTask() {
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        // 当Activity显示出来后，每两秒钟切换一次图片显示
        scheduledExecutorService.scheduleAtFixedRate(new ScrollTask(), 1, 4, TimeUnit.SECONDS);
    }

    public void runAsyncTask() {
        GetAdsProtocol getAdsProtocol = new GetAdsProtocol();
        GetAdsRequest getAdsRequest = new GetAdsRequest();
        url = getAdsProtocol.getApiFun();
        getAdsRequest.map.put("position_name", "APP首页");


        MyVolley.uploadNoFile(MyVolley.POST, url, getAdsRequest.map, new MyVolley.VolleyCallback() {
            @Override
            public void dealWithJson(String address, String json) {
                Gson gson = new Gson();
                LogUtils.e("json:" + json);
                getAdsResponse = gson.fromJson(json, GetAdsResponse.class);
                if (getAdsResponse.code.equals("0")) {
                    if (getAdsResponse.dataList.size() > 0) {
                        setheadDate(getAdsResponse.dataList);
                    }
                } else {
                    DialogUtils.showAlertDialog(mContext,
                            getAdsResponse.resultText);
                }

            }

            @Override
            public void dealWithError(String address, String error) {
                loadingDialog.dismiss();
                DialogUtils.showAlertDialog(mContext, error);
            }
        });
    }

    public void setheadDate(List<GetAdsResponse.AdsBean> adsBeanList) {
//        Shader shader = new LinearGradient(0, 0, 0, 60, Color.RED, Color.YELLOW, Shader.TileMode.CLAMP);
//        homeHeadHolder.tv_new.getPaint().setShader(shader);
//        homeHeadHolder.tv_new.postInvalidate();

        Shader shader = new LinearGradient(0, 0, 0, 60, Color.rgb(255, 72, 106
        ), Color.rgb(254, 211, 70), Shader.TileMode.CLAMP);
        Shader shader1 = new LinearGradient(0, 0, 0, 0, Color.BLACK, Color.BLACK, Shader.TileMode.CLAMP);
        if(type==1){
            homeHeadHolder.tv_new.getPaint().setShader(shader);
            homeHeadHolder.tv_new.postInvalidate();
            homeHeadHolder.tv_hot.getPaint().setShader(shader1);
            homeHeadHolder.tv_hot.postInvalidate();
        }else {
            homeHeadHolder.tv_hot.getPaint().setShader(shader);
            homeHeadHolder.tv_hot.postInvalidate();
            homeHeadHolder.tv_new.getPaint().setShader(shader1);
            homeHeadHolder.tv_new.postInvalidate();
        }

        isloaded = true;
        imageViews = new ArrayList<ImageView>();

        // 初始化图片资源
        for (int i = 0; i < adsBeanList.size(); i++) {
            if (i < 5) {
                imageView = new ImageView(mContext);
                ViewListener viewListener = new ViewListener(i);
                imageView.setOnClickListener(viewListener);
                ImageLoader.getInstance().displayImage(adsBeanList.get(i).ad_code, imageView, PictureOption.getSimpleOptions());
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageViews.add(imageView);
            }

        }


        dots = new ArrayList<View>();
        if (adsBeanList.size() > 5) {
            dots.add(homeHeadHolder.v_dot0);
            dots.add(homeHeadHolder.v_dot1);
            dots.add(homeHeadHolder.v_dot2);
            dots.add(homeHeadHolder.v_dot3);
            dots.add(homeHeadHolder.v_dot4);
        } else {
            switch (adsBeanList.size()) {
                case 1:
                    homeHeadHolder.v_dot0.setVisibility(View.GONE);
                    homeHeadHolder.v_dot1.setVisibility(View.GONE);
                    homeHeadHolder.v_dot2.setVisibility(View.GONE);
                    homeHeadHolder.v_dot3.setVisibility(View.GONE);
                    homeHeadHolder.v_dot4.setVisibility(View.GONE);
                    break;
                case 2:
                    dots.add(homeHeadHolder.v_dot0);
                    dots.add(homeHeadHolder.v_dot1);
                    homeHeadHolder.v_dot2.setVisibility(View.GONE);
                    homeHeadHolder.v_dot3.setVisibility(View.GONE);
                    homeHeadHolder.v_dot4.setVisibility(View.GONE);
                    break;
                case 3:
                    dots.add(homeHeadHolder.v_dot0);
                    dots.add(homeHeadHolder.v_dot1);
                    dots.add(homeHeadHolder.v_dot2);
                    homeHeadHolder.v_dot3.setVisibility(View.GONE);
                    homeHeadHolder.v_dot4.setVisibility(View.GONE);
                    break;
                case 4:
                    dots.add(homeHeadHolder.v_dot0);
                    dots.add(homeHeadHolder.v_dot1);
                    dots.add(homeHeadHolder.v_dot2);
                    dots.add(homeHeadHolder.v_dot3);
                    homeHeadHolder.v_dot4.setVisibility(View.GONE);
                    break;
                case 5:
                    dots.add(homeHeadHolder.v_dot0);
                    dots.add(homeHeadHolder.v_dot1);
                    dots.add(homeHeadHolder.v_dot2);
                    dots.add(homeHeadHolder.v_dot3);
                    dots.add(homeHeadHolder.v_dot4);
                    break;
                default:
                    break;
            }
        }


        homeHeadHolder.vp.setAdapter(new MyAdapter());// 设置填充ViewPager页面的适配器
        // 设置一个监听器，当ViewPager中的页面改变时调用
        homeHeadHolder.vp.setOnPageChangeListener(new MyPageChangeListener());
        //设置初始选择的页面，要在setadapter之后
        homeHeadHolder.vp.setCurrentItem(currentItem);
        if (!isopen) {
            LogUtils.e("开");
            startScrollTask();
            isopen = true;
        }
    }

    private void getViedeoList() {
        if (NetWorkStatusUtil.getNetWorkStatus(mContext) == 1) {
            loadingDialog.show();
            url = "video/getVideoList.do";
            Map<String, String> params = new HashMap<String, String>();
            params.put("type", String.valueOf(type));
            params.put("position", "0");
            params.put("pageNo", String.valueOf(1));
            params.put("pageSize", "10");
            MyVolley.uploadNoFile(MyVolley.POST, url, params, new MyVolley.VolleyCallback() {

                @Override
                public void dealWithJson(String address, String json) {
                    Gson gson = new Gson();
                    LogUtils.e("获取视频列表4:" + json);
                    loadingDialog.dismiss();
                    videoListResponse = gson.fromJson(json, VideoListResponse.class);
                    if (videoListResponse.getCode() == 0) {

                        videoListBeanList.clear();
                        videoListBeanList.add(null);
                        videoListBeanList.add(null);
                        videoListBeanList.addAll(videoListResponse.getDataList());
                        setDate(videoListBeanList);

                    } else {
                        DialogUtils.showAlertDialog(mContext, videoListResponse.getResultText());
                    }
                }

                @Override
                public void dealWithError(String address, String error) {
                    LogUtils.e("获取视频列表错误:" + error);
                    loadingDialog.dismiss();
                    DialogUtils.showAlertDialog(mContext, error);
                }
            });
        } else {
            alertDialog = DialogUtils.showAlertDialog(mContext, "当前不是wifi状态！", this);
        }
    }

    public int random() {
        Random random = new java.util.Random();// 定义随机类
        int result = random.nextInt(9);// 返回[0,9)集合中的整数，注意不包括9
        return result;
    }

    /**
     * 点击事件
     */
    @SuppressLint("NewApi")
    private class ViewListener implements View.OnClickListener {

        /**
         * 选择的某项
         */
        public int position = 0;

        public ViewListener(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            switch (getAdsResponse.dataList.get(position).type_) {
                case 0: {
                    if (!TextUtils.isEmpty(getAdsResponse.dataList.get(position).ad_link)) {
                        Intent intent = new Intent(mContext, LogisticsActivity.class);
                        intent.putExtra("logistics", getAdsResponse.dataList.get(position).ad_link);
                        intent.putExtra("title", "详情");
                        intent.putExtra("type", 1);
                        UIUtils.startActivityNextAnim(intent);
                    }
                    break;
                }
                case 1: {
                    if (!TextUtils.isEmpty(getAdsResponse.dataList.get(position).ad_link)) {
//                        Intent intent=new Intent(mContext,VideoInfoActivity.class);
//                        intent.putExtra("video_id",Integer.parseInt(getAdsResponse.dataList.get(position).ad_link));
//                        UIUtils.startActivityNextAnim(intent);
                        getVideoInfo(getAdsResponse.dataList.get(position).ad_link);
                    }
                    break;
                }
                case 2: {
                    if (!TextUtils.isEmpty(getAdsResponse.dataList.get(position).ad_link)) {
                        Intent intent = new Intent(mContext, GoodInfoActivity.class);
                        intent.putExtra("goods_id", Integer.parseInt(getAdsResponse.dataList.get(position).ad_link));
                        UIUtils.startActivityNextAnim(intent);
                    }
                    break;
                }
                case 3: {
                    if (!TextUtils.isEmpty(getAdsResponse.dataList.get(position).ad_link)) {
                        Intent intent = new Intent(mContext, OfficialEventsInfoActivity.class);
                        intent.putExtra("act_id", getAdsResponse.dataList.get(position).ad_link);
                        UIUtils.startActivityNextAnim(intent);
                    }
                    break;
                }
            }
            LogUtils.e("广告:" + getAdsResponse.dataList.get(position).ad_link);
        }
    }

    /**
     * 获取视频详情
     */
    private void getVideoInfo(final String video_id) {
        loadingDialog.show();
        url = "video/getVideoDesc.do";
        Map<String, String> params = new HashMap<String, String>();
        params.put("video_id", video_id + "");
        params.put("user_id", SharedPrefrenceUtils.getString(mContext, "user_id"));
        MyVolley.uploadNoFile(MyVolley.POST, url, params, new MyVolley.VolleyCallback() {

            @Override
            public void dealWithJson(String address, String json) {
                loadingDialog.dismiss();
                LogUtils.e("获取视频详情1:" + json);
                Gson gson = new Gson();
                videoInfoResponse = gson.fromJson(json, VideoInfoResponse.class);
                if (videoInfoResponse.getCode() == 0) {
                    try {
                        VideoInfoBean videoInfoBean = videoInfoResponse.getData();
                        Intent intent = new Intent(mContext, VideoInfoActivity.class);
                        int videoId = Integer.valueOf(video_id);
                        intent.putExtra("video_id", videoId);
//                    intent.putExtra("rondom","0");
                        intent.putExtra("video_name", videoInfoBean.getTitle());
                        intent.putExtra("cover_img", videoInfoBean.getCover_img());
                        intent.putExtra("video_url", videoInfoBean.getVideo_url());
                        UIUtils.startActivityNextAnim(intent);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }

                } else {
                    DialogUtils.showAlertDialog(mContext, videoInfoResponse.getResultText());
                }
            }

            @Override
            public void dealWithError(String address, String error) {
                loadingDialog.dismiss();
                LogUtils.e("获取视频详情错误:" + error);
                DialogUtils.showAlertDialog(mContext, error);
            }
        });
    }

    public interface ChangePageNo{
        void changeNo();
    }

    public void setChangeLisenter(ChangePageNo changeLisenter){
        changePageNo = changeLisenter;
    }

}
