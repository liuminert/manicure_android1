package com.tesu.manicurehouse.tabpager;

import android.app.Dialog;
import android.content.Context;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lidroid.xutils.ViewUtils;
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
public class HomeMostNewPager extends ViewTabBasePager implements
        PullToRefreshBase.OnRefreshListener, View.OnClickListener {
    // 分类listview
    private PullToRefreshListView lv_home_new;
    private HomeAdapter homeAdapter;
    private Dialog loadingDialog;
    private String url;
    private VideoListResponse videoListResponse;
    private Gson gson;
    private List<VideoListBean> videoListBeanList;
    SwipeRefreshLayout swipeRefreshLayout;
    private boolean isSearch;
    private int pageNo = 1;
    //判断是否刷新
    private boolean isRefresh = false;
    private String keyword;
    private boolean isloading = false;
    private boolean isfirst;
    private Dialog alertDialog;
    private boolean scrollFlag;
    private int lastVisibleItemPosition;// 标记上次滑动位置
    private ViewPager vp;
    private LinearLayout ll_pager;
    private PercentRelativeLayout prl_head;
    private int int_type;

    public HomeMostNewPager(Context context, SwipeRefreshLayout swipeRefreshLayout, boolean isSearch, String keyword, Dialog loadingDialog, boolean isfirst, ViewPager vp, LinearLayout ll_pager, PercentRelativeLayout prl_head,int int_type) {
        super(context);
        this.swipeRefreshLayout = swipeRefreshLayout;
        this.isSearch = isSearch;
        this.keyword = keyword;
        this.loadingDialog = loadingDialog;
        this.isfirst = isfirst;
        this.vp = vp;
        this.ll_pager = ll_pager;
        this.prl_head = prl_head;
        this.int_type=int_type;
    }

    @Override
    protected View initView() {
        View view = View.inflate(mContext,
                R.layout.home_new_pager, null);
        ViewUtils.inject(this, view);
        lv_home_new = (PullToRefreshListView) view.findViewById(R.id.lv_home_new);
        return view;
    }

    public void getDate() {
        pageNo = 1;
        getViedeoList();
    }

    @Override
    public void initData() {
        videoListBeanList = new ArrayList<VideoListBean>();
        if (isSearch) {
            lv_home_new.setMode(PullToRefreshBase.Mode.BOTH);
        } else {
            lv_home_new.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
        }
        lv_home_new.getLoadingLayoutProxy(false, true).setPullLabel("上拉加载");
        lv_home_new.getLoadingLayoutProxy(false, true).setRefreshingLabel("加载中");
        lv_home_new.getLoadingLayoutProxy(false, true).setReleaseLabel("放开以刷新");
        lv_home_new.setOnRefreshListener(this);
        gson = new GsonBuilder()
                .setPrettyPrinting()
                .disableHtmlEscaping()
                .create();
        if (!isSearch) {
            url = "video/getVideoList.do";
            String json = SharedPrefrenceUtils.getString(mContext, url + "1");
            if (json.equals("null") || TextUtils.isEmpty(json) || isfirst) {
                isfirst = false;
                getViedeoList();
            } else {
                videoListResponse = gson.fromJson(json, VideoListResponse.class);
                setDate(videoListResponse.getDataList());
            }
        } else {
            runSearchVideo();
        }

        lv_home_new.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                boolean enable = false;
                if (lv_home_new != null && lv_home_new.getChildCount() > 0) {
                    // check if the first item of the list is visible
                    boolean firstItemVisible = lv_home_new.getFirstVisiblePosition() == 0;
                    // check if the top of the first item is visible
                    boolean topOfFirstItemVisible = lv_home_new.getChildAt(0).getTop() == 0;
                    // enabling or disabling the refresh layout
                    enable = firstItemVisible && topOfFirstItemVisible;
                }
                if (swipeRefreshLayout != null) {
                    swipeRefreshLayout.setEnabled(enable);
                }

                if (firstVisibleItem == lastVisibleItemPosition) {

                    return;

                }

                lastVisibleItemPosition = firstVisibleItem;

            }
        });
    }

    private void getViedeoList() {
        if (NetWorkStatusUtil.getNetWorkStatus(mContext) == 1 || isloading == true) {
            loadingDialog.show();
            url = "video/getVideoList.do";
            Map<String, String> params = new HashMap<String, String>();
            params.put("type", "1");
            params.put("position", "0");
            params.put("pageNo", String.valueOf(pageNo));
            params.put("pageSize", "10");
            MyVolley.uploadNoFile(MyVolley.POST, url, params, new MyVolley.VolleyCallback() {

                @Override
                public void dealWithJson(String address, String json) {
                    LogUtils.e("获取视频列表1:" + json);
                    loadingDialog.dismiss();
//				json = gson.toJson(videoListResponse);
//				json = json.replaceAll("\\\\","");
//				LogUtils.e("替换后:" + json);
                    videoListResponse = gson.fromJson(json, VideoListResponse.class);
                    if (videoListResponse.getCode() == 0) {
                        if (videoListResponse.getDataList().size() > 0) {
                            if (pageNo == 1) {
                                videoListBeanList.clear();
                                SharedPrefrenceUtils.setString(mContext, url + "1", json);
                            }
                            videoListBeanList.addAll(videoListResponse.getDataList());
                            setDate(videoListBeanList);

                        } else {
//                        DialogUtils.showAlertDialog(mContext, "没有更多数据了！");
                        }
                    } else {
                        DialogUtils.showAlertDialog(mContext, videoListResponse.getResultText());
                    }
                    if (isRefresh) {
                        isRefresh = false;
                        lv_home_new.onRefreshComplete();
                    }
                }

                @Override
                public void dealWithError(String address, String error) {
                    LogUtils.e("获取视频列表错误:" + error);
                    loadingDialog.dismiss();
                    DialogUtils.showAlertDialog(mContext, error);
                    if (isRefresh) {
                        isRefresh = false;
                        lv_home_new.onRefreshComplete();
                    }
                }
            });
        } else {
            alertDialog = DialogUtils.showAlertDialog(mContext, "当前不是wifi状态！", this);
        }
    }

    public void runSearchVideo() {
        LogUtils.e("进int_type"+int_type);
        loadingDialog.show();
        SearchVideoProtocol searchVideoProtocol = new SearchVideoProtocol();
        SearchVideoRequest searchVideoRequest = new SearchVideoRequest();
        url = searchVideoProtocol.getApiFun();
        searchVideoRequest.map.put("pageNo", String.valueOf(pageNo));
        searchVideoRequest.map.put("pageSize", "10");
        searchVideoRequest.map.put("keyword", keyword);
        searchVideoRequest.map.put("user_id", SharedPrefrenceUtils.getString(mContext, "user_id"));
        searchVideoRequest.map.put("key", "1");
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
//                        DialogUtils.showAlertDialog(mContext, "没有更多数据了！");
                    }
                } else {
                    loadingDialog.dismiss();
                    DialogUtils.showAlertDialog(mContext,
                            searchVideoResponse.resultText);
                }
                if (isRefresh) {
                    isRefresh = false;
                    lv_home_new.onRefreshComplete();
                }

            }

            @Override
            public void dealWithError(String address, String error) {
                loadingDialog.dismiss();
                DialogUtils.showAlertDialog(mContext, error);
                if (isRefresh) {
                    isRefresh = false;
                    lv_home_new.onRefreshComplete();
                }
            }
        });
    }

    public void setDate(List<VideoListBean> videoListBeans) {
        if (homeAdapter == null) {
            homeAdapter = new HomeAdapter(mContext, videoListBeans,null,false,int_type);
        } else {
            homeAdapter.setDate(videoListBeans);
        }
        lv_home_new.setAdapter(homeAdapter);
    }

    @Override
    public void onRefresh(PullToRefreshBase refreshView) {
        if (!isRefresh) {
            isRefresh = true;
            if (lv_home_new.isHeaderShown()) {
                pageNo = 1;
                videoListBeanList.clear();
                if (!isSearch) {
                    LogUtils.e("进1");
                    getViedeoList();
                } else {
                    runSearchVideo();
                }
            } else if (lv_home_new.isFooterShown()) {
                pageNo++;
                if (!isSearch) {
                    LogUtils.e("进2");
                    getViedeoList();
                } else {
                    runSearchVideo();
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_roger: {
                alertDialog.dismiss();
                isloading = true;
                getViedeoList();
                break;
            }
        }
    }


}