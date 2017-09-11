package com.tesu.manicurehouse.protocol;

import com.google.gson.Gson;
import com.tesu.manicurehouse.base.BaseProtocol;
import com.tesu.manicurehouse.response.AuthenticateResponse;
import com.tesu.manicurehouse.response.CancelFollowResponse;

public class AuthenticateProtocol extends BaseProtocol<AuthenticateResponse> {
	private Gson gson;

	public AuthenticateProtocol() {
		gson = new Gson();
	}


	// 1. 封装请求参数
	// 2. 处理请求返回结果
	@Override
	protected AuthenticateResponse parseJson(String json) {
		AuthenticateResponse authenticateResponse = gson.fromJson(json, AuthenticateResponse.class);
		return authenticateResponse;
	}

	@Override
	public String getApiFun() {
		// TODO Auto-generated method stub
		return "app/authenticate.do";
	}

	@Override
	public String getReqMethod() {
		// TODO Auto-generated method stub
		return "post";
	}
}
