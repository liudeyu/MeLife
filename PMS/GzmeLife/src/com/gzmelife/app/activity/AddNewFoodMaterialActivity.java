package com.gzmelife.app.activity;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.gzmelife.app.R;
import com.gzmelife.app.bean.LocalFoodMaterialLevelOne;
import com.gzmelife.app.bean.LocalFoodMaterialLevelThree;
import com.gzmelife.app.dao.FoodMaterialDAO;
import com.gzmelife.app.tools.KappUtils;
import com.gzmelife.app.tools.MyLogger;

@ContentView(R.layout.activity_add_new_foodmaterial)
public class AddNewFoodMaterialActivity extends BaseActivity implements
		OnClickListener {

	MyLogger HHDLog = MyLogger.HHDLog();

	@ViewInject(R.id.tv_title)
	TextView tv_title;
	@ViewInject(R.id.tv_category)
	TextView tv_category;
	@ViewInject(R.id.tv_title_left)
	TextView tv_title_left;
	@ViewInject(R.id.et_foodMaterialNames)
	EditText et_foodMaterialName;

	private Context context;

	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);

		context = this;
		initView();
	}

	@Override
	protected void onResume() {
		super.onResume();
		HHDLog.v("测试20161103_2305");
	}

	private void initView() {
		tv_title.setText("添加新食材");
		tv_title_left.setVisibility(View.VISIBLE);
		tv_title_left.setText("我的食材");
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.layout_category:
			startActivityForResult((new Intent(context,
					FoodMaterialCategoryActivity.class)), 10001);
			break;
		case R.id.btn_confirm:
			String catogoryName = tv_category.getText().toString();
			String foodMaterialName = et_foodMaterialName.getText().toString()
					.trim();
			if (TextUtils.isEmpty(foodMaterialName)) {
				KappUtils.showToast(context, "请输入食材名");
			} else {
				LocalFoodMaterialLevelThree bean2 = new LocalFoodMaterialLevelThree();

				LocalFoodMaterialLevelOne bean1 = new LocalFoodMaterialLevelOne();
				bean1.setName(catogoryName);

				bean2.setPid(FoodMaterialDAO
						.saveLocalFoodMaterialLevelOne(bean1));
				bean2.setName(foodMaterialName);
				if(!catogoryName.equals("自定义食材")){
					if (FoodMaterialDAO.saveLocalFoodMaterialLevelThree(bean2) == -1) {
						KappUtils.showToast(context, "该食材已经存在，请勿重复添加");
					} else {
						KappUtils.showToast(context, "食材添加成功");
						AddNewFoodMaterialActivity.this.finish();
					}
				}else{
					KappUtils.showToast(context, "请选择食材分类");
				}
				
			}
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK && requestCode == 10001) {
			tv_category.setText(data.getStringExtra("category"));
		}
	}
}