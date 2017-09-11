package com.tesu.manicurehouse.protocol;

import com.google.gson.Gson;
import com.tesu.manicurehouse.base.BaseProtocol;
import com.tesu.manicurehouse.response.GetActDescByIdResponse;
import com.tesu.manicurehouse.response.GetActListResponse;

public class GetActDescByIdProtocol extends BaseProtocol<GetActDescByIdResponse> {
	private Gson gson;

	public GetActDescByIdProtocol() {
		gson = new Gson();
	}


	// 1. 封装请求参数
	// 2. 处理请求返回结果
	@Override
	protected GetActDescByIdResponse parseJson(String json) {
		GetActDescByIdResponse getActDescByIdResponse = gson.fromJson(json, GetActDescByIdResponse.class);
		return getActDescByIdResponse;
	}

	@Override
	public String getApiFun() {
		// TODO Auto-generated method stub
		return "app/getActDescById.do";
	}

	@Override
	public String getReqMethod() {
		// TODO Auto-generated method stub
		return "post";
	}
}
