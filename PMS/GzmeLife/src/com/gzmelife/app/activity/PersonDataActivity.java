package com.gzmelife.app.activity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.x;
import org.xutils.common.Callback.CommonCallback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.gzmelife.app.R;
import com.gzmelife.app.UrlInterface;
import com.gzmelife.app.bean.UserInfoBean;
import com.gzmelife.app.tools.KappUtils;
import com.gzmelife.app.tools.MyLog;

@ContentView(R.layout.actvitiy_person_data)
public class PersonDataActivity extends BaseActivity implements OnClickListener {
	@ViewInject(R.id.tv_title)
	TextView tv_title;
	@ViewInject(R.id.et_email)
	EditText et_email;
	@ViewInject(R.id.et_autograph)
	EditText et_autograph;
	@ViewInject(R.id.rg_gender)
	RadioGroup rg_gender;
	@ViewInject(R.id.rb_man)
	RadioButton rb_man;
	@ViewInject(R.id.rb_woman)
	RadioButton rb_woman;
	@ViewInject(R.id.rb_secrecy)
	RadioButton rb_secrecy;
	private UserInfoBean bean;
	private Context context;
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		
		context = this;
		bean = (UserInfoBean) getIntent().getSerializableExtra("bean");
		initView();
	}
	
	private void initView() {
		tv_title.setText("个人资料");	
		
		if (bean.getSex().equals("1")) {
			rb_man.setChecked(true);
		} else if (bean.getSex().equals("2")) {
			rb_woman.setChecked(true);
		} else if (bean.getSex().equals("3")) {
			rb_secrecy.setChecked(true);
		}
		et_email.setText(bean.getEmail());
		et_autograph.setText(bean.getAutograph());
	}
	 private boolean checkString(String s) {   
		 String strPattern = "^[a-zA-Z][\\w\\.-]*[a-zA-Z0-9]@[a-zA-Z0-9][\\w\\.-]*[a-zA-Z0-9]\\.[a-zA-Z][a-zA-Z\\.]*[a-zA-Z]$";   
	        Pattern p = Pattern.compile(strPattern);   
	        Matcher m = p.matcher(s);   
         return m.matches();   
     }   

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btn_confirm:
				String gender = "";
				String email = "";
				if (rg_gender.getCheckedRadioButtonId() != -1) {
					gender = findViewById(rg_gender.getCheckedRadioButtonId()).getTag().toString();
				}
				if (et_email.getText().toString().matches("^[A-Za-z0-9][\\w\\._]*[a-zA-Z0-9]+@[A-Za-z0-9-_]+\\.([A-Za-z]{2,4})") || et_email.getText().toString().length() == 0) {
					email = getEditTextString(et_email);
					String autograph = getEditTextString(et_autograph);
					showDlg();
					RequestParams params = new RequestParams(UrlInterface.URL_UPDATE_USERINFO);
					params.addBodyParameter("userId",  bean.getId());
					params.addBodyParameter("nickName", bean.getNickName());
					params.addBodyParameter("logoPath", bean.getLogoPath());
					params.addBodyParameter("sex", gender);
					params.addBodyParameter("email", email);
					params.addBodyParameter("autograph", autograph);
					x.http().post(params, new CommonCallback<String>() {
						@Override
						public void onSuccess(String result) {
							MyLog.i(MyLog.TAG_I_JSON, result);
							closeDlg();
							try {
								JSONObject obj = new JSONObject(result);
								if (!isSuccess(obj)) {
									return;
								}
								KappUtils.showToast(context, "保存成功");
								PersonDataActivity.this.finish();
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
				}else {
					KappUtils.showToast(context, "请输入正确的邮箱");
				}
				
				
				break;
		}
	}
}