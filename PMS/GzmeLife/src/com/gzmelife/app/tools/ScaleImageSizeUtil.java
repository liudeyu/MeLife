package com.gzmelife.app.tools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Environment;

public class ScaleImageSizeUtil {

	/**
	 * 压缩图片大小
	 */
	/**
	 * 压缩指定路径图片，并将其保存在缓存目录中，通过isDelSrc判定是否删除源文件，并获取到缓存后的图片路径
	 * 
	 * @param context
	 * @param srcPath
	 * @param rqsW
	 * @param rqsH
	 * @param isDelSrc
	 * @return
	 */

	public final static String compressBitmap(Context context, String srcPath,
			int rqsW, int rqsH, boolean isDelSrc) {
		Bitmap bitmap = compressBitmap(srcPath, rqsW, rqsH);
		File srcFile = new File(srcPath);
		String desPath = getImageCacheDir() + UUID.randomUUID().toString()
				+ srcFile.getName();
		int degree = getDegress(srcPath);
		try {
			if (degree != 0)
				bitmap = rotateBitmap(bitmap, degree);
			File file = new File(desPath);
			FileOutputStream fos = new FileOutputStream(file);
			// 这个压缩会压到几M的
			// bitmap.compress(Bitmap.CompressFormat.PNG, 70, fos);
			// 换这种压缩方式能降到300K以下
			bitmap.compress(Bitmap.CompressFormat.JPEG, 60, fos);
			fos.close();
			// if (isDelSrc) srcFile.deleteOnExit();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (null != bitmap) {
			bitmap.recycle();
		}
		return desPath;
	}

	/**
	 * 压缩指定路径的图片，并得到图片对象
	 * 
	 * @param path
	 *            bitmap source path
	 * @return Bitmap {@link Bitmap}
	 */
	public final static Bitmap compressBitmap(String path, int rqsW, int rqsH) {
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, options);
		options.inSampleSize = caculateInSampleSize(options, rqsW, rqsH);
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeFile(path, options);
	}

	/**
	 * caculate the bitmap sampleSize
	 * 
	 * @return
	 */
	public final static int caculateInSampleSize(BitmapFactory.Options options,
			int rqsW, int rqsH) {
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;
		if (rqsW == 0 || rqsH == 0)
			return 1;
		if (height > rqsH || width > rqsW) {
			final int heightRatio = Math.round((float) height / (float) rqsH);
			final int widthRatio = Math.round((float) width / (float) rqsW);
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}
		return inSampleSize;
	}

	/**
	 * 获取图片缓存路径
	 * 
	 * @return
	 */

	public static final String fileInPhone = Environment
			.getExternalStorageDirectory().getAbsolutePath() + "/manyu";
	public static final String fileInPhone_tempimage = fileInPhone
			+ "/tempimage";

	private static String getImageCacheDir() {
		String dir = fileInPhone_tempimage + File.separator;
		File file = new File(dir);
		if (!file.exists()) {
			file.mkdirs();
		}
		return dir;
	}

	/**
	 * get the orientation of the bitmap {@link ExifInterface}
	 * 
	 * @param path
	 * @return
	 */
	public final static int getDegress(String path) {
		int degree = 0;
		try {
			ExifInterface exifInterface = new ExifInterface(path);
			int orientation = exifInterface.getAttributeInt(
					ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);
			switch (orientation) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				degree = 90;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				degree = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				degree = 270;
				break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return degree;
	}

	/**
	 * rotate the bitmap
	 * 
	 * @param bitmap
	 * @param degress
	 * @return
	 */
	public static Bitmap rotateBitmap(Bitmap bitmap, int degress) {
		if (bitmap != null) {
			Matrix m = new Matrix();
			m.postRotate(degress);
			bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
					bitmap.getHeight(), m, true);
			return bitmap;
		}
		return bitmap;
	}

}
