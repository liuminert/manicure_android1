package com.tesu.manicurehouse.protocol;

import com.google.gson.Gson;
import com.tesu.manicurehouse.base.BaseProtocol;
import com.tesu.manicurehouse.response.CancelFollowResponse;
import com.tesu.manicurehouse.response.GetUserCollectionOrWorkListResponse;

public class GetUserCollectionOrWorkListProtocol extends BaseProtocol<GetUserCollectionOrWorkListResponse> {
	private Gson gson;

	public GetUserCollectionOrWorkListProtocol() {
		gson = new Gson();
	}


	// 1. 封装请求参数
	// 2. 处理请求返回结果
	@Override
	protected GetUserCollectionOrWorkListResponse parseJson(String json) {
		GetUserCollectionOrWorkListResponse getUserCollectionOrWorkListResponse = gson.fromJson(json, GetUserCollectionOrWorkListResponse.class);
		return getUserCollectionOrWorkListResponse;
	}

	@Override
	public String getApiFun() {
		// TODO Auto-generated method stub
		return "app/getUserCollectionOrWorkList.do";
	}

	@Override
	public String getReqMethod() {
		// TODO Auto-generated method stub
		return "post";
	}
}
