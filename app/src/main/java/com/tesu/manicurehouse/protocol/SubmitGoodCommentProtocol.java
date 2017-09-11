package com.tesu.manicurehouse.protocol;

import com.google.gson.Gson;
import com.tesu.manicurehouse.base.BaseProtocol;
import com.tesu.manicurehouse.response.SubmitGoodCommentResponse;
import com.tesu.manicurehouse.response.SubmitPostCommentResponse;

public class SubmitGoodCommentProtocol extends BaseProtocol<SubmitGoodCommentResponse> {
	private Gson gson;

	public SubmitGoodCommentProtocol() {
		gson = new Gson();
	}


	// 1. 封装请求参数
	// 2. 处理请求返回结果
	@Override
	protected SubmitGoodCommentResponse parseJson(String json) {
		SubmitGoodCommentResponse submitGoodCommentResponse = gson.fromJson(json, SubmitGoodCommentResponse.class);
		return submitGoodCommentResponse;
	}

	@Override
	public String getApiFun() {
		// TODO Auto-generated method stub
		return "shop/submitGoodComment.do";
	}

	@Override
	public String getReqMethod() {
		// TODO Auto-generated method stub
		return "post";
	}
}
