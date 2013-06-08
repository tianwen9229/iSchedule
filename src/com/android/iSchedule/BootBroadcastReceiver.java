package com.android.iSchedule;

import java.sql.Date;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.util.Log;

//开机广播接受类，在开机后将查询数据库，为与今天相关的日程设置的闹钟
public class BootBroadcastReceiver extends BroadcastReceiver {
    static final String tag = "BootBroadcastReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")){ 
        	
        	Log.d(tag, "BootBroadcastReceiver");
        	//重启后所有pendingintent都会消失，故需要查询数据库，将与今天相关的日程
        	DiaryReceiver drDiaryReceiver = new DiaryReceiver();
        	drDiaryReceiver.setAlarmByGettingEventsToday(context);
        }

    }
}
