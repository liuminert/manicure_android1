package com.tesu.manicurehouse.fragment;


import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.tesu.manicurehouse.R;
import com.tesu.manicurehouse.activity.MainActivity;
import com.tesu.manicurehouse.util.BitmapUtils;
import com.tesu.manicurehouse.util.LogUtils;
import com.tesu.manicurehouse.util.MobileUtils;
import com.tesu.manicurehouse.util.PictureOption;
import com.tesu.manicurehouse.util.UIUtils;
import com.tesu.manicurehouse.widget.ScaleView;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

@SuppressLint("ValidFragment")
public class PictrueFragment extends Fragment {

    private String path;
    /**
     * 照片保存界面
     */
    private PopupWindow pWindow;
    private View root;
    private LayoutInflater mInflater;
    private ScaleView scale_pic_item;
//    private Bitmap bitmap;
    private String oldpath;
    private boolean isFirst;
    private boolean isOld;

    @SuppressLint("ValidFragment")
    public PictrueFragment(String path, boolean isFirst, boolean isOld) {

        this.path = path;
        this.isFirst = isFirst;
        this.isOld = isOld;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = LayoutInflater.from(getActivity()).inflate(
                R.layout.scale_pic_item, null);
        initView(view);
        scale_pic_item = (ScaleView) view.findViewById(R.id.scale_pic_item);
        if (mInflater == null) {
            mInflater = (LayoutInflater) UIUtils.getContext().getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
        }
        root = mInflater.inflate(R.layout.alert_save_dialog, null);
        pWindow = new PopupWindow(root, ActionBar.LayoutParams.FILL_PARENT,
                ActionBar.LayoutParams.FILL_PARENT);
        if (!ImageLoader.getInstance().isInited()) {
            ImageLoader.getInstance()
                    .init(ImageLoaderConfiguration.createDefault(UIUtils
                            .getContext()));
        }
        root.findViewById(R.id.btn_savePicture).setOnClickListener(itemsOnClick);
        root.findViewById(R.id.bg_photo).getBackground().setAlpha(100);
        root.findViewById(R.id.btn_cancel).setOnClickListener(itemsOnClick);
        return view;
    }

    private View.OnClickListener itemsOnClick = new View.OnClickListener() {

        public void onClick(View v) {
            pWindow.dismiss();
            switch (v.getId()) {
                case R.id.btn_savePicture: {
//                    ImageSize mImageSize = new ImageSize(100, 100);

                    ImageLoader.getInstance().loadImage(path, new SimpleImageLoadingListener() {

                        @Override
                        public void onLoadingComplete(String imageUri, View view,
                                                      Bitmap loadedImage) {
                            super.onLoadingComplete(imageUri, view, loadedImage);
                            SavaImage(loadedImage, Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "mingda");
                        }

                    });


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

    private void initView(View view) {
        ImageView imageView = (ImageView) view
                .findViewById(R.id.scale_pic_item);
        ImageLoader.getInstance().displayImage(path, imageView, PictureOption.getSimpleOptions());
        imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                pWindow.setAnimationStyle(R.style.AnimBottom);
                pWindow.showAtLocation(scale_pic_item,
                        Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                return false;
            }
        });
    }

    /**
     * 获取网络图片
     *
     * @param imageurl 图片网络地址
     * @return Bitmap 返回位图
     */
//    public Bitmap GetImageInputStream(String imageurl) {
//
//        Bitmap bitmap = null;
//        try {
//            HttpClient client = new DefaultHttpClient();
//            HttpGet httpGet = new HttpGet(imageurl);
//            HttpResponse response = client.execute(httpGet);
//            int code = response.getStatusLine().getStatusCode();
//            if (200 == code) {
//                InputStream is = response.getEntity().getContent();
//
//                BitmapFactory.Options opts = new BitmapFactory.Options();
//                opts.inJustDecodeBounds = true;
//                //根据计算出的比例进行缩放
//                int scale = BitmapUtils.calculateInSampleSize(opts, 400, 200);
//                opts.inSampleSize = scale;
//
//                bitmap= BitmapFactory.decodeStream(is, null, opts);
//
//            }
//        } catch (Exception e) {
//            LogUtils.e("bitmap异常:"+e.getMessage());
//            e.printStackTrace();
//        }
//        return bitmap;
//    }

    Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.what == 0x123) {

            } else if (msg.what == 2) {
                File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "mingda");
                if (file.exists()) {
                    SavaImage(createWaterMaskImage(UIUtils.getContext(), compressImageFromFile(oldpath), R.drawable.img_watermark), Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "mingda");
                } else {
                    file.mkdirs();
                    SavaImage(createWaterMaskImage(UIUtils.getContext(), compressImageFromFile(oldpath), R.drawable.img_watermark), Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "mingda");
                }
            }
        }

        ;
    };


//    /**
//     * 异步线程下载图片
//     */
//    class Task extends AsyncTask<String, Integer, Void> {
//
//        protected Void doInBackground(String... params) {
//            bitmap = GetImageInputStream((String) params[0]);
//            return null;
//        }
//
//        protected void onPostExecute(Void result) {
//            super.onPostExecute(result);
//            Message message = new Message();
//            message.what = 0x123;
//            handler.sendMessage(message);
//        }
//
//    }

    /**
     * 保存位图到本地
     *
     * @param bitmap
     * @param path   本地路径
     * @return void
     */
    public void SavaImage(Bitmap bitmap, String path) {
        if (isOld) {
            oldpath = path + File.separator + System.currentTimeMillis() + ".png";
            isOld = false;
        }

        File file = new File(path);
        FileOutputStream fileOutputStream = null;
        //文件夹不存在，则创建它
        if (!file.exists()) {
            file.mkdir();
        }
        try {
            fileOutputStream = new FileOutputStream(oldpath);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            fileOutputStream.close();
            if (isFirst) {
                Message message = new Message();
                message.what = 2;
                handler.sendMessage(message);
                isFirst = false;
            } else {
                Toast.makeText(UIUtils.getContext(), "保存成功！", Toast.LENGTH_SHORT).show();
                pWindow.dismiss();
                //通知相册刷新
                Uri data = Uri.parse("file://" + oldpath);
                UIUtils.getContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, data));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //加水印
    private Bitmap createWaterMaskImage(Context gContext, Bitmap src, int draw_watermark) {

        Resources res = getResources();
        Bitmap watermark1 = BitmapFactory.decodeResource(res, draw_watermark);
        if (src == null) {
            return null;
        }

        int w = src.getWidth();
        int h = src.getHeight();

        Bitmap watermark=Bitmap.createScaledBitmap(watermark1, w/3, h/3, false);

        int ww = watermark.getWidth();
        int wh = watermark.getHeight();
//        // create the new blank bitmap
        LogUtils.e("w:" + w + "    h:" + h+"ww:"+ww+"wh:"+wh);
        Bitmap newb = Bitmap.createBitmap(w, h, Bitmap.Config.RGB_565);// 创建一个新的和SRC长度宽度一样的位图

        Canvas cv = new Canvas(newb);
        // draw src into
        cv.drawBitmap(src, 0, 0, null);// 在 0，0坐标开始画入src
        Paint paint = new Paint();
        paint.setAlpha(200);
        // draw watermark into
        cv.drawBitmap(watermark, 0, 0, paint);// 在src的右下角画入水印
        // save all clip
        cv.save(Canvas.ALL_SAVE_FLAG);// 保存
        // store
        cv.restore();// 存储
        return newb;
    }

    //压缩本地图片，转成bitmap
    private Bitmap compressImageFromFile(String srcPath) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        float hh = 800f;//
        float ww = 480f;//
        int be = 1;
        if (w > h && w > ww) {
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;//设置采样率

        newOpts.inPreferredConfig = Bitmap.Config.ARGB_8888;//该模式是默认的,可不设
        newOpts.inPurgeable = true;// 同时设置才会有效
        newOpts.inInputShareable = true;//。当系统内存不够时候图片自动被回收

        Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
        LogUtils.e("压缩:" + bitmap.getWidth() + "    " + bitmap.getHeight());
//      return compressBmpFromBmp(bitmap);//原来的方法调用了这个方法企图进行二次压缩
        //其实是无效的,大家尽管尝试
        return bitmap;
    }

    //删除图片
    private void DeleteImage(String imgPath) {
        File file = new File(imgPath);
        file.delete();
    }
}
