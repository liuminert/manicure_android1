package com.tesu.manicurehouse.protocol;

import com.google.gson.Gson;
import com.tesu.manicurehouse.base.BaseProtocol;
import com.tesu.manicurehouse.response.AddCartResponse;
import com.tesu.manicurehouse.response.UpdateOrderStatusResponse;

public class UpdateOrderStatusProtocol extends BaseProtocol<UpdateOrderStatusResponse> {
	private Gson gson;

	public UpdateOrderStatusProtocol() {
		gson = new Gson();
	}


	// 1. 封装请求参数
	// 2. 处理请求返回结果
	@Override
	protected UpdateOrderStatusResponse parseJson(String json) {
		UpdateOrderStatusResponse updateOrderStatusResponse = gson.fromJson(json, UpdateOrderStatusResponse.class);
		return updateOrderStatusResponse;
	}

	@Override
	public String getApiFun() {
		// TODO Auto-generated method stub
		return "shop/updateOrderStatus.do";
	}

	@Override
	public String getReqMethod() {
		// TODO Auto-generated method stub
		return "post";
	}
}
