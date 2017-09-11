package com.tesu.manicurehouse.protocol;

import com.google.gson.Gson;
import com.tesu.manicurehouse.base.BaseProtocol;
import com.tesu.manicurehouse.response.AddCartResponse;
import com.tesu.manicurehouse.response.SearchGoodsResponse;

public class SearchGoodsProtocol extends BaseProtocol<SearchGoodsResponse> {
	private Gson gson;

	public SearchGoodsProtocol() {
		gson = new Gson();
	}


	// 1. 封装请求参数
	// 2. 处理请求返回结果
	@Override
	protected SearchGoodsResponse parseJson(String json) {
		SearchGoodsResponse searchGoodsResponse = gson.fromJson(json, SearchGoodsResponse.class);
		return searchGoodsResponse;
	}

	@Override
	public String getApiFun() {
		// TODO Auto-generated method stub
		return "shop/searchGoods.do";
	}

	@Override
	public String getReqMethod() {
		// TODO Auto-generated method stub
		return "post";
	}
}
