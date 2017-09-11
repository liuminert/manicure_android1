package com.tesu.manicurehouse.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tesu.manicurehouse.R;
import com.tesu.manicurehouse.adapter.VideoPhoteItemAdapter;
import com.tesu.manicurehouse.base.BaseActivity;
import com.tesu.manicurehouse.bean.PhotoUpImageBucket;
import com.tesu.manicurehouse.bean.PhotoUpImageItem;
import com.tesu.manicurehouse.record.VideoChoosePhotoActivity;
import com.tesu.manicurehouse.util.DialogUtils;
import com.tesu.manicurehouse.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class PostChooseImageActivity extends BaseActivity {
    private ImageView video_new_img_back;
    private View rootView;
    private GridView phote_gv;
    private PhotoUpImageBucket photoUpImageBucket;
    private ArrayList<PhotoUpImageItem> selectImages;
    private VideoPhoteItemAdapter adapter;
    private Gson gson;
    private TextView tv_selected_count;
    private TextView tv_complete;

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_post_choose_image);
//    }

    @Override
    protected View initView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        rootView = View.inflate(this, R.layout.activity_post_choose_image, null);
        video_new_img_back = (ImageView) rootView.findViewById(R.id.video_new_img_back);
        phote_gv = (GridView) rootView.findViewById(R.id.phote_gv);
        tv_selected_count = (TextView) rootView.findViewById(R.id.tv_selected_count);
        tv_complete = (TextView) rootView.findViewById(R.id.tv_complete);
        setContentView(rootView);

        initData();
        return null;
    }

    private void initData() {
        gson = new Gson();

        Intent intent = getIntent();
        photoUpImageBucket = (PhotoUpImageBucket) intent.getSerializableExtra("imagelist");
        adapter = new VideoPhoteItemAdapter(photoUpImageBucket.getImageList(), PostChooseImageActivity.this);
        phote_gv.setAdapter(adapter);

        String selectImagesStr = intent.getStringExtra("selectImagesStr");
        if(StringUtils.isEmpty(selectImagesStr)){
            selectImages = new ArrayList<PhotoUpImageItem>();
        }else {
            selectImages = gson.fromJson(selectImagesStr,new TypeToken<List<PhotoUpImageItem>>(){}.getType());
        }

        tv_selected_count.setText("已选择"+selectImages.size()+"张图片");


        video_new_img_back.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PostChooseImageActivity.this, PostChooseFolderActivity.class);
                intent.putExtra("selectImages", gson.toJson(selectImages));
                setResult(100, intent);
                finish();
            }
        });

        phote_gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                if (!photoUpImageBucket.getImageList().get(position).isSelected()) {
                    if (selectImages.size() >= 6) {
                        DialogUtils.showAlertDialog(PostChooseImageActivity.this,"最多只能加6张图片");
                        return;
                    }

                    CheckBox checkBox = (CheckBox) view.findViewById(R.id.check);
                    photoUpImageBucket.getImageList().get(position).setIsSelected(
                            !checkBox.isChecked());
                    adapter.notifyDataSetChanged();

                    if (selectImages.contains(photoUpImageBucket.getImageList().get(position))) {

                    } else {
                        selectImages.add(photoUpImageBucket.getImageList().get(position));
                    }
                } else {
                    CheckBox checkBox = (CheckBox) view.findViewById(R.id.check);
                    photoUpImageBucket.getImageList().get(position).setIsSelected(
                            !checkBox.isChecked());
                    adapter.notifyDataSetChanged();

                    if (selectImages.contains(photoUpImageBucket.getImageList().get(position))) {
                        selectImages.remove(photoUpImageBucket.getImageList().get(position));
                    } else {

                    }
                }

                tv_selected_count.setText("已选择" + selectImages.size() + "张图片");
            }
        });

        tv_complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getIntent();
                intent.putExtra("selectImages", gson.toJson(selectImages));
                setResult(200, intent);
                finish();
            }
        });
    }
}
