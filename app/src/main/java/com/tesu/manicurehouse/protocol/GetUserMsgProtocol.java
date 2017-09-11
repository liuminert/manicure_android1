package com.tesu.manicurehouse.protocol;

import com.google.gson.Gson;
import com.tesu.manicurehouse.base.BaseProtocol;
import com.tesu.manicurehouse.response.GetUserInfoResponse;
import com.tesu.manicurehouse.response.GetUserMsgResponse;

public class GetUserMsgProtocol extends BaseProtocol<GetUserMsgResponse> {
	private Gson gson;

	public GetUserMsgProtocol() {
		gson = new Gson();
	}


	// 1. 封装请求参数
	// 2. 处理请求返回结果
	@Override
	protected GetUserMsgResponse parseJson(String json) {
		GetUserMsgResponse getUserMsgResponse = gson.fromJson(json, GetUserMsgResponse.class);
		return getUserMsgResponse;
	}

	@Override
	public String getApiFun() {
		// TODO Auto-generated method stub
		return "app/getUserMsg.do";
	}

	@Override
	public String getReqMethod() {
		// TODO Auto-generated method stub
		return "post";
	}
}
