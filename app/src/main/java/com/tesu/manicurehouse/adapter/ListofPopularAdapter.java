package com.tesu.manicurehouse.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tesu.manicurehouse.R;
import com.tesu.manicurehouse.response.GetPostListResponse;
import com.tesu.manicurehouse.util.LogUtils;
import com.tesu.manicurehouse.util.PictureOption;
import com.tesu.manicurehouse.util.UIUtils;

import java.util.List;


@SuppressLint("ResourceAsColor")
public class ListofPopularAdapter extends BaseAdapter {
    //下面list的holder
    private DiscoveryHolder holder;
    /**
     * 上下文
     */
    List<GetPostListResponse.PostBean> dataList;
    private Context mContext;


    public ListofPopularAdapter
            (Context context, List<GetPostListResponse.PostBean> dataList) {
        this.mContext = context;
        this.dataList = dataList;
    }
    public void setDataList(List<GetPostListResponse.PostBean> dataList){
        this.dataList = dataList;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // 获取集合内的对象
        int type = getItemViewType(position);
        if (convertView == null) {
                    convertView = View.inflate(UIUtils.getContext(),
                            R.layout.personal_communication_secord_item, null);
                    holder = new DiscoveryHolder();
                    holder.iv_image=(ImageView)convertView.findViewById(R.id.iv_image);
                    holder.tv_title=(TextView)convertView.findViewById(R.id.tv_title);
                    holder.tv_time=(TextView)convertView.findViewById(R.id.tv_time);
                    holder.tv_like_num=(TextView)convertView.findViewById(R.id.tv_like_num);
                    holder.tv_share_num=(TextView)convertView.findViewById(R.id.tv_share_num);
                    holder.tv_comment_num=(TextView)convertView.findViewById(R.id.tv_comment_num);
                    holder.tv_user_name=(TextView)convertView.findViewById(R.id.tv_user_name);
                    holder.iv_like=(ImageView)convertView.findViewById(R.id.iv_like);
                    holder.iv_one=(ImageView)convertView.findViewById(R.id.iv_one);
                    holder.iv_two=(ImageView)convertView.findViewById(R.id.iv_two);
                    holder.iv_three=(ImageView)convertView.findViewById(R.id.iv_three);
                    holder.ll_image=(LinearLayout)convertView.findViewById(R.id.ll_image);
                    convertView.setTag(holder);

        } else {
                    holder = (DiscoveryHolder) convertView.getTag();
        }
                holder.tv_title.setText(dataList.get(position).title);
                holder.tv_time.setText(dataList.get(position).add_time);
                holder.tv_like_num.setText(""+dataList.get(position).liked_cnt);
                holder.tv_comment_num.setText(""+dataList.get(position).comment_cnt);
                holder.tv_share_num.setText("" + dataList.get(position).forward_cnt);
                holder.tv_user_name.setText(dataList.get(position).alias);
                if(dataList.get(position).is_liked==1){
                    holder.iv_like.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.bt_love_selectable));
                }
                else{
                    holder.iv_like.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.bt_love_noselectable));
                }
                ImageLoader.getInstance().displayImage(dataList.get(position).avatar,holder.iv_image, PictureOption.getSimpleOptions());
                if(!TextUtils.isEmpty(dataList.get(position).pics)){
                    holder.ll_image.setVisibility(View.VISIBLE);
                    Gson gson=new Gson();
                    List<String> piscs=gson.fromJson(dataList.get(position).pics,new TypeToken<List<String>>(){}.getType());
                    LogUtils.e("piscs:"+piscs.toString());
                    if(piscs.size()>0&&piscs.size()<4){
                        switch (piscs.size()){
                            case 1:
                                ImageLoader.getInstance().displayImage(piscs.get(0),holder.iv_one, PictureOption.getSimpleOptions());
                                holder.iv_one.setVisibility(View.VISIBLE);
                                holder.iv_two.setVisibility(View.INVISIBLE);
                                holder.iv_three.setVisibility(View.INVISIBLE);
                                break;
                            case 2:
                                ImageLoader.getInstance().displayImage(piscs.get(0),holder.iv_one, PictureOption.getSimpleOptions());
                                ImageLoader.getInstance().displayImage(piscs.get(1), holder.iv_two, PictureOption.getSimpleOptions());
                                holder.iv_one.setVisibility(View.VISIBLE);
                                holder.iv_two.setVisibility(View.VISIBLE);
                                holder.iv_three.setVisibility(View.INVISIBLE);
                                break;
                            case 3:
                                ImageLoader.getInstance().displayImage(piscs.get(0),holder.iv_one, PictureOption.getSimpleOptions());
                                ImageLoader.getInstance().displayImage(piscs.get(1),holder.iv_two, PictureOption.getSimpleOptions());
                                ImageLoader.getInstance().displayImage(piscs.get(2),holder.iv_three, PictureOption.getSimpleOptions());
                                holder.iv_one.setVisibility(View.VISIBLE);
                                holder.iv_two.setVisibility(View.VISIBLE);
                                holder.iv_three.setVisibility(View.VISIBLE);
                                break;
                        }
                    }
                    else if(piscs.size()>3){
                        LogUtils.e("piscs:"+piscs.get(1)+"  "+piscs.get(2));
                        ImageLoader.getInstance().displayImage(piscs.get(0),holder.iv_one, PictureOption.getSimpleOptions());
                        ImageLoader.getInstance().displayImage(piscs.get(1),holder.iv_two, PictureOption.getSimpleOptions());
                        ImageLoader.getInstance().displayImage(piscs.get(2),holder.iv_three, PictureOption.getSimpleOptions());
                        holder.iv_one.setVisibility(View.VISIBLE);
                        holder.iv_two.setVisibility(View.VISIBLE);
                        holder.iv_three.setVisibility(View.VISIBLE);
                    }
                }
                else{
                    holder.iv_one.setVisibility(View.GONE);
                    holder.iv_two.setVisibility(View.GONE);
                    holder.iv_three.setVisibility(View.GONE);
                    holder.ll_image.setVisibility(View.GONE);
                }
        return convertView;
    }



    public class DiscoveryHolder {
        ImageView iv_image;
        TextView tv_user_name;
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
