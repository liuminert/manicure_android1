package com.tesu.manicurehouse.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;


public class MyLinearLayout extends LinearLayout {
    private DragLayout mDragLayout;
    private Drawable mShadowDrawable;

    public MyLinearLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public MyLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyLinearLayout(Context context) {
        super(context);
    }

    public void setDragLayout(DragLayout mDragLayout) {
        this.mDragLayout = mDragLayout;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (mDragLayout == null || mDragLayout.getStatus() == DragLayout.Status.Close) {
            // 如果mDragLayout是空，或者是关闭状态，按照原本的方法处理
            return super.onInterceptTouchEvent(ev);
        } else {
            return true;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mDragLayout == null || mDragLayout.getStatus() == DragLayout.Status.Close) {
            // 如果mDragLayout是空，或者是关闭状态，按照原本的方法处理
            return super.onTouchEvent(event);
        } else {
            if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_UP) {
                mDragLayout.close();
            }
            return true;
        }
    }

}
