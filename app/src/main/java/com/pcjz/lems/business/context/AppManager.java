package com.pcjz.lems.business.context;

import android.app.Activity;
import android.content.Context;

import java.util.Stack;

public class AppManager {

	private static Stack<Activity> activityStack;
	private static AppManager instance;

	private AppManager() {
	}

	public static AppManager getAppManager() {
		if (instance == null) {
			instance = new AppManager();
		}
		if (activityStack == null) {
			activityStack = new Stack<Activity>();
		}
		return instance;
	}

	// 添加Activity到堆栈
	public void addActivity(Activity activity) {
		activityStack.add(activity);
	}

	// 获取当前Activity（堆栈中最后一个压入的）
	public Activity currentActivity() {
		Activity activity = activityStack.lastElement();
		return activity;
	}

	// 结束当前Activity（堆栈中最后一个压入的）
	public void finishActivity() {
		Activity activity = activityStack.lastElement();
		finishActivity(activity);
	}

	// 结束指定的Activity
	public void finishActivity(Activity activity) {
		if (activity != null) {
			activityStack.remove(activity);
			activity.finish();
			activity = null;
		}
	}

	// 结束指定类名的Activity
	public void finishActivity(Class<?> cls) {
		for (Activity activity : activityStack) {
			if (activity.getClass().equals(cls)) {
				finishActivity(activity);
			}
		}
	}

	// 结束所有Activity
	public void finishAllActivity() {
		for (int i = 0, size = activityStack.size(); i < size; i++) {
			if (null != activityStack.get(i)) {
				activityStack.get(i).finish();
			}
		}
		activityStack.clear();
	}
	
	public void finishAllExcepActivity(Class<?> cls) {
		for (int i = 0, size = activityStack.size(); i < size; i++) {
			if (null != activityStack.get(i) && !activityStack.get(i).getClass().equals(cls)) {
				activityStack.get(i).finish();
			}
		}
		activityStack.clear();
	}

	// 退出应用程序
	public void exitApp(Context context) {
		finishAllActivity();
		/*
		 * ActivityManager activityMgr= (ActivityManager)
		 * context.getSystemService(Context.ACTIVITY_SERVICE);
		 * activityMgr.restartPackage(context.getPackageName());
		 */
		System.exit(0);
	}
}
