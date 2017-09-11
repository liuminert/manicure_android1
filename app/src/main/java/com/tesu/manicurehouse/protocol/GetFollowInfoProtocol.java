package com.tesu.manicurehouse.protocol;

import com.google.gson.Gson;
import com.tesu.manicurehouse.base.BaseProtocol;
import com.tesu.manicurehouse.response.GetFollowInfoResponse;
import com.tesu.manicurehouse.response.GetOrderListResponse;

public class GetFollowInfoProtocol extends BaseProtocol<GetFollowInfoResponse> {
	private Gson gson;

	public GetFollowInfoProtocol() {
		gson = new Gson();
	}


	// 1. 封装请求参数
	// 2. 处理请求返回结果
	@Override
	protected GetFollowInfoResponse parseJson(String json) {
		GetFollowInfoResponse getFollowInfoResponse = gson.fromJson(json, GetFollowInfoResponse.class);
		return getFollowInfoResponse;
	}

	@Override
	public String getApiFun() {
		// TODO Auto-generated method stub
		return "app/getFollowInfo.do";
	}

	@Override
	public String getReqMethod() {
		// TODO Auto-generated method stub
		return "post";
	}
}
