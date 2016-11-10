package com.gzmelife.app.activity;

import java.util.List;

import javax.security.auth.callback.Callback;

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
import com.gzmelife.app.KappAppliction;
import com.gzmelife.app.R;
import com.gzmelife.app.UrlInterface;
import com.gzmelife.app.adapter.CookFoodBeanAdapter;
import com.gzmelife.app.adapter.GvSecondClassifyAdapter;
import com.gzmelife.app.bean.CoonFoodMenuBean;
import com.gzmelife.app.tools.pop_dlg;
import com.gzmelife.app.tools.pop_dlg.dialog;
import com.gzmelife.app.views.MyListView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.RadioGroup.OnCheckedChangeListener;

@ContentView(R.layout.activity_second_classify)
public class SecondClassifyActivity extends BaseActivity {
	@ViewInject(R.id.rv_goodfood)
	GridView gv_food;
	GvSecondClassifyAdapter secondClassifyAdapter;
	@ViewInject(R.id.tv_title)
	TextView tv_title;

	RadioGroup rg;
	// 最新
	RadioButton rb_new;
	// 最热
	RadioButton rb_hot;
	// // 推荐
	// RadioButton rb_recommend;
	private String TAG="SecondClassifyActivity";
	private List<CoonFoodMenuBean> categoryCoonFoodMenuBeanList;
	
	CoonFoodMenuBean bean = null;
	private String categoryId;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		String name = getIntent().getStringExtra("name");
		categoryId = getIntent().getStringExtra("categoryId");
		tv_title.setText(name);
		rg = (RadioGroup) findViewById(R.id.rg);
		rb_new = (RadioButton) findViewById(R.id.rb_new);
		rb_hot = (RadioButton) findViewById(R.id.rb_hot);
		// gv_food.setOnItemClickListener(new OnItemClickListener() {
		//
		// @Override
		// public void onItemClick(AdapterView<?> parent, View view,
		// int position, long id) {
		// // TODO Auto-generated method stub
		//
		// Intent intent = new Intent(SecondClassifyActivity.this,
		// NetCookBookDetailActivity.class);
		// // String number = categoryCoonFoodMenuBeanList.get(position)
		// // .getClickNumber();
		// TextView nums = (TextView) view.findViewById(R.id.tv_num);
		// String number = nums.getText().toString();
		// System.out.println(">>>>>num====" + number);
		// String[] str = number.split("次阅读");
		// int n = Integer.valueOf(str[0]);
		// if (KappAppliction.liLogin == 1) {
		// n++;
		// }
		// nums.setText(String.valueOf(n + "次阅读"));
		// System.out.println(">>>>>n====" + n + "次阅读");
		// intent.putExtra("menuBookId",
		// categoryCoonFoodMenuBeanList.get(position).getId());
		// secondClassifyAdapter.notifyDataSetChanged();
		// startActivity(intent);
		// showCookBook(1);
		// showCookBook(2);
		// }
		// });

		rb_new.setChecked(true);
		showCookBook(1);

		rg.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				switch (checkedId) {
				case R.id.rb_new:
					showCookBook(1);
					break;
				case R.id.rb_hot:
					showCookBook(2);
					break;
				// case R.id.rb_recommend:
				// showCookBook(3);
				// break;
				default:
					break;
				}
			}
		});
	}

	// @Override
	// protected void onActivityResult(int arg0, int arg1, Intent arg2) {
	// // TODO Auto-generated method stub
	// if (arg1 == RESULT_OK) {
	// int num = arg2.getIntExtra("nunber", 0);
	// super.onActivityResult(arg0, arg1, arg2);
	// switch (arg0) {
	// case 12008:
	// showCookBook(1);
	// break;
	//
	// default:
	// break;
	// }
	// }
	// }

	private void showCookBook(int flag) {
		// 美食最新、最热、推荐菜谱
		showDlg();
		RequestParams params = new RequestParams(UrlInterface.URL_FINDMENUBOOK);
		params.addBodyParameter("categoryId", categoryId);
		params.addBodyParameter("order", flag + "");
		params.addBodyParameter("name", "");
		params.addBodyParameter("page", "1");

		x.http().post(params, new CommonCallback<String>() {
			@Override
			public void onSuccess(String result) {
				
				closeDlg();
				Gson gson = new Gson();
				JSONObject obj;
				try {
					Log.i(TAG, "net showCookBook->onSuccess"+result);
					obj = new JSONObject(result);
					categoryCoonFoodMenuBeanList = gson.fromJson(obj
							.getJSONObject("data").getJSONArray("menubooks")
							.toString(),
							new TypeToken<List<CoonFoodMenuBean>>() {
							}.getType());
					if(categoryCoonFoodMenuBeanList.size()==0){
						pop_dlg	mcurPop= new pop_dlg(SecondClassifyActivity.this, new dialog() {

							@Override
							public void onclick(pop_dlg pop, View v) {
								// TODO Auto-generated method stub
								pop.dismiss();
							}

							@Override
							public View initLayout(pop_dlg pop, LayoutInflater flater) {
								// TODO Auto-generated method stub
								View v = flater.inflate(R.layout.dialog_tishi_network, null);
								TextView ok = (TextView) v.findViewById(R.id.ok);
								TextView tv_content = (TextView) v.findViewById(R.id.tv_content);
								tv_content.setText("暂时没有对应的菜谱");
								ok.setOnClickListener(pop);
								return v;
							}
						});
					}
					secondClassifyAdapter = new GvSecondClassifyAdapter(
							SecondClassifyActivity.this,
							categoryCoonFoodMenuBeanList);

					gv_food.setAdapter(secondClassifyAdapter);
					secondClassifyAdapter.notifyDataSetChanged();
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
