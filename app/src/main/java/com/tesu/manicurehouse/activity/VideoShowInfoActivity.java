package com.tesu.manicurehouse.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.tesu.manicurehouse.R;
import com.tesu.manicurehouse.adapter.LookAdapter;
import com.tesu.manicurehouse.adapter.ShareAdapter;
import com.tesu.manicurehouse.adapter.VideoCommentsAdapter;
import com.tesu.manicurehouse.adapter.VideoLableAdapter;
import com.tesu.manicurehouse.base.BaseActivity;
import com.tesu.manicurehouse.base.MyVolley;
import com.tesu.manicurehouse.bean.GoodsAttrBean;
import com.tesu.manicurehouse.bean.ShareDateBean;
import com.tesu.manicurehouse.bean.ShareInfo;
import com.tesu.manicurehouse.bean.VideoInfoBean;
import com.tesu.manicurehouse.bean.VideoLableBean;
import com.tesu.manicurehouse.bean.VideoListBean;
import com.tesu.manicurehouse.play.MediaPlayActivity;
import com.tesu.manicurehouse.play.Subtitle;
import com.tesu.manicurehouse.protocol.AddVideoPlayOrForwardCntProtocol;
import com.tesu.manicurehouse.protocol.GetFollowInfoProtocol;
import com.tesu.manicurehouse.protocol.GetVideoCommentListProtocol;
import com.tesu.manicurehouse.protocol.ProblemFeedbackProtocol;
import com.tesu.manicurehouse.protocol.ReplyVideoCommentProtocol;
import com.tesu.manicurehouse.protocol.SubmitVideoCommentProtocol;
import com.tesu.manicurehouse.record.VideoShowPhotoActivity;
import com.tesu.manicurehouse.request.AddVideoPlayOrForwardCntRequest;
import com.tesu.manicurehouse.request.GetFollowInfoRequest;
import com.tesu.manicurehouse.request.GetVideoCommentListRequest;
import com.tesu.manicurehouse.request.ProblemFeedbackRequest;
import com.tesu.manicurehouse.request.ReplyVideoCommentRequest;
import com.tesu.manicurehouse.request.SubmitVideoCommentRequest;
import com.tesu.manicurehouse.response.AddVideoPlayOrForwardCntResponse;
import com.tesu.manicurehouse.response.BaseResponse;
import com.tesu.manicurehouse.response.GetFollowInfoResponse;
import com.tesu.manicurehouse.response.GetVideoCommentListResponse;
import com.tesu.manicurehouse.response.GetVideoListResponse;
import com.tesu.manicurehouse.response.ProblemFeedbackResponse;
import com.tesu.manicurehouse.response.ReplyInformationCommentResponse;
import com.tesu.manicurehouse.response.SubmitVideoCommentResponse;
import com.tesu.manicurehouse.response.VideoInfoResponse;
import com.tesu.manicurehouse.support.PercentLinearLayout;
import com.tesu.manicurehouse.support.PercentRelativeLayout;
import com.tesu.manicurehouse.util.BitmapUtils;
import com.tesu.manicurehouse.util.Constants;
import com.tesu.manicurehouse.util.DialogUtils;
import com.tesu.manicurehouse.util.LogUtils;
import com.tesu.manicurehouse.util.LoginUtils;
import com.tesu.manicurehouse.util.PictureOption;
import com.tesu.manicurehouse.util.SharedPrefrenceUtils;
import com.tesu.manicurehouse.util.StringUtils;
import com.tesu.manicurehouse.util.UIUtils;
import com.tesu.manicurehouse.util.VideoUtil;
import com.tesu.manicurehouse.widget.CircleImageView;
import com.tesu.manicurehouse.widget.CustomExpandableListView;
import com.tesu.manicurehouse.widget.HorizontalListView;
import com.tesu.manicurehouse.widget.MyVideoView;
import com.tesu.manicurehouse.widget.XExpandableListView;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Formatter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;
import io.vov.vitamio.Vitamio;

/**
 * 类描述：
 * 创建人：许明达
 * 创建时间：2016/8/16 11:40
 * 视频详情
 */
public class VideoShowInfoActivity extends BaseActivity implements View.OnClickListener, PlatformActionListener, MediaPlayer.OnCompletionListener,
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnBufferingUpdateListener {

    private RelativeLayout rl_comment;
    private PullToRefreshScrollView sv;
    private ScrollView sv1;
    private ImageView iv_share;
    private ImageView iv_top_back;
    private EditText et_comment;
    private VideoCommentsAdapter videoCommentsAdapter;
    private CustomExpandableListView lv_comment;
    private TextView tv_goto_comment;
    //交作业
    private TextView tv_submit_homework;
    //一键下单
    private TextView tv_one_key_order;
    private List<List<String>> lists;
    private List<List<String>> templists;
    private List<String> templist;
    private List<String> firstsecondlist;
    private GridView gv_lable;
    //    private LableAdapter lableAdapter;
    private TextView tv_goto_attention;
    private PercentRelativeLayout prl_share;
    private ShareAdapter sadapter;
    private GridView gv_share;
    /**
     * 分享的第三方软件名字
     */
    public List<String> names = null;
    private PercentLinearLayout rl_share;
    private boolean isopen = false;
    //    private View v_graphic_tutorials;
//    private RelativeLayout rl_look_graphic_tutorials;
    private int video_id;
    private String userId;
    private VideoInfoResponse videoInfoResponse;
    private Gson gson;
    private Dialog loadingDialog;
    private String url;
    private VideoInfoBean videoInfoBean;

    private CircleImageView iv_uesr_img;
    private TextView tv_user_name;
    private TextView tv_look_num;
    private TextView tv_date;
    private ImageView iv_vdieo_image;
    //    private MyVideoView videoView;
    private TextView subtitleText;
    private PercentRelativeLayout rl_video_start;
    private ImageView iv_video_start;

    private TextView user_share_video;
    private TextView user_collection_video;
    private TextView user_like_num;
    private ShareDateBean shareDateBean;

    private LayoutInflater layoutInflater;
    private DisplayImageOptions options;
    private List<VideoLableBean> videoLableBeanList;
    private List<VideoLableBean> showVideoLableBeanList;
    private VideoLableAdapter videoLableAdapter;

    private Subtitle subtitle;
    private Handler playerHandler;
    private Timer timer = new Timer();
    private TimerTask timerTask;
    private boolean isPrepared;
    private String videoUrl;
    private String subiUrl;


    private PercentRelativeLayout prl_collection;
    private PercentRelativeLayout prl_like;
    private ImageView iv_collection;
    private ImageView iv_liked;

    private int isLiked;
    private int isCollection;
    private BaseResponse baseResponse;
    private List<GetVideoCommentListResponse.VideoCommentBean> videoCommentBeanList;
    //页数
    private int pageNo = 1;
    //个数
    private int pageSize = 1000;
    //评论Id
    private String comment_id;
    //父id(如果是回复评论的就填0，如果回复的是回复的，就填reply_id跟帖id)
    private String parent_id;
    private boolean isReply = false;
    //评论内容
    private String content;
    private TextView tv_cancle;
    private TextView tv_comment_num;
    private boolean isRefresh = false;

    private HorizontalListView lv_look;
    private LookAdapter lookAdapter;
    private List<VideoListBean> lookVideos;
    private GetVideoListResponse getVideoListResponse;

    //    private MyVideoView videoview;
    private TextView tv_error;
    private TextView tv_title;
    private ImageView iv_full_screen;
    private Dialog mDialog;
    private ImageView placeholder;

    private SeekBar seekBar;
    private boolean isError;  //是否纠错
    private String cover_img;

    private TextView tv_time;
    private String maxShowTime;

    private ImageLoader imageLoader;


    private SurfaceView surfaceView;
    /**
     * surfaceView播放控制
     */
    private SurfaceHolder surfaceHolder;
    /**
     * 播放视频
     */
    private MediaPlayer mediaPlayer;
    /**
     * 记录当前播放的位置
     */
    private int playPosition = -1;
    /**
     * seekBar是否自动拖动
     */
    private boolean seekBarAutoFlag = false;

    private String videoTimeString;

    private int videoTimeLong;

    /**
     * 屏幕的宽度和高度
     */
    private int screenWidth, screenHeight;
    private int screenType;  //0 窗口，  1 全屏
    private int cuTime;

    private boolean isVideoPrepared;
    private GetFollowInfoResponse getFollowInfoResponse;
    private PercentLinearLayout pl_look;
    private boolean ismy;   //是否从我的作品，我的收藏跳转过来
    private boolean isReady;  //视频是否准备好
    private LinearLayout ll_text_all;

    @Override
    protected View initView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Vitamio.isInitialized(getApplicationContext());
        View rootView = View.inflate(this, R.layout.activity_video_show_info, null);
        setContentView(rootView);
        sv1 = (ScrollView) rootView.findViewById(R.id.sv1);
        sv = (PullToRefreshScrollView) rootView.findViewById(R.id.sv);
        gv_lable = (GridView) rootView.findViewById(R.id.gv_lable);
        lv_comment = (CustomExpandableListView) rootView.findViewById(R.id.lv_comment);
        iv_share = (ImageView) rootView.findViewById(R.id.iv_share);
        tv_error = (TextView) rootView.findViewById(R.id.tv_error);
        iv_top_back = (ImageView) rootView.findViewById(R.id.iv_top_back);
        tv_goto_attention = (TextView) rootView.findViewById(R.id.tv_goto_attention);
        rl_comment = (RelativeLayout) rootView.findViewById(R.id.rl_comment);
        et_comment = (EditText) rootView.findViewById(R.id.et_comment);
        tv_submit_homework = (TextView) rootView.findViewById(R.id.tv_submit_homework);
        tv_goto_comment = (TextView) rootView.findViewById(R.id.tv_goto_comment);
        tv_one_key_order = (TextView) rootView.findViewById(R.id.tv_one_key_order);
        prl_share = (PercentRelativeLayout) rootView.findViewById(R.id.prl_share);
        gv_share = (GridView) rootView.findViewById(R.id.gv_share);
        rl_share = (PercentLinearLayout) rootView.findViewById(R.id.rl_share);
        tv_comment_num = (TextView) rootView.findViewById(R.id.tv_comment_num);
//        v_graphic_tutorials=(View)rootView.findViewById(R.id.v_graphic_tutorials);
//        rl_look_graphic_tutorials=(RelativeLayout)rootView.findViewById(R.id.rl_look_graphic_tutorials);

        iv_uesr_img = (CircleImageView) rootView.findViewById(R.id.iv_uesr_img);
        tv_user_name = (TextView) rootView.findViewById(R.id.tv_user_name);
        tv_look_num = (TextView) rootView.findViewById(R.id.tv_look_num);
        tv_date = (TextView) rootView.findViewById(R.id.tv_date);
        iv_vdieo_image = (ImageView) rootView.findViewById(R.id.iv_vdieo_image);
        rl_video_start = (PercentRelativeLayout) rootView.findViewById(R.id.rl_video_start);
        iv_video_start = (ImageView) rootView.findViewById(R.id.iv_video_start);
        user_share_video = (TextView) rootView.findViewById(R.id.user_share_video);
        user_collection_video = (TextView) rootView.findViewById(R.id.user_collection_video);
        user_like_num = (TextView) rootView.findViewById(R.id.user_like_num);
        prl_collection = (PercentRelativeLayout) rootView.findViewById(R.id.prl_collection);
        prl_like = (PercentRelativeLayout) rootView.findViewById(R.id.prl_like);
        iv_collection = (ImageView) rootView.findViewById(R.id.iv_collection);
        iv_liked = (ImageView) rootView.findViewById(R.id.iv_liked);
        tv_cancle = (TextView) rootView.findViewById(R.id.tv_cancle);
        lv_look = (HorizontalListView) rootView.findViewById(R.id.lv_look);

//        videoview = (MyVideoView) findViewById(R.id.videoView);
        subtitleText = (TextView) findViewById(R.id.subtitleText);
        tv_title = (TextView) findViewById(R.id.tv_title);
        iv_full_screen = (ImageView) findViewById(R.id.iv_full_screen);
        pl_look = (PercentLinearLayout) findViewById(R.id.pl_look);

        placeholder = (ImageView) findViewById(R.id.placeholder);
        isPrepared = true;

        seekBar = (SeekBar) findViewById(R.id.seekBar);
        tv_time = (TextView) findViewById(R.id.tv_time);
        ll_text_all = (LinearLayout) findViewById(R.id.ll_text_all);

        // 获取屏幕的宽度和高度
        DisplayMetrics displayMetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenWidth = displayMetrics.widthPixels;
        screenHeight = displayMetrics.heightPixels;

        surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        surfaceHolder = surfaceView.getHolder();
        // 设置Holder类型,该类型表示surfaceView自己不管理缓存区,虽然提示过时，但最好还是要设置
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        // 设置surface回调
        surfaceHolder.addCallback(new SurfaceCallback());

        seekBar.setOnSeekBarChangeListener(change);

//        MediaController mc = new MediaController(this); //显示进度条和播放按钮
//        videoview.setMediaController(mc);//设置VedioView与MediaController相关联
//        mc.setMediaPlayer(videoview);

//        videoview.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//            @Override
//            public void onPrepared(MediaPlayer mp) {
//               mp.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
//                   @Override
//                   public void onBufferingUpdate(MediaPlayer mp, int percent) {
//                       int current = mp.getCurrentPosition();
//                       int maxTime = mp.getDuration();
//
//                       seekBar.setMax(maxTime);
//                       seekBar.setProgress(current);
//
//                       maxShowTime = makeTimeString(maxTime);
//
//                       tv_time.setText(makeTimeString(current) + "/" + maxShowTime);
//                   }
//               });
//            }
//        });

//        new Thread() {
//
//            @Override
//            public void run() {
//                try {
//                    while (videoview.isPlaying()) {
//                        // 如果正在播放，没0.5.毫秒更新一次进度条
//                        int current = videoview.getCurrentPosition();
//                        seekBar.setProgress(current);
//                        int duration = videoview.getDuration();
//                        int time = ((current * 100) / duration);
//                        LogUtils.e("当前时间:"+time);
//                        LogUtils.e("当前进度:"+current);
//                        tv_time.setText(makeTimeString(time)+"/" + maxShowTime);
//                        sleep(500);
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }.start();

        tv_cancle.setOnClickListener(this);
        prl_share.setOnClickListener(this);
        iv_share.setOnClickListener(this);
        iv_top_back.setOnClickListener(this);
        tv_goto_comment.setOnClickListener(this);
        tv_submit_homework.setOnClickListener(this);
        tv_one_key_order.setOnClickListener(this);
        tv_goto_attention.setOnClickListener(this);
//        rl_look_graphic_tutorials.setOnClickListener(this);
        rl_video_start.setOnClickListener(this);
//        videoView.setOnCompletionListener(this);
        prl_collection.setOnClickListener(this);
        prl_like.setOnClickListener(this);
        tv_error.setOnClickListener(this);
        iv_full_screen.setOnClickListener(this);
        iv_uesr_img.setOnClickListener(this);

        layoutInflater = LayoutInflater.from(this);
        if (!ImageLoader.getInstance().isInited()) {
            ImageLoader.getInstance()
                    .init(ImageLoaderConfiguration.createDefault(UIUtils
                            .getContext()));
        }
        // 使用DisplayImageOption.Builder()创建DisplayImageOptions
        options = new DisplayImageOptions.Builder()
                .showStubImage(R.drawable.pic_s) // 设置图片下载期间显示的图片
                .showImageForEmptyUri(R.drawable.pic_s) // 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.drawable.pic_s) // 设置图片加载或解码过程中发生错误显示的图片
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisc(true) // 设置下载的图片是否缓存在SD卡中
                        // .displayer(new RoundedBitmapDisplayer(20)) // 设置成圆角图片
                .bitmapConfig(Bitmap.Config.ARGB_8888)
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                .build(); // 创建配置过的DisplayImageOption对象

        initData();
        return null;
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
//        seekBar.setSecondaryProgress(percent);
        LogUtils.e("percent:"+percent);
//        if(percent == 100){
//            loadingDialog.dismiss();
//        }else {
//            loadingDialog.show();
//        }

    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
//        switch (what) {
//            case MediaPlayer.MEDIA_ERROR_UNKNOWN:
//                Toast.makeText(this, "MEDIA_ERROR_UNKNOWN", Toast.LENGTH_SHORT).show();
//                break;
//            case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
//                Toast.makeText(this, "MEDIA_ERROR_SERVER_DIED", Toast.LENGTH_SHORT).show();
//                break;
//            default:
//                break;
//        }
//
//        switch (extra) {
//            case MediaPlayer.MEDIA_ERROR_IO:
//                Toast.makeText(this, "MEDIA_ERROR_IO", Toast.LENGTH_SHORT).show();
//                break;
//            case MediaPlayer.MEDIA_ERROR_MALFORMED:
//                Toast.makeText(this, "MEDIA_ERROR_MALFORMED", Toast.LENGTH_SHORT).show();
//                break;
//            case MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK:
//                Toast.makeText(this, "MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK",
//                        Toast.LENGTH_SHORT).show();
//                break;
//            case MediaPlayer.MEDIA_ERROR_TIMED_OUT:
//                Toast.makeText(this, "MEDIA_ERROR_TIMED_OUT", Toast.LENGTH_SHORT).show();
//                break;
//            case MediaPlayer.MEDIA_ERROR_UNSUPPORTED:
//                Toast.makeText(this, "MEDIA_ERROR_UNSUPPORTED", Toast.LENGTH_SHORT).show();
//                break;
//        }
//        Toast.makeText(this, "视频文件格式错误", Toast.LENGTH_SHORT).show();
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        // 判断是否有保存的播放位置,防止屏幕旋转时，界面被重新构建，播放位置丢失。
        if (playPosition >= 0) {
            mediaPlayer.seekTo(playPosition);
            playPosition = -1;
            // surfaceHolder.unlockCanvasAndPost(Constants.getCanvas());
        }
        seekBarAutoFlag = true;
        // 设置控制条,放在加载完成以后设置，防止获取getDuration()错误
        seekBar.setMax(mediaPlayer.getDuration());
        // 设置播放时间
        videoTimeLong = mediaPlayer.getDuration();
        videoTimeString = makeTimeString(videoTimeLong);
        videoTimeString = videoTimeString.substring(3);
        tv_time.setText("00:00/" + videoTimeString);
        // 设置拖动监听事件
        seekBar.setOnSeekBarChangeListener(change);
        // 设置按钮监听事件
        // 播放视频
//        mediaPlayer.start();
        // 设置显示到屏幕
        mediaPlayer.setDisplay(surfaceHolder);
        // 开启线程 刷新进度条
        new Thread(runnable).start();
        // 设置surfaceView保持在屏幕上
        mediaPlayer.setScreenOnWhilePlaying(true);
        surfaceHolder.setKeepScreenOn(true);

        isVideoPrepared = true;
        if(loadingDialog != null){
            loadingDialog.dismiss();
        }
//        Toast.makeText(VideoShowInfoActivity.this, "视频已经缓冲完成，可以播放", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        seekBar.setProgress(0);
        mediaPlayer.pause();
        cuTime = 0;
        iv_video_start.setVisibility(View.VISIBLE);
        iv_video_start.setImageResource(R.mipmap.bt_video);
//        seekBar.setProgress(Integer.parseInt(String.valueOf(videoTimeLong)));
//        // 设置播放标记为false
//        seekBarAutoFlag = false;
    }

    /**
     * 滑动条变化线程
     */
    private Runnable runnable = new Runnable() {

        public void run() {
            // TODO Auto-generated method stub
            // 增加对异常的捕获，防止在判断mediaPlayer.isPlaying的时候，报IllegalStateException异常
            try {
                while (seekBarAutoFlag) {
                    /*
                     * mediaPlayer不为空且处于正在播放状态时，使进度条滚动。
                     * 通过指定类名的方式判断mediaPlayer防止状态发生不一致
                     */

                    if (null != VideoShowInfoActivity.this.mediaPlayer
                            && VideoShowInfoActivity.this.mediaPlayer.isPlaying()) {
                        seekBar.setProgress(mediaPlayer.getCurrentPosition());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    // SurfaceView的callBack
    private class SurfaceCallback implements SurfaceHolder.Callback {
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        }

        public void surfaceCreated(SurfaceHolder holder) {
            // surfaceView被创建
            // 设置播放资源
            playVideo();
        }

        public void surfaceDestroyed(SurfaceHolder holder) {
            // surfaceView销毁,同时销毁mediaPlayer
            if (null != mediaPlayer) {
                mediaPlayer.release();
                mediaPlayer = null;
            }

        }

    }

    /**
     * 播放视频
     */
    public void playVideo() {
        // 初始化MediaPlayer
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
        }
        // 重置mediaPaly,建议在初始滑mediaplay立即调用。
        mediaPlayer.reset();
        // 设置声音效果
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        // 设置播放完成监听
        mediaPlayer.setOnCompletionListener(this);
        // 设置媒体加载完成以后回调函数。
        mediaPlayer.setOnPreparedListener(this);
        // 错误监听回调函数
        mediaPlayer.setOnErrorListener(this);
        // 设置缓存变化监听
        mediaPlayer.setOnBufferingUpdateListener(this);
//        Uri uri = Uri.parse("http://183.61.13.14/vmind.qqvideo.tc.qq.com/x0200hkt1cg.p202.1.mp4");
        if (!TextUtils.isEmpty(videoUrl)) {
            Uri uri = Uri.parse(videoUrl);
            try {
                // mediaPlayer.reset();
//            mediaPlayer.setDataSource(pathString);
                mediaPlayer.setDataSource(this, uri);
                // mediaPlayer.setDataSource(SurfaceViewTestActivity.this, uri);
                // 设置异步加载视频，包括两种方式 prepare()同步，prepareAsync()异步
                mediaPlayer.prepareAsync();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "加载视频错误！", Toast.LENGTH_LONG).show();
            }
        }
    }

    private SeekBar.OnSeekBarChangeListener change = new SeekBar.OnSeekBarChangeListener() {

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            // 当进度条停止修改的时候触发
            // 取得当前进度条的刻度
//            int progress = seekBar.getProgress();
////            int progress = videoview.getDuration()*seekBar.getProgress()/100;
//            if (videoview != null && videoview.isPlaying()) {
//                // 设置当前播放的位置
//                videoview.seekTo(progress);
//                videoview.start();
//            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {
            if (progress >= 0) {
                // 如果是用户手动拖动控件，则设置视频跳转。
                if (fromUser) {
                    mediaPlayer.seekTo(progress);
                }
                // 设置当前播放时间
                String cTime = makeTimeString(progress);
                cTime = cTime.substring(3);
                tv_time.setText(cTime + "/" + videoTimeString);
            }
        }
    };

    public void initData() {
        videoCommentBeanList = new ArrayList<GetVideoCommentListResponse.VideoCommentBean>();
        imageLoader = ImageLoader.getInstance();

        //设置分享数据
        Intent intent = getIntent();
        video_id = intent.getIntExtra("video_id", 0);
        userId = SharedPrefrenceUtils.getString(this, "user_id");
        cover_img = intent.getStringExtra("cover_img");
        videoUrl = intent.getStringExtra("video_url");
        if(TextUtils.isEmpty(videoUrl)){
            isReady = false;
        }else{
            isReady = true;
        }
        LogUtils.e("cover_img:" + cover_img);
        LogUtils.e("videoUrl:" + videoUrl);
        if (!StringUtils.isEmpty(cover_img)) {
            imageLoader.displayImage(cover_img, placeholder, PictureOption.getSimpleOptions());
        }

        ismy = intent.getBooleanExtra("ismy", false);
        if (ismy) {
            pl_look.setVisibility(View.GONE);
        } else {
            pl_look.setVisibility(View.VISIBLE);
        }
        gson = new Gson();
        loadingDialog = DialogUtils.createLoadDialog(VideoShowInfoActivity.this, true);
        showVideoLableBeanList = new ArrayList<VideoLableBean>();

        getVideoInfo();

        List<ShareInfo> slist = new ArrayList<ShareInfo>();
        ShareInfo share1 = new ShareInfo();
        share1.setShare_photo(R.drawable.img_qq);
        share1.setShare_photo_selected(R.drawable.img_qq);
        share1.setShare_name(QQ.NAME);
        slist.add(share1);

        ShareInfo share3 = new ShareInfo();
        share3.setShare_photo(R.drawable.img_qzone);
        share3.setShare_photo_selected(R.drawable.img_qzone);
        share3.setShare_name(QZone.NAME);
        slist.add(share3);

        ShareInfo share2 = new ShareInfo();
        share2.setShare_photo(R.drawable.img_sina);
        share2.setShare_photo_selected(R.drawable.img_sina);
        share2.setShare_name(SinaWeibo.NAME);
        slist.add(share2);

        ShareInfo share4 = new ShareInfo();
        share4.setShare_photo(R.drawable.icon_share_circle);
        share4.setShare_photo_selected(R.drawable.icon_share_circle);
        share4.setShare_name(WechatMoments.NAME);
        slist.add(share4);

        ShareInfo share5 = new ShareInfo();
        share5.setShare_photo(R.drawable.icon_share_wechat);
        share5.setShare_photo_selected(R.drawable.icon_share_wechat);
        share5.setShare_name(Wechat.NAME);
        slist.add(share5);

        ShareInfo share6 = new ShareInfo();
        share6.setShare_photo(R.drawable.img_video_copy);
        share6.setShare_photo_selected(R.drawable.img_video_copy);
        share6.setShare_name("复制链接");
        slist.add(share6);

        sadapter = new ShareAdapter(slist, rl_share, this, false, this);
        gv_share.setAdapter(sadapter);
        sv.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ScrollView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
                if (!isRefresh) {
                    pageNo = 1;
                    videoCommentBeanList.clear();
                    isRefresh = true;
                    getVideoInfo();
                }
            }
        });
        //搜索输入监听
        et_comment.setOnKeyListener(new View.OnKeyListener() {
            Message msg;

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (KeyEvent.KEYCODE_ENTER == keyCode && event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (LoginUtils.isLogin()) {
                        content = et_comment.getText().toString();
                        loadingDialog.show();
                        if (isError) {
                            runProblemFeedback();
                        } else {
                            if (!isReply) {
                                runSubmitVideoComment();
                            } else {
                                runReplyVideoComment();
                            }
                        }
                    } else {
                        mDialog = DialogUtils.showAlertDialog(VideoShowInfoActivity.this,
                                "请先登陆您的账号!", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        mDialog.dismiss();
                                        Intent intent = new Intent(VideoShowInfoActivity.this, LoginActivity.class);
                                        intent.putExtra("type", 0);
                                        UIUtils.startActivityNextAnim(intent);
                                    }
                                });
                    }

                    return true;
                }
                return false;

            }
        });
        rl_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                //隐藏软键盘
                imm.hideSoftInputFromWindow(VideoShowInfoActivity.this.getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
                rl_comment.setVisibility(View.GONE);
                et_comment.clearFocus();
                isReply = false;
            }
        });
//        videoview.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//            @Override
//            public void onPrepared(MediaPlayer mp) {
//                View placeholder = findViewById(R.id.placeholder);
//
//                placeholder.setVisibility(View.GONE);
//            }
//        });
    }

    /**
     * 纠错
     */
    public void runProblemFeedback() {
        loadingDialog.show();
        ProblemFeedbackProtocol problemFeedbackProtocol = new ProblemFeedbackProtocol();
        ProblemFeedbackRequest problemFeedbackRequest = new ProblemFeedbackRequest();
        url = problemFeedbackProtocol.getApiFun();
        problemFeedbackRequest.map.put("user_id", SharedPrefrenceUtils.getString(VideoShowInfoActivity.this, "user_id"));
        problemFeedbackRequest.map.put("msg_type", "1");
        problemFeedbackRequest.map.put("msg_title", "纠错");
        problemFeedbackRequest.map.put("video_id", video_id + "");
        String wholeContent = content;
        if (videoInfoBean != null) {
            wholeContent = "投诉的作品id:" + videoInfoBean.getId() + ",投诉的作品名称:" + videoInfoBean.getTitle() + ",投诉的内容:" + content;
        }
        problemFeedbackRequest.map.put("msg_content", wholeContent);
        isError = false;
        MyVolley.uploadNoFile(MyVolley.POST, url, problemFeedbackRequest.map, new MyVolley.VolleyCallback() {
            @Override
            public void dealWithJson(String address, String json) {
                LogUtils.e("json:" + json);
                Gson gson = new Gson();
                ProblemFeedbackResponse problemFeedbackResponse = gson.fromJson(json, ProblemFeedbackResponse.class);
                if (problemFeedbackResponse.code == 0) {
                    loadingDialog.dismiss();
                    et_comment.setText("");
                    DialogUtils.showAlertDialog(VideoShowInfoActivity.this,
                            "提交纠错成功!");
                } else {
                    loadingDialog.dismiss();
                    DialogUtils.showAlertDialog(VideoShowInfoActivity.this,
                            problemFeedbackResponse.resultText);
                }


            }

            @Override
            public void dealWithError(String address, String error) {
                loadingDialog.dismiss();
                et_comment.setText("");
                DialogUtils.showAlertDialog(VideoShowInfoActivity.this, error);
            }
        });
    }


    /**
     * 获取视频详情
     */
    private void getVideoInfo() {
        loadingDialog.show();
        url = "video/getVideoDesc.do";
        Map<String, String> params = new HashMap<String, String>();
        params.put("video_id", video_id + "");
        if (StringUtils.isEmpty(userId)) {
            userId = "0";
        }
        LogUtils.e("userid:" + userId);
        params.put("user_id", userId);
        MyVolley.uploadNoFile(MyVolley.POST, url, params, new MyVolley.VolleyCallback() {

            @Override
            public void dealWithJson(String address, String json) {
                LogUtils.e("获取视频详情:" + json);
                videoInfoResponse = gson.fromJson(json, VideoInfoResponse.class);
                if (videoInfoResponse.getCode() == 0) {
                    videoInfoBean = videoInfoResponse.getData();
                    if (videoInfoBean != null) {
                        if (!TextUtils.isEmpty(videoInfoBean.getGoodList())) {
                            List<GoodsAttrBean> goodList = gson.fromJson(videoInfoBean.getGoodList(), new TypeToken<List<GoodsAttrBean>>() {
                            }.getType());
                            if (goodList.size() > 0) {
                                tv_one_key_order.setVisibility(View.VISIBLE);
                            } else {
                                tv_one_key_order.setVisibility(View.GONE);
                            }
                            ll_text_all.setVisibility(View.VISIBLE);
                        }
                        isCollection = videoInfoBean.getIs_collection();
                        isLiked = videoInfoBean.getIs_liked();
                        if (isCollection == 0) {
                            iv_collection.setImageResource(R.mipmap.bt_collect_noselectable);
                        } else {
                            iv_collection.setImageResource(R.mipmap.bt_collect_selectable);
                        }
                        if (isLiked == 0) {
                            iv_liked.setImageResource(R.mipmap.bt_love_noselectable);
                        } else {
                            iv_liked.setImageResource(R.mipmap.bt_love_selectable);
                        }

//                        if (!StringUtils.isEmpty(videoInfoBean.getAvatar())) {
                            ImageLoader.getInstance().displayImage(videoInfoBean.getAvatar(), iv_uesr_img, PictureOption.getSimpleOptions());
//                        }
                        if (!StringUtils.isEmpty(videoInfoBean.getAlias())) {
                            tv_user_name.setText(videoInfoBean.getAlias());
                        }
                        tv_look_num.setText(videoInfoBean.getPlay_cnt() + "");
                        SimpleDateFormat time = new SimpleDateFormat("yyyy.MM.dd");
                        if (!StringUtils.isEmpty(videoInfoBean.getCreate_time())) {
                            long time1 = Long.valueOf(videoInfoBean.getCreate_time());
                            String timeStr = time.format(new Date(time1 * 1000));
                            tv_date.setText(timeStr);
                        }
                        videoLableBeanList = videoInfoResponse.getData().getTagDataLists();
                        if (videoLableBeanList != null) {
                            if (videoLableBeanList.size() >= 2) {
                                showVideoLableBeanList.clear();
                                showVideoLableBeanList.add(videoLableBeanList.get(0));
                                showVideoLableBeanList.add(videoLableBeanList.get(1));
                                videoLableAdapter = new VideoLableAdapter(VideoShowInfoActivity.this, showVideoLableBeanList);
                            } else {
                                videoLableAdapter = new VideoLableAdapter(VideoShowInfoActivity.this, videoLableBeanList);
                            }
                            gv_lable.setAdapter(videoLableAdapter);
                        }
                        if (!StringUtils.isEmpty(videoInfoBean.getVideo_url())) {
                            videoUrl = videoInfoBean.getVideo_url();
                            setVideoMessage(videoInfoBean.getSubtitle_file_url());

                            if(!isReady){
                                playVideo();
                            }
                        }

                        if (!StringUtils.isEmpty(videoInfoBean.getTitle())) {
                            tv_title.setText(videoInfoBean.getTitle());
                        }

                        if (videoInfoBean.getIs_fee() == 1) {
                            tv_one_key_order.setVisibility(View.VISIBLE);
                        } else {
                            tv_one_key_order.setVisibility(View.GONE);
                        }
                        ll_text_all.setVisibility(View.VISIBLE);
                        user_share_video.setText(videoInfoBean.getForward_cnt() + "");
                        user_collection_video.setText(videoInfoBean.getCollection_cnt() + "");
                        user_like_num.setText(videoInfoBean.getLiked_cnt() + "");
                        String shareUrl = Constants.SHARE_URL + "share/video_share.shtml?share_user_id=" + SharedPrefrenceUtils.getString(VideoShowInfoActivity.this, "user_id") + "&video_id=" + video_id;
                        shareDateBean = new ShareDateBean();
                        shareDateBean.setContent("我在美甲屋APP发现一个不错的教程，快来看看吧！");
//                        new MyBobAsynctack1(sadapter, videoInfoBean.getVideo_url()).execute(videoInfoBean.getVideo_url());
                        shareDateBean.setTitle(videoInfoBean.getTitle());
                        shareDateBean.setUrl(shareUrl);
                        shareDateBean.setImageurl(cover_img);
                        shareDateBean.setVideoUrl(videoInfoBean.getVideo_url());
                        sadapter.setShareDateBean(shareDateBean);
                        tv_comment_num.setText("" + videoInfoBean.getCommentCnt());

                        if(LoginUtils.isLogin()) {
                            getAttention(videoInfoBean.getId_value());
                        }
                        getLookVideos();

                        runGetVideoCommentList();
                    }

                } else {
                    loadingDialog.dismiss();
                    DialogUtils.showAlertDialog(VideoShowInfoActivity.this, videoInfoResponse.getResultText());
                }
                if (isRefresh) {
                    isRefresh = false;
                    sv.onRefreshComplete();
                }
            }

            @Override
            public void dealWithError(String address, String error) {
                if (isRefresh) {
                    isRefresh = false;
                    sv.onRefreshComplete();
                }
                loadingDialog.dismiss();
                DialogUtils.showAlertDialog(VideoShowInfoActivity.this, error);
            }
        });
    }

    /**
     * 是否已经关注
     */
    public void getAttention(int follow_userid) {
        loadingDialog.show();
        GetFollowInfoProtocol getFollowInfoProtocol = new GetFollowInfoProtocol();
        GetFollowInfoRequest getFollowInfoRequest = new GetFollowInfoRequest();
        url = getFollowInfoProtocol.getApiFun();
        getFollowInfoRequest.map.put("user_id", SharedPrefrenceUtils.getString(VideoShowInfoActivity.this, "user_id"));
        getFollowInfoRequest.map.put("follow_userid", String.valueOf(follow_userid));


        MyVolley.uploadNoFile(MyVolley.POST, url, getFollowInfoRequest.map, new MyVolley.VolleyCallback() {
            @Override
            public void dealWithJson(String address, String json) {

                Gson gson = new Gson();
                getFollowInfoResponse = gson.fromJson(json, GetFollowInfoResponse.class);
                LogUtils.e("sendVerificationCodeResponse:" + json.toString());
                if (getFollowInfoResponse.code == 0) {
                    if (getFollowInfoResponse.data != null) {
                        if (getFollowInfoResponse.data.is_follow == 1) {
                            tv_goto_attention.setText("已关注");
                        } else {
                            tv_goto_attention.setText("未关注");
                        }
                    }

                } else {
                    loadingDialog.dismiss();
                    DialogUtils.showAlertDialog(VideoShowInfoActivity.this,
                            getFollowInfoResponse.resultText);
                }


            }

            @Override
            public void dealWithError(String address, String error) {
                loadingDialog.dismiss();
                DialogUtils.showAlertDialog(VideoShowInfoActivity.this, error);
            }
        });
    }

    /**
     * 获取看了又看列表
     */
    private void getLookVideos() {
        loadingDialog.show();
        url = "video/searchVideo.do";
        Map<String, String> params = new HashMap<String, String>();
        if (videoInfoResponse.getData().getTagDataLists() != null && videoInfoResponse.getData().getTagDataLists().size() > 0) {
            params.put("keyword", videoInfoResponse.getData().getTagDataLists().get(0).getTag_name());
        }else {
            params.put("keyword","''");
        }
        if (StringUtils.isEmpty(userId)) {
            userId = "0";
        }
        params.put("user_id", userId);
        params.put("pageNo", "1");
        params.put("pageSize", "4");
        params.put("type", "1");
        LogUtils.e("params:" + params.toString());
        MyVolley.uploadNoFile(MyVolley.POST, url, params, new MyVolley.VolleyCallback() {

            @Override
            public void dealWithJson(String address, String json) {
                LogUtils.e("获取看了又看列表:" + json);
                getVideoListResponse = gson.fromJson(json, GetVideoListResponse.class);
                if (getVideoListResponse != null) {
                    if (getVideoListResponse.code == 0) {
                        lookVideos = getVideoListResponse.dataList;
                        if (lookVideos != null) {
                            if (lookVideos.size() > 1) {
                                for (int i = 0; i < lookVideos.size(); i++) {
                                    VideoListBean videoListBean = lookVideos.get(i);
                                    if (videoListBean.getId() == video_id) {
                                        lookVideos.remove(i);
                                        break;
                                    }
                                }

                                if(lookVideos.size()>3){
                                    lookVideos.remove(lookVideos.size()-1);
                                }

                                lv_look.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        VideoListBean videoListBean = lookVideos.get(position);
                                        LogUtils.e("选中的video:" + videoListBean.toString());
                                        Intent intent = null;
                                        if (videoListBean.getType() == 0) {
                                            intent = new Intent(VideoShowInfoActivity.this, VideoInfoActivity.class);
                                            intent.putExtra("video_name", videoListBean.getTitle());
                                            intent.putExtra("video_id", videoListBean.getId());
                                            intent.putExtra("cover_img", videoListBean.getCover_img());
                                            intent.putExtra("video_url", videoListBean.getVideo_url());
                                        } else if (videoListBean.getType() == 1) {
                                            intent = new Intent(VideoShowInfoActivity.this, VideoShowInfoActivity.class);
                                            intent.putExtra("video_name", videoListBean.getTitle());
                                            intent.putExtra("video_id", videoListBean.getId());
                                            intent.putExtra("cover_img", videoListBean.getCover_img());
                                            intent.putExtra("video_url", videoListBean.getVideo_url());
                                        } else if (videoListBean.getType() == 2) {
                                            intent = new Intent(VideoShowInfoActivity.this, VideoShowPhotoInfoActivity.class);
                                            intent.putExtra("video_name", videoListBean.getTitle());
                                            intent.putExtra("video_id", videoListBean.getId());
                                            intent.putExtra("video_url", videoListBean.getVideo_url());
                                        }
                                        startActivity(intent);
                                        finish();
                                    }
                                });
                                lookAdapter = new LookAdapter(VideoShowInfoActivity.this, lookVideos);
                                lv_look.setAdapter(lookAdapter);
                            } else {
                                lv_look.setVisibility(View.GONE);
                            }
                        }
                    } else {
                        DialogUtils.showAlertDialog(VideoShowInfoActivity.this, getVideoListResponse.resultText);
                    }
                }
            }

            @Override
            public void dealWithError(String address, String error) {
                LogUtils.e("获取看了又看列表错误:" + error);
                DialogUtils.showAlertDialog(VideoShowInfoActivity.this, error);
            }
        });
    }

    /**
     * 设置视频播放
     */
    private void setVideoMessage(String subtitleUrl) {
        subiUrl = subtitleUrl;
//        videoview.setVideoPath(videoUrl);
////        videoview.start();
//        videoview.requestFocus();

        if (!StringUtils.isEmpty(subtitleUrl)) {
            // 设置视频字幕
            subtitle = new Subtitle();
            subtitle.initSubtitleResource(subtitleUrl);

            playerHandler = new Handler() {
                public void handleMessage(Message msg) {

                    if (mediaPlayer == null) {
                        return;
                    }

                    // 刷新字幕
                    subtitleText.setText(subtitle.getSubtitleByTime(mediaPlayer
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

            timer.schedule(timerTask, 0, 500);
        }

//        new PlayAsyncTask().execute("");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_cancle:
                rl_share.setVisibility(View.INVISIBLE);
                break;
//            case R.id.rl_look_graphic_tutorials:{
//                if(isopen){
//                    v_graphic_tutorials.setVisibility(View.GONE);
//                    isopen=false;
//                }
//                else {
//                    v_graphic_tutorials.setVisibility(View.VISIBLE);
//                    isopen=true;
//                }
//                break;
//            }
            case R.id.iv_uesr_img:
                if (LoginUtils.isLogin()) {
                    Intent intent = new Intent(VideoShowInfoActivity.this, AttentionActivity.class);
                    if (videoInfoBean != null) {
                        intent.putExtra("follow_userid", videoInfoBean.getId_value());
                    }
                    UIUtils.startActivityNextAnim(intent);
                } else {
                    mDialog = DialogUtils.showAlertDialog(VideoShowInfoActivity.this,
                            "请先登陆您的账号!", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mDialog.dismiss();
                                    Intent intent = new Intent(VideoShowInfoActivity.this, LoginActivity.class);
                                    intent.putExtra("type", 2);
                                    UIUtils.startActivityNextAnim(intent);
                                }
                            });
                }
                break;
            case R.id.tv_goto_attention: {
                if (LoginUtils.isLogin()) {
                    Intent intent = new Intent(VideoShowInfoActivity.this, AttentionActivity.class);
                    if (videoInfoBean != null) {
                        intent.putExtra("follow_userid", videoInfoBean.getId_value());
                    }
                    UIUtils.startActivityNextAnim(intent);
                } else {
                    mDialog = DialogUtils.showAlertDialog(VideoShowInfoActivity.this,
                            "请先登陆您的账号!", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mDialog.dismiss();
                                    Intent intent = new Intent(VideoShowInfoActivity.this, LoginActivity.class);
                                    intent.putExtra("type", 2);
                                    UIUtils.startActivityNextAnim(intent);
                                }
                            });
                }
                break;
            }
            case R.id.tv_one_key_order: {
                if (LoginUtils.isLogin()) {
                    if(videoInfoBean == null){
                        DialogUtils.showAlertDialog(VideoShowInfoActivity.this, "亲，数据还没有加载完成，请稍后再试试!");
                        return;
                    }
                    if (!TextUtils.isEmpty(videoInfoBean.getGoodList())) {
                        List<GoodsAttrBean> goodList = gson.fromJson(videoInfoBean.getGoodList(), new TypeToken<List<GoodsAttrBean>>() {
                        }.getType());
                        if (goodList.size() > 0) {
                            Intent intent = new Intent(VideoShowInfoActivity.this, IncludeGoodsActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("goodList", (Serializable) goodList);
                            intent.putExtras(bundle);
                            intent.putExtra("fee", videoInfoBean.getFee());
                            intent.putExtra("video_id", videoInfoBean.getId());
                            UIUtils.startActivityNextAnim(intent);
                        } else {
                            DialogUtils.showAlertDialog(VideoShowInfoActivity.this, "亲，没有商品可以下单!");
                        }
                    } else {
                        DialogUtils.showAlertDialog(VideoShowInfoActivity.this, "亲，没有商品可以下单!");
                    }
                } else {
                    mDialog = DialogUtils.showAlertDialog(VideoShowInfoActivity.this,
                            "请先登陆您的账号!", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mDialog.dismiss();
                                    Intent intent = new Intent(VideoShowInfoActivity.this, LoginActivity.class);
                                    intent.putExtra("type", 2);
                                    UIUtils.startActivityNextAnim(intent);
                                }
                            });
                }
                break;
            }
            case R.id.tv_submit_homework: {
                if (LoginUtils.isLogin()) {
                    Intent intent = new Intent(VideoShowInfoActivity.this, SubmitHomeworkActivity.class);
                    intent.putExtra("video_id", String.valueOf(video_id));
                    UIUtils.startActivityForResultNextAnim(intent, 1);
                } else {
                    mDialog = DialogUtils.showAlertDialog(VideoShowInfoActivity.this,
                            "请先登陆您的账号!", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mDialog.dismiss();
                                    Intent intent = new Intent(VideoShowInfoActivity.this, LoginActivity.class);
                                    intent.putExtra("type", 2);
                                    UIUtils.startActivityNextAnim(intent);
                                }
                            });
                }
                break;
            }
            case R.id.tv_error: {
                if (LoginUtils.isLogin()) {
                    rl_comment.setVisibility(View.VISIBLE);
                    rl_comment.setClickable(true);
                    et_comment.requestFocus();
                    isError = true;
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(et_comment, InputMethodManager.SHOW_IMPLICIT);
                } else {
                    mDialog = DialogUtils.showAlertDialog(VideoShowInfoActivity.this,
                            "请先登陆您的账号!", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mDialog.dismiss();
                                    Intent intent = new Intent(VideoShowInfoActivity.this, LoginActivity.class);
                                    intent.putExtra("type", 2);
                                    UIUtils.startActivityNextAnim(intent);
                                }
                            });
                }
                break;
            }
            case R.id.tv_goto_comment: {
                if (LoginUtils.isLogin()) {
                    rl_comment.setVisibility(View.VISIBLE);
                    rl_comment.setClickable(true);
                    et_comment.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(et_comment, InputMethodManager.SHOW_IMPLICIT);
                } else {
                    mDialog = DialogUtils.showAlertDialog(VideoShowInfoActivity.this,
                            "请先登陆您的账号!", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mDialog.dismiss();
                                    Intent intent = new Intent(VideoShowInfoActivity.this, LoginActivity.class);
                                    intent.putExtra("type", 2);
                                    UIUtils.startActivityNextAnim(intent);
                                }
                            });
                }
                break;
            }
            case R.id.prl_share:
                if (SharedPrefrenceUtils.getBoolean(VideoShowInfoActivity.this, "isLogin")) {
                    if(mediaPlayer.isPlaying()){
                        mediaPlayer.pause();
                        iv_video_start.setVisibility(View.VISIBLE);
                        iv_video_start.setImageResource(R.mipmap.bt_pause);
                        cuTime = mediaPlayer.getCurrentPosition();
                    }
                    String shareUrl = Constants.SHARE_URL + "share/video_share.shtml?share_user_id=" + SharedPrefrenceUtils.getString(VideoShowInfoActivity.this, "user_id") + "&video_id=" + video_id;
                    shareDateBean.setUrl(shareUrl);
                    rl_share.setVisibility(View.VISIBLE);
                    // 初始化ShareSDK
                    ShareSDK.initSDK(UIUtils.getContext());
                } else {
                    mDialog = DialogUtils.showAlertDialog(VideoShowInfoActivity.this,
                            "请先登陆您的账号!", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mDialog.dismiss();
                                    Intent intent = new Intent(VideoShowInfoActivity.this, LoginActivity.class);
                                    intent.putExtra("type", 0);
                                    UIUtils.startActivityNextAnim(intent);
                                }
                            });
                }

                break;
            case R.id.iv_top_back:
                finish();
                overridePendingTransition(R.anim.animprv_in, R.anim.animprv_out);
                break;
            case R.id.prl_like:
                if (LoginUtils.isLogin()) {
                    //点赞
                    if (isLiked == 0) {
                        addCollectionOrLike(0);
                    }
//                    else {
//                        cancelCollectionOrLike(0);
//                    }
                } else {
                    mDialog = DialogUtils.showAlertDialog(VideoShowInfoActivity.this,
                            "请先登陆您的账号!", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mDialog.dismiss();
                                    Intent intent = new Intent(VideoShowInfoActivity.this, LoginActivity.class);
                                    intent.putExtra("type", 2);
                                    UIUtils.startActivityNextAnim(intent);
                                }
                            });
                }
                break;
            case R.id.prl_collection:
                if (LoginUtils.isLogin()) {
                    //收藏
                    if (isCollection == 0) {
                        addCollectionOrLike(1);
                    }
//                    else {
//                        cancelCollectionOrLike(1);
//                    }
                } else {
                    mDialog = DialogUtils.showAlertDialog(VideoShowInfoActivity.this,
                            "请先登陆您的账号!", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mDialog.dismiss();
                                    Intent intent = new Intent(VideoShowInfoActivity.this, LoginActivity.class);
                                    intent.putExtra("type", 2);
                                    UIUtils.startActivityNextAnim(intent);
                                }
                            });
                }
                break;
            case R.id.iv_full_screen:
                mediaPlayer.pause();
                iv_video_start.setVisibility(View.VISIBLE);
                iv_video_start.setImageResource(R.mipmap.bt_pause);
                int currenttime = mediaPlayer.getCurrentPosition();
                Intent intent = new Intent(VideoShowInfoActivity.this, MediaPlayActivity.class);
                intent.putExtra("path", videoUrl);
                intent.putExtra("subtitleUr", subiUrl);
                intent.putExtra("currenttime", currenttime);
                UIUtils.startActivityForResult(intent, 100);
//                if(screenType == 0){
//                    screenType = 1;
//                }else {
//                    screenType = 0;
//                }
//                LogUtils.e("screenType:"+screenType);
//                changeVideoSize();
                break;
            case R.id.rl_video_start:
            case R.id.iv_video_start:
                LogUtils.e("cuTime:" + cuTime);
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    iv_video_start.setVisibility(View.VISIBLE);
                    iv_video_start.setImageResource(R.mipmap.bt_pause);
                } else {
                    if (isVideoPrepared) {
                        if (cuTime != 0) {
                            mediaPlayer.seekTo(cuTime);
                        }
                        mediaPlayer.start();
                        iv_video_start.setVisibility(View.GONE);
                        placeholder.setVisibility(View.GONE);

                        addPlayCount();
                    } else {
                        Toast.makeText(VideoShowInfoActivity.this, "视频正在缓冲，请稍后再点击.", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }

    /**
     * 增加播放量
     */
    private void addPlayCount() {
        url = "video/addVideoPlayOrForwardCnt.do";
        Map<String, String> params = new HashMap<String, String>();
        params.put("video_id", video_id + "");
        params.put("type", "0");
        MyVolley.uploadNoFile(MyVolley.POST, url, params, new MyVolley.VolleyCallback(){

            @Override
            public void dealWithJson(String address, String json) {
                LogUtils.e("增加播放量结果:" + json);
                baseResponse = gson.fromJson(json, BaseResponse.class);
                if(baseResponse.getCode()==0){

                }else {
                    DialogUtils.showAlertDialog(VideoShowInfoActivity.this, baseResponse.getResultText());
                }

            }

            @Override
            public void dealWithError(String address, String error) {
                LogUtils.e("增加播放量错误:" + error);
                DialogUtils.showAlertDialog(VideoShowInfoActivity.this, error);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == 2) {
                tv_comment_num.setText("" + (Integer.parseInt(tv_comment_num.getText().toString()) + 1));
                pageNo = 1;
                videoCommentBeanList.clear();
                runGetVideoCommentList();
            }
        } else if (requestCode == 100) {
            if (resultCode == 200) {
                cuTime = data.getIntExtra("cuTime", 0);
                isVideoPrepared = false;
                if (mediaPlayer != null) {
                    mediaPlayer.seekTo(cuTime);
                    mediaPlayer.start();
                }
            }
        }
    }

    /**
     * 收藏或者点赞
     */
    private void addCollectionOrLike(final int type) {
        loadingDialog.show();
        url = "video/addCollectionOrWork.do";
        Map<String, String> params = new HashMap<String, String>();
        params.put("video_id", video_id + "");
        if (StringUtils.isEmpty(userId)) {
            userId = "0";
        }
        params.put("user_id", userId);
        params.put("type", type + "");
        MyVolley.uploadNoFile(MyVolley.POST, url, params, new MyVolley.VolleyCallback() {

            @Override
            public void dealWithJson(String address, String json) {
                LogUtils.e("收藏或者点赞:" + json);
                loadingDialog.dismiss();
                baseResponse = gson.fromJson(json, BaseResponse.class);
                if (baseResponse.getCode() == 0) {
                    if (type == 0) {
                        isLiked = 1;
                        iv_liked.setImageResource(R.mipmap.bt_love_selectable);
                        user_like_num.setText((Integer.parseInt(user_like_num.getText().toString().trim()) + 1) + "");
                    } else {
                        isCollection = 1;
                        iv_collection.setImageResource(R.mipmap.bt_collect_selectable);
                        user_collection_video.setText((Integer.parseInt(user_collection_video.getText().toString().trim()) + 1) + "");
                        SharedPrefrenceUtils.setBoolean(UIUtils.getContext(), "isUpdate", true);
                    }

                } else {
                    DialogUtils.showAlertDialog(VideoShowInfoActivity.this, baseResponse.getResultText());
                }

            }

            @Override
            public void dealWithError(String address, String error) {
                LogUtils.e("收藏或者点赞错误:" + error);
                loadingDialog.dismiss();
                DialogUtils.showAlertDialog(VideoShowInfoActivity.this, error);
            }
        });
    }

//    /**
//     * 取消收藏或者点赞
//     */
//    private void cancelCollectionOrLike(final int type) {
//        loadingDialog.show();
//        url = "video/cancelCollectionOrWork.do";
//        Map<String, String> params = new HashMap<String, String>();
//        params.put("video_id", video_id + "");
//        if (StringUtils.isEmpty(userId)) {
//            userId = "0";
//        }
//        params.put("user_id", userId);
//        params.put("type", type + "");
//        MyVolley.uploadNoFile(MyVolley.POST, url, params, new MyVolley.VolleyCallback() {
//
//            @Override
//            public void dealWithJson(String address, String json) {
//                LogUtils.e("取消收藏或者点赞:" + json);
//                loadingDialog.dismiss();
//                baseResponse = gson.fromJson(json, BaseResponse.class);
//                if (baseResponse.getCode() == 0) {
//                    if (type == 0) {
//                        isLiked = 0;
//                        iv_liked.setImageResource(R.mipmap.bt_love_noselectable);
//                        user_like_num.setText((Integer.parseInt(user_like_num.getText().toString().trim()) - 1) + "");
//                    } else {
//                        isCollection = 0;
//                        iv_collection.setImageResource(R.mipmap.bt_collect_noselectable);
//                        user_collection_video.setText((Integer.parseInt(user_collection_video.getText().toString().trim()) - 1) + "");
//                        SharedPrefrenceUtils.setBoolean(UIUtils.getContext(), "isUpdate", true);
//                    }
//                } else {
//                    DialogUtils.showAlertDialog(VideoShowInfoActivity.this, baseResponse.getResultText());
//                }
//            }
//
//            @Override
//            public void dealWithError(String address, String error) {
//                LogUtils.e("取消收藏或者点赞错误:" + error);
//                loadingDialog.dismiss();
//                DialogUtils.showAlertDialog(VideoShowInfoActivity.this, error);
//            }
//        });
//    }

    @Override
    public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
        Toast.makeText(VideoShowInfoActivity.this, "分享成功!",
                Toast.LENGTH_SHORT).show();
        runVideoPlayOrForwardCnt();
    }

    @Override
    public void onError(Platform platform, int i, Throwable throwable) {
        Toast.makeText(VideoShowInfoActivity.this, "分享失败!" + throwable,
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onCancel(Platform platform, int i) {
        Toast.makeText(VideoShowInfoActivity.this, "分享取消!",
                Toast.LENGTH_SHORT).show();
    }

    //测试刷新
    private class GetDataTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            // Simulates a background job.
            try {
                Thread.sleep(4000);
                //这里做网络请求数据，并返回数据
            } catch (InterruptedException e) {
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            // Do some stuff here
            //把得到的数据设置好

//			mPullRefreshScrollView.setMode(Mode.DISABLED);
//            lv_comment.stopLoadMore();
            super.onPostExecute(result);
        }
    }

    //获取视频评论
    public void runGetVideoCommentList() {
        GetVideoCommentListProtocol getVideoCommentListProtocol = new GetVideoCommentListProtocol();
        GetVideoCommentListRequest getVideoCommentListRequest = new GetVideoCommentListRequest();
        url = getVideoCommentListProtocol.getApiFun();
        getVideoCommentListRequest.map.put("user_id", SharedPrefrenceUtils.getString(VideoShowInfoActivity.this, "user_id"));
        getVideoCommentListRequest.map.put("video_id", String.valueOf(video_id));
        getVideoCommentListRequest.map.put("pageNo", String.valueOf(pageNo));
        getVideoCommentListRequest.map.put("pageSize", String.valueOf(pageSize));

        MyVolley.uploadNoFile(MyVolley.POST, url, getVideoCommentListRequest.map, new MyVolley.VolleyCallback() {
            @Override
            public void dealWithJson(String address, String json) {
                LogUtils.e("json:" + json);
                Gson gson = new Gson();
                GetVideoCommentListResponse getVideoCommentListResponse = gson.fromJson(json, GetVideoCommentListResponse.class);
                if (getVideoCommentListResponse.code.equals("0")) {
                    if(isVideoPrepared){
                        loadingDialog.dismiss();
                    }
                    videoCommentBeanList.clear();
                    if (getVideoCommentListResponse.dataList.size() > 0) {
                        videoCommentBeanList.addAll(getVideoCommentListResponse.dataList);
                        setComment(videoCommentBeanList);
                    } else {
                        if (videoCommentsAdapter != null) {
                            videoCommentsAdapter.setDataList(videoCommentBeanList);
                            videoCommentsAdapter.notifyDataSetChanged();
                        }
                    }
                } else {
                    if(isVideoPrepared){
                        loadingDialog.dismiss();
                    }
                    DialogUtils.showAlertDialog(VideoShowInfoActivity.this,
                            getVideoCommentListResponse.resultText);
                }
//                if (pageNo > 1) {
//                    lv_comment.stopLoadMore();
//                }

            }

            @Override
            public void dealWithError(String address, String error) {
                if(isVideoPrepared){
                    loadingDialog.dismiss();
                }
                DialogUtils.showAlertDialog(VideoShowInfoActivity.this, error);
            }
        });
    }

    public void setComment(final List<GetVideoCommentListResponse.VideoCommentBean> dataList) {
        if (videoCommentsAdapter == null) {
            videoCommentsAdapter = new VideoCommentsAdapter(VideoShowInfoActivity.this, dataList, loadingDialog);
        } else {
            videoCommentsAdapter.setDataList(dataList);
            videoCommentsAdapter.notifyDataSetChanged();
        }
        lv_comment.setAdapter(videoCommentsAdapter);
        lv_comment.expandGroup(0);
        for (int i = 0; i < dataList.size(); i++) {
            lv_comment.expandGroup(i);
        }
        //设置listview的滑动优先级
//        lv_comment.setOnTouchListener(new View.OnTouchListener() {
//
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                if (event.getAction() == MotionEvent.ACTION_UP) {
//                    sv1.requestDisallowInterceptTouchEvent(false);
//                } else {
//                    sv1.requestDisallowInterceptTouchEvent(true);
//                }
//                return false;
//            }
//        });

//        lv_comment.setPullLoadEnable(true);
//        lv_comment.setGroupIndicator(null);
        //禁止展开
        lv_comment.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                                        int groupPosition, long id) {
                // TODO Auto-generated method stub
                LogUtils.e("groupPosition:" + groupPosition);
                comment_id = String.valueOf(dataList.get(groupPosition).comment_id);
                parent_id = "0";
                isReply = true;
                rl_comment.setVisibility(View.VISIBLE);
                rl_comment.setClickable(true);
                et_comment.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(et_comment, InputMethodManager.SHOW_IMPLICIT);
                return true;
            }
        });
        lv_comment.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                comment_id = String.valueOf(dataList.get(groupPosition).comment_id);
                parent_id = dataList.get(groupPosition).replyDataList.get(childPosition).reply_id;
                isReply = true;
                rl_comment.setVisibility(View.VISIBLE);
                rl_comment.setClickable(true);
                et_comment.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(et_comment, InputMethodManager.SHOW_IMPLICIT);
                return false;
            }
        });
    }

    //评论视频
    public void runSubmitVideoComment() {
        loadingDialog.show();
        SubmitVideoCommentProtocol submitVideoCommentProtocol = new SubmitVideoCommentProtocol();
        SubmitVideoCommentRequest submitVideoCommentRequest = new SubmitVideoCommentRequest();
        url = submitVideoCommentProtocol.getApiFun();
        submitVideoCommentRequest.map.put("user_id", SharedPrefrenceUtils.getString(VideoShowInfoActivity.this, "user_id"));
        submitVideoCommentRequest.map.put("video_id", String.valueOf(video_id));
        submitVideoCommentRequest.map.put("content", content);

        MyVolley.uploadNoFile(MyVolley.POST, url, submitVideoCommentRequest.map, new MyVolley.VolleyCallback() {
            @Override
            public void dealWithJson(String address, String json) {
                LogUtils.e("json:" + json);
                Gson gson = new Gson();
                SubmitVideoCommentResponse submitVideoCommentResponse = gson.fromJson(json, SubmitVideoCommentResponse.class);
                if (submitVideoCommentResponse.code.equals("0")) {
                    et_comment.setText("");
                    loadingDialog.dismiss();
                    rl_comment.setVisibility(View.GONE);
                    tv_comment_num.setText("" + (Integer.parseInt(tv_comment_num.getText().toString()) + 1));
                    pageNo = 1;
                    videoCommentBeanList.clear();
                    runGetVideoCommentList();
                } else {
                    loadingDialog.dismiss();
                    DialogUtils.showAlertDialog(VideoShowInfoActivity.this,
                            submitVideoCommentResponse.resultText);
                }

            }

            @Override
            public void dealWithError(String address, String error) {
                loadingDialog.dismiss();
                DialogUtils.showAlertDialog(VideoShowInfoActivity.this, error);
            }
        });
    }

    //回复评论
    public void runReplyVideoComment() {
        loadingDialog.show();
        ReplyVideoCommentProtocol replyVideoCommentProtocol = new ReplyVideoCommentProtocol();
        ReplyVideoCommentRequest replyVideoCommentRequest = new ReplyVideoCommentRequest();
        url = replyVideoCommentProtocol.getApiFun();
        replyVideoCommentRequest.map.put("user_id", SharedPrefrenceUtils.getString(VideoShowInfoActivity.this, "user_id"));
        replyVideoCommentRequest.map.put("video_id", String.valueOf(video_id));
        replyVideoCommentRequest.map.put("content", content);
        replyVideoCommentRequest.map.put("comment_id", comment_id);
        replyVideoCommentRequest.map.put("parent_id", parent_id);

        MyVolley.uploadNoFile(MyVolley.POST, url, replyVideoCommentRequest.map, new MyVolley.VolleyCallback() {
            @Override
            public void dealWithJson(String address, String json) {
                LogUtils.e("json:" + json);
                Gson gson = new Gson();
                ReplyInformationCommentResponse replyInformationCommentResponse = gson.fromJson(json, ReplyInformationCommentResponse.class);
                if (replyInformationCommentResponse.code.equals("0")) {
                    et_comment.setText("");
                    loadingDialog.dismiss();
                    rl_comment.setVisibility(View.GONE);
                    isReply = false;
                    pageNo = 1;
                    videoCommentBeanList.clear();
                    runGetVideoCommentList();
                } else {
                    loadingDialog.dismiss();
                    DialogUtils.showAlertDialog(VideoShowInfoActivity.this,
                            replyInformationCommentResponse.resultText);
                }


            }

            @Override
            public void dealWithError(String address, String error) {
                loadingDialog.dismiss();
                DialogUtils.showAlertDialog(VideoShowInfoActivity.this, error);
            }
        });
    }

    //分享
    public void runVideoPlayOrForwardCnt() {
        loadingDialog.show();
        AddVideoPlayOrForwardCntProtocol addVideoPlayOrForwardCntProtocol = new AddVideoPlayOrForwardCntProtocol();
        AddVideoPlayOrForwardCntRequest addVideoPlayOrForwardCntRequest = new AddVideoPlayOrForwardCntRequest();
        url = addVideoPlayOrForwardCntProtocol.getApiFun();
        addVideoPlayOrForwardCntRequest.map.put("video_id", String.valueOf(video_id));
        addVideoPlayOrForwardCntRequest.map.put("type", String.valueOf(1));

        MyVolley.uploadNoFile(MyVolley.POST, url, addVideoPlayOrForwardCntRequest.map, new MyVolley.VolleyCallback() {
            @Override
            public void dealWithJson(String address, String json) {
                LogUtils.e("json:" + json);
                Gson gson = new Gson();
                AddVideoPlayOrForwardCntResponse addPostOrInformationForwardCntResponse = gson.fromJson(json, AddVideoPlayOrForwardCntResponse.class);
                if (addPostOrInformationForwardCntResponse.code.equals("0")) {
                    loadingDialog.dismiss();
                    user_share_video.setText(String.valueOf(videoInfoBean.getForward_cnt() + 1));
                } else {
                    loadingDialog.dismiss();
                    DialogUtils.showAlertDialog(VideoShowInfoActivity.this,
                            addPostOrInformationForwardCntResponse.resultText);
                }


            }

            @Override
            public void dealWithError(String address, String error) {
                loadingDialog.dismiss();
                DialogUtils.showAlertDialog(VideoShowInfoActivity.this, error);
            }
        });
    }

    class MyBobAsynctack1 extends AsyncTask<String, Void, Bitmap> {
        private String path;

        public MyBobAsynctack1(ShareAdapter shareAdapter, String path) {
            this.path = path;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            //这里的创建缩略图方法是调用VideoUtil类的方法，也是通过 android中提供的 ThumbnailUtils.createVideoThumbnail(vidioPath, kind);
            Bitmap bitmap = VideoUtil.createVideoThumbnail(params[0], 70, 50, MediaStore.Video.Thumbnails.MICRO_KIND);

            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            try {
                if (bitmap != null) {
                    String path = BitmapUtils.saveMyBitmap("videopath", bitmap);
                    sadapter.getShareDateBean().setImagepath(path);
                    LogUtils.e("图片拿到了" + path);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * 格式化的Builder
     */
    private StringBuilder sFormatBuilder = new StringBuilder();
    /**
     * 格式化的Formatter
     */
    private Formatter sFormatter = new Formatter(sFormatBuilder, Locale.getDefault());
    /**
     * 格式化的相关属性
     */
    private final Object[] sTimeArgs = new Object[3];

    /**
     * 转换进度值为时间
     *
     * @param secs
     * @return
     */
    private String makeTimeString(int secs) {
        /**
         * %[argument_index$][flags][width]conversion 可选的
         * argument_index 是一个十进制整数，用于表明参数在参数列表中的位置。第一个参数由 "1$"
         * 引用，第二个参数由 "2$" 引用，依此类推。 可选 flags
         * 是修改输出格式的字符集。有效标志集取决于转换类型。 可选 width
         * 是一个非负十进制整数，表明要向输出中写入的最少字符数。 可选 precision
         * 是一个非负十进制整数，通常用来限制字符数。特定行为取决于转换类型。 所需 conversion
         * 是一个表明应该如何格式化参数的字符。给定参数的有效转换集取决于参数的数据类型。
         */
        String durationformat = getString(R.string.durationformat);// <xliff:g
        // id="format">%1$02d:%2$02d:%3$02d</xliff:g>
        sFormatBuilder.setLength(0);
        secs = secs / 1000;
        Object[] timeArgs = sTimeArgs;
        timeArgs[0] = secs / 3600; // 秒
        timeArgs[1] = (secs % 3600) / 60; // 分
        timeArgs[2] = (secs % 3600 % 60) % 60; // 时
        return sFormatter.format(durationformat, timeArgs).toString().trim();
    }

    /**
     * 改变视频的显示大小，全屏，窗口，内容
     */
    public void changeVideoSize() {
        // 改变视频大小
        // 获取视频的宽度和高度
        int width = mediaPlayer.getVideoWidth();
        int height = mediaPlayer.getVideoHeight();
        // 如果按钮文字为窗口则设置为窗口模式
        if (screenType == 1) {
            /*
             * 如果为全屏模式则改为适应内容的，前提是视频宽高小于屏幕宽高，如果大于宽高 我们要做缩放
             * 如果视频的宽高度有一方不满足我们就要进行缩放. 如果视频的大小都满足就直接设置并居中显示。
             */
            if (width > screenWidth || height > screenHeight) {
                // 计算出宽高的倍数
                float vWidth = (float) width / (float) screenWidth;
                float vHeight = (float) height / (float) screenHeight;
                // 获取最大的倍数值，按大数值进行缩放
                float max = Math.max(vWidth, vHeight);
                // 计算出缩放大小,取接近的正值
                width = (int) Math.ceil((float) width / max);
                height = (int) Math.ceil((float) height / max);
            }
            // 设置SurfaceView的大小并居中显示
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(width,
                    height);
            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
            surfaceView.setLayoutParams(layoutParams);
        } else if (screenType == 0) {
            // 设置全屏
            // 设置SurfaceView的大小并居中显示
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(screenWidth,
                    screenHeight);
            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
            surfaceView.setLayoutParams(layoutParams);
        }
    }
}
