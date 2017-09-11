package com.tesu.manicurehouse.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.tesu.manicurehouse.R;
import com.tesu.manicurehouse.bean.GoodsDataBean;
import com.tesu.manicurehouse.bean.ShowPhotoContentBean;
import com.tesu.manicurehouse.callback.OnCallBackContentList;
import com.tesu.manicurehouse.callback.OnCallBackDelete;
import com.tesu.manicurehouse.util.DialogUtils;
import com.tesu.manicurehouse.util.LogUtils;
import com.tesu.manicurehouse.util.PictureOption;
import com.tesu.manicurehouse.util.UIUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * 类描述：
 * 创建人：许明达
 * 创建时间：2016/04/14 10:49
 * 修改备注：
 */
public class SubmitGoodCommentAdapter extends BaseAdapter implements OnCallBackDelete{

    private String TAG="ClientListAdapter";
    private List<GoodsDataBean> goodsDataBeanList;
    private Context mContext;
    private GoodCommentImageAdapter goodCommentImageAdapter;
    private OnCallBackContentList onCallBackContentList;
    private HashMap<Integer,List<String>> pichashMap ;
    private HashMap<Integer,List<Bitmap>> bitmaphashMap;
    private PopupWindow pw;
    private View myview;
    private List<String> pics;
    private HashMap<Integer,String> contents=new HashMap<Integer,String>();
    private List<Bitmap> bitmapList;
    public SubmitGoodCommentAdapter(Context context, List<GoodsDataBean> goodsDataBeanList,OnCallBackContentList onCallBackContentList,PopupWindow pw,View view,HashMap<Integer,List<String>> pichashMap,HashMap<Integer,List<Bitmap>> bitmaphashMap){
        mContext=context;
        this.goodsDataBeanList=goodsDataBeanList;
        this.onCallBackContentList=onCallBackContentList;
        this.pw=pw;
        this.myview=view;
        this.pichashMap=pichashMap;
        this.bitmaphashMap=bitmaphashMap;
        if (!ImageLoader.getInstance().isInited()) {
            ImageLoader.getInstance()
                    .init(ImageLoaderConfiguration.createDefault(UIUtils
                            .getContext()));
        }
    }
    public void setDate(HashMap<Integer,List<String>> pichashMap,HashMap<Integer,List<Bitmap>> bitmaphashMap){
        this.pichashMap=pichashMap;
        this.bitmaphashMap=bitmaphashMap;
    }
    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return goodsDataBeanList.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(final int pos, View view, ViewGroup arg2) {
         ViewHolder vh;
        // TODO Auto-generated method stub
        if(view==null){
            view= LayoutInflater.from(mContext).inflate(R.layout.good_comment_item, null);
            vh=new ViewHolder();
            vh.iv_good=(ImageView)view.findViewById(R.id.iv_good);
            vh.tv_good_name=(TextView)view.findViewById(R.id.tv_good_name);
            vh.gv_image=(GridView)view.findViewById(R.id.gv_image);
            vh.et_tips=(EditText)view.findViewById(R.id.et_tips);
            vh.ll_add_image=(RelativeLayout)view.findViewById(R.id.ll_add_image);
            vh.et_tips.setTag(pos);
            view.setTag(vh);
        }else{
            vh=(ViewHolder)view.getTag();
            vh.et_tips.setTag(pos);
        }
        ImageLoader.getInstance().displayImage(goodsDataBeanList.get(pos).getGoods_img(), vh.iv_good, PictureOption.getSimpleOptions());
        vh.tv_good_name.setText(goodsDataBeanList.get(pos).getGoods_name());
        vh.et_tips.addTextChangedListener(new ContontTextWatcher(vh));
        if(pichashMap.get(pos)==null&&bitmaphashMap.get(pos)==null){
            bitmapList=new ArrayList<>();
            pics=new ArrayList<>();
            goodCommentImageAdapter = new GoodCommentImageAdapter(mContext, bitmapList, pics,this,pos);
        }
        else{
            pics.clear();
            bitmapList.clear();
            pics.addAll(pichashMap.get(pos));
            bitmapList.addAll(bitmaphashMap.get(pos));
            goodCommentImageAdapter = new GoodCommentImageAdapter(mContext, bitmapList, pics,this,pos);
        }
        vh.gv_image.setAdapter(goodCommentImageAdapter);
        vh.ll_add_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pichashMap.get(pos) == null && bitmaphashMap.get(pos) == null) {
                    pw.setAnimationStyle(R.style.AnimBottom);
                    pw.showAtLocation(myview,
                            Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                    onCallBackContentList.ListCallBack(pos);
                } else {
                    if (bitmaphashMap.get(pos).size() < 3) {
                        pw.setAnimationStyle(R.style.AnimBottom);
                        pw.showAtLocation(myview,
                                Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                        onCallBackContentList.ListCallBack(pos);
                    } else {
                        DialogUtils.showAlertDialog(mContext, "最多添加3张图片！");
                    }
                }
            }
        });
        if(contents.get(pos)!=null) {
            LogUtils.e("position非:"+pos);
            vh.et_tips.setText("" + contents.get(pos));
        }
        else{
            LogUtils.e("position空:"+pos);
            vh.et_tips.setText("");
        }
        return view;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return goodsDataBeanList.size();
    }

    @Override
    public void delImageCallBack(int par,int pos) {
        onCallBackContentList.ImageDelCallBack(par,pos);
    }


    class ViewHolder{
        ImageView iv_good;
        TextView tv_good_name;
        GridView gv_image;
        EditText et_tips;
        RelativeLayout ll_add_image;
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
            int position = (Integer) holder.et_tips.getTag();
            if (s.length() > 0) {
                LogUtils.e("position:"+position);
                contents.put(position, s.toString());
                onCallBackContentList.ContentListCallBack(s.toString(), position);
            } else {
                contents.put(position,null);
                onCallBackContentList.delContentList(position);
            }
        }
    }
}
