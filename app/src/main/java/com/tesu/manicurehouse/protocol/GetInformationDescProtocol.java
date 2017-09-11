package com.tesu.manicurehouse.protocol;

import com.google.gson.Gson;
import com.tesu.manicurehouse.base.BaseProtocol;
import com.tesu.manicurehouse.response.GetFollowInfoResponse;
import com.tesu.manicurehouse.response.GetInformationDescResponse;

public class GetInformationDescProtocol extends BaseProtocol<GetInformationDescResponse> {
	private Gson gson;

	public GetInformationDescProtocol() {
		gson = new Gson();
	}


	// 1. 封装请求参数
	// 2. 处理请求返回结果
	@Override
	protected GetInformationDescResponse parseJson(String json) {
		GetInformationDescResponse getInformationDescResponse = gson.fromJson(json, GetInformationDescResponse.class);
		return getInformationDescResponse;
	}

	@Override
	public String getApiFun() {
		// TODO Auto-generated method stub
		return "ecsinfo/getInformationDesc.do";
	}

	@Override
	public String getReqMethod() {
		// TODO Auto-generated method stub
		return "post";
	}
}
