package com.tesu.manicurehouse.protocol;

import com.google.gson.Gson;
import com.tesu.manicurehouse.base.BaseProtocol;
import com.tesu.manicurehouse.response.LoginResponse;
import com.tesu.manicurehouse.response.PartyLandingResponse;

public class PartyLandingProtocol extends BaseProtocol<PartyLandingResponse> {
	private Gson gson;

	public PartyLandingProtocol() {
		gson = new Gson();
	}


	// 1. 封装请求参数
	// 2. 处理请求返回结果
	@Override
	protected PartyLandingResponse parseJson(String json) {
		PartyLandingResponse partyLandingResponse = gson.fromJson(json, PartyLandingResponse.class);
		return partyLandingResponse;
	}

	@Override
	public String getApiFun() {
		// TODO Auto-generated method stub
		return "app/partyLanding.do";
	}

	@Override
	public String getReqMethod() {
		// TODO Auto-generated method stub
		return "post";
	}
}
