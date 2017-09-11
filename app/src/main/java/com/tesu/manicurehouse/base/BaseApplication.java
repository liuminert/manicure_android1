package com.tesu.manicurehouse.base;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Handler;
import android.os.Looper;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.easemob.chat.KefuChat;
import com.tesu.manicurehouse.huanxin.CustomerHelper;
import com.tesu.manicurehouse.huanxin.HelpDeskPreferenceUtils;
import com.tesu.manicurehouse.util.MySQLiteHelper;


/**
 * @作者: 周岐同
 * @创建时间: 2015年3月31日下午4:16:08
 * @版权: 微位科技版权所有 
 * @描述: 记录全局变量	
 */
public class BaseApplication extends MultiDexApplication {
	/** 全局Context，原理是因为Application类是应用最先运行的，所以在我们的代码调用时，该值已经被赋值过了 */
	private static BaseApplication mInstance;
	/** 主线程ID */
	private static int mMainThreadId = -1;
	/** 主线程 */
	private static Thread mMainThread;
	/** 主线程Handler */
	private static Handler mMainThreadHandler;
	/** 主线程Looper */
	private static Looper mMainLooper;

	private static SQLiteOpenHelper dbHelper;



	@Override
	public void onCreate() {
//		SDKInitializer.initialize(this);
		//android.os.Process.myTid()  获取调用进程的id
		//android.os.Process.myUid() 获取 该进程的用户id
		//android.os.Process.myPid() 获取进程的id
		mMainThreadId = android.os.Process.myTid();
		mMainThread = Thread.currentThread();
		mMainThreadHandler = new Handler();
		mMainLooper = getMainLooper();
		mInstance = this;

		/*初始化volley*/
		MyVolley.init(this);
		super.onCreate();

		MultiDex.install(this);

//		//极光推送初始化
//		JPushInterface.setDebugMode(true); 	// 设置开启日志,发布时请关闭日志
//		JPushInterface.init(this);     		// 初始化 JPush

		// CustomerHelper 初始化
		String appkey = HelpDeskPreferenceUtils.getInstance(this).getSettingCustomerAppkey();
		KefuChat.getInstance().setAppkey(appkey);
		CustomerHelper.getInstance().init(this);

//		SQLiteDatabase db = Connector.getDatabase();
		dbHelper = new MySQLiteHelper(this, "manicurehouse.db", null, 2);

	}

	public static SQLiteOpenHelper getDbHelper(){
		return  dbHelper;
	}

	public static BaseApplication getApplication() {
		return mInstance;
	}

	/** 获取主线程ID */
	public static int getMainThreadId() {
		return mMainThreadId;
	}

	/** 获取主线程 */
	public static Thread getMainThread() {
		return mMainThread;
	}

	/** 获取主线程的handler */
	public static Handler getMainThreadHandler() {
		return mMainThreadHandler;
	}

	/** 获取主线程的looper */
	public static Looper getMainThreadLooper() {
		return mMainLooper;
	}
}
