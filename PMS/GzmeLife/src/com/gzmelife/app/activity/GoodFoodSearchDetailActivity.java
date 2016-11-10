package com.gzmelife.app.activity;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.x;
import org.xutils.common.Callback.CancelledException;
import org.xutils.common.Callback.CommonCallback;
import org.xutils.http.RequestParams;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gzmelife.app.R;
import com.gzmelife.app.UrlInterface;
import com.gzmelife.app.adapter.LvfoodSearchAdapter;
import com.gzmelife.app.adapter.LvfoodsSearchAdapter;
import com.gzmelife.app.bean.SearchFoodBean;
import com.gzmelife.app.bean.SearchMenuBookBean;
import com.gzmelife.app.bean.UserInfoBean;
import com.gzmelife.app.tools.KappUtils;
import com.gzmelife.app.tools.MyLogger;

public class GoodFoodSearchDetailActivity extends BaseActivity{

	MyLogger HHDLog = MyLogger.HHDLog();

	private ListView lv_food;
	LvfoodSearchAdapter lvFoodSearchAdapter;
	TextView tv_title;

	private List<SearchMenuBookBean> searchMenuBookBeanList = new ArrayList<SearchMenuBookBean>();
	private String name;
	UserInfoBean user;

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.activity_search_deatail);
		initView();

	}

	private void initView() {
		// TODO Auto-generated method stub
		lv_food = (ListView) findViewById(R.id.lv_food);
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setText("搜索结果");
		name = getIntent().getStringExtra("name");
		searchMenuBook(name);
	}
	private void searchMenuBook(String text) {
		// 搜索
		showDlg();
		RequestParams params = new RequestParams(
				UrlInterface.URL_SEARCHMENUBOOK);
		params.addBodyParameter("menubookName", text);
		params.addBodyParameter("pages", "1");
		params.addBodyParameter("pageSize", "20");

		x.http().post(params, new CommonCallback<String>() {
			@Override
			public void onSuccess(String result) {
				closeDlg();
				Gson gson = new Gson();
				JSONObject obj;
				try {
					obj = new JSONObject(result.trim());
					// 菜谱

					searchMenuBookBeanList = gson.fromJson(
							obj.getJSONObject("data").getJSONArray("list")
									.toString(),
							new TypeToken<List<SearchMenuBookBean>>() {
							}.getType());
					if (searchMenuBookBeanList.size() == 0) {
						KappUtils.showToast(GoodFoodSearchDetailActivity.this,
								"搜索不到结果哦");
					} else {
						lvFoodSearchAdapter = new LvfoodSearchAdapter(context, searchMenuBookBeanList);

						lv_food.setAdapter(lvFoodSearchAdapter);

						lv_food.setOnItemClickListener(new OnItemClickListener() {

							@Override
							public void onItemClick(AdapterView<?> parent,
									View view, int position, long id) {
								// TODO Auto-generated method stub
								Intent intent = new Intent(context, NetCookBookDetailActivity.class);
								// intent.putExtra("category",
								// categoryFirstBeanList.get(position));
								intent.putExtra("position", position);
								intent.putExtra("menuBookId", searchMenuBookBeanList.get(position).getId());
								startActivity(intent);
							}

						});
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				System.out.println("======result======>>>>" + result.toString());
				HHDLog.e("1"+result.toString());
			}

			@Override
			public void onError(Throwable ex, boolean isOnCallback) {
				// TODO Auto-generated method stub
				closeDlg();
			}

			@Override
			public void onCancelled(CancelledException cex) {
				// TODO Auto-generated method stub
				closeDlg();
			}

			@Override
			public void onFinished() {
				// TODO Auto-generated method stub
				closeDlg();
			}

		});

	}

}
