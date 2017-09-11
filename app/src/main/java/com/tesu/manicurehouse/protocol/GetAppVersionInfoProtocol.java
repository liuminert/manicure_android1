package com.tesu.manicurehouse.protocol;

import com.google.gson.Gson;
import com.tesu.manicurehouse.base.BaseProtocol;
import com.tesu.manicurehouse.response.GetAppVersionInfoResponse;

public class GetAppVersionInfoProtocol extends BaseProtocol<GetAppVersionInfoResponse> {
	private Gson gson;

	public GetAppVersionInfoProtocol() {
		gson = new Gson();
	}


	// 1. 封装请求参数
	// 2. 处理请求返回结果
	@Override
	protected GetAppVersionInfoResponse parseJson(String json) {
		GetAppVersionInfoResponse getAppVersionInfoResponse = gson.fromJson(json, GetAppVersionInfoResponse.class);
		return getAppVersionInfoResponse;
	}

	@Override
	public String getApiFun() {
		// TODO Auto-generated method stub
		return "app/getAppVersionInfo.do";
	}

	@Override
	public String getReqMethod() {
		// TODO Auto-generated method stub
		return "post";
	}
}
