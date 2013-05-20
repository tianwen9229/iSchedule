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
    static final String action_boot="android.intent.action.BOOT_COMPLETED"; 
    static final String tag = "BootBroadcastReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals(action_boot)){ 
        	
        	Log.d(tag, intent.getAction());
        	
        	AudioManager audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    		AlarmManager aManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

    		iScheduleDB helper = new iScheduleDB(context);
    		List<Event> list = new ArrayList<Event>();
    		Date curDate = new Date(System.currentTimeMillis());
   		
    		try {
    			list = helper.getEventByDate(curDate);
    		} catch (ParseException e) {
    			e.printStackTrace();
    		}
    		
    		for(int i = 0; i < list.size(); i++)
    		{
    			Event event = list.get(i);
    			Mode mode = helper.getModeById(1);
    			
    			if(event.getCreatTime().getDay() == curDate.getDay()){
    				
    				//保存当前的情景模式
    				if(audio.getRingerMode() == AudioManager.RINGER_MODE_NORMAL){
    					mode.setVolume(1);
    				}
    				else{
    					mode.setVolume(0);
    				}
    				if(audio.getVibrateSetting(AudioManager.VIBRATE_TYPE_RINGER) == AudioManager.VIBRATE_SETTING_ON){
    					mode.setVibrate(1);
    				}
    				else{
    					mode.setVibrate(0);
    				}				
    				helper.updateModeById(mode);
    				
    				Intent eIntent = new Intent(context, ModifyReceiver.class);
    				eIntent.putExtra("RING", helper.getModeByEventId((int) event.getEventId()).getVolume());
    				eIntent.putExtra("VIBRATE", helper.getModeByEventId((int) event.getEventId()).getVibrate());
    				
    				PendingIntent ePendingIntent = PendingIntent.getBroadcast(context, (int)(event.getEventId() * 2), eIntent, 0);
    				
    				aManager.set(AlarmManager.RTC_WAKEUP, event.getCreatTime().getTime(), ePendingIntent);
    			}
    			if(event.getEndTime().getDay() == curDate.getDay()){
    				Intent eIntent = new Intent(context, ModifyReceiver.class); 

    				eIntent.putExtra("VOLUME", helper.getModeById(1).getVolume());
    				eIntent.putExtra("VIBRATE", helper.getModeById(1).getVibrate());
    				
    				PendingIntent ePendingIntent = PendingIntent.getBroadcast(context, (int)(event.getEventId() * 2 + 1), eIntent, 0);
    				
    				aManager.set(AlarmManager.RTC_WAKEUP, event.getEndTime().getTime(), ePendingIntent);
    			}
    				
    		}
        }

    }
}
