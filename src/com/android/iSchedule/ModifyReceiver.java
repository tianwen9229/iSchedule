package com.android.iSchedule;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.util.Log;
//情景模式修改类，包含情景模式修改和发送通知两部分
public class ModifyReceiver extends BroadcastReceiver{
	
	static final String tag = "ModifyReceiver";
	
	@Override
    public void onReceive(Context context, Intent intent) {
		
		Log.i(tag, "ModifyReceiver=======================================================");
		Log.i(tag, "VOLUME="+intent.getIntExtra("VOLUME", -1)+"  VIBRATE="+intent.getIntExtra("VIBRATE", -1));
		
		
		AudioManager audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		NotificationManager nManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
		
		//获得要修改情景模式
		int VOLUME = intent.getIntExtra("VOLUME", -1);
		int VIBRATE = intent.getIntExtra("VIBRATE", -1);
		//获得要修改的日程id
		int eventId = intent.getIntExtra("eventId", -1);
		
		
		Log.i(tag, "eventId=" + eventId);
		
		//设置通知栏信息
		int icon = R.drawable.ic_launcher;
		CharSequence text = "已经更改为：" + (VOLUME == 1?" 响铃":" 静音") + " & " + (VIBRATE == 1?"震动":"不震动") + "  么么哒~" ;
		long when = System.currentTimeMillis();
		Notification notification = new Notification(icon, text, when);
		//通知栏被点击后会打开查看日程的界面
		Intent nIntent = new Intent(context, watchEvent.class);
		nIntent.putExtra("eventId", eventId);
		nIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
		Log.i(tag, "nIntent=" + nIntent.getIntExtra("eventId", -1));
		//以eventid作为标识创建pendingintent
		PendingIntent nPendingIntent = PendingIntent.getActivity(context, eventId, nIntent, 0);
		notification.setLatestEventInfo(context, "情景模式", text, nPendingIntent);
		
		
		//设置通知
		nManager.notify(eventId, notification);
		//更改情景模式
		switch (VOLUME * 10 + VIBRATE){
			case 11: ringAndVibrate(audio); break;
			case 10: ring(audio); break;
			case 01: vibrate(audio); break;
			case 00: silent(audio); break;
			default: ;
		}
		
	}
	
    protected void ringAndVibrate(AudioManager audio) {
    	audio.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
    	int maxRingVolume  = audio.getStreamMaxVolume( AudioManager.STREAM_RING );
    	audio.setStreamVolume(AudioManager.STREAM_RING, maxRingVolume, 0);

        audio.setVibrateSetting(AudioManager.VIBRATE_TYPE_RINGER, AudioManager.VIBRATE_SETTING_ON);     
        audio.setVibrateSetting(AudioManager.VIBRATE_TYPE_NOTIFICATION, AudioManager.VIBRATE_SETTING_ON);
    }
  
    protected void ring(AudioManager audio) {
    	audio.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
    	int maxRingVolume  = audio.getStreamMaxVolume( AudioManager.STREAM_RING );
    	audio.setStreamVolume(AudioManager.STREAM_RING, maxRingVolume, 0);
    	
        audio.setVibrateSetting(AudioManager.VIBRATE_TYPE_RINGER, AudioManager.VIBRATE_SETTING_OFF);
        audio.setVibrateSetting(AudioManager.VIBRATE_TYPE_NOTIFICATION, AudioManager.VIBRATE_SETTING_OFF);
        
    }
  
    protected void vibrate(AudioManager audio) {
    	audio.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
        audio.setVibrateSetting(AudioManager.VIBRATE_TYPE_RINGER, AudioManager.VIBRATE_SETTING_ON);
        audio.setVibrateSetting(AudioManager.VIBRATE_TYPE_NOTIFICATION, AudioManager.VIBRATE_SETTING_ON);   
        
    }
  
    protected void silent(AudioManager audio) {
    	audio.setRingerMode(AudioManager.RINGER_MODE_SILENT);
        audio.setVibrateSetting(AudioManager.VIBRATE_TYPE_RINGER, AudioManager.VIBRATE_SETTING_OFF);
        audio.setVibrateSetting(AudioManager.VIBRATE_TYPE_NOTIFICATION, AudioManager.VIBRATE_SETTING_OFF);
          
    }
}
