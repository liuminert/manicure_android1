package com.tesu.manicurehouse.protocol;

import com.google.gson.Gson;
import com.tesu.manicurehouse.base.BaseProtocol;
import com.tesu.manicurehouse.response.AddCartResponse;
import com.tesu.manicurehouse.response.GetKeywordsResponse;

public class GetKeywordsProtocol extends BaseProtocol<GetKeywordsResponse> {
	private Gson gson;

	public GetKeywordsProtocol() {
		gson = new Gson();
	}


	// 1. 封装请求参数
	// 2. 处理请求返回结果
	@Override
	protected GetKeywordsResponse parseJson(String json) {
		GetKeywordsResponse getKeywordsResponse = gson.fromJson(json, GetKeywordsResponse.class);
		return getKeywordsResponse;
	}

	@Override
	public String getApiFun() {
		// TODO Auto-generated method stub
		return "app/getKeywords.do";
	}

	@Override
	public String getReqMethod() {
		// TODO Auto-generated method stub
		return "post";
	}
}
