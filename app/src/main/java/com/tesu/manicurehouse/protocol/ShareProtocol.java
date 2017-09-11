package com.tesu.manicurehouse.protocol;

import com.google.gson.Gson;
import com.tesu.manicurehouse.base.BaseProtocol;
import com.tesu.manicurehouse.response.AddCartResponse;
import com.tesu.manicurehouse.response.ShareResponse;

public class ShareProtocol extends BaseProtocol<ShareResponse> {
	private Gson gson;

	public ShareProtocol() {
		gson = new Gson();
	}


	// 1. 封装请求参数
	// 2. 处理请求返回结果
	@Override
	protected ShareResponse parseJson(String json) {
		ShareResponse shareResponse = gson.fromJson(json, ShareResponse.class);
		return shareResponse;
	}

	@Override
	public String getApiFun() {
		// TODO Auto-generated method stub
		return "app/share.do";
	}

	@Override
	public String getReqMethod() {
		// TODO Auto-generated method stub
		return "post";
	}
}
