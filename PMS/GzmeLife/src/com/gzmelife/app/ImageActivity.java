package com.gzmelife.app;

import com.gzmelife.app.tools.ImgLoader;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class ImageActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image);
		ImageView imageView = (ImageView) findViewById(R.id.imageView);
		ImageView view = (ImageView) findViewById(R.id.iv_titleLeft);
		TextView tv = (TextView) findViewById(R.id.tv_title);
		tv.setText("");
		// Bundle bundle = getIntent().getExtras();
		// Bitmap bitmap = bundle.getParcelable("bitmap");
		Intent intent = getIntent();
		if (intent != null) {
			byte[] bis = intent.getByteArrayExtra("bitmap");
			Bitmap bitmap = BitmapFactory.decodeByteArray(bis, 0, bis.length);
			imageView.setImageBitmap(bitmap);

		}
		String bitmapUrl = getIntent().getStringExtra("bitmapUrl");
		if (bitmapUrl != null || !bitmapUrl.equals("")) {
			new ImgLoader(this).showPic(bitmapUrl, imageView);
		}

		// view.setImageBitmap(bitmap);
		imageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ImageActivity.this.finish();
			}
		});
		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ImageActivity.this.finish();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.image, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
