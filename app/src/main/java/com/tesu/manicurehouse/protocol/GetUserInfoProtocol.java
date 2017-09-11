package com.tesu.manicurehouse.protocol;

import com.google.gson.Gson;
import com.tesu.manicurehouse.base.BaseProtocol;
import com.tesu.manicurehouse.response.GetUserInfoResponse;
import com.tesu.manicurehouse.response.LoginResponse;

public class GetUserInfoProtocol extends BaseProtocol<GetUserInfoResponse> {
	private Gson gson;

	public GetUserInfoProtocol() {
		gson = new Gson();
	}


	// 1. 封装请求参数
	// 2. 处理请求返回结果
	@Override
	protected GetUserInfoResponse parseJson(String json) {
		GetUserInfoResponse getUserInfoResponse = gson.fromJson(json, GetUserInfoResponse.class);
		return getUserInfoResponse;
	}

	@Override
	public String getApiFun() {
		// TODO Auto-generated method stub
		return "app/getUserInfo.do";
	}

	@Override
	public String getReqMethod() {
		// TODO Auto-generated method stub
		return "post";
	}
}
