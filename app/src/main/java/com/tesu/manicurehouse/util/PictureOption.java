package com.tesu.manicurehouse.util;

import android.graphics.Bitmap;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.tesu.manicurehouse.R;

//设置在加载网络图片前的显示
public class PictureOption {
	 /**
     * 设置常用的设置项
     * @return
     */
    public static DisplayImageOptions getSimpleOptions() {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
        .showImageOnLoading(R.mipmap.myself_pic) //设置图片在下载期间显示的图片
        .showImageForEmptyUri(R.mipmap.myself_pic)//设置图片Uri为空或是错误的时候显示的图片
        .showImageOnFail(R.mipmap.myself_pic)  //设置图片加载/解码过程中错误时候显示的图片
        .cacheInMemory(true)//设置下载的图片是否缓存在内存中  
        .cacheOnDisk(true)//设置下载的图片是否缓存在SD卡中  
        .imageScaleType(ImageScaleType.IN_SAMPLE_INT)//设置图片以如何的编码方式显示
        .bitmapConfig(Bitmap.Config.RGB_565)//设置图片的解码类型
        .build();//构建完成
        return options;
    }

    /**
     * 设置常用的设置项
     * @return
     */
    public static DisplayImageOptions getBgOptions() {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.mipmap.pic_communucation_bg) //设置图片在下载期间显示的图片
                .showImageForEmptyUri(R.mipmap.pic_communucation_bg)//设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.mipmap.pic_communucation_bg)  //设置图片加载/解码过程中错误时候显示的图片
                .cacheInMemory(true)//设置下载的图片是否缓存在内存中
                .cacheOnDisk(true)//设置下载的图片是否缓存在SD卡中
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT)//设置图片以如何的编码方式显示
                .bitmapConfig(Bitmap.Config.RGB_565)//设置图片的解码类型
                .build();//构建完成
        return options;
    }
//    /**
//     * 设置常用的设置项
//     * @return
//     */
//    public static DisplayImageOptions getHeadImageOptions() {
//        DisplayImageOptions options = new DisplayImageOptions.Builder()
//        .showImageOnLoading(R.drawable.headimage_default) //设置图片在下载期间显示的图片
//        .showImageForEmptyUri(R.drawable.headimage_default)//设置图片Uri为空或是错误的时候显示的图片
//        .showImageOnFail(R.drawable.headimage_default)  //设置图片加载/解码过程中错误时候显示的图片
//        .cacheInMemory(true)//设置下载的图片是否缓存在内存中
//        .cacheOnDisk(true)//设置下载的图片是否缓存在SD卡中
//        .imageScaleType(ImageScaleType.IN_SAMPLE_INT)//设置图片以如何的编码方式显示
//        .bitmapConfig(Bitmap.Config.RGB_565)//设置图片的解码类型
//        .build();//构建完成
//        return options;
//    }
//
//    /**
//     * 设置常用的设置项
//     * @return
//     */
//    public static DisplayImageOptions getBgOptions() {
//        DisplayImageOptions options = new DisplayImageOptions.Builder()
//        .showImageOnLoading(R.drawable.bg_myself) //设置图片在下载期间显示的图片
//        .showImageForEmptyUri(R.drawable.bg_myself)//设置图片Uri为空或是错误的时候显示的图片
//        .showImageOnFail(R.drawable.bg_myself)  //设置图片加载/解码过程中错误时候显示的图片
//        .cacheInMemory(true)//设置下载的图片是否缓存在内存中
//        .cacheOnDisk(true)//设置下载的图片是否缓存在SD卡中
////        .imageScaleType(ImageScaleType.IN_SAMPLE_INT)//设置图片以如何的编码方式显示
////        .bitmapConfig(Bitmap.Config.RGB_565)//设置图片的解码类型
//        .build();//构建完成
//        return options;
//    }
    public static DisplayImageOptions getOptions() {
        DisplayImageOptions options = options = new DisplayImageOptions.Builder().cacheInMemory(true) // default不缓存至内存
				.cacheOnDisk(true) // default 不缓存至手机SDCard
				.build();
        return options;
    }
}
