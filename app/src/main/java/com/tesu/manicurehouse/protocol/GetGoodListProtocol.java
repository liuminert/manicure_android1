package com.tesu.manicurehouse.protocol;

import com.google.gson.Gson;
import com.tesu.manicurehouse.base.BaseProtocol;
import com.tesu.manicurehouse.response.GetGoodListResponse;
import com.tesu.manicurehouse.response.GetRegionListResponse;

public class GetGoodListProtocol extends BaseProtocol<GetGoodListResponse> {
	private Gson gson;

	public GetGoodListProtocol() {
		gson = new Gson();
	}


	// 1. 封装请求参数
	// 2. 处理请求返回结果
	@Override
	protected GetGoodListResponse parseJson(String json) {
		GetGoodListResponse getGoodListResponse = gson.fromJson(json, GetGoodListResponse.class);
		return getGoodListResponse;
	}

	@Override
	public String getApiFun() {
		// TODO Auto-generated method stub
		return "shop/getGoodList.do";
	}

	@Override
	public String getReqMethod() {
		// TODO Auto-generated method stub
		return "post";
	}
}
