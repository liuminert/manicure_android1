package com.tesu.manicurehouse.protocol;

import com.google.gson.Gson;
import com.tesu.manicurehouse.base.BaseProtocol;
import com.tesu.manicurehouse.response.GetGoodDescResponse;
import com.tesu.manicurehouse.response.GetGoodListResponse;

public class GetGoodDescProtocol extends BaseProtocol<GetGoodDescResponse> {
	private Gson gson;

	public GetGoodDescProtocol() {
		gson = new Gson();
	}


	// 1. 封装请求参数
	// 2. 处理请求返回结果
	@Override
	protected GetGoodDescResponse parseJson(String json) {
		GetGoodDescResponse getGoodDescResponse = gson.fromJson(json, GetGoodDescResponse.class);
		return getGoodDescResponse;
	}

	@Override
	public String getApiFun() {
		// TODO Auto-generated method stub
		return "shop/getGoodDesc.do";
	}

	@Override
	public String getReqMethod() {
		// TODO Auto-generated method stub
		return "post";
	}
}
