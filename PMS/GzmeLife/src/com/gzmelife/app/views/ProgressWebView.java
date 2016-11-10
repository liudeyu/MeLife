package com.gzmelife.app.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.gzmelife.app.R;

/**
 * @Description: 带进度条的WebView
 */
@SuppressLint("SetJavaScriptEnabled")
@SuppressWarnings("deprecation")
public class ProgressWebView extends WebView {
	private ProgressBar progressbar;
	TextView tv_title;

	public ProgressWebView(Context context, AttributeSet attrs) {
		super(context, attrs);
		progressbar = new ProgressBar(context, null,
				android.R.attr.progressBarStyleHorizontal);
		progressbar.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
				10, 0, 0));

		Drawable drawable = context.getResources().getDrawable(
				R.drawable.progress_bar_states);
		progressbar.setProgressDrawable(drawable);
		addView(progressbar);
		setWebViewClient(wvc);
		setWebChromeClient(new MyWebChromeClient() {});
		getSettings().setJavaScriptEnabled(true);
		// 是否支持缩放
		getSettings().setSupportZoom(false);
		getSettings().setBuiltInZoomControls(false);
		getSettings().setDefaultTextEncodingName("utf-8");
	}

	// 创建WebViewClient对象
	WebViewClient wvc = new WebViewClient() {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			// 使用自己的WebView组件来响应Url加载事件，而不是使用默认浏览器器加载页面
			loadUrl(url);
			// 消耗掉这个事件。Android中返回True的即到此为止吧,事件就会不会冒泡传递了，我们称之为消耗掉
			return true;
		}
	};

	public void getTitleView(TextView tv_title) {
		this.tv_title = tv_title;
	}

	public class MyWebChromeClient extends android.webkit.WebChromeClient {
		@Override
		public void onReceivedTitle(WebView view, String title) {
			super.onReceivedTitle(view, title);
			if (tv_title != null) {
				tv_title.setText(title);
			}
		}

		@Override
		public void onProgressChanged(WebView view, int newProgress) {
			if (newProgress == 100) {
				progressbar.setVisibility(GONE);
			} else {
				if (progressbar.getVisibility() == GONE) {
					progressbar.setVisibility(VISIBLE);
				}
				progressbar.setProgress(newProgress);
			}
			super.onProgressChanged(view, newProgress);
		}
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		LayoutParams lp = (LayoutParams) progressbar.getLayoutParams();
		lp.x = l;
		lp.y = t;
		progressbar.setLayoutParams(lp);
		super.onScrollChanged(l, t, oldl, oldt);
	}
}