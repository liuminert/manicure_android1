package com.tesu.manicurehouse.adapter;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tesu.manicurehouse.R;
import com.tesu.manicurehouse.callback.OnCallVideoList;
import com.tesu.manicurehouse.response.GetVideoCategoryListResponse;
import com.tesu.manicurehouse.util.LogUtils;

import java.util.HashMap;
import java.util.List;


/**
 * 类描述：
 * 创建人：许明达
 * 创建时间：2016/04/14 10:49
 * 修改备注：
 */
public class ClassifyTitleAdapter extends BaseAdapter{

    private HashMap<String,Integer> grouphashMap=new HashMap<String,Integer>();
    private String TAG="ClientListAdapter";
    private List<GetVideoCategoryListResponse.DataList> dataLists;
    private Context mContext;
    private  OnCallVideoList onCallVideoList;
    public ClassifyTitleAdapter(Context context, List<GetVideoCategoryListResponse.DataList> dataLists,OnCallVideoList onCallVideoList){
        mContext=context;
        this.dataLists=dataLists;
        this.onCallVideoList=onCallVideoList;
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
        if(view==null){
            view= LayoutInflater.from(mContext).inflate(R.layout.classify_title_item, null);
            vh=new ViewHolder();
            vh.tv_classify=(TextView)view.findViewById(R.id.tv_classify);
            view.setTag(vh);
        }else{
            vh=(ViewHolder)view.getTag();
        }
        vh.tv_classify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setState(pos);
                onCallVideoList.VideoListItemCallBack(dataLists.get(pos).video_category_id);
                notifyDataSetChanged();
            }
        });
        if(grouphashMap.get("groupPosition")!=null){
            int groupPosition=grouphashMap.get("groupPosition");
            LogUtils.e("设置");
            if(groupPosition==pos){
                vh.tv_classify.setBackgroundColor(mContext.getResources().getColor(R.color.transparent));
                //渐变色
                Shader shader =new LinearGradient(0, 0, 0, 60, Color.rgb(255, 72, 106
                ), Color.rgb(254, 211, 70), Shader.TileMode.CLAMP);
                vh.tv_classify.getPaint().setShader(shader);
            }
            else{
                vh.tv_classify.setBackground(mContext.getResources().getDrawable(R.drawable.border_nostork));
                Shader shader =new LinearGradient(0, 0, 0, 0, Color.BLACK, Color.BLACK, Shader.TileMode.CLAMP);
                vh.tv_classify.getPaint().setShader(shader);
            }
            vh.tv_classify.postInvalidate();
        }
        vh.tv_classify.setText(""+dataLists.get(pos).category_name);
        return view;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return dataLists.size();
    }

    public void setState(int groupPosition){
        grouphashMap.put("groupPosition",groupPosition);
    }
    class ViewHolder{
        public TextView tv_classify;
    }
}
