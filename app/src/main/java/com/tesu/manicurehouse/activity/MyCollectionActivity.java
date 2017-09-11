package com.tesu.manicurehouse.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.tesu.manicurehouse.R;
import com.tesu.manicurehouse.adapter.MyAttestionAdapter;
import com.tesu.manicurehouse.adapter.MyProductionAdapter;
import com.tesu.manicurehouse.base.BaseActivity;
import com.tesu.manicurehouse.base.MyVolley;
import com.tesu.manicurehouse.bean.CollectionItem;
import com.tesu.manicurehouse.bean.MessageItem;
import com.tesu.manicurehouse.protocol.GetUserCollectionOrWorkListProtocol;
import com.tesu.manicurehouse.protocol.GetUserInfoProtocol;
import com.tesu.manicurehouse.request.GetUserCollectionOrWorkListRequest;
import com.tesu.manicurehouse.request.GetUserInfoRequest;
import com.tesu.manicurehouse.response.BaseResponse;
import com.tesu.manicurehouse.response.GetUserCollectionOrWorkListResponse;
import com.tesu.manicurehouse.response.GetUserInfoResponse;
import com.tesu.manicurehouse.util.DialogUtils;
import com.tesu.manicurehouse.util.LogUtils;
import com.tesu.manicurehouse.util.SharedPrefrenceUtils;
import com.tesu.manicurehouse.util.StringUtils;
import com.tesu.manicurehouse.util.UIUtils;
import com.tesu.manicurehouse.widget.ListViewCompat;
import com.tesu.manicurehouse.widget.SlideView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 类描述：
 * 创建人：许明达
 * 创建时间：2016/8/28 11:40
 * 我的关注
 */
public class MyCollectionActivity extends BaseActivity implements View.OnClickListener {

    private TextView tv_top_back;
    private View rootView;
    private ListView mListView;
    private String url;
    private Dialog loadingDialog;
    private GetUserCollectionOrWorkListResponse getUserCollectionOrWorkListResponse;
    private List<MessageItem> mMessageItems = new ArrayList<MessageItem>();
    private MyProductionAdapter myProductionAdapter;
    private List<CollectionItem> collectionDatas;
    private SlideView mLastSlideViewWithStatusOn;
    //1视频，2图文
    private String video_type="1";
    private RelativeLayout rl_video;
    private RelativeLayout rl_photo;
    private String userId;
    private BaseResponse baseResponse;
    private Gson gson;
    private AlertDialog deleteDialog;
    private ImageView iv_photo;
    private ImageView iv_video;
    private TextView tv_photo;
    private TextView tv_video;

    @Override
    protected View initView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        rootView = View.inflate(this, R.layout.activity_my_collection, null);
        setContentView(rootView);
        tv_top_back = (TextView) rootView.findViewById(R.id.tv_top_back);
        mListView = (ListView) findViewById(R.id.lv_collection);
        rl_video = (RelativeLayout) findViewById(R.id.rl_video);
        rl_photo = (RelativeLayout) findViewById(R.id.rl_photo);
        iv_photo = (ImageView) findViewById(R.id.iv_photo);
        iv_video = (ImageView) findViewById(R.id.iv_video);
        tv_photo = (TextView) findViewById(R.id.tv_photo);
        tv_video = (TextView) findViewById(R.id.tv_video);

        initData();
        return null;
    }


    public void initData() {
        tv_top_back.setOnClickListener(this);
        rl_video.setOnClickListener(this);
        rl_photo.setOnClickListener(this);
        loadingDialog = DialogUtils.createLoadDialog(MyCollectionActivity.this, true);
        collectionDatas = new ArrayList<>();
        userId = SharedPrefrenceUtils.getString(MyCollectionActivity.this, "user_id");
        gson = new Gson();

        runAsyncTask();
    }

    public void setData(List<GetUserCollectionOrWorkListResponse.CollectionData> collectionItemList) {
        collectionDatas.clear();
            for (int i = 0; i < collectionItemList.size(); i++) {
                CollectionItem collectionItem = new CollectionItem();
                collectionItem.alias = collectionItemList.get(i).alias;
                collectionItem.avatar = collectionItemList.get(i).avatar;
                collectionItem.video_id = collectionItemList.get(i).video_id;
                collectionItem.id_value = collectionItemList.get(i).id_value;
                collectionItem.type = collectionItemList.get(i).type;
                collectionItem.video_url = collectionItemList.get(i).video_url;
                collectionItem.subtitle_file_url = collectionItemList.get(i).subtitle_file_url;
                collectionItem.pics = collectionItemList.get(i).pics;
                collectionItem.title = collectionItemList.get(i).title;
                collectionItem.is_fee = collectionItemList.get(i).is_fee;
                collectionItem.fee = collectionItemList.get(i).fee;
                collectionItem.play_cnt = collectionItemList.get(i).play_cnt;
                collectionItem.collection_cnt = collectionItemList.get(i).collection_cnt;
                collectionItem.forward_cnt = collectionItemList.get(i).forward_cnt;
                collectionItem.liked_cnt = collectionItemList.get(i).liked_cnt;
                collectionItem.is_collection = collectionItemList.get(i).is_collection;
                collectionItem.is_liked = collectionItemList.get(i).is_liked;
                collectionItem.create_time = collectionItemList.get(i).create_time;
                collectionItem.cover_img = collectionItemList.get(i).cover_img;
                collectionItem.index_ = collectionItemList.get(i).index_;
                collectionDatas.add(collectionItem);
            }
            myProductionAdapter = new MyProductionAdapter(MyCollectionActivity.this, collectionDatas, mLastSlideViewWithStatusOn);
            mListView.setAdapter(myProductionAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CollectionItem collectionItem = collectionDatas.get(position);
                Intent intent=null;
                if(collectionItem.type==0){
                    intent = new Intent(MyCollectionActivity.this, VideoInfoActivity.class);
                }else if(collectionItem.type == 1){
                    intent = new Intent(MyCollectionActivity.this, VideoShowInfoActivity.class);

                }else if(collectionItem.type == 2){
                    intent = new Intent(MyCollectionActivity.this, VideoShowPhotoInfoActivity.class);
                }
                intent.putExtra("video_id",collectionItem.video_id);
                intent.putExtra("video_url",collectionItem.video_url);
                intent.putExtra("cover_img",collectionItem.cover_img);
                intent.putExtra("ismy",true);
                intent.putExtra("rondom",collectionItem.index_);
                intent.putExtra("video_name",collectionItem.title);
                UIUtils.startActivityNextAnim(intent);
            }
        });
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                deleteDialog =DialogUtils.showAlertDoubleBtnDialog(MyCollectionActivity.this, "是否删除该视频", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switch (v.getId()) {
                            case R.id.tv_cancle:
                                deleteDialog.dismiss();
                                break;
                            case R.id.tv_ensure:
                                deleteDialog.dismiss();
                                CollectionItem collectionItem = collectionDatas.get(position);
                                deleteCollection(collectionItem);
                                break;
                        }
                    }
                });
                return true;
            }
        });
    }

    /**
     * 长按删除收藏视频
     * @param collectionItem
     */
    private void deleteCollection(final CollectionItem collectionItem) {
        int video_id = collectionItem.video_id;
        loadingDialog.show();
        url = "video/cancelCollectionOrWork.do";
        Map<String,String> params = new HashMap<String,String>();
        params.put("video_id", video_id + "");
        if(StringUtils.isEmpty(userId)){
            userId = "0";
        }
        params.put("user_id", userId);
        params.put("type",  "1");
        MyVolley.uploadNoFile(MyVolley.POST, url, params, new MyVolley.VolleyCallback() {

            @Override
            public void dealWithJson(String address, String json) {
                LogUtils.e("取消收藏:" + json);
                loadingDialog.dismiss();
                baseResponse = gson.fromJson(json,BaseResponse.class);
                if(baseResponse.getCode()==0){
                    collectionDatas.remove(collectionItem);
                    myProductionAdapter.notifyDataSetChanged();
                }else {
                    DialogUtils.showAlertDialog(MyCollectionActivity.this, baseResponse.getResultText());
                }
            }

            @Override
            public void dealWithError(String address, String error) {
                LogUtils.e("取消收藏错误:" + error);
                loadingDialog.dismiss();
                DialogUtils.showAlertDialog(MyCollectionActivity.this, error);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_video:
                video_type="1";
                rl_video.setBackgroundResource(R.mipmap.bt_video_selectable);
                rl_photo.setBackgroundResource(R.mipmap.bt_video_noselectable);
                tv_video.setTextColor(getResources().getColor(R.color.white));
                tv_photo.setTextColor(getResources().getColor(R.color.text_color_black));
                iv_photo.setImageResource(R.mipmap.icon_photo);
                iv_video.setImageResource(R.mipmap.icon_video_white);
                runAsyncTask();
                break;
            case R.id.rl_photo:
                rl_video.setBackgroundResource(R.mipmap.bt_video_noselectable);
                rl_photo.setBackgroundResource(R.mipmap.bt_video_selectable);
                video_type="2";
                tv_video.setTextColor(getResources().getColor(R.color.text_color_black));
                tv_photo.setTextColor(getResources().getColor(R.color.white));
                iv_photo.setImageResource(R.mipmap.icon_photo_white);
                iv_video.setImageResource(R.mipmap.icon_video);
                runAsyncTask();
                break;
            case R.id.tv_top_back:
                finish();
                overridePendingTransition(R.anim.animprv_in, R.anim.animprv_out);
                break;
        }
    }

    public void runAsyncTask() {
        loadingDialog.show();
        GetUserCollectionOrWorkListProtocol getUserCollectionOrWorkListProtocol = new GetUserCollectionOrWorkListProtocol();
        GetUserCollectionOrWorkListRequest getUserCollectionOrWorkListRequest = new GetUserCollectionOrWorkListRequest();
        url = getUserCollectionOrWorkListProtocol.getApiFun();
        getUserCollectionOrWorkListRequest.map.put("user_id",userId );
        getUserCollectionOrWorkListRequest.map.put("type", "1");
        getUserCollectionOrWorkListRequest.map.put("video_type", video_type);

        MyVolley.uploadNoFile(MyVolley.POST, url, getUserCollectionOrWorkListRequest.map, new MyVolley.VolleyCallback() {
            @Override
            public void dealWithJson(String address, String json) {

                Gson gson = new Gson();
                getUserCollectionOrWorkListResponse = gson.fromJson(json, GetUserCollectionOrWorkListResponse.class);
                LogUtils.e("sendVerificationCodeResponse:" + json.toString());
                if (getUserCollectionOrWorkListResponse.code.equals("0")) {
                    loadingDialog.dismiss();
                    if (getUserCollectionOrWorkListResponse.dataList.size() >= 0) {
                        setData(getUserCollectionOrWorkListResponse.dataList);
                    }

                } else {
                    loadingDialog.dismiss();
                    DialogUtils.showAlertDialog(MyCollectionActivity.this,
                            getUserCollectionOrWorkListResponse.resultText);
                }
            }

            @Override
            public void dealWithError(String address, String error) {
                loadingDialog.dismiss();
                DialogUtils.showAlertDialog(MyCollectionActivity.this, error);
            }
        });
    }
}
