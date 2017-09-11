package com.tesu.manicurehouse.activity;

import android.app.Dialog;
import android.content.Intent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.tesu.manicurehouse.R;
import com.tesu.manicurehouse.adapter.MyCommentAdapter;
import com.tesu.manicurehouse.adapter.TutorialAdapter;
import com.tesu.manicurehouse.base.BaseActivity;
import com.tesu.manicurehouse.base.MyVolley;
import com.tesu.manicurehouse.bean.VideoInfoBean;
import com.tesu.manicurehouse.protocol.GetUserCommentListProtocol;
import com.tesu.manicurehouse.protocol.GetUserInfoProtocol;
import com.tesu.manicurehouse.request.GetUserCommentListRequest;
import com.tesu.manicurehouse.request.GetUserInfoRequest;
import com.tesu.manicurehouse.response.GetUserCommentListResponse;
import com.tesu.manicurehouse.response.GetUserInfoResponse;
import com.tesu.manicurehouse.response.VideoInfoResponse;
import com.tesu.manicurehouse.util.DialogUtils;
import com.tesu.manicurehouse.util.LogUtils;
import com.tesu.manicurehouse.util.SharedPrefrenceUtils;
import com.tesu.manicurehouse.util.UIUtils;
import com.tesu.manicurehouse.util.Utility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 类描述：
 * 创建人：许明达
 * 创建时间：2016/8/29 10:40
 * 我的评论页面
 */
public class MyCommentActivity extends BaseActivity implements View.OnClickListener {

    private ImageView iv_top_back;
    private View rootView;
    private ListView lv_my_comment;
    private MyCommentAdapter myCommentAdapter;
    private String url;
    private Dialog loadingDialog;
    private GetUserCommentListResponse getUserCommentListResponse;
    @Override
    protected View initView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        rootView = View.inflate(this, R.layout.activity_my_comment, null);
        setContentView(rootView);
        lv_my_comment=(ListView)rootView.findViewById(R.id.lv_my_comment);
        iv_top_back = (ImageView) rootView.findViewById(R.id.iv_top_back);
        iv_top_back.setOnClickListener(this);
        initData();
        return null;
    }


    public void initData() {
        loadingDialog = DialogUtils.createLoadDialog(MyCommentActivity.this, true);
        runAsyncTask();
    }
    public void setDate(List<GetUserCommentListResponse.DataList> dataLists){
        if(myCommentAdapter==null){
            myCommentAdapter=new MyCommentAdapter(MyCommentActivity.this,dataLists);
        }
        lv_my_comment.setAdapter(myCommentAdapter);
        lv_my_comment.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (getUserCommentListResponse.dataList.get(position).type) {
                    case 0: {
                        Intent intent = new Intent(MyCommentActivity.this, CommunicationInfoActivity.class);
                        intent.putExtra("post_id", getUserCommentListResponse.dataList.get(position).value_id);
                        UIUtils.startActivityNextAnim(intent);
                        break;
                    }
                    case 2: {
                        Intent intent = new Intent(MyCommentActivity.this, ManicureInformationDetailsActivity.class);
                        intent.putExtra("information_id", getUserCommentListResponse.dataList.get(position).value_id);
                        intent.putExtra("title", getUserCommentListResponse.dataList.get(position).title);
                        UIUtils.startActivityNextAnim(intent);
                        break;
                    }
                    case 1: {
                        getVideoInfo(getUserCommentListResponse.dataList.get(position).value_id);
                        break;
                    }
                }
            }
        });
//        Utility.setListViewHeightBasedOnChildren(lv_my_comment);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_top_back:
                finish();
                overridePendingTransition(R.anim.animprv_in, R.anim.animprv_out);
                break;
        }
    }

    public void runAsyncTask() {
        loadingDialog.show();
        GetUserCommentListProtocol getUserCommentListProtocol = new GetUserCommentListProtocol();
        GetUserCommentListRequest getUserCommentListRequest = new GetUserCommentListRequest();
        url = getUserCommentListProtocol.getApiFun();
        getUserCommentListRequest.map.put("user_id", SharedPrefrenceUtils.getString(MyCommentActivity.this, "user_id"));
        getUserCommentListRequest.map.put("pageNo","1");
        getUserCommentListRequest.map.put("pageSize", "10000");

        MyVolley.uploadNoFile(MyVolley.POST, url, getUserCommentListRequest.map, new MyVolley.VolleyCallback() {
            @Override
            public void dealWithJson(String address, String json) {

                Gson gson = new Gson();
                getUserCommentListResponse = gson.fromJson(json, GetUserCommentListResponse.class);
                LogUtils.e("sendVerificationCodeResponse:" + json.toString());
                if (getUserCommentListResponse.code.equals("0")) {
                    loadingDialog.dismiss();
                    setDate(getUserCommentListResponse.dataList);
                } else {
                    loadingDialog.dismiss();
                    DialogUtils.showAlertDialog(MyCommentActivity.this,
                            getUserCommentListResponse.resultText);
                }


            }

            @Override
            public void dealWithError(String address, String error) {
                loadingDialog.dismiss();
                DialogUtils.showAlertDialog(MyCommentActivity.this, error);
            }
        });
    }

    /**
     * 获取视频详情
     */
    private void getVideoInfo(final int video_id) {
        loadingDialog.show();
        url = "video/getVideoDesc.do";
        Map<String, String> params = new HashMap<String, String>();
        params.put("video_id", video_id + "");
        params.put("user_id", SharedPrefrenceUtils.getString(MyCommentActivity.this, "user_id"));
        MyVolley.uploadNoFile(MyVolley.POST, url, params, new MyVolley.VolleyCallback() {

            @Override
            public void dealWithJson(String address, String json) {
                loadingDialog.dismiss();
                LogUtils.e("获取视频详情1:" + json);
                Gson gson = new Gson();
                VideoInfoResponse videoInfoResponse = gson.fromJson(json, VideoInfoResponse.class);
                if (videoInfoResponse.getCode() == 0) {
                    try {
                        VideoInfoBean videoInfoBean = videoInfoResponse.getData();
                        Intent intent ;

                        if (videoInfoBean.getType() == 0) {
                            intent = new Intent(MyCommentActivity.this, VideoInfoActivity.class);
                        } else if (videoInfoBean.getType() == 1) {
                            intent = new Intent(MyCommentActivity.this, VideoShowInfoActivity.class);
                        } else {
                            intent = new Intent(MyCommentActivity.this, VideoShowPhotoInfoActivity.class);
                        }

                        intent.putExtra("video_id", video_id);
                        intent.putExtra("video_name", videoInfoBean.getTitle());
                        intent.putExtra("cover_img", videoInfoBean.getCover_img());
                        intent.putExtra("video_url", videoInfoBean.getVideo_url());
                        UIUtils.startActivityNextAnim(intent);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }

                } else {
                    DialogUtils.showAlertDialog(MyCommentActivity.this, videoInfoResponse.getResultText());
                }
            }

            @Override
            public void dealWithError(String address, String error) {
                loadingDialog.dismiss();
                LogUtils.e("获取视频详情错误:" + error);
                DialogUtils.showAlertDialog(MyCommentActivity.this, error);
            }
        });
    }
}
