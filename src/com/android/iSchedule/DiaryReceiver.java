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

//每日广播接收类，于每日0点查询数据库中与今天有关的日程，为之设置闹钟
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
			//获得与今天相关的所有日程
			list = helper.getEventByDate(curDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		Log.i(tag, "listsize" + list.size() + "=======================================================");
		for(int i = 0; i < list.size(); i++)
		{
			Event event = list.get(i);
			
			//对与今天相关的所有日程遍历，分别设置闹钟
			setAlarmByGettingEvents(context, event);
		}
	}
	
	public void	setAlarmByGettingEvents(Context context, Event event){
		
		Date curDate = new Date(System.currentTimeMillis());
		iScheduleDB helper = new iScheduleDB(context);
		AudioManager audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		AlarmManager aManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
		//从数据库中获得情景模式更改前的情景模式
		Mode curMode = helper.getModeById(1);
		//若日程是从今天开始
			if(event.getStartTime().getYear() ==  curDate.getYear() && 
					event.getStartTime().getMonth() == curDate.getMonth() &&
							event.getStartTime().getDate() == curDate.getDate()){
				Log.i(tag, "modifying=======================================================");
				Log.i(tag, "MID=" + (int)(event.getEventId() * 2));
				Log.i(tag, formatter.format(event.getStartTime()));
				
				//保存当前的情景模式到数据库
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
				//获得日程的情景模式
				eIntent.putExtra("VOLUME", helper.getModeByEventId((int) event.getEventId()).getVolume());
				eIntent.putExtra("VIBRATE", helper.getModeByEventId((int) event.getEventId()).getVibrate());
				eIntent.putExtra("eventId", (int)event.getEventId());
				
				PendingIntent ePendingIntent = PendingIntent.getBroadcast(context, (int)(event.getEventId() * 2), eIntent, 0);//以EventId*2作为标识参数，表明是开始时间
				//为日程的开始设置闹钟
				aManager.set(AlarmManager.RTC_WAKEUP, event.getStartTime().getTime(), ePendingIntent);
			}
			//若日程是在今天结束
			if(event.getEndTime().getYear() ==  curDate.getYear() && 
					event.getEndTime().getMonth() == curDate.getMonth() &&
							event.getEndTime().getDate() == curDate.getDate()){
				Log.i(tag, "resuming=======================================================");
				Log.i(tag, "MID=" + (int)(event.getEventId() * 2 + 1));
				Log.i(tag, formatter.format(event.getEndTime()));
				Log.i(tag, helper.getModeById(1).getName());
				
				Intent eIntent = new Intent(context, ModifyReceiver.class); 
				//取得日程更改前的情景模式
				eIntent.putExtra("VOLUME", helper.getModeById(1).getVolume());
				eIntent.putExtra("VIBRATE", helper.getModeById(1).getVibrate());
				eIntent.putExtra("eventId", (int)event.getEventId());
				
				PendingIntent ePendingIntent = PendingIntent.getBroadcast(context, (int)(event.getEventId() * 2 + 1), eIntent, 0);//以EventId*2+1作为标识参数，表明是结束时间
				//为日程的结束设置闹钟
				aManager.set(AlarmManager.RTC_WAKEUP, event.getEndTime().getTime(), ePendingIntent);
			}	
		}
}