package com.tesu.manicurehouse.record;

import java.util.ArrayList;
import java.util.List;

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
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.tesu.manicurehouse.R;
import com.tesu.manicurehouse.util.MyVideoThumbLoader;

public class VideoNewSelectActivity extends RecordBaseActivity {
	private static final String TAG = VideoNewSelectActivity.class.getSimpleName();
	/** 图片展示器 */
	private ListView listview;
	/** 图片适配器 */
//	private ViewNewSelectAdapter adapter;
	private ViewNewSelectFoldAdapter adapter;
	/** 数据集 */
	private ArrayList<ViewNewSelectBean> list;
	
	private ImageView img_back;

	/** 显示图片的宽 */
	private int width;

	private ArrayList<ViewNewSelectFolder> viewNewSelectFolderList;

	@Override
	protected int getContentViewId() {
		return R.layout.activity_video_new_select;
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void findViews() {
		img_back = (ImageView) findViewById(R.id.video_new_img_back);
		listview = (ListView) findViewById(R.id.video_new_select_listview);

		width = (getWindowManager().getDefaultDisplay().getWidth() - DisplayUtil.dip2px(VideoNewSelectActivity.this, 60)) / 3;
	}

	@Override
	protected void init() {
		list = new ArrayList<ViewNewSelectBean>();
		viewNewSelectFolderList = new ArrayList<ViewNewSelectFolder>();

//		adapter = new ViewNewSelectAdapter(list, width, VideoNewSelectActivity.this);
		adapter = new ViewNewSelectFoldAdapter(viewNewSelectFolderList, width, VideoNewSelectActivity.this);
		listview.setAdapter(adapter);

		getList();
	}

	@Override
	protected void widgetListener() {
		img_back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Bundle bundle = new Bundle();
//				bundle.putSerializable("serializable",viewNewSelectFolderList.get(position));
				bundle.putSerializable("cPath",viewNewSelectFolderList.get(position).getPath());
				Intent intent = new Intent(VideoNewSelectActivity.this, VideoNewCheckActivity.class);
				intent.putExtras(bundle);
				startActivity(intent);
//				finish();
				setFinish();
			}
		});
	}

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == 1) {
				adapter.notifyDataSetChanged();
			}
		};
	};

	/**
	 * 获取数据
	 *
	 * @version 1.0
	 * @createTime 2015年6月16日,下午6:14:09
	 * @updateTime 2015年6月16日,下午6:14:09
	 * @createAuthor WangYuWen
	 * @updateAuthor WangYuWen
	 * @updateInfo (此处输入修改内容,若无修改可不写.)
	 *
	 */
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

					boolean isAdded = false;
					String cPath = bean.getPath().substring(0,bean.getPath().lastIndexOf("/"));
					for(ViewNewSelectFolder selectFolder : viewNewSelectFolderList){
						if(selectFolder.getPath().equals(cPath)){
							selectFolder.getViewNewSelectBeanList().add(bean);
							isAdded = true;
							break;
						}
					}
					if(!isAdded){
						ViewNewSelectFolder viewNewSelectFolder = new ViewNewSelectFolder();
						viewNewSelectFolder.setPath(cPath);
						List<ViewNewSelectBean> viewNewSelectBeans = new ArrayList<ViewNewSelectBean>();
						viewNewSelectBeans.add(bean);
						viewNewSelectFolder.setViewNewSelectBeanList(viewNewSelectBeans);
						viewNewSelectFolderList.add(viewNewSelectFolder);
					}
					list.add(bean);

				}

				Log.e(TAG,"视频list："+list.toString());

				Message message = handler.obtainMessage();
				message.what = 1;
				handler.sendMessage(message);

				// /data/data/com.android.providers.media/databases/external.db
				// {5dd10730} 数据库位置
			}
		}).start();
	}


	private class ViewNewSelectFoldAdapter extends BaseAdapter {
		private Context context;
		/** 数据集 */
		private ArrayList<ViewNewSelectFolder> list;
		/** 图片宽 */
		private int width;
		private MyVideoThumbLoader mVideoThumbLoader;

		public ViewNewSelectFoldAdapter(ArrayList<ViewNewSelectFolder> list, int width, Context context) {
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
			final ViewNewSelectFolder bean = list.get(position);
			if (convertView == null) {
				viewHolder = new ViewHolder();
				convertView = LayoutInflater.from(context).inflate(R.layout.item_video_new_select_folder, null);
				viewHolder.txt = (TextView) convertView.findViewById(R.id.item_video_new_select_txt_time);
				viewHolder.img = (ImageView) convertView.findViewById(R.id.item_video_new_select_img);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}

			String path = bean.getViewNewSelectBeanList().get(0).getPath();
			viewHolder.img.setTag(path);
			mVideoThumbLoader.showThumbByAsynctack(path, viewHolder.img);
			// 设置时长
			viewHolder.txt.setText(bean.getPath().substring(bean.getPath().lastIndexOf("/")+1)+"("+bean.getViewNewSelectBeanList().size()+")");

			return convertView;
		}

		private class ViewHolder {
			private ImageView img;
			private TextView txt;
		}

	}

}
