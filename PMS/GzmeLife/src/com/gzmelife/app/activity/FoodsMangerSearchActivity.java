package com.gzmelife.app.activity;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.x;
import org.xutils.common.Callback.CommonCallback;
import org.xutils.http.RequestParams;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gzmelife.app.KappAppliction;
import com.gzmelife.app.R;
import com.gzmelife.app.UrlInterface;
import com.gzmelife.app.adapter.LvfoodSearchAdapter;
import com.gzmelife.app.adapter.MySearchHosAdapter;
import com.gzmelife.app.adapter.SearchAutoAdapter;
import com.gzmelife.app.bean.SearchAutoData;
import com.gzmelife.app.bean.SearchFoodBean;
import com.gzmelife.app.bean.SearchHosBean;
import com.gzmelife.app.bean.SearchMenuBookBean;
import com.gzmelife.app.dao.FoodMaterialDAO;
import com.gzmelife.app.hhd.SearchsDetailActivity;
import com.gzmelife.app.tools.KappUtils;
import com.gzmelife.app.tools.MyLogger;
import com.gzmelife.app.tools.RecordSQLiteOpenHelper;
import com.gzmelife.app.hhd.TimeNode;
import com.gzmelife.app.views.MyListView;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
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


/**
 * 界面【搜索】（标准食材库搜索前）
 */
public class FoodsMangerSearchActivity extends BaseActivity implements
		OnClickListener {

	MyLogger HHDLog = MyLogger.HHDLog();

	ListView lv_food;
	MyListView listView;
	EditText et_search;
	Button clear, btn_addFoodMaterial;
	TextView tv_title;
	TextView tv_title_left;
	ImageView iv_sousuo;

	String TAG = "PrivateFragment";
	SearchMenuBookBean bean;
	private List<SearchMenuBookBean> searchMenuBookBeanList = new ArrayList<SearchMenuBookBean>();
	private List<SearchAutoData> searchAutoDataList = new ArrayList<SearchAutoData>();
	List<SearchHosBean> list = new ArrayList<SearchHosBean>();
	private RecordSQLiteOpenHelper helper = new RecordSQLiteOpenHelper(this);
	public static final String SEARCH_HISTORY = "search_history";
	private SearchAutoAdapter mSearchAutoAdapter;
	private SQLiteDatabase db;
	private BaseAdapter adapter;
	LvfoodSearchAdapter lvFoodSearchAdapter;
	private MySearchHosAdapter myAdapter;
	public static FoodsMangerSearchActivity fooddManger = null;
	private ArrayList<String> mlistMore = new ArrayList<String>();
	TimeNode timeNode;
	int startTime;
	int endTime;
	String step;
	boolean state;
	private String filePath;
	private int count;

	@Override
	protected void onResume() {
		super.onResume();
		HHDLog.v("界面【搜索】（标准食材库_搜索前）");
	}

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.activity_good_foods_search);
		fooddManger = this;
		if (KappAppliction.getLiLogin() == 1) {
			initView();

		} else {
			KappUtils.showToast(context, "暂未登录");
			Intent intent = new Intent(FoodsMangerSearchActivity.this,
					LoginActivity.class);
			startActivity(intent);
			FoodsMangerSearchActivity.this.finish();
			return;
		}

	}

	// 加载布局
	private void initView() {
		filePath = getIntent().getStringExtra("filePath");
		timeNode = (TimeNode) getIntent().getSerializableExtra("timeNode");
		state = getIntent().getBooleanExtra("isEdt", false);
		startTime = getIntent().getIntExtra("startTime", 0);
		endTime = getIntent().getIntExtra("endTime", 0);
		step = getIntent().getStringExtra("step");
		count = getIntent().getIntExtra("count", 0);
		mSearchAutoAdapter = new SearchAutoAdapter(this, -1, this);
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title_left = (TextView) findViewById(R.id.tv_title_left);
		iv_sousuo = (ImageView) findViewById(R.id.iv_sousuo);
		clear = (Button) findViewById(R.id.clear);
		btn_addFoodMaterial = (Button) findViewById(R.id.btn_addFoodMaterial);
		btn_addFoodMaterial.setVisibility(View.GONE);
		clear.setOnClickListener(this);
		tv_title.setText("搜索");
		tv_title_left.setVisibility(View.VISIBLE);
		tv_title_left.setText("我的食材");
		lv_food = (ListView) findViewById(R.id.lv_food);
		lv_food.setAdapter(mSearchAutoAdapter);
		listView = (MyListView) findViewById(R.id.listView);
		et_search = (EditText) findViewById(R.id.et_search);

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
		// // mSearchAutoAdapter.initSearchHistory();
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
							int id = FoodMaterialDAO
									.getCategoryIds(searchContent);
							if (id == -1) {
								KappUtils.showToast(
										FoodsMangerSearchActivity.this,
										"搜索不到结果哦");
							} else {
								// ArrayList<LocalFoodMaterialLevelThree> mList
								// = FoodMaterialDAO
								// .getAllFoodMaterialByCategoryIds(id);
								// String name = mList.get(id).toString();
								// searchMenuBooks(mList);
								KappUtils.showToast(
										FoodsMangerSearchActivity.this, "结果哦");
							}

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

			}
		});
		btn_addFoodMaterial.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (count == 5 || count > 5) {
					KappUtils.showToast(context, "您最多只能有5样食材");
				}

				Intent intent = new Intent(context, AddNewFoodsMaterialActivity.class);
				intent.putExtra("timeNode", timeNode);
				intent.putExtra("startTime", startTime);
				intent.putExtra("endTime", endTime);
				intent.putExtra("step", step);
				intent.putExtra("isEdt", state);
				intent.putExtra("filePath", filePath);
				startActivityForResult(intent, 10569);
				// AddStepActivity a = new AddStepActivity();
				// CookFoodsMaterialManageActivity cook = new
				// CookFoodsMaterialManageActivity();
				// a.instance.finish();
				// cook.cookfood.finish();
			}
		});
		iv_sousuo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String searchContent = et_search.getText().toString().trim();
				if ("".equals(searchContent)) {
					KappUtils.showToast(FoodsMangerSearchActivity.this,
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
				FoodsMangerSearchActivity.this.finish();
			}
		});

		listView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				// if (searchAutoDataList.size() != 0
				// || searchAutoDataList != null) {
				// searchAutoDataList.remove(position);
				// mSearchAutoAdapter.notifyDataSetChanged();
				// }
				// if (searchMenuBookBeanList.size() != 0
				// || searchMenuBookBeanList != null) {
				// searchMenuBookBeanList.remove(position);
				// lvFoodSearchAdapter.notifyDataSetChanged();
				// }

				deleteData(list.get(position).getId());
				getData(list.get(position).getContent());
				queryData("");
				myAdapter.notifyDataSetChanged();

				return false;
			}
		});
		listView.setOnItemClickListener(new OnItemClickListener() {

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

				TextView textView = (TextView) view
						.findViewById(R.id.auto_content);
				String name = textView.getText().toString();
				et_search.setText(name);
				searchMenuBook(name);

			}
		});

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
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

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		// TODO Auto-generated method stub
		super.onActivityResult(arg0, arg1, arg2);
		if (arg1 == RESULT_OK) {
			switch (arg0) {
			case 10489:
				mlistMore = arg2.getStringArrayListExtra("mlistMore");
				System.out.println(">>mlistMoreSearchs=======" + mlistMore);
				Intent intent1 = new Intent();
				intent1.putExtra("mlistMore", mlistMore);
				setResult(RESULT_OK, intent1);
				// startActivity(intent);
				FoodsMangerSearchActivity.this.finish();
				break;
			case 10569:
				mlistMore = arg2.getStringArrayListExtra("mlistMore");
				System.out.println(">>mlistMoreFood=======" + mlistMore);
				Intent intent2 = new Intent();
				intent2.putExtra("mlistMore", mlistMore);
				setResult(RESULT_OK, intent2);
				// startActivity(intent);
				FoodsMangerSearchActivity.this.finish();
				break;
			default:
				break;
			}
		}
	}

	private void searchMenuBook(final String text) {
		// 搜索
		showDlg();
		RequestParams params = new RequestParams(UrlInterface.URL_SERACHFOODSTORE);
		params.addBodyParameter("foodStoreName", text);
		params.addBodyParameter("userId", KappAppliction.myApplication.getUser().getId());
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
							obj.getJSONObject("data").getJSONArray("foodStores").toString(),
							new TypeToken<List<SearchFoodBean>>() {
							}.getType());
					if (searchMenuBookBeanList.size() == 0) {
						KappUtils.showToast(FoodsMangerSearchActivity.this,
								"搜索不到结果哦");
					} else {

						Intent intent = new Intent(context,
								SearchsDetailActivity.class);
						intent.putExtra("name", text);
						intent.putExtra("timeNode", timeNode);
						intent.putExtra("startTime", startTime);
						intent.putExtra("endTime", endTime);
						intent.putExtra("step", step);
						intent.putExtra("isEdt", state);
						intent.putExtra("count", count);
						intent.putExtra("filePath", filePath);
						startActivityForResult(intent, 10489);
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

}