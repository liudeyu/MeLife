package com.gzmelife.app.tools;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.graphics.drawable.AnimationDrawable;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.gzmelife.app.R;
import com.gzmelife.app.device.Config;

public class ShowDialogUtil {
	private static ProgressBar pb_circle;
	private static TextView tv_title;

	/** 取消按钮 */
	private static Button btn_cancel;

	public static AlertDialog getShowDialog(Activity context, int layout, int width, int height, int x, int y, boolean isclickmiss,int visibleButton) {
		AlertDialog.Builder ab = new AlertDialog.Builder(context);
		final AlertDialog dlg = ab.create();
		dlg.setCanceledOnTouchOutside(isclickmiss);
		Window w = dlg.getWindow();
		WindowManager.LayoutParams lp = w.getAttributes();
		dlg.onWindowAttributesChanged(lp);
		lp.x = x;
		lp.y = y;
		// dlg.setView(view);
		dlg.setCancelable(false);
		dlg.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				// TODO Auto-generated method stub
				if (keyCode == KeyEvent.KEYCODE_SEARCH)
				{
					return true;
				}
				else
				{
					return false; //默认返回 false
				}
			}
		});
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View note_view = inflater.inflate(layout, null);
		pb_circle = (ProgressBar) note_view.findViewById(R.id.pb_circle);
		tv_title = (TextView) note_view.findViewById(R.id.tv_title);
		btn_cancel = (Button) note_view.findViewById(R.id.btn_cancel);
		btn_cancel.setVisibility(visibleButton);

		btn_cancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Config.cancelTransfer = true;
				if (dlg.isShowing()){
					dlg.dismiss();
				}
			}
		});

		dlg.show();
		dlg.getWindow().setContentView(note_view);

		WindowManager.LayoutParams params = dlg.getWindow().getAttributes();
		params.width = width;
		params.height = height;
		dlg.getWindow().setAttributes(params);

		return dlg;
	}

	public static AlertDialog getShowDialog(Activity context, int layout, int width, int height, int x, int y, boolean isclickmiss) {
		AlertDialog.Builder ab = new AlertDialog.Builder(context);
		final AlertDialog dlg = ab.create();
		dlg.setCanceledOnTouchOutside(isclickmiss);
		Window w = dlg.getWindow();
		WindowManager.LayoutParams lp = w.getAttributes();
		dlg.onWindowAttributesChanged(lp);
		lp.x = x;
		lp.y = y;
		// dlg.setView(view);
		dlg.setCancelable(false);
		dlg.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				// TODO Auto-generated method stub
				if (keyCode == KeyEvent.KEYCODE_SEARCH)
				{
					return true;
				}
				else
				{
					return false; //默认返回 false
				}
			}
		});
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View note_view = inflater.inflate(layout, null);
		pb_circle = (ProgressBar) note_view.findViewById(R.id.pb_circle);
		tv_title = (TextView) note_view.findViewById(R.id.tv_title);
		btn_cancel = (Button) note_view.findViewById(R.id.btn_cancel);

		btn_cancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Config.cancelTransfer = true;
				if (dlg.isShowing()){
					dlg.dismiss();
				}
			}
		});

		dlg.show();
		dlg.getWindow().setContentView(note_view);

		WindowManager.LayoutParams params = dlg.getWindow().getAttributes();
		params.width = width;
		params.height = height;
		dlg.getWindow().setAttributes(params);

		return dlg;
	}

	/** 20161009设置是否显示取消按钮 */
	public static void setCancelButton(int visibility) {
		btn_cancel.setVisibility(visibility);
	}

	public static void setTitle(String str) {
		if (tv_title != null) {
			tv_title.setText(str);
		}
	}

	public static AlertDialog getShowDialog(Activity context) {
		return getShowDialog(context, R.layout.dialog_progressbar, 0, 0, false);
	}

	public static AlertDialog getShowDialog(Activity context, int layout,
											int x, int y, boolean isclickmiss) {
		AlertDialog.Builder ab = new AlertDialog.Builder(context);
		AlertDialog dlg = ab.create();
		if (isclickmiss) {
			dlg.setCanceledOnTouchOutside(false);
		} else {
			dlg.setCancelable(false);
		}
		Window w = dlg.getWindow();
		WindowManager.LayoutParams lp = w.getAttributes();
		dlg.onWindowAttributesChanged(lp);
		lp.x = x;
		lp.y = y;
		// dlg.setView(view);

		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View note_view = inflater.inflate(layout, null);
		ImageView iv_dialog_img = (ImageView) note_view.findViewById(R.id.iv_dialog_img);
		if (iv_dialog_img != null) {
			AnimationDrawable animationDrawable = (AnimationDrawable) iv_dialog_img.getDrawable();
			animationDrawable.start();
		}
		pb_circle = (ProgressBar) note_view.findViewById(R.id.pb_circle);
		tv_title = (TextView) note_view.findViewById(R.id.tv_title);

		dlg.show();

		dlg.getWindow().setContentView(note_view);
		// dlg.getWindow().setContentView(R.layout.aboutpop);

		return dlg;
	}

	public static void setProgress(int now, int all) {
		if (pb_circle != null) {
			pb_circle.setProgress(now * 100 / all);
		}else{
			pb_circle.setVisibility(View.GONE);
		}
//		if (tv_title != null) {
//			tv_title.setText("正在下载菜谱，请稍候\n" + DeviceFragment.saveFileName + "..." + now + " / " + all);
//		}
	}

	public static int getScreenWidth(Activity context) {
		DisplayMetrics dm = new DisplayMetrics();
		context.getWindowManager().getDefaultDisplay().getMetrics(dm);
		return dm.widthPixels;
	}

	public static int getScreenHeight(Activity context) {
		DisplayMetrics dm = new DisplayMetrics();
		context.getWindowManager().getDefaultDisplay().getMetrics(dm);
		return dm.heightPixels;
	}

	public static float getScreenDensity(Activity context) {
		DisplayMetrics dm = new DisplayMetrics();
		context.getWindowManager().getDefaultDisplay().getMetrics(dm);
		return dm.density;
	}
}
