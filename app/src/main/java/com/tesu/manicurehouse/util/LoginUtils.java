package com.tesu.manicurehouse.util;

import android.text.TextUtils;

public class LoginUtils {

	/** 用户个性简介 */
	public static String user_summary;
	/** 我的粉丝ID */
	public static String fansId;
	/** 其他的粉丝ID */
	public static String othersId;
	/** 是否从其他用户跳转过 */
	public static boolean isothers = false;
	public static String subscribed;
	public static int changetype;
	/**是否要跳转回关注页面*/
	public static boolean tosub=false;
	/**我的页面是否发布过需要刷新*/
	public static boolean ismyselfNeedUpdate=false;

	public static int getChangetype() {
		return changetype;
	}
	public static void setChangetype(int changetype) {
		LoginUtils.changetype = changetype;
	}
	public static String getSubscribed() {
		return subscribed;
	}
	public static void setSubscribed(String subscribed) {
		LoginUtils.subscribed = subscribed;
	}
	public static String getOthersId() {
		return othersId;
	}
	public static String getFansId() {
		return fansId;
	}

	public static String getUid() {
		if (!TextUtils.isEmpty(SharedPrefrenceUtils.getString(
				UIUtils.getContext(), "userId"))) {
			return SharedPrefrenceUtils.getString(UIUtils.getContext(),
					"userId");
		}
		return null;
	}

	public static String getSid() {
		if (!TextUtils.isEmpty(SharedPrefrenceUtils.getString(
				UIUtils.getContext(), "sid"))) {
			return SharedPrefrenceUtils.getString(UIUtils.getContext(), "sid");
		}
		return null;
	}

	public static String getUser_name() {
		if (!TextUtils.isEmpty(SharedPrefrenceUtils.getString(
				UIUtils.getContext(), "nick"))) {
			return SharedPrefrenceUtils.getString(UIUtils.getContext(), "nick");
		}
		return null;
	}

	public static String getMobile() {
		if (!TextUtils.isEmpty(SharedPrefrenceUtils.getString(
				UIUtils.getContext(), "mobile"))) {
			return SharedPrefrenceUtils.getString(UIUtils.getContext(),
					"mobile");
		}
		return null;
	}

	public static String getUser_bg() {
		if (!TextUtils.isEmpty(SharedPrefrenceUtils.getString(
				UIUtils.getContext(), "bg"))) {
			return SharedPrefrenceUtils.getString(UIUtils.getContext(),
					"bg");
		}
		return null;
	}
	public static String getUser_avatar() {
		if (!TextUtils.isEmpty(SharedPrefrenceUtils.getString(
				UIUtils.getContext(), "avatar"))) {
			return SharedPrefrenceUtils.getString(UIUtils.getContext(),
					"avatar");
		}
		return null;
	}

	public static String getUser_cAvatar() {
		if (!TextUtils.isEmpty(SharedPrefrenceUtils.getString(
				UIUtils.getContext(), "cAvatar"))) {
			return SharedPrefrenceUtils.getString(UIUtils.getContext(),
					"cAvatar");
		}
		return null;
	}

	public static String getUser_summary() {
		return user_summary;
	}

	public static String getUser_sex() {
		if (!TextUtils.isEmpty(SharedPrefrenceUtils.getString(
				UIUtils.getContext(), "sex"))) {
			return SharedPrefrenceUtils.getString(UIUtils.getContext(), "sex");
		}
		return null;
	}

	public static String getVibrate() {
		if (!TextUtils.isEmpty(SharedPrefrenceUtils.getString(
				UIUtils.getContext(), "vibrate"))) {
			return SharedPrefrenceUtils.getString(UIUtils.getContext(),
					"vibrate");
		}
		return null;
	}

	public static String getPdisturb() {
		if (!TextUtils.isEmpty(SharedPrefrenceUtils.getString(
				UIUtils.getContext(), "pdisturb"))) {
			return SharedPrefrenceUtils.getString(UIUtils.getContext(),
					"pdisturb");
		}
		return null;
	}

	public static boolean isLogin() {
		if (SharedPrefrenceUtils.getBoolean(UIUtils.getContext(), "isLogin")) {
			return true;
		}
		return false;
	}
	public static boolean isLoading() {
		if (SharedPrefrenceUtils.getBoolean(UIUtils.getContext(), "isLoading")) {
			return true;
		}
		return false;
	}
	public static boolean isUpdate() {
		if (SharedPrefrenceUtils.getBoolean(UIUtils.getContext(), "isUpdate")) {
			return true;
		}
		return false;
	}
}
