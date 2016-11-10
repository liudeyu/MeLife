package com.gzmelife.app.hhd;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import com.gzmelife.app.R;
import com.gzmelife.app.activity.BaseActivity;
import com.gzmelife.app.activity.CookBookDetailActivity;
import com.gzmelife.app.activity.CookFoodsMaterialManageActivity;
import com.gzmelife.app.adapter.FoodAdapter;
import com.gzmelife.app.bean.LocalFoodMaterialLevelThree;
import com.gzmelife.app.tools.KappUtils;
import com.gzmelife.app.tools.MyLogger;
import com.gzmelife.app.views.ListViewForScrollView;
import com.gzmelife.app.views.TipConfirmView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

@ContentView(R.layout.activity_add_step)

/**
 * 界面【编辑步骤&添加步骤】
 */
public class AddStepActivity extends BaseActivity implements OnClickListener {

	MyLogger HHDLog = MyLogger.HHDLog();

	@ViewInject(R.id.lv_food)
	ListViewForScrollView lv_food;
	@ViewInject(R.id.sb_time)
	SeekBar sb_time;
	@ViewInject(R.id.edt_describe)
	EditText edt_describe;
	@ViewInject(R.id.tv_startTime)
	TextView tv_startTime;
	@ViewInject(R.id.tv_middleTime)
	TextView tv_middleTime;
	@ViewInject(R.id.tv_nowTime)
	TextView tv_nowTime;
	@ViewInject(R.id.tv_endTime)
	TextView tv_endTime;
	@ViewInject(R.id.btn_titleRight)
	Button btn_titleRight;
	@ViewInject(R.id.tv_title)
	TextView tv_title;
	@ViewInject(R.id.tv_title_left)
	TextView tv_title_left;
	ArrayList<LocalFoodMaterialLevelThree> LocalFoodMaterialLevelThrees = new ArrayList<LocalFoodMaterialLevelThree>();
	LocalFoodMaterialLevelThree bean = new LocalFoodMaterialLevelThree();
	FoodAdapter foodAdapter;
	@ViewInject(R.id.btn_edtfood)
	Button btn_edtfood;
	int startTime;
	int endTime;
	String step;
	TimeNode timeNode;
	String TAG = "AddStepActivity";
	/** 标记状态（编辑步骤、添加步骤）：true=编辑步骤 */
	boolean state;
	private ArrayList<String> selectedList = new ArrayList<String>();
	private ArrayList<String> mlist = new ArrayList<String>();
	private ArrayList<String> mlistMore = new ArrayList<String>();
	private ArrayList<String> mlistMoreUID = new ArrayList<String>();
	List<TimeNode> listTimeNode = new ArrayList<TimeNode>();
	int timeS;
	private int position1;
	private String name;
	public static AddStepActivity instance = null;
	private String filePath;
	private int size;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		instance = this;
		mlist = getIntent().getStringArrayListExtra("mList");
		state = getIntent().getBooleanExtra("isEdt", false);
		foodAdapter = new FoodAdapter(this, LocalFoodMaterialLevelThrees);
		lv_food.setAdapter(foodAdapter);
		// for (int i = 0; i < lv_food.getChildCount(); i++) {
		// EditText et_name = (EditText) lv_food.getChildAt(i).findViewById(
		// R.id.et_name);
		// timeNode.FoodWgts[i] = Integer
		// .valueOf(et_name.getText().toString());
		// foodAdapter.notifyDataSetChanged();
		// }
		lv_food.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
				TipConfirmView.showConfirmDialog(AddStepActivity.this, "是否确认删除?", new OnClickListener() {
					@Override
					public void onClick(View v) {
						TipConfirmView.dismiss();
						LocalFoodMaterialLevelThrees.remove(position);
						KappUtils.showToast(AddStepActivity.this, "食材删除成功");
						foodAdapter.notifyDataSetInvalidated();
					}
				});
				return false;
			}

		});
		btn_titleRight.setVisibility(View.VISIBLE);
		btn_titleRight.setOnClickListener(this);
		tv_title_left.setVisibility(View.VISIBLE);

		if (state == true) {
			if (getIntent().getStringExtra("filePath") != null) {
				filePath = getIntent().getStringExtra("filePath");
			}

			startTime = getIntent().getIntExtra("startTime", 0);
			endTime = getIntent().getIntExtra("endTime", 0);
			step = getIntent().getStringExtra("edt_describe");
			position1 = getIntent().getIntExtra("position", 0);
			tv_title_left.setText("编辑菜谱");
			edt_describe.setText(step);
			tv_startTime.setText(formatTime(startTime));
			timeNode = (TimeNode) getIntent().getSerializableExtra("timeNode");
			tv_endTime.setText(formatTime(endTime));
			tv_middleTime.setText(formatTime(timeNode.times));
			sb_time.setMax(endTime - startTime);

			// LocalFoodMaterialLevelThrees.add(bean.setName(name));
			sb_time.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
				@Override
				public void onStopTrackingTouch(SeekBar seekBar) {
					//
				}
				@Override
				public void onStartTrackingTouch(SeekBar seekBar) {
					//
				}
				@Override
				public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
					tv_nowTime.setText(formatTime(startTime + progress));
				}
			});
			sb_time.setProgress(timeNode.times - startTime);
			LocalFoodMaterialLevelThrees.clear();
			for (int i = 0; i < timeNode.FoodNames.length; i++) {
				if (timeNode.FoodNames[i] != null && !"".equals(timeNode.FoodNames[i])) {
					LocalFoodMaterialLevelThree LocalFoodMaterialLevelThree = new LocalFoodMaterialLevelThree();
					LocalFoodMaterialLevelThree.setName(timeNode.FoodNames[i]);
					LocalFoodMaterialLevelThree.setWeight(timeNode.FoodWgts[i]);
					LocalFoodMaterialLevelThree.setUid(String.valueOf(timeNode.foodIDs[i]));
					LocalFoodMaterialLevelThrees.add(LocalFoodMaterialLevelThree);

					//String n=timeNode.FoodNames[i];//20161108
					//int w=timeNode.FoodWgts[i];
					//int id=timeNode.foodIDs[i];
					//HHDLog.v("食材名称="+n+"，食材重量="+w+"，食材ID="+id);
				}
			}
			try {
				if (mlist.size() != 0 || mlist != null) {
					for (int j = 0; j < mlist.size(); j++) {
						LocalFoodMaterialLevelThree localFoodMaterialLevelThree = new LocalFoodMaterialLevelThree();
						localFoodMaterialLevelThree.setName(mlist.get(j));
						LocalFoodMaterialLevelThrees.add(localFoodMaterialLevelThree);
						// foodAdapter.notifyDataSetChanged();
					}
				}
			} catch (Exception e) {
				//
			}

			foodAdapter.notifyDataSetChanged();
			tv_title.setText("编辑步骤");

		} else if (state == false) {
			if (getIntent().getStringExtra("filePath") != null) {
				filePath = getIntent().getStringExtra("filePath");
			}
			tv_title_left.setText("添加菜谱");
			// sb_time.setMax(300);
			startTime = getIntent().getIntExtra("startTime", 0);
			endTime = getIntent().getIntExtra("endTime", 0);
			// tv_startTime.setText(formatTime(startTime));
			// name = getIntent().getStringExtra("name");
			// LocalFoodMaterialLevelThrees.add(bean.setName(name));
			position1 = getIntent().getIntExtra("position", 0);
			timeNode = (TimeNode) getIntent().getSerializableExtra("timeNode");
			timeS = timeNode.times;
			tv_startTime.setText(formatTime(timeS));
			tv_endTime.setText(formatTime(endTime));

			// LocalFoodMaterialLevelThrees.add(bean.setName(name));
			// tv_middleTime.setText(formatTime(timeNode.times));
			tv_middleTime.setVisibility(View.GONE);
			sb_time.setMax(endTime - timeNode.times);
			tv_nowTime.setText(formatTime(timeNode.times));
			try {
				if (mlist.size() != 0 || mlist != null) {
					for (int i = 0; i < mlist.size(); i++) {
						LocalFoodMaterialLevelThree bean = new LocalFoodMaterialLevelThree();
						LocalFoodMaterialLevelThrees.add(bean.setName(mlist.get(i)));
						foodAdapter.notifyDataSetChanged();
					}
				}
			} catch (Exception e) {
				//
			}
			sb_time.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
				@Override
				public void onStopTrackingTouch(SeekBar seekBar) {
				}

				@Override
				public void onStartTrackingTouch(SeekBar seekBar) {
				}

				@Override
				public void onProgressChanged(SeekBar seekBar, int progress,
						boolean fromUser) {
					tv_nowTime.setText(formatTime(timeNode.times + progress));
				}
			});

			foodAdapter.notifyDataSetChanged();
			tv_title.setText("添加步骤");
			// sb_time.setProgress(timeNode.times);
		}
		btn_edtfood.setOnClickListener(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		HHDLog.v("界面【编辑步骤&添加步骤】");
	}

	private String formatTime(int time) {
		SimpleDateFormat format = new SimpleDateFormat("mm’ss”");
		return format.format(new Date(time * 1000));
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_titleRight:

			if (state == true) {
				// 编辑状态
				if (timeNode != null) {
					if (!TextUtils.isEmpty(edt_describe.getText()) && edt_describe!=null){//20161011修复描述不能清零
						timeNode.Tips = edt_describe.getText().toString();
					} else {
						timeNode.Tips = "";
					}

					// LocalFoodMaterialLevelThree nameBean =
					// bean.setName(name);
					// LocalFoodMaterialLevelThrees.add(nameBean);
					timeNode.foodIDs=new int[LocalFoodMaterialLevelThrees
												.size()];
					timeNode.FoodNames = new String[LocalFoodMaterialLevelThrees
							.size()];
					timeNode.FoodWgts = new int[LocalFoodMaterialLevelThrees
							.size()];
					for (int i = 0; i < lv_food.getChildCount(); i++) {
						TextView textView = (TextView) lv_food.getChildAt(i)
								.findViewById(R.id.tv_name);
						final EditText et_name = (EditText) lv_food.getChildAt(
								i).findViewById(R.id.et_name);
						if (!TextUtils.isEmpty(textView.getText())) {
							// if(timeNode.FoodNames[i].equals("")||timeNode.FoodNames[i]==null){
							timeNode.FoodNames[i] = textView.getText()
									.toString();
							if (!et_name.getText().toString().equals("")) {
								timeNode.FoodWgts[i] = Integer.parseInt(et_name
										.getText().toString());
							}

						}
						try {
							timeNode.foodIDs[i]=Integer.parseInt(LocalFoodMaterialLevelThrees.get(i).getUid());
						} catch (Exception e) {
							//
						}
					}
					timeNode.times = startTime + sb_time.getProgress();
					// Intent intent = new Intent(this,
					// CookBookDetailActivity.class);
					Intent intent = new Intent();
					intent.putExtra("timeNode", timeNode);
					intent.putExtra("startTime", startTime);
					intent.putExtra("endTime", endTime);
					intent.putExtra("step", step);
					intent.putExtra("position",
							getIntent().getIntExtra("position", 0));
					intent.putExtra("filePath", filePath);
					intent.putExtra("state", state);
					setResult(RESULT_OK, intent);
					// new
					// CookBookDetailActivity().getData(getIntent().getIntExtra("position",
					// 0),timeNode);;
					// startActivity(intent);
					AddStepActivity.this.finish();
					// CookBookDetailActivity book = new
					// CookBookDetailActivity();
					// book.detail.finish();
				} else {
					timeNode = new TimeNode();
					if (!TextUtils.isEmpty(edt_describe.getText()))
						timeNode.Tips = edt_describe.getText().toString();
					// /storage/emulated/0/pms/test111.pms
					timeNode.FoodNames = new String[LocalFoodMaterialLevelThrees
							.size()];
					timeNode.FoodWgts = new int[LocalFoodMaterialLevelThrees
							.size()];
					for (int i = 0; i < lv_food.getChildCount(); i++) {
						TextView textView = (TextView) lv_food.getChildAt(i)
								.findViewById(R.id.tv_name);
						final EditText et_name = (EditText) lv_food.getChildAt(
								i).findViewById(R.id.et_name);
						if (!TextUtils.isEmpty(textView.getText())) {
							timeNode.FoodNames[i] = textView.getText()
									.toString();
							if (!et_name.getText().toString().equals("")) {
								timeNode.FoodWgts[i] = Integer.parseInt(et_name
										.getText().toString());
								et_name.setText("");
							}
						}
					}
					timeNode.times = startTime + sb_time.getProgress();

					Intent intent = new Intent();
					intent.putExtra("timeNode", timeNode);
					intent.putExtra("startTime", startTime);
					intent.putExtra("endTime", endTime);
					intent.putExtra("step", step);
					intent.putExtra("state", state);
					intent.putExtra("position",
							getIntent().getIntExtra("position", 0));
					setResult(RESULT_OK, intent);
					AddStepActivity.this.finish();
					CookBookDetailActivity book = new CookBookDetailActivity();
					book.detail.finish();
				}
			} else {
				timeNode = new TimeNode();
				if (!TextUtils.isEmpty(edt_describe.getText()))
					timeNode.Tips = edt_describe.getText().toString();

				timeNode.FoodNames = new String[LocalFoodMaterialLevelThrees
						.size()];
				timeNode.FoodWgts = new int[LocalFoodMaterialLevelThrees.size()];
				for (int i = 0; i < lv_food.getChildCount(); i++) {
					TextView textView = (TextView) lv_food.getChildAt(i)
							.findViewById(R.id.tv_name);
					final EditText et_name = (EditText) lv_food.getChildAt(i)
							.findViewById(R.id.et_name);
					if (!TextUtils.isEmpty(textView.getText())) {
						timeNode.FoodNames[i] = textView.getText().toString();
						if (!et_name.getText().toString().equals("")) {
							timeNode.FoodWgts[i] = Integer.parseInt(et_name
									.getText().toString());
						}
					}
					String uid= LocalFoodMaterialLevelThrees.get(i).getUid();
					timeNode.foodIDs[i] = Integer.parseInt(uid);
					/*try {
						String uid= LocalFoodMaterialLevelThrees.get(i).getuid();
						for (int j = 0; j < uid.length(); j++) {
							timeNode.foodIDs[j] = Integer.parseInt(uid.substring(j));
						}
					
					} catch (Exception e) {
						//
					}*/
				}
				timeNode.times = timeS + sb_time.getProgress();
				Intent intent = new Intent();
				intent.putExtra("timeNode", timeNode);
				intent.putExtra("startTime", startTime);
				intent.putExtra("endTime", endTime);
				intent.putExtra("step", step);
				intent.putExtra("position",
						getIntent().getIntExtra("position", 0));
				intent.putExtra("filePath", filePath);
				intent.putExtra("state", state);
				setResult(RESULT_OK, intent);
				AddStepActivity.this.finish();
				// CookBookDetailActivity book = new CookBookDetailActivity();
				// book.detail.finish();
			}
			break;
		case R.id.btn_edtfood:
			if (state == true) {
				if (LocalFoodMaterialLevelThrees.size() >= 5) {
					KappUtils.showToast(context, "您选中任何食材的食材不能超过五样，请长按删除食材后重选！");
				} else {
					Intent intent2 = new Intent(AddStepActivity.this, CookFoodsMaterialManageActivity.class);
					int count = lv_food.getChildCount();
					intent2.putExtra("foodnameslength", timeNode.FoodNames.length);
					intent2.putExtra("foodnames", timeNode.FoodNames);
					intent2.putExtra("timeNode", timeNode);
					intent2.putExtra("startTime", startTime);
					intent2.putExtra("endTime", endTime);
					intent2.putExtra("step", step);
					intent2.putExtra("isEdt", state);
					intent2.putExtra("count", count);
					intent2.putExtra("filePath", filePath);
					// Config.bufConnect
					// intent.setClass(AddStepActivity.this,
					// CookFoodsMaterialManageActivity.class);
					startActivityForResult(intent2, 10015);
					// AddStepActivity.this.finish();
				}
			} else {
				int count = lv_food.getChildCount();
				Intent intent2 = new Intent(AddStepActivity.this, CookFoodsMaterialManageActivity.class);
				intent2.putExtra("timeNode", timeNode);
				intent2.putExtra("startTime", startTime);
				intent2.putExtra("endTime", endTime);
				intent2.putExtra("step", step);
				intent2.putExtra("isEdt", state);
				intent2.putExtra("count", count);
				intent2.putExtra("filePath", filePath);
				startActivityForResult(intent2, 10016);
			}

			break;
		default:
			break;
		}

	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		if (arg1 == RESULT_OK) {
			switch (arg0) {
				case 10015://20161104？？？
//				selectedList = arg2.getStringArrayListExtra("mlistMore");
//				if(selectedList!=null){
//
//				}
					mlistMore = arg2.getStringArrayListExtra("mlistMore");
					if (mlistMoreUID!=null){
						mlistMoreUID= arg2.getStringArrayListExtra("mlisetMoreID");
					} else {
						//mlistMoreUID.set(0,SearchsDetailActivity.strUid);
						mlistMoreUID=SearchsDetailActivity.uidList;//20161108
						HHDLog.v(mlistMoreUID.size());
					}

					try {
						if(mlistMore!=null){
							for (int i = 0; i < mlistMore.size(); i++) {
								LocalFoodMaterialLevelThree bean = new LocalFoodMaterialLevelThree();
								bean.setName(mlistMore.get(i));
								if (mlistMoreUID!=null){
									bean.setUid(mlistMoreUID.get(i));
								} else {
									//bean.setuid(SearchsDetailActivity.strUid);
									bean.setUid(SearchsDetailActivity.uidList.get(i));//20161108搜索结果只用到这个模块
									HHDLog.v(SearchsDetailActivity.uidList.get(i));
								}
								LocalFoodMaterialLevelThrees.add(bean);
							}
						}


//					if (selectedList != null && mlistMore == null) {
//						if (selectedList.size() != 0 || selectedList != null) {
//							for (int i = 0; i < selectedList.size(); i++) {
//								Log.i(TAG, "selectedList -->"+selectedList.get(i));
//								LocalFoodMaterialLevelThree bean = new LocalFoodMaterialLevelThree();
////								LocalFoodMaterialLevelThrees.add(bean
////										.setName(selectedList.get(i)));
//								bean
//								.setName(mlistMore.get(i));
//								bean
//								.setuid(mlistMoreUID.get(i));
//								LocalFoodMaterialLevelThrees.add(bean);
//							}
//						}
//					} else if (selectedList == null && mlistMore != null) {
//						if (mlistMore.size() != 0 || mlistMore != null) {
//							for (int i = 0; i < mlistMore.size(); i++) {
//								LocalFoodMaterialLevelThree bean = new LocalFoodMaterialLevelThree();
////								LocalFoodMaterialLevelThrees.add(bean
////										.setName(mlistMore.get(i)));
//								bean
//								.setName(mlistMore.get(i));
//								bean
//								.setuid(mlistMoreUID.get(i));
//								LocalFoodMaterialLevelThrees.add(bean
//								);
//								Log.i(TAG, "mlistMore -->"+mlistMore.get(i));
//							}
//						}
//					}
//					if (selectedList != null && mlistMore != null) {
//						if (selectedList.size() != 0 || selectedList != null) {
//							for (int i = 0; i < selectedList.size(); i++) {
//								LocalFoodMaterialLevelThree bean = new LocalFoodMaterialLevelThree();
////								LocalFoodMaterialLevelThrees.add(bean
////										.setName(selectedList.get(i)));
//								bean
//								.setName(mlistMore.get(i));
//								bean
//								.setuid(mlistMoreUID.get(i));
//								LocalFoodMaterialLevelThrees.add(bean
//								);
//								Log.i(TAG, "selectedList -->"+selectedList.get(i));
//							}
//						}
//						if (mlistMore.size() != 0 || mlistMore != null) {
//							for (int i = 0; i < mlistMore.size(); i++) {
//								LocalFoodMaterialLevelThree bean = new LocalFoodMaterialLevelThree();
////								LocalFoodMaterialLevelThrees.add(bean
////										.setName(mlistMore.get(i)));
//								bean
//								.setName(mlistMore.get(i));
//								bean
//								.setuid(mlistMoreUID.get(i));
//								LocalFoodMaterialLevelThrees.add(bean
//								);
//								Log.i(TAG, "mlistMore -->"+mlistMore.get(i));
//							}
//						}
//					}
					} catch (Exception e) {
						//
					}
					foodAdapter.notifyDataSetChanged();
					break;
				case 10016:
//			}
					mlistMore = arg2.getStringArrayListExtra("mlistMore");
					if (mlistMoreUID!=null){
						mlistMoreUID= arg2.getStringArrayListExtra("mlisetMoreID");
					} else {
						mlistMoreUID=SearchsDetailActivity.uidList;//20161108
						HHDLog.v(mlistMoreUID);
					}

					try {
						if(mlistMore!=null){
							for (int i = 0; i < mlistMore.size(); i++) {
								LocalFoodMaterialLevelThree bean = new LocalFoodMaterialLevelThree();
								bean.setName(mlistMore.get(i));
								if (mlistMoreUID!=null){
									bean.setUid(mlistMoreUID.get(i));
								} else {
									bean.setUid(SearchsDetailActivity.uidList.get(i));//20161108
									HHDLog.v(SearchsDetailActivity.uidList.get(i));
								}
								LocalFoodMaterialLevelThrees.add(bean);
							}
						}

//				selectedList = arg2.getStringArrayListExtra("mList");
//				selectedList = arg2.getStringArrayListExtra("mlistMore");
//				mlistMoreUID = arg2.getStringArrayListExtra("mlisetMoreID");
//				size = LocalFoodMaterialLevelThrees.size();
//				try {
//
//					if (selectedList != null && mlistMore == null) {
//						if (selectedList.size() != 0 || selectedList != null) {
//							for (int i = 0; i < selectedList.size(); i++) {
//								LocalFoodMaterialLevelThree bean = new LocalFoodMaterialLevelThree();
//								bean.setName(selectedList.get(i));
//								bean.setName(mlistMoreUID.get(i));
//								LocalFoodMaterialLevelThrees.add(bean);
//								LocalFoodMaterialLevelThrees.add(bean
//										.setName(selectedList.get(i)));
//							}
//						}
//					} else if (selectedList == null && mlistMore != null) {
//						if (mlistMore.size() != 0 || mlistMore != null) {
//							for (int i = 0; i < mlistMore.size(); i++) {
//								LocalFoodMaterialLevelThree bean = new LocalFoodMaterialLevelThree();
//								LocalFoodMaterialLevelThrees.add(bean
//										.setName(mlistMore.get(i)));
//								bean.setName(selectedList.get(i));
//								bean.setName(mlistMoreUID.get(i));
//								LocalFoodMaterialLevelThrees.add(bean);
//							}
//						}
//					}
//					if (selectedList != null && mlistMore != null) {
//						if (selectedList.size() != 0 || selectedList != null) {
//							for (int i = 0; i < selectedList.size(); i++) {
//								LocalFoodMaterialLevelThree bean = new LocalFoodMaterialLevelThree();
//								LocalFoodMaterialLevelThrees.add(bean
//										.setName(selectedList.get(i)));
//								bean.setName(selectedList.get(i));
//								bean.setName(mlistMoreUID.get(i));
//								LocalFoodMaterialLevelThrees.add(bean);
//							}
//						}
//						if (mlistMore.size() != 0 || mlistMore != null) {
//							for (int i = 0; i < mlistMore.size(); i++) {
//								LocalFoodMaterialLevelThree bean = new LocalFoodMaterialLevelThree();
//								LocalFoodMaterialLevelThrees.add(bean
//										.setName(mlistMore.get(i)));
//								bean.setName(selectedList.get(i));
//								bean.setName(mlistMoreUID.get(i));
//								LocalFoodMaterialLevelThrees.add(bean);
//							}
//						}
//					}
					} catch (Exception e) {
						//
					}
					foodAdapter.notifyDataSetChanged();
					break;

				default:

					break;
			}
		}
		super.onActivityResult(arg0, arg1, arg2);
	}

	public void saveData(boolean sta) {
		if (sta == true) {
			// 编辑状态
			if (timeNode != null) {
				if (!TextUtils.isEmpty(edt_describe.getText()))
					timeNode.Tips = edt_describe.getText().toString();
				// LocalFoodMaterialLevelThree nameBean =
				// bean.setName(name);
				// LocalFoodMaterialLevelThrees.add(nameBean);
				timeNode.FoodNames = new String[LocalFoodMaterialLevelThrees
						.size()];
				timeNode.FoodWgts = new int[LocalFoodMaterialLevelThrees.size()];
				for (int i = 0; i < lv_food.getChildCount(); i++) {
					TextView textView = (TextView) lv_food.getChildAt(i)
							.findViewById(R.id.tv_name);
					final EditText et_name = (EditText) lv_food.getChildAt(i)
							.findViewById(R.id.et_name);
					if (!TextUtils.isEmpty(textView.getText())) {
						// if(timeNode.FoodNames[i].equals("")||timeNode.FoodNames[i]==null){
						timeNode.FoodNames[i] = textView.getText().toString();
						if (!et_name.getText().toString().equals("")) {
							timeNode.FoodWgts[i] = Integer.parseInt(et_name
									.getText().toString());
						}

					}
				}
				timeNode.times = startTime + sb_time.getProgress();

			} else {
				timeNode = new TimeNode();
				if (!TextUtils.isEmpty(edt_describe.getText()))
					timeNode.Tips = edt_describe.getText().toString();
				// /storage/emulated/0/pms/test111.pms
				timeNode.FoodNames = new String[LocalFoodMaterialLevelThrees
						.size()];
				timeNode.FoodWgts = new int[LocalFoodMaterialLevelThrees.size()];
				for (int i = 0; i < lv_food.getChildCount(); i++) {
					TextView textView = (TextView) lv_food.getChildAt(i)
							.findViewById(R.id.tv_name);
					final EditText et_name = (EditText) lv_food.getChildAt(i)
							.findViewById(R.id.et_name);
					if (!TextUtils.isEmpty(textView.getText())) {
						timeNode.FoodNames[i] = textView.getText().toString();
						if (!et_name.getText().toString().equals("")) {
							timeNode.FoodWgts[i] = Integer.parseInt(et_name
									.getText().toString());
						}
					}
				}
				timeNode.times = startTime + sb_time.getProgress();

			}
		} else {
			timeNode = new TimeNode();
			if (!TextUtils.isEmpty(edt_describe.getText()))
				timeNode.Tips = edt_describe.getText().toString();

			timeNode.FoodNames = new String[LocalFoodMaterialLevelThrees.size()];
			timeNode.FoodWgts = new int[LocalFoodMaterialLevelThrees.size()];
			for (int i = 0; i < lv_food.getChildCount(); i++) {
				TextView textView = (TextView) lv_food.getChildAt(i)
						.findViewById(R.id.tv_name);
				final EditText et_name = (EditText) lv_food.getChildAt(i)
						.findViewById(R.id.et_name);
				if (!TextUtils.isEmpty(textView.getText())) {
					timeNode.FoodNames[i] = textView.getText().toString();
					if (!et_name.getText().toString().equals("")) {
						timeNode.FoodWgts[i] = Integer.parseInt(et_name
								.getText().toString());
					}
				}
			}
			timeNode.times = timeS + sb_time.getProgress();

		}
	}
}
