package com.tesu.manicurehouse.protocol;

import com.google.gson.Gson;
import com.tesu.manicurehouse.base.BaseProtocol;
import com.tesu.manicurehouse.response.GetGoodListResponse;
import com.tesu.manicurehouse.response.GetInformationListResponse;

public class GetInformationListProtocol extends BaseProtocol<GetInformationListResponse> {
	private Gson gson;

	public GetInformationListProtocol() {
		gson = new Gson();
	}


	// 1. 封装请求参数
	// 2. 处理请求返回结果
	@Override
	protected GetInformationListResponse parseJson(String json) {
		GetInformationListResponse getInformationListResponse = gson.fromJson(json, GetInformationListResponse.class);
		return getInformationListResponse;
	}

	@Override
	public String getApiFun() {
		// TODO Auto-generated method stub
		return "ecsinfo/getInformationList.do";
	}

	@Override
	public String getReqMethod() {
		// TODO Auto-generated method stub
		return "post";
	}
}
