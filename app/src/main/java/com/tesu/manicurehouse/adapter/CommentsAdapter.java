package com.tesu.manicurehouse.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.tesu.manicurehouse.R;
import com.tesu.manicurehouse.bean.GoodCommentBean;
import com.tesu.manicurehouse.util.LogUtils;
import com.tesu.manicurehouse.util.PictureOption;
import com.tesu.manicurehouse.util.StringUtils;
import com.tesu.manicurehouse.util.UIUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * 类描述：
 * 创建人：许明达
 * 创建时间：2016/04/14 10:49
 * 修改备注：
 */
public class CommentsAdapter extends BaseAdapter {

    private String TAG = "ClientListAdapter";
    private List<GoodCommentBean> goodCommentBeanList;
    private Context mContext;
    private CommentImageAdapter commentImageAdapter;

    public CommentsAdapter(Context context, List<GoodCommentBean> goodCommentBeanList) {
        mContext = context;
        this.goodCommentBeanList = goodCommentBeanList;
        if (!ImageLoader.getInstance().isInited()) {
            ImageLoader.getInstance()
                    .init(ImageLoaderConfiguration.createDefault(UIUtils
                            .getContext()));
        }
    }
    public void setDate(List<GoodCommentBean> goodCommentBeanList){
        this.goodCommentBeanList = goodCommentBeanList;
        notifyDataSetChanged();
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
    public View getView(int pos, View view, ViewGroup arg2) {
        // TODO Auto-generated method stub
        ViewHolder vh;
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.comment_item, null);
            vh = new ViewHolder();
            vh.gv_comment_image = (GridView) view.findViewById(R.id.gv_comment_image);
            vh.iv_image = (ImageView) view.findViewById(R.id.iv_image);
            vh.tv_user_name = (TextView) view.findViewById(R.id.tv_user_name);
            vh.tv_time = (TextView) view.findViewById(R.id.tv_time);
            vh.tv_comment = (TextView) view.findViewById(R.id.tv_comment);
            view.setTag(vh);
        } else {
            vh = (ViewHolder) view.getTag();
        }
        if (!TextUtils.isEmpty(goodCommentBeanList.get(pos).getPics())&&!goodCommentBeanList.get(pos).getPics().equals("null")) {
            Gson gson = new Gson();
            List<String> piscs = gson.fromJson(goodCommentBeanList.get(pos).getPics(), new TypeToken<List<String>>() {
            }.getType());
            commentImageAdapter = new CommentImageAdapter(mContext, piscs);
            vh.gv_comment_image.setAdapter(commentImageAdapter);
            vh.gv_comment_image.setVisibility(View.VISIBLE);
        } else {
            vh.gv_comment_image.setVisibility(View.GONE);
        }
        ImageLoader.getInstance().displayImage(goodCommentBeanList.get(pos).getAvatar(), vh.iv_image, PictureOption.getSimpleOptions());
        vh.tv_comment.setText(goodCommentBeanList.get(pos).getContent());
        vh.tv_time.setText(goodCommentBeanList.get(pos).getAdd_time());
        vh.tv_user_name.setText(goodCommentBeanList.get(pos).getUser_name());
        return view;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return goodCommentBeanList.size();
    }


    class ViewHolder {
        GridView gv_comment_image;
        ImageView iv_image;
        TextView tv_user_name;
        TextView tv_time;
        TextView tv_comment;
    }
}
