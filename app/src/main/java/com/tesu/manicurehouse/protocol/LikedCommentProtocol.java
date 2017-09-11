package com.tesu.manicurehouse.protocol;

import com.google.gson.Gson;
import com.tesu.manicurehouse.base.BaseProtocol;
import com.tesu.manicurehouse.response.AddCartResponse;
import com.tesu.manicurehouse.response.LikedCommentResponse;

public class LikedCommentProtocol extends BaseProtocol<LikedCommentResponse> {
	private Gson gson;

	public LikedCommentProtocol() {
		gson = new Gson();
	}


	// 1. 封装请求参数
	// 2. 处理请求返回结果
	@Override
	protected LikedCommentResponse parseJson(String json) {
		LikedCommentResponse likedCommentResponse = gson.fromJson(json, LikedCommentResponse.class);
		return likedCommentResponse;
	}

	@Override
	public String getApiFun() {
		// TODO Auto-generated method stub
		return "video/likedComment.do";
	}

	@Override
	public String getReqMethod() {
		// TODO Auto-generated method stub
		return "post";
	}
}
