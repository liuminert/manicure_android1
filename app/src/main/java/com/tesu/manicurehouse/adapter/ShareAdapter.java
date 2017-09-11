package com.tesu.manicurehouse.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.ClipboardManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;


import com.tesu.manicurehouse.R;
import com.tesu.manicurehouse.base.SuperBaseAdapter;
import com.tesu.manicurehouse.bean.ShareDateBean;
import com.tesu.manicurehouse.bean.ShareInfo;
import com.tesu.manicurehouse.share.OnekeyShare;
import com.tesu.manicurehouse.support.PercentLinearLayout;
import com.tesu.manicurehouse.support.PercentRelativeLayout;
import com.tesu.manicurehouse.util.LogUtils;
import com.tesu.manicurehouse.util.UIUtils;
import com.tesu.manicurehouse.widget.MyShareImageView;

import java.util.ArrayList;
import java.util.List;

import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.system.text.ShortMessage;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

public class ShareAdapter extends SuperBaseAdapter<ShareInfo> {
    ShareHolder holder;
    private PercentLinearLayout prl_share;
    private PlatformActionListener platformActionListener;
    private ShareDateBean shareDateBean;
    private boolean isshare_weixin;
    private Context mContext;

    public ShareAdapter(List<ShareInfo> datas, PercentLinearLayout prl_share, PlatformActionListener platformActionListener, boolean isshare_weixin,Context mContext) {
        super(datas);
        this.prl_share = prl_share;
        this.platformActionListener = platformActionListener;
        this.isshare_weixin = isshare_weixin;
        this.mContext = mContext;
    }

    public ShareDateBean getShareDateBean() {
        return shareDateBean;
    }

    public void setShareDateBean(ShareDateBean shareDateBean) {
        this.shareDateBean = shareDateBean;
    }

    @SuppressLint("NewApi")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = View.inflate(UIUtils.getContext(),
                    R.layout.gridview_share_state, null);
            holder = new ShareHolder();
            holder.ivbtn_gridview_share = (MyShareImageView) convertView
                    .findViewById(R.id.ivbtn_gridview_share);
            holder.tv_gridview_share = (TextView) convertView
                    .findViewById(R.id.tv_gridview_share);
            convertView.setTag(holder);
        } else {
            holder = (ShareHolder) convertView.getTag();
        }
        // 获取集合内的对象
        ShareInfo share = mDatas.get(position);
        // 设置控件显示
        holder.ivbtn_gridview_share.setBackground(UIUtils.getContext()
                .getResources().getDrawable(share.getShare_photo()));
        if (share.getShare_name().equals(SinaWeibo.NAME)) {
            holder.tv_gridview_share.setText("新浪微博");
        } else if (share.getShare_name().equals(QZone.NAME)) {
            holder.tv_gridview_share.setText("QQ空间");
        } else if (share.getShare_name().equals(Wechat.NAME)) {
            holder.tv_gridview_share.setText("微信");
        } else if (share.getShare_name().equals(WechatMoments.NAME)) {
            holder.tv_gridview_share.setText("朋友圈");
        }else if (share.getShare_name().equals(ShortMessage.NAME)) {
            holder.tv_gridview_share.setText("通讯录");
        }
        else {
            holder.tv_gridview_share.setText("" + share.getShare_name());
        }
        holder.ivbtn_gridview_share.setOnClickListener(new ImageButtonListener(
                share, position));
        return convertView;
    }

    class ShareHolder {
        public MyShareImageView ivbtn_gridview_share;
        public TextView tv_gridview_share;
    }

    /**
     * 点击事件
     */
    @SuppressLint("NewApi")
    private class ImageButtonListener implements OnClickListener {

        /**
         * 选择的某项
         */
        private ShareInfo share = null;
        private int pos = 0;

        public ImageButtonListener(ShareInfo share, int pos) {
            this.share = share;
            this.pos = pos;
        }

        @Override
        public void onClick(View v) {
//
            if (isshare_weixin) {
                // 快捷分享，没有九宫格，只有编辑页
                OnekeyShare oks = new OnekeyShare();
                oks.setCallback(platformActionListener);
                // 设置编辑页的初始化选中平台，设置后，就没有九格宫
                oks.setPlatform(share.getShare_name());
                // 标题
                oks.setTitle(shareDateBean.getTitle());
                oks.setTitleUrl(shareDateBean.getUrl());
                oks.setSite("美甲屋");
                oks.setSiteUrl(shareDateBean.getUrl());
                if(share.getShare_name().equals(SinaWeibo.NAME)||share.getShare_name().equals(ShortMessage.NAME)) {
                     // text是分享文本,新浪要拼接在后面
                    oks.setText(shareDateBean.getContent()+shareDateBean.getUrl());
                }
                else{
                    // text是分享文本
                    oks.setText(shareDateBean.getContent());
                    oks.setUrl(shareDateBean.getUrl());
                }
                if(TextUtils.isEmpty(shareDateBean.getImageurl())){
                    oks.setImagePath(shareDateBean.getImagepath());
                }
                else{
                    oks.setImageUrl(shareDateBean.getImageurl());
                }
                // 设置platform后，silent=true,没有界面，直接分享；silent=false,就有编辑界面，没有就九格宫
                // 开发者可以自己修改，玩玩
                oks.setSilent(false);
                // 执行动作, Action share
                oks.show(UIUtils.getContext());

            }
            else{
                if(pos<=4) {
                    // 快捷分享，没有九宫格，只有编辑页
                    OnekeyShare oks = new OnekeyShare();
                    oks.setCallback(platformActionListener);
                    // 设置编辑页的初始化选中平台，设置后，就没有九格宫
                    oks.setPlatform(share.getShare_name());
                    // 标题
                    oks.setTitle(shareDateBean.getTitle());
                    oks.setTitleUrl(shareDateBean.getUrl());
                    oks.setSite("美甲屋");
                    oks.setSiteUrl(shareDateBean.getUrl());
                    if(share.getShare_name().equals(SinaWeibo.NAME)) {
//                        oks.setUrl(null);
                        // text是分享文本,新浪要拼接在后面
                        oks.setText(shareDateBean.getContent()+shareDateBean.getUrl());
                        LogUtils.e("地址："+shareDateBean.getUrl());
                    }
                    else{
                        // text是分享文本
                        oks.setText(shareDateBean.getContent());
                        oks.setUrl(shareDateBean.getUrl());
                    }

                    if(TextUtils.isEmpty(shareDateBean.getImageurl())){
                        oks.setImagePath(shareDateBean.getImagepath());
                    }
                    else{
                        oks.setImageUrl(shareDateBean.getImageurl());
                    }
                    // 设置platform后，silent=true,没有界面，直接分享；silent=false,就有编辑界面，没有就九格宫
                    // 开发者可以自己修改，玩玩
                    oks.setSilent(false);
                    // 执行动作, Action share
                    oks.show(UIUtils.getContext());
                }
                //保存和其他用的
                else{
                    ClipboardManager cm = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                    cm.setText(shareDateBean.getUrl());
                    LogUtils.e("videoUrl:"+shareDateBean.getUrl());
                    Toast.makeText(mContext, "复制成功，可以发给朋友们了。", Toast.LENGTH_LONG).show();
                }
            }
            prl_share.setVisibility(View.INVISIBLE);
        }
    }
}
