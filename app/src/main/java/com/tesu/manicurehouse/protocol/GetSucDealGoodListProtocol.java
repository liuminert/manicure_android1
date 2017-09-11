package com.tesu.manicurehouse.protocol;

import com.google.gson.Gson;
import com.tesu.manicurehouse.base.BaseProtocol;
import com.tesu.manicurehouse.response.GetGoodAttrListResponse;
import com.tesu.manicurehouse.response.GetSucDealGoodListResponse;

public class GetSucDealGoodListProtocol extends BaseProtocol<GetSucDealGoodListResponse> {
	private Gson gson;

	public GetSucDealGoodListProtocol() {
		gson = new Gson();
	}


	// 1. 封装请求参数
	// 2. 处理请求返回结果
	@Override
	protected GetSucDealGoodListResponse parseJson(String json) {
		GetSucDealGoodListResponse getSucDealGoodListResponse = gson.fromJson(json, GetSucDealGoodListResponse.class);
		return getSucDealGoodListResponse;
	}

	@Override
	public String getApiFun() {
		// TODO Auto-generated method stub
		return "shop/getSucDealGoodList.do";
	}

	@Override
	public String getReqMethod() {
		// TODO Auto-generated method stub
		return "post";
	}
}
