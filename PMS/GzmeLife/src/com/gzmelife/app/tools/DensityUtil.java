package com.gzmelife.app.tools;

import android.app.Activity;
import android.content.Context;

public class DensityUtil {
	private static int width = -1;
	private static int height = -1;
	
	@SuppressWarnings("deprecation")
	public static int getWidth(Activity activity) {
		if (width == -1) {
			width = activity.getWindowManager().getDefaultDisplay().getWidth(); 
		}
		return width;
	}
	
	@SuppressWarnings("deprecation")
	public static int getWidth(Context context) {
		if (width == -1) {
			width = ((Activity) context).getWindowManager().getDefaultDisplay().getWidth(); 
		}
		return width;
	}
	
	@SuppressWarnings("deprecation")
	public static int getHeight(Activity activity) {
		if (height == -1) {
			height = activity.getWindowManager().getDefaultDisplay().getHeight(); 
		}
		return height;
	}

	/**
	 */
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}
}
