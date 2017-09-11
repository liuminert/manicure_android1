package com.tesu.manicurehouse.protocol;

import com.google.gson.Gson;
import com.tesu.manicurehouse.base.BaseProtocol;
import com.tesu.manicurehouse.response.GetInformationCommentListResponse;
import com.tesu.manicurehouse.response.GetVideoCommentListResponse;

public class GetVideoCommentListProtocol extends BaseProtocol<GetVideoCommentListResponse> {
	private Gson gson;

	public GetVideoCommentListProtocol() {
		gson = new Gson();
	}


	// 1. 封装请求参数
	// 2. 处理请求返回结果
	@Override
	protected GetVideoCommentListResponse parseJson(String json) {
		GetVideoCommentListResponse getVideoCommentListResponse = gson.fromJson(json, GetVideoCommentListResponse.class);
		return getVideoCommentListResponse;
	}

	@Override
	public String getApiFun() {
		// TODO Auto-generated method stub
		return "video/getVideoCommentList.do";
	}

	@Override
	public String getReqMethod() {
		// TODO Auto-generated method stub
		return "post";
	}
}
