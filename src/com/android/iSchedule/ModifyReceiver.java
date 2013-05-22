package com.android.iSchedule;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.util.Log;

public class ModifyReceiver extends BroadcastReceiver{
	
	static final String tag = "ModifyReceiver";
	
	@Override
    public void onReceive(Context context, Intent intent) {
		
		Log.i(tag, "ModifyReceiver=======================================================");
		Log.i(tag, "MID" + intent.getIntExtra("MID", -1));
		Log.i(tag, "VOLUME="+intent.getIntExtra("VOLUME", -1)+"  VIBRATE="+intent.getIntExtra("VIBRATE", -1));
		
		
		AudioManager audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		
		int VOLUME = intent.getIntExtra("VOLUME", -1);
		int VIBRATE = intent.getIntExtra("VIBRATE", -1);
		
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
        audio.setVibrateSetting(AudioManager.VIBRATE_TYPE_RINGER, AudioManager.VIBRATE_SETTING_ON);     
        audio.setVibrateSetting(AudioManager.VIBRATE_TYPE_NOTIFICATION, AudioManager.VIBRATE_SETTING_ON);
    }
  
    protected void ring(AudioManager audio) {
    	audio.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
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
