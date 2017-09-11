package com.tesu.manicurehouse.protocol;

import com.google.gson.Gson;
import com.tesu.manicurehouse.base.BaseProtocol;
import com.tesu.manicurehouse.response.GetAdsResponse;
import com.tesu.manicurehouse.response.GetGoodDescResponse;

public class GetAdsProtocol extends BaseProtocol<GetAdsResponse> {
	private Gson gson;

	public GetAdsProtocol() {
		gson = new Gson();
	}


	// 1. 封装请求参数
	// 2. 处理请求返回结果
	@Override
	protected GetAdsResponse parseJson(String json) {
		GetAdsResponse getAdsResponse = gson.fromJson(json, GetAdsResponse.class);
		return getAdsResponse;
	}

	@Override
	public String getApiFun() {
		// TODO Auto-generated method stub
		return "shop/getAds.do";
	}

	@Override
	public String getReqMethod() {
		// TODO Auto-generated method stub
		return "post";
	}
}
