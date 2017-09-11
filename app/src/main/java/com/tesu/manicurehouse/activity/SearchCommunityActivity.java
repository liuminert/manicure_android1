package com.tesu.manicurehouse.activity;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.tesu.manicurehouse.R;
import com.tesu.manicurehouse.adapter.SearchCommunityAdapter;
import com.tesu.manicurehouse.adapter.ShopAdapter;
import com.tesu.manicurehouse.base.BaseActivity;
import com.tesu.manicurehouse.base.MyVolley;
import com.tesu.manicurehouse.base.ViewTabBasePager;
import com.tesu.manicurehouse.bean.GoodBean;
import com.tesu.manicurehouse.protocol.GetGoodListProtocol;
import com.tesu.manicurehouse.protocol.SearchCommunityProtocol;
import com.tesu.manicurehouse.request.GetGoodListRequest;
import com.tesu.manicurehouse.request.SearchCommunityRequest;
import com.tesu.manicurehouse.response.GetGoodListResponse;
import com.tesu.manicurehouse.response.SearchCommunityResponse;
import com.tesu.manicurehouse.tabpager.ManicuristPager;
import com.tesu.manicurehouse.tabpager.PersonalPager;
import com.tesu.manicurehouse.util.DialogUtils;
import com.tesu.manicurehouse.util.LogUtils;
import com.tesu.manicurehouse.util.SharedPrefrenceUtils;
import com.tesu.manicurehouse.util.UIUtils;
import com.tesu.manicurehouse.widget.NoScrollViewPager;
import com.tesu.manicurehouse.widget.TabSlidingIndicator;

import java.util.ArrayList;
import java.util.List;

/**
 * 类描述：
 * 创建人：许明达
 * 创建时间：2016/8/16 11:40
 * 搜索页面
 */
public class SearchCommunityActivity extends BaseActivity implements View.OnClickListener {

    private TextView tv_top_back;
    private View rootView;
    private ListView lv_community;
    private String keyword;
    private String url;
    private Dialog loadingDialog;
    private SearchCommunityResponse searchCommunityResponse;
    private int pageNo = 1;
    private SearchCommunityAdapter searchCommunityAdapter;
    //判断是否刷新
    private boolean isRefresh = false;
    private List<SearchCommunityResponse.CommunityBean> communityBeanList;

    protected View initView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        rootView = View.inflate(this, R.layout.activity_search_community, null);
        setContentView(rootView);
        tv_top_back = (TextView) rootView.findViewById(R.id.tv_top_back);
        lv_community = (ListView) rootView.findViewById(R.id.lv_community);
        initData();
        return null;
    }


    public void initData() {
        communityBeanList = new ArrayList<SearchCommunityResponse.CommunityBean>();
        loadingDialog = DialogUtils.createLoadDialog(SearchCommunityActivity.this, true);
        Intent intent = getIntent();
        keyword = intent.getStringExtra("keyword");
        tv_top_back.setOnClickListener(this);
        runAsyncTask();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_top_back:
                finish();
                overridePendingTransition(R.anim.animprv_in, R.anim.animprv_out);
                break;
        }
    }

    public void runAsyncTask() {
        loadingDialog.show();
        SearchCommunityProtocol searchCommunityProtocol = new SearchCommunityProtocol();
        SearchCommunityRequest searchCommunityRequest = new SearchCommunityRequest();
        url = searchCommunityProtocol.getApiFun();
        searchCommunityRequest.map.put("pageNo", String.valueOf(pageNo));
        searchCommunityRequest.map.put("pageSize", "10");
        searchCommunityRequest.map.put("keyword", keyword);
        searchCommunityRequest.map.put("user_id", SharedPrefrenceUtils.getString(SearchCommunityActivity.this, "user_id"));


        MyVolley.uploadNoFile(MyVolley.POST, url, searchCommunityRequest.map, new MyVolley.VolleyCallback() {
            @Override
            public void dealWithJson(String address, String json) {

                Gson gson = new Gson();
                searchCommunityResponse = gson.fromJson(json, SearchCommunityResponse.class);
                LogUtils.e("sendVerificationCodeResponse:" + json.toString());
                if (searchCommunityResponse.code == 0) {
                    loadingDialog.dismiss();
                    if (searchCommunityResponse.dataList.size() > 0) {
                        communityBeanList.addAll(searchCommunityResponse.dataList);
                        setDate(communityBeanList);
                    }
//                    else{
//                        DialogUtils.showAlertDialog(SearchCommunityActivity.this, "没有更多数据了！");
//                    }
                } else {
                    loadingDialog.dismiss();
                    DialogUtils.showAlertDialog(SearchCommunityActivity.this,
                            searchCommunityResponse.resultText);
                }
//                if (isRefresh) {
//                    isRefresh = false;
//                    lv_community.onRefreshComplete();
//                }

            }

            @Override
            public void dealWithError(String address, String error) {
                loadingDialog.dismiss();
                DialogUtils.showAlertDialog(SearchCommunityActivity.this, error);
            }
        });
    }

    public void setDate(final List<SearchCommunityResponse.CommunityBean> communityBeanList) {
        if (searchCommunityAdapter == null) {
            searchCommunityAdapter = new SearchCommunityAdapter(SearchCommunityActivity.this, communityBeanList);
        }
        lv_community.setAdapter(searchCommunityAdapter);
//        lv_community.setOnRefreshListener(this);
//        lv_community.setMode(PullToRefreshBase.Mode.BOTH);
//        lv_community.getLoadingLayoutProxy(false, true).setPullLabel("上拉加载");
//        lv_community.getLoadingLayoutProxy(false, true).setRefreshingLabel("加载中");
//        lv_community.getLoadingLayoutProxy(false, true).setReleaseLabel("放开以刷新");
        lv_community.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(communityBeanList.get(position).type==1) {
                    Intent intent = new Intent(SearchCommunityActivity.this, CommunicationInfoActivity.class);
                    intent.putExtra("post_id", communityBeanList.get(position).id);
                    UIUtils.startActivityNextAnim(intent);
                }
                else{
                    Intent intent = new Intent(SearchCommunityActivity.this, ManicureInformationDetailsActivity.class);
                    intent.putExtra("information_id",String.valueOf(communityBeanList.get(position).id));
                    intent.putExtra("title",communityBeanList.get(position).title);
                    UIUtils.startActivityNextAnim(intent);
                }
            }
        });
    }

//    @Override
//    public void onRefresh(PullToRefreshBase refreshView) {
//        if (!isRefresh) {
//            isRefresh = true;
//            if (lv_community.isHeaderShown()) {
//                pageNo = 1;
//                communityBeanList.clear();
//                runAsyncTask();
//            } else if (lv_community.isFooterShown()) {
//                pageNo++;
//                runAsyncTask();
//            }
//
//        }
//    }
}
