package com.tesu.manicurehouse.protocol;

import com.google.gson.Gson;
import com.tesu.manicurehouse.base.BaseProtocol;
import com.tesu.manicurehouse.response.AddCartResponse;
import com.tesu.manicurehouse.response.AddPostResponse;

public class AddCartProtocol extends BaseProtocol<AddCartResponse> {
	private Gson gson;

	public AddCartProtocol() {
		gson = new Gson();
	}


	// 1. 封装请求参数
	// 2. 处理请求返回结果
	@Override
	protected AddCartResponse parseJson(String json) {
		AddCartResponse addCartResponse = gson.fromJson(json, AddCartResponse.class);
		return addCartResponse;
	}

	@Override
	public String getApiFun() {
		// TODO Auto-generated method stub
		return "shop/addCart.do";
	}

	@Override
	public String getReqMethod() {
		// TODO Auto-generated method stub
		return "post";
	}
}
