package com.pcjz.lems.business.common.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;

/**
 * Created by ${YGP} on 2017/4/24.
 */

public class FileUtils {

    public static String readTxtFile(String filePath, String fileName) {
        InputStreamReader read = null;
        StringBuffer sbf = new StringBuffer();
        try {
            File file = new File(filePath,fileName);
            if (file.exists() && file.isFile()) { //判断文件是否存在
                read = new InputStreamReader(new FileInputStream(file), "utf-8");//考虑到编码格式
                BufferedReader bufferedReader = new BufferedReader(read);
                String lineTxt = null;
                while ((lineTxt = bufferedReader.readLine()) != null) {
                    byte[] dataArr = lineTxt.getBytes("utf-8");
                    sbf.append(new String(dataArr));
                }

            } else {
                System.out.println("找不到指定的文件");
            }

        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            if (read != null) {
                try {
                    read.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return sbf.toString();
    }


    public static void createDir(String fileDir) {
        File file = new File(fileDir);
        if (file.exists() && file.isDirectory()) {
            return;
        }
        try {
            file.mkdirs();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void createFile(String filePath, String fileName) {
        File f = new File(filePath, fileName);
        if (f.exists()) {
            // 文件已经存在，输出文件的相关信息
            System.out.println(f.getAbsolutePath());
            System.out.println(f.getName());
            System.out.println(f.length());
        } else {
            //  先创建文件所在的目录
            f.getParentFile().mkdirs();
            try {
                // 创建新文件
                f.createNewFile();
            } catch (IOException e) {
                System.out.println("创建新文件时出现了错误。。。");
                e.printStackTrace();
            }
        }
    }

    public static void writeFile(String filePath, String fileDir, String fileName, String content) {
        FileOutputStream fos = null;
        try {
            File file0 = new File(filePath,fileDir);
            if (!file0.exists()) {
                file0.createNewFile();
            }
            File file = new File(file0, fileName);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            if (!file.exists()) {
                file.createNewFile();
            }
            fos = new FileOutputStream(file);
            // get the content in bytes
            byte[] contentBytes = content.getBytes("utf-8");

            fos.write(contentBytes);
            fos.flush();
            fos.close();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void writeFile(String filePath, String fileName, String content) {
        FileOutputStream fos = null;
        try {
            File file = new File(filePath, fileName);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            if (!file.exists()) {
                file.createNewFile();
            }
            fos = new FileOutputStream(file);
            // get the content in bytes
            byte[] contentBytes = content.getBytes("utf-8");

            fos.write(contentBytes);
            fos.flush();
            fos.close();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String randomReadFile(String fileName) {
        StringBuffer sbf = new StringBuffer();

        try {
            RandomAccessFile raf = new RandomAccessFile(fileName, "r");
            //raf.seek(finishedFileSize);
            //无乱码
            //        byte[] b = new byte[1024];
            //        raf.read(b, 0, 500);
            //        System.out.println(new String(b));

            //乱码
            while(raf.read()!=-1){
                sbf.append(new String(raf.readLine().getBytes(),"utf-8"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sbf.toString();
    }

    public static void deleteFile(String filePath, String fileName) {
        File file = new File(filePath, fileName);
        if (!file.getParentFile().exists()) {
            return;
        }
        if (!file.exists()) {
            return;
        }
        try {
            file.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void renameFile(String filePath, String oldName,String newName) {
        File oldFile = new File(filePath, oldName);
        if (!oldFile.exists()) {
            return;
        }
        File newFile = new File(filePath, newName);
        try {
            oldFile.renameTo(newFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void copyFile(String oldPath, String newPath) {
        FileInputStream is = null;
        FileOutputStream fos = null;
        BufferedReader br = null;
        BufferedWriter bw = null;
        boolean isFirst = false;
        try {
            File oldFile = new File(oldPath);
            if (oldFile.exists()) {                  //文件存在时
                is = new FileInputStream(oldFile);      //读入原文件
                File newFile = new File(newPath);
                fos = new FileOutputStream(newFile);

                String str = "";
                br = new BufferedReader(new InputStreamReader(is));

                bw = new BufferedWriter(new OutputStreamWriter(fos, "utf-8"));

                while ( (str = br.readLine()) != null) {
                    if (!isFirst) {
                        int position = str.indexOf("{\"id\"");
                        if(position > 0) {
                            isFirst = true;
                            str = str.substring(position, str.length());
                        }
                        else
                        {
                            continue;
                        }
                    }

                    bw.write(str);
                    bw.newLine();
                }
            }
        }  catch (Exception e) {
            System.out.println("复制单个文件操作出错");
            e.printStackTrace();
        } finally {
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public static void copyFileBak(String oldPath, String newPath) {
        FileInputStream is = null;
        FileOutputStream fos = null;
        try {
            File oldFile = new File(oldPath);
            if (oldFile.exists()) {                  //文件存在时
                is = new FileInputStream(oldFile);      //读入原文件
                File newFile = new File(newPath);
                fos = new FileOutputStream(newFile);
                byte[] buffer = new byte[1444];
                int length;
                boolean isFirst = false;
                while ( (length = is.read(buffer)) != -1) {
                    String dataStr;
                    if (length != 1444) {
                        byte[] tem = new byte[length];
                        for (int i = 0; i < length; i++) {
                            tem[i] = buffer[i];
                        }
                        dataStr = new String(tem, "utf-8");
                    } else {
                        dataStr = new String(buffer, "utf-8");
                    }
                    if (!isFirst) {
                        isFirst = true;
                        int position = dataStr.indexOf("{\"id\"");
                        dataStr = dataStr.substring(position,dataStr.length());
                    }
                    byte[] dataArr = dataStr.getBytes();
                    fos.write(dataArr,0,dataArr.length);
                }
            }
        }  catch (Exception e) {
            System.out.println("复制单个文件操作出错");
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public static void copyAccessFile(String oldPath, String newPath) {
        FileOutputStream fos = null;
        try {
            File oldFile = new File(oldPath);
            if (oldFile.exists()) {                  //文件存在时
                RandomAccessFile randomAccessFile = new RandomAccessFile(oldFile,"r");
                File newFile = new File(newPath);
                fos = new FileOutputStream(newFile);
                while ( randomAccessFile.read() != -1) {
                    String dataStr = new String(randomAccessFile.readLine().getBytes(),"utf-8");
                    byte[] dataArr = dataStr.getBytes();
                    fos.write(dataArr);
                }
            }
        }  catch (Exception e) {
            System.out.println("复制单个文件操作出错");
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static boolean DeleteFolder(String sPath) {
        boolean flag = false;
        File file = new File(sPath);
        // 判断目录或文件是否存在
        if (!file.exists()) {  // 不存在返回 false
            return flag;
        } else {
            // 判断是否为文件
            if (file.isFile()) {  // 为文件时调用删除文件方法
                return deleteFile(sPath);
            } else {  // 为目录时调用删除目录方法
                return deleteDirectory(sPath);
            }
        }
    }

    /**
     * 删除单个文件
     * @param   sPath    被删除文件的文件名
     * @return 单个文件删除成功返回true，否则返回false
     */
    public static boolean deleteFile(String sPath) {
        boolean flag = false;
        File file = new File(sPath);
        // 路径为文件且不为空则进行删除
        if (file.isFile() && file.exists()) {
            file.delete();
            flag = true;
        }
        return flag;
    }

    /**
     * 删除目录（文件夹）以及目录下的文件
     * @param   sPath 被删除目录的文件路径
     * @return  目录删除成功返回true，否则返回false
     */
    public static boolean deleteDirectory(String sPath) {
        //如果sPath不以文件分隔符结尾，自动添加文件分隔符
        if (!sPath.endsWith(File.separator)) {
            sPath = sPath + File.separator;
        }
        File dirFile = new File(sPath);
        //如果dir对应的文件不存在，或者不是一个目录，则退出
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            return false;
        }
        boolean flag = true;
        //删除文件夹下的所有文件(包括子目录)
        File[] files = dirFile.listFiles();
        for (int i = 0; i < files.length; i++) {
            //删除子文件
            if (files[i].isFile()) {
                flag = deleteFile(files[i].getAbsolutePath());
                if (!flag) break;
            } //删除子目录
            else {
                flag = deleteDirectory(files[i].getAbsolutePath());
                if (!flag) break;
            }
        }
        if (!flag) return false;
        //删除当前目录
        return dirFile.delete();
    }


}
