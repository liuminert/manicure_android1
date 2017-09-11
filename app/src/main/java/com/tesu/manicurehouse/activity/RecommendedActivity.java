package com.tesu.manicurehouse.activity;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.tesu.manicurehouse.R;
import com.tesu.manicurehouse.adapter.ShareAdapter;
import com.tesu.manicurehouse.base.BaseActivity;
import com.tesu.manicurehouse.base.MyVolley;
import com.tesu.manicurehouse.bean.RecommendDescBean;
import com.tesu.manicurehouse.bean.ShareDateBean;
import com.tesu.manicurehouse.bean.ShareInfo;
import com.tesu.manicurehouse.protocol.GetFollowInfoProtocol;
import com.tesu.manicurehouse.protocol.GetRecommendDescListProtocol;
import com.tesu.manicurehouse.protocol.ShareProtocol;
import com.tesu.manicurehouse.request.GetFollowInfoRequest;
import com.tesu.manicurehouse.request.GetRecommendDescListRequest;
import com.tesu.manicurehouse.request.ShareRequest;
import com.tesu.manicurehouse.response.GetFollowInfoResponse;
import com.tesu.manicurehouse.response.GetRecommendDescListResponse;
import com.tesu.manicurehouse.response.ShareResponse;
import com.tesu.manicurehouse.support.PercentLinearLayout;
import com.tesu.manicurehouse.util.Constants;
import com.tesu.manicurehouse.util.DialogUtils;
import com.tesu.manicurehouse.util.LogUtils;
import com.tesu.manicurehouse.util.SharedPrefrenceUtils;
import com.tesu.manicurehouse.util.StringUtils;
import com.tesu.manicurehouse.util.UIUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.system.text.ShortMessage;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

/**
 * 类描述：
 * 创建人：许明达
 * 创建时间：2016/8/16 11:40
 * 推荐有奖页面
 */
public class RecommendedActivity extends BaseActivity implements View.OnClickListener, PlatformActionListener {

    private TextView tv_top_back;
    private View rootView;
    private RelativeLayout rl_shop_owner;
    private PercentLinearLayout rl_share;
    private Button btn_invitation;
    private TextView tv_cancle;
    private ShareAdapter shareAdapter;
    private GridView gv_share;
    private TextView tv_content;
    private String url;
    private Dialog loadingDialog;
    private TextView tv_recommendCnt;
    private List<RecommendDescBean> recommendDescBeanList;
    //积分
    private int recommendScore;
    //好友数
    private int recommendCnt;
    @Override
    protected View initView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        rootView = View.inflate(this, R.layout.activity_recommended, null);
        setContentView(rootView);
        tv_recommendCnt=(TextView)rootView.findViewById(R.id.tv_recommendCnt);
        tv_cancle = (TextView) rootView.findViewById(R.id.tv_cancle);
        tv_top_back = (TextView) rootView.findViewById(R.id.tv_top_back);
        rl_shop_owner = (RelativeLayout) rootView.findViewById(R.id.rl_shop_owner);
        rl_share = (PercentLinearLayout) rootView.findViewById(R.id.rl_share);
        btn_invitation = (Button) rootView.findViewById(R.id.btn_invitation);
        gv_share = (GridView) rootView.findViewById(R.id.gv_share);
        tv_content = (TextView) rootView.findViewById(R.id.tv_content);
        initData();
        return null;
    }


    public void initData() {
        loadingDialog = DialogUtils.createLoadDialog(RecommendedActivity.this, true);
        tv_content.setText("1.店主要通过你的链接注册美甲屋账号。\n2.然后在”我的-店主认证“进行认证，店主认证需要上传营业执照（或租凭合同）和身份证，美甲屋将在24小时内审核，通过认证则推荐成功。\n3.多人邀请同一个店主，以邀请链接为准。\n4.邀请得到的积分奖励无上限，邀请越多奖励越多。\n5.最终解释权归美甲屋所有，作弊违规用户将取消奖励积分。");
        tv_content.getViewTreeObserver().addOnGlobalLayoutListener(new OnTvGlobalLayoutListener());
        tv_cancle.setOnClickListener(this);
        tv_top_back.setOnClickListener(this);
        rl_shop_owner.setOnClickListener(this);
        btn_invitation.setOnClickListener(this);
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
        share6.setShare_photo(R.drawable.icon_share_address);
        share6.setShare_photo_selected(R.drawable.icon_share_address);
        share6.setShare_name(ShortMessage.NAME);
        slist.add(share6);
        shareAdapter = new ShareAdapter(slist, rl_share, this, true,this);
        gv_share.setAdapter(shareAdapter);
        runAsyncTask();
        getRecommendDescList();
    }

    private class OnTvGlobalLayoutListener implements ViewTreeObserver.OnGlobalLayoutListener {
        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void onGlobalLayout() {
            tv_content.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            final String newText = autoSplitText(tv_content);
            if (!TextUtils.isEmpty(newText)) {
                tv_content.setText(newText);
            }
        }
    }

    private String autoSplitText(final TextView tv) {
        final String rawText = tv.getText().toString();
        final Paint tvPaint = tv.getPaint();
        final int tvWidth = tv.getWidth() - tv.getPaddingLeft() - tv.getPaddingRight();

        String newText = rawText;
        return newText;
    }

    public void setDate(GetRecommendDescListResponse getRecommendDescListResponse) {
        recommendDescBeanList=new ArrayList<>();
        //添加第一项
        recommendDescBeanList.addAll(getRecommendDescListResponse.dataList);
        recommendScore=getRecommendDescListResponse.recommendScore;
        recommendCnt= getRecommendDescListResponse.recommendCnt;
        tv_recommendCnt.setText("已邀请"+getRecommendDescListResponse.recommendCnt+"位店主");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_cancle: {
                rl_share.setVisibility(View.INVISIBLE);
                break;
            }
            case R.id.btn_invitation: {
                rl_share.setVisibility(View.VISIBLE);
                // 初始化ShareSDK
                ShareSDK.initSDK(UIUtils.getContext());
                break;
            }
            case R.id.rl_shop_owner: {
                Intent intent = new Intent(RecommendedActivity.this, ShopOwnerInfoActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("recommendDescBeanList", (Serializable) recommendDescBeanList);
                intent.putExtras(bundle);
                intent.putExtra("recommendCnt", recommendCnt);
                intent.putExtra("recommendScore",recommendScore);
                UIUtils.startActivityNextAnim(intent);
                break;
            }
            case R.id.tv_top_back:
                finish();
                overridePendingTransition(R.anim.animprv_in, R.anim.animprv_out);
                break;
        }
    }

    @Override
    public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
        Toast.makeText(RecommendedActivity.this, "分享成功!",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onError(Platform platform, int i, Throwable throwable) {
        Toast.makeText(RecommendedActivity.this, "分享失败!" + throwable,
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onCancel(Platform platform, int i) {
        Toast.makeText(RecommendedActivity.this, "分享取消!",
                Toast.LENGTH_SHORT).show();
    }

    public void runAsyncTask() {
        ShareProtocol shareProtocol = new ShareProtocol();
        ShareRequest shareRequest = new ShareRequest();
        url = shareProtocol.getApiFun();
        shareRequest.map.put("type", String.valueOf(1));


        MyVolley.uploadNoFile(MyVolley.POST, url, shareRequest.map, new MyVolley.VolleyCallback() {
            @Override
            public void dealWithJson(String address, String json) {

                Gson gson = new Gson();
                ShareResponse shareResponse = gson.fromJson(json, ShareResponse.class);
                LogUtils.e("sendVerificationCodeResponse:" + json.toString());
                if (shareResponse.code == 0) {
                    //设置分享的内容
                    String shareUrl = Constants.SHARE_URL + "share/recommend.shtml?share_user_id=" + SharedPrefrenceUtils.getString(RecommendedActivity.this, "user_id");
                    ShareDateBean shareDateBean = new ShareDateBean();
                    shareDateBean.setContent(shareResponse.content);
                    shareDateBean.setImageurl(shareResponse.pic);
                    shareDateBean.setTitle(shareResponse.title);
                    shareDateBean.setUrl(shareUrl);
                    shareAdapter.setShareDateBean(shareDateBean);
                } else {
                    DialogUtils.showAlertDialog(RecommendedActivity.this,
                            shareResponse.resultText);
                }


            }

            @Override
            public void dealWithError(String address, String error) {
                DialogUtils.showAlertDialog(RecommendedActivity.this, error);
            }
        });
    }

    public void getRecommendDescList() {
        loadingDialog.show();
        GetRecommendDescListProtocol getRecommendDescListProtocol = new GetRecommendDescListProtocol();
        GetRecommendDescListRequest getRecommendDescListRequest = new GetRecommendDescListRequest();
        url = getRecommendDescListProtocol.getApiFun();
        getRecommendDescListRequest.map.put("user_id", SharedPrefrenceUtils.getString(RecommendedActivity.this,"user_id"));


        MyVolley.uploadNoFile(MyVolley.POST, url, getRecommendDescListRequest.map, new MyVolley.VolleyCallback() {
            @Override
            public void dealWithJson(String address, String json) {

                Gson gson = new Gson();
                GetRecommendDescListResponse getRecommendDescListResponse = gson.fromJson(json, GetRecommendDescListResponse.class);
                LogUtils.e("sendVerificationCodeResponse:" + json.toString());
                if (getRecommendDescListResponse.code == 0) {
                    loadingDialog.dismiss();
                    setDate(getRecommendDescListResponse);
                } else {
                    loadingDialog.dismiss();
                    DialogUtils.showAlertDialog(RecommendedActivity.this,
                            getRecommendDescListResponse.resultText);
                }


            }

            @Override
            public void dealWithError(String address, String error) {
                loadingDialog.dismiss();
                DialogUtils.showAlertDialog(RecommendedActivity.this, error);
            }
        });
    }
}
