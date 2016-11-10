package com.gzmelife.app.activity;

import java.util.ArrayList;

import com.gzmelife.app.R;
import com.gzmelife.app.adapter.LvfoodssSearchsAdapter;
import com.gzmelife.app.bean.UserInfoBean;
import com.gzmelife.app.tools.KappUtils;
import com.gzmelife.app.tools.MyLogger;
import com.gzmelife.app.hhd.TimeNode;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class SearchssDetailActivity extends BaseActivity {

	MyLogger HHDLog = MyLogger.HHDLog();

	private ListView lv_food;
	LvfoodssSearchsAdapter lvFoodSearchAdapter;
	TextView tv_title;

	private ArrayList<String> thressList = new ArrayList<String>();
	private String name;
	UserInfoBean user;

	TimeNode timeNode;
	int startTime;
	int endTime;
	String step;
	boolean state;
	private String filePath, searchContent;
	private int count;
	private ArrayList<String> threeList;

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.activity_search_deatail);
		// if (KappAppliction.liLogin == 1) {
		// user = KappAppliction.myApplication.getUser();
		// if (user != null) {
		// searchMenuBook(text);
		// }
		//
		// } else {
		// KappUtils.showToast(context, "暂未登录");
		// Intent intent = new Intent(SearchsDetailActivity.this,
		// LoginActivity.class);
		// startActivity(intent);
		// SearchsDetailActivity.this.finish();
		// return;
		// }

		initView();

	}

	private void initView() {
		threeList = getIntent().getStringArrayListExtra("threesList");
		System.out.println(">>>>>thressList/////----" + thressList);
		filePath = getIntent().getStringExtra("filePath");
		timeNode = (TimeNode) getIntent().getSerializableExtra("timeNode");
		searchContent = getIntent().getStringExtra("searchContent");
		state = getIntent().getBooleanExtra("isEdt", false);
		startTime = getIntent().getIntExtra("startTime", 0);
		endTime = getIntent().getIntExtra("endTime", 0);
		step = getIntent().getStringExtra("step");
		count = getIntent().getIntExtra("count", 0);
		lv_food = (ListView) findViewById(R.id.lv_food);
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setText("搜索结果");//我的食材库
		name = getIntent().getStringExtra("name");

		final ArrayList<String> mList = new ArrayList<String>();
		mList.add(searchContent);
		for (int i = 0; i < threeList.size(); i++) {
			String name = threeList.get(i);
			if (!searchContent.equals(name)) {
				if (name.equals(searchContent)) {
					mList.add(name);
				}
			}

		}
		System.out.println(">>>>>mList/////----" + mList);
		lvFoodSearchAdapter = new LvfoodssSearchsAdapter(context, mList);
		// searchMenuBook(name);
		lv_food.setAdapter(lvFoodSearchAdapter);
		lv_food.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				// String strName = thressList.get(position).getName();
				// mList.add(strName);
				Intent intent = new Intent();
				intent.putExtra("mlistMore", mList);
				intent.putExtra("timeNode", timeNode);
				intent.putExtra("startTime", startTime);
				intent.putExtra("endTime", endTime);
				intent.putExtra("step", step);
				intent.putExtra("isEdt", state);
				intent.putExtra("filePath", filePath);
				setResult(RESULT_OK, intent);
				SearchssDetailActivity.this.finish();
				KappUtils.showToast(context, "食材添加成功");

				// startActivity(intent);
				// CookFoodsMaterialManageActivity cook = new
				// CookFoodsMaterialManageActivity();
				// cook.cookfood.finish();
				// AddStepActivity add = new AddStepActivity();
				// add.instance.finish();
				// FoodsMangerSearchActivity foods = new
				// FoodsMangerSearchActivity();
				// foods.fooddManger.finish();

			}
		});
	}

}
