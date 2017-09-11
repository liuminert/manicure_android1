package com.tesu.manicurehouse.tabpager;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lidroid.xutils.ViewUtils;
import com.tesu.manicurehouse.R;
import com.tesu.manicurehouse.activity.LoginActivity;
import com.tesu.manicurehouse.activity.SearchActivity;
import com.tesu.manicurehouse.activity.ShoppingCarActivity;
import com.tesu.manicurehouse.base.MyVolley;
import com.tesu.manicurehouse.base.TabBasePager;
import com.tesu.manicurehouse.base.ViewTabBasePager;
import com.tesu.manicurehouse.huanxin.HuanXinLoginActivity;
import com.tesu.manicurehouse.protocol.GetCartListProtocol;
import com.tesu.manicurehouse.request.GetCartListRequest;
import com.tesu.manicurehouse.response.GetCartListResponse;
import com.tesu.manicurehouse.util.DialogUtils;
import com.tesu.manicurehouse.util.LogUtils;
import com.tesu.manicurehouse.util.LoginUtils;
import com.tesu.manicurehouse.util.SharedPrefrenceUtils;
import com.tesu.manicurehouse.util.UIUtils;
import com.tesu.manicurehouse.widget.NoScrollViewPager;
import com.tesu.manicurehouse.widget.TabSlidingIndicator;

import java.util.ArrayList;
import java.util.List;

/**
 * @作者: 许明达
 * @创建时间: 2016年3月23日上午11:10:20
 * @版权: 特速版权所有
 * @描述: TODO
 */
public class TabShopPager extends TabBasePager implements
        ViewPager.OnPageChangeListener, View.OnClickListener {

    private String url;
    private Dialog loadingDialog;
    private GetCartListResponse getCartListResponse;
    private RelativeLayout rl_search_brand;
    LinearLayout view;
    LayoutInflater mInflater;
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
    private TextView tv_personal;
    private TextView tv_manicuer;
    private ImageView iv_shopcar;
    private TextView tv_shoping_num;
    private ImageView iv_message;
    private boolean isfirst=true;
    private ImageView iv_red;
    private   PersonalPager personalPager;
    private ManicuristPager manicuristPager;

    /**
     * @param context
     */
    public TabShopPager(Context context, FrameLayout mDragLayout
    ) {
        super(context, mDragLayout);
    }

    @Override
    protected View initView() {
        view = (LinearLayout) View.inflate(mContext, R.layout.shop_pager, null);
        ViewUtils.inject(this, view);
        if (mInflater == null) {
            mInflater = (LayoutInflater) UIUtils.getContext().getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
        }
        iv_red=(ImageView)view.findViewById(R.id.iv_red);
        tv_shoping_num=(TextView)view.findViewById(R.id.tv_shoping_num);
        tv_personal = (TextView) view.findViewById(R.id.tv_personal);
        tv_manicuer = (TextView) view.findViewById(R.id.tv_manicuer);
        titleIndicator = (TabSlidingIndicator) view.findViewById(R.id.indicator_concern_title);
        vpContent = (NoScrollViewPager) view.findViewById(R.id.viewpager_concern_content);
        iv_shopcar=(ImageView)view.findViewById(R.id.iv_shopcar);
        rl_search_brand=(RelativeLayout)view.findViewById(R.id.rl_search_brand);
        iv_message= (ImageView) view.findViewById(R.id.iv_message);
        return view;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void initData() {
        loadingDialog = DialogUtils.createLoadDialog(mContext, true);

        rl_search_brand.setOnClickListener(this);
        iv_shopcar.setOnClickListener(this);
        tv_manicuer.setOnClickListener(this);
        tv_personal.setOnClickListener(this);
        iv_message.setOnClickListener(this);
        pagerTitles = new ArrayList<String>();
        pagerTitles.add("个人");
        pagerTitles.add("美甲师");

        concernBasePagerList = new ArrayList<ViewTabBasePager>();
         personalPager = new PersonalPager(mContext,null,false,loadingDialog,isfirst);
         manicuristPager = new ManicuristPager(mContext,null,false,loadingDialog,isfirst);
        concernBasePagerList.add(personalPager);
        concernBasePagerList.add(manicuristPager);

        ConcernInfoPagerAdapter concerninfopageradapter = new ConcernInfoPagerAdapter();
        vpContent.setAdapter(concerninfopageradapter);

        titleIndicator.setViewPager(vpContent);
        titleIndicator.setOnPageChangeListener(this);
        // 设置指示器缩小部分的比例
        titleIndicator.setScaleRadio(0.0f);
        // 设置indicator的颜色
        titleIndicator.setTextColor(UIUtils.getColor(R.color.black),
                UIUtils.getColor(R.color.LikedColor));
        tv_personal.setTextColor(Color.WHITE);
        tv_personal.setBackground(mContext.getResources().getDrawable(R.drawable.bg_selectable));
        tv_manicuer.setTextColor(Color.BLACK);
        tv_manicuer.setBackground(mContext.getResources().getDrawable(R.drawable.bg_noselectable));
        isfirst=false;
    }

    public void getCarDate(){
        if (SharedPrefrenceUtils.getBoolean(mContext, "isLogin")) {
            runGetCartList();
        }
    }
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if(position==0){
            personalPager.setPageNo(1);
        }
        else{
            manicuristPager.setPageNo(1);
        }
        concernBasePagerList.get(position).initData();
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_search_brand:{
                Intent intent=new Intent(mContext, SearchActivity.class);
                intent.putExtra("type",2);
                intent.putExtra("position",3);
                UIUtils.startActivityNextAnim(intent);
                break;
            }
            case R.id.iv_shopcar:{
                if(LoginUtils.isLogin()) {
                    Intent intent = new Intent(mContext, ShoppingCarActivity.class);
                    UIUtils.startActivityForResultNextAnim(intent, 2);
                }
                else{
                    DialogUtils.showAlertDialog(mContext,
                            "请先登陆您的账号!");
                }
                break;
            }
            case R.id.tv_personal: {

                tv_personal.setTextColor(Color.WHITE);
                tv_personal.setBackground(mContext.getResources().getDrawable(R.drawable.bg_selectable));
                tv_manicuer.setTextColor(Color.BLACK);
                tv_manicuer.setBackground(mContext.getResources().getDrawable(R.drawable.bg_noselectable));
                vpContent.setCurrentItem(0);
                break;
            }
            case R.id.tv_manicuer: {

                tv_manicuer.setTextColor(Color.WHITE);
                tv_manicuer.setBackground(mContext.getResources().getDrawable(R.drawable.bg_selectable));
                tv_personal.setTextColor(Color.BLACK);
                tv_personal.setBackground(mContext.getResources().getDrawable(R.drawable.bg_noselectable));
                vpContent.setCurrentItem(1);
                break;
            }
            case R.id.iv_message:
                Intent intent = new Intent(mContext, HuanXinLoginActivity.class);
                mContext.startActivity(intent);
                break;
        }
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
    public void runGetCartList() {
        GetCartListProtocol getCartListProtocol = new GetCartListProtocol();
        GetCartListRequest getCartListRequest = new GetCartListRequest();
        url = getCartListProtocol.getApiFun();
        getCartListRequest.map.put("user_id", SharedPrefrenceUtils.getString(mContext, "user_id"));
        getCartListRequest.map.put("pageNo", "1");
        getCartListRequest.map.put("pageSize", "100");

        MyVolley.uploadNoFile(MyVolley.POST, url, getCartListRequest.map, new MyVolley.VolleyCallback() {
            @Override
            public void dealWithJson(String address, String json) {

                Gson gson = new Gson();
                getCartListResponse = gson.fromJson(json, GetCartListResponse.class);
                LogUtils.e("sendVerificationCodeResponse:" + json.toString());
                if (getCartListResponse.code.equals("0")) {
                    if(getCartListResponse.dataList.size()>0) {
                        iv_red.setVisibility(View.VISIBLE);
                        tv_shoping_num.setVisibility(View.VISIBLE);
                        tv_shoping_num.setText("" + getCartListResponse.dataList.size());
                    }
                    else{
                        iv_red.setVisibility(View.GONE);
                        tv_shoping_num.setVisibility(View.GONE);
                    }
                } else {
//                    DialogUtils.showAlertDialog(mContext,
//                            getCartListResponse.resultText);
                }


            }

            @Override
            public void dealWithError(String address, String error) {
                DialogUtils.showAlertDialog(mContext, error);
            }
        });
    }
    public void setShopingNum(int num){
            tv_shoping_num.setText(String.valueOf(num));
    }

}
