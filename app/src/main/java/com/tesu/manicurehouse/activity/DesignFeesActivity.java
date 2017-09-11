package com.tesu.manicurehouse.activity;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.os.Build;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.tesu.manicurehouse.R;
import com.tesu.manicurehouse.adapter.ShowAdapter;
import com.tesu.manicurehouse.adapter.TutorialAdapter;
import com.tesu.manicurehouse.base.BaseActivity;
import com.tesu.manicurehouse.base.MyVolley;
import com.tesu.manicurehouse.protocol.GetDesignListProtocol;
import com.tesu.manicurehouse.protocol.WithdrawalsProtocol;
import com.tesu.manicurehouse.request.GetDesignListRequest;
import com.tesu.manicurehouse.request.WithdrawalsRequest;
import com.tesu.manicurehouse.response.GetDesignListResponse;
import com.tesu.manicurehouse.response.VideoListResponse;
import com.tesu.manicurehouse.response.WithdrawalsResponse;
import com.tesu.manicurehouse.util.DialogUtils;
import com.tesu.manicurehouse.util.LogUtils;
import com.tesu.manicurehouse.util.SharedPrefrenceUtils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 类描述：
 * 创建人：许明达
 * 创建时间：2016/8/29 10:40
 * 设计分成页面
 */
public class DesignFeesActivity extends BaseActivity implements View.OnClickListener {

    private TextView tv_top_back;
    private View rootView;
    private ListView lv_tutorial;
    private TutorialAdapter tutorialAdapter;
    private ScrollView sv;
    private Dialog loadingDialog;
    private String url;
    private GetDesignListResponse getDesignListResponse;
    //是否已经付款0否，1是
    private int pay_status = 1;
    private int pageNo = 1;
    private RelativeLayout rl_wait;
    private RelativeLayout rl_finish;
    public List<GetDesignListResponse.DesignBean> designBeanList;
    private TextView tv_finish_pay;
    private TextView tv_wait_pay;
    private TextView tv_free;
    private TextView tv_unPayMoney;
    private TextView tv_payMoney;
    private TextView tv_withdrawal;
    private Dialog mDialog;
    private String messageContent;  //提现账号
    private String designFee="0";

    @Override
    protected View initView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        rootView = View.inflate(this, R.layout.activity_design_free, null);
        setContentView(rootView);
        rl_finish = (RelativeLayout) rootView.findViewById(R.id.rl_finish);
        rl_wait = (RelativeLayout) rootView.findViewById(R.id.rl_wait);
        lv_tutorial = (ListView) rootView.findViewById(R.id.lv_tutorial);
        tv_top_back = (TextView) rootView.findViewById(R.id.tv_top_back);
        tv_finish_pay = (TextView) rootView.findViewById(R.id.tv_finish_pay);
        tv_wait_pay = (TextView) rootView.findViewById(R.id.tv_wait_pay);
        tv_free = (TextView) rootView.findViewById(R.id.tv_free);
        tv_unPayMoney = (TextView) rootView.findViewById(R.id.tv_unPayMoney);
        tv_payMoney = (TextView) rootView.findViewById(R.id.tv_payMoney);
        tv_withdrawal = (TextView) rootView.findViewById(R.id.tv_withdrawal);
        sv = (ScrollView) rootView.findViewById(R.id.sv);
        tv_top_back.setOnClickListener(this);
        initData();
        return null;
    }


    public void initData() {
        designBeanList = new ArrayList<>();
        loadingDialog = DialogUtils.createLoadDialog(DesignFeesActivity.this, true);
        getDesginList();
        //设置listview的滑动优先级
        lv_tutorial.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    sv.requestDisallowInterceptTouchEvent(false);
                } else {
                    sv.requestDisallowInterceptTouchEvent(true);
                }
                return false;
            }
        });
        tv_withdrawal.setOnClickListener(this);
        rl_wait.setOnClickListener(this);
        rl_finish.setOnClickListener(this);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_withdrawal: {
                if(designFee.equals("0")){
                    DialogUtils.showAlertDialog(DesignFeesActivity.this, "没有可提现的金额！");
                }
                else{
                    mDialog = DialogUtils.showInputAlertDialog(DesignFeesActivity.this, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            EditText alert_dialog_content = (EditText) mDialog.findViewById(R.id.alert_dialog_content);
                            messageContent = alert_dialog_content.getText().toString();
                            LogUtils.e("messageContent:"+messageContent);
                            if(TextUtils.isEmpty(messageContent)){
                                Toast.makeText(DesignFeesActivity.this, "请输入支付宝账号", Toast.LENGTH_LONG).show();
                            }else {
                                mDialog.dismiss();
                                runWithdrawals();
                            }

                        }
                    },"支付宝账号");
                }


                break;
            }
            case R.id.rl_wait: {
                designBeanList.clear();
                rl_wait.setBackground(getResources().getDrawable(R.mipmap.bt_video_selectable));
                tv_wait_pay.setTextColor(getResources().getColor(R.color.tab_background));
                rl_finish.setBackground(getResources().getDrawable(R.mipmap.bt_video_noselectable));
                tv_finish_pay.setTextColor(getResources().getColor(R.color.text_color_black));
                pay_status = 0;
                getDesginList();
                break;
            }
            case R.id.rl_finish: {
                designBeanList.clear();
                rl_finish.setBackground(getResources().getDrawable(R.mipmap.bt_video_selectable));
                tv_finish_pay.setTextColor(getResources().getColor(R.color.tab_background));
                rl_wait.setBackground(getResources().getDrawable(R.mipmap.bt_video_noselectable));
                tv_wait_pay.setTextColor(getResources().getColor(R.color.text_color_black));
                pay_status = 1;
                getDesginList();
                break;
            }
            case R.id.tv_top_back:
                finish();
                overridePendingTransition(R.anim.animprv_in, R.anim.animprv_out);
                break;
        }
    }

    private void getDesginList() {
        loadingDialog.show();
        GetDesignListProtocol getDesignListProtocol = new GetDesignListProtocol();
        GetDesignListRequest getDesignListRequest = new GetDesignListRequest();
        url = getDesignListProtocol.getApiFun();
        getDesignListRequest.map.put("user_id", SharedPrefrenceUtils.getString(DesignFeesActivity.this, "user_id"));
        getDesignListRequest.map.put("pay_status", String.valueOf(pay_status));
        getDesignListRequest.map.put("pageNo", String.valueOf(pageNo));
        getDesignListRequest.map.put("pageSize", "10");
        MyVolley.uploadNoFile(MyVolley.POST, url, getDesignListRequest.map, new MyVolley.VolleyCallback() {

            @Override
            public void dealWithJson(String address, String json) {
                LogUtils.e("获取视频列表:" + json);
                Gson gson = new Gson();
                loadingDialog.dismiss();
                getDesignListResponse = gson.fromJson(json, GetDesignListResponse.class);
                if (getDesignListResponse.code == 0) {
                        DecimalFormat df = new DecimalFormat("0.00");
                        designFee=getDesignListResponse.designFee;
                        tv_free.setText("￥" + df.format(Double.parseDouble(getDesignListResponse.designFee)));
                        tv_payMoney.setText("￥" + df.format(Double.parseDouble(getDesignListResponse.payMoney)));
                        tv_unPayMoney.setText("￥" + df.format(Double.parseDouble(getDesignListResponse.unPayMoney)));
                    if (getDesignListResponse.dataList.size() > 0) {
                        designBeanList.addAll(getDesignListResponse.dataList);
                    }
                    if (tutorialAdapter == null) {
                        tutorialAdapter = new TutorialAdapter(DesignFeesActivity.this, designBeanList);
                    } else {
                        tutorialAdapter.notifyDataSetChanged();
                    }
                    lv_tutorial.setAdapter(tutorialAdapter);

                } else {
                    DialogUtils.showAlertDialog(DesignFeesActivity.this, getDesignListResponse.resultText);
                }

            }

            @Override
            public void dealWithError(String address, String error) {
                LogUtils.e("获取视频列表错误:" + error);
                loadingDialog.dismiss();
                DialogUtils.showAlertDialog(DesignFeesActivity.this, error);

            }
        });
    }

    //提现
    private void runWithdrawals() {
        loadingDialog.show();
        WithdrawalsProtocol withdrawalsProtocol = new WithdrawalsProtocol();
        WithdrawalsRequest withdrawalsRequest = new WithdrawalsRequest();
        url = withdrawalsProtocol.getApiFun();
        withdrawalsRequest.map.put("user_id", SharedPrefrenceUtils.getString(DesignFeesActivity.this, "user_id"));
        withdrawalsRequest.map.put("alipay_account", messageContent);
        MyVolley.uploadNoFile(MyVolley.POST, url, withdrawalsRequest.map, new MyVolley.VolleyCallback() {

            @Override
            public void dealWithJson(String address, String json) {
                Gson gson = new Gson();
                loadingDialog.dismiss();
                WithdrawalsResponse withdrawalsResponse = gson.fromJson(json, WithdrawalsResponse.class);
                if (withdrawalsResponse.code == 0) {
                    DecimalFormat df = new DecimalFormat("0.00");
                    DialogUtils.showAlertDialog(DesignFeesActivity.this, "提现成功！");
                    tv_free.setText("￥" + df.format(Double.parseDouble(withdrawalsResponse.designFee)));
                    tv_payMoney.setText("￥" + df.format(Double.parseDouble(withdrawalsResponse.payMoney)));
                    tv_unPayMoney.setText("￥" + df.format(Double.parseDouble(withdrawalsResponse.unPayMoney)));
                    designFee="0";
                } else {
                    DialogUtils.showAlertDialog(DesignFeesActivity.this, withdrawalsResponse.resultText);
                }

            }

            @Override
            public void dealWithError(String address, String error) {
                loadingDialog.dismiss();
                DialogUtils.showAlertDialog(DesignFeesActivity.this, error);
            }
        });
    }
}
