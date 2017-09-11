package com.tesu.manicurehouse.record;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.media.MediaRecorder.OnErrorListener;
import android.media.MediaRecorder.OnInfoListener;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.tesu.manicurehouse.R;
import com.tesu.manicurehouse.base.MyVolley;
import com.tesu.manicurehouse.bean.ShowPhotoContentBean;
import com.tesu.manicurehouse.play.MediaPlayActivity;
import com.tesu.manicurehouse.response.UploadFileResponse;
import com.tesu.manicurehouse.support.PercentLinearLayout;
import com.tesu.manicurehouse.support.PercentRelativeLayout;
import com.tesu.manicurehouse.util.DialogUtils;
import com.tesu.manicurehouse.util.LogUtils;
import com.tesu.manicurehouse.util.MyUpyunUtils;
import com.upyun.library.listener.UpCompleteListener;

public class VideoNewActivity extends RecordBaseActivity implements
		SurfaceHolder.Callback {
	private static final String TAG = VideoNewActivity.class.getSimpleName();

	/** 视频最大支持60秒 */
	public static final int VIDEO_TIME_END = 60;
	/** 视频最少必须5秒 */
	public static final int VIDEO_TIME = 5;
	/** 最少得录制多少秒 */
	private ImageView img_at_last;
	/** 闪现光标图片 */
	private ImageView img_shan;
	/** 删除录制 */
//	private ImageView img_delete;
	private PercentRelativeLayout rl_delete;
	/** 开始录制 */
	private ImageButton img_start;
//	private PercentRelativeLayout rl_start;
	/** 确认 */
//	private ImageView img_enter;
	private PercentRelativeLayout rl_enter;
	/** 闪光灯切换 */
	private ImageView img_flashlight;
	/** 摄像头切换 */
	private ImageView img_camera;
	/** 选择录像 */
//	private ImageView img_video;
	private PercentRelativeLayout rl_video;
	/**照片电影*/
	private PercentRelativeLayout rl_img_movie;
	/** 返回按钮 */
	private ImageView img_back;
	/** 计时器 */
	private TimeCount timeCount;
	/** 录制了多少秒 */
	private int now;
	/** 每次录制结束时是多少秒 */
	private int old;

	/** 录制进度控件 */
	private LinearLayout linear_seekbar;
	/** 屏幕宽度 */
	private int width;
	/** 偶数才执行 */
	private int even;
	/** 是否点击删除了一次 */
	private boolean isOnclick = false;
	/** 录制视频集合 */
	private ArrayList<VideoNewBean> list;
	/** 录制bean */
	private VideoNewBean bean;
	/** 为了能保存到bundler 录制bean */
	private VideoNewParentBean parent_bean;
	/** 录制视频保存文件 */
	private String vedioPath;
	/** 合并之后的视频文件 */
	private String videoPath_merge;
	/** 字幕文件 */
	private String subtitlePath;
	/** 是否满足视频的最少播放时长 */
	private boolean isMeet = false;

	/** 录制视频的类 */
	private MediaRecorder mMediaRecorder;
	/** 摄像头对象 */
	private Camera mCamera;
	/** 显示的view */
	private SurfaceView surfaceView;
	/** 摄像头参数 */
	private Parameters mParameters;
	// /** 视频输出质量 */
	private CamcorderProfile mProfile;
	/** 文本属性获取器 */
	private SharedPreferences mPreferences;
	/** 刷新界面的回调 */
	private SurfaceHolder mHolder;
	/** 1表示后置，0表示前置 */
	private int cameraPosition = 1;
	/** 路径 */
	private String Ppath = Environment.getExternalStorageDirectory()
			.getAbsolutePath() + "/videoTest/";
	private Camera.AutoFocusCallback mAutoFocusCallback;
	// /** 压缩jni */
	// private LoadJNI vk;
	private Timer mTimer;
	private TimerTask mTimerTask;

	private PercentRelativeLayout rl_add_subtitle;
	private PercentLinearLayout rl_input_subtitle;
	private PercentRelativeLayout rl_record;
	private TextView movie_title;
	private EditText subtitle_et;
	private Button subtitle_submit_bt;
	private Button subtitle_cancel_bt;
	private ImageButton subtitle_ib;
	private List<SubtitleBean> subtitleBeans;
	private long totalTime=60000;  //最多可录视频时长
	private TextView start_tv;

	private Dialog loadingDialog;
	private UploadFileResponse uploadFileResponse;
	private String subtitleUrl;

//	private static final int MY_PERMISSIONS_REQUEST_CAMERA = 1;
//	private static final int MY_PERMISSIONS_REQUEST_AUDIO = 2;

	private Size mLargestSize;

	@Override
	protected int getContentViewId() {
		return R.layout.activity_video_new;
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void findViews() {
		img_camera = (ImageView) findViewById(R.id.video_new_img_right);
		img_flashlight = (ImageView) findViewById(R.id.video_new_img_flashlight);
		surfaceView = (SurfaceView) findViewById(R.id.video_new_surfaceview);
		img_at_last = (ImageView) findViewById(R.id.video_new_img_time_atlast);
		img_shan = (ImageView) findViewById(R.id.video_new_img_time_start);
//		img_delete = (ImageView) findViewById(R.id.video_new_img_delete);
		rl_delete = (PercentRelativeLayout) findViewById(R.id.rl_video_new_delete);
		img_start = (ImageButton) findViewById(R.id.video_new_img_start);
//		rl_start = (PercentRelativeLayout) findViewById(R.id.rl_video_new_start);
//		img_enter = (ImageView) findViewById(R.id.video_new_img_enter);
		rl_enter = (PercentRelativeLayout) findViewById(R.id.rl_video_new_img_enter);
		img_back = (ImageView) findViewById(R.id.video_new_img_back);
//		img_video = (ImageView) findViewById(R.id.video_new_img_video);
		rl_video = (PercentRelativeLayout) findViewById(R.id.rl_video_new_video);
		rl_img_movie = (PercentRelativeLayout) findViewById(R.id.rl_img_movie);
		linear_seekbar = (LinearLayout) findViewById(R.id.video_new_seekbar);
		subtitle_et = (EditText) findViewById(R.id.subtitle_et);
		subtitle_ib = (ImageButton) findViewById(R.id.add_subititle_ib);
		rl_add_subtitle = (PercentRelativeLayout) findViewById(R.id.rl_add_subititle);
		rl_input_subtitle = (PercentLinearLayout) findViewById(R.id.rl_input_subtitle);
		rl_record = (PercentRelativeLayout) findViewById(R.id.rl_record);
		movie_title = (TextView) findViewById(R.id.movie_title);
		subtitle_submit_bt = (Button) findViewById(R.id.subtitle_submit_bt);
		subtitle_cancel_bt = (Button) findViewById(R.id.subtitle_cancel_bt);
		start_tv = (TextView) findViewById(R.id.video_new_text_start);

		width = getWindowManager().getDefaultDisplay().getWidth();

//		LayoutParams layoutParam = (LayoutParams) surfaceView.getLayoutParams();
//		// 高：宽 1 : 1
//		layoutParam.height = width ;
//		// 隐藏多少dp才能让屏幕显示正常像素
//		layoutParam.topMargin = -(width  - width - DisplayUtil.dip2px(
//				VideoNewActivity.this, 44));
//		surfaceView.setLayoutParams(layoutParam);

		LayoutParams layoutParams = (LayoutParams) img_at_last
				.getLayoutParams();
		layoutParams.leftMargin = width / VIDEO_TIME_END * VIDEO_TIME;
		img_at_last.setLayoutParams(layoutParams);

		mAutoFocusCallback = new Camera.AutoFocusCallback() {

			public void onAutoFocus(boolean success, Camera camera) {
				// TODO Auto-generated method stub
				if (success) {
					mCamera.setOneShotPreviewCallback(null);
					Toast.makeText(VideoNewActivity.this, "聚焦",
							Toast.LENGTH_SHORT).show();
				}

			}
		};
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		parent_bean.setList(list);
		outState.putSerializable("parent_bean", parent_bean);

		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		parent_bean = (VideoNewParentBean) savedInstanceState
				.getSerializable("parent_bean");
		list = parent_bean.getList();

		super.onRestoreInstanceState(savedInstanceState);
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void init() {
//		handler.postDelayed(runnable, 0);
		even = 0;
		old = 0;
		// 创建文件夹
		File file = new File(Ppath);
		if (!file.exists()) {
			file.mkdir();
		}
		list = new ArrayList<VideoNewBean>();
		subtitleBeans = new ArrayList<SubtitleBean>();
		parent_bean = new VideoNewParentBean();
		// 安装一个SurfaceHolder.Callback
		mHolder = surfaceView.getHolder();

		mHolder.addCallback(this);
		// 针对低于3.0的Android
		mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		// // 设置surfaceView分辨率
//		mHolder.setFixedSize(640, 480);

		mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		readVideoPreferences();

		loadingDialog = DialogUtils.createLoadDialog(VideoNewActivity.this, false);

	}

	@Override
	protected void onStart() {
		super.onStart();
		// 获取Camera实例
			mCamera = getCamera();
			if (mCamera != null) {
				// 因为android不支持竖屏录制，所以需要顺时针转90度，让其游览器显示正常
				mCamera.setDisplayOrientation(90);
				mCamera.lock();
				initCameraParameters();
			}

	}

	@Override
	protected void onPause() {
		super.onPause();
		releaseCamera();
	}

	/**
	 * 获取摄像头实例
	 * 
	 * @version 1.0
	 * @createTime 2015年6月16日,上午10:44:11
	 * @updateTime 2015年6月16日,上午10:44:11
	 * @createAuthor WangYuWen
	 * @updateAuthor WangYuWen
	 * @updateInfo (此处输入修改内容,若无修改可不写.)
	 * 
	 * @return
	 */
	private Camera getCamera() {
		Camera camera = null;
		try {
			camera = Camera.open();
		} catch (Exception e) {
			LogUtils.e("getCamera:"+e.toString());
			camera = null;
		}
		return camera;
	}

	private Handler handler = new Handler();

//	private Runnable runnable = new Runnable() {
//
//		@Override
//		public void run() {
//			if (img_shan.isShown()) {
//				img_shan.setVisibility(View.GONE);
//			} else {
//				img_shan.setVisibility(View.VISIBLE);
//			}
//			handler.postDelayed(runnable, 500);
//		}
//	};

	@SuppressLint("ClickableViewAccessibility")
	@Override
	protected void widgetListener() {
		img_start.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:

//						if (isOnclick) {
//							((ImageView) linear_seekbar.getChildAt(linear_seekbar
//									.getChildCount() - 2))
//									.setBackgroundColor(getResources().getColor(
//											R.color.ff135689));
//						}

						even = 1;

						rl_delete.setVisibility(View.VISIBLE);
						rl_enter.setVisibility(View.VISIBLE);
						rl_add_subtitle.setVisibility(View.VISIBLE);
						rl_video.setVisibility(View.GONE);
						rl_img_movie.setVisibility(View.GONE);
//						img_camera.setVisibility(View.GONE);
						img_start.setImageResource(R.drawable.bt_star);
						start_tv.setVisibility(View.INVISIBLE);

						addView_Red();

						// 构造CountDownTimer对象
						timeCount = new TimeCount(totalTime - old, 50);
						timeCount.start();// 开始计时
//						if (
//								ContextCompat.checkSelfPermission(VideoNewActivity.this,
//										Manifest.permission.RECORD_AUDIO)
//										!= PackageManager.PERMISSION_GRANTED)
//						{
//							ActivityCompat.requestPermissions(VideoNewActivity.this,
//									new String[]{Manifest.permission.RECORD_AUDIO},
//									MY_PERMISSIONS_REQUEST_AUDIO);
//						}else {
//
//							startRecord();
//						}

						startRecord();

						break;
					case MotionEvent.ACTION_UP:
						old = now + old;

						if (old >= VIDEO_TIME * 1000) {
							isMeet = true;
						}

						timeCount.cancel();

						addView_black();

						stopRecord();

//					addSubtitle();

						break;
				}
				return false;
			}
		});
		/** 删除按钮 */
		rl_delete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				isOnclick = false;
				if (even % 2 == 0) {
					if (linear_seekbar.getChildCount() > 1) {
						linear_seekbar.removeViewAt(linear_seekbar
								.getChildCount() - 1);
						linear_seekbar.removeViewAt(linear_seekbar
								.getChildCount() - 1);
					}
					if (list.size() > 0) {
//						for (int i = 0; i < list.size(); i++) {
							File file = new File(list.get(list.size() - 1)
									.getPath());
							if (file.exists()) {
								file.delete();
							}
//						}
						VideoNewBean newBean = list.get(list.size()-1);
						old -= newBean.getTime();
						for(int i=0;i<subtitleBeans.size();i++){
							SubtitleBean subtitleBean = subtitleBeans.get(i);
							if(subtitleBean.getVideoId() == newBean.getVideoId()){
								subtitleBeans.remove(i);
							}
						}
						list.remove(list.size() - 1);
						if (old < VIDEO_TIME * 1000) {
							isMeet = false;
						}
						if (list.size() <= 0) {
							rl_delete.setVisibility(View.GONE);
							rl_enter.setVisibility(View.GONE);
							rl_video.setVisibility(View.VISIBLE);
//							img_camera.setVisibility(View.VISIBLE);

							rl_add_subtitle.setVisibility(View.INVISIBLE);
							rl_img_movie.setVisibility(View.VISIBLE);
							img_start.setImageResource(R.drawable.bt_shoot);
							start_tv.setVisibility(View.VISIBLE);
						}
					}
				} else {
					if (linear_seekbar.getChildCount() > 1) {
						isOnclick = true;
//						((ImageView) linear_seekbar.getChildAt(linear_seekbar
//								.getChildCount() - 2))
//								.setBackgroundColor(getResources().getColor(
//								R.color.ff135689));
					}
				}
				even++;
			}
		});
		/** 开启或关闭闪光灯 */
		img_flashlight.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (cameraPosition != 0) {// 前置摄像头的时候不能切换闪光灯
					if (mParameters != null) {
						if (mParameters.getFlashMode() != null
								&& mParameters.getFlashMode().equals(
										Parameters.FLASH_MODE_TORCH)) {
							mParameters.setFlashMode(Parameters.FLASH_MODE_OFF);
							img_flashlight
									.setImageResource(R.drawable.img_video_new_flashlight_close);
						} else if (mParameters.getFlashMode() != null
								&& mParameters.getFlashMode().equals(
										Parameters.FLASH_MODE_OFF)) {
							mParameters
									.setFlashMode(Parameters.FLASH_MODE_TORCH);
							img_flashlight
									.setImageResource(R.drawable.img_video_new_flashlight_open);
						}
						if (mCamera != null) {
							mCamera.setParameters(mParameters);
						}
					}
				}
			}
		});
		/** 摄像头切换 */
		img_camera.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				switchCamera();
			}
		});

		/** 确认按钮 */
		rl_enter.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (isMeet) {
					loadingDialog.show();
					int size = list.size();
					String[] strs = new String[size];
					long cuTime = System.currentTimeMillis();
//					long cuTime = 123456;
					videoPath_merge = Ppath + cuTime
							+ ".mp4";
//					subtitlePath = Ppath + cuTime+".srt";
					subtitlePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/videoSub/" + cuTime+".srt";
					for (int i = 0; i < size; i++) {
						strs[i] = list.get(i).getPath();
					}
					LogUtils.e("recodeList:"+list.toString());
					try {
						FUckTest.appendVideo(strs, videoPath_merge);
						if(subtitleBeans != null && subtitleBeans.size()>0){
							StringBuffer sb=new StringBuffer();
							for(SubtitleBean bean : subtitleBeans){
								sb.append(bean.getId()+"\r\n");
								sb.append(bean.getTime()+"\r\n");
								sb.append(bean.getContent()+"\r\n\r\n");
							}
							subtitleBeans.clear();
							writerSubititle(sb.toString());

							uploadSubititle();
						}else {
							//						Intent it = new Intent(VideoNewActivity.this,
//								VideoActivity.class);
							loadingDialog.dismiss();
							Intent it = new Intent(VideoNewActivity.this,
									VideoAddMusicActivity.class);
							it.putExtra("path", videoPath_merge);
//							it.putExtra("subtitleUrl", subtitleUrl);
							startActivity(it);
//							finish();
							setFinish();

						}

//						for (int i = size - 1; i >= 0; i--) {
//							File file = new File(list.get(i).getPath());
//							if (file.exists()) {
//								file.delete();
//							}
//							list.remove(i);
//						}


					} catch (IOException e) {
						e.printStackTrace();
					}
				} else {
					Toast.makeText(VideoNewActivity.this, "视频最少必须录制5秒以上才能用！",
							Toast.LENGTH_LONG).show();
				}
			}
		});
		/** 选择录像按钮 */
		rl_video.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(VideoNewActivity.this,
						VideoNewSelectActivity.class);
				startActivity(intent);
				setFinish();
//				finish();
			}
		});

		img_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (list.size() > 0) {
					exitVideoNewDialog();
				} else {
					releaseCamera();
					finish();
				}
			}
		});

		surfaceView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mParameters != null && mCamera != null) {
					mParameters.setFocusMode(Parameters.FOCUS_MODE_AUTO);
					try {
						mCamera.setParameters(mParameters);
						mCamera.autoFocus(mAutoFocusCallback);
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					Log.e("num", "mei聚焦");
				}
			}
		});

		subtitle_ib.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(list==null || list.size()==0){
					Toast.makeText(getApplicationContext(), "请先录制视频，然后再加字幕", Toast.LENGTH_SHORT).show();
					return;
				}
				rl_input_subtitle.setVisibility(View.VISIBLE);
				rl_record.setVisibility(View.GONE);
				movie_title.setVisibility(View.GONE);
				rl_add_subtitle.setVisibility(View.INVISIBLE);

			}
		});

		subtitle_submit_bt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				rl_input_subtitle.setVisibility(View.GONE);
				rl_record.setVisibility(View.VISIBLE);
				movie_title.setVisibility(View.VISIBLE);
				rl_add_subtitle.setVisibility(View.VISIBLE);

				addSubtitle();

			}
		});

		subtitle_cancel_bt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				rl_input_subtitle.setVisibility(View.GONE);
				rl_record.setVisibility(View.VISIBLE);
				movie_title.setVisibility(View.VISIBLE);
				rl_add_subtitle.setVisibility(View.VISIBLE);
				subtitle_et.setText("");
			}
		});

		/**照片电影*/
		rl_img_movie.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(VideoNewActivity.this,VideoChoosePhotoActivity.class);
				startActivity(intent);
				setFinish();
//				finish();
			}
		});
	}

//	@Override
//	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
//	{
//
//		if (requestCode == MY_PERMISSIONS_REQUEST_AUDIO)
//		{
//			if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED)
//			{
//				startRecord();
//			} else
//			{
//				// Permission Denied
//				Toast.makeText(VideoNewActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
//			}
//			return;
//		}else if(requestCode == MY_PERMISSIONS_REQUEST_CAMERA){
//			if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
//			{
//				onStart();
//			} else
//			{
//				// Permission Denied
//				Toast.makeText(VideoNewActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
//			}
//			return;
//		}
//		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//	}

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

                    loadingDialog.dismiss();
                    Intent it = new Intent(VideoNewActivity.this,
                            VideoAddMusicActivity.class);
                    it.putExtra("path", videoPath_merge);
                    it.putExtra("subtitleUrl", subtitleUrl);
                    startActivity(it);
    //				finish();
                    setFinish();

                }else {
                    LogUtils.e("上传字幕文件到又拍错误:"+result);
                    DialogUtils.showAlertDialog(VideoNewActivity.this, "上传字幕文件到又拍云错误");
                }
			} catch (JsonSyntaxException e) {
				e.printStackTrace();
			}
		}

	};
	/**
	 * 上传字幕文件
	 */
	private void uploadSubititle() {
		loadingDialog.show();
		MyUpyunUtils.upload(subtitlePath, completeListener);
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

	/**
	 * 添加字幕
	 */
	protected void addSubtitle() {
		String subtitle = subtitle_et.getText().toString();
		int startTime = old-now;
		Log.e(TAG, "list:"+list.toString());
		SubtitleBean subtitleBean = new SubtitleBean();
		if(TextUtils.isEmpty(subtitle)){
			subtitle = "null";
		}
		subtitleBean.setContent(subtitle);
		subtitleBean.setId(subtitleBeans.size());
		subtitleBean.setVideoId(list.get(list.size()-1).getVideoId());
		String time1=null;
		if(startTime==0){
			time1 ="00:00:00,000";
		}else{
			time1 = secToTime(startTime/1000)+","+mimToTime(startTime%1000);
		}
		String time2 = secToTime(old/1000)+","+mimToTime(old%1000);
		String time = time1 +" --> "+time2;
		subtitleBean.setTime(time);
		subtitleBeans.add(subtitleBean);
		Log.e(TAG, "now:"+now +",old:"+old);
		Log.e(TAG, "time:"+time);
		Log.e(TAG, "subtitleBeans:"+subtitleBeans.toString());
		subtitle_et.setText("");
		
	}
	
	public String mimToTime(int time){
		String s ="";
		if(time<10){
			s = "00"+time;
		}else if(time<100){
			s = "0"+time;
		}else{
			s = time+"";
		}
		return s;
	}
	
	public String secToTime(int time) {  
        String timeStr = null;  
        int hour = 0;  
        int minute = 0;  
        int second = 0;  
        if (time <= 0)  
            return "00:00";  
        else {  
            minute = time / 60;  
//            if (minute < 60) {  
//                second = time % 60;  
//                timeStr = unitFormat(minute) + ":" + unitFormat(second);  
//            } else {  
                hour = minute / 60;  
                if (hour > 99)  
                    return "99:59:59";  
                minute = minute % 60;  
                second = time - hour * 3600 - minute * 60;  
                timeStr = unitFormat(hour) + ":" + unitFormat(minute) + ":" + unitFormat(second);  
//            }  
        }  
        return timeStr;  
    }  
	
	public String unitFormat(int i) {  
        String retStr = null;  
        if (i >= 0 && i < 10)  
            retStr = "0" + Integer.toString(i);  
        else  
            retStr = "" + i;  
        return retStr;  
    } 

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			if (event.getAction() == KeyEvent.ACTION_DOWN
					&& event.getRepeatCount() == 0) {
				if (list.size() > 0) {
					exitVideoNewDialog();
				} else {
					releaseCamera();
					finish();
				}
			}
			return true;
		}
		return super.dispatchKeyEvent(event);
	}

	/**
	 * 弹出对话框
	 * 
	 * @version 1.0
	 * @createTime 2015年6月16日,下午3:45:35
	 * @updateTime 2015年6月16日,下午3:45:35
	 * @createAuthor WangYuWen
	 * @updateAuthor WangYuWen
	 * @updateInfo (此处输入修改内容,若无修改可不写.)
	 * 
	 */
	private void exitVideoNewDialog() {

		Builder builder = new Builder(VideoNewActivity.this);
		builder.setMessage("确定放弃这段视频吗？");
		builder.setTitle("温馨提示");
		builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				for (int i = 0; i < list.size(); i++) {
					File file = new File(list.get(i).getPath());
					if (file.exists()) {
						file.delete();
					}
				}
				finish();
			}

		});
		builder.create().show();
	}

	/**
	 * 切换摄像头
	 * 
	 * @version 1.0
	 * @createTime 2015年6月16日,上午10:40:17
	 * @updateTime 2015年6月16日,上午10:40:17
	 * @createAuthor WangYuWen
	 * @updateAuthor WangYuWen
	 * @updateInfo (此处输入修改内容,若无修改可不写.)
	 * 
	 */
	@SuppressLint("NewApi")
	private void switchCamera() {
		// 切换前后摄像头
		int cameraCount = 0;
		CameraInfo cameraInfo = new CameraInfo();
		cameraCount = Camera.getNumberOfCameras();// 得到摄像头的个数

		for (int i = 0; i < cameraCount; i++) {
			Camera.getCameraInfo(i, cameraInfo);// 得到每一个摄像头的信息
			if (cameraPosition == 1) {
				// 现在是后置，变更为前置
				if (cameraInfo.facing == CameraInfo.CAMERA_FACING_FRONT) {// 代表摄像头的方位，CAMERA_FACING_FRONT前置
					// CAMERA_FACING_BACK后置
					// 前置摄像头时必须关闭闪光灯，不然会报错
					if (mParameters != null) {
						if (mParameters.getFlashMode() != null
								&& mParameters.getFlashMode().equals(
										Parameters.FLASH_MODE_TORCH)) {
							mParameters.setFlashMode(Parameters.FLASH_MODE_OFF);
							img_flashlight
									.setImageResource(R.drawable.img_video_new_flashlight_close);
						}
						if (mCamera != null) {
							mCamera.setParameters(mParameters);
						}
					}

					// 释放Camera
					releaseCamera();

					// 打开当前选中的摄像头
					mCamera = Camera.open(i);
					mCamera.setDisplayOrientation(90);
					mCamera.lock();

					// 通过surfaceview显示取景画面
					setStartPreview(mHolder);

					cameraPosition = 0;

					break;
				}
			} else {
				// 现在是前置， 变更为后置
				if (cameraInfo.facing == CameraInfo.CAMERA_FACING_BACK) {// 代表摄像头的方位，CAMERA_FACING_FRONT前置
					// CAMERA_FACING_BACK后置
					// 释放Camera
					releaseCamera();
					// 打开当前选中的摄像头
					mCamera = Camera.open(i);
					mCamera.setDisplayOrientation(90);
					mCamera.lock();

					// 通过surfaceview显示取景画面
					setStartPreview(mHolder);

					cameraPosition = 1;

					break;
				}
			}

		}
	}

	/**
	 * 定义一个倒计时的内部类
	 * 
	 * @Description
	 * @author
	 * @version 1.0
	 * @date 2015-5-25
	 * @Copyright: Copyright (c) 2015 Shenzhen Utoow Technology Co., Ltd. All
	 *             rights reserved.
	 */
	private class TimeCount extends CountDownTimer {
		public TimeCount(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);// 参数依次为总时长,和计时的时间间隔
		}

		@Override
		public void onFinish() {// 计时完毕时触发

		}

		@Override
		public void onTick(long millisUntilFinished) {// 计时过程显示
			now = (int) (totalTime - millisUntilFinished - old);
			if ((old > 0 && old > VIDEO_TIME * 1000)
					|| (old == 0 && now > VIDEO_TIME * 1000)) {
				rl_enter.setEnabled(true);
			}
			if (linear_seekbar.getChildCount() > 0) {
				ImageView img = (ImageView) linear_seekbar
						.getChildAt(linear_seekbar.getChildCount() - 1);
				LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) img
						.getLayoutParams();
				layoutParams.width = (int) (((float) now / 1000f) * (width / VIDEO_TIME_END)) + 1;
				img.setLayoutParams(layoutParams);
			}
		}
	}

	/**
	 * 初始化摄像头参数
	 * 
	 * @version 1.0
	 * @createTime 2015年6月15日,下午4:53:41
	 * @updateTime 2015年6月15日,下午4:53:41
	 * @createAuthor WangYuWen
	 * @updateAuthor WangYuWen
	 * @updateInfo (此处输入修改内容,若无修改可不写.)
	 * 
	 */
	@SuppressWarnings("deprecation")
	private void initCameraParameters() {
		// 初始化摄像头参数
		mParameters = mCamera.getParameters();

		mParameters.setPreviewSize(mProfile.videoFrameWidth,
				mProfile.videoFrameHeight);
		mParameters.setPreviewFrameRate(mProfile.videoFrameRate);

		mParameters.setFocusMode(Parameters.FOCUS_MODE_AUTO);

		// 设置白平衡参数。
		String whiteBalance = mPreferences.getString(
				"pref_camera_whitebalance_key", "auto");
		if (isSupported(whiteBalance, mParameters.getSupportedWhiteBalance())) {
			mParameters.setWhiteBalance(whiteBalance);
		}

		// 参数设置颜色效果。
		String colorEffect = mPreferences.getString(
				"pref_camera_coloreffect_key", "none");
		if (isSupported(colorEffect, mParameters.getSupportedColorEffects())) {
			mParameters.setColorEffect(colorEffect);
		}

		try {
			mCamera.setParameters(mParameters);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 开始录制
	 * 
	 * @version 1.0
	 * @createTime 2015年6月15日,下午4:48:49
	 * @updateTime 2015年6月15日,下午4:48:49
	 * @createAuthor WangYuWen
	 * @updateAuthor WangYuWen
	 * @updateInfo (此处输入修改内容,若无修改可不写.)
	 * 
	 */
	@SuppressLint("NewApi")
	private void startRecord() {
		try {
			bean = new VideoNewBean();
			vedioPath = Ppath + System.currentTimeMillis() + ".mp4";
			bean.setPath(vedioPath);

			mCamera.unlock();

			if (mMediaRecorder == null) {
				mMediaRecorder = new MediaRecorder();// 创建mediaRecorder对象
			} else {
				mMediaRecorder.reset();
			}
			mMediaRecorder.setCamera(mCamera);
			// 设置录制视频源为Camera(相机)
			mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
			mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);

			// 相机参数配置类
			CamcorderProfile cProfile = CamcorderProfile
					.get(CamcorderProfile.QUALITY_HIGH);
			mMediaRecorder.setProfile(cProfile);

//			mMediaRecorder.setVideoSize(cProfile.videoFrameWidth,
//					cProfile.videoFrameHeight);
			LogUtils.e("cProfile:"+cProfile.videoFrameWidth+","+cProfile.videoFrameHeight);
			mMediaRecorder.setVideoSize(640,
					480);

//			mMediaRecorder.setVideoEncodingBitRate(5 * 1920 * 1080);// 设置视频一次写多少字节(可调节视频空间大小)
			mMediaRecorder.setVideoEncodingBitRate(5 * 1024 * 1024);// 设置视频一次写多少字节(可调节视频空间大小)
			// 最大期限
			mMediaRecorder.setMaxDuration(35 * 1000);

			// 第4步:指定输出文件 ， 设置视频文件输出的路径

			mMediaRecorder.setOutputFile(vedioPath);
			mMediaRecorder.setPreviewDisplay(mHolder.getSurface());
			// 设置尺寸使拍摄清晰

//			mHolder.setFixedSize(640, 480);
			// // 设置保存录像方向
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
				if (cameraPosition == 1) {
					// 由于不支持竖屏录制，后置摄像头需要把视频顺时针旋转90度、、但是视频本身在电脑上看还是逆时针旋转了90度
					mMediaRecorder.setOrientationHint(90);
				} else if (cameraPosition == 0) {
					// 由于不支持竖屏录制，前置摄像头需要把视频顺时针旋转270度、、而前置摄像头在电脑上则是顺时针旋转了90度
					mMediaRecorder.setOrientationHint(270);
				}
			}

			mMediaRecorder.setOnInfoListener(new OnInfoListener() {

				@Override
				public void onInfo(MediaRecorder mr, int what, int extra) {

				}
			});

			mMediaRecorder.setOnErrorListener(new OnErrorListener() {

				@Override
				public void onError(MediaRecorder mr, int what, int extra) {
					LogUtils.e("what:"+what);
					LogUtils.e("extra:"+extra);
					recodError();
				}
			});

			// 第6步:根据以上配置准备MediaRecorder

			mMediaRecorder.prepare();
			mMediaRecorder.start();
		} catch (IllegalStateException e) {
			e.printStackTrace();
			LogUtils.e("exception"+e.toString());
			recodError();
		} catch (IOException e) {
			e.printStackTrace();
			LogUtils.e("exception" + e.toString());
			recodError();
		} catch (RuntimeException e) {
			e.printStackTrace();
			LogUtils.e("exception" + e.toString());
			recodError();
		}

	}

	/**
	 * 异常处理
	 * 
	 * @version 1.0
	 * @createTime 2015年6月16日,上午10:49:18
	 * @updateTime 2015年6月16日,上午10:49:18
	 * @createAuthor WangYuWen
	 * @updateAuthor WangYuWen
	 * @updateInfo (此处输入修改内容,若无修改可不写.)
	 * 
	 */
	private void recodError() {

		Builder builder = new Builder(VideoNewActivity.this);
		builder.setMessage("该设备暂不支持视频录制");
		builder.setTitle("出错啦");
		builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				finish();
			}

		});
		builder.create().show();

	}

	/**
	 * 结束录制
	 * 
	 * @version 1.0
	 * @createTime 2015年6月15日,下午4:48:53
	 * @updateTime 2015年6月15日,下午4:48:53
	 * @createAuthor WangYuWen
	 * @updateAuthor WangYuWen
	 * @updateInfo (此处输入修改内容,若无修改可不写.)
	 * 
	 */
	private void stopRecord() {

		if (bean != null) {
			if (list.size() > 0) {
				int cuTime=old;
				for(VideoNewBean newBean : list){
					cuTime -= newBean.getTime();
				}
//				bean.setTime(old - list.get(list.size() - 1).getTime());
				bean.setTime(cuTime);
			} else {
				bean.setTime(now);
			}
			bean.setCameraPosition(cameraPosition);
			bean.setVideoId(System.currentTimeMillis());
			list.add(bean);
		}

		if (mMediaRecorder != null) {
			try {
				// 停止录像，释放camera
				mMediaRecorder.setOnErrorListener(null);
				mMediaRecorder.setOnInfoListener(null);
				mMediaRecorder.stop();
				// 清除recorder配置
				mMediaRecorder.reset();
				// 释放recorder对象
				mMediaRecorder.release();
				mMediaRecorder = null;
				// 没超过3秒就删除录制所有数据
				if (old < 3000) {
					clearList();
				}
			} catch (Exception e) {
				clearList();
			}
		}
	}

	/**
	 * 清楚数据
	 * 
	 * @version 1.0
	 * @createTime 2015年6月25日,下午6:04:28
	 * @updateTime 2015年6月25日,下午6:04:28
	 * @createAuthor WangYuWen
	 * @updateAuthor WangYuWen
	 * @updateInfo (此处输入修改内容,若无修改可不写.)
	 * 
	 */
	private void clearList() {
		Toast.makeText(VideoNewActivity.this, "单次录制视频最少3秒", Toast.LENGTH_LONG)
				.show();
		if (linear_seekbar.getChildCount() > 1) {
			linear_seekbar.removeViewAt(linear_seekbar.getChildCount() - 1);
			linear_seekbar.removeViewAt(linear_seekbar.getChildCount() - 1);
		}
		if (list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				File file = new File(list.get(list.size() - 1).getPath());
				if (file.exists()) {
					file.delete();
				}
			}
			list.remove(list.size() - 1);
			if (list.size() <= 0) {
				rl_delete.setVisibility(View.GONE);
				rl_enter.setVisibility(View.GONE);
				rl_video.setVisibility(View.VISIBLE);
//				img_camera.setVisibility(View.VISIBLE);

				rl_add_subtitle.setVisibility(View.INVISIBLE);
				rl_img_movie.setVisibility(View.VISIBLE);
				img_start.setImageResource(R.drawable.bt_shoot);
				start_tv.setVisibility(View.VISIBLE);
			}
		}
	}

	private static boolean isSupported(String value, List<String> supported) {
		return supported == null ? false : supported.indexOf(value) >= 0;
	}

	public static boolean getVideoQuality(String quality) {
		return "youtube".equals(quality) || "high".equals(quality);
	}

	/**
	 * 设置摄像头参数
	 * 
	 * @version 1.0
	 * @createTime 2015年6月15日,下午5:12:31
	 * @updateTime 2015年6月15日,下午5:12:31
	 * @createAuthor WangYuWen
	 * @updateAuthor WangYuWen
	 * @updateInfo (此处输入修改内容,若无修改可不写.)
	 * 
	 */
	private void readVideoPreferences() {
		String quality = mPreferences.getString("pref_video_quality_key",
				"high");

		boolean videoQualityHigh = getVideoQuality(quality);

		// 设置视频质量。
		Intent intent = getIntent();
		if (intent.hasExtra(MediaStore.EXTRA_VIDEO_QUALITY)) {
			int extraVideoQuality = intent.getIntExtra(
					MediaStore.EXTRA_VIDEO_QUALITY, 0);
			videoQualityHigh = (extraVideoQuality > 0);
		}

		videoQualityHigh = false;
		mProfile = CamcorderProfile
				.get(videoQualityHigh ? CamcorderProfile.QUALITY_HIGH
						: CamcorderProfile.QUALITY_LOW);
		mProfile.videoFrameWidth = (int) (mProfile.videoFrameWidth * 2.0f);
		mProfile.videoFrameHeight = (int) (mProfile.videoFrameHeight * 2.0f);
		mProfile.videoBitRate = 256000 * 3;

		CamcorderProfile highProfile = CamcorderProfile
				.get(CamcorderProfile.QUALITY_HIGH);
		mProfile.videoCodec = highProfile.videoCodec;
		mProfile.audioCodec = highProfile.audioCodec;
		mProfile.fileFormat = MediaRecorder.OutputFormat.MPEG_4;
	}

	/**
	 * 添加红色进度条
	 * 
	 * @version 1.0
	 * @createTime 2015年6月15日,下午3:04:21
	 * @updateTime 2015年6月15日,下午3:04:21
	 * @createAuthor WangYuWen
	 * @updateAuthor WangYuWen
	 * @updateInfo (此处输入修改内容,若无修改可不写.)
	 * 
	 */
	private void addView_Red() {
		ImageView img = new ImageView(VideoNewActivity.this);
		img.setBackgroundColor(getResources().getColor(R.color.text_red));
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
				DisplayUtil.dip2px(VideoNewActivity.this, 1),
				LinearLayout.LayoutParams.MATCH_PARENT);
		img.setLayoutParams(layoutParams);
		linear_seekbar.addView(img);
	}

	/**
	 * 添加黑色断条
	 * 
	 * @version 1.0
	 * @createTime 2015年6月15日,下午3:03:52
	 * @updateTime 2015年6月15日,下午3:03:52
	 * @createAuthor WangYuWen
	 * @updateAuthor WangYuWen
	 * @updateInfo (此处输入修改内容,若无修改可不写.)
	 * 
	 */
	private void addView_black() {
		ImageView img = new ImageView(VideoNewActivity.this);
		img.setBackgroundColor(Color.BLACK);
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
				DisplayUtil.dip2px(VideoNewActivity.this, 2),
				LinearLayout.LayoutParams.MATCH_PARENT);
		img.setLayoutParams(layoutParams);
		linear_seekbar.addView(img);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		setStartPreview(holder);

	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

		// 先开启在关闭 先开启录制在关闭可以 解决游览的时候比较卡顿的现象，但是会有视频开启时声音。打开这个功能时较慢
		// startRecord();
		// stopRecord();

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		releaseCamera();
	}

	/**
	 * 设置camera显示取景画面,并预览
	 * 
	 * @version 1.0
	 * @createTime 2015年6月16日,上午10:48:15
	 * @updateTime 2015年6月16日,上午10:48:15
	 * @createAuthor WangYuWen
	 * @updateAuthor WangYuWen
	 * @updateInfo (此处输入修改内容,若无修改可不写.)
	 * 
	 * @param holder
	 */
	private void setStartPreview(SurfaceHolder holder) {

		try {
			if (mCamera != null) {
				Log.e("num", "jinliai ");
				Parameters parameters = mCamera.getParameters();// 获取mCamera的参数对象
//				Size largestSize = getBestSupportedSize(parameters
//						.getSupportedPreviewSizes());
				WindowManager wm = this.getWindowManager();
				int width = wm.getDefaultDisplay().getWidth();
				int height = width;
				Size largestSize = getCloselyPreSize(true, width, height, parameters.getSupportedPreviewSizes());
//				parameters
//						.setPreviewSize(largestSize.width, largestSize.height);// 设置预览图片尺寸
				LogUtils.e("largestSize:"+largestSize.width+","+largestSize.height);
				parameters
						.setPreviewSize(640, 480);// 设置预览图片尺寸
				largestSize = getBestSupportedSize(parameters
						.getSupportedPictureSizes());// 设置捕捉图片尺寸
				parameters
						.setPictureSize(largestSize.width, largestSize.height);
				mCamera.setParameters(parameters);
				mCamera.setPreviewDisplay(holder);
				mCamera.startPreview();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private Size getBestSupportedSize(List<Size> sizes) {
		// 取能适用的最大的SIZE
		Size largestSize = sizes.get(0);
		// int largestArea = sizes.get(0).height * sizes.get(0).width;
		for (Size s : sizes) {
			// int area = s.width * s.height;
			if (s.width == s.height) {
				// largestArea = area;
				largestSize = s;
			}
		}
		return largestSize;
	}

	/**
	 * 释放Camera
	 * 
	 * @version 1.0
	 * @createTime 2015年6月16日,上午10:38:08
	 * @updateTime 2015年6月16日,上午10:38:08
	 * @createAuthor WangYuWen
	 * @updateAuthor WangYuWen
	 * @updateInfo (此处输入修改内容,若无修改可不写.)
	 * 
	 */
	private void releaseCamera() {
		if (mCamera != null) {
			mCamera.setPreviewCallback(null);
			mCamera.stopPreview();// 停掉原来摄像头的预览
			mCamera.release();
			mCamera = null;
		}
	}

	class CameraTimerTask extends TimerTask {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			if (mCamera != null) {
				mCamera.autoFocus(mAutoFocusCallback);
			}

		}

	}

	public  Camera.Size getCloselyPreSize(boolean isPortrait, int surfaceWidth, int surfaceHeight, List<Camera.Size> preSizeList) {
		int reqTmpWidth;
		int reqTmpHeight;
		// 当屏幕为垂直的时候需要把宽高值进行调换，保证宽大于高
		if (isPortrait) {
			reqTmpWidth = surfaceHeight;
			reqTmpHeight = surfaceWidth;
		} else {
			reqTmpWidth = surfaceWidth;
			reqTmpHeight = surfaceHeight;
		}
		//先查找preview中是否存在与surfaceview相同宽高的尺寸
		for(Camera.Size size : preSizeList){
			if((size.width == reqTmpWidth) && (size.height == reqTmpHeight)){
				return size;
			}
		}

		// 得到与传入的宽高比最接近的size
		float reqRatio = ((float) reqTmpWidth) / reqTmpHeight;
		float curRatio, deltaRatio;
		float deltaRatioMin = Float.MAX_VALUE;
		Camera.Size retSize = null;
		for (Camera.Size size : preSizeList) {
			curRatio = ((float) size.width) / size.height;
			deltaRatio = Math.abs(reqRatio - curRatio);
			if (deltaRatio < deltaRatioMin) {
				deltaRatioMin = deltaRatio;
				retSize = size;
			}
		}

		return retSize;
	}
}
