package com.tesu.manicurehouse.tabpager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.tesu.manicurehouse.R;
import com.tesu.manicurehouse.activity.ClassifyActivity;
import com.tesu.manicurehouse.activity.SearchActivity;
import com.tesu.manicurehouse.base.TabBasePager;
import com.tesu.manicurehouse.base.ViewTabBasePager;
import com.tesu.manicurehouse.record.DisplayUtil;
import com.tesu.manicurehouse.record.VideoNewActivity;
import com.tesu.manicurehouse.record.VideoShowPhotoActivity;
import com.tesu.manicurehouse.support.PercentRelativeLayout;
import com.tesu.manicurehouse.util.UIUtils;
import com.tesu.manicurehouse.widget.DragLayout;
import com.tesu.manicurehouse.widget.MyLinearLayout;
import com.tesu.manicurehouse.widget.TabSlidingIndicator;

import java.util.ArrayList;
import java.util.List;

/**
 * @作者: 许明达
 * @创建时间: 2016年3月23日上午11:10:20
 * @版权: 特速版权所有
 * @描述: TODO
 */
public class TabShowPager extends TabBasePager implements
        ViewPager.OnPageChangeListener,View.OnClickListener {
    private static final String TAG = TabShowPager.class.getSimpleName();

    LinearLayout view;
    LayoutInflater mInflater;
    /**
     * 关注的内容viewpager
     */
    private ViewPager vpContent;
    /**
     * 关注标题指示器
     */
//    private TabSlidingIndicator titleIndicator;
    /**
     * 关注标题
     */
    private List<String> pagerTitles;
    /**
     * 存放商场,品牌,喜欢我，我喜欢页面的集合
     */
    private List<ViewTabBasePager> concernBasePagerList;

    private TextView recordTv;
    private TextView show_phote_bt;
    private ImageView video_iv;
    private TextView cancelTv;
    private LinearLayout video_ll;
    private TranslateAnimation mShowAction;
    private TranslateAnimation mHiddenAction;
    private int type;    //全部选项卡，选择的是 全部 0，   收费 1，   免费 2；

    private TextView most_new_tv,most_host_tv,most_class_tv,most_all_tv;
    private ImageView most_new_iv,most_host_iv,most_class_iv,most_all_iv;
    private PercentRelativeLayout most_new_rl,most_host_rl,most_class_rl,most_all_rl;

    private ICallBack iCallBack;
    /** 显示图片的宽 */
    private int width;
    private PercentRelativeLayout prl_search;
    private boolean isFirst = true;

    private ImageView most_all_arrow;

    /**
     * @param context
     */
    public TabShowPager(Context context, FrameLayout mDragLayout) {
        super(context, mDragLayout);
    }

    @Override
    protected View initView() {
        view = (LinearLayout) View.inflate(mContext, R.layout.show_pager, null);
        ViewUtils.inject(this, view);
        if (mInflater == null) {
            mInflater = (LayoutInflater) UIUtils.getContext().getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
        }
//        titleIndicator = (TabSlidingIndicator) view.findViewById(R.id.indicator_concern_title);
        vpContent = (ViewPager) view.findViewById(R.id.viewpager_concern_content);
        prl_search=(PercentRelativeLayout)view.findViewById(R.id.prl_search);
        recordTv = (TextView) view.findViewById(R.id.record_bt);
        show_phote_bt = (TextView) view.findViewById(R.id.show_phote_bt);
        cancelTv = (TextView) view.findViewById(R.id.cancel_bt);
        video_iv = (ImageView) view.findViewById(R.id.video_iv);
        video_ll = (LinearLayout) view.findViewById(R.id.video_ll);

        most_new_tv = (TextView) view.findViewById(R.id.most_new_tv);
        most_host_tv = (TextView) view.findViewById(R.id.most_host_tv);
        most_class_tv = (TextView) view.findViewById(R.id.most_class_tv);
        most_all_tv = (TextView) view.findViewById(R.id.most_all_tv);

        most_new_iv = (ImageView) view.findViewById(R.id.most_new_iv);
        most_host_iv = (ImageView) view.findViewById(R.id.most_host_iv);
        most_class_iv = (ImageView) view.findViewById(R.id.most_class_iv);
        most_all_iv = (ImageView) view.findViewById(R.id.most_all_iv);

        most_all_arrow = (ImageView) view.findViewById(R.id.most_all_arrow);

        most_new_rl = (PercentRelativeLayout) view.findViewById(R.id.most_new_rl);
        most_host_rl = (PercentRelativeLayout) view.findViewById(R.id.most_host_rl);
        most_class_rl = (PercentRelativeLayout) view.findViewById(R.id.most_class_rl);
        most_all_rl = (PercentRelativeLayout) view.findViewById(R.id.most_all_rl);

        width = (((WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getWidth()) / 2;

        return view;
    }

    @Override
    public void initData() {
        pagerTitles = new ArrayList<String>();
        pagerTitles.add("最新");
        pagerTitles.add("热门");
        pagerTitles.add("分类");
        pagerTitles.add("全部");

        type = 0;

        concernBasePagerList = new ArrayList<ViewTabBasePager>();
        MostNewPager mostNewPager = new MostNewPager(width,mContext,isFirst);
        MostHotPager mostHotPager = new MostHotPager(width,mContext,isFirst);
        HomeClassifyPager homeClassifyPager=new HomeClassifyPager(mContext);
//        ChargePager chargePager=new ChargePager(mContext);
        FreePager freePager=new FreePager(width,this,mContext,isFirst);
        concernBasePagerList.add(mostNewPager);
        concernBasePagerList.add(mostHotPager);
        concernBasePagerList.add(homeClassifyPager);
        concernBasePagerList.add(freePager);

        ConcernInfoPagerAdapter concerninfopageradapter = new ConcernInfoPagerAdapter();
        vpContent.setAdapter(concerninfopageradapter);

        concernBasePagerList.get(0).initData();
        vpContent.setCurrentItem(0);
        most_new_tv.setTextColor(UIUtils.getColor(R.color.LikedColor));
        most_new_iv.setVisibility(View.VISIBLE);
        most_host_tv.setTextColor(UIUtils.getColor(R.color.black));
        most_host_iv.setVisibility(View.INVISIBLE);
        most_class_tv.setTextColor(UIUtils.getColor(R.color.black));
        most_class_iv.setVisibility(View.INVISIBLE);
        most_all_tv.setTextColor(UIUtils.getColor(R.color.black));
        most_all_iv.setVisibility(View.INVISIBLE);
//
//        titleIndicator.setViewPager(vpContent);
//        titleIndicator.setOnPageChangeListener(this);
//        // 设置指示器缩小部分的比例
//        titleIndicator.setScaleRadio(0.0f);
//        // 设置indicator的颜色
//        titleIndicator.setTextColor(UIUtils.getColor(R.color.black),
//                UIUtils.getColor(R.color.LikedColor));
        prl_search.setOnClickListener(this);
        recordTv.setOnClickListener(this);
        show_phote_bt.setOnClickListener(this);
        cancelTv.setOnClickListener(this);
        video_iv.setOnClickListener(this);

        most_new_rl.setOnClickListener(this);
        most_host_rl.setOnClickListener(this);
        most_class_rl.setOnClickListener(this);
        most_all_rl.setOnClickListener(this);

        mShowAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        mShowAction.setDuration(500);
        mHiddenAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                0.0f);
        mHiddenAction.setDuration(500);
        isFirst=false;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if(position==2){
            Intent intent=new Intent(mContext, ClassifyActivity.class);
            intent.putExtra("postion",1);
            UIUtils.startActivityNextAnim(intent);
            vpContent.setCurrentItem(0);
        }
        else {
            concernBasePagerList.get(position).initData();
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.prl_search:{
                Intent intent=new Intent(mContext, SearchActivity.class);
                intent.putExtra("type",1);
                intent.putExtra("int_type",1);
                intent.putExtra("position",2);
                UIUtils.startActivityNextAnim(intent);
                break;
            }
            case R.id.record_bt:
                video_ll.startAnimation(mHiddenAction);
                video_ll.setVisibility(View.GONE);
                Intent intent = new Intent(mContext, VideoNewActivity.class);
                mContext.startActivity(intent);
                break;
            case R.id.show_phote_bt:
                video_ll.startAnimation(mHiddenAction);
                video_ll.setVisibility(View.GONE);
                Intent intent1 = new Intent(mContext, VideoShowPhotoActivity.class);
                mContext.startActivity(intent1);
                break;
            case R.id.video_iv:
                if(video_ll.getVisibility() == View.VISIBLE){
                    video_ll.startAnimation(mHiddenAction);
                    video_ll.setVisibility(View.GONE);
                }else {
                    video_ll.startAnimation(mShowAction);
                    video_ll.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.cancel_bt:
                video_ll.startAnimation(mHiddenAction);
                video_ll.setVisibility(View.GONE);
                break;
            case R.id.most_new_rl:
                most_new_tv.setTextColor(UIUtils.getColor(R.color.LikedColor));
                most_new_iv.setVisibility(View.VISIBLE);
                most_host_tv.setTextColor(UIUtils.getColor(R.color.black));
                most_host_iv.setVisibility(View.INVISIBLE);
                most_class_tv.setTextColor(UIUtils.getColor(R.color.black));
                most_class_iv.setVisibility(View.INVISIBLE);
                most_all_tv.setTextColor(UIUtils.getColor(R.color.black));
                most_all_iv.setVisibility(View.INVISIBLE);
                concernBasePagerList.get(0).initData();
                vpContent.setCurrentItem(0);
                most_all_arrow.setImageResource(R.mipmap.down_arrow);
                break;
            case R.id.most_host_rl:
                most_new_tv.setTextColor(UIUtils.getColor(R.color.black));
                most_new_iv.setVisibility(View.INVISIBLE);
                most_host_tv.setTextColor(UIUtils.getColor(R.color.LikedColor));
                most_host_iv.setVisibility(View.VISIBLE);
                most_class_tv.setTextColor(UIUtils.getColor(R.color.black));
                most_class_iv.setVisibility(View.INVISIBLE);
                most_all_tv.setTextColor(UIUtils.getColor(R.color.black));
                most_all_iv.setVisibility(View.INVISIBLE);
                concernBasePagerList.get(1).initData();
                vpContent.setCurrentItem(1);
                most_all_arrow.setImageResource(R.mipmap.down_arrow);
                break;
            case R.id.most_class_rl:
                intent=new Intent(mContext, ClassifyActivity.class);
                intent.putExtra("postion",1);
                UIUtils.startActivityNextAnim(intent);
                vpContent.setCurrentItem(0);
                vpContent.setCurrentItem(0);
                most_new_tv.setTextColor(UIUtils.getColor(R.color.LikedColor));
                most_new_iv.setVisibility(View.VISIBLE);
                most_host_tv.setTextColor(UIUtils.getColor(R.color.black));
                most_host_iv.setVisibility(View.INVISIBLE);
                most_class_tv.setTextColor(UIUtils.getColor(R.color.black));
                most_class_iv.setVisibility(View.INVISIBLE);
                most_all_tv.setTextColor(UIUtils.getColor(R.color.black));
                most_all_iv.setVisibility(View.INVISIBLE);
                most_all_arrow.setImageResource(R.mipmap.down_arrow);
                break;
            case R.id.most_all_rl:
                if(most_all_iv.getVisibility() == View.VISIBLE){
                    switch(type){
                        case 0:
                            most_all_tv.setText("收费");
                            type = 1;
                            break;
                        case 1:
                            most_all_tv.setText("免费");
                            type = 2;
                            break;
                        case 2:
                            most_all_tv.setText("全部");
                            type = 0;
                            break;
                    }
                   changeType();
                }else {
                    most_new_tv.setTextColor(UIUtils.getColor(R.color.black));
                    most_new_iv.setVisibility(View.INVISIBLE);
                    most_host_tv.setTextColor(UIUtils.getColor(R.color.black));
                    most_host_iv.setVisibility(View.INVISIBLE);
                    most_class_tv.setTextColor(UIUtils.getColor(R.color.black));
                    most_class_iv.setVisibility(View.INVISIBLE);
                    most_all_tv.setTextColor(UIUtils.getColor(R.color.LikedColor));
                    most_all_iv.setVisibility(View.VISIBLE);
                    concernBasePagerList.get(3).initData();
                    vpContent.setCurrentItem(3);
                    most_all_arrow.setImageResource(R.mipmap.down_arrow_red);
                }
                break;
        }
    }

    private void changeType() {
        iCallBack.changeType(type);
    }

    private class ConcernInfoPagerAdapter extends PagerAdapter {
        @Override
        public CharSequence getPageTitle(int position) {
            return pagerTitles.get(position);

        }

        @Override
        public int getCount() {
            return pagerTitles.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            ViewTabBasePager concernBasePager = concernBasePagerList
                    .get(position);
            View rootView = concernBasePager.getRootView();

            container.removeView(rootView);
            container.addView(rootView);

            return rootView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

    }

    public interface ICallBack {
        void changeType(int type);
    }

    public void setiCallBack(ICallBack callBack){
        this.iCallBack = callBack;
    }
}
