package com.gzmelife.app.activity;

import java.util.List;

import org.xutils.DbManager;
import org.xutils.ex.DbException;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import com.gzmelife.app.KappAppliction;
import com.gzmelife.app.R;
import com.gzmelife.app.adapter.FoodLibraryAdapter;
import com.gzmelife.app.bean.LocalFoodMaterialLevelOne;
import com.gzmelife.app.views.ListViewForScrollView;

import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
@ContentView(R.layout.activity_my_food)
public class FoodLibraryActivity extends BaseActivity implements OnCheckedChangeListener{
	@ViewInject(R.id.tv_search)
	TextView tv_search;
	
	@ViewInject(R.id.tv_title)
	TextView tv_title;
	
	@ViewInject(R.id.lv_myFoodLibrary)
	ListViewForScrollView lv_myFoodLibrary;
	
	@ViewInject(R.id.lv_foodLibrary)
	ListViewForScrollView lv_foodLibrary;
	@ViewInject(R.id.tv_title_left)
	TextView tv_title_left;
	FoodLibraryAdapter foodLibraryAdapter ;
	RadioGroup rg_foodLibrary;
	
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		
		init();
	
	}
	
	private void init(){
		tv_title.setText("我的食材");
		tv_title_left.setVisibility(View.VISIBLE);
		tv_title_left.setText("个人中心");
		rg_foodLibrary.check(R.id.rg_foodLibrary);
		lv_foodLibrary.setVisibility(View.GONE);
		lv_myFoodLibrary.setVisibility(View.VISIBLE);
		
		DbManager db=KappAppliction.db;
		List<LocalFoodMaterialLevelOne> foodLibrarys=null;
		foodLibraryAdapter=new FoodLibraryAdapter(context, foodLibrarys);
		lv_myFoodLibrary.setAdapter(foodLibraryAdapter);
		
		try {
			 foodLibrarys= db.selector(LocalFoodMaterialLevelOne.class).findAll();
			 foodLibraryAdapter.notifyDataSetChanged();
		} catch (DbException e) {
			e.printStackTrace();
		}
		rg_foodLibrary.setOnCheckedChangeListener(this);
	}
	
	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		if(group.getId()==rg_foodLibrary.getId()){
			switch (checkedId) {
				case R.id.rb_myFoodLibrary:
					lv_foodLibrary.setVisibility(View.GONE);
					lv_myFoodLibrary.setVisibility(View.VISIBLE);
					
					break;
				case R.id.rb_foodLibrary:
					lv_foodLibrary.setVisibility(View.VISIBLE);
					lv_myFoodLibrary.setVisibility(View.GONE);
					
					
					break;
			default:
				break;
			}
		}
	}
}
