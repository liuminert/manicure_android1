package com.tesu.manicurehouse.adapter;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.TextAppearanceSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.tesu.manicurehouse.R;
import com.tesu.manicurehouse.base.MyVolley;
import com.tesu.manicurehouse.protocol.AddOrCancelLikedPostOrInformationCommentProtocol;
import com.tesu.manicurehouse.request.AddOrCancelLikedPostOrInformationCommentRequest;
import com.tesu.manicurehouse.response.AddOrCancelLikedPostOrInformationCommentResponse;
import com.tesu.manicurehouse.response.GetInformationCommentListResponse;
import com.tesu.manicurehouse.util.DateUtils;
import com.tesu.manicurehouse.util.DialogUtils;
import com.tesu.manicurehouse.util.LogUtils;
import com.tesu.manicurehouse.util.LoginUtils;
import com.tesu.manicurehouse.util.PictureOption;
import com.tesu.manicurehouse.util.SharedPrefrenceUtils;
import com.tesu.manicurehouse.util.UIUtils;
import com.tesu.manicurehouse.widget.CollapsibleTextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/7/16 0016.
 */
public class DetailCommentsAdapter extends BaseExpandableListAdapter {
    private Context context;
    private List<GetInformationCommentListResponse.InformationCommentBean> dataList;
    Handler handler;
    private String url;
    private Dialog loadingDialog;
    private AddOrCancelLikedPostOrInformationCommentResponse addOrCancelLikedPostOrInformationCommentResponse;
    private int operation;
    private String comment_id;
    private int clickitem;
    private  GroupHolder groupHolder = null;
    public DetailCommentsAdapter(Context context, List<GetInformationCommentListResponse.InformationCommentBean> dataList,Dialog loadingDialog) {
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
            groupHolder.iv_image=(ImageView)convertView.findViewById(R.id.iv_image);
            groupHolder.tv_user_name=(TextView)convertView.findViewById(R.id.tv_user_name);
            groupHolder.tv_like=(TextView)convertView.findViewById(R.id.tv_like);
            groupHolder.tv_time=(TextView)convertView.findViewById(R.id.tv_time);
            groupHolder.iv_comment_image=(ImageView)convertView.findViewById(R.id.iv_comment_image);
            convertView.setTag(groupHolder);
        } else {
            groupHolder = (GroupHolder) convertView.getTag();
        }
        if(TextUtils.isEmpty(  dataList.get(groupPosition).pics)){
            groupHolder.iv_comment_image.setVisibility(View.GONE);
        }
        ImageLoader.getInstance().displayImage(dataList.get(groupPosition).avatar, groupHolder.iv_image, PictureOption.getSimpleOptions());
        groupHolder.tv_user_name.setText(dataList.get(groupPosition).alias);
        if(dataList.get(groupPosition).comment_time>0) {
//            groupHolder.tv_time.setText(DateUtils.milliToSimpleDate(dataList.get(groupPosition).comment_time));
            groupHolder.tv_time.setText(DateUtils.milliToSimpleDateTime(dataList.get(groupPosition).comment_time*1000));
        }
        groupHolder.tv_like.setText(""+dataList.get(groupPosition).liked_cnt);
        groupHolder.tv_comment.setDesc(dataList.get(groupPosition).content, TextView.BufferType.NORMAL);
        groupHolder.tv_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtils.e("json 点击了");
                clickitem = groupPosition;
                comment_id = dataList.get(groupPosition).comment_id;
                if (dataList.get(groupPosition).is_liked == 1) {
                    operation = 2;
                } else {
                    operation = 1;
                }
                if (!TextUtils.isEmpty(comment_id)) {
                    if(LoginUtils.isLogin()) {
                        runAddOrCancelLikedPostOrInformationComment();
                    }
                    else{
                        DialogUtils.showAlertDialog(context,
                                "请您先登陆！");
                    }
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
                    R.layout.second_details_comment_item, null);
            itemHolder = new ItemHolder();
            itemHolder.tv_repay=(TextView)convertView.findViewById(R.id.tv_repay);
            itemHolder.tv_second_time=(TextView)convertView.findViewById(R.id.tv_second_time);
            itemHolder.tv_second_comment=(TextView)convertView.findViewById(R.id.tv_second_comment);
            itemHolder.tv_master=(TextView)convertView.findViewById(R.id.tv_master);
            itemHolder.rl_bg=(RelativeLayout)convertView.findViewById(R.id.rl_bg);
            convertView.setTag(itemHolder);
        } else {
            itemHolder = (ItemHolder) convertView.getTag();
        }
        itemHolder.tv_repay.setText(dataList.get(groupPosition).replyDataList.get(childPosition).alias);
        itemHolder.tv_second_comment.setText(dataList.get(groupPosition).replyDataList.get(childPosition).content);
        itemHolder.tv_second_time.setText(dataList.get(groupPosition).replyDataList.get(childPosition).reply_time);
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
        public TextView tv_repay;
        TextView tv_master;
        public TextView tv_second_time;
        public RelativeLayout rl_bg;
        TextView tv_second_comment;
    }

    //点赞评论
    public void runAddOrCancelLikedPostOrInformationComment() {
        loadingDialog.show();
        AddOrCancelLikedPostOrInformationCommentProtocol addOrCancelLikedPostOrInformationProtocol = new AddOrCancelLikedPostOrInformationCommentProtocol();
        AddOrCancelLikedPostOrInformationCommentRequest addOrCancelLikedPostOrInformationRequest = new AddOrCancelLikedPostOrInformationCommentRequest();
        url = addOrCancelLikedPostOrInformationProtocol.getApiFun();
        addOrCancelLikedPostOrInformationRequest.map.put("user_id", SharedPrefrenceUtils.getString(context, "user_id"));
        addOrCancelLikedPostOrInformationRequest.map.put("id", comment_id);
        addOrCancelLikedPostOrInformationRequest.map.put("operation", String.valueOf(operation));
        addOrCancelLikedPostOrInformationRequest.map.put("type", String.valueOf(2));
        LogUtils.e("map:" + addOrCancelLikedPostOrInformationRequest.map.toString());

        MyVolley.uploadNoFile(MyVolley.POST, url, addOrCancelLikedPostOrInformationRequest.map, new MyVolley.VolleyCallback() {
            @Override
            public void dealWithJson(String address, String json) {
                LogUtils.e("json:" + json);
                Gson gson = new Gson();
                addOrCancelLikedPostOrInformationCommentResponse = gson.fromJson(json, AddOrCancelLikedPostOrInformationCommentResponse.class);
                if (addOrCancelLikedPostOrInformationCommentResponse.code.equals("0")) {
                    loadingDialog.dismiss();
                    if (operation == 1) {
                        dataList.get(clickitem).is_liked = 1;
                        dataList.get(clickitem).liked_cnt=dataList.get(clickitem).liked_cnt+1;
                        groupHolder.tv_like.setText("" + dataList.get(clickitem).liked_cnt);
                    } else {
                        dataList.get(clickitem).is_liked = 0;
                        dataList.get(clickitem).liked_cnt=dataList.get(clickitem).liked_cnt-1;
                        groupHolder.tv_like.setText("" + dataList.get(clickitem).liked_cnt);
                    }
                    setDataList(dataList);
                    refresh();
                } else {
                    loadingDialog.dismiss();
                    DialogUtils.showAlertDialog(context,
                            addOrCancelLikedPostOrInformationCommentResponse.resultText);
                }


            }

            @Override
            public void dealWithError(String address, String error) {
                loadingDialog.dismiss();
                DialogUtils.showAlertDialog(context, error);
            }
        });
    }
    public void setDataList(List<GetInformationCommentListResponse.InformationCommentBean> dataList){
        this.dataList = dataList;
    }
}


