package com.tesu.manicurehouse.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.tesu.manicurehouse.R;
import com.tesu.manicurehouse.bean.ShowPhotoContentBean;
import com.tesu.manicurehouse.bean.VideoLableBean;
import com.tesu.manicurehouse.support.PercentRelativeLayout;
import com.tesu.manicurehouse.util.StringUtils;

import java.util.List;


/**
 * 类描述：
 * 创建人：许明达
 * 创建时间：2016/04/14 10:49
 * 修改备注：
 */
public class ShowPhotoContentAdapter extends BaseAdapter{

    private String TAG=ShowPhotoContentAdapter.class.getSimpleName();
    private List<ShowPhotoContentBean> showPhotoContentBeanList;
    private Context mContext;
    private CommentImageAdapter commentImageAdapter;
    private ICallBack callBack;
    private ImageLoader imageLoader;
    private DisplayImageOptions options;
    public ShowPhotoContentAdapter(Context context, List<ShowPhotoContentBean> showPhotoContentBeanList){
        mContext=context;
        this.showPhotoContentBeanList=showPhotoContentBeanList;

        imageLoader = ImageLoader.getInstance();
        imageLoader.init((ImageLoaderConfiguration.createDefault(mContext)));
        // 使用DisplayImageOption.Builder()创建DisplayImageOptions
        options = new DisplayImageOptions.Builder()
                .showStubImage(R.drawable.pic_s) // 设置图片下载期间显示的图片
                .showImageForEmptyUri(R.drawable.pic_s) // 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.drawable.pic_s) // 设置图片加载或解码过程中发生错误显示的图片
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisc(true) // 设置下载的图片是否缓存在SD卡中
                        // .displayer(new RoundedBitmapDisplayer(20)) // 设置成圆角图片
                .bitmapConfig(Bitmap.Config.ARGB_8888)
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                .build(); // 创建配置过的DisplayImageOption对象

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
    public View getView(final int pos, View view, ViewGroup parent) {
        // TODO Auto-generated method stub
        ViewHolder vh;
        if(view==null){
            view= LayoutInflater.from(mContext).inflate(R.layout.show_photo_content_item, parent,false);
            vh=new ViewHolder();
            vh.add_image_iv = (ImageView) view.findViewById(R.id.add_image_iv);
            vh.image_one_iv = (ImageView) view.findViewById(R.id.image_one_iv);
            vh.image_two_iv = (ImageView) view.findViewById(R.id.image_two_iv);
            vh.show_message_tv = (TextView) view.findViewById(R.id.show_message_tv);
            vh.content_et = (EditText) view.findViewById(R.id.content_et);
            vh.add_total_iv = (ImageView) view.findViewById(R.id.add_total_iv);
            vh.content_et.setTag(pos);
            vh.content_et.addTextChangedListener(new ContontTextWatcher(vh));
            view.setTag(vh);
        }else{
            vh=(ViewHolder)view.getTag();
            vh.content_et.setTag(pos);
        }
        final ShowPhotoContentBean showPhotoContentBean = showPhotoContentBeanList.get(pos);
        if(!StringUtils.isEmpty(showPhotoContentBean.getPic1())){
            vh.image_one_iv.setVisibility(View.VISIBLE);
//            imageLoader.displayImage("file://"+showPhotoContentBean.getPic1(), vh.image_one_iv, options);
            imageLoader.displayImage(showPhotoContentBean.getPic1(), vh.image_one_iv, options);
            vh.show_message_tv.setVisibility(View.GONE);
        }else {
            vh.image_one_iv.setVisibility(View.GONE);
            vh.show_message_tv.setVisibility(View.VISIBLE);
        }
        if(!StringUtils.isEmpty(showPhotoContentBean.getPic2())){
            vh.image_two_iv.setVisibility(View.VISIBLE);
//            imageLoader.displayImage("file://"+showPhotoContentBean.getPic2(), vh.image_two_iv, options);
            imageLoader.displayImage(showPhotoContentBean.getPic2(), vh.image_two_iv, options);
            vh.show_message_tv.setVisibility(View.GONE);
        }else {
            vh.image_two_iv.setVisibility(View.GONE);
        }
        if(pos ==0){
            if(showPhotoContentBean.getPicCnt()!=0){
                vh.add_image_iv.setVisibility(View.GONE);
            }else {
                vh.add_image_iv.setVisibility(View.VISIBLE);
            }
        }else {
            if(showPhotoContentBean.getPicCnt()==2){
                vh.add_image_iv.setVisibility(View.GONE);
            }else {
                vh.add_image_iv.setVisibility(View.VISIBLE);
            }
        }

        if(pos == showPhotoContentBeanList.size()-1){
            vh.add_total_iv.setVisibility(View.VISIBLE);
        }else {
            vh.add_total_iv.setVisibility(View.GONE);
        }
        String content = showPhotoContentBean.getContent();
        if(!StringUtils.isEmpty(content)){
            vh.content_et.setText(content);
        }else {
            vh.content_et.setText("");
        }

        final int position = pos;

        vh.add_image_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addImage(position);
            }
        });
        vh.add_total_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTotal(position);
            }
        });
        vh.image_one_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callBack.updateImage(1, position);
            }
        });
        vh.image_two_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callBack.updateImage(2, position);
            }
        });

        return view;

    }

    class ContontTextWatcher implements TextWatcher{
        private ViewHolder holder;
        public ContontTextWatcher(ViewHolder viewHolder){
            super();
            this.holder = viewHolder;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            String contont = s.toString();
            int position = (Integer) holder.content_et.getTag();
            ShowPhotoContentBean showPhotoContentBean = showPhotoContentBeanList.get(position);
            showPhotoContentBean.setContent(contont);
            showPhotoContentBeanList.set(position,showPhotoContentBean);
        }
    }

    private void addTotal(int pos){
        callBack.addTotal(pos);
    }

    /**添加图片**/
    private void addImage(int pos) {
        callBack.addImage(pos);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return showPhotoContentBeanList.size();
    }



    class ViewHolder{
        ImageView add_image_iv;
        ImageView image_one_iv;
        ImageView image_two_iv;
        TextView show_message_tv;
        EditText content_et;
        ImageView add_total_iv;
    }

    public interface ICallBack{
        void addImage(int pos);
        void addTotal(int pos);
        void updateImage(int type,int pos);
    }

    public void setCallBack(ICallBack callBack){
        this.callBack =callBack;
    }
}
