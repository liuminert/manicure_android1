package com.tesu.manicurehouse.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.tesu.manicurehouse.R;
import com.tesu.manicurehouse.adapter.MyAddressAdapter;
import com.tesu.manicurehouse.adapter.OrderGoodsAdapter;
import com.tesu.manicurehouse.base.BaseActivity;
import com.tesu.manicurehouse.base.MyVolley;
import com.tesu.manicurehouse.bean.AddressBean;
import com.tesu.manicurehouse.callback.OnCallBackAddress;
import com.tesu.manicurehouse.protocol.GetUserAddressListProtocol;
import com.tesu.manicurehouse.protocol.UpdateUserInfoProtocol;
import com.tesu.manicurehouse.request.GetUserAddressListRequest;
import com.tesu.manicurehouse.request.UpdateUserInfoRequest;
import com.tesu.manicurehouse.response.GetUserAddressListResponse;
import com.tesu.manicurehouse.response.UpdateUserInfoResponse;
import com.tesu.manicurehouse.util.DialogUtils;
import com.tesu.manicurehouse.util.LogUtils;
import com.tesu.manicurehouse.util.SharedPrefrenceUtils;
import com.tesu.manicurehouse.util.UIUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 类描述：
 * 创建人：许明达
 * 创建时间：2016/8/16 11:40
 * 全部订单页面
 */
public class MyAddressActivity extends BaseActivity implements View.OnClickListener ,OnCallBackAddress{

    private TextView tv_top_back;
    private View rootView;
    private ListView lv_address;
    private MyAddressAdapter myAddressAdapter;
    private RelativeLayout rl_add_address;
    private String url;
    private Dialog loadingDialog;
    private GetUserAddressListResponse getUserAddressListResponse;
    private TextView tv_finish;
    private AddressBean selected_address=new AddressBean();
    //是否选择地址
    private boolean checkable=false;
    @Override
    protected View initView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        rootView = View.inflate(this, R.layout.activity_my_address, null);
        setContentView(rootView);
        tv_top_back = (TextView) rootView.findViewById(R.id.tv_top_back);
        tv_finish= (TextView) rootView.findViewById(R.id.tv_finish);
        lv_address=(ListView)rootView.findViewById(R.id.lv_address);
        rl_add_address=(RelativeLayout)rootView.findViewById(R.id.rl_add_address);
        tv_top_back.setOnClickListener(this);
        rl_add_address.setOnClickListener(this);
        tv_finish.setOnClickListener(this);
        initData();
        return null;
    }


    public void initData() {
        Intent intent=getIntent();
        checkable=intent.getBooleanExtra("checkable", false);
        loadingDialog = DialogUtils.createLoadDialog(MyAddressActivity.this, true);
        runGetUserAddressList();
        if(checkable){
            tv_finish.setVisibility(View.VISIBLE);
        }
        else {
            tv_finish.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_finish: {
                if(TextUtils.isEmpty(selected_address.getAddress())){
                    DialogUtils.showAlertDialog(MyAddressActivity.this,
                            "请选择收货地址，并点击完成！");
                }
                else{
                    Intent intent=getIntent();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("selected_address", (Serializable) selected_address);
                    intent.putExtras(bundle);
                    setResult(1, intent);
                    finish();
                    overridePendingTransition(R.anim.animprv_in, R.anim.animprv_out);
                }
                break;
            }
            case R.id.tv_top_back: {
                if(checkable){
                    if(TextUtils.isEmpty(selected_address.getAddress())){
                        DialogUtils.showAlertDialog(MyAddressActivity.this,
                                "请选择收货地址，并点击完成！");
                    }
                    else{
                        Intent intent=getIntent();
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("selected_address", (Serializable) selected_address);
                        intent.putExtras(bundle);
                        setResult(1, intent);
                        finish();
                        overridePendingTransition(R.anim.animprv_in, R.anim.animprv_out);
                    }
                }
                else{
                    finish();
                    overridePendingTransition(R.anim.animprv_in, R.anim.animprv_out);
                }

                break;
            }
            case R.id.rl_add_address:
                Intent intent=new Intent(MyAddressActivity.this,AddAddressActivity.class);
                UIUtils.startActivityForResultNextAnim(intent,1);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==1){
            if(resultCode==1){
                runGetUserAddressList();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void setDate(List<AddressBean> addressBeanList){
        if(myAddressAdapter==null){
            myAddressAdapter=new MyAddressAdapter(MyAddressActivity.this,addressBeanList,loadingDialog,this,checkable);
            lv_address.setAdapter(myAddressAdapter);
        }
        else{
            myAddressAdapter.setDate(addressBeanList);
            myAddressAdapter.notifyDataSetChanged();
        }
    }
    public void runGetUserAddressList() {
        loadingDialog.show();
        GetUserAddressListProtocol getUserAddressListProtocol = new GetUserAddressListProtocol();
        GetUserAddressListRequest getUserAddressListRequest = new GetUserAddressListRequest();
        url = getUserAddressListProtocol.getApiFun();
        getUserAddressListRequest.map.put("user_id", SharedPrefrenceUtils.getString(MyAddressActivity.this, "user_id"));


        MyVolley.uploadNoFile(MyVolley.POST, url, getUserAddressListRequest.map, new MyVolley.VolleyCallback() {
            @Override
            public void dealWithJson(String address, String json) {

                Gson gson = new Gson();
                getUserAddressListResponse = gson.fromJson(json, GetUserAddressListResponse.class);
                LogUtils.e("sendVerificationCodeResponse:" + json.toString());
                if (getUserAddressListResponse.code.equals("0")) {
                    loadingDialog.dismiss();
                    setDate(getUserAddressListResponse.dataList);
                } else {
                    loadingDialog.dismiss();
                    DialogUtils.showAlertDialog(MyAddressActivity.this,
                            getUserAddressListResponse.resultText);
                }


            }

            @Override
            public void dealWithError(String address, String error) {
                loadingDialog.dismiss();
                DialogUtils.showAlertDialog(MyAddressActivity.this, error);
            }
        });
    }

    @Override
    public void AddressCallBack(AddressBean addressBean) {
        selected_address=addressBean;
    }
}
