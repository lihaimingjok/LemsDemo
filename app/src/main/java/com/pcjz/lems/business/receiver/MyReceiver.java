package com.pcjz.lems.business.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.pcjz.lems.business.constant.SysCode;

import cn.jpush.android.api.JPushInterface;

/**
 * created by yezhengyu on 2017/6/8 20:28
 */
public class MyReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        Log.d("TAG", "onReceive - " + intent.getAction());
        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
            //逻辑代码
        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
            String msg = bundle.getString(JPushInterface.EXTRA_MESSAGE);
            System.out.println("收到了自定义消息。消息内容是：" + bundle.getString(JPushInterface.EXTRA_MESSAGE));
            // 自定义消息不会展示在通知栏，完全要开发者写代码去处理
        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
            String msg1 = bundle.getString(JPushInterface.EXTRA_NOTIFICATION_TITLE);
            String msg2 = bundle.getString(JPushInterface.EXTRA_ALERT);
            Intent intentBroadcast = new Intent();
            intentBroadcast.setAction(SysCode.ACTION_JPUSH_NOTIFICATION_RECEIVED);
            context.sendBroadcast(intentBroadcast);

            System.out.println("收到了通知");
            // 在这里可以做些统计，或者做些其他工作
        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
            System.out.println("用户点击打开了通知");
            // 在这里可以自己写代码去定义用户点击后的行为
            //Intent i = new Intent(context, SecondActivity.class);  //自定义打开的界面
            //i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //context.startActivity(i);
        } else {
            Log.d("TAG", "Unhandled intent - " + intent.getAction());
        }
    }
}
