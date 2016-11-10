package com.gzmelife.app.tools;

import java.io.File;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.gzmelife.app.UrlInterface;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.utils.StorageUtils;

public class ImgLoader {
	private ImageLoader imageLoader = null;
	private DisplayImageOptions options;
	private File cacheDir;

	/**
	 * 配置参数，设置缓存
	 */
	public ImgLoader(Context context) {
		if (imageLoader == null) {
			// 初始化imageLoader 否则会报错
			imageLoader = ImageLoader.getInstance();
			cacheDir = StorageUtils.getCacheDirectory(context);
			if (!cacheDir.exists()) {
				cacheDir.mkdirs();
			}

			ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
					context).discCache(new UnlimitedDiscCache(cacheDir))
					.build();// 开始构建
			imageLoader.init(config);
			options = new DisplayImageOptions.Builder()
//					.showStubImage(R.drawable.ic_launcher) // 设置图片下载期间显示的图片
//					.showImageForEmptyUri(R.drawable.ic_error) // 设置图片Uri为空或是错误的时候显示的图片
//					.showImageOnFail(R.drawable.ic_error) // 设置图片加载或解码过程中发生错误显示的图片
					.cacheInMemory(true) // 设置下载的图片是否缓存在内存中
					.cacheOnDisc(true) // 设置下载的图片是否缓存在SD卡中
					.build();
		}
	}

	/** 加载图片，图片错误时是否显示默认图片 */
//	public ImgLoader(Context context, boolean loadingPic) {
//		if (imageLoader == null) {
//			// 初始化imageLoader 否则会报错
//			imageLoader = ImageLoader.getInstance();
//			cacheDir = StorageUtils.getCacheDirectory(context);
//			if (!cacheDir.exists()) {
//				cacheDir.mkdirs();
//			}
//
//			ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
//					context).discCache(new UnlimitedDiscCache(cacheDir))
//					.build();// 开始构建
//			imageLoader.init(config);
//			if (loadingPic) {
//				options = new DisplayImageOptions.Builder()
//						.showStubImage(R.drawable.default_image) // 设置图片下载期间显示的图片
//						.showImageForEmptyUri(R.drawable.ic_error) // 设置图片Uri为空或是错误的时候显示的图片
//						.showImageOnFail(R.drawable.ic_error) // 设置图片加载或解码过程中发生错误显示的图片
//						.cacheInMemory(true) // 设置下载的图片是否缓存在内存中
//						.cacheOnDisc(true) // 设置下载的图片是否缓存在SD卡中
//						.build();
//			} else {
//				options = new DisplayImageOptions.Builder().cacheInMemory(true) // 设置下载的图片是否缓存在内存中
//						.cacheOnDisc(true) // 设置下载的图片是否缓存在SD卡中
//						.build();
//			}
//		}
//	}

	public void showPic(String url, ImageView imgView) {
		if (!url.startsWith("http")) {
			url = UrlInterface.URL_HOST + url;
		}
		imageLoader.displayImage(url, imgView, options);
	}
	
	
	
	@SuppressWarnings("deprecation")
	public void showPic(String url, ImageView imgView, int rid) {
		DisplayImageOptions options2 = new DisplayImageOptions.Builder()
		.showStubImage(rid) // 设置图片下载期间显示的图片
		.showImageForEmptyUri(rid) // 设置图片Uri为空或是错误的时候显示的图片
		.showImageOnFail(rid) // 设置图片加载或解码过程中发生错误显示的图片
		.cacheInMemory(true) // 设置下载的图片是否缓存在内存中
		.cacheOnDisc(true) // 设置下载的图片是否缓存在SD卡中
		.build();
		if (!url.startsWith("http")) {
			url = UrlInterface.URL_HOST + url;
		}
		imageLoader.displayImage(url, imgView, options2);
	}
	
	
	/**
    
	   * @param bitmap      原图
	   * @param edgeLength  希望得到的正方形部分的边长
	   * @return  缩放截取正中部分后的位图。
	   */
	  public static Bitmap centerSquareScaleBitmap(Bitmap bitmap, int edgeLength)
	  {
	   if(null == bitmap || edgeLength <= 0)
	   {
	    return  null;
	   }
	                                                                                 
	   Bitmap result = bitmap;
	   int widthOrg = bitmap.getWidth();
	   int heightOrg = bitmap.getHeight();
	                                                                                 
	   if(widthOrg > edgeLength && heightOrg > edgeLength)
	   {
	    //压缩到一个最小长度是edgeLength的bitmap
	    int longerEdge = (int)(edgeLength * Math.max(widthOrg, heightOrg) / Math.min(widthOrg, heightOrg));
	    int scaledWidth = widthOrg > heightOrg ? longerEdge : edgeLength;
	    int scaledHeight = widthOrg > heightOrg ? edgeLength : longerEdge;
	    Bitmap scaledBitmap;
	                                                                                  
	          try{
	           scaledBitmap = Bitmap.createScaledBitmap(bitmap, scaledWidth, scaledHeight, true);
	          }
	          catch(Exception e){
	           return null;
	          }
	                                                                                       
	       //从图中截取正中间的正方形部分。
	       int xTopLeft = (scaledWidth - edgeLength) / 2;
	       int yTopLeft = (scaledHeight - edgeLength) / 2;
	                                                                                     
	       try{
	        result = Bitmap.createBitmap(scaledBitmap, xTopLeft, yTopLeft, edgeLength, edgeLength);
	        scaledBitmap.recycle();
	       }
	       catch(Exception e){
	        return null;
	       }       
	   }
	                                                                                      
	   return result;
	  }
}
