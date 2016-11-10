package com.gzmelife.app.activity;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.x;
import org.xutils.common.Callback.CancelledException;
import org.xutils.common.Callback.CommonCallback;
import org.xutils.http.RequestParams;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gzmelife.app.KappAppliction;
import com.gzmelife.app.R;
import com.gzmelife.app.UrlInterface;
import com.gzmelife.app.adapter.LvfoodSearchAdapter;
import com.gzmelife.app.adapter.LvfoodsSearchAdapter;
import com.gzmelife.app.bean.CategoryFirstBean;
import com.gzmelife.app.bean.LocalFoodMaterialLevelOne;
import com.gzmelife.app.bean.LocalFoodMaterialLevelThree;
import com.gzmelife.app.bean.SearchFoodBean;
import com.gzmelife.app.bean.SearchMenuBookBean;
import com.gzmelife.app.bean.UserInfoBean;
import com.gzmelife.app.dao.FoodMaterialDAO;
import com.gzmelife.app.tools.KappUtils;
import com.gzmelife.app.tools.MyLog;
import com.gzmelife.app.tools.MyLogger;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class SearchDetailActivity extends BaseActivity {

	MyLogger HHDLog = MyLogger.HHDLog();

	private ListView lv_food;
	LvfoodsSearchAdapter lvFoodSearchAdapter;
	TextView tv_title;

	private List<SearchFoodBean> searchMenuBookBeanList = new ArrayList<SearchFoodBean>();
	private String name;
	UserInfoBean user;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_search_deatail);
		initView();
	}

	@Override
	protected void onResume() {
		super.onResume();
		HHDLog.v("界面【搜索】SearchDetailActivity");
	}

	private void initView() {
		lv_food = (ListView) findViewById(R.id.lv_food);
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setText("搜索结果");
		name = getIntent().getStringExtra("name");
		searchMenuBook(name);

		lv_food.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				LocalFoodMaterialLevelOne bean1 = new LocalFoodMaterialLevelOne();
				String c_name = searchMenuBookBeanList.get(position).getC_name();
				bean1.setName(c_name);
				String nameStr = searchMenuBookBeanList.get(position).getName();
				for (int i = 0; i < searchMenuBookBeanList.size(); i++) {
					LocalFoodMaterialLevelThree bean2 = new LocalFoodMaterialLevelThree();
					bean2.setPid(FoodMaterialDAO
							.saveLocalFoodMaterialLevelOne(bean1));
					bean2.setName(nameStr);
					FoodMaterialDAO.saveLocalFoodMaterialLevelThree(bean2);
				}
				KappUtils.showToast(context, "食材添加成功");
			}
		});
	}

	private void searchMenuBook(String text) {
		// 搜索
		showDlg();
		RequestParams params = new RequestParams(
				UrlInterface.URL_SERACHFOODSTORE);
		params.addBodyParameter("foodStoreName", text);
		params.addBodyParameter("userId", KappAppliction.myApplication
				.getUser().getId());

		x.http().post(params, new CommonCallback<String>() {
			@Override
			public void onSuccess(String result) {
				closeDlg();
				Gson gson = new Gson();
				JSONObject obj;
				try {
					obj = new JSONObject(result.trim());
					// 菜谱
					MyLog.i(MyLog.TAG_I_JSON, "result123=" + result);
					searchMenuBookBeanList = gson.fromJson(
							obj.getJSONObject("data")
									.getJSONArray("foodStores").toString(),
							new TypeToken<List<SearchFoodBean>>() {
							}.getType());
					if (searchMenuBookBeanList.size() == 0) {
						KappUtils.showToast(SearchDetailActivity.this, "搜索不到结果哦");
					} else {
						lvFoodSearchAdapter = new LvfoodsSearchAdapter(context,
								searchMenuBookBeanList);
						// ImageView iv_icon = (ImageView)
						// findViewById(R.id.iv_icon);
						// iv_icon.setVisibility(View.GONE);
						lv_food.setAdapter(lvFoodSearchAdapter);
						lvFoodSearchAdapter.notifyDataSetChanged();
						// lv_food.setOnItemClickListener(new
						// OnItemClickListener() {
						//
						// @Override
						// public void onItemClick(AdapterView<?> parent,
						// View view, int position, long id) {
						// Intent intent = new Intent(context,
						// NetCookBookDetailActivity.class);
						// // intent.putExtra("category",
						// // categoryFirstBeanList.get(position));
						// intent.putExtra("position", position);
						// intent.putExtra("menuBookId",
						// searchMenuBookBeanList.get(position)
						// .getId());
						// startActivity(intent);
						// }
						//
						// });

					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				System.out.println("======result======>>>>" + result.toString());
				HHDLog.e("4"+result);
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
