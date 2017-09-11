package com.tesu.manicurehouse.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.google.gson.Gson;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tesu.manicurehouse.R;
import com.tesu.manicurehouse.base.BaseActivity;
import com.tesu.manicurehouse.base.MyVolley;
import com.tesu.manicurehouse.protocol.AlipayProtocol;
import com.tesu.manicurehouse.protocol.BalancePayProtocol;
import com.tesu.manicurehouse.protocol.GetUserInfoProtocol;
import com.tesu.manicurehouse.protocol.GetUserInfomationProtocol;
import com.tesu.manicurehouse.protocol.RechargeProtocol;
import com.tesu.manicurehouse.protocol.WeixinPayProtocol;
import com.tesu.manicurehouse.request.AlipayRequest;
import com.tesu.manicurehouse.request.BalancePayRequest;
import com.tesu.manicurehouse.request.GetUserInfoRequest;
import com.tesu.manicurehouse.request.GetUserInfomationRequest;
import com.tesu.manicurehouse.request.RechargeRequest;
import com.tesu.manicurehouse.request.WeixinPayRequest;
import com.tesu.manicurehouse.response.AlipayResponse;
import com.tesu.manicurehouse.response.BalancePayResponse;
import com.tesu.manicurehouse.response.GetUserInfoResponse;
import com.tesu.manicurehouse.response.GetUserInfomationResponse;
import com.tesu.manicurehouse.response.RechargeResponse;
import com.tesu.manicurehouse.response.WeixinPayResponse;
import com.tesu.manicurehouse.util.Constants;
import com.tesu.manicurehouse.util.DialogUtils;
import com.tesu.manicurehouse.util.LogUtils;
import com.tesu.manicurehouse.util.PayResult;
import com.tesu.manicurehouse.util.SharedPrefrenceUtils;
import com.tesu.manicurehouse.util.StartCustomTextView;
import com.tesu.manicurehouse.util.UIUtils;

/**
 * 类描述：
 * 创建人：许明达
 * 创建时间：2016/8/16 11:40
 * 充值页面
 */
public class RechargeActivity extends BaseActivity implements View.OnClickListener {
    private String url;
    private Dialog loadingDialog;
    private TextView tv_top_back;
    private View rootView;
    private RelativeLayout rl_weixin_pay;
    private RelativeLayout rl_zhifubao_pay;
    private EditText et_recharge;
    //充值金额
    private double recharge;
    private String out_trade_no;
    private static final int SDK_PAY_FLAG = 1;
    private IWXAPI api;
    //0代表微信充值，1代表支付宝
    private int type = -1;
    private TextView tv_balance_money;
    private TextView tv_integral_money;
    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    PayResult payResult = new PayResult((String) msg.obj);
                    /**
                     * 同步返回的结果必须放置到服务端进行验证（验证的规则请看https://doc.open.alipay.com/doc2/
                     * detail.htm?spm=0.0.0.0.xdvAU6&treeId=59&articleId=103665&
                     * docType=1) 建议商户依赖异步通知
                     */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息

                    String resultStatus = payResult.getResultStatus();
                    LogUtils.e("resultStatus:" + resultStatus);
                    // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                    if (TextUtils.equals(resultStatus, "9000")) {
                        Toast.makeText(RechargeActivity.this, "充值成功", Toast.LENGTH_SHORT).show();
                        runAsyncTask();
                    } else {
                        // 判断resultStatus 为非"9000"则代表可能支付失败
                        // "8000"代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                        if (TextUtils.equals(resultStatus, "8000")) {
                            Toast.makeText(RechargeActivity.this, "支付结果确认中", Toast.LENGTH_SHORT).show();

                        } else if (TextUtils.equals(resultStatus, "4000")) {
                            // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                            Toast.makeText(RechargeActivity.this, "请先安装支付宝客户端", Toast.LENGTH_SHORT).show();
                        } else {
                            // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                            Toast.makeText(RechargeActivity.this, "支付失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
                }
                default:
                    break;
            }
        }

        ;
    };

    @Override
    protected View initView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        rootView = View.inflate(this, R.layout.activity_recharge, null);
        setContentView(rootView);
        tv_top_back = (TextView) rootView.findViewById(R.id.tv_top_back);
        rl_weixin_pay = (RelativeLayout) rootView.findViewById(R.id.rl_weixin_pay);
        rl_zhifubao_pay = (RelativeLayout) rootView.findViewById(R.id.rl_zhifubao_pay);
        et_recharge = (EditText) rootView.findViewById(R.id.et_recharge);
        tv_balance_money = (TextView) rootView.findViewById(R.id.tv_balance_money);
        tv_integral_money = (TextView) rootView.findViewById(R.id.tv_integral_money);
        initData();
        return null;
    }


    public void initData() {
        api = WXAPIFactory.createWXAPI(this, Constants.APP_ID);
        loadingDialog = DialogUtils.createLoadDialog(RechargeActivity.this, true);
        rl_weixin_pay.setOnClickListener(this);
        rl_zhifubao_pay.setOnClickListener(this);
        tv_top_back.setOnClickListener(this);
        runAsyncTask();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_weixin_pay: {

                if (!TextUtils.isEmpty(et_recharge.getText().toString().trim())) {
                    type = 0;
                    recharge = Double.parseDouble(et_recharge.getText().toString());
                    runRecharge();
                } else {
                    DialogUtils.showAlertDialog(RechargeActivity.this,
                            "请输入要充值的金额！");
                }
                break;
            }
            case R.id.rl_zhifubao_pay: {

                if (!TextUtils.isEmpty(et_recharge.getText().toString().trim())) {
                    type = 1;
                    recharge = Double.parseDouble(et_recharge.getText().toString());
                    runRecharge();
                } else {
                    DialogUtils.showAlertDialog(RechargeActivity.this,
                            "请输入要充值的金额！");
                }
                break;
            }
            case R.id.tv_top_back:
                finish();
                overridePendingTransition(R.anim.animprv_in, R.anim.animprv_out);
                break;
        }
    }

    //充值
    public void runRecharge() {
        loadingDialog.show();
        RechargeProtocol rechargeProtocol = new RechargeProtocol();
        RechargeRequest rechargeRequest = new RechargeRequest();
        url = rechargeProtocol.getApiFun();
        rechargeRequest.map.put("user_id", SharedPrefrenceUtils.getString(RechargeActivity.this, "user_id"));
        rechargeRequest.map.put("total_fee", String.valueOf(recharge));

        MyVolley.uploadNoFile(MyVolley.POST, url, rechargeRequest.map, new MyVolley.VolleyCallback() {
            @Override
            public void dealWithJson(String address, String json) {

                Gson gson = new Gson();
                RechargeResponse rechargeResponse = gson.fromJson(json, RechargeResponse.class);
                LogUtils.e("sendVerificationCodeResponse:" + json.toString());
                if (rechargeResponse.code == 0) {
                    out_trade_no = rechargeResponse.out_trade_no;
                    if (type == 1) {
                        //支付宝支付
                        runAlipay();
                    } else if (type == 0) {
                        //微信支付
                        SharedPrefrenceUtils.setBoolean(RechargeActivity.this, "recharge", true);
                        runWeixinpay();
                    }
                } else {
                    loadingDialog.dismiss();
                    DialogUtils.showAlertDialog(RechargeActivity.this,
                            rechargeResponse.resultText);
                }
            }

            @Override
            public void dealWithError(String address, String error) {
                loadingDialog.dismiss();
                DialogUtils.showAlertDialog(RechargeActivity.this, error);
            }
        });
    }

    //支付宝支付
    public void runAlipay() {
        AlipayProtocol alipayProtocol = new AlipayProtocol();
        AlipayRequest alipayRequest = new AlipayRequest();
        url = alipayProtocol.getApiFun();
        alipayRequest.map.put("user_id", SharedPrefrenceUtils.getString(RechargeActivity.this, "user_id"));
        alipayRequest.map.put("out_trade_no", out_trade_no);
        alipayRequest.map.put("is_use_integral", "0");
        alipayRequest.map.put("is_use_balance", "0");
        MyVolley.uploadNoFile(MyVolley.POST, url, alipayRequest.map, new MyVolley.VolleyCallback() {
            @Override
            public void dealWithJson(String address, String json) {

                Gson gson = new Gson();
                AlipayResponse alipayResponse = gson.fromJson(json, AlipayResponse.class);
                LogUtils.e("sendVerificationCodeResponse:" + json.toString());
                if (alipayResponse.code == 0) {
                    loadingDialog.dismiss();
                    alipay(alipayResponse.data);
                } else {
                    loadingDialog.dismiss();
                    DialogUtils.showAlertDialog(RechargeActivity.this,
                            alipayResponse.resultText);
                }
            }

            @Override
            public void dealWithError(String address, String error) {
                loadingDialog.dismiss();
                DialogUtils.showAlertDialog(RechargeActivity.this, error);
            }
        });
    }

    //微信支付
    public void runWeixinpay() {
        loadingDialog.show();
        WeixinPayProtocol weixinPayProtocol = new WeixinPayProtocol();
        WeixinPayRequest weixinPayRequest = new WeixinPayRequest();
        url = weixinPayProtocol.getApiFun();
        weixinPayRequest.map.put("user_id", SharedPrefrenceUtils.getString(RechargeActivity.this, "user_id"));
        weixinPayRequest.map.put("out_trade_no", out_trade_no);
        //       是否使用积分 0否，1是
        weixinPayRequest.map.put("is_use_integral", String.valueOf(0));
        weixinPayRequest.map.put("is_use_balance", String.valueOf(0));
        MyVolley.uploadNoFile(MyVolley.POST, url, weixinPayRequest.map, new MyVolley.VolleyCallback() {
            @Override
            public void dealWithJson(String address, String json) {

                Gson gson = new Gson();
                WeixinPayResponse weixinPayResponse = gson.fromJson(json, WeixinPayResponse.class);
                LogUtils.e("sendVerificationCodeResponse:" + json.toString());
                if (weixinPayResponse.code == 0) {
                    loadingDialog.dismiss();
                    PayReq req = new PayReq();
                    req.appId = weixinPayResponse.appid;
                    req.partnerId = weixinPayResponse.partnerid;
                    req.prepayId = weixinPayResponse.prepayid;
                    req.nonceStr = weixinPayResponse.noncestr;
                    req.timeStamp = weixinPayResponse.timestamp;
                    req.packageValue = "Sign=WXPay";
                    req.sign = weixinPayResponse.sign;
                    req.extData = "app data"; // optional
                    api.registerApp(Constants.APP_ID);
                    api.sendReq(req);
                    finish();
                    overridePendingTransition(R.anim.animprv_in, R.anim.animprv_out);
                } else {
                    loadingDialog.dismiss();
                    DialogUtils.showAlertDialog(RechargeActivity.this,
                            weixinPayResponse.resultText);
                }
            }

            @Override
            public void dealWithError(String address, String error) {
                loadingDialog.dismiss();
                DialogUtils.showAlertDialog(RechargeActivity.this, error);
            }
        });
    }

    public void alipay(final String payInfo) {
        /**
         * 完整的符合支付宝参数规范的订单信息
         */

        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                // 构造PayTask 对象
                PayTask alipay = new PayTask(RechargeActivity.this);
                // 调用支付接口，获取支付结果
                String result = alipay.pay(payInfo, true);

                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };

        // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

    //获取会员信息
    public void runAsyncTask() {
        loadingDialog.show();
        GetUserInfomationProtocol getUserInfoProtocol = new GetUserInfomationProtocol();
        GetUserInfomationRequest getUserInfoRequest = new GetUserInfomationRequest();
        url = getUserInfoProtocol.getApiFun();
        getUserInfoRequest.map.put("user_id", SharedPrefrenceUtils.getString(RechargeActivity.this, "user_id"));


        MyVolley.uploadNoFile(MyVolley.POST, url, getUserInfoRequest.map, new MyVolley.VolleyCallback() {
            @Override
            public void dealWithJson(String address, String json) {

                Gson gson = new Gson();
                GetUserInfomationResponse getUserInfoResponse = gson.fromJson(json, GetUserInfomationResponse.class);
                LogUtils.e("sendVerificationCodeResponse:" + json.toString());
                if (getUserInfoResponse.code.equals("0")) {
                    loadingDialog.dismiss();
                    tv_balance_money.setText("￥" + getUserInfoResponse.user_money);
                    tv_integral_money.setText(""+getUserInfoResponse.pay_points);
                    et_recharge.setText("");
                } else {
                    loadingDialog.dismiss();
                    DialogUtils.showAlertDialog(RechargeActivity.this,
                            getUserInfoResponse.resultText);
                }


            }

            @Override
            public void dealWithError(String address, String error) {
                loadingDialog.dismiss();
                DialogUtils.showAlertDialog(RechargeActivity.this, error);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtils.e("requestCode:" + requestCode + " data:" + data);
        if (data != null) {

            Toast.makeText(RechargeActivity.this, data.getStringExtra("type"), Toast.LENGTH_LONG).show();
        }
    }
}
