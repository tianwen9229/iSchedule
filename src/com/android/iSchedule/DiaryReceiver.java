package com.android.iSchedule;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.security.PublicKey;
import java.sql.Date;
import java.util.List;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.util.Log;


public class DiaryReceiver extends BroadcastReceiver{
	
	static final String tag = "DiaryReceiver";
	SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	@Override
    public void onReceive(Context context, Intent intent) {
		
		Log.i(tag, "DiaryReceiver=======================================================" );
	
		setAlarmByGettingEventsToday(context);
	}
		
	public void setAlarmByGettingEventsToday(Context context){
		
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
		Log.i(tag, "listsize" + list.size() + "=======================================================");
		for(int i = 0; i < list.size(); i++)
		{
			Event event = list.get(i);
			
			
			setAlarmByGettingEvents(context, event);
		}
	}
	
	public void	setAlarmByGettingEvents(Context context, Event event){
		
		Date curDate = new Date(System.currentTimeMillis());
		iScheduleDB helper = new iScheduleDB(context);
		AudioManager audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		AlarmManager aManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
		Mode curMode = helper.getModeById(1);
		
			if(event.getStartTime().getYear() ==  curDate.getYear() && 
					event.getStartTime().getMonth() == curDate.getMonth() &&
							event.getStartTime().getDate() == curDate.getDate()){
				Log.i(tag, "modifying=======================================================");
				Log.i(tag, "MID=" + (int)(event.getEventId() * 2));
				Log.i(tag, formatter.format(event.getStartTime()));
				
				//保存当前的情景模式
				if(audio.getRingerMode() == AudioManager.RINGER_MODE_NORMAL){
					curMode.setVolume(1);
				}
				else{
					curMode.setVolume(0);
				}
				if(audio.getVibrateSetting(AudioManager.VIBRATE_TYPE_RINGER) == AudioManager.VIBRATE_SETTING_ON){
					curMode.setVibrate(1);
				}
				else{
					curMode.setVibrate(0);
				}				
				helper.updateModeById(curMode);
				
				Intent eIntent = new Intent(context, ModifyReceiver.class);
				Log.i(tag, helper.getModeByEventId((int) event.getEventId()).getName());
				eIntent.putExtra("VOLUME", helper.getModeByEventId((int) event.getEventId()).getVolume());
				eIntent.putExtra("VIBRATE", helper.getModeByEventId((int) event.getEventId()).getVibrate());
				eIntent.putExtra("MID", (int)event.getEventId() * 2);
				
				PendingIntent ePendingIntent = PendingIntent.getBroadcast(context, (int)(event.getEventId() * 2), eIntent, 0);
				
				aManager.set(AlarmManager.RTC_WAKEUP, event.getStartTime().getTime(), ePendingIntent);
			}
			if(event.getEndTime().getYear() ==  curDate.getYear() && 
					event.getEndTime().getMonth() == curDate.getMonth() &&
							event.getEndTime().getDate() == curDate.getDate()){
				Log.i(tag, "resuming=======================================================");
				Log.i(tag, "MID=" + (int)(event.getEventId() * 2 + 1));
				Log.i(tag, formatter.format(event.getEndTime()));
				Log.i(tag, helper.getModeById(1).getName());
				
				Intent eIntent = new Intent(context, ModifyReceiver.class); 
				eIntent.putExtra("VOLUME", helper.getModeById(1).getVolume());
				eIntent.putExtra("VIBRATE", helper.getModeById(1).getVibrate());
				eIntent.putExtra("MID", (int)event.getEventId() * 2 + 1);
				
				PendingIntent ePendingIntent = PendingIntent.getBroadcast(context, (int)(event.getEventId() * 2 + 1), eIntent, 0);
				
				aManager.set(AlarmManager.RTC_WAKEUP, event.getEndTime().getTime(), ePendingIntent);
			}	
		}
}