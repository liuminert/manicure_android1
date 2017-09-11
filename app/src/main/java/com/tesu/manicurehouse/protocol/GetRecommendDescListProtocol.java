package com.tesu.manicurehouse.protocol;

import com.google.gson.Gson;
import com.tesu.manicurehouse.base.BaseProtocol;
import com.tesu.manicurehouse.response.GetActListResponse;
import com.tesu.manicurehouse.response.GetRecommendDescListResponse;

public class GetRecommendDescListProtocol extends BaseProtocol<GetRecommendDescListResponse> {
	private Gson gson;

	public GetRecommendDescListProtocol() {
		gson = new Gson();
	}


	// 1. 封装请求参数
	// 2. 处理请求返回结果
	@Override
	protected GetRecommendDescListResponse parseJson(String json) {
		GetRecommendDescListResponse getRecommendDescListResponse = gson.fromJson(json, GetRecommendDescListResponse.class);
		return getRecommendDescListResponse;
	}

	@Override
	public String getApiFun() {
		// TODO Auto-generated method stub
		return "app/getRecommendDescList.do";
	}

	@Override
	public String getReqMethod() {
		// TODO Auto-generated method stub
		return "post";
	}
}
