package com.tesu.manicurehouse.wxapi;







import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tesu.manicurehouse.R;
import com.tesu.manicurehouse.activity.PaySuccessActivity;
import com.tesu.manicurehouse.activity.RechargeActivity;
import com.tesu.manicurehouse.util.Constants;
import com.tesu.manicurehouse.util.DialogUtils;
import com.tesu.manicurehouse.util.SharedPrefrenceUtils;
import com.tesu.manicurehouse.util.UIUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler,View.OnClickListener{
	
	private static final String TAG = "MicroMsg.SDKSample.WXPayEntryActivity";
	
    private IWXAPI api;
	private boolean isSuccess=false;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.pay_result);
        
    	api = WXAPIFactory.createWXAPI(this, Constants.APP_ID);
        api.handleIntent(getIntent(), this);
    }

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
        api.handleIntent(intent, this);
	}

	@Override
	public void onReq(BaseReq req) {
	}

	@Override
	public void onResp(BaseResp resp) {

		if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
			String msg = "";

			if(resp.errCode == 0)
			{
				msg = "支付成功";
				isSuccess=true;
			}
			else if(resp.errCode == -2)
			{
				msg = "已取消支付";
			}
			else if(resp.errCode == -1)
			{
				msg = "支付失败";
			}
			DialogUtils.showAlertDialog(WXPayEntryActivity.this,msg,this);
//			AlertDialog.Builder builder = new AlertDialog.Builder(this);
//			builder.setTitle("提示");
//			builder.setMessage(getString(R.string.pay_result_callback_msg, String.valueOf(resp.errCode)));
//			builder.show();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()){
			case R.id.tv_roger:
				if(isSuccess) {
					if(SharedPrefrenceUtils.getBoolean(WXPayEntryActivity.this,"recharge")){
						Intent intent = new Intent(WXPayEntryActivity.this, RechargeActivity.class);
						UIUtils.startActivityNextAnim(intent);
					}
					else {
						Intent intent = new Intent(WXPayEntryActivity.this, PaySuccessActivity.class);
						UIUtils.startActivityNextAnim(intent);
					}
				}
				else{
					if(SharedPrefrenceUtils.getBoolean(WXPayEntryActivity.this,"recharge")){
						Intent intent = new Intent(WXPayEntryActivity.this, RechargeActivity.class);
						UIUtils.startActivityNextAnim(intent);
					}
				}
				SharedPrefrenceUtils.setBoolean(WXPayEntryActivity.this,"recharge",false);
				finish();
				overridePendingTransition(R.anim.animprv_in, R.anim.animprv_out);
				break;
		}
	}
}