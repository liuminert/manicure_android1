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
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.tesu.manicurehouse.R;
import com.tesu.manicurehouse.adapter.CommentsAdapter;
import com.tesu.manicurehouse.adapter.ShowAdapter;
import com.tesu.manicurehouse.base.MyVolley;
import com.tesu.manicurehouse.base.ViewTabBasePager;
import com.tesu.manicurehouse.bean.GoodCommentBean;
import com.tesu.manicurehouse.protocol.GetGoodCommentListProtocol;
import com.tesu.manicurehouse.request.GetGoodCommentListRequest;
import com.tesu.manicurehouse.response.GetGoodCommentListResponse;
import com.tesu.manicurehouse.util.DialogUtils;
import com.tesu.manicurehouse.util.LogUtils;
import com.tesu.manicurehouse.util.UIUtils;
import com.tesu.manicurehouse.widget.MyListView;

import java.util.ArrayList;
import java.util.List;

/**
 * @作者: 许明达
 * @创建时间: 2016-4-27下午5:24:30
 * @版权: 特速版权所有
 * @描述: 评论页面
 * @更新人:
 * @更新时间:
 * @更新内容: TODO
 */
public class CommentsPager extends ViewTabBasePager {
    // 分类listview
    @ViewInject(R.id.lv_comment)
    private MyListView lv_comment;
    // 0 没有下一页
    private int next;

    /**
     * 关注品牌的页数，1表示第一页
     */
    private int currentPage = 1;
    private CommentsAdapter commentsAdapter;
    private ScrollView scrollView;
    private String url;
    private Dialog loadingDialog;
    private String goods_id;

    public CommentsPager(Context context, ScrollView scrollView, String goods_id) {
        super(context);
        this.scrollView = scrollView;
        this.goods_id = goods_id;
    }

    @Override
    protected View initView() {
        View view = View.inflate(mContext,
                R.layout.comments_pager, null);
        ViewUtils.inject(this, view);
        return view;
    }

    @Override
    public void initData() {
        LogUtils.e("commentsAdapter撒打算当:"+commentsAdapter);
        loadingDialog = DialogUtils.createLoadDialog(mContext, true);
//        runGetGoodCommentList();
//		sv.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ScrollView>() {
//			@Override
//			public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
//
////                sv.setMode(PullToRefreshBase.Mode.DISABLED);
//
//				// Call onRefreshComplete when the list has been refreshed.
//				//在更新UI后，无需其它Refresh操作，系统会自己加载新的listView
//				isUpdate=true;
//				runGetGoodCommentList();
//			}
//		});
    }


    //获取商品评论
    public void runGetGoodCommentList() {
        loadingDialog.show();
        GetGoodCommentListProtocol getGoodCommentListProtocol = new GetGoodCommentListProtocol();
        GetGoodCommentListRequest getGoodCommentListRequest = new GetGoodCommentListRequest();
        url = getGoodCommentListProtocol.getApiFun();
        getGoodCommentListRequest.map.put("goods_id", goods_id);
        getGoodCommentListRequest.map.put("pageNo", "1");
        getGoodCommentListRequest.map.put("pageSize", "100");
        MyVolley.uploadNoFile(MyVolley.POST, url, getGoodCommentListRequest.map, new MyVolley.VolleyCallback() {
            @Override
            public void dealWithJson(String address, String json) {
                Gson gson = new Gson();
                GetGoodCommentListResponse getGoodCommentListResponse = gson.fromJson(json, GetGoodCommentListResponse.class);
                if (getGoodCommentListResponse.code == 0) {
                    LogUtils.e("json:" + json);
                    loadingDialog.dismiss();
                    setGoodComment(getGoodCommentListResponse.dataList);
                } else {
                    loadingDialog.dismiss();
                    DialogUtils.showAlertDialog(mContext,
                            getGoodCommentListResponse.resultText);
                }


            }

            @Override
            public void dealWithError(String address, String error) {
                loadingDialog.dismiss();
                DialogUtils.showAlertDialog(mContext, error);
            }
        });
    }

    public void setGoodComment(List<GoodCommentBean> goodCommentBeanList) {
        if (commentsAdapter == null) {
            commentsAdapter = new CommentsAdapter(mContext, goodCommentBeanList);
            lv_comment.setAdapter(commentsAdapter);
        } else {
            commentsAdapter.setDate(goodCommentBeanList);
            commentsAdapter.notifyDataSetChanged();
        }
    }
}