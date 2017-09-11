package com.tesu.manicurehouse.activity;

import android.app.Dialog;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.tesu.manicurehouse.R;
import com.tesu.manicurehouse.base.BaseActivity;
import com.tesu.manicurehouse.base.MyVolley;
import com.tesu.manicurehouse.huanxin.ChatActivity;
import com.tesu.manicurehouse.huanxin.HuanXinLoginActivity;
import com.tesu.manicurehouse.huanxin.HuanXinMainActivity;
import com.tesu.manicurehouse.protocol.GetActListProtocol;
import com.tesu.manicurehouse.protocol.GetInformationDescProtocol;
import com.tesu.manicurehouse.protocol.ProblemFeedbackProtocol;
import com.tesu.manicurehouse.request.GetActListRequest;
import com.tesu.manicurehouse.request.GetInformationDescRequest;
import com.tesu.manicurehouse.request.ProblemFeedbackRequest;
import com.tesu.manicurehouse.response.GetActListResponse;
import com.tesu.manicurehouse.response.GetInformationDescResponse;
import com.tesu.manicurehouse.response.ProblemFeedbackResponse;
import com.tesu.manicurehouse.support.PercentLinearLayout;
import com.tesu.manicurehouse.util.DialogUtils;
import com.tesu.manicurehouse.util.LogUtils;
import com.tesu.manicurehouse.util.SharedPrefrenceUtils;
import com.tesu.manicurehouse.util.UIUtils;

/**
 * 类描述：
 * 创建人：许明达
 * 创建时间：2016/8/16 11:40
 * 官方客服页面
 */
public class OfficialServiceActivity extends BaseActivity implements View.OnClickListener {

    private EditText et_title;
    private TextView tv_top_back;
    private View rootView;
    private String url;
    private Dialog loadingDialog;
    private EditText et_input;
    private Button btn_comit;
    private RadioGroup rg_content;
    //反馈的类型，0，留言；1，投诉；2，询问；3，售后；4，求购
    private int msg_type;
    private String msg_content;
    private PercentLinearLayout ll_online_customer;
    private String title;
    private TextView tv1;
    @Override
    protected View initView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        rootView = View.inflate(this, R.layout.activity_official_service, null);
        setContentView(rootView);
        rg_content=(RadioGroup)rootView.findViewById(R.id.rg_content);
        tv1 = (TextView) rootView.findViewById(R.id.tv1);
        tv_top_back = (TextView) rootView.findViewById(R.id.tv_top_back);
        et_input=(EditText)rootView.findViewById(R.id.et_input);
        btn_comit=(Button)rootView.findViewById(R.id.btn_comit);
        ll_online_customer= (PercentLinearLayout) rootView.findViewById(R.id.ll_online_customer);
        et_title= (EditText) rootView.findViewById(R.id.et_title);

        tv_top_back.setOnClickListener(this);
        ll_online_customer.setOnClickListener(this);
        initData();
        return null;
    }


    public void initData() {
        loadingDialog = DialogUtils.createLoadDialog(OfficialServiceActivity.this, true);
        rg_content.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_one:
                        msg_type = 0;
                        break;
                    case R.id.rb_two:
                        msg_type = 1;
                        break;
                    case R.id.rb_three:
                        msg_type = 2;
                        break;
                    case R.id.rb_four:
                        msg_type = 3;
                        break;
                    case R.id.rb_five:
                        msg_type = 4;
                        break;
                    default:
                        break;
                }
            }
        });
        tv1.setOnClickListener(this);
        btn_comit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv1:{
                Intent intent = new Intent(OfficialServiceActivity.this, MyLeaveMessageActivity.class);
                UIUtils.startActivityNextAnim(intent);
                break;
            }
            case R.id.btn_comit:
                msg_content=et_input.getText().toString();
                title = et_title.getText().toString();
                if(TextUtils.isEmpty(title)){
                    DialogUtils.showAlertDialog(OfficialServiceActivity.this,
                            "请输入反馈标题!");
                    return;
                }
                if(TextUtils.isEmpty(msg_content)){
                    DialogUtils.showAlertDialog(OfficialServiceActivity.this,
                            "请输入反馈内容!");
                    return;
                }
                runProblemFeedback();
                break;
            case R.id.tv_top_back:
                finish();
                overridePendingTransition(R.anim.animprv_in, R.anim.animprv_out);
                break;
            case R.id.ll_online_customer:
                Intent intent = new Intent(OfficialServiceActivity.this, HuanXinLoginActivity.class);
                startActivity(intent);
                break;
        }
    }
    public void runProblemFeedback() {
        loadingDialog.show();
        ProblemFeedbackProtocol problemFeedbackProtocol = new ProblemFeedbackProtocol();
        ProblemFeedbackRequest problemFeedbackRequest = new ProblemFeedbackRequest();
        url = problemFeedbackProtocol.getApiFun();
        problemFeedbackRequest.map.put("user_id", SharedPrefrenceUtils.getString(OfficialServiceActivity.this, "user_id"));
        problemFeedbackRequest.map.put("msg_type", String.valueOf(msg_type));
        problemFeedbackRequest.map.put("msg_title",title);
        problemFeedbackRequest.map.put("msg_content",msg_content);
        MyVolley.uploadNoFile(MyVolley.POST, url, problemFeedbackRequest.map, new MyVolley.VolleyCallback() {
            @Override
            public void dealWithJson(String address, String json) {
                LogUtils.e("json:" + json);
                Gson gson = new Gson();
                ProblemFeedbackResponse problemFeedbackResponse = gson.fromJson(json, ProblemFeedbackResponse.class);
                if (problemFeedbackResponse.code==0) {
                    loadingDialog.dismiss();
                    DialogUtils.showAlertDialog(OfficialServiceActivity.this,
                            "提交成功!");
                    et_input.setText("");
                    et_title.setText("");
                } else {
                    loadingDialog.dismiss();
                    DialogUtils.showAlertDialog(OfficialServiceActivity.this,
                            problemFeedbackResponse.resultText);
                }


            }

            @Override
            public void dealWithError(String address, String error) {
                loadingDialog.dismiss();
                DialogUtils.showAlertDialog(OfficialServiceActivity.this, error);
            }
        });
    }
}
