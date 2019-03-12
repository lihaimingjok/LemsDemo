package com.pcjz.lems.business.common.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;

import com.pcjz.lems.business.config.AppConfig;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class VersionChk {
	private Context context;
	private ProgressDialog mProgressDialog;
	private int mProgress = 0;
	private static final int UPDATE_PROGRESS = 1;
	private static final int UPDATE_VERSION = 2;

	public VersionChk(Context context) {
		this.context = context;
	}

	public int getVerCode() {
		int verCode = -1;
		try {
			verCode = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0).versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return verCode;
	}

	public String getVerName() {
		String verName = "";
		try {
			verName = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return verName;
	}

	public String[] getServerVer(String url) {
		String[] verInfo = { "-1", "" };
		HttpClient client = new DefaultHttpClient();
		HttpGet get = new HttpGet(url);
		HttpResponse response;
		try {
			response = client.execute(get);
			HttpEntity entity = response.getEntity();
			InputStream is = entity.getContent();
			String verjson = "";
			String line = "";

			BufferedReader rd = new BufferedReader(new InputStreamReader(is));

			while ((line = rd.readLine()) != null) {
				verjson += line;
			}
			JSONObject obj = new JSONObject(verjson);
			try {
				verInfo[0] = obj.getString("verCode");
				verInfo[1] = obj.getString("verName");
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				return verInfo;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			return verInfo;
		}
	}

	public void updateVersion(final String url, String currVerName,
			String  appVersion) {
		Dialog altDialog = new AlertDialog.Builder(context)
				.setTitle("软件更新")
				.setMessage(
						"要升级吗？\n当前版本：\t" + currVerName + "\n更新版本：\t"
								+ appVersion)
				.setPositiveButton("是",// 设置确定按钮
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								mProgressDialog = new ProgressDialog(context);
								mProgressDialog.setTitle("正在下载");
								mProgressDialog.setMessage("请稍候...");
								mProgressDialog
										.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
								downFile(url);
							}
						})
				.setNegativeButton("暂不", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						/*Intent intent = new Intent(context, LoginActivity.class);
						context.startActivity(intent);*/
					}
				}).create();
			altDialog.show();
			
		
		
	}

	public void downFile(final String url) {
		mProgressDialog.show();
		new Thread() {
			public void run() {
				HttpClient client = new DefaultHttpClient();
				HttpGet get = new HttpGet(url);
				HttpResponse response;
				try {
					response = client.execute(get);
					HttpEntity entity = response.getEntity();
					long length = entity.getContentLength();

					InputStream is = entity.getContent();
					FileOutputStream fileOutputStream = null;
					if (is != null) {
						File file = new File(AppConfig.LOCAL_PATH1);
						if (!file.exists()) {
							file.mkdirs();
						}
						File file1 = new File(file, AppConfig.APK_NAME);
						fileOutputStream = new FileOutputStream(file1);
						byte[] buf = new byte[1024];
						int ch = -1;
						int count = 0;
						while ((ch = is.read(buf)) != -1) {
							fileOutputStream.write(buf, 0, ch);
							count += ch;
							mProgress = (int) (((float) count / length) * 100);
							Message msg = new Message();
							msg.what = UPDATE_PROGRESS;
							msg.arg1 = mProgress;
							handler.sendMessage(msg);
						}
						if (ch == -1) {
							handler.sendEmptyMessage(UPDATE_VERSION);
						}
					}
					fileOutputStream.flush();
					if (fileOutputStream != null) {
						fileOutputStream.close();
					}

				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}.start();
	}

	public void update() {
		mProgressDialog.cancel();
		install();
		System.exit(0);
	}

	public void install() {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(new File(AppConfig.LOCAL_PATH1,
				AppConfig.APK_NAME)), "application/vnd.android.package-archive");
		context.startActivity(intent);
	}

	final Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case UPDATE_PROGRESS:
				mProgressDialog.setProgress(msg.arg1);
				break;
			case UPDATE_VERSION:
				update();
				break;
			default:
				break;
			}
		}

	};

}
