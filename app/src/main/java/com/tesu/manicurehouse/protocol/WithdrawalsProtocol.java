package com.tesu.manicurehouse.protocol;

import com.google.gson.Gson;
import com.tesu.manicurehouse.base.BaseProtocol;
import com.tesu.manicurehouse.response.AddCartResponse;
import com.tesu.manicurehouse.response.WithdrawalsResponse;

public class WithdrawalsProtocol extends BaseProtocol<WithdrawalsResponse> {
	private Gson gson;

	public WithdrawalsProtocol() {
		gson = new Gson();
	}


	// 1. 封装请求参数
	// 2. 处理请求返回结果
	@Override
	protected WithdrawalsResponse parseJson(String json) {
		WithdrawalsResponse withdrawalsResponse = gson.fromJson(json, WithdrawalsResponse.class);
		return withdrawalsResponse;
	}

	@Override
	public String getApiFun() {
		// TODO Auto-generated method stub
		return "app/withdrawals.do";
	}

	@Override
	public String getReqMethod() {
		// TODO Auto-generated method stub
		return "post";
	}
}
