package com.tesu.manicurehouse.protocol;

import com.google.gson.Gson;
import com.tesu.manicurehouse.base.BaseProtocol;
import com.tesu.manicurehouse.response.AddCartResponse;
import com.tesu.manicurehouse.response.ProblemFeedbackResponse;

public class ProblemFeedbackProtocol extends BaseProtocol<ProblemFeedbackResponse> {
	private Gson gson;

	public ProblemFeedbackProtocol() {
		gson = new Gson();
	}


	// 1. 封装请求参数
	// 2. 处理请求返回结果
	@Override
	protected ProblemFeedbackResponse parseJson(String json) {
		ProblemFeedbackResponse problemFeedbackResponse = gson.fromJson(json, ProblemFeedbackResponse.class);
		return problemFeedbackResponse;
	}

	@Override
	public String getApiFun() {
		// TODO Auto-generated method stub
		return "app/problemFeedback.do";
	}

	@Override
	public String getReqMethod() {
		// TODO Auto-generated method stub
		return "post";
	}
}
