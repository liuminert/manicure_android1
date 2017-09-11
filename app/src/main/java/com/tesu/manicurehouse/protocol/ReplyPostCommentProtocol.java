package com.tesu.manicurehouse.protocol;

import com.google.gson.Gson;
import com.tesu.manicurehouse.base.BaseProtocol;
import com.tesu.manicurehouse.response.ReplyInformationCommentResponse;
import com.tesu.manicurehouse.response.ReplyPostCommentResponse;

public class ReplyPostCommentProtocol extends BaseProtocol<ReplyPostCommentResponse> {
	private Gson gson;

	public ReplyPostCommentProtocol() {
		gson = new Gson();
	}


	// 1. 封装请求参数
	// 2. 处理请求返回结果
	@Override
	protected ReplyPostCommentResponse parseJson(String json) {
		ReplyPostCommentResponse replyPostCommentResponse = gson.fromJson(json, ReplyPostCommentResponse.class);
		return replyPostCommentResponse;
	}

	@Override
	public String getApiFun() {
		// TODO Auto-generated method stub
		return "ecspost/replyPostComment.do";
	}

	@Override
	public String getReqMethod() {
		// TODO Auto-generated method stub
		return "post";
	}
}
