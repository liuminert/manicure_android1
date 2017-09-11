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

import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.tesu.manicurehouse.R;
import com.tesu.manicurehouse.adapter.ListofPopularAdapter;
import com.tesu.manicurehouse.adapter.PersonalCommunicationAdapter;
import com.tesu.manicurehouse.base.BaseActivity;
import com.tesu.manicurehouse.base.MyVolley;
import com.tesu.manicurehouse.protocol.GetBackgroupPicProtocol;
import com.tesu.manicurehouse.protocol.GetPostListProtocol;
import com.tesu.manicurehouse.request.GetBackgroupPicRequest;
import com.tesu.manicurehouse.request.GetPostListRequest;
import com.tesu.manicurehouse.response.GetBackgroupPicResponse;
import com.tesu.manicurehouse.response.GetPostListResponse;
import com.tesu.manicurehouse.support.PercentRelativeLayout;
import com.tesu.manicurehouse.util.BitmapBlurUtils;
import com.tesu.manicurehouse.util.DialogUtils;
import com.tesu.manicurehouse.util.LogUtils;
import com.tesu.manicurehouse.util.LoginUtils;
import com.tesu.manicurehouse.util.PictureOption;
import com.tesu.manicurehouse.util.SharedPrefrenceUtils;
import com.tesu.manicurehouse.util.UIUtils;
import com.tesu.manicurehouse.widget.MyListView;

import java.util.ArrayList;
import java.util.List;

/**
 * 类描述：
 * 创建人：许明达
 * 创建时间：2016/9/1 10:40
 * 测试室页面
 */
public class MeasurementChamberActivity extends BaseActivity implements View.OnClickListener {

    private ImageView iv_top_back;
    private View rootView;
    private ListofPopularAdapter personalCommunicationAdapter;
    private ListView lv_post;
    private ImageView iv_release;
    private ImageView iv_goto_top;
    private String url;
    private Dialog loadingDialog;
    private GetPostListResponse getPostListResponse;
    private GetBackgroupPicResponse getBackgroupPicResponse;
    private ImageView iv_bg_image;
    private Dialog mDialog;
    @Override
    protected View initView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        rootView = View.inflate(this, R.layout.activity_measurement_chamber, null);
        setContentView(rootView);
        iv_bg_image= (ImageView) rootView.findViewById(R.id.iv_bg_image);
        iv_top_back = (ImageView) rootView.findViewById(R.id.iv_top_back);
        iv_release=(ImageView)rootView.findViewById(R.id.iv_release);
        lv_post=(ListView)rootView.findViewById(R.id.lv_post);
        iv_goto_top= (ImageView) rootView.findViewById(R.id.iv_goto_top);
        initData();
        return null;
    }


    public void initData() {
        iv_release.setOnClickListener(this);
        iv_goto_top.setOnClickListener(this);
        iv_top_back.setOnClickListener(this);
        if (!ImageLoader.getInstance().isInited()) {
            ImageLoader.getInstance()
                    .init(ImageLoaderConfiguration.createDefault(UIUtils
                            .getContext()));
        }
        loadingDialog = DialogUtils.createLoadDialog(MeasurementChamberActivity.this, true);
        runAsyncTask();
//        getBackgroupPic();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_release:{
                if(LoginUtils.isLogin()) {
                    Intent intent=new Intent(MeasurementChamberActivity.this,ReleaseNewPostActivity.class);
                    intent.putExtra("type",1);
                    intent.putExtra("set_check",1);
                    UIUtils.startActivityForResultNextAnim(intent,1);
                }
                else{
                    mDialog = DialogUtils.showAlertDialog(MeasurementChamberActivity.this,
                            "请先登陆您的账号!", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mDialog.dismiss();
                                    Intent intent = new Intent(MeasurementChamberActivity.this, LoginActivity.class);
                                    intent.putExtra("type", 3);
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
        getPostListRequest.map.put("user_id", SharedPrefrenceUtils.getString(MeasurementChamberActivity.this, "user_id"));
        getPostListRequest.map.put("check", "1");
        getPostListRequest.map.put("type", "1");
        getPostListRequest.map.put("pageNo", "1");
        getPostListRequest.map.put("pageSize", "100");

        MyVolley.uploadNoFile(MyVolley.POST, url, getPostListRequest.map, new MyVolley.VolleyCallback() {
            @Override
            public void dealWithJson(String address, String json) {
                LogUtils.e("json:" + json);
                Gson gson = new Gson();
                getPostListResponse = gson.fromJson(json, GetPostListResponse.class);
                if (getPostListResponse.code.equals("0")) {
                    loadingDialog.dismiss();
                    if (getPostListResponse.dataList.size() > 0) {
                        setDate(getPostListResponse.dataList);
                    }
                } else {
                    loadingDialog.dismiss();
                    DialogUtils.showAlertDialog(MeasurementChamberActivity.this,
                            getPostListResponse.resultText);
                }


            }

            @Override
            public void dealWithError(String address, String error) {
                loadingDialog.dismiss();
                DialogUtils.showAlertDialog(MeasurementChamberActivity.this, error);
            }
        });
    }

    public void setDate( List<GetPostListResponse.PostBean> dataList){
        if(personalCommunicationAdapter==null){
            personalCommunicationAdapter=new ListofPopularAdapter(MeasurementChamberActivity.this,dataList);
        }
        else{
            personalCommunicationAdapter.setDataList(dataList);
            personalCommunicationAdapter.notifyDataSetChanged();
        }
        lv_post.setAdapter(personalCommunicationAdapter);
        lv_post.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MeasurementChamberActivity.this, CommunicationInfoActivity.class);
                intent.putExtra("post_id", getPostListResponse.dataList.get(position).post_id);
                UIUtils.startActivityForResultNextAnim(intent, 1);
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1){
            if(resultCode==1){
                runAsyncTask();
            }
        }
    }

//    public void getBackgroupPic() {
//        GetBackgroupPicProtocol getBackgroupPicProtocol = new GetBackgroupPicProtocol();
//        GetBackgroupPicRequest getBackgroupPicRequest = new GetBackgroupPicRequest();
//        url = getBackgroupPicProtocol.getApiFun();
//        getBackgroupPicRequest.map.put("position", "1");
//
//        MyVolley.uploadNoFile(MyVolley.POST, url, getBackgroupPicRequest.map, new MyVolley.VolleyCallback() {
//            @Override
//            public void dealWithJson(String address, String json) {
//                LogUtils.e("json:" + json);
//                Gson gson = new Gson();
//                getBackgroupPicResponse = gson.fromJson(json, GetBackgroupPicResponse.class);
//                if (getBackgroupPicResponse.code.equals("0")) {
//                    if (!TextUtils.isEmpty(getBackgroupPicResponse.pic)) {
//                        ImageLoader.getInstance().displayImage(getBackgroupPicResponse.pic, iv_bg_image, PictureOption.getBgOptions());
//                    }
//                } else {
//                    DialogUtils.showAlertDialog(MeasurementChamberActivity.this,
//                            getBackgroupPicResponse.resultText);
//                }
//
//
//            }
//
//            @Override
//            public void dealWithError(String address, String error) {
//                DialogUtils.showAlertDialog(MeasurementChamberActivity.this, error);
//            }
//        });
//    }
}
