package com.tesu.manicurehouse.protocol;

import com.google.gson.Gson;
import com.tesu.manicurehouse.base.BaseProtocol;
import com.tesu.manicurehouse.response.AddOrUpdateUserAddressResponse;
import com.tesu.manicurehouse.response.LoginResponse;

public class AddOrUpdateUserAddressProtocol extends BaseProtocol<AddOrUpdateUserAddressResponse> {
	private Gson gson;

	public AddOrUpdateUserAddressProtocol() {
		gson = new Gson();
	}


	// 1. 封装请求参数
	// 2. 处理请求返回结果
	@Override
	protected AddOrUpdateUserAddressResponse parseJson(String json) {
		AddOrUpdateUserAddressResponse addOrUpdateUserAddressResponse = gson.fromJson(json, AddOrUpdateUserAddressResponse.class);
		return addOrUpdateUserAddressResponse;
	}

	@Override
	public String getApiFun() {
		// TODO Auto-generated method stub
		return "app/addOrUpdateUserAddress.do";
	}

	@Override
	public String getReqMethod() {
		// TODO Auto-generated method stub
		return "post";
	}
}
