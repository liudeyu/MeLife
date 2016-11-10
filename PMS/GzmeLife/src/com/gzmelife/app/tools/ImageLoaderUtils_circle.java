package com.gzmelife.app.tools;

import android.graphics.Bitmap;

import com.gzmelife.app.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

public class ImageLoaderUtils_circle {	
	public static DisplayImageOptions MyDisplayImageOptions() {		
		DisplayImageOptions options = new DisplayImageOptions.Builder()
		.showImageOnLoading(R.drawable.tu2) //设置图片在下载期间显示的图片  		
		.showImageForEmptyUri(R.drawable.tu2)//设置图片Uri为空或是错误的时候显示的图片  		
		.showImageOnFail(R.drawable.tu2)  //设置图片加载/解码过程中错误时候显示的图片		
		.cacheInMemory(true)//设置下载的图片是否缓存在内存中  		
		.cacheOnDisk(true)		
		.considerExifParams(true)  //是否考虑JPEG图像EXIF参数（旋转，翻转）		
		.imageScaleType(ImageScaleType.EXACTLY_STRETCHED)//设置图片以如何的编码方式显示  		
		.bitmapConfig(Bitmap.Config.RGB_565)//设置图片的解码类型//  		
		.displayer(new RoundedBitmapDisplayer(360))//是否设置为圆角，弧度为多少  //		
		.displayer(new FadeInBitmapDisplayer(100))//是否图片加载好后渐入的动画时间  		
		.build();//构建完成  				
		return options;	
		}
}