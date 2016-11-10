package com.gzmelife.app.activity;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.x;
import org.xutils.common.Callback.CommonCallback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.gzmelife.app.KappAppliction;
import com.gzmelife.app.R;
import com.gzmelife.app.UrlInterface;
import com.gzmelife.app.bean.UserInfoBean;
import com.gzmelife.app.tools.DataUtil;
import com.gzmelife.app.tools.KappUtils;
import com.gzmelife.app.tools.MyLog;
import com.gzmelife.app.views.wheelview.OnWheelChangedListener;
import com.gzmelife.app.views.wheelview.WheelView;
import com.gzmelife.app.views.wheelview.adapter.CityWheelAdapter;
import com.gzmelife.app.views.wheelview.adapter.DistrictWheelAdapter;
import com.gzmelife.app.views.wheelview.adapter.ProvinceWheelAdapter;

@ContentView(R.layout.activity_contact_info)
public class ContactInfoActivity extends BaseActivity implements OnClickListener {
	@ViewInject(R.id.tv_title)
	TextView tv_title;
	@ViewInject(R.id.tv_district)
	TextView tv_district;
	
	@ViewInject(R.id.et_contacter)
	EditText et_contacter;
	@ViewInject(R.id.et_mobilePhone)
	EditText et_mobilePhone;
	@ViewInject(R.id.et_contacterAddress)
	EditText et_contacterAddress;
	@ViewInject(R.id.tv_title_left)
	TextView tv_title_left;
	@ViewInject(R.id.layout_district)
	View layout_district;
	
	private PopupWindow popWin;
	private View popViewCity;
	private WheelView wheelViewProvince;
	private WheelView wheelViewCity;
	private WheelView wheelViewDistrict;
	
	/** 当前省的名称 */
	private String mCurrentProviceName = "";
	/** 当前市的名称 */
	private String mCurrentCityName = "";
	/** 当前区的名称 */
	private String mCurrentDistrictName = "";
	/** 当前省的ID */
	private String mCurrentProviceId = "";
	/** 当前市的ID */
	private String mCurrentCityId = "";
	/** 当前区的ID */
	private String mCurrentDistrictId = "";
	
	private UserInfoBean bean;
	
	private Context context;
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		
		context = this;
		bean = (UserInfoBean) getIntent().getSerializableExtra("UserInfoBean");
		initView();
	}
	
	private void initView() {
		tv_title.setText("修改联系信息");	
		tv_title_left.setVisibility(View.VISIBLE);
		tv_title_left.setText("个人资料");
		if (!TextUtils.isEmpty(bean.getContacter())) {
			et_contacter.setText(bean.getContacter());
			et_mobilePhone.setText(bean.getMobilePhone());
			tv_district.setText(bean.getContacterProvinceName() + bean.getContacterCityName() + bean.getContacterDistrictName());
			et_contacterAddress.setText(bean.getContacterAddress());
		}
		
		layout_district.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.layout_district:
				choiceDistrict(v);
				break;
			case R.id.btn_confirm:
				String contacter = getEditTextString(et_contacter);
				String mobilePhone = getEditTextString(et_mobilePhone);
				String contacterAddress = getEditTextString(et_contacterAddress);
				String district = tv_district.getText().toString();
				if (TextUtils.isEmpty(contacter)) {
					KappUtils.showToast(context, "请输入联系人");
					return;
				}
				if (TextUtils.isEmpty(mobilePhone)) {
					KappUtils.showToast(context, "请输入手机号");
					return;
				}
				if (!DataUtil.isMobileNo(mobilePhone)) {
					KappUtils.showToast(context, "请输入正确格式的手机号");
					return;
				}
				if (TextUtils.isEmpty(district)) {
					KappUtils.showToast(context, "请选择地区");
					return;
				}
				if (TextUtils.isEmpty(contacterAddress)) {
					KappUtils.showToast(context, "请输入详细地址");
					return;
				}
				
				showDlg();
				RequestParams params = new RequestParams(UrlInterface.URL_UPDATE_USERINFO);
				params.addBodyParameter("userId",  bean.getId());
				params.addBodyParameter("nickName", bean.getNickName());
				params.addBodyParameter("logoPath", bean.getLogoPath());
				params.addBodyParameter("contacter", contacter);
				params.addBodyParameter("mobilePhone", mobilePhone);
				params.addBodyParameter("contacterAddress", contacterAddress);
				params.addBodyParameter("contacterProviceId", mCurrentProviceId);
				params.addBodyParameter("contacterCityId", mCurrentCityId);
				params.addBodyParameter("contacterDistrictId", mCurrentDistrictId);
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
							KappUtils.showToast(context, "保存成功");
							ContactInfoActivity.this.finish();
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
				break;
		}
	}
	
	@SuppressWarnings("deprecation")
	private void choiceDistrict(View v) {
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
			
			((TextView) popViewCity.findViewById(R.id.tv_title)).setText("地区");
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
					int pCurrent = wheelViewDistrict.getCurrentItem();
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
							&& !mCurrentProviceName.equals("台湾") && !mCurrentProviceName.endsWith("省")) {
						mCurrentProviceName += "省";
					}
					if (!mCurrentCityName.endsWith("县") && !mCurrentCityName.endsWith("区") && !mCurrentCityName.endsWith("市")) {
						mCurrentCityName += "市";
					}
					popWin.dismiss();
					popWin = null;
					tv_district.setText(mCurrentProviceName + mCurrentCityName + mCurrentDistrictName);
					
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
			mCurrentProviceId =  KappAppliction.myApplication.getmProvinceDatas()[pCurrent].getId();
			wheelViewCity.setViewAdapter(new CityWheelAdapter<String>(this, KappAppliction.myApplication.getmCitisDatasMap().get(mCurrentProviceName)));
			wheelViewCity.setCurrentItem(0);
			updateDistricts(0);
		}
	}

	private void updateDistricts(int index) {
		if(KappAppliction.myApplication.getmCitisDatasMap().get(mCurrentProviceName) != null){
			mCurrentCityName =  KappAppliction.myApplication.getmCitisDatasMap().get(mCurrentProviceName)[index].getName();
			mCurrentCityId =  KappAppliction.myApplication.getmCitisDatasMap().get(mCurrentProviceName)[index].getId();
			wheelViewDistrict.setViewAdapter(new DistrictWheelAdapter<String>(this, KappAppliction.myApplication.getmDistrictDatasMap().get(mCurrentCityName)));
			if (KappAppliction.myApplication.getmDistrictDatasMap().get(mCurrentCityName).length > 0) {
				wheelViewDistrict.setCurrentItem(0);
			}
		}
	}
}
