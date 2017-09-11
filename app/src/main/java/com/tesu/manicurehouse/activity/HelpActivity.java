package com.tesu.manicurehouse.activity;

import android.app.Dialog;
import android.content.Intent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.tesu.manicurehouse.R;
import com.tesu.manicurehouse.adapter.ProblemAdapter;
import com.tesu.manicurehouse.base.BaseActivity;
import com.tesu.manicurehouse.base.MyVolley;
import com.tesu.manicurehouse.protocol.AddPostOrInformationForwardCntProtocol;
import com.tesu.manicurehouse.protocol.GetCommonProblemProtocol;
import com.tesu.manicurehouse.request.AddPostOrInformationForwardCntRequest;
import com.tesu.manicurehouse.request.GetCommonProblemRequest;
import com.tesu.manicurehouse.response.AddPostOrInformationForwardCntResponse;
import com.tesu.manicurehouse.response.GetCommonProblemResponse;
import com.tesu.manicurehouse.util.DialogUtils;
import com.tesu.manicurehouse.util.LogUtils;
import com.tesu.manicurehouse.util.UIUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 类描述：
 * 创建人：许明达
 * 创建时间：2016/8/16 11:40
 * 帮助与反馈页面
 */
public class HelpActivity extends BaseActivity implements View.OnClickListener {

    private TextView tv_top_back;
    private View rootView;
    private ListView lv_problem;
    private ProblemAdapter problemAdapter;
    private String url;
    private Dialog loadingDialog;
    private TextView tv_help;
    @Override
    protected View initView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        rootView = View.inflate(this, R.layout.activity_help, null);
        setContentView(rootView);
        tv_top_back = (TextView) rootView.findViewById(R.id.tv_top_back);
        tv_help= (TextView) rootView.findViewById(R.id.tv_help);
        lv_problem=(ListView)rootView.findViewById(R.id.lv_problem);
        initData();
        return null;
    }


    public void initData() {
        loadingDialog = DialogUtils.createLoadDialog(HelpActivity.this, true);
        tv_top_back.setOnClickListener(this);
        tv_help.setOnClickListener(this);
        runGetCommonProblem();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_help:{
                Intent intent=new Intent(HelpActivity.this,OfficialServiceActivity.class);
                UIUtils.startActivityNextAnim(intent);
                break;
            }
            case R.id.tv_top_back: {
                finish();
                overridePendingTransition(R.anim.animprv_in, R.anim.animprv_out);
                break;
            }
        }
    }
    //分享
    public void runGetCommonProblem() {
        loadingDialog.show();
        GetCommonProblemProtocol getCommonProblemProtocol = new GetCommonProblemProtocol();
        GetCommonProblemRequest getCommonProblemRequest = new GetCommonProblemRequest();
        url = getCommonProblemProtocol.getApiFun();
        getCommonProblemRequest.map.put("pageNo", String.valueOf(1));
        getCommonProblemRequest.map.put("pageSize", String.valueOf(100));

        MyVolley.uploadNoFile(MyVolley.POST, url, getCommonProblemRequest.map, new MyVolley.VolleyCallback() {
            @Override
            public void dealWithJson(String address, String json) {
                LogUtils.e("json:" + json);
                Gson gson = new Gson();
                GetCommonProblemResponse getCommonProblemResponse = gson.fromJson(json, GetCommonProblemResponse.class);
                if (getCommonProblemResponse.code.equals("0")) {
                    loadingDialog.dismiss();
                    setDate(getCommonProblemResponse.dataList);
                } else {
                    loadingDialog.dismiss();
                    DialogUtils.showAlertDialog(HelpActivity.this,
                            getCommonProblemResponse.resultText);
                }


            }

            @Override
            public void dealWithError(String address, String error) {
                loadingDialog.dismiss();
                DialogUtils.showAlertDialog(HelpActivity.this, error);
            }
        });
    }

    public void setDate(List<GetCommonProblemResponse.CommonProblemBean> commonProblemBeanList){
        if(problemAdapter==null){
            problemAdapter=new ProblemAdapter(HelpActivity.this,commonProblemBeanList);
        }
        lv_problem.setAdapter(problemAdapter);
        lv_problem.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                problemAdapter.setSelect(position);
            }
        });
    }
}
