package com.tesu.manicurehouse.tabpager;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.tesu.manicurehouse.R;
import com.tesu.manicurehouse.adapter.ShowAdapter;
import com.tesu.manicurehouse.base.ViewTabBasePager;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * @作者: 许明达
 * @创建时间: 2016-4-27下午5:24:30
 * @版权: 特速版权所有
 * @描述: 详情页面
 * @更新人:
 * @更新时间:
 * @更新内容: TODO
 */
public class DetailsPager extends ViewTabBasePager  {
    // 分类listview
    @ViewInject(R.id.wv_desc)
    private WebView wv_desc;
    protected static final int ERROR = 0;

    protected static final int SUCCESS = 1;

    protected static final int TIME_OUT = 2;
    protected static final int NETWORK_NOT_OPEN = 3;
    protected static final int EMPTY = 4;
    // 0 没有下一页
    private int next;

    /**
     * 关注品牌的页数，1表示第一页
     */
    private int currentPage = 1;
    private ShowAdapter showAdapter;
    private String goods_desc;
    //css显示图片样式
    private String CSS_STYPE = "<head><style>img{max-width:340px !important;}</style></head>";
    public DetailsPager(Context context, String goods_desc) {
        super(context);
        this.goods_desc = goods_desc;
    }

    @Override
    protected View initView() {
        View view = View.inflate(mContext,
                R.layout.details_pager, null);
        ViewUtils.inject(this, view);
        return view;
    }

    @Override
    public void initData() {
        wv_desc.loadDataWithBaseURL(null, CSS_STYPE + goods_desc, "text/html", "utf-8", null);
    }



}