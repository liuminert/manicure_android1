package com.tesu.manicurehouse.adapter;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tesu.manicurehouse.R;
import com.tesu.manicurehouse.bean.ShareDescBean;
import com.tesu.manicurehouse.callback.OnCallVideoList;
import com.tesu.manicurehouse.util.DateUtils;

import java.util.List;


/**
 * 类描述：
 * 创建人：许明达
 * 创建时间：2016/04/14 10:49
 * 修改备注：
 */
public class ShareInfoAdapter extends BaseAdapter {

    private String TAG = "ClientListAdapter";
    private List<ShareDescBean> shareDescBeanList;
    private Context mContext;
    private OnCallVideoList onCallVideoList;
    //0代表邀请，1代表积分记录
    private int type;

    public ShareInfoAdapter(Context context, List<ShareDescBean> shareDescBeanList, int type) {
        mContext = context;
        this.shareDescBeanList = shareDescBeanList;
        this.type = type;
    }
    public void setDate(List<ShareDescBean> shareDescBeanList, int type){
        this.shareDescBeanList = shareDescBeanList;
        this.type = type;
    }

    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return shareDescBeanList.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return 0;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public View getView(final int pos, View view, ViewGroup arg2) {
        // TODO Auto-generated method stub
        final ViewHolder vh;
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.recommend_item, null);
            vh = new ViewHolder();
            vh.tv_time = (TextView) view.findViewById(R.id.tv_time);
            vh.tv_people = (TextView) view.findViewById(R.id.tv_people);
            vh.tv_integral = (TextView) view.findViewById(R.id.tv_integral);
            view.setTag(vh);
        } else {
            vh = (ViewHolder) view.getTag();
        }
        if (type == 0) {
            vh.tv_time.setText(shareDescBeanList.get(pos).getUser_name());
            if (!TextUtils.isEmpty(shareDescBeanList.get(pos).getCreate_time())) {
                vh.tv_people.setText(DateUtils.milliToSimpleDateTime(Long.parseLong(shareDescBeanList.get(pos).getCreate_time())*1000));
            }
            vh.tv_integral.setText(shareDescBeanList.get(pos).getMobile_phone());
        } else {
            vh.tv_time.setText(shareDescBeanList.get(pos).getType());
            if (!TextUtils.isEmpty(shareDescBeanList.get(pos).getTime())) {
                vh.tv_people.setText(DateUtils.milliToSimpleDateTime(Long.parseLong(shareDescBeanList.get(pos).getTime())*1000));
            }
            vh.tv_integral.setText(""+shareDescBeanList.get(pos).getReward());
        }
        return view;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return shareDescBeanList.size();
    }

    class ViewHolder {
        public TextView tv_time;
        public TextView tv_people;
        public TextView tv_integral;
    }
}
