package com.tesu.manicurehouse.record;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.tesu.manicurehouse.R;
import com.tesu.manicurehouse.adapter.VideoAddMusicAdapter;
import com.tesu.manicurehouse.adapter.VideoPhoteFolderAdapter;
import com.tesu.manicurehouse.bean.PhotoUpImageBucket;
import com.tesu.manicurehouse.bean.PhotoUpImageItem;
import com.tesu.manicurehouse.support.PercentRelativeLayout;
import com.tesu.manicurehouse.util.PhotoUpAlbumHelper;
import com.tesu.manicurehouse.util.StringUtils;

import java.util.ArrayList;
import java.util.List;


public class VideoChoosePhotoActivity extends RecordBaseActivity {
	private ImageView backIv;
	private ListView image_folder_lv;
	private PhotoUpAlbumHelper photoUpAlbumHelper;
	private List<PhotoUpImageBucket> list;
	private VideoPhoteFolderAdapter mAdapter;
	private String selectImagesStr;

	@Override
	protected int getContentViewId() {
		return R.layout.activity_video_choose_photo;
	}

	@Override
	protected void findViews() {
		backIv = (ImageView) findViewById(R.id.video_new_img_back);
		image_folder_lv = (ListView) findViewById(R.id.image_folder_lv);
	}

	@Override
	protected void initGetData() {
		super.initGetData();

		photoUpAlbumHelper = PhotoUpAlbumHelper.getHelper();
		photoUpAlbumHelper.init(VideoChoosePhotoActivity.this);
		photoUpAlbumHelper.setGetAlbumList(new PhotoUpAlbumHelper.GetAlbumList() {
			@Override
			public void getAlbumList(List<PhotoUpImageBucket> list) {
				mAdapter.setArrayList(list);
				mAdapter.notifyDataSetChanged();
				VideoChoosePhotoActivity.this.list = list;
			}
		});
		photoUpAlbumHelper.execute(false);
	}

	@Override
	protected void init() {
		mAdapter = new VideoPhoteFolderAdapter(VideoChoosePhotoActivity.this);
		image_folder_lv.setAdapter(mAdapter);

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode==200){
			if(resultCode == 100){
				selectImagesStr = data.getStringExtra("selectImages");
			}
		}
	}

	@Override
	protected void widgetListener() {
		backIv.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		image_folder_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
									int position, long id) {
				Intent intent = new Intent(VideoChoosePhotoActivity.this, VideoMakePhotoActivity.class);
				intent.putExtra("imagelist", list.get(position));
				if(!StringUtils.isEmpty(selectImagesStr)){
					intent.putExtra("selectImagesStr", selectImagesStr);
				}
//				startActivity(intent);
				startActivityForResult(intent,200);
				setFinish();
//				finish();
			}
		});
	}
}
