package com.tesu.manicurehouse.protocol;

import com.google.gson.Gson;
import com.tesu.manicurehouse.base.BaseProtocol;
import com.tesu.manicurehouse.response.AddCartResponse;
import com.tesu.manicurehouse.response.AddFollowResponse;

public class AddFollowProtocol extends BaseProtocol<AddFollowResponse> {
	private Gson gson;

	public AddFollowProtocol() {
		gson = new Gson();
	}


	// 1. 封装请求参数
	// 2. 处理请求返回结果
	@Override
	protected AddFollowResponse parseJson(String json) {
		AddFollowResponse addFollowResponse = gson.fromJson(json, AddFollowResponse.class);
		return addFollowResponse;
	}

	@Override
	public String getApiFun() {
		// TODO Auto-generated method stub
		return "app/addFollow.do";
	}

	@Override
	public String getReqMethod() {
		// TODO Auto-generated method stub
		return "post";
	}
}
