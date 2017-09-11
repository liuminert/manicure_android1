package com.tesu.manicurehouse.protocol;

import com.google.gson.Gson;
import com.tesu.manicurehouse.base.BaseProtocol;
import com.tesu.manicurehouse.response.AddPostOrInformationForwardCntResponse;
import com.tesu.manicurehouse.response.AddVideoPlayOrForwardCntResponse;

public class AddVideoPlayOrForwardCntProtocol extends BaseProtocol<AddVideoPlayOrForwardCntResponse> {
	private Gson gson;

	public AddVideoPlayOrForwardCntProtocol() {
		gson = new Gson();
	}


	// 1. 封装请求参数
	// 2. 处理请求返回结果
	@Override
	protected AddVideoPlayOrForwardCntResponse parseJson(String json) {
		AddVideoPlayOrForwardCntResponse addVideoPlayOrForwardCntResponse = gson.fromJson(json, AddVideoPlayOrForwardCntResponse.class);
		return addVideoPlayOrForwardCntResponse;
	}

	@Override
	public String getApiFun() {
		// TODO Auto-generated method stub
		return "video/addVideoPlayOrForwardCnt.do";
	}

	@Override
	public String getReqMethod() {
		// TODO Auto-generated method stub
		return "post";
	}
}
