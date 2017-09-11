package com.tesu.manicurehouse.protocol;

import com.google.gson.Gson;
import com.tesu.manicurehouse.base.BaseProtocol;
import com.tesu.manicurehouse.response.GetRegionListResponse;
import com.tesu.manicurehouse.response.GetVideoListResponse;

public class GetVideoListProtocol extends BaseProtocol<GetVideoListResponse> {
	private Gson gson;

	public GetVideoListProtocol() {
		gson = new Gson();
	}


	// 1. 封装请求参数
	// 2. 处理请求返回结果
	@Override
	protected GetVideoListResponse parseJson(String json) {
		GetVideoListResponse getVideoListResponse = gson.fromJson(json, GetVideoListResponse.class);
		return getVideoListResponse;
	}

	@Override
	public String getApiFun() {
		// TODO Auto-generated method stub
		return "video/getVideoList.do";
	}

	@Override
	public String getReqMethod() {
		// TODO Auto-generated method stub
		return "post";
	}
}
