package com.tesu.manicurehouse.protocol;

import com.google.gson.Gson;
import com.tesu.manicurehouse.base.BaseProtocol;
import com.tesu.manicurehouse.response.GetActListResponse;
import com.tesu.manicurehouse.response.GetDesignListResponse;

public class GetDesignListProtocol extends BaseProtocol<GetDesignListResponse> {
	private Gson gson;

	public GetDesignListProtocol() {
		gson = new Gson();
	}


	// 1. 封装请求参数
	// 2. 处理请求返回结果
	@Override
	protected GetDesignListResponse parseJson(String json) {
		GetDesignListResponse getDesignListResponse = gson.fromJson(json, GetDesignListResponse.class);
		return getDesignListResponse;
	}

	@Override
	public String getApiFun() {
		// TODO Auto-generated method stub
		return "app/getDesignList.do";
	}

	@Override
	public String getReqMethod() {
		// TODO Auto-generated method stub
		return "post";
	}
}
