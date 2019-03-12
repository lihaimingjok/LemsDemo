package com.pcjz.lems.business.common.utils;

import android.content.Context;
import android.os.Environment;

import com.pcjz.lems.business.config.AppConfig;
import com.pcjz.lems.business.config.ConfigAppPath;
import com.pcjz.lems.business.config.ConfigPath;
import com.pcjz.lems.business.storage.downloaddb.DBHelper;

import java.io.File;
import java.math.BigDecimal;

/**
 * created by yezhengyu on 2017/6/22 11:00
 */
public class DataCleanManager2 {
    public static String getTotalCacheSize(Context context) throws Exception {
        File downloads = new File(ConfigPath.downLoadPath);
        File appInfos = new File(ConfigAppPath.downLoadPath);
        File downloadImagePaths = new File(ConfigPath.downloadImagePath);
        File pictures = new File(AppConfig.PICTURE_PATH);
        long cacheSize = getFolderSize(context.getCacheDir());
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            cacheSize += getFolderSize(context.getExternalCacheDir());
            cacheSize += getFolderSize(context.getDatabasePath(DBHelper.DB_NAME));
            cacheSize += getFolderSize(downloads);
            cacheSize += getFolderSize(appInfos);
            cacheSize += getFolderSize(downloadImagePaths);
            cacheSize += getFolderSize(pictures);
        }
        return getFormatSize(cacheSize);
    }

    public static void clearAllCache(Context context) {
        deleteDir(context.getCacheDir());
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            //zhy
            deleteDir(context.getExternalCacheDir());
            deleteDir(context.getDatabasePath(DBHelper.DB_NAME));
        }
    }

    private static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        if (dir == null) {
            return false;
        }
        return dir.delete();
    }

    // 获取文件
    //Context.getExternalFilesDir() --> SDCard/Android/data/你的应用的包名/files/ 目录，一般放一些长时间保存的数据
    //Context.getExternalCacheDir() --> SDCard/Android/data/你的应用包名/cache/目录，一般存放临时缓存数据
    public static long getFolderSize(File file) throws Exception {
        long size = 0;
        try {
            if (file != null && file.exists()) {
                File[] fileList = file.listFiles();
                for (int i = 0; i < fileList.length; i++) {
                    // 如果下面还有文件
                    if (fileList[i].isDirectory()) {
                        size = size + getFolderSize(fileList[i]);
                    } else {
                        size = size + fileList[i].length();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }

    /**
     * 格式化单位
     *
     * @param size
     * @return
     */
    public static String getFormatSize(double size) {
        double kiloByte = size / 1024;
        if (kiloByte < 1) {
//            return size + "Byte";
            return "0KB";
        }

        double megaByte = kiloByte / 1024;
        if (megaByte < 1) {
            BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));
            return result1.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "KB";
        }

        double gigaByte = megaByte / 1024;
        if (gigaByte < 1) {
            BigDecimal result2 = new BigDecimal(Double.toString(megaByte));
            return result2.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "MB";
        }

        double teraBytes = gigaByte / 1024;
        if (teraBytes < 1) {
            BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));
            return result3.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "GB";
        }
        BigDecimal result4 = new BigDecimal(teraBytes);
        return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString()
                + "TB";
    }
}
