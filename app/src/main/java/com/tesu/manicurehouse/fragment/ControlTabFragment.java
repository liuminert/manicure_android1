package com.tesu.manicurehouse.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.tesu.manicurehouse.R;
import com.tesu.manicurehouse.activity.LoginActivity;
import com.tesu.manicurehouse.base.BaseFragment;
import com.tesu.manicurehouse.base.TabBasePager;
import com.tesu.manicurehouse.tabpager.TabCommunityPager;
import com.tesu.manicurehouse.tabpager.TabHomePager;
import com.tesu.manicurehouse.tabpager.TabMyselfPager;
import com.tesu.manicurehouse.tabpager.TabShopPager;
import com.tesu.manicurehouse.tabpager.TabShowPager;
import com.tesu.manicurehouse.util.LogUtils;
import com.tesu.manicurehouse.util.LoginUtils;
import com.tesu.manicurehouse.util.SharedPrefrenceUtils;
import com.tesu.manicurehouse.util.UIUtils;
import com.tesu.manicurehouse.widget.DragLayout;
import com.tesu.manicurehouse.widget.MyLinearLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * @作者: 许明达
 * @创建时间: 2015年3月31日下午3:14:18
 * @版权: 微位科技版权所有
 * @描述: 控制侧滑菜单以及 附近 找店 发现 我的四个页面
 */
public class ControlTabFragment extends BaseFragment implements
        OnCheckedChangeListener {

    @ViewInject(R.id.rg_content)
    private RadioGroup mRadioGroup;

    @ViewInject(R.id.rb_content_home)
    private RadioButton rb_content_home;
    @ViewInject(R.id.rb_content_shop)
    private RadioButton rb_content_shop;
    @ViewInject(R.id.rb_content_show)
    private RadioButton rb_content_show;
    @ViewInject(R.id.rb_content_myself)
    private RadioButton rb_content_myself;
    @ViewInject(R.id.rb_content_community)
    private RadioButton rb_content_community;

    // 内容区域
    @ViewInject(R.id.fl_content_fragment)
    private FrameLayout mFrameLayout;
    // 底部区域
    @ViewInject(R.id.fl_bottom)
    private FrameLayout bFrameLayout;
    @ViewInject(R.id.dl)
    private FrameLayout mDragLayout;

    // 处理事件分发的自定义LinearLayout
    @ViewInject(R.id.my_ll)
    private LinearLayout mLinearLayout;
    @ViewInject(R.id.lv)
    private ListView lv;

    // 默认选中第0页面
    public static int mCurrentIndex = 0;
    // 默认选中第0页面
    public static int beforeIndex = 0;
    // 中间变量
    public static int temp = 0;
    // 底部页面的集合
    private List<TabBasePager> mPagerList;
    private TabShowPager tabShowPager;
    private TabHomePager tabHomePager;
    private TabShopPager tabShopPager;
    private TabCommunityPager tabCommunityPager;
    private TabMyselfPager tabMyselfbyPager;
    List<RadioButton> radioButtonList;
    public static boolean isLogin=false;
    @Override
    protected View initView() {
        View view = View.inflate(mActivity, R.layout.control_tab, null);
        // Viewutil工具的注入
        ViewUtils.inject(this, view);
        return view;
    }

    @Override
    protected void initData() {

        // 添加实际的页面
        mPagerList = new ArrayList<TabBasePager>();
        tabHomePager = new TabHomePager(mActivity, mDragLayout);
        mPagerList.add(tabHomePager);
        //商店
        tabShopPager = new TabShopPager(mActivity, mDragLayout);
        mPagerList.add(tabShopPager);
        // 晒美甲
        tabShowPager=new TabShowPager(mActivity, mDragLayout);
        mPagerList.add(tabShowPager);
        tabCommunityPager = new TabCommunityPager(mActivity, mDragLayout);
        mPagerList.add(tabCommunityPager);
        // 我的
        tabMyselfbyPager = new TabMyselfPager(mActivity, mDragLayout);
        mPagerList.add(tabMyselfbyPager);
        // 给RadioGroup 设置监听
        getmRadioGroup().setOnCheckedChangeListener(this);
        if(radioButtonList==null){
            radioButtonList=new ArrayList<RadioButton>();
            radioButtonList.add(rb_content_home);
            radioButtonList.add(rb_content_shop);
            radioButtonList.add(rb_content_show);
            radioButtonList.add(rb_content_community);
            radioButtonList.add(rb_content_myself);
        }
        switchCurrentPage();

    }

    public void setChecked(int item) {
        switch (item) {
            case 0:
                rb_content_home.setChecked(true);

                break;
            case 1:
                rb_content_shop.setChecked(true);
                break;
            case 2:
                rb_content_show.setChecked(true);
                break;
            case 3:
                rb_content_community.setChecked(true);
                break;
            case 4:
                rb_content_myself.setChecked(true);
                break;
            default:
                break;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        // 根据选中的RadioButton的id切换页面
        switch (checkedId) {
            case R.id.rb_content_home:
                beforeIndex=0;
                mCurrentIndex = 0;
                break;
            case R.id.rb_content_shop:
                beforeIndex=1;
                mCurrentIndex = 1;
                break;
            case R.id.rb_content_show:
                beforeIndex=2;
                mCurrentIndex = 2;
                break;
            case R.id.rb_content_community:
                beforeIndex=3;
                mCurrentIndex = 3;
                break;
            case R.id.rb_content_myself:
                mCurrentIndex = 4;
                break;
            default:
                break;
        }
        LogUtils.e("mCurrentIndex:"+mCurrentIndex);
        SharedPrefrenceUtils.setString(mActivity, "currentIndex", String.valueOf(mCurrentIndex));
        switchCurrentPage();
    }

    /**
     * 切换RadioGroup对应的页面
     */
    public void switchCurrentPage() {
        mFrameLayout.removeAllViews();
        TabBasePager tabBasePager = mPagerList.get(mCurrentIndex);
        // 获得每个页面对应的布局
        View rootView = tabBasePager.getRootView();

        // 填充数据
        if (mCurrentIndex == 4) {
            // 判断是否登录 在LoginUtils 工具里面判断
            // 没有登录 登录页面
            TabBasePager tabLoginPager;

            if (LoginUtils.isLogin()) {
                View myselfView = tabBasePager.getRootView();
                tabBasePager.initData();
                mFrameLayout.addView(myselfView);
            } else {
                // 进入登录注册页面
                Intent inent=new Intent(UIUtils.getContext(), LoginActivity.class);
                UIUtils.startActivityNextAnim(inent);
            }

        }
        else {
            tabBasePager.initData();
            mFrameLayout.addView(rootView);
        }
        setSelectedColor(mCurrentIndex);
    }

    public RadioGroup getmRadioGroup() {
        return mRadioGroup;
    }

    public TabShopPager getTabShopPager() {
        return tabShopPager;
    }

    public TabHomePager getTabNearByPager() {
        return tabHomePager;
    }

    public TabMyselfPager getTabMyselfbyPager() {
        return tabMyselfbyPager;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public void setSelectedColor(int item){
        for (int i=0;i<radioButtonList.size();i++){
            RadioButton radioButton=radioButtonList.get(i);
            if(i==item){
                //渐变色
                Shader shader =new LinearGradient(0, 0, 0, 60, Color.rgb(255, 72, 106
                ), Color.rgb(254, 211, 70), Shader.TileMode.CLAMP);
                radioButton.getPaint().setShader(shader);
            }
            else {
                Shader shader =new LinearGradient(0, 0, 0, 0, Color.BLACK, Color.BLACK, Shader.TileMode.CLAMP);
                radioButton.getPaint().setShader(shader);
            }
            radioButton.postInvalidate();
        }
    }

}
