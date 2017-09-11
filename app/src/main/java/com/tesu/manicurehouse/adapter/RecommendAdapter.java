package com.tesu.manicurehouse.adapter;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.os.Build;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tesu.manicurehouse.R;
import com.tesu.manicurehouse.bean.RecommendDescBean;
import com.tesu.manicurehouse.callback.OnCallVideoList;
import com.tesu.manicurehouse.response.GetVideoCategoryListResponse;
import com.tesu.manicurehouse.util.DateUtils;
import com.tesu.manicurehouse.util.LogUtils;

import java.util.HashMap;
import java.util.List;


/**
 * 类描述：
 * 创建人：许明达
 * 创建时间：2016/04/14 10:49
 * 修改备注：
 */
public class RecommendAdapter extends BaseAdapter {

    private String TAG = "ClientListAdapter";
    private List<RecommendDescBean> dataLists;
    private Context mContext;
    private OnCallVideoList onCallVideoList;
    //0代表分享详情，1代表积分记录
    private int type;

    public RecommendAdapter(Context context, List<RecommendDescBean> dataLists) {
        mContext = context;
        this.dataLists = dataLists;
    }

    public void setDataLists(List<RecommendDescBean> dataLists) {
        this.dataLists = dataLists;
    }

    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return dataLists.get(arg0);
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
        if(!TextUtils.isEmpty(dataLists.get(pos).getCreate_time())) {
            vh.tv_time.setText(DateUtils.milliToSimpleDateTime(Long.parseLong(dataLists.get(pos).getCreate_time())*1000));
        }
        vh.tv_people.setText(dataLists.get(pos).getUser_name());
        vh.tv_integral.setText(""+dataLists.get(pos).getReward());
        return view;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return dataLists.size();
    }

    class ViewHolder {
        public TextView tv_time;
        public TextView tv_people;
        public TextView tv_integral;
    }
}
