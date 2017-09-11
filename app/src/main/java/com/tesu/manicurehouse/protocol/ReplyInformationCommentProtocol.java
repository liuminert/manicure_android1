package com.tesu.manicurehouse.protocol;

import com.google.gson.Gson;
import com.tesu.manicurehouse.base.BaseProtocol;
import com.tesu.manicurehouse.response.AddOrCancelLikedPostOrInformationResponse;
import com.tesu.manicurehouse.response.ReplyInformationCommentResponse;

public class ReplyInformationCommentProtocol extends BaseProtocol<ReplyInformationCommentResponse> {
	private Gson gson;

	public ReplyInformationCommentProtocol() {
		gson = new Gson();
	}


	// 1. 封装请求参数
	// 2. 处理请求返回结果
	@Override
	protected ReplyInformationCommentResponse parseJson(String json) {
		ReplyInformationCommentResponse replyInformationCommentResponse = gson.fromJson(json, ReplyInformationCommentResponse.class);
		return replyInformationCommentResponse;
	}

	@Override
	public String getApiFun() {
		// TODO Auto-generated method stub
		return "ecsinfo/replyInformationComment.do";
	}

	@Override
	public String getReqMethod() {
		// TODO Auto-generated method stub
		return "post";
	}
}
