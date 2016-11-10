package com.gzmelife.app.activity;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.x;
import org.xutils.common.Callback.CommonCallback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gzmelife.app.R;
import com.gzmelife.app.UrlInterface;
import com.gzmelife.app.bean.CoonFoodMenuBean;
import com.gzmelife.app.bean.LoginBean;
import com.gzmelife.app.bean.UserInfoBean;
import com.gzmelife.app.tools.DataUtil;
import com.gzmelife.app.tools.KappUtils;
import com.gzmelife.app.tools.MyLog;

@ContentView(R.layout.activity_forgetpwd_and_register)
public class ForgetpwdAndRegisterActivity extends BaseActivity implements
		OnClickListener {
	@ViewInject(R.id.tv_title)
	TextView tv_title;
	@ViewInject(R.id.et_phone)
	EditText et_phone;
	@ViewInject(R.id.et_authCode)
	EditText et_authCode;
	@ViewInject(R.id.btn_authCode)
	Button btn_authCode;
	@ViewInject(R.id.et_pwd)
	EditText et_pwd;
	@ViewInject(R.id.btn_OK)
	Button btn_OK;
	@ViewInject(R.id.cb_protocol)
	CheckBox cb_protocol;
	@ViewInject(R.id.btn_protocol)
	Button btn_protocol;
	@ViewInject(R.id.tv_title_left)
	TextView tv_title_left;
	boolean isRegister;
	String TAG = "RegisterActivity";
	@ViewInject(R.id.ll_protocol)
	LinearLayout ll_protocol;
	String dsf;
	String singid;
	String name;
	public static String code;
	LoginBean bean = new LoginBean();

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		isRegister = getIntent().getBooleanExtra("isRegister", false);
		dsf = getIntent().getStringExtra("DSF");
		singid = getIntent().getStringExtra("signId");
		name = getIntent().getStringExtra("name");
		MyLog.i(MyLog.TAG_I_INFO, "singId="+singid);
		if (isRegister) {
			if ("".equals(dsf)) {
				tv_title.setText("注册");
				btn_OK.setText("下一步");
				ll_protocol.setVisibility(View.VISIBLE);
			}else {
				tv_title.setText("绑定手机");
				btn_OK.setText("绑定手机");
				ll_protocol.setVisibility(View.VISIBLE);
			}
		} else {
			tv_title.setText("更改密码");
			btn_OK.setText("确定");
			ll_protocol.setVisibility(View.GONE);
		}
		btn_authCode.setOnClickListener(this);
		btn_OK.setOnClickListener(this);
		btn_protocol.setOnClickListener(this);
		tv_title_left.setVisibility(View.VISIBLE);
		tv_title_left.setText("登录");
		btn_protocol.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); // 下划线
		btn_protocol.getPaint().setAntiAlias(true);// 抗锯
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_authCode:

			if (!TextUtils.isEmpty(et_phone.getText())) {
				if (isMobileNO(et_phone.getText().toString()) == true) {
					if (et_phone.getText().length() == 11) {
						authCode();
					} else {
						KappUtils.showToast(context, "您输入手机号有误,请重新输入");
					}
				} else {
					KappUtils.showToast(context, "您输入手机号有误,请重新输入");
				}

			} else {
				KappUtils.showToast(context, "请输入手机号");
			}
			break;
		case R.id.btn_OK:
			if (TextUtils.isEmpty(et_phone.getText())) {
				KappUtils.showToast(context, "请输入手机号");
				return;
			}
			if (isMobileNO(et_phone.getText().toString()) == false) {
				KappUtils.showToast(context, "您输入手机号有误,请重新输入");
				return;
			}

			if (et_phone.getText().length() != 11) {
				KappUtils.showToast(context, "您输入手机号有误,请重新输入");
				return;
			}
			if (TextUtils.isEmpty(et_authCode.getText())) {
				KappUtils.showToast(context, "请输入验证码");
				return;
			}
			if (!et_authCode.getText().toString().equals(bean.getCode())) {
				KappUtils.showToast(context, "请输入正确验证码");
				return;
			}
			if (TextUtils.isEmpty(et_pwd.getText())) {
				KappUtils.showToast(context, "请输入密码");
				return;
			}

			if (et_pwd.getText().toString().length() < 6
					|| et_pwd.getText().toString().length() > 16) {
				KappUtils.showToast(context, "请填写6-16位的密码");
				return;
			}

			if (isRegister) {
				if (cb_protocol.isChecked()) {
					if (null== dsf) {
						UserInfoBean userBean = new UserInfoBean();
						userBean.setUserName(et_phone.getText().toString());
						userBean.setPassword(et_pwd.getText().toString());
						userBean.setValidCode(et_authCode.getText().toString());
						Intent intent = new Intent(
								ForgetpwdAndRegisterActivity.this,
								FillInInfoActivity.class);
						intent.putExtra("userBean", userBean);
						startActivity(intent);
					}else {
						UserInfoBean userBean = new UserInfoBean();
						userBean.setUserName(et_phone.getText().toString());
						userBean.setPassword(et_pwd.getText().toString());
						userBean.setValidCode(et_authCode.getText().toString());
						Intent intent = new Intent(
								ForgetpwdAndRegisterActivity.this,
								FillInInfoActivity.class);
						intent.putExtra("userBean", userBean);
						intent.putExtra("singId", singid);
						intent.putExtra("name", name);
						MyLog.i(MyLog.TAG_I_INFO, "singId="+singid);
						startActivity(intent);
					}
				} else {
					KappUtils.showToast(context, "请阅读并同意软件许可服务协议");
				}
			} else {
				// 忘记密码
				forgetPassword();
			}
			break;
		case R.id.btn_protocol:
			Intent intent = new Intent(context, ProtocolActivity.class);
			startActivity(intent);
			break;
		default:
			break;
		}
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

	/**
	 * 发出验证码请求
	 */
	private void authCode() {
		RequestParams entity = new RequestParams(
				UrlInterface.URL_GETVALIDATECODE);
		entity.addBodyParameter("phone", et_phone.getText().toString());
		if (isRegister) {
				entity.addBodyParameter("type", "1");
		}else {
			entity.addBodyParameter("type", "2");
		}
//		entity.addBodyParameter("type", (isRegister ? 1 : 2) + "");
		MyLog.i(TAG, "isRegister:" + (isRegister ? 1 : 2) + "");
		x.http().post(entity, new CommonCallback<String>() {
			@Override
			public void onSuccess(String result) {
				
				Gson gson = new Gson();
				JSONObject obj;
				try {
					obj = new JSONObject(result);
					bean = gson.fromJson(obj.getJSONObject("data").toString(),
							new TypeToken<LoginBean>() {
							}.getType());
					String msg = obj.getString("msg");
					if (!msg.equals("成功")) {
						KappUtils.showToast(context, msg);
						return;
					}else{
						KappUtils.showToast(context, "获取验证码成功，请注意查收！");
					} 
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				MyCount mc = new MyCount(60 * 1000, 1000);
				mc.start();
				btn_authCode.setEnabled(false);

				btn_authCode.setBackgroundResource(R.drawable.shape_gray_bg);
			}

			@Override
			public void onError(Throwable ex, boolean isOnCallback) {
				KappUtils.showToast(context, "获取验证码失败");
				btn_authCode.setText("重获验证码");
			}

			@Override
			public void onCancelled(CancelledException cex) {
			}

			@Override
			public void onFinished() {
			}
		});
	}

	/* 定义一个倒计时的内部类 */
	class MyCount extends CountDownTimer {
		public MyCount(int millisInFuture, int countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onFinish() {
			btn_authCode.setEnabled(true);
			btn_authCode.setBackgroundResource(R.drawable.shape_blue_bg);
			btn_authCode.setText("重获验证码");
		}

		@Override
		public void onTick(long millisUntilFinished) {
			btn_authCode.setText("(" + millisUntilFinished / 1000 + ")重新获取");
		}
	}

	/**
	 * 忘记密码
	 */
	private void forgetPassword() {
		RequestParams entity = new RequestParams(UrlInterface.URL_RESETPASSWORD);
		entity.addBodyParameter("phone", et_phone.getText().toString());
		entity.addBodyParameter("type", "1");
		entity.addBodyParameter("newPassword",
				KappUtils.md5(et_pwd.getText().toString().trim()));
		entity.addBodyParameter("validCode", et_authCode.getText().toString()
				.trim());
		x.http().post(entity, new CommonCallback<String>() {
			@Override
			public void onSuccess(String result) {
				KappUtils.showToast(context, "修改密码成功！");
				finish();
			}

			@Override
			public void onError(Throwable ex, boolean isOnCallback) {
			}

			@Override
			public void onCancelled(CancelledException cex) {
			}

			@Override
			public void onFinished() {
			}
		});

	}

}
