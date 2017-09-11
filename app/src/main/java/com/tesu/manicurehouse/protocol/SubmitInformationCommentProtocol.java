package com.tesu.manicurehouse.protocol;

import com.google.gson.Gson;
import com.tesu.manicurehouse.base.BaseProtocol;
import com.tesu.manicurehouse.response.AddOrCancelLikedPostOrInformationResponse;
import com.tesu.manicurehouse.response.SubmitInformationCommentResponse;

public class SubmitInformationCommentProtocol extends BaseProtocol<SubmitInformationCommentResponse> {
	private Gson gson;

	public SubmitInformationCommentProtocol() {
		gson = new Gson();
	}


	// 1. 封装请求参数
	// 2. 处理请求返回结果
	@Override
	protected SubmitInformationCommentResponse parseJson(String json) {
		SubmitInformationCommentResponse submitInformationCommentResponse = gson.fromJson(json, SubmitInformationCommentResponse.class);
		return submitInformationCommentResponse;
	}

	@Override
	public String getApiFun() {
		// TODO Auto-generated method stub
		return "ecsinfo/submitInformationComment.do";
	}

	@Override
	public String getReqMethod() {
		// TODO Auto-generated method stub
		return "post";
	}
}
