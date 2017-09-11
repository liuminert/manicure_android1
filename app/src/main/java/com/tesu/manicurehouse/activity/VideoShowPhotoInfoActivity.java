package com.tesu.manicurehouse.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.tesu.manicurehouse.R;
import com.tesu.manicurehouse.adapter.LookAdapter;
import com.tesu.manicurehouse.adapter.ShareAdapter;
import com.tesu.manicurehouse.adapter.VideoCommentsAdapter;
import com.tesu.manicurehouse.adapter.VideoLableAdapter;
import com.tesu.manicurehouse.adapter.VideoShowPhotoAdapter;
import com.tesu.manicurehouse.base.BaseActivity;
import com.tesu.manicurehouse.base.MyVolley;
import com.tesu.manicurehouse.bean.GoodsAttrBean;
import com.tesu.manicurehouse.bean.ShareDateBean;
import com.tesu.manicurehouse.bean.ShareInfo;
import com.tesu.manicurehouse.bean.ShowPhotoContentBean;
import com.tesu.manicurehouse.bean.VideoInfoBean;
import com.tesu.manicurehouse.bean.VideoLableBean;
import com.tesu.manicurehouse.bean.VideoListBean;
import com.tesu.manicurehouse.protocol.AddVideoPlayOrForwardCntProtocol;
import com.tesu.manicurehouse.protocol.GetFollowInfoProtocol;
import com.tesu.manicurehouse.protocol.GetVideoCommentListProtocol;
import com.tesu.manicurehouse.protocol.ProblemFeedbackProtocol;
import com.tesu.manicurehouse.protocol.ReplyVideoCommentProtocol;
import com.tesu.manicurehouse.protocol.SubmitVideoCommentProtocol;
import com.tesu.manicurehouse.request.AddVideoPlayOrForwardCntRequest;
import com.tesu.manicurehouse.request.GetFollowInfoRequest;
import com.tesu.manicurehouse.request.GetVideoCommentListRequest;
import com.tesu.manicurehouse.request.ProblemFeedbackRequest;
import com.tesu.manicurehouse.request.ReplyVideoCommentRequest;
import com.tesu.manicurehouse.request.SubmitVideoCommentRequest;
import com.tesu.manicurehouse.response.AddVideoPlayOrForwardCntResponse;
import com.tesu.manicurehouse.response.BaseResponse;
import com.tesu.manicurehouse.response.GetFollowInfoResponse;
import com.tesu.manicurehouse.response.GetVideoCommentListResponse;
import com.tesu.manicurehouse.response.GetVideoListResponse;
import com.tesu.manicurehouse.response.ProblemFeedbackResponse;
import com.tesu.manicurehouse.response.ReplyInformationCommentResponse;
import com.tesu.manicurehouse.response.SubmitVideoCommentResponse;
import com.tesu.manicurehouse.response.VideoInfoResponse;
import com.tesu.manicurehouse.support.PercentLinearLayout;
import com.tesu.manicurehouse.support.PercentRelativeLayout;
import com.tesu.manicurehouse.util.BitmapUtils;
import com.tesu.manicurehouse.util.Constants;
import com.tesu.manicurehouse.util.DialogUtils;
import com.tesu.manicurehouse.util.LogUtils;
import com.tesu.manicurehouse.util.LoginUtils;
import com.tesu.manicurehouse.util.PictureOption;
import com.tesu.manicurehouse.util.SharedPrefrenceUtils;
import com.tesu.manicurehouse.util.StringUtils;
import com.tesu.manicurehouse.util.UIUtils;
import com.tesu.manicurehouse.util.VideoUtil;
import com.tesu.manicurehouse.widget.CircleImageView;
import com.tesu.manicurehouse.widget.CustomExpandableListView;
import com.tesu.manicurehouse.widget.HorizontalListView;
import com.tesu.manicurehouse.widget.MyListView;
import com.tesu.manicurehouse.widget.XExpandableListView;

import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;
import io.vov.vitamio.Vitamio;

/**
 * 类描述：
 * 创建人：许明达
 * 创建时间：2016/8/16 11:40
 * 视频详情
 */
public class VideoShowPhotoInfoActivity extends BaseActivity implements View.OnClickListener, PlatformActionListener {

    private RelativeLayout rl_comment;
    private PullToRefreshScrollView sv;
    private ScrollView sv1;
    private ImageView iv_share;
    private ImageView iv_top_back;
    private EditText et_comment;
    private VideoCommentsAdapter videoCommentsAdapter;
    private CustomExpandableListView lv_comment;
    private TextView tv_goto_comment;
    //交作业
    private TextView tv_submit_homework;
    //一键下单
    private TextView tv_one_key_order;
    private List<List<String>> lists;
    private List<List<String>> templists;
    private List<String> templist;
    private List<String> firstsecondlist;
    private GridView gv_lable;
    //    private LableAdapter lableAdapter;
    private TextView tv_goto_attention;
    private PercentRelativeLayout prl_share;
    private ShareAdapter sadapter;
    private GridView gv_share;
    private PercentLinearLayout rl_share;
    private boolean isopen = false;
    private int video_id;
    private String userId;
    private VideoInfoResponse videoInfoResponse;
    private Gson gson;
    private Dialog loadingDialog;
    private String url;
    private VideoInfoBean videoInfoBean;

    private CircleImageView iv_uesr_img;
    private TextView tv_user_name;
    private TextView tv_look_num;
    private TextView tv_date;

    private TextView user_share_video;
    private TextView user_collection_video;
    private TextView user_like_num;
    private ShareDateBean shareDateBean;

    private PercentRelativeLayout prl_collection;
    private PercentRelativeLayout prl_like;

    private ImageView iv_collection;
    private ImageView iv_liked;

    private LayoutInflater layoutInflater;
    private ImageLoader imageLoader;
    private DisplayImageOptions options;
    private List<VideoLableBean> videoLableBeanList;
    private List<VideoLableBean> showVideoLableBeanList;
    private VideoLableAdapter videoLableAdapter;

    private VideoShowPhotoAdapter videoShowPhotoAdapter;
    private List<ShowPhotoContentBean> showPhotoContentBeanList;
    private MyListView lv_show_photo;
    private BaseResponse baseResponse;
    private int isLiked;
    private int isCollection;
    private List<GetVideoCommentListResponse.VideoCommentBean> videoCommentBeanList;
    //页数
    private int pageNo = 1;
    //个数
    private int pageSize = 1000;
    //评论Id
    private String comment_id;
    //父id(如果是回复评论的就填0，如果回复的是回复的，就填reply_id跟帖id)
    private String parent_id;
    private boolean isReply = false;
    //评论内容
    private String content;
    private TextView tv_cancle;
    private TextView tv_comment_num;
    private boolean isRefresh = false;

    private HorizontalListView lv_look;
    private LookAdapter lookAdapter;
    private List<VideoListBean> lookVideos;
    private GetVideoListResponse getVideoListResponse;
    private TextView tv_error;
    private TextView tv_title;
    private Dialog mDialog;
    private boolean isError;  //是否纠错
    private ArrayList<String> imagepaths;
    private GetFollowInfoResponse getFollowInfoResponse;
    private PercentLinearLayout pl_look;
    private boolean ismy;   //是否从我的作品，我的收藏跳转过来
    private LinearLayout ll_text_all;
    @Override
    protected View initView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Vitamio.isInitialized(getApplicationContext());
        View rootView = View.inflate(this, R.layout.activity_video_show_photo_info, null);
        setContentView(rootView);
        sv1 = (ScrollView) rootView.findViewById(R.id.sv1);
        sv = (PullToRefreshScrollView) rootView.findViewById(R.id.sv);
        gv_lable = (GridView) rootView.findViewById(R.id.gv_lable);
        lv_comment = (CustomExpandableListView) rootView.findViewById(R.id.lv_comment);
        iv_share = (ImageView) rootView.findViewById(R.id.iv_share);
        iv_top_back = (ImageView) rootView.findViewById(R.id.iv_top_back);
        tv_goto_attention = (TextView) rootView.findViewById(R.id.tv_goto_attention);
        rl_comment = (RelativeLayout) rootView.findViewById(R.id.rl_comment);
        et_comment = (EditText) rootView.findViewById(R.id.et_comment);
        tv_submit_homework = (TextView) rootView.findViewById(R.id.tv_submit_homework);
        tv_goto_comment = (TextView) rootView.findViewById(R.id.tv_goto_comment);
        tv_error = (TextView) rootView.findViewById(R.id.tv_error);
        tv_one_key_order = (TextView) rootView.findViewById(R.id.tv_one_key_order);
        prl_share = (PercentRelativeLayout) rootView.findViewById(R.id.prl_share);
        gv_share = (GridView) rootView.findViewById(R.id.gv_share);
        rl_share = (PercentLinearLayout) rootView.findViewById(R.id.rl_share);

        iv_uesr_img = (CircleImageView) rootView.findViewById(R.id.iv_uesr_img);
        tv_user_name = (TextView) rootView.findViewById(R.id.tv_user_name);
        tv_look_num = (TextView) rootView.findViewById(R.id.tv_look_num);
        tv_date = (TextView) rootView.findViewById(R.id.tv_date);
        user_share_video = (TextView) rootView.findViewById(R.id.user_share_video);
        user_collection_video = (TextView) rootView.findViewById(R.id.user_collection_video);
        user_like_num = (TextView) rootView.findViewById(R.id.user_like_num);
        lv_show_photo = (MyListView) rootView.findViewById(R.id.lv_show_photo);
        prl_collection = (PercentRelativeLayout) rootView.findViewById(R.id.prl_collection);
        prl_like = (PercentRelativeLayout) rootView.findViewById(R.id.prl_like);
        iv_collection = (ImageView) rootView.findViewById(R.id.iv_collection);
        iv_liked = (ImageView) rootView.findViewById(R.id.iv_liked);
        tv_comment_num = (TextView) rootView.findViewById(R.id.tv_comment_num);
        tv_cancle = (TextView) rootView.findViewById(R.id.tv_cancle);
        lv_look = (HorizontalListView) rootView.findViewById(R.id.lv_look);
        tv_title = (TextView) rootView.findViewById(R.id.tv_title);
        pl_look = (PercentLinearLayout) findViewById(R.id.pl_look);
        ll_text_all = (LinearLayout) findViewById(R.id.ll_text_all);

        tv_cancle.setOnClickListener(this);
        prl_share.setOnClickListener(this);
        iv_share.setOnClickListener(this);
        iv_top_back.setOnClickListener(this);
        tv_goto_comment.setOnClickListener(this);
        tv_submit_homework.setOnClickListener(this);
        tv_one_key_order.setOnClickListener(this);
        tv_goto_attention.setOnClickListener(this);
        prl_collection.setOnClickListener(this);
        prl_like.setOnClickListener(this);
        tv_error.setOnClickListener(this);
        iv_uesr_img.setOnClickListener(this);

        layoutInflater = LayoutInflater.from(this);
        imageLoader = ImageLoader.getInstance();
        // 使用DisplayImageOption.Builder()创建DisplayImageOptions
        options = new DisplayImageOptions.Builder()
                .showStubImage(R.drawable.pic_s) // 设置图片下载期间显示的图片
                .showImageForEmptyUri(R.drawable.pic_s) // 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.drawable.pic_s) // 设置图片加载或解码过程中发生错误显示的图片
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisc(true) // 设置下载的图片是否缓存在SD卡中
                        // .displayer(new RoundedBitmapDisplayer(20)) // 设置成圆角图片
                .bitmapConfig(Bitmap.Config.ARGB_8888)
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                .build(); // 创建配置过的DisplayImageOption对象

        initData();
        return null;
    }

    public void initData() {
        videoCommentBeanList = new ArrayList<GetVideoCommentListResponse.VideoCommentBean>();
        //设置分享数据
        Intent intent = getIntent();
        video_id = intent.getIntExtra("video_id", 0);
        ismy = intent.getBooleanExtra("ismy", false);
        if(ismy){
            pl_look.setVisibility(View.GONE);
        }else {
            pl_look.setVisibility(View.VISIBLE);
        }

        userId = SharedPrefrenceUtils.getString(this, "user_id");
        gson = new Gson();
        loadingDialog = DialogUtils.createLoadDialog(VideoShowPhotoInfoActivity.this, true);
        showVideoLableBeanList = new ArrayList<VideoLableBean>();

        getVideoInfo();

        List<ShareInfo> slist = new ArrayList<ShareInfo>();
        ShareInfo share1 = new ShareInfo();
        share1.setShare_photo(R.drawable.img_qq);
        share1.setShare_photo_selected(R.drawable.img_qq);
        share1.setShare_name(QQ.NAME);
        slist.add(share1);

        ShareInfo share3 = new ShareInfo();
        share3.setShare_photo(R.drawable.img_qzone);
        share3.setShare_photo_selected(R.drawable.img_qzone);
        share3.setShare_name(QZone.NAME);
        slist.add(share3);

        ShareInfo share2 = new ShareInfo();
        share2.setShare_photo(R.drawable.img_sina);
        share2.setShare_photo_selected(R.drawable.img_sina);
        share2.setShare_name(SinaWeibo.NAME);
        slist.add(share2);
        ShareInfo share4 = new ShareInfo();
        share4.setShare_photo(R.drawable.icon_share_circle);
        share4.setShare_photo_selected(R.drawable.icon_share_circle);
        share4.setShare_name(WechatMoments.NAME);
        slist.add(share4);

        ShareInfo share5 = new ShareInfo();
        share5.setShare_photo(R.drawable.icon_share_wechat);
        share5.setShare_photo_selected(R.drawable.icon_share_wechat);
        share5.setShare_name(Wechat.NAME);
        slist.add(share5);

        ShareInfo share6 = new ShareInfo();
        share6.setShare_photo(R.drawable.img_video_copy);
        share6.setShare_photo_selected(R.drawable.img_video_copy);
        share6.setShare_name("复制链接");
        slist.add(share6);

        sadapter = new ShareAdapter(slist, rl_share, this, false,this);
        gv_share.setAdapter(sadapter);
        sv.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ScrollView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
                if (!isRefresh) {
                    pageNo = 1;
                    videoCommentBeanList.clear();
                    isRefresh = true;
                    getVideoInfo();
                }
            }
        });
        //搜索输入监听
        et_comment.setOnKeyListener(new View.OnKeyListener() {
            Message msg;

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (KeyEvent.KEYCODE_ENTER == keyCode && event.getAction() == KeyEvent.ACTION_DOWN) {

                    if (LoginUtils.isLogin()) {
                        content = et_comment.getText().toString();
                        loadingDialog.show();
                        if(isError){
                            runProblemFeedback();
                        }else {
                            if (!isReply) {
                                runSubmitVideoComment();
                            } else {
                                runReplyVideoComment();
                            }
                        }
                    } else {
                        mDialog = DialogUtils.showAlertDialog(VideoShowPhotoInfoActivity.this,
                                "请先登陆您的账号!", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        mDialog.dismiss();
                                        Intent intent = new Intent(VideoShowPhotoInfoActivity.this, LoginActivity.class);
                                        intent.putExtra("type",0);
                                        UIUtils.startActivityNextAnim(intent);
                                    }
                                });
                    }

                    return true;
                }
                return false;

            }
        });
        rl_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                //隐藏软键盘
                imm.hideSoftInputFromWindow(VideoShowPhotoInfoActivity.this.getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
                rl_comment.setVisibility(View.GONE);
                et_comment.clearFocus();
                isReply = false;
            }
        });
    }

    /**
     * 纠错
     */
    public void runProblemFeedback() {
        loadingDialog.show();
        ProblemFeedbackProtocol problemFeedbackProtocol = new ProblemFeedbackProtocol();
        ProblemFeedbackRequest problemFeedbackRequest = new ProblemFeedbackRequest();
        url = problemFeedbackProtocol.getApiFun();
        problemFeedbackRequest.map.put("user_id", userId);
        problemFeedbackRequest.map.put("msg_type", "1");
        problemFeedbackRequest.map.put("msg_title","纠错");
        problemFeedbackRequest.map.put("video_id",video_id+"");
        String wholeContent = content;
        if(videoInfoBean != null){
            wholeContent = "投诉的作品id:"+videoInfoBean.getId()+",投诉的作品名称:"+videoInfoBean.getTitle()+",投诉的内容:"+content;
        }
        problemFeedbackRequest.map.put("msg_content",wholeContent);
        isError = false;
        MyVolley.uploadNoFile(MyVolley.POST, url, problemFeedbackRequest.map, new MyVolley.VolleyCallback() {
            @Override
            public void dealWithJson(String address, String json) {
                LogUtils.e("json:" + json);
                Gson gson = new Gson();
                ProblemFeedbackResponse problemFeedbackResponse = gson.fromJson(json, ProblemFeedbackResponse.class);
                if (problemFeedbackResponse.code==0) {
                    loadingDialog.dismiss();
                    et_comment.setText("");
                    DialogUtils.showAlertDialog(VideoShowPhotoInfoActivity.this,
                            "提交纠错成功!");
                } else {
                    loadingDialog.dismiss();
                    DialogUtils.showAlertDialog(VideoShowPhotoInfoActivity.this,
                            problemFeedbackResponse.resultText);
                }


            }

            @Override
            public void dealWithError(String address, String error) {
                loadingDialog.dismiss();
                et_comment.setText("");
                DialogUtils.showAlertDialog(VideoShowPhotoInfoActivity.this, error);
            }
        });
    }


    /**
     * 获取视频详情
     */
    private void getVideoInfo() {
        loadingDialog.show();
        url = "video/getVideoDesc.do";
        Map<String, String> params = new HashMap<String, String>();
        params.put("video_id", video_id + "");
        if (StringUtils.isEmpty(userId)) {
            userId = "0";
        }
        params.put("user_id", userId);
        MyVolley.uploadNoFile(MyVolley.POST, url, params, new MyVolley.VolleyCallback() {

            @Override
            public void dealWithJson(String address, String json) {
                LogUtils.e("获取视频详情:" + json);
                videoInfoResponse = gson.fromJson(json, VideoInfoResponse.class);
                if (videoInfoResponse.getCode() == 0) {
                    videoInfoBean = videoInfoResponse.getData();
                    if (videoInfoBean != null) {
                        if (!TextUtils.isEmpty(videoInfoBean.getGoodList())) {
                            List<GoodsAttrBean> goodList = gson.fromJson(videoInfoBean.getGoodList(), new TypeToken<List<GoodsAttrBean>>() {
                            }.getType());
                            if (goodList.size() > 0) {
                                tv_one_key_order.setVisibility(View.VISIBLE);
                            } else {
                                tv_one_key_order.setVisibility(View.GONE);
                            }
                            ll_text_all.setVisibility(View.VISIBLE);
                        }
                        isCollection = videoInfoBean.getIs_collection();
                        isLiked = videoInfoBean.getIs_liked();
                        if (isCollection == 0) {
                            iv_collection.setImageResource(R.mipmap.bt_collect_noselectable);
                        } else {
                            iv_collection.setImageResource(R.mipmap.bt_collect_selectable);
                        }
                        if (isLiked == 0) {
                            iv_liked.setImageResource(R.mipmap.bt_love_noselectable);
                        } else {
                            iv_liked.setImageResource(R.mipmap.bt_love_selectable);
                        }
                        if (!StringUtils.isEmpty(videoInfoBean.getAvatar())) {
                            imageLoader.displayImage(videoInfoBean.getAvatar(), iv_uesr_img, PictureOption.getSimpleOptions());
                        }
                        if (!StringUtils.isEmpty(videoInfoBean.getAlias())) {
                            tv_user_name.setText(videoInfoBean.getAlias());
                        }
                        tv_look_num.setText(videoInfoBean.getPlay_cnt() + "");
                        SimpleDateFormat time = new SimpleDateFormat("yyyy.MM.dd");
                        if (!StringUtils.isEmpty(videoInfoBean.getCreate_time())) {
                            long time1 = Long.valueOf(videoInfoBean.getCreate_time());
                            String timeStr = time.format(new Date(time1 * 1000));
                            tv_date.setText(timeStr);
                        }
                        videoLableBeanList = videoInfoResponse.getData().getTagDataLists();
                        if (videoLableBeanList != null) {
                            if (videoLableBeanList.size() >= 2) {
                                showVideoLableBeanList.clear();
                                showVideoLableBeanList.add(videoLableBeanList.get(0));
                                showVideoLableBeanList.add(videoLableBeanList.get(1));
                                videoLableAdapter = new VideoLableAdapter(VideoShowPhotoInfoActivity.this, showVideoLableBeanList);
                            } else {
                                videoLableAdapter = new VideoLableAdapter(VideoShowPhotoInfoActivity.this, videoLableBeanList);
                            }
                            gv_lable.setAdapter(videoLableAdapter);
                        }

                        if (!StringUtils.isEmpty(videoInfoBean.getPics())) {
                            try{
                                showPhotoContentBeanList = gson.fromJson(videoInfoBean.getPics(), new TypeToken<List<ShowPhotoContentBean>>() {
                                }.getType());
                                if (showPhotoContentBeanList != null) {
                                    videoShowPhotoAdapter = new VideoShowPhotoAdapter(VideoShowPhotoInfoActivity.this, showPhotoContentBeanList);
                                    lv_show_photo.setAdapter(videoShowPhotoAdapter);
                                    imagepaths=new ArrayList<String>();
                                    for(int i=0;i<showPhotoContentBeanList.size();i++){
                                        if(!StringUtils.isEmpty(showPhotoContentBeanList.get(i).getPic1())) {
                                            imagepaths.add(showPhotoContentBeanList.get(i).getPic1());
                                        }
                                        if(!StringUtils.isEmpty(showPhotoContentBeanList.get(i).getPic2())) {
                                            imagepaths.add(showPhotoContentBeanList.get(i).getPic2());
                                        }
                                    }


                                }
                            }
                            catch (Exception e){

                            }

                            lv_show_photo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    // 跳到查看大图界面
                                    Intent intent = new Intent(VideoShowPhotoInfoActivity.this,
                                            ShowBigPictrue.class);
                                    intent.putExtra("position", position);
                                    intent.putStringArrayListExtra("imagePath", (ArrayList)imagepaths);
                                    UIUtils.startActivityNextAnim(intent);
                                }
                            });
                        }
                        if (videoInfoBean.getIs_fee() == 1) {
                            tv_one_key_order.setVisibility(View.VISIBLE);
                        } else {
                            tv_one_key_order.setVisibility(View.GONE);
                        }
                        ll_text_all.setVisibility(View.VISIBLE);

                        if (!StringUtils.isEmpty(videoInfoBean.getTitle())) {
                            tv_title.setText(videoInfoBean.getTitle());
                        }

                        tv_comment_num.setText("" + videoInfoBean.getCommentCnt());
                        user_share_video.setText(videoInfoBean.getForward_cnt() + "");
                        user_collection_video.setText(videoInfoBean.getCollection_cnt() + "");
                        user_like_num.setText(videoInfoBean.getLiked_cnt() + "");
                        String shareUrl = Constants.SHARE_URL + "share/video_pic_share.shtml?share_user_id=" + SharedPrefrenceUtils.getString(VideoShowPhotoInfoActivity.this, "user_id") + "&video_id=" + video_id;
                        shareDateBean = new ShareDateBean();
                        shareDateBean.setContent("我在美甲屋APP发现一个不错的教程，快来看看吧！");
//                        shareDateBean.setContent(videoInfoBean.getContent());
                        if (showPhotoContentBeanList != null) {
                            shareDateBean.setImageurl(showPhotoContentBeanList.get(0).getPic1());
                        }
                        shareDateBean.setTitle(videoInfoBean.getTitle());
                        shareDateBean.setUrl(shareUrl);
                        sadapter.setShareDateBean(shareDateBean);

                        if(LoginUtils.isLogin()) {
                            getAttention(videoInfoBean.getId_value());

                        }
                        getLookVideos();

                        runGetVideoCommentList();
                    }
                } else {
                    DialogUtils.showAlertDialog(VideoShowPhotoInfoActivity.this, videoInfoResponse.getResultText());
                }
                if (isRefresh) {
                    isRefresh = false;
                    sv.onRefreshComplete();
                }
            }

            @Override
            public void dealWithError(String address, String error) {
                LogUtils.e("获取视频详情错误:" + error);
                if (isRefresh) {
                    isRefresh = false;
                    sv.onRefreshComplete();
                }
                DialogUtils.showAlertDialog(VideoShowPhotoInfoActivity.this, error);
            }
        });
    }

    /**
     * 是否已经关注
     */
    public void getAttention(int follow_userid) {
        loadingDialog.show();
        GetFollowInfoProtocol getFollowInfoProtocol = new GetFollowInfoProtocol();
        GetFollowInfoRequest getFollowInfoRequest = new GetFollowInfoRequest();
        url = getFollowInfoProtocol.getApiFun();
        getFollowInfoRequest.map.put("user_id", SharedPrefrenceUtils.getString(VideoShowPhotoInfoActivity.this, "user_id"));
        getFollowInfoRequest.map.put("follow_userid", String.valueOf(follow_userid));


        MyVolley.uploadNoFile(MyVolley.POST, url, getFollowInfoRequest.map, new MyVolley.VolleyCallback() {
            @Override
            public void dealWithJson(String address, String json) {

                Gson gson = new Gson();
                getFollowInfoResponse = gson.fromJson(json, GetFollowInfoResponse.class);
                LogUtils.e("sendVerificationCodeResponse:" + json.toString());
                if (getFollowInfoResponse.code == 0) {
                    if (getFollowInfoResponse.data != null) {
                        if (getFollowInfoResponse.data.is_follow == 1) {
                            tv_goto_attention.setText("已关注");
                        } else {
                            tv_goto_attention.setText("未关注");
                        }
                    }

                } else {
                    loadingDialog.dismiss();
                    DialogUtils.showAlertDialog(VideoShowPhotoInfoActivity.this,
                            getFollowInfoResponse.resultText);
                }


            }

            @Override
            public void dealWithError(String address, String error) {
                loadingDialog.dismiss();
                DialogUtils.showAlertDialog(VideoShowPhotoInfoActivity.this, error);
            }
        });
    }


    /**
     * 获取看了又看列表
     */
    private void getLookVideos() {
        loadingDialog.show();
        url = "video/searchVideo.do";
        Map<String, String> params = new HashMap<String, String>();
        if (videoInfoResponse.getData().getTagDataLists() != null && videoInfoResponse.getData().getTagDataLists().size() > 0) {
            params.put("keyword", videoInfoResponse.getData().getTagDataLists().get(0).getTag_name());
        }else {
            params.put("keyword","''");
        }
        if (StringUtils.isEmpty(userId)) {
            userId = "0";
        }
        params.put("user_id", userId);
        params.put("pageNo", "1");
        params.put("pageSize", "4");
        params.put("type", "2");
        MyVolley.uploadNoFile(MyVolley.POST, url, params, new MyVolley.VolleyCallback() {

            @Override
            public void dealWithJson(String address, String json) {
                LogUtils.e("获取看了又看列表:" + json);
                getVideoListResponse = gson.fromJson(json, GetVideoListResponse.class);
                if (getVideoListResponse != null) {
                    if (getVideoListResponse.code == 0) {
                        lookVideos = getVideoListResponse.dataList;
                        if (lookVideos != null) {
                            if(lookVideos.size()>1) {
                                for (int i = 0; i < lookVideos.size(); i++) {
                                    VideoListBean videoListBean = lookVideos.get(i);
                                    if (videoListBean.getId() == video_id) {
                                        lookVideos.remove(i);
                                        break;
                                    }
                                }

                                if(lookVideos.size()>3){
                                    lookVideos.remove(lookVideos.size()-1);
                                }

                                lookAdapter = new LookAdapter(VideoShowPhotoInfoActivity.this, lookVideos);
                                lv_look.setAdapter(lookAdapter);

                                lv_look.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        VideoListBean videoListBean = lookVideos.get(position);
                                        Intent intent = null;
                                        if (videoListBean.getType() == 0) {
                                            intent = new Intent(VideoShowPhotoInfoActivity.this, VideoInfoActivity.class);
                                            intent.putExtra("video_name", videoListBean.getTitle());
                                            intent.putExtra("video_id", videoListBean.getId());
                                            intent.putExtra("cover_img", videoListBean.getCover_img());
                                            intent.putExtra("video_url", videoListBean.getVideo_url());
                                        } else if (videoListBean.getType() == 1) {
                                            intent = new Intent(VideoShowPhotoInfoActivity.this, VideoShowInfoActivity.class);
                                            intent.putExtra("video_name", videoListBean.getTitle());
                                            intent.putExtra("video_id", videoListBean.getId());
                                            intent.putExtra("cover_img", videoListBean.getCover_img());
                                            intent.putExtra("video_url", videoListBean.getVideo_url());
                                        } else if (videoListBean.getType() == 2) {
                                            intent = new Intent(VideoShowPhotoInfoActivity.this, VideoShowPhotoInfoActivity.class);
                                            intent.putExtra("video_name", videoListBean.getTitle());
                                            intent.putExtra("video_id", videoListBean.getId());
                                            intent.putExtra("cover_img", videoListBean.getCover_img());
                                            intent.putExtra("video_url", videoListBean.getVideo_url());
                                        }
                                        startActivity(intent);
                                        finish();
                                    }
                                });
                            }
                            else{
                                lv_look.setVisibility(View.GONE);
                            }
                        }
                    } else {
                        DialogUtils.showAlertDialog(VideoShowPhotoInfoActivity.this, getVideoListResponse.resultText);
                    }
                }
            }

            @Override
            public void dealWithError(String address, String error) {
                LogUtils.e("获取看了又看列表错误:" + error);
                DialogUtils.showAlertDialog(VideoShowPhotoInfoActivity.this, error);
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_cancle:
                rl_share.setVisibility(View.INVISIBLE);
                break;
            case R.id.iv_uesr_img:
                if (LoginUtils.isLogin()) {
                    Intent intent = new Intent(VideoShowPhotoInfoActivity.this, AttentionActivity.class);
                    intent.putExtra("follow_userid", videoInfoBean.getId_value());
                    UIUtils.startActivityNextAnim(intent);
                } else {
                    mDialog = DialogUtils.showAlertDialog(VideoShowPhotoInfoActivity.this,
                            "请先登陆您的账号!", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mDialog.dismiss();
                                    Intent intent = new Intent(VideoShowPhotoInfoActivity.this, LoginActivity.class);
                                    intent.putExtra("type", 2);
                                    UIUtils.startActivityNextAnim(intent);
                                }
                            });
                }
                break;
            case R.id.tv_goto_attention: {
                if (LoginUtils.isLogin()) {
                    Intent intent = new Intent(VideoShowPhotoInfoActivity.this, AttentionActivity.class);
                    intent.putExtra("follow_userid", videoInfoBean.getId_value());
                    UIUtils.startActivityNextAnim(intent);
                } else {
                    mDialog = DialogUtils.showAlertDialog(VideoShowPhotoInfoActivity.this,
                            "请先登陆您的账号!", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mDialog.dismiss();
                                    Intent intent = new Intent(VideoShowPhotoInfoActivity.this, LoginActivity.class);
                                    intent.putExtra("type",2);
                                    UIUtils.startActivityNextAnim(intent);
                                }
                            });
                }
                break;
            }
            case R.id.tv_one_key_order: {
                if (LoginUtils.isLogin()) {
                    if (!TextUtils.isEmpty(videoInfoBean.getGoodList())) {
                        List<GoodsAttrBean> goodList = gson.fromJson(videoInfoBean.getGoodList(), new TypeToken<List<GoodsAttrBean>>() {
                        }.getType());
                        if (goodList.size() > 0) {
                            Intent intent = new Intent(VideoShowPhotoInfoActivity.this, IncludeGoodsActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("goodList", (Serializable) goodList);
                            intent.putExtras(bundle);
                            intent.putExtra("fee", videoInfoBean.getFee());
                            intent.putExtra("video_id", videoInfoBean.getId());
                            UIUtils.startActivityNextAnim(intent);
                        } else {
                            DialogUtils.showAlertDialog(VideoShowPhotoInfoActivity.this, "亲，没有商品可以下单!");
                        }
                    } else {
                        DialogUtils.showAlertDialog(VideoShowPhotoInfoActivity.this, "亲，没有商品可以下单!");
                    }
                } else {
                    mDialog = DialogUtils.showAlertDialog(VideoShowPhotoInfoActivity.this,
                            "请先登陆您的账号!", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mDialog.dismiss();
                                    Intent intent = new Intent(VideoShowPhotoInfoActivity.this, LoginActivity.class);
                                    intent.putExtra("type",2);
                                    UIUtils.startActivityNextAnim(intent);
                                }
                            });
                }
                break;
            }
            case R.id.tv_submit_homework: {
                if (LoginUtils.isLogin()) {
                    Intent intent = new Intent(VideoShowPhotoInfoActivity.this, SubmitHomeworkActivity.class);
                    intent.putExtra("video_id", String.valueOf(video_id));
                    UIUtils.startActivityForResultNextAnim(intent, 1);
                } else {
                    mDialog = DialogUtils.showAlertDialog(VideoShowPhotoInfoActivity.this,
                            "请先登陆您的账号!", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mDialog.dismiss();
                                    Intent intent = new Intent(VideoShowPhotoInfoActivity.this, LoginActivity.class);
                                    intent.putExtra("type",2);
                                    UIUtils.startActivityNextAnim(intent);
                                }
                            });
                }
                break;
            }
            case R.id.tv_goto_comment: {
                if (LoginUtils.isLogin()) {
                    rl_comment.setVisibility(View.VISIBLE);
                    rl_comment.setClickable(true);
                    et_comment.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(et_comment, InputMethodManager.SHOW_IMPLICIT);
                } else {
                    mDialog = DialogUtils.showAlertDialog(VideoShowPhotoInfoActivity.this,
                            "请先登陆您的账号!", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mDialog.dismiss();
                                    Intent intent = new Intent(VideoShowPhotoInfoActivity.this, LoginActivity.class);
                                    intent.putExtra("type",2);
                                    UIUtils.startActivityNextAnim(intent);
                                }
                            });
                }
                break;
            }
            case R.id.tv_error: {
                if (LoginUtils.isLogin()) {
                    rl_comment.setVisibility(View.VISIBLE);
                    rl_comment.setClickable(true);
                    et_comment.requestFocus();
                    isError = true;
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(et_comment, InputMethodManager.SHOW_IMPLICIT);
                } else {
                    mDialog = DialogUtils.showAlertDialog(VideoShowPhotoInfoActivity.this,
                            "请先登陆您的账号!", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mDialog.dismiss();
                                    Intent intent = new Intent(VideoShowPhotoInfoActivity.this, LoginActivity.class);
                                    intent.putExtra("type",2);
                                    UIUtils.startActivityNextAnim(intent);
                                }
                            });
                }
                break;
            }
            case R.id.prl_share:
                if (SharedPrefrenceUtils.getBoolean(VideoShowPhotoInfoActivity.this, "isLogin")) {
                    String shareUrl = Constants.SHARE_URL + "share/video_pic_share.shtml?share_user_id=" + SharedPrefrenceUtils.getString(VideoShowPhotoInfoActivity.this, "user_id") + "&video_id=" + video_id;
                    shareDateBean.setUrl(shareUrl);
                    rl_share.setVisibility(View.VISIBLE);
                    // 初始化ShareSDK
                    ShareSDK.initSDK(UIUtils.getContext());
                } else {
                    mDialog = DialogUtils.showAlertDialog(VideoShowPhotoInfoActivity.this,
                            "请先登陆您的账号!", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mDialog.dismiss();
                                    Intent intent = new Intent(VideoShowPhotoInfoActivity.this, LoginActivity.class);
                                    intent.putExtra("type", 0);
                                    UIUtils.startActivityNextAnim(intent);
                                }
                            });
                }
                break;
            case R.id.iv_top_back:
                finish();
                overridePendingTransition(R.anim.animprv_in, R.anim.animprv_out);
                break;
            case R.id.prl_like:
                if (LoginUtils.isLogin()) {
                    //点赞
                    if (isLiked == 0) {
                        addCollectionOrLike(0);
                    }
//                    else {
//                        cancelCollectionOrLike(0);
//                    }
                } else {
                    mDialog = DialogUtils.showAlertDialog(VideoShowPhotoInfoActivity.this,
                            "请先登陆您的账号!", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mDialog.dismiss();
                                    Intent intent = new Intent(VideoShowPhotoInfoActivity.this, LoginActivity.class);
                                    intent.putExtra("type",2);
                                    UIUtils.startActivityNextAnim(intent);
                                }
                            });
                }
                break;
            case R.id.prl_collection:
                if (LoginUtils.isLogin()) {
                    //收藏
                    if (isCollection == 0) {
                        addCollectionOrLike(1);
                    }
//                    else {
//                        cancelCollectionOrLike(1);
//                    }
                } else {
                    mDialog = DialogUtils.showAlertDialog(VideoShowPhotoInfoActivity.this,
                            "请先登陆您的账号!", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mDialog.dismiss();
                                    Intent intent = new Intent(VideoShowPhotoInfoActivity.this, LoginActivity.class);
                                    intent.putExtra("type",2);
                                    UIUtils.startActivityNextAnim(intent);
                                }
                            });
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == 2) {
                tv_comment_num.setText("" + (Integer.parseInt(tv_comment_num.getText().toString()) + 1));
                pageNo = 1;
                videoCommentBeanList.clear();
                runGetVideoCommentList();
            }
        }
    }

    /**
     * 收藏或者点赞
     */
    private void addCollectionOrLike(final int type) {
        loadingDialog.show();
        url = "video/addCollectionOrWork.do";
        Map<String, String> params = new HashMap<String, String>();
        params.put("video_id", video_id + "");
        if (StringUtils.isEmpty(userId)) {
            userId = "0";
        }
        params.put("user_id", userId);
        params.put("type", type + "");
        MyVolley.uploadNoFile(MyVolley.POST, url, params, new MyVolley.VolleyCallback() {

            @Override
            public void dealWithJson(String address, String json) {
                LogUtils.e("收藏或者点赞:" + json);
                loadingDialog.dismiss();
                baseResponse = gson.fromJson(json, BaseResponse.class);
                if (baseResponse.getCode() == 0) {
                    if (type == 0) {
                        isLiked = 1;
                        iv_liked.setImageResource(R.mipmap.bt_love_selectable);
                        user_like_num.setText((Integer.parseInt(user_like_num.getText().toString().trim()) + 1) + "");
                    } else {
                        isCollection = 1;
                        iv_collection.setImageResource(R.mipmap.bt_collect_selectable);
                        user_collection_video.setText((Integer.parseInt(user_collection_video.getText().toString().trim()) + 1) + "");
                        SharedPrefrenceUtils.setBoolean(UIUtils.getContext(), "isUpdate", true);
                    }
                } else {
                    DialogUtils.showAlertDialog(VideoShowPhotoInfoActivity.this, baseResponse.getResultText());
                }

            }

            @Override
            public void dealWithError(String address, String error) {
                LogUtils.e("收藏或者点赞错误:" + error);
                loadingDialog.dismiss();
                DialogUtils.showAlertDialog(VideoShowPhotoInfoActivity.this, error);
            }
        });
    }

//    /**
//     * 取消收藏或者点赞
//     */
//    private void cancelCollectionOrLike(final int type) {
//        loadingDialog.show();
//        url = "video/cancelCollectionOrWork.do";
//        Map<String, String> params = new HashMap<String, String>();
//        params.put("video_id", video_id + "");
//        if (StringUtils.isEmpty(userId)) {
//            userId = "0";
//        }
//        params.put("user_id", userId);
//        params.put("type", type + "");
//        MyVolley.uploadNoFile(MyVolley.POST, url, params, new MyVolley.VolleyCallback() {
//
//            @Override
//            public void dealWithJson(String address, String json) {
//                LogUtils.e("取消收藏或者点赞:" + json);
//                loadingDialog.dismiss();
//                baseResponse = gson.fromJson(json, BaseResponse.class);
//                if (baseResponse.getCode() == 0) {
//                    if (type == 0) {
//                        isLiked = 0;
//                        iv_liked.setImageResource(R.mipmap.bt_love_noselectable);
//                        user_like_num.setText((Integer.parseInt(user_like_num.getText().toString().trim()) - 1) + "");
//                    } else {
//                        isCollection = 0;
//                        iv_collection.setImageResource(R.mipmap.bt_collect_noselectable);
//                        user_collection_video.setText((Integer.parseInt(user_collection_video.getText().toString().trim()) - 1) + "");
//                        SharedPrefrenceUtils.setBoolean(UIUtils.getContext(), "isUpdate", true);
//                    }
//                } else {
//                    DialogUtils.showAlertDialog(VideoShowPhotoInfoActivity.this, baseResponse.getResultText());
//                }
//            }
//
//            @Override
//            public void dealWithError(String address, String error) {
//                LogUtils.e("取消收藏或者点赞错误:" + error);
//                loadingDialog.dismiss();
//                DialogUtils.showAlertDialog(VideoShowPhotoInfoActivity.this, error);
//            }
//        });
//    }

    @Override
    public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
        Toast.makeText(VideoShowPhotoInfoActivity.this, "分享成功!",
                Toast.LENGTH_SHORT).show();
        runVideoPlayOrForwardCnt();
    }

    @Override
    public void onError(Platform platform, int i, Throwable throwable) {
        Toast.makeText(VideoShowPhotoInfoActivity.this, "分享失败!" + throwable,
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onCancel(Platform platform, int i) {
        Toast.makeText(VideoShowPhotoInfoActivity.this, "分享取消!",
                Toast.LENGTH_SHORT).show();
    }


    //获取视频评论
    public void runGetVideoCommentList() {
        GetVideoCommentListProtocol getVideoCommentListProtocol = new GetVideoCommentListProtocol();
        GetVideoCommentListRequest getVideoCommentListRequest = new GetVideoCommentListRequest();
        url = getVideoCommentListProtocol.getApiFun();
        getVideoCommentListRequest.map.put("user_id", SharedPrefrenceUtils.getString(VideoShowPhotoInfoActivity.this, "user_id"));
        getVideoCommentListRequest.map.put("video_id", String.valueOf(video_id));
        getVideoCommentListRequest.map.put("pageNo", String.valueOf(pageNo));
        getVideoCommentListRequest.map.put("pageSize", String.valueOf(pageSize));

        MyVolley.uploadNoFile(MyVolley.POST, url, getVideoCommentListRequest.map, new MyVolley.VolleyCallback() {
            @Override
            public void dealWithJson(String address, String json) {
                LogUtils.e("json:" + json);
                Gson gson = new Gson();
                GetVideoCommentListResponse getVideoCommentListResponse = gson.fromJson(json, GetVideoCommentListResponse.class);
                if (getVideoCommentListResponse.code.equals("0")) {
                    loadingDialog.dismiss();
                    videoCommentBeanList.clear();
                    if (getVideoCommentListResponse.dataList.size() > 0) {
                        videoCommentBeanList.addAll(getVideoCommentListResponse.dataList);
                        setComment(videoCommentBeanList);
                    }
                    else{
                        if (videoCommentsAdapter != null){
                            videoCommentsAdapter.setDataList(videoCommentBeanList);
                            videoCommentsAdapter.notifyDataSetChanged();
                        }
                    }
                } else {
                    loadingDialog.dismiss();
                    DialogUtils.showAlertDialog(VideoShowPhotoInfoActivity.this,
                            getVideoCommentListResponse.resultText);
                }

            }

            @Override
            public void dealWithError(String address, String error) {
                loadingDialog.dismiss();
                DialogUtils.showAlertDialog(VideoShowPhotoInfoActivity.this, error);
            }
        });
    }

    public void setComment(final List<GetVideoCommentListResponse.VideoCommentBean> dataList) {
        if (videoCommentsAdapter == null) {
            videoCommentsAdapter = new VideoCommentsAdapter(VideoShowPhotoInfoActivity.this, dataList, loadingDialog);
        } else {
            videoCommentsAdapter.setDataList(dataList);
            videoCommentsAdapter.notifyDataSetChanged();
        }
        lv_comment.setAdapter(videoCommentsAdapter);
        lv_comment.expandGroup(0);
        for (int i = 0; i < dataList.size(); i++) {
            lv_comment.expandGroup(i);
        }
        //设置listview的滑动优先级
//        lv_comment.setOnTouchListener(new View.OnTouchListener() {
//
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                if (event.getAction() == MotionEvent.ACTION_UP) {
//                    sv1.requestDisallowInterceptTouchEvent(false);
//                } else {
//                    sv1.requestDisallowInterceptTouchEvent(true);
//                }
//                return false;
//            }
//        });

//        lv_comment.setGroupIndicator(null);
        //禁止展开
        lv_comment.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                                        int groupPosition, long id) {
                // TODO Auto-generated method stub
                LogUtils.e("groupPosition:" + groupPosition);
                comment_id = String.valueOf(dataList.get(groupPosition).comment_id);
                parent_id = "0";
                isReply = true;
                rl_comment.setVisibility(View.VISIBLE);
                rl_comment.setClickable(true);
                et_comment.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(et_comment, InputMethodManager.SHOW_IMPLICIT);
                return true;
            }
        });
        lv_comment.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                comment_id = String.valueOf(dataList.get(groupPosition).comment_id);
                parent_id = dataList.get(groupPosition).replyDataList.get(childPosition).reply_id;
                isReply = true;
                rl_comment.setVisibility(View.VISIBLE);
                rl_comment.setClickable(true);
                et_comment.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(et_comment, InputMethodManager.SHOW_IMPLICIT);
                return false;
            }
        });
    }

    //评论视频
    public void runSubmitVideoComment() {
        loadingDialog.show();
        SubmitVideoCommentProtocol submitVideoCommentProtocol = new SubmitVideoCommentProtocol();
        SubmitVideoCommentRequest submitVideoCommentRequest = new SubmitVideoCommentRequest();
        url = submitVideoCommentProtocol.getApiFun();
        submitVideoCommentRequest.map.put("user_id", SharedPrefrenceUtils.getString(VideoShowPhotoInfoActivity.this, "user_id"));
        submitVideoCommentRequest.map.put("video_id", String.valueOf(video_id));
        submitVideoCommentRequest.map.put("content", content);

        MyVolley.uploadNoFile(MyVolley.POST, url, submitVideoCommentRequest.map, new MyVolley.VolleyCallback() {
            @Override
            public void dealWithJson(String address, String json) {
                LogUtils.e("json:" + json);
                Gson gson = new Gson();
                SubmitVideoCommentResponse submitVideoCommentResponse = gson.fromJson(json, SubmitVideoCommentResponse.class);
                if (submitVideoCommentResponse.code.equals("0")) {
                    et_comment.setText("");
                    loadingDialog.dismiss();
                    rl_comment.setVisibility(View.GONE);
                    tv_comment_num.setText("" + (Integer.parseInt(tv_comment_num.getText().toString()) + 1));
                    pageNo = 1;
                    videoCommentBeanList.clear();
                    runGetVideoCommentList();
                } else {
                    loadingDialog.dismiss();
                    DialogUtils.showAlertDialog(VideoShowPhotoInfoActivity.this,
                            submitVideoCommentResponse.resultText);
                }

            }

            @Override
            public void dealWithError(String address, String error) {
                loadingDialog.dismiss();
                DialogUtils.showAlertDialog(VideoShowPhotoInfoActivity.this, error);
            }
        });
    }

    //回复评论
    public void runReplyVideoComment() {
        loadingDialog.show();
        ReplyVideoCommentProtocol replyVideoCommentProtocol = new ReplyVideoCommentProtocol();
        ReplyVideoCommentRequest replyVideoCommentRequest = new ReplyVideoCommentRequest();
        url = replyVideoCommentProtocol.getApiFun();
        replyVideoCommentRequest.map.put("user_id", SharedPrefrenceUtils.getString(VideoShowPhotoInfoActivity.this, "user_id"));
        replyVideoCommentRequest.map.put("video_id", String.valueOf(video_id));
        replyVideoCommentRequest.map.put("content", content);
        replyVideoCommentRequest.map.put("comment_id", comment_id);
        replyVideoCommentRequest.map.put("parent_id", parent_id);

        MyVolley.uploadNoFile(MyVolley.POST, url, replyVideoCommentRequest.map, new MyVolley.VolleyCallback() {
            @Override
            public void dealWithJson(String address, String json) {
                LogUtils.e("json:" + json);
                Gson gson = new Gson();
                ReplyInformationCommentResponse replyInformationCommentResponse = gson.fromJson(json, ReplyInformationCommentResponse.class);
                if (replyInformationCommentResponse.code.equals("0")) {
                    et_comment.setText("");
                    loadingDialog.dismiss();
                    rl_comment.setVisibility(View.GONE);
                    isReply = false;
                    pageNo = 1;
                    videoCommentBeanList.clear();
                    runGetVideoCommentList();
                } else {
                    loadingDialog.dismiss();
                    DialogUtils.showAlertDialog(VideoShowPhotoInfoActivity.this,
                            replyInformationCommentResponse.resultText);
                }


            }

            @Override
            public void dealWithError(String address, String error) {
                loadingDialog.dismiss();
                DialogUtils.showAlertDialog(VideoShowPhotoInfoActivity.this, error);
            }
        });
    }

    //分享
    public void runVideoPlayOrForwardCnt() {
        loadingDialog.show();
        AddVideoPlayOrForwardCntProtocol addVideoPlayOrForwardCntProtocol = new AddVideoPlayOrForwardCntProtocol();
        AddVideoPlayOrForwardCntRequest addVideoPlayOrForwardCntRequest = new AddVideoPlayOrForwardCntRequest();
        url = addVideoPlayOrForwardCntProtocol.getApiFun();
        addVideoPlayOrForwardCntRequest.map.put("video_id", String.valueOf(video_id));
        addVideoPlayOrForwardCntRequest.map.put("type", String.valueOf(1));

        MyVolley.uploadNoFile(MyVolley.POST, url, addVideoPlayOrForwardCntRequest.map, new MyVolley.VolleyCallback() {
            @Override
            public void dealWithJson(String address, String json) {
                LogUtils.e("json:" + json);
                Gson gson = new Gson();
                AddVideoPlayOrForwardCntResponse addPostOrInformationForwardCntResponse = gson.fromJson(json, AddVideoPlayOrForwardCntResponse.class);
                if (addPostOrInformationForwardCntResponse.code.equals("0")) {
                    loadingDialog.dismiss();
                    user_share_video.setText(String.valueOf(videoInfoBean.getForward_cnt() + 1));
                } else {
                    loadingDialog.dismiss();
                    DialogUtils.showAlertDialog(VideoShowPhotoInfoActivity.this,
                            addPostOrInformationForwardCntResponse.resultText);
                }


            }

            @Override
            public void dealWithError(String address, String error) {
                loadingDialog.dismiss();
                DialogUtils.showAlertDialog(VideoShowPhotoInfoActivity.this, error);
            }
        });
    }
}
