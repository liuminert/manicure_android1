package com.tesu.manicurehouse.activity;

import android.app.ActionBar;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.tesu.manicurehouse.R;
import com.tesu.manicurehouse.adapter.HomeworkImageAdapter;
import com.tesu.manicurehouse.base.BaseActivity;
import com.tesu.manicurehouse.base.MyVolley;
import com.tesu.manicurehouse.protocol.GetUserInfoProtocol;
import com.tesu.manicurehouse.protocol.UpdateUserInfoProtocol;
import com.tesu.manicurehouse.request.GetUserInfoRequest;
import com.tesu.manicurehouse.request.UpdateUserInfoRequest;
import com.tesu.manicurehouse.response.GetUserInfoResponse;
import com.tesu.manicurehouse.response.UpdateUserInfoResponse;
import com.tesu.manicurehouse.util.BitmapUtils;
import com.tesu.manicurehouse.util.DialogUtils;
import com.tesu.manicurehouse.util.LogUtils;
import com.tesu.manicurehouse.util.MyUpyunUtils;
import com.tesu.manicurehouse.util.PictureOption;
import com.tesu.manicurehouse.util.SharedPrefrenceUtils;
import com.tesu.manicurehouse.util.UIUtils;
import com.tesu.manicurehouse.widget.CircleImageView;
import com.upyun.library.listener.UpCompleteListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * 类描述：
 * 创建人：许明达
 * 创建时间：2016/8/16 11:40
 * 个人信息页面
 */
public class PersonalActivity extends BaseActivity implements View.OnClickListener {

    /**
     * 照相选择界面
     */
    private PopupWindow pWindow;
    private View root;
    private LayoutInflater mInflater;
    private String timepath;
    /**
     * 照片参数
     */
    private static final int PHOTO_GRAPH = 1;// 拍照
    private static final int PHOTO_ZOOM = 2; // 缩放
    private static final int DESCRIPTION_RESOULT = 3;// 结果

    private static final String IMAGE_UNSPECIFIED = "image/*";
    private TextView tv_top_back;
    private View rootView;
    private RelativeLayout rl_address;
    private RelativeLayout rl_city;
    private RelativeLayout rl_head_portrait;
    private RelativeLayout rl_name;
    private RelativeLayout rl_lable;
    private CircleImageView iv_image;
    private TextView tv_name;
    private TextView tv_identity;
    private TextView tv_lable;
    private TextView tv_city;
    private String path;
    private String imageurl;
    private String url;
    private Dialog loadingDialog;
    private GetUserInfoResponse getUserInfoResponse;
    private int UPDATENAME=4;
    private int UPDATECITY=5;
    private int UPDATELABLE=6;
    @Override
    protected View initView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        rootView = View.inflate(this, R.layout.personal_layout, null);
        setContentView(rootView);
        if (mInflater == null) {
            mInflater = (LayoutInflater) UIUtils.getContext().getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
        }
        root = mInflater.inflate(R.layout.alert_dialog, null);
        pWindow = new PopupWindow(root, ActionBar.LayoutParams.FILL_PARENT,
                ActionBar.LayoutParams.FILL_PARENT);
        root.findViewById(R.id.btn_Phone).setOnClickListener(itemsOnClick);
        root.findViewById(R.id.btn_TakePicture)
                .setOnClickListener(itemsOnClick);
        root.findViewById(R.id.bg_photo).getBackground().setAlpha(100);
        root.findViewById(R.id.btn_cancel).setOnClickListener(itemsOnClick);
        tv_name=(TextView)rootView.findViewById(R.id.tv_name);
        tv_identity=(TextView)rootView.findViewById(R.id.tv_identity);
        tv_city=(TextView)rootView.findViewById(R.id.tv_city);
        tv_lable=(TextView)rootView.findViewById(R.id.tv_lable);
        iv_image=(CircleImageView)rootView.findViewById(R.id.iv_image);
        tv_top_back = (TextView) rootView.findViewById(R.id.tv_top_back);
        rl_address=(RelativeLayout)rootView.findViewById(R.id.rl_address);
        rl_name=(RelativeLayout)rootView.findViewById(R.id.rl_name);
        rl_city=(RelativeLayout)rootView.findViewById(R.id.rl_city);
        rl_lable=(RelativeLayout)rootView.findViewById(R.id.rl_lable);
        rl_head_portrait=(RelativeLayout)rootView.findViewById(R.id.rl_head_portrait);
        initData();
        return null;
    }

    private View.OnClickListener itemsOnClick = new View.OnClickListener() {

        public void onClick(View v) {
            pWindow.dismiss();
            switch (v.getId()) {
                case R.id.btn_TakePicture: {
                    timepath = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

                    if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                        if (!dir.exists()) {
                            dir.mkdir();
                        }
                        File file = new File(dir, timepath + ".jpg");
                        startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(
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

    public void setDate(GetUserInfoResponse getUserInfoResponse){
        ImageLoader.getInstance().displayImage(getUserInfoResponse.userData.avatar,iv_image, PictureOption.getSimpleOptions());
        tv_name.setText(getUserInfoResponse.userData.alias);
        tv_city.setText(getUserInfoResponse.userData.city);
        tv_lable.setText(getUserInfoResponse.userData.signature);
        //身份 0：普通用户，1：美甲师，2：美甲店主
        if(getUserInfoResponse.userData.identity_type.equals("0")){
            tv_identity.setText("普通用户");
        }
        else if(getUserInfoResponse.userData.identity_type.equals("1")){
            tv_identity.setText("美甲师");
        }
        else{
            tv_identity.setText("美甲店主");
        }

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 拍照
        if (requestCode == PHOTO_GRAPH) {
            // 设置文件保存路径
            File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            path = dir + "/" + timepath + ".jpg";
            File file = new File(path);
            if (file.exists()) {
                MyUpyunUtils.upload(path, completeListener);
            }

        }

        // 读取相册缩放图片
        if (requestCode == PHOTO_ZOOM && data != null) {
            if(data.getData()!=null){
                // 图片信息需包含在返回数据中
                String[] proj = {MediaStore.Images.Media.DATA};
                // 获取包含所需数据的Cursor对象
                Uri uri = data.getData();
                @SuppressWarnings("deprecation")
                Cursor cursor = null;
                cursor = managedQuery(uri, proj, null, null, null);
                if (cursor == null) {
                    uri = BitmapUtils.getPictureUri(data, PersonalActivity.this);
                    cursor = managedQuery(uri, proj, null, null, null);
                }
                // 获取索引
                int photocolumn = cursor
                        .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                // 将光标一直开头
                cursor.moveToFirst();
                // 根据索引值获取图片路径
                path = cursor.getString(photocolumn);
                if (!TextUtils.isEmpty(path)) {
                    MyUpyunUtils.upload(path, completeListener);
                }
            }
        }
        if(requestCode==UPDATENAME){
            if(resultCode==1){
                SharedPrefrenceUtils.setBoolean(UIUtils.getContext(), "isUpdate", true);
                tv_name.setText(data.getStringExtra("alias"));
                SharedPrefrenceUtils.setString(PersonalActivity.this, "alias", data.getStringExtra("alias"));
            }
        }
        if(requestCode==UPDATECITY){
            if(resultCode==1) {
                SharedPrefrenceUtils.setBoolean(UIUtils.getContext(), "isUpdate", true);
                tv_city.setText(data.getStringExtra("city"));
                SharedPrefrenceUtils.setString(PersonalActivity.this, "city", data.getStringExtra("city"));
            }
        }
        if(requestCode==UPDATELABLE){
            if(resultCode==1) {
                SharedPrefrenceUtils.setBoolean(UIUtils.getContext(), "isUpdate", true);
                tv_lable.setText(data.getStringExtra("signature"));
                SharedPrefrenceUtils.setString(PersonalActivity.this, "signature", data.getStringExtra("signature"));
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
    public void initData() {
        if (!ImageLoader.getInstance().isInited()) {
            ImageLoader.getInstance()
                    .init(ImageLoaderConfiguration.createDefault(UIUtils
                            .getContext()));
        }
        loadingDialog = DialogUtils.createLoadDialog(PersonalActivity.this, true);
        tv_top_back.setOnClickListener(this);
        rl_address.setOnClickListener(this);
        rl_city.setOnClickListener(this);
        rl_head_portrait.setOnClickListener(this);
        rl_name.setOnClickListener(this);
        rl_lable.setOnClickListener(this);
        runAsyncTask();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.rl_head_portrait:{
                pWindow.setAnimationStyle(R.style.AnimBottom);
                pWindow.showAtLocation(rootView.findViewById(R.id.ll_myself),
                        Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                break;
            }
            case R.id.rl_lable: {
                Intent intent=new Intent(PersonalActivity.this,UpdateLableActivity.class);
                UIUtils.startActivityForResultNextAnim(intent, UPDATELABLE);
                break;
            }
            case R.id.rl_name: {
                Intent intent=new Intent(PersonalActivity.this,UpdateNameActivity.class);
                UIUtils.startActivityForResultNextAnim(intent,UPDATENAME);
                break;
            }
            case R.id.rl_address: {
                Intent intent=new Intent(PersonalActivity.this,MyAddressActivity.class);
                UIUtils.startActivityNextAnim(intent);
                break;
            }
            case R.id.rl_city: {
                Intent intent=new Intent(PersonalActivity.this,CitySelectActivity.class);
                UIUtils.startActivityForResultNextAnim(intent, UPDATECITY);
                break;
            }
            case R.id.tv_top_back: {
                finish();
                overridePendingTransition(R.anim.animprv_in, R.anim.animprv_out);
                break;
            }
        }
    }

    public void runAsyncTask() {
        loadingDialog.show();
        GetUserInfoProtocol getUserInfoProtocol = new GetUserInfoProtocol();
        GetUserInfoRequest getUserInfoRequest = new GetUserInfoRequest();
        url = getUserInfoProtocol.getApiFun();
        getUserInfoRequest.map.put("user_id", SharedPrefrenceUtils.getString(PersonalActivity.this, "user_id"));


        MyVolley.uploadNoFile(MyVolley.POST, url, getUserInfoRequest.map, new MyVolley.VolleyCallback() {
            @Override
            public void dealWithJson(String address, String json) {

                Gson gson = new Gson();
                getUserInfoResponse = gson.fromJson(json, GetUserInfoResponse.class);
                LogUtils.e("sendVerificationCodeResponse:" + json.toString());
                if (getUserInfoResponse.code==0) {
                    loadingDialog.dismiss();
                    setDate(getUserInfoResponse);
                } else {
                    loadingDialog.dismiss();
                    DialogUtils.showAlertDialog(PersonalActivity.this,
                            getUserInfoResponse.resultText);
                }


            }

            @Override
            public void dealWithError(String address, String error) {
                loadingDialog.dismiss();
                DialogUtils.showAlertDialog(PersonalActivity.this, error);
            }
        });
    }

    /**
     * 修改头像
     */
    public void runUpdateUserInfo() {
        loadingDialog.show();
        UpdateUserInfoProtocol updateUserInfoProtocol = new UpdateUserInfoProtocol();
        UpdateUserInfoRequest updateUserInfoRequest = new UpdateUserInfoRequest();
        url = updateUserInfoProtocol.getApiFun();
        updateUserInfoRequest.map.put("user_id", SharedPrefrenceUtils.getString(PersonalActivity.this, "user_id"));
        updateUserInfoRequest.map.put("avatar",imageurl);
        MyVolley.uploadNoFile(MyVolley.POST, url, updateUserInfoRequest.map, new MyVolley.VolleyCallback() {
            @Override
            public void dealWithJson(String address, String json) {
                LogUtils.e("json:" + json);
                Gson gson = new Gson();
                loadingDialog.dismiss();
                UpdateUserInfoResponse updateUserInfoResponse = gson.fromJson(json, UpdateUserInfoResponse.class);
                if (updateUserInfoResponse.code.equals("0")) {
                    Bitmap photo = BitmapUtils.getSmallBitmap(path);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    photo.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    iv_image.setImageBitmap(photo);
                    SharedPrefrenceUtils.setBoolean(UIUtils.getContext(), "isUpdate", true);
                    SharedPrefrenceUtils.setString(PersonalActivity.this, "avatar", imageurl);
                } else {
                    DialogUtils.showAlertDialog(PersonalActivity.this,
                            updateUserInfoResponse.resultText);
                }
            }

            @Override
            public void dealWithError(String address, String error) {
                loadingDialog.dismiss();
                DialogUtils.showAlertDialog(PersonalActivity.this, error);
            }
        });
    }
    //结束回调，不可为空
    UpCompleteListener completeListener = new UpCompleteListener() {
        @Override
        public void onComplete(boolean isSuccess, String result) {
            LogUtils.e("上传图片result:"+result);
            try {
                if(isSuccess){
                    Gson gson = new Gson();
                    HashMap<String,String> hs = gson.fromJson(result,HashMap.class);
                    imageurl = MyUpyunUtils.UpyunBaseUrl+hs.get("url");
                    LogUtils.e("上传图片到有拍:"+ MyUpyunUtils.UpyunBaseUrl+url);
                    runUpdateUserInfo();
                }
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
            }
        }

    };
}
