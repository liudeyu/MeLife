package com.gzmelife.app.activity;

import com.gzmelife.app.UrlInterface;
import com.gzmelife.app.dao.Share_data_entity;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.utils.Log;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class share_Test {
	private static final String HostURL = UrlInterface.URL_HOST;
	private static final String HostImagURL = UrlInterface.URL_HOST_IP + "/";
	private static final String HostImagURL2 = UrlInterface.URL_HOST_IP;
	private final static String TAG = "share_Test";
	public final static String SER_KEY = "Share_Entity";
	private Share_data_entity mshare_data;
	private Activity mcontext;

	public static void initApp() {
		PlatformConfig.setWeixin("wxca3ec615b5c7533b", "ae6d731c958a12f6d13d7b5cafe79a4a");
		// //豆瓣RENREN平台目前只能在服务器端配置
		// //新浪微博
		// PlatformConfig.setSinaWeibo("3921700954",
		// "04b48b094faeb16683c32669824ebdad");
		// //易信
		// PlatformConfig.setYixin("yxc0614e80c9304c11b0391514d09f13bf");
		// PlatformConfig.setQQZone("100424468",
		// "c7394704798a158208a74ab60104f0ba");
		// PlatformConfig.setTwitter("3aIN7fuF685MuZ7jtXkQxalyi",
		// "MK6FEYG63eWcpDFgRYw4w9puJhzDl0tyuqWjZ3M7XJuuG7mMbO");
		// PlatformConfig.setAlipay("2015111700822536");
		// PlatformConfig.setLaiwang("laiwangd497e70d4",
		// "d497e70d4c3e4efeab1381476bac4c5e");
		// PlatformConfig.setPinterest("1439206");
		// PlatformConfig.setWeixin("wxe26dadc0181f8907",
		// "80486d42a952440bf1e65091e72ad38b");
		PlatformConfig.setQQZone("1105441528", "mRU5AuQFWiBAfwcg");
	}

	public void setData(Activity context, Share_data_entity data) {
		mshare_data = data;
		mcontext = context;
	}

	private UMShareListener umShareListener = new UMShareListener() {
		@Override
		public void onResult(SHARE_MEDIA platform) {
			android.util.Log.i(TAG, "UMShareListener->onResult" + platform);
			Log.d("plat", "platform" + platform);
			// Toast.makeText(Share_Entity.this,platform + "
			// 分享成功啦",Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onError(SHARE_MEDIA platform, Throwable t) {
			android.util.Log.i(TAG, "UMShareListener->onError" + platform);
			// Toast.makeText(Share_Entity.this,platform + " 分享失败啦",
			// Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onCancel(SHARE_MEDIA platform) {
			android.util.Log.i(TAG, "UMShareListener->onCancel" + platform);
			// Toast.makeText(Share_Entity.this,platform + " 分享取消了",
			// Toast.LENGTH_SHORT).show();
		}
	};

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		// super.onActivityResult(requestCode, resultCode, data);
		UMShareAPI.get(mcontext).onActivityResult(requestCode, resultCode, data);
	}

	public static class platform {
		public static String TAG = "platform";
		public static String weixin = "weixin";
		public static String weixinCircle = "weixinCircle";
		public static String qq = "qq";
		public static String qqCircle = "qqCircle";

	}

	public void share(String mplatform) {
		android.util.Log.i(TAG, "进入share");
		String TargeURL;
		if (mshare_data.getTargeURL().startsWith("http")) {
			TargeURL = mshare_data.getTargeURL();
		} else {
			if (mshare_data.getTargeURL().startsWith("/")) {
				TargeURL = HostImagURL2 + mshare_data.getTargeURL();
			} else {
				TargeURL = HostImagURL + mshare_data.getTargeURL();
			}
			// TargeURL=HostImagURL+mshare_data.getTargeURL();
		}

		android.util.Log.i(TAG, "TargeURL:" + TargeURL);
		UMImage image = new UMImage(mcontext, HostImagURL + mshare_data.getImageURL());
		android.util.Log.i(TAG, HostImagURL + mshare_data.getImageURL());
		ShareAction mshareAction;

		if (mplatform.endsWith(platform.weixin)) {
			android.util.Log.i(TAG, platform.weixin);
			mshareAction = new ShareAction(mcontext).setPlatform(SHARE_MEDIA.WEIXIN).setCallback(umShareListener);
			// .withText("hello umeng")
			// .withText(")
			// .withMedia(image)
			// .withTargetUrl("http://www.baidu.com")
			// .share();
		} else if (mplatform.endsWith(platform.weixinCircle)) {
			android.util.Log.i(TAG, platform.weixinCircle);
			mshareAction = new ShareAction(mcontext).setPlatform(SHARE_MEDIA.WEIXIN_CIRCLE)
					.setCallback(umShareListener);
			// .withText("this is description")
			// .withMedia(image)
			// .withTargetUrl("http://www.baidu.com")
			// .share();
		} else if (mplatform.endsWith(platform.qq)) {
			android.util.Log.i(TAG, platform.qq);
			mshareAction = new ShareAction(mcontext).setPlatform(SHARE_MEDIA.QQ).setCallback(umShareListener);
			// .withTitle("this is title")
			// .withText("hello umeng")
			// .withMedia(image)
			// //.withMedia(music)
			// //.withTargetUrl(url)
			// //.withTitle("qqshare")
			// .share();
		} else if (mplatform.endsWith(platform.qqCircle)) {
			android.util.Log.i(TAG, platform.qqCircle);
			mshareAction = new ShareAction(mcontext).setPlatform(SHARE_MEDIA.QZONE).setCallback(umShareListener);
			// .withText("绌洪棿")
			// .withTitle("鍒嗕韩鍒扮┖闂�")
			// .withMedia(image)
			// //.withMedia(video)
			// .share();
		} else {
			android.util.Log.i(TAG, "什么都没有选中，");
			return;
		}

		mshareAction.withTitle(mshare_data.getTitle()).withText(mshare_data.getText()).withMedia(image)
				.withTargetUrl(TargeURL).share();

	}

}
