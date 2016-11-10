package com.gzmelife.app.views;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.gzmelife.app.R;
import com.gzmelife.app.tools.DensityUtil;

@SuppressLint("InflateParams")
public class TipConfirmView {
	public static Dialog dialog;

	/** 弹出一个确定对话框 */
	public static void showConfirmDialog(Context context, String text,
			OnClickListener listener) {
		dialog = new android.app.Dialog(context, R.style.Dialog);
		// getSuccessDialog.setCancelable(false); // 点击外部和手机返回键均不关闭dialog
		dialog.setCanceledOnTouchOutside(false); // 点击外部不关闭dialog
		View successView = LayoutInflater.from(context).inflate(
				R.layout.pop_tip_confirm, null);
		((TextView) successView.findViewById(R.id.tv_title)).setText(text);
		successView.findViewById(R.id.btn_cancel).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog.dismiss();
					}
				});
		successView.findViewById(R.id.btn_ok).setOnClickListener(listener);
		dialog.setContentView(successView, new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		Window dialogWindow = dialog.getWindow();
		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
		lp.width = DensityUtil.getWidth((Activity) context)
				- DensityUtil.dip2px(context, 48);
		lp.height = LayoutParams.WRAP_CONTENT;
		// lp.gravity = Gravity.CENTER;
		dialogWindow.setAttributes(lp);
		dialog.show();
	}
	
	/** 弹出一个确定对话框，只有确定按钮 */
	public static void showConfirmDialog2(Context context, String text,
			OnClickListener listener) {
		dialog = new android.app.Dialog(context, R.style.Dialog);
		dialog.setCancelable(false); // 点击外部和手机返回键均不关闭dialog
//		dialog.setCanceledOnTouchOutside(false); // 点击外部不关闭dialog
		View successView = LayoutInflater.from(context).inflate(
				R.layout.pop_tip_confirm_2, null);
		((TextView) successView.findViewById(R.id.tv_title)).setText(text);
		successView.findViewById(R.id.btn_ok).setOnClickListener(listener);
		dialog.setContentView(successView, new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		Window dialogWindow = dialog.getWindow();
		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
		lp.width = DensityUtil.getWidth((Activity) context)
				- DensityUtil.dip2px(context, 48);
		lp.height = LayoutParams.WRAP_CONTENT;
		// lp.gravity = Gravity.CENTER;
		dialogWindow.setAttributes(lp);
		dialog.show();
	}

	public static void dismiss() {
		if (dialog != null && dialog.isShowing()) {
			dialog.dismiss();
		}
	}
}
