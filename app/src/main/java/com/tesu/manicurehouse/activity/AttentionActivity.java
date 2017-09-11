package com.tesu.manicurehouse.activity;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.tesu.manicurehouse.R;
import com.tesu.manicurehouse.adapter.ClassifyContentAdapter;
import com.tesu.manicurehouse.adapter.ClassifyTitleAdapter;
import com.tesu.manicurehouse.adapter.MyProductionAdapter;
import com.tesu.manicurehouse.base.BaseActivity;
import com.tesu.manicurehouse.base.MyVolley;
import com.tesu.manicurehouse.bean.CollectionItem;
import com.tesu.manicurehouse.protocol.AddFollowProtocol;
import com.tesu.manicurehouse.protocol.GetFollowInfoProtocol;
import com.tesu.manicurehouse.protocol.GetUserCollectionOrWorkListProtocol;
import com.tesu.manicurehouse.protocol.GetUserFollowListProtocol;
import com.tesu.manicurehouse.request.AddFollowRequest;
import com.tesu.manicurehouse.request.GetFollowInfoRequest;
import com.tesu.manicurehouse.request.GetUserCollectionOrWorkListRequest;
import com.tesu.manicurehouse.request.GetUserFoollowListRequest;
import com.tesu.manicurehouse.response.AddFollowResponse;
import com.tesu.manicurehouse.response.BaseResponse;
import com.tesu.manicurehouse.response.GetFollowInfoResponse;
import com.tesu.manicurehouse.response.GetUserCollectionOrWorkListResponse;
import com.tesu.manicurehouse.response.GetUserFollowListResponse;
import com.tesu.manicurehouse.util.CircleImageView;
import com.tesu.manicurehouse.util.DialogUtils;
import com.tesu.manicurehouse.util.LogUtils;
import com.tesu.manicurehouse.util.PictureOption;
import com.tesu.manicurehouse.util.SharedPrefrenceUtils;
import com.tesu.manicurehouse.util.StringUtils;
import com.tesu.manicurehouse.util.UIUtils;
import com.tesu.manicurehouse.widget.MyListView;
import com.tesu.manicurehouse.widget.SlideView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 类描述：
 * 创建人：许明达
 * 创建时间：2016/8/16 11:40
 * 关注人页面
 */
public class AttentionActivity extends BaseActivity implements View.OnClickListener {

    private MyListView mListView;
    private TextView tv_top_back;
    private ImageView iv_addmore;
    private View rootView;
    private TextView tv_attention_name;
    private ImageView iv_myself_img;
    private TextView tv_followCnt;
    private TextView tv_followedCnt;
    private TextView tv_likedCnt;
    private RelativeLayout rl_message;  //私信

    private String url;
    private Dialog loadingDialog;
    private GetFollowInfoResponse getFollowInfoResponse;
    private GetUserCollectionOrWorkListResponse getUserCollectionOrWorkListResponse;
    private int follow_userid;
    private RelativeLayout rl_like;
    private TextView tv_like;
    //1视频，2图文
    private String video_type = "1";
    private MyProductionAdapter myProductionAdapter;
    private List<CollectionItem> collectionDatas;
    private SlideView mLastSlideViewWithStatusOn;
    private RelativeLayout rl_photo;
    private ImageView iv_photo;
    private TextView tv_photo;
    private RelativeLayout rl_video;
    private ImageView iv_video;
    private TextView tv_video;
    private Dialog mDialog;
    private String messageContent;  //私信内容
    private String userId;
    private BaseResponse baseResponse;
    private Gson gson;
    private TextView tv_lable;
    @Override
    protected View initView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        rootView = View.inflate(this, R.layout.activity_attention, null);
        setContentView(rootView);
        mListView = (MyListView) findViewById(R.id.lv_collection);
        tv_lable=(TextView)rootView.findViewById(R.id.tv_lable);
        rl_like = (RelativeLayout) rootView.findViewById(R.id.rl_like);
        tv_like = (TextView) rootView.findViewById(R.id.tv_like);
        tv_top_back = (TextView) rootView.findViewById(R.id.tv_top_back);
        iv_addmore = (ImageView) rootView.findViewById(R.id.iv_addmore);
        tv_attention_name = (TextView) rootView.findViewById(R.id.tv_attention_name);
        tv_followCnt = (TextView) rootView.findViewById(R.id.tv_followCnt);
        tv_followedCnt = (TextView) rootView.findViewById(R.id.tv_followedCnt);
        tv_likedCnt = (TextView) rootView.findViewById(R.id.tv_likedCnt);
        iv_myself_img = (ImageView) rootView.findViewById(R.id.iv_myself_img);
        rl_photo = (RelativeLayout) rootView.findViewById(R.id.rl_photo);
        iv_photo = (ImageView) rootView.findViewById(R.id.iv_photo);
        tv_photo = (TextView) rootView.findViewById(R.id.tv_photo);
        rl_video = (RelativeLayout) rootView.findViewById(R.id.rl_video);
        iv_video = (ImageView) rootView.findViewById(R.id.iv_video);
        tv_video = (TextView) rootView.findViewById(R.id.tv_video);
        rl_message = (RelativeLayout) rootView.findViewById(R.id.rl_message);

        View dialogView = View.inflate(AttentionActivity.this, R.layout.alert_dialog_show_input,
                null);

        initData();
        return null;
    }


    public void initData() {
        if (!ImageLoader.getInstance().isInited()) {
            ImageLoader.getInstance()
                    .init(ImageLoaderConfiguration.createDefault(UIUtils
                            .getContext()));
        }
        gson = new Gson();
        userId = SharedPrefrenceUtils.getString(this, "user_id");
        Intent intent = getIntent();
        follow_userid = intent.getIntExtra("follow_userid", -1);
        loadingDialog = DialogUtils.createLoadDialog(AttentionActivity.this, true);
        tv_top_back.setOnClickListener(this);
        iv_addmore.setOnClickListener(this);
        rl_photo.setOnClickListener(this);
        rl_video.setOnClickListener(this);
        rl_like.setOnClickListener(this);
        rl_message.setOnClickListener(this);
        collectionDatas = new ArrayList<>();
        runAsyncTask();
    }

    public void setDate(GetFollowInfoResponse.Data data) {
        tv_attention_name.setText(data.alias);
        tv_followCnt.setText("关注 " + data.followCnt);
        tv_followedCnt.setText("粉丝 " + data.followedCnt);
        if(!TextUtils.isEmpty(data.signature)){
            tv_lable.setText("个性签名 "+data.signature);
        }
        tv_likedCnt.setText("" + data.likedCnt);
        ImageLoader.getInstance().displayImage(data.avatar, iv_myself_img, PictureOption.getSimpleOptions());
        if (data.is_follow == 1) {
            tv_like.setText("已关注");
        } else {
            tv_like.setText("未关注");
        }
        runGetUserCollectionOrWorkList();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_photo:
                video_type = "2";
                rl_video.setBackground(getResources().getDrawable(R.mipmap.bt_video_noselectable));
                iv_video.setImageDrawable(getResources().getDrawable(R.mipmap.icon_video));
                tv_video.setTextColor(getResources().getColor(R.color.text_color_black));

                rl_photo.setBackground(getResources().getDrawable(R.mipmap.bt_video_selectable));
                iv_photo.setImageDrawable(getResources().getDrawable(R.mipmap.icon_photo_white));
                tv_photo.setTextColor(getResources().getColor(R.color.tab_background));
                loadingDialog.show();
                runGetUserCollectionOrWorkList();
                break;
            case R.id.rl_video:
                video_type = "1";
                rl_video.setBackground(getResources().getDrawable(R.mipmap.bt_video_selectable));
                iv_video.setImageDrawable(getResources().getDrawable(R.mipmap.icon_video_white));
                tv_video.setTextColor(getResources().getColor(R.color.tab_background));

                rl_photo.setBackground(getResources().getDrawable(R.mipmap.bt_video_noselectable));
                iv_photo.setImageDrawable(getResources().getDrawable(R.mipmap.icon_photo));
                tv_photo.setTextColor(getResources().getColor(R.color.text_color_black));
                loadingDialog.show();
                runGetUserCollectionOrWorkList();
                break;
            case R.id.rl_like:
                runAddFollow();
                break;
            case R.id.iv_addmore:
                Intent intent = new Intent(AttentionActivity.this, OthrersInfoActivity.class);
                intent.putExtra("identity_type", getFollowInfoResponse.data.identity_type);
                intent.putExtra("avatar", getFollowInfoResponse.data.avatar);
                intent.putExtra("alias", getFollowInfoResponse.data.alias);
                intent.putExtra("signature", getFollowInfoResponse.data.signature);
                intent.putExtra("city", getFollowInfoResponse.data.city);
                UIUtils.startActivityNextAnim(intent);
                break;
            case R.id.tv_top_back:
                finish();
                overridePendingTransition(R.anim.animprv_in, R.anim.animprv_out);
                break;
            case R.id.rl_message:
                mDialog = DialogUtils.showInputAlertDialog(AttentionActivity.this, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EditText alert_dialog_content = (EditText) mDialog.findViewById(R.id.alert_dialog_content);
                        messageContent = alert_dialog_content.getText().toString();
                        LogUtils.e("messageContent:"+messageContent);
                        if(TextUtils.isEmpty(messageContent)){
                            Toast.makeText(AttentionActivity.this,"请输入内容",Toast.LENGTH_LONG).show();
                        }else {
                            mDialog.dismiss();
                            sendMessage();
                        }

                    }
                },"私信内容");
                break;
        }
    }

    /**
     * 私信对方
     */
    private void sendMessage() {
        loadingDialog.show();
        url = "app/sendMsg.do";
        Map<String, String> params = new HashMap<String, String>();
        if (StringUtils.isEmpty(userId)) {
            userId = "0";
        }
        params.put("user_id", userId);
        params.put("to_user_id", follow_userid+"");
        params.put("content", messageContent);
        MyVolley.uploadNoFile(MyVolley.POST, url, params, new MyVolley.VolleyCallback(){

            @Override
            public void dealWithJson(String address, String json) {
                loadingDialog.dismiss();
                LogUtils.e("私信对方结果:" + json);
                baseResponse = gson.fromJson(json,BaseResponse.class);
                if(baseResponse.getCode()==0){
                    Toast.makeText(AttentionActivity.this,"发送成功",Toast.LENGTH_LONG).show();
                }else {
                    DialogUtils.showAlertDialog(AttentionActivity.this, baseResponse.getResultText());
                }

            }

            @Override
            public void dealWithError(String address, String error) {
                loadingDialog.dismiss();
                LogUtils.e("私信对方结果错误:" + error);
                DialogUtils.showAlertDialog(AttentionActivity.this, error);
            }
        });
    }

    public void runAsyncTask() {
        loadingDialog.show();
        GetFollowInfoProtocol getFollowInfoProtocol = new GetFollowInfoProtocol();
        GetFollowInfoRequest getFollowInfoRequest = new GetFollowInfoRequest();
        url = getFollowInfoProtocol.getApiFun();
        getFollowInfoRequest.map.put("user_id", SharedPrefrenceUtils.getString(AttentionActivity.this, "user_id"));
        getFollowInfoRequest.map.put("follow_userid", String.valueOf(follow_userid));


        MyVolley.uploadNoFile(MyVolley.POST, url, getFollowInfoRequest.map, new MyVolley.VolleyCallback() {
            @Override
            public void dealWithJson(String address, String json) {

                Gson gson = new Gson();
                getFollowInfoResponse = gson.fromJson(json, GetFollowInfoResponse.class);
                LogUtils.e("sendVerificationCodeResponse:" + json.toString());
                if (getFollowInfoResponse.code == 0) {
                    if (getFollowInfoResponse.data != null) {
                        setDate(getFollowInfoResponse.data);
                    }

                } else {
                    loadingDialog.dismiss();
                    DialogUtils.showAlertDialog(AttentionActivity.this,
                            getFollowInfoResponse.resultText);
                }


            }

            @Override
            public void dealWithError(String address, String error) {
                loadingDialog.dismiss();
                DialogUtils.showAlertDialog(AttentionActivity.this, error);
            }
        });
    }

    public void runAddFollow() {
        loadingDialog.show();
        AddFollowProtocol addFollowProtocol = new AddFollowProtocol();
        AddFollowRequest addFollowRequest = new AddFollowRequest();
        url = addFollowProtocol.getApiFun();
        addFollowRequest.map.put("user_id", SharedPrefrenceUtils.getString(AttentionActivity.this, "user_id"));
        addFollowRequest.map.put("follow_userid", String.valueOf(follow_userid));


        MyVolley.uploadNoFile(MyVolley.POST, url, addFollowRequest.map, new MyVolley.VolleyCallback() {
            @Override
            public void dealWithJson(String address, String json) {

                Gson gson = new Gson();
                AddFollowResponse addFollowResponse = gson.fromJson(json, AddFollowResponse.class);
                LogUtils.e("sendVerificationCodeResponse:" + json.toString());
                if (addFollowResponse.code == 0) {
                    loadingDialog.dismiss();
                    if (getFollowInfoResponse.data.is_follow == 0) {
                        tv_like.setText("已关注");
                    } else {
                        tv_like.setText("未关注");
                    }
                    SharedPrefrenceUtils.setBoolean(UIUtils.getContext(), "isUpdate", true);
                } else {
                    loadingDialog.dismiss();
                    DialogUtils.showAlertDialog(AttentionActivity.this,
                            addFollowResponse.resultText);
                }


            }

            @Override
            public void dealWithError(String address, String error) {
                loadingDialog.dismiss();
                DialogUtils.showAlertDialog(AttentionActivity.this, error);
            }
        });
    }

    public void runGetUserCollectionOrWorkList() {
        GetUserCollectionOrWorkListProtocol getUserCollectionOrWorkListProtocol = new GetUserCollectionOrWorkListProtocol();
        GetUserCollectionOrWorkListRequest getUserCollectionOrWorkListRequest = new GetUserCollectionOrWorkListRequest();
        url = getUserCollectionOrWorkListProtocol.getApiFun();
        getUserCollectionOrWorkListRequest.map.put("user_id", String.valueOf(follow_userid));
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
                    DialogUtils.showAlertDialog(AttentionActivity.this,
                            getUserCollectionOrWorkListResponse.resultText);
                }
            }

            @Override
            public void dealWithError(String address, String error) {
                loadingDialog.dismiss();
                DialogUtils.showAlertDialog(AttentionActivity.this, error);
            }
        });
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
        myProductionAdapter = new MyProductionAdapter(AttentionActivity.this, collectionDatas, mLastSlideViewWithStatusOn);
        mListView.setAdapter(myProductionAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CollectionItem collectionItem = collectionDatas.get(position);
                Intent intent = null;
                if (collectionItem.type == 0) {
                    intent = new Intent(AttentionActivity.this, VideoInfoActivity.class);
                } else if (collectionItem.type == 1) {
                    intent = new Intent(AttentionActivity.this, VideoShowInfoActivity.class);

                } else if (collectionItem.type == 2) {
                    intent = new Intent(AttentionActivity.this, VideoShowPhotoInfoActivity.class);
                }
                intent.putExtra("video_id", collectionItem.video_id);
                intent.putExtra("cover_img", collectionItem.cover_img);
                UIUtils.startActivityNextAnim(intent);
            }
        });
    }
}
