package com.tesu.manicurehouse.protocol;

import com.google.gson.Gson;
import com.tesu.manicurehouse.base.BaseProtocol;
import com.tesu.manicurehouse.response.SubmitInformationCommentResponse;
import com.tesu.manicurehouse.response.SubmitPostCommentResponse;

public class SubmitPostCommentProtocol extends BaseProtocol<SubmitPostCommentResponse> {
	private Gson gson;

	public SubmitPostCommentProtocol() {
		gson = new Gson();
	}


	// 1. 封装请求参数
	// 2. 处理请求返回结果
	@Override
	protected SubmitPostCommentResponse parseJson(String json) {
		SubmitPostCommentResponse submitPostCommentResponse = gson.fromJson(json, SubmitPostCommentResponse.class);
		return submitPostCommentResponse;
	}

	@Override
	public String getApiFun() {
		// TODO Auto-generated method stub
		return "ecspost/submitPostComment.do";
	}

	@Override
	public String getReqMethod() {
		// TODO Auto-generated method stub
		return "post";
	}
}
