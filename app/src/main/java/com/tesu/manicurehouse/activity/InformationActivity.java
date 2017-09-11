package com.tesu.manicurehouse.activity;

import android.app.Dialog;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.tesu.manicurehouse.R;
import com.tesu.manicurehouse.adapter.MessageAdapter;
import com.tesu.manicurehouse.adapter.MyAddressAdapter;
import com.tesu.manicurehouse.base.BaseActivity;
import com.tesu.manicurehouse.base.MyVolley;
import com.tesu.manicurehouse.bean.MessageBean;
import com.tesu.manicurehouse.fragment.ControlTabFragment;
import com.tesu.manicurehouse.response.BaseResponse;
import com.tesu.manicurehouse.util.DialogUtils;
import com.tesu.manicurehouse.util.LogUtils;
import com.tesu.manicurehouse.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 类描述：
 * 创建人：许明达
 * 创建时间：2016/8/16 11:40
 * 全部订单页面
 */
public class InformationActivity extends BaseActivity implements View.OnClickListener {

    private TextView tv_top_back;
    private View rootView;
    private ListView lv_message;
    private ArrayList<MessageBean> messageBeanArrayList;
    private MessageAdapter messageAdapter;
    private Dialog loadingDialog;
    private String url;
    private BaseResponse baseResponse;
    private Gson gson;
    private MessageBean messageBean;
    private ControlTabFragment ctf;

    @Override
    protected View initView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        rootView = View.inflate(this, R.layout.activity_information, null);
        setContentView(rootView);
        tv_top_back = (TextView) rootView.findViewById(R.id.tv_top_back);
        lv_message = (ListView) rootView.findViewById(R.id.lv_message);
        tv_top_back.setOnClickListener(this);

        loadingDialog = DialogUtils.createLoadDialog(InformationActivity.this, true);
        gson = new Gson();
        if (ctf == null) {
            ctf = MainActivity.getCtf();
        }
        initData();
        return null;
    }

    public void initData() {
        messageBeanArrayList = (ArrayList<MessageBean>) getIntent().getSerializableExtra("messageList");
        if(messageBeanArrayList != null){
            messageAdapter = new MessageAdapter(this,messageBeanArrayList);
            lv_message.setAdapter(messageAdapter);

            lv_message.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    MessageBean messageBean = messageBeanArrayList.get(position);
                    if(messageBean.getIs_read() == 0){
//                        messageBean.setIs_read(1);
//                        messageAdapter.notifyDataSetChanged();
                        setMessageRead(position);
                    }
                    if(messageBean.isShow()){
                        messageBean.setIsShow(false);
                    }else {
                        messageBean.setIsShow(true);
                    }
                    messageBeanArrayList.set(position,messageBean);
                    messageAdapter.notifyDataSetChanged();
                }
            });
        }
    }

    /**
     * 将消息设为已读
     */
    private void setMessageRead(int positon) {
        loadingDialog.show();
        url = "app/setMsgRead.do";
        messageBean = messageBeanArrayList.get(positon);
        Map<String, String> params = new HashMap<String, String>();
        params.put("msg_id", messageBean.getMsg_id()+"");
        MyVolley.uploadNoFile(MyVolley.POST, url, params, new MyVolley.VolleyCallback() {

            @Override
            public void dealWithJson(String address, String json) {
                loadingDialog.dismiss();
                LogUtils.e("修改消息状态:" + json);
                baseResponse = gson.fromJson(json,BaseResponse.class);
                if(baseResponse.getCode()==0){
                    ctf.getTabMyselfbyPager().initData();
                    messageBean.setIs_read(1);
                    messageAdapter.notifyDataSetChanged();
                }else {
                    DialogUtils.showAlertDialog(InformationActivity.this,baseResponse.getResultText());
                }

            }

            @Override
            public void dealWithError(String address, String error) {
                loadingDialog.dismiss();
                LogUtils.e("修改消息状态错误:" + error);
                DialogUtils.showAlertDialog(InformationActivity.this, error);
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
}
