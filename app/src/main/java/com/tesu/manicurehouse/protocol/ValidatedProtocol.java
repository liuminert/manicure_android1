package com.tesu.manicurehouse.protocol;

import com.google.gson.Gson;
import com.tesu.manicurehouse.base.BaseProtocol;
import com.tesu.manicurehouse.response.SendVerificationCodeResponse;
import com.tesu.manicurehouse.response.ValidatedResponse;

public class ValidatedProtocol extends BaseProtocol<ValidatedResponse> {
	private Gson gson;

	public ValidatedProtocol() {
		gson = new Gson();
	}


	// 1. 封装请求参数
	// 2. 处理请求返回结果
	@Override
	protected ValidatedResponse parseJson(String json) {
		ValidatedResponse validatedResponse = gson.fromJson(json, ValidatedResponse.class);
		return validatedResponse;
	}

	@Override
	public String getApiFun() {
		// TODO Auto-generated method stub
		return "app/validated.do";
	}

	@Override
	public String getReqMethod() {
		// TODO Auto-generated method stub
		return "post";
	}
}
