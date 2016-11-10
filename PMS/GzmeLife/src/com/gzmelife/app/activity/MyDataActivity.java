package com.gzmelife.app.activity;

import java.io.File;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.x;
import org.xutils.common.Callback.CommonCallback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gzmelife.app.KappAppliction;
import com.gzmelife.app.R;
import com.gzmelife.app.UrlInterface;
import com.gzmelife.app.bean.UserInfoBean;
import com.gzmelife.app.tools.CameraUtil;
import com.gzmelife.app.tools.ImgLoader;
import com.gzmelife.app.tools.KappUtils;
import com.gzmelife.app.tools.MyLog;
import com.gzmelife.app.tools.ScaleImageSizeUtil;
import com.gzmelife.app.views.CircleImageView;
import com.gzmelife.app.views.wheelview.OnWheelChangedListener;
import com.gzmelife.app.views.wheelview.WheelView;
import com.gzmelife.app.views.wheelview.adapter.CityWheelAdapter;
import com.gzmelife.app.views.wheelview.adapter.DistrictWheelAdapter;
import com.gzmelife.app.views.wheelview.adapter.ProvinceWheelAdapter;

@ContentView(R.layout.activity_my_data)
public class MyDataActivity extends BaseActivity implements OnClickListener {
	@ViewInject(R.id.tv_title)
	TextView tv_title;
	@ViewInject(R.id.tv_birthplace)
	TextView tv_birthplace;
	@ViewInject(R.id.tv_residence)
	TextView tv_residence;
	@ViewInject(R.id.tv_myCookingStyle)
	TextView tv_myCookingStyle;
	@ViewInject(R.id.tv_taste)
	TextView tv_taste;
	@ViewInject(R.id.tv_gender)
	TextView tv_gender;
	@ViewInject(R.id.tv_email)
	TextView tv_email;
	@ViewInject(R.id.tv_autograph)
	TextView tv_autograph;
	@ViewInject(R.id.tv_joinTime)
	TextView tv_joinTime;
	@ViewInject(R.id.tv_contact_info)
	TextView tv_contact_info;
	@ViewInject(R.id.et_nickName)
	EditText et_nickName;
	@ViewInject(R.id.iv_head)
	CircleImageView iv_head;
	@ViewInject(R.id.layout_taste)
	View layout_taste;
	@ViewInject(R.id.layout_cookingStyle)
	View layout_cookingStyle;
	@ViewInject(R.id.layout_birthplace)
	View layout_birthplace;
	@ViewInject(R.id.layout_residence)
	View layout_residence;
	@ViewInject(R.id.layout_personData)
	View layout_personData;
	@ViewInject(R.id.tv_title_left)
	TextView tv_title_left;
	@ViewInject(R.id.btn_titleRight)
	Button btn_titleRight;
	
	private boolean isActivityResult = false;
	
	private PopupWindow popWin;
	private View popViewCity;
	private WheelView wheelViewProvince;
	private WheelView wheelViewCity;
	private WheelView wheelViewDistrict;
	private PopupWindow popWin_2;
	private View popViewCity_2;
	private WheelView wheelViewProvince_2;
	private WheelView wheelViewCity_2;
	private WheelView wheelViewDistrict_2;
	/**
	 * 当前省的名称
	 */
	private String mCurrentProviceName = "";
	/**
	 * 当前市的名称
	 */
	private String mCurrentCityName = "";
	/**
	 * 当前区的名称
	 */
	private String mCurrentDistrictName = "";
	/**
	 * 当前省的ID
	 */
	private String mCurrentProviceId = "";
	/**
	 * 当前市的ID
	 */
	private String mCurrentCityId = "";
	/**
	 * 当前区的ID
	 */
	private String mCurrentDistrictId = "";
	
	private View layout_contact_info;
	
	private CameraUtil cameraUtil;
	
	private UserInfoBean bean;
	
	private Context context;
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		context = this;
		initView();
		getData();
	}

	@Override
	protected void onResume() {
		super.onResume();
//		if (!isActivityResult) {
//		}
//		isActivityResult = false;
		showDlg();
		RequestParams params = new RequestParams(UrlInterface.URL_USERINFO_DETAIL);
		params.addBodyParameter("userId", KappAppliction.myApplication.getUser().getId() + "");
		x.http().post(params, new CommonCallback<String>() {
			@Override
			public void onSuccess(String result) {
				MyLog.i(MyLog.TAG_I_JSON, result);
				closeDlg();
				try {
					Gson gson = new Gson();
					JSONObject obj = new JSONObject(result);
					if (!isSuccess(obj)) {
						return;
					}
					
					bean = gson.fromJson(obj.getJSONObject("data")
							.getJSONObject("user").toString(), new TypeToken<UserInfoBean>(){}.getType());
					
					new ImgLoader(context).showPic(bean.getLogoPath(), iv_head, R.drawable.icon_default);
					KappAppliction.myApplication
					.getUser().setLogoPath(bean.getLogoPath());
//					et_nickName.setText(bean.getNickName());
					tv_joinTime.setText(bean.getCreateTime().substring(0, 10));
					tv_myCookingStyle.setText(bean.getCuisineMenuCategory());
					tv_taste.setText(bean.getFlavorMenuCategory());
//					tv_birthplace.setText(bean.getBirthProvinceName() + bean.getBirthCityName());
//					tv_residence.setText(bean.getDwellingProvinceName() + bean.getDwellingCityName() + bean.getDwellingDistrictName());
					switch (bean.getSex()) {
						case "1":
							tv_gender.setText("男");
							break;
						case "2":
							tv_gender.setText("女");							
							break;
						case "3":
							tv_gender.setText("保密");
							break;
					}
					tv_email.setText(bean.getEmail());
					tv_autograph.setText(bean.getAutograph());
					if (!TextUtils.isEmpty(bean.getContacter())) {
						tv_contact_info.setText("已填写");
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onError(Throwable ex, boolean isOnCallback) {
				closeDlg();
			}

			@Override
			public void onCancelled(CancelledException cex) {
				closeDlg();
			}

			@Override
			public void onFinished() {
				closeDlg();
			}
		});
	}
	
	private void initView() {
		layout_contact_info = findViewById(R.id.layout_contact_info);
		tv_title.setText("个人资料");
		tv_title_left.setVisibility(View.VISIBLE);
		tv_title_left.setText("个人中心");
		cameraUtil = new CameraUtil(context, MyDataActivity.this);
		cameraUtil.setWidthHeight(1, 1);
		
		layout_birthplace.setOnClickListener(this);
		layout_residence.setOnClickListener(this);
		iv_head.setOnClickListener(this);
		layout_contact_info.setOnClickListener(this);
		layout_cookingStyle.setOnClickListener(this);
		layout_personData.setOnClickListener(this);
		layout_taste.setOnClickListener(this);
		
		btn_titleRight.setText("保存");
		btn_titleRight.setVisibility(View.VISIBLE);
		btn_titleRight.setOnClickListener(this);
	}

	private void getData() {
		showDlg();
		RequestParams params = new RequestParams(UrlInterface.URL_USERINFO_DETAIL);
		params.addBodyParameter("userId", KappAppliction.myApplication.getUser().getId() + "");
		x.http().post(params, new CommonCallback<String>() {
			@Override
			public void onSuccess(String result) {
				MyLog.i(MyLog.TAG_I_JSON, result);
				closeDlg();
				try {
					Gson gson = new Gson();
					JSONObject obj = new JSONObject(result);
					if (!isSuccess(obj)) {
						return;
					}
					
					bean = gson.fromJson(obj.getJSONObject("data")
							.getJSONObject("user").toString(), new TypeToken<UserInfoBean>(){}.getType());
					
					new ImgLoader(context).showPic(bean.getLogoPath(), iv_head, R.drawable.icon_default);
					KappAppliction.myApplication
					.getUser().setLogoPath(bean.getLogoPath());
					et_nickName.setText(bean.getNickName());
					tv_joinTime.setText(bean.getCreateTime().substring(0, 10));
					tv_myCookingStyle.setText(bean.getCuisineMenuCategory());
					tv_taste.setText(bean.getFlavorMenuCategory());
					tv_birthplace.setText(bean.getBirthProvinceName() + bean.getBirthCityName());
					tv_residence.setText(bean.getDwellingProvinceName() + bean.getDwellingCityName() + bean.getDwellingDistrictName());
					switch (bean.getSex()) {
						case "1":
							tv_gender.setText("男");
							break;
						case "2":
							tv_gender.setText("女");							
							break;
						case "3":
							tv_gender.setText("保密");
							break;
					}
					tv_email.setText(bean.getEmail());
					tv_autograph.setText(bean.getAutograph());
					if (!TextUtils.isEmpty(bean.getContacter())) {
						tv_contact_info.setText("已填写");
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onError(Throwable ex, boolean isOnCallback) {
				closeDlg();
			}

			@Override
			public void onCancelled(CancelledException cex) {
				closeDlg();
			}

			@Override
			public void onFinished() {
				closeDlg();
			}
		});
	}
	
	@Override
	public void onClick(View v) {
		Intent intent;
		switch (v.getId()) {
			case R.id.layout_birthplace:
				choiceBirthPlaceDistrict(v);
				break;
			case R.id.layout_residence:
				choiceResidenceDistrict(v);
				break;
			case R.id.layout_contact_info:
				updateUserInfo2();
				intent = new Intent(context, ContactInfoActivity.class);
				intent.putExtra("UserInfoBean", bean);
				startActivity(intent);
				break;
			case R.id.iv_head:
				cameraUtil.toCameraPhoto(true);
				break;
			case R.id.layout_cookingStyle:
				updateUserInfo2();
				intent = new Intent(context, CookingStyleActivity.class);
				intent.putExtra("bean", bean);
				startActivity(intent);
				break;
			case R.id.layout_taste:
				updateUserInfo2();
				intent = new Intent(context, TasteActivity.class);
				intent.putExtra("bean", bean);
				startActivity(intent);
				break;
			case R.id.layout_personData:
				updateUserInfo2();
				intent = new Intent(context, PersonDataActivity.class);
				intent.putExtra("bean", bean);
				startActivity(intent);
				
				break;
			case R.id.btn_titleRight:
				updateUserInfo();
				break;
		}
	}
	
	
	private void updateUserInfo() {
		final String nickName = getEditTextString(et_nickName);
		if (TextUtils.isEmpty(nickName)) {
			KappUtils.showToast(context, "请输入昵称");
			return;
		}
		if (TextUtils.isEmpty(bean.getLogoPath())) {
			KappUtils.showToast(context, "请输入昵称");
			return;
		}
		showDlg();
		RequestParams params = new RequestParams(UrlInterface.URL_UPDATE_USERINFO);
		params.addBodyParameter("userId",  KappAppliction.myApplication.getUser().getId() + "");
		params.addBodyParameter("nickName", nickName);
		params.addBodyParameter("logoPath", bean.getLogoPath());
		
		if (!TextUtils.isEmpty(bean.getBirthProvinceId())) {
			params.addBodyParameter("birthProvinceId", bean.getBirthProvinceId());
			params.addBodyParameter("birthCityId", bean.getBirthCityId());
		}
		
		if (!TextUtils.isEmpty(bean.getDwellingDistrictId())) {
			params.addBodyParameter("dwellingProvinceId", bean.getDwellingProvinceId());
			params.addBodyParameter("dwellingCityId", bean.getDwellingCityId());
			params.addBodyParameter("dwellingDistrictId", bean.getDwellingDistrictId());
		}
		
//		params.addBodyParameter("userId", getUserBean().getId() + "");
//		params.addBodyParameter("userId", getUserBean().getId() + "");
//		params.addBodyParameter("userId", getUserBean().getId() + "");
//		params.addBodyParameter("userId", getUserBean().getId() + "");
//		params.addBodyParameter("userId", getUserBean().getId() + "");
//		params.addBodyParameter("userId", getUserBean().getId() + "");
//		params.addBodyParameter("userId", getUserBean().getId() + "");
//		params.addBodyParameter("userId", getUserBean().getId() + "");
		MyLog.i(MyLog.TAG_I_PARAMS, "userId=" + KappAppliction.myApplication.getUser().getId() + "&nickName=" + nickName);
		x.http().post(params, new CommonCallback<String>() {
			@Override
			public void onSuccess(String result) {
				MyLog.i(MyLog.TAG_I_JSON, result);
				closeDlg();
				try {
					JSONObject obj = new JSONObject(result);
					if (!isSuccess(obj)) {
						return;
					}
					KappUtils.showToast(context, "更新信息成功");
					KappAppliction.myApplication.getUser().setNickName(nickName);
					KappAppliction.myApplication.getUser().setAutograph(bean.getAutograph());
					setResult(1111);
					MyDataActivity.this.finish();
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onError(Throwable ex, boolean isOnCallback) {
				closeDlg();
			}

			@Override
			public void onCancelled(CancelledException cex) {
				closeDlg();
			}

			@Override
			public void onFinished() {
				closeDlg();
			}
		});
	}
	private void updateUserInfo2() {
		final String nickName = getEditTextString(et_nickName);
		if (TextUtils.isEmpty(nickName)) {
			KappUtils.showToast(context, "请输入昵称");
			return;
		}
		if (TextUtils.isEmpty(bean.getLogoPath())) {
			KappUtils.showToast(context, "请输入昵称");
			return;
		}
		showDlg();
		RequestParams params = new RequestParams(UrlInterface.URL_UPDATE_USERINFO);
		params.addBodyParameter("userId",  KappAppliction.myApplication.getUser().getId() + "");
		params.addBodyParameter("nickName", nickName);
		params.addBodyParameter("logoPath", bean.getLogoPath());
		
		if (!TextUtils.isEmpty(bean.getBirthProvinceId())) {
			params.addBodyParameter("birthProvinceId", bean.getBirthProvinceId());
			params.addBodyParameter("birthCityId", bean.getBirthCityId());
		}
		
		if (!TextUtils.isEmpty(bean.getDwellingDistrictId())) {
			params.addBodyParameter("dwellingProvinceId", bean.getDwellingProvinceId());
			params.addBodyParameter("dwellingCityId", bean.getDwellingCityId());
			params.addBodyParameter("dwellingDistrictId", bean.getDwellingDistrictId());
		}
		
//		params.addBodyParameter("userId", getUserBean().getId() + "");
//		params.addBodyParameter("userId", getUserBean().getId() + "");
//		params.addBodyParameter("userId", getUserBean().getId() + "");
//		params.addBodyParameter("userId", getUserBean().getId() + "");
//		params.addBodyParameter("userId", getUserBean().getId() + "");
//		params.addBodyParameter("userId", getUserBean().getId() + "");
//		params.addBodyParameter("userId", getUserBean().getId() + "");
//		params.addBodyParameter("userId", getUserBean().getId() + "");
		MyLog.i(MyLog.TAG_I_PARAMS, "userId=" + KappAppliction.myApplication.getUser().getId() + "&nickName=" + nickName);
		x.http().post(params, new CommonCallback<String>() {
			@Override
			public void onSuccess(String result) {
				MyLog.i(MyLog.TAG_I_JSON, result);
				closeDlg();
				try {
					JSONObject obj = new JSONObject(result);
					if (!isSuccess(obj)) {
						return;
					}
//					KappUtils.showToast(context, "更新信息成功");
//					KappAppliction.myApplication.getUser().setNickName(nickName);
//					KappAppliction.myApplication.getUser().setAutograph(bean.getAutograph());
//					setResult(111);
//					MyDataActivity.this.finish();
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onError(Throwable ex, boolean isOnCallback) {
				closeDlg();
			}

			@Override
			public void onCancelled(CancelledException cex) {
				closeDlg();
			}

			@Override
			public void onFinished() {
				closeDlg();
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		isActivityResult = true;
		super.onActivityResult(requestCode, resultCode, data);
		String imgPath = cameraUtil.getImgPath(requestCode, resultCode, data);
		
		if (imgPath != null) {
			imgPath = ScaleImageSizeUtil.compressBitmap(context, imgPath, 1024, 1024, false);
			
			showDlg();
			RequestParams params = new RequestParams(UrlInterface.URL_FILEUPLOADBYBYTE);
			params.setMultipart(true);
			params.addBodyParameter("file", new File(imgPath), "multipart/form-data");
			x.http().post(params, new CommonCallback<String>() {
				@Override
				public void onSuccess(String result) {
					closeDlg();
					MyLog.i(MyLog.TAG_I_INFO, result);
					try {
						JSONObject jsonObject = new JSONObject(result);
						if (!isSuccess(jsonObject)) {
							return;
						}
						JSONObject data=jsonObject.getJSONObject("data");
						bean.setLogoPath(data.getString("path"));
						new ImgLoader(context).showPic(data.getString("path"), iv_head);
						KappAppliction.myApplication
						.getUser().setLogoPath(data.getString("path"));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				@Override
				public void onError(Throwable ex, boolean isOnCallback) {
					closeDlg();
				}

				@Override
				public void onCancelled(CancelledException cex) {
					closeDlg();
				}

				@Override
				public void onFinished() {
					closeDlg();
				}
			});
		}
	}
	
	@SuppressWarnings("deprecation")
	private void choiceResidenceDistrict(View v) {
		if (popViewCity_2 == null) {
			popViewCity_2 = LayoutInflater.from(context).inflate(R.layout.layout_wheel_city, null);
			wheelViewProvince_2 = (WheelView) popViewCity_2.findViewById(R.id.wheelView_1);
			wheelViewCity_2 = (WheelView) popViewCity_2.findViewById(R.id.wheelView_2);
			wheelViewDistrict_2 = (WheelView) popViewCity_2.findViewById(R.id.wheelView_3);
			wheelViewProvince_2.setViewAdapter(new ProvinceWheelAdapter<String>(context, KappAppliction.myApplication.getmProvinceDatas()));
			updateCities2(0);
			
			wheelViewProvince_2.setVisibleItems(7);
			wheelViewCity_2.setVisibleItems(7);
			wheelViewDistrict_2.setVisibleItems(7);
			
			((TextView) popViewCity_2.findViewById(R.id.tv_title)).setText("居住地");
			popViewCity_2.findViewById(R.id.btn_cancel).setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					popWin_2.dismiss();
					popWin_2 = null;
				}
			});
			popViewCity_2.findViewById(R.id.btn_confirm).setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					int pCurrent = wheelViewDistrict_2.getCurrentItem();
					if (KappAppliction.myApplication.getmDistrictDatasMap().get(mCurrentCityName) != null
							&& KappAppliction.myApplication.getmDistrictDatasMap().get(mCurrentCityName).length > pCurrent) {
						mCurrentDistrictName = KappAppliction.myApplication
							.getmDistrictDatasMap().get(mCurrentCityName)[pCurrent].getName();
						mCurrentDistrictId = KappAppliction.myApplication
								.getmDistrictDatasMap().get(mCurrentCityName)[pCurrent].getId();
					} else {
						mCurrentDistrictName = "";
					}
					if (!mCurrentProviceName.equals("香港") && !mCurrentProviceName.equals("澳门")
							&& !mCurrentProviceName.equals("台湾") && !mCurrentProviceName.endsWith("省")
							 && !mCurrentProviceName.endsWith("市") && !mCurrentProviceName.endsWith("区")) {
						mCurrentProviceName += "省";
					}
					if (!mCurrentCityName.endsWith("县") && !mCurrentCityName.endsWith("区") && !mCurrentCityName.endsWith("市")
							 && !mCurrentCityName.endsWith("省")) {
						mCurrentCityName += "市";
					}
					popWin_2.dismiss();
					popWin_2 = null;
					tv_residence.setText(mCurrentProviceName + mCurrentCityName + mCurrentDistrictName);
					bean.setDwellingProvinceId(mCurrentProviceId);
					bean.setDwellingCityId(mCurrentCityId);
					bean.setDwellingDistrictId(mCurrentDistrictId);
				}
			});
			wheelViewProvince_2.addChangingListener(new OnWheelChangedListener() {
				@Override
				public void onChanged(WheelView wheel, int oldValue, int newValue) {
					if (wheel == wheelViewProvince_2) {
						int pCurrent = wheelViewProvince_2.getCurrentItem();
						updateCities2(pCurrent);
					}
				}
			});
			wheelViewCity_2.addChangingListener(new OnWheelChangedListener() {
				@Override
				public void onChanged(WheelView wheel, int oldValue, int newValue) {
					if (wheel == wheelViewCity_2) {
						int pCurrent = wheelViewCity_2.getCurrentItem();
						updateDistricts2(pCurrent);
					}
				}
			});
		}
		popWin_2 = new PopupWindow(popViewCity_2, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, false);
		popWin_2.setBackgroundDrawable(new BitmapDrawable());
//		popWin.setFocusable(true);
		popWin_2.setOutsideTouchable(true);
		popWin_2.showAtLocation(v, Gravity.BOTTOM, 0, 0);
	}
	
	@SuppressWarnings("deprecation")
	private void choiceBirthPlaceDistrict(View v) {
		if (popViewCity == null) {
			popViewCity = LayoutInflater.from(context).inflate(R.layout.layout_wheel_city, null);
			wheelViewProvince = (WheelView) popViewCity.findViewById(R.id.wheelView_1);
			wheelViewCity = (WheelView) popViewCity.findViewById(R.id.wheelView_2);
			wheelViewDistrict = (WheelView) popViewCity.findViewById(R.id.wheelView_3);
			wheelViewProvince.setViewAdapter(new ProvinceWheelAdapter<String>(context, KappAppliction.myApplication.getmProvinceDatas()));
			updateCities(0);
			
			wheelViewProvince.setVisibleItems(7);
			wheelViewCity.setVisibleItems(7);
			wheelViewDistrict.setVisibleItems(7);
			wheelViewDistrict.setVisibility(View.GONE);
			
			((TextView) popViewCity.findViewById(R.id.tv_title)).setText("出生地");
			popViewCity.findViewById(R.id.btn_cancel).setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					popWin.dismiss();
					popWin = null;
				}
			});
			popViewCity.findViewById(R.id.btn_confirm).setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
//					int pCurrent = wheelViewDistrict.getCurrentItem();
//					if (KappAppliction.myApplication.getmDistrictDatasMap().get(mCurrentCityName) != null
//							&& KappAppliction.myApplication.getmDistrictDatasMap().get(mCurrentCityName).length > pCurrent) {
//						mCurrentDistrictName = KappAppliction.myApplication
//							.getmDistrictDatasMap().get(mCurrentCityName)[pCurrent];
//					} else {
						mCurrentDistrictName = "";
//					}
					if (!mCurrentProviceName.equals("香港") && !mCurrentProviceName.equals("澳门")
							&& !mCurrentProviceName.equals("台湾") && !mCurrentProviceName.endsWith("省")
							 && !mCurrentProviceName.endsWith("市") && !mCurrentProviceName.endsWith("区")) {
						mCurrentProviceName += "省";
					}
					if (!mCurrentCityName.endsWith("县") && !mCurrentCityName.endsWith("区") && !mCurrentCityName.endsWith("市")
							 && !mCurrentCityName.endsWith("省")) {
						mCurrentCityName += "市";
					}
					popWin.dismiss();
					popWin = null;
					tv_birthplace.setText(mCurrentProviceName + mCurrentCityName + mCurrentDistrictName);
					bean.setBirthProvinceId(mCurrentProviceId);
					bean.setBirthCityId(mCurrentCityId);
				}
			});
			wheelViewProvince.addChangingListener(new OnWheelChangedListener() {
				@Override
				public void onChanged(WheelView wheel, int oldValue, int newValue) {
					if (wheel == wheelViewProvince) {
						int pCurrent = wheelViewProvince.getCurrentItem();
						updateCities(pCurrent);
					}
				}
			});
			wheelViewCity.addChangingListener(new OnWheelChangedListener() {
				@Override
				public void onChanged(WheelView wheel, int oldValue, int newValue) {
					if (wheel == wheelViewCity) {
						int pCurrent = wheelViewCity.getCurrentItem();
						updateDistricts(pCurrent);
					}
				}
			});
		}
		popWin = new PopupWindow(popViewCity, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, false);
		popWin.setBackgroundDrawable(new BitmapDrawable());
//		popWin.setFocusable(true);
		popWin.setOutsideTouchable(true);
		popWin.showAtLocation(v, Gravity.BOTTOM, 0, 0);
	}
	
	private void updateCities(int pCurrent) {
		if(KappAppliction.myApplication.getmProvinceDatas() != null){
			mCurrentProviceName =  KappAppliction.myApplication.getmProvinceDatas()[pCurrent].getName();
			mCurrentProviceId = KappAppliction.myApplication.getmProvinceDatas()[pCurrent].getId();
			wheelViewCity.setViewAdapter(new CityWheelAdapter<String>(this, KappAppliction.myApplication.getmCitisDatasMap().get(mCurrentProviceName)));
			wheelViewCity.setCurrentItem(0);
			updateDistricts(0);
		}
	}

	private void updateDistricts(int index) {
		if(KappAppliction.myApplication.getmCitisDatasMap().get(mCurrentProviceName) != null){
			mCurrentCityName =  KappAppliction.myApplication.getmCitisDatasMap().get(mCurrentProviceName)[index].getName();
			mCurrentCityId = KappAppliction.myApplication.getmCitisDatasMap().get(mCurrentProviceName)[index].getId();
			wheelViewDistrict.setViewAdapter(new DistrictWheelAdapter<String>(this, KappAppliction.myApplication.getmDistrictDatasMap().get(mCurrentCityName)));
			if (KappAppliction.myApplication.getmDistrictDatasMap().get(mCurrentCityName).length > 0) {
				wheelViewDistrict.setCurrentItem(0);
			}
		}
	}
    
	private void updateCities2(int pCurrent) {
		if(KappAppliction.myApplication.getmProvinceDatas() != null){
			mCurrentProviceName =  KappAppliction.myApplication.getmProvinceDatas()[pCurrent].getName();
			mCurrentProviceId = KappAppliction.myApplication.getmProvinceDatas()[pCurrent].getId();
			wheelViewCity_2.setViewAdapter(new CityWheelAdapter<String>(this, KappAppliction.myApplication.getmCitisDatasMap().get(mCurrentProviceName)));
			wheelViewCity_2.setCurrentItem(0);
			updateDistricts2(0);
		}
	}

	private void updateDistricts2(int index) {
		if(KappAppliction.myApplication.getmCitisDatasMap().get(mCurrentProviceName) != null){
			mCurrentCityName =  KappAppliction.myApplication.getmCitisDatasMap().get(mCurrentProviceName)[index].getName();
			mCurrentCityId = KappAppliction.myApplication.getmCitisDatasMap().get(mCurrentProviceName)[index].getId();
			wheelViewDistrict_2.setViewAdapter(new DistrictWheelAdapter<String>(this, KappAppliction.myApplication.getmDistrictDatasMap().get(mCurrentCityName)));
			if (KappAppliction.myApplication.getmDistrictDatasMap().get(mCurrentCityName).length > 0) {
				wheelViewDistrict_2.setCurrentItem(0);
			}
		}
	}
}
