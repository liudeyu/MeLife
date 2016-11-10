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
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gzmelife.app.R;
import com.gzmelife.app.UrlInterface;
import com.gzmelife.app.adapter.MyCookFoodMaterialAdapter;
import com.gzmelife.app.adapter.StandardFoodMaterialAdapter;
import com.gzmelife.app.bean.CategoryFirstBean;
import com.gzmelife.app.bean.LocalFoodMaterialLevelOne;
import com.gzmelife.app.bean.LocalFoodMaterialLevelThree;
import com.gzmelife.app.dao.FoodMaterialDAO;
import com.gzmelife.app.tools.KappUtils;
import com.gzmelife.app.tools.MyLogger;
import com.gzmelife.app.hhd.TimeNode;
import com.gzmelife.app.views.TipConfirmView;

/**
 * 界面【我的食材（我的食材库&标准食材库）】
 *
 * 食材管理
 * 
 * @author chenxiaoyan
 *
 */
@SuppressLint("InflateParams")
@ContentView(R.layout.activity_cook_foods_material_manage)
public class CookFoodsMaterialManageActivity extends BaseActivity implements
		OnClickListener {

	MyLogger HHDLog=MyLogger.HHDLog();

	@ViewInject(R.id.btn_addFoodMaterial)
	Button btn_addFoodMaterial;
	@ViewInject(R.id.btn_manageFoodMaterial)
	Button btn_manageFoodMaterial;

	@ViewInject(R.id.layout_noData)
	View layout_noData;
	@ViewInject(R.id.layout_myFoodMaterialManage)
	View layout_myFoodMaterialManage;
	@ViewInject(R.id.layout_myFoodMaterialBottom)
	View layout_myFoodMaterialBottom;
	@ViewInject(R.id.layout_clearSelect)
	View layout_clearSelect;
	@ViewInject(R.id.linear_search)
	LinearLayout linear_search;
	@ViewInject(R.id.linear_searchs)
	LinearLayout linear_searchs;
	@ViewInject(R.id.iv_delete)
	ImageView iv_delete;
	@ViewInject(R.id.tv_title_left)
	TextView tv_title_left;
	@ViewInject(R.id.tv_title)
	TextView tv_title;
	@ViewInject(R.id.btn_search)
	Button serach_food_name;
	@ViewInject(R.id.btn_searchs)
	Button btn_searchs;
	@ViewInject(R.id.iv_titleLeft)
	ImageView iv_titleLeft;

	private String TAG="CookFoodsMaterialManageActivity";
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
	private MyCookFoodMaterialAdapter myAdapter;

	private List<CategoryFirstBean> categoryFirstBeanList = new ArrayList<CategoryFirstBean>();
	private StandardFoodMaterialAdapter standardAdapter;

	private Context context;
	static CookFoodsMaterialManageActivity cookfood;
	private String searchContent;
	int startTime;
	int endTime;
	String step;
	TimeNode timeNode;
	boolean state;
	private int count;
	private int i, positions;
	private ArrayList<String> mlistMore = new ArrayList<String>();
	private ArrayList<String> mlistMoreuid = new ArrayList<String>();
	private String filePath;

	@Override
	public void onCreate(Bundle savedInstanceState) {
	
		super.onCreate(savedInstanceState);
		cookfood = this;
		context = this;
		//Log.i(TAG, "进入");
		initView();
	}

	@Override
	protected void onResume() {
		super.onResume();
		HHDLog.v("界面【我的食材（我的食材库&标准食材库）】");
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
					myAdapter = new MyCookFoodMaterialAdapter(context,
							myFoodMaterialCategoryList, myFoodMaterialMap, 1);
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

		filePath = getIntent().getStringExtra("filePath");
		timeNode = (TimeNode) getIntent().getSerializableExtra("timeNode");
		state = getIntent().getBooleanExtra("isEdt", false);
		startTime = getIntent().getIntExtra("startTime", 0);
		endTime = getIntent().getIntExtra("endTime", 0);
		step = getIntent().getStringExtra("step");
		count = getIntent().getIntExtra("count", 0);
		positions = getIntent().getIntExtra("position", 0);
		System.out.println(">>>position?2>>" + positions);
		rb_myFoodMaterial = (RadioButton) findViewById(R.id.rb_myFoodMaterial);
		rb_standardFoodMaterial = (RadioButton) findViewById(R.id.rb_standardFoodMaterial);
		layout_myFoodMaterial = findViewById(R.id.layout_myFoodMaterial);
		layout_standardFoodMaterial = findViewById(R.id.layout_standardFoodMaterial);
		view_myFoodMaterial = findViewById(R.id.view_myFoodMaterial);
		view_standardFoodMaterial = findViewById(R.id.view_standardFoodMaterial);

		tv_title.setText("我的食材");
		tv_title_left.setVisibility(View.VISIBLE);
		tv_title_left.setText("添加食材");
		expandLv_myFoodMaterial = (ExpandableListView) findViewById(R.id.expandLv_myFoodMaterial);
		expandLv_standardFoodMaterial = (ExpandableListView) findViewById(R.id.expandLv_standardFoodMaterial);

		if (layout_standardFoodMaterial.getVisibility() == View.GONE) {
			serach_food_name.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent(
							CookFoodsMaterialManageActivity.this,
							FoodssMangerSearchActivity.class);
					intent.putExtra("timeNode", timeNode);
					intent.putExtra("startTime", startTime);
					intent.putExtra("endTime", endTime);
					intent.putExtra("step", step);
					intent.putExtra("isEdt", state);
					intent.putExtra("position", positions);
					intent.putExtra("count", count);
					intent.putExtra("filePath", filePath);
					startActivityForResult(intent, 10569);
				}
			});
		} else if (layout_standardFoodMaterial.getVisibility() == View.VISIBLE) {

		}

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
							layout_myFoodMaterialManage
									.setVisibility(View.VISIBLE);
							linear_search.setVisibility(View.VISIBLE);

						} else {
							rb_myFoodMaterial.setTextColor(Color
									.parseColor("#3f3f3f"));
							view_myFoodMaterial.setVisibility(View.INVISIBLE);
							layout_myFoodMaterial.setVisibility(View.GONE);
							// linear_search.setVisibility(View.VISIBLE);
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
							linear_search.setVisibility(View.GONE);
							linear_searchs.setVisibility(View.VISIBLE);
							btn_searchs
									.setOnClickListener(new OnClickListener() {

										@Override
										public void onClick(View v) {
											HHDLog.v("点击了onCheckedChanged-->onClick");
											Intent intent = new Intent(
													CookFoodsMaterialManageActivity.this,
													FoodsMangerSearchActivity.class);
											intent.putExtra("timeNode",
													timeNode);
											intent.putExtra("startTime",
													startTime);
											intent.putExtra("endTime", endTime);
											intent.putExtra("step", step);
											intent.putExtra("isEdt", state);
											intent.putExtra("position",
													positions);
											intent.putExtra("count", count);
											intent.putExtra("filePath",
													filePath);
											startActivityForResult(intent,
													10569);
										}
									});
							showStandardLib();
							// serach_food_name.setVisibility(View.VISIBLE);
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

		btn_addFoodMaterial.setOnClickListener(this);
		btn_manageFoodMaterial.setOnClickListener(this);
		layout_clearSelect.setOnClickListener(this);
		iv_delete.setOnClickListener(this);
	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		// TODO Auto-generated method stub
		super.onActivityResult(arg0, arg1, arg2);
		if (arg1 == RESULT_OK) {
			switch (arg0) {
			case 10569:
				HHDLog.v("进入 onActivityResult+10569");
				mlistMore = arg2.getStringArrayListExtra("mlistMore");
				mlistMoreuid = arg2.getStringArrayListExtra("mlisetMoreID");
				System.out.println(">>mlistMore=======" + mlistMore);
				Intent intent1 = new Intent();
				intent1.putExtra("mlistMore", mlistMore);
				intent1.putExtra("mlisetMoreID", mlistMoreuid);
				setResult(RESULT_OK, intent1);
				// startActivity(intent);
				CookFoodsMaterialManageActivity.this.finish();
				break;
			case 10258:
				mlistMore = arg2.getStringArrayListExtra("mlistMore");
				mlistMoreuid = arg2.getStringArrayListExtra("mlisetMoreID");
				Intent intent = new Intent();
				intent.putExtra("mlistMore", mlistMore);
				intent.putExtra("mlisetMoreID", mlistMoreuid);
				setResult(RESULT_OK, intent);
				// startActivity(intent);
				CookFoodsMaterialManageActivity.this.finish();

				break;
			default:
				break;
			}
		}
	}

	private void showStandardLib() {
		// 标准食材库
		HHDLog.v("showStandardLib-->");
		if (standardAdapter == null) {
			RequestParams params = new RequestParams(
					UrlInterface.URL_FOOD_MATERIAL_LIB);
			showDlg();
			x.http().post(params, new CommonCallback<String>() {
				@Override
				public void onSuccess(String result) {
					HHDLog.v("修改食材UI showStandardLib onSuccess--》"+result);
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
											HHDLog.v("跳转 StandardFoodsMaterialDetailsActivity onSuccess--》");
											Intent intent = new Intent(
													context,
													StandardFoodsMaterialDetailsActivity.class);
											intent.putExtra("category",
													categoryFirstBeanList
															.get(groupPosition));
											intent.putExtra("position",
													position);
											intent.putExtra("ChildPosition",
													ChildPosition);
											intent.putExtra("timeNode",
													timeNode);
											intent.putExtra("startTime",
													startTime);
											intent.putExtra("endTime", endTime);
											intent.putExtra("step", step);
											intent.putExtra("isEdt", state);
											intent.putExtra("filePath",
													filePath);
											intent.putExtra("positions",
													positions);
											intent.putExtra("count", count);
											startActivityForResult(intent,
													10258);

											// CookFoodsMaterialManageActivity.this
											// .finish();
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
		if (layout_myFoodMaterialManage.getVisibility() == View.GONE) {
			cancelManage();
		} else {
			this.finish();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		// case R.id.iv_titleLeft:
		// CookFoodMaterialManageActivity.this.finish();
		// break;
		case R.id.btn_addFoodMaterial:

			if (myFoodMaterialMap.size() == 0){
				KappUtils.showToast(context, "这里没有食材选择，请到其他渠道选择");
				return;
			}
			final ArrayList<String> listId = (ArrayList<String>) myAdapter
					.getSelectedIds();
			final ArrayList<String> listUid = (ArrayList<String>) myAdapter
					.getSelectedIdsUid();
			i = count + listId.size();
			if (i <= 5) {
				if (listId.size() == 0) {
					KappUtils.showToast(context, "您未选中任何食材");
				} else {
					System.out.println(">>>>>>>mlistMore======" + mlistMore);
					// Intent intent = new Intent(
					// CookFoodsMaterialManageActivity.this,
					// AddStepActivity.class);
					Intent intent = new Intent();
					intent.putExtra("mlistMore", listId);
					intent.putExtra("mlisetMoreID", listUid);
					intent.putExtra("filePath", filePath);
					intent.putExtra("foodnames", timeNode.FoodNames);
					intent.putExtra("timeNode", timeNode);
					intent.putExtra("startTime", startTime);
					intent.putExtra("endTime", endTime);
					intent.putExtra("step", step);
					intent.putExtra("isEdt", state);
					intent.putExtra("count", count);
					setResult(RESULT_OK, intent);
					// startActivity(intent);
					CookFoodsMaterialManageActivity.this.finish();
					// AddStepActivity add = new AddStepActivity();
					// add.instance.finish();
				}
			} else {
				int num = 5 - count;
				KappUtils.showToast(context, "您现在只可以选择" + num + "样食材");
			}

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
			layout_myFoodMaterialBottom.setVisibility(View.VISIBLE);
			layout_myFoodMaterialManage.setVisibility(View.VISIBLE);
			myAdapter.setClickable(true);
			myAdapter.notifyDataSetChanged();
		} else {
			KappUtils.showToast(context, "食材库中无食材可管理");
		}
	}

	private void cancelManage() {
		if (layout_myFoodMaterialBottom.getVisibility() == View.VISIBLE) {
			layout_myFoodMaterialBottom.setVisibility(View.VISIBLE);
			layout_myFoodMaterialManage.setVisibility(View.VISIBLE);
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