package com.gzmelife.app.activity;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.x;
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
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gzmelife.app.R;
import com.gzmelife.app.UrlInterface;
import com.gzmelife.app.adapter.GalleryAdapter;
import com.gzmelife.app.adapter.GalleryAdapter.OnRecyclerViewItemClickListener;
import com.gzmelife.app.adapter.MyFoodMaterialChildAdapter;
import com.gzmelife.app.bean.CategoryFirstBean;
import com.gzmelife.app.bean.CategorySecondBean;
import com.gzmelife.app.bean.LocalFoodMaterialLevelOne;
import com.gzmelife.app.bean.LocalFoodMaterialLevelThree;
import com.gzmelife.app.dao.FoodMaterialDAO;
import com.gzmelife.app.tools.KappUtils;
import com.gzmelife.app.tools.MyLogger;
import com.gzmelife.app.hhd.TimeNode;
import com.gzmelife.app.views.GridViewForScrollView;

/**
 * 界面【购物车】添加标准食材库
 *
 * 个人中心-三级食材(食材库管理)
 */
@ContentView(R.layout.activity_standard_foods_material_details)
public class StandardFoodsMaterialDetailsActivity extends BaseActivity implements android.view.View.OnClickListener {
	MyLogger HHDLog = MyLogger.HHDLog();

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
	/** 选中了的食材的名字 */
	private ArrayList<String> selectedList = new ArrayList<String>();
	/** 选中了的食材的名字 */
	private ArrayList<String> selectedListID = new ArrayList<String>();

	private int p;
	private String TAG="StandardFoodsMaterialDetailsActivity";
	/** 一级分类 */
	private CategoryFirstBean category;
	/** 二级的index */
	private int position, ChildPosition;
	int startTime;
	int endTime;
	String step;
	TimeNode timeNode;
	boolean state;
	private Context context;
	private int count;
	private String filePath;

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
		filePath = getIntent().getStringExtra("filePath");
		ChildPosition = getIntent().getIntExtra("ChildPosition", 0);
		timeNode = (TimeNode) getIntent().getSerializableExtra("timeNode");
		startTime = getIntent().getIntExtra("startTime", 0);
		endTime = getIntent().getIntExtra("endTime", 0);
		step = getIntent().getStringExtra("step");
		count = getIntent().getIntExtra("count", 0);
		position = getIntent().getIntExtra("position", 0);
		state = getIntent().getBooleanExtra("isEdt", false);
	}

	@Override
	protected void onResume() {
		super.onResume();
		HHDLog.v("界面【标准食材库“二级 展现”&购物车】");
	}

	@SuppressWarnings("deprecation")
	private void initView() {

		tv_title.setText(category.getSecondCategorieList().get(ChildPosition)
				.getScName());

		// 控制界面显示三个，不让用户随意滑动
		rv_goodfood.setOnScrollListener(new OnScrollListener() {
		});

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
			// final ArrayList<String> listId = (ArrayList<String>) myAdapter
			// .getSelectedIds();
			// if (listId.size() == 0) {
			// KappUtils.showToast(context, "您未选中任何食材");
			// } else {
			// Intent intent = new Intent();
			// intent.putExtra("listId", listId);
			// setResult(RESULT_OK, intent);
			// StandardFoodsMaterialDetailsActivity.this.finish();

			// }

			if (selectedList.size() == 0) {
				KappUtils.showToast(context, "请选择食材");
			} else {
				int c = count + selectedList.size();
				if (c <= 5) {
					LocalFoodMaterialLevelOne bean1 = new LocalFoodMaterialLevelOne();
					bean1.setName(category.getFcName());

					/** 20161026缓存选中的食材的名称 */
					ArrayList<String> name = new ArrayList<String>();
					/** 20161026缓存选中的食材的UID */
					ArrayList<String> uid = new ArrayList<String>();
					for (int i = 0; i < selectedList.size(); i++) {
						LocalFoodMaterialLevelThree bean2 = new LocalFoodMaterialLevelThree();
						bean2.setPid(FoodMaterialDAO.saveLocalFoodMaterialLevelOne(bean1));
						bean2.setName(selectedList.get(i));
						bean2.setUid(selectedListID.get(i));
						FoodMaterialDAO.saveLocalFoodMaterialLevelThree(bean2);
						name.add(selectedList.get(i));
						uid.add(selectedListID.get(i));
						Intent intent = new Intent();

						System.out.println("===name===" + name);
						System.out.println("===uid===" + uid);
						setResult(RESULT_OK, intent);
					}
					// Intent intents = new Intent(
					// StandardFoodsMaterialDetailsActivity.this,
					// AddStepActivity.class);
					Intent intents = new Intent();
//					intents.putExtra("mList", name);
					intents.putExtra("mlistMore", name);
					intents.putExtra("mlisetMoreID",uid);
					intents.putExtra("timeNode", timeNode);
					intents.putExtra("startTime", startTime);
					intents.putExtra("endTime", endTime);
					intents.putExtra("step", step);
					intents.putExtra("isEdt", state);
					intents.putExtra("filePath", filePath);
					HHDLog.v("mlistMore="+name.get(0)+"_"+name.size()+"，mlisetMoreID="+uid.get(0)+"_"+uid.size()+"，timeNode="+timeNode+"，startTime="+startTime+"，endTime="+endTime+"，step="+step+"，isEdt="+state+"，filePath="+filePath);
					setResult(RESULT_OK, intents);

					// startActivity(intents);
					// intents.putExtra("position", positions);
					// System.out.println(">>>position?3>>"+positions);

					// CookFoodsMaterialManageActivity cook = new
					// CookFoodsMaterialManageActivity();
					// cook.cookfood.finish();
					// AddStepActivity add = new AddStepActivity();
					// add.instance.finish();
					StandardFoodsMaterialDetailsActivity.this.finish();
					KappUtils.showToast(context, "食材添加成功");
					// CookBookDetailActivity book = new
					// CookBookDetailActivity();
					// book.detail.finish();
					// 清除选中
					clearSelect();
				} else {
					int num = 5 - count;
					KappUtils.showToast(context, "您现在只可以选择" + num + "样食材");
				}
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
		// p = 1;
		if (p < 0) {
			p = 0;
		}
		if (p > mDatas.size() - 3) {
			p = mDatas.size() - 3;
		}
		linearLayoutManager.scrollToPosition(ChildPosition);
		rv_goodfood.setLayoutManager(linearLayoutManager);

		// 设置适配器
		galleryAdapter = new GalleryAdapter(this, mDatas, ChildPosition);
		galleryAdapter
				.setOnItemClickListener(new OnRecyclerViewItemClickListener() {
					@Override
					public void onItemClick(View view, int position, int pos) {
						isButton(pos, view);
						System.out.println("position==" + position + "pos==="
								+ pos+"ChildPosition===="+ChildPosition);
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

	private void isButton(int poss, View tv) {
		if (ChildPosition == poss) {
			tv.setSelected(true);
		} else {
			tv.setSelected(false);
		}
	}

	private void getById(int position) {
		RequestParams params = new RequestParams(
				UrlInterface.URL_FOOD_CATEGORY_ID);
		params.addBodyParameter("categoryId", position + "");
		params.addBodyParameter("standardStatus", 1 + "");
		x.http().post(params, new CommonCallback<String>() {
			@Override
			public void onSuccess(String result) {
				//Log.i(TAG, "getById-->onSuccess"+result);
				HHDLog.v("getById-->onSuccess"+result);
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
								public void onCheckChange(String id, String id1,
										boolean isChecked) {

									if (isChecked) {
										selectedList.add(id);
										selectedListID.add(id1);
										HHDLog.v("选择-》名称："+selectedList.get(0)+"_UID："+selectedListID.get(0));
										//Log.i(TAG, "选择 "+selectedList.get(0));
										//Log.i(TAG, "选择 "+selectedListID.get(0));
									} else {
										selectedList.remove(id);
										selectedListID.remove(id1);
									}
									tv_selectNum.setText("" + selectedList.size());
								}
							});
					gvFoodAdapter.setClickable(true);
					gv_food.setAdapter(gvFoodAdapter);
					gvFoodAdapter.notifyDataSetChanged();

					HHDLog.v(foods.get(0).getUid());
					//Log.i(TAG, foods.get(0).getUid());

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
