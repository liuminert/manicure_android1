package com.tesu.manicurehouse.protocol;

import com.google.gson.Gson;
import com.tesu.manicurehouse.base.BaseProtocol;
import com.tesu.manicurehouse.response.DeleteCartResponse;
import com.tesu.manicurehouse.response.DeleteUserAddressResponse;

public class DeleteCartProtocol extends BaseProtocol<DeleteCartResponse> {
	private Gson gson;

	public DeleteCartProtocol() {
		gson = new Gson();
	}


	// 1. 封装请求参数
	// 2. 处理请求返回结果
	@Override
	protected DeleteCartResponse parseJson(String json) {
		DeleteCartResponse deleteCartResponse = gson.fromJson(json, DeleteCartResponse.class);
		return deleteCartResponse;
	}

	@Override
	public String getApiFun() {
		// TODO Auto-generated method stub
		return "app/deleteCart.do";
	}

	@Override
	public String getReqMethod() {
		// TODO Auto-generated method stub
		return "post";
	}
}
