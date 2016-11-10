package com.gzmelife.app.tools;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.Html;
import android.text.InputType;
import android.text.Spanned;
import android.text.method.NumberKeyListener;
import android.util.Log;

@SuppressLint("SimpleDateFormat")
public class Tools {
	public static Boolean isMobileNo(String str) {
		Boolean isMobileNo = false;
		try {
			Pattern p = Pattern
					.compile("^((13[0-9])|(15[^4,\\D])|(18[0,2-9])|(17[7]))\\d{8}$");
			Matcher m = p.matcher(str);
			isMobileNo = m.matches();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return isMobileNo;
	}
	
	public static String getTimeNow() {
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
				Locale.CHINA);
		return format.format(date);
	}

	@SuppressWarnings("deprecation")
	/** 获取year年后的日期 */
	public static String getTimeAfterYear(int year) {
		Date date = new Date(System.currentTimeMillis());
		date.setYear(date.getYear() + year);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd",
				Locale.CHINA);
		return format.format(date);
	}

	// public static String getImgPath(String path) {
	// if (path == null || path.startsWith("http") || path.equals("")) {
	// return path == null ? "" : path;
	// } else {
	// return Config.URL + (path.startsWith("/") ? path : "/" + path);
	// }
	// }

	public static String getMD5(String val) throws NoSuchAlgorithmException {
		String re_md5 = new String();
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(val.getBytes());
			byte b[] = md.digest();
			int i;
			StringBuffer buf = new StringBuffer("");
			for (int offset = 0; offset < b.length; offset++) {
				i = b[offset];
				if (i < 0)
					i += 256;
				if (i < 16)
					buf.append("0");
				buf.append(Integer.toHexString(i));
			}
			re_md5 = buf.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return re_md5;
	}

	@SuppressWarnings("unused")
	private static String getString(byte[] b) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < b.length; i++) {
			sb.append(b[i]);
		}
		return sb.toString();
	}

	/**
	 * 
	 * @param context
	 * @param dpValue
	 * @return
	 */

	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	public static String getDay(int day) {
		String nextDate_1 = null;
		try {
			// 获取当前日期
			Date date = new Date();
			SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
			String nowDate = sf.format(date);
			System.out.println(nowDate);
			// 通过日历获取下一天日期
			Calendar cal = Calendar.getInstance();
			cal.setTime(sf.parse(nowDate));
			cal.add(Calendar.DAY_OF_YEAR, +day);
			nextDate_1 = sf.format(cal.getTime());

		} catch (Exception e) {
			e.printStackTrace();
		}
		return nextDate_1;
	}

	public static String subDot(String str) {
		if (str.contains(".")) {
			Log.i("TAG", str + "======" + str.indexOf(".") + "==========="
					+ (str.length() - 1));
			str = str.substring(0, str.indexOf("."));

		}
		return str;
	}

	public static Spanned addColor(String str) {
		return Html.fromHtml("<font color='#000000'>" + str + "</font>");

		// ("<font color='blue'>出错</font>");
	}
	
	/** 只允许输入数字或者空格 */
	public static NumberKeyListener numOrGapKeyListener = new NumberKeyListener(){
		@Override
		protected char[] getAcceptedChars() {
			char[] numberChars = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', ' '};
			return numberChars;
		}

		@Override
		public int getInputType() {
			return InputType.TYPE_CLASS_TEXT;
		}
	};
	
	/**
	 * 获取版本号
	 * @return 当前应用的版本号
	 */
	public static String getVersion(Context context) {
	    try {
	        PackageManager manager = context.getPackageManager();
	        PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
	        String version = info.versionName;
	        return version;
	    } catch (Exception e) {
	        e.printStackTrace();
	        return "";
	    }
	}
}
