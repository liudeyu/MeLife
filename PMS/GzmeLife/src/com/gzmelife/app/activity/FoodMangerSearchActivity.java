package com.gzmelife.app.activity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.x;
import org.xutils.common.Callback.CancelledException;
import org.xutils.common.Callback.CommonCallback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gzmelife.app.R;
import com.gzmelife.app.UrlInterface;
import com.gzmelife.app.adapter.CookBookStepAdapter;
import com.gzmelife.app.adapter.LvCommentAdapter;
import com.gzmelife.app.adapter.LvfoodSearchAdapter;
import com.gzmelife.app.adapter.SearchAutoAdapter;
import com.gzmelife.app.bean.CookBookMenuBookBean;
import com.gzmelife.app.bean.CookBookMenuBookCommentsBean;
import com.gzmelife.app.bean.CookBookMenuBookStepsBean;
import com.gzmelife.app.bean.LocalFoodMaterialLevelOne;
import com.gzmelife.app.bean.SearchAutoData;
import com.gzmelife.app.bean.SearchMenuBookBean;
import com.gzmelife.app.dao.FoodMaterialDAO;
import com.gzmelife.app.tools.ImgLoader;
import com.gzmelife.app.tools.KappUtils;
import com.gzmelife.app.tools.MyLogger;
import com.gzmelife.app.tools.RecordSQLiteOpenHelper;
import com.gzmelife.app.views.MyListView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

public class FoodMangerSearchActivity extends BaseActivity implements
		OnClickListener {

	MyLogger HHDLog = MyLogger.HHDLog();

	ListView lv_food;
	MyListView listView;
	EditText et_search;
	TextView tv_clear;
	TextView tv_tip;
	Button clear;
	TextView tv_title;
	TextView tv_title_left;
	ImageView iv_sousuo;

	String TAG = "PrivateFragment";
	SearchMenuBookBean bean;
	private List<SearchMenuBookBean> searchMenuBookBeanList = new ArrayList<SearchMenuBookBean>();
	private List<SearchAutoData> searchAutoDataList = new ArrayList<SearchAutoData>();
	private RecordSQLiteOpenHelper helper = new RecordSQLiteOpenHelper(this);
	public static final String SEARCH_HISTORY = "search_history";
	private SearchAutoAdapter mSearchAutoAdapter;
	private SQLiteDatabase db;
	private BaseAdapter adapter;
	LvfoodSearchAdapter lvFoodSearchAdapter;

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.activity_good_food_search);
		initView();

	}

	// 加载布局
	private void initView() {
		mSearchAutoAdapter = new SearchAutoAdapter(this, -1, this);
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title_left = (TextView) findViewById(R.id.tv_title_left);
		iv_sousuo = (ImageView) findViewById(R.id.iv_sousuo);
		clear = (Button) findViewById(R.id.clear);
		clear.setOnClickListener(this);

		tv_title.setText("搜索");
		tv_title_left.setVisibility(View.VISIBLE);
		tv_title_left.setText("美食");
		lv_food = (ListView) findViewById(R.id.lv_food);
		lv_food.setAdapter(mSearchAutoAdapter);
		listView = (MyListView) findViewById(R.id.listView);
		et_search = (EditText) findViewById(R.id.et_search);
		tv_clear = (TextView) findViewById(R.id.tv_clear);
		tv_tip = (TextView) findViewById(R.id.tv_tip);

		et_search.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView arg0, int arg1, KeyEvent arg2) {
				String searchContent = et_search.getText().toString().trim();
				et_search.setText("");
				saveSearchHistory(searchContent);
				mSearchAutoAdapter.initSearchHistory();
				if (arg1 == EditorInfo.IME_ACTION_SEARCH
						&& !searchContent.equals("")) {
					searchMenuBook(searchContent);
				}
				if (arg1 == EditorInfo.IME_ACTION_SEARCH
						&& searchContent.equals("")) {
					mSearchAutoAdapter.initSearchHistory();
					mSearchAutoAdapter.notifyDataSetChanged();
				}

				return false;
			}
		});

		et_search.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				mSearchAutoAdapter.performFiltering(s);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});

		iv_sousuo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String searchContent = et_search.getText().toString().trim();
				if ("".equals(searchContent)) {
					KappUtils.showToast(FoodMangerSearchActivity.this,
							"请输入您要查询的菜名");
				} else {
					searchMenuBook(searchContent);

				}
			}
		});

		// clear = (Button) findViewById(R.id.clear);
		clear.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				FoodMangerSearchActivity.this.finish();
			}
		});

		lv_food.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				// if (searchAutoDataList.size() != 0
				// || searchAutoDataList != null) {
				// SearchAutoData searchAutoData = mSearchAutoAdapter.mObjects
				// .get(position);
				// searchAutoDataList.remove(searchAutoData);
				// mSearchAutoAdapter.notifyDataSetChanged();
				// }

				// SearchAutoData searchAutoData = mSearchAutoAdapter.mObjects
				// .get(position);
				// mSearchAutoAdapter.clearData();
				mSearchAutoAdapter.mObjects.remove(position);
				// searchAutoDataList.remove(searchAutoData);
				mSearchAutoAdapter.notifyDataSetChanged();
				// if (searchMenuBookBeanList.size() != 0
				// || searchMenuBookBeanList != null) {
				// searchMenuBookBeanList.remove(position);
				// lvFoodSearchAdapter.notifyDataSetChanged();
				// }
				return false;
			}
		});
		lv_food.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub

				// if (!searchMenuBookBeanList.toString().equals(text)) {
				//
				// SearchAutoData data = (SearchAutoData) mSearchAutoAdapter
				// .getItem(position);
				// System.out.println("======data========>>>>" + data);
				// et_search.setText(data.getName());
				// clear.performClick();
				//
				// } else {
				// }

				SearchAutoData data = (SearchAutoData) mSearchAutoAdapter
						.getItem(position);
				et_search.setText(data.getName());
				searchMenuBook(data.getName());
				System.out.println("==========dataBean.getName()============>>"
						+ data.getName());

			}
		});

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		// int id = v.getId();
		// if (id == R.id.clear) {// 搜索按钮
		// saveSearchHistory();
		// mSearchAutoAdapter.initSearchHistory();
		// searchMenuBook(et_search.getText().toString());
		// } else {// "+"号按钮
		// SearchAutoData data = (SearchAutoData) v.getTag();
		// et_search.setText(data.getName());
		// }
	}

	/**
	 * 插入数据
	 */
	private void insertData(String tempName) {
		db = helper.getWritableDatabase();
		db.execSQL("insert into records(name) values('" + tempName + "')");
		db.close();
	}

	/**
	 * 清空数据
	 */
	private void deleteData() {
		db = helper.getWritableDatabase();
		db.execSQL("delete from records");
		db.close();
	}

	/**
	 * 模糊查询数据
	 */
	private void queryData(String tempName) {
		Cursor cursor = helper.getReadableDatabase().rawQuery(
				"select id as _id,name from records where name like '%"
						+ tempName + "%' order by id desc ", null);
		// 创建adapter适配器对象
		adapter = new SimpleCursorAdapter(this, R.layout.item_lv_foodcook,
				cursor, new String[] { "name" }, new int[] { R.id.tv_name },
				CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
		// 设置适配器
		listView.setAdapter(adapter);
		adapter.notifyDataSetChanged();
	}

	/**
	 * 检查数据库中是否已经有该条记录
	 */
	private boolean hasData(String tempName) {
		Cursor cursor = helper.getReadableDatabase().rawQuery(
				"select id as _id,name from records where name =?",
				new String[] { tempName });
		// 判断是否有下一个
		return cursor.moveToNext();
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
						KappUtils.showToast(FoodMangerSearchActivity.this,
								"搜索不到结果哦");
					} else {
						lvFoodSearchAdapter = new LvfoodSearchAdapter(context,
								searchMenuBookBeanList);

						lv_food.setAdapter(lvFoodSearchAdapter);

						lv_food.setOnItemClickListener(new OnItemClickListener() {

							@Override
							public void onItemClick(AdapterView<?> parent,
									View view, int position, long id) {
								// TODO Auto-generated method stub
								Intent intent = new Intent(context,
										NetCookBookDetailActivity.class);
								// intent.putExtra("category",
								// categoryFirstBeanList.get(position));
								intent.putExtra("position", position);
								intent.putExtra("menuBookId",
										searchMenuBookBeanList.get(position)
												.getId());
								startActivity(intent);
							}

						});
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				System.out.println("======result======>>>>" + result.toString());
				HHDLog.e("2"+result.toString());
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

	/*
	 * 保存搜索记录
	 */
	private void saveSearchHistory(String text) {
		if (text.length() < 1) {
			return;
		}
		SharedPreferences sp = getSharedPreferences(SEARCH_HISTORY, 0);
		String longhistory = sp.getString(SEARCH_HISTORY, "");
		String[] tmpHistory = longhistory.split(",");
		ArrayList<String> history = new ArrayList<String>(
				Arrays.asList(tmpHistory));
		if (history.size() > 0) {
			int i;
			for (i = 0; i < history.size(); i++) {
				if (text.equals(history.get(i))) {
					history.remove(i);
					break;
				}
			}
			history.add(0, text);
		}
		if (history.size() > 0) {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < history.size(); i++) {
				sb.append(history.get(i) + ",");
			}
			sp.edit().putString(SEARCH_HISTORY, sb.toString()).commit();
		} else {
			sp.edit().putString(SEARCH_HISTORY, text + ",").commit();
		}
	}

	public ArrayList<String> getSharePrefresh() throws Exception {
		SharedPreferences sp = context.getSharedPreferences(SEARCH_HISTORY, 0);
		ArrayList<String> list = new ArrayList<String>();
		for (int i = 0; i < list.size(); i++) {
			list.add(i + "");
		}
		String data = "";
		for (String item : list) {
			data = data + item + ",";
		}
		sp.edit().putString(SEARCH_HISTORY, data);
		String hosData = sp.getString("SEARCH_HISTORY", null);
		list.clear();
		String[] newData = hosData.split(",");
		for (String str : newData) {
			list.add(str);
		}
		return list;

	}

}