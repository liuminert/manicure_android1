package com.tesu.manicurehouse.tabpager;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ListView;

import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.tesu.manicurehouse.R;
import com.tesu.manicurehouse.adapter.ShowAdapter;
import com.tesu.manicurehouse.base.MyVolley;
import com.tesu.manicurehouse.base.ViewTabBasePager;
import com.tesu.manicurehouse.bean.VideoListBean;
import com.tesu.manicurehouse.response.VideoListResponse;
import com.tesu.manicurehouse.util.DialogUtils;
import com.tesu.manicurehouse.util.LogUtils;
import com.tesu.manicurehouse.util.SharedPrefrenceUtils;

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
public class FreePager extends ViewTabBasePager implements
		PullToRefreshBase.OnRefreshListener {
	// 分类listview
	@ViewInject(R.id.lv_show_free)
	private PullToRefreshListView lv_show_free;
	private ShowAdapter showAdapter;

	private Dialog loadingDialog;
	private String url;
	private VideoListResponse videoListResponse;
	private Gson gson;
	private List<VideoListBean> videoListBeanList;
	private TabShowPager.ICallBack iCallBack;
	private TabShowPager tabShowPager;
	private List<VideoListBean> freeVideoListBeanList;
	private List<VideoListBean> unFreeVideoListBeanList;
	private int width;
	private int pageNo = 1;
	//判断是否刷新
	private boolean isRefresh = false;
	private boolean isFirst;
	private int isAll;   //视频类型  0，全部    1，收费     2，免费
	public FreePager(int width,TabShowPager tabShowPager,Context context, boolean isFirst) {
		super(context);
		this.tabShowPager = tabShowPager;
		this.width = width;
		this.isFirst = isFirst;
	}

	@Override
	protected View initView() {
		View view = View.inflate(mContext,
				R.layout.free_pager, null);
		ViewUtils.inject(this, view);
		return view;
	}

	@Override
	public void initData() {
		gson = new Gson();
		loadingDialog = DialogUtils.createLoadDialog(mContext, true);


		freeVideoListBeanList = new ArrayList<VideoListBean>();
		unFreeVideoListBeanList = new ArrayList<VideoListBean>();
		tabShowPager.setiCallBack(new TabShowPager.ICallBack() {
			@Override
			public void changeType(int type) {
				switch(type){
					case 0:
						isAll = 0;
						showAdapter = new ShowAdapter(width,mContext, videoListBeanList);
						lv_show_free.setAdapter(showAdapter);
						break;
					case 1:
						isAll = 1;
						showAdapter = new ShowAdapter(width,mContext, unFreeVideoListBeanList);
						lv_show_free.setAdapter(showAdapter);
						break;
					case 2:
						isAll = 2;
						showAdapter = new ShowAdapter(width,mContext, freeVideoListBeanList);
						lv_show_free.setAdapter(showAdapter);
						break;
				}
			}
		});
		videoListBeanList=new ArrayList<>();
		lv_show_free.setMode(PullToRefreshBase.Mode.BOTH);
		lv_show_free.getLoadingLayoutProxy(false, true).setPullLabel("上拉加载");
		lv_show_free.getLoadingLayoutProxy(false, true).setRefreshingLabel("加载中");
		lv_show_free.getLoadingLayoutProxy(false, true).setReleaseLabel("放开以刷新");
		lv_show_free.setOnRefreshListener(this);

		url = "video/getVideoList.do";
		String json = SharedPrefrenceUtils.getString(mContext, url + "5");
		if (json.equals("null") || TextUtils.isEmpty(json) || isFirst) {
			getViedeoList();
		} else {
			videoListResponse = gson.fromJson(json, VideoListResponse.class);
			setData();
		}

//		getViedeoList();
	}

	private void setData() {
		videoListBeanList.addAll(videoListResponse.getDataList());
//		showAdapter = new ShowAdapter(width, mContext, videoListBeanList);
//		lv_show_free.setAdapter(showAdapter);
		if(videoListBeanList != null){
            for(VideoListBean videoListBean : videoListBeanList){
                if(videoListBean.getIs_fee()==0){
                    if(!freeVideoListBeanList.contains(videoListBean)){
                        freeVideoListBeanList.add(videoListBean);
                    }

                }else {
                    if(!unFreeVideoListBeanList.contains(videoListBean)){
                        unFreeVideoListBeanList.add(videoListBean);
                    }

                }
            }
        }

		switch(isAll){
			case 0:
				isAll = 0;
				showAdapter = new ShowAdapter(width,mContext, videoListBeanList);
				lv_show_free.setAdapter(showAdapter);
				break;
			case 1:
				isAll = 1;
				showAdapter = new ShowAdapter(width,mContext, unFreeVideoListBeanList);
				lv_show_free.setAdapter(showAdapter);
				break;
			case 2:
				isAll = 2;
				showAdapter = new ShowAdapter(width,mContext, freeVideoListBeanList);
				lv_show_free.setAdapter(showAdapter);
				break;
		}
	}

	private void getViedeoList() {
		loadingDialog.show();
		url = "video/getVideoList.do";
		Map<String,String> params = new HashMap<String,String>();
		params.put("type", "0");
		params.put("position", "1");
		params.put("pageNo", String.valueOf(pageNo));
		params.put("pageSize", "100");
		MyVolley.uploadNoFile(MyVolley.POST, url, params, new MyVolley.VolleyCallback() {

			@Override
			public void dealWithJson(String address, String json) {
				LogUtils.e("获取视频列表:" + json);
				loadingDialog.dismiss();
				videoListResponse = gson.fromJson(json, VideoListResponse.class);
				if (videoListResponse.getCode() == 0) {
					isFirst = false;
					if(videoListResponse.getDataList().size()>0){
						SharedPrefrenceUtils.setString(mContext, url + "5", json);
						setData();
					}
					else{
//						DialogUtils.showAlertDialog(mContext, "没有更多数据了！");
					}
				} else {
					DialogUtils.showAlertDialog(mContext, videoListResponse.getResultText());
				}
				if (isRefresh) {
					isRefresh = false;
					lv_show_free.onRefreshComplete();
				}
			}

			@Override
			public void dealWithError(String address, String error) {
				LogUtils.e("获取视频列表错误:" + error);
				loadingDialog.dismiss();
				DialogUtils.showAlertDialog(mContext, error);
				if (isRefresh) {
					isRefresh = false;
					lv_show_free.onRefreshComplete();
				}
			}
		});
	}


	@Override
	public void onRefresh(PullToRefreshBase refreshView) {
		if (!isRefresh) {
			isRefresh = true;
			if (lv_show_free.isHeaderShown()) {
				pageNo = 1;
				videoListBeanList.clear();
				getViedeoList();
			} else if (lv_show_free.isFooterShown()) {
				pageNo++;
				getViedeoList();
			}
		}
	}
}