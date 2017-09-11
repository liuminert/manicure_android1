package com.tesu.manicurehouse.protocol;

import com.google.gson.Gson;
import com.tesu.manicurehouse.base.BaseProtocol;
import com.tesu.manicurehouse.response.CancelFollowResponse;
import com.tesu.manicurehouse.response.GetFollowInfoResponse;

public class CancelFollowProtocol extends BaseProtocol<CancelFollowResponse> {
	private Gson gson;

	public CancelFollowProtocol() {
		gson = new Gson();
	}


	// 1. 封装请求参数
	// 2. 处理请求返回结果
	@Override
	protected CancelFollowResponse parseJson(String json) {
		CancelFollowResponse cancelFollowResponse = gson.fromJson(json, CancelFollowResponse.class);
		return cancelFollowResponse;
	}

	@Override
	public String getApiFun() {
		// TODO Auto-generated method stub
		return "app/cancelFollow.do";
	}

	@Override
	public String getReqMethod() {
		// TODO Auto-generated method stub
		return "post";
	}
}
