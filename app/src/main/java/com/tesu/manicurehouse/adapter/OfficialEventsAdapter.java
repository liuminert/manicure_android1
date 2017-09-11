package com.tesu.manicurehouse.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.tesu.manicurehouse.R;
import com.tesu.manicurehouse.response.GetActListResponse;
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
public class OfficialEventsAdapter extends BaseAdapter{

    private String TAG="ClientListAdapter";
    private List<GetActListResponse.ActBean> dataList;
    private Context mContext;
    public OfficialEventsAdapter(Context context, List<GetActListResponse.ActBean> dataList){
        mContext=context;
        this.dataList=dataList;
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
        ViewHolder vh;
        if(view==null){
            view= LayoutInflater.from(mContext).inflate(R.layout.official_event_item, null);
            vh=new ViewHolder();
            vh.iv_info_image=(ImageView)view.findViewById(R.id.iv_info_image);
            vh.tv_title_name=(TextView)view.findViewById(R.id.tv_title_name);
            vh.tv_content=(TextView)view.findViewById(R.id.tv_content);
            vh.tv_date=(TextView)view.findViewById(R.id.tv_date);
            view.setTag(vh);
        }else{
            vh=(ViewHolder)view.getTag();
        }
        vh.tv_title_name.setText(dataList.get(pos).act_title);
        vh.tv_content.setText(Html.fromHtml(dataList.get(pos).act_content, imgGetter, null));

        vh.tv_date.setText(dataList.get(pos).create_time);
        ImageLoader.getInstance().displayImage(dataList.get(pos).act_pic,vh.iv_info_image, PictureOption.getSimpleOptions());
        return view;
    }
    //这里面的resource就是fromhtml函数的第一个参数里面的含有的url
    Html.ImageGetter imgGetter = new Html.ImageGetter() {
        public Drawable getDrawable(String source) {
            Drawable drawable = null;
            URL url;
            try {
                url = new URL(source);
                drawable = Drawable.createFromStream(url.openStream(), ""); // 获取网路图片
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
                    drawable.getIntrinsicHeight());
            return drawable;
        }
    };

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return dataList.size();
    }


    class ViewHolder{
        ImageView iv_info_image;
        TextView tv_title_name;
        TextView tv_content;
        TextView tv_date;
    }
}
