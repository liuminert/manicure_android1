package com.tesu.manicurehouse.protocol;

import com.google.gson.Gson;
import com.tesu.manicurehouse.base.BaseProtocol;
import com.tesu.manicurehouse.response.GetActListResponse;
import com.tesu.manicurehouse.response.GetCartListResponse;

public class GetCartListProtocol extends BaseProtocol<GetCartListResponse> {
	private Gson gson;

	public GetCartListProtocol() {
		gson = new Gson();
	}


	// 1. 封装请求参数
	// 2. 处理请求返回结果
	@Override
	protected GetCartListResponse parseJson(String json) {
		GetCartListResponse getCartListResponse = gson.fromJson(json, GetCartListResponse.class);
		return getCartListResponse;
	}

	@Override
	public String getApiFun() {
		// TODO Auto-generated method stub
		return "shop/getCartList.do";
	}

	@Override
	public String getReqMethod() {
		// TODO Auto-generated method stub
		return "post";
	}
}
