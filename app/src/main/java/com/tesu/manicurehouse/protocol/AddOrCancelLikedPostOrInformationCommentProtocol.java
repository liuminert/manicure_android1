package com.tesu.manicurehouse.protocol;

import com.google.gson.Gson;
import com.tesu.manicurehouse.base.BaseProtocol;
import com.tesu.manicurehouse.response.AddOrCancelLikedPostOrInformationCommentResponse;
import com.tesu.manicurehouse.response.AddOrCancelLikedPostOrInformationResponse;

public class AddOrCancelLikedPostOrInformationCommentProtocol extends BaseProtocol<AddOrCancelLikedPostOrInformationCommentResponse> {
	private Gson gson;

	public AddOrCancelLikedPostOrInformationCommentProtocol() {
		gson = new Gson();
	}


	// 1. 封装请求参数
	// 2. 处理请求返回结果
	@Override
	protected AddOrCancelLikedPostOrInformationCommentResponse parseJson(String json) {
		AddOrCancelLikedPostOrInformationCommentResponse addOrCancelLikedPostOrInformationCommentResponse = gson.fromJson(json, AddOrCancelLikedPostOrInformationCommentResponse.class);
		return addOrCancelLikedPostOrInformationCommentResponse;
	}

	@Override
	public String getApiFun() {
		// TODO Auto-generated method stub
		return "ecspost/addOrCancelPostOrInformationComment.do";
	}

	@Override
	public String getReqMethod() {
		// TODO Auto-generated method stub
		return "post";
	}
}
