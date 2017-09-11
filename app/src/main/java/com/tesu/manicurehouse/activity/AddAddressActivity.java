package com.tesu.manicurehouse.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.tesu.manicurehouse.R;
import com.tesu.manicurehouse.base.BaseActivity;
import com.tesu.manicurehouse.base.MyVolley;
import com.tesu.manicurehouse.bean.MyRegion;
import com.tesu.manicurehouse.protocol.AddOrUpdateUserAddressProtocol;
import com.tesu.manicurehouse.protocol.GetRegionListProtocol;
import com.tesu.manicurehouse.request.AddOrUpdateUserAddressRequest;
import com.tesu.manicurehouse.request.GetRegionListRequest;
import com.tesu.manicurehouse.response.AddOrUpdateUserAddressResponse;
import com.tesu.manicurehouse.response.GetRegionListResponse;
import com.tesu.manicurehouse.support.PercentRelativeLayout;
import com.tesu.manicurehouse.util.DialogUtils;
import com.tesu.manicurehouse.util.LogUtils;
import com.tesu.manicurehouse.util.SharedPrefrenceUtils;
import com.tesu.manicurehouse.util.UIUtils;

import java.util.ArrayList;

/**
 * 类描述：
 * 创建人：许明达
 * 创建时间：2016/8/30 10:40
 * 更换手机页面
 */
public class AddAddressActivity extends BaseActivity implements View.OnClickListener {

    private ImageView iv_top_back;
    private View rootView;
    private PercentRelativeLayout rl_region;
    private String province;
    private String city;
    private String district;
    private TextView tv_region;
    private String url;
    private Dialog loadingDialog;
    private AddOrUpdateUserAddressResponse addOrUpdateUserAddressResponse;
    //收货人
    private String consignee;
    private String address;
    private String tell;
    private EditText et_consignee;
    private EditText et_tellphone;
    private EditText et_address;
    private Button bt_save;
    private TextView tv_title;
    private int address_id=0;
    private Handler handler = new Handler() {
        @SuppressWarnings("unchecked")
        public void handleMessage(android.os.Message msg) {

            switch (msg.what) {

                case 1:
                    Intent intent = getIntent();
                    setResult(1, intent);
                    finish();
                    overridePendingTransition(R.anim.animprv_in, R.anim.animprv_out);
                    break;

            }
        }

        ;
    };

    @Override
    protected View initView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        rootView = View.inflate(this, R.layout.activity_add_address, null);
        setContentView(rootView);
        iv_top_back = (ImageView) rootView.findViewById(R.id.iv_top_back);
        rl_region = (PercentRelativeLayout) rootView.findViewById(R.id.rl_region);
        tv_region = (TextView) rootView.findViewById(R.id.tv_region);
        et_consignee = (EditText) rootView.findViewById(R.id.et_consignee);
        et_tellphone = (EditText) rootView.findViewById(R.id.et_tellphone);
        et_address = (EditText) rootView.findViewById(R.id.et_address);
        bt_save = (Button) rootView.findViewById(R.id.bt_save);
        tv_title=(TextView)rootView.findViewById(R.id.tv_title);
        initData();
        return null;
    }


    public void initData() {
        Intent intent=getIntent();
        consignee=intent.getStringExtra("consignee");
        tell=intent.getStringExtra("tel");
        address=intent.getStringExtra("address");
        address_id=intent.getIntExtra("address_id", 0);
        province=intent.getStringExtra("province");
        district=intent.getStringExtra("district");
        city=intent.getStringExtra("city");
        loadingDialog = DialogUtils.createLoadDialog(AddAddressActivity.this, true);
        rl_region.setOnClickListener(this);
        iv_top_back.setOnClickListener(this);
        bt_save.setOnClickListener(this);
        LogUtils.e("address_id:"+address_id);
        if(address_id>0){
            tv_title.setText("编辑收货地址");
            et_tellphone.setText(tell);
            et_address.setText(address);
            et_consignee.setText(consignee);
        }
        else{
            tv_title.setText("新增收货地址");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_save: {
                consignee = et_consignee.getText().toString().trim();
                address = et_address.getText().toString().trim();
                tell = et_tellphone.getText().toString().trim();
                if (!TextUtils.isEmpty(consignee) && !TextUtils.isEmpty(address) && !TextUtils.isEmpty(tell)) {
                    runAsyncTask();
                }
                break;
            }
            case R.id.iv_top_back: {
                finish();
                overridePendingTransition(R.anim.animprv_in, R.anim.animprv_out);
                break;
            }
            case R.id.rl_region: {
                Intent intent = new Intent(AddAddressActivity.this, RegionSelectActivity.class);
                UIUtils.startActivityForResultNextAnim(intent, 1);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == 1) {
                tv_region.setText(data.getStringExtra("region"));
                province = data.getStringExtra("province");
                city = data.getStringExtra("city");
                district = data.getStringExtra("district");
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void runAsyncTask() {
        loadingDialog.show();
        AddOrUpdateUserAddressProtocol addOrUpdateUserAddressProtocol = new AddOrUpdateUserAddressProtocol();
        AddOrUpdateUserAddressRequest addOrUpdateUserAddressRequest = new AddOrUpdateUserAddressRequest();
        url = addOrUpdateUserAddressProtocol.getApiFun();
        addOrUpdateUserAddressRequest.map.put("address_id", String.valueOf(address_id));
        addOrUpdateUserAddressRequest.map.put("consignee", consignee);
        addOrUpdateUserAddressRequest.map.put("country", "1");
        addOrUpdateUserAddressRequest.map.put("province", province);
        addOrUpdateUserAddressRequest.map.put("city", city);
        addOrUpdateUserAddressRequest.map.put("district", district);
        addOrUpdateUserAddressRequest.map.put("address", address);
        addOrUpdateUserAddressRequest.map.put("tel", tell);
        addOrUpdateUserAddressRequest.map.put("user_id", SharedPrefrenceUtils.getString(AddAddressActivity.this, "user_id"));

        MyVolley.uploadNoFile(MyVolley.POST, url, addOrUpdateUserAddressRequest.map, new MyVolley.VolleyCallback() {
            @Override
            public void dealWithJson(String address, String json) {

                Gson gson = new Gson();
                addOrUpdateUserAddressResponse = gson.fromJson(json, AddOrUpdateUserAddressResponse.class);
                LogUtils.e("sendVerificationCodeResponse:" + json.toString());
                loadingDialog.dismiss();
                if (addOrUpdateUserAddressResponse.code.equals("0")) {
                    handler.sendEmptyMessage(1);
                } else {
                    DialogUtils.showAlertDialog(AddAddressActivity.this,
                            addOrUpdateUserAddressResponse.resultText);
                }


            }

            @Override
            public void dealWithError(String address, String error) {
                loadingDialog.dismiss();
                DialogUtils.showAlertDialog(AddAddressActivity.this, error);
            }
        });
    }
}
