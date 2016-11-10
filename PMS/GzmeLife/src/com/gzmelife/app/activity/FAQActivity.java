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
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gzmelife.app.R;
import com.gzmelife.app.UrlInterface;
import com.gzmelife.app.adapter.FAQAdapter;
import com.gzmelife.app.bean.FAQBean;
import com.gzmelife.app.tools.DateUtil;
import com.gzmelife.app.tools.KappUtils;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

@ContentView(R.layout.activity_audit_information)
public class FAQActivity extends BaseActivity implements android.view.View.OnClickListener {
	@ViewInject(R.id.tv_title)
	TextView tv_title;
	@ViewInject(R.id.tv_title_left)
	TextView tv_title_left;
	@ViewInject(R.id.lv_data)
	PullToRefreshListView lv_data;
	
	private List<FAQBean> list = new ArrayList<FAQBean>();
	private FAQAdapter adapter;
	
//	private int page = 1;
//	private int pageSize = 10;
	
	private Context context;
	
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		
		context = this;
		initView();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void initView() {
		tv_title.setText("常见问题");
		tv_title_left.setVisibility(View.VISIBLE);
		tv_title_left.setText("个人中心");
		adapter = new FAQAdapter(context, list);
		lv_data.setAdapter(adapter);
		
		lv_data.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(context, AboutUsActivity.class);
				intent.putExtra("flag", KappUtils.FLAG_FAQ_DETAIL_1);
				intent.putExtra("id", list.get(position - 1).getId());
				intent.putExtra("name", list.get(position-1).getErrorName());
				startActivity(intent);
			}
		});
		
		lv_data.setMode(Mode.DISABLED);
		lv_data.setOnRefreshListener(new OnRefreshListener2() {
			@Override
			public void onPullDownToRefresh(PullToRefreshBase refreshView) {
//				list.clear();
//				page = 1;
//				getData();
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase refreshView) {
//				page++;
//				getData();
			}
		});
		getData();
	}

	private void getData() {
		showDlg();
		RequestParams params = new RequestParams(UrlInterface.URL_FAQ);
		x.http().post(params, new CommonCallback<String>() {
			@Override
			public void onSuccess(String result) {
				refreshingEnd();
				try {
					Gson gson = new Gson();
					JSONObject obj = new JSONObject(result);
					if (!isSuccess(obj)) {
						return;
					}
					
					List<FAQBean> listTemp = gson.fromJson(obj.getJSONObject("data")
							.getJSONArray("error").toString(), new TypeToken<List<FAQBean>>(){}.getType());
					if (listTemp.size() == 0) {
//						if (page == 1) {
							KappUtils.showToast(context, "暂无常见问题");
//						} else {
//							page--;
//							KappUtils.showToast(context, "无更多数据");
//						}
					} else {
						list.addAll(listTemp);
					}
					adapter.notifyDataSetChanged();
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onError(Throwable ex, boolean isOnCallback) {
				refreshingEnd();
			}

			@Override
			public void onCancelled(CancelledException cex) {
				refreshingEnd();
			}

			@Override
			public void onFinished() {
				refreshingEnd();
			}
		});
	}
	
	private void refreshingEnd() {
		closeDlg();
		lv_data.getLoadingLayoutProxy().setLastUpdatedLabel("最近更新:" + DateUtil.getCurrentTimeString(0));
		lv_data.onRefreshComplete();
	}

	@Override
	public void onClick(View v) {
//		Intent intent;
//		switch (v.getId()) {
//			case R.id.layout_update:
//				intent = new Intent(this, CheckUpdateActivity.class);
//				startActivity(intent);
//				break;
//		}
	}

}
