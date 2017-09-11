package com.tesu.manicurehouse.protocol;

import com.google.gson.Gson;
import com.tesu.manicurehouse.base.BaseProtocol;
import com.tesu.manicurehouse.response.AddOrCancelLikedPostOrInformationCommentResponse;
import com.tesu.manicurehouse.response.AddPostOrInformationForwardCntResponse;

public class AddPostOrInformationForwardCntProtocol extends BaseProtocol<AddPostOrInformationForwardCntResponse> {
	private Gson gson;

	public AddPostOrInformationForwardCntProtocol() {
		gson = new Gson();
	}


	// 1. 封装请求参数
	// 2. 处理请求返回结果
	@Override
	protected AddPostOrInformationForwardCntResponse parseJson(String json) {
		AddPostOrInformationForwardCntResponse addPostOrInformationForwardCntResponse = gson.fromJson(json, AddPostOrInformationForwardCntResponse.class);
		return addPostOrInformationForwardCntResponse;
	}

	@Override
	public String getApiFun() {
		// TODO Auto-generated method stub
		return "ecspost/addPostOrInformationForwardCnt.do";
	}

	@Override
	public String getReqMethod() {
		// TODO Auto-generated method stub
		return "post";
	}
}
