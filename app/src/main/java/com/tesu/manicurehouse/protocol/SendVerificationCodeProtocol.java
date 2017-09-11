package com.tesu.manicurehouse.protocol;

import com.google.gson.Gson;
import com.tesu.manicurehouse.base.BaseProtocol;
import com.tesu.manicurehouse.response.SendVerificationCodeResponse;

public class SendVerificationCodeProtocol extends BaseProtocol<SendVerificationCodeResponse> {
	private Gson gson;

	public SendVerificationCodeProtocol() {
		gson = new Gson();
	}


	// 1. 封装请求参数
	// 2. 处理请求返回结果
	@Override
	protected SendVerificationCodeResponse parseJson(String json) {
		SendVerificationCodeResponse sendVerificationCodeResponse = gson.fromJson(json, SendVerificationCodeResponse.class);
		return sendVerificationCodeResponse;
	}

	@Override
	public String getApiFun() {
		// TODO Auto-generated method stub
		return "app/sendVerificationCode.do";
	}

	@Override
	public String getReqMethod() {
		// TODO Auto-generated method stub
		return "post";
	}
}
