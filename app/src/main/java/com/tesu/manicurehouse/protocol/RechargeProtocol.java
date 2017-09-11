package com.tesu.manicurehouse.protocol;

import com.google.gson.Gson;
import com.tesu.manicurehouse.base.BaseProtocol;
import com.tesu.manicurehouse.response.AddCartResponse;
import com.tesu.manicurehouse.response.RechargeResponse;

public class RechargeProtocol extends BaseProtocol<RechargeResponse> {
	private Gson gson;

	public RechargeProtocol() {
		gson = new Gson();
	}


	// 1. 封装请求参数
	// 2. 处理请求返回结果
	@Override
	protected RechargeResponse parseJson(String json) {
		RechargeResponse rechargeResponse = gson.fromJson(json, RechargeResponse.class);
		return rechargeResponse;
	}

	@Override
	public String getApiFun() {
		// TODO Auto-generated method stub
		return "app/recharge.do";
	}

	@Override
	public String getReqMethod() {
		// TODO Auto-generated method stub
		return "post";
	}
}
