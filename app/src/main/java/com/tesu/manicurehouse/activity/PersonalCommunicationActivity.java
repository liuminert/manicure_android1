package com.tesu.manicurehouse.activity;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;

import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.tesu.manicurehouse.R;
import com.tesu.manicurehouse.adapter.ManicureInformationAdapter;
import com.tesu.manicurehouse.adapter.PersonalCommunicationAdapter;
import com.tesu.manicurehouse.base.BaseActivity;
import com.tesu.manicurehouse.base.MyVolley;
import com.tesu.manicurehouse.protocol.GetBackgroupPicProtocol;
import com.tesu.manicurehouse.protocol.GetInformationListProtocol;
import com.tesu.manicurehouse.protocol.GetPostListProtocol;
import com.tesu.manicurehouse.request.GetBackgroupPicRequest;
import com.tesu.manicurehouse.request.GetInformationListRequest;
import com.tesu.manicurehouse.request.GetPostListRequest;
import com.tesu.manicurehouse.response.GetBackgroupPicResponse;
import com.tesu.manicurehouse.response.GetInformationListResponse;
import com.tesu.manicurehouse.response.GetPostListResponse;
import com.tesu.manicurehouse.support.PercentRelativeLayout;
import com.tesu.manicurehouse.util.BitmapBlurUtils;
import com.tesu.manicurehouse.util.DialogUtils;
import com.tesu.manicurehouse.util.LogUtils;
import com.tesu.manicurehouse.util.LoginUtils;
import com.tesu.manicurehouse.util.PictureOption;
import com.tesu.manicurehouse.util.SharedPrefrenceUtils;
import com.tesu.manicurehouse.util.UIUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 类描述：
 * 创建人：许明达
 * 创建时间：2016/9/1 10:40
 * 个人交流页面
 */
public class PersonalCommunicationActivity extends BaseActivity implements View.OnClickListener, PullToRefreshBase.OnRefreshListener {

    private ImageView iv_top_back;
    private View rootView;
    private PersonalCommunicationAdapter personalCommunicationAdapter;
    private PullToRefreshListView lv_post;
    private ImageView iv_goto_top;

    private ImageView iv_release;
    private String url;
    private Dialog loadingDialog;
    private GetPostListResponse getPostListResponse;

    private Dialog mDialog;
    private boolean isRefresh = false;
    private int pageNo=1;
    private List<GetPostListResponse.PostBean> postBeanList;
    @Override
    protected View initView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        rootView = View.inflate(this, R.layout.activity_personal_communication, null);
        setContentView(rootView);
        iv_goto_top= (ImageView) rootView.findViewById(R.id.iv_goto_top);
        iv_top_back = (ImageView) rootView.findViewById(R.id.iv_top_back);
        iv_release=(ImageView)rootView.findViewById(R.id.iv_release);

        lv_post=(PullToRefreshListView)rootView.findViewById(R.id.lv_post);

        initData();
        return null;
    }


    public void initData() {
        if (!ImageLoader.getInstance().isInited()) {
//            ImageLoader.getInstance()
//                    .init(ImageLoaderConfiguration.createDefault(UIUtils
//                            .getContext()));

            DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                    .cacheInMemory(true).imageScaleType(ImageScaleType.EXACTLY)
                    .cacheOnDisk(true).build();
            ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                    PersonalCommunicationActivity.this)
                    .threadPoolSize(3)
                    .threadPriority(Thread.NORM_PRIORITY - 2)
                    .denyCacheImageMultipleSizesInMemory()
                    .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                    .tasksProcessingOrder(QueueProcessingType.LIFO)
                    .denyCacheImageMultipleSizesInMemory()
                    .memoryCache(new WeakMemoryCache())
                    .memoryCacheSize((int) (2 * 1024 * 1024))
                    .memoryCacheSizePercentage(13)
                    .diskCacheSize(50 * 1024 * 1024).diskCacheFileCount(100)
                    .diskCacheFileNameGenerator(new HashCodeFileNameGenerator())
                    .defaultDisplayImageOptions(defaultOptions).writeDebugLogs() // Remove
                    .build();
            ImageLoader.getInstance().init(config);
        }
        postBeanList=new ArrayList<GetPostListResponse.PostBean>();
        loadingDialog = DialogUtils.createLoadDialog(PersonalCommunicationActivity.this, true);
        runAsyncTask();

        iv_goto_top.setOnClickListener(this);
        iv_release.setOnClickListener(this);

        iv_top_back.setOnClickListener(this);
        lv_post.setOnRefreshListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.iv_release:{
                if(LoginUtils.isLogin()) {
                    Intent intent = new Intent(PersonalCommunicationActivity.this, ReleaseNewPostActivity.class);
                    intent.putExtra("type",0);
                    UIUtils.startActivityForResultNextAnim(intent, 1);
                }
                else{
                    mDialog = DialogUtils.showAlertDialog(PersonalCommunicationActivity.this,
                            "请先登陆您的账号!", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mDialog.dismiss();
                                    Intent intent = new Intent(PersonalCommunicationActivity.this, LoginActivity.class);
                                    intent.putExtra("type",3);
                                    UIUtils.startActivityNextAnim(intent);
                                }
                            });
                }
                break;
            }
            case R.id.iv_goto_top: {
                lv_post.setSelection(0);
                break;
            }
            case R.id.iv_top_back: {
                finish();
                overridePendingTransition(R.anim.animprv_in, R.anim.animprv_out);
                break;
            }
        }
    }
    public void runAsyncTask() {
        loadingDialog.show();
        GetPostListProtocol getPostListProtocol = new GetPostListProtocol();
        GetPostListRequest getPostListRequest = new GetPostListRequest();
        url = getPostListProtocol.getApiFun();
        getPostListRequest.map.put("user_id", SharedPrefrenceUtils.getString(PersonalCommunicationActivity.this, "user_id"));
//        getPostListRequest.map.put("essence", "0");
//        getPostListRequest.map.put("hot", "0");
//        getPostListRequest.map.put("check", "0");
        getPostListRequest.map.put("type", "0");
        getPostListRequest.map.put("pageNo", String.valueOf(pageNo));
        getPostListRequest.map.put("pageSize", "10");

        MyVolley.uploadNoFile(MyVolley.POST, url, getPostListRequest.map, new MyVolley.VolleyCallback() {
            @Override
            public void dealWithJson(String address, String json) {
                LogUtils.e("json:" + json);
                Gson gson = new Gson();
                getPostListResponse = gson.fromJson(json, GetPostListResponse.class);
                if (getPostListResponse.code.equals("0")) {
                    loadingDialog.dismiss();
                    if (getPostListResponse.dataList.size() > 0) {

                        if (pageNo == 1) {
                            postBeanList.clear();
                            postBeanList.add(null);
                        }
                        postBeanList.addAll(getPostListResponse.getDataList());
                        setDate(postBeanList);
                    }
                } else {
                    loadingDialog.dismiss();
                    DialogUtils.showAlertDialog(PersonalCommunicationActivity.this,
                            getPostListResponse.resultText);
                }

                if (isRefresh) {
                    isRefresh = false;
                    lv_post.onRefreshComplete();
                }
            }

            @Override
            public void dealWithError(String address, String error) {
                loadingDialog.dismiss();
                DialogUtils.showAlertDialog(PersonalCommunicationActivity.this, error);
                if (isRefresh) {
                    isRefresh = false;
                    lv_post.onRefreshComplete();
                }
            }
        });
    }

    public void setDate(  List<GetPostListResponse.PostBean> dataList){
        if(personalCommunicationAdapter==null){
            personalCommunicationAdapter=new PersonalCommunicationAdapter(PersonalCommunicationActivity.this,dataList);
            lv_post.setAdapter(personalCommunicationAdapter);
        }
        else{
            personalCommunicationAdapter.notifyDataSetChanged();
        }

        lv_post.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position>1){
                    Intent intent = new Intent(PersonalCommunicationActivity.this, CommunicationInfoActivity.class);
                    intent.putExtra("post_id",postBeanList.get(position-1).post_id);
                    UIUtils.startActivityForResultNextAnim(intent, 1);
                }
                LogUtils.e("position:"+position+" '   "+postBeanList.size());
            }
        });
        lv_post.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1){
            if(resultCode==1){
                runAsyncTask();
                pageNo = 1;
            }
        }
    }


    @Override
    public void onRefresh(PullToRefreshBase refreshView) {
        if (!isRefresh) {
            isRefresh = true;
            if (lv_post.isHeaderShown()) {
                pageNo = 1;
                postBeanList.clear();
                if(personalCommunicationAdapter!=null) {
                    personalCommunicationAdapter.setLoading(false);
                }
                runAsyncTask();
            } else if (lv_post.isFooterShown()) {
                pageNo++;
                runAsyncTask();
            }
        }
    }
}
