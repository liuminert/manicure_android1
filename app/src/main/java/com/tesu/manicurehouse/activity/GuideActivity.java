package com.tesu.manicurehouse.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.TextView;


import com.tesu.manicurehouse.R;
import com.tesu.manicurehouse.adapter.ViewPagerAdapter;
import com.tesu.manicurehouse.base.BaseActivity;
import com.tesu.manicurehouse.util.SharedPrefrenceUtils;

import java.util.ArrayList;

public class GuideActivity extends BaseActivity {
	// 定义ViewPager对象
	private ViewPager viewPager;

	// 定义ViewPager适配器
	private ViewPagerAdapter vpAdapter;

	// 定义一个ArrayList来存放View
	private ArrayList<View> views;

	// 定义各个界面View对象
	private View view1, view6;


	// 定义开始按钮对象
	private Button startBt;
	//直接跳转
	private TextView tv_skip_one;
	private TextView tv_skip_two;
	private TextView tv_skip_three;
	private TextView tv_skip_six;

	// 当前的位置索引值
	private int currIndex = 0;


	@Override
	protected View initView() {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		View rootView = View.inflate(this, R.layout.guide_layout, null);
		setContentView(rootView);
		// 实例化ViewPager
		viewPager = (ViewPager) findViewById(R.id.viewpager);
	initData();
		return rootView;
	}

	/**
	 * 初始化数据
	 */
	private void initData() {
		// 实例化各个界面的布局对象

		view1 = this.getLayoutInflater().inflate(R.layout.guide_view01, null);
		view6 = this.getLayoutInflater().inflate(R.layout.guide_view06, null);

		// 实例化ArrayList对象
		views = new ArrayList<View>();
		// 将要分页显示的View装入数组中
		views.add(view1);
		views.add(view6);
		// 实例化ViewPager适配器
		vpAdapter = new ViewPagerAdapter(views);
		tv_skip_one=(TextView)view1.findViewById(R.id.tv_skip_one);
		tv_skip_six=(TextView)view6.findViewById(R.id.tv_skip_six);
		// 实例化开始按钮
		startBt = (Button) view6.findViewById(R.id.startBtn);
		// 设置监听
		viewPager.setOnPageChangeListener(new MyOnPageChangeListener());
		// 设置适配器数据
		viewPager.setAdapter(vpAdapter);

		// 给开始按钮设置监听
		startBt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(GuideActivity.this, MainActivity.class);
				startActivity(intent);
				finish();
				SharedPrefrenceUtils.setBoolean(GuideActivity.this, "isGuided",
						true);
			}
		});
		//直接跳转
		tv_skip_one.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(GuideActivity.this, MainActivity.class);
				startActivity(intent);
				finish();
				SharedPrefrenceUtils.setBoolean(GuideActivity.this, "isGuided",
						true);
			}
		});
		tv_skip_six.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(GuideActivity.this, MainActivity.class);
				startActivity(intent);
				finish();
				SharedPrefrenceUtils.setBoolean(GuideActivity.this, "isGuided",
						true);
			}
		});
		AlphaAnimation alphaAnimation1 = new AlphaAnimation(0.3f, 1.0f);
		alphaAnimation1.setDuration(1000);
		alphaAnimation1.setRepeatCount(Animation.INFINITE);
		alphaAnimation1.setRepeatMode(Animation.REVERSE);
//		startBt.setAnimation(alphaAnimation1);
//		startBt.startAnimation(alphaAnimation1);
//		alphaAnimation1.start();
	}

	public class MyOnPageChangeListener implements OnPageChangeListener {
		@Override
		public void onPageSelected(int position) {
			switch (position) {
			case 0:
				break;
			case 1:
				break;
			case 2:
				break;
			case 3:
				break;
			case 4:
				break;
			case 5:
				break;
			}
			currIndex = position;
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {

		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}
	}
}
