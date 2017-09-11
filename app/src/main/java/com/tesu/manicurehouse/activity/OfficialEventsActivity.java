package com.tesu.manicurehouse.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.gson.Gson;
import com.tesu.manicurehouse.R;
import com.tesu.manicurehouse.adapter.ManicureInformationAdapter;
import com.tesu.manicurehouse.adapter.OfficialEventsAdapter;
import com.tesu.manicurehouse.adapter.PersonalCommunicationAdapter;
import com.tesu.manicurehouse.base.BaseActivity;
import com.tesu.manicurehouse.base.MyVolley;
import com.tesu.manicurehouse.protocol.GetActListProtocol;
import com.tesu.manicurehouse.protocol.GetPostListProtocol;
import com.tesu.manicurehouse.request.GetActListRequest;
import com.tesu.manicurehouse.request.GetPostListRequest;
import com.tesu.manicurehouse.response.GetActListResponse;
import com.tesu.manicurehouse.response.GetPostListResponse;
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
 * 创建时间：2016/8/30 10:40
 * 官方活动页面
 */
public class OfficialEventsActivity extends BaseActivity implements View.OnClickListener {

    private ImageView iv_top_back;
    private View rootView;
    private ListView lv_official_events;
    private OfficialEventsAdapter officialEventsAdapter;
    private String url;
    private Dialog loadingDialog;
    private GetActListResponse getActListResponse;

    @Override
    protected View initView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        rootView = View.inflate(this, R.layout.activity_official_events, null);
        setContentView(rootView);
        iv_top_back = (ImageView) rootView.findViewById(R.id.iv_top_back);
        lv_official_events=(ListView)rootView.findViewById(R.id.lv_official_events);
        initData();
        return null;
    }


    public void initData() {
        loadingDialog = DialogUtils.createLoadDialog(OfficialEventsActivity.this, true);
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
        GetActListProtocol getActListProtocol = new GetActListProtocol();
        GetActListRequest getActListRequest = new GetActListRequest();
        getActListRequest.map.put("pageNo",String.valueOf(1));
        getActListRequest.map.put("pageSize",String.valueOf(100));
        url = getActListProtocol.getApiFun();

        MyVolley.uploadNoFile(MyVolley.POST, url, getActListRequest.map, new MyVolley.VolleyCallback() {
            @Override
            public void dealWithJson(String address, String json) {
                LogUtils.e("json:" + json);
                Gson gson = new Gson();
                getActListResponse = gson.fromJson(json, GetActListResponse.class);
                if (getActListResponse.code.equals("0")) {
                    loadingDialog.dismiss();
                    if (getActListResponse.dataList.size() > 0) {
                        setDate(getActListResponse.dataList);
                    }
                } else {
                    loadingDialog.dismiss();
                    DialogUtils.showAlertDialog(OfficialEventsActivity.this,
                            getActListResponse.resultText);
                }


            }

            @Override
            public void dealWithError(String address, String error) {
                loadingDialog.dismiss();
                DialogUtils.showAlertDialog(OfficialEventsActivity.this, error);
            }
        });
    }

    public void setDate( final List<GetActListResponse.ActBean> dataList){
        if(officialEventsAdapter==null){
            officialEventsAdapter=new OfficialEventsAdapter(OfficialEventsActivity.this,dataList);
        }
        else{
            officialEventsAdapter.notifyDataSetChanged();
        }
        lv_official_events.setAdapter(officialEventsAdapter);
        lv_official_events.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(OfficialEventsActivity.this,OfficialEventsInfoActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("actBean", (Serializable) dataList.get(position));
                intent.putExtras(bundle);
                UIUtils.startActivityNextAnim(intent);
            }
        });
    }
}
