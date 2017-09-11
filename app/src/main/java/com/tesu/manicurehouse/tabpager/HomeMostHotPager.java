package com.tesu.manicurehouse.tabpager;

import android.app.Dialog;
import android.content.Context;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.tesu.manicurehouse.R;
import com.tesu.manicurehouse.adapter.HomeAdapter;
import com.tesu.manicurehouse.base.MyVolley;
import com.tesu.manicurehouse.base.ViewTabBasePager;
import com.tesu.manicurehouse.bean.VideoListBean;
import com.tesu.manicurehouse.protocol.SearchVideoProtocol;
import com.tesu.manicurehouse.request.SearchVideoRequest;
import com.tesu.manicurehouse.response.SearchVideoResponse;
import com.tesu.manicurehouse.response.VideoListResponse;
import com.tesu.manicurehouse.support.PercentRelativeLayout;
import com.tesu.manicurehouse.util.DialogUtils;
import com.tesu.manicurehouse.util.LogUtils;
import com.tesu.manicurehouse.util.NetWorkStatusUtil;
import com.tesu.manicurehouse.util.SharedPrefrenceUtils;
import com.tesu.manicurehouse.util.UIUtils;
import com.tesu.manicurehouse.widget.RefreshListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @作者: 许明达
 * @创建时间: 2016-4-27下午5:24:30
 * @版权: 特速版权所有
 * @描述: 最热门的品牌页面
 * @更新人:
 * @更新时间:
 * @更新内容: TODO
 */
public class HomeMostHotPager extends ViewTabBasePager implements
		PullToRefreshBase.OnRefreshListener,View.OnClickListener{
	// 分类listview
	@ViewInject(R.id.lv_home_hot)
	private PullToRefreshListView lv_home_hot;
	private HomeAdapter homeAdapter;

	private Dialog loadingDialog;
	private String url;
	private VideoListResponse videoListResponse;
	private Gson gson;
	private List<VideoListBean> videoListBeanList;
	private SwipeRefreshLayout swipeRefreshLayout;
	private  boolean isSearch;
	private String keyword;
	//判断是否刷新
	private boolean isRefresh=false;
	private int pageNo=1;
	private boolean isloading=false;
	private Dialog alertDialog;
	private boolean isfirst;
	private ViewPager vp;
	private LinearLayout ll_pager;
	private PercentRelativeLayout prl_head;
	private boolean scrollFlag;
	private int lastVisibleItemPosition;// 标记上次滑动位置
	private int int_type;
	public HomeMostHotPager(Context context,SwipeRefreshLayout swipeRefreshLayout,boolean isSearch,String keyword,Dialog loadingDialog,boolean isfirst,ViewPager vp, LinearLayout ll_pager,PercentRelativeLayout prl_head,int int_type) {
		super(context);
		this.swipeRefreshLayout=swipeRefreshLayout;
		this.isSearch=isSearch;
		this.keyword=keyword;
		this.loadingDialog=loadingDialog;
		this.isfirst=isfirst;
		this.vp=vp;
		this.ll_pager=ll_pager;
		this.prl_head=prl_head;
		this.int_type=int_type;
	}

	@Override
	protected View initView() {
		View view = View.inflate(mContext,
				R.layout.home_hot_pager, null);
		ViewUtils.inject(this, view);
		return view;
	}

	@Override
	public void initData() {
		videoListBeanList=new ArrayList<VideoListBean>();
		if(isSearch){
			lv_home_hot.setMode(PullToRefreshBase.Mode.BOTH);
		}
		else{
			lv_home_hot.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
		}
		lv_home_hot.getLoadingLayoutProxy(false, true).setPullLabel("上拉加载");
		lv_home_hot.getLoadingLayoutProxy(false, true).setRefreshingLabel("加载中");
		lv_home_hot.getLoadingLayoutProxy(false, true).setReleaseLabel("放开以刷新");
		lv_home_hot.setOnRefreshListener(this);
		gson = new GsonBuilder()
				.setPrettyPrinting()
				.disableHtmlEscaping()
				.create();
		if(!isSearch){
			url = "video/getVideoList.do";
			String json = SharedPrefrenceUtils.getString(mContext, url+"2");
			if (json.equals("null") || TextUtils.isEmpty(json)||isfirst) {
				isfirst=false;
				getViedeoList();
			} else {
				videoListResponse = gson.fromJson(json, VideoListResponse.class);
				setDate(videoListResponse.getDataList());
			}
		}
		else{
			SearchVideo();
		}

		lv_home_hot.setOnScrollListener(new AbsListView.OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView absListView, int scrollState) {
				if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {

					scrollFlag = true;

				} else {

					scrollFlag = false;

				}
			}


			@Override
			public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				boolean enable = false;
				if (lv_home_hot != null && lv_home_hot.getChildCount() > 0) {
					// check if the first item of the list is visible
					boolean firstItemVisible = lv_home_hot.getFirstVisiblePosition() == 0;
					// check if the top of the first item is visible
					boolean topOfFirstItemVisible = lv_home_hot.getChildAt(0).getTop() == 0;
					// enabling or disabling the refresh layout
					enable = firstItemVisible && topOfFirstItemVisible;
				}
				if (swipeRefreshLayout != null) {
					swipeRefreshLayout.setEnabled(enable);
				}
				if (scrollFlag) {

					if (firstVisibleItem > lastVisibleItemPosition) {
						if(vp!=null&&ll_pager != null && prl_head!=null) {
							vp.setVisibility(View.GONE);
							ll_pager.setVisibility(View.GONE);
							prl_head.setVisibility(View.GONE);
						}
						LogUtils.e("上滑");

					}

					if (firstVisibleItem < lastVisibleItemPosition&&enable) {
						if(vp!=null&&ll_pager!=null&&prl_head!=null) {
							vp.setVisibility(View.VISIBLE);
							ll_pager.setVisibility(View.VISIBLE);
							prl_head.setVisibility(View.VISIBLE);
						}
						LogUtils.e( "下滑顶部");

					}

					if (firstVisibleItem == lastVisibleItemPosition) {

						return;

					}

					lastVisibleItemPosition = firstVisibleItem;

				}
			}
		});
	}

	private void getViedeoList() {
		if(NetWorkStatusUtil.getNetWorkStatus(mContext)==1||isloading==true) {
			loadingDialog.show();
			url = "video/getVideoList.do";
			Map<String, String> params = new HashMap<String, String>();
			params.put("type", "2");
			params.put("position", "0");
			params.put("pageNo", String.valueOf(pageNo));
			params.put("pageSize", "10");
			MyVolley.uploadNoFile(MyVolley.POST, url, params, new MyVolley.VolleyCallback() {

				@Override
				public void dealWithJson(String address, String json) {
					LogUtils.e("获取视频列表2:" + json);
					loadingDialog.dismiss();
					videoListResponse = gson.fromJson(json, VideoListResponse.class);
					if (videoListResponse.getCode() == 0) {
						if (videoListResponse.getDataList().size() > 0) {
							if (pageNo == 1) {
								videoListBeanList.clear();
								SharedPrefrenceUtils.setString(mContext, url + "2", json);
							}
							videoListBeanList.addAll(videoListResponse.getDataList());
							setDate(videoListBeanList);

						} else {
//						DialogUtils.showAlertDialog(mContext, "没有更多数据了！");
						}

					} else {
						DialogUtils.showAlertDialog(mContext, videoListResponse.getResultText());
					}
					if (isRefresh) {
						isRefresh = false;
						lv_home_hot.onRefreshComplete();
					}
				}

				@Override
				public void dealWithError(String address, String error) {
					LogUtils.e("获取视频列表错误:" + error);
					loadingDialog.dismiss();
					DialogUtils.showAlertDialog(mContext, error);
					if (isRefresh) {
						isRefresh = false;
						lv_home_hot.onRefreshComplete();
					}
				}
			});
		}else{
			alertDialog= DialogUtils.showAlertDialog(mContext, "当前不是wifi状态！", this);
		}
	}
	public void SearchVideo() {
		loadingDialog.show();
		SearchVideoProtocol searchVideoProtocol = new SearchVideoProtocol();
		SearchVideoRequest searchVideoRequest = new SearchVideoRequest();
		url = searchVideoProtocol.getApiFun();
		searchVideoRequest.map.put("pageNo", String.valueOf(pageNo));
		searchVideoRequest.map.put("pageSize", "10");
		searchVideoRequest.map.put("keyword", keyword);
		searchVideoRequest.map.put("user_id", SharedPrefrenceUtils.getString(mContext, "user_id"));
		searchVideoRequest.map.put("key", "2");
		searchVideoRequest.map.put("type", String.valueOf(int_type));

		MyVolley.uploadNoFile(MyVolley.POST, url, searchVideoRequest.map, new MyVolley.VolleyCallback() {
			@Override
			public void dealWithJson(String address, String json) {

				Gson gson = new Gson();
				SearchVideoResponse searchVideoResponse = gson.fromJson(json, SearchVideoResponse.class);
				LogUtils.e("sendVerificationCodeResponse:" + json.toString());
				if (searchVideoResponse.code == 0) {
					loadingDialog.dismiss();
					if (searchVideoResponse.dataList.size() > 0) {
						videoListBeanList.addAll(searchVideoResponse.dataList);
						setDate(videoListBeanList);
					} else {
//						DialogUtils.showAlertDialog(mContext, "没有更多数据了！");
					}
				} else {
					loadingDialog.dismiss();
					DialogUtils.showAlertDialog(mContext,
							searchVideoResponse.resultText);
				}
				if (isRefresh) {
					isRefresh = false;
					lv_home_hot.onRefreshComplete();
				}

			}

			@Override
			public void dealWithError(String address, String error) {
				loadingDialog.dismiss();
				DialogUtils.showAlertDialog(mContext, error);
			}
		});
	}
	public void setDate(List<VideoListBean> videoListBeans) {
		if (homeAdapter == null) {
			homeAdapter = new HomeAdapter(mContext, videoListBeans,null,false,int_type);
		}
		else{
			homeAdapter.setDate(videoListBeans);
		}
		lv_home_hot.setAdapter(homeAdapter);
	}

	@Override
	public void onRefresh(PullToRefreshBase refreshView) {
		if(!isRefresh){
			isRefresh=true;
			if (lv_home_hot.isHeaderShown()) {
				pageNo=1;
				videoListBeanList.clear();
				if(!isSearch){
					getViedeoList();
				}
				else{
					SearchVideo();
				}
			} else if (lv_home_hot.isFooterShown()) {
				pageNo++;
				if(!isSearch){
					getViedeoList();
				}
				else{
					SearchVideo();
				}
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()){
			case R.id.tv_roger:{
				alertDialog.dismiss();
				isloading=true;
				getViedeoList();
				break;
			}
		}
	}
}