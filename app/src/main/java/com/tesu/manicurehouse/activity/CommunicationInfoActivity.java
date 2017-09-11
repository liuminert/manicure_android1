package com.tesu.manicurehouse.activity;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.tesu.manicurehouse.R;
import com.tesu.manicurehouse.adapter.CommunicationCommentsAdapter;
import com.tesu.manicurehouse.adapter.DetailCommentsAdapter;
import com.tesu.manicurehouse.adapter.PersonalCommunicationAdapter;
import com.tesu.manicurehouse.adapter.PostImageAdapter;
import com.tesu.manicurehouse.adapter.ShareAdapter;
import com.tesu.manicurehouse.base.BaseActivity;
import com.tesu.manicurehouse.base.MyVolley;
import com.tesu.manicurehouse.bean.ShareDateBean;
import com.tesu.manicurehouse.bean.ShareInfo;
import com.tesu.manicurehouse.protocol.AddOrCancelLikedPostOrInformationProtocol;
import com.tesu.manicurehouse.protocol.AddPostOrInformationForwardCntProtocol;
import com.tesu.manicurehouse.protocol.GetInformationCommentListProtocol;
import com.tesu.manicurehouse.protocol.GetPostCommentListProtocol;
import com.tesu.manicurehouse.protocol.GetPostDescProtocol;
import com.tesu.manicurehouse.protocol.GetPostListProtocol;
import com.tesu.manicurehouse.protocol.ReplyInformationCommentProtocol;
import com.tesu.manicurehouse.protocol.ReplyPostCommentProtocol;
import com.tesu.manicurehouse.protocol.SubmitInformationCommentProtocol;
import com.tesu.manicurehouse.protocol.SubmitPostCommentProtocol;
import com.tesu.manicurehouse.request.AddOrCancelLikedPostOrInformationRequest;
import com.tesu.manicurehouse.request.AddPostOrInformationForwardCntRequest;
import com.tesu.manicurehouse.request.GetInformationCommentListRequest;
import com.tesu.manicurehouse.request.GetPostCommentListRequest;
import com.tesu.manicurehouse.request.GetPostDescRequest;
import com.tesu.manicurehouse.request.GetPostListRequest;
import com.tesu.manicurehouse.request.ReplyInformationCommentRequest;
import com.tesu.manicurehouse.request.ReplyPostCommentRequest;
import com.tesu.manicurehouse.request.SubmitInformationCommentRequest;
import com.tesu.manicurehouse.request.SubmitPostCommentRequest;
import com.tesu.manicurehouse.response.AddOrCancelLikedPostOrInformationResponse;
import com.tesu.manicurehouse.response.AddPostOrInformationForwardCntResponse;
import com.tesu.manicurehouse.response.GetInformationCommentListResponse;
import com.tesu.manicurehouse.response.GetPostCommentListResponse;
import com.tesu.manicurehouse.response.GetPostDescResponse;
import com.tesu.manicurehouse.response.GetPostListResponse;
import com.tesu.manicurehouse.response.ReplyInformationCommentResponse;
import com.tesu.manicurehouse.response.ReplyPostCommentResponse;
import com.tesu.manicurehouse.response.SubmitInformationCommentResponse;
import com.tesu.manicurehouse.response.SubmitPostCommentResponse;
import com.tesu.manicurehouse.support.PercentLinearLayout;
import com.tesu.manicurehouse.support.PercentRelativeLayout;
import com.tesu.manicurehouse.util.BitmapBlurUtils;
import com.tesu.manicurehouse.util.CircleImageView;
import com.tesu.manicurehouse.util.Constants;
import com.tesu.manicurehouse.util.DialogUtils;
import com.tesu.manicurehouse.util.LogUtils;
import com.tesu.manicurehouse.util.LoginUtils;
import com.tesu.manicurehouse.util.PictureOption;
import com.tesu.manicurehouse.util.SharedPrefrenceUtils;
import com.tesu.manicurehouse.util.StringUtils;
import com.tesu.manicurehouse.util.UIUtils;
import com.tesu.manicurehouse.widget.CustomExpandableListView;
import com.tesu.manicurehouse.widget.MyListView;
import com.tesu.manicurehouse.widget.XExpandableListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;
import io.vov.vitamio.utils.Log;

/**
 * 类描述：
 * 创建人：许明达
 * 创建时间：2016/9/5 9:40
 * 交流详情页面
 */
public class CommunicationInfoActivity extends BaseActivity implements View.OnClickListener, PlatformActionListener {

    private ImageView iv_top_back;
    private View rootView;
    private CustomExpandableListView lv_communucation_comment;
    private CommunicationCommentsAdapter communicationCommentsAdapter;
    private ScrollView sv1;
    private PullToRefreshScrollView sv;
    private PercentRelativeLayout rl_comment;
    private PercentRelativeLayout prl_comment;
    private int post_id;
    //1增加，2取消
    private int operation;
    //评论内容
    private String content;
    //评论Id
    private String comment_id;
    //父id(如果是回复评论的就填0，如果回复的是回复的，就填reply_id跟帖id)
    private String parent_id;
    private String url;
    private Dialog loadingDialog;
    private GetPostDescResponse getPostDescResponse;
    private GetPostCommentListResponse getPostCommentListResponse;
    private TextView tv_add_time;
    private TextView tv_like_num;
    private TextView tv_share_num;
    private TextView tv_comment_num;
    private TextView tv_content;
    private ShareDateBean shareDateBean;
    //    private ImageView iv_info_image;
    private EditText et_comment;
    private ShareAdapter sadapter;
    private GridView gv_share;
    private TextView tv_cancle;
    private PercentLinearLayout rl_share;
    private ImageView iv_like;
    /**
     * 评论集合
     */
    private List<GetPostCommentListResponse.InformationCommentBean> postCommentBeanList = new ArrayList<GetPostCommentListResponse.InformationCommentBean>();
    private boolean isReply = false;
    private int pageNo = 1;
    private PercentRelativeLayout prl_like;
    private PercentRelativeLayout prl_share;
    private TextView tv_comment_title;
    private List<GetPostCommentListResponse.InformationCommentBean> informationCommentBeanList;
    private boolean isRefresh = false;
    private TextView tv_post_name;
    private TextView tv_user_name;
    private ImageView iv_image;
//    private LinearLayout img_content;
    private int screenWidth;
    private boolean UI_Loaded;
    private boolean waitForLoad = true;
    private Dialog mDialog;
    private MyListView lv_image;
    @Override
    protected View initView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        rootView = View.inflate(this, R.layout.activity_communication_info, null);
        setContentView(rootView);
        prl_like = (PercentRelativeLayout) rootView.findViewById(R.id.prl_like);
        prl_share = (PercentRelativeLayout) rootView.findViewById(R.id.prl_share);
        iv_top_back = (ImageView) rootView.findViewById(R.id.iv_top_back);
        iv_image = (ImageView) rootView.findViewById(R.id.iv_image);
        tv_user_name = (TextView) rootView.findViewById(R.id.tv_user_name);
        sv = (PullToRefreshScrollView) rootView.findViewById(R.id.sv);
        sv1 = (ScrollView) rootView.findViewById(R.id.sv1);
        iv_top_back = (ImageView) rootView.findViewById(R.id.iv_top_back);
        prl_comment = (PercentRelativeLayout) rootView.findViewById(R.id.prl_comment);
        rl_comment = (PercentRelativeLayout) rootView.findViewById(R.id.rl_comment);
        lv_communucation_comment = (CustomExpandableListView) rootView.findViewById(R.id.lv_communucation_comment);
        tv_comment_title = (TextView) rootView.findViewById(R.id.tv_comment_title);
        tv_add_time = (TextView) rootView.findViewById(R.id.tv_add_time);
        tv_like_num = (TextView) rootView.findViewById(R.id.tv_like_num);
        tv_share_num = (TextView) rootView.findViewById(R.id.tv_share_num);
        tv_comment_num = (TextView) rootView.findViewById(R.id.tv_comment_num);
        tv_post_name = (TextView) rootView.findViewById(R.id.tv_post_name);
        tv_content = (TextView) rootView.findViewById(R.id.tv_content);
//        iv_info_image = (ImageView) rootView.findViewById(R.id.iv_info_image);
        iv_like = (ImageView) rootView.findViewById(R.id.iv_like);
        et_comment = (EditText) rootView.findViewById(R.id.et_comment);
        gv_share = (GridView) rootView.findViewById(R.id.gv_share);
        tv_cancle = (TextView) rootView.findViewById(R.id.tv_cancle);
        rl_share = (PercentLinearLayout) rootView.findViewById(R.id.rl_share);
//        img_content = (LinearLayout) rootView.findViewById(R.id.img_content);
        lv_image= (MyListView) rootView.findViewById(R.id.lv_image);
        initData();
        return null;
    }


    public void initData() {

        informationCommentBeanList = new ArrayList<GetPostCommentListResponse.InformationCommentBean>();
        Intent intent = getIntent();
        post_id = intent.getIntExtra("post_id", -1);
        if (!ImageLoader.getInstance().isInited()) {
            ImageLoader.getInstance()
                    .init(ImageLoaderConfiguration.createDefault(UIUtils
                            .getContext()));
        }
        loadingDialog = DialogUtils.createLoadDialog(CommunicationInfoActivity.this, true);
        runAsyncTask();
        prl_comment.setOnClickListener(this);
        iv_top_back.setOnClickListener(this);
        prl_like.setOnClickListener(this);
        prl_share.setOnClickListener(this);
        tv_cancle.setOnClickListener(this);
        sv.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ScrollView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {

                if (!isRefresh) {
                    pageNo = 1;
                    postCommentBeanList.clear();
                    isRefresh = true;
                    runAsyncTask();
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
                        if (!isReply) {
                            runSubmitPostComment();
                        } else {
                            runReplyPostComment();
                        }
                    } else {
                        mDialog = DialogUtils.showAlertDialog(CommunicationInfoActivity.this,
                                "请先登陆您的账号!", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        mDialog.dismiss();
                                        Intent intent = new Intent(CommunicationInfoActivity.this, LoginActivity.class);
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
                imm.hideSoftInputFromWindow(CommunicationInfoActivity.this.getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
                rl_comment.setVisibility(View.GONE);
                et_comment.clearFocus();
                isReply = false;
            }
        });

        //设置分享数据
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
        sadapter = new ShareAdapter(slist, rl_share, this, true,this);
        gv_share.setAdapter(sadapter);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_cancle:
                rl_share.setVisibility(View.INVISIBLE);
                break;
            case R.id.prl_share: {
                if (TextUtils.isEmpty(SharedPrefrenceUtils.getString(CommunicationInfoActivity.this, "user_id"))) {
                    mDialog = DialogUtils.showAlertDialog(CommunicationInfoActivity.this,
                            "请先登陆您的账号!", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mDialog.dismiss();
                                    Intent intent = new Intent(CommunicationInfoActivity.this, LoginActivity.class);
                                    intent.putExtra("type",3);
                                    UIUtils.startActivityNextAnim(intent);
                                }
                            });
                } else {
                    String shareUrl = Constants.SHARE_URL + "share/post_share.shtml?share_user_id=" + SharedPrefrenceUtils.getString(CommunicationInfoActivity.this, "user_id") + "&post_id=" + getPostDescResponse.data.post_id;
                    shareDateBean.setUrl(shareUrl);
                    rl_share.setVisibility(View.VISIBLE);
                    // 初始化ShareSDK
                    ShareSDK.initSDK(UIUtils.getContext());
                }
                break;
            }
            case R.id.prl_comment: {
                if (TextUtils.isEmpty(SharedPrefrenceUtils.getString(CommunicationInfoActivity.this, "user_id"))) {
                    mDialog = DialogUtils.showAlertDialog(CommunicationInfoActivity.this,
                            "请先登陆您的账号!", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mDialog.dismiss();
                                    Intent intent = new Intent(CommunicationInfoActivity.this, LoginActivity.class);
                                    intent.putExtra("type",3);
                                    UIUtils.startActivityNextAnim(intent);
                                }
                            });
                } else {
                    rl_comment.setVisibility(View.VISIBLE);
                    rl_comment.setClickable(true);
                }
                break;
            }
            case R.id.tv_roger:{
                Intent intent = getIntent();
                setResult(1, intent);
                finish();
                overridePendingTransition(R.anim.animprv_in, R.anim.animprv_out);
                break;
            }
            case R.id.iv_top_back: {
                finish();
                overridePendingTransition(R.anim.animprv_in, R.anim.animprv_out);
                break;
            }
            case R.id.prl_like: {
                if (TextUtils.isEmpty(SharedPrefrenceUtils.getString(CommunicationInfoActivity.this, "user_id"))) {
                    mDialog = DialogUtils.showAlertDialog(CommunicationInfoActivity.this,
                            "请先登陆您的账号!", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mDialog.dismiss();
                                    Intent intent = new Intent(CommunicationInfoActivity.this, LoginActivity.class);
                                    intent.putExtra("type",3);
                                    UIUtils.startActivityNextAnim(intent);
                                }
                            });
                } else {
                    if (getPostDescResponse.data.is_liked == 1) {
//                        operation = 2;
                    } else {
                        operation = 1;
                        runAddOrCancelLikedPostOrInformation();
                    }

                }
                break;
            }
        }
    }

    public void runAsyncTask() {
        loadingDialog.show();
        GetPostDescProtocol getPostDescProtocol = new GetPostDescProtocol();
        GetPostDescRequest getPostDescRequest = new GetPostDescRequest();
        url = getPostDescProtocol.getApiFun();
        getPostDescRequest.map.put("user_id", SharedPrefrenceUtils.getString(CommunicationInfoActivity.this, "user_id"));
        getPostDescRequest.map.put("post_id", String.valueOf(post_id));

        MyVolley.uploadNoFile(MyVolley.POST, url, getPostDescRequest.map, new MyVolley.VolleyCallback() {
            @Override
            public void dealWithJson(String address, String json) {
                LogUtils.e("json:" + json);
                if (isRefresh) {
                    isRefresh = false;
                    sv.onRefreshComplete();
                }
                Gson gson = new Gson();
                getPostDescResponse = gson.fromJson(json, GetPostDescResponse.class);
                if (getPostDescResponse.code.equals("0")) {
                    if(getPostDescResponse.data!=null){
                        setDate(getPostDescResponse.data);
                    }
                    else{
                        DialogUtils.showAlertDialog(CommunicationInfoActivity.this,
                                "帖子已经删除！",CommunicationInfoActivity.this);
                        loadingDialog.dismiss();
                    }
                } else {
                    DialogUtils.showAlertDialog(CommunicationInfoActivity.this,
                            getPostDescResponse.resultText);
                }
            }

            @Override
            public void dealWithError(String address, String error) {
                if (isRefresh) {
                    isRefresh = false;
                    sv.onRefreshComplete();
                }
                DialogUtils.showAlertDialog(CommunicationInfoActivity.this, error);
            }
        });
    }

    public void setDate(GetPostDescResponse.PostBean data) {
            String shareUrl = Constants.SHARE_URL + "share/post_share.shtml?share_user_id=" + SharedPrefrenceUtils.getString(CommunicationInfoActivity.this, "user_id") + "&post_id=" + getPostDescResponse.data.post_id;
            shareDateBean = new ShareDateBean();
            shareDateBean.setContent("我在美甲屋APP发现了一个不错的帖子，快来看看吧！");
            try{
                if (!getPostDescResponse.data.pics.equals("[]")&&!getPostDescResponse.data.pics.equals("[\n\n]")&&!TextUtils.isEmpty(getPostDescResponse.data.pics)) {
                    shareDateBean.setImageurl(StringUtils.getList(getPostDescResponse.data.pics).get(0));
                }
                if(!getPostDescResponse.data.pics.equals("[]")&&!TextUtils.isEmpty(getPostDescResponse.data.pics)) {
                    PostImageAdapter postImageAdapter = new PostImageAdapter(CommunicationInfoActivity.this, StringUtils.getList(getPostDescResponse.data.pics));
                    lv_image.setAdapter(postImageAdapter);
                }
            }catch (NullPointerException e){

            }

            shareDateBean.setTitle(getPostDescResponse.data.title);
            shareDateBean.setUrl(shareUrl);
            sadapter.setShareDateBean(shareDateBean);
            tv_content.setText(data.content);
            tv_post_name.setText(data.title);
            tv_user_name.setText(data.alias);
            tv_comment_num.setText("" + data.comment_cnt);
            tv_share_num.setText("" + data.forward_cnt);
            tv_like_num.setText("" + data.liked_cnt);
            ImageLoader.getInstance().displayImage(data.avatar, iv_image, PictureOption.getSimpleOptions());
            tv_add_time.setText(data.add_time.substring(0, data.add_time.length() - 8));
            if (data.is_liked == 1) {
                iv_like.setImageDrawable(getResources().getDrawable(R.mipmap.bt_love_selectable));
            } else {
                iv_like.setImageDrawable(getResources().getDrawable(R.mipmap.bt_love_noselectable));
            }
            runGetPostCommentList();
    }


    public void runGetPostCommentList() {
        GetPostCommentListProtocol getPostCommentListProtocol = new GetPostCommentListProtocol();
        GetPostCommentListRequest getPostCommentListRequest = new GetPostCommentListRequest();
        url = getPostCommentListProtocol.getApiFun();
        getPostCommentListRequest.map.put("user_id", SharedPrefrenceUtils.getString(CommunicationInfoActivity.this, "user_id"));
        getPostCommentListRequest.map.put("post_id", String.valueOf(post_id));
        getPostCommentListRequest.map.put("pageNo", String.valueOf(pageNo));
        getPostCommentListRequest.map.put("pageSize", "100");

        MyVolley.uploadNoFile(MyVolley.POST, url, getPostCommentListRequest.map, new MyVolley.VolleyCallback() {
            @Override
            public void dealWithJson(String address, String json) {
                Gson gson = new Gson();
                getPostCommentListResponse = gson.fromJson(json, GetPostCommentListResponse.class);
                if (getPostCommentListResponse.code.equals("0")) {
                    LogUtils.e("评论:"+json.toString());
                    loadingDialog.dismiss();
                    postCommentBeanList.clear();
                    if (getPostCommentListResponse.dataList.size() > 0) {
                        postCommentBeanList.addAll(getPostCommentListResponse.dataList);
                        setComment(postCommentBeanList);
                    }
//                    else{
//                        DialogUtils.showAlertDialog(CommunicationInfoActivity.this,
//                                "没有评论了！");
//                    }
                } else {
                    loadingDialog.dismiss();
                    DialogUtils.showAlertDialog(CommunicationInfoActivity.this,
                            getPostCommentListResponse.resultText);
                }
//                if (pageNo > 1) {
//                    lv_communucation_comment.stopLoadMore();
//                }

            }

            @Override
            public void dealWithError(String address, String error) {
//                if (pageNo > 1) {
//                    lv_communucation_comment.stopLoadMore();
//                }
                loadingDialog.dismiss();
                DialogUtils.showAlertDialog(CommunicationInfoActivity.this, error);
            }
        });
    }

    //初始话评论数据
    public void setComment(final List<GetPostCommentListResponse.InformationCommentBean> dataList) {
        tv_comment_title.setText("全部跟帖(" + getPostCommentListResponse.commentCnt + ")");
        if (communicationCommentsAdapter == null) {
            communicationCommentsAdapter = new CommunicationCommentsAdapter(CommunicationInfoActivity.this, dataList, loadingDialog);
        } else {
            communicationCommentsAdapter.setDataList(dataList);
            communicationCommentsAdapter.notifyDataSetChanged();
        }
        lv_communucation_comment.setAdapter(communicationCommentsAdapter);
        lv_communucation_comment.expandGroup(0);
        for (int i = 0; i < dataList.size(); i++) {
            lv_communucation_comment.expandGroup(i);
        }
//        //设置listview的滑动优先级
//        lv_communucation_comment.setOnTouchListener(new View.OnTouchListener() {
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

//        lv_communucation_comment.setPullLoadEnable(true);
        lv_communucation_comment.setGroupIndicator(null);
//        lv_communucation_comment.setXListViewListener(new XExpandableListView.IXListViewListener() {
//
//            @Override
//            public void onRefresh() {
////                //设置显示刷新的提示
////                initUserData1();     //测试下拉刷新的数据
////                mxListView.setAdapter(mAdapter);
////                mxListView.stopRefresh();
////                mxListView.setRefreshTime(System.currentTimeMillis());
////                new GetDataTask().execute();
//            }
//
//            @Override
//            public void onLoadMore() {
//                pageNo++;
//                runGetPostCommentList();
//            }
//        });
        //禁止展开
        lv_communucation_comment.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

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
        lv_communucation_comment.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
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

    //提交帖子评论
    public void runSubmitPostComment() {
        loadingDialog.show();
        SubmitPostCommentProtocol submitPostCommentProtocol = new SubmitPostCommentProtocol();
        SubmitPostCommentRequest submitPostCommentRequest = new SubmitPostCommentRequest();
        url = submitPostCommentProtocol.getApiFun();
        submitPostCommentRequest.map.put("user_id", SharedPrefrenceUtils.getString(CommunicationInfoActivity.this, "user_id"));
        submitPostCommentRequest.map.put("post_id", String.valueOf(post_id));
        submitPostCommentRequest.map.put("content", content);

        MyVolley.uploadNoFile(MyVolley.POST, url, submitPostCommentRequest.map, new MyVolley.VolleyCallback() {
            @Override
            public void dealWithJson(String address, String json) {
                LogUtils.e("json:" + json);
                Gson gson = new Gson();
                SubmitPostCommentResponse submitPostCommentResponse = gson.fromJson(json, SubmitPostCommentResponse.class);
                if (submitPostCommentResponse.code.equals("0")) {
                    et_comment.setText("");
                    loadingDialog.dismiss();
                    rl_comment.setVisibility(View.GONE);
                    tv_comment_num.setText("" + (Integer.parseInt(tv_comment_num.getText().toString()) + 1));
                    Intent intent = getIntent();
                    setResult(1, intent);
                    runGetPostCommentList();
                } else {
                    loadingDialog.dismiss();
                    DialogUtils.showAlertDialog(CommunicationInfoActivity.this,
                            submitPostCommentResponse.resultText);
                }


            }

            @Override
            public void dealWithError(String address, String error) {
                loadingDialog.dismiss();
                DialogUtils.showAlertDialog(CommunicationInfoActivity.this, error);
            }
        });
    }

    //回复评论
    public void runReplyPostComment() {
        loadingDialog.show();
        ReplyPostCommentProtocol replyPostCommentProtocol = new ReplyPostCommentProtocol();
        ReplyPostCommentRequest replyPostCommentRequest = new ReplyPostCommentRequest();
        url = replyPostCommentProtocol.getApiFun();
        replyPostCommentRequest.map.put("user_id", SharedPrefrenceUtils.getString(CommunicationInfoActivity.this, "user_id"));
        replyPostCommentRequest.map.put("post_id", String.valueOf(post_id));
        replyPostCommentRequest.map.put("content", content);
        replyPostCommentRequest.map.put("comment_id", comment_id);
        replyPostCommentRequest.map.put("parent_id", parent_id);

        MyVolley.uploadNoFile(MyVolley.POST, url, replyPostCommentRequest.map, new MyVolley.VolleyCallback() {
            @Override
            public void dealWithJson(String address, String json) {
                LogUtils.e("json:" + json);
                Gson gson = new Gson();
                ReplyPostCommentResponse replyPostCommentResponse = gson.fromJson(json, ReplyPostCommentResponse.class);
                if (replyPostCommentResponse.code.equals("0")) {
                    et_comment.setText("");
                    loadingDialog.dismiss();
                    rl_comment.setVisibility(View.GONE);
                    isReply = false;
                    pageNo = 1;
                    postCommentBeanList.clear();
                    runGetPostCommentList();
                } else {
                    loadingDialog.dismiss();
                    DialogUtils.showAlertDialog(CommunicationInfoActivity.this,
                            replyPostCommentResponse.resultText);
                }


            }

            @Override
            public void dealWithError(String address, String error) {
                loadingDialog.dismiss();
                DialogUtils.showAlertDialog(CommunicationInfoActivity.this, error);
            }
        });
    }

    //点赞
    public void runAddOrCancelLikedPostOrInformation() {
        loadingDialog.show();
        AddOrCancelLikedPostOrInformationProtocol addOrCancelLikedPostOrInformationProtocol = new AddOrCancelLikedPostOrInformationProtocol();
        AddOrCancelLikedPostOrInformationRequest addOrCancelLikedPostOrInformationRequest = new AddOrCancelLikedPostOrInformationRequest();
        url = addOrCancelLikedPostOrInformationProtocol.getApiFun();
        addOrCancelLikedPostOrInformationRequest.map.put("user_id", SharedPrefrenceUtils.getString(CommunicationInfoActivity.this, "user_id"));
        addOrCancelLikedPostOrInformationRequest.map.put("id", String.valueOf(post_id));
        addOrCancelLikedPostOrInformationRequest.map.put("operation", String.valueOf(operation));
        addOrCancelLikedPostOrInformationRequest.map.put("type", String.valueOf(1));
        LogUtils.e("map:" + addOrCancelLikedPostOrInformationRequest.map.toString());

        MyVolley.uploadNoFile(MyVolley.POST, url, addOrCancelLikedPostOrInformationRequest.map, new MyVolley.VolleyCallback() {
            @Override
            public void dealWithJson(String address, String json) {
                LogUtils.e("json:" + json);
                Gson gson = new Gson();
                AddOrCancelLikedPostOrInformationResponse addOrCancelLikedPostOrInformationResponse = gson.fromJson(json, AddOrCancelLikedPostOrInformationResponse.class);
                if (addOrCancelLikedPostOrInformationResponse.code.equals("0")) {
                    loadingDialog.dismiss();
                    if (operation == 1) {
                        getPostDescResponse.data.is_liked = 1;
                        iv_like.setImageDrawable(getResources().getDrawable(R.mipmap.bt_love_selectable));
                        tv_like_num.setText("" + (Integer.parseInt(tv_like_num.getText().toString()) + 1));
                    } else {
                        getPostDescResponse.data.is_liked = 0;
                        iv_like.setImageDrawable(getResources().getDrawable(R.mipmap.bt_love_noselectable));
                        tv_like_num.setText("" + (Integer.parseInt(tv_like_num.getText().toString()) - 1));
                    }
                    Intent intent = getIntent();
                    setResult(1, intent);
                } else {
                    loadingDialog.dismiss();
                    DialogUtils.showAlertDialog(CommunicationInfoActivity.this,
                            addOrCancelLikedPostOrInformationResponse.resultText);
                }


            }

            @Override
            public void dealWithError(String address, String error) {
                loadingDialog.dismiss();
                DialogUtils.showAlertDialog(CommunicationInfoActivity.this, error);
            }
        });
    }

    //分享
    public void runAddPostOrInformationForwardCnt() {
        loadingDialog.show();
        AddPostOrInformationForwardCntProtocol addPostOrInformationForwardCntProtocol = new AddPostOrInformationForwardCntProtocol();
        AddPostOrInformationForwardCntRequest addPostOrInformationForwardCntRequest = new AddPostOrInformationForwardCntRequest();
        url = addPostOrInformationForwardCntProtocol.getApiFun();
        addPostOrInformationForwardCntRequest.map.put("id", String.valueOf(post_id));
        addPostOrInformationForwardCntRequest.map.put("type", String.valueOf(1));

        MyVolley.uploadNoFile(MyVolley.POST, url, addPostOrInformationForwardCntRequest.map, new MyVolley.VolleyCallback() {
            @Override
            public void dealWithJson(String address, String json) {
                LogUtils.e("json:" + json);
                Gson gson = new Gson();
                AddPostOrInformationForwardCntResponse addPostOrInformationForwardCntResponse = gson.fromJson(json, AddPostOrInformationForwardCntResponse.class);
                if (addPostOrInformationForwardCntResponse.code.equals("0")) {
                    loadingDialog.dismiss();
                    Intent intent = getIntent();
                    setResult(1, intent);
                    tv_share_num.setText(""+(Integer.parseInt(tv_share_num.getText().toString())+1));
//                    finish();
//                    overridePendingTransition(R.anim.animprv_in, R.anim.animprv_out);
                } else {
                    loadingDialog.dismiss();
                    DialogUtils.showAlertDialog(CommunicationInfoActivity.this,
                            addPostOrInformationForwardCntResponse.resultText);
                }


            }

            @Override
            public void dealWithError(String address, String error) {
                loadingDialog.dismiss();
                DialogUtils.showAlertDialog(CommunicationInfoActivity.this, error);
            }
        });
    }

    @Override
    public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
        Toast.makeText(CommunicationInfoActivity.this, "分享成功!",
                Toast.LENGTH_SHORT).show();
        runAddPostOrInformationForwardCnt();
    }

    @Override
    public void onError(Platform platform, int i, Throwable throwable) {
        Toast.makeText(CommunicationInfoActivity.this, "分享失败!" + throwable,
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onCancel(Platform platform, int i) {
        Toast.makeText(CommunicationInfoActivity.this, "分享取消!",
                Toast.LENGTH_SHORT).show();
    }
}
