package com.gzmelife.app.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

import android.util.Log;



public class HttpDownloader {
	private URL url=null;
	FileUtil fileUtils=new FileUtil();
	File resultFile;
	private HttpURLConnection curConnect=null;
	public interface loaderListener{
		void onStart(int cursize,int allSize);
		void onError(int code);
		void OnComplete(int code);
		void onLoading(int cursize,int allSize);
	}
	private loaderListener mloaderListener;
	public File downfilePms(String urlStr,String path,String fileName)
	{
//		int flag = 2;
//		if(fileUtils.isFileExist(path+fileName)){
			
//			String resultPath = path.substring(0, path.indexOf('.')) + "(" + flag++ + ")" + path.substring(path.indexOf('.'));
//			String filePath=fileUtils.getFileName(path+fileName);
//			String[] str=filePath.split("/");
//			try {
//				InputStream input=null;
//				input = getInputStream(urlStr);
//				File resultFile=fileUtils.write2SDFromInput(path, str[1], input);
//				if(resultFile==null){
//					return -1;
//				}
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			return null;
//			
//		}else{
			try {
				InputStream input=null;
				input = getInputStream(urlStr);
				resultFile=write2SDFromInput(path, fileName, input);
				if(resultFile==null){
					return null;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
//		}
		
		
		
		
		return resultFile;
	}
	
	public int downfile(String urlStr,String path,String fileName)
	{
		//返回值：1：已下载              -1下载失败
		int flag = 2;
		if(fileUtils.isFileExist(path+fileName)){
//			
//			String resultPath = path.substring(0, path.indexOf('.')) + "(" + flag++ + ")" + path.substring(path.indexOf('.'));
//			String filePath=fileUtils.getFileName(path+fileName);
//			String[] str=filePath.split("/");
//			try {
//				InputStream input=null;
//				input = getInputStream(urlStr);
//				File resultFile=fileUtils.write2SDFromInput(path, str[1], input);
//				if(resultFile==null){
//					return -1;
//				}
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			if(mloaderListener!=null)
				mloaderListener.onError(1);
			return 1;
			
		}else{
			try {
				InputStream input=null;
				
				input = getInputStream(urlStr);
//				File resultFile=fileUtils.write2SDFromInput(path, fileName, input);
				File resultFile=write2SDFromInput(path, fileName, input);
				if(resultFile==null){
//					if(mloaderListener!=null)
//						mloaderListener.onError(-1);
					return -1;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
		
		
		return 0;
	}
  //由于得到一个InputStream对象是所有文件处理前必须的操作，所以将这个操作封装成了一个方法
       public InputStream getInputStream(String urlStr) throws IOException
       {     
    	   InputStream is=null;
    	    try {
				url=new URL(urlStr);
				HttpURLConnection urlConn=(HttpURLConnection)url.openConnection();
				curConnect=urlConn;
				is=urlConn.getInputStream();
				
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
    	    return is;
       }
       
   	private File write2SDFromInput(String path,String fileName,InputStream input){
		File file = null;
		OutputStream output = null;
//		InputStreamReader  
		try{
			fileUtils.creatSDDir(path);
			byte[] fileName_gbk=fileName.getBytes("gbk");
			String fileName_gbk_str=new String(fileName_gbk, "gbk");
			file = fileUtils.creatSDFile(path + fileName);
			long newTime = (new Date()).getTime();
			file.setLastModified(newTime);
//			Log.i("PMS", "into—--"+dateFormat(file.lastModified()));
			output = new FileOutputStream(file);
//			byte buffer [] = new byte[(int) file.length()];
			  int temp = 0; 
//              byte[] data = new byte[1024]; 
              byte[] data = new byte[512]; 
              if(mloaderListener!=null){
            	  if(curConnect!=null)
            		  mloaderListener.onStart(0, curConnect.getContentLength()); 
              }
            int allsize=  curConnect.getContentLength();
            int count=0;
			while((temp = input.read(data)) != -1){
				output.write(data, 0, temp);
				count+=temp;
				if(mloaderListener!=null){
					mloaderListener.onLoading(count, allsize);
				}
			}
			output.flush();
		}
		catch(Exception e){
			e.printStackTrace();
			if(mloaderListener!=null){
				mloaderListener.onError(-1);
			}
		}
		finally{
			try{
				output.close();
				if(mloaderListener!=null){
					mloaderListener.OnComplete(0);
				}
			}
			catch(Exception e){
				e.printStackTrace();
				if(mloaderListener!=null){
					mloaderListener.onError(-1);
				}
			}
		}
		return file;
	}
public void setOnLoaderListener(loaderListener lister){
	mloaderListener=lister;
}
}