package com.tesu.manicurehouse.adapter;

import android.app.Dialog;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.TextAppearanceSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RemoteViews;
import android.widget.TextView;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.tesu.manicurehouse.R;
import com.tesu.manicurehouse.activity.ShowBigPictrue;
import com.tesu.manicurehouse.base.MyVolley;
import com.tesu.manicurehouse.protocol.AddOrCancelLikedPostOrInformationCommentProtocol;
import com.tesu.manicurehouse.protocol.LikedCommentProtocol;
import com.tesu.manicurehouse.request.AddOrCancelLikedPostOrInformationCommentRequest;
import com.tesu.manicurehouse.request.LikedCommentRequest;
import com.tesu.manicurehouse.response.AddOrCancelLikedPostOrInformationCommentResponse;
import com.tesu.manicurehouse.response.GetVideoCommentListResponse;
import com.tesu.manicurehouse.response.LikedCommentResponse;
import com.tesu.manicurehouse.util.DateUtils;
import com.tesu.manicurehouse.util.DialogUtils;
import com.tesu.manicurehouse.util.LogUtils;
import com.tesu.manicurehouse.util.PictureOption;
import com.tesu.manicurehouse.util.SharedPrefrenceUtils;
import com.tesu.manicurehouse.util.StringUtils;
import com.tesu.manicurehouse.util.UIUtils;
import com.tesu.manicurehouse.widget.CollapsibleTextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2016/7/16 0016.
 */
public class VideoCommentsAdapter extends BaseExpandableListAdapter {
    private Context context;
    private List<GetVideoCommentListResponse.VideoCommentBean> dataList;
    private Dialog loadingDialog;
    private String url;
    private LikedCommentResponse likedCommentResponse;
    private Handler handler;
    private String comment_id;
    private  GroupHolder groupHolder = null;
    private int clickitem;
    public VideoCommentsAdapter(Context context,List<GetVideoCommentListResponse.VideoCommentBean> dataList,Dialog loadingDialog) {
        this.context = context;
        this.dataList = dataList;
        this.loadingDialog=loadingDialog;
        if (!ImageLoader.getInstance().isInited()) {
            ImageLoader.getInstance()
                    .init(ImageLoaderConfiguration.createDefault(UIUtils
                            .getContext()));
        }
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                notifyDataSetChanged();
                super.handleMessage(msg);
            }
        };
    }
    public void setDataList(List<GetVideoCommentListResponse.VideoCommentBean> dataList){
        this.dataList = dataList;
    }
    public void refresh() {
        handler.sendMessage(new Message());
    }
    @Override
    public int getGroupCount() {
        return dataList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return dataList.get(groupPosition).replyDataList.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return dataList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return dataList.get(groupPosition).replyDataList.get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(final int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(
                    R.layout.video_comment_item, null);
            groupHolder = new GroupHolder();
            // groupHolder.img = (ImageView) convertView
            // .findViewById(R.id.img);
            groupHolder.tv_comment=(CollapsibleTextView)convertView.findViewById(R.id.tv_user_comment);
            groupHolder.tv_like=(TextView)convertView.findViewById(R.id.tv_like);
            groupHolder.tv_user_name=(TextView)convertView.findViewById(R.id.tv_user_name);
            groupHolder.tv_time=(TextView)convertView.findViewById(R.id.tv_time);
            groupHolder.iv_image=(ImageView)convertView.findViewById(R.id.iv_image);
            groupHolder.iv_comment_image=(ImageView)convertView.findViewById(R.id.iv_comment_image);
            convertView.setTag(groupHolder);
        } else {
            groupHolder = (GroupHolder) convertView.getTag();
        }
        groupHolder.tv_comment.setDesc(dataList.get(groupPosition).content, TextView.BufferType.NORMAL);
        groupHolder.tv_like.setText("" + dataList.get(groupPosition).liked_cnt);
        groupHolder.tv_time.setText(DateUtils.milliToSimpleDate(Long.parseLong(dataList.get(groupPosition).comment_time)));
        groupHolder.tv_user_name.setText(dataList.get(groupPosition).alias);
        if(!TextUtils.isEmpty(dataList.get(groupPosition).pics)){
            groupHolder.iv_comment_image.setVisibility(View.VISIBLE);
//            Gson gson=new Gson();
//            List<String> piscs=gson.fromJson(dataList.get(groupPosition).pics,new TypeToken<List<String>>(){}.getType());
            if((StringUtils.getList(dataList.get(groupPosition).pics).size()>0)){
                ImageLoader.getInstance().displayImage(StringUtils.getList(dataList.get(groupPosition).pics).get(0), groupHolder.iv_comment_image, PictureOption.getSimpleOptions());
            }
        }
        else{
            groupHolder.iv_comment_image.setVisibility(View.GONE);
        }
        groupHolder.iv_comment_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if((StringUtils.getList(dataList.get(groupPosition).pics).size()>0)){
                    // 跳到查看大图界面
                    Intent intent = new Intent(context,
                            ShowBigPictrue.class);
                    intent.putExtra("position", 0);
                    intent.putStringArrayListExtra("imagePath", (ArrayList)StringUtils.getList(dataList.get(groupPosition).pics));
                    UIUtils.startActivityNextAnim(intent);
                }

            }
        });
        ImageLoader.getInstance().displayImage(dataList.get(groupPosition).avatar, groupHolder.iv_image, PictureOption.getSimpleOptions());
        groupHolder.tv_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(dataList.get(groupPosition).is_liked==0){
                    clickitem = groupPosition;
                    comment_id = dataList.get(groupPosition).comment_id;
                    if (!TextUtils.isEmpty(comment_id)) {
                        runLikedComment();
                    }
                }
                else{
                    DialogUtils.showAlertDialog(context, "您已经点赞过!");
                }
            }
        });
        return convertView;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        final ItemHolder itemHolder ;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(
                    R.layout.second_comment_item, null);
            itemHolder = new ItemHolder();
            itemHolder.tv_second_user_name=(TextView)convertView.findViewById(R.id.tv_second_user_name);
            itemHolder.tv_second_comment=(TextView)convertView.findViewById(R.id.tv_second_comment);
            itemHolder.tv_second_time=(TextView)convertView.findViewById(R.id.tv_second_time);
            itemHolder.iv_second_image=(ImageView)convertView.findViewById(R.id.iv_second_image);
            itemHolder.tv_master=(TextView)convertView.findViewById(R.id.tv_master);
            convertView.setTag(itemHolder);
        } else {
            itemHolder = (ItemHolder) convertView.getTag();
        }
        itemHolder.tv_second_user_name.setText(dataList.get(groupPosition).replyDataList.get(childPosition).alias);
        itemHolder.tv_second_comment.setText(dataList.get(groupPosition).replyDataList.get(childPosition).content);
        itemHolder.tv_second_time.setText(DateUtils.milliToSimpleDate(Long.parseLong(dataList.get(groupPosition).replyDataList.get(childPosition).reply_time)));
        itemHolder.tv_master.setText(dataList.get(groupPosition).replyDataList.get(childPosition).alias);
        ImageLoader.getInstance().displayImage(dataList.get(groupPosition).replyDataList.get(childPosition).avatar,itemHolder.iv_second_image,PictureOption.getSimpleOptions());
        if(dataList.get(groupPosition).replyDataList.get(childPosition).parent_id.equals("0")){
            itemHolder.tv_master.setText(dataList.get(groupPosition).alias);
        }
        else{
            for(int i=0;i<dataList.get(groupPosition).replyDataList.size();i++){
                if(dataList.get(groupPosition).replyDataList.get(childPosition).parent_id.equals(dataList.get(groupPosition).replyDataList.get(i).reply_id)){
                    itemHolder.tv_master.setText(dataList.get(groupPosition).replyDataList.get(i).alias);
                }
            }
        }
        return convertView;
    }


    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
    class GroupHolder {
        public CollapsibleTextView tv_comment;
        ImageView iv_image;
        TextView tv_user_name;
        TextView tv_time;
        TextView tv_like;
        ImageView iv_comment_image;
    }

    class ItemHolder {
        public TextView tv_second_user_name;
        TextView tv_second_comment;
        ImageView iv_second_image;
        TextView tv_second_time;
        TextView tv_master;
    }
    //点赞评论
    public void runLikedComment() {
        loadingDialog.show();
        LikedCommentProtocol likedCommentProtocol = new LikedCommentProtocol();
        LikedCommentRequest likedCommentRequest = new LikedCommentRequest();
        url = likedCommentProtocol.getApiFun();
        likedCommentRequest.map.put("user_id", SharedPrefrenceUtils.getString(context, "user_id"));
        likedCommentRequest.map.put("comment_id", comment_id);
        LogUtils.e("map:" + likedCommentRequest.map.toString());

        MyVolley.uploadNoFile(MyVolley.POST, url, likedCommentRequest.map, new MyVolley.VolleyCallback() {
            @Override
            public void dealWithJson(String address, String json) {
                LogUtils.e("json:" + json);
                Gson gson = new Gson();
                likedCommentResponse = gson.fromJson(json, LikedCommentResponse.class);
                if (likedCommentResponse.code==0) {
                    loadingDialog.dismiss();
                        dataList.get(clickitem).is_liked = 1;
                        dataList.get(clickitem).liked_cnt = dataList.get(clickitem).liked_cnt + 1;
                        groupHolder.tv_like.setText("" + dataList.get(clickitem).liked_cnt);
                    setDataList(dataList);
                    refresh();
                } else {
                    loadingDialog.dismiss();
                    DialogUtils.showAlertDialog(context,
                            likedCommentResponse.resultText);
                }


            }

            @Override
            public void dealWithError(String address, String error) {
                loadingDialog.dismiss();
                DialogUtils.showAlertDialog(context, error);
            }
        });
    }
}


