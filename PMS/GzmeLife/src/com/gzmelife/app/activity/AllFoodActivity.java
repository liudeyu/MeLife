package com.gzmelife.app.activity;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.x;
import org.xutils.common.Callback.CancelledException;
import org.xutils.common.Callback.CommonCallback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gzmelife.app.R;
import com.gzmelife.app.UrlInterface;
import com.gzmelife.app.adapter.LvAllFoodClassAdapter;
import com.gzmelife.app.adapter.LvAllFoodClassAdapter.OnItemClickListener;
import com.gzmelife.app.adapter.StandardFoodMaterialAdapter;
import com.gzmelife.app.bean.CategoryFirstBean;
import com.gzmelife.app.bean.FindTowMenuCategoryFirstBean;
import com.gzmelife.app.tools.KappUtils;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ExpandableListView.OnGroupClickListener;

/**
 * 菜谱分类
 * 
 * @author chenxiaoyan
 *
 */
@ContentView(R.layout.activity_allfood)
public class AllFoodActivity extends BaseActivity {
	@ViewInject(R.id.tv_title)
	TextView tv_title;
	@ViewInject(R.id.lv)
	ListView lv;
	@ViewInject(R.id.tv_title_left)
	TextView tv_title_left;
	String TAG = "AllFoodActivity";
	String result;
	private List<FindTowMenuCategoryFirstBean> categoryFirstBeanList;

	LvAllFoodClassAdapter lvAllFoodClassAdapter;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		tv_title.setText("菜谱分类");
		tv_title_left.setVisibility(View.VISIBLE);
		tv_title_left.setText("美食");
		showStandardLib();
		/**
		 * 自定义的监听，为了解决listview用了viewholder引起的监听错乱
		 */

	}

	private void showStandardLib() {
		// 标准食材库
		RequestParams params = new RequestParams(
				UrlInterface.URL_FINDTWOMENUCATEGORY);
		showDlg();
		x.http().post(params, new CommonCallback<String>() {
			@Override
			public void onSuccess(String result) {

				closeDlg();
				Gson gson = new Gson();
				JSONObject obj;
				try {
					AllFoodActivity.this.result = result;
					obj = new JSONObject(result);
					categoryFirstBeanList = gson
							.fromJson(
									obj.getJSONObject("data")
											.getJSONArray("menucategorys")
											.toString(),
									new TypeToken<List<FindTowMenuCategoryFirstBean>>() {
									}.getType());
					lvAllFoodClassAdapter = new LvAllFoodClassAdapter(
							AllFoodActivity.this, categoryFirstBeanList);
					lv.setAdapter(lvAllFoodClassAdapter);

					lvAllFoodClassAdapter
							.setOnItemsClickListener(new OnItemClickListener() {
								@Override
								public void onItemClick(int position) {
									GridView gv_item = (GridView) lv
											.getChildAt(position).findViewById(
													R.id.gv_item);
									ImageView iv_arrow = (ImageView) lv
											.getChildAt(position).findViewById(
													R.id.iv_arrow);
									if (categoryFirstBeanList.size() == 0) {
										gv_item.setVisibility(View.GONE);
										iv_arrow.setSelected(false);
										KappUtils.showToast(context, "该分类没有分类");
									} else if (categoryFirstBeanList.size() != 0) {
										if (gv_item.getVisibility() == View.VISIBLE) {
											gv_item.setVisibility(View.GONE);
											iv_arrow.setSelected(false);
										} else {
											gv_item.setVisibility(View.VISIBLE);
											iv_arrow.setSelected(true);
										}
									}

								}
							});
				} catch (JSONException e) {
					e.printStackTrace();
				}
				System.out.println("result>>>>>>>==="
						+ AllFoodActivity.this.result);
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
