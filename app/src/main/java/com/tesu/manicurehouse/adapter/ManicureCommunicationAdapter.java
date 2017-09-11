package com.tesu.manicurehouse.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tesu.manicurehouse.R;
import com.tesu.manicurehouse.activity.ElitePostActivity;
import com.tesu.manicurehouse.activity.ListofPopularActivity;
import com.tesu.manicurehouse.activity.MeasurementChamberActivity;
import com.tesu.manicurehouse.base.MyVolley;
import com.tesu.manicurehouse.protocol.GetBackgroupPicProtocol;
import com.tesu.manicurehouse.request.GetBackgroupPicRequest;
import com.tesu.manicurehouse.response.GetBackgroupPicResponse;
import com.tesu.manicurehouse.response.GetPostListResponse;
import com.tesu.manicurehouse.support.PercentRelativeLayout;
import com.tesu.manicurehouse.util.DialogUtils;
import com.tesu.manicurehouse.util.LogUtils;
import com.tesu.manicurehouse.util.PictureOption;
import com.tesu.manicurehouse.util.UIUtils;

import java.util.List;


@SuppressLint("ResourceAsColor")
public class ManicureCommunicationAdapter extends BaseAdapter implements View.OnClickListener{
    //头部滑动页面的holder
    private HomeHeadHolder homeHeadHolder;
    private PersonalHeadHolder personal_headHolder;
    //下面list的holder
    private DiscoveryHolder holder;
    /**
     * 上下文
     */
    List<GetPostListResponse.PostBean> dataList;
    //布局类型
    private final int TYPE_ONE = 0, TYPE_TWO = 1, TYPE_THREE = 2, TYPE_COUNT = 3;
    private Context mContext;
    private  GetBackgroupPicResponse getBackgroupPicResponse;
    private String url;
    private boolean isloaded;
    public ManicureCommunicationAdapter
            (Context context, List<GetPostListResponse.PostBean> dataList) {
        this.mContext = context;
        this.dataList = dataList;
    }

    public void setDataList(List<GetPostListResponse.PostBean> dataList) {
        this.dataList = dataList;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }
    public void setLoading(boolean isloaded) {
        this.isloaded = isloaded;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // 获取集合内的对象
        int type = getItemViewType(position);
        if (convertView == null) {
            switch (type) {
                case TYPE_ONE:
                    convertView = View.inflate(UIUtils.getContext(),
                            R.layout.personal_communication_top_item, null);
                    homeHeadHolder = new HomeHeadHolder();
                    homeHeadHolder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
                    convertView.setTag(homeHeadHolder);
                    break;
                case TYPE_TWO:
                    convertView = View.inflate(UIUtils.getContext(),
                            R.layout.personal_communication_secord_item, null);
                    holder = new DiscoveryHolder();
                    holder.iv_image = (ImageView) convertView.findViewById(R.id.iv_image);
                    holder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
                    holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
                    holder.tv_like_num = (TextView) convertView.findViewById(R.id.tv_like_num);
                    holder.tv_share_num = (TextView) convertView.findViewById(R.id.tv_share_num);
                    holder.tv_comment_num = (TextView) convertView.findViewById(R.id.tv_comment_num);
                    holder.tv_user_name = (TextView) convertView.findViewById(R.id.tv_user_name);
                    holder.iv_like = (ImageView) convertView.findViewById(R.id.iv_like);
                    holder.iv_one = (ImageView) convertView.findViewById(R.id.iv_one);
                    holder.iv_two = (ImageView) convertView.findViewById(R.id.iv_two);
                    holder.iv_three = (ImageView) convertView.findViewById(R.id.iv_three);
                    holder.ll_image = (LinearLayout) convertView.findViewById(R.id.ll_image);
                    convertView.setTag(holder);
                    break;
                case TYPE_THREE:
                    convertView = View.inflate(UIUtils.getContext(),
                            R.layout.manicure_head, null);
                    personal_headHolder = new PersonalHeadHolder();
                    personal_headHolder.iv_bg_image = (ImageView) convertView.findViewById(R.id.iv_bg_image);
                    personal_headHolder.rl_elite_post = (PercentRelativeLayout) convertView.findViewById(R.id.rl_elite_post);
                    personal_headHolder.rl_list_of_popular = (PercentRelativeLayout) convertView.findViewById(R.id.rl_list_of_popular);
                    personal_headHolder.rl_measurement_chamber = (PercentRelativeLayout) convertView.findViewById(R.id.rl_measurement_chamber);
                    convertView.setTag(personal_headHolder);
                    break;
            }

        } else {
            switch (type) {
                case TYPE_ONE:
                    homeHeadHolder = (HomeHeadHolder) convertView.getTag();
                    break;
                case TYPE_TWO:
                    holder = (DiscoveryHolder) convertView.getTag();
                    break;
                case TYPE_THREE:
                    personal_headHolder = (PersonalHeadHolder) convertView.getTag();
                    break;
            }
        }
        switch (type) {
            case TYPE_THREE:
                if (!isloaded) {
                    getBackgroupPic();
                }
                personal_headHolder. rl_list_of_popular.setOnClickListener(this);
                personal_headHolder.rl_elite_post.setOnClickListener(this);
                personal_headHolder.rl_measurement_chamber.setOnClickListener(this);
                break;
            case TYPE_ONE:
                homeHeadHolder.tv_title.setText(dataList.get(position).title);
                break;
            case TYPE_TWO:
                holder.tv_title.setText(dataList.get(position).title);
                holder.tv_time.setText(dataList.get(position).add_time);
                holder.tv_like_num.setText("" + dataList.get(position).liked_cnt);
                holder.tv_comment_num.setText("" + dataList.get(position).comment_cnt);
                holder.tv_share_num.setText("" + dataList.get(position).forward_cnt);
                holder.tv_user_name.setText(dataList.get(position).alias);
                if (dataList.get(position).is_liked == 1) {
                    holder.iv_like.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.bt_love_selectable));
                } else {
                    holder.iv_like.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.bt_love_noselectable));
                }
                ImageLoader.getInstance().displayImage(dataList.get(position).avatar, holder.iv_image, PictureOption.getSimpleOptions());
                if (!TextUtils.isEmpty(dataList.get(position).pics)) {
                    holder.ll_image.setVisibility(View.VISIBLE);
                    Gson gson = new Gson();
                    List<String> piscs = gson.fromJson(dataList.get(position).pics, new TypeToken<List<String>>() {
                    }.getType());
                    LogUtils.e("piscs:" + piscs.toString());
                    if (piscs.size() > 0 && piscs.size() < 4) {
                        switch (piscs.size()) {
                            case 1:
                                ImageLoader.getInstance().displayImage(piscs.get(0), holder.iv_one, PictureOption.getSimpleOptions());
                                holder.iv_one.setVisibility(View.VISIBLE);
                                holder.iv_two.setVisibility(View.INVISIBLE);
                                holder.iv_three.setVisibility(View.INVISIBLE);
                                break;
                            case 2:
                                ImageLoader.getInstance().displayImage(piscs.get(0), holder.iv_one, PictureOption.getSimpleOptions());
                                ImageLoader.getInstance().displayImage(piscs.get(1), holder.iv_two, PictureOption.getSimpleOptions());
                                holder.iv_one.setVisibility(View.VISIBLE);
                                holder.iv_two.setVisibility(View.VISIBLE);
                                holder.iv_three.setVisibility(View.INVISIBLE);
                                break;
                            case 3:
                                ImageLoader.getInstance().displayImage(piscs.get(0), holder.iv_one, PictureOption.getSimpleOptions());
                                ImageLoader.getInstance().displayImage(piscs.get(1), holder.iv_two, PictureOption.getSimpleOptions());
                                ImageLoader.getInstance().displayImage(piscs.get(2), holder.iv_three, PictureOption.getSimpleOptions());
                                holder.iv_one.setVisibility(View.VISIBLE);
                                holder.iv_two.setVisibility(View.VISIBLE);
                                holder.iv_three.setVisibility(View.VISIBLE);
                                break;
                        }
                    } else if (piscs.size() > 3) {
                        LogUtils.e("piscs:" + piscs.get(1) + "  " + piscs.get(2));
                        ImageLoader.getInstance().displayImage(piscs.get(0), holder.iv_one, PictureOption.getSimpleOptions());
                        ImageLoader.getInstance().displayImage(piscs.get(1), holder.iv_two, PictureOption.getSimpleOptions());
                        ImageLoader.getInstance().displayImage(piscs.get(2), holder.iv_three, PictureOption.getSimpleOptions());
                        holder.iv_one.setVisibility(View.VISIBLE);
                        holder.iv_two.setVisibility(View.VISIBLE);
                        holder.iv_three.setVisibility(View.VISIBLE);
                    }
                } else {
                    holder.iv_one.setVisibility(View.GONE);
                    holder.iv_two.setVisibility(View.GONE);
                    holder.iv_three.setVisibility(View.GONE);
                    holder.ll_image.setVisibility(View.GONE);
                }
                break;
        }
        return convertView;
    }


    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_THREE;
        } else {
            if (dataList.get(position).set_top == 1) {
                return TYPE_ONE;
            } else {
                return TYPE_TWO;
            }
        }

    }

    @Override
    public int getViewTypeCount() {
        return TYPE_COUNT;
    }

    @Override
    public void onClick(View v) {
       switch (v.getId()){
           case R.id.rl_list_of_popular: {
               Intent intent = new Intent(mContext, ListofPopularActivity.class);
               intent.putExtra("pic", getBackgroupPicResponse.pic);
               intent.putExtra("type", 1);
               UIUtils.startActivityNextAnim(intent);
               break;
           }
           case R.id.rl_elite_post: {
               Intent intent = new Intent(mContext, ElitePostActivity.class);
               intent.putExtra("pic", getBackgroupPicResponse.pic);
               intent.putExtra("type", 1);
               UIUtils.startActivityNextAnim(intent);
               break;
           }
           case R.id.rl_measurement_chamber: {
               Intent intent = new Intent(mContext, MeasurementChamberActivity.class);
               UIUtils.startActivityNextAnim(intent);
               break;
           }
       }
    }

    public class PersonalHeadHolder {
        ImageView iv_bg_image;
        PercentRelativeLayout rl_elite_post;
        PercentRelativeLayout rl_list_of_popular;
        PercentRelativeLayout rl_measurement_chamber;
    }

    public class HomeHeadHolder {
        TextView tv_title;
    }

    public class DiscoveryHolder {
        ImageView iv_image;
        TextView tv_user_name;
        TextView tv_time;
        TextView tv_title;
        TextView tv_like_num;
        TextView tv_share_num;
        TextView tv_comment_num;
        ImageView iv_like;
        ImageView iv_one;
        ImageView iv_two;
        ImageView iv_three;
        LinearLayout ll_image;

    }

    public void getBackgroupPic() {
        GetBackgroupPicProtocol getBackgroupPicProtocol = new GetBackgroupPicProtocol();
        GetBackgroupPicRequest getBackgroupPicRequest = new GetBackgroupPicRequest();
        url = getBackgroupPicProtocol.getApiFun();
        getBackgroupPicRequest.map.put("position", "2");

        MyVolley.uploadNoFile(MyVolley.POST, url, getBackgroupPicRequest.map, new MyVolley.VolleyCallback() {
            @Override
            public void dealWithJson(String address, String json) {
                LogUtils.e("json:" + json);
                Gson gson = new Gson();
                getBackgroupPicResponse = gson.fromJson(json, GetBackgroupPicResponse.class);
                if (getBackgroupPicResponse.code.equals("0")) {
                    if (!TextUtils.isEmpty(getBackgroupPicResponse.pic)) {
                        ImageLoader.getInstance().displayImage(getBackgroupPicResponse.pic, personal_headHolder.iv_bg_image);
                    }
                } else {
                    DialogUtils.showAlertDialog(mContext,
                            getBackgroupPicResponse.resultText);
                }


            }

            @Override
            public void dealWithError(String address, String error) {
                DialogUtils.showAlertDialog(mContext, error);
            }
        });
    }
}
