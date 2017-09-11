package com.tesu.manicurehouse.protocol;

import com.google.gson.Gson;
import com.tesu.manicurehouse.base.BaseProtocol;
import com.tesu.manicurehouse.response.AddPostResponse;
import com.tesu.manicurehouse.response.DealGoodResponse;

public class DealGoodProtocol extends BaseProtocol<DealGoodResponse> {
	private Gson gson;

	public DealGoodProtocol() {
		gson = new Gson();
	}


	// 1. 封装请求参数
	// 2. 处理请求返回结果
	@Override
	protected DealGoodResponse parseJson(String json) {
		DealGoodResponse dealGoodResponse = gson.fromJson(json, DealGoodResponse.class);
		return dealGoodResponse;
	}

	@Override
	public String getApiFun() {
		// TODO Auto-generated method stub
		return "app/dealGood.do";
	}

	@Override
	public String getReqMethod() {
		// TODO Auto-generated method stub
		return "post";
	}
}
