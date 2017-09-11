package com.tesu.manicurehouse.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Message;
import android.text.Html;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.GridView;
import android.widget.ImageView;
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
import com.tesu.manicurehouse.adapter.DetailCommentsAdapter;
import com.tesu.manicurehouse.adapter.ShareAdapter;
import com.tesu.manicurehouse.base.BaseActivity;
import com.tesu.manicurehouse.base.MyVolley;
import com.tesu.manicurehouse.bean.ShareDateBean;
import com.tesu.manicurehouse.bean.ShareInfo;
import com.tesu.manicurehouse.protocol.AddOrCancelLikedPostOrInformationProtocol;
import com.tesu.manicurehouse.protocol.AddPostOrInformationForwardCntProtocol;
import com.tesu.manicurehouse.protocol.GetInformationCommentListProtocol;
import com.tesu.manicurehouse.protocol.GetInformationDescProtocol;
import com.tesu.manicurehouse.protocol.ReplyInformationCommentProtocol;
import com.tesu.manicurehouse.protocol.SubmitInformationCommentProtocol;
import com.tesu.manicurehouse.request.AddOrCancelLikedPostOrInformationRequest;
import com.tesu.manicurehouse.request.AddPostOrInformationForwardCntRequest;
import com.tesu.manicurehouse.request.GetInformationCommentListRequest;
import com.tesu.manicurehouse.request.GetInformationDescRequest;
import com.tesu.manicurehouse.request.ReplyInformationCommentRequest;
import com.tesu.manicurehouse.request.SubmitInformationCommentRequest;
import com.tesu.manicurehouse.response.AddOrCancelLikedPostOrInformationResponse;
import com.tesu.manicurehouse.response.AddPostOrInformationForwardCntResponse;
import com.tesu.manicurehouse.response.GetInformationCommentListResponse;
import com.tesu.manicurehouse.response.GetInformationDescResponse;
import com.tesu.manicurehouse.response.ReplyInformationCommentResponse;
import com.tesu.manicurehouse.response.SubmitInformationCommentResponse;
import com.tesu.manicurehouse.support.PercentLinearLayout;
import com.tesu.manicurehouse.support.PercentRelativeLayout;
import com.tesu.manicurehouse.util.Constants;
import com.tesu.manicurehouse.util.DialogUtils;
import com.tesu.manicurehouse.util.LogUtils;
import com.tesu.manicurehouse.util.PictureOption;
import com.tesu.manicurehouse.util.SharedPrefrenceUtils;
import com.tesu.manicurehouse.util.StringUtils;
import com.tesu.manicurehouse.util.UIUtils;
import com.tesu.manicurehouse.widget.CustomExpandableListView;
import com.tesu.manicurehouse.widget.XExpandableListView;

import java.net.URL;
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

/**
 * 类描述：
 * 创建人：许明达
 * 创建时间：2016/8/30 10:40
 * 美甲资讯详情页面
 */
public class ManicureInformationDetailsActivity extends BaseActivity implements View.OnClickListener ,PlatformActionListener {

    private ImageView iv_top_back;
    private View rootView;
    private CustomExpandableListView lv_details_comment;
    private DetailCommentsAdapter detailCommentsAdapter;
    private PullToRefreshScrollView sv;
    private WebView wv_content;
    private ImageView iv_info_image;
    private String url;
    private Dialog loadingDialog;
    private AddOrCancelLikedPostOrInformationResponse addOrCancelLikedPostOrInformationResponse;
    private GetInformationDescResponse getInformationDescResponse;
    private GetInformationCommentListResponse getInformationCommentListResponse;
    private SubmitInformationCommentResponse submitInformationCommentResponse;
    //帖子id/资讯id
    private String id;
    //1增加，2取消
    private int operation;
    //1帖子，2资讯
    private int type = 2;
    private PercentRelativeLayout prl_like;
    private PercentRelativeLayout prl_comment;
    private PercentRelativeLayout prl_share;
    private String information_id;
    private TextView tv_title_name;
    private TextView tv_like_num;
    private TextView tv_share_num;
    private TextView tv_comment_num;
    private ImageView iv_like;
    private RelativeLayout rl_comment;
    private EditText et_comment;
    //评论Id
    private String comment_id;
    //父id(如果是回复评论的就填0，如果回复的是回复的，就填reply_id跟帖id)
    private String parent_id;
    //评论内容
    private String content;
    //页数
    private int pageNo=1;
    //个数
    private int pageSize=10;
    /**评论集合*/
    private List<GetInformationCommentListResponse.InformationCommentBean> informationCommentBeanList=new ArrayList<GetInformationCommentListResponse.InformationCommentBean>();
    private boolean isReply=false;
    //css显示图片样式
    private String CSS_STYPE = "<head><style>img{max-width:340px !important;}</style></head>";
    //是否在刷新
    private boolean isRefresh=false;
    private GridView gv_share;
    private TextView tv_cancle;
    private PercentLinearLayout rl_share;
    private ShareAdapter sadapter;
    private String title;
    private TextView tv_title;
    @Override
    protected View initView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        rootView = View.inflate(this, R.layout.activity_manicure_info_details, null);
        setContentView(rootView);
        sv = (PullToRefreshScrollView) rootView.findViewById(R.id.sv);
        tv_title=(TextView)rootView.findViewById(R.id.tv_title);
        iv_info_image = (ImageView) rootView.findViewById(R.id.iv_info_image);
        tv_title_name = (TextView) rootView.findViewById(R.id.tv_title_name);
        wv_content = (WebView) rootView.findViewById(R.id.wv_content);
        tv_like_num = (TextView) rootView.findViewById(R.id.tv_like_num);
        tv_comment_num = (TextView) rootView.findViewById(R.id.tv_comment_num);
        tv_share_num = (TextView) rootView.findViewById(R.id.tv_share_num);
        iv_top_back = (ImageView) rootView.findViewById(R.id.iv_top_back);
        iv_like = (ImageView) rootView.findViewById(R.id.iv_like);
        et_comment= (EditText) rootView.findViewById(R.id.et_comment);
        prl_like = (PercentRelativeLayout) rootView.findViewById(R.id.prl_like);
        prl_comment = (PercentRelativeLayout) rootView.findViewById(R.id.prl_comment);
        prl_share= (PercentRelativeLayout) rootView.findViewById(R.id.prl_share);
        lv_details_comment = (CustomExpandableListView) rootView.findViewById(R.id.lv_details_comment);
        rl_comment = (RelativeLayout) rootView.findViewById(R.id.rl_comment);
        gv_share=(GridView)rootView.findViewById(R.id.gv_share);
        tv_cancle=(TextView)rootView.findViewById(R.id.tv_cancle);
        rl_share=(PercentLinearLayout)rootView.findViewById(R.id.rl_share);
        initData();
        return null;
    }

    //这里面的resource就是fromhtml函数的第一个参数里面的含有的url
    Html.ImageGetter imgGetter = new Html.ImageGetter() {
        public Drawable getDrawable(String source) {
            Drawable drawable = null;
            URL url;
            try {
                url = new URL(source);
                drawable = Drawable.createFromStream(url.openStream(), ""); // 获取网路图片
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
                    drawable.getIntrinsicHeight());
            return drawable;
        }
    };

    public void initData() {
        if (!ImageLoader.getInstance().isInited()) {
            ImageLoader.getInstance()
                    .init(ImageLoaderConfiguration.createDefault(UIUtils
                            .getContext()));
        }
        loadingDialog = DialogUtils.createLoadDialog(ManicureInformationDetailsActivity.this, true);
        Intent intent = getIntent();
        information_id = intent.getStringExtra("information_id");
        title = intent.getStringExtra("title");
        tv_title.setText(title);
        runGetInformationDesc();
        tv_cancle.setOnClickListener(this);
        prl_share.setOnClickListener(this);
        prl_comment.setOnClickListener(this);
        iv_top_back.setOnClickListener(this);
        prl_like.setOnClickListener(this);
        //搜索输入监听
        et_comment.setOnKeyListener(new View.OnKeyListener() {
            Message msg;

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (KeyEvent.KEYCODE_ENTER == keyCode && event.getAction() == KeyEvent.ACTION_DOWN) {
                    content = et_comment.getText().toString();
                    loadingDialog.show();
                    if(!isReply){
                        runSubmitInformationComment();
                    }
                    else{
                        runReplyInformationComment();
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
                imm.hideSoftInputFromWindow(ManicureInformationDetailsActivity.this.getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
                rl_comment.setVisibility(View.GONE);
                et_comment.clearFocus();
                isReply = false;
            }
        });
        sv.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ScrollView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {

                if(!isRefresh){
                    informationCommentBeanList.clear();
                    pageNo=1;
                    isRefresh=true;
                    runGetInformationDesc();
                }
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

        ShareInfo share4= new ShareInfo();
        share4.setShare_photo(R.drawable.icon_share_circle);
        share4.setShare_photo_selected(R.drawable.icon_share_circle);
        share4.setShare_name(WechatMoments.NAME);
        slist.add(share4);

        ShareInfo share5= new ShareInfo();
        share5.setShare_photo(R.drawable.icon_share_wechat);
        share5.setShare_photo_selected(R.drawable.icon_share_wechat);
        share5.setShare_name(Wechat.NAME);
        slist.add(share5);
        sadapter = new ShareAdapter(slist,rl_share,this,true,this);
        gv_share.setAdapter(sadapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_cancle:
                rl_share.setVisibility(View.INVISIBLE);
                break;
            case R.id.prl_share:{
                if(TextUtils.isEmpty(SharedPrefrenceUtils.getString(ManicureInformationDetailsActivity.this, "user_id"))){
                    DialogUtils.showAlertDialog(ManicureInformationDetailsActivity.this,
                            "请先登陆！");
                }
                else{
                    rl_share.setVisibility(View.VISIBLE);
                    // 初始化ShareSDK
                    ShareSDK.initSDK(UIUtils.getContext());
                }
                break;
            }
            case R.id.prl_comment: {
                if(TextUtils.isEmpty(SharedPrefrenceUtils.getString(ManicureInformationDetailsActivity.this, "user_id"))){
                    DialogUtils.showAlertDialog(ManicureInformationDetailsActivity.this,
                            "请先登陆！");
                }
                else {
                    rl_comment.setVisibility(View.VISIBLE);
                }
                break;
            }
            case R.id.iv_top_back: {
                finish();
                overridePendingTransition(R.anim.animprv_in, R.anim.animprv_out);
                break;
            }
            case R.id.prl_like: {
                if(TextUtils.isEmpty(SharedPrefrenceUtils.getString(ManicureInformationDetailsActivity.this, "user_id"))){
                    DialogUtils.showAlertDialog(ManicureInformationDetailsActivity.this,
                            "请先登陆！");
                }
                else {
                    if (getInformationDescResponse.data.is_liked == 1) {
//                        operation = 2;
                    } else {
                        operation = 1;
                        id = getInformationDescResponse.data.information_id;
                        runAddOrCancelLikedPostOrInformation();
                    }

                }
                break;
            }
        }
    }


    //点赞
    public void runAddOrCancelLikedPostOrInformation() {
        loadingDialog.show();
        AddOrCancelLikedPostOrInformationProtocol addOrCancelLikedPostOrInformationProtocol = new AddOrCancelLikedPostOrInformationProtocol();
        AddOrCancelLikedPostOrInformationRequest addOrCancelLikedPostOrInformationRequest = new AddOrCancelLikedPostOrInformationRequest();
        url = addOrCancelLikedPostOrInformationProtocol.getApiFun();
        addOrCancelLikedPostOrInformationRequest.map.put("user_id", SharedPrefrenceUtils.getString(ManicureInformationDetailsActivity.this, "user_id"));
        addOrCancelLikedPostOrInformationRequest.map.put("id", id);
        addOrCancelLikedPostOrInformationRequest.map.put("operation", String.valueOf(operation));
        addOrCancelLikedPostOrInformationRequest.map.put("type", String.valueOf(type));
        LogUtils.e("map:" + addOrCancelLikedPostOrInformationRequest.map.toString());

        MyVolley.uploadNoFile(MyVolley.POST, url, addOrCancelLikedPostOrInformationRequest.map, new MyVolley.VolleyCallback() {
            @Override
            public void dealWithJson(String address, String json) {
                LogUtils.e("json:" + json);
                Gson gson = new Gson();
                addOrCancelLikedPostOrInformationResponse = gson.fromJson(json, AddOrCancelLikedPostOrInformationResponse.class);
                if (addOrCancelLikedPostOrInformationResponse.code.equals("0")) {
                    loadingDialog.dismiss();
                    if (operation == 1) {
                        getInformationDescResponse.data.is_liked = 1;
                        iv_like.setImageDrawable(getResources().getDrawable(R.mipmap.bt_love_selectable));
                        tv_like_num.setText("" + (Integer.parseInt(tv_like_num.getText().toString()) + 1));
                    }
//                    else {
//                        getInformationDescResponse.data.is_liked = 0;
//                        iv_like.setImageDrawable(getResources().getDrawable(R.mipmap.bt_love_noselectable));
//                        tv_like_num.setText("" + (Integer.parseInt(tv_like_num.getText().toString()) - 1));
//                    }
                    Intent intent=getIntent();
                    setResult(1, intent);
                } else {
                    loadingDialog.dismiss();
                    DialogUtils.showAlertDialog(ManicureInformationDetailsActivity.this,
                            addOrCancelLikedPostOrInformationResponse.resultText);
                }


            }

            @Override
            public void dealWithError(String address, String error) {
                loadingDialog.dismiss();
                DialogUtils.showAlertDialog(ManicureInformationDetailsActivity.this, error);
            }
        });
    }

    public void runGetInformationDesc() {
        loadingDialog.show();
        GetInformationDescProtocol getInformationDescProtocol = new GetInformationDescProtocol();
        GetInformationDescRequest getInformationDescRequest = new GetInformationDescRequest();
        url = getInformationDescProtocol.getApiFun();
        getInformationDescRequest.map.put("user_id", SharedPrefrenceUtils.getString(ManicureInformationDetailsActivity.this, "user_id"));
        getInformationDescRequest.map.put("information_id", information_id);

        MyVolley.uploadNoFile(MyVolley.POST, url, getInformationDescRequest.map, new MyVolley.VolleyCallback() {
            @Override
            public void dealWithJson(String address, String json) {
                LogUtils.e("json:" + json);
                Gson gson = new Gson();
                getInformationDescResponse = gson.fromJson(json, GetInformationDescResponse.class);
                if (getInformationDescResponse.code.equals("0")) {
                    setData(getInformationDescResponse.data);
                } else {
                    DialogUtils.showAlertDialog(ManicureInformationDetailsActivity.this,
                            getInformationDescResponse.resultText);
                }
                if(isRefresh){
                    isRefresh=false;
                    sv.onRefreshComplete();
                }
                loadingDialog.dismiss();
            }

            @Override
            public void dealWithError(String address, String error) {
                loadingDialog.dismiss();
                if(isRefresh){
                    isRefresh=false;
                    sv.onRefreshComplete();
                }
                DialogUtils.showAlertDialog(ManicureInformationDetailsActivity.this, error);
            }
        });
    }

    public void runGetInformationCommentList() {
        GetInformationCommentListProtocol getInformationCommentListProtocol = new GetInformationCommentListProtocol();
        GetInformationCommentListRequest getInformationCommentListRequest = new GetInformationCommentListRequest();
        url = getInformationCommentListProtocol.getApiFun();
        getInformationCommentListRequest.map.put("user_id", SharedPrefrenceUtils.getString(ManicureInformationDetailsActivity.this, "user_id"));
        getInformationCommentListRequest.map.put("information_id", information_id);
        getInformationCommentListRequest.map.put("pageNo",String.valueOf(pageNo));
        getInformationCommentListRequest.map.put("pageSize", String.valueOf(pageSize));

        MyVolley.uploadNoFile(MyVolley.POST, url, getInformationCommentListRequest.map, new MyVolley.VolleyCallback() {
            @Override
            public void dealWithJson(String address, String json) {
                LogUtils.e("json:" + json);
                Gson gson = new Gson();
                getInformationCommentListResponse = gson.fromJson(json, GetInformationCommentListResponse.class);
                if (getInformationCommentListResponse.code.equals("0")) {
                    loadingDialog.dismiss();
                    if(getInformationCommentListResponse.dataList.size()>0) {
                        if(pageNo==1){
                            informationCommentBeanList.clear();
                        }
                        informationCommentBeanList.addAll(getInformationCommentListResponse.dataList);
                        setComment(informationCommentBeanList);
                    }
                } else {
                    loadingDialog.dismiss();
                    DialogUtils.showAlertDialog(ManicureInformationDetailsActivity.this,
                            getInformationCommentListResponse.resultText);
                }
            }

            @Override
            public void dealWithError(String address, String error) {
                loadingDialog.dismiss();
                DialogUtils.showAlertDialog(ManicureInformationDetailsActivity.this, error);
            }
        });
    }

    public void runSubmitInformationComment() {
        loadingDialog.show();
        SubmitInformationCommentProtocol submitInformationCommentProtocol = new SubmitInformationCommentProtocol();
        SubmitInformationCommentRequest submitInformationCommentRequest = new SubmitInformationCommentRequest();
        url = submitInformationCommentProtocol.getApiFun();
        submitInformationCommentRequest.map.put("user_id", SharedPrefrenceUtils.getString(ManicureInformationDetailsActivity.this, "user_id"));
        submitInformationCommentRequest.map.put("information_id", information_id);
        submitInformationCommentRequest.map.put("content", content);

        MyVolley.uploadNoFile(MyVolley.POST, url, submitInformationCommentRequest.map, new MyVolley.VolleyCallback() {
            @Override
            public void dealWithJson(String address, String json) {
                LogUtils.e("json:" + json);
                Gson gson = new Gson();
                submitInformationCommentResponse = gson.fromJson(json, SubmitInformationCommentResponse.class);
                if (submitInformationCommentResponse.code.equals("0")) {
                    et_comment.setText("");
                    loadingDialog.dismiss();
                    rl_comment.setVisibility(View.GONE);
                    tv_comment_num.setText("" + (Integer.parseInt(tv_comment_num.getText().toString()) + 1));
                    Intent intent=getIntent();
                    setResult(1,intent);
                    runGetInformationCommentList();
                } else {
                    loadingDialog.dismiss();
                    DialogUtils.showAlertDialog(ManicureInformationDetailsActivity.this,
                            submitInformationCommentResponse.resultText);
                }


            }

            @Override
            public void dealWithError(String address, String error) {
                loadingDialog.dismiss();
                DialogUtils.showAlertDialog(ManicureInformationDetailsActivity.this, error);
            }
        });
    }

    public void runReplyInformationComment() {
        loadingDialog.show();
        ReplyInformationCommentProtocol replyInformationCommentProtocol = new ReplyInformationCommentProtocol();
        ReplyInformationCommentRequest replyInformationCommentRequest = new ReplyInformationCommentRequest();
        url = replyInformationCommentProtocol.getApiFun();
        replyInformationCommentRequest.map.put("user_id", SharedPrefrenceUtils.getString(ManicureInformationDetailsActivity.this, "user_id"));
        replyInformationCommentRequest.map.put("information_id", information_id);
        replyInformationCommentRequest.map.put("content",content);
        replyInformationCommentRequest.map.put("comment_id",comment_id);
        replyInformationCommentRequest.map.put("parent_id", parent_id);

        MyVolley.uploadNoFile(MyVolley.POST, url, replyInformationCommentRequest.map, new MyVolley.VolleyCallback() {
            @Override
            public void dealWithJson(String address, String json) {
                LogUtils.e("json:" + json);
                Gson gson = new Gson();
                ReplyInformationCommentResponse replyInformationCommentResponse = gson.fromJson(json, ReplyInformationCommentResponse.class);
                if (replyInformationCommentResponse.code.equals("0")) {
                    et_comment.setText("");
                    loadingDialog.dismiss();
                    rl_comment.setVisibility(View.GONE);
                    isReply=false;
                    pageNo=1;
                    informationCommentBeanList.clear();
                    runGetInformationCommentList();
                } else {
                    loadingDialog.dismiss();
                    DialogUtils.showAlertDialog(ManicureInformationDetailsActivity.this,
                            replyInformationCommentResponse.resultText);
                }


            }

            @Override
            public void dealWithError(String address, String error) {
                loadingDialog.dismiss();
                DialogUtils.showAlertDialog(ManicureInformationDetailsActivity.this, error);
            }
        });
    }
    public void setData(GetInformationDescResponse.InformationDescBean data) {

        String shareUrl= Constants.SHARE_URL+"share/consult_share.shtml?share_user_id="+SharedPrefrenceUtils.getString(ManicureInformationDetailsActivity.this,"user_id")+"&information_id="+data.information_id;
        ShareDateBean shareDateBean=new ShareDateBean();
        shareDateBean.setContent("我在美甲屋APP发现了一个不错的帖子，快来看看吧！");
        shareDateBean.setImageurl(data.pic);
        shareDateBean.setTitle(data.title);
        shareDateBean.setUrl(shareUrl);
        sadapter.setShareDateBean(shareDateBean);

        tv_title_name.setText(data.title);
        wv_content.loadDataWithBaseURL(null, CSS_STYPE + data.content, "text/html", "utf-8", null);
        ImageLoader.getInstance().displayImage(data.pic, iv_info_image, PictureOption.getSimpleOptions());
        tv_comment_num.setText("" + data.comment_cnt);
        tv_share_num.setText("" + data.forward_cnt);
        tv_like_num.setText("" + data.liked_cnt);
        if (data.is_liked == 1) {
            iv_like.setImageDrawable(getResources().getDrawable(R.mipmap.bt_love_selectable));
        } else {
            iv_like.setImageDrawable(getResources().getDrawable(R.mipmap.bt_love_noselectable));
        }
        runGetInformationCommentList();
    }

    //初始话评论数据
    public void setComment(final List<GetInformationCommentListResponse.InformationCommentBean> dataList){
        if (detailCommentsAdapter == null) {
            detailCommentsAdapter = new DetailCommentsAdapter(ManicureInformationDetailsActivity.this, dataList,loadingDialog);
        }
        else{
            detailCommentsAdapter.setDataList(dataList);
            detailCommentsAdapter.notifyDataSetChanged();
        }
        lv_details_comment.setAdapter(detailCommentsAdapter);
        lv_details_comment.expandGroup(0);
        for (int i = 0; i < dataList.size(); i++) {
            lv_details_comment.expandGroup(i);
        }

        //禁止展开
        lv_details_comment.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

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
        lv_details_comment.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
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
    //分享
    public void runAddPostOrInformationForwardCnt() {
        loadingDialog.show();
        AddPostOrInformationForwardCntProtocol addPostOrInformationForwardCntProtocol = new AddPostOrInformationForwardCntProtocol();
        AddPostOrInformationForwardCntRequest addPostOrInformationForwardCntRequest = new AddPostOrInformationForwardCntRequest();
        url = addPostOrInformationForwardCntProtocol.getApiFun();
        addPostOrInformationForwardCntRequest.map.put("id", information_id);
        addPostOrInformationForwardCntRequest.map.put("type", String.valueOf(2));

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
                    finish();
                    overridePendingTransition(R.anim.animprv_in, R.anim.animprv_out);
                } else {
                    loadingDialog.dismiss();
                    DialogUtils.showAlertDialog(ManicureInformationDetailsActivity.this,
                            addPostOrInformationForwardCntResponse.resultText);
                }


            }

            @Override
            public void dealWithError(String address, String error) {
                loadingDialog.dismiss();
                DialogUtils.showAlertDialog(ManicureInformationDetailsActivity.this, error);
            }
        });
    }
    @Override
    public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
        Toast.makeText(ManicureInformationDetailsActivity.this, "分享成功!",
                Toast.LENGTH_SHORT).show();
        runAddPostOrInformationForwardCnt();
    }

    @Override
    public void onError(Platform platform, int i, Throwable throwable) {
        Toast.makeText(ManicureInformationDetailsActivity.this, "分享失败!"+throwable,
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onCancel(Platform platform, int i) {
        Toast.makeText(ManicureInformationDetailsActivity.this, "分享取消!",
                Toast.LENGTH_SHORT).show();
    }
}
