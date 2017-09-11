package com.tesu.manicurehouse.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.tesu.manicurehouse.R;
import com.tesu.manicurehouse.adapter.LocalMessageAdapter;
import com.tesu.manicurehouse.adapter.MyAttestionAdapter;
import com.tesu.manicurehouse.adapter.MyProductionAdapter;
import com.tesu.manicurehouse.base.BaseActivity;
import com.tesu.manicurehouse.base.BaseApplication;
import com.tesu.manicurehouse.base.MyVolley;
import com.tesu.manicurehouse.bean.CollectionItem;
import com.tesu.manicurehouse.bean.MessageItem;
import com.tesu.manicurehouse.bean.UploadVideoMessageBean;
import com.tesu.manicurehouse.play.MediaPlayActivity;
import com.tesu.manicurehouse.protocol.GetUserCollectionOrWorkListProtocol;
import com.tesu.manicurehouse.record.VideoActivity;
import com.tesu.manicurehouse.record.VideoShowPhotoActivity;
import com.tesu.manicurehouse.request.GetUserCollectionOrWorkListRequest;
import com.tesu.manicurehouse.response.BaseResponse;
import com.tesu.manicurehouse.response.GetUserCollectionOrWorkListResponse;
import com.tesu.manicurehouse.util.DialogUtils;
import com.tesu.manicurehouse.util.LogUtils;
import com.tesu.manicurehouse.util.MySQLiteHelper;
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
 * 我的作品
 */
public class MyProductionActivity extends BaseActivity implements View.OnClickListener {

    private TextView tv_top_back;
    private View rootView;
    private ListView mListView;
    private SlideView mLastSlideViewWithStatusOn;

    private String url;
    private Dialog loadingDialog;
    private GetUserCollectionOrWorkListResponse getUserCollectionOrWorkListResponse;
    private List<MessageItem> mMessageItems = new ArrayList<MessageItem>();
    private MyProductionAdapter myProductionAdapter;
    private List<CollectionItem> collectionDatas;
    private RelativeLayout rl_video;
    private RelativeLayout rl_photo;
    private RelativeLayout rl_announce;  //发表
    private RelativeLayout rl_noannounce; //未发表
    private TextView tv_announce;
    private TextView tv_noannounce;
    private TextView tv_video;
    private TextView tv_photo;
    private ImageView iv_announce;
    private ImageView iv_noannounce;
    private ImageView iv_video;
    private ImageView iv_photo;
    //1视频，2图文
    private String video_type="1";   //1表示视频   2表示图文
    private int isAnnounce=0;    //0 已发表    1未发表
    private String userId;
    private AlertDialog deleteDialog;
    private ListView lv_production_local;
    private SQLiteDatabase db ;
    private LocalMessageAdapter localMessageAdapter;
    private List<UploadVideoMessageBean> videoMessageBeanList;
    private List<UploadVideoMessageBean> photoMessageBeanList;

    private BaseResponse baseResponse;
    private Gson gson;
    @Override
    protected View initView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        rootView = View.inflate(this, R.layout.activity_my_production, null);
        setContentView(rootView);
        tv_top_back = (TextView) rootView.findViewById(R.id.tv_top_back);
        mListView = (ListView) findViewById(R.id.lv_production);
        rl_video = (RelativeLayout) findViewById(R.id.rl_video);
        rl_photo = (RelativeLayout) findViewById(R.id.rl_photo);
        rl_announce = (RelativeLayout) findViewById(R.id.rl_announce);
        rl_noannounce = (RelativeLayout) findViewById(R.id.rl_noannounce);
        tv_announce = (TextView) findViewById(R.id.tv_announce);
        tv_noannounce = (TextView) findViewById(R.id.tv_noannounce);
        tv_video = (TextView) findViewById(R.id.tv_video);
        tv_photo = (TextView) findViewById(R.id.tv_photo);
        iv_announce = (ImageView) findViewById(R.id.iv_announce);
        iv_noannounce = (ImageView) findViewById(R.id.iv_noannounce);
        iv_video = (ImageView) findViewById(R.id.iv_video);
        iv_photo = (ImageView) findViewById(R.id.iv_photo);
        lv_production_local = (ListView) findViewById(R.id.lv_production_local);

        db = BaseApplication.getDbHelper().getWritableDatabase();
        gson = new Gson();

        initData();
        return null;
    }


    public void initData() {
        tv_top_back.setOnClickListener(this);
        rl_video.setOnClickListener(this);
        rl_photo.setOnClickListener(this);
        rl_announce.setOnClickListener(this);
        rl_noannounce.setOnClickListener(this);
        loadingDialog = DialogUtils.createLoadDialog(MyProductionActivity.this, true);
        collectionDatas = new ArrayList<>();
        userId = SharedPrefrenceUtils.getString(MyProductionActivity.this, "user_id");

        getLocalMessage();
        runAsyncTask();
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
                if(isAnnounce==0){
                    runAsyncTask();
                }else {
                    setLocalMessage();
                }
                break;
            case R.id.rl_photo:
                video_type="2";
                rl_video.setBackgroundResource(R.mipmap.bt_video_noselectable);
                rl_photo.setBackgroundResource(R.mipmap.bt_video_selectable);
                tv_video.setTextColor(getResources().getColor(R.color.text_color_black));
                tv_photo.setTextColor(getResources().getColor(R.color.white));
                iv_photo.setImageResource(R.mipmap.icon_photo_white);
                iv_video.setImageResource(R.mipmap.icon_video);
                if(isAnnounce==0){
                    runAsyncTask();
                }else {
                    setLocalMessage();
                }
                break;
            case R.id.tv_top_back:
                finish();
                overridePendingTransition(R.anim.animprv_in, R.anim.animprv_out);
                break;
            case R.id.rl_noannounce:
                //未发表
                rl_noannounce.setBackgroundResource(R.mipmap.bt_video_selectable);
                rl_announce.setBackgroundResource(R.mipmap.bt_video_noselectable);
                tv_noannounce.setTextColor(getResources().getColor(R.color.white));
                tv_announce.setTextColor(getResources().getColor(R.color.text_color_black));
                iv_announce.setImageResource(R.mipmap.bt_release);
                iv_noannounce.setImageResource(R.mipmap.bt_norelease_white);
                isAnnounce = 1;
                lv_production_local.setVisibility(View.VISIBLE);
                mListView.setVisibility(View.GONE);
                setLocalMessage();
                break;
            case R.id.rl_announce:
                //发表
                isAnnounce = 0;
                rl_announce.setBackgroundResource(R.mipmap.bt_video_selectable);
                rl_noannounce.setBackgroundResource(R.mipmap.bt_video_noselectable);
                tv_noannounce.setTextColor(getResources().getColor(R.color.text_color_black));
                tv_announce.setTextColor(getResources().getColor(R.color.white));
                iv_announce.setImageResource(R.mipmap.bt_release_white);
                iv_noannounce.setImageResource(R.mipmap.bt_norelease);
                lv_production_local.setVisibility(View.GONE);
                mListView.setVisibility(View.VISIBLE);
                runAsyncTask();
                break;
        }
    }

    /**
     * 获取本地保存的数据
     */
    private void getLocalMessage() {
        Cursor cursor = db.query("videomessage", null, "user_id=?", new String[]{userId}, null, null, null);
        videoMessageBeanList = new ArrayList<UploadVideoMessageBean>();
        photoMessageBeanList = new ArrayList<UploadVideoMessageBean>();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                int type = cursor.getInt(cursor.getColumnIndex("type"));
                String user_id = cursor.getString(cursor.getColumnIndex("user_id"));
                String pics = cursor.getString(cursor.getColumnIndex("pics"));
                String title = cursor.getString(cursor.getColumnIndex("title"));
                long create_time = cursor.getLong(cursor.getColumnIndex("create_time"));
                String subtitle_file_url = cursor.getString(cursor.getColumnIndex("subtitle_file_url"));
                String video_url = cursor.getString(cursor.getColumnIndex("video_url"));
                int is_fee = cursor.getInt(cursor.getColumnIndex("is_fee"));
                String converImageUrl = cursor.getString(cursor.getColumnIndex("converImageUrl"));
                UploadVideoMessageBean uploadVideoMessageBean = new UploadVideoMessageBean();
                uploadVideoMessageBean.type = type;
                uploadVideoMessageBean.user_id = user_id;
                uploadVideoMessageBean.pics = pics;
                uploadVideoMessageBean.title = title;
                uploadVideoMessageBean.create_time = create_time;
                uploadVideoMessageBean.subtitle_file_url = subtitle_file_url;
                uploadVideoMessageBean.video_url = video_url;
                uploadVideoMessageBean.id = id;
                uploadVideoMessageBean.alias = SharedPrefrenceUtils.getString(this,"alias");
                uploadVideoMessageBean.avatar = SharedPrefrenceUtils.getString(this,"avatar");
                uploadVideoMessageBean.is_fee = is_fee;
                uploadVideoMessageBean.converImageUrl = converImageUrl;
                if(type ==1){
                    videoMessageBeanList.add(uploadVideoMessageBean);
                }else {
                    photoMessageBeanList.add(uploadVideoMessageBean);
                }
            } while (cursor.moveToNext());
        }

        LogUtils.e("未发表视频列表:"+videoMessageBeanList.toString());
        LogUtils.e("未发表图文列表:" + photoMessageBeanList.toString());
    }

    private void setLocalMessage(){
        if(video_type.equals("1")){
            localMessageAdapter = new LocalMessageAdapter(this,videoMessageBeanList);
            lv_production_local.setAdapter(localMessageAdapter);
        }else {
            localMessageAdapter = new LocalMessageAdapter(this,photoMessageBeanList);
            lv_production_local.setAdapter(localMessageAdapter);
        }
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
                collectionDatas.add(collectionItem);
        }
        myProductionAdapter = new MyProductionAdapter(MyProductionActivity.this, collectionDatas, mLastSlideViewWithStatusOn);
        mListView.setAdapter(myProductionAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CollectionItem collectionItem = collectionDatas.get(position);
                Intent intent = null;
                if (collectionItem.type == 0) {
                    intent = new Intent(MyProductionActivity.this, VideoInfoActivity.class);
                } else if (collectionItem.type == 1) {
                    intent = new Intent(MyProductionActivity.this, VideoShowInfoActivity.class);

                } else if (collectionItem.type == 2) {
                    intent = new Intent(MyProductionActivity.this, VideoShowPhotoInfoActivity.class);
                }
                intent.putExtra("video_id", collectionItem.video_id);
                intent.putExtra("video_url",collectionItem.video_url);
                intent.putExtra("cover_img",collectionItem.cover_img);
                intent.putExtra("ismy",true);
                UIUtils.startActivityNextAnim(intent);
            }
        });
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                deleteDialog = DialogUtils.showAlertDoubleBtnDialog(MyProductionActivity.this, "是否删除该视频", new View.OnClickListener() {
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

        lv_production_local.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                UploadVideoMessageBean uploadVideoMessageBean = null;
                if(video_type.equals("1")){
                    uploadVideoMessageBean = videoMessageBeanList.get(position);
                    LogUtils.e("本地视频:"+uploadVideoMessageBean.toString());
//                    Intent intent = new Intent(MyProductionActivity.this, MediaPlayActivity.class);
                    Intent intent = new Intent(MyProductionActivity.this, VideoActivity.class);
                    intent.putExtra("fileUrl",uploadVideoMessageBean.video_url);
                    intent.putExtra("subtitleUr",uploadVideoMessageBean.subtitle_file_url);
                    intent.putExtra("title",uploadVideoMessageBean.title);
                    intent.putExtra("is_fee",uploadVideoMessageBean.is_fee);
                    intent.putExtra("converImageUrl",uploadVideoMessageBean.converImageUrl);
                    startActivity(intent);

                }else {
                    uploadVideoMessageBean = photoMessageBeanList.get(position);
//                    Intent intent = new Intent(MyProductionActivity.this,ShowLocalPhotoMessageActivity.class);
                    Intent intent = new Intent(MyProductionActivity.this,VideoShowPhotoActivity.class);
                    intent.putExtra("pics",uploadVideoMessageBean.pics);
                    intent.putExtra("title",uploadVideoMessageBean.title);
                    intent.putExtra("is_fee",uploadVideoMessageBean.is_fee);
                    startActivity(intent);
                }
            }
        });
        lv_production_local.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                deleteDialog = DialogUtils.showAlertDoubleBtnDialog(MyProductionActivity.this, "是否删除该视频", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switch (v.getId()) {
                            case R.id.tv_cancle:
                                deleteDialog.dismiss();
                                break;
                            case R.id.tv_ensure:
                                deleteDialog.dismiss();
                                UploadVideoMessageBean uploadVideoMessageBean = null;
                                if(video_type.equals("1")){
                                    uploadVideoMessageBean = videoMessageBeanList.get(position);
                                }else {
                                    uploadVideoMessageBean = photoMessageBeanList.get(position);
                                }
                                deleteLocalMessage(uploadVideoMessageBean);
                                break;
                        }
                    }
                });
                return true;
            }
        });
    }

    /**
     * 删除本地视频
     * @param uploadVideoMessageBean
     */
    private void deleteLocalMessage(UploadVideoMessageBean uploadVideoMessageBean) {
        if(uploadVideoMessageBean != null){
            int count =db.delete("videomessage","id = ?",new String[] {uploadVideoMessageBean.id+""});
            if(count != 0){
                getLocalMessage();
                setLocalMessage();
            }else {
                DialogUtils.showAlertDialog(MyProductionActivity.this, "删除数据失败");
            }
        }
    }

    /**
     * 长按删除我的产品
     * @param collectionItem
     */
    private void deleteCollection(final CollectionItem collectionItem) {
        int video_id = collectionItem.video_id;
        loadingDialog.show();
        url = "app/deleteUserWork.do";
        Map<String,String> params = new HashMap<String,String>();
        params.put("video_id", video_id + "");
        if(StringUtils.isEmpty(userId)){
            userId = "0";
        }
        params.put("user_id", userId);
        MyVolley.uploadNoFile(MyVolley.POST, url, params, new MyVolley.VolleyCallback() {

            @Override
            public void dealWithJson(String address, String json) {
                LogUtils.e("删除我的作品:" + json);
                loadingDialog.dismiss();
                baseResponse = gson.fromJson(json, BaseResponse.class);
                if (baseResponse.getCode() == 0) {
                    collectionDatas.remove(collectionItem);
                    myProductionAdapter.notifyDataSetChanged();
                } else {
                    DialogUtils.showAlertDialog(MyProductionActivity.this, baseResponse.getResultText());
                }
            }

            @Override
            public void dealWithError(String address, String error) {
                LogUtils.e("删除我的作品错误:" + error);
                loadingDialog.dismiss();
                DialogUtils.showAlertDialog(MyProductionActivity.this, error);
            }
        });
    }


    public void runAsyncTask() {
        loadingDialog.show();
        GetUserCollectionOrWorkListProtocol getUserCollectionOrWorkListProtocol = new GetUserCollectionOrWorkListProtocol();
        GetUserCollectionOrWorkListRequest getUserCollectionOrWorkListRequest = new GetUserCollectionOrWorkListRequest();
        url = getUserCollectionOrWorkListProtocol.getApiFun();
        getUserCollectionOrWorkListRequest.map.put("user_id",userId);
        getUserCollectionOrWorkListRequest.map.put("type", "2");
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
                    DialogUtils.showAlertDialog(MyProductionActivity.this,
                            getUserCollectionOrWorkListResponse.resultText);
                }


            }

            @Override
            public void dealWithError(String address, String error) {
                loadingDialog.dismiss();
                DialogUtils.showAlertDialog(MyProductionActivity.this, error);
            }
        });
    }
}
