package com.tesu.manicurehouse.protocol;

import com.google.gson.Gson;
import com.tesu.manicurehouse.base.BaseProtocol;
import com.tesu.manicurehouse.response.GetActListResponse;
import com.tesu.manicurehouse.response.GetBackgroupPicResponse;

public class GetBackgroupPicProtocol extends BaseProtocol<GetBackgroupPicResponse> {
	private Gson gson;

	public GetBackgroupPicProtocol() {
		gson = new Gson();
	}


	// 1. 封装请求参数
	// 2. 处理请求返回结果
	@Override
	protected GetBackgroupPicResponse parseJson(String json) {
		GetBackgroupPicResponse getBackgroupPicResponse = gson.fromJson(json, GetBackgroupPicResponse.class);
		return getBackgroupPicResponse;
	}

	@Override
	public String getApiFun() {
		// TODO Auto-generated method stub
		return "pic/getBackgroupPic.do";
	}

	@Override
	public String getReqMethod() {
		// TODO Auto-generated method stub
		return "post";
	}
}
