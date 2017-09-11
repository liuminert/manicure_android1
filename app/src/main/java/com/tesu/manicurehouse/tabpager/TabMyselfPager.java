package com.tesu.manicurehouse.tabpager;

import android.app.ActionBar;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lidroid.xutils.ViewUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.tesu.manicurehouse.R;
import com.tesu.manicurehouse.activity.AllOrdersActivity;
import com.tesu.manicurehouse.activity.DesignFeesActivity;
import com.tesu.manicurehouse.activity.InformationActivity;
import com.tesu.manicurehouse.activity.LoginActivity;
import com.tesu.manicurehouse.activity.MainActivity;
import com.tesu.manicurehouse.activity.ManicuristCertificationActivity;
import com.tesu.manicurehouse.activity.MyAddressActivity;
import com.tesu.manicurehouse.activity.MyAttentionActivity;
import com.tesu.manicurehouse.activity.MyCollectionActivity;
import com.tesu.manicurehouse.activity.MyCommentActivity;
import com.tesu.manicurehouse.activity.MyLeaveMessageActivity;
import com.tesu.manicurehouse.activity.MyProductionActivity;
import com.tesu.manicurehouse.activity.OfficialServiceActivity;
import com.tesu.manicurehouse.activity.OrderForTheCommentActivity;
import com.tesu.manicurehouse.activity.OrderForTheDeliveryActivity;
import com.tesu.manicurehouse.activity.OrderForTheFoodActivity;
import com.tesu.manicurehouse.activity.OrderForThePaymentActivity;
import com.tesu.manicurehouse.activity.PersonalActivity;
import com.tesu.manicurehouse.activity.RechargeActivity;
import com.tesu.manicurehouse.activity.RecommendedActivity;
import com.tesu.manicurehouse.activity.SetupActivity;
import com.tesu.manicurehouse.activity.ShareAwardsActivity;
import com.tesu.manicurehouse.activity.ShopkeeperCertificationActivity;
import com.tesu.manicurehouse.activity.ShoppingCarActivity;
import com.tesu.manicurehouse.base.MyVolley;
import com.tesu.manicurehouse.base.TabBasePager;
import com.tesu.manicurehouse.bean.MessageBean;
import com.tesu.manicurehouse.huanxin.HuanXinLoginActivity;
import com.tesu.manicurehouse.protocol.GetUserInfoProtocol;
import com.tesu.manicurehouse.protocol.UpdatePwdProtocol;
import com.tesu.manicurehouse.request.GetUserInfoRequest;
import com.tesu.manicurehouse.request.UpdatePwdRequest;
import com.tesu.manicurehouse.response.GetMessageResponse;
import com.tesu.manicurehouse.response.GetUserInfoResponse;
import com.tesu.manicurehouse.response.UpdatePwdResponse;
import com.tesu.manicurehouse.util.DialogUtils;
import com.tesu.manicurehouse.util.LogUtils;
import com.tesu.manicurehouse.util.LoginUtils;
import com.tesu.manicurehouse.util.MyUpyunUtils;
import com.tesu.manicurehouse.util.PictureOption;
import com.tesu.manicurehouse.util.SharedPrefrenceUtils;
import com.tesu.manicurehouse.util.StringUtils;
import com.tesu.manicurehouse.util.UIUtils;
import com.tesu.manicurehouse.widget.DragLayout;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @作者: 许明达
 * @创建时间: 2016年3月23日上午11:10:20
 * @版权: 特速版权所有
 * @描述: TODO
 */
public class TabMyselfPager extends TabBasePager implements View.OnClickListener {

    LinearLayout view;
    private TextView tv_attention;
    private TextView tv_production;
    private TextView tv_collection;
    private RelativeLayout ll_order;
    private TextView tv_shoppingcar;
    private TextView tv_for_payment;
    private TextView tv_for_delivery;
    private TextView tv_for_good;
    private TextView tv_for_comment;
    private RelativeLayout rl_recharge;
    private RelativeLayout rl_degsin;
    private RelativeLayout rl_comments;
    private RelativeLayout rl_address;
    private ImageView iv_message_titile;
    private ImageView iv_setup;
    private RelativeLayout ll_shopkeeper_certification;
    private RelativeLayout ll_manicurist_certification;
    private RelativeLayout ll_official_service;
    private RelativeLayout ll_leave;
    private RelativeLayout ll_recommended;
    private RelativeLayout ll_share;
    private View line1;
    private View line2;
    private RelativeLayout rl_member_manage;
    private RelativeLayout rl_account_manage;
    private boolean member_open = false;
    private boolean account_open = false;
    private ImageView iv_down;
    private ImageView iv_down2;
    private TextView tv_name;
    private String url;
    private Dialog loadingDialog;
    private GetUserInfoResponse getUserInfoResponse;
    private ImageView iv_myself_img;
    private GetMessageResponse getMessageResponse;
    private Gson gson;
    private String userId;
    private TextView tv_message_num;  //消息数量
    private ArrayList<MessageBean> messageBeanList;


    private ImageView iv_message_back;
    private String timepath;
    /**
     * 照片参数
     */
    private static final int PHOTO_GRAPH = 4;// 拍照
    private static final int PHOTO_ZOOM = 5; // 缩放
    private static final int DESCRIPTION_RESOULT = 6;// 结果

    private static final String IMAGE_UNSPECIFIED = "image/*";
    /**
     * 照相选择界面
     */
    private PopupWindow pWindow;
    private View root;
    private LayoutInflater mInflater;
    /**
     * @param context
     */
    public TabMyselfPager(Context context, FrameLayout mDragLayout) {
        super(context, mDragLayout);
    }
    private boolean isloading=false;
    private ImageView iv_v_img;
    private TextView tv_nick;
    @Override
    protected View initView() {
        view = (LinearLayout) View.inflate(mContext, R.layout.myself, null);
        ViewUtils.inject(this, view);
        if (mInflater == null) {
            mInflater = (LayoutInflater) UIUtils.getContext().getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
        }
        root = mInflater.inflate(R.layout.alert_dialog, null);
        pWindow = new PopupWindow(root, ActionBar.LayoutParams.FILL_PARENT,
                ActionBar.LayoutParams.FILL_PARENT);
        tv_nick= (TextView) view.findViewById(R.id.tv_nick);
        tv_name = (TextView) view.findViewById(R.id.tv_name);
        rl_member_manage = (RelativeLayout) view.findViewById(R.id.rl_member_manage);
        line1 = (View) view.findViewById(R.id.line1);
        iv_myself_img = (ImageView) view.findViewById(R.id.iv_myself_img);
        iv_down = (ImageView) view.findViewById(R.id.iv_down);
        rl_account_manage = (RelativeLayout) view.findViewById(R.id.rl_account);
        line2 = (View) view.findViewById(R.id.line2);
        iv_down2 = (ImageView) view.findViewById(R.id.iv_down1);
        ll_leave = (RelativeLayout) view.findViewById(R.id.ll_leave);
        iv_setup = (ImageView) view.findViewById(R.id.iv_set);
        rl_address = (RelativeLayout) view.findViewById(R.id.rl_address);
        rl_comments = (RelativeLayout) view.findViewById(R.id.rl_comments);
        rl_degsin = (RelativeLayout) view.findViewById(R.id.rl_degsin);
        rl_recharge = (RelativeLayout) view.findViewById(R.id.rl_recharge);
        ll_share= (RelativeLayout) view.findViewById(R.id.ll_share);
        tv_shoppingcar = (TextView) view.findViewById(R.id.tv_shoppingcar);
        tv_production = (TextView) view.findViewById(R.id.tv_production);
        tv_attention = (TextView) view.findViewById(R.id.tv_attention);
        tv_collection = (TextView) view.findViewById(R.id.tv_collection);
        ll_order = (RelativeLayout) view.findViewById(R.id.ll_order);
        tv_for_payment = (TextView) view.findViewById(R.id.tv_for_payment);
        tv_for_delivery = (TextView) view.findViewById(R.id.tv_for_delivery);
        tv_for_good = (TextView) view.findViewById(R.id.tv_for_good);
        tv_for_comment = (TextView) view.findViewById(R.id.tv_for_comment);
        iv_message_titile = (ImageView) view.findViewById(R.id.iv_message_titile);
        ll_shopkeeper_certification = (RelativeLayout) view.findViewById(R.id.ll_shopkeeper_certification);
        ll_manicurist_certification = (RelativeLayout) view.findViewById(R.id.ll_manicurist_certification);
        ll_official_service = (RelativeLayout) view.findViewById(R.id.ll_official_service);
        ll_recommended = (RelativeLayout) view.findViewById(R.id.ll_recommended);
        tv_message_num = (TextView) view.findViewById(R.id.tv_message_num);
        iv_message_back = (ImageView) view.findViewById(R.id.iv_message_back);
        iv_v_img = (ImageView) view.findViewById(R.id.iv_v_img);
        return view;
    }

    private View.OnClickListener itemsOnClick = new View.OnClickListener() {

        public void onClick(View v) {
            pWindow.dismiss();
            switch (v.getId()) {
                case R.id.btn_TakePicture: {
                    timepath = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                    MainActivity.timepath = timepath;
                    if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                        if (!dir.exists()) {
                            dir.mkdir();
                        }
                        File file = new File(dir, timepath + ".jpg");
                        UIUtils.startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(
                                MediaStore.EXTRA_OUTPUT, Uri.fromFile(file)), PHOTO_GRAPH);
                    }
                    break;
                }
                case R.id.btn_Phone: {
                    Intent intent = new Intent(Intent.ACTION_PICK, null);
                    intent.setDataAndType(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            IMAGE_UNSPECIFIED);
                    UIUtils.startActivityForResult(intent, PHOTO_ZOOM);
                    break;
                }
                case R.id.btn_cancel: {
                    pWindow.dismiss();
                    break;
                }
                default:
                    break;
            }

        }

    };

    @Override
    public void initData() {
        if (!ImageLoader.getInstance().isInited()) {
            ImageLoader.getInstance()
                    .init(ImageLoaderConfiguration.createDefault(UIUtils
                            .getContext()));
        }
        gson = new Gson();
        userId = SharedPrefrenceUtils.getString(mContext, "user_id");

        loadingDialog = DialogUtils.createLoadDialog(mContext, true);
        iv_myself_img.setOnClickListener(this);
        ll_order.setOnClickListener(this);
        ll_share.setOnClickListener(this);
        tv_attention.setOnClickListener(this);
        tv_production.setOnClickListener(this);
        tv_collection.setOnClickListener(this);
        tv_shoppingcar.setOnClickListener(this);
        tv_for_payment.setOnClickListener(this);
        tv_for_delivery.setOnClickListener(this);
        tv_for_good.setOnClickListener(this);
        tv_for_comment.setOnClickListener(this);
        rl_recharge.setOnClickListener(this);
        rl_degsin.setOnClickListener(this);
        rl_comments.setOnClickListener(this);
        rl_address.setOnClickListener(this);
        iv_setup.setOnClickListener(this);
        rl_member_manage.setOnClickListener(this);
        iv_message_titile.setOnClickListener(this);
        ll_recommended.setOnClickListener(this);
        ll_shopkeeper_certification.setOnClickListener(this);
        ll_manicurist_certification.setOnClickListener(this);
        ll_official_service.setOnClickListener(this);
        ll_leave.setOnClickListener(this);
        rl_account_manage.setOnClickListener(this);
        root.findViewById(R.id.btn_Phone).setOnClickListener(itemsOnClick);
        root.findViewById(R.id.btn_TakePicture)
                .setOnClickListener(itemsOnClick);
        root.findViewById(R.id.bg_photo).getBackground().setAlpha(100);
        root.findViewById(R.id.btn_cancel).setOnClickListener(itemsOnClick);

        if(LoginUtils.isLogin()){
            getMessge();
        }
        if (LoginUtils.isLogin()&&(!isloading||LoginUtils.isUpdate()) ) {
            runAsyncTask();
            isloading=true;
            if(LoginUtils.isUpdate()){
                SharedPrefrenceUtils.setBoolean(UIUtils.getContext(), "isUpdate",false);
            }
        }
//        else {
//            if (!TextUtils.isEmpty(SharedPrefrenceUtils.getString(mContext, "avatar"))) {
//                ImageLoader.getInstance().displayImage(SharedPrefrenceUtils.getString(mContext, "avatar"), iv_myself_img, PictureOption.getSimpleOptions());
//            }
//            tv_attention.setText("关注" + SharedPrefrenceUtils.getString(mContext, "followCnt"));
//            tv_collection.setText("收藏" +SharedPrefrenceUtils.getString(mContext, "collectionCnt"));
//            tv_production.setText("作品" + SharedPrefrenceUtils.getString(mContext, "worksCnt"));
//            tv_name.setText(SharedPrefrenceUtils.getString(mContext, "alias"));
//        }
    }

    /**
     * 获得消息列表
     */
    private void getMessge() {
        url = "app/getMsg.do";
        Map<String, String> params = new HashMap<String, String>();
        if (StringUtils.isEmpty(userId)) {
            userId = "0";
        }
        params.put("user_id", userId);
        MyVolley.uploadNoFile(MyVolley.POST, url, params, new MyVolley.VolleyCallback() {

            @Override
            public void dealWithJson(String address, String json) {
                LogUtils.e("获得消息列表:" + json);
                getMessageResponse = gson.fromJson(json, GetMessageResponse.class);
                if (getMessageResponse.getCode() == 0) {
                    if(getMessageResponse.getUnReadCnt()==0){
                        tv_message_num.setVisibility(View.GONE);
                        iv_message_back.setVisibility(View.GONE);
                    }else{
                        tv_message_num.setVisibility(View.VISIBLE);
                        iv_message_back.setVisibility(View.VISIBLE);
                        tv_message_num.setText(getMessageResponse.getUnReadCnt() + "");
                    }
                } else {
                    DialogUtils.showAlertDialog(mContext, getMessageResponse.getResultText());
                }

            }

            @Override
            public void dealWithError(String address, String error) {
                LogUtils.e("获得消息列表错误:" + error);
                DialogUtils.showAlertDialog(mContext, error);

            }
        });
    }

    public void setDate(GetUserInfoResponse getUserInfoResponse) {


        ImageLoader.getInstance().displayImage(getUserInfoResponse.userData.avatar, iv_myself_img, PictureOption.getSimpleOptions());
        tv_attention.setText("关注" + getUserInfoResponse.followCnt);
        tv_collection.setText("收藏" + getUserInfoResponse.collectionCnt);
        tv_production.setText("作品" + getUserInfoResponse.worksCnt);
        tv_name.setText(getUserInfoResponse.userData.user_name);
        tv_nick.setText(getUserInfoResponse.userData.alias);
        SharedPrefrenceUtils.setString(mContext, "avatar", getUserInfoResponse.userData.avatar);
        SharedPrefrenceUtils.setString(mContext, "alias", getUserInfoResponse.userData.alias);
        SharedPrefrenceUtils.setString(mContext, "followCnt", String.valueOf(getUserInfoResponse.followCnt));
        SharedPrefrenceUtils.setString(mContext, "collectionCnt", String.valueOf(getUserInfoResponse.collectionCnt));
        SharedPrefrenceUtils.setString(mContext, "worksCnt", String.valueOf(getUserInfoResponse.worksCnt));
        SharedPrefrenceUtils.setBoolean(mContext, "isloaded", true);

        if(TextUtils.isEmpty(getUserInfoResponse.userData.identity_type) || getUserInfoResponse.userData.identity_type.equals("0")){
            iv_v_img.setVisibility(View.GONE);
        }else if(getUserInfoResponse.userData.identity_type.equals("1")){
            iv_v_img.setVisibility(View.VISIBLE);
            iv_v_img.setImageResource(R.mipmap.v_slivery);
        }else if(getUserInfoResponse.userData.identity_type.equals("2")){
            iv_v_img.setVisibility(View.VISIBLE);
            iv_v_img.setImageResource(R.mipmap.v_golden);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_myself_img:{
                pWindow.setAnimationStyle(R.style.AnimBottom);
                pWindow.showAtLocation(view.findViewById(R.id.ll_myself),
                        Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                break;
            }
            case R.id.ll_share:{
                Intent intent = new Intent(mContext, ShareAwardsActivity.class);
                UIUtils.startActivityNextAnim(intent);
                break;
            }
            case R.id.ll_recommended: {
                Intent intent = new Intent(mContext, RecommendedActivity.class);
                UIUtils.startActivityNextAnim(intent);
                break;
            }
            case R.id.rl_member_manage: {
                if (!member_open) {
                    rl_recharge.setVisibility(View.VISIBLE);
                    line1.setVisibility(View.VISIBLE);
                    rl_degsin.setVisibility(View.VISIBLE);
                    iv_down.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.bt_more_l));
                    member_open = true;
                } else {
                    rl_recharge.setVisibility(View.GONE);
                    line1.setVisibility(View.GONE);
                    rl_degsin.setVisibility(View.GONE);
                    iv_down.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.down_arrow));
                    member_open = false;
                }
                break;
            }
            case R.id.rl_account: {
                if (!account_open) {
                    ll_leave.setVisibility(View.VISIBLE);
                    line2.setVisibility(View.VISIBLE);
                    rl_comments.setVisibility(View.VISIBLE);
                    rl_address.setVisibility(View.VISIBLE);
                    iv_down2.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.bt_more_l));
                    account_open = true;
                } else {
                    ll_leave.setVisibility(View.GONE);
                    line2.setVisibility(View.GONE);
                    rl_comments.setVisibility(View.GONE);
                    rl_address.setVisibility(View.GONE);
                    iv_down2.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.down_arrow));
                    account_open = false;
                }
                break;
            }
            case R.id.ll_leave: {
                Intent intent = new Intent(mContext, MyLeaveMessageActivity.class);
                UIUtils.startActivityNextAnim(intent);
                break;
            }
            case R.id.ll_official_service: {
                Intent intent = new Intent(mContext, HuanXinLoginActivity.class);
                UIUtils.startActivityNextAnim(intent);
                break;
            }
            case R.id.ll_manicurist_certification: {
                Intent intent = new Intent(mContext, ManicuristCertificationActivity.class);
                UIUtils.startActivityNextAnim(intent);
                break;
            }
            case R.id.ll_shopkeeper_certification: {
                Intent intent = new Intent(mContext, ShopkeeperCertificationActivity.class);
                UIUtils.startActivityNextAnim(intent);
                break;
            }
            case R.id.iv_set: {
                Intent intent = new Intent(mContext, SetupActivity.class);
                UIUtils.startActivityForResultNextAnim(intent,1);
//                UIUtils.startActivityNextAnim(intent);
                break;
            }
            case R.id.iv_message_titile: {
                if(getMessageResponse != null){
                    messageBeanList = getMessageResponse.getDataList();
                }
                Intent intent = new Intent(mContext, InformationActivity.class);
                if(messageBeanList != null){
                    intent.putExtra("messageList", messageBeanList);
                }
                UIUtils.startActivityNextAnim(intent);
                break;
            }
            case R.id.rl_address: {
                Intent intent = new Intent(mContext, MyAddressActivity.class);
                UIUtils.startActivityNextAnim(intent);
                break;
            }
            case R.id.rl_comments: {
                Intent intent = new Intent(mContext, MyCommentActivity.class);
                UIUtils.startActivityNextAnim(intent);
                break;
            }
            case R.id.rl_degsin: {
                Intent intent = new Intent(mContext, DesignFeesActivity.class);
                UIUtils.startActivityNextAnim(intent);
                break;
            }
            case R.id.rl_recharge: {
                Intent intent = new Intent(mContext, RechargeActivity.class);
                UIUtils.startActivityNextAnim(intent);
                break;
            }
            case R.id.tv_for_payment: {
                Intent intent = new Intent(mContext, AllOrdersActivity.class);
                intent.putExtra("status", "1");
                UIUtils.startActivityNextAnim(intent);
                break;
            }
            case R.id.tv_for_delivery: {
                Intent intent = new Intent(mContext, AllOrdersActivity.class);
                intent.putExtra("status", "2");
                UIUtils.startActivityNextAnim(intent);
                break;
            }
            case R.id.tv_for_good: {
                Intent intent = new Intent(mContext, AllOrdersActivity.class);
                intent.putExtra("status", "3");
                UIUtils.startActivityNextAnim(intent);
                break;
            }
            case R.id.tv_for_comment: {
                Intent intent = new Intent(mContext, AllOrdersActivity.class);
                intent.putExtra("status", "4");
                UIUtils.startActivityNextAnim(intent);
                break;
            }
            case R.id.tv_shoppingcar: {
                Intent intent = new Intent(mContext, ShoppingCarActivity.class);
                UIUtils.startActivityNextAnim(intent);
                break;
            }

            case R.id.ll_order: {
                Intent intent = new Intent(mContext, AllOrdersActivity.class);
                intent.putExtra("status", "0");
                UIUtils.startActivityNextAnim(intent);
                break;
            }
            case R.id.tv_production: {
                Intent intent = new Intent(mContext, MyProductionActivity.class);
                UIUtils.startActivityNextAnim(intent);
                break;
            }
            case R.id.tv_attention: {
                Intent intent = new Intent(mContext, MyAttentionActivity.class);
                UIUtils.startActivityForResultNextAnim(intent, 1);
                break;
            }
            case R.id.tv_collection: {
                Intent intent = new Intent(mContext, MyCollectionActivity.class);
                UIUtils.startActivityNextAnim(intent);
                break;
            }
        }

    }

    public void runAsyncTask() {
        LogUtils.e("进这里");
        loadingDialog.show();
        GetUserInfoProtocol getUserInfoProtocol = new GetUserInfoProtocol();
        GetUserInfoRequest getUserInfoRequest = new GetUserInfoRequest();
        url = getUserInfoProtocol.getApiFun();
        getUserInfoRequest.map.put("user_id", SharedPrefrenceUtils.getString(mContext, "user_id"));


        MyVolley.uploadNoFile(MyVolley.POST, url, getUserInfoRequest.map, new MyVolley.VolleyCallback() {
            @Override
            public void dealWithJson(String address, String json) {

                Gson gson = new Gson();
                getUserInfoResponse = gson.fromJson(json, GetUserInfoResponse.class);
                LogUtils.e("sendVerificationCodeResponse:" + json.toString());
                if (getUserInfoResponse.code==0) {
                    loadingDialog.dismiss();
                    setDate(getUserInfoResponse);
                    SharedPrefrenceUtils.setString(mContext, url + "0", json);
                } else {
                    loadingDialog.dismiss();
                    DialogUtils.showAlertDialog(mContext,
                            getUserInfoResponse.resultText);
                }


            }

            @Override
            public void dealWithError(String address, String error) {
                loadingDialog.dismiss();
                DialogUtils.showAlertDialog(mContext, error);
            }
        });
    }
}
