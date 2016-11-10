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

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gzmelife.app.KappAppliction;
import com.gzmelife.app.R;
import com.gzmelife.app.UrlInterface;
import com.gzmelife.app.adapter.GalleryAdapter;
import com.gzmelife.app.adapter.StandardFoodMaterialAdapter;
import com.gzmelife.app.adapter.GalleryAdapter.OnRecyclerViewItemClickListener;
import com.gzmelife.app.adapter.MyFoodMaterialChildAdapter;
import com.gzmelife.app.bean.CategoryFirstBean;
import com.gzmelife.app.bean.CategorySecondBean;
import com.gzmelife.app.bean.LocalFoodMaterialLevelOne;
import com.gzmelife.app.bean.LocalFoodMaterialLevelThree;
import com.gzmelife.app.dao.FoodMaterialDAO;
import com.gzmelife.app.tools.KappUtils;
import com.gzmelife.app.views.GridViewForScrollView;

/** 个人中心-三级食材(食材库管理) */
@ContentView(R.layout.activity_standard_food_material_details)
public class StandardFoodMaterialDetailsActivity extends BaseActivity implements
		android.view.View.OnClickListener {
	protected   String TAG = "StandardFoodMaterialDetailsActivity";
	@ViewInject(R.id.tv_title)
	TextView tv_title;
	@ViewInject(R.id.tv_selectNum)
	TextView tv_selectNum;
	@ViewInject(R.id.tv_all)
	TextView tv_all;

	@ViewInject(R.id.layout_manage)
	View layout_manage;

	@ViewInject(R.id.rv_goodfood)
	RecyclerView rv_goodfood;

	@ViewInject(R.id.iv_car)
	ImageView iv_car;

	@ViewInject(R.id.gv_food)
	GridViewForScrollView gv_food;

	private GalleryAdapter galleryAdapter;
	private MyFoodMaterialChildAdapter gvFoodAdapter;

	private List<CategorySecondBean> mDatas; // 二级分类
	private List<LocalFoodMaterialLevelThree> foods; // 三级食材

	private List<String> selectedList = new ArrayList<String>(); // 选中了的食材的名字

	private int p;

	private CategoryFirstBean category; // 一级分类
	private int position; // 二级的index
	private int count, ChildPosition;
	private Context context;

	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);

		context = this;
		getIntentData();
		initView();
		getData();
	}

	private void getIntentData() {
		category = (CategoryFirstBean) getIntent().getSerializableExtra(
				"category");
		ChildPosition = getIntent().getIntExtra("ChildPosition", 0);
		position = getIntent().getIntExtra("position", 0);
		count = getIntent().getIntExtra("count", 0);
	}

	@SuppressWarnings("deprecation")
	private void initView() {
		tv_title.setText(category.getSecondCategorieList().get(ChildPosition)
				.getScName());
		// 控制界面显示三个，不让用户随意滑动
		// rv_goodfood.setOnScrollListener(new OnScrollListener() {
		// });

		tv_all.setOnClickListener(this);
	
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		// case R.id.btn_titleRight:
		// if (btn_titleRight.getText().toString().equals("编辑")) {
		// clearSelect();
		// btn_titleRight.setText("取消");
		// layout_manage.setVisibility(View.VISIBLE);
		// gvFoodAdapter.setClickable(true);
		// gvFoodAdapter.notifyDataSetChanged();
		// } else {
		// cancelEdit();
		// }
		// break;
		case R.id.btn_add:
			if (selectedList.size() == 0) {
				KappUtils.showToast(context, "请选择食材");
			} else {
				LocalFoodMaterialLevelOne bean1 = new LocalFoodMaterialLevelOne();
				bean1.setName(category.getFcName());

				for (int i = 0; i < selectedList.size(); i++) {
					LocalFoodMaterialLevelThree bean2 = new LocalFoodMaterialLevelThree();
					bean2.setPid(FoodMaterialDAO
							.saveLocalFoodMaterialLevelOne(bean1));
					bean2.setName(selectedList.get(i));
					FoodMaterialDAO.saveLocalFoodMaterialLevelThree(bean2);
				}
				KappUtils.showToast(context, "食材添加成功");
				// 清除选中
				clearSelect();
			}
			break;
		case R.id.tv_all:
			if (selectedList.size() == foods.size()) {
				clearSelect();
			} else/* if (selectedList.size() == 0) */{
				selectedList.clear();
				for (int i = 0; i < foods.size(); i++) {
					foods.get(i).setChecked(true);
					selectedList.add(foods.get(i).getName());
				}
				gvFoodAdapter.notifyDataSetChanged();
				tv_selectNum.setText("" + selectedList.size());
			}
			break;
		}
	}

	private void clearSelect() {
		selectedList.clear();
		for (int i = 0; i < foods.size(); i++) {
			foods.get(i).setChecked(false);
		}
		gvFoodAdapter.notifyDataSetChanged();
		tv_selectNum.setText("0");
	}

	private void cancelEdit() {
		layout_manage.setVisibility(View.GONE);
		for (LocalFoodMaterialLevelThree b : foods) {
			b.setChecked(false);
		}
		gvFoodAdapter.setClickable(false);
		gvFoodAdapter.notifyDataSetChanged();
	}

	private void getData() {
		mDatas = category.getSecondCategorieList();

		foods = new ArrayList<LocalFoodMaterialLevelThree>();
		// for (int i = 0; i < 8; i++) {
		// foods.add(new LocalFoodMaterialLevelThree().setName("食材"+i));
		// }
		showDlg();
		getById(position);
		final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(
				this);
		linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
		// 控制初始选择显示的Item
		p = position;
		p -= 1;
		if (p < mDatas.size()) {
			p = 0;
		}
		if (p > mDatas.size() - 3) {
			p = mDatas.size();
		}

		System.out.println(">>>p======" + p);
		linearLayoutManager.scrollToPosition(ChildPosition);
		rv_goodfood.setLayoutManager(linearLayoutManager);

		// 设置适配器
		galleryAdapter = new GalleryAdapter(this, mDatas, position);
		galleryAdapter
				.setOnItemClickListener(new OnRecyclerViewItemClickListener() {
					@Override
					public void onItemClick(View view, int position,int pos) {
						System.out.println(">>>>pos===" + pos);
						for (int i = 0; i < rv_goodfood.getChildCount(); i++) {
							rv_goodfood.getChildAt(i).findViewById(R.id.tv)
									.setSelected(false);
							tv_title.setText(category.getSecondCategorieList()
									.get(pos).getScName());
							getById(position);
						}
					}
				});
		rv_goodfood.setAdapter(galleryAdapter);

		// gvFoodAdapter = new MyFoodMaterialChildAdapter(context, foods, 1, new
		// MyFoodMaterialChildAdapter.OnReceiver() {
		// @Override
		// public void onCheckChange(String id, boolean isChecked) {
		// if (isChecked) {
		// selectedList.add(id);
		// } else {
		// selectedList.remove(id);
		// }
		// tv_selectNum.setText("" + selectedList.size());
		// }
		// });
		// gvFoodAdapter.setClickable(false);
		// gv_food.setAdapter(gvFoodAdapter);
	}

	private void getById(int position) {
		RequestParams params = new RequestParams(
				UrlInterface.URL_FOOD_CATEGORY_ID);
		params.addBodyParameter("categoryId", position + "");
		params.addBodyParameter("standardStatus", 1 + "");
		x.http().post(params, new CommonCallback<String>() {
			@Override
			public void onSuccess(String result) {
				Log.i(TAG, "修改步骤UID  net getById-->onSuccess:"+result);

				closeDlg();
				Gson gson = new Gson();
				JSONObject obj;
				try {
					obj = new JSONObject(result);
					foods = gson.fromJson(obj.getJSONObject("data")
							.getJSONArray("foodStores").toString(),
							new TypeToken<List<LocalFoodMaterialLevelThree>>() {
							}.getType());

					gvFoodAdapter = new MyFoodMaterialChildAdapter(context,
							foods, 1,
							new MyFoodMaterialChildAdapter.OnReceiver() {
								
								@Override
								public void onCheckChange(String name, String id, boolean isChecked) {
									// TODO Auto-generated method stub
									if (isChecked) {
										selectedList.add(name);
									} else {
										selectedList.remove(name);
									}
									tv_selectNum.setText(""
											+ selectedList.size());
								}
							});
					gvFoodAdapter.setClickable(true);
					gv_food.setAdapter(gvFoodAdapter);
					gvFoodAdapter.notifyDataSetChanged();

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
