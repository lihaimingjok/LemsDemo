/*
 * Copyright @ 2015 com.iflytek.android
 * BZMP 上午11:27:27
 * All right reserved.
 *
 */
package com.pcjz.lems.business.storage;

import android.content.Context;
import android.os.Environment;
import android.text.format.Formatter;

import com.pcjz.lems.business.config.AppConfig;

import java.io.File;

/**
 * 缓存清理工具类
 * 
 * @desc: BZFamily
 * @author: junzhang11
 * @createTime: 2015-5-6 下午2:38:24
 * @history:
 * @version: v1.0
 */
public class DataCleanManager {
	/**
	 * 清除的文件大小
	 */
	private static long size;

	/**
	 * * 清除本应用缓存
	 * 
	 * @param context
	 */
	public static String cleanCache(Context context) {
		size = 0l;
		File internalCache = context.getCacheDir();
		File ExternalCache = context.getExternalCacheDir();
		String parentPath = Environment.getExternalStorageDirectory().toString();
		// 清除sd卡temp缓存
		File tempCache = new File(parentPath + "/"+ AppConfig.ROOT_FILE+"/"+AppConfig.FILE_TEMP);
		deleteFilesByDirectory(tempCache);
		// 清除外部存储缓存
		cleanExternalCache(ExternalCache);
		// 清除应用内部缓存
		cleanInternalCache(internalCache);
//		return String.valueOf(size / 1024) + "KB";
		return Formatter.formatFileSize(context, size);
	}

	/**
	 * * 清除本应用内部缓存(/data/data/com.xxx.xxx/cache)
	 * 
	 * @param inFile
	 */
	private static void cleanInternalCache(File inFile) {
		deleteFilesByDirectory(inFile);
	}

	/**
	 * * 清除外部cache下的内容(/mnt/sdcard/android/data/com.xxx.xxx/cache)
	 * 
	 * @param exFile
	 */
	private static void cleanExternalCache(File exFile) {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			deleteFilesByDirectory(exFile);
		}
	}

	/**
	 * 删除方法 这里只会删除某个文件夹下的文件，如果传入的directory是个文件，将不做处理
	 * 
	 * @param directory
	 */
	private static void deleteFilesByDirectory(File directory) {
		if (directory != null && directory.exists() && directory.isDirectory()) {

			File[] items = directory.listFiles();
			if(null == items){
				return;
			}
			for (File item : items) {
				if (item.isFile()) {
					size += item.length();
					item.delete();
				}
			}
		}
	}


	/** * 清除本应用所有数据库(/data/data/com.xxx.xxx/databases) * * @param context */
	public static void cleanDatabases(Context context) {
		deleteFilesByDirectory(new File("/data/data/"
				+ context.getPackageName() + "/databases"));
	}
}
