package com.tesu.manicurehouse.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.tesu.manicurehouse.R;
import com.tesu.manicurehouse.adapter.VideoPhoteFolderAdapter;
import com.tesu.manicurehouse.base.BaseActivity;
import com.tesu.manicurehouse.bean.PhotoUpImageBucket;
import com.tesu.manicurehouse.record.VideoMakePhotoActivity;
import com.tesu.manicurehouse.util.PhotoUpAlbumHelper;
import com.tesu.manicurehouse.util.StringUtils;

import java.util.List;

public class PostChooseFolderActivity extends BaseActivity implements View.OnClickListener{
    private ImageView video_new_img_back;
    private View rootView;
    private ListView image_folder_lv;
    private PhotoUpAlbumHelper photoUpAlbumHelper;
    private List<PhotoUpImageBucket> list;
    private VideoPhoteFolderAdapter mAdapter;
    private String selectImagesStr;

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_post_choose_folder);
//    }

    @Override
    protected View initView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        rootView = View.inflate(this, R.layout.activity_post_choose_folder, null);
        video_new_img_back = (ImageView) rootView.findViewById(R.id.video_new_img_back);
        image_folder_lv = (ListView) rootView.findViewById(R.id.image_folder_lv);
        setContentView(rootView);

        video_new_img_back.setOnClickListener(this);

        image_folder_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent intent = new Intent(PostChooseFolderActivity.this, PostChooseImageActivity.class);
                intent.putExtra("imagelist", list.get(position));
                if (!StringUtils.isEmpty(selectImagesStr)) {
                    intent.putExtra("selectImagesStr", selectImagesStr);
                }
//				startActivity(intent);
                startActivityForResult(intent, 200);
                setFinish();
//				finish();
            }
        });

        initData();
        return null;
    }

    private void initData() {
        photoUpAlbumHelper = PhotoUpAlbumHelper.getHelper();
        photoUpAlbumHelper.init(PostChooseFolderActivity.this);
        photoUpAlbumHelper.setGetAlbumList(new PhotoUpAlbumHelper.GetAlbumList() {
            @Override
            public void getAlbumList(List<PhotoUpImageBucket> list) {
                mAdapter.setArrayList(list);
                mAdapter.notifyDataSetChanged();
                PostChooseFolderActivity.this.list = list;
            }
        });
        photoUpAlbumHelper.execute(false);

        mAdapter = new VideoPhoteFolderAdapter(PostChooseFolderActivity.this);
        image_folder_lv.setAdapter(mAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==200){
            if(resultCode == 100){
                selectImagesStr = data.getStringExtra("selectImages");
            }else if(resultCode ==200){
                selectImagesStr = data.getStringExtra("selectImages");

                Intent intent = getIntent();
                intent.putExtra("selectImages", selectImagesStr);
                setResult(200, intent);
                finish();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.video_new_img_back:
                finish();
                break;
        }
    }
}
