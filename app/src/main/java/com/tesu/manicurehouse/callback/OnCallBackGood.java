package com.tesu.manicurehouse.callback;


import com.tesu.manicurehouse.bean.CartBean;
import com.tesu.manicurehouse.bean.GoodsAttrBean;

import java.util.List;

/**
 *
 * @update 2016-7-20 上午11:12:18
 * @version V1.0
 */
public interface OnCallBackGood {

	public void delCallBack(int par, GoodsAttrBean cartBean);
	public void delSelectItemCallBack(GoodsAttrBean cartBean);
	public void addSelectItemCallBack(GoodsAttrBean cartBean);
	public void changeSelectItemCallBack(List<GoodsAttrBean> cartBeanList);
}
