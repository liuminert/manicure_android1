package com.tesu.manicurehouse.protocol;

import com.google.gson.Gson;
import com.tesu.manicurehouse.base.BaseProtocol;
import com.tesu.manicurehouse.response.AddCartResponse;
import com.tesu.manicurehouse.response.UpdateCartGoodsNumberResponse;

public class UpdateCartGoodsNumberProtocol extends BaseProtocol<UpdateCartGoodsNumberResponse> {
	private Gson gson;

	public UpdateCartGoodsNumberProtocol() {
		gson = new Gson();
	}


	// 1. 封装请求参数
	// 2. 处理请求返回结果
	@Override
	protected UpdateCartGoodsNumberResponse parseJson(String json) {
		UpdateCartGoodsNumberResponse updateCartGoodsNumberResponse = gson.fromJson(json, UpdateCartGoodsNumberResponse.class);
		return updateCartGoodsNumberResponse;
	}

	@Override
	public String getApiFun() {
		// TODO Auto-generated method stub
		return "shop/updateCartGoodsNumber.do";
	}

	@Override
	public String getReqMethod() {
		// TODO Auto-generated method stub
		return "post";
	}
}
