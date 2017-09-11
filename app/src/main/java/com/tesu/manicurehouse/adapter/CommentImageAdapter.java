package com.tesu.manicurehouse.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.tesu.manicurehouse.R;
import com.tesu.manicurehouse.activity.ShowBigPictrue;
import com.tesu.manicurehouse.util.LogUtils;
import com.tesu.manicurehouse.util.PictureOption;
import com.tesu.manicurehouse.util.UIUtils;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CommentImageAdapter extends BaseAdapter {

    private String TAG = "ClientListAdapter";
    private List<String> imagepaths;
    private Context mContext;

    public CommentImageAdapter(Context context, List<String> imagepaths) {
        mContext = context;
        this.imagepaths = imagepaths;
        if (!ImageLoader.getInstance().isInited()) {
            ImageLoader.getInstance()
                    .init(ImageLoaderConfiguration.createDefault(UIUtils
                            .getContext()));
        }
    }

    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return imagepaths.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(final int pos, View view, ViewGroup arg2) {
        // TODO Auto-generated method stub
        ViewHolder vh;
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.add_image_item, null);
            vh = new ViewHolder();
            vh.iv_comment = (ImageView) view.findViewById(R.id.iv_comment);
            vh.iv_image= (ImageView) view.findViewById(R.id.iv_image);
            vh.rl=(RelativeLayout)view.findViewById(R.id.rl);
            view.setTag(vh);
        } else {
            vh = (ViewHolder) view.getTag();
        }
        vh.iv_comment.setVisibility(View.INVISIBLE);
        vh.iv_image.setVisibility(View.VISIBLE);
        vh.iv_image.setBackgroundColor(Color.TRANSPARENT);
        vh.iv_image.setScaleType(ImageView.ScaleType.FIT_XY);
        ImageLoader.getInstance().displayImage(imagepaths.get(pos), vh.iv_image, PictureOption.getSimpleOptions());
        vh.iv_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtils.e("点击图片:"+pos);
                // 跳到查看大图界面
                Intent intent = new Intent(mContext,
                        ShowBigPictrue.class);
                intent.putExtra("position", pos);
                intent.putStringArrayListExtra("imagePath", (ArrayList)imagepaths);
                UIUtils.startActivityNextAnim(intent);
            }
        });
        return view;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return imagepaths.size();
    }


    class ViewHolder {
        ImageView iv_comment;
        ImageView iv_image;
        RelativeLayout rl;
    }
//    /**
//     * 点击事件
//     */
//    @SuppressLint("NewApi")
//    private class TextViewListener implements View.OnClickListener {
//
//        /**
//         * 选择的某项
//         */
//        public int position = 0;
//        public TextViewListener(int position) {
//            this.position = position;
//        }
//
//        @Override
//        public void onClick(View v) {
//            TextView textView=(TextView)v;
//            if (position == 0) {
//                listView.setSelection(0);
//                textView.setTextColor(Color.RED);
//                hashMap.put("item",0);
//            }
//            else if(position == 1) {
//                listView.setSelection(13);
//                textView.setTextColor(Color.RED);
//                hashMap.put("item",1);
//            }
//            notifyDataSetChanged();
//        }
//    }

}
