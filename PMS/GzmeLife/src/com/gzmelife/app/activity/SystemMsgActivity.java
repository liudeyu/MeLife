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
import com.gzmelife.app.adapter.SystemMsgAdapter;
import com.gzmelife.app.bean.SystemMsgBean;
import com.gzmelife.app.tools.DateUtil;
import com.gzmelife.app.tools.KappUtils;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

@ContentView(R.layout.activity_audit_information)
public class SystemMsgActivity extends BaseActivity {
	@ViewInject(R.id.tv_title)
	TextView tv_title;
	@ViewInject(R.id.tv_title_left)
	TextView tv_title_left;
	@ViewInject(R.id.lv_data)
	PullToRefreshListView lv_data;
	
	private List<SystemMsgBean> list = new ArrayList<SystemMsgBean>();
	private SystemMsgAdapter adapter;
	
	private int page = 1;
	
	private Context context;
	
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		
		context = this;
		initView();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void initView() {
		tv_title.setText("系统消息");
		tv_title_left.setVisibility(View.VISIBLE);
		tv_title_left.setText("设置");
		adapter = new SystemMsgAdapter(context, list);
		lv_data.setAdapter(adapter);
		
		lv_data.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(context, AboutUsActivity.class);
				intent.putExtra("id", list.get(position - 1).getId());
				intent.putExtra("flag", KappUtils.FLAG_SYSTEM_MSG_DETAIL);
				startActivity(intent);
			}
		});
		
		lv_data.setMode(Mode.BOTH);
		lv_data.setOnRefreshListener(new OnRefreshListener2() {
			@Override
			public void onPullDownToRefresh(PullToRefreshBase refreshView) {
				list.clear();
				page = 1;
				getData();
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase refreshView) {
				page++;
				getData();
			}
		});
		showDlg();
		getData();
	}

	private void getData() {
		RequestParams params = new RequestParams(UrlInterface.URL_SYSTEM_MSG);
		params.addBodyParameter("page", page + "");
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
					
					List<SystemMsgBean> listTemp = gson.fromJson(obj.getJSONObject("data")
							.getJSONArray("systemMsgs").toString(), new TypeToken<List<SystemMsgBean>>(){}.getType());
					if (listTemp.size() == 0) {
						if (page == 1) {
							KappUtils.showToast(context, "您还没有系统消息");
						} else {
							page--;
							KappUtils.showToast(context, "无更多数据");
						}
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
}
