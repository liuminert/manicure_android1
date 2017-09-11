package com.tesu.manicurehouse.protocol;

import com.google.gson.Gson;
import com.tesu.manicurehouse.base.BaseProtocol;
import com.tesu.manicurehouse.response.ReplyInformationCommentResponse;
import com.tesu.manicurehouse.response.ReplyVideoCommentResponse;

public class ReplyVideoCommentProtocol extends BaseProtocol<ReplyVideoCommentResponse> {
	private Gson gson;

	public ReplyVideoCommentProtocol() {
		gson = new Gson();
	}


	// 1. 封装请求参数
	// 2. 处理请求返回结果
	@Override
	protected ReplyVideoCommentResponse parseJson(String json) {
		ReplyVideoCommentResponse replyVideoCommentResponse = gson.fromJson(json, ReplyVideoCommentResponse.class);
		return replyVideoCommentResponse;
	}

	@Override
	public String getApiFun() {
		// TODO Auto-generated method stub
		return "video/replyVideoComment.do";
	}

	@Override
	public String getReqMethod() {
		// TODO Auto-generated method stub
		return "post";
	}
}
