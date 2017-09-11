package com.tesu.manicurehouse.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.tesu.manicurehouse.R;
import com.tesu.manicurehouse.response.GetInformationListResponse;
import com.tesu.manicurehouse.response.SearchCommunityResponse;
import com.tesu.manicurehouse.support.PercentRelativeLayout;
import com.tesu.manicurehouse.util.LogUtils;
import com.tesu.manicurehouse.util.PictureOption;
import com.tesu.manicurehouse.util.UIUtils;

import java.net.URL;
import java.util.List;


/**
 * 类描述：
 * 创建人：许明达
 * 创建时间：2016/04/14 10:49
 * 修改备注：
 */
public class SearchCommunityAdapter extends BaseAdapter {

    private String TAG = "ClientListAdapter";
    private List<SearchCommunityResponse.CommunityBean> dataList;
    private Context mContext;

    public SearchCommunityAdapter(Context context, List<SearchCommunityResponse.CommunityBean> dataList) {
        mContext = context;
        this.dataList = dataList;
        if (!ImageLoader.getInstance().isInited()) {
            ImageLoader.getInstance()
                    .init(ImageLoaderConfiguration.createDefault(UIUtils
                            .getContext()));
        }
    }

    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return dataList.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(int pos, View view, ViewGroup arg2) {
        // TODO Auto-generated method stub
        DiscoveryHolder holder;
        if (view == null) {
            view = View.inflate(UIUtils.getContext(),
                    R.layout.search_community_item, null);
            holder = new DiscoveryHolder();
            holder.tv_title=(TextView)view.findViewById(R.id.tv_title);
            holder.tv_time=(TextView)view.findViewById(R.id.tv_time);
            holder.tv_like_num=(TextView)view.findViewById(R.id.tv_like_num);
            holder.tv_share_num=(TextView)view.findViewById(R.id.tv_share_num);
            holder.tv_comment_num=(TextView)view.findViewById(R.id.tv_comment_num);
            holder.iv_like=(ImageView)view.findViewById(R.id.iv_like);
            holder.iv_one=(ImageView)view.findViewById(R.id.iv_one);
            holder.iv_two=(ImageView)view.findViewById(R.id.iv_two);
            holder.iv_three=(ImageView)view.findViewById(R.id.iv_three);
            holder.ll_image=(LinearLayout)view.findViewById(R.id.ll_image);
            view.setTag(holder);
        } else {
            holder = (DiscoveryHolder) view.getTag();
        }
        holder.tv_title.setText(dataList.get(pos).title);
        holder.tv_time.setText(dataList.get(pos).add_time);
        holder.tv_like_num.setText(""+dataList.get(pos).liked_cnt);
        holder.tv_comment_num.setText(""+dataList.get(pos).comment_cnt);
        holder.tv_share_num.setText("" + dataList.get(pos).forward_cnt);
        if(dataList.get(pos).is_liked==1){
            holder.iv_like.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.bt_love_selectable));
        }
        else{
            holder.iv_like.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.bt_love_noselectable));
        }
        if(!TextUtils.isEmpty(dataList.get(pos).pic)){
            if(dataList.get(pos).type==1) {
                Gson gson = new Gson();
                List<String> piscs = gson.fromJson(dataList.get(pos).pic, new TypeToken<List<String>>() {
                }.getType());
                LogUtils.e("piscs:" + piscs.toString());
                if (piscs.size() > 0 && piscs.size() < 4) {
                    switch (piscs.size()) {
                        case 1:
                            ImageLoader.getInstance().displayImage(piscs.get(0), holder.iv_one, PictureOption.getSimpleOptions());
                            holder.iv_one.setVisibility(View.VISIBLE);
                            holder.iv_two.setVisibility(View.INVISIBLE);
                            holder.iv_three.setVisibility(View.INVISIBLE);
                            break;
                        case 2:
                            ImageLoader.getInstance().displayImage(piscs.get(0), holder.iv_one, PictureOption.getSimpleOptions());
                            ImageLoader.getInstance().displayImage(piscs.get(1), holder.iv_two, PictureOption.getSimpleOptions());
                            holder.iv_one.setVisibility(View.VISIBLE);
                            holder.iv_two.setVisibility(View.VISIBLE);
                            holder.iv_three.setVisibility(View.INVISIBLE);
                            break;
                        case 3:
                            ImageLoader.getInstance().displayImage(piscs.get(0), holder.iv_one, PictureOption.getSimpleOptions());
                            ImageLoader.getInstance().displayImage(piscs.get(1), holder.iv_two, PictureOption.getSimpleOptions());
                            ImageLoader.getInstance().displayImage(piscs.get(2), holder.iv_three, PictureOption.getSimpleOptions());
                            holder.iv_one.setVisibility(View.VISIBLE);
                            holder.iv_two.setVisibility(View.VISIBLE);
                            holder.iv_three.setVisibility(View.VISIBLE);
                            break;
                    }
                } else if (piscs.size() > 3) {
                    LogUtils.e("piscs:" + piscs.get(1) + "  " + piscs.get(2));
                    ImageLoader.getInstance().displayImage(piscs.get(0), holder.iv_one, PictureOption.getSimpleOptions());
                    ImageLoader.getInstance().displayImage(piscs.get(1), holder.iv_two, PictureOption.getSimpleOptions());
                    ImageLoader.getInstance().displayImage(piscs.get(2), holder.iv_three, PictureOption.getSimpleOptions());
                    holder.iv_one.setVisibility(View.VISIBLE);
                    holder.iv_two.setVisibility(View.VISIBLE);
                    holder.iv_three.setVisibility(View.VISIBLE);
                }
            }
            else{
                ImageLoader.getInstance().displayImage(dataList.get(pos).pic, holder.iv_one, PictureOption.getSimpleOptions());
                holder.iv_one.setVisibility(View.VISIBLE);
                holder.iv_two.setVisibility(View.INVISIBLE);
                holder.iv_three.setVisibility(View.INVISIBLE);
            }
        }
        else{
            holder.iv_one.setVisibility(View.GONE);
            holder.iv_two.setVisibility(View.GONE);
            holder.iv_three.setVisibility(View.GONE);
        }
        return view;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return dataList.size();
    }


    public class DiscoveryHolder {
        TextView tv_time;
        TextView tv_title;
        TextView tv_like_num;
        TextView tv_share_num;
        TextView tv_comment_num;
        ImageView iv_like;
        ImageView iv_one;
        ImageView iv_two;
        ImageView iv_three;
        LinearLayout ll_image;

    }

}
