package com.tesu.manicurehouse.play;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.tesu.manicurehouse.R;
import com.tesu.manicurehouse.record.RecordBaseActivity;
import com.tesu.manicurehouse.util.DialogUtils;
import com.tesu.manicurehouse.util.StringUtils;
import com.tesu.manicurehouse.widget.MyVideoView;



public class MediaPlayActivity extends RecordBaseActivity implements OnClickListener {
    private static final String TAG = MediaPlayActivity.class.getSimpleName();
    private String mPath;
    private String subtitleUr;


    private TextView subtitleText;
    private Subtitle subtitle;
    private Handler playerHandler;
    private Timer timer = new Timer();
    private TimerTask timerTask;
    private boolean isPrepared;
    private TextView tv_top_back;

    private VideoView videoview;
    /**播放按钮*/
    private ImageView img_start;
    private RelativeLayout relative;
    private int  currentTime;


    @Override
    protected int getContentViewId() {
        return R.layout.activity_media_play;
    }

    @Override
    protected void findViews() {
        tv_top_back = (TextView) findViewById(R.id.tv_top_back);
        img_start = (ImageView) findViewById(R.id.img_start);
        subtitleText = (TextView) findViewById(R.id.subtitleText);
        videoview = (VideoView) findViewById(R.id.videoView);
        relative = (RelativeLayout) findViewById(R.id.relative);

        MediaController mc = new MediaController(this); //显示进度条和播放按钮
        videoview.setMediaController(mc);//设置VedioView与MediaController相关联

        isPrepared = true;
    }

    @Override
    protected void initGetData() {
        super.initGetData();
        Intent intent = getIntent();
        mPath = intent.getStringExtra("path");
        subtitleUr = intent.getStringExtra("subtitleUr");
//        if (mPath == null) {
//            mPath = "http://121.201.66.7:8080/manicure_videocomp/videoFiles/shipin.mp4";
//        }
        currentTime = intent.getIntExtra("currenttime", 0);
        Log.e(TAG, "path:" + mPath);
    }

    @Override
    protected void init() {
        // 设置视频字幕

        if(!StringUtils.isEmpty(mPath)){
            videoview.setVideoPath(mPath);
            videoview.requestFocus();
            videoview.seekTo(currentTime);
            videoview.start();

        }
        if(!StringUtils.isEmpty(subtitleUr)){
            subtitle = new Subtitle();
            subtitle.initSubtitleResource(subtitleUr);
//        subtitle.initSubtitleResource("http://meijiawu.b0.upaiyun.com/uploads/20160913/ruc5moy5xzvgksg5pv98ac1nhqdb8sm4.srt");

            playerHandler = new Handler() {
                public void handleMessage(Message msg) {

                    if (videoview == null) {
                        return;
                    }

                    // 刷新字幕
                    subtitleText.setText(subtitle.getSubtitleByTime(videoview
                            .getCurrentPosition()));

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

            timer.schedule(timerTask, 0, 1000);
        }
    }

    @Override
    public void onBackPressed() {
        int cuTime = videoview.getCurrentPosition();
        Intent intent = getIntent();
        intent.putExtra("cuTime",cuTime);
        setResult(200,intent);
        finish();
    }

    @Override
    protected void widgetListener() {
        tv_top_back.setOnClickListener(this);
        relative.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
//            case R.id.relative:
//                if (videoview.isPlaying()) {
//                    videoview.pause();
//                    img_start.setVisibility(View.VISIBLE);
//                } else {
//                    videoview.start();
//                    img_start.setVisibility(View.GONE);
//                }
//                break;
        }

    }
}
