package com.gzmelife.app.tools;

import com.gzmelife.app.R;
import com.gzmelife.app.activity.CookBookDetailActivity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.widget.ImageView;

/**
 * @package��com.example.imageshowerdemo
 * @author��Allen
 * @email��jaylong1302@163.com
 * @data��2012-9-27 ����10:58:13
 * @description��The class is for...
 */
public class ImageShower extends Activity {

	private ImageView iv;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.imageshower);
		iv=(ImageView) findViewById(R.id.iv);
		if(CookBookDetailActivity.ivState==true){
//			iv.setBackgroundResource(CookBookDetailActivity.backgroundResource);
		}else{
			iv.setImageBitmap(CookBookDetailActivity.bitmap);
		}
		
//		final ImageLoadingDialog dialog = new ImageLoadingDialog(this);
//		dialog.setCanceledOnTouchOutside(false);
//		dialog.show();
//		// �����رպ�dialog
//		new Handler().postDelayed(new Runnable() {
//			@Override
//			public void run() {
//				dialog.dismiss();
//			}
//		}, 1000 * 2);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		finish();
		return true;
	}

}
