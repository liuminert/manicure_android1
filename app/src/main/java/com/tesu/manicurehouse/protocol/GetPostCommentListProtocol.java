package com.tesu.manicurehouse.protocol;

import com.google.gson.Gson;
import com.tesu.manicurehouse.base.BaseProtocol;
import com.tesu.manicurehouse.response.GetInformationCommentListResponse;
import com.tesu.manicurehouse.response.GetPostCommentListResponse;

public class GetPostCommentListProtocol extends BaseProtocol<GetPostCommentListResponse> {
	private Gson gson;

	public GetPostCommentListProtocol() {
		gson = new Gson();
	}


	// 1. 封装请求参数
	// 2. 处理请求返回结果
	@Override
	protected GetPostCommentListResponse parseJson(String json) {
		GetPostCommentListResponse getPostCommentListResponse = gson.fromJson(json, GetPostCommentListResponse.class);
		return getPostCommentListResponse;
	}

	@Override
	public String getApiFun() {
		// TODO Auto-generated method stub
		return "ecspost/getPostCommentList.do";
	}

	@Override
	public String getReqMethod() {
		// TODO Auto-generated method stub
		return "post";
	}
}
