package com.tesu.manicurehouse.protocol;

import com.google.gson.Gson;
import com.tesu.manicurehouse.base.BaseProtocol;
import com.tesu.manicurehouse.response.AlipayResponse;
import com.tesu.manicurehouse.response.BalancePayResponse;

public class AlipayProtocol extends BaseProtocol<AlipayResponse> {
	private Gson gson;

	public AlipayProtocol() {
		gson = new Gson();
	}


	// 1. 封装请求参数
	// 2. 处理请求返回结果
	@Override
	protected AlipayResponse parseJson(String json) {
		AlipayResponse alipayResponse = gson.fromJson(json, AlipayResponse.class);
		return alipayResponse;
	}

	@Override
	public String getApiFun() {
		// TODO Auto-generated method stub
		return "app/alipay.do";
	}

	@Override
	public String getReqMethod() {
		// TODO Auto-generated method stub
		return "post";
	}
}
