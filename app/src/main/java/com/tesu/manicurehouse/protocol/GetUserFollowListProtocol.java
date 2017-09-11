package com.tesu.manicurehouse.protocol;

import com.google.gson.Gson;
import com.tesu.manicurehouse.base.BaseProtocol;
import com.tesu.manicurehouse.response.GetUserFollowListResponse;
import com.tesu.manicurehouse.response.GetUserMsgResponse;

public class GetUserFollowListProtocol extends BaseProtocol<GetUserFollowListResponse> {
	private Gson gson;

	public GetUserFollowListProtocol() {
		gson = new Gson();
	}


	// 1. 封装请求参数
	// 2. 处理请求返回结果
	@Override
	protected GetUserFollowListResponse parseJson(String json) {
		GetUserFollowListResponse getUserFollowListResponse = gson.fromJson(json, GetUserFollowListResponse.class);
		return getUserFollowListResponse;
	}

	@Override
	public String getApiFun() {
		// TODO Auto-generated method stub
		return "app/getUserFollowList.do";
	}

	@Override
	public String getReqMethod() {
		// TODO Auto-generated method stub
		return "post";
	}
}
