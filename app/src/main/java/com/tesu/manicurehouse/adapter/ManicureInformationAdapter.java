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
import com.tesu.manicurehouse.response.GetInformationListResponse;
import com.tesu.manicurehouse.support.PercentRelativeLayout;
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
public class ManicureInformationAdapter extends BaseAdapter {

    private String TAG = "ClientListAdapter";
    private List<GetInformationListResponse.InformationBean> dataList;
    private Context mContext;
    private CommentImageAdapter commentImageAdapter;

    public ManicureInformationAdapter(Context context, List<GetInformationListResponse.InformationBean> dataList) {
        mContext = context;
        this.dataList = dataList;
        if (!ImageLoader.getInstance().isInited()) {
            ImageLoader.getInstance()
                    .init(ImageLoaderConfiguration.createDefault(UIUtils
                            .getContext()));
        }
    }

    public void setDataList(List<GetInformationListResponse.InformationBean> dataList){
        this.dataList = dataList;
        notifyDataSetChanged();
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
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.manicure_information_item, null);
            vh = new ViewHolder();
            vh.tv_title = (TextView) view.findViewById(R.id.tv_title);
            vh.tv_content = (TextView) view.findViewById(R.id.tv_content);
            vh.tv_like_num = (TextView) view.findViewById(R.id.tv_like_num);
            vh.tv_share_num = (TextView) view.findViewById(R.id.tv_share_num);
            vh.tv_comment_num = (TextView) view.findViewById(R.id.tv_comment_num);
            vh.tv_date = (TextView) view.findViewById(R.id.tv_date);
            vh.iv_like = (ImageView) view.findViewById(R.id.iv_like);
            vh.iv_info_image = (ImageView) view.findViewById(R.id.iv_info_image);
            vh.prl_like = (PercentRelativeLayout) view.findViewById(R.id.prl_like);
            view.setTag(vh);
        } else {
            vh = (ViewHolder) view.getTag();
        }
        String html = dataList.get(pos).content;
        vh.tv_content.setText(Html.fromHtml(html, imgGetter, null));
        vh.tv_title.setText(dataList.get(pos).title);
        vh.tv_comment_num.setText("" + dataList.get(pos).comment_cnt);
        vh.tv_share_num.setText("" + dataList.get(pos).forward_cnt);
        vh.tv_like_num.setText("" + dataList.get(pos).liked_cnt);
        vh.tv_date.setText(dataList.get(pos).add_time.substring(0,(dataList.get(pos).add_time.length()-8)));
        ImageLoader.getInstance().displayImage(dataList.get(pos).pic, vh.iv_info_image, PictureOption.getSimpleOptions());
        if (dataList.get(pos).is_liked == 1) {
            vh.iv_like.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.bt_love_selectable));
        } else {
            vh.iv_like.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.bt_love_noselectable));
        }
        return view;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return dataList.size();
    }


    class ViewHolder {
        TextView tv_title;
        ImageView iv_info_image;
        TextView tv_content;
        TextView tv_like_num;
        TextView tv_share_num;
        TextView tv_comment_num;
        TextView tv_date;
        ImageView iv_like;
        PercentRelativeLayout prl_like;
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
}
