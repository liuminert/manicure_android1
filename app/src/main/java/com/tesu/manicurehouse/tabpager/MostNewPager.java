package com.tesu.manicurehouse.tabpager;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.tesu.manicurehouse.R;
import com.tesu.manicurehouse.adapter.HomeAdapter;
import com.tesu.manicurehouse.adapter.ShowAdapter;
import com.tesu.manicurehouse.base.MyVolley;
import com.tesu.manicurehouse.base.ViewTabBasePager;
import com.tesu.manicurehouse.bean.ShowPhotoContentBean;
import com.tesu.manicurehouse.bean.VideoListBean;
import com.tesu.manicurehouse.response.VideoListResponse;
import com.tesu.manicurehouse.util.DialogUtils;
import com.tesu.manicurehouse.util.LogUtils;
import com.tesu.manicurehouse.util.SharedPrefrenceUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import io.vov.vitamio.utils.Log;

/**
 * @作者: 许明达
 * @创建时间: 2016-4-27下午5:24:30
 * @版权: 特速版权所有
 * @描述: 最热门的品牌页面
 * @更新人:
 * @更新时间:
 * @更新内容: TODO
 */
public class MostNewPager extends ViewTabBasePager implements
        PullToRefreshBase.OnRefreshListener {
    // 分类listview
    @ViewInject(R.id.lv_show_most_new)
    private PullToRefreshListView lv_show_most_new;
    private ShowAdapter showAdapter;
    private Dialog loadingDialog;
    private String url;
    private VideoListResponse videoListResponse;
    private Gson gson;
    private List<VideoListBean> videoListBeanList;
    private int width;
    private int pageNo = 1;
    //判断是否刷新
    private boolean isRefresh = false;
    private boolean isFirst;

    public MostNewPager(int width, Context context, boolean isFirst) {
        super(context);
        this.width = width;
        this.isFirst = isFirst;
    }

    @Override
    protected View initView() {
        View view = View.inflate(mContext,
                R.layout.most_new_pager, null);
        ViewUtils.inject(this, view);
        return view;
    }

    @Override
    public void initData() {
        gson = new GsonBuilder()
                .setPrettyPrinting()
                .disableHtmlEscaping()
                .create();
        loadingDialog = DialogUtils.createLoadDialog(mContext, true);
        videoListBeanList = new ArrayList<>();
        lv_show_most_new.setMode(PullToRefreshBase.Mode.BOTH);
        lv_show_most_new.getLoadingLayoutProxy(false, true).setPullLabel("上拉加载");
        lv_show_most_new.getLoadingLayoutProxy(false, true).setRefreshingLabel("加载中");
        lv_show_most_new.getLoadingLayoutProxy(false, true).setReleaseLabel("放开以刷新");
        lv_show_most_new.setOnRefreshListener(this);

        url = "video/getVideoList.do";
        String json = SharedPrefrenceUtils.getString(mContext, url + "3");
        if (json.equals("null") || TextUtils.isEmpty(json) || isFirst) {
            LogUtils.e("json:" + isFirst);
            getViedeoList();
        } else {
            videoListResponse = gson.fromJson(json, VideoListResponse.class);
            setData();
        }
//        getViedeoList();
    }

    private void setData() {
        videoListBeanList.addAll(videoListResponse.getDataList());
        if (showAdapter == null) {
            showAdapter = new ShowAdapter(width, mContext, videoListBeanList);
            lv_show_most_new.setAdapter(showAdapter);
        } else {
            showAdapter.notifyDataSetChanged();
        }
    }

    private void getViedeoList() {
        loadingDialog.show();
//        url = "video/getVideoList.do";
        Map<String, String> params = new HashMap<String, String>();
        params.put("type", "1");
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
                    if (videoListResponse.getDataList().size() > 0) {
                        if (pageNo == 1) {
                            videoListBeanList.clear();
                            SharedPrefrenceUtils.setString(mContext, url + "3", json);
                        }
                        setData();
                    } else {
//                         DialogUtils.showAlertDialog(mContext, "没有更多数据了！");
                    }

                } else {
                    DialogUtils.showAlertDialog(mContext, videoListResponse.getResultText());
                }
                if (isRefresh) {
                    LogUtils.e("结束");
                    isRefresh = false;
                    lv_show_most_new.onRefreshComplete();
                }
            }

            @Override
            public void dealWithError(String address, String error) {
                LogUtils.e("获取视频列表错误:" + error);
                loadingDialog.dismiss();
                DialogUtils.showAlertDialog(mContext, error);
                if (isRefresh) {
                    isRefresh = false;
                    lv_show_most_new.onRefreshComplete();
                }
            }
        });
    }


    @Override
    public void onRefresh(PullToRefreshBase refreshView) {
        if (!isRefresh) {
            isRefresh = true;
            if (lv_show_most_new.isHeaderShown()) {
                pageNo = 1;
                videoListBeanList.clear();
                getViedeoList();
            } else if (lv_show_most_new.isFooterShown()) {
                pageNo++;
                getViedeoList();
            }
        }
    }
}