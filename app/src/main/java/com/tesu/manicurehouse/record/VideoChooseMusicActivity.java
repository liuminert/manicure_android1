package com.tesu.manicurehouse.record;

import android.app.Dialog;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.google.gson.Gson;
import com.tesu.manicurehouse.R;
import com.tesu.manicurehouse.adapter.VideoAddMusicAdapter;
import com.tesu.manicurehouse.base.MyVolley;
import com.tesu.manicurehouse.bean.AudioBean;
import com.tesu.manicurehouse.response.ComposeVideoResponse;
import com.tesu.manicurehouse.response.GetAudioListResponse;
import com.tesu.manicurehouse.support.PercentRelativeLayout;
import com.tesu.manicurehouse.util.Constants;
import com.tesu.manicurehouse.util.DialogUtils;
import com.tesu.manicurehouse.util.LogUtils;
import com.tesu.manicurehouse.util.SharedPrefrenceUtils;
import com.tesu.manicurehouse.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class VideoChooseMusicActivity extends RecordBaseActivity implements MediaPlayer.OnPreparedListener {
	/**传过来的路径*/
	private String fileName;
	private TextView completeTv;
	private ImageView backIv;
	private ListView recommend_music_lv;
	private VideoAddMusicAdapter recommendMusicAdapter;
	private List<AudioBean> list;
	private Dialog loadingDialog;
	private GetAudioListResponse getAudioListResponse;
	private Gson gson;
	private MediaPlayer mediaPlayer; // 媒体播放器
	private int audioId;
	private ImageView no_music_iv;
	private PercentRelativeLayout no_music_rl;
	private boolean isSelectedNoMusic;
	private ComposeVideoResponse composeVideoResponse;

	@Override
	protected int getContentViewId() {
		return R.layout.activity_video_choose_music;
	}

	@Override
	protected void findViews() {
		completeTv = (TextView) findViewById(R.id.complete_tv);
		backIv = (ImageView) findViewById(R.id.video_new_img_back);
		recommend_music_lv = (ListView) findViewById(R.id.recommend_music_lv);
		no_music_iv = (ImageView) findViewById(R.id.no_music_iv);
		no_music_rl = (PercentRelativeLayout) findViewById(R.id.no_music_rl);
	}

	@Override
	protected void initGetData() {
		super.initGetData();
		if (getIntent().getExtras()!=null) {
			fileName = getIntent().getExtras().getString("fileName");
		}

		loadingDialog = DialogUtils.createLoadDialog(VideoChooseMusicActivity.this, false);
		gson = new Gson();

	}

	private void initMediaPlayer(){
		mediaPlayer = new MediaPlayer();
		mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);// 设置媒体流类型
		mediaPlayer.setOnPreparedListener(this);
	}
	@Override
	protected void init() {
		list = new ArrayList<AudioBean>();
//		for(int i =0;i<10;i++){
//			AudioBean audioBean = new AudioBean();
//			audioBean.setAudio_id(i);
//			audioBean.setAudio_name("测试"+i);
//			audioBean.setIsChecked(false);
//			list.add(audioBean);
//		}
//
//		AudioBean audioBean = new AudioBean();
//		audioBean.setAudio_id(11);
//		audioBean.setAudio_name("测试" + 11);
//		audioBean.setIsChecked(true);
//		list.add(audioBean);



		isSelectedNoMusic = true;

       getAudioList();
	}

	private void getAudioList() {
		loadingDialog.show();
		String url="video/getAudioList.do";
		Map<String,String> params = new HashMap<String,String>();

		MyVolley.uploadNoFile(MyVolley.POST, url, params, new MyVolley.VolleyCallback() {
			@Override
			public void dealWithJson(String address, String json) {
				LogUtils.e("获得音频列表：" + json);
				loadingDialog.dismiss();
				getAudioListResponse = gson.fromJson(json, GetAudioListResponse.class);
				if (getAudioListResponse.getCode() == 0) {
					list = getAudioListResponse.getDataList();
					recommendMusicAdapter = new VideoAddMusicAdapter(VideoChooseMusicActivity.this, list);
					recommend_music_lv.setAdapter(recommendMusicAdapter);

				} else {
					DialogUtils.showAlertDialog(VideoChooseMusicActivity.this, getAudioListResponse.getResultText());
				}

			}

			@Override
			public void dealWithError(String address, String error) {
				LogUtils.e("获得音频列表错误：" + error);
				loadingDialog.dismiss();
				DialogUtils.showAlertDialog(VideoChooseMusicActivity.this, error);

			}
		});
	}

	@Override
	protected void widgetListener() {
		backIv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		completeTv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				if(audioId ==0){
					Intent intent = new Intent(VideoChooseMusicActivity.this,VideoAddMusicActivity.class);
					setResult(300, intent);
					finish();
				}else {
					stop();
					composeVedio();
				}
			}
		});
		recommend_music_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				AudioBean audioBean = list.get(position);
				stop();
				if(audioBean.isChecked()){
					audioBean.setIsChecked(false);
					audioId = 0;
				}else {
					for (AudioBean audioBean1 : list) {
						audioBean1.setIsChecked(false);
					}
					initMediaPlayer();
					playUrl(audioBean.getFile_url());
					audioId = audioBean.getAudio_id();
					isSelectedNoMusic = false;
					no_music_iv.setImageResource(R.drawable.ic_checkbox_unselected);
					audioBean.setIsChecked(true);
				}
				recommendMusicAdapter.notifyDataSetChanged();


			}
		});
		no_music_rl.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (isSelectedNoMusic) {
					isSelectedNoMusic = false;
					no_music_iv.setImageResource(R.drawable.ic_checkbox_unselected);
				} else {
					audioId = 0;
					isSelectedNoMusic = true;
					stop();
					no_music_iv.setImageResource(R.drawable.ic_checkbox_selected);
					if (list != null) {
						for (AudioBean audioBean : list) {
							audioBean.setIsChecked(false);
							recommendMusicAdapter.notifyDataSetChanged();
						}
					}
				}
			}
		});
	}

	/**合成音视频**/
	private void composeVedio() {
		loadingDialog.show();
		String url= SharedPrefrenceUtils.getString(this,"synthesis_url");
		if(StringUtils.isEmpty(url)){
		url= Constants.COMPOSEVEDIO_URL;
		}
		Map<String,String> params = new HashMap<String,String>();
		params.put("video_name",fileName);
		params.put("audio_id",audioId+"");

		LogUtils.e("audioId:" + audioId);
		LogUtils.e("fileName:"+fileName);
		MyVolley.uploadNoFileWholeUrl(MyVolley.POST, url, params, new MyVolley.VolleyCallback() {
			@Override
			public void dealWithJson(String address, String json) {
				LogUtils.e("合成音视频：" + json);
				loadingDialog.dismiss();
				composeVideoResponse = gson.fromJson(json, ComposeVideoResponse.class);
				if (composeVideoResponse.getCode() == 0) {
					Intent intent = new Intent(VideoChooseMusicActivity.this,VideoAddMusicActivity.class);
					intent.putExtra("fileUrl",composeVideoResponse.getFile_url());
					setResult(200, intent);
					finish();

				} else {
					DialogUtils.showAlertDialog(VideoChooseMusicActivity.this, composeVideoResponse.getResultText());
				}

			}

			@Override
			public void dealWithError(String address, String error) {
				LogUtils.e("合成音视频：" + error);
				loadingDialog.dismiss();
				DialogUtils.showAlertDialog(VideoChooseMusicActivity.this, error);

			}
		});
	}

	/**
	 *
	 * @param url
	 *            url地址
	 */
	public void playUrl(String url) {
		try {
			mediaPlayer.reset();
			mediaPlayer.setDataSource(url); // 设置数据源
			mediaPlayer.prepare(); // prepare自动播放
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		stop();
	}

	// 暂停
	public void pause() {
		mediaPlayer.pause();
	}

	// 停止
	public void stop() {
		if (mediaPlayer != null) {
			mediaPlayer.stop();
			mediaPlayer.release();
			mediaPlayer = null;
		}
	}

	@Override
	public void onPrepared(MediaPlayer mp) {
		mp.start();
	}
}
