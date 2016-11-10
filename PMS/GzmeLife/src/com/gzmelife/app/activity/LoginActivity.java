package com.gzmelife.app.activity;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.x;
import org.xutils.common.Callback.CommonCallback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.PlatformDb;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.wechat.friends.Wechat;

import com.google.gson.Gson;
import com.gzmelife.app.KappAppliction;
import com.gzmelife.app.R;
import com.gzmelife.app.UrlInterface;
import com.gzmelife.app.bean.UserInfoBean;
import com.gzmelife.app.tools.KappUtils;
import com.gzmelife.app.tools.MyLog;
import com.gzmelife.app.tools.SharedPreferenceUtil;

@ContentView(R.layout.actvitiy_login)
public class LoginActivity extends BaseActivity implements OnClickListener,
		PlatformActionListener {
	@ViewInject(R.id.et_phone)
	EditText et_phone;
	@ViewInject(R.id.et_pwd)
	EditText et_pwd;
	@ViewInject(R.id.btn_forgetPwd)
	Button btn_forgetPwd;
	@ViewInject(R.id.btn_login)
	Button btn_login;
	@ViewInject(R.id.btn_qq)
	Button btn_qq;
	@ViewInject(R.id.btn_wchat)
	Button btn_wchat;
	@ViewInject(R.id.tv_title)
	TextView tv_title;
	@ViewInject(R.id.btn_titleRight)
	Button btn_titleRight;
	@ViewInject(R.id.iv_titleLeft)
	ImageView iv_titleleft;
	String TAG = "LoginActivity";
	private Handler handler;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		btn_titleRight.setVisibility(View.VISIBLE);
		tv_title.setText("登录");
		btn_titleRight.setText("注册");
		iv_titleleft.setVisibility(View.INVISIBLE);
		btn_titleRight.setOnClickListener(this);
		btn_forgetPwd.setOnClickListener(this);
		btn_login.setOnClickListener(this);
		btn_wchat.setOnClickListener(this);
		btn_qq.setOnClickListener(this);
		handler = new Handler(new Handler.Callback() {

			@Override
			public boolean handleMessage(Message msg) {
				// TODO Auto-generated method stub
				switch (msg.what) {
				case 0:
					KappUtils.showToast(context, "已取消");
					break;
				case -1:
					// 授权失败
//					KappUtils.showToast(context, "发生错误");
					KappUtils.showToast(context, "授权失败，请查看是否安装"+
							 msg.obj.toString());
					break;
				case 1:
					// 授权成功
					KappUtils.showToast(context, "授权成功");
					 String str = msg.obj.toString();
					 String id = str.substring(0, str.indexOf(','));
					 MyLog.i(MyLog.TAG_I_INFO, "id="+id);
					// // showToast(Login_Index_Activity.this, "----id="+id);
					  String name = str.substring(str.indexOf(',') + 1);
					//
					// //第三方登录
					// RequestParams params = new RequestParams();
					// params.addBodyParameter("id",DataUtil.md5(id));
					// params.addBodyParameter("type","1");//1是用户端，2是护师端
					//
					  if (name.equals("QQ")) {
						name = "qq";
					}else {
						name="wechat";
					}
					RequestParams entityParams = new RequestParams(UrlInterface.URL_LOGIN);
//					entityParams
//							.addBodyParameter("userName", id);
					entityParams.addBodyParameter("password",
							KappUtils.md5(name+id));
					entityParams.addBodyParameter("type", "2");
//					showDlg();
					thirdLogin(entityParams,name,id,name);
					MyLog.i(MyLog.TAG_I_INFO, "third="+name + id +"   " +KappUtils.md5(name +id)+"   "+KappUtils.md5(name +id));
					break;
				}
				return false;
			}
		});
	}

	/**
	 * 验证手机格式
	 */
	public static boolean isMobileNO(String mobiles) {
		/*
		 * 移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
		 * 联通：130、131、132、152、155、156、185、186 电信：133、153、180、189、（1349卫通）
		 * 总结起来就是第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9
		 */
		String telRegex = "[1][35874]\\d{9}";// "[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
		if (TextUtils.isEmpty(mobiles)) {
			return false;
		} else {
			return mobiles.matches(telRegex);
		}
	}

	@Override
	public void onClick(View v) {
		Intent intent;
		switch (v.getId()) {
		case R.id.btn_login:
			// UserBean user = new UserBean();
			// user.setId(6);
			// setUserBean(user);
			// LoginActivity.this.finish();

			if (TextUtils.isEmpty(et_phone.getText().toString())) {
				KappUtils.showToast(context, "请填写手机号码");
				return;
			}
			if (TextUtils.isEmpty(et_pwd.getText().toString())) {
				KappUtils.showToast(context, "请填写密码");
				return;
			}
			if (isMobileNO(et_phone.getText().toString()) == true) {
				if (et_phone.getText().length() == 11) {
					login();
				} else {
					KappUtils.showToast(context, "您输入手机号有误,请重新输入");
					return;
				}
			} else {
				KappUtils.showToast(context, "您输入手机号有误,请重新输入");
				return;
			}

			break;
		case R.id.btn_forgetPwd:
			intent = new Intent(LoginActivity.this,
					ForgetpwdAndRegisterActivity.class);
			intent.putExtra("isRegister", false);
			startActivity(intent);
			break;
		case R.id.btn_qq:
			Platform qq = ShareSDK.getPlatform(context, QQ.NAME);
			authorize(qq);
			break;
		case R.id.btn_wchat:
			Platform weChat = ShareSDK.getPlatform(context, Wechat.NAME);
			authorize(weChat);
			break;
		case R.id.btn_titleRight:
			intent = new Intent(LoginActivity.this,
					ForgetpwdAndRegisterActivity.class);
			intent.putExtra("isRegister", true);
			startActivity(intent);
			break;
		}
	}

	public void login() {
		RequestParams entityParams = new RequestParams(UrlInterface.URL_LOGIN);
		entityParams
				.addBodyParameter("userName", et_phone.getText().toString());
		entityParams.addBodyParameter("password",
				KappUtils.md5(et_pwd.getText().toString()));
		entityParams.addBodyParameter("type", "1");
		x.http().post(entityParams, new CommonCallback<String>() {
			@Override
			public void onSuccess(String result) {

				MyLog.i(TAG, "登录：" + result);
				// {"data":{"user":{"id":15,"logoPath":"/a.jpg","nickName":"呵呵"}},"msg":"成功","status":"10001","time":"2016-02-26 14:55:44"}
				try {
					JSONObject jsonObject = new JSONObject(result);
					if (jsonObject.getString("status").equals("10001")) {
						Gson gson = new Gson();
						UserInfoBean user = gson.fromJson(jsonObject
								.getJSONObject("data").getJSONObject("user")
								.toString(), UserInfoBean.class);
						KappAppliction.myApplication.setUser(user);
						KappAppliction.setLiLogin(1);
						SharedPreferenceUtil.setUser(LoginActivity.this, user);
						SharedPreferenceUtil.setUserAccount(context, et_phone
								.getText().toString());
						SharedPreferenceUtil.setUserPwd(
								context,
								KappUtils.md5(et_pwd.getText().toString()
										.trim()));
						LoginActivity.this.finish();
					} else {
						KappUtils.showToast(context, "帐号或密码错误");
						KappAppliction.setLiLogin(2);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onError(Throwable ex, boolean isOnCallback) {
				KappUtils.showToast(context, "帐号或密码错误");
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

	protected void thirdLogin(RequestParams params, final String passwordMD5,
			final String signid,final String name) {
		MyLog.i("TAG",UrlInterface.URL_LOGIN+"?password="+ passwordMD5 +"&type=2"+ signid +"    "+ name);
		x.http().post(params, new CommonCallback<String>() {
			@Override
			public void onSuccess(String result) {
				MyLog.i(TAG, "name：" + name);
				MyLog.i(TAG, "登录：" + result);
				// {"data":{"user":{"id":15,"logoPath":"/a.jpg","nickName":"呵呵"}},"msg":"成功","status":"10001","time":"2016-02-26 14:55:44"}
				try {
					JSONObject jsonObject = new JSONObject(result);
					if (jsonObject.getString("status").equals("10001")) {
						Gson gson = new Gson();
						UserInfoBean user = gson.fromJson(jsonObject
								.getJSONObject("data").getJSONObject("user")
								.toString(), UserInfoBean.class);
						KappAppliction.myApplication.setUser(user);
						SharedPreferenceUtil.setUser(LoginActivity.this, user);
						KappAppliction.setLiLogin(1); 
						SharedPreferenceUtil.setUserAccount(context, et_phone
								.getText().toString());
						SharedPreferenceUtil.setUserPwd(
								context,
								KappUtils.md5(passwordMD5));
						LoginActivity.this.finish();
					} else if (jsonObject.getString("status").equals("20010")) {
						Intent intent =new Intent(LoginActivity.this,ForgetpwdAndRegisterActivity.class);
						intent.putExtra("isRegister", true);
						intent.putExtra("DSF", "dsf");
						intent.putExtra("signId", signid);
						intent.putExtra("name", name);
						LoginActivity.this.startActivity(intent);
					}else {
						KappUtils.showToast(context, "帐号或密码错误");
						KappAppliction.setLiLogin(2);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onError(Throwable ex, boolean isOnCallback) {
				KappUtils.showToast(context, "帐号或密码错误");
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

	// 点击第三方后的发起授权
	private void authorize(Platform plat) {
		if (plat == null) {
			KappUtils.showToast(context, "授权失败");
			return;
		}
		plat.setPlatformActionListener(this);
		// 关闭SSO授权
		plat.SSOSetting(false);
		plat.showUser(null);
	}

	@Override
	public void onCancel(Platform platform, int action) {
		// TODO Auto-generated method stub
		if (action == Platform.ACTION_USER_INFOR) {
			handler.sendEmptyMessage(0);
		}
	}

	@Override
	public void onComplete(Platform platform, int action,
			HashMap<String, Object> arg2) {
		// TODO Auto-generated method stub
		// 解析部分用户资料字段
		if (action == Platform.ACTION_USER_INFOR) {
			PlatformDb platDB = platform.getDb();
			String id = platDB.getUserId();
			String name = platDB.getPlatformNname();

			Message msg = new Message();
			msg.what = 1;
			msg.obj = id + "," + name;
			ShareSDK.removeCookieOnAuthorize(true);
			if (name.equals("QQ")) {
			ShareSDK.getPlatform(context, QQ.NAME).removeAccount();
			}else {
			ShareSDK.getPlatform(context, Wechat.NAME).removeAccount();
			}
			handler.sendMessage(msg);
		}
	}

	@Override
	public void onError(Platform platform, int action, Throwable throwable) {
		// TODO Auto-generated method stub
		if (action == Platform.ACTION_USER_INFOR) {
			String name = platform.getDb().getPlatformNname();
			Message msg = new Message();
			msg.what = -1;
			msg.obj = name;
			handler.sendMessage(msg);
		}
		throwable.printStackTrace();
	}

}
