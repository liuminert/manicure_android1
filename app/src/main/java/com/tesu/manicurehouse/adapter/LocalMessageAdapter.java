package com.tesu.manicurehouse.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.tesu.manicurehouse.R;
import com.tesu.manicurehouse.bean.ShowPhotoContentBean;
import com.tesu.manicurehouse.bean.UploadVideoMessageBean;
import com.tesu.manicurehouse.support.PercentLinearLayout;
import com.tesu.manicurehouse.support.PercentRelativeLayout;
import com.tesu.manicurehouse.util.LogUtils;
import com.tesu.manicurehouse.util.MyVideoThumbLoader;
import com.tesu.manicurehouse.util.PictureOption;
import com.tesu.manicurehouse.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


/**
 * 类描述：
 * 创建人：许明达
 * 创建时间：2016/04/14 10:49
 * 修改备注：
 */
public class LocalMessageAdapter extends BaseAdapter{

    private String TAG="LocalMessageAdapter";
    private List<UploadVideoMessageBean> uploadVideoMessageBeanList;
    private Context mContext;
    private ImageLoader imageLoader;
    private Gson gson;
    private MyVideoThumbLoader mVideoThumbLoader;

    public LocalMessageAdapter(Context context, List<UploadVideoMessageBean> uploadVideoMessageBeanList){
        mContext=context;
        this.uploadVideoMessageBeanList=uploadVideoMessageBeanList;

        imageLoader = ImageLoader.getInstance();
        imageLoader.init((ImageLoaderConfiguration.createDefault(mContext)));
        // 使用DisplayImageOption.Builder()创建DisplayImageOptions
        gson = new Gson();
        mVideoThumbLoader = new MyVideoThumbLoader();// 初始化缩略图载入方法
    }
    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return uploadVideoMessageBeanList.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(int pos, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        ViewHolder holder;
        if(convertView==null){
            convertView= LayoutInflater.from(mContext).inflate(R.layout.local_message_item, parent,false);
            holder=new ViewHolder();
            holder.tv_user_name = (TextView) convertView.findViewById(R.id.tv_user_name);
            holder.tv_date = (TextView) convertView.findViewById(R.id.tv_date);
            holder.iv_vdieo_image = (ImageView) convertView.findViewById(R.id.iv_vdieo_image);
            holder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
            holder.iv_start = (ImageView) convertView.findViewById(R.id.iv_start);
            holder.iv_vido = (ImageView) convertView.findViewById(R.id.iv_vido);
            holder.iv_uesr_img = (ImageView) convertView.findViewById(R.id.iv_uesr_img);
            convertView.setTag(holder);
        }else{
            holder=(ViewHolder)convertView.getTag();
        }

        UploadVideoMessageBean item = uploadVideoMessageBeanList.get(pos);
        LogUtils.e("item:"+item.toString());
        if(!StringUtils.isEmpty(item.alias)){
            holder.tv_user_name.setText(item.alias);
        }

        if(!TextUtils.isEmpty(item.avatar)){
            imageLoader.displayImage(item.avatar, holder.iv_uesr_img, PictureOption.getSimpleOptions());
        }
        SimpleDateFormat time = new SimpleDateFormat("yyyy.MM.dd");
        String timeStr = time.format(new Date(item.create_time * 1000));
        holder.tv_date.setText(timeStr);
        if(item.type==2){
            if(!StringUtils.isEmpty(item.pics)){
                List<ShowPhotoContentBean> showPhotoContentBeans = gson.fromJson(item.pics,new TypeToken<List<ShowPhotoContentBean>>(){}.getType());
                if(showPhotoContentBeans != null){
                    imageLoader.displayImage(showPhotoContentBeans.get(0).getPic1(), holder.iv_vdieo_image, PictureOption.getSimpleOptions());
                }
            }
            holder.iv_vido.setImageResource(R.drawable.img_fouce);
            holder.iv_start.setVisibility(View.GONE);
        }else {
            holder.iv_start.setVisibility(View.VISIBLE);
            holder.iv_vido.setImageResource(R.mipmap.bt_small_video);
            if(!TextUtils.isEmpty(item.converImageUrl)){
                imageLoader.displayImage(item.converImageUrl, holder.iv_vdieo_image, PictureOption.getSimpleOptions());
            }else {
                if(!StringUtils.isEmpty(item.video_url)){
                    holder.iv_vdieo_image.setTag(item.video_url);
                    mVideoThumbLoader.showThumbByAsynctack(item.video_url, holder.iv_vdieo_image);
                }
            }
        }
        if(!StringUtils.isEmpty(item.title)){
            holder.tv_title.setText(item.title);
        }

        return convertView;

    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return uploadVideoMessageBeanList.size();
    }



    class ViewHolder{
        public TextView tv_user_name;
        public TextView tv_date;
        public ImageView iv_vdieo_image;
        public TextView tv_title;
        public ImageView iv_start;
        public ImageView iv_vido;
        public ImageView iv_uesr_img;

    }
}
