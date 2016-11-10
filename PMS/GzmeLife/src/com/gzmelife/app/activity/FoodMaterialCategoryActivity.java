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

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gzmelife.app.R;
import com.gzmelife.app.UrlInterface;
import com.gzmelife.app.adapter.StandardFoodMaterialAdapter;
import com.gzmelife.app.bean.CategoryFirstBean;
import com.gzmelife.app.tools.MyLogger;

@SuppressLint("InflateParams")
@ContentView(R.layout.activity_food_material_category)
public class FoodMaterialCategoryActivity extends BaseActivity implements OnClickListener {

	MyLogger HHDLog =MyLogger.HHDLog();

	@ViewInject(R.id.tv_title)
	private TextView tv_title;
	@ViewInject(R.id.tv_title_left)
	TextView tv_title_left;
	@ViewInject(R.id.expandLv_standardFoodMaterial)
	private ExpandableListView expandLv_standardFoodMaterial;
	
	private List<CategoryFirstBean> categoryFirstBeanList = new ArrayList<CategoryFirstBean>();
	private StandardFoodMaterialAdapter standardAdapter;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		initView();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		HHDLog.v("测试20161103_2307");

		RequestParams params = new RequestParams(UrlInterface.URL_FOOD_MATERIAL_LIB);
		showDlg();
		x.http().post(params, new CommonCallback<String>() {
			@Override
			public void onSuccess(String result) {
				closeDlg();
				Gson gson = new Gson();
				JSONObject obj;
				try {
					obj = new JSONObject(result);
					categoryFirstBeanList = gson.fromJson(obj.getJSONObject("data")
							.getJSONArray("towFoodStoreCategorys").toString(), new TypeToken<List<CategoryFirstBean>>(){}.getType());
					
					standardAdapter = new StandardFoodMaterialAdapter(context, categoryFirstBeanList, 
							new StandardFoodMaterialAdapter.OnReceiver() {
							@Override
							public void onClick(int groupPosition, int position,int ChildPosition) {
								Intent intent = new Intent();
								intent.putExtra("category", categoryFirstBeanList.get(groupPosition).getFcName());
								intent.putExtra("ChildPosition", ChildPosition);
								setResult(RESULT_OK, intent);
								FoodMaterialCategoryActivity.this.finish();
							}
						});
					expandLv_standardFoodMaterial.setAdapter(standardAdapter);
					for (int i = 0; i < standardAdapter.getGroupCount(); i++) {  
						expandLv_standardFoodMaterial.expandGroup(i);  
					}  
					expandLv_standardFoodMaterial.setOnGroupClickListener(new OnGroupClickListener() {
						@Override
						public boolean onGroupClick(ExpandableListView parent, View v,
								int groupPosition, long id) {
							return true;
						}
					});
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
		tv_title.setText("分类");
		tv_title_left.setVisibility(View.VISIBLE);
		tv_title_left.setText("添加新食材");
		expandLv_standardFoodMaterial.setGroupIndicator(null);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
//			case R.id.btn_addFoodMaterial:
//				startActivity(new Intent(context, AddNewFoodMaterialActivity.class));
//				break;
		}
	}
}