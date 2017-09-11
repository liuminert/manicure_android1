package com.tesu.manicurehouse.protocol;

import com.google.gson.Gson;
import com.tesu.manicurehouse.base.BaseProtocol;
import com.tesu.manicurehouse.response.AddOrCancelLikedPostOrInformationCommentResponse;
import com.tesu.manicurehouse.response.AddPostResponse;

public class AddPostProtocol extends BaseProtocol<AddPostResponse> {
	private Gson gson;

	public AddPostProtocol() {
		gson = new Gson();
	}


	// 1. 封装请求参数
	// 2. 处理请求返回结果
	@Override
	protected AddPostResponse parseJson(String json) {
		AddPostResponse addPostResponse = gson.fromJson(json, AddPostResponse.class);
		return addPostResponse;
	}

	@Override
	public String getApiFun() {
		// TODO Auto-generated method stub
		return "ecspost/addPost.do";
	}

	@Override
	public String getReqMethod() {
		// TODO Auto-generated method stub
		return "post";
	}
}
