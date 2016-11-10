package com.gzmelife.app.activity;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.x;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.gzmelife.app.R;
import com.gzmelife.app.tools.CountDownTimerUtil;
import com.gzmelife.app.tools.KappUtils;
import com.gzmelife.app.tools.ShowDialogUtil;
import com.gzmelife.app.tools.SystemBarUtils;

public class BaseActivity extends FragmentActivity {
	private AlertDialog dlg;

	public Context context;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		 x.view().inject(this);
		SystemBarUtils.applyKitKatTranslucency(this);
		context = getApplicationContext();
	}

	public void back(View v) {
		this.finish();
	}

	public void showDlg() {
		if (null != this && null != dlg && !dlg.isShowing()) {
			dlg.show();
		} else if (null != this && null == dlg) {
			dlg = ShowDialogUtil.getShowDialog(this,
					R.layout.dialog_progressbar, 0, 0, false);
		}
	}

	public void closeDlg() {
		if (null != this && null != dlg && dlg.isShowing()) {
			dlg.dismiss();
		}
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if (ev.getAction() == MotionEvent.ACTION_DOWN) {
			View v = getCurrentFocus();
			if (isShouldHideInput(v, ev)) {
				hideSoftInput(v.getWindowToken());
			}
		}
		return super.dispatchTouchEvent(ev);
	}

	/**
	 * @param v
	 * @param event
	 * @return
	 */
	private boolean isShouldHideInput(View v, MotionEvent event) {
		if (v != null && (v instanceof EditText)) {
			int[] l = { 0, 0 };
			v.getLocationInWindow(l);
			int left = l[0], top = l[1], bottom = top + v.getHeight(), right = left
					+ v.getWidth();
			if (event.getX() > left && event.getX() < right
					&& event.getY() > top && event.getY() < bottom) {
				return false;
			} else {
				return true;
			}
		}
		return false;
	}

	/**
	 * @param token
	 */
	private void hideSoftInput(IBinder token) {
		if (token != null) {
			InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			im.hideSoftInputFromWindow(token,
					InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	public class TimeCountOut extends CountDownTimerUtil {
		private OnEvent onEvent;

		public TimeCountOut(long millisInFuture, long countDownInterval, OnEvent onEvent2) {
			super(millisInFuture, countDownInterval);
			this.onEvent = onEvent2;
		}

		@Override
		public void onFinish() {
			if (dlg.isShowing()) {
				// KappUtils.showToast(BaseActivity.this, "超时，请重试");
				closeDlg();
			}
			if (onEvent != null) {
				onEvent.onFinish();
			}
		}

		@Override
		public void onTick(long millisUntilFinished) {
			if (onEvent != null) {
				onEvent.onTick(millisUntilFinished);
			}
		}
	}

	public interface OnEvent {
		void onFinish();

		void onTick(long millisUntilFinished);
	}

	/** 后台返回成功，则继续；否则给出提示 */
	public boolean isSuccess(JSONObject obj) {
		try {
			if (!obj.getString("status").equals("10001")) {
				String errorMsg = obj.getString("msg");
				if (TextUtils.isEmpty(errorMsg)) {
					errorMsg = "后台错误";
				}
				KappUtils.showToast(context, errorMsg);
				return false;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return true;
	}

	public String getEditTextString(EditText et) {
		if (et == null) {
			return "";
		}
		return et.getText().toString().trim();
	}
}
