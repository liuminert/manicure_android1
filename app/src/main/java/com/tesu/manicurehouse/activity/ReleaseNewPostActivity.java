package com.tesu.manicurehouse.activity;

import android.app.ActionBar;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.tesu.manicurehouse.R;
import com.tesu.manicurehouse.adapter.HomeworkImageAdapter;
import com.tesu.manicurehouse.base.BaseActivity;
import com.tesu.manicurehouse.base.MyVolley;
import com.tesu.manicurehouse.bean.PhotoUpImageItem;
import com.tesu.manicurehouse.protocol.AddPostProtocol;
import com.tesu.manicurehouse.protocol.UpdateUserInfoProtocol;
import com.tesu.manicurehouse.request.AddPostRequest;
import com.tesu.manicurehouse.request.UpdateUserInfoRequest;
import com.tesu.manicurehouse.response.AddPostResponse;
import com.tesu.manicurehouse.response.GetPostListResponse;
import com.tesu.manicurehouse.response.UpdateUserInfoResponse;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * 类描述：
 * 创建人：许明达
 * 创建时间：2016/8/16 11:40
 * 发布新帖
 */
public class ReleaseNewPostActivity extends BaseActivity implements View.OnClickListener {

    private ImageView iv_top_back;
    private GridView gv_image;
    private HomeworkImageAdapter homeworkImageAdapter;
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
    private static final int CHOOSELOCALIMAGE = 4;// 结果

    private static final String IMAGE_UNSPECIFIED = "image/*";
    private String path;
    private List<Bitmap> bitmapList;
    private View rootView;
    private String url;
    private Dialog loadingDialog;
    private AddPostResponse addPostResponse;
    //标题
    private String title;
    //内容
    private String content;
    //是否测评0否，1是
    private int set_check;
    //	可能有多个，暂定为json数组，格式:[图片url,’图片url”,”图片url”]
    private List<String> pics = new ArrayList<String>();
    private TextView tv_submit;
    private EditText et_title;
    private EditText et_content;
    private List<String> select_pics = new ArrayList<String>();
    private String selectImageStr;
    private ArrayList<PhotoUpImageItem> selectImages;
    private List<String> upload_path_lists = new ArrayList<String>();
    //0个人，1美甲师
    private int type;
    // 图片储存成功后
    protected static final int INTERCEPT = 1;
    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case INTERCEPT:
                    if (homeworkImageAdapter == null) {
                        homeworkImageAdapter = new HomeworkImageAdapter(ReleaseNewPostActivity.this, bitmapList, pWindow, rootView.findViewById(R.id.ll_main), pics, 6);
                        gv_image.setAdapter(homeworkImageAdapter);
                    } else {
                        homeworkImageAdapter.notifyDataSetChanged();
                    }
                    break;

            }
        }
    };

    @Override
    protected View initView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        rootView = View.inflate(this, R.layout.activity_release_new_post, null);
        setContentView(rootView);
        gv_image = (GridView) rootView.findViewById(R.id.gv_image);
        iv_top_back = (ImageView) rootView.findViewById(R.id.iv_top_back);
        tv_submit = (TextView) rootView.findViewById(R.id.tv_submit);
        et_title = (EditText) rootView.findViewById(R.id.et_title);
        et_content = (EditText) rootView.findViewById(R.id.et_content);
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
        iv_top_back.setOnClickListener(this);
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
                            dir.mkdirs();
                        }
                        path = dir + File.separator + timepath + ".jpg";
                        File file = new File(path);
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
//                    Intent intent = new Intent(ReleaseNewPostActivity.this, PostChooseFolderActivity.class);
//                    UIUtils.startActivityForResult(intent, CHOOSELOCALIMAGE);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 拍照
        if (requestCode == PHOTO_GRAPH) {
            // 设置文件保存路径
            File file = new File(path);
            if (file.exists()) {

                new Thread() {
                    public void run() {
                        try {
                            Bitmap photo = BitmapUtils.getSmallBitmap(path);
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            photo.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                            Bitmap new_photo=BitmapUtils.rotateBitmapByDegree(photo,BitmapUtils.getBitmapDegree(path));
                            bitmapList.add(new_photo);
                            String suoName = new SimpleDateFormat("yyyyMMdd_HHmmss")
                                    .format(new Date());
                            pics.add(BitmapUtils.saveMyBitmap(suoName, new_photo));
                            handler.sendEmptyMessage(INTERCEPT);
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }

                    ;
                }.start();
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
                    uri = BitmapUtils.getPictureUri(data, ReleaseNewPostActivity.this);
                    cursor = managedQuery(uri, proj, null, null, null);
                }
                // 获取索引
                int photocolumn = cursor
                        .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                // 将光标一直开头
                cursor.moveToFirst();
                // 根据索引值获取图片路径
                path = cursor.getString(photocolumn);
                LogUtils.e("相册路径:"+path);
                if (!TextUtils.isEmpty(path)) {
//                MyUpyunUtils.upload(path, completeListener);
//                pics.add(path);
                    new Thread() {
                        public void run() {
                            try {
                                Bitmap photo = BitmapUtils.getSmallBitmap(path);
                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                photo.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                                Bitmap new_photo=BitmapUtils.rotateBitmapByDegree(photo,BitmapUtils.getBitmapDegree(path));
                                bitmapList.add(new_photo);
                                String suoName = new SimpleDateFormat("yyyyMMdd_HHmmss")
                                        .format(new Date());
                                pics.add(BitmapUtils.saveMyBitmap(suoName, new_photo));
                                handler.sendEmptyMessage(INTERCEPT);

                            } catch (IOException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                    }.start();
                }
            }
        }

        if (requestCode == CHOOSELOCALIMAGE) {
            if (resultCode == 200) {
                selectImageStr = data.getStringExtra("selectImages");
                selectImages = new Gson().fromJson(selectImageStr, new TypeToken<List<PhotoUpImageItem>>() {
                }.getType());
                LogUtils.e("selectImages:" + selectImages.size());
                new Thread() {
                    public void run() {
                        try {
                            for (int i = 0; i < selectImages.size(); i++) {
                                Bitmap photo = BitmapUtils.getSmallBitmap(selectImages.get(i).getImagePath());
                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                photo.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                                Bitmap new_photo=BitmapUtils.rotateBitmapByDegree(photo,BitmapUtils.getBitmapDegree(path));
                                bitmapList.add(new_photo);

                                String suoName = new SimpleDateFormat("yyyyMMdd_HHmmss")
                                        .format(new Date())+String.valueOf(i);
                                pics.add(BitmapUtils.saveMyBitmap(suoName, new_photo));
                            }
                            handler.sendEmptyMessage(INTERCEPT);
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }

                    ;
                }.start();

            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void initData() {
        Intent intent = getIntent();
        set_check= intent.getIntExtra("set_check", 0);
        type = intent.getIntExtra("type", -1);
        loadingDialog = DialogUtils.createLoadDialog(ReleaseNewPostActivity.this, true);
        bitmapList = new ArrayList<Bitmap>();
        bitmapList.add(null);
        pics.add(null);
        if (homeworkImageAdapter == null) {
            homeworkImageAdapter = new HomeworkImageAdapter(ReleaseNewPostActivity.this, bitmapList, pWindow, rootView.findViewById(R.id.ll_main), pics, 6);
            gv_image.setAdapter(homeworkImageAdapter);
        }
        tv_submit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_submit:
                loadingDialog.show();
                pics.remove(0);
                title = et_title.getText().toString();
                content = et_content.getText().toString();
                if(pics.size()>0){
                    for (int i = 0; i < pics.size(); i++) {
                        LogUtils.e("地址:"+pics.get(i));
                        MyUpyunUtils.upload(pics.get(i), completeListener);
                    }
                }else{
                    runAddPost();
                }

                break;
            case R.id.iv_top_back:
                finish();
                overridePendingTransition(R.anim.animprv_in, R.anim.animprv_out);
                break;
        }
    }

    public void runAddPost() {
        Gson gson = new Gson();
        AddPostProtocol addPostProtocol = new AddPostProtocol();
        AddPostRequest addPostRequest = new AddPostRequest();
        url = addPostProtocol.getApiFun();
        addPostRequest.map.put("user_id", SharedPrefrenceUtils.getString(ReleaseNewPostActivity.this, "user_id"));
        addPostRequest.map.put("title", title);
        addPostRequest.map.put("content", content);
        addPostRequest.map.put("set_check", String.valueOf(set_check));
        if(pics.size()>0) {
            addPostRequest.map.put("pics", gson.toJson(upload_path_lists));
        }
        else{
            addPostRequest.map.put("pics", "");
        }
        addPostRequest.map.put("type", String.valueOf(type));
        LogUtils.e("set_check"+set_check);
        MyVolley.uploadNoFile(MyVolley.POST, url, addPostRequest.map, new MyVolley.VolleyCallback() {
            @Override
            public void dealWithJson(String address, String json) {
                LogUtils.e("json:" + json);
                Gson gson = new Gson();
                addPostResponse = gson.fromJson(json, AddPostResponse.class);
                if (addPostResponse.code.equals("0")) {
                    loadingDialog.dismiss();
                    Intent intent = getIntent();
                    setResult(1, intent);
                    finish();
                    overridePendingTransition(R.anim.animprv_in, R.anim.animprv_out);
                } else {
                    loadingDialog.dismiss();
                    DialogUtils.showAlertDialog(ReleaseNewPostActivity.this,
                            addPostResponse.resultText);
                }
            }

            @Override
            public void dealWithError(String address, String error) {
                loadingDialog.dismiss();
                DialogUtils.showAlertDialog(ReleaseNewPostActivity.this, error);
            }
        });
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
                    upload_path_lists.add(MyUpyunUtils.UpyunBaseUrl + hs.get("url"));
                    if (upload_path_lists.size() == pics.size()) {
                        runAddPost();
                    }
                    LogUtils.e("上传图片到有拍:" + MyUpyunUtils.UpyunBaseUrl + url);
                }
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
            }
        }

    };
}
