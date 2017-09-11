package com.tesu.manicurehouse.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.tesu.manicurehouse.R;
import com.tesu.manicurehouse.activity.ShowBigPictrue;
import com.tesu.manicurehouse.bean.GoodCommentBean;
import com.tesu.manicurehouse.util.BitmapBlurUtils;
import com.tesu.manicurehouse.util.MobileUtils;
import com.tesu.manicurehouse.util.PictureOption;
import com.tesu.manicurehouse.util.UIUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * 类描述：
 * 创建人：许明达
 * 创建时间：2016/04/14 10:49
 * 修改备注：
 */
public class PostImageAdapter extends BaseAdapter {

    private String TAG = "ClientListAdapter";
    private List<String> goodCommentBeanList;
    private Context mContext;
    private CommentImageAdapter commentImageAdapter;

    public PostImageAdapter(Context context, List<String> goodCommentBeanList) {
        mContext = context;
        this.goodCommentBeanList = goodCommentBeanList;
        if (!ImageLoader.getInstance().isInited()) {
            ImageLoader.getInstance()
                    .init(ImageLoaderConfiguration.createDefault(UIUtils
                            .getContext()));
        }
    }

    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return goodCommentBeanList.get(arg0);
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
            view = LayoutInflater.from(mContext).inflate(R.layout.post_iamge_item, null);
            vh = new ViewHolder();
            vh.iv_image = (ImageView) view.findViewById(R.id.iv_post_image);
            view.setTag(vh);
        } else {
            vh = (ViewHolder) view.getTag();
        }
        ImageLoader.getInstance().displayImage(goodCommentBeanList.get(pos), vh.iv_image, PictureOption.getSimpleOptions());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(MobileUtils.getScreenWidth(),MobileUtils.getScreenWidth());
        vh.iv_image.setLayoutParams(params);
        vh.iv_image.setPadding(0, 0, BitmapBlurUtils.dip2px(mContext, 5), 0);
        vh.iv_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 跳到查看大图界面
                Intent intent = new Intent(mContext,
                        ShowBigPictrue.class);
                intent.putExtra("position", pos);
                intent.putStringArrayListExtra("imagePath", (ArrayList)goodCommentBeanList);
                UIUtils.startActivityNextAnim(intent);
            }
        });
        return view;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return goodCommentBeanList.size();
    }


    class ViewHolder {
        ImageView iv_image;
    }
}
