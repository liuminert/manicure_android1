package com.tesu.manicurehouse.record;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tesu.manicurehouse.R;
import com.tesu.manicurehouse.play.MediaPlayActivity;
import com.tesu.manicurehouse.util.MyVideoThumbLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/9/5 0005.
 */
public class VideoNewCheckActivity extends RecordBaseActivity {
    private static final String TAG = VideoNewCheckActivity.class.getSimpleName();
    private ImageView img_back;
    private GridView gridView;
    private ViewNewSelectAdapter adapter;
    private List<ViewNewSelectBean> list;
    private ViewNewSelectFolder selectFolder;
    private int width;
    private String cPath;

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                adapter.notifyDataSetChanged();
            }
        };
    };

    @Override
    protected int getContentViewId() {
        return R.layout.activity_video_new_check;
    }

    @Override
    protected void findViews() {
        img_back = (ImageView) findViewById(R.id.video_new_img_back);
        gridView = (GridView) findViewById(R.id.video_new_select_gridview);

        width = (getWindowManager().getDefaultDisplay().getWidth() - DisplayUtil.dip2px(VideoNewCheckActivity.this, 60)) / 3;

    }

    @Override
    protected void init() {
        list = new ArrayList<ViewNewSelectBean>();
        adapter = new ViewNewSelectAdapter(list, width, VideoNewCheckActivity.this);
        gridView.setAdapter(adapter);

    }

    @Override
    protected void initGetData() {
        super.initGetData();
        if (getIntent().getExtras() != null) {
//            selectFolder = (ViewNewSelectFolder) getIntent().getExtras().getSerializable("serializable");
//            if (selectFolder != null) {
//                list = selectFolder.getViewNewSelectBeanList();
//                if (list != null) {
//                    adapter = new ViewNewSelectAdapter(list, width, VideoNewCheckActivity.this);
//                    gridView.setAdapter(adapter);
//                }
//            }
            cPath = getIntent().getStringExtra("cPath");

            getList();


        }
    }

    private void getList() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                // 若为图片则为MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                Uri originalUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                ContentResolver cr = getContentResolver();
                Cursor cursor = cr.query(originalUri, null, null, null, null);
                if (cursor == null) {
                    return;
                }

                while (cursor.moveToNext()) {
                    ViewNewSelectBean bean = new ViewNewSelectBean();
                    bean.set_id(cursor.getLong(cursor.getColumnIndex("_ID")));
                    bean.setName(cursor.getString(cursor.getColumnIndex("_display_name")));// 视频名字
                    bean.setPath(cursor.getString(cursor.getColumnIndex("_data")));// 路径
                    bean.setWidth(cursor.getInt(cursor.getColumnIndex("width")));// 视频宽
                    bean.setHeight(cursor.getInt(cursor.getColumnIndex("height")));// 视频高
                    bean.setDuration(cursor.getLong(cursor.getColumnIndex("duration")));// 时长

                    String cPath1 = bean.getPath().substring(0,bean.getPath().lastIndexOf("/"));
                    if(cPath1.equals(cPath)){
                        list.add(bean);
                    }

                }

                Log.e(TAG, "视频list：" + list.toString());
                Log.e(TAG, "视频大小：" + list.size());
                Log.e(TAG, "cPath：" + cPath);

//                adapter = new ViewNewSelectAdapter(list, width, VideoNewCheckActivity.this);
//                gridView.setAdapter(adapter);

                Message message = handler.obtainMessage();
                message.what = 1;
                handler.sendMessage(message);

                // /data/data/com.android.providers.media/databases/external.db
                // {5dd10730} 数据库位置
            }
        }).start();
    }

    @Override
    protected void widgetListener() {
        img_back.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("serializable", list.get(position));
                Intent intent = new Intent(VideoNewCheckActivity.this, VideoNewCutActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
//                finish();
                setFinish();
//                Intent intent = new Intent(VideoNewCheckActivity.this, MediaPlayActivity.class);
//                intent.putExtra("path",list.get(position).getPath());
//                startActivity(intent);
//                finish();
            }
        });

    }

    private class ViewNewSelectAdapter extends BaseAdapter {
        private Context context;
        /**
         * 数据集
         */
        private List<ViewNewSelectBean> list;
        /**
         * 图片宽
         */
        private int width;
        private MyVideoThumbLoader mVideoThumbLoader;

        public ViewNewSelectAdapter(List<ViewNewSelectBean> list, int width, Context context) {
            this.list = list;
            this.width = width;
            this.context = context;
            mVideoThumbLoader = new MyVideoThumbLoader();// 初始化缩略图载入方法
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @SuppressLint("InflateParams")
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder viewHolder;
            final ViewNewSelectBean bean = list.get(position);
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(context).inflate(R.layout.item_video_new_select_gridview, null);
                viewHolder.txt = (TextView) convertView.findViewById(R.id.item_video_new_select_txt_time);
                viewHolder.img = (ImageView) convertView.findViewById(R.id.item_video_new_select_img);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            String path = bean.getPath();
            viewHolder.img.setTag(path);
            mVideoThumbLoader.showThumbByAsynctack(path, viewHolder.img);
            // 设置时长
            viewHolder.txt.setText(secToTime((int) bean.getDuration() / 1000));
//            viewHolder.txt.setText(bean.getDuration() / 1000+"");

            return convertView;
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
                if (minute < 60) {
                    second = time % 60;
                    timeStr = unitFormat(minute) + ":" + unitFormat(second);
                } else {
                    hour = minute / 60;
                    if (hour > 99)
                        return "99:59:59";
                    minute = minute % 60;
                    second = time - hour * 3600 - minute * 60;
                    timeStr = unitFormat(hour) + ":" + unitFormat(minute) + ":" + unitFormat(second);
                }
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

        private class ViewHolder {
            private ImageView img;
            private TextView txt;
        }

    }
}
