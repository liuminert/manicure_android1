package com.tesu.manicurehouse.protocol;

import com.google.gson.Gson;
import com.tesu.manicurehouse.base.BaseProtocol;
import com.tesu.manicurehouse.response.AddCartResponse;
import com.tesu.manicurehouse.response.CalculatePostageResponse;

public class CalculatePostageProtocol extends BaseProtocol<CalculatePostageResponse> {
	private Gson gson;

	public CalculatePostageProtocol() {
		gson = new Gson();
	}


	// 1. 封装请求参数
	// 2. 处理请求返回结果
	@Override
	protected CalculatePostageResponse parseJson(String json) {
		CalculatePostageResponse calculatePostageResponse = gson.fromJson(json, CalculatePostageResponse.class);
		return calculatePostageResponse;
	}

	@Override
	public String getApiFun() {
		// TODO Auto-generated method stub
		return "app/calculatePostage.do";
	}

	@Override
	public String getReqMethod() {
		// TODO Auto-generated method stub
		return "post";
	}
}
