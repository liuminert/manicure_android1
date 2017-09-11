package com.tesu.manicurehouse.base;

import android.widget.BaseAdapter;

import java.util.List;

/**
 * @作者: 许明达
 * @创建时间: 2016-3-23上午10:26:51
 * @版权: 特速版权所有
 * @描述: adapter的基类
 * @更新人:
 * @更新时间:
 * @更新内容: TODO
 */
public abstract class SuperBaseAdapter<T> extends BaseAdapter {
	protected List<T> mDatas;
	public SuperBaseAdapter(List<T> datas) {
		this.mDatas = datas;
	}
	@Override
	public int getCount() {
		if (mDatas != null) {
			return mDatas.size();
		}
		return 0;
	}

	@Override
	public Object getItem(int position) {
		if (mDatas != null) {
			return mDatas.get(position);
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

}
