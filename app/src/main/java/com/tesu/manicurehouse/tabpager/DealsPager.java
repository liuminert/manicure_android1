package com.tesu.manicurehouse.tabpager;

import android.app.Dialog;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.ScrollView;

import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.tesu.manicurehouse.R;
import com.tesu.manicurehouse.adapter.CommentsAdapter;
import com.tesu.manicurehouse.adapter.DealsAdapter;
import com.tesu.manicurehouse.adapter.ShowAdapter;
import com.tesu.manicurehouse.base.MyVolley;
import com.tesu.manicurehouse.base.ViewTabBasePager;
import com.tesu.manicurehouse.bean.GoodCommentBean;
import com.tesu.manicurehouse.bean.SucDealGoodBean;
import com.tesu.manicurehouse.protocol.GetGoodCommentListProtocol;
import com.tesu.manicurehouse.protocol.GetSucDealGoodListProtocol;
import com.tesu.manicurehouse.request.GetGoodCommentListRequest;
import com.tesu.manicurehouse.request.GetSucDealGoodListRequest;
import com.tesu.manicurehouse.response.GetGoodCommentListResponse;
import com.tesu.manicurehouse.response.GetSucDealGoodListResponse;
import com.tesu.manicurehouse.util.DialogUtils;
import com.tesu.manicurehouse.util.LogUtils;
import com.tesu.manicurehouse.widget.MyListView;

import java.util.ArrayList;
import java.util.List;

/**
 * @作者: 许明达
 * @创建时间: 2016-4-27下午5:24:30
 * @版权: 特速版权所有
 * @描述: 评论的页面
 * @更新人:
 * @更新时间:
 * @更新内容: TODO
 */
public class DealsPager extends ViewTabBasePager  {
	// 分类listview
	@ViewInject(R.id.lv_deal)
	private MyListView lv_deal;
	@ViewInject(R.id.sv)
	private PullToRefreshScrollView sv;
	private ScrollView scrollView;
	private DealsAdapter dealsAdapter;
	private String url;
	private Dialog loadingDialog;
	private String goods_id;
	private boolean isUpdate;
	public DealsPager(Context context,ScrollView scrollView,String goods_id) {
		super(context);
		this.scrollView=scrollView;
		this.goods_id=goods_id;
	}

	@Override
	protected View initView() {
		View view = View.inflate(mContext,
				R.layout.deals_pager, null);
		ViewUtils.inject(this, view);
		return view;
	}

	@Override
	public void initData() {
		loadingDialog = DialogUtils.createLoadDialog(mContext, true);
		if(dealsAdapter==null){
			runSucDealGoodList();
		}
		sv.setMode(PullToRefreshBase.Mode.DISABLED);
//		sv.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ScrollView>() {
//			@Override
//			public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
//
////                sv.setMode(PullToRefreshBase.Mode.DISABLED);
//
//				// Call onRefreshComplete when the list has been refreshed.
//				//在更新UI后，无需其它Refresh操作，系统会自己加载新的listView
//				isUpdate=true;
//				runSucDealGoodList();
//			}
//		});
	}

	//获取商品成交记录
	public void runSucDealGoodList() {
		if(!isUpdate){
			loadingDialog.show();
		}

		GetSucDealGoodListProtocol getSucDealGoodListProtocol = new GetSucDealGoodListProtocol();
		GetSucDealGoodListRequest getSucDealGoodListRequest = new GetSucDealGoodListRequest();
		url = getSucDealGoodListProtocol.getApiFun();
		getSucDealGoodListRequest.map.put("goods_id", goods_id);
		getSucDealGoodListRequest.map.put("pageNo", "1");
		getSucDealGoodListRequest.map.put("pageSize", "100");
		MyVolley.uploadNoFile(MyVolley.POST, url, getSucDealGoodListRequest.map, new MyVolley.VolleyCallback() {
			@Override
			public void dealWithJson(String address, String json) {
				Gson gson = new Gson();
				GetSucDealGoodListResponse getSucDealGoodListResponse = gson.fromJson(json, GetSucDealGoodListResponse.class);
				if (getSucDealGoodListResponse.code == 0) {
					LogUtils.e("json:" + json);
					loadingDialog.dismiss();
					if (isUpdate) {
						isUpdate = false;
						sv.onRefreshComplete();
					}
					setSucDealGoodList(getSucDealGoodListResponse.dataList);
				} else {
					loadingDialog.dismiss();
					DialogUtils.showAlertDialog(mContext,
							getSucDealGoodListResponse.resultText);
				}


			}

			@Override
			public void dealWithError(String address, String error) {
				loadingDialog.dismiss();
				DialogUtils.showAlertDialog(mContext, error);
			}
		});
	}
	public void setSucDealGoodList(List<SucDealGoodBean> sucDealGoodList){
		if(dealsAdapter==null) {
			dealsAdapter = new DealsAdapter(mContext, sucDealGoodList);
			lv_deal.setAdapter(dealsAdapter);
		}
	}

}