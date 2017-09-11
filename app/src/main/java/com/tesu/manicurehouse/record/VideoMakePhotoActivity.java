package com.tesu.manicurehouse.record;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.googlecode.javacv.FFmpegFrameRecorder;
import com.googlecode.javacv.cpp.opencv_core;
import com.tesu.manicurehouse.R;
import com.tesu.manicurehouse.adapter.VideoPhoteItemAdapter;
import com.tesu.manicurehouse.adapter.VideoSelectPhoteItemAdapter;
import com.tesu.manicurehouse.bean.PhotoUpImageBucket;
import com.tesu.manicurehouse.bean.PhotoUpImageItem;
import com.tesu.manicurehouse.util.BitmapUtils;
import com.tesu.manicurehouse.util.DialogUtils;
import com.tesu.manicurehouse.util.FileUtils;
import com.tesu.manicurehouse.util.LogUtils;
import com.tesu.manicurehouse.util.MyUpyunUtils;
import com.tesu.manicurehouse.util.StringUtils;
import com.upyun.library.listener.UpCompleteListener;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


import static com.googlecode.javacv.cpp.opencv_highgui.cvLoadImage;


public class VideoMakePhotoActivity extends RecordBaseActivity {
	private ImageView backIv;
	private GridView phote_gv;
	private PhotoUpImageBucket photoUpImageBucket;
	private ArrayList<PhotoUpImageItem> selectImages;
	private VideoPhoteItemAdapter adapter;
	private GridView select_phote_gv;
	private VideoSelectPhoteItemAdapter mAdapter;
	private TextView start_make_tv;
	private String filepath = "videoTest/";
	private String Ppath = Environment.getExternalStorageDirectory().getAbsolutePath() + "videoTest/";
	private String fileName;
	private Handler makeVideoHandler;
	private Dialog loadingDialog;
	private Gson gson;
	/** 字幕文件 */
	private String subtitlePath;
//	private List<SubtitleBean> subtitleBeans;
	private String subtitleUrl;
	private StringBuffer sb;

	@Override
	protected int getContentViewId() {
		return R.layout.activity_video_make_photo;
	}

	@Override
	protected void findViews() {
		backIv = (ImageView) findViewById(R.id.video_new_img_back);
		phote_gv = (GridView) findViewById(R.id.phote_gv);
		select_phote_gv = (GridView) findViewById(R.id.selected_phote_gv);
		start_make_tv = (TextView) findViewById(R.id.start_make_tv);
	}

	@Override
	protected void initGetData() {
		super.initGetData();

	}

	@Override
	protected void init() {
		gson = new Gson();

		loadingDialog = DialogUtils.createLoadDialog(VideoMakePhotoActivity.this, false);

		Intent intent = getIntent();
		photoUpImageBucket = (PhotoUpImageBucket) intent.getSerializableExtra("imagelist");
		adapter = new VideoPhoteItemAdapter(photoUpImageBucket.getImageList(), VideoMakePhotoActivity.this);
		phote_gv.setAdapter(adapter);

		String selectImagesStr = intent.getStringExtra("selectImagesStr");
		if(StringUtils.isEmpty(selectImagesStr)){
			selectImages = new ArrayList<PhotoUpImageItem>();
		}else {
			selectImages = gson.fromJson(selectImagesStr,new TypeToken<List<PhotoUpImageItem>>(){}.getType());
		}

		mAdapter = new VideoSelectPhoteItemAdapter(selectImages, VideoMakePhotoActivity.this);
		select_phote_gv.setAdapter(mAdapter);

		makeVideoHandler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				subtitlePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/videoSub/" + System.currentTimeMillis()+".srt";
				writerSubititle(sb.toString());
				MyUpyunUtils.upload(subtitlePath, completeListener);

			}
		};

	}

	@Override
	public void onBackPressed() {
		Intent intent = new Intent(VideoMakePhotoActivity.this,VideoChoosePhotoActivity.class);
		intent.putExtra("selectImages", gson.toJson(selectImages));
		setResult(100, intent);
		finish();
	}

	@Override
	protected void widgetListener() {
		backIv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(VideoMakePhotoActivity.this, VideoChoosePhotoActivity.class);
				intent.putExtra("selectImages", gson.toJson(selectImages));
				setResult(100, intent);
				finish();
			}
		});

		phote_gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
									int position, long id) {

				CheckBox checkBox = (CheckBox) view.findViewById(R.id.check);
				photoUpImageBucket.getImageList().get(position).setIsSelected(
						!checkBox.isChecked());
				adapter.notifyDataSetChanged();

				if (photoUpImageBucket.getImageList().get(position).isSelected()) {
					if (selectImages.contains(photoUpImageBucket.getImageList().get(position))) {

					} else {
						selectImages.add(photoUpImageBucket.getImageList().get(position));
					}
				} else {
					if (selectImages.contains(photoUpImageBucket.getImageList().get(position))) {
						selectImages.remove(photoUpImageBucket.getImageList().get(position));
					} else {

					}
				}

				mAdapter.notifyDataSetChanged();
			}
		});
		start_make_tv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (selectImages == null || selectImages.size() == 0) {
					Toast.makeText(VideoMakePhotoActivity.this, "请先选择图片", Toast.LENGTH_LONG).show();
					return;
				}
				if (selectImages.size() < 3 || selectImages.size() > 6) {
					Toast.makeText(VideoMakePhotoActivity.this, "请先选择3~6张图片", Toast.LENGTH_LONG).show();
					return;
				}
				makeVideo();
			}
		});
	}

	/**
	 * 合成视频
	 */
	private void makeVideo() {
		loadingDialog.show();
		new Thread(){
			@Override
			public void run() {
				OutputStream os = null;
				try {
					fileName ="test1.mp4";
					LogUtils.e("selectImages"+selectImages.toString());
					Bitmap testBitmap =
							BitmapUtils.getSmallBitmap(selectImages.get(0).getImagePath());
					FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(
							new FileUtils().getSDCardRoot()+filepath+File.separator+fileName,
							testBitmap.getWidth(),
							testBitmap.getHeight());

					recorder.setFormat("mp4");
					recorder.setFrameRate(10f);//录像帧率
					// recorder.setTimestamp(1000*System.currentTimeMillis());
					recorder.start();
//					subtitleBeans = new ArrayList<SubtitleBean>();
					sb=new StringBuffer();

					selectImages.add(selectImages.get(selectImages.size()-1));  //因为合成的视频最后一张图片的时间取不到，所以制作的时候最后一张图片重复加一张，
					for(int i=0;i<selectImages.size();i++){                     //那样的话就是多余的最后一张图片的时间取不到，而且最后一张图片重复展示，也
						File file = null;
						if(!new FileUtils().isFileExist("video.jpg", filepath)){
							file = new FileUtils().createFileInSDCard("video.jpg",
									filepath);
						}else{
							file = new FileUtils().getFile("video.jpg",filepath);
						}

						os = new FileOutputStream(file);
						Bitmap frame = BitmapUtils.getSmallBitmap(selectImages.get(i).getImagePath());
						frame.compress(Bitmap.CompressFormat.JPEG, 100, os);
						os.flush();
						os.close();
						frame.recycle();
						frame = null;

						opencv_core.IplImage image = cvLoadImage(new
								FileUtils().getSDCardRoot()+filepath+File.separator+"video.jpg");
						recorder.record(image);
//						if(i!=0){
							recorder.record(image);
							recorder.record(image);
							recorder.record(image);
							recorder.record(image);
							recorder.record(image);
							recorder.record(image);
							recorder.record(image);
							recorder.record(image);
							recorder.record(image);
							recorder.record(image);
							recorder.record(image);
							recorder.record(image);
							recorder.record(image);
							recorder.record(image);
							recorder.record(image);
							recorder.record(image);
							recorder.record(image);
							recorder.record(image);
							recorder.record(image);
//						}

						if(i < selectImages.size()-1){
							sb.append(i + "\r\n");
							sb.append(getTime(i)+"\r\n");
							String contentStr = selectImages.get(i).getContent();
							if(TextUtils.isEmpty(contentStr)){
								contentStr = "null";
							}
							sb.append(contentStr + "\r\n\r\n");
						}
					}

					selectImages.remove(selectImages.size()-1);

					recorder.stop();
					makeVideoHandler.sendEmptyMessage(0);
				}
				catch(FileNotFoundException e){
					e.printStackTrace();
				}
				catch(IOException e){
					e.printStackTrace();
				}
				catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}.start();
	}

	private String getTime(int i) {
		String time="";
		switch(i){
			case 0:
				time ="00:00:00,000 --> 00:00:02,000";
				break;
			case 1:
				time ="00:00:02,000 --> 00:00:04,000";
				break;
			case 2:
				time ="00:00:04,000 --> 00:00:06,000";
				break;
			case 3:
				time ="00:00:06,000 --> 00:00:08,000";
				break;
			case 4:
				time ="00:00:08,000 --> 00:00:10,000";
				break;
			case 5:
				time ="00:00:10,000 --> 00:00:12,000";
				break;
			case 6:
				time ="00:00:12,000 --> 00:00:14,000";
				break;
		}
		return time;
	}

	//结束回调，不可为空
	UpCompleteListener completeListener = new UpCompleteListener() {
		@Override
		public void onComplete(boolean isSuccess, String result) {
			Gson gson = new Gson();
			loadingDialog.dismiss();
			LogUtils.e("上传图片result:" + result);
			try {
				if(isSuccess){
                    HashMap<String,String> hs = gson.fromJson(result, HashMap.class);
                    subtitleUrl = hs.get("url");
                    subtitleUrl =MyUpyunUtils.UpyunBaseUrl+subtitleUrl;
                    LogUtils.e("上传字幕文件到又拍返回url:" + subtitleUrl);

                    Intent intent = new Intent(VideoMakePhotoActivity.this,VideoAddMusicActivity.class);
                    try {
                        intent.putExtra("path",new FileUtils().getSDCardRoot()+filepath+File.separator+fileName);
                        intent.putExtra("subtitleUrl", subtitleUrl);
                    } catch (FileUtils.NoSdcardException e) {
                        e.printStackTrace();
                    }
                    VideoMakePhotoActivity.this.startActivity(intent);
                    setFinish();
    //				finish();

                }else {
                    LogUtils.e("上传字幕文件到又拍错误:"+result);
                    DialogUtils.showAlertDialog(VideoMakePhotoActivity.this, "上传字幕文件到又拍云错误");
                }
			} catch (JsonSyntaxException e) {
				e.printStackTrace();
			}
		}

	};

	public void deletePhoto(int position){
		PhotoUpImageItem photoUpImageItem = selectImages.get(position);
		if(photoUpImageItem != null){
			selectImages.remove(photoUpImageItem);
			mAdapter.notifyDataSetChanged();

			if(photoUpImageBucket.getImageList().contains(photoUpImageItem)){
				int pos = photoUpImageBucket.getImageList().lastIndexOf(photoUpImageItem);
				photoUpImageBucket.getImageList().get(pos).setIsSelected(false);
				adapter.notifyDataSetChanged();
			}
		}
	}

	/**
	 * 将字幕文件写入
	 * @param
	 */
	@SuppressWarnings("resource")
	protected void writerSubititle(String content) {
		File file = new File(subtitlePath);
		if (!file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}
		try {
			if(file.exists()){
				file.delete();
			}
			file.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			FileWriter fw = new FileWriter(file, true);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(content);
			bw.flush();
			bw.close();
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
