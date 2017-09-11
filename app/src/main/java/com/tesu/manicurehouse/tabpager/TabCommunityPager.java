package com.tesu.manicurehouse.tabpager;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.lidroid.xutils.ViewUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.tesu.manicurehouse.R;
import com.tesu.manicurehouse.activity.GoodInfoActivity;
import com.tesu.manicurehouse.activity.LogisticsActivity;
import com.tesu.manicurehouse.activity.ManicureCommunicationActivity;
import com.tesu.manicurehouse.activity.ManicureInformationActivity;
import com.tesu.manicurehouse.activity.OfficialEventsActivity;
import com.tesu.manicurehouse.activity.OfficialEventsInfoActivity;
import com.tesu.manicurehouse.activity.PersonalCommunicationActivity;
import com.tesu.manicurehouse.activity.SearchActivity;
import com.tesu.manicurehouse.activity.VideoInfoActivity;
import com.tesu.manicurehouse.base.MyVolley;
import com.tesu.manicurehouse.base.TabBasePager;
import com.tesu.manicurehouse.bean.VideoInfoBean;
import com.tesu.manicurehouse.protocol.GetAdsProtocol;
import com.tesu.manicurehouse.protocol.GetGoodDescProtocol;
import com.tesu.manicurehouse.request.GetAdsRequest;
import com.tesu.manicurehouse.request.GetGoodDescRequest;
import com.tesu.manicurehouse.response.GetAdsResponse;
import com.tesu.manicurehouse.response.GetGoodDescResponse;
import com.tesu.manicurehouse.response.VideoInfoResponse;
import com.tesu.manicurehouse.support.PercentRelativeLayout;
import com.tesu.manicurehouse.util.DialogUtils;
import com.tesu.manicurehouse.util.LogUtils;
import com.tesu.manicurehouse.util.PictureOption;
import com.tesu.manicurehouse.util.SharedPrefrenceUtils;
import com.tesu.manicurehouse.util.UIUtils;
import com.tesu.manicurehouse.widget.DragLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @作者: 许明达
 * @创建时间: 2016年3月23日上午11:10:20
 * @版权: 特速版权所有
 * @描述: TODO
 */
public class TabCommunityPager extends TabBasePager implements View.OnClickListener{

    PercentRelativeLayout view;
    LayoutInflater mInflater;
    private ViewPager community_vp;

    private String[] imageResURL; // 图片ID
    private List<View> dots; // 图片标题正文的那些点

    private int currentItem = 0; // 当前图片的索引号
    private List<ImageView> imageViews; // 滑动的图片集合
    private ImageView imageView;
    private View v_dot0;
    private View v_dot1;
    private View v_dot2;
    private View v_dot3;
    private View v_dot4;
    private int oldPosition = 0;
    private ScheduledExecutorService scheduledExecutorService;
    boolean isopen=false;
    boolean isloaded=false;
    private ImageView iv_manicurist_communication;
    private ImageView iv_manicure_information;
    private ImageView iv_personal_communication;
    private ImageView iv_official_events;
    private String url;
    private Dialog loadingDialog;
    private GetAdsResponse getAdsResponse;
    private ImageView iv_search;
    private VideoInfoResponse videoInfoResponse;
    /**
     * @param context
     */
    public TabCommunityPager(Context context, FrameLayout mDragLayout) {
        super(context,mDragLayout);
    }

    @Override
    protected View initView() {
        view = (PercentRelativeLayout) View.inflate(mContext, R.layout.community, null);
        ViewUtils.inject(this, view);
        if (mInflater == null) {
            mInflater = (LayoutInflater) UIUtils.getContext().getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
        }
        community_vp = (ViewPager) view.findViewById(R.id.community_vp);
        v_dot0 = (View) view.findViewById(R.id.v_dot0);
        v_dot1 = (View) view.findViewById(R.id.v_dot1);
        v_dot2 = (View) view.findViewById(R.id.v_dot2);
        v_dot3 = (View) view.findViewById(R.id.v_dot3);
        v_dot4 = (View) view.findViewById(R.id.v_dot4);
        iv_search=(ImageView)view.findViewById(R.id.iv_search);
        iv_manicurist_communication=(ImageView)view.findViewById(R.id.iv_manicurist_communication);
        iv_manicure_information=(ImageView)view.findViewById(R.id.iv_manicure_information);
        iv_personal_communication=(ImageView)view.findViewById(R.id.iv_personal_communication);
        iv_official_events=(ImageView)view.findViewById(R.id.iv_official_events);
        return view;
    }

    @Override
    public void initData() {
        if (!ImageLoader.getInstance().isInited()) {
            ImageLoader.getInstance()
                    .init(ImageLoaderConfiguration.createDefault(UIUtils
                            .getContext()));
        }
        loadingDialog = DialogUtils.createLoadDialog(mContext, true);
        if(!isloaded) {
            runAsyncTask();
        }
        iv_search.setOnClickListener(this);
        iv_manicurist_communication.setOnClickListener(this);
        iv_manicure_information.setOnClickListener(this);
        iv_personal_communication.setOnClickListener(this);
        iv_official_events.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_search: {
                Intent intent=new Intent(mContext, SearchActivity.class);
                intent.putExtra("type",3);
                intent.putExtra("position",1);
                UIUtils.startActivityNextAnim(intent);
                break;
            }
            case R.id.iv_official_events: {
                Intent intent=new Intent(mContext, OfficialEventsActivity.class);
                UIUtils.startActivityNextAnim(intent);
                break;
            }
            case R.id.iv_manicurist_communication: {
                Intent intent=new Intent(mContext, ManicureCommunicationActivity.class);
                UIUtils.startActivityNextAnim(intent);
                break;
            }
            case R.id.iv_personal_communication: {
                Intent intent=new Intent(mContext, PersonalCommunicationActivity.class);
                UIUtils.startActivityNextAnim(intent);
                break;
            }
            case R.id.iv_manicure_information: {
                Intent intent=new Intent(mContext, ManicureInformationActivity.class);
                UIUtils.startActivityNextAnim(intent);
                break;
            }
        }
    }

    private class MyAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return getAdsResponse.dataList.size();
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
            currentItem = position;
            dots.get(oldPosition).setBackgroundResource(R.drawable.dot_normal);
            dots.get(position).setBackgroundResource(R.drawable.dot_focused);
            oldPosition = position;
        }

        public void onPageScrollStateChanged(int arg0) {

        }

        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }
    }

    /**
     * 换行切换任务
     */
    private class ScrollTask implements Runnable {

        public void run() {
            synchronized (community_vp) {
                currentItem = (currentItem + 1) % imageViews.size();
                handler.obtainMessage().sendToTarget(); // 通过Handler切换图片
            }
        }

    }
    // 切换当前显示的图片
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            community_vp.setCurrentItem(currentItem);// 切换当前显示的图片
        }
    };

    public void startScrollTask() {
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        // 当Activity显示出来后，每两秒钟切换一次图片显示
        scheduledExecutorService.scheduleAtFixedRate(new ScrollTask(), 1, 4, TimeUnit.SECONDS);
    }

    public void runAsyncTask() {
        loadingDialog.show();
        GetAdsProtocol getAdsProtocol = new GetAdsProtocol();
        GetAdsRequest getAdsRequest = new GetAdsRequest();
        url = getAdsProtocol.getApiFun();
        getAdsRequest.map.put("position_name","APP社区");



        MyVolley.uploadNoFile(MyVolley.POST, url, getAdsRequest.map, new MyVolley.VolleyCallback() {
            @Override
            public void dealWithJson(String address, String json) {
                LogUtils.e("社区数据:"+json);
                Gson gson = new Gson();
                getAdsResponse = gson.fromJson(json, GetAdsResponse.class);
                if (getAdsResponse.code.equals("0")) {
                    isloaded=true;
                    loadingDialog.dismiss();
                    if(getAdsResponse.dataList.size()>0) {
                        setDate(getAdsResponse.dataList);
                    }
                } else {
                    loadingDialog.dismiss();
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

    public void setDate(List<GetAdsResponse.AdsBean> adsBeanList){

        imageViews = new ArrayList<ImageView>();

        // 初始化图片资源
        for (int i = 0; i < adsBeanList.size(); i++) {
            imageView = new ImageView(mContext);
            ViewListener viewListener=new ViewListener(i);
            imageView.setOnClickListener(viewListener);
            ImageLoader.getInstance().displayImage(adsBeanList.get(i).ad_code, imageView, PictureOption.getSimpleOptions());
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageViews.add(imageView);
        }


        dots = new ArrayList<View>();
        switch (adsBeanList.size()){
            case 1:
                v_dot0.setVisibility(View.GONE);
                v_dot1.setVisibility(View.GONE);
                v_dot2.setVisibility(View.GONE);
                v_dot3.setVisibility(View.GONE);
                v_dot4.setVisibility(View.GONE);
                break;
            case 2:
                dots.add(v_dot0);
                dots.add(v_dot1);
                v_dot2.setVisibility(View.GONE);
                v_dot3.setVisibility(View.GONE);
                v_dot4.setVisibility(View.GONE);
                break;
            case 3:
                dots.add(v_dot0);
                dots.add(v_dot1);
                dots.add(v_dot2);
                v_dot3.setVisibility(View.GONE);
                v_dot4.setVisibility(View.GONE);
                break;
            case 4:
                dots.add(v_dot0);
                dots.add(v_dot1);
                dots.add(v_dot2);
                dots.add(v_dot3);
                v_dot4.setVisibility(View.GONE);
                break;
            case 5:
                dots.add(v_dot0);
                dots.add(v_dot1);
                dots.add(v_dot2);
                dots.add(v_dot3);
                dots.add(v_dot4);
                break;
            default:
                break;
        }



        community_vp.setAdapter(new MyAdapter());// 设置填充ViewPager页面的适配器
        // 设置一个监听器，当ViewPager中的页面改变时调用
        community_vp.setOnPageChangeListener(new MyPageChangeListener());
        //设置初始选择的页面，要在setadapter之后
        community_vp.setCurrentItem(currentItem);
        if(!isopen) {
            startScrollTask();
            isopen=true;
        }
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
            switch (getAdsResponse.dataList.get(position).type_){
                case 0:{
                    if(!TextUtils.isEmpty(getAdsResponse.dataList.get(position).ad_link)){
                        Intent intent=new Intent(mContext,LogisticsActivity.class);
                        intent.putExtra("logistics",getAdsResponse.dataList.get(position).ad_link);
                        intent.putExtra("type",1);
                        UIUtils.startActivityNextAnim(intent);
                    }
                    break;
                }
                case 1:{
                    if(!TextUtils.isEmpty(getAdsResponse.dataList.get(position).ad_link)){
//                        Intent intent=new Intent(mContext,VideoInfoActivity.class);
//                        intent.putExtra("video_id",Integer.parseInt(getAdsResponse.dataList.get(position).ad_link));
//                        UIUtils.startActivityNextAnim(intent);

                        getVideoInfo(getAdsResponse.dataList.get(position).ad_link);
                    }
                    break;
                }
                case 2:{
                    if(!TextUtils.isEmpty(getAdsResponse.dataList.get(position).ad_link)){
                        Intent intent=new Intent(mContext,GoodInfoActivity.class);
                        intent.putExtra("goods_id",Integer.parseInt(getAdsResponse.dataList.get(position).ad_link));
                        UIUtils.startActivityNextAnim(intent);
                    }
                    break;
                }
                case 3:{
                    if(!TextUtils.isEmpty(getAdsResponse.dataList.get(position).ad_link)){
                        Intent intent=new Intent(mContext,OfficialEventsInfoActivity.class);
                        intent.putExtra("act_id",getAdsResponse.dataList.get(position).ad_link);
                        UIUtils.startActivityNextAnim(intent);
                    }
                    break;
                }
            }
            LogUtils.e("广告:"+getAdsResponse.dataList.get(position).ad_link);
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
                Gson gson  = new Gson();
                videoInfoResponse = gson.fromJson(json, VideoInfoResponse.class);
                if (videoInfoResponse.getCode() == 0) {
                    try {
                        VideoInfoBean videoInfoBean = videoInfoResponse.getData();
                        Intent intent=new Intent(mContext,VideoInfoActivity.class);
                        int videoId = Integer.valueOf(video_id);
                        intent.putExtra("video_id",videoId);
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
}
