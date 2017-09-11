package com.tesu.manicurehouse.tabpager;

import android.content.Context;
import android.view.View;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.tesu.manicurehouse.R;
import com.tesu.manicurehouse.adapter.HomeAdapter;
import com.tesu.manicurehouse.base.ViewTabBasePager;

import java.util.ArrayList;
import java.util.List;

/**
 * @作者: 许明达
 * @创建时间: 2016-4-27下午5:24:30
 * @版权: 特速版权所有
 * @描述: 分类页面
 * @更新人:
 * @更新时间:
 * @更新内容: TODO
 */
public class HomeClassifyPager extends ViewTabBasePager implements
		PullToRefreshBase.OnRefreshListener {
	protected static final int ERROR = 0;

	protected static final int SUCCESS = 1;

	protected static final int TIME_OUT = 2;
	protected static final int NETWORK_NOT_OPEN = 3;
	protected static final int EMPTY = 4;
	// 0 没有下一页
	private int next;

	/** 关注品牌的页数，1表示第一页 */
	private int currentPage = 1;
	private HomeAdapter homeAdapter;

	public HomeClassifyPager(Context context) {
		super(context);
	}

	@Override
	protected View initView() {
		View view = View.inflate(mContext,
				R.layout.home_classify_pager, null);
		ViewUtils.inject(this, view);
		return view;
	}

	@Override
	public void initData() {
	}


	@Override
	public void onRefresh(PullToRefreshBase refreshView) {

	}
}