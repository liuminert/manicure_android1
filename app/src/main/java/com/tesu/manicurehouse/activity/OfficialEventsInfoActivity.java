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
import android.view.ViewGroup;
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
import com.tesu.manicurehouse.adapter.CommunicationCommentsAdapter;
import com.tesu.manicurehouse.adapter.DetailCommentsAdapter;
import com.tesu.manicurehouse.adapter.PostImageAdapter;
import com.tesu.manicurehouse.adapter.ShareAdapter;
import com.tesu.manicurehouse.base.BaseActivity;
import com.tesu.manicurehouse.base.MyVolley;
import com.tesu.manicurehouse.bean.GoodsAttrBean;
import com.tesu.manicurehouse.bean.ShareDateBean;
import com.tesu.manicurehouse.bean.ShareInfo;
import com.tesu.manicurehouse.protocol.AddOrCancelLikedPostOrInformationProtocol;
import com.tesu.manicurehouse.protocol.AddPostOrInformationForwardCntProtocol;
import com.tesu.manicurehouse.protocol.GetActDescByIdProtocol;
import com.tesu.manicurehouse.protocol.GetInformationCommentListProtocol;
import com.tesu.manicurehouse.protocol.GetInformationDescProtocol;
import com.tesu.manicurehouse.protocol.GetPostCommentListProtocol;
import com.tesu.manicurehouse.protocol.GetPostDescProtocol;
import com.tesu.manicurehouse.protocol.ReplyInformationCommentProtocol;
import com.tesu.manicurehouse.protocol.ReplyPostCommentProtocol;
import com.tesu.manicurehouse.protocol.SubmitInformationCommentProtocol;
import com.tesu.manicurehouse.protocol.SubmitPostCommentProtocol;
import com.tesu.manicurehouse.request.AddOrCancelLikedPostOrInformationRequest;
import com.tesu.manicurehouse.request.AddPostOrInformationForwardCntRequest;
import com.tesu.manicurehouse.request.GetActDescByIdRequest;
import com.tesu.manicurehouse.request.GetInformationCommentListRequest;
import com.tesu.manicurehouse.request.GetInformationDescRequest;
import com.tesu.manicurehouse.request.GetPostCommentListRequest;
import com.tesu.manicurehouse.request.GetPostDescRequest;
import com.tesu.manicurehouse.request.ReplyInformationCommentRequest;
import com.tesu.manicurehouse.request.ReplyPostCommentRequest;
import com.tesu.manicurehouse.request.SubmitInformationCommentRequest;
import com.tesu.manicurehouse.request.SubmitPostCommentRequest;
import com.tesu.manicurehouse.response.AddOrCancelLikedPostOrInformationResponse;
import com.tesu.manicurehouse.response.AddPostOrInformationForwardCntResponse;
import com.tesu.manicurehouse.response.GetActDescByIdResponse;
import com.tesu.manicurehouse.response.GetActListResponse;
import com.tesu.manicurehouse.response.GetInformationCommentListResponse;
import com.tesu.manicurehouse.response.GetInformationDescResponse;
import com.tesu.manicurehouse.response.GetPostCommentListResponse;
import com.tesu.manicurehouse.response.GetPostDescResponse;
import com.tesu.manicurehouse.response.ReplyInformationCommentResponse;
import com.tesu.manicurehouse.response.ReplyPostCommentResponse;
import com.tesu.manicurehouse.response.SubmitInformationCommentResponse;
import com.tesu.manicurehouse.response.SubmitPostCommentResponse;
import com.tesu.manicurehouse.support.PercentLinearLayout;
import com.tesu.manicurehouse.support.PercentRelativeLayout;
import com.tesu.manicurehouse.util.BitmapBlurUtils;
import com.tesu.manicurehouse.util.Constants;
import com.tesu.manicurehouse.util.DialogUtils;
import com.tesu.manicurehouse.util.LogUtils;
import com.tesu.manicurehouse.util.PictureOption;
import com.tesu.manicurehouse.util.SharedPrefrenceUtils;
import com.tesu.manicurehouse.util.StringUtils;
import com.tesu.manicurehouse.util.UIUtils;
import com.tesu.manicurehouse.widget.CustomExpandableListView;
import com.tesu.manicurehouse.widget.MyListView;
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
 * 创建时间：2016/9/5 9:40
 * 交流详情页面
 */
public class OfficialEventsInfoActivity extends BaseActivity implements View.OnClickListener{

    private ImageView iv_top_back;
    private View rootView;
    private ScrollView sv1;
    private PullToRefreshScrollView sv;
    private WebView wv_content;
    private ImageView iv_info_image;
    //帖子id/资讯id
    private String id;
    //1增加，2取消
    private int operation;
    //1帖子，2资讯
    private int type = 2;
    private String information_id;
    //活动Id
    private String act_id;
    //css显示图片样式
    private String CSS_STYPE = "<head><style>img{max-width:340px !important;}</style></head>";
    private String title;
    private TextView tv_title;
    private TextView tv_title_name;
    //已选择的色卡集合
    private GetActListResponse.ActBean actBean;
    private String url;
    private Dialog loadingDialog;
    @Override
    protected View initView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        rootView = View.inflate(this, R.layout.activity_official_event_info, null);
        setContentView(rootView);
        sv = (PullToRefreshScrollView) rootView.findViewById(R.id.sv);
        sv1 = (ScrollView) rootView.findViewById(R.id.sv1);
        tv_title=(TextView)rootView.findViewById(R.id.tv_title);
        tv_title_name=(TextView)rootView.findViewById(R.id.tv_title_name);
        iv_info_image = (ImageView) rootView.findViewById(R.id.iv_info_image);
        wv_content = (WebView) rootView.findViewById(R.id.wv_content);
        iv_top_back = (ImageView) rootView.findViewById(R.id.iv_top_back);
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
        loadingDialog = DialogUtils.createLoadDialog(OfficialEventsInfoActivity.this, true);
        if (!ImageLoader.getInstance().isInited()) {
            ImageLoader.getInstance()
                    .init(ImageLoaderConfiguration.createDefault(UIUtils
                            .getContext()));
        }
        Intent intent = getIntent();
        actBean = (GetActListResponse.ActBean) intent.getSerializableExtra("actBean");
        act_id=intent.getStringExtra("act_id");
        if(TextUtils.isEmpty(act_id)){
            if(actBean != null){
                if(!TextUtils.isEmpty(actBean.act_title)){
                    title = actBean.act_title;
                    tv_title.setText(title);
                    tv_title_name.setText(title);
                }
                wv_content.loadDataWithBaseURL(null, CSS_STYPE + actBean.act_content, "text/html", "utf-8", null);
                ImageLoader.getInstance().displayImage(actBean.act_pic, iv_info_image, PictureOption.getSimpleOptions());
            }
        }
        else{
            runAsyncTask();
        }
        iv_top_back.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_top_back: {
                finish();
                overridePendingTransition(R.anim.animprv_in, R.anim.animprv_out);
                break;
            }
        }
    }

    public void runAsyncTask() {
        loadingDialog.show();
        GetActDescByIdProtocol getActDescByIdProtocol = new GetActDescByIdProtocol();
        GetActDescByIdRequest getActDescByIdRequest = new GetActDescByIdRequest();
        getActDescByIdRequest.map.put("act_id",act_id);
        url = getActDescByIdProtocol.getApiFun();

        MyVolley.uploadNoFile(MyVolley.POST, url, getActDescByIdRequest.map, new MyVolley.VolleyCallback() {
            @Override
            public void dealWithJson(String address, String json) {
                LogUtils.e("json:" + json);
                Gson gson = new Gson();
                GetActDescByIdResponse getActDescByIdResponse = gson.fromJson(json, GetActDescByIdResponse.class);
                if (getActDescByIdResponse.getCode()==0) {
                    loadingDialog.dismiss();
                    title = getActDescByIdResponse.getData().act_title;
                    tv_title.setText(title);
                    tv_title_name.setText(title);
                    wv_content.loadDataWithBaseURL(null, CSS_STYPE + getActDescByIdResponse.getData().act_content, "text/html", "utf-8", null);
                    ImageLoader.getInstance().displayImage(getActDescByIdResponse.getData().act_pic, iv_info_image, PictureOption.getSimpleOptions());
                } else {
                    loadingDialog.dismiss();
                    DialogUtils.showAlertDialog(OfficialEventsInfoActivity.this,
                            getActDescByIdResponse.getResultText());
                }


            }

            @Override
            public void dealWithError(String address, String error) {
                loadingDialog.dismiss();
                DialogUtils.showAlertDialog(OfficialEventsInfoActivity.this, error);
            }
        });
    }





}
