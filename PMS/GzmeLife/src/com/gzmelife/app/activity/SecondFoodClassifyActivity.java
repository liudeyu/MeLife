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
import com.gzmelife.app.adapter.CookFoodBeanAdapter;
import com.gzmelife.app.adapter.GvSecondClassifyAdapter;
import com.gzmelife.app.bean.CoonFoodMenuBean;
import com.gzmelife.app.tools.pop_dlg;
import com.gzmelife.app.tools.pop_dlg.dialog;
import com.gzmelife.app.views.MyListView;

import android.content.Intent;
import android.os.Bundle;
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

@ContentView(R.layout.activity_second_food_classify)
public class SecondFoodClassifyActivity extends BaseActivity {
	@ViewInject(R.id.rv_goodfood)
	GridView gv_food;
	GvSecondClassifyAdapter secondClassifyAdapter;
	@ViewInject(R.id.tv_title)
	TextView tv_title;

	private List<CoonFoodMenuBean> categoryCoonFoodMenuBeanList;
	private String TAG="SecondFoodClassifyActivity";
	private String categoryId;

	@Override
	protected void onCreate(Bundle arg0) {
		
		super.onCreate(arg0);
		String name = getIntent().getStringExtra("name");
		categoryId = getIntent().getStringExtra("categoryId");
		tv_title.setText(name);
		Log.i(TAG, "onCreate  name:"+name+"categoryId:"+categoryId);
		gv_food.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Log.i(TAG, "gv_food.setOnItemClickListener->onItemClick");
				Intent intent = new Intent(SecondFoodClassifyActivity.this,
						NetCookBookDetailActivity.class);
				Log.i(TAG, "跳转NetCookBookDetailActivity");
				String number = categoryCoonFoodMenuBeanList.get(position)
						.getClickNumber();
				int num = Integer.valueOf(number);
				num++;
				intent.putExtra("number", num);
				intent.putExtra("menuBookId",
						categoryCoonFoodMenuBeanList.get(position).getId());
				secondClassifyAdapter.notifyDataSetChanged();
				startActivityForResult(intent, 12008);

			}
		});

		showCookBook();
	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		// TODO Auto-generated method stub
		if (arg1 == RESULT_OK) {
			int num = arg2.getIntExtra("nunber", 0);
			super.onActivityResult(arg0, arg1, arg2);
			switch (arg0) {
			case 12008:
				showCookBook();
				break;

			default:
				break;
			}
		}
	}

	private void showCookBook() {
		// 美食最新、最热、推荐菜谱
		showDlg();
		RequestParams params = new RequestParams(UrlInterface.URL_FINDMENUBOOKBYFOODSTORE);
		params.addBodyParameter("foodStoreId", categoryId);

		x.http().post(params, new CommonCallback<String>() {
			@Override
			public void onSuccess(String result) {
				Log.i(TAG, "net showCookBook->onSuccess"+result);
				closeDlg();
				Gson gson = new Gson();
				JSONObject obj;
				try {
					obj = new JSONObject(result);
					categoryCoonFoodMenuBeanList = gson.fromJson(obj
							.getJSONObject("data").getJSONArray("menubooks")
							.toString(),
							new TypeToken<List<CoonFoodMenuBean>>() {
							}.getType());
					
					if(categoryCoonFoodMenuBeanList.size()==0){
						pop_dlg dlg=new pop_dlg(SecondFoodClassifyActivity.this, new dialog() {
							
							@Override
							public void onclick(pop_dlg pop, View v) {
								// TODO Auto-generated method stub
								SecondFoodClassifyActivity.this.finish();
								pop.dismiss();
							}
							
							@Override
							public View initLayout(pop_dlg pop, LayoutInflater flater) {
								// TODO Auto-generated method stub
								View v=flater.inflate(R.layout.dialog_tishi1, null);
								TextView ok=(TextView) v.findViewById(R.id.ok);
								ok.setOnClickListener(pop);
								return v;
							}
						});
					}
					secondClassifyAdapter = new GvSecondClassifyAdapter(
							SecondFoodClassifyActivity.this,
							categoryCoonFoodMenuBeanList);

					gv_food.setAdapter(secondClassifyAdapter);

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
