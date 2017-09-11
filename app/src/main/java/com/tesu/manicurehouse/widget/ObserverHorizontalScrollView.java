package com.tesu.manicurehouse.widget;


import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.HorizontalScrollView;
import android.widget.Scroller;

import com.tesu.manicurehouse.R;


public class ObserverHorizontalScrollView extends HorizontalScrollView {

	private static final String TAG = ObserverHorizontalScrollView.class
			.getName();

	private static final int AUTO_SCROLL_LEFT=0x10;
	private static final int AUTO_SCROLL_RIGHT=0x11;
	// context
	Context mContext;

	// the child View
	private View mChildView;

	private boolean handleStop = false;

	private static final int MAX_SCROLL_HEIGHT = 280;
	
	private int autoScrollStyle;//0 默认无 ，0x10 自动向左滚动 ，0x11 自动向右滚动
	
	private int childViewWidth;
	private int scrollViewWidth;
	private int autoScrollWidth;
    private int leftScroll;

    private boolean autoScrollPage=true;

    private VelocityTracker mVelocityTracker;
    private Scroller  mScroller;

	public ObserverHorizontalScrollView(Context context) {
		super(context);
	}

	public ObserverHorizontalScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray tArray = context.obtainStyledAttributes(attrs,
                R.styleable.HsvAutoScrollStyle);
		String autoStr=tArray.getString(R.styleable.HsvAutoScrollStyle_autoScroll);
		tArray.recycle();
        mScroller=new Scroller(context);
		if(autoStr==null) return;
		if(autoStr.equalsIgnoreCase("left")){
			autoScrollStyle=AUTO_SCROLL_LEFT;
		}else if(autoStr.equalsIgnoreCase("right")){
			autoScrollStyle=AUTO_SCROLL_RIGHT;
		}
	}

	public ObserverHorizontalScrollView(Context context, AttributeSet attrs,
                                        int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onFinishInflate() {
		// get child View
		if (getChildCount() > 0) {
			this.mChildView = getChildAt(0);
			getViewTreeObserver().addOnGlobalLayoutListener(ogll);
		}
        super.onFinishInflate();
	}

	private float touchX = 0;

	@Override
	public boolean onInterceptTouchEvent(MotionEvent arg0) {
		if (arg0.getAction() == MotionEvent.ACTION_DOWN) {
			// get touch X
			touchX = arg0.getX();
		}
		return super.onInterceptTouchEvent(arg0);
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
        boolean intercept=false;
		if (mChildView != null) {
            intercept=commonOnTouchEvent(ev);
		}
        if(intercept) return true;
		return super.onTouchEvent(ev);
	}

    private int currPage;
    private int allPage;
    private int delaXToScrollView;
	// Scroll drag
	private static final float SCROLL_DRAG = 0.4f;

	private boolean commonOnTouchEvent(MotionEvent ev) {
		int action = ev.getAction();
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(ev);
        mVelocityTracker.computeCurrentVelocity(1000);
        mScollX = mChildView.getScrollX();
		switch (action) {
		case MotionEvent.ACTION_UP:
            Log.i(TAG,"ACTION_UP mChildView.getScrollX():"+mScollX);
			if (mChildView.getScrollX() != 0) {
				handleStop = true;
				startAnimation();
			}else if(autoScrollPage&&mChildView.getScrollX()==0){
                int parentScrollX=getScrollX();
                Log.i(TAG,"ACTION_UP parentScrollX:"+parentScrollX+" scrollViewWidth:"+scrollViewWidth);
                currPage=parentScrollX/scrollViewWidth;
                delaXToScrollView=parentScrollX%scrollViewWidth;
                float speedX=mVelocityTracker.getXVelocity();
                Log.i(TAG,"ACTION_UP currPage:"+currPage+" delaXToScrollView:"+delaXToScrollView+" speedX:"+speedX);
                if(speedX<-800){
                    scrollToNext(scrollViewWidth-delaXToScrollView);
                }else if (speedX > 800) {
                    scrollBack(-delaXToScrollView);
                }else{
                    if(delaXToScrollView<scrollViewWidth/2){
                        scrollBack(-delaXToScrollView);
                    }else{
                        scrollToNext(scrollViewWidth-delaXToScrollView);
                    }
                }

                return true;
            }
			break;
		case MotionEvent.ACTION_MOVE:
			float nowX = ev.getX();
			int deltaX = (int) (touchX - nowX);
			touchX = nowX;
			if (isEdge(-deltaX)) {
				if (mScollX < MAX_SCROLL_HEIGHT && mScollX > -MAX_SCROLL_HEIGHT) {
					mChildView.scrollBy((int) (deltaX * SCROLL_DRAG), 0);
					handleStop = false;
				}
			}
			break;
			case MotionEvent.ACTION_DOWN:
				if(autoScrollStyle==AUTO_SCROLL_RIGHT){
					mScollX=mChildView.getScrollX();
					if((int)mScollX!=0){
						mChildView.scrollTo(0, 0);
						//smoothScrollTo(-(int)mScollX,0);
						super.scrollTo((int)mScollX,0);
					}
				}
				break;
		default:
			break;
		}
        return false;
	}

    private void scrollBack(int deltaX) {
        smoothScrollTo(getScrollX()+deltaX,getScrollY());
    }

    private void scrollToNext(int deltaX) {
        smoothScrollTo(getScrollX()+deltaX,getScrollY());
    }


	/*
	 * whether to edge
	 */
	private boolean isEdge(int deltaX) {
		int tempOffset = childViewWidth - scrollViewWidth;
		int scrollX = this.getScrollX();
		if ((scrollX == 0&&deltaX>0) || (scrollX == tempOffset&&deltaX<0)) {
			return true;
		}
		return false;
	}

	private float RESET_RADIO = 0.9f;

	private void startAnimation() {
		resetChildViewPositionHandler.sendEmptyMessage(0);
	}
	
	public void  autoScroll(){//回弹效果
		if(loadFinish&&isAutoScroll()) {
            Log.i(TAG,"autoScroll");
            resetChildViewPositionHandler.sendEmptyMessage(1);
        }
	}

	private float childScrollX = 0;
	private int mScollX;
	private int offset=-1;
	private boolean reset=false;
	Handler resetChildViewPositionHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if(msg.what==0){
				childScrollX = mChildView.getScrollX();
				//Log.i("test","msg.what==1 childScrollX:"+childScrollX);
				if (childScrollX != 0 && handleStop) {
					childScrollX = childScrollX * RESET_RADIO;
					if (Math.abs(childScrollX) <= 2) {
						childScrollX = 0;
					}
					mChildView.scrollTo((int) childScrollX, 0);
					this.sendEmptyMessageDelayed(0, 3);
				}
			}else if(msg.what==1){
                if(!reset){
                    scrollTo(autoScrollWidth,0);
                    postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            step1();
                        }
                    },200);
                }else{

                }


			}else if(msg.what==2){
				step2();
				
			}
			
		};
	};

    private void step1(){
        if(offset<=0) return;
        mScroller.startScroll(autoScrollWidth,0,-autoScrollWidth,0,1200);
        /*mScollX=mChildView.getScrollX();
        if(!reset){
            Log.i(TAG,"reset");
            if(autoScrollStyle==AUTO_SCROLL_LEFT){
                if(autoScrollPage){
                    mScollX=offset;
                }else{
                    if(offset<scrollViewWidth/3){
                        mScollX=offset;
                    }else{
                        mScollX=scrollViewWidth/3;
                    }
                }

            }else if(autoScrollStyle==AUTO_SCROLL_RIGHT){
                if(offset<scrollViewWidth/3){
                    mScollX=0;
                }else{
                    mScollX=offset-scrollViewWidth/3;
                }
            }
            smoothScrollTo(0, 0);
            reset=true;
        }
        //Log.i("test","msg.what==1 scollX:"+mScollX);
        if(autoScrollStyle==AUTO_SCROLL_LEFT){
            if(mScollX>leftScroll){
                mScollX-=10;
                if(mScollX<leftScroll ) mScollX=leftScroll;
                mChildView.scrollTo((int) mScollX, 0);
                //scrollTo((int) mScollX+10, 0);
                resetChildViewPositionHandler.sendEmptyMessageDelayed(1, 10);
            }else {
                reset=false;
                resetChildViewPositionHandler.removeMessages(1);
                resetChildViewPositionHandler.sendEmptyMessage(2);
            }
        }else if(autoScrollStyle==AUTO_SCROLL_RIGHT){
            if(mScollX<(offset+autoScrollWidth)){
                mScollX+=3;
                if(mScollX>(offset+autoScrollWidth) ) mScollX=(offset+autoScrollWidth);
                mChildView.scrollTo((int) mScollX, 0);
                resetChildViewPositionHandler.sendEmptyMessageDelayed(1, 3);
            }else {
                reset=false;
                resetChildViewPositionHandler.removeMessages(1);
                resetChildViewPositionHandler.sendEmptyMessage(2);
            }
        }*/
    }

    private void step2(){
        if(autoScrollStyle==AUTO_SCROLL_LEFT){
            mScollX=mChildView.getScrollX();
            Log.i("test","msg.what==0 scollX:"+mScollX);
            if(mScollX<0){
                //mScollX+=3;
                mScollX=(int)(mScollX*RESET_RADIO);
                if(mScollX>-2) mScollX=0;
                mChildView.scrollTo((int) mScollX, 0);
                resetChildViewPositionHandler.sendEmptyMessageDelayed(2, 3);
            }else{
                resetChildViewPositionHandler.removeMessages(2);
            }
        }else if(autoScrollStyle==AUTO_SCROLL_RIGHT){
            mScollX=mChildView.getScrollX();
            if(mScollX>offset){
                //mScollX-=3;
                mScollX=(int)(mScollX*RESET_RADIO);
                if(mScollX<offset) mScollX=offset;
                mChildView.scrollTo((int) mScollX, 0);
                resetChildViewPositionHandler.sendEmptyMessageDelayed(2, 3);
            }else{
                resetChildViewPositionHandler.removeMessages(2);
            }
        }
    }

    private void scroll(int x){
        super.scrollTo(x,0);
        Log.i(TAG, "autoScrollPage scrollTo x:"+getScrollX()+" autoScrollWidth:"+autoScrollWidth);
        smoothScrollTo(0, 0);
    }
	private boolean loadFinish;
	private ViewTreeObserver.OnGlobalLayoutListener ogll = new ViewTreeObserver.OnGlobalLayoutListener() {
		@Override
		public void onGlobalLayout() {
			loadFinish=true;
            int count=0;
			if(offset==-1){
				// get the child view Width
				childViewWidth = mChildView.getMeasuredWidth();
				// get the ScrollView Width
				scrollViewWidth = getWidth();
                allPage=childViewWidth/scrollViewWidth;
				offset = childViewWidth - scrollViewWidth;
                if(autoScrollPage){
                    count=((ViewGroup)mChildView).getChildCount();
                    autoScrollWidth=scrollViewWidth*(count-1);
                    leftScroll=0;
                }else{
                    autoScrollWidth=scrollViewWidth/10;
                    leftScroll=-autoScrollWidth/2;
                }
				Log.i(TAG,"offset:"+offset+" MAX_SCROLL_HEIGHT:"+MAX_SCROLL_HEIGHT+"childViewWidth:"+childViewWidth+" child count:"+count);
			}
            if(offset<0||childViewWidth==0){
                offset=-1;
            }else{
                getViewTreeObserver().removeGlobalOnLayoutListener(ogll);
                autoScroll();
            }

		}
	};
	
	private boolean isAutoScroll(){
		return autoScrollStyle==AUTO_SCROLL_LEFT||autoScrollStyle==AUTO_SCROLL_RIGHT;
	}

    @Override
    public void computeScroll(){
        super.computeScroll();
        if(mScroller.computeScrollOffset()){
            int scrollX=mScroller.getCurrX();
            super.scrollTo(scrollX,0);
        }
    }
}
