package com.tesu.manicurehouse.protocol;

import com.google.gson.Gson;
import com.tesu.manicurehouse.base.BaseProtocol;
import com.tesu.manicurehouse.response.GetFollowInfoResponse;
import com.tesu.manicurehouse.response.GetInformationCommentListResponse;

public class GetInformationCommentListProtocol extends BaseProtocol<GetInformationCommentListResponse> {
	private Gson gson;

	public GetInformationCommentListProtocol() {
		gson = new Gson();
	}


	// 1. 封装请求参数
	// 2. 处理请求返回结果
	@Override
	protected GetInformationCommentListResponse parseJson(String json) {
		GetInformationCommentListResponse getInformationCommentListResponse = gson.fromJson(json, GetInformationCommentListResponse.class);
		return getInformationCommentListResponse;
	}

	@Override
	public String getApiFun() {
		// TODO Auto-generated method stub
		return "ecsinfo/getInformationCommentList.do";
	}

	@Override
	public String getReqMethod() {
		// TODO Auto-generated method stub
		return "post";
	}
}
