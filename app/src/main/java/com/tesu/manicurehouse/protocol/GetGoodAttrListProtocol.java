package com.tesu.manicurehouse.protocol;

import com.google.gson.Gson;
import com.tesu.manicurehouse.base.BaseProtocol;
import com.tesu.manicurehouse.response.GetActListResponse;
import com.tesu.manicurehouse.response.GetGoodAttrListResponse;

public class GetGoodAttrListProtocol extends BaseProtocol<GetGoodAttrListResponse> {
	private Gson gson;

	public GetGoodAttrListProtocol() {
		gson = new Gson();
	}


	// 1. 封装请求参数
	// 2. 处理请求返回结果
	@Override
	protected GetGoodAttrListResponse parseJson(String json) {
		GetGoodAttrListResponse getGoodAttrListResponse = gson.fromJson(json, GetGoodAttrListResponse.class);
		return getGoodAttrListResponse;
	}

	@Override
	public String getApiFun() {
		// TODO Auto-generated method stub
		return "shop/getGoodAttrList.do";
	}

	@Override
	public String getReqMethod() {
		// TODO Auto-generated method stub
		return "post";
	}
}
