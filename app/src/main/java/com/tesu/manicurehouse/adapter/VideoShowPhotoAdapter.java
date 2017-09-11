package com.tesu.manicurehouse.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.tesu.manicurehouse.R;
import com.tesu.manicurehouse.bean.ShowPhotoContentBean;
import com.tesu.manicurehouse.bean.VideoLableBean;
import com.tesu.manicurehouse.support.PercentLinearLayout;
import com.tesu.manicurehouse.support.PercentRelativeLayout;
import com.tesu.manicurehouse.util.PictureOption;
import com.tesu.manicurehouse.util.StringUtils;

import org.w3c.dom.Text;

import java.util.List;


/**
 * 类描述：
 * 创建人：许明达
 * 创建时间：2016/04/14 10:49
 * 修改备注：
 */
public class VideoShowPhotoAdapter extends BaseAdapter{

    private String TAG="VideoShowPhotoAdapter";
    private List<ShowPhotoContentBean> showPhotoContentBeanList;
    private Context mContext;
    private ImageLoader imageLoader;

    public VideoShowPhotoAdapter(Context context, List<ShowPhotoContentBean> showPhotoContentBeanList){
        mContext=context;
        this.showPhotoContentBeanList=showPhotoContentBeanList;

        imageLoader = ImageLoader.getInstance();
        imageLoader.init((ImageLoaderConfiguration.createDefault(mContext)));
        // 使用DisplayImageOption.Builder()创建DisplayImageOptions
    }
    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return showPhotoContentBeanList.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(int pos, View view, ViewGroup parent) {
        // TODO Auto-generated method stub
        ViewHolder vh;
        if(view==null){
            view= LayoutInflater.from(mContext).inflate(R.layout.video_show_photo_item, parent,false);
            vh=new ViewHolder();
            vh.tv_content = (TextView) view.findViewById(R.id.tv_content);
            vh.image_one_iv = (ImageView) view.findViewById(R.id.image_one_iv);
            vh.image_two_iv = (ImageView) view.findViewById(R.id.image_two_iv);
            vh.rl_first = (PercentRelativeLayout) view.findViewById(R.id.rl_first);
            vh.rl_secord = (PercentLinearLayout) view.findViewById(R.id.rl_secord);
            vh.iv_first = (ImageView) view.findViewById(R.id.iv_first);
            vh.tv_first = (TextView) view.findViewById(R.id.tv_first);
            vh.tv_first_show = (TextView) view.findViewById(R.id.tv_first_show);
            vh.iv_third = (ImageView) view.findViewById(R.id.iv_third);
            view.setTag(vh);
        }else{
            vh=(ViewHolder)view.getTag();
        }
        ShowPhotoContentBean showPhotoContentBean = showPhotoContentBeanList.get(pos);


        if(pos==0){
            vh.rl_first.setVisibility(View.VISIBLE);
            vh.rl_secord.setVisibility(View.GONE);
            vh.tv_content.setVisibility(View.GONE);
            vh.iv_third.setVisibility(View.GONE);

            if(showPhotoContentBeanList.size()==1){
                vh.tv_first_show.setVisibility(View.GONE);
            }else {
                vh.tv_first_show.setVisibility(View.VISIBLE);
            }

            if(!StringUtils.isEmpty(showPhotoContentBean.getPic1())){
                imageLoader.displayImage(showPhotoContentBean.getPic1(), vh.iv_first, PictureOption.getSimpleOptions());
            }
            if(!StringUtils.isEmpty(showPhotoContentBean.getContent())){
                vh.tv_first.setText(showPhotoContentBean.getContent());
            }
        }else {
            vh.rl_first.setVisibility(View.GONE);
            vh.tv_first_show.setVisibility(View.GONE);

            if(!StringUtils.isEmpty(showPhotoContentBean.getContent())){
                vh.tv_content.setText(showPhotoContentBean.getContent());
            }
            if(showPhotoContentBean.getPicCnt()==2){
                vh.rl_secord.setVisibility(View.VISIBLE);
                vh.iv_third.setVisibility(View.GONE);
                if(!StringUtils.isEmpty(showPhotoContentBean.getPic1())){
                    imageLoader.displayImage(showPhotoContentBean.getPic1(), vh.image_one_iv, PictureOption.getSimpleOptions());
                }
                if(!StringUtils.isEmpty(showPhotoContentBean.getPic2())){
                    imageLoader.displayImage(showPhotoContentBean.getPic2(), vh.image_two_iv, PictureOption.getSimpleOptions());
                }
                vh.tv_content.setVisibility(View.VISIBLE);
            }else if(showPhotoContentBean.getPicCnt()==1){
                vh.rl_secord.setVisibility(View.GONE);
                vh.iv_third.setVisibility(View.VISIBLE);
                if(!StringUtils.isEmpty(showPhotoContentBean.getPic1())){
                    imageLoader.displayImage(showPhotoContentBean.getPic1(), vh.iv_third, PictureOption.getSimpleOptions());
                }
                vh.tv_content.setVisibility(View.VISIBLE);
            }else {
                vh.rl_secord.setVisibility(View.GONE);
                vh.iv_third.setVisibility(View.GONE);
                vh.tv_content.setVisibility(View.GONE);
            }

        }

        return view;

    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return showPhotoContentBeanList.size();
    }



    class ViewHolder{
        TextView tv_content;
        ImageView image_one_iv;
        ImageView image_two_iv;

        PercentRelativeLayout rl_first;
        PercentLinearLayout rl_secord;
        ImageView iv_first;
        TextView tv_first;
        TextView tv_first_show;
        ImageView iv_third;

    }
}
