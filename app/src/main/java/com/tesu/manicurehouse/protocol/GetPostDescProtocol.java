package com.tesu.manicurehouse.protocol;

import com.google.gson.Gson;
import com.tesu.manicurehouse.base.BaseProtocol;
import com.tesu.manicurehouse.response.GetGoodDescResponse;
import com.tesu.manicurehouse.response.GetPostDescResponse;

public class GetPostDescProtocol extends BaseProtocol<GetPostDescResponse> {
	private Gson gson;

	public GetPostDescProtocol() {
		gson = new Gson();
	}


	// 1. 封装请求参数
	// 2. 处理请求返回结果
	@Override
	protected GetPostDescResponse parseJson(String json) {
		GetPostDescResponse getPostDescResponse = gson.fromJson(json, GetPostDescResponse.class);
		return getPostDescResponse;
	}

	@Override
	public String getApiFun() {
		// TODO Auto-generated method stub
		return "ecspost/getPostDesc.do";
	}

	@Override
	public String getReqMethod() {
		// TODO Auto-generated method stub
		return "post";
	}
}
