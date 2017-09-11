package com.tesu.manicurehouse.activity;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.tesu.manicurehouse.R;
import com.tesu.manicurehouse.base.BaseActivity;
import com.tesu.manicurehouse.base.ViewTabBasePager;
import com.tesu.manicurehouse.response.SearchVideoResponse;
import com.tesu.manicurehouse.tabpager.HomeClassifyPager;
import com.tesu.manicurehouse.tabpager.HomeMostHotPager;
import com.tesu.manicurehouse.tabpager.HomeMostNewPager;
import com.tesu.manicurehouse.tabpager.ManicuristPager;
import com.tesu.manicurehouse.tabpager.PersonalPager;
import com.tesu.manicurehouse.util.DialogUtils;
import com.tesu.manicurehouse.util.UIUtils;
import com.tesu.manicurehouse.widget.NoScrollViewPager;
import com.tesu.manicurehouse.widget.TabSlidingIndicator;

import java.util.ArrayList;
import java.util.List;

/**
 * 类描述：
 * 创建人：许明达
 * 创建时间：2016/8/16 11:40
 * 搜索教程页面
 */
public class SearchVideoActivity extends BaseActivity implements View.OnClickListener ,
        ViewPager.OnPageChangeListener{

    private TextView tv_top_back;
    private View rootView;
    /**
     * 关注的内容viewpager
     */
    private NoScrollViewPager vpContent;
    /**
     * 关注标题指示器
     */
    private TabSlidingIndicator titleIndicator;
    /**
     * 关注标题
     */
    private List<String> pagerTitles;
    /**
     * 存放商场,品牌,喜欢我，我喜欢页面的集合
     */
    private List<ViewTabBasePager> concernBasePagerList;
    private String keyword;
    private Dialog loadingDialog;
    private TextView tv_title;
    //1代表首页,2代表晒美甲
    private int int_type;
    protected View initView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        rootView = View.inflate(this, R.layout.activity_search_video, null);
        setContentView(rootView);
        tv_title= (TextView) rootView.findViewById(R.id.tv_title);
        tv_top_back = (TextView) rootView.findViewById(R.id.tv_top_back);
        titleIndicator = (TabSlidingIndicator) rootView.findViewById(R.id.indicator_concern_title);
        vpContent = (NoScrollViewPager) rootView.findViewById(R.id.viewpager_concern_content);
        initData();
        return null;
    }


    public void initData() {
        loadingDialog = DialogUtils.createLoadDialog(SearchVideoActivity.this, true);
        Intent intent = getIntent();
        keyword =  intent.getStringExtra("keyword");
        tv_top_back.setOnClickListener(this);
        int_type=intent.getIntExtra("int_type", -1);
        tv_title.setText(keyword);
        pagerTitles = new ArrayList<String>();
        pagerTitles.add("最新");
        pagerTitles.add("热门");

        concernBasePagerList = new ArrayList<ViewTabBasePager>();
        HomeMostNewPager homeMostNewPager = new HomeMostNewPager(SearchVideoActivity.this,null,true,keyword,loadingDialog,true,null,null,null,int_type);
        HomeMostHotPager homeMostHotPager = new HomeMostHotPager(SearchVideoActivity.this,null,true,keyword,loadingDialog,true,null,null,null,int_type);

        concernBasePagerList.add(homeMostNewPager);
        concernBasePagerList.add(homeMostHotPager);

        ConcernInfoPagerAdapter concerninfopageradapter = new ConcernInfoPagerAdapter();
        vpContent.setAdapter(concerninfopageradapter);

        titleIndicator.setViewPager(vpContent);
        titleIndicator.setOnPageChangeListener(this);
        // 设置指示器缩小部分的比例
        titleIndicator.setScaleRadio(0.0f);
        // 设置indicator的颜色
        titleIndicator.setTextColor(UIUtils.getColor(R.color.black),
                UIUtils.getColor(R.color.LikedColor));
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        concernBasePagerList.get(position).initData();
    }

    @Override
    public void onPageScrollStateChanged(int state) {

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
        public Object instantiateItem(ViewGroup container, int position) {
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
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_top_back:
                finish();
                overridePendingTransition(R.anim.animprv_in, R.anim.animprv_out);
                break;
        }
    }

}
