package com.tesu.manicurehouse.protocol;

import com.google.gson.Gson;
import com.tesu.manicurehouse.base.BaseProtocol;
import com.tesu.manicurehouse.response.LoginResponse;
import com.tesu.manicurehouse.response.UpdatePwdResponse;

public class UpdatePwdProtocol extends BaseProtocol<UpdatePwdResponse> {
	private Gson gson;

	public UpdatePwdProtocol() {
		gson = new Gson();
	}


	// 1. 封装请求参数
	// 2. 处理请求返回结果
	@Override
	protected UpdatePwdResponse parseJson(String json) {
		UpdatePwdResponse updatePwdResponse = gson.fromJson(json, UpdatePwdResponse.class);
		return updatePwdResponse;
	}

	@Override
	public String getApiFun() {
		// TODO Auto-generated method stub
		return "app/updatePwd.do";
	}

	@Override
	public String getReqMethod() {
		// TODO Auto-generated method stub
		return "post";
	}
}
