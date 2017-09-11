package com.tesu.manicurehouse.protocol;

import com.google.gson.Gson;
import com.tesu.manicurehouse.base.BaseProtocol;
import com.tesu.manicurehouse.response.GetRecommendDescListResponse;
import com.tesu.manicurehouse.response.GetShareDescListResponse;

public class GetShareDescListProtocol extends BaseProtocol<GetShareDescListResponse> {
	private Gson gson;

	public GetShareDescListProtocol() {
		gson = new Gson();
	}


	// 1. 封装请求参数
	// 2. 处理请求返回结果
	@Override
	protected GetShareDescListResponse parseJson(String json) {
		GetShareDescListResponse getRecommendDescListResponse = gson.fromJson(json, GetShareDescListResponse.class);
		return getRecommendDescListResponse;
	}

	@Override
	public String getApiFun() {
		// TODO Auto-generated method stub
		return "app/getShareDescList.do";
	}

	@Override
	public String getReqMethod() {
		// TODO Auto-generated method stub
		return "post";
	}
}
