package com.tesu.manicurehouse.protocol;

import com.google.gson.Gson;
import com.tesu.manicurehouse.base.BaseProtocol;
import com.tesu.manicurehouse.response.GetFollowInfoResponse;
import com.tesu.manicurehouse.response.GetVideoCategoryListResponse;

public class GetVideoCategoryListProtocol extends BaseProtocol<GetVideoCategoryListResponse> {
	private Gson gson;

	public GetVideoCategoryListProtocol() {
		gson = new Gson();
	}


	// 1. 封装请求参数
	// 2. 处理请求返回结果
	@Override
	protected GetVideoCategoryListResponse parseJson(String json) {
		GetVideoCategoryListResponse getVideoCategoryListResponse = gson.fromJson(json, GetVideoCategoryListResponse.class);
		return getVideoCategoryListResponse;
	}

	@Override
	public String getApiFun() {
		// TODO Auto-generated method stub
		return "video/getVideoCategoryList.do";
	}

	@Override
	public String getReqMethod() {
		// TODO Auto-generated method stub
		return "post";
	}
}
