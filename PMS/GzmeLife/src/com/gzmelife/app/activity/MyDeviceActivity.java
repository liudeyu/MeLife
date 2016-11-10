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
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gzmelife.app.KappAppliction;
import com.gzmelife.app.R;
import com.gzmelife.app.UrlInterface;
import com.gzmelife.app.adapter.MyDeviceAdapter;
import com.gzmelife.app.bean.MyDeviceBean;
import com.gzmelife.app.tools.KappUtils;
import com.gzmelife.app.tools.MyLog;
import com.gzmelife.app.views.TipConfirmView;
import com.zxing.activity.CaptureActivity;

@ContentView(R.layout.activity_my_device)
public class MyDeviceActivity extends BaseActivity implements OnClickListener {
	@ViewInject(R.id.tv_title)
	TextView tv_title;

	@ViewInject(R.id.layout_no_data)
	View layout_no_data;

	@ViewInject(R.id.lv_data)
	ListView lv_data;
	@ViewInject(R.id.tv_title_left)
	TextView tv_title_left;
	@ViewInject(R.id.iv_scan)
	ImageView iv_scan;

	private List<MyDeviceBean> list = new ArrayList<MyDeviceBean>();
	private MyDeviceAdapter adapter;

	private static final int PHOTO_PIC = 1;

	private Context context;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);

		context = this;
		initView();
		getDeviceList();
	}

	private void getDeviceList() {
		showDlg();
		RequestParams params = new RequestParams(UrlInterface.URL_MY_DEVICES);
		params.addBodyParameter("userId", KappAppliction.myApplication
				.getUser().getId() + "");
		x.http().post(params, new CommonCallback<String>() {
			@Override
			public void onSuccess(String result) {
				closeDlg();
				MyLog.i(MyLog.TAG_I_JSON, result);

				try {
					Gson gson = new Gson();
					JSONObject obj = new JSONObject(result);
					if (!isSuccess(obj)) {
						return;
					}

					list.clear();
					List<MyDeviceBean> listTemp = gson.fromJson(obj
							.getJSONObject("data").getJSONArray("equipments")
							.toString(), new TypeToken<List<MyDeviceBean>>() {
					}.getType());
					if (listTemp.size() == 0) {
						lv_data.setVisibility(View.GONE);
						layout_no_data.setVisibility(View.VISIBLE);
						// if (page == 1) {
						// KappUtils.showToast(context, "暂无系统消息");
						// } else {
						// page--;
						// KappUtils.showToast(context, "无更多数据");
						// }

					} else {
						lv_data.setVisibility(View.VISIBLE);
						layout_no_data.setVisibility(View.GONE);
						list.addAll(listTemp);
					}
					adapter.notifyDataSetChanged();
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

	private void initView() {
		tv_title.setText("我的设备");
		iv_scan.setOnClickListener(this);
		tv_title_left.setVisibility(View.VISIBLE);
		tv_title_left.setText("个人中心");
		adapter = new MyDeviceAdapter(context, list,
				new MyDeviceAdapter.OnReceiver() {
					@Override
					public void onClick(final int position) {
						TipConfirmView.showConfirmDialog(context, "是否确认删除该设备？",
								new OnClickListener() {
									@Override
									public void onClick(View v) {
										TipConfirmView.dismiss();
										showDlg();
										RequestParams params = new RequestParams(
												UrlInterface.URL_DELETE_DEVICES);
										params.addBodyParameter("equipmentId",
												list.get(position).getId());
										x.http().post(params,
												new CommonCallback<String>() {
													@Override
													public void onSuccess(
															String result) {
														closeDlg();
														MyLog.i(MyLog.TAG_I_JSON,
																result);

														try {
															JSONObject obj = new JSONObject(
																	result);
															if (!isSuccess(obj)) {
																return;
															}

															KappUtils
																	.showToast(
																			context,
																			"删除设备成功");
															getDeviceList();
														} catch (JSONException e) {
															e.printStackTrace();
														}
													}

													@Override
													public void onError(
															Throwable ex,
															boolean isOnCallback) {
														closeDlg();
													}

													@Override
													public void onCancelled(
															CancelledException cex) {
														closeDlg();
													}

													@Override
													public void onFinished() {
														closeDlg();
													}
												});
									}
								});
					}
				});
		lv_data.setAdapter(adapter);
	}

	@Override
	public void onClick(View v) {
		Intent intent;
		switch (v.getId()) {
		case R.id.iv_scan:
			// 跳转到拍照界面扫描二维码
			intent = new Intent(context, CaptureActivity.class);
			startActivityForResult(intent, PHOTO_PIC);
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case PHOTO_PIC:
				String result = data.getExtras().getString("result");
				MyLog.d("解析结果：" + result);
				showDlg();
				RequestParams params = new RequestParams(
						UrlInterface.URL_ADD_DEVICES);
				params.addBodyParameter("userId", KappAppliction.myApplication
						.getUser().getId() + "");
				params.addBodyParameter("number", result);
				x.http().post(params, new CommonCallback<String>() {
					@Override
					public void onSuccess(String result) {
						closeDlg();
						MyLog.i(MyLog.TAG_I_JSON, result);
						try {
							JSONObject obj = new JSONObject(result);
							if (!isSuccess(obj)) {
								return;
							}

							KappUtils.showToast(context, "添加设备成功");
							getDeviceList();
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
				break;
			}
		}
	}
}
