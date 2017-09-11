package com.tesu.manicurehouse.protocol;

import com.google.gson.Gson;
import com.tesu.manicurehouse.base.BaseProtocol;
import com.tesu.manicurehouse.response.SubmitInformationCommentResponse;
import com.tesu.manicurehouse.response.SubmitVideoCommentResponse;

public class SubmitVideoCommentProtocol extends BaseProtocol<SubmitVideoCommentResponse> {
	private Gson gson;

	public SubmitVideoCommentProtocol() {
		gson = new Gson();
	}


	// 1. 封装请求参数
	// 2. 处理请求返回结果
	@Override
	protected SubmitVideoCommentResponse parseJson(String json) {
		SubmitVideoCommentResponse submitVideoCommentResponse = gson.fromJson(json, SubmitVideoCommentResponse.class);
		return submitVideoCommentResponse;
	}

	@Override
	public String getApiFun() {
		// TODO Auto-generated method stub
		return "video/submitVideoComment.do";
	}

	@Override
	public String getReqMethod() {
		// TODO Auto-generated method stub
		return "post";
	}
}
