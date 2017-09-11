package com.tesu.manicurehouse.callback;


import android.graphics.Bitmap;

import java.util.HashMap;
import java.util.List;

/**
 *
 * @update 2016-7-20 上午11:12:18
 * @version V1.0
 */
public interface OnCallBackContentList {

	public void ContentListCallBack(String s,int pos);

	public void delContentList(int pos);

	public void ListCallBack(int pos);

	public void ImageDelCallBack(int par,int pos);
}
