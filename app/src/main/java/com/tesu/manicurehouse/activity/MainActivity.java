package com.tesu.manicurehouse.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.tesu.manicurehouse.R;
import com.tesu.manicurehouse.base.BaseActivity;
import com.tesu.manicurehouse.base.MyVolley;
import com.tesu.manicurehouse.fragment.ControlTabFragment;
import com.tesu.manicurehouse.protocol.GetAppVersionInfoProtocol;
import com.tesu.manicurehouse.protocol.UpdateUserInfoProtocol;
import com.tesu.manicurehouse.request.GetAppVersionInfoRequest;
import com.tesu.manicurehouse.request.UpdateUserInfoRequest;
import com.tesu.manicurehouse.response.GetAppVersionInfoResponse;
import com.tesu.manicurehouse.response.UpdateUserInfoResponse;
import com.tesu.manicurehouse.util.BitmapUtils;
import com.tesu.manicurehouse.util.Constants;
import com.tesu.manicurehouse.util.DialogUtils;
import com.tesu.manicurehouse.util.LogUtils;
import com.tesu.manicurehouse.util.MyUpyunUtils;
import com.tesu.manicurehouse.util.SharedPrefrenceUtils;
import com.tesu.manicurehouse.util.StringUtils;
import com.tesu.manicurehouse.util.UIUtils;
import com.tesu.manicurehouse.util.Utils;
import com.upyun.library.listener.UpCompleteListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;


public class MainActivity extends BaseActivity {

    public static boolean isForeground = false;
    public static ControlTabFragment ctf;
    private String path;
    public static String timepath;
    /**
     * 照片参数
     */
    private static final int PHOTO_GRAPH = 4;// 拍照
    private static final int PHOTO_ZOOM = 5; // 缩放
    private static final int DESCRIPTION_RESOULT = 6;// 结果

    private static final String IMAGE_UNSPECIFIED = "image/*";
    private String imageurl;
    private String url;
    private Dialog loadingDialog;

    @Override
    protected View initView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        View rootView = View.inflate(this, R.layout.activity_main, null);
        setContentView(rootView);

        initFragment();
        loadingDialog = DialogUtils.createLoadDialog(MainActivity.this, true);
        runClientConfigAsyncTask();
        return rootView;
    }

    private void initFragment() {
        FragmentManager fm = getSupportFragmentManager();
        // 1. 开启事务
        FragmentTransaction transaction = fm.beginTransaction();
        // 添加主页fragment
        ctf = new ControlTabFragment();
        transaction.replace(R.id.main_container, ctf);
        transaction.commit();

    }

    public static ControlTabFragment getCtf() {
        return ctf;
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // 监听返回键
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            onKeyDownBack();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPrefrenceUtils.setString(this, "currentIndex", "0");
    }

    @Override
    protected void onResume() {
        LogUtils.e("onResume");
        String string = SharedPrefrenceUtils.getString(this, "currentIndex");
        if (!TextUtils.isEmpty(string)) {
            ctf.mCurrentIndex = Integer.parseInt(string);
            ctf.setChecked(ctf.mCurrentIndex);
        }
        isForeground = true;
        super.onResume();
    }

    @Override
    protected void onPause() {
        isForeground = false;
        super.onPause();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PHOTO_GRAPH) {
            // 设置文件保存路径
            File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            path = dir + "/" + timepath + ".jpg";
            File file = new File(path);
            if (file.exists()) {
                try {
                    Bitmap photo = BitmapUtils.getSmallBitmap(path);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    photo.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    Bitmap new_photo=BitmapUtils.rotateBitmapByDegree(photo,BitmapUtils.getBitmapDegree(path));
                    String suoName = new SimpleDateFormat("yyyyMMdd_HHmmss")
                            .format(new Date());
                    MyUpyunUtils.upload(BitmapUtils.saveMyBitmap(suoName, new_photo), completeListener);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            //通知相册刷新
            Uri uriData = Uri.parse("file://" + path);
            UIUtils.getContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uriData));
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
                    uri = BitmapUtils.getPictureUri(data, MainActivity.this);
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
                    new Thread() {
                        public void run() {
                            try {
                                Bitmap photo = BitmapUtils.getSmallBitmap(path);
                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                photo.compress(Bitmap.CompressFormat.JPEG, 50, baos);
                                Bitmap new_photo=BitmapUtils.rotateBitmapByDegree(photo,BitmapUtils.getBitmapDegree(path));
                                String suoName = new SimpleDateFormat("yyyyMMdd_HHmmss")
                                        .format(new Date());
                                MyUpyunUtils.upload(BitmapUtils.saveMyBitmap(suoName, new_photo), completeListener);
                            } catch (IOException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                    }.start();

                }
            }

        }
        if (requestCode == 1) {
            SharedPrefrenceUtils.setBoolean(UIUtils.getContext(), "isUpdate", true);
            ctf.getTabMyselfbyPager().initData();
        } else if (requestCode == 2) {
            if (resultCode == 1) {
                int num = data.getIntExtra("num", -1);
                ctf.getTabShopPager().setShopingNum(num);
            }
        } else if (requestCode == 3) {
            ctf.getTabShopPager().getCarDate();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //结束回调，不可为空
    UpCompleteListener completeListener = new UpCompleteListener() {
        @Override
        public void onComplete(boolean isSuccess, String result) {
            LogUtils.e("上传图片result:"+result);
            try {
                if (isSuccess) {
                    Gson gson = new Gson();
                    HashMap<String, String> hs = gson.fromJson(result, HashMap.class);
                    imageurl = MyUpyunUtils.UpyunBaseUrl + hs.get("url");
    //                LogUtils.e("上传图片到有拍:" + MyUpyunUtils.UpyunBaseUrl + url);
                    runUpdateUserInfo();
                }
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
            }
        }

    };

    /**
     * 修改头像
     */
    public void runUpdateUserInfo() {
        loadingDialog.show();
        UpdateUserInfoProtocol updateUserInfoProtocol = new UpdateUserInfoProtocol();
        UpdateUserInfoRequest updateUserInfoRequest = new UpdateUserInfoRequest();
        url = updateUserInfoProtocol.getApiFun();
        updateUserInfoRequest.map.put("user_id", SharedPrefrenceUtils.getString(MainActivity.this, "user_id"));
        updateUserInfoRequest.map.put("avatar", imageurl);
        MyVolley.uploadNoFile(MyVolley.POST, url, updateUserInfoRequest.map, new MyVolley.VolleyCallback() {
            @Override
            public void dealWithJson(String address, String json) {
                LogUtils.e("json:" + json);
                Gson gson = new Gson();
                loadingDialog.dismiss();
                UpdateUserInfoResponse updateUserInfoResponse = gson.fromJson(json, UpdateUserInfoResponse.class);
                if (updateUserInfoResponse.code.equals("0")) {
                    SharedPrefrenceUtils.setString(MainActivity.this, "avatar", imageurl);
                    SharedPrefrenceUtils.setBoolean(UIUtils.getContext(), "isUpdate", true);
                    ctf.getTabMyselfbyPager().initData();
                } else {
                    DialogUtils.showAlertDialog(MainActivity.this,
                            updateUserInfoResponse.resultText);
                }
            }

            @Override
            public void dealWithError(String address, String error) {
                loadingDialog.dismiss();
                DialogUtils.showAlertDialog(MainActivity.this, error);
            }
        });
    }

    @Override
    protected void onRestart() {
        LogUtils.e("onRestart");
        String string = SharedPrefrenceUtils.getString(this, "currentIndex");
        if (!TextUtils.isEmpty(string)) {
            ctf.mCurrentIndex = Integer.parseInt(string);
            ctf.setChecked(ctf.mCurrentIndex);
        }
        super.onRestart();
    }

    public void runClientConfigAsyncTask() {

        GetAppVersionInfoProtocol getAppVersionInfoProtocol = new GetAppVersionInfoProtocol();
        GetAppVersionInfoRequest getAppVersionInfoRequest = new GetAppVersionInfoRequest();
        getAppVersionInfoRequest.map.put("platform", "android");
        String url = Constants.SERVER_URL +  getAppVersionInfoProtocol.getApiFun();
        MyVolley.uploadNoFileWholeUrl(MyVolley.POST, url, getAppVersionInfoRequest.map, new MyVolley.VolleyCallback() {
            @Override
            public void dealWithJson(String address, String json) {
                try {
                    Gson gson = new Gson();
                    GetAppVersionInfoResponse getAppVersionInfoResponse = gson.fromJson(json, GetAppVersionInfoResponse.class);
                    LogUtils.e("更新" + json);
                    if (getAppVersionInfoResponse.code == 0) {
//                        boolean shouldUpdate = false;
//                        if (getAppVersionInfoResponse.data.force_update > 0) {
//                            shouldUpdate = true;
//                        }
//                        if (shouldUpdate) {
                        if ( Integer.parseInt(cutStringToInt(getAppVersionInfoResponse.data.newest_version, ".")) >  Integer.parseInt(cutStringToInt(Utils.getVersionName(MainActivity.this),"."))) {
                            if (getAppVersionInfoResponse.data.force_update == 0) {
                                DialogUtils.showUpdateDialog(MainActivity.this, "存在新版本", getAppVersionInfoResponse.data.download_url, false);
                            } else {
                                DialogUtils.showUpdateDialog(MainActivity.this, "存在新版本", getAppVersionInfoResponse.data.download_url, true);
                            }
                        }
//                        }
                    } else {
                        DialogUtils.showAlertDialog(MainActivity.this, getAppVersionInfoResponse.msg);
                    }
                } catch (JsonSyntaxException e) {

                }


            }

            @Override
            public void dealWithError(String address, String error) {
                DialogUtils.showAlertDialog(MainActivity.this, error);
            }
        });
    }

    /**
     * 去掉“.”
     */
    public String cutStringToInt(String str, String cut) {
        String result = str.replace(cut, "");
        return result.trim();
    }
}
