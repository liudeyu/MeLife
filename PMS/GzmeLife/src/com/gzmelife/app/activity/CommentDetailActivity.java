package com.gzmelife.app.activity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback.CommonCallback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;



import com.google.gson.Gson;
import com.gzmelife.app.KappAppliction;
import com.gzmelife.app.R;
import com.gzmelife.app.UrlInterface;
import com.gzmelife.app.tools.DataUtil;
import com.gzmelife.app.tools.KappUtils;
import com.gzmelife.app.views.ContainsEmojiEditText;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("NewApi")
@ContentView(R.layout.activity_comment_detail)
public class CommentDetailActivity extends BaseActivity implements
		OnClickListener {
	// 菜谱ID
	private String menuBookId;
	private TextView title;
//	private ContainsEmojiEditText et_content;
	private EditText et_content;
	private Button bt_content;

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		menuBookId = getIntent().getStringExtra("menuBookId");
		getviews();
	}

	private void getviews() {
		title = (TextView) findViewById(R.id.tv_title);
		title.setText("评论");
//		et_content = (ContainsEmojiEditText) findViewById(R.id.et_content);
		et_content = (EditText) findViewById(R.id.et_content);
		bt_content = (Button) findViewById(R.id.bt_content);
		bt_content.setOnClickListener(this);
	    et_content.setFilters(emojiFilters);
	}

	private void getdata() {
		showDlg();
		RequestParams params = new RequestParams(UrlInterface.URL_CONTENT);
		params.addBodyParameter("userId", KappAppliction.myApplication
				.getUser().getId());
		params.addBodyParameter("menuBookId", menuBookId);
		params.addBodyParameter("content", et_content.getText().toString());
		org.xutils.x.http().post(params, new CommonCallback<String>() {

			@Override
			public void onSuccess(String result) {
				// TODO Auto-generated method stub
				closeDlg();
				Gson gson = new Gson();
				JSONObject obj;
				try {
					obj = new JSONObject(result.trim());

					String msg = obj.getString("msg");
					if (!msg.equals("成功")) {
						KappUtils.showToast(context, msg);
						return;
					} else {
						KappUtils.showToast(context, "评论成功");
						finish();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				System.out.println(">>>>>>>result====" + result);
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

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.bt_content:
			if (et_content.getText().toString().equals("")) {
				KappUtils.showToast(context, "请输入评论");
			} else {
				getdata();
				Intent intent = new Intent();
				setResult(RESULT_OK);
				finish();
			}
			break;

		default:
			break;
		}
	}
	private  InputFilter emojiFilter = new InputFilter() {


        Pattern emoji = Pattern.compile(


        "[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\u2600-\u27ff]",


        Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE);


        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest,
                int dstart,


                int dend) {


            Matcher emojiMatcher = emoji.matcher(source);


            if (emojiMatcher.find()) {
            	KappUtils.showToast(CommentDetailActivity.this, "请勿输入表情符号");
                return "";
            }
            return null;


        }
    };


    public  InputFilter[] emojiFilters = {emojiFilter};


}
