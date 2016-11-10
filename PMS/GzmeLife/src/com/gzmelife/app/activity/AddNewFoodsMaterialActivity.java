package com.gzmelife.app.activity;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
//import android.os.PersistableBundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.gzmelife.app.R;
import com.gzmelife.app.bean.LocalFoodMaterialLevelOne;
import com.gzmelife.app.bean.LocalFoodMaterialLevelThree;
import com.gzmelife.app.dao.FoodMaterialDAO;
import com.gzmelife.app.tools.KappUtils;
import com.gzmelife.app.tools.MyLogger;
import com.gzmelife.app.hhd.TimeNode;

public class AddNewFoodsMaterialActivity extends BaseActivity implements
		OnClickListener {

	MyLogger HHDLog=MyLogger.HHDLog();

	TextView tv_title;
	TextView tv_category;
	TextView tv_title_left;
	EditText et_foodMaterialName;
	EditText et_foodMaterialWight;
	Button btn_confirm;
	TimeNode timeNode;
	int startTime;
	int endTime;
	String step;
	private Context context;
	boolean state;
	private String filePath;

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.activity_add_new_foodsmaterial);
		context = this;
		initView();
	}

	@Override
	protected void onResume() {
		super.onResume();
		HHDLog.v("测试20161103_2235");
	}

	private void initView() {

		timeNode = (TimeNode) getIntent().getSerializableExtra("timeNode");
		state = getIntent().getBooleanExtra("isEdt", false);
		startTime = getIntent().getIntExtra("startTime", 0);
		endTime = getIntent().getIntExtra("endTime", 0);
		step = getIntent().getStringExtra("step");
		filePath = getIntent().getStringExtra("filePath");
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_category = (TextView) findViewById(R.id.tv_category);
		tv_title_left = (TextView) findViewById(R.id.tv_title_left);
		et_foodMaterialWight = (EditText) findViewById(R.id.et_foodMaterialWight);
		et_foodMaterialName = (EditText) findViewById(R.id.et_foodMaterialName);
		btn_confirm = (Button) findViewById(R.id.btn_confirm);
		tv_title.setText("添加新食材");
		tv_title_left.setVisibility(View.VISIBLE);
		tv_title_left.setText("我的食材");
		btn_confirm.setOnClickListener(this);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// if (resultCode == RESULT_OK && requestCode == 10001) {
		// tv_category.setText(data.getStringExtra("category"));
		// }
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		String catogoryName = tv_category.getText().toString();
		String foodMaterialName = et_foodMaterialName.getText().toString()
				.trim();
		if (TextUtils.isEmpty(foodMaterialName)) {
			KappUtils.showToast(context, "请输入食材名");
		} else {
			LocalFoodMaterialLevelThree bean2 = new LocalFoodMaterialLevelThree();

			LocalFoodMaterialLevelOne bean1 = new LocalFoodMaterialLevelOne();
			bean1.setName(catogoryName);

			bean2.setPid(FoodMaterialDAO.saveLocalFoodMaterialLevelOne(bean1));
			bean2.setName(foodMaterialName);
			// bean2.setWeight(foodMaterialWight);
			ArrayList<String> mList = new ArrayList<String>();
			mList.add(foodMaterialName);
			if (FoodMaterialDAO.saveLocalFoodMaterialLevelThree(bean2) == -1) {
				KappUtils.showToast(context, "该食材已经存在，请勿重复添加");
			} else {
				Intent intent = new Intent();
				intent.putExtra("mlistMore", mList);
				System.out.println(">>>>mList=====" + mList);
				// intent.putExtra("wight", foodMaterialWight);
				intent.putExtra("timeNode", timeNode);
				intent.putExtra("startTime", startTime);
				intent.putExtra("endTime", endTime);
				intent.putExtra("step", step);
				intent.putExtra("isEdt", state);
				intent.putExtra("filePath", filePath);
				setResult(RESULT_OK, intent);
				KappUtils.showToast(context, "食材添加成功");
				AddNewFoodsMaterialActivity.this.finish();

				// CookFoodsMaterialManageActivity cook = new
				// CookFoodsMaterialManageActivity();
				// cook.cookfood.finish();
				// AddStepActivity add = new AddStepActivity();
				// add.instance.finish();
			}
		}
		// int foodMaterialWight =
		// Integer.valueOf(et_foodMaterialWight.getText().toString());
	}
}