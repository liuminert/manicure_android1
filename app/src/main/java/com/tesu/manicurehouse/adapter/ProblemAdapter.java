package com.tesu.manicurehouse.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tesu.manicurehouse.R;
import com.tesu.manicurehouse.response.GetCommonProblemResponse;
import com.tesu.manicurehouse.util.LogUtils;

import java.util.HashMap;
import java.util.List;


/**
 * 类描述：
 * 创建人：许明达
 * 创建时间：2016/04/14 10:49
 * 修改备注：
 */
public class ProblemAdapter extends BaseAdapter{

    private String TAG="ClientListAdapter";
    List<GetCommonProblemResponse.CommonProblemBean> commonProblemBeanList;
    private Context mContext;
    private HashMap<Integer,Boolean> hashMap=new HashMap<Integer,Boolean>();
    public ProblemAdapter(Context context,List<GetCommonProblemResponse.CommonProblemBean> commonProblemBeanList){
        mContext=context;
        this.commonProblemBeanList=commonProblemBeanList;
    }
    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return commonProblemBeanList.get(arg0);
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
            view= LayoutInflater.from(mContext).inflate(R.layout.problem_item, null);
            vh=new ViewHolder();
            vh.tv_question=(TextView)view.findViewById(R.id.tv_question);
            vh.tv_problem=(TextView)view.findViewById(R.id.tv_problem);
            view.setTag(vh);
        }else{
            vh=(ViewHolder)view.getTag();
        }
        if(hashMap.get(pos)!=null){
            if(hashMap.get(pos)){
                vh.tv_question.setVisibility(View.VISIBLE);
            }
            else{
                vh.tv_question.setVisibility(View.GONE);
            }
        }
        else{
            vh.tv_question.setVisibility(View.GONE);
        }
        vh.tv_question.setText(commonProblemBeanList.get(pos).help_ans);
        vh.tv_problem.setText(commonProblemBeanList.get(pos).help_que);
        return view;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return commonProblemBeanList.size();
    }


    class ViewHolder{
        TextView tv_question;
        TextView tv_problem;
    }

    public void setSelect(int pos){
        if(hashMap.get(pos)!=null){
            if(hashMap.get(pos)){
                hashMap.put(pos,false);
            }
            else{
                hashMap.put(pos,true);
            }
        }
        else{
            hashMap.put(pos,true);
        }
        notifyDataSetChanged();
    }
}
