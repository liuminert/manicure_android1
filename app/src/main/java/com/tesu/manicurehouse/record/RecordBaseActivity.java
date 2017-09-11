package com.tesu.manicurehouse.record;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public abstract class RecordBaseActivity extends Activity {
	private static final List<RecordBaseActivity> mActivities = new LinkedList<RecordBaseActivity>();
	private static final List<RecordBaseActivity> delActivities = new LinkedList<RecordBaseActivity>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		synchronized (mActivities) {
			mActivities.add(this);
		}

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(getContentViewId());
		findViews();
		initGetData();
		init();
		widgetListener();
	}
	protected abstract int getContentViewId();
	protected abstract void findViews();
	protected abstract void init();
	protected void initGetData() {
	};
	protected abstract void widgetListener();

//	/**
//	 * 关闭所有Activity
//	 */
//	public static void finishAll() {
//		List<RecordBaseActivity> copy;
//		synchronized (mActivities) {
//			copy = new ArrayList<RecordBaseActivity>(mActivities);
//		}
//		for (RecordBaseActivity activity : copy) {
//			activity.finish();
//		}
//	}

	protected void setFinish() {
		synchronized (delActivities) {
			delActivities.add(this);
		}
	}

	protected void finishActivity() {
		List<RecordBaseActivity> copy;
		synchronized (delActivities) {
			copy = new ArrayList<RecordBaseActivity>(delActivities);
		}
		for (RecordBaseActivity activity : copy) {
			activity.finish();
		}
	}


}
