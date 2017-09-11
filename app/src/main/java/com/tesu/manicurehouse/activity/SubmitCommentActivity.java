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
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.tesu.manicurehouse.R;
import com.tesu.manicurehouse.adapter.GoodCommentImageAdapter;
import com.tesu.manicurehouse.adapter.HomeworkImageAdapter;
import com.tesu.manicurehouse.adapter.SubmitGoodCommentAdapter;
import com.tesu.manicurehouse.base.BaseActivity;
import com.tesu.manicurehouse.base.MyVolley;
import com.tesu.manicurehouse.bean.GoodsDataBean;
import com.tesu.manicurehouse.callback.OnCallBackContentList;
import com.tesu.manicurehouse.protocol.SubmitGoodCommentProtocol;
import com.tesu.manicurehouse.protocol.UpdateOrderStatusProtocol;
import com.tesu.manicurehouse.request.SubmitGoodCommentRequest;
import com.tesu.manicurehouse.request.UpdateOrderStatusRequest;
import com.tesu.manicurehouse.response.SubmitGoodCommentResponse;
import com.tesu.manicurehouse.response.UpdateOrderStatusResponse;
import com.tesu.manicurehouse.util.BitmapUtils;
import com.tesu.manicurehouse.util.DialogUtils;
import com.tesu.manicurehouse.util.LogUtils;
import com.tesu.manicurehouse.util.MyUpyunUtils;
import com.tesu.manicurehouse.util.SharedPrefrenceUtils;
import com.tesu.manicurehouse.util.UIUtils;
import com.upyun.library.listener.UpCompleteListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
 * 评论
 */
public class SubmitCommentActivity extends BaseActivity implements View.OnClickListener, OnCallBackContentList {

    private ListView lv_good_comment;
    private ImageView iv_top_back;
    private GoodCommentImageAdapter goodCommentImageAdapter;
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
    //	可能有多个，暂定为json数组，格式:[”图片url”,’图片url”,”图片url”]
    private List<String> pics ;
    private List<String> upload_path_lists=new ArrayList<String>();
    private List<Bitmap> bitmapList;
    private View rootView;
    private List<GoodsDataBean> goodsDataBeanList;
    private SubmitGoodCommentAdapter submitGoodCommentAdapter;
    private String order_id;
    private String url;
    private Dialog loadingDialog;
    private TextView tv_submit;

    private HashMap<Integer,String> contentLists = new HashMap<Integer,String>();
    private HashMap<Integer,List<String>> hashpics=new HashMap<Integer,List<String>>();
    private HashMap<Integer,List<Bitmap> > hashbitmaplist=new HashMap<Integer,List<Bitmap>>();
    private int item;
    @Override
    protected View initView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        rootView = View.inflate(this, R.layout.activity_submit_good_comment, null);
        setContentView(rootView);
        lv_good_comment = (ListView) rootView.findViewById(R.id.lv_good_comment);
        tv_submit = (TextView) rootView.findViewById(R.id.tv_submit);
        iv_top_back = (ImageView) rootView.findViewById(R.id.iv_top_back);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 拍照
        if (requestCode == PHOTO_GRAPH) {
            // 设置文件保存路径
            File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            path = dir + "/" + timepath + ".jpg";
            File file = new File(path);
            if (file.exists()) {
//                MyUpyunUtils.upload(path, completeListener);
//                Bitmap photo = BitmapUtils.getSmallBitmap(path);
//                ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                photo.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//                if(hashbitmaplist.get(item)==null){
//                    bitmapList=new ArrayList<Bitmap>();
//                }
//                else{
//                    bitmapList=hashbitmaplist.get(item);
//                }
//
//                bitmapList.add(photo);
//                hashbitmaplist.put(item, bitmapList);

                new Thread() {
                    public void run() {
                        try {
                            Bitmap photo = BitmapUtils.getSmallBitmap(path);
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            photo.compress(Bitmap.CompressFormat.JPEG, 50, baos);
                            if(hashbitmaplist.get(item)==null){
                                bitmapList=new ArrayList<Bitmap>();
                            }
                            else{
                                bitmapList=hashbitmaplist.get(item);
                            }

                            Bitmap new_photo=BitmapUtils.rotateBitmapByDegree(photo,BitmapUtils.getBitmapDegree(path));
                            bitmapList.add(new_photo);
                            hashbitmaplist.put(item, bitmapList);

                            String suoName = new SimpleDateFormat("yyyyMMdd_HHmmss")
                                    .format(new Date());
                            MyUpyunUtils.upload(BitmapUtils.saveMyBitmap(suoName, new_photo), completeListener);
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
                    uri = BitmapUtils.getPictureUri(data, SubmitCommentActivity.this);
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
//
//                Bitmap photo = BitmapUtils.getSmallBitmap(path);
//                ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                photo.compress(Bitmap.CompressFormat.JPEG, 100, baos);


                    new Thread() {
                        public void run() {
                            try {
                                Bitmap photo = BitmapUtils.getSmallBitmap(path);
                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                photo.compress(Bitmap.CompressFormat.JPEG, 50, baos);
                                if(hashbitmaplist.get(item)==null){
                                    bitmapList=new ArrayList<Bitmap>();
                                }
                                else{
                                    bitmapList=hashbitmaplist.get(item);
                                }

                                Bitmap new_photo=BitmapUtils.rotateBitmapByDegree(photo,BitmapUtils.getBitmapDegree(path));
                                bitmapList.add(new_photo);
                                hashbitmaplist.put(item, bitmapList);

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
                    if(hashpics.get(item)==null) {
                        pics = new ArrayList<>();
                    }
                    else{
                        pics=hashpics.get(item);
                    }
                    pics.add(MyUpyunUtils.UpyunBaseUrl + hs.get("url"));
                    hashpics.put(item, pics);
                    submitGoodCommentAdapter.setDate(hashpics, hashbitmaplist);
                    submitGoodCommentAdapter.notifyDataSetChanged();
                }
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
            }
        }

    };

    public void initData() {
//        bitmapList = new ArrayList<Bitmap>();
//        pics = new ArrayList<String>();
//        //添加第一个按钮的空地址，防止错误
//        pics.add(null);
//        bitmapList.add(null);
        loadingDialog = DialogUtils.createLoadDialog(SubmitCommentActivity.this, true);
        Intent intent = getIntent();
        order_id = intent.getStringExtra("order_id");
        goodsDataBeanList = (List<GoodsDataBean>) intent.getSerializableExtra("GoodsDataBean");

        tv_submit.setOnClickListener(this);
        iv_top_back.setOnClickListener(this);
        if (submitGoodCommentAdapter == null) {
            submitGoodCommentAdapter = new SubmitGoodCommentAdapter(SubmitCommentActivity.this, goodsDataBeanList,this,pWindow,rootView.findViewById(R.id.ll_main),hashpics,hashbitmaplist);
        }
        lv_good_comment.setAdapter(submitGoodCommentAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_submit:
                if (goodsDataBeanList != null) {
                    if(contentLists.size()==goodsDataBeanList.size()){
                        runSubmitGoodComment();
                    }
                    else{
                        DialogUtils.showAlertDialog(SubmitCommentActivity.this,
                                "还有商品没有评论！");
                    }
                }
                break;
            case R.id.iv_top_back:
                finish();
                overridePendingTransition(R.anim.animprv_in, R.anim.animprv_out);
                break;
        }
    }

    public void runSubmitGoodComment() {
        loadingDialog.show();
        Gson gson=new Gson();
        SubmitGoodCommentProtocol submitGoodCommentProtocol = new SubmitGoodCommentProtocol();
        SubmitGoodCommentRequest submitGoodCommentRequest = new SubmitGoodCommentRequest();
        url = submitGoodCommentProtocol.getApiFun();
        submitGoodCommentRequest.map.put("user_id", SharedPrefrenceUtils.getString(SubmitCommentActivity.this, "user_id"));
        submitGoodCommentRequest.map.put("order_id", order_id);
        JSONArray jsonArray = new JSONArray();
        try {
            for (int i = 0; i < goodsDataBeanList.size(); i++) {
                JSONObject json = new JSONObject();
                json.put("goods_id", goodsDataBeanList.get(i).getGoods_id());
                json.put("content", contentLists.get(i));
                json.put("pics",gson.toJson(hashpics.get(i)) );
                jsonArray.put(json);
            }
            submitGoodCommentRequest.map.put("commentList", jsonArray.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        MyVolley.uploadNoFile(MyVolley.POST, url, submitGoodCommentRequest.map, new MyVolley.VolleyCallback() {
            @Override
            public void dealWithJson(String address, String json) {

                Gson gson = new Gson();
                SubmitGoodCommentResponse submitGoodCommentResponse = gson.fromJson(json, SubmitGoodCommentResponse.class);
                LogUtils.e("sendVerificationCodeResponse:" + json.toString());
                if (submitGoodCommentResponse.code == 0) {
                    loadingDialog.dismiss();
                    finishActivity();
                    finish();
                    overridePendingTransition(R.anim.animprv_in, R.anim.animprv_out);
                } else {
                    loadingDialog.dismiss();
                    DialogUtils.showAlertDialog(SubmitCommentActivity.this,
                            submitGoodCommentResponse.resultText);
                }


            }

            @Override
            public void dealWithError(String address, String error) {
                loadingDialog.dismiss();
                DialogUtils.showAlertDialog(SubmitCommentActivity.this, error);
            }
        });
    }

    @Override
    public void ContentListCallBack(String s,int pos) {
        contentLists.put(pos,s);
    }

    @Override
    public void delContentList(int pos) {
        contentLists.remove(pos);
    }

    @Override
    public void ListCallBack(int pos) {
        item=pos;
    }

    @Override
    public void ImageDelCallBack(int par,int pos) {
        List<String> pics=hashpics.get(par);
        List<Bitmap> bitmapList=hashbitmaplist.get(par);
        pics.remove(pos);
        bitmapList.remove(pos);
        hashpics.put(par, pics);
        hashbitmaplist.put(par, bitmapList);
    }
}
