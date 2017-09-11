package com.tesu.manicurehouse.protocol;

import com.google.gson.Gson;
import com.tesu.manicurehouse.base.BaseProtocol;
import com.tesu.manicurehouse.response.GetRegionListResponse;
import com.tesu.manicurehouse.response.GetUserAddressListResponse;

public class GetRegionListProtocol extends BaseProtocol<GetRegionListResponse> {
	private Gson gson;

	public GetRegionListProtocol() {
		gson = new Gson();
	}


	// 1. 封装请求参数
	// 2. 处理请求返回结果
	@Override
	protected GetRegionListResponse parseJson(String json) {
		GetRegionListResponse getRegionListResponse = gson.fromJson(json, GetRegionListResponse.class);
		return getRegionListResponse;
	}

	@Override
	public String getApiFun() {
		// TODO Auto-generated method stub
		return "app/getRegionList.do";
	}

	@Override
	public String getReqMethod() {
		// TODO Auto-generated method stub
		return "post";
	}
}
