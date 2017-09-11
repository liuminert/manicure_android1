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
import com.tesu.manicurehouse.protocol.GetPostListProtocol;
import com.tesu.manicurehouse.request.GetPostListRequest;
import com.tesu.manicurehouse.response.GetPostListResponse;
import com.tesu.manicurehouse.support.PercentRelativeLayout;
import com.tesu.manicurehouse.util.BitmapBlurUtils;
import com.tesu.manicurehouse.util.DialogUtils;
import com.tesu.manicurehouse.util.LogUtils;
import com.tesu.manicurehouse.util.PictureOption;
import com.tesu.manicurehouse.util.SharedPrefrenceUtils;
import com.tesu.manicurehouse.util.UIUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 类描述：
 * 创建人：许明达
 * 创建时间：2016/9/1 10:40
 * 人气榜页面
 */
public class ListofPopularActivity extends BaseActivity implements View.OnClickListener {

    private ImageView iv_top_back;
    private View rootView;
    private ListofPopularAdapter listofPopularAdapter;
    private ListView lv_post;
    private ImageView iv_release;
    private ImageView iv_goto_top;
    private String url;
    private Dialog loadingDialog;
    private GetPostListResponse getPostListResponse;
    private int type;

    @Override
    protected View initView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        rootView = View.inflate(this, R.layout.activity_list_of_popular, null);
        setContentView(rootView);
        iv_top_back = (ImageView) rootView.findViewById(R.id.iv_top_back);
        iv_release = (ImageView) rootView.findViewById(R.id.iv_release);
        lv_post = (ListView) rootView.findViewById(R.id.lv_post);
        iv_goto_top = (ImageView) rootView.findViewById(R.id.iv_goto_top);
        initData();
        return null;
    }


    public void initData() {
        Intent intent = getIntent();
        type = intent.getIntExtra("type",0);

        if (!ImageLoader.getInstance().isInited()) {
            ImageLoader.getInstance()
                    .init(ImageLoaderConfiguration.createDefault(UIUtils
                            .getContext()));
        }
        loadingDialog = DialogUtils.createLoadDialog(ListofPopularActivity.this, true);
        runAsyncTask();
        iv_goto_top.setOnClickListener(this);
        iv_top_back.setOnClickListener(this);
        iv_release.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_release: {
                Intent intent = new Intent(ListofPopularActivity.this, ReleaseNewPostActivity.class);
                UIUtils.startActivityForResultNextAnim(intent, 1);
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
        getPostListRequest.map.put("user_id", SharedPrefrenceUtils.getString(ListofPopularActivity.this, "user_id"));
        getPostListRequest.map.put("hot", "1");
        getPostListRequest.map.put("type", type+"");
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
                    DialogUtils.showAlertDialog(ListofPopularActivity.this,
                            getPostListResponse.resultText);
                }


            }

            @Override
            public void dealWithError(String address, String error) {
                loadingDialog.dismiss();
                DialogUtils.showAlertDialog(ListofPopularActivity.this, error);
            }
        });
    }

    public void setDate(List<GetPostListResponse.PostBean> dataList) {
        if (listofPopularAdapter == null) {
            listofPopularAdapter = new ListofPopularAdapter(ListofPopularActivity.this, dataList);
        } else {
            listofPopularAdapter.setDataList(dataList);
            listofPopularAdapter.notifyDataSetChanged();
        }
        lv_post.setAdapter(listofPopularAdapter);
        lv_post.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ListofPopularActivity.this, CommunicationInfoActivity.class);
                intent.putExtra("post_id", getPostListResponse.dataList.get(position).post_id);
                UIUtils.startActivityForResultNextAnim(intent, 1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == 1) {
                runAsyncTask();
            }
        }
    }
}
