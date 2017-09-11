package com.tesu.manicurehouse.callback;


import com.tesu.manicurehouse.bean.CartBean;

import java.util.List;

/**
 *
 * @update 2016-7-20 上午11:12:18
 * @version V1.0
 */
public interface OnCallBackDeleteShopingCart {

	public void delCallBack(int par,CartBean cartBean);
	public void delSelectItemCallBack(CartBean cartBean);
	public void addSelectItemCallBack(CartBean cartBean);
	public void changeSelectItemCallBack(List<CartBean> cartBeanList);
}
