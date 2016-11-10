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

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.GridView;
import android.widget.ExpandableListView.OnGroupClickListener;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gzmelife.app.KappAppliction;
import com.gzmelife.app.R;
import com.gzmelife.app.UrlInterface;
import com.gzmelife.app.adapter.GalleryAdapter;
import com.gzmelife.app.adapter.StandardFoodMaterialAdapter;
import com.gzmelife.app.adapter.GalleryAdapter.OnRecyclerViewItemClickListener;
import com.gzmelife.app.adapter.GvFoodAdapter;
import com.gzmelife.app.bean.CategoryFirstBean;
import com.gzmelife.app.bean.CategorySecondBean;
import com.gzmelife.app.bean.LocalFoodMaterialLevelThree;

/** 美食三级食材 */
@ContentView(R.layout.activity_secondary_food_classification)
public class SecondaryFoodClassificationActivity extends BaseActivity {
	@ViewInject(R.id.rv_goodfood)
	RecyclerView rv_goodfood;
	@ViewInject(R.id.gv_food)
	GridView gv_food;

	GalleryAdapter galleryAdapter;
	GvFoodAdapter gvFoodAdapter;
	String TAG = "SecondaryFoodClassificationActivity";

	private List<CategorySecondBean> mDatas;
	private List<LocalFoodMaterialLevelThree> foods;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		mDatas = new ArrayList<CategorySecondBean>();
		foods = new ArrayList<LocalFoodMaterialLevelThree>();
		for (int i = 0; i < 6; i++) {
			CategorySecondBean b = new CategorySecondBean();
			b.setScName("分类" + i);
			mDatas.add(b);
		}
		for (int i = 0; i < 8; i++) {
			foods.add(new LocalFoodMaterialLevelThree().setName("食材" + i));
		}
		LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
		linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
		rv_goodfood.setLayoutManager(linearLayoutManager);
		// 设置适配器
		galleryAdapter = new GalleryAdapter(this, mDatas, 0);
		rv_goodfood.setAdapter(galleryAdapter);
		gvFoodAdapter = new GvFoodAdapter(foods, context);
		gv_food.setAdapter(gvFoodAdapter);

		galleryAdapter
				.setOnItemClickListener(new OnRecyclerViewItemClickListener() {
					@Override
					public void onItemClick(View view, int position,int pos) {
						for (int i = 0; i < rv_goodfood.getChildCount(); i++) {
							rv_goodfood.getChildAt(i).findViewById(R.id.tv)
									.setSelected(false);
						}
					}
				});
	}

}
