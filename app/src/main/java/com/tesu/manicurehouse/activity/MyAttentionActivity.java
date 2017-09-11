package com.tesu.manicurehouse.activity;

import android.app.Dialog;
import android.content.Intent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.tesu.manicurehouse.R;
import com.tesu.manicurehouse.adapter.MyAttestionAdapter;
import com.tesu.manicurehouse.base.BaseActivity;
import com.tesu.manicurehouse.base.MyVolley;
import com.tesu.manicurehouse.bean.MessageItem;
import com.tesu.manicurehouse.protocol.GetUserFollowListProtocol;
import com.tesu.manicurehouse.protocol.GetUserMsgProtocol;
import com.tesu.manicurehouse.request.GetUserFoollowListRequest;
import com.tesu.manicurehouse.request.GetUserMsgRequest;
import com.tesu.manicurehouse.response.GetUserFollowListResponse;
import com.tesu.manicurehouse.response.GetUserMsgResponse;
import com.tesu.manicurehouse.util.DialogUtils;
import com.tesu.manicurehouse.util.LogUtils;
import com.tesu.manicurehouse.util.SharedPrefrenceUtils;
import com.tesu.manicurehouse.util.UIUtils;
import com.tesu.manicurehouse.widget.ListViewCompat;
import com.tesu.manicurehouse.widget.SlideView;

import java.util.ArrayList;
import java.util.List;

/**
 * 类描述：
 * 创建人：许明达
 * 创建时间：2016/8/28 11:40
 * 我的关注
 */
public class MyAttentionActivity extends BaseActivity implements View.OnClickListener {

    private TextView tv_top_back;
    private View rootView;
    private ListViewCompat mListView;

    private List<MessageItem> mMessageItems = new ArrayList<MessageItem>();

    private SlideView mLastSlideViewWithStatusOn;
    private String url;
    private Dialog loadingDialog;
    private GetUserFollowListResponse getUserFollowListResponse;
    private MyAttestionAdapter myAttestionAdapter;

    @Override
    protected View initView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        rootView = View.inflate(this, R.layout.activity_my_attention, null);
        setContentView(rootView);
        tv_top_back = (TextView) rootView.findViewById(R.id.tv_top_back);
        mListView = (ListViewCompat) findViewById(R.id.lv_attention);
        tv_top_back.setOnClickListener(this);
        initData();
        return null;
    }


    public void initData() {
        loadingDialog = DialogUtils.createLoadDialog(MyAttentionActivity.this, true);

        runAsyncTask();

    }

    public void setDate(List<GetUserFollowListResponse.DataList> lists) {
        for (int i = 0; i < lists.size(); i++) {
            MessageItem item = new MessageItem();
            item.alias = lists.get(i).alias;
            item.avatar = lists.get(i).avatar;
            item.user_id = lists.get(i).user_id;
            mMessageItems.add(item);
        }
        if(myAttestionAdapter==null){
            myAttestionAdapter=new MyAttestionAdapter(MyAttentionActivity.this, mMessageItems, mLastSlideViewWithStatusOn,loadingDialog);
            mListView.setAdapter(myAttestionAdapter);
        }
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MyAttentionActivity.this, AttentionActivity.class);
                intent.putExtra("follow_userid",getUserFollowListResponse.dataList.get(position).user_id);
                UIUtils.startActivityNextAnim(intent);
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
        GetUserFollowListProtocol getUserFollowListProtocol = new GetUserFollowListProtocol();
        GetUserFoollowListRequest getUserFoollowListRequest = new GetUserFoollowListRequest();
        url = getUserFollowListProtocol.getApiFun();
        getUserFoollowListRequest.map.put("user_id", SharedPrefrenceUtils.getString(MyAttentionActivity.this, "user_id"));


        MyVolley.uploadNoFile(MyVolley.POST, url, getUserFoollowListRequest.map, new MyVolley.VolleyCallback() {
            @Override
            public void dealWithJson(String address, String json) {

                Gson gson = new Gson();
                getUserFollowListResponse = gson.fromJson(json, GetUserFollowListResponse.class);
                LogUtils.e("sendVerificationCodeResponse:" + json.toString());
                if (getUserFollowListResponse.code.equals("0")) {
                    loadingDialog.dismiss();
                    setDate(getUserFollowListResponse.dataList);
                } else {
                    loadingDialog.dismiss();
                    DialogUtils.showAlertDialog(MyAttentionActivity.this,
                            getUserFollowListResponse.resultText);
                }


            }

            @Override
            public void dealWithError(String address, String error) {
                loadingDialog.dismiss();
                DialogUtils.showAlertDialog(MyAttentionActivity.this, error);
            }
        });
    }
}
