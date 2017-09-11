package com.tesu.manicurehouse.protocol;

import com.google.gson.Gson;
import com.tesu.manicurehouse.base.BaseProtocol;
import com.tesu.manicurehouse.response.SearchCommunityResponse;
import com.tesu.manicurehouse.response.SearchGoodsResponse;

public class SearchCommunityProtocol extends BaseProtocol<SearchCommunityResponse> {
	private Gson gson;

	public SearchCommunityProtocol() {
		gson = new Gson();
	}


	// 1. 封装请求参数
	// 2. 处理请求返回结果
	@Override
	protected SearchCommunityResponse parseJson(String json) {
		SearchCommunityResponse searchCommunityResponse = gson.fromJson(json, SearchCommunityResponse.class);
		return searchCommunityResponse;
	}

	@Override
	public String getApiFun() {
		// TODO Auto-generated method stub
		return "app/searchCommunity.do";
	}

	@Override
	public String getReqMethod() {
		// TODO Auto-generated method stub
		return "post";
	}
}
