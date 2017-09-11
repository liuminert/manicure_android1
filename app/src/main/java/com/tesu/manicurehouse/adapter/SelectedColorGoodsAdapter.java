package com.tesu.manicurehouse.adapter;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.tesu.manicurehouse.R;
import com.tesu.manicurehouse.bean.GoodsAttrBean;
import com.tesu.manicurehouse.callback.OnCallBackSelectColorList;
import com.tesu.manicurehouse.util.DialogUtils;
import com.tesu.manicurehouse.util.LogUtils;
import com.tesu.manicurehouse.util.LoginUtils;
import com.tesu.manicurehouse.util.PictureOption;
import com.tesu.manicurehouse.util.UIUtils;

import java.util.List;


public class SelectedColorGoodsAdapter extends BaseAdapter implements View.OnClickListener{

    private String TAG = "ClientListAdapter";
    private  List<GoodsAttrBean> selectcolorlist;
    private Context mContext;
    //选择的数量
    private String goods_num;
    private int good_storage ;
    private int del_item;
    private AlertDialog alertDialog;
    private  OnCallBackSelectColorList onCallBackSelectColorList;
    public SelectedColorGoodsAdapter(Context context,  List<GoodsAttrBean> selectcolorlist,OnCallBackSelectColorList onCallBackSelectColorList) {
        mContext = context;
        this.selectcolorlist = selectcolorlist;
        this.onCallBackSelectColorList=onCallBackSelectColorList;
        if (!ImageLoader.getInstance().isInited()) {
            ImageLoader.getInstance()
                    .init(ImageLoaderConfiguration.createDefault(UIUtils
                            .getContext()));
        }
    }

    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return selectcolorlist.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return 0;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public View getView(int pos, View view, ViewGroup arg2) {
        // TODO Auto-generated method stub
        ViewHolder vh;
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.selected_color_good_item, null);
            vh = new ViewHolder();
            vh.iv_good=(ImageView)view.findViewById(R.id.iv_good);
            vh.et_num=(EditText)view.findViewById(R.id.et_num);
            vh.tv_color_name=(TextView)view.findViewById(R.id.tv_color_name);
            vh.tv_add=(TextView)view.findViewById(R.id.tv_add);
            vh.tv_reduce=(TextView)view.findViewById(R.id.tv_reduce);
            view.setTag(vh);
        } else {
            vh = (ViewHolder) view.getTag();
        }
        TextViewListener textViewListener = new TextViewListener(pos,vh);
        vh.tv_color_name.setText(selectcolorlist.get(pos).getAttr_value());
        vh.et_num.setText(String.valueOf(selectcolorlist.get(pos).getNum()));
        ImageLoader.getInstance().displayImage(selectcolorlist.get(pos).getAttr_img(), vh.iv_good, PictureOption.getSimpleOptions());
        vh.tv_add.setOnClickListener(textViewListener);
        vh.tv_reduce.setOnClickListener(textViewListener);
        return view;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return selectcolorlist.size();
    }

    @Override
    public void onClick(View v) {
            switch (v.getId()){
                case R.id.tv_ensure:
                    selectcolorlist.remove(del_item);
                    notifyDataSetChanged();
                    alertDialog.dismiss();
                    onCallBackSelectColorList.SelectColorListCallBack(selectcolorlist);
                    break;
                case R.id.tv_cancle:
                    alertDialog.dismiss();
                    break;
            }
    }


    class ViewHolder {
        ImageView iv_good;
        TextView tv_color_name;
        EditText et_num;
        TextView tv_reduce;
        TextView tv_add;
    }
    /**
     /**
     * 点击事件
     */
    @SuppressLint("NewApi")
    private class TextViewListener implements View.OnClickListener {

        /**
         * 选择的某项
         */
        public int position = 0;
        public ViewHolder vh;
        public TextViewListener(int position,ViewHolder vh) {
            this.position = position;
            this.vh=vh;
        }

        @Override
        public void onClick(View v) {
            good_storage=selectcolorlist.get(position).getProduct_number();
            switch (v.getId()){
                case R.id.tv_add: {
                    int good_num = 1;
                    goods_num = vh.et_num.getText().toString();
                    if (TextUtils.isEmpty(goods_num)) {
                        vh.et_num.setText("1");
                    } else {
                        good_num = Integer.parseInt(goods_num);
                    }

                    if (good_num < good_storage) {
                        if (good_num < good_storage) {
                            good_num = good_num + 1;
                        }
                        goods_num = String.valueOf(good_num);
                    } else {
                        DialogUtils.showAlertDialog(mContext,
                                "购买数量不能大于库存" + good_storage + "份");
                        goods_num = "" + good_storage;
                    }
                    vh.et_num.setText(goods_num);
                    selectcolorlist.get(position).setNum(Integer.parseInt(goods_num));
                    break;
                }
                case R.id.tv_reduce: {
                    int good_num = 1;
                    goods_num = vh.et_num.getText().toString();
                    if (TextUtils.isEmpty(goods_num)) {
                        vh.et_num.setText("1");
                    } else {
                        good_num = Integer.parseInt(goods_num);
                    }
                    good_num = good_num - 1;
                    if (good_num == 0) {
                        del_item=position;
                        alertDialog=DialogUtils.showAlertDoubleBtnDialog(mContext,
                                "确定要删除这个色卡吗？", SelectedColorGoodsAdapter.this);
                    } else {
                        vh.et_num.setText("" + good_num);
                        selectcolorlist.get(position).setNum(good_num);
                    }
                    break;
                }
            }
            onCallBackSelectColorList.SelectColorListCallBack(selectcolorlist);
        }
    }

}
