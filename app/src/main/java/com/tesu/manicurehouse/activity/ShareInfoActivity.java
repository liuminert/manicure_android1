package com.tesu.manicurehouse.activity;

import android.app.Dialog;
import android.content.Intent;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.tesu.manicurehouse.R;
import com.tesu.manicurehouse.adapter.RecommendAdapter;
import com.tesu.manicurehouse.adapter.ShareInfoAdapter;
import com.tesu.manicurehouse.base.BaseActivity;
import com.tesu.manicurehouse.base.MyVolley;
import com.tesu.manicurehouse.bean.ShareDescBean;
import com.tesu.manicurehouse.bean.ShareInfo;
import com.tesu.manicurehouse.protocol.GetShareDescListProtocol;
import com.tesu.manicurehouse.request.GetShareDescListRequest;
import com.tesu.manicurehouse.response.GetFollowInfoResponse;
import com.tesu.manicurehouse.response.GetShareDescListResponse;
import com.tesu.manicurehouse.util.DialogUtils;
import com.tesu.manicurehouse.util.LogUtils;
import com.tesu.manicurehouse.util.SharedPrefrenceUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 类描述：
 * 创建人：许明达
 * 创建时间：2016/8/16 11:40
 * 分享详情页面
 */
public class ShareInfoActivity extends BaseActivity implements View.OnClickListener, PullToRefreshBase.OnRefreshListener {
    private int pageNo = 1;
    //判断是否刷新
    private boolean isRefresh = false;
    private TextView tv_top_back;
    private View rootView;
    private PullToRefreshListView lv_recommend;
    private ShareInfoAdapter shareInfoAdapter;
    private TextView tv_owner_num;
    private TextView tv2;
    //积分
    private int recommendScore;
    //好友数
    private int recommendCnt;
    private List<ShareDescBean> shareDescBeanList;
    private List<ShareDescBean> updateshareDescBeanList=new ArrayList<>();
    public TextView tv_time;
    public TextView tv_people;
    public TextView tv_integral;
    //邀请记录
    private RelativeLayout rl_invited;
    //积分记录
    private RelativeLayout rl_integral;
    private String url;
    private Dialog loadingDialog;
    private GetShareDescListResponse getShareDescListResponse;
    //0邀请记录，1积分记录
    private int search_type = 0;
    private TextView tv_invited_title;
    private TextView tv_integral_title;

    @Override
    protected View initView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        rootView = View.inflate(this, R.layout.activity_share_info, null);
        setContentView(rootView);

        rl_invited = (RelativeLayout) rootView.findViewById(R.id.rl_invited);
        rl_integral = (RelativeLayout) rootView.findViewById(R.id.rl_integral);
        tv_owner_num = (TextView) rootView.findViewById(R.id.tv_owner_num);
        tv2 = (TextView) rootView.findViewById(R.id.tv2);
        tv_time = (TextView) rootView.findViewById(R.id.tv_time);
        tv_people = (TextView) rootView.findViewById(R.id.tv_people);
        tv_invited_title= (TextView) rootView.findViewById(R.id.tv_invited_title);
        tv_integral_title= (TextView) rootView.findViewById(R.id.tv_integral_title);
        tv_integral = (TextView) rootView.findViewById(R.id.tv_integral);
        tv_top_back = (TextView) rootView.findViewById(R.id.tv_top_back);
        lv_recommend = (PullToRefreshListView) rootView.findViewById(R.id.lv_recommend);
        initData();
        return null;
    }


    public void initData() {
        Intent intent = getIntent();
        shareDescBeanList = (List<ShareDescBean>) intent.getSerializableExtra("ShareDescBean");
        recommendScore = intent.getIntExtra("recommendScore", 0);
        recommendCnt = intent.getIntExtra("recommendCnt", 0);
        loadingDialog = DialogUtils.createLoadDialog(ShareInfoActivity.this, true);
        tv_top_back.setOnClickListener(this);
        rl_integral.setOnClickListener(this);
        rl_invited.setOnClickListener(this);
        tv_owner_num.setText(recommendCnt + "位");
        tv2.setText("共获得" + recommendScore + "积分");
        setDate(shareDescBeanList);
    }

    public void setDate(List<ShareDescBean> shareDescBeanList) {
        LogUtils.e("shareDescBeanList:" + shareDescBeanList.size());
        if (shareInfoAdapter == null) {
            shareInfoAdapter = new ShareInfoAdapter(ShareInfoActivity.this, shareDescBeanList, search_type);
        }
        else{
            shareInfoAdapter.setDate(shareDescBeanList,search_type);
            shareInfoAdapter.notifyDataSetChanged();
        }
        lv_recommend.setAdapter(shareInfoAdapter);
        lv_recommend.setOnRefreshListener(this);
        lv_recommend.setMode(PullToRefreshBase.Mode.BOTH);
        lv_recommend.getLoadingLayoutProxy(false, true).setPullLabel("上拉加载");
        lv_recommend.getLoadingLayoutProxy(false, true).setRefreshingLabel("加载中");
        lv_recommend.getLoadingLayoutProxy(false, true).setReleaseLabel("放开以刷新");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_integral: {
                updateshareDescBeanList.clear();
                rl_invited.setBackgroundResource(R.mipmap.bt_video_noselectable);
                rl_integral.setBackgroundResource(R.mipmap.bt_video_selectable);
                tv_invited_title.setTextColor(getResources().getColor(R.color.text_color_black));
                tv_integral_title.setTextColor(getResources().getColor(R.color.white));
                tv_time.setText("积分获得");
                tv_people.setText("获得时间");
                tv_integral.setText("积分数目");
                search_type = 1;
                pageNo=1;
                updateshareDescBeanList.clear();
                getShareDescList();
                break;
            }
            case R.id.rl_invited: {
                updateshareDescBeanList.clear();
                rl_integral.setBackgroundResource(R.mipmap.bt_video_noselectable);
                rl_invited.setBackgroundResource(R.mipmap.bt_video_selectable);
                tv_integral_title.setTextColor(getResources().getColor(R.color.text_color_black));
                tv_invited_title.setTextColor(getResources().getColor(R.color.white));
                tv_time.setText("用户名");
                tv_people.setText("成交时间");
                tv_integral.setText("认证手机");
                search_type = 0;
                pageNo=1;
                updateshareDescBeanList.clear();
                getShareDescList();
                break;
            }
            case R.id.tv_top_back:
                finish();
                overridePendingTransition(R.anim.animprv_in, R.anim.animprv_out);
                break;
        }
    }

    @Override
    public void onRefresh(PullToRefreshBase refreshView) {
        if (!isRefresh) {
            isRefresh = true;
            if (lv_recommend.isHeaderShown()) {
                pageNo = 1;
                updateshareDescBeanList.clear();
                getShareDescList();
            } else if (lv_recommend.isFooterShown()) {
                pageNo++;
                getShareDescList();
            }

        }
    }

    public void getShareDescList() {
        loadingDialog.show();
        GetShareDescListProtocol getShareDescListProtocol = new GetShareDescListProtocol();
        GetShareDescListRequest getShareDescListRequest = new GetShareDescListRequest();
        url = getShareDescListProtocol.getApiFun();
        getShareDescListRequest.map.put("user_id", SharedPrefrenceUtils.getString(ShareInfoActivity.this, "user_id"));
        getShareDescListRequest.map.put("search_type", String.valueOf(search_type));
        getShareDescListRequest.map.put("pageNo", String.valueOf(pageNo));
        getShareDescListRequest.map.put("pageSize", "10");

        MyVolley.uploadNoFile(MyVolley.POST, url, getShareDescListRequest.map, new MyVolley.VolleyCallback() {
            @Override
            public void dealWithJson(String address, String json) {

                Gson gson = new Gson();
                getShareDescListResponse = gson.fromJson(json, GetShareDescListResponse.class);
                LogUtils.e("sendVerificationCodeResponse:" + json.toString());
                if (getShareDescListResponse.code == 0) {
                    if(getShareDescListResponse.dataList.size()>0){
                        updateshareDescBeanList.addAll(getShareDescListResponse.dataList);
                        setDate(updateshareDescBeanList);
                    }
                    loadingDialog.dismiss();
                } else {
                    loadingDialog.dismiss();
                    DialogUtils.showAlertDialog(ShareInfoActivity.this,
                            getShareDescListResponse.resultText);
                }
                if(isRefresh){
                    isRefresh=false;
                    lv_recommend.onRefreshComplete();
                }

            }

            @Override
            public void dealWithError(String address, String error) {
                loadingDialog.dismiss();
                DialogUtils.showAlertDialog(ShareInfoActivity.this, error);
            }
        });
    }


}
