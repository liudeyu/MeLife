package com.gzmelife.app.tools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.gzmelife.app.views.pulldown.Logger;

import android.os.Environment;
import android.text.format.DateFormat;
import android.util.Log;

public class FileUtil {

		public static String SDPATH = Environment.getExternalStorageDirectory().getAbsolutePath()+"/";

		public static String PMSPATH=SDPATH+"pms/";
		public String getSDPATH() {
			return SDPATH;
		}
		public FileUtil() {
			//得到当前外部存储设备的目录
			// /SDCARD
//			SDPATH = Environment.getExternalStorageDirectory() + "/";
			
//			SDPATH = Environment.getExternalStorageDirectory().getAbsolutePath()+"/";
		}
		/**
		 * 在SD卡上创建文件
		 * 
		 * @throws IOException
		 */
		public File creatSDFile(String fileName) throws IOException {
			File file = new File(SDPATH + fileName);
			file.createNewFile();
			return file;
		}
		
		/**
		 * 在SD卡上创建目录
		 * 
		 * @param dirName
		 */
		public File creatSDDir(String dirName) {
			File dir = new File(SDPATH + dirName);
			dir.mkdir();
			return dir;
		}
		private String dateFormat(long time) {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
					Locale.getDefault());
			return format.format(new Date(time));
		}
		/**
		 * 判断SD卡上的文件夹是否存在
		 */
		public boolean isFileExist(String fileName){
			File file = new File(SDPATH + fileName);
			return file.exists();
		}
		
	    /** 若该PMS文件存在，则加上(2)(3)之类的标识 */
	    public static String getFileName(String path) {
	    	String resultPath = path;
	    	MyLog.d("要下载的文件名：" + resultPath);
	    	File dir = new File(SDPATH);
			if (!dir.exists()) {
				dir.mkdir();
			}
			int flag = 2;
			File file = new File(SDPATH + resultPath);
			while (file.exists()) {
				resultPath = path.substring(0, path.indexOf('.')) + "(" + flag++ + ")" + path.substring(path.indexOf('.'));
				file = new File(SDPATH + resultPath);
			}
			MyLog.d("保存在手机内的文件名：" + resultPath);
	    	return resultPath;
	    }
		
		/**
		 * 将一个InputStream里面的数据写入到SD卡中
		 */
		public File write2SDFromInput(String path,String fileName,InputStream input){
			File file = null;
			OutputStream output = null;
//			InputStreamReader  
			try{
				creatSDDir(path);
				byte[] fileName_gbk=fileName.getBytes("gbk");
				String fileName_gbk_str=new String(fileName_gbk, "gbk");
				file = creatSDFile(path + fileName);
				long newTime = (new Date()).getTime();
				file.setLastModified(newTime);
				Log.i("PMS", "into—--"+dateFormat(file.lastModified()));
				output = new FileOutputStream(file);
//				byte buffer [] = new byte[(int) file.length()];
				  int temp = 0; 
                  byte[] data = new byte[1024]; 
                  
                 
				while((temp = input.read(data)) != -1){
					output.write(data, 0, temp);
				}
				output.flush();
			}
			catch(Exception e){
				e.printStackTrace();
			}
			finally{
				try{
					output.close();
				}
				catch(Exception e){
					e.printStackTrace();
				}
			}
			return file;
		}
}
