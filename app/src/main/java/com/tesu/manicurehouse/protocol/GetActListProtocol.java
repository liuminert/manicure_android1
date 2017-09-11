package com.tesu.manicurehouse.protocol;

import com.google.gson.Gson;
import com.tesu.manicurehouse.base.BaseProtocol;
import com.tesu.manicurehouse.response.GetActListResponse;
import com.tesu.manicurehouse.response.GetAdsResponse;

public class GetActListProtocol extends BaseProtocol<GetActListResponse> {
	private Gson gson;

	public GetActListProtocol() {
		gson = new Gson();
	}


	// 1. 封装请求参数
	// 2. 处理请求返回结果
	@Override
	protected GetActListResponse parseJson(String json) {
		GetActListResponse getActListResponse = gson.fromJson(json, GetActListResponse.class);
		return getActListResponse;
	}

	@Override
	public String getApiFun() {
		// TODO Auto-generated method stub
		return "app/getActList.do";
	}

	@Override
	public String getReqMethod() {
		// TODO Auto-generated method stub
		return "post";
	}
}
