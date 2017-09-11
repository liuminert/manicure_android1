package com.tesu.manicurehouse.protocol;

import com.google.gson.Gson;
import com.tesu.manicurehouse.base.BaseProtocol;
import com.tesu.manicurehouse.response.AddOrCancelLikedPostOrInformationResponse;
import com.tesu.manicurehouse.response.AddOrUpdateUserAddressResponse;

public class AddOrCancelLikedPostOrInformationProtocol extends BaseProtocol<AddOrCancelLikedPostOrInformationResponse> {
	private Gson gson;

	public AddOrCancelLikedPostOrInformationProtocol() {
		gson = new Gson();
	}


	// 1. 封装请求参数
	// 2. 处理请求返回结果
	@Override
	protected AddOrCancelLikedPostOrInformationResponse parseJson(String json) {
		AddOrCancelLikedPostOrInformationResponse addOrCancelLikedPostOrInformationResponse = gson.fromJson(json, AddOrCancelLikedPostOrInformationResponse.class);
		return addOrCancelLikedPostOrInformationResponse;
	}

	@Override
	public String getApiFun() {
		// TODO Auto-generated method stub
		return "ecspost/addOrCancelLikedPostOrInformation.do";
	}

	@Override
	public String getReqMethod() {
		// TODO Auto-generated method stub
		return "post";
	}
}
