package com.tesu.manicurehouse.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.tesu.manicurehouse.R;
import com.tesu.manicurehouse.adapter.ClassifyContentAdapter;
import com.tesu.manicurehouse.adapter.ClassifyTitleAdapter;
import com.tesu.manicurehouse.adapter.HomeAdapter;
import com.tesu.manicurehouse.adapter.HomeClassifyAdapter;
import com.tesu.manicurehouse.adapter.ShoppingcarAdapter;
import com.tesu.manicurehouse.adapter.ShowAdapter;
import com.tesu.manicurehouse.adapter.ShowClassifyAdapter;
import com.tesu.manicurehouse.base.BaseActivity;
import com.tesu.manicurehouse.base.MyVolley;
import com.tesu.manicurehouse.callback.OnCallVideoList;
import com.tesu.manicurehouse.protocol.GetVideoCategoryListProtocol;
import com.tesu.manicurehouse.protocol.GetVideoListProtocol;
import com.tesu.manicurehouse.request.GetVideoCategoryListRequest;
import com.tesu.manicurehouse.request.GetVideoListRequest;
import com.tesu.manicurehouse.response.GetVideoCategoryListResponse;
import com.tesu.manicurehouse.response.GetVideoListResponse;
import com.tesu.manicurehouse.util.DialogUtils;
import com.tesu.manicurehouse.util.LogUtils;
import com.tesu.manicurehouse.util.UIUtils;

import java.util.ArrayList;
import java.util.List;

/*
 * 类描述：
 * 创建人：许明达
 * 创建时间：2016/8/16 11:40
 * 搜索页面
 */
public class ClassifyActivity extends BaseActivity implements View.OnClickListener ,OnCallVideoList{

    private TextView tv_top_back;
    private View rootView;
    private ListView lv_classify_title;
    private GridView gv_classify;
    private ClassifyTitleAdapter classifyTitleAdapter;
    private ClassifyContentAdapter classifyContentAdapter;
    private String url;
    private Dialog loadingDialog;
    private GetVideoCategoryListResponse getVideoCategoryListResponse;
    private GetVideoListResponse getVideoListResponse;
    private int video_category_id;
    /** 显示图片的宽 */
    private int width;
    private HomeClassifyAdapter homeAdapter;
    private int viewPostion;
    private ShowClassifyAdapter showAdapter;
    private ImageView iv_search;
    @Override
    protected View initView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        rootView = View.inflate(this, R.layout.activity_classify, null);
        setContentView(rootView);
        iv_search=(ImageView)rootView.findViewById(R.id.iv_search);
        lv_classify_title=(ListView)rootView.findViewById(R.id.lv_classify_title);
        gv_classify=(GridView)rootView.findViewById(R.id.gv_classify);
        tv_top_back = (TextView) rootView.findViewById(R.id.tv_top_back);
        tv_top_back.setOnClickListener(this);

        viewPostion = getIntent().getIntExtra("postion", 0);

        initData();
        return null;
    }


    public void initData() {
        width = (((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getWidth()) / 2;
        loadingDialog = DialogUtils.createLoadDialog(ClassifyActivity.this, true);
        iv_search.setOnClickListener(this);
        runAsyncTask();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_search: {
                Intent intent=new Intent(this, SearchActivity.class);
                intent.putExtra("type",1);
                intent.putExtra("position",2);
                UIUtils.startActivityNextAnim(intent);
                setFinish();
                break;
            }
            case R.id.tv_top_back:
                finish();
                overridePendingTransition(R.anim.animprv_in, R.anim.animprv_out);
                break;
        }
    }
    public void runAsyncTask() {
        loadingDialog.show();
        GetVideoCategoryListProtocol getVideoCategoryListProtocoll = new GetVideoCategoryListProtocol();
        GetVideoCategoryListRequest getVideoCategoryListRequest = new GetVideoCategoryListRequest();
        url = getVideoCategoryListProtocoll.getApiFun();
        getVideoCategoryListRequest.map.put("pageNo", "1");
        getVideoCategoryListRequest.map.put("pageSize", "100");


        MyVolley.uploadNoFile(MyVolley.POST, url, getVideoCategoryListRequest.map, new MyVolley.VolleyCallback() {
            @Override
            public void dealWithJson(String address, String json) {

                Gson gson = new Gson();
                getVideoCategoryListResponse = gson.fromJson(json, GetVideoCategoryListResponse.class);
                LogUtils.e("sendVerificationCodeResponse:" + json.toString());
                if (getVideoCategoryListResponse.code.equals("0")) {
                    loadingDialog.dismiss();
                    setDate(getVideoCategoryListResponse.dataList);
                } else {
                    loadingDialog.dismiss();
                    DialogUtils.showAlertDialog(ClassifyActivity.this,
                            getVideoCategoryListResponse.resultText);
                }


            }

            @Override
            public void dealWithError(String address, String error) {
                loadingDialog.dismiss();
                DialogUtils.showAlertDialog(ClassifyActivity.this, error);
            }
        });
    }
    public void runGetVideoList() {
        loadingDialog.show();
        GetVideoListProtocol getVideoListProtocol = new GetVideoListProtocol();
        GetVideoListRequest getVideoListRequest = new GetVideoListRequest();
        url = getVideoListProtocol.getApiFun();

        getVideoListRequest.map.put("pageNo","1");
        getVideoListRequest.map.put("pageSize","100");
        getVideoListRequest.map.put("type","0");
        getVideoListRequest.map.put("position",String.valueOf(viewPostion));
        getVideoListRequest.map.put("video_category_id",String.valueOf(video_category_id));
        LogUtils.e("video_category_id:" + video_category_id);
        LogUtils.e("postion:"+viewPostion);
        MyVolley.uploadNoFile(MyVolley.POST, url, getVideoListRequest.map, new MyVolley.VolleyCallback() {
            @Override
            public void dealWithJson(String address, String json) {

                Gson gson = new Gson();
                getVideoListResponse = gson.fromJson(json, GetVideoListResponse.class);
                LogUtils.e("sendVerificationCodeResponse:" + json.toString());
                if (getVideoListResponse.code==0) {
                    loadingDialog.dismiss();
                    if(getVideoListResponse.dataList.size()>=0) {
                        if (classifyContentAdapter == null) {
//                            classifyContentAdapter = new ClassifyContentAdapter(ClassifyActivity.this, getVideoListResponse.dataList);
//                            gv_classify.setAdapter(classifyContentAdapter);
                            if(viewPostion ==0){
                                homeAdapter=new HomeClassifyAdapter(ClassifyActivity.this,getVideoListResponse.dataList);
                                gv_classify.setAdapter(homeAdapter);
                            }else {
                                showAdapter = new ShowClassifyAdapter(0,ClassifyActivity.this,getVideoListResponse.dataList);
                                gv_classify.setAdapter(showAdapter);
                            }
                        }
                        else{
                            if(viewPostion == 0){
                                homeAdapter.setDate(getVideoListResponse.dataList);
                                classifyContentAdapter.notifyDataSetChanged();
                            }else {
                                showAdapter.setDate(getVideoListResponse.dataList);
                                classifyContentAdapter.notifyDataSetChanged();
                            }
                        }

                    }
                    else{
                        DialogUtils.showAlertDialog(ClassifyActivity.this,
                                "没有数据！");
                    }
                } else {
                    loadingDialog.dismiss();
                    DialogUtils.showAlertDialog(ClassifyActivity.this,
                            getVideoListResponse.resultText);
                }


            }

            @Override
            public void dealWithError(String address, String error) {
                loadingDialog.dismiss();
                DialogUtils.showAlertDialog(ClassifyActivity.this, error);
            }
        });
    }
    public void setDate(List<GetVideoCategoryListResponse.DataList> dataLists){
        if(classifyTitleAdapter==null){
            classifyTitleAdapter=new ClassifyTitleAdapter(ClassifyActivity.this,dataLists,this);
        }
        lv_classify_title.setAdapter(classifyTitleAdapter);
        if(dataLists.size()>0){
            classifyTitleAdapter.setState(0);
            video_category_id=dataLists.get(0).video_category_id;
            runGetVideoList();
        }

    }

    @Override
    public void VideoListItemCallBack(int video_category_id) {
        this.video_category_id=video_category_id;
        runGetVideoList();
    }
}
