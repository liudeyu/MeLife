package com.gzmelife.app.tools;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.view.Window;
import android.view.WindowManager;

import com.gzmelife.app.R;

@TargetApi(Build.VERSION_CODES.KITKAT)
public class SystemBarUtils {
	/**
     * Apply KitKat specific translucency.
     */
    public static void applyKitKatTranslucency(Activity activity) {
        // KitKat translucent navigation/status bar.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true, activity);
            SystemBarTintManager mTintManager = new SystemBarTintManager(activity);
            mTintManager.setStatusBarTintEnabled(true);
            mTintManager.setNavigationBarTintEnabled(true);
            mTintManager.setTintColor(activity.getResources().getColor(R.color.titlebarbg));
        }
    }
    
    public static void applyKitKatTranslucencyOfPic(Activity activity) {
        // KitKat translucent navigation/status bar.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true, activity);
            SystemBarTintManager mTintManager = new SystemBarTintManager(activity);
            mTintManager.setTintAlpha(0f);
//            mTintManager.setStatusBarTintEnabled(true);
//            mTintManager.setNavigationBarTintEnabled(true);
//            mTintManager.setTintColor(activity.getResources().getColor(R.color.titlebarbg));
        }
    }
    
    public static void applyKitKatTranslucency(Activity activity, int colorId) {
        // KitKat translucent navigation/status bar.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true, activity);
            SystemBarTintManager mTintManager = new SystemBarTintManager(activity);
            mTintManager.setStatusBarTintEnabled(true);
            mTintManager.setNavigationBarTintEnabled(true);
            mTintManager.setTintColor(activity.getResources().getColor(colorId));
        }
    }
     
    public static void setTranslucentStatus(boolean on, Activity activity) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }
}
