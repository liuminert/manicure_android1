package com.tesu.manicurehouse.tabpager;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.view.View;
import android.widget.ListView;

import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.tesu.manicurehouse.R;
import com.tesu.manicurehouse.adapter.ShopAdapter;
import com.tesu.manicurehouse.base.MyVolley;
import com.tesu.manicurehouse.base.ViewTabBasePager;
import com.tesu.manicurehouse.bean.GoodBean;
import com.tesu.manicurehouse.protocol.GetGoodListProtocol;
import com.tesu.manicurehouse.protocol.SearchGoodsProtocol;
import com.tesu.manicurehouse.request.GetGoodListRequest;
import com.tesu.manicurehouse.request.SearchGoodsRequest;
import com.tesu.manicurehouse.response.GetGoodListResponse;
import com.tesu.manicurehouse.response.SearchGoodsResponse;
import com.tesu.manicurehouse.response.VideoListResponse;
import com.tesu.manicurehouse.util.DialogUtils;
import com.tesu.manicurehouse.util.LogUtils;
import com.tesu.manicurehouse.util.NetWorkStatusUtil;
import com.tesu.manicurehouse.util.SharedPrefrenceUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @作者: 许明达
 * @创建时间: 2016-4-27下午5:24:30
 * @版权: 特速版权所有
 * @描述: 最热门的品牌页面
 * @更新人:
 * @更新时间:
 * @更新内容: TODO
 */
public class ManicuristPager extends ViewTabBasePager implements
		PullToRefreshBase.OnRefreshListener ,View.OnClickListener{
	// 分类listview
	@ViewInject(R.id.lv_manicurist)
	private PullToRefreshListView lv_manicurist;
	private ShopAdapter shopAdapter;
	private String url;
	private Dialog loadingDialog;
	private GetGoodListResponse getGoodListResponse;
	private  List<GoodBean> goodBeanList;
	private  boolean isSearch;
	private String keyword;
	private SearchGoodsResponse searchGoodsResponse;
	private int pageNo=1;
	//判断是否刷新
	private boolean isRefresh=false;
	private boolean isloading=false;
	private Dialog alertDialog;
	private GetGoodListProtocol getGoodListProtocol;
	private boolean isfirst;
	public ManicuristPager(Context context,String keyword,boolean isSearch,Dialog loadingDialog, boolean isfirst) {
		super(context);
		this.keyword=keyword;
		this.isSearch=isSearch;
		this.isfirst=isfirst;
		this.loadingDialog=loadingDialog;
	}
	public void setPageNo(int pageNo){
		this.pageNo=pageNo;
	}
	@Override
	protected View initView() {
		View view = View.inflate(mContext,
				R.layout.manicurist_pager, null);
		ViewUtils.inject(this, view);
		return view;
	}

	@Override
	public void initData() {
		goodBeanList=new ArrayList<GoodBean>();
		getGoodListProtocol = new GetGoodListProtocol();
		if(!isSearch) {
			String json = SharedPrefrenceUtils.getString(mContext, getGoodListProtocol.getApiFun()+ "1");
			if (json.equals("null") || TextUtils.isEmpty(json)||isfirst) {
				runAsyncTask();
				isfirst=false;
			} else {
				Gson gson = new Gson();
				getGoodListResponse = gson.fromJson(json, GetGoodListResponse.class);
				goodBeanList.addAll(getGoodListResponse.dataList);
				setDate(goodBeanList);
			}
		}
		else{
			runSearchGoods();
		}
	}

	@Override
	public void onRefresh(PullToRefreshBase refreshView) {
		if(!isRefresh){
			isRefresh=true;
			if (lv_manicurist.isHeaderShown()) {
				pageNo=1;
				goodBeanList.clear();
				if(!isSearch){
					runAsyncTask();
				}
				else{
					runSearchGoods();
				}
			} else if (lv_manicurist.isFooterShown()) {
				pageNo++;
				if(!isSearch){
					runAsyncTask();
				}
				else{
					runSearchGoods();
				}
			}

		}
	}

	public void runAsyncTask() {
		if(NetWorkStatusUtil.getNetWorkStatus(mContext)==1||isloading==true) {
			loadingDialog.show();
			GetGoodListRequest getGoodListRequest = new GetGoodListRequest();
			url = getGoodListProtocol.getApiFun();
			getGoodListRequest.map.put("pageNo", String.valueOf(pageNo));
			getGoodListRequest.map.put("pageSize", "10");
			getGoodListRequest.map.put("type", "1");
			LogUtils.e("mmmmmpppppageNo:" + pageNo);

			MyVolley.uploadNoFile(MyVolley.POST, url, getGoodListRequest.map, new MyVolley.VolleyCallback() {
				@Override
				public void dealWithJson(String address, String json) {

					Gson gson = new Gson();
					getGoodListResponse = gson.fromJson(json, GetGoodListResponse.class);
					LogUtils.e("sendVerificationCodeResponse:" + json.toString());
					if (getGoodListResponse.code.equals("0")) {
						loadingDialog.dismiss();
						if (getGoodListResponse.dataList.size() > 0) {
							goodBeanList.addAll(getGoodListResponse.dataList);
							setDate(goodBeanList);
							if(pageNo==1) {
								SharedPrefrenceUtils.setString(mContext, url + "1", json);
							}
						}
//					else{
//						DialogUtils.showAlertDialog(mContext, "没有更多数据了！");
//					}

					} else {
						loadingDialog.dismiss();
//					DialogUtils.showAlertDialog(mContext,
//							getGoodListResponse.resultText);
					}
					if (isRefresh) {
						isRefresh = false;
						lv_manicurist.onRefreshComplete();
					}


				}

				@Override
				public void dealWithError(String address, String error) {
					loadingDialog.dismiss();
					DialogUtils.showAlertDialog(mContext, error);
				}
			});
		}
		else{
			alertDialog= DialogUtils.showAlertDialog(mContext, "当前不是wifi状态！", this);
		}
	}
	public void setDate(List<GoodBean> goodBeans){
		if (shopAdapter == null) {
			shopAdapter = new ShopAdapter(mContext, goodBeans);
			lv_manicurist.setAdapter(shopAdapter);
		}
		else{
			shopAdapter.setDate(goodBeans);
		}
		lv_manicurist.setOnRefreshListener(this);
		lv_manicurist.setMode(PullToRefreshBase.Mode.BOTH);
		lv_manicurist.getLoadingLayoutProxy(false, true).setPullLabel("上拉加载");
		lv_manicurist.getLoadingLayoutProxy(false, true).setRefreshingLabel("加载中");
		lv_manicurist.getLoadingLayoutProxy(false, true).setReleaseLabel("放开以刷新");
	}

	public void runSearchGoods() {
		loadingDialog.show();
		SearchGoodsProtocol searchGoodsProtocol = new SearchGoodsProtocol();
		SearchGoodsRequest searchGoodsRequest = new SearchGoodsRequest();
		url = searchGoodsProtocol.getApiFun();
		searchGoodsRequest.map.put("user_id", SharedPrefrenceUtils.getString(mContext, "user_id"));
		searchGoodsRequest.map.put("keyword", keyword);
		searchGoodsRequest.map.put("type", "1");
		searchGoodsRequest.map.put("pageNo", String.valueOf(pageNo));
		searchGoodsRequest.map.put("pageSize", "10");
		MyVolley.uploadNoFile(MyVolley.POST, url, searchGoodsRequest.map, new MyVolley.VolleyCallback() {
			@Override
			public void dealWithJson(String address, String json) {
				LogUtils.e("json:" + json);
				Gson gson = new Gson();
				searchGoodsResponse = gson.fromJson(json, SearchGoodsResponse.class);
				if (searchGoodsResponse.code == 0) {
					loadingDialog.dismiss();
					if (searchGoodsResponse.dataList.size() > 0) {
						goodBeanList.addAll(searchGoodsResponse.dataList);
						setDate(goodBeanList);
					}
//					else{
//						DialogUtils.showAlertDialog(mContext, "没有更多数据了！");
//					}
				} else {
					loadingDialog.dismiss();
					DialogUtils.showAlertDialog(mContext,
							searchGoodsResponse.resultText);
				}
				if (isRefresh) {
					isRefresh = false;
					lv_manicurist.onRefreshComplete();
				}
			}

			@Override
			public void dealWithError(String address, String error) {
				loadingDialog.dismiss();
				DialogUtils.showAlertDialog(mContext, error);
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()){
			case R.id.tv_roger:{
				alertDialog.dismiss();
				isloading=true;
				runAsyncTask();
				break;
			}
		}
	}
}