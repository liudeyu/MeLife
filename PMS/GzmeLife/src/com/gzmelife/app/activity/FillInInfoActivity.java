package com.gzmelife.app.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.x;
import org.xutils.common.Callback.CancelledException;
import org.xutils.common.Callback.CommonCallback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import cn.jpush.android.data.s;

import com.google.gson.Gson;
import com.gzmelife.app.KappAppliction;
import com.gzmelife.app.R;
import com.gzmelife.app.UrlInterface;
import com.gzmelife.app.adapter.DeviceAdapter;
import com.gzmelife.app.bean.UserInfoBean;
import com.gzmelife.app.tools.CameraUtil;
import com.gzmelife.app.tools.KappUtils;
import com.gzmelife.app.tools.MyLog;
import com.gzmelife.app.tools.SharedPreferenceUtil;
import com.gzmelife.app.views.CircleImageView;
import com.gzmelife.app.views.ListViewForScrollView;
import com.zxing.activity.CaptureActivity;

@ContentView(R.layout.activity_fillininfo)
public class FillInInfoActivity extends BaseActivity implements OnClickListener {

	DeviceAdapter deviceAdapter;
	@ViewInject(R.id.lv_device)
	ListViewForScrollView lv_device;
	@ViewInject(R.id.iv_head)
	CircleImageView iv_head;
	@ViewInject(R.id.et_nickName)
	EditText et_nickName;
	@ViewInject(R.id.tv_styleOfCooking)
	TextView tv_styleOfCooking;
	@ViewInject(R.id.tv_taste)
	TextView tv_taste;
	@ViewInject(R.id.iv_code)
	ImageView iv_code;
	@ViewInject(R.id.btn_submit)
	Button btn_submit;
	@ViewInject(R.id.tv_title)
	TextView tv_title;
	@ViewInject(R.id.tv_title_left)
	TextView tv_title_left;
	CameraUtil cameraUtil;
	String headpath;
	private final int STYLEOFCOOKING_REQUESTCODE = 0x111;
	private final int TASTE_REQUESTCODE = 0x222;
	private final int PHOTO_PIC = 0x333;
	String TAG = "FillInInfoActivity";
	List<String> codes;
	String tasteIds;
	String cookingStyleIds;
	UserInfoBean userBean;
	String urlHeadPath;
	String singid;
	String name;
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		userBean = (UserInfoBean) getIntent().getSerializableExtra("userBean");
		singid = getIntent().getStringExtra("singId");
		name = getIntent().getStringExtra("name");
		MyLog.i(MyLog.TAG_I_INFO, "注册时候的singId="+singid);
		MyLog.i(MyLog.TAG_I_INFO, "注册时候的name="+name);
		codes = new ArrayList<String>();
		deviceAdapter = new DeviceAdapter(this, codes);
		lv_device.setAdapter(deviceAdapter);
		iv_head.setOnClickListener(this);
		tv_taste.setOnClickListener(this);
		tv_styleOfCooking.setOnClickListener(this);
		iv_code.setOnClickListener(this);
		cameraUtil = new CameraUtil(FillInInfoActivity.this,
				FillInInfoActivity.this);
		btn_submit.setOnClickListener(this);
		tv_title.setText("填写资料");
		tv_title_left.setVisibility(View.VISIBLE);
		tv_title_left.setText("注册");
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_taste:
			Intent intent1 = new Intent(FillInInfoActivity.this,
					TasteActivity.class);
			startActivityForResult(intent1, TASTE_REQUESTCODE);
			break;
		case R.id.tv_styleOfCooking:
			Intent intent = new Intent(FillInInfoActivity.this,
					CookingStyleActivity.class);
			startActivityForResult(intent, STYLEOFCOOKING_REQUESTCODE);
			break;
		case R.id.iv_code:
			intent = new Intent(context, CaptureActivity.class);
			startActivityForResult(intent, PHOTO_PIC);
			break;
		case R.id.btn_submit:
			register();
			break;
		case R.id.iv_head:
			cameraUtil.toCameraPhoto(true);
			break;
		default:
			break;
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case STYLEOFCOOKING_REQUESTCODE:
				// tv_styleOfCooking.setText(data.getStringExtra("tasteNames"));
				// cookingStyleIds = data.getStringExtra("tasteIds");
				tv_styleOfCooking.setText(data
						.getStringExtra("cookingStyleNames"));
				cookingStyleIds = data.getStringExtra("cookingStyleIds");

				break;
			case TASTE_REQUESTCODE:
				tv_taste.setText(data.getStringExtra("tasteNames"));
				tasteIds = data.getStringExtra("tasteIds");
				break;
			case PHOTO_PIC:
				Bundle bundle = data.getExtras();
				String result = bundle.getString("result");
				deviceAdapter.addItem(result);
				break;
			default:
				headpath = cameraUtil.getImgPath(requestCode, resultCode, data);
				if (!TextUtils.isEmpty(headpath)) {
					iv_head.setImageBitmap(BitmapFactory.decodeFile(headpath));
					uploadHeadPortrait(headpath);
				}
				break;
			}

		}
	}

	/**
	 * 上传头像
	 * 
	 * @param headpath
	 */
	private void uploadHeadPortrait(String headpath) {
		RequestParams entity = new RequestParams(
				UrlInterface.URL_FILEUPLOADBYBYTE);
		MyLog.i(TAG, "路径：" + UrlInterface.URL_FILEUPLOADBYBYTE);
		entity.setMultipart(true);
		entity.addBodyParameter("file", new File(headpath),
				"multipart/form-data");
		x.http().post(entity, new CommonCallback<String>() {
			@Override
			public void onSuccess(String result) {
				try {
					JSONObject jsonObject = new JSONObject(result);
					urlHeadPath = jsonObject.getJSONObject("data").getString(
							"path");
					KappUtils.showToast(context, "上传头像成功！");
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onError(Throwable ex, boolean isOnCallback) {
				KappUtils.showToast(context, "上传头像失败！");
			}

			@Override
			public void onCancelled(CancelledException cex) {
			}

			@Override
			public void onFinished() {
			}
		});
	}

	private void register() {
		RequestParams entity = new RequestParams(UrlInterface.URL_REGISTER);
		MyLog.i(TAG, "===============1===>" + singid);
		if (null == singid) {
			entity.addBodyParameter("type", "1");
		}else {
			entity.addBodyParameter("type", "2");
			entity.addBodyParameter("uid", singid);
			entity.addBodyParameter("platform", name);
		}
		entity.addBodyParameter("userName", userBean.getUserName());
		entity.addBodyParameter("password",
				KappUtils.md5(userBean.getPassword()));
		entity.addBodyParameter("validCode", userBean.getValidCode());
		if (!TextUtils.isEmpty(et_nickName.getText().toString())) {
			entity.addBodyParameter("nickName", et_nickName.getText()
					.toString());
		} else {
			KappUtils.showToast(context, "请填写昵称！");
			return;
		}
		if (!TextUtils.isEmpty(urlHeadPath)) {
			entity.addBodyParameter("logoPath", urlHeadPath);
		} else {
			KappUtils.showToast(context, "请上传头像！");
			return;
		}
		if (!TextUtils.isEmpty(cookingStyleIds)) {
			entity.addBodyParameter("cuisineMenuCategoryIds", cookingStyleIds);
		}
		if (!TextUtils.isEmpty(tasteIds)) {
			entity.addBodyParameter("flavorMenuCategoryIds", tasteIds);
		}
		StringBuffer stringBuffer = new StringBuffer();
		for (int i = 0; i < codes.size(); i++) {
			stringBuffer.append(codes.get(i));
			if (i != codes.size() - 1) {
				stringBuffer.append(",");
			}
		}
		entity.addBodyParameter("equipments", stringBuffer.toString());
		x.http().post(entity, new CommonCallback<String>() {
			@Override
			public void onSuccess(String result) {
				KappUtils.showToast(context, "注册成功");
				login();
//				 Intent intent = new Intent(FillInInfoActivity.this,
//				 LoginActivity.class);
////				 intent.putExtra("flag", "110");
//				 startActivity(intent);
				MyLog.i(TAG, "===============1===>" + result);
			}

			@Override
			public void onError(Throwable ex, boolean isOnCallback) {
				MyLog.i(TAG, "==============2===>" + ex.getMessage());
				KappUtils.showToast(context, ex.getMessage() + "");
			}

			@Override
			public void onCancelled(CancelledException cex) {
				KappUtils.showToast(context, cex.getMessage() + "");
				MyLog.i(TAG, "==============2===>" + cex.getMessage());
			}

			@Override
			public void onFinished() {
			}
		});
	}

	public void login() {
		MyLog.d("--->login");
		RequestParams entityParams = new RequestParams(UrlInterface.URL_LOGIN);

//		entityParams.addBodyParameter("userName",
//				SharedPreferenceUtil.getUserAccount(context));
//		entityParams.addBodyParameter("password",
//				SharedPreferenceUtil.getUserPwd(context));
		entityParams.addBodyParameter("userName", userBean.getUserName());
		entityParams.addBodyParameter("password",
				KappUtils.md5(userBean.getPassword()));
		if (!"".equals(singid)) {
			entityParams.addBodyParameter("type", "1");
		}else {
			entityParams.addBodyParameter("type", "2");
		}
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
						SharedPreferenceUtil.setUser(context, user);
						KappAppliction.myApplication.setUser(user);
						KappAppliction.setLiLogin(1);
						Intent intent = new Intent(FillInInfoActivity.this,
								MainActivity.class);
						intent.putExtra("flag", "110");
						startActivity(intent);
						FillInInfoActivity.this.finish();

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
				// KappUtils.showToast(context, "登陆失败~");
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

}
