package com.tesu.manicurehouse.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
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
import com.tesu.manicurehouse.protocol.GetUserInfomationProtocol;
import com.tesu.manicurehouse.protocol.UpdateOrderStatusProtocol;
import com.tesu.manicurehouse.protocol.WeixinPayProtocol;
import com.tesu.manicurehouse.request.AlipayRequest;
import com.tesu.manicurehouse.request.BalancePayRequest;
import com.tesu.manicurehouse.request.GetUserInfomationRequest;
import com.tesu.manicurehouse.request.UpdateOrderStatusRequest;
import com.tesu.manicurehouse.request.WeixinPayRequest;
import com.tesu.manicurehouse.response.AlipayResponse;
import com.tesu.manicurehouse.response.BalancePayResponse;
import com.tesu.manicurehouse.response.GetUserInfomationResponse;
import com.tesu.manicurehouse.response.UpdateOrderStatusResponse;
import com.tesu.manicurehouse.response.WeixinPayResponse;
import com.tesu.manicurehouse.util.AuthResult;
import com.tesu.manicurehouse.util.Constants;
import com.tesu.manicurehouse.util.DialogUtils;
import com.tesu.manicurehouse.util.LogUtils;
import com.tesu.manicurehouse.util.PayResult;
import com.tesu.manicurehouse.util.SharedPrefrenceUtils;
import com.tesu.manicurehouse.util.SignUtils;
import com.tesu.manicurehouse.util.UIUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

/**
 * 类描述：
 * 创建人：许明达
 * 创建时间：2016/8/16 11:40
 * 支付订单
 */
public class PayOrderActivity extends BaseActivity implements View.OnClickListener {

    private ImageView iv_top_back;
    private View rootView;
    private Button btn_ensure_pay;
    private String url;
    private Dialog loadingDialog;
    private String order_id;
    private String out_trade_no;
    //订单总金额
    private double total_price;
    //需要支付的金额
    private String total_fee;
    private TextView tv_total_price;
    private CheckBox ch_balance;
    private CheckBox ch_weixin;
    private CheckBox ch_zhifubao;
    private CheckBox cb_integral;
    //1余额支付，2微信支付,3支付宝支付
    private int type = 0;
    private static final int SDK_PAY_FLAG = 1;
    private String body;
    private String detail;
    private IWXAPI api;
    //是否使用积分 0否，1是
    private int is_use_integral;
    //是否使用余额 0否，1是
    private int is_use_balance;
    private TextView tv_user_moneyuser_money;
    private TextView tv_pay_points;
    private int total_integral;
    private int user_integral;
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
                        Toast.makeText(PayOrderActivity.this, "支付成功", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(PayOrderActivity.this, PaySuccessActivity.class);
                        UIUtils.startActivityNextAnim(intent);
                        setFinish();
                    } else {
                        // 判断resultStatus 为非"9000"则代表可能支付失败
                        // "8000"代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                        if (TextUtils.equals(resultStatus, "8000")) {
                            Toast.makeText(PayOrderActivity.this, "支付结果确认中", Toast.LENGTH_SHORT).show();

                        } else if (TextUtils.equals(resultStatus, "4000")) {
                            // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                            Toast.makeText(PayOrderActivity.this, "请先安装支付宝客户端", Toast.LENGTH_SHORT).show();
                        } else {
                            // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                            Toast.makeText(PayOrderActivity.this, "支付失败", Toast.LENGTH_SHORT).show();
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
        rootView = View.inflate(this, R.layout.activity_pay_order, null);
        setContentView(rootView);
        tv_user_moneyuser_money = (TextView) rootView.findViewById(R.id.tv_user_moneyuser_money);
        tv_pay_points= (TextView) rootView.findViewById(R.id.tv_pay_points);
        ch_balance = (CheckBox) rootView.findViewById(R.id.ch_balance);
        ch_weixin = (CheckBox) rootView.findViewById(R.id.ch_weixin);
        ch_zhifubao = (CheckBox) rootView.findViewById(R.id.ch_zhifubao);
        cb_integral = (CheckBox) rootView.findViewById(R.id.cb_integral);
        tv_total_price = (TextView) rootView.findViewById(R.id.tv_total_price);
        iv_top_back = (ImageView) rootView.findViewById(R.id.iv_top_back);
        btn_ensure_pay = (Button) rootView.findViewById(R.id.btn_ensure_pay);
        initData();
        return null;
    }


    public void initData() {
        api = WXAPIFactory.createWXAPI(this, Constants.APP_ID);
        Intent intent = getIntent();
        body = intent.getStringExtra("body");
        detail = intent.getStringExtra("detail");
        total_fee = intent.getStringExtra("total_fee");
        out_trade_no = intent.getStringExtra("out_trade_no");
        total_integral = intent.getIntExtra("total_integral", 0);
        total_price = intent.getDoubleExtra("total_price", 0);
        order_id = intent.getStringExtra("order_id");
        LogUtils.e("total_integral:"+total_integral);
        loadingDialog = DialogUtils.createLoadDialog(PayOrderActivity.this, true);
        iv_top_back.setOnClickListener(this);
        btn_ensure_pay.setOnClickListener(this);
        DecimalFormat df = new DecimalFormat("0.00");
        tv_total_price.setText("￥" + df.format(Double.parseDouble(total_fee)));
        ch_balance.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    type = 1;
                    is_use_balance = 1;
                } else {
                    is_use_balance = 0;
                }
            }
        });
        ch_weixin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    type = 2;
                    ch_zhifubao.setChecked(false);
                }
                else{
                    if(ch_balance.isChecked()){
                        type = 1;
                    }
                    else{
                        type = 0;
                    }
                }
            }
        });
        ch_zhifubao.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    type = 3;
                    ch_weixin.setChecked(false);
                }
                else{
                    if(ch_balance.isChecked()){
                        type = 1;
                    }
                    else{
                        type = 0;
                    }
                }
            }
        });
        cb_integral.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                DecimalFormat df = new DecimalFormat("0.00");
                if (isChecked) {
                    if (total_integral > 0) {
                        is_use_integral = 1;
                        if (user_integral >= total_integral) {
                            tv_total_price.setText("￥" + df.format(Double.parseDouble(total_fee) - total_integral / 10.0));
                        } else {
                            tv_total_price.setText("￥" + df.format(Double.parseDouble(total_fee) - user_integral / 10.0));
                        }
                    } else {
                        DialogUtils.showAlertDialog(PayOrderActivity.this,
                                "此商品不能用积分抵扣！");
                        cb_integral.setChecked(false);
                    }
                } else {
                    tv_total_price.setText("￥" + df.format(Double.parseDouble(total_fee)));
                    is_use_integral = 0;
                }
            }
        });

        runGetUserInfo();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_top_back:
                finish();
                overridePendingTransition(R.anim.animprv_in, R.anim.animprv_out);
                break;
            case R.id.btn_ensure_pay: {
                if (type > 0) {
                    switch (type) {
                        case 1:
                            runBalancePay();
                            break;
                        case 2:
                            runWeixinpay();
                            break;
                        case 3:
                            runAlipay();
                            break;
                    }
                } else {
                    DialogUtils.showAlertDialog(PayOrderActivity.this,
                            "请选择一种支付方式！");
                }
                break;

            }
        }
    }

    public void runUpdateOrderStatus() {
        loadingDialog.show();
        UpdateOrderStatusProtocol updateOrderStatusProtocol = new UpdateOrderStatusProtocol();
        UpdateOrderStatusRequest updateOrderStatusRequest = new UpdateOrderStatusRequest();
        url = updateOrderStatusProtocol.getApiFun();
        updateOrderStatusRequest.map.put("user_id", SharedPrefrenceUtils.getString(PayOrderActivity.this, "user_id"));
        updateOrderStatusRequest.map.put("pay_status", "2");
        updateOrderStatusRequest.map.put("order_id", order_id);

        MyVolley.uploadNoFile(MyVolley.POST, url, updateOrderStatusRequest.map, new MyVolley.VolleyCallback() {
            @Override
            public void dealWithJson(String address, String json) {

                Gson gson = new Gson();
                UpdateOrderStatusResponse updateOrderStatusResponse = gson.fromJson(json, UpdateOrderStatusResponse.class);
                LogUtils.e("sendVerificationCodeResponse:" + json.toString());
                if (updateOrderStatusResponse.code == 0) {
                    loadingDialog.dismiss();
                    Intent intent = new Intent(PayOrderActivity.this, PaySuccessActivity.class);
                    UIUtils.startActivityNextAnim(intent);
                    setFinish();
                } else {
                    loadingDialog.dismiss();
                    DialogUtils.showAlertDialog(PayOrderActivity.this,
                            updateOrderStatusResponse.resultText);
                }


            }

            @Override
            public void dealWithError(String address, String error) {
                loadingDialog.dismiss();
                DialogUtils.showAlertDialog(PayOrderActivity.this, error);
            }
        });
    }

    //余额支付
    public void runBalancePay() {
        loadingDialog.show();
        BalancePayProtocol balancePayProtocol = new BalancePayProtocol();
        BalancePayRequest balancePayRequest = new BalancePayRequest();
        url = balancePayProtocol.getApiFun();
        balancePayRequest.map.put("user_id", SharedPrefrenceUtils.getString(PayOrderActivity.this, "user_id"));
        balancePayRequest.map.put("out_trade_no", out_trade_no);
        balancePayRequest.map.put("order_id", order_id);
        //       是否使用积分 0否，1是
        balancePayRequest.map.put("is_use_integral", String.valueOf(is_use_integral));
        MyVolley.uploadNoFile(MyVolley.POST, url, balancePayRequest.map, new MyVolley.VolleyCallback() {
            @Override
            public void dealWithJson(String address, String json) {

                Gson gson = new Gson();
                BalancePayResponse balancePayResponse = gson.fromJson(json, BalancePayResponse.class);
                LogUtils.e("sendVerificationCodeResponse:" + json.toString());
                if (balancePayResponse.code == 0) {
                    loadingDialog.dismiss();
//                    runUpdateOrderStatus();
                    Intent intent = new Intent(PayOrderActivity.this, PaySuccessActivity.class);
                    UIUtils.startActivityNextAnim(intent);
                    setFinish();
                } else {
                    loadingDialog.dismiss();
                    DialogUtils.showAlertDialog(PayOrderActivity.this,
                            balancePayResponse.resultText);
                }
            }

            @Override
            public void dealWithError(String address, String error) {
                loadingDialog.dismiss();
                DialogUtils.showAlertDialog(PayOrderActivity.this, error);
            }
        });
    }

    //支付宝支付
    public void runAlipay() {
        loadingDialog.show();
        AlipayProtocol alipayProtocol = new AlipayProtocol();
        AlipayRequest alipayRequest = new AlipayRequest();
        url = alipayProtocol.getApiFun();
        alipayRequest.map.put("user_id", SharedPrefrenceUtils.getString(PayOrderActivity.this, "user_id"));
        alipayRequest.map.put("out_trade_no", out_trade_no);
        alipayRequest.map.put("order_id", order_id);
        //       是否使用积分 0否，1是
        alipayRequest.map.put("is_use_integral", String.valueOf(is_use_integral));
        alipayRequest.map.put("is_use_balance", String.valueOf(is_use_balance));
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
                    DialogUtils.showAlertDialog(PayOrderActivity.this,
                            alipayResponse.resultText);
                }
            }

            @Override
            public void dealWithError(String address, String error) {
                loadingDialog.dismiss();
                DialogUtils.showAlertDialog(PayOrderActivity.this, error);
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
                PayTask alipay = new PayTask(PayOrderActivity.this);
                // 调用支付接口，获取支付结果
                String result = alipay.pay(payInfo, true);

                LogUtils.e("调用支付宝结果:" + result);
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

    //微信支付
    public void runWeixinpay() {
        loadingDialog.show();
        WeixinPayProtocol weixinPayProtocol = new WeixinPayProtocol();
        WeixinPayRequest weixinPayRequest = new WeixinPayRequest();
        url = weixinPayProtocol.getApiFun();
        weixinPayRequest.map.put("user_id", SharedPrefrenceUtils.getString(PayOrderActivity.this, "user_id"));
        weixinPayRequest.map.put("out_trade_no", out_trade_no);
        weixinPayRequest.map.put("order_id", order_id);
        //       是否使用积分 0否，1是
        weixinPayRequest.map.put("is_use_integral", String.valueOf(is_use_integral));
        weixinPayRequest.map.put("is_use_balance", String.valueOf(is_use_balance));
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
                } else {
                    loadingDialog.dismiss();
                    DialogUtils.showAlertDialog(PayOrderActivity.this,
                            weixinPayResponse.resultText);
                }
            }

            @Override
            public void dealWithError(String address, String error) {
                loadingDialog.dismiss();
                DialogUtils.showAlertDialog(PayOrderActivity.this, error);
            }
        });
    }

    //获取会员信息
    public void runGetUserInfo() {
        loadingDialog.show();
        GetUserInfomationProtocol getUserInfoProtocol = new GetUserInfomationProtocol();
        GetUserInfomationRequest getUserInfoRequest = new GetUserInfomationRequest();
        url = getUserInfoProtocol.getApiFun();
        getUserInfoRequest.map.put("user_id", SharedPrefrenceUtils.getString(PayOrderActivity.this, "user_id"));


        MyVolley.uploadNoFile(MyVolley.POST, url, getUserInfoRequest.map, new MyVolley.VolleyCallback() {
            @Override
            public void dealWithJson(String address, String json) {

                Gson gson = new Gson();
                GetUserInfomationResponse getUserInfoResponse = gson.fromJson(json, GetUserInfomationResponse.class);
                LogUtils.e("sendVerificationCodeResponse:" + json.toString());
                loadingDialog.dismiss();
                if (getUserInfoResponse.code.equals("0")) {
                    tv_user_moneyuser_money.setText("￥" + getUserInfoResponse.user_money);
                    DecimalFormat df = new DecimalFormat("0.0");
                    user_integral=getUserInfoResponse.pay_points;
                    if (total_integral > 0) {
                        if (user_integral >= total_integral) {
                            tv_pay_points.setText("可用" + total_integral + "积分抵" + df.format(Double.parseDouble(String.valueOf(total_integral / 10.0))) + "块");
                        } else {
                            tv_pay_points.setText("可用" + user_integral + "积分抵" + df.format(Double.parseDouble(String.valueOf(user_integral / 10.0))) + "块");
                        }
                    } else {
                        tv_pay_points.setText("可用0积分抵0块");
                    }

                } else {
                    DialogUtils.showAlertDialog(PayOrderActivity.this,
                            getUserInfoResponse.resultText);
                }


            }

            @Override
            public void dealWithError(String address, String error) {
                loadingDialog.dismiss();
                DialogUtils.showAlertDialog(PayOrderActivity.this, error);
            }
        });
    }
}
