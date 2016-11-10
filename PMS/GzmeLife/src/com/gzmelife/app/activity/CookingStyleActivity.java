package com.gzmelife.app.activity;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.x;
import org.xutils.common.Callback.CommonCallback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gzmelife.app.R;
import com.gzmelife.app.UrlInterface;
import com.gzmelife.app.adapter.CookingStyleAdapter;
import com.gzmelife.app.bean.CookingStyleBean;
import com.gzmelife.app.bean.UserInfoBean;
import com.gzmelife.app.tools.KappUtils;
import com.gzmelife.app.tools.MyLog;
import com.gzmelife.app.views.GridViewForScrollView;

@ContentView(R.layout.activity_cooking_style)
public class CookingStyleActivity extends BaseActivity implements OnClickListener{
	@ViewInject(R.id.tv_title)
	TextView tv_title;
	@ViewInject(R.id.gv_data)
	GridViewForScrollView gv_data;
	
	private List<CookingStyleBean> list = new  ArrayList<CookingStyleBean>();
	private CookingStyleAdapter adapter;
	
	private UserInfoBean bean;
	
	private Context context;
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		
		
		context = this;
		bean = (UserInfoBean) getIntent().getSerializableExtra("bean");
		initView();
		getData();
	}
	
	private void getData() {
		RequestParams params = new RequestParams(UrlInterface.URL_FOOD_TASTE_CATEGORY);
		params.addBodyParameter("pType", "1");
		x.http().post(params, new CommonCallback<String>() {
			@Override
			public void onSuccess(String result) {
				try {
					Gson gson = new Gson();
					JSONObject obj = new JSONObject(result);
					if (!isSuccess(obj)) {
						return;
					}
					List<CookingStyleBean> listTemp = gson.fromJson(obj.getJSONObject("data")
							.getJSONArray("menuCategorys").toString(), new TypeToken<List<CookingStyleBean>>(){}.getType());
					if (bean != null) {
						String ids[] = bean.getCuisineMenuCategoryIds().split(",");
						List<String> idList = new ArrayList<String>();
						for (int i = 0; i < ids.length; i++) {
							idList.add(ids[i]);
						}
						for (int i = 0; i < listTemp.size(); i++) {
							if (idList.contains(listTemp.get(i).getId())) {
								listTemp.get(i).setChecked(true);
							}
						}
					}
					list.addAll(listTemp);
					adapter.notifyDataSetChanged();
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
		tv_title.setText("我的菜系");
		
		adapter = new CookingStyleAdapter(context, list);
		gv_data.setAdapter(adapter);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btn_confirm:
				if (bean != null) {
					showDlg();
					RequestParams params = new RequestParams(UrlInterface.URL_UPDATE_USERINFO);
					params.addBodyParameter("userId",  bean.getId());
					params.addBodyParameter("nickName", bean.getNickName());
					params.addBodyParameter("logoPath", bean.getLogoPath());
					params.addBodyParameter("cuisineMenuCategoryIds", adapter.getCheckedId());
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
								CookingStyleActivity.this.finish();
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
				} else {
					Intent i = new Intent();
					i.putExtra("cookingStyleIds", adapter.getCheckedId());
					i.putExtra("cookingStyleNames", adapter.getCheckedName());
					setResult(RESULT_OK, i);
					CookingStyleActivity.this.finish();
				}
				break;
		}
	}
}
