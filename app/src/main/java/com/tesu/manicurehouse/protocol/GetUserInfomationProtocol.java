package com.tesu.manicurehouse.protocol;

import com.google.gson.Gson;
import com.tesu.manicurehouse.base.BaseProtocol;
import com.tesu.manicurehouse.response.GetUserInfoResponse;
import com.tesu.manicurehouse.response.GetUserInfomationResponse;

public class GetUserInfomationProtocol extends BaseProtocol<GetUserInfomationResponse> {
	private Gson gson;

	public GetUserInfomationProtocol() {
		gson = new Gson();
	}


	// 1. 封装请求参数
	// 2. 处理请求返回结果
	@Override
	protected GetUserInfomationResponse parseJson(String json) {
		GetUserInfomationResponse getUserInfoResponse = gson.fromJson(json, GetUserInfomationResponse.class);
		return getUserInfoResponse;
	}

	@Override
	public String getApiFun() {
		// TODO Auto-generated method stub
		return "app/getUserInfomation.do";
	}

	@Override
	public String getReqMethod() {
		// TODO Auto-generated method stub
		return "post";
	}
}
