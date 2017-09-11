package com.tesu.manicurehouse.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.tesu.manicurehouse.R;
import com.tesu.manicurehouse.bean.PhotoUpImageItem;
import com.tesu.manicurehouse.bean.ShowPhotoContentBean;
import com.tesu.manicurehouse.record.VideoMakePhotoActivity;
import com.tesu.manicurehouse.util.StringUtils;

import java.util.List;


/**
 * 类描述：
 * 创建人：许明达
 * 创建时间：2016/04/14 10:49
 * 修改备注：
 */
public class VideoSelectPhoteItemAdapter extends BaseAdapter{

    private List<PhotoUpImageItem> list;
    private LayoutInflater layoutInflater;
    private ImageLoader imageLoader;
    private DisplayImageOptions options;
    private VideoMakePhotoActivity mContext;
    public VideoSelectPhoteItemAdapter(List<PhotoUpImageItem> list, VideoMakePhotoActivity context){
        this.list = list;
        layoutInflater = LayoutInflater.from(context);
        imageLoader = ImageLoader.getInstance();
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

        this.mContext = context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Holder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.item_video_select_photo_item, parent, false);
            holder = new Holder();
            holder.imageView = (ImageView) convertView.findViewById(R.id.image_item);
            holder.delete_iv = (ImageView) convertView.findViewById(R.id.delete_iv);
            holder.et_subtitle = (EditText) convertView.findViewById(R.id.et_subtitle);
            holder.et_subtitle.setTag(position);
            holder.et_subtitle.addTextChangedListener(new ContontTextWatcher(holder));
            convertView.setTag(holder);
        }else {
            holder = (Holder) convertView.getTag();
            holder.et_subtitle.setTag(position);
        }
        //图片加载器的使用代码，就这一句代码即可实现图片的加载。请注意
        //这里的uri地址，因为我们现在实现的是获取本地图片，所以使
        //用"file://"+图片的存储地址。如果要获取网络图片，
        //这里的uri就是图片的网络地址。
        imageLoader.displayImage("file://" + list.get(position).getImagePath(), holder.imageView, options);

        holder.delete_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.deletePhoto(position);
            }
        });

        String content = list.get(position).getContent();
        if(StringUtils.isEmpty(content)){
            holder.et_subtitle.setText("");
        }else {
            holder.et_subtitle.setText(content);
        }
        return convertView;
    }

    class ContontTextWatcher implements TextWatcher {
        private Holder holder;
        public ContontTextWatcher(Holder viewHolder){
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
            int position = (Integer) holder.et_subtitle.getTag();
            PhotoUpImageItem photoUpImageItem = list.get(position);
            photoUpImageItem.setContent(contont);
            list.set(position,photoUpImageItem);
        }
    }

    class Holder{
        ImageView imageView;
        ImageView delete_iv;
        EditText et_subtitle;
    }
}
