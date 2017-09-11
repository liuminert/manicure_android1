package com.tesu.manicurehouse.protocol;

import com.google.gson.Gson;
import com.tesu.manicurehouse.base.BaseProtocol;
import com.tesu.manicurehouse.response.SearchGoodsResponse;
import com.tesu.manicurehouse.response.SearchVideoResponse;

public class SearchVideoProtocol extends BaseProtocol<SearchVideoResponse> {
	private Gson gson;

	public SearchVideoProtocol() {
		gson = new Gson();
	}


	// 1. 封装请求参数
	// 2. 处理请求返回结果
	@Override
	protected SearchVideoResponse parseJson(String json) {
		SearchVideoResponse searchVideoResponse = gson.fromJson(json, SearchVideoResponse.class);
		return searchVideoResponse;
	}

	@Override
	public String getApiFun() {
		// TODO Auto-generated method stub
		return "video/searchVideo.do";
	}

	@Override
	public String getReqMethod() {
		// TODO Auto-generated method stub
		return "post";
	}
}
