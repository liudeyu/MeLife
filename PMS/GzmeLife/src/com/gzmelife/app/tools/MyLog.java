package com.gzmelife.app.tools;

import android.util.Log;

public class MyLog {
	private static boolean isOpen = true;

	public static final String TAG_I_URL = "url";
	public static final String TAG_I_PARAMS = "params";
	public static final String TAG_I_JSON = "json";
	public static final String TAG_I_INFO = "info";
	public static final String TAG_D = "debug";
	public static final String TAG_E = "error";

	public static void i(String tag, String data) {
		if (isOpen) {
			Log.i(tag, data);
		}
	}
	
	public static void i(String data) {
		if (isOpen) {
			Log.i(TAG_I_INFO, data);
		}
	}

	public static void d(String tag, String data) {
		if (isOpen) {
			Log.d(tag, data);
		}
	}
	
	public static void d(String data) {
		if (isOpen) {
			Log.d(TAG_D, data);
		}
	}
	
	public static void e(String data) {
		if (isOpen) {
			Log.e(TAG_E, data);
		}
	}
}
