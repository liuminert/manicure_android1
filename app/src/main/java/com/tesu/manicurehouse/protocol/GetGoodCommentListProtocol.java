package com.tesu.manicurehouse.protocol;

import com.google.gson.Gson;
import com.tesu.manicurehouse.base.BaseProtocol;
import com.tesu.manicurehouse.response.GetGoodCommentListResponse;
import com.tesu.manicurehouse.response.GetGoodListResponse;

public class GetGoodCommentListProtocol extends BaseProtocol<GetGoodCommentListResponse> {
	private Gson gson;

	public GetGoodCommentListProtocol() {
		gson = new Gson();
	}


	// 1. 封装请求参数
	// 2. 处理请求返回结果
	@Override
	protected GetGoodCommentListResponse parseJson(String json) {
		GetGoodCommentListResponse getGoodCommentListResponse = gson.fromJson(json, GetGoodCommentListResponse.class);
		return getGoodCommentListResponse;
	}

	@Override
	public String getApiFun() {
		// TODO Auto-generated method stub
		return "shop/getGoodCommentList.do";
	}

	@Override
	public String getReqMethod() {
		// TODO Auto-generated method stub
		return "post";
	}
}
