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


public class BootBroadcastReceiver extends BroadcastReceiver {
    static final String tag = "BootBroadcastReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")){ 
        	
        	Log.d(tag, "BootBroadcastReceiver");
        	
        	DiaryReceiver drDiaryReceiver = new DiaryReceiver();
        	drDiaryReceiver.setAlarmByGettingEventsToday(context);
        }

    }
}
