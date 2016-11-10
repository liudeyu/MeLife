package com.gzmelife.app.fragment;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.x;
import org.xutils.common.Callback.CommonCallback;
import org.xutils.http.RequestParams;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gzmelife.app.KappAppliction;
import com.gzmelife.app.R;
import com.gzmelife.app.UrlInterface;
import com.gzmelife.app.activity.AboutUsActivity;
import com.gzmelife.app.activity.AuditInformationActivity;
import com.gzmelife.app.activity.CollectionActivity;
import com.gzmelife.app.activity.DeviceDetailActivity;
import com.gzmelife.app.activity.FAQActivity;
import com.gzmelife.app.activity.FoodMaterialManageActivity;
import com.gzmelife.app.activity.LoginActivity;
import com.gzmelife.app.activity.MainActivity;
import com.gzmelife.app.activity.MyDataActivity;
import com.gzmelife.app.activity.MyDeviceActivity;
import com.gzmelife.app.activity.MyUploadCookbookActivity;
import com.gzmelife.app.activity.SettingsActivity;
import com.gzmelife.app.bean.MyUploadCookbookBean;
import com.gzmelife.app.device.Config;
import com.gzmelife.app.tools.DensityUtil;
import com.gzmelife.app.tools.ImgLoader;
import com.gzmelife.app.tools.KappUtils;
import com.gzmelife.app.tools.MyLog;
import com.gzmelife.app.tools.SharedPreferenceUtil;
import com.gzmelife.app.views.TipConfirmView;

public class PersonalCenterFragment extends Fragment implements OnClickListener {
	private TextView tv_title;

	private ImageView iv_titleLeft;

	private ImageView iv_head;

	private TextView tv_nickname;
	private TextView tv_uploadNumber;
	private TextView tv_autograph;

	private Button btn_titleRight;
	private Button btn_logout;

	private View layout_myData;
	private View layout_settings;
	private View layout_aboutUs;
	private View layout_faq;
	private View layout_myUploadCookbook;
	private View layout_myDevice;
	private View layout_foodMaterialManage;
	private View layout_myCollection;

	private LocalBroadcastManager broadcastManager;

	private Context context;
	@SuppressWarnings("unused")
	private MainActivity activity;
	
	public static int flag=0;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return LayoutInflater.from(getActivity()).inflate(
				R.layout.fragment_personal_center, null);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		context = this.getActivity();
		activity = (MainActivity) this.getActivity();
		initView();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(KappUtils.ACTION_PMS_STATUS);
		// intentFilter.addCategory();
		broadcastManager = LocalBroadcastManager.getInstance(getActivity());
		broadcastManager.registerReceiver(receiver, intentFilter);
	}

	BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(KappUtils.ACTION_PMS_STATUS)) {
				updatePmsStatus();
			}
		}
	};

	@Override
	public void onDestroy() {
		super.onDestroy();

		broadcastManager.unregisterReceiver(receiver);
	}

	@Override
	public void onResume() {
		super.onResume();
		updatePmsStatus();
		if (KappAppliction.myApplication.getUser() != null) {
			new ImgLoader(context).showPic(KappAppliction.myApplication
					.getUser().getLogoPath(), iv_head);
			tv_nickname.setText(KappAppliction.myApplication.getUser()
					.getNickName());
			tv_autograph.setText(KappAppliction.myApplication.getUser()
					.getAutograph());
			getUploadNumber();
		}
	}

	
	
	/** 后台返回成功，则继续；否则给出提示 */
	private boolean isSuccess(JSONObject obj) {
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

	private void getUploadNumber() {
		RequestParams params = new RequestParams(
				UrlInterface.URL_MY_UPLOAD_COOKBOOK);
		params.addBodyParameter("userId", KappAppliction.myApplication
				.getUser().getId() + "");
		x.http().post(params, new CommonCallback<String>() {
			@Override
			public void onSuccess(String result) {
				MyLog.i(MyLog.TAG_I_JSON, result);
				try {
					Gson gson = new Gson();
					JSONObject obj = new JSONObject(result);
					if (!isSuccess(obj)) {
						return;
					}

					List<MyUploadCookbookBean> listTemp = gson.fromJson(obj
							.getJSONObject("data").getJSONArray("menubooks")
							.toString(),
							new TypeToken<List<MyUploadCookbookBean>>() {
							}.getType());
					tv_uploadNumber.setText("查看菜谱上传记录（数量" + listTemp.size()
							+ "）");
				} catch (JSONException e) {
					e.printStackTrace();
				}
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

	private void initView() {
		tv_title = (TextView) getView().findViewById(R.id.tv_title);
		iv_titleLeft = (ImageView) getView().findViewById(R.id.iv_titleLeft);
		iv_head = (ImageView) getView().findViewById(R.id.iv_head);
		layout_myData = getView().findViewById(R.id.layout_myData);
		btn_titleRight = (Button) getView().findViewById(R.id.btn_titleRight);
		btn_logout = (Button) getView().findViewById(R.id.btn_logout);
		layout_settings = getView().findViewById(R.id.layout_settings);
		layout_aboutUs = getView().findViewById(R.id.layout_aboutUs);
		layout_faq = getView().findViewById(R.id.layout_faq);
		layout_myUploadCookbook = getView().findViewById(
				R.id.layout_myUploadCookbook);
		layout_myDevice = getView().findViewById(R.id.layout_myDevice);
		layout_foodMaterialManage = getView().findViewById(
				R.id.layout_foodMaterialManage);
		tv_nickname = (TextView) getView().findViewById(R.id.tv_nickname);
		tv_uploadNumber = (TextView) getView().findViewById(
				R.id.tv_uploadNumber);
		tv_autograph = (TextView) getView().findViewById(R.id.tv_autograph);
		layout_myCollection = getView().findViewById(R.id.layout_myCollection);

		tv_title.setText("个人中心");
		btn_titleRight.setVisibility(View.VISIBLE);
		btn_titleRight.setText("审核信息");
		btn_titleRight.setOnClickListener(this);
		layout_myData.setOnClickListener(this);
		layout_settings.setOnClickListener(this);
		layout_aboutUs.setOnClickListener(this);
		btn_logout.setOnClickListener(this);
		layout_faq.setOnClickListener(this);
		layout_myUploadCookbook.setOnClickListener(this);
		layout_myDevice.setOnClickListener(this);
		layout_foodMaterialManage.setOnClickListener(this);
		layout_myCollection.setOnClickListener(this);

		int normal = DensityUtil.dip2px(context, 12);
		iv_titleLeft.setPadding(normal, normal, normal, normal);
		iv_titleLeft.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!TextUtils.isEmpty(Config.SERVER_HOST_NAME)) { // 未连接，点击无效
					startActivity(new Intent(context,
							DeviceDetailActivity.class));
				}
			}
		});

		updatePmsStatus();
	}

	private void updatePmsStatus() {
		if (TextUtils.isEmpty(Config.SERVER_HOST_NAME)) {
			iv_titleLeft.setImageResource(R.drawable.icon05);
		} else {
			if (Config.PMS_ERRORS.size() > 0) {
				iv_titleLeft.setImageResource(R.drawable.icon06);
			} else {
				iv_titleLeft.setImageResource(R.drawable.icon04);
			}
			if(Config.isConnext=true){
				iv_titleLeft.setImageResource(R.drawable.icon04);
			}else{
				iv_titleLeft.setImageResource(R.drawable.icon06);
			}
		}
	}

	@Override
	public void onClick(View v) {
		Intent intent = null;
		switch (v.getId()) {
		case R.id.layout_myData:
			Intent intent2 = new Intent(context, MyDataActivity.class);
			startActivityForResult(intent2, 111);
			break;
		case R.id.layout_settings:
			intent = new Intent(context, SettingsActivity.class);
			break;
		case R.id.layout_aboutUs:
			intent = new Intent(context, AboutUsActivity.class);
			intent.putExtra("flag", KappUtils.FLAG_ABOUT_US);
			break;
		case R.id.layout_faq:
			intent = new Intent(context, FAQActivity.class);
			break;
		case R.id.layout_myUploadCookbook:
			intent = new Intent(context, MyUploadCookbookActivity.class);
			break;
		case R.id.layout_foodMaterialManage:
			intent = new Intent(context, FoodMaterialManageActivity.class);
			break;
		case R.id.layout_myDevice:
			intent = new Intent(context, MyDeviceActivity.class);
			break;
		case R.id.btn_titleRight:
			intent = new Intent(context, AuditInformationActivity.class);
			break;
		case R.id.btn_logout:
			TipConfirmView.showConfirmDialog(context, "是否确认退出登录?",
					new OnClickListener() {
						@Override
						public void onClick(View v) {
							TipConfirmView.dismiss();
							SharedPreferenceUtil.setUserAccount(context, "");
							SharedPreferenceUtil.setUserPwd(context, "");
							SharedPreferenceUtil.setUser(context, null);
							KappAppliction.setLiLogin(2);
							KappAppliction.myApplication.setUser(null);
							
							startActivity(new Intent(context,
									LoginActivity.class));
						}
					});
			break;
		case R.id.layout_myCollection:
//			intent = new Intent(context, MainActivity.class);
//			intent.putExtra("flag", "2");
//			MainActivity activity = (MainActivity) getActivity();
////			activity.localFlag = 2;
//			activity.setVis(2);
			intent = new Intent(getActivity(),CollectionActivity.class);
			break;
		}
		if (intent != null) {
			startActivity(intent);
		}
	}
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 111) {
			tv_nickname.setText(KappAppliction.myApplication.getUser()
					.getNickName());
			tv_autograph.setText(KappAppliction.myApplication.getUser()
					.getAutograph());
		}
	}
}
