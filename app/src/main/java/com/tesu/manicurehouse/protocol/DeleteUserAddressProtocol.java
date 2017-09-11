package com.tesu.manicurehouse.protocol;

import com.google.gson.Gson;
import com.tesu.manicurehouse.base.BaseProtocol;
import com.tesu.manicurehouse.response.CancelFollowResponse;
import com.tesu.manicurehouse.response.DeleteUserAddressResponse;

public class DeleteUserAddressProtocol extends BaseProtocol<DeleteUserAddressResponse> {
	private Gson gson;

	public DeleteUserAddressProtocol() {
		gson = new Gson();
	}


	// 1. 封装请求参数
	// 2. 处理请求返回结果
	@Override
	protected DeleteUserAddressResponse parseJson(String json) {
		DeleteUserAddressResponse deleteUserAddressResponse = gson.fromJson(json, DeleteUserAddressResponse.class);
		return deleteUserAddressResponse;
	}

	@Override
	public String getApiFun() {
		// TODO Auto-generated method stub
		return "app/deleteUserAddress.do";
	}

	@Override
	public String getReqMethod() {
		// TODO Auto-generated method stub
		return "post";
	}
}
