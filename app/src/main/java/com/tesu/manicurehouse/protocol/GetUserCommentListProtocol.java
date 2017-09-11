package com.tesu.manicurehouse.protocol;

import com.google.gson.Gson;
import com.tesu.manicurehouse.base.BaseProtocol;
import com.tesu.manicurehouse.response.GetUserCommentListResponse;
import com.tesu.manicurehouse.response.GetUserFollowListResponse;

public class GetUserCommentListProtocol extends BaseProtocol<GetUserCommentListResponse> {
	private Gson gson;

	public GetUserCommentListProtocol() {
		gson = new Gson();
	}


	// 1. 封装请求参数
	// 2. 处理请求返回结果
	@Override
	protected GetUserCommentListResponse parseJson(String json) {
		GetUserCommentListResponse getUserCommentListResponse = gson.fromJson(json, GetUserCommentListResponse.class);
		return getUserCommentListResponse;
	}

	@Override
	public String getApiFun() {
		// TODO Auto-generated method stub
		return "app/getUserCommentList.do";
	}

	@Override
	public String getReqMethod() {
		// TODO Auto-generated method stub
		return "post";
	}
}
