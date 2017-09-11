package com.tesu.manicurehouse.protocol;

import com.google.gson.Gson;
import com.tesu.manicurehouse.base.BaseProtocol;
import com.tesu.manicurehouse.response.GetActListResponse;
import com.tesu.manicurehouse.response.GetCommonProblemResponse;

public class GetCommonProblemProtocol extends BaseProtocol<GetCommonProblemResponse> {
	private Gson gson;

	public GetCommonProblemProtocol() {
		gson = new Gson();
	}


	// 1. 封装请求参数
	// 2. 处理请求返回结果
	@Override
	protected GetCommonProblemResponse parseJson(String json) {
		GetCommonProblemResponse getCommonProblemResponse = gson.fromJson(json, GetCommonProblemResponse.class);
		return getCommonProblemResponse;
	}

	@Override
	public String getApiFun() {
		// TODO Auto-generated method stub
		return "app/getCommonProblem.do";
	}

	@Override
	public String getReqMethod() {
		// TODO Auto-generated method stub
		return "post";
	}
}
