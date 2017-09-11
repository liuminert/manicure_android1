package com.tesu.manicurehouse.widget;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;


import com.tesu.manicurehouse.R;
import com.tesu.manicurehouse.util.DialogUtils;
import com.tesu.manicurehouse.util.LogUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class RefreshListView extends ListView implements OnScrollListener {
    private static final String TAG = "RefreshListView";
    // 1. 需要下拉刷新
    // 2. 释放刷新
    // 3. 正在刷新

    public static final int STATE_PULL_DOWN_REFRESH = 0; // 下拉刷新
    public static final int STATE_PULL_UP_REFRESH = 1;//上拉刷新
    public static final int STATE_PULL_DOWN_BACK = 2; // 下拉回弹
    public static final int STATE_DEFAULT = 3; // 默认状态
    /*public static final int STATE_PULL_DOWN_REFRESH = 0; // 下拉刷新
	public static final int STATE_RELEASE_REFRESH = 1; // 释放刷新
	public static final int STATE_REFRESHING = 2; // 释放刷新*/

    public static final int STATE_DRAG_UP_REFRESH = 3; // 上拉刷新
    public static final int STATE_DRAG_UP_RELEASE_REFRESH = 4; // 上拉释放刷新
    public static final int STATE_DRAG_UP_REFRESHING = 5; // 上拉刷新中

    private static final long DURATION = 200;

    private LinearLayout mHeaderLayout; // 包含
    // 刷新的view

    private View mCustomHeaderView; // 自定义的headerView
    private View mRefreshView; // 刷新的view

    private int mRefreshViewHeight; // 下拉刷新view的高度
    private int imageHeight;

    private float mDownX;
    private float mDownY;

    private int mCurrentPullDownState = STATE_DEFAULT; // 默认值
    private int mCurrentDragUpState = STATE_DRAG_UP_REFRESH; // 默认值
//    private ImageView mRefreshingDog; // 帧动画图片
//    private ImageView mScaleDog; // 缩放图片

    private OnRefreshListener mRefreshListener;

    private View mFooterLayout; // 底部加载更多的布局
    private int mFooterHeight;
    private boolean isLoadingMore; // 标记是否是加载更多
    private int scrollState = -1;
    private boolean isDragUp;
    private boolean isPullDown;
    private TextView tvFooter; // 上拉状态提示
    private boolean onceTouch = true;
    private float hiddenTop;
    // 用于处理 onitemclicklistener 标志位
    private boolean isTouching = false;
    private int firstVisiblePosition;
    private int lastVisiblePosition;

    private int currentScollState = -1;
    public int UPSCROLL = 6;
    public int DOWNSCROLL = 7;
    ReleaseAnimimation releaseAnim;

    private static final int VERSION=android.os.Build.VERSION.SDK_INT;

    //获得当前滑动状态
    public int getCurrentScollState() {
        return currentScollState;
    }

    public RefreshListView(Context context) {
        super(context);

        // 加载头布局
        initFooterLayout();
        initHeaderLayout();

    }

    public RefreshListView(Context context, AttributeSet attrs) {
        super(context, attrs);

        // 加载头布局
        initFooterLayout();
        initHeaderLayout();
        this.setOnItemClickListener(new InterceptOnItemClickListener());

        loadDialog = DialogUtils.createLoadDialog(this.getContext(), false);
    }

    private void initHeaderLayout() {
        // 加载 头布局
        mHeaderLayout = (LinearLayout) View.inflate(getContext(),
                R.layout.refresh_header_layout, null);

        // 添加到listView的headerView中
//        this.addHeaderView(mHeaderLayout);

        // 需要隐藏刷新的View
        mRefreshView = mHeaderLayout
                .findViewById(R.id.refresh_header_refresh_part);
//        mRefreshingDog = (ImageView) mHeaderLayout
//                .findViewById(R.id.refresh_header_pb);
//        mScaleDog = (ImageView) mHeaderLayout
//                .findViewById(R.id.refresh_header_dog);
        // 给头布局设置PaddingTop为负数来隐藏控件
        mRefreshView.measure(0, 0);
        mRefreshViewHeight = mRefreshView.getMeasuredHeight();
        imageHeight=(int)(mRefreshViewHeight-60*getResources().getDisplayMetrics().density);

        LogUtils.d("高度 : " + mRefreshViewHeight + "mScaleDog:" + imageHeight);
        //mHeaderLayout.setPadding(0, -mRefreshViewHeight, 0, 0);
        mRefreshView.getLayoutParams().height = 0;
        mRefreshView.requestLayout();
        releaseAnim = new ReleaseAnimimation(mRefreshView);
        releaseAnim.setDuration(300);
        mRefreshView.setAnimation(releaseAnim);
//        setOverScrollMode(OVER_SCROLL_NEVER);
    }

    private void initFooterLayout() {
        mFooterLayout = View.inflate(getContext(),
                R.layout.refresh_footer_layout, null);
        tvFooter = (TextView) mFooterLayout
                .findViewById(R.id.refresh_footer_tv);
        // 添加到footerView中
        this.addFooterView(mFooterLayout);
        // 隐藏footerLayout
        mFooterLayout.measure(0, 0);
        mFooterHeight = mFooterLayout.getMeasuredHeight();
        mFooterLayout.setPadding(0, 0, 0, -mFooterHeight);

        // 设置当listView滑动时的监听
        this.setOnScrollListener(this);
    }

//    private void initAnimation() {
//        mRefreshingDog.setBackgroundResource(R.anim.refreshing_anim);
//        animationDrawable = (AnimationDrawable) mRefreshingDog.getBackground();
//    }

    /* 预留给以后拓展 */
    public void addCustomHeaderView(View headerView) {
        this.mCustomHeaderView = headerView;
        mHeaderLayout.addView(headerView);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownY = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                // listview 在下拉的时候不要被父控件拦截

                float moveY = ev.getY();

                int diffY = (int) (moveY - mDownY);
                mDownY=moveY;
                // 设置当前滑动方向
                if(Math.abs(diffY)>= ViewConfiguration.getTouchSlop()){
                    if (diffY > 0) {
                        currentScollState = DOWNSCROLL;
                    } else {
                        currentScollState = UPSCROLL;
                    }
                    if(mOnScrollUpDownListener!=null) mOnScrollUpDownListener.onScroll(currentScollState,scrollState);
                }
                break;
        }
        return super.onTouchEvent(ev);
    }


    /**
     * 上拉加载刷新界面
     */
    private void refreshUI2() {
        switch (mCurrentDragUpState) {

            case STATE_DRAG_UP_REFRESH:// 上拉刷新
                tvFooter.setText("上拉加载更多...");
                break;
            case STATE_DRAG_UP_RELEASE_REFRESH:// 松开刷新
                tvFooter.setText("松开加载更多...");

                break;
            case STATE_DRAG_UP_REFRESHING:// 正在刷新
                tvFooter.setText("加载中...");
                break;
            default:
                break;
        }
    }

    OnScrollUpDownListener mOnScrollUpDownListener;

    public void setOnScrollUpDownListener(OnScrollUpDownListener listener){
        mOnScrollUpDownListener=listener;
    }
    public interface OnScrollUpDownListener{
        void onScroll(int direction, int scrollState);
    }

    public void setOnRefreshListener(OnRefreshListener listener) {
        this.mRefreshListener = listener;
    }

    public interface OnRefreshListener {
        /**
         * 正在刷新时的回调
         */
//        void onRefreshing();

        /**
         * 加载更多的时候的回调
         */
        void onLoadingMore();

        /**
         * 处理item点击事件
         */
        void implOnItemClickListener(AdapterView<?> parent, View view, int position, long id);
    }

    /**
     * 告知 ListView刷新完成
     */
    public void refreshFinish() {
		/*if (isLoadingMore) {
			// 上拉加载
			mFooterLayout.setPadding(0, -mFooterHeight, 0, 0);
			mCurrentDragUpState = STATE_DRAG_UP_REFRESH;
			refreshUI2();
			isLoadingMore = false;
		} else {
			// 隐藏 刷新的View
			mHeaderLayout.setPadding(0, -mRefreshViewHeight, 0, 0);
			// 状态重置
			mCurrentPullDownState = STATE_PULL_DOWN_REFRESH;
			// UI更新
			refreshUI();
		}*/
        Log.i(TAG, "refreshFinish");
        if (loadDialog != null) {
            loadDialog.dismiss();
        }
        if (refreshIndex==2) {
            mRefreshView.startAnimation(releaseAnim);
        }
        refreshIndex = 0;
    }

    private String getCurrentTimeString() {
        long time = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new Date(time));
    }

    //通过这个判断listview是否填充满以及是否使用系统自带滚动属性
    // getOverScrollMode()==OVER_SCROLL_NEVER;表示不滚动
    private boolean isFull=false;
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

        Log.i(TAG, "onScrollStateChanged scrollState:" + scrollState);
        this.scrollState = scrollState;
        lastVisiblePosition = getLastVisiblePosition();
        isFull=getOverScrollMode()!=OVER_SCROLL_NEVER;
        if(getFirstVisiblePosition()==0&&lastVisiblePosition==getCount()-1){
            if((getChildAt(lastVisiblePosition).getBottom()-getChildAt(0).getTop())<(getHeight()-getPaddingTop()-getPaddingBottom())){
                isFull=false;
            }
        }
        refreshStatus();
        mCurrentPullDownState = STATE_DEFAULT;
        //当item个数没有填满整个listview时，不会触发Fling，所以需要特殊处理
        /*if(scrollState==OnScrollListener.SCROLL_STATE_IDLE&&!isFull){
            if(mRefreshView.getHeight()<imageHeight){
                mRefreshView.startAnimation(releaseAnim);
            }else{
                mScaleDog.setVisibility(View.INVISIBLE);
                mRefreshingDog.setVisibility(View.VISIBLE);
                animationDrawable.start();
                loadDialog.show();
                mRefreshListener.onRefreshing();
                refreshIndex = 2;
            }
        }*/

        // 当前显示的位置是第一个 并且向下滑动 为 下拉刷新
        // 当前显示的位置是最后一个并且是向上滑动 为上拉 加载

        // if (lastVisiblePosition == getAdapter().getCount() - 1) {
        // isDragUp = true;
        // } else if (getFirstVisiblePosition() == 0) {
        // isPullDown = true;
        // }
        // if(scrollState == OnScrollListener.SCROLL_STATE_IDLE || scrollState
        // == OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
        //
        //
        //
        // }
        //
		/*if (lastVisiblePosition == getAdapter().getCount() - 1) {
			if (scrollState == OnScrollListener.SCROLL_STATE_IDLE
					|| scrollState == OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
				if (!isLoadingMore) {
					// 是滑动到底部
					isLoadingMore = true;
					// 通知状态变化，
					if (mRefreshListener != null) {
						loadDialog.show();
						mRefreshListener.onLoadingMore();
					}
				}
			}
		}*/
        /*if(scrollState==OnScrollListener.SCROLL_STATE_FLING) {
            if(mCurrentPullDownState==STATE_PULL_UP_REFRESH) {
                Log.i(TAG,"STATE_PULL_UP_REFRESH");
                if (mRefreshListener != null) {
                    loadDialog.show();
                    mRefreshListener.onLoadingMore();
                }
            }else if(mCurrentPullDownState==STATE_PULL_DOWN_REFRESH) {
                Log.i(TAG,"STATE_PULL_DOWN_REFRESH");
                mScaleDog.setVisibility(View.INVISIBLE);
                mRefreshingDog.setVisibility(View.VISIBLE);
                animationDrawable.start();
                loadDialog.show();
                mRefreshListener.onRefreshing();
            }else if(mCurrentPullDownState==STATE_PULL_DOWN_BACK) {
                Log.i(TAG,"STATE_PULL_DOWN_BACK");
                mRefreshView.startAnimation(releaseAnim);
            }
        }else {
            mCurrentPullDownState=STATE_DEFAULT;
        }*/
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {

    }

    private float dampingRatio;
    @Override
    protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX, int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {
        int currH = mRefreshView.getHeight();
        int destImageViewHeight=currH;
        Log.i(TAG, "overScrollBy scrollY:" + scrollY + "  deltaY:" + deltaY + "  scrollY:" + scrollY + " currH:" + currH + "  top:" + getScrollY() + " maxOverScrollY:" + maxOverScrollY + " isFull:" + isFull);
        if (isTouchEvent&&refreshIndex==0&&getFirstVisiblePosition()==0) {
            dampingRatio=(mRefreshViewHeight-currH)*1.0f/mRefreshViewHeight;
            if(dampingRatio<0.2) dampingRatio=0.2f;
            destImageViewHeight = isFull?(-scrollY):(int)(currH-deltaY*dampingRatio);
            Log.i(TAG, "destImageViewHeight------ scrollY:"+destImageViewHeight);
            if (destImageViewHeight > mRefreshViewHeight) {
                destImageViewHeight = mRefreshViewHeight;
            } else if (destImageViewHeight < 0) {
                destImageViewHeight = 0;
            }
            mRefreshView.getLayoutParams().height = destImageViewHeight;
            mRefreshView.requestLayout();
            /*if (destImageViewHeight!=currH){
                Log.i(TAG, "destImageViewHeight scrollY:"+destImageViewHeight);
                mRefreshView.getLayoutParams().height = destImageViewHeight;
                mRefreshView.requestLayout();
            }*/
        }
        if (destImageViewHeight==0&&deltaY > 0) {
            mCurrentPullDownState = STATE_PULL_UP_REFRESH;
        } else if (destImageViewHeight > 0) {
            LogUtils.e("搞："+destImageViewHeight+"  imageHeight:"+imageHeight);
            if (destImageViewHeight >= imageHeight) {
                mCurrentPullDownState = STATE_PULL_DOWN_REFRESH;
            } else {
                mCurrentPullDownState = STATE_PULL_DOWN_BACK;
            }
        }
//        if (destImageViewHeight >= imageHeight) {
//            mRefreshingDog.setVisibility(View.VISIBLE);
//        }else{
//            mRefreshingDog.setVisibility(View.INVISIBLE);
//        }
        //refreshStatus();
        return super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX, scrollRangeY, maxOverScrollX, maxOverScrollY, isTouchEvent);
    }

    private int  refreshIndex;
    private void refreshStatus() {
        if (scrollState == -1 || mCurrentPullDownState == STATE_DEFAULT) return;
        if ((isFull&&scrollState == OnScrollListener.SCROLL_STATE_FLING)||(!isFull&&scrollState==OnScrollListener.SCROLL_STATE_IDLE)) {
            if (mCurrentPullDownState == STATE_PULL_UP_REFRESH) {
                Log.i(TAG, "STATE_PULL_UP_REFRESH");
                if (mRefreshListener != null) {
                    refreshIndex = 1;
                    loadDialog.show();
                    mRefreshListener.onLoadingMore();
                }
            } else if (mCurrentPullDownState == STATE_PULL_DOWN_REFRESH) {
                Log.i(TAG, "STATE_PULL_DOWN_REFRESH");
//                refreshIndex = 2;
//                loadDialog.show();
//                mRefreshListener.onRefreshing();
            } else if (mCurrentPullDownState == STATE_PULL_DOWN_BACK) {
                Log.i(TAG, "STATE_PULL_DOWN_BACK");
                mRefreshView.startAnimation(releaseAnim);
            }
        }
        scrollState = -1;
    }

    public class ReleaseAnimimation extends Animation {
        View mView;
        private int fromH;
        private int toH;

        @Override
        protected void applyTransformation(float interpolatedTime,
                                           Transformation t) {
            int h = (int) (fromH + (toH - fromH) * interpolatedTime);
            mView.getLayoutParams().height = h;
            mView.requestLayout();
        }

        protected ReleaseAnimimation(View view) {
            mView = view;
            toH = 0;
        }

        @Override
        public void reset() {
            super.reset();
            fromH = mView.getHeight();
        }
    }

    class InterceptOnItemClickListener implements OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            if (isTouching || mCurrentPullDownState != STATE_DEFAULT || mCurrentDragUpState == STATE_DRAG_UP_REFRESHING) {
                return;
            }
            mRefreshListener.implOnItemClickListener(parent, view, position, id);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (mOnLayout != null) {
            mOnLayout.mOnLayout(getFirstVisiblePosition(), getLastVisiblePosition());
        }
    }

    private OnLayoutListener mOnLayout;
    private Dialog loadDialog;

    public OnLayoutListener getmOnLayout() {
        return mOnLayout;
    }

    public void setmOnLayout(OnLayoutListener mOnLayout) {
        this.mOnLayout = mOnLayout;
    }

    public interface OnLayoutListener {
        void mOnLayout(int first, int last);
    }
}
