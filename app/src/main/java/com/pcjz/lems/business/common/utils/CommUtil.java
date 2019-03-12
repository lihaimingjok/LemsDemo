package com.pcjz.lems.business.common.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.pcjz.lems.R;
import com.pcjz.lems.business.config.AppConfig;
import com.pcjz.lems.business.config.ConfigAppPath;
import com.pcjz.lems.business.constant.ResultStatus;
import com.pcjz.lems.business.storage.SharedPreferencesManager;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.graphics.Bitmap.createBitmap;

public class CommUtil {


    private static CommUtil instance = null;
    public synchronized static CommUtil getInstance() {
        if (instance == null) {
            instance = new CommUtil();
        }
        return instance;
    }

    /**
     * 检测指定路径文件是否存在，不存在则创建
     *
     * @param filePath
     * @throws IOException
     */
    public static boolean checkFile(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            //如果 父文件夹 不存在，则创建
            if (!file.getParentFile().exists()) {
                if (file.getParentFile().mkdirs()) {
                }
            }
            try {
                if (file.createNewFile()) {
                }
            } catch (IOException e) {
                Log.i("file", "拍照文件创建失败");
                return false;
            }
        }
        return true;
    }

    /**
     * 获得指定文件的byte数组
     */
    public static byte[] getBytes(String filePath) {
        byte[] buffer = null;
        try {
            File file = new File(filePath);
            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream(1000);
            byte[] b = new byte[1000];
            int n;
            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }
            fis.close();
            bos.close();
            buffer = bos.toByteArray();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer;
    }

    /**
     * 判断是否手机号
     *
     * @param phoneNum
     * @return
     */
    public static boolean isMobilePhone(String phoneNum) {
        Pattern p = null;
        Matcher m = null;
        boolean flag = false;
        p = Pattern.compile("^[1][3,4,5,7,8][0-9]{9}$"); // 验证手机号
        m = p.matcher(phoneNum);
        flag = m.matches();
        return flag;
    }

    public static String haveWeek() {
        Date date = new Date();
        String[] weekDaysName = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        String[] weekDaysCode = {"0", "1", "2", "3", "4", "5", "6"};
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int intWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        return weekDaysName[intWeek];
    }

    public static boolean isPhone(String phoneNum){
        String regex = "^((13[0-9])|(15[^4])|(18[0,2,3,5-9])|(17[0-8])|(147))\\d{8}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(phoneNum);
        if (TextUtils.isEmpty(phoneNum)) return false;
        else return phoneNum.matches(regex);
    }


    public static String haveDate() {
        Date dt = new Date();
        SimpleDateFormat matter1 = new SimpleDateFormat("yyyy年MM月dd日");
        return matter1.format(dt);
    }

    public static String currentDate() {
        Date dt = new Date();
        SimpleDateFormat matter1 = new SimpleDateFormat("yyyy/MM/dd");
        return matter1.format(dt);
    }

    public Bitmap addWaterBitmap(Context context, Bitmap bitmap) {
        //水印图片
        Bitmap waterBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher_round);
        //水印文字
        String realName = SharedPreferencesManager.getString(ResultStatus.REALNAME);
        String currentDate = CommUtil.currentDate();
        //给所拍照片加水印
        Bitmap addWater = CommUtil.getInstance().createWaterMaskRightTop(context, bitmap, waterBitmap, 12, 12);
        //给所拍照片加文字
        //int left = bitmap.getWidth() - float secondWidth = paint.measureText(secondPoint);;
        Bitmap realNameBitmap = CommUtil.getInstance().drawTextToLeftBottom(context, addWater, realName, 10, Color.WHITE, 12, 26);
        //Bitmap realNameBitmap = CommUtil.getInstance().drawTextToCenterBottom(context, addWater, realName, 12, Color.WHITE, 26);
        //给所拍照片加日期
        Bitmap txtBitmap = CommUtil.getInstance().drawTextToLeftBottom(context, realNameBitmap, currentDate, 10, Color.WHITE, 12, 12);
        //Bitmap txtBitmap = CommUtil.getInstance().drawTextToCenterBottom(context, realNameBitmap, currentDate, 12, Color.WHITE, 12);
        return txtBitmap;
    }

    /**
     * 判断姓名的格式
     */
    public static boolean isName(String text) {
        String regex = "^[\\p{L} .'-]+$";
        boolean isName = Pattern.matches(regex, text);
        return isName;
    }

    /**
     * 判断密码是否符合   数字，字母 ,符号  6-20位
     *
     * @param pwd
     * @return
     */
    public static boolean isMatcherPassword(String pwd) {
        //String regex = "^[0-9A-Za-z]{6,14}$";
        String regex = "^[0-9a-zA-Z|,.?!:/@…\";'~()<>*&\\[\\]\\\\`#$%^_+-={}]{6,20}$";
        return pwd.matches(regex);
    }

    /**
     * 计算listView的高度
     *
     * @param listView
     */
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        // 获取ListView对应的Adapter
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) { // listAdapter.getCount()返回数据项的数目
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0); // 计算子项View 的宽高
            totalHeight += listItem.getMeasuredHeight(); // 统计所有子项的总高度
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        // listView.getDividerHeight()获取子项间分隔符占用的高度
        // params.height最后得到整个ListView完整显示需要的高度
        listView.setLayoutParams(params);
    }

    /**
     * 获取根文件目录路径
     *
     * @return
     */
    public static String getRootFilePath() {
        StringBuilder sb = new StringBuilder();
        sb.append(Environment.getExternalStorageDirectory().getAbsolutePath()).append(File.separator)
                .append(AppConfig.ROOT_FILE).append(File.separator);
        return sb.toString();
    }

    /**
     * 获取图片文件目录路径
     *
     * @return
     */
    public static String getImgFilePath() {
        StringBuilder sb = new StringBuilder();
        sb.append(getRootFilePath()).append(AppConfig.FILE_IMG).append(File.separator);
        return sb.toString();
    }

    /**
     * 将图片转换为base64
     *
     * @param imgPath
     * @return
     */
    public static String imgToBase64(String imgPath) {
        Bitmap bitmap = null;
        if (StringUtils.isEmpty(imgPath)) {
            bitmap = readBitmap(imgPath);
        }
        if (null == bitmap) {
            //bitmap not found!!
            return null;
        }
        ByteArrayOutputStream out = null;
        try {
            out = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);

            out.flush();
            out.close();

            byte[] imgBytes = out.toByteArray();
            return Base64.encodeToString(imgBytes, Base64.DEFAULT);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            return null;
        } finally {
            try {
                if (null != out) {
                    out.flush();
                    out.close();
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public static Bitmap readBitmap(String imgPath) {
        try {
            return BitmapFactory.decodeFile(imgPath);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            return null;
        }

    }

    /** 保存方法 */
    public void saveBitmap(Bitmap bm, String filePath, String picName) {
        Log.e("TAG", "保存图片");

        File f = new File(filePath);
        if (!f.exists()) {
            f.mkdirs();
        }

        try {
            File file = new File(f,picName + ".jpg");

            if (file.exists()) {
                file.delete();
            }
            FileOutputStream out = new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
            Log.i("TAG", "已经保存");
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    //给图片加水印

    public static Bitmap zoomImg(Bitmap bm, int newWidth ,int newHeight){
        // 获得图片的宽高
        int width = bm.getWidth();
        int height = bm.getHeight();
        // 计算缩放比例
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // 得到新的图片   www.2cto.com
        Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
        return newbm;
    }

    /**
     * 设置水印图片到右上角
     *
     * @param src
     * @param watermark
     * @param paddingRight
     * @param paddingTop
     * @return
     */
    public Bitmap createWaterMaskRightTop(
            Context context, Bitmap src, Bitmap watermark,
            int paddingRight, int paddingTop) {
        return createWaterMaskBitmap(src, watermark,
                src.getWidth() - watermark.getWidth() - dp2px(context, paddingRight),
                dp2px(context, paddingTop));
    }

    private Bitmap createWaterMaskBitmap(Bitmap src, Bitmap watermark,
                                                int paddingLeft, int paddingTop) {
        if (src == null) {
            return null;
        }
        int width = src.getWidth();
        int height = src.getHeight();
        //创建一个bitmap
        Bitmap.Config bitmapConfig = src.getConfig();
        Bitmap newb = Bitmap.createBitmap(width, height, bitmapConfig/*ARGB_8888*/);//createBitmap(width, height, RGB_565/*ARGB_8888*/);//Bitmap.createBitmap(width, height, RGB_565/*ARGB_8888*/);// 创建一个新的和SRC长度宽度一样的位图
        //将该图片作为画布
        Canvas canvas = new Canvas(newb);
        //在画布 0，0坐标上开始绘制原始图片
        canvas.drawBitmap(src, 0, 0, null);
        //在画布上绘制水印图片
        canvas.drawBitmap(watermark, paddingLeft, paddingTop, null);
        // 保存
        canvas.save(Canvas.ALL_SAVE_FLAG);
        // 存储
        canvas.restore();
        return newb;
    }

    /**
     * 绘制文字到左下方
     *
     * @param context
     * @param bitmap
     * @param text
     * @param size
     * @param color
     * @param paddingLeft
     * @param paddingBottom
     * @return
     */
    public Bitmap drawTextToLeftBottom(Context context, Bitmap bitmap, String text,
                                              int size, int color, int paddingLeft, int paddingBottom) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(color);
        paint.setTextSize(dp2px(context, size));
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);
        return drawTextToBitmap(context, bitmap, text, paint, bounds,
                dp2px(context, paddingLeft),
                bitmap.getHeight() - dp2px(context, paddingBottom));
    }

    public Bitmap drawTextToCenterBottom(Context context, Bitmap bitmap, String text,
                                              int size, int color,int paddingBottom) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(color);
        paint.setTextSize(dp2px(context, size));
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);
        int textWeight = (int)paint.measureText(text);
        int paddingleft = (bitmap.getWidth() - textWeight)/2;
        return drawTextToBitmap(context, bitmap, text, paint, bounds,
                paddingleft/*dp2px(context, paddingleft)*/,
                bitmap.getHeight() - dp2px(context, paddingBottom));
    }

    //图片上绘制文字
    private Bitmap drawTextToBitmap(Context context, Bitmap bitmap, String text,
                                           Paint paint, Rect bounds, int paddingLeft, int paddingTop) {
        /*android.graphics.Bitmap.Config bitmapConfig = bitmap.getConfig();

        paint.setDither(true); // 获取跟清晰的图像采样
        paint.setFilterBitmap(true);// 过滤一些
        if (bitmapConfig == null) {
            bitmapConfig = android.graphics.Bitmap.Config.RGB_565;//ARGB_8888;
        }
        bitmap = bitmap.copy(bitmapConfig, true);*/
        Canvas canvas = new Canvas(bitmap);

        canvas.drawText(text, paddingLeft, paddingTop, paint);
        return bitmap;
    }

    /**
     * 缩放图片
     *
     * @param src
     * @param w
     * @param h
     * @return
     */
    public static Bitmap scaleWithWH(Bitmap src, double w, double h) {
        if (w == 0 || h == 0 || src == null) {
            return src;
        } else {
            // 记录src的宽高
            int width = src.getWidth();
            int height = src.getHeight();
            // 创建一个matrix容器
            Matrix matrix = new Matrix();
            // 计算缩放比例
            float scaleWidth = (float) (w / width);
            float scaleHeight = (float) (h / height);
            // 开始缩放
            matrix.postScale(scaleWidth, scaleHeight);
            // 创建缩放后的图片
            return createBitmap(src, 0, 0, width, height, matrix, true);
        }
    }

    /**
     * dip转pix
     *
     * @param context
     * @param dp
     * @return
     */
    public static int dp2px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }


    public void installApk(Context context, String appName) {
        //根据路径应用名安装apk
        File dir = new File(ConfigAppPath.downLoadPath);
        File apkfile = new File(dir,appName);
        if (!apkfile.exists()) {
            return;
        }
        // 通过Intent安装APK文件
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setDataAndType(Uri.parse("file://" + apkfile.toString()), "application/vnd.android.package-archive");
        context.startActivity(i);
    }

    /**
     * 判断应用是否已安装
     *
     * @param context
     * @param packageName
     * @return
     */
    public boolean isInstalled(Context context, String packageName) {
        boolean hasInstalled = false;
        PackageManager pm = context.getPackageManager();
        List<PackageInfo> list = pm
                .getInstalledPackages(PackageManager.PERMISSION_GRANTED);
        for (PackageInfo p : list) {
            if (packageName != null && packageName.equals(p.packageName)) {
                hasInstalled = true;
                break;
            }
        }
        return hasInstalled;
    }

    public void doStartApplicationWithPackageName(Context context, String packagename) {

        // 通过包名获取此APP详细信息，包括Activities、services、versioncode、name等等
        PackageInfo packageinfo = null;
        try {
            packageinfo = context.getPackageManager().getPackageInfo(packagename, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (packageinfo == null) {
            return;
        }

        // 创建一个类别为CATEGORY_LAUNCHER的该包名的Intent
        Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
        resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        resolveIntent.setPackage(packageinfo.packageName);

        // 通过getPackageManager()的queryIntentActivities方法遍历
        List<ResolveInfo> resolveinfoList = context.getPackageManager()
                .queryIntentActivities(resolveIntent, 0);

        ResolveInfo resolveinfo = resolveinfoList.iterator().next();
        if (resolveinfo != null) {
            // packagename = 参数packname
            String packageName = resolveinfo.activityInfo.packageName;
            // 这个就是我们要找的该APP的LAUNCHER的Activity[组织形式：packagename.mainActivityname]
            String className = resolveinfo.activityInfo.name;
            // LAUNCHER Intent
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);

            // 设置ComponentName参数1:packagename参数2:MainActivity路径
            ComponentName cn = new ComponentName(packageName, className);

            intent.setComponent(cn);
            context.startActivity(intent);
        }
    }
}
