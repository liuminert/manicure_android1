package com.tesu.manicurehouse.activity;

import android.Manifest;
import android.app.ActionBar;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.tesu.manicurehouse.R;
import com.tesu.manicurehouse.adapter.HomeworkImageAdapter;
import com.tesu.manicurehouse.base.BaseActivity;
import com.tesu.manicurehouse.base.MyVolley;
import com.tesu.manicurehouse.protocol.AuthenticateProtocol;
import com.tesu.manicurehouse.request.AuthenticateRequest;
import com.tesu.manicurehouse.response.AuthenticateResponse;
import com.tesu.manicurehouse.util.BitmapUtils;
import com.tesu.manicurehouse.util.DialogUtils;
import com.tesu.manicurehouse.util.LogUtils;
import com.tesu.manicurehouse.util.MyUpyunUtils;
import com.tesu.manicurehouse.util.SharedPrefrenceUtils;
import com.tesu.manicurehouse.util.UIUtils;
import com.upyun.library.listener.UpCompleteListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * 类描述：
 * 创建人：许明达
 * 创建时间：2016/8/16 11:40
 * 美甲师认证页面
 */
public class ManicuristCertificationActivity extends BaseActivity implements View.OnClickListener {

    private TextView tv_top_back;
    private View rootView;
    private String url;
    private Dialog loadingDialog;
    private AuthenticateResponse authenticateResponse;
    private String timepath;
    /**
     * 照片参数
     */
    private static final int PHOTO_GRAPH = 1;// 拍照
    private static final int PHOTO_ZOOM = 2; // 缩放
    private static final int DESCRIPTION_RESOULT = 3;// 结果

    private static final String IMAGE_UNSPECIFIED = "image/*";
    private String path;
    /**
     * 照相选择界面
     */
    private PopupWindow pWindow;
    private View root;
    private LayoutInflater mInflater;
    private TextView tv_submit_front_license;
    private TextView tv_submit_behond_license;
    private ImageView iv_front_license;
    private ImageView iv_behond_license;
    private boolean front = false;
    private boolean behond = false;
    private String pic1;
    private String pic2;
    private Button btn_comit;
    private File mFile;
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 1;
    @Override
    protected View initView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        rootView = View.inflate(this, R.layout.activity_manicurist_certification, null);
        setContentView(rootView);
        tv_top_back = (TextView) rootView.findViewById(R.id.tv_top_back);
        tv_submit_front_license = (TextView) rootView.findViewById(R.id.tv_submit_front_license);
        tv_submit_behond_license = (TextView) rootView.findViewById(R.id.tv_submit_behond_license);
        iv_front_license = (ImageView) rootView.findViewById(R.id.iv_front_license);
        iv_behond_license = (ImageView) rootView.findViewById(R.id.iv_behond_license);
        btn_comit = (Button) rootView.findViewById(R.id.btn_comit);
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
                        mFile = new File(dir, timepath + ".jpg");
                        if (ContextCompat.checkSelfPermission(ManicuristCertificationActivity.this,
                                Manifest.permission.CAMERA)
                                != PackageManager.PERMISSION_GRANTED)
                        {

                            ActivityCompat.requestPermissions(ManicuristCertificationActivity.this,
                                    new String[]{Manifest.permission.CAMERA},
                                    MY_PERMISSIONS_REQUEST_CAMERA);
                        }else {
                            startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(
                                    MediaStore.EXTRA_OUTPUT, Uri.fromFile(mFile)), PHOTO_GRAPH);
                        }
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
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {

        if (requestCode == MY_PERMISSIONS_REQUEST_CAMERA)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(
                        MediaStore.EXTRA_OUTPUT, Uri.fromFile(mFile)), PHOTO_GRAPH);
            } else
            {
                // Permission Denied
                Toast.makeText(ManicuristCertificationActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void initData() {
        loadingDialog = DialogUtils.createLoadDialog(ManicuristCertificationActivity.this, true);
        tv_top_back.setOnClickListener(this);
        tv_submit_front_license.setOnClickListener(this);
        tv_submit_behond_license.setOnClickListener(this);
        btn_comit.setOnClickListener(this);
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
                loadingDialog.show();
                Bitmap photo = BitmapUtils.getSmallBitmap(path);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                photo.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                Bitmap new_photo=BitmapUtils.rotateBitmapByDegree(photo,BitmapUtils.getBitmapDegree(path));
                String suoName = new SimpleDateFormat("yyyyMMdd_HHmmss")
                        .format(new Date());
                try {
                    path=BitmapUtils.saveMyBitmap(suoName, new_photo);
                    MyUpyunUtils.upload(path, completeListener);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (front) {
                    iv_front_license.setImageBitmap(new_photo);
                }
                if (behond) {
                    iv_behond_license.setImageBitmap(new_photo);
                }
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
                    uri = BitmapUtils.getPictureUri(data, ManicuristCertificationActivity.this);
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
                    loadingDialog.show();
                    Bitmap photo = BitmapUtils.getSmallBitmap(path);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    photo.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    Bitmap new_photo=BitmapUtils.rotateBitmapByDegree(photo,BitmapUtils.getBitmapDegree(path));
                    String suoName = new SimpleDateFormat("yyyyMMdd_HHmmss")
                            .format(new Date());
                    try {
                        path=BitmapUtils.saveMyBitmap(suoName, new_photo);
                        MyUpyunUtils.upload(path, completeListener);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (front) {
                        iv_front_license.setImageBitmap(new_photo);
                    }
                    if (behond) {
                        iv_behond_license.setImageBitmap(new_photo);
                    }
                }
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
    //结束回调，不可为空
    UpCompleteListener completeListener = new UpCompleteListener() {
        @Override
        public void onComplete(boolean isSuccess, String result) {
            loadingDialog.dismiss();
            LogUtils.e("上传图片result:" + result);
            try {
                if(isSuccess){
                    Gson gson = new Gson();
                    HashMap<String,String> hs = gson.fromJson(result,HashMap.class);
                    if (front) {
                        pic1 = MyUpyunUtils.UpyunBaseUrl + hs.get("url");
                    }
                    if (behond) {
                        pic2 = MyUpyunUtils.UpyunBaseUrl + hs.get("url");
                    }
                }else {
                    Toast.makeText(ManicuristCertificationActivity.this, "上传图片到又拍云失败", Toast.LENGTH_LONG).show();
                }
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
            }
        }

    };
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_submit_behond_license:
                front = false;
                behond = true;
                pWindow.setAnimationStyle(R.style.AnimBottom);
                pWindow.showAtLocation(rootView.findViewById(R.id.rl_content),
                        Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                break;
            case R.id.tv_submit_front_license:
                front = true;
                behond = false;
                pWindow.setAnimationStyle(R.style.AnimBottom);
                pWindow.showAtLocation(rootView.findViewById(R.id.rl_content),
                        Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                break;
            case R.id.tv_top_back:
                finish();
                overridePendingTransition(R.anim.animprv_in, R.anim.animprv_out);
                break;
            case R.id.btn_comit:
                if (!TextUtils.isEmpty(pic1) && !TextUtils.isEmpty(pic2)) {
                    runAsyncTask();
                } else {
                    if (TextUtils.isEmpty(pic1)) {
                        DialogUtils.showAlertDialog(ManicuristCertificationActivity.this,
                                "请选择好身份证正面先！");
                    } else {
                        DialogUtils.showAlertDialog(ManicuristCertificationActivity.this,
                                "请选择好身份证反面先！");
                    }
                }
                break;
            case R.id.tv_roger:{
                finish();
                overridePendingTransition(R.anim.animprv_in, R.anim.animprv_out);
                break;
            }
        }
    }

    public void runAsyncTask() {
        loadingDialog.show();
        AuthenticateProtocol authenticateProtocol = new AuthenticateProtocol();
        AuthenticateRequest authenticateRequest = new AuthenticateRequest();
        url = authenticateProtocol.getApiFun();
        authenticateRequest.map.put("user_id", SharedPrefrenceUtils.getString(ManicuristCertificationActivity.this, "user_id"));
        authenticateRequest.map.put("authentication_type", "0");
        authenticateRequest.map.put("pic1", pic1);
        authenticateRequest.map.put("pic2", pic2);

        MyVolley.uploadNoFile(MyVolley.POST, url, authenticateRequest.map, new MyVolley.VolleyCallback() {
            @Override
            public void dealWithJson(String address, String json) {

                Gson gson = new Gson();
                authenticateResponse = gson.fromJson(json, AuthenticateResponse.class);
                LogUtils.e("sendVerificationCodeResponse:" + json.toString());
                if (authenticateResponse.code.equals("0")) {
                    loadingDialog.dismiss();
                    front = false;
                    behond = false;
                    DialogUtils.showAlertDialog(ManicuristCertificationActivity.this,
                            "提交成功!", ManicuristCertificationActivity.this);
                } else {
                    loadingDialog.dismiss();
                    DialogUtils.showAlertDialog(ManicuristCertificationActivity.this,
                            authenticateResponse.resultText);
                }


            }

            @Override
            public void dealWithError(String address, String error) {
                loadingDialog.dismiss();
                DialogUtils.showAlertDialog(ManicuristCertificationActivity.this, error);
            }
        });
    }
}
