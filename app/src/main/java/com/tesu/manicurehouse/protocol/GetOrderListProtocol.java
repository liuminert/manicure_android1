package com.tesu.manicurehouse.protocol;

import com.google.gson.Gson;
import com.tesu.manicurehouse.base.BaseProtocol;
import com.tesu.manicurehouse.response.GetOrderListResponse;
import com.tesu.manicurehouse.response.GetRegionListResponse;

public class GetOrderListProtocol extends BaseProtocol<GetOrderListResponse> {
	private Gson gson;

	public GetOrderListProtocol() {
		gson = new Gson();
	}


	// 1. 封装请求参数
	// 2. 处理请求返回结果
	@Override
	protected GetOrderListResponse parseJson(String json) {
		GetOrderListResponse getOrderListResponse = gson.fromJson(json, GetOrderListResponse.class);
		return getOrderListResponse;
	}

	@Override
	public String getApiFun() {
		// TODO Auto-generated method stub
		return "app/getOrderList.do";
	}

	@Override
	public String getReqMethod() {
		// TODO Auto-generated method stub
		return "post";
	}
}
