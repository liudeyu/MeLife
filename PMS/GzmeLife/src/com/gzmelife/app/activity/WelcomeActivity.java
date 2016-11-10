package com.gzmelife.app.activity;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.x;
import org.xutils.common.Callback.CommonCallback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import cn.jpush.android.api.JPushInterface;
import cn.sharesdk.framework.ShareSDK;

import com.google.gson.Gson;
import com.gzmelife.app.KappAppliction;
import com.gzmelife.app.R;
import com.gzmelife.app.UrlInterface;
import com.gzmelife.app.bean.UserInfoBean;
import com.gzmelife.app.tools.KappUtils;
import com.gzmelife.app.tools.MyLog;
import com.gzmelife.app.tools.SharedPreferenceUtil;
import com.umeng.update.UmengUpdateAgent;

@ContentView(R.layout.activity_welcome)
public class WelcomeActivity extends Activity {

	String TAG = "WelcomeActivity";
	Context context = this;
	private SharedPreferences preferences;
	private Editor editor;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		preferences = getSharedPreferences("phone", Context.MODE_PRIVATE);
		x.view().inject(this);
		UmengUpdateAgent.setUpdateOnlyWifi(true);
		UmengUpdateAgent.update(this);
//		ShareSDK.initSDK(this);
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				if (preferences.getBoolean("firststart", true)) {
					editor = preferences.edit();
					// 将登录标志位设置为false，下次登录时不再显示引导页面
					editor.putBoolean("firststart", false);
					editor.commit();
					Intent intent = new Intent();
					intent.setClass(WelcomeActivity.this, StartActivity.class);
					WelcomeActivity.this.startActivity(intent);
					WelcomeActivity.this.finish();
				} else {
					try {
						MyLog.d("--->"
								+ SharedPreferenceUtil.getUserAccount(context)
								+ ","
								+ SharedPreferenceUtil.getUserPwd(context));
						Intent intent = new Intent(WelcomeActivity.this,
								MainActivity.class);
						startActivity(intent);
						finish();
//						if (!TextUtils.isEmpty(SharedPreferenceUtil
//								.getUserAccount(context))
//								&& !TextUtils.isEmpty(SharedPreferenceUtil
//										.getUserPwd(context))) {
//							ConnectivityManager connectivity = (ConnectivityManager) context
//									.getSystemService(Context.CONNECTIVITY_SERVICE);
//							if (connectivity != null) {
//								NetworkInfo info = connectivity
//										.getActiveNetworkInfo();
//								if (info != null && info.isConnected()) {
//									// 当前网络是连接的
//									if (info.getState() == NetworkInfo.State.CONNECTED) {
//										// 当前所连接的网络可用
//										login();
//
//									} else {
//										Intent intent = new Intent(
//												WelcomeActivity.this,
//												MainActivity.class);
//										startActivity(intent);
//										WelcomeActivity.this.finish();
//									}
//								}
//							}
//						} else {
//							Intent intent = new Intent(WelcomeActivity.this,
//									MainActivity.class);
//							startActivity(intent);
//							finish();
//						}

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}, 3000);
	}

	public void login() {
		MyLog.d("--->login");
		RequestParams entityParams = new RequestParams(UrlInterface.URL_LOGIN);

		entityParams.addBodyParameter("userName",
				SharedPreferenceUtil.getUserAccount(context));
		entityParams.addBodyParameter("password",
				SharedPreferenceUtil.getUserPwd(context));
		entityParams.addBodyParameter("type", "1");
		x.http().post(entityParams, new CommonCallback<String>() {
			@Override
			public void onSuccess(String result) {
				MyLog.i(TAG, "登陆：" + result);
				MyLog.i(MyLog.TAG_I_JSON, result);
				try {
					JSONObject jsonObject = new JSONObject(result);
					if (jsonObject.getString("status").equals("10001")) {
						Gson gson = new Gson();
						UserInfoBean user = gson.fromJson(jsonObject
								.getJSONObject("data").getJSONObject("user")
								.toString(), UserInfoBean.class);
						KappAppliction.myApplication.setUser(user);
						KappAppliction.setLiLogin(1);
						Intent intent = new Intent(WelcomeActivity.this,
								MainActivity.class);
						startActivity(intent);
						WelcomeActivity.this.finish();

					} else {
						KappUtils.showToast(context, "登陆失败！");
						KappAppliction.setLiLogin(2);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onError(Throwable ex, boolean isOnCallback) {
				KappUtils.showToast(context, "网络连接错误~");
				KappAppliction.setLiLogin(2);
			}

			@Override
			public void onCancelled(CancelledException cex) {
			}

			@Override
			public void onFinished() {
			}
		});
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		 JPushInterface.onResume(this);
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		JPushInterface.onPause(this);
	}

}
