package com.tesu.manicurehouse.protocol;

import com.google.gson.Gson;
import com.tesu.manicurehouse.base.BaseProtocol;
import com.tesu.manicurehouse.response.GetUserAddressListResponse;
import com.tesu.manicurehouse.response.UpdatePwdResponse;

public class GetUserAddressListProtocol extends BaseProtocol<GetUserAddressListResponse> {
	private Gson gson;

	public GetUserAddressListProtocol() {
		gson = new Gson();
	}


	// 1. 封装请求参数
	// 2. 处理请求返回结果
	@Override
	protected GetUserAddressListResponse parseJson(String json) {
		GetUserAddressListResponse getUserAddressListResponse = gson.fromJson(json, GetUserAddressListResponse.class);
		return getUserAddressListResponse;
	}

	@Override
	public String getApiFun() {
		// TODO Auto-generated method stub
		return "app/getUserAddressList.do";
	}

	@Override
	public String getReqMethod() {
		// TODO Auto-generated method stub
		return "post";
	}
}
