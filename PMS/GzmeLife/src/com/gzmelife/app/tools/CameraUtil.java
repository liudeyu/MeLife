package com.gzmelife.app.tools;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.TextView;

import com.gzmelife.app.R;

@SuppressLint("InflateParams")
public class CameraUtil {
	private final int CAMERA = 10001;
	private final int PHONTO = 10002;
	private final int CUT_PHOTO = 10003;
	public  final static int CUT_PIC = 10004;
	private Context context;
	private Activity actvity;
	
	private boolean isRate = false;
	private int width;
	private int height;

	public CameraUtil(Context context, Activity actvity) {
		this.context = context;
		this.actvity = actvity;
	}

	/** if is select a pic as a headPic,set true, so the pic is large enough */
	public void setWidthHeight(int width, int height) {
		this.isRate = true;
		this.width = width;
		this.height = height;
	}
	
	boolean isCut;
	View view;

	public void toCameraPhoto(final boolean isCut) {
		this.isCut = isCut;
		final AlertDialog myDialog = new AlertDialog.Builder(context).create();
		myDialog.setCanceledOnTouchOutside(false);
		myDialog.show();
		Window window = myDialog.getWindow();
		window.setWindowAnimations(R.style.bottomAnimation); // 从底部弹出，往底部消失
		window.setGravity(Gravity.BOTTOM);
		LayoutParams layoutParams = window.getAttributes();
		layoutParams.width = context.getResources().getDisplayMetrics().widthPixels;
		window.setAttributes(layoutParams);
		view = LayoutInflater.from(context).inflate(
				R.layout.dialog_camera, null);
		myDialog.setContentView(view);
		TextView camera = (TextView) view.findViewById(R.id.camera);
		camera.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 拍照功能
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				// 下面这句指定调用相机拍照后的照片存储的路径
				intent.putExtra(MediaStore.EXTRA_OUTPUT, getUri());
				actvity.startActivityForResult(intent, CAMERA);
				myDialog.dismiss();
			}
		});
		TextView photo = (TextView) view.findViewById(R.id.photo);
		photo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 相册
				if (isCut) {
					getCutPic();
				} else {
					getPic();
				}
				myDialog.dismiss();
			}
		});

		TextView cancel = (TextView) view.findViewById(R.id.cancel);
		cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				myDialog.dismiss();
			}
		});
	}

	String imagePath;

	private Uri getUri() {
		File file = new File(Environment.getExternalStorageDirectory(), "WA");

		if (!file.exists()) { // 创建目录
			file.mkdirs();
		}

		String name = "temp" /*+ DateUtil.getLongTime()*/ + ".jpg";
		File file1 = new File(file, name);
		file1.deleteOnExit();
		imagePath = file1.getAbsolutePath();
		Uri uri = Uri.fromFile(file1);
		return uri;
	}
	
	private void getCutPic() {
//		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//		intent.putExtra("crop", "true"); // 说明要裁剪
//		intent.putExtra("aspectX", 1); // 宽度
//		intent.putExtra("aspectY", 1); // 高度
//
////		intent.putExtra("outputX", 500);
////		intent.putExtra("outputY", 500);
//
//		intent.setType("image/*"); // 说明你想获得图片
//		intent.putExtra(MediaStore.EXTRA_OUTPUT, getUri());
		
//		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//		intent.addCategory(Intent.CATEGORY_OPENABLE);
//		intent.putExtra("return-data", true);
		Intent intent=new Intent(Intent.ACTION_PICK); // 直接从图库选取
		intent.setType("image/*"); // 说明你想获得图片
		actvity.startActivityForResult(intent, CUT_PHOTO);
	}

	private void getPic() {
/*		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		intent.putExtra("return-data", true);
		intent.setType("image/*"); // 说明你想获得图片*/
//		Intent intent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		Intent intent=new Intent(Intent.ACTION_PICK); // 直接从图库选取
		intent.setType("image/*"); // 说明你想获得图片
		actvity.startActivityForResult(intent, PHONTO);
	}

	public void startPhotoZoom(Uri uri) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		intent.putExtra(MediaStore.EXTRA_OUTPUT, getUri());
		intent.putExtra("crop", "true");
		if (isRate) {
			intent.putExtra("aspectX", width);
			intent.putExtra("aspectY", height);
		}
		intent.putExtra("return-data", false);
		actvity.startActivityForResult(intent, CUT_PIC);

	}

	/**
	 * 做剪切处理
	 * 
	 * @param requestCode
	 * @param resultCode
	 * @param data
	 * @return
	 */
	public String getImgPath(int requestCode, int resultCode, Intent data) {
		Log.d("ww", "requestCode:"+requestCode);
		Log.d("ww", "resultCode:"+resultCode);
		if (resultCode != Activity.RESULT_OK) {
			return null;
		}
		if (requestCode == CAMERA && isCut) {
			File file = new File(imagePath);
			startPhotoZoom(Uri.fromFile(file));
		} else if(requestCode == CAMERA){
			//return decodeFile(imagePath);
			return imagePath;
		}
		
		if (requestCode == CUT_PHOTO && data != null) {
//			return decodeFile(imagePath);
			Uri uri = data.getData();
			if (uri == null) {
				return null;
			}
			// 解决红米、华为等手机的问题
			String path;
			if (!TextUtils.isEmpty(uri.getAuthority())) {    
				String[] proj = { MediaColumns.DATA };
				@SuppressWarnings("deprecation")
				Cursor cursor = actvity.managedQuery(uri, proj, null, null, null);
		        if (null == cursor) {    
		        	KappUtils.showToast(context, "图片没找到");
		            return null;    
		        }    
		        cursor.moveToFirst();    
		        path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
//		        cursor.close();    
		    } else {    
		        path = uri.getPath();    
		    }  
			if (path == null) {
				KappUtils.showToast(context, "图片路径为null，错误未知");
			}
			File file = new File(path);
			startPhotoZoom(Uri.fromFile(file));
			
//			String[] proj = { MediaColumns.DATA };
//			@SuppressWarnings("deprecation")
//			Cursor cursor = actvity.managedQuery(uri, proj, null, null, null);
//			Log.d("debug", "actvity=" + (actvity == null));
//			Log.d("debug", "cursor=" + (cursor == null));
//			if (cursor != null) {
//				cursor.moveToFirst();
//				int column_index = cursor.getColumnIndexOrThrow(MediaColumns.DATA);
//				String imagePath = cursor.getString(column_index);
//				File file = new File(imagePath);
//				startPhotoZoom(Uri.fromFile(file));
//			} else {, Toast.LENGTH_SHORT).show();
//			}
		}
		if (requestCode == PHONTO && data != null) {
			Uri uri = data.getData();
			if (uri == null)
				return null;
//			String[] proj = { MediaColumns.DATA };
//			@SuppressWarnings("deprecation")
//			Cursor cursor = actvity.managedQuery(uri, proj, null, null, null);
//			int column_index = cursor.getColumnIndexOrThrow(MediaColumns.DATA);
//			cursor.moveToFirst();
//			String imagePath = cursor.getString(column_index);
//			//return decodeFile(imagePath);
//			return imagePath;
			
			// 解决红米、华为等手机的问题
			String path;
			if (!TextUtils.isEmpty(uri.getAuthority())) {    
				String[] proj = { MediaColumns.DATA };
				@SuppressWarnings("deprecation")
				Cursor cursor = actvity.managedQuery(uri, proj, null, null, null);
		        if (null == cursor) {
		        	KappUtils.showToast(context, "图片没找到");
		            return null;    
		        }    
		        cursor.moveToFirst();    
		        path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
//					        cursor.close();    
		    } else {    
		        path = uri.getPath();    
		    }  
			if (path == null) {
				KappUtils.showToast(context, "图片路径为null，错误未知");
			}
			return path;
		}
		if (requestCode == CUT_PIC) {
			//return decodeFile(imagePath);
			return imagePath;
		}
		return null;
	}

	@SuppressWarnings("unused")
	private static final int imageUpperLimitPix = 100;

	// decode这个图片并且按比例缩放以减少内存消耗，虚拟机对每张图片的缓存大小也是有限制的
//	public static String decodeFile(String imagePath) {
//		File file = new File(imagePath);
//		try {
//			// decode image size
//			BitmapFactory.Options o = new BitmapFactory.Options();
//			o.inJustDecodeBounds = true;
//			BitmapFactory.decodeStream(new FileInputStream(file), null, o);
//
//			// Find the correct scale value. It should be the power of 2.
//			int width_tmp = o.outWidth, height_tmp = o.outHeight;
//			int scale = 1;
//			while (true) {
//				if (width_tmp / 2 < imageUpperLimitPix
//						&& height_tmp / 2 < imageUpperLimitPix)
//					break;
//				width_tmp /= 2;
//				height_tmp /= 2;
//				scale *= 2;
//			}
//			// decode with inSampleSize
//			BitmapFactory.Options o2 = new BitmapFactory.Options();
//			o2.inSampleSize = scale;
//			Bitmap newBitmap = BitmapFactory.decodeStream(new FileInputStream(
//					file), null, o2);
//			return storeImageToFile(newBitmap);
//		} catch (FileNotFoundException e) {
//		}
//		return null;
//	}

	@SuppressWarnings("unused")
	private static String storeImageToFile(Bitmap bitmap) {
		if (bitmap == null) {
			return null;
		}
		File file1 = new File(Environment.getExternalStorageDirectory(),
				"mrj");

		if (!file1.exists()) { // 创建目录
			file1.mkdirs();
		}

		String name = System.currentTimeMillis() + ".jpg";
		File file = new File(file1, name);

		RandomAccessFile accessFile = null;

		ByteArrayOutputStream steam = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, steam);
		byte[] buffer = steam.toByteArray();

		try {
			accessFile = new RandomAccessFile(file, "rw");
			accessFile.write(buffer);
		} catch (Exception e) {
			return null;
		}

		try {
			steam.close();
			// XXX annother trye
			accessFile.close();
		} catch (IOException e) {
			// Note: do nothing.
		}
		return file.getAbsolutePath();
	}

	/**
	 * 半角转换为全角
	 * @param input
	 * @return
	 */
	public static String ToDBC(String input) {
		char[] c = input.toCharArray();
		for (int i = 0; i < c.length; i++) {
			if (c[i] == 12288) {
				c[i] = (char) 32;
				continue;
			}
			if (c[i] > 65280 && c[i] < 65375)
				c[i] = (char) (c[i] - 65248);
		}
		return new String(c);
	}
}