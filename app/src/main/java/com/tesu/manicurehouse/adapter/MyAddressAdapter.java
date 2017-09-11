package com.tesu.manicurehouse.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.gson.Gson;
import com.tesu.manicurehouse.R;
import com.tesu.manicurehouse.activity.AddAddressActivity;
import com.tesu.manicurehouse.base.MyVolley;
import com.tesu.manicurehouse.bean.AddressBean;
import com.tesu.manicurehouse.callback.OnCallBackAddress;
import com.tesu.manicurehouse.protocol.DeleteUserAddressProtocol;
import com.tesu.manicurehouse.request.DeleteUserAddressRequest;
import com.tesu.manicurehouse.response.DeleteUserAddressResponse;
import com.tesu.manicurehouse.support.PercentRelativeLayout;
import com.tesu.manicurehouse.util.DialogUtils;
import com.tesu.manicurehouse.util.LogUtils;
import com.tesu.manicurehouse.util.SharedPrefrenceUtils;
import com.tesu.manicurehouse.util.UIUtils;

import java.util.HashMap;
import java.util.List;


/**
 * 类描述：
 * 创建人：许明达
 * 创建时间：2016/04/14 10:49
 * 修改备注：
 */
public class MyAddressAdapter extends BaseAdapter {

    private String TAG = "ClientListAdapter";
    private List<AddressBean> addressBeanList;
    private Context mContext;
    private CommentImageAdapter commentImageAdapter;
    private String url;
    private Dialog loadingDialog;
    private DeleteUserAddressResponse deleteUserAddressResponse;
    private int address_id;
    private AddressBean deldata;
    private HashMap<String, Integer> isSelected = new HashMap<String, Integer>();
    private OnCallBackAddress onCallBackAddress;
    private boolean checkable;

    public MyAddressAdapter(Context context, List<AddressBean> addressBeanList, Dialog loadingDialog,OnCallBackAddress onCallBackAddress,boolean checkable) {
        mContext = context;
        this.addressBeanList = addressBeanList;
        this.loadingDialog = loadingDialog;
        this.onCallBackAddress=onCallBackAddress;
        this.checkable=checkable;
    }

    public void setDate(List<AddressBean> addressBeanList) {
        this.addressBeanList = addressBeanList;
    }

    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return addressBeanList.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(final int pos, View view, ViewGroup arg2) {
        // TODO Auto-generated method stub
        ViewHolder vh;
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.address_item, null);
            vh = new ViewHolder();
            vh.tv_name = (TextView) view.findViewById(R.id.tv_name);
            vh.tv_tell = (TextView) view.findViewById(R.id.tv_tell);
            vh.tv_address = (TextView) view.findViewById(R.id.tv_address);
            vh.tv_editor = (TextView) view.findViewById(R.id.tv_editor);
            vh.tv_del = (TextView) view.findViewById(R.id.tv_del);
            vh.prl_address = (PercentRelativeLayout) view.findViewById(R.id.prl_address);
            view.setTag(vh);
        } else {
            vh = (ViewHolder) view.getTag();
        }
        vh.tv_name.setText("姓名:" + addressBeanList.get(pos).getConsignee());
        vh.tv_tell.setText("电话:" + addressBeanList.get(pos).getTel());
        vh.tv_address.setText("地址:" +addressBeanList.get(pos).getProvince_name()+addressBeanList.get(pos).getCity_name()+addressBeanList.get(pos).getDistrict_name()+ addressBeanList.get(pos).getAddress());
        vh.tv_editor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, AddAddressActivity.class);
                intent.putExtra("consignee", addressBeanList.get(pos).getConsignee());
                intent.putExtra("tel", addressBeanList.get(pos).getTel());
                intent.putExtra("address", addressBeanList.get(pos).getAddress());
                intent.putExtra("address_id", addressBeanList.get(pos).getAddress_id());
                intent.putExtra("province", addressBeanList.get(pos).getProvince());
                intent.putExtra("district", addressBeanList.get(pos).getDistrict());
                intent.putExtra("city", addressBeanList.get(pos).getCity());
                UIUtils.startActivityForResultNextAnim(intent, 1);
            }
        });
        vh.tv_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                address_id = addressBeanList.get(pos).getAddress_id();
                if (address_id > 0) {
                    deldata = addressBeanList.get(pos);
                    runDeleteUserAddress();
                }
            }
        });
        if(checkable){
            if(isSelected.get("selected")!=null){
                if (isSelected.get("selected") == pos) {
                    vh.prl_address.setBackgroundDrawable(mContext.getResources().getDrawable(R.mipmap.address_selected_bg));
                } else {
                    vh.prl_address.setBackgroundDrawable(mContext.getResources().getDrawable(R.mipmap.address_bg));
                }
            }

            vh.prl_address.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isSelected.put("selected", pos);
                    onCallBackAddress.AddressCallBack(addressBeanList.get(pos));
                    notifyDataSetChanged();
                }
            });
        }
        return view;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return addressBeanList.size();
    }


    class ViewHolder {
        TextView tv_name;
        TextView tv_tell;
        TextView tv_address;
        TextView tv_editor;
        TextView tv_del;
        PercentRelativeLayout prl_address;
    }

    public void runDeleteUserAddress() {
        loadingDialog.show();
        DeleteUserAddressProtocol deleteUserAddressProtocol = new DeleteUserAddressProtocol();
        DeleteUserAddressRequest deleteUserAddressRequest = new DeleteUserAddressRequest();
        url = deleteUserAddressProtocol.getApiFun();
        deleteUserAddressRequest.map.put("user_id", SharedPrefrenceUtils.getString(mContext, "user_id"));
        deleteUserAddressRequest.map.put("address_id", String.valueOf(address_id));

        MyVolley.uploadNoFile(MyVolley.POST, url, deleteUserAddressRequest.map, new MyVolley.VolleyCallback() {
            @Override
            public void dealWithJson(String address, String json) {

                Gson gson = new Gson();
                deleteUserAddressResponse = gson.fromJson(json, DeleteUserAddressResponse.class);
                LogUtils.e("sendVerificationCodeResponse:" + json.toString());
                if (deleteUserAddressResponse.code.equals("0")) {
                    loadingDialog.dismiss();
                    addressBeanList.remove(deldata);
                    notifyDataSetChanged();
                } else {
                    loadingDialog.dismiss();
                    DialogUtils.showAlertDialog(mContext,
                            deleteUserAddressResponse.resultText);
                }


            }

            @Override
            public void dealWithError(String address, String error) {
                loadingDialog.dismiss();
                DialogUtils.showAlertDialog(mContext, error);
            }
        });
    }
}
