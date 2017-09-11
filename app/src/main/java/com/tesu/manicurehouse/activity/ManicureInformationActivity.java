package com.tesu.manicurehouse.activity;

import android.app.Dialog;
import android.content.Intent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.tesu.manicurehouse.R;
import com.tesu.manicurehouse.adapter.ManicureInformationAdapter;
import com.tesu.manicurehouse.base.BaseActivity;
import com.tesu.manicurehouse.base.MyVolley;
import com.tesu.manicurehouse.protocol.GetAdsProtocol;
import com.tesu.manicurehouse.protocol.GetInformationListProtocol;
import com.tesu.manicurehouse.request.GetAdsRequest;
import com.tesu.manicurehouse.request.GetInformationListRequest;
import com.tesu.manicurehouse.response.GetAdsResponse;
import com.tesu.manicurehouse.response.GetInformationListResponse;
import com.tesu.manicurehouse.util.DialogUtils;
import com.tesu.manicurehouse.util.LogUtils;
import com.tesu.manicurehouse.util.SharedPrefrenceUtils;
import com.tesu.manicurehouse.util.UIUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 类描述：
 * 创建人：许明达
 * 创建时间：2016/8/30 10:40
 * 美甲资讯页面
 */
public class ManicureInformationActivity extends BaseActivity implements View.OnClickListener {

    private ImageView iv_top_back;
    private View rootView;
    private ListView lv_manicuer_information;
    private ManicureInformationAdapter manicureInformationAdapter;
    private String url;
    private Dialog loadingDialog;
    private GetInformationListResponse getInformationListResponse;
    @Override
    protected View initView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        rootView = View.inflate(this, R.layout.activity_manicuer_information, null);
        setContentView(rootView);
        iv_top_back = (ImageView) rootView.findViewById(R.id.iv_top_back);
        lv_manicuer_information=(ListView)rootView.findViewById(R.id.lv_manicuer_information);
        initData();
        return null;
    }


    public void initData() {
        if (!ImageLoader.getInstance().isInited()) {
            ImageLoader.getInstance()
                    .init(ImageLoaderConfiguration.createDefault(UIUtils
                            .getContext()));
        }
        loadingDialog = DialogUtils.createLoadDialog(ManicureInformationActivity.this, true);
        runAsyncTask();
        iv_top_back.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_top_back: {
                finish();
                overridePendingTransition(R.anim.animprv_in, R.anim.animprv_out);
                break;
            }
        }
    }

    public void runAsyncTask() {
        loadingDialog.show();
        GetInformationListProtocol getInformationListProtocol = new GetInformationListProtocol();
        GetInformationListRequest getInformationListRequest = new GetInformationListRequest();
        url = getInformationListProtocol.getApiFun();
        getInformationListRequest.map.put("user_id", SharedPrefrenceUtils.getString(ManicureInformationActivity.this,"user_id"));
        getInformationListRequest.map.put("pageNo", "1");
        getInformationListRequest.map.put("pageSize", "100");

        MyVolley.uploadNoFile(MyVolley.POST, url, getInformationListRequest.map, new MyVolley.VolleyCallback() {
            @Override
            public void dealWithJson(String address, String json) {
                LogUtils.e("json:"+json);
                Gson gson = new Gson();
                getInformationListResponse = gson.fromJson(json, GetInformationListResponse.class);
                if (getInformationListResponse.code==0) {
                    loadingDialog.dismiss();
                    if (getInformationListResponse.dataList.size() > 0) {
                        setDate(getInformationListResponse.dataList);
                    }
                } else {
                    loadingDialog.dismiss();
                    DialogUtils.showAlertDialog(ManicureInformationActivity.this,
                            getInformationListResponse.resultText);
                }


            }

            @Override
            public void dealWithError(String address, String error) {
                loadingDialog.dismiss();
                DialogUtils.showAlertDialog(ManicureInformationActivity.this, error);
            }
        });
    }

    public void setDate( List<GetInformationListResponse.InformationBean> dataList){
        if(manicureInformationAdapter==null){
            manicureInformationAdapter=new ManicureInformationAdapter(ManicureInformationActivity.this,dataList);
        }
        else{
            manicureInformationAdapter.setDataList(dataList);
        }
        lv_manicuer_information.setAdapter(manicureInformationAdapter);
        lv_manicuer_information.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ManicureInformationActivity.this, ManicureInformationDetailsActivity.class);
                intent.putExtra("information_id",getInformationListResponse.dataList.get(position).information_id);
                intent.putExtra("title",getInformationListResponse.dataList.get(position).title);
                UIUtils.startActivityForResultNextAnim(intent,1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==1){
            if(resultCode==1){
                runAsyncTask();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
