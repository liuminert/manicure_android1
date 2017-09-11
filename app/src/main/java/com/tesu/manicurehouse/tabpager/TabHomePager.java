package com.tesu.manicurehouse.tabpager;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.tesu.manicurehouse.R;
import com.tesu.manicurehouse.activity.ClassifyActivity;
import com.tesu.manicurehouse.activity.SearchActivity;
import com.tesu.manicurehouse.adapter.HomeAdapter;
import com.tesu.manicurehouse.base.MyVolley;
import com.tesu.manicurehouse.base.TabBasePager;
import com.tesu.manicurehouse.base.ViewTabBasePager;
import com.tesu.manicurehouse.bean.VideoListBean;
import com.tesu.manicurehouse.protocol.GetAdsProtocol;
import com.tesu.manicurehouse.request.GetAdsRequest;
import com.tesu.manicurehouse.response.GetAdsResponse;
import com.tesu.manicurehouse.response.VideoListResponse;
import com.tesu.manicurehouse.support.PercentRelativeLayout;
import com.tesu.manicurehouse.util.DialogUtils;
import com.tesu.manicurehouse.util.LogUtils;
import com.tesu.manicurehouse.util.NetWorkStatusUtil;
import com.tesu.manicurehouse.util.PictureOption;
import com.tesu.manicurehouse.util.SharedPrefrenceUtils;
import com.tesu.manicurehouse.util.UIUtils;
import com.tesu.manicurehouse.widget.DragLayout;
import com.tesu.manicurehouse.widget.MyLinearLayout;
import com.tesu.manicurehouse.widget.NoScrollViewPager;
import com.tesu.manicurehouse.widget.TabSlidingIndicator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @作者: 许明达
 * @创建时间: 2016年3月23日上午11:10:20
 * @版权: 特速版权所有
 * @描述: TODO
 */
public class TabHomePager extends TabBasePager implements View.OnClickListener, PullToRefreshBase.OnRefreshListener,HomeAdapter.ChangePageNo{
    private Dialog alertDialog;
    private boolean isloading=false;
    // 分类listview
    private PullToRefreshListView lv_home_new;
    private HomeAdapter homeAdapter;
    private VideoListResponse videoListResponse;
    LinearLayout view;
    LayoutInflater mInflater;
    private FrameLayout mDragLayout;
    @ViewInject(R.id.tv_search)
    private TextView tv_search;
    private ScrollView mScrollView;
    /**
     * 判定是否已经开启
     */
    private boolean isopen = false;
    private boolean isRefresh = false;
    private String url;
    private Dialog loadingDialog;
    private int pageNo = 1;
    boolean isloaded = false;
    private boolean isfirst = true;
    private PercentRelativeLayout prl_head;
    private List<VideoListBean> videoListBeanList;
    private int type=1;
    private ImageView iv_goto_top;

    public TabHomePager(Context context, FrameLayout mDragLayout
    ) {
        super(context, mDragLayout);
        this.mDragLayout = mDragLayout;

    }


    @Override
    protected View initView() {
        view = (LinearLayout) View.inflate(mContext, R.layout.home_pager, null);
        ViewUtils.inject(this, view);
        if (mInflater == null) {
            mInflater = (LayoutInflater) UIUtils.getContext().getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
        }
        lv_home_new = (PullToRefreshListView) view.findViewById(R.id.lv_home_new);
        prl_head = (PercentRelativeLayout) view.findViewById(R.id.prl_head);
        iv_goto_top = (ImageView) view.findViewById(R.id.iv_goto_top);

        videoListBeanList = new ArrayList<VideoListBean>();
        loadingDialog = DialogUtils.createLoadDialog(mContext, true);

        homeAdapter = new HomeAdapter(mContext, videoListBeanList,loadingDialog,true,0);
        homeAdapter.setChangeLisenter(this);
        lv_home_new.setAdapter(homeAdapter);

        return view;
    }

    public void initData() {

        if (!ImageLoader.getInstance().isInited()) {
            ImageLoader.getInstance()
                    .init(ImageLoaderConfiguration.createDefault(UIUtils
                            .getContext()));
        }
        tv_search.setOnClickListener(this);
        lv_home_new.setOnRefreshListener(this);
        if (!isloaded) {
            getViedeoList();
        } else {
            isfirst = false;
        }
        iv_goto_top.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(videoListBeanList.size()>0){
                    lv_home_new.setSelection(0);
                }

            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_search:
                Intent intent = new Intent(mContext, SearchActivity.class);
                intent.putExtra("type", 1);
                intent.putExtra("int_type",0);
                intent.putExtra("position", 2);
                UIUtils.startActivityNextAnim(intent);
                break;
            case R.id.tv_roger:{
                alertDialog.dismiss();
                isloading=true;
                getViedeoList();
                break;
            }
        }
    }


    private void getViedeoList() {
        if (NetWorkStatusUtil.getNetWorkStatus(mContext) == 1||isloading==true) {
            loadingDialog.show();
            url = "video/getVideoList.do";
            Map<String, String> params = new HashMap<String, String>();
            params.put("type", String.valueOf(type));
            params.put("position", "0");
            params.put("pageNo", String.valueOf(pageNo));
            params.put("pageSize", "10");
            LogUtils.e("pageNo:" + pageNo + "列表个数:" + videoListBeanList.size());
            MyVolley.uploadNoFile(MyVolley.POST, url, params, new MyVolley.VolleyCallback() {

                @Override
                public void dealWithJson(String address, String json) {
                    Gson gson = new Gson();
                    LogUtils.e("获取视频列表:" + json);
                    loadingDialog.dismiss();
                    videoListResponse = gson.fromJson(json, VideoListResponse.class);
                    if (videoListResponse.getCode() == 0) {
                        if (videoListResponse.getDataList().size() > 0) {
                            isloaded = true;
                            if (pageNo == 1) {
                                videoListBeanList.clear();
                                videoListBeanList.add(null);
                                videoListBeanList.add(null);
                                SharedPrefrenceUtils.setString(mContext, url + "1", json);
                            }
                            videoListBeanList.addAll(videoListResponse.getDataList());
                            setDate();

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

    public void setDate() {
//        if (homeAdapter == null) {
//            homeAdapter = new HomeAdapter(mContext, videoListBeans,loadingDialog,true,0);
//            homeAdapter.setChangeLisenter(this);
//            lv_home_new.setAdapter(homeAdapter);
//        }
//        else {
//            homeAdapter.notifyDataSetChanged();
//        }
        homeAdapter.notifyDataSetChanged();
        lv_home_new.setMode(PullToRefreshBase.Mode.BOTH);
    }

    @Override
    public void onRefresh(PullToRefreshBase refreshView) {
        if (!isRefresh) {
            isRefresh = true;
            if (lv_home_new.isHeaderShown()) {
                pageNo = 1;
                videoListBeanList.clear();
                if(homeAdapter!=null) {
                    homeAdapter.setLoading(false);
                    type = homeAdapter.type;
                }
                getViedeoList();
            } else if (lv_home_new.isFooterShown()) {
                pageNo++;
                if(homeAdapter != null){
                    type = homeAdapter.type;
                }
                getViedeoList();
            }
        }
    }

    @Override
    public void changeNo() {
        pageNo = 1;

    }
}
