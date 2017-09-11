package com.tesu.manicurehouse.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

public class MyShareImageView extends ImageView{

	public boolean isclicked;
	public MyShareImageView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public MyShareImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public MyShareImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public boolean isIsclicked() {
		return isclicked;
	}

	public void setIsclicked(boolean isclicked) {
		this.isclicked = isclicked;
	}


}
