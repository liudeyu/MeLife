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
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gzmelife.app.KappAppliction;
import com.gzmelife.app.R;
import com.gzmelife.app.UrlInterface;
import com.gzmelife.app.adapter.MyUploadCookbookAdapter;
import com.gzmelife.app.bean.MyUploadCookbookBean;
import com.gzmelife.app.tools.DateUtil;
import com.gzmelife.app.tools.MyLog;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

@ContentView(R.layout.activity_audit_information)
public class MyUploadCookbookActivity extends BaseActivity {
	@ViewInject(R.id.tv_title)
	TextView tv_title;

	@ViewInject(R.id.layout_noData)
	View layout_noData;
	@ViewInject(R.id.tv_title_left)
	TextView tv_title_left;
	@ViewInject(R.id.lv_data)
	PullToRefreshListView lv_data;

	List<MyUploadCookbookBean> listTemp;

	private List<MyUploadCookbookBean> list = new ArrayList<MyUploadCookbookBean>();
	private MyUploadCookbookAdapter adapter;

	private String TAG="MyUploadCookbookActivity";
	// private int page = 1;
	// private int pageSize = 10;

	private Context context;

	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);

		context = this;
		initView();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void initView() {
		tv_title.setText("上传菜谱记录");
		tv_title_left.setVisibility(View.VISIBLE);
		tv_title_left.setText("个人中心");
		adapter = new MyUploadCookbookAdapter(context, list);
		lv_data.setAdapter(adapter);

		// lv_data.setOnItemClickListener(new OnItemClickListener() {
		// @Override
		// public void onItemClick(AdapterView<?> parent, View view,
		// int position, long id) {
		// startActivity(new Intent(context,
		// AboutUsActivity.class).putExtra("flag", KappUtils.FLAG_FAQ_DETAIL));
		// }
		// });

		lv_data.setMode(Mode.DISABLED);
		lv_data.setOnRefreshListener(new OnRefreshListener2() {
			@Override
			public void onPullDownToRefresh(PullToRefreshBase refreshView) {
				list.clear();
				// page = 1;
				getData();
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase refreshView) {
				// page++;
				getData();
			}
		});
		getData();

		lv_data.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				String menuBookId = list.get((int) id).getId();
				Intent intent = new Intent(MyUploadCookbookActivity.this,
						NetCookBooksDetailActivity.class);
				intent.putExtra("menuBookId", menuBookId);
				startActivity(intent);
			}
		});
	}

	private void getData() {
		Log.i(TAG, "getData-->");
		showDlg();
		RequestParams params = new RequestParams(
				UrlInterface.URL_MY_UPLOAD_COOKBOOK);
		params.addBodyParameter("userId", KappAppliction.myApplication
				.getUser().getId() + "");
		Log.i(TAG, "getData-->URL:"+UrlInterface.URL_MY_UPLOAD_COOKBOOK+"?userId="+ KappAppliction.myApplication
				.getUser().getId()
				);
		x.http().post(params, new CommonCallback<String>() {
			@Override
			public void onSuccess(String result) {
				Log.i(TAG, "net getData-->onSuccess-->"+result);
				refreshingEnd();
				MyLog.i(MyLog.TAG_I_JSON, result);
				try {
					Gson gson = new Gson();
					JSONObject obj = new JSONObject(result);
					if (!isSuccess(obj)) {
						return;
					}

					listTemp = gson.fromJson(obj.getJSONObject("data")
							.getJSONArray("menubooks").toString(),
							new TypeToken<List<MyUploadCookbookBean>>() {
							}.getType());
					if (listTemp.size() == 0) {

						// if (page == 1) {
						// KappUtils.showToast(context, "暂无系统消息");
						// } else {
						// page--;
						// KappUtils.showToast(context, "无更多数据");
						// }

						layout_noData.setVisibility(View.VISIBLE);
						lv_data.setVisibility(View.GONE);
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
		Log.i(TAG, "refreshingEnd-->");
		closeDlg();
		lv_data.getLoadingLayoutProxy().setLastUpdatedLabel(
				"最近更新:" + DateUtil.getCurrentTimeString(0));
		lv_data.onRefreshComplete();
	}
}
