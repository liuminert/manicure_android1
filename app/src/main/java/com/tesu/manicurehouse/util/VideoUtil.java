package com.tesu.manicurehouse.util;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

/**
 * Created by Administrator on 2015/7/17.
 */
public class VideoUtil {
	public static Bitmap createVideoThumbnail(String vidioPath, int width,
			int height, int kind) {
		Bitmap bitmap = null;
		if(vidioPath.startsWith("http")){
			MediaMetadataRetriever retriever = new MediaMetadataRetriever();
//        int kind = MediaStore.Video.Thumbnails.MINI_KIND;
			try {
				if (Build.VERSION.SDK_INT >= 14) {
					retriever.setDataSource(vidioPath, new HashMap<String, String>());
				} else {
					retriever.setDataSource(vidioPath);
				}
				bitmap = retriever.getFrameAtTime();
			} catch (IllegalArgumentException ex) {
				// Assume this is a corrupt video file
			} catch (RuntimeException ex) {
				// Assume this is a corrupt video file.
			} finally {
				try {
					retriever.release();
				} catch (RuntimeException ex) {
					// Ignore failures while cleaning up.
				}
			}
			if (kind == MediaStore.Images.Thumbnails.MICRO_KIND && bitmap != null) {
				bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
						ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
			}
		}else {
			bitmap = ThumbnailUtils.createVideoThumbnail(vidioPath, kind);
			bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
					ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
		}
		return bitmap;
	}

//	public static ArrayList<VideoBean> scanVidoSdCord() {
//		ArrayList<VideoBean> videoList = new ArrayList<VideoBean>();
//		String strPath = Environment.getExternalStorageDirectory()
//				+ File.separator + "phonetest1";// �ļ�·�����Զ���
//		File rootFile = new File(strPath);
//		File[] files = rootFile.listFiles();
//		if (files.length > 0) {
//			for (int i = 0; i < files.length; i++) {
//				if (files[i].isDirectory()) {
//					File[] videoFiles = files[i].listFiles();
//					if (videoFiles.length > 0) {// ��ͼƬ
//						for (int j = 0; j < videoFiles.length; j++) {
//							if (videoFiles[j].getName().endsWith(".mp4")
//									|| videoFiles[j].getName().endsWith(".MP4")
//									|| videoFiles[j].getName().endsWith(".3gp")
//									|| videoFiles[j].getName().endsWith(".avi")) {
//								VideoBean video = new VideoBean();
//								video.setFileParentVideo(files[i].getName());
//								video.setVidioName(videoFiles[j].getName());
//								video.setLocationPath(videoFiles[j]
//										.getAbsolutePath());
//								videoList.add(video);
//							}
//						}
//					}
//				}
//			}
//		}
//		return videoList;
//	}
}
