package com.tesu.manicurehouse.record;

import android.app.Dialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.google.gson.Gson;
import com.tesu.manicurehouse.R;
import com.tesu.manicurehouse.base.MyVolley;
import com.tesu.manicurehouse.play.Subtitle;
import com.tesu.manicurehouse.response.UploadFileResponse;
import com.tesu.manicurehouse.util.Constants;
import com.tesu.manicurehouse.util.DialogUtils;
import com.tesu.manicurehouse.util.LogUtils;
import com.tesu.manicurehouse.util.SharedPrefrenceUtils;
import com.tesu.manicurehouse.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;


public class VideoAddMusicActivity extends RecordBaseActivity {
	private static final String TAG = VideoAddMusicActivity.class.getSimpleName();
	/**视频控件*/
	private VideoView videoview;
	/**传过来的路径*/
	private String path;
	/**播放按钮*/
	private ImageView img_start;
	/**容器*/
	private RelativeLayout relative;
	private TextView addMusicTv;
	private TextView nextTv;
	private Dialog loadingDialog;
	private UploadFileResponse uploadFileResponse;
	private String fileUrl;
	private String subtitleUrl;

	private TextView subtitleText;
	private Subtitle subtitle;
	private Handler playerHandler;
	private Timer timer = new Timer();
	private TimerTask timerTask;
	private boolean isPrepared;

	private ImageView video_new_img_back;

	@Override
	protected int getContentViewId() {
		return R.layout.activity_video_add_music;
	}

	@Override
	protected void findViews() {
		videoview = (VideoView) findViewById(R.id.videoView);
		img_start = (ImageView) findViewById(R.id.img_start);
		relative = (RelativeLayout) findViewById(R.id.relative);
		addMusicTv = (TextView) findViewById(R.id.add_music_tv);
		nextTv = (TextView) findViewById(R.id.next_tv);
		subtitleText = (TextView) findViewById(R.id.subtitleText);
		video_new_img_back = (ImageView) findViewById(R.id.video_new_img_back);

//		MediaController mc = new MediaController(this);
//		videoview.setMediaController(mc);
		isPrepared = true;
	}
	
	@Override
	protected void initGetData() {
		super.initGetData();
		if (getIntent().getExtras()!=null) {
			path = getIntent().getExtras().getString("path");
//			path = Environment.getExternalStorageDirectory()
//					.getAbsolutePath() + "/videoTest/test12.mp4";
			subtitleUrl = getIntent().getExtras().getString("subtitleUrl");
		}

		loadingDialog = DialogUtils.createLoadDialog(VideoAddMusicActivity.this, false);
	}

	@Override
	protected void init() {
		if(!StringUtils.isEmpty(path)){
			videoview.setVideoPath(path);
			videoview.requestFocus();
			videoview.start();
//			MediaMetadataRetriever media = new MediaMetadataRetriever();
//			media.setDataSource(path);
//			Bitmap bitmap = media.getFrameAtTime();
//			image_show_iv.setImageBitmap(bitmap);
		}
		if(!StringUtils.isEmpty(subtitleUrl)){
			// 设置视频字幕
			subtitle = new Subtitle();
			subtitle.initSubtitleResource(subtitleUrl);

			playerHandler = new Handler() {
				public void handleMessage(Message msg) {

					if (videoview == null) {
						return;
					}

					// 刷新字幕
					subtitleText.setText(subtitle.getSubtitleByTime(videoview
							.getCurrentPosition()));

//					LogUtils.e("videotime111:"+videoview
//							.getCurrentPosition());

				}

				;
			};

			// 通过定时器和Handler来更新进度
			timerTask = new TimerTask() {
				@Override
				public void run() {

					if (!isPrepared) {
						return;
					}

					playerHandler.sendEmptyMessage(0);
				}
			};

			timer.schedule(timerTask, 0, 500);
		}

	}

	@Override
	protected void widgetListener() {
		relative.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (videoview.isPlaying()) {
					videoview.pause();
					img_start.setVisibility(View.VISIBLE);
					img_start.setImageResource(R.mipmap.bt_pause);
				} else {
					videoview.start();
					img_start.setVisibility(View.GONE);
				}
			}
		});
		addMusicTv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				uploadVideo();
//				Intent intent = new Intent(VideoAddMusicActivity.this,VideoChooseMusicActivity.class);
//				startActivity(intent);
			}
		});
		nextTv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(VideoAddMusicActivity.this, VideoActivity.class);
				intent.putExtra("fileUrl", fileUrl);
				if (!StringUtils.isEmpty(subtitleUrl)) {
					intent.putExtra("subtitleUrl", subtitleUrl);
				}
				intent.putExtra("path", path);
				startActivity(intent);
				setFinish();
//				finish();
			}
		});
		video_new_img_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == 100){
			if(resultCode==200){
				fileUrl = data.getStringExtra("fileUrl");
				videoview.setVideoPath(fileUrl);
				videoview.requestFocus();
				videoview.start();
			}else {
				fileUrl = "";
			}
		}
	}

	/**上传视频**/
	private void uploadVideo() {
		loadingDialog.show();
		String url= SharedPrefrenceUtils.getString(this,"uploadfile_url");
		if(StringUtils.isEmpty(url)){
		url= Constants.UPLOADFILE_URL;
		}
		LogUtils.e("url:"+url);
		Map<String,String> paramMap = new HashMap<String,String>();
		Map<String,String> filesMap = new HashMap<String,String>();
		filesMap.put("file", path);
		MyVolley.uploadWithFileWholeUrl(url, paramMap, filesMap, null, new MyVolley.VolleyCallback() {
			@Override
			public void dealWithJson(String address, String json) {
				loadingDialog.dismiss();
				Gson gson = new Gson();
				uploadFileResponse = gson.fromJson(json, UploadFileResponse.class);
				LogUtils.e("uploadFileResponse:" + json.toString());
				if (uploadFileResponse.getCode() == 0) {
					Intent intent = new Intent(VideoAddMusicActivity.this, VideoChooseMusicActivity.class);
					intent.putExtra("fileName", uploadFileResponse.getFileName());
					startActivityForResult(intent, 100);
				} else {
					DialogUtils.showAlertDialog(VideoAddMusicActivity.this,
							uploadFileResponse.getResultText());
				}

			}

			@Override
			public void dealWithError(String address, String error) {
				LogUtils.e("uploadFileError:" + error);
				loadingDialog.dismiss();
				DialogUtils.showAlertDialog(VideoAddMusicActivity.this,
						error);
			}
		});
	}

}

