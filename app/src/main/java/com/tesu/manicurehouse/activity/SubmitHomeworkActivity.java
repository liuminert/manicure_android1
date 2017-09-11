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
import android.support.v4.view.PagerAdapter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.tesu.manicurehouse.R;
import com.tesu.manicurehouse.adapter.HomeworkImageAdapter;
import com.tesu.manicurehouse.adapter.SubmitGoodCommentAdapter;
import com.tesu.manicurehouse.base.BaseActivity;
import com.tesu.manicurehouse.base.MyVolley;
import com.tesu.manicurehouse.protocol.SubmitVideoCommentProtocol;
import com.tesu.manicurehouse.request.SubmitVideoCommentRequest;
import com.tesu.manicurehouse.response.SubmitVideoCommentResponse;
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

import cn.sharesdk.framework.ShareSDK;

/**
 * 类描述：
 * 创建人：许明达
 * 创建时间：2016/8/16 11:40
 * 交作业
 */
public class SubmitHomeworkActivity extends BaseActivity implements View.OnClickListener{

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
    private static final int DESCRIPTION_RESOULT = 3;// 结果

    private static final String IMAGE_UNSPECIFIED = "image/*";
    private String path;
    private List<Bitmap> bitmapList;
    private View rootView;
    //	可能有多个，暂定为json数组，格式:[”图片url”,’图片url”,”图片url”]
    private List<String> pics=new ArrayList<String>();
    private List<String> upload_path_lists=new ArrayList<String>();
    private Dialog loadingDialog;
    private String url;
    private String video_id;
    private String content;
    private TextView tv_submit;
    private EditText et_tips;
    private File mFile;
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 1;
    // 图片储存成功后
    protected static final int INTERCEPT = 3;
    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case INTERCEPT:
                    if(homeworkImageAdapter==null){
                        homeworkImageAdapter=new HomeworkImageAdapter(SubmitHomeworkActivity.this,bitmapList,pWindow,rootView.findViewById(R.id.ll_main),pics,1);
                        gv_image.setAdapter(homeworkImageAdapter);
                    }
                    else{
                        homeworkImageAdapter.setDate(bitmapList,pics);
                        homeworkImageAdapter.notifyDataSetChanged();
                    }
                    break;

            }
        }
    };

    @Override
    protected View initView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        rootView = View.inflate(this, R.layout.activity_submit_homework, null);
        setContentView(rootView);
        et_tips=(EditText)rootView.findViewById(R.id.et_tips);
        gv_image=(GridView)rootView.findViewById(R.id.gv_image);
        tv_submit=(TextView)rootView.findViewById(R.id.tv_submit);
        iv_top_back=(ImageView)rootView.findViewById(R.id.iv_top_back);
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
                        if (ContextCompat.checkSelfPermission(SubmitHomeworkActivity.this,
                                Manifest.permission.CAMERA)
                                != PackageManager.PERMISSION_GRANTED)
                        {

                            ActivityCompat.requestPermissions(SubmitHomeworkActivity.this,
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
                Toast.makeText(SubmitHomeworkActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
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
                new Thread() {
                    public void run() {
                        try {
                            Bitmap photo = BitmapUtils.getSmallBitmap(path);
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            photo.compress(Bitmap.CompressFormat.JPEG, 50, baos);
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
                //通知相册刷新
                Uri uriData = Uri.parse("file://" + path);
                UIUtils.getContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uriData));
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
                    uri = BitmapUtils.getPictureUri(data, SubmitHomeworkActivity                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               .this);
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
//                pics.add(path);
////                MyUpyunUtils.upload(path, completeListener);
//                Bitmap photo = BitmapUtils.getSmallBitmap(path);
//                ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                photo.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//                bitmapList.add(photo);
//                if(homeworkImageAdapter==null){
//                    homeworkImageAdapter=new HomeworkImageAdapter(SubmitHomeworkActivity.this,bitmapList,pWindow,rootView.findViewById(R.id.ll_main),pics,1);
//                    gv_image.setAdapter(homeworkImageAdapter);
//                }
//                else{
//                    homeworkImageAdapter.setDate(bitmapList,pics);
//                    homeworkImageAdapter.notifyDataSetChanged();
//                }

                    new Thread() {
                        public void run() {
                            try {
                                Bitmap photo = BitmapUtils.getSmallBitmap(path);
                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                photo.compress(Bitmap.CompressFormat.JPEG, 50, baos);
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
        super.onActivityResult(requestCode, resultCode, data);
    }
    public void initData(){
        //添加第一个按钮的空地址，防止错误
        pics.add(null);
        video_id=getIntent().getStringExtra("video_id");
        loadingDialog = DialogUtils.createLoadDialog(SubmitHomeworkActivity.this, true);
        bitmapList=new ArrayList<Bitmap>();
        bitmapList.add(null);
        if(homeworkImageAdapter==null){
            homeworkImageAdapter=new HomeworkImageAdapter(SubmitHomeworkActivity.this,bitmapList,pWindow,rootView.findViewById(R.id.ll_main),pics,1);
            gv_image.setAdapter(homeworkImageAdapter);
        }
        iv_top_back.setOnClickListener(this);
        tv_submit.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_submit:{
                content=et_tips.getText().toString();
                if(!TextUtils.isEmpty(content)&&pics.size()>1){
                    //除掉第一项
                    pics.remove(0);
                    for(int i=0;i<pics.size();i++){
                        loadingDialog.show();
                        MyUpyunUtils.upload(pics.get(i), completeListener);
                    }
                }
                else{
                    if(TextUtils.isEmpty(content)) {
                        DialogUtils.showAlertDialog(SubmitHomeworkActivity.this,
                                "请先输入您的心得！");
                    }
                    else{
                        DialogUtils.showAlertDialog(SubmitHomeworkActivity.this,
                                "请选择一张照片！");
                    }
                }
                break;
            }
            case R.id.iv_top_back:
                finish();
                overridePendingTransition(R.anim.animprv_in, R.anim.animprv_out);
                break;
        }
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
                    upload_path_lists.add(MyUpyunUtils.UpyunBaseUrl + hs.get("url"));
                    runSubmitVideoComment();
                }
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
            }
        }

    };

    //评论视频
    public void runSubmitVideoComment() {
        Gson gson=new Gson();
        SubmitVideoCommentProtocol submitVideoCommentProtocol = new SubmitVideoCommentProtocol();
        SubmitVideoCommentRequest submitVideoCommentRequest = new SubmitVideoCommentRequest();
        url = submitVideoCommentProtocol.getApiFun();
        submitVideoCommentRequest.map.put("user_id", SharedPrefrenceUtils.getString(SubmitHomeworkActivity.this, "user_id"));
        submitVideoCommentRequest.map.put("video_id",video_id);
        submitVideoCommentRequest.map.put("content", content);
        LogUtils.e("上传:" + upload_path_lists.size() + "    " + upload_path_lists.get(0));
        submitVideoCommentRequest.map.put("pics",gson.toJson(upload_path_lists));

        MyVolley.uploadNoFile(MyVolley.POST, url, submitVideoCommentRequest.map, new MyVolley.VolleyCallback() {
            @Override
            public void dealWithJson(String address, String json) {
                LogUtils.e("json:" + json);
                Gson gson = new Gson();
                SubmitVideoCommentResponse submitVideoCommentResponse = gson.fromJson(json, SubmitVideoCommentResponse.class);
                if (submitVideoCommentResponse.code.equals("0")) {
                    loadingDialog.dismiss();
                    Intent intent = getIntent();
                    setResult(2, intent);
                    finish();
                    overridePendingTransition(R.anim.animprv_in, R.anim.animprv_out);
                } else {
                    loadingDialog.dismiss();
                    DialogUtils.showAlertDialog(SubmitHomeworkActivity.this,
                            submitVideoCommentResponse.resultText);
                }

            }

            @Override
            public void dealWithError(String address, String error) {
                loadingDialog.dismiss();
                DialogUtils.showAlertDialog(SubmitHomeworkActivity.this, error);
            }
        });
    }
}
