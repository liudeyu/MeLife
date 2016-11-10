package com.gzmelife.app.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.x;
import org.xutils.common.Callback.CommonCallback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gzmelife.app.R;
import com.gzmelife.app.UrlInterface;
import com.gzmelife.app.adapter.MyFoodMaterialAdapter;
import com.gzmelife.app.adapter.StandardFoodMaterialAdapter;
import com.gzmelife.app.bean.CategoryFirstBean;
import com.gzmelife.app.bean.LocalFoodMaterialLevelOne;
import com.gzmelife.app.bean.LocalFoodMaterialLevelThree;
import com.gzmelife.app.dao.FoodMaterialDAO;
import com.gzmelife.app.tools.KappUtils;
import com.gzmelife.app.views.TipConfirmView;

/**
 * 食材管理
 * 
 * @author chenxiaoyan
 *
 */
@SuppressLint("InflateParams")
@ContentView(R.layout.activity_food_material_manage)
public class FoodMaterialManageActivity extends BaseActivity implements
		OnClickListener {
	@ViewInject(R.id.btn_addFoodMaterial)
	Button btn_addFoodMaterial;
	@ViewInject(R.id.btn_manageFoodMaterial)
	Button btn_manageFoodMaterial;
	@ViewInject(R.id.btn_titleRight)
	Button btn_titleRight;

	@ViewInject(R.id.layout_noData)
	View layout_noData;
	@ViewInject(R.id.layout_myFoodMaterialManage)
	View layout_myFoodMaterialManage;
	@ViewInject(R.id.layout_myFoodMaterialBottom)
	View layout_myFoodMaterialBottom;
	@ViewInject(R.id.layout_clearSelect)
	View layout_clearSelect;

	@ViewInject(R.id.iv_delete)
	ImageView iv_delete;
	@ViewInject(R.id.tv_title_left)
	TextView tv_title_left;
	@ViewInject(R.id.btn_search)
	Button btn_search;

	private TextView tv_title;
	private RadioButton rb_myFoodMaterial;
	private RadioButton rb_standardFoodMaterial;

	private View view_myFoodMaterial;
	private View view_standardFoodMaterial;
	private View layout_myFoodMaterial;
	private View layout_standardFoodMaterial;

	private ExpandableListView expandLv_myFoodMaterial;
	private ExpandableListView expandLv_standardFoodMaterial;

	private List<LocalFoodMaterialLevelOne> myFoodMaterialCategoryList = new ArrayList<LocalFoodMaterialLevelOne>();
	private List<LocalFoodMaterialLevelOne> myFoodMaterialCategoryListTemp = new ArrayList<LocalFoodMaterialLevelOne>();
	private Map<String, List<LocalFoodMaterialLevelThree>> myFoodMaterialMap = new HashMap<String, List<LocalFoodMaterialLevelThree>>();
	private MyFoodMaterialAdapter myAdapter;

	private List<CategoryFirstBean> categoryFirstBeanList = new ArrayList<CategoryFirstBean>();
	private StandardFoodMaterialAdapter standardAdapter;

	private Context context;

	private String searchContent;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		context = this;
		initView();
	}

	@Override
	protected void onResume() {
		super.onResume();
		// 我的食材库
		myFoodMaterialCategoryListTemp = FoodMaterialDAO.getAllCategory();
		myFoodMaterialCategoryList.clear();
		myFoodMaterialMap = new HashMap<String, List<LocalFoodMaterialLevelThree>>();
		if (myFoodMaterialCategoryListTemp == null
				|| myFoodMaterialCategoryListTemp.size() == 0) {
			layout_noData.setVisibility(View.VISIBLE);
			expandLv_myFoodMaterial.setVisibility(View.GONE);
		} else {
			layout_noData.setVisibility(View.GONE);
			expandLv_myFoodMaterial.setVisibility(View.VISIBLE);

			for (int i = 0; i < myFoodMaterialCategoryListTemp.size(); i++) {
				List<LocalFoodMaterialLevelThree> list = FoodMaterialDAO
						.getAllFoodMaterialByCategoryId(myFoodMaterialCategoryListTemp
								.get(i).getId());
				if (list.size() > 0) {
					myFoodMaterialMap.put(myFoodMaterialCategoryListTemp.get(i)
							.getId() + "", list);
					myFoodMaterialCategoryList
							.add(myFoodMaterialCategoryListTemp.get(i));
				}
			}

			if (myFoodMaterialMap.size() == 0) {
				layout_noData.setVisibility(View.VISIBLE);
				expandLv_myFoodMaterial.setVisibility(View.GONE);
			} else {
				if (myAdapter == null) {
					myAdapter = new MyFoodMaterialAdapter(context,
							myFoodMaterialCategoryList, myFoodMaterialMap, 0);
					expandLv_myFoodMaterial.setAdapter(myAdapter);
					expandLv_myFoodMaterial
							.setOnGroupClickListener(new OnGroupClickListener() {
								@Override
								public boolean onGroupClick(
										ExpandableListView parent, View v,
										int groupPosition, long id) {
									return true;
								}
							});
				} else {
					myAdapter.setClickable(false);
					myAdapter.setData(myFoodMaterialCategoryList,
							myFoodMaterialMap);
				}
				myAdapter.initSelectedIds();
				for (int i = 0; i < myAdapter.getGroupCount(); i++) {
					expandLv_myFoodMaterial.expandGroup(i);
				}
			}
		}
	}

	private void initView() {
		tv_title = (TextView) findViewById(R.id.tv_title);
		rb_myFoodMaterial = (RadioButton) findViewById(R.id.rb_myFoodMaterial);
		rb_standardFoodMaterial = (RadioButton) findViewById(R.id.rb_standardFoodMaterial);
		layout_myFoodMaterial = findViewById(R.id.layout_myFoodMaterial);
		layout_standardFoodMaterial = findViewById(R.id.layout_standardFoodMaterial);
		view_myFoodMaterial = findViewById(R.id.view_myFoodMaterial);
		view_standardFoodMaterial = findViewById(R.id.view_standardFoodMaterial);

		btn_search.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(FoodMaterialManageActivity.this,
						FoodMangerSearchsActivity.class);
				startActivity(intent);
			}
		});

		tv_title.setText("食材库管理");
		tv_title_left.setVisibility(View.VISIBLE);
		tv_title_left.setText("个人中心");
		expandLv_myFoodMaterial = (ExpandableListView) findViewById(R.id.expandLv_myFoodMaterial);
		expandLv_standardFoodMaterial = (ExpandableListView) findViewById(R.id.expandLv_standardFoodMaterial);

		expandLv_myFoodMaterial.setGroupIndicator(null);
		expandLv_standardFoodMaterial.setGroupIndicator(null);

		rb_myFoodMaterial
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						if (isChecked) {
							rb_myFoodMaterial.setTextColor(Color
									.parseColor("#ff5b80"));
							view_myFoodMaterial.setVisibility(View.VISIBLE);
							layout_myFoodMaterial.setVisibility(View.VISIBLE);
						} else {
							rb_myFoodMaterial.setTextColor(Color
									.parseColor("#3f3f3f"));
							view_myFoodMaterial.setVisibility(View.INVISIBLE);
							layout_myFoodMaterial.setVisibility(View.GONE);
						}
					}
				});
		rb_standardFoodMaterial
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						if (isChecked) {
							cancelManage();
							rb_standardFoodMaterial.setTextColor(Color
									.parseColor("#ff5b80"));
							view_standardFoodMaterial
									.setVisibility(View.VISIBLE);
							layout_standardFoodMaterial
									.setVisibility(View.VISIBLE);
							showStandardLib();
						} else {
							rb_standardFoodMaterial.setTextColor(Color
									.parseColor("#3f3f3f"));
							view_standardFoodMaterial
									.setVisibility(View.INVISIBLE);
							layout_standardFoodMaterial
									.setVisibility(View.GONE);
						}
					}
				});

		btn_titleRight = (Button) findViewById(R.id.btn_titleRight);
		btn_titleRight.setText("取消");

		btn_addFoodMaterial.setOnClickListener(this);
		btn_manageFoodMaterial.setOnClickListener(this);
		btn_titleRight.setOnClickListener(this);
		layout_clearSelect.setOnClickListener(this);
		iv_delete.setOnClickListener(this);
	}

	private void showStandardLib() {
		// 标准食材库
		if (standardAdapter == null) {
			RequestParams params = new RequestParams(
					UrlInterface.URL_FOOD_MATERIAL_LIB);
			showDlg();
			x.http().post(params, new CommonCallback<String>() {
				@Override
				public void onSuccess(String result) {
					closeDlg();
					Gson gson = new Gson();
					JSONObject obj;
					try {
						obj = new JSONObject(result);
						categoryFirstBeanList = gson.fromJson(
								obj.getJSONObject("data")
										.getJSONArray("towFoodStoreCategorys")
										.toString(),
								new TypeToken<List<CategoryFirstBean>>() {
								}.getType());

						standardAdapter = new StandardFoodMaterialAdapter(
								context, categoryFirstBeanList,
								new StandardFoodMaterialAdapter.OnReceiver() {
									@Override
									public void onClick(int groupPosition,
											int position, int ChildPosition) {

										if (position != -1) {
											Intent intent = new Intent(
													context,
													StandardFoodMaterialDetailsActivity.class);
											intent.putExtra("category",
													categoryFirstBeanList
															.get(groupPosition));
											intent.putExtra("position",
													position);
											intent.putExtra("ChildPosition",
													ChildPosition);
											startActivity(intent);
										}
									}
								});
						expandLv_standardFoodMaterial
								.setAdapter(standardAdapter);
						for (int i = 0; i < standardAdapter.getGroupCount(); i++) {
							expandLv_standardFoodMaterial.expandGroup(i);
						}
						expandLv_standardFoodMaterial
								.setOnGroupClickListener(new OnGroupClickListener() {
									@Override
									public boolean onGroupClick(
											ExpandableListView parent, View v,
											int groupPosition, long id) {
										return true;
									}
								});
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}

				@Override
				public void onError(Throwable ex, boolean isOnCallback) {
					closeDlg();
				}

				@Override
				public void onCancelled(CancelledException cex) {
					closeDlg();
				}

				@Override
				public void onFinished() {
					closeDlg();
				}
			});
		}
	}

	public void back(View v) {
		if (layout_myFoodMaterialManage.getVisibility() == View.VISIBLE) {
			cancelManage();
		} else {
			this.finish();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_addFoodMaterial:
			startActivity(new Intent(context, AddNewFoodMaterialActivity.class));
			break;
		case R.id.btn_manageFoodMaterial:
			manageMyFoodMaterial();
			break;
		case R.id.btn_titleRight:
			cancelManage();
			break;
		case R.id.layout_clearSelect:
			if (myAdapter.getSelectedIds().size() == 0) {
				KappUtils.showToast(context, "您未选中任何食材");
			} else {
				TipConfirmView.showConfirmDialog(context, "是否确认清除选择？",
						new OnClickListener() {
							@Override
							public void onClick(View v) {
								TipConfirmView.dismiss();
								onResume();
								manageMyFoodMaterial();
							}
						});
			}
			break;
		case R.id.iv_delete:
			final List<String> idList = myAdapter.getSelectedIds();
			if (idList.size() == 0) {
				KappUtils.showToast(context, "您未选中任何食材");
			} else {
				TipConfirmView.showConfirmDialog(context, "是否确认删除所选择的食材？",
						new OnClickListener() {
							@Override
							public void onClick(View v) {
								TipConfirmView.dismiss();
								// 数据库中删除
								FoodMaterialDAO
										.deleteLocalFoodMaterialLevelThree(idList);
								cancelManage();
								// onResume();
							}
						});
			}
			break;

		}
	}

	private void manageMyFoodMaterial() {
		if (expandLv_myFoodMaterial.getVisibility() == View.VISIBLE) {
			layout_myFoodMaterialBottom.setVisibility(View.GONE);
			layout_myFoodMaterialManage.setVisibility(View.VISIBLE);
			btn_titleRight.setVisibility(View.VISIBLE);
			myAdapter.setClickable(true);
			myAdapter.notifyDataSetChanged();
		} else {
			KappUtils.showToast(context, "食材库中无食材可管理");
		}
	}

	private void cancelManage() {
		if (layout_myFoodMaterialBottom.getVisibility() == View.GONE) {
			layout_myFoodMaterialBottom.setVisibility(View.VISIBLE);
			layout_myFoodMaterialManage.setVisibility(View.GONE);
			btn_titleRight.setVisibility(View.GONE);
			onResume();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (layout_myFoodMaterialManage.getVisibility() == View.VISIBLE) {
				cancelManage();
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
}