package com.tesu.manicurehouse.adapter;

import android.content.Context;
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
import com.tesu.manicurehouse.bean.VideoInfoBean;
import com.tesu.manicurehouse.bean.VideoListBean;
import com.tesu.manicurehouse.util.MyVideoThumbLoader;
import com.tesu.manicurehouse.util.PictureOption;
import com.tesu.manicurehouse.util.StringUtils;

import java.util.List;


/**
 * 类描述：
 * 创建人：许明达
 * 创建时间：2016/04/14 10:49
 * 修改备注：
 */
public class LookAdapter extends BaseAdapter{

    private String TAG="LookAdapter";
    private List<VideoListBean> videoListBeanList;
    private Context mContext;
    private MyVideoThumbLoader mVideoThumbLoader;
    private ImageLoader imageLoader;
    private Gson gson;
    public LookAdapter(Context context, List<VideoListBean> videoListBeanList){
        mContext=context;
        this.videoListBeanList=videoListBeanList;
        mVideoThumbLoader = new MyVideoThumbLoader();// 初始化缩略图载入方法
        imageLoader = ImageLoader.getInstance();
        imageLoader.init((ImageLoaderConfiguration.createDefault(mContext)));
        gson = new Gson();
    }
    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return videoListBeanList.get(arg0);
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
        if(view==null){
            view= LayoutInflater.from(mContext).inflate(R.layout.look_item, null);
            vh=new ViewHolder();

            vh.iv_video = (ImageView) view.findViewById(R.id.iv_video);
            vh.tv_title = (TextView) view.findViewById(R.id.tv_title);
            view.setTag(vh);
        }else{
            vh=(ViewHolder)view.getTag();
        }

        VideoListBean videoListBean = videoListBeanList.get(pos);

        if(!StringUtils.isEmpty(videoListBean.getTitle())){
            vh.tv_title.setText(videoListBean.getTitle());
        }
        if(videoListBean.getType()==0 || videoListBean.getType()==1){
            if(!StringUtils.isEmpty(videoListBean.getCover_img())){
                imageLoader.displayImage(videoListBean.getCover_img(), vh.iv_video, PictureOption.getSimpleOptions());
            }else {
                String url = videoListBean.getVideo_url();
                if(!StringUtils.isEmpty(url)){
                    mVideoThumbLoader.showThumbByAsynctack(url, vh.iv_video);
                }
            }

        }else if(videoListBean.getType()==2){
            List<ShowPhotoContentBean> showPhotoContentBeans = null;
            if(!StringUtils.isEmpty(videoListBean.getPics())){
                try{
                    showPhotoContentBeans = gson.fromJson(videoListBean.getPics(),new TypeToken<List<ShowPhotoContentBean>>(){}.getType());
                    if(showPhotoContentBeans != null && showPhotoContentBeans.size()>0){
                        imageLoader.displayImage(showPhotoContentBeans.get(0).getPic1(), vh.iv_video, PictureOption.getSimpleOptions());
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }

        return view;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return videoListBeanList.size();
    }


    class ViewHolder{
        ImageView iv_video;
        TextView tv_title;
    }
}
