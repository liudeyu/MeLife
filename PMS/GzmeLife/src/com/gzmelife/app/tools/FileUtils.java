package com.gzmelife.app.tools;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;

public class FileUtils {
	public static String PMS_FILE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath()
		+ File.separator + "pms" + File.separator;
	
	/**
	 * 根据Uri获取图片绝对路径，解决Android4.4以上版本Uri转换
	 * 
	 * @param activity
	 * @param imageUri
	 * @author yaoxing
	 * @date 2014-10-12
	 */
	@TargetApi(19)
	public static String getImageAbsolutePath(Activity context, Uri imageUri) {
		if (context == null || imageUri == null)
			return null;
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT
				&& DocumentsContract.isDocumentUri(context, imageUri)) {
			if (isExternalStorageDocument(imageUri)) {
				String docId = DocumentsContract.getDocumentId(imageUri);
				String[] split = docId.split(":");
				String type = split[0];
				if ("primary".equalsIgnoreCase(type)) {
					return Environment.getExternalStorageDirectory() + "/"
							+ split[1];
				}
			} else if (isDownloadsDocument(imageUri)) {
				String id = DocumentsContract.getDocumentId(imageUri);
				Uri contentUri = ContentUris.withAppendedId(
						Uri.parse("content://downloads/public_downloads"),
						Long.valueOf(id));
				return getDataColumn(context, contentUri, null, null);
			} else if (isMediaDocument(imageUri)) {
				String docId = DocumentsContract.getDocumentId(imageUri);
				String[] split = docId.split(":");
				String type = split[0];
				Uri contentUri = null;
				if ("image".equals(type)) {
					contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				} else if ("video".equals(type)) {
					contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
				} else if ("audio".equals(type)) {
					contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
				}
				String selection = MediaStore.Images.Media._ID + "=?";
				String[] selectionArgs = new String[] { split[1] };
				return getDataColumn(context, contentUri, selection,
						selectionArgs);
			}
		} // MediaStore (and general)
		else if ("content".equalsIgnoreCase(imageUri.getScheme())) {
			// Return the remote address
			if (isGooglePhotosUri(imageUri))
				return imageUri.getLastPathSegment();
			return getDataColumn(context, imageUri, null, null);
		}
		// File
		else if ("file".equalsIgnoreCase(imageUri.getScheme())) {
			return imageUri.getPath();
		}
		return null;
	}

	public static String getDataColumn(Context context, Uri uri,
			String selection, String[] selectionArgs) {
		Cursor cursor = null;
		String column = MediaStore.Images.Media.DATA;
		String[] projection = { column };
		try {
			cursor = context.getContentResolver().query(uri, projection,
					selection, selectionArgs, null);
			if (cursor != null && cursor.moveToFirst()) {
				int index = cursor.getColumnIndexOrThrow(column);
				return cursor.getString(index);
			}
		} finally {
			if (cursor != null)
				cursor.close();
		}
		return null;
	}

	/**
	 * @param uri
	 *            The Uri to check.
	 * @return Whether the Uri authority is ExternalStorageProvider.
	 */
	public static boolean isExternalStorageDocument(Uri uri) {
		return "com.android.externalstorage.documents".equals(uri
				.getAuthority());
	}

	/**
	 * @param uri
	 *            The Uri to check.
	 * @return Whether the Uri authority is DownloadsProvider.
	 */
	public static boolean isDownloadsDocument(Uri uri) {
		return "com.android.providers.downloads.documents".equals(uri
				.getAuthority());
	}

	/**
	 * @param uri
	 *            The Uri to check.
	 * @return Whether the Uri authority is MediaProvider.
	 */
	public static boolean isMediaDocument(Uri uri) {
		return "com.android.providers.media.documents".equals(uri
				.getAuthority());
	}

	/**
	 * @param uri
	 *            The Uri to check.
	 * @return Whether the Uri authority is Google Photos.
	 */
	public static boolean isGooglePhotosUri(Uri uri) {
		return "com.google.android.apps.photos.content".equals(uri
				.getAuthority());
	}
	
	/**
     * 文件转化为字节数组
     */
    public static byte[] getBytesFromFile(File f) {
        if (f == null) {
        	Log.e("error", "file is null");
            return null;
        }
        try {
            FileInputStream stream = new FileInputStream(f);
            ByteArrayOutputStream out = new ByteArrayOutputStream(1000);
            byte[] b = new byte[1000];
            int n;
            while ((n = stream.read(b)) != -1) {
                out.write(b, 0, n);
            }
            stream.close();
            out.close();
            return out.toByteArray();
        } catch (IOException e) {
        	e.printStackTrace();
        }
        return null;
    }
    
    /** 追加数据到文件 */
    public static void appendTextToFile(String file, String conent) {  
        BufferedWriter out = null;  
        try {  
            out = new BufferedWriter(new OutputStreamWriter(  
                    new FileOutputStream(file, true)));  
            out.write(conent);
        } catch (Exception e) {  
            e.printStackTrace();  
        } finally {  
            try {  
                out.close();  
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
        }  
    }  
    
    /** 覆盖数据到文件 */
    public static void writeTextToFile(String filePath, byte[] content) {  
    	OutputStream out = null;
    	try {  
	    	File file = new File(filePath);
	    	out = new FileOutputStream(file, false);
            out.write(content);
            out.flush();
        } catch (Exception e) {  
            e.printStackTrace();  
        } finally {  
            try {  
            	if (out != null) {
            		out.close();
            	}
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
        }  
    }  
    
    /** 若该PMS文件存在，则加上(2)(3)之类的标识 */
    public static String getFileName(String path) {
    	String resultPath = path;
    	MyLog.d("要下载的文件名：" + resultPath);
    	File dir = new File(PMS_FILE_PATH);
		if (!dir.exists()) {
			dir.mkdir();
		}
		int flag = 2;
		File file = new File(PMS_FILE_PATH + resultPath);
		while (file.exists()) {
			resultPath = path.substring(0, path.indexOf('.')) + "(" + flag++ + ")" + path.substring(path.indexOf('.'));
			file = new File(PMS_FILE_PATH + resultPath);
		}
		MyLog.d("保存在手机内的文件名：" + resultPath);
    	return resultPath;
    }
}