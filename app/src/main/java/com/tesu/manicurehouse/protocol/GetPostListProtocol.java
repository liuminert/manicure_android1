package com.tesu.manicurehouse.protocol;

import com.google.gson.Gson;
import com.tesu.manicurehouse.base.BaseProtocol;
import com.tesu.manicurehouse.response.GetGoodListResponse;
import com.tesu.manicurehouse.response.GetPostListResponse;

public class GetPostListProtocol extends BaseProtocol<GetPostListResponse> {
	private Gson gson;

	public GetPostListProtocol() {
		gson = new Gson();
	}


	// 1. 封装请求参数
	// 2. 处理请求返回结果
	@Override
	protected GetPostListResponse parseJson(String json) {
		GetPostListResponse getPostListResponse = gson.fromJson(json, GetPostListResponse.class);
		return getPostListResponse;
	}

	@Override
	public String getApiFun() {
		// TODO Auto-generated method stub
		return "ecspost/getPostList.do";
	}

	@Override
	public String getReqMethod() {
		// TODO Auto-generated method stub
		return "post";
	}
}
