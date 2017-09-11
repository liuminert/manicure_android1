package com.tesu.manicurehouse.protocol;

import com.google.gson.Gson;
import com.tesu.manicurehouse.base.BaseProtocol;
import com.tesu.manicurehouse.response.AddCartResponse;
import com.tesu.manicurehouse.response.BalancePayResponse;

public class BalancePayProtocol extends BaseProtocol<BalancePayResponse> {
	private Gson gson;

	public BalancePayProtocol() {
		gson = new Gson();
	}


	// 1. 封装请求参数
	// 2. 处理请求返回结果
	@Override
	protected BalancePayResponse parseJson(String json) {
		BalancePayResponse balancePayResponse = gson.fromJson(json, BalancePayResponse.class);
		return balancePayResponse;
	}

	@Override
	public String getApiFun() {
		// TODO Auto-generated method stub
		return "app/balancePay.do";
	}

	@Override
	public String getReqMethod() {
		// TODO Auto-generated method stub
		return "post";
	}
}
