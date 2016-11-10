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
import com.gzmelife.app.KappAppliction;
import com.gzmelife.app.R;
import com.gzmelife.app.UrlInterface;
import com.gzmelife.app.adapter.CookBookStepAdapter;
import com.gzmelife.app.adapter.LvCommentAdapter;
import com.gzmelife.app.adapter.LvfoodSearchAdapter;
import com.gzmelife.app.adapter.MySearchHosAdapter;
import com.gzmelife.app.adapter.SearchAutoAdapter;
import com.gzmelife.app.adapter.SearchsAutoAdapter;
import com.gzmelife.app.bean.CookBookMenuBookBean;
import com.gzmelife.app.bean.CookBookMenuBookCommentsBean;
import com.gzmelife.app.bean.CookBookMenuBookStepsBean;
import com.gzmelife.app.bean.LocalFoodMaterialLevelOne;
import com.gzmelife.app.bean.SearchAutoData;
import com.gzmelife.app.bean.SearchFoodBean;
import com.gzmelife.app.bean.SearchHosBean;
import com.gzmelife.app.bean.SearchMenuBookBean;
import com.gzmelife.app.bean.UserInfoBean;
import com.gzmelife.app.dao.FoodMaterialDAO;
import com.gzmelife.app.tools.GoodFoodSQLiteOpenHelper;
import com.gzmelife.app.tools.ImgLoader;
import com.gzmelife.app.tools.KappUtils;
import com.gzmelife.app.tools.RecordSQLiteOpenHelper;
import com.gzmelife.app.views.MyListView;
import com.gzmelife.app.views.TipConfirmView;

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

public class GoodFoodSearchActivity extends BaseActivity implements
		OnClickListener {
	MyListView listView;
	MyListView lv_food;
	EditText et_search;
	TextView tv_clear;
	Button clear;
	TextView tv_title;
	TextView tv_title_left;
	ImageView iv_sousuo;

	UserInfoBean user;

	String TAG = "PrivateFragment";
	SearchMenuBookBean bean;
	private List<SearchMenuBookBean> searchMenuBookBeanList = new ArrayList<SearchMenuBookBean>();
	private List<SearchAutoData> searchAutoDataList = new ArrayList<SearchAutoData>();
	private List<SearchHosBean> list = new ArrayList<SearchHosBean>();
	private GoodFoodSQLiteOpenHelper helper = new GoodFoodSQLiteOpenHelper(this);
	public static final String SEARCH_HISTORY = "search_history";
	private SearchsAutoAdapter mSearchAutoAdapter;
	private SQLiteDatabase db;
	private BaseAdapter adapter;
	private MySearchHosAdapter myAdapter;
	LvfoodSearchAdapter lvFoodSearchAdapter;

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.activity_goods_foods_search);
		if (KappAppliction.getLiLogin() == 1) {
			initView();

		} else {
			KappUtils.showToast(context, "暂未登录");
			Intent intent = new Intent(GoodFoodSearchActivity.this,
					LoginActivity.class);
			startActivity(intent);
			GoodFoodSearchActivity.this.finish();
			return;
		}

	}

	// 加载布局
	private void initView() {
		mSearchAutoAdapter = new SearchsAutoAdapter(this, -1, this);
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title_left = (TextView) findViewById(R.id.tv_title_left);
		iv_sousuo = (ImageView) findViewById(R.id.iv_sousuo);
		clear = (Button) findViewById(R.id.clear);
		clear.setOnClickListener(this);

		tv_title.setText("搜索");
		tv_title_left.setVisibility(View.VISIBLE);
		tv_title_left.setText("美食");
		lv_food = (MyListView) findViewById(R.id.lv_food);
		listView = (MyListView) findViewById(R.id.listView);
		// listView.setAdapter(mSearchAutoAdapter);
		et_search = (EditText) findViewById(R.id.et_search);
		tv_clear = (TextView) findViewById(R.id.tv_clear);

		// 第一次进入查询所有的历史记录
		queryData("");

		// et_search.setOnEditorActionListener(new OnEditorActionListener() {
		//
		// @Override
		// public boolean onEditorAction(TextView arg0, int arg1, KeyEvent arg2)
		// {
		// String searchContent = et_search.getText().toString().trim();
		// et_search.setText("");
		// saveSearchHistory(searchContent);
		// // mSearchAutoAdapter.initSearchHistory();
		// if (arg1 == EditorInfo.IME_ACTION_SEARCH
		// && !searchContent.equals("")) {
		// searchMenuBook(searchContent);
		// }
		// if (arg1 == EditorInfo.IME_ACTION_SEARCH
		// && searchContent.equals("")) {
		// mSearchAutoAdapter.initSearchHistory();
		// mSearchAutoAdapter.notifyDataSetChanged();
		// }
		//
		// return false;
		// }
		// });

		// 搜索框的键盘搜索键点击回调
		et_search.setOnKeyListener(new View.OnKeyListener() {// 输入完后按键盘上的搜索键

					public boolean onKey(View v, int keyCode, KeyEvent event) {
						if (keyCode == KeyEvent.KEYCODE_ENTER
								&& event.getAction() == KeyEvent.ACTION_DOWN) {// 修改回车键功能
							// 先隐藏键盘
							((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
									.hideSoftInputFromWindow(getCurrentFocus()
											.getWindowToken(),
											InputMethodManager.HIDE_NOT_ALWAYS);
							// 按完搜索键后将当前查询的关键字保存起来,如果该关键字已经存在就不执行保存
							String searchContent = et_search.getText()
									.toString().trim();
							boolean hasData = hasData(searchContent);
							if (!hasData) {
								if (!searchContent.equals("")) {
									insertData(searchContent);
								}
								list.clear();
								queryData("");
							}
							// TODO 根据输入的内容模糊查询商品，并跳转到另一个界面，由你自己去实现
							// KappUtils.showToast(FoodMangerSearchsActivity.this,
							// "无结果");

							searchMenuBook(searchContent);
							// et_search.setText("");
							// if (!searchContent.equals("")) {
							// searchMenuBook(searchContent);
							// } else {
							// mSearchAutoAdapter.initSearchHistory();
							// adapter.notifyDataSetChanged();
							// adapter.notifyDataSetChanged();
							// }
						}
						return false;
					}
				});
		et_search.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				// mSearchAutoAdapter.performFiltering(s);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

				String tempName = et_search.getText().toString();
				// 根据tempName去模糊查询数据库中有没有数据
				// queryData(tempName);
			}
		});

		iv_sousuo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String searchContent = et_search.getText().toString().trim();
				if ("".equals(searchContent)) {
					KappUtils.showToast(GoodFoodSearchActivity.this,
							"请输入您要查询的菜名");
				} else {
					// searchMenuBook(searchContent);

				}
			}
		});

		// clear = (Button) findViewById(R.id.clear);
		clear.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				GoodFoodSearchActivity.this.finish();
			}
		});

		listView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					final int position, long id) {
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
				// mSearchAutoAdapter.mObjects.remove(position);
				// searchAutoDataList.remove(searchAutoData);
				// mSearchAutoAdapter.notifyDataSetChanged();

				TipConfirmView.showConfirmDialog(GoodFoodSearchActivity.this,
						"是否确认删除所选择的食材？", new OnClickListener() {
							@Override
							public void onClick(View v) {
								TipConfirmView.dismiss();
								// 数据库中删除
								deleteData(list.get(position).getId());
								getData(list.get(position).getContent());
								queryData("");
								myAdapter.notifyDataSetChanged();
								// onResume();
							}
						});

				// if (searchMenuBookBeanList.size() != 0
				// || searchMenuBookBeanList != null) {
				// searchMenuBookBeanList.remove(position);
				// lvFoodSearchAdapter.notifyDataSetChanged();
				// }
				return true;
			}
		});
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub

				TextView textView = (TextView) view
						.findViewById(R.id.auto_content);
				String name = textView.getText().toString();
				et_search.setText(name);
				searchMenuBook(name);
				System.out.println("==========dataBean.getName()============>>"
						+ name);

			}
		});

	}

	private void getData(String tempName) {
		if (list.size() > 0)
			list.clear();
		list.addAll(queryData(tempName));

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
	private void deleteData(int position) {
		db = helper.getWritableDatabase();
		db.execSQL("delete from records where _id = " + position);
		// db.delete(records, "_id="+position, null);
		// db.execSQL("delete from records");
		db.close();
	}

	/**
	 * 模糊查询数据
	 */
	public List<SearchHosBean> queryData(String tempName) {
		Cursor cursor = helper.getReadableDatabase().rawQuery(
				"select _id,name from records where name like '%" + tempName
						+ "%' order by _id desc ", null);
		// List<SearchHosBean> list = new ArrayList<SearchHosBean>();
		while (cursor.moveToNext()) {
			SearchHosBean s = new SearchHosBean();
			s.setId((int) cursor.getLong(0));
			s.setContent(cursor.getString(1));

			Log.w("tag", "" + cursor.getString(1));

			list.add(s);
		}
		myAdapter = new MySearchHosAdapter(list, this);
		listView.setAdapter(myAdapter);
		// adapter = new SimpleCursorAdapter(this,
		// R.layout.auto_seach_list_item,
		// cursor, new String[] { "name" },
		// new int[] { R.id.auto_content },
		// CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
		// 设置适配器
		return list;
	}

	/**
	 * 检查数据库中是否已经有该条记录
	 */
	private boolean hasData(String tempName) {
		Cursor cursor = helper.getReadableDatabase().rawQuery(
				"select _id,name from records where name =?",
				new String[] { tempName });
		// 判断是否有下一个
		return cursor.moveToNext();
	}

	private void searchMenuBook(final String text) {
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
						KappUtils.showToast(GoodFoodSearchActivity.this,
								"搜索不到结果哦");
					} else {

						Intent intent = new Intent(context,
								GoodFoodSearchDetailActivity.class);
						intent.putExtra("name", text);
						startActivity(intent);

					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
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

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

	}

}
