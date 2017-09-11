package com.tesu.manicurehouse.activity;

import android.app.Dialog;
import android.view.View;
import android.view.Window;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.tesu.manicurehouse.R;
import com.tesu.manicurehouse.adapter.MyLeaveMessageAdapter;
import com.tesu.manicurehouse.base.BaseActivity;
import com.tesu.manicurehouse.base.MyVolley;
import com.tesu.manicurehouse.protocol.GetUserInfoProtocol;
import com.tesu.manicurehouse.protocol.GetUserMsgProtocol;
import com.tesu.manicurehouse.request.GetUserInfoRequest;
import com.tesu.manicurehouse.request.GetUserMsgRequest;
import com.tesu.manicurehouse.response.GetUserInfoResponse;
import com.tesu.manicurehouse.response.GetUserMsgResponse;
import com.tesu.manicurehouse.util.DialogUtils;
import com.tesu.manicurehouse.util.LogUtils;
import com.tesu.manicurehouse.util.SharedPrefrenceUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 类描述：
 * 创建人：许明达
 * 创建时间：2016/8/16 11:40
 * 我的留言页面
 */
public class MyLeaveMessageActivity extends BaseActivity implements View.OnClickListener {

    private TextView tv_top_back;
    private View rootView;
    private ExpandableListView lv_leave_message;
    private MyLeaveMessageAdapter myLeaveMessageAdapter;
    private String url;
    private Dialog loadingDialog;
    private GetUserMsgResponse getUserMsgResponse;
    @Override
    protected View initView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        rootView = View.inflate(this, R.layout.activity_my_leave_message, null);
        setContentView(rootView);
        tv_top_back = (TextView) rootView.findViewById(R.id.tv_top_back);
        lv_leave_message=(ExpandableListView)rootView.findViewById(R.id.lv_leave_message);
        tv_top_back.setOnClickListener(this);
        initData();
        return null;
    }


    public void initData() {
        loadingDialog = DialogUtils.createLoadDialog(MyLeaveMessageActivity.this, true);

        runAsyncTask();
    }
    public void setDate(List<GetUserMsgResponse.DataList> dataLists){
        List<List<GetUserMsgResponse.DataList>> lists=new ArrayList<List<GetUserMsgResponse.DataList>>();
        for(int i=0;i<dataLists.size();i++){
            List<GetUserMsgResponse.DataList> temp=new ArrayList<GetUserMsgResponse.DataList>();
            temp.add(dataLists.get(i));
            lists.add(temp);
        }
        if(myLeaveMessageAdapter==null){
            myLeaveMessageAdapter=new MyLeaveMessageAdapter(MyLeaveMessageActivity.this,lists);
        }
        lv_leave_message.setAdapter(myLeaveMessageAdapter);
        lv_leave_message.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                int count = lv_leave_message.getExpandableListAdapter().getGroupCount();
                for (int j = 0; j < count; j++) {
                    if (j != groupPosition) {
                        lv_leave_message.collapseGroup(j);
                    }
                }
            }
        });
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_top_back:
                finish();
                overridePendingTransition(R.anim.animprv_in, R.anim.animprv_out);
                break;
        }
    }
    public void runAsyncTask() {
        loadingDialog.show();
        GetUserMsgProtocol getUserMsgProtocol = new GetUserMsgProtocol();
        GetUserMsgRequest getUserMsgRequest = new GetUserMsgRequest();
        url = getUserMsgProtocol.getApiFun();
        getUserMsgRequest.map.put("user_id", SharedPrefrenceUtils.getString(MyLeaveMessageActivity.this, "user_id"));
        getUserMsgRequest.map.put("pageNo","1");
        getUserMsgRequest.map.put("pageSize", "10000");
        MyVolley.uploadNoFile(MyVolley.POST, url, getUserMsgRequest.map, new MyVolley.VolleyCallback() {
            @Override
            public void dealWithJson(String address, String json) {

                Gson gson = new Gson();
                getUserMsgResponse = gson.fromJson(json, GetUserMsgResponse.class);
                LogUtils.e("sendVerificationCodeResponse:" + json.toString());
                if (getUserMsgResponse.code.equals("0")) {
                    loadingDialog.dismiss();
                    setDate(getUserMsgResponse.dataList);
                } else {
                    loadingDialog.dismiss();
                    DialogUtils.showAlertDialog(MyLeaveMessageActivity.this,
                            getUserMsgResponse.resultText);
                }


            }

            @Override
            public void dealWithError(String address, String error) {
                loadingDialog.dismiss();
                DialogUtils.showAlertDialog(MyLeaveMessageActivity.this, error);
            }
        });
    }
}
