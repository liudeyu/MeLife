package com.gzmelife.app.activity;

import java.util.ArrayList;
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
import com.gzmelife.app.adapter.CookBookfoodSearchAdapter;
import com.gzmelife.app.adapter.LvCommentAdapter;
import com.gzmelife.app.adapter.LvfoodSearchAdapter;
import com.gzmelife.app.bean.CookBookMenuBookBean;
import com.gzmelife.app.bean.CookBookMenuBookCommentsBean;
import com.gzmelife.app.bean.CookBookMenuBookStepsBean;
import com.gzmelife.app.bean.SearchFoodStoreLibraryBean;
import com.gzmelife.app.bean.SearchMenuBookBean;
import com.gzmelife.app.tools.ImgLoader;
import com.gzmelife.app.tools.KappUtils;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

@ContentView(R.layout.activity_cook_book_food_search)
public class CookBoodFoodSearchActivity extends BaseActivity {
	@ViewInject(R.id.lv_food)
	ListView lv_food;

	@ViewInject(R.id.et_search)
	EditText et_search;

	@ViewInject(R.id.clear)
	Button clear;
	@ViewInject(R.id.tv_title)
	TextView tv_title;
	@ViewInject(R.id.tv_title_left)
	TextView tv_title_left;
	@ViewInject(R.id.iv_sousuo)
	ImageView iv_sousuo;

	private List<SearchFoodStoreLibraryBean> searchMenuBookBeanList = new ArrayList<SearchFoodStoreLibraryBean>();
	
	
	CookBookfoodSearchAdapter lvFoodSearchAdapter;

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		initView();

	}

	// 加载布局
	private void initView() {
		tv_title.setText("搜索");
		tv_title_left.setVisibility(View.VISIBLE);
		tv_title_left.setText("美食");
		lv_food = (ListView) findViewById(R.id.lv_food);
		et_search = (EditText) findViewById(R.id.et_search);

		et_search.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView arg0, int arg1, KeyEvent arg2) {
				String searchContent = et_search.getText().toString().trim();
				et_search.setText("");
				if (arg1 == EditorInfo.IME_ACTION_SEARCH
						&& !searchContent.equals("")) {
					searchMenuBook(searchContent);
				}

				return false;
			}
		});
		iv_sousuo.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String searchContent = et_search.getText().toString().trim();
				if ("".equals(searchContent)) {
					KappUtils.showToast(CookBoodFoodSearchActivity.this, "请输入您要查询的菜名");
				}else {
					searchMenuBook(searchContent);
				}
			}
		});
		clear = (Button) findViewById(R.id.clear);
		clear.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				CookBoodFoodSearchActivity.this.finish();
			}
		});
		
		lv_food.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				if(searchMenuBookBeanList.size()!=0||searchMenuBookBeanList!=null){
					searchMenuBookBeanList.remove(position);
					lvFoodSearchAdapter.notifyDataSetChanged();
				}
				return false;
			}
		});
		lv_food.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(context,
						NetCookBookDetailActivity.class);
				// intent.putExtra("category",
				// categoryFirstBeanList.get(position));
				intent.putExtra("position", position);
				intent.putExtra("menuBookId", searchMenuBookBeanList.get(position).getId());
				startActivity(intent);
			}
		});
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
					if (searchMenuBookBeanList.size()==0) {
						KappUtils.showToast(CookBoodFoodSearchActivity.this, "搜索不到结果哦");
					}else {
						lvFoodSearchAdapter = new CookBookfoodSearchAdapter(context,
								searchMenuBookBeanList);
						
						lv_food.setAdapter(lvFoodSearchAdapter);
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
