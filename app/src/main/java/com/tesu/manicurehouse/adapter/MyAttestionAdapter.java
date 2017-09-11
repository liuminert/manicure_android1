package com.tesu.manicurehouse.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.tesu.manicurehouse.R;
import com.tesu.manicurehouse.base.MyVolley;
import com.tesu.manicurehouse.bean.MessageItem;
import com.tesu.manicurehouse.protocol.CancelFollowProtocol;
import com.tesu.manicurehouse.protocol.GetUserFollowListProtocol;
import com.tesu.manicurehouse.request.CancelFollowRequest;
import com.tesu.manicurehouse.request.GetUserFoollowListRequest;
import com.tesu.manicurehouse.response.CancelFollowResponse;
import com.tesu.manicurehouse.response.GetUserFollowListResponse;
import com.tesu.manicurehouse.util.DialogUtils;
import com.tesu.manicurehouse.util.LogUtils;
import com.tesu.manicurehouse.util.PictureOption;
import com.tesu.manicurehouse.util.SharedPrefrenceUtils;
import com.tesu.manicurehouse.util.UIUtils;
import com.tesu.manicurehouse.widget.SlideView;

import java.util.List;

/**
 * Created by Administrator on 2016/8/28 0028.
 */
public class MyAttestionAdapter extends BaseAdapter implements SlideView.OnSlideListener {
    private Context mContext;
    List<MessageItem> messageItemList;
    SlideView mLastSlideViewWithStatusOn;
    private String url;
    private Dialog loadingDialog;
    private int del_pos=0;
    private int follow_userid;
    public MyAttestionAdapter(Context context, List<MessageItem> messageItemList, SlideView mLastSlideViewWithStatusOn,Dialog loadingDialog){
        mContext=context;
        this.messageItemList=messageItemList;
        this.mLastSlideViewWithStatusOn=mLastSlideViewWithStatusOn;
        this.loadingDialog=loadingDialog;
        if (!ImageLoader.getInstance().isInited()) {
            ImageLoader.getInstance()
                    .init(ImageLoaderConfiguration.createDefault(UIUtils
                            .getContext()));
        }
    }
    @Override
    public int getCount() {
        return messageItemList.size();
    }

    @Override
    public Object getItem(int position) {
        return messageItemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        SlideView slideView = (SlideView) convertView;
        if (slideView == null) {
            View itemView =LayoutInflater.from(mContext).inflate(R.layout.myattention_list_item, null);

            slideView = new SlideView(mContext);
            slideView.setContentView(itemView);
            holder = new ViewHolder(slideView);
            slideView.setOnSlideListener(this);
            slideView.setTag(holder);
        } else {
            holder = (ViewHolder) slideView.getTag();
        }
        MessageItem item = messageItemList.get(position);
        item.slideView = slideView;
        item.slideView.shrink();

        holder.tv_attention_name.setText(item.alias);
        ImageLoader.getInstance().displayImage(item.avatar,holder.iv_attention, PictureOption.getSimpleOptions());
        holder.deleteHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                follow_userid=messageItemList.get(position).user_id;
                del_pos=position;
                runAsyncTask();
            }
        });

        return slideView;
    }

    @Override
    public void onSlide(View view, int status) {
        if (mLastSlideViewWithStatusOn != null && mLastSlideViewWithStatusOn != view) {
            mLastSlideViewWithStatusOn.shrink();
        }

        if (status == SLIDE_STATUS_ON) {
            mLastSlideViewWithStatusOn = (SlideView) view;
        }
    }

    private static class ViewHolder {
        public ImageView iv_attention;
        public TextView tv_attention_name;
        public ImageView iv_right;
        public ViewGroup deleteHolder;
        ViewHolder(View view) {
            iv_attention = (ImageView) view.findViewById(R.id.iv_attention);
            tv_attention_name = (TextView) view.findViewById(R.id.tv_attention_name);
            iv_right = (ImageView) view.findViewById(R.id.iv_right);
            deleteHolder = (ViewGroup)view.findViewById(R.id.holder);
        }

    }

    public void runAsyncTask() {
        loadingDialog.show();
        CancelFollowProtocol cancelFollowProtocol = new CancelFollowProtocol();
        CancelFollowRequest cancelFollowRequest = new CancelFollowRequest();
        url = cancelFollowProtocol.getApiFun();
        cancelFollowRequest.map.put("user_id", SharedPrefrenceUtils.getString(mContext, "user_id"));
        cancelFollowRequest.map.put("follow_userid",String.valueOf(follow_userid));

        MyVolley.uploadNoFile(MyVolley.POST, url, cancelFollowRequest.map, new MyVolley.VolleyCallback() {
            @Override
            public void dealWithJson(String address, String json) {

                Gson gson = new Gson();
                CancelFollowResponse cancelFollowResponse = gson.fromJson(json, CancelFollowResponse.class);
                LogUtils.e("sendVerificationCodeResponse:" + json.toString());
                if (cancelFollowResponse.code.equals("0")) {
                    String followCnt=SharedPrefrenceUtils.getString(mContext, "followCnt");
                    int temp=Integer.parseInt(followCnt);
                    SharedPrefrenceUtils.setString(mContext, "followCnt",""+(temp-1));
                    loadingDialog.dismiss();
                    messageItemList.remove(del_pos);
                    notifyDataSetChanged();
                } else {
                    loadingDialog.dismiss();
                    DialogUtils.showAlertDialog(mContext,
                            cancelFollowResponse.resultText);
                }


            }

            @Override
            public void dealWithError(String address, String error) {
                loadingDialog.dismiss();
                DialogUtils.showAlertDialog(mContext, error);
            }
        });
    }
}
