package com.android.iSchedule;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class watchEvent extends Activity{
	
	public TextView eventTitleTextView;
	public TextView eventPlaceTextView;
	public TextView createTimeTextView;
	public TextView startTimeTextView;
	public TextView endTimeTextView;
	public TextView contentTextView;
	public TextView modifyModeTextView;
	public String[] item = new String[] {"更改这个活动主题一致的所有活动","仅更改当前活动" };
	public Button backButton;
	public Button editButton;
	
	Event pickEvent ;
	
	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日  HH:mm:ss");
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		NotificationManager nManager = (NotificationManager)this.getSystemService(Context.NOTIFICATION_SERVICE);

		
		requestWindowFeature(Window.FEATURE_NO_TITLE); 
		super.onCreate(savedInstanceState);
		setContentView(R.layout.watch_event);
		
		Integer EventId;
		Bundle bundle = this.getIntent().getExtras();
		EventId = bundle.getInt("eventId");
		Log.i("watch", "" + this.getIntent().getIntExtra("eventId", -1));
		
		nManager.cancel(EventId);
		
		eventTitleTextView = (TextView)this.findViewById(R.id.eventTitle);
		eventPlaceTextView = (TextView)this.findViewById(R.id.eventPlace);
		createTimeTextView = (TextView)this.findViewById(R.id.eventCreateTime);
		startTimeTextView = (TextView)this.findViewById(R.id.eventStartTime);
		endTimeTextView = (TextView)this.findViewById(R.id.eventEndTime);
		contentTextView = (TextView)this.findViewById(R.id.eventContent);
		modifyModeTextView = (TextView)this.findViewById(R.id.modifyMode);
		
		
		backButton = (Button)this.findViewById(R.id.back);
		editButton = (Button)this.findViewById(R.id.edit);
		backButton.setOnClickListener(backOnclick);
		editButton.setOnClickListener(editOnclick);
		
		

		iScheduleDB dbHelper = new iScheduleDB(this);
		try {
			pickEvent = dbHelper.getEventById(EventId);
			String temp;
			temp = "活动主题:" + pickEvent.getTitle();
			eventTitleTextView.setText(temp);
			temp = "活动地点:" + pickEvent.getPlace();
			eventPlaceTextView.setText(temp);
			temp = "创建时间:" + dateFormat.format(pickEvent.getCreatTime());
			createTimeTextView.setText(temp);
			temp = "开始时间:" + dateFormat.format(pickEvent.getStartTime());
			startTimeTextView.setText(temp);
			temp = "结束时间:" + dateFormat.format(pickEvent.getEndTime());
			endTimeTextView.setText(temp);
			temp = "活动内容:" + pickEvent.getContent();
			contentTextView.setText(temp);
			temp = "情景模式：";
			Mode mode = new Mode("temp", 1, 1);
			mode = dbHelper.getModeByEventId(EventId);
			if(mode.getVibrate() == 1){
				temp += "振动" ;
			} else {
				temp += "不振动";
			}
			temp += " && ";
			if(mode.getVolume() == 1){
				temp += "响铃" ;
			} else {
				temp += "静音";
			}
			modifyModeTextView.setText(temp);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
	}

	public OnClickListener backOnclick = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			Intent intent=new Intent();
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    		intent.setClass(watchEvent.this,Main.class);
    		startActivity(intent);
			finish();
		}
	}; 
	
	public OnClickListener editOnclick = new OnClickListener() {
		
		@Override
		public void onClick(View v) {

			DialogInterface.OnClickListener onclick = new  DialogInterface.OnClickListener() {
		            
				public void onClick(DialogInterface dialog, int which) {
					Intent intent = new Intent();
					Bundle bundle = new Bundle();
					bundle.putInt("editOrNew", which);
					bundle.putInt("eventId", (int) pickEvent.getEventId());
					intent.putExtras(bundle);
					intent.setClass(watchEvent.this, AddEvent.class);
					startActivity(intent);
					finish();
				}
			};
				
				new AlertDialog.Builder(watchEvent.this).setTitle("编辑这个活动？请选择")
				.setIcon(android.R.drawable.ic_dialog_info)                
				.setSingleChoiceItems(item, 0, onclick).setNegativeButton("取消", null).show();
		}
	};
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.watch_event, menu);
		return true;
	}
	public boolean onOptionsItemSelected(MenuItem item)
	{
		if(item.getItemId() == R.id.Delete)
		{
			delEvent(watchEvent.this, (int)pickEvent.getEventId());
		}
		if(item.getItemId() == R.id.Edit)
		{
			DialogInterface.OnClickListener onclick = new  DialogInterface.OnClickListener() {
	            
				public void onClick(DialogInterface dialog, int which) {
					Intent intent = new Intent();
					Bundle bundle = new Bundle();
					bundle.putInt("editOrNew", which);
					bundle.putInt("eventId", (int) pickEvent.getEventId());
					intent.putExtras(bundle);
					intent.setClass(watchEvent.this, AddEvent.class);
					startActivity(intent);
				}
			};
				
				new AlertDialog.Builder(watchEvent.this).setTitle("编辑这个活动？请选择")
				.setIcon(android.R.drawable.ic_dialog_info)                
				.setSingleChoiceItems(this.item, 0, onclick).setNegativeButton("取消", null).show();
		}
		return true;
	}
	
	public void delEvent(Context context, final int eventId)  {
		final iScheduleDB dbHelper = new iScheduleDB(context);
		final Event event;
		String[] item = new String[] {"删除这个活动主题一致的所有活动","仅删除当前活动" };
		final Date curDate = new Date(System.currentTimeMillis());
		
		try {
			event = dbHelper.getEventById(eventId);
			
			//final Date date = new Date(dateFormat.parse(datePickButton.getText() + " 00:00:00").getTime());
			DialogInterface.OnClickListener onclick = new  DialogInterface.OnClickListener() {
	            
				public void onClick(DialogInterface dialog, int which) {
					
					if(which == 0){
						String title = event.getTitle();
						List<Event> list = new ArrayList<Event>();
						iScheduleDB helper = new iScheduleDB(watchEvent.this);
						try {
							list = dbHelper.getEventByTitle(title);
							//Log.d("test", Integer.toString(list.size()));
							for(int i = 0; i < list.size(); i++){
								dbHelper.deleteModify(list.get(i));
								
								Date eventStartDate = list.get(i).getStartTime();
								if(eventStartDate.getYear() ==  curDate.getYear() && 
										eventStartDate.getMonth() == curDate.getMonth() &&
												eventStartDate.getDate() == curDate.getDate()){
									if(System.currentTimeMillis() < eventStartDate.getTime()){
										cancelAlarmByEvent(watchEvent.this, (int)list.get(i).getEventId() * 2);
										if(list.get(i).getEndTime().getTime() < System.currentTimeMillis()){
											cancelAlarmByEvent(watchEvent.this, (int)list.get(i).getEventId() * 2 + 1);
										}	
									}
								}
							}
						} catch (ParseException e) {
							e.printStackTrace();
						}	
						dbHelper.deleteEventByTitle(title);
					}
					else if(which == 1){
						if(event.getStartTime().getYear() ==  curDate.getYear() && 
								event.getStartTime().getMonth() == curDate.getMonth() &&
										event.getStartTime().getDate() == curDate.getDate()){
							if(System.currentTimeMillis() < event.getStartTime().getTime()){
								cancelAlarmByEvent(watchEvent.this, (int)event.getEventId() * 2);
								if(event.getEndTime().getDay() == curDate.getDay()){
									cancelAlarmByEvent(watchEvent.this, (int)event.getEventId() * 2 + 1);
								}	
							}
						try {
							dbHelper.deleteModify(dbHelper.getEventById(eventId));
							dbHelper.deleteEventById(eventId);
						} catch (ParseException e) {
							e.printStackTrace();
						}
					}
					}
					Intent intent=new Intent();
		    		intent.setClass(watchEvent.this,Main.class);
		    		startActivity(intent);
					finish();
					dialog.dismiss();
				}
			};
			
			new AlertDialog.Builder(context).setTitle("真的要删除这个活动？")
			.setIcon(android.R.drawable.ic_dialog_info)                
			.setSingleChoiceItems(item, 0, onclick).setNegativeButton("取消", null).show();
			
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
	}

	private void cancelAlarmByEvent(Context context, int requestID){
		Log.i("canceling", "" + requestID);
		
		Intent intent = new Intent(context, ModifyReceiver.class);
		PendingIntent pIntent = PendingIntent.getBroadcast(context, (int)requestID, intent, 0);
		AlarmManager aManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
		aManager.cancel(pIntent);
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event){
		if(keyCode == KeyEvent.KEYCODE_BACK){
			Intent intent=new Intent();
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    		intent.setClass(watchEvent.this,Main.class);
    		startActivity(intent);
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}
}