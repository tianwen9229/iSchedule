package com.android.iSchedule;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import android.media.AudioManager;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class AddEvent extends Activity {
	
	public Button backButton;
	public Button doneButton;
	public EditText eventTitleEditText;
	public EditText eventContentEditText;
	public Button fromDatePickerButton;
	public Button fromTimePickerButton;
	public Button toDatePickerButton;
	public Button toTimePickerButton;
	public Button endDatePickerButton;
	public Spinner frequencySpinner;
	public TextView frequencyResultText;
	public EditText eventPlaceEditText;
	public Spinner modifyModeSpinner;
	public TextView modifyResultText;
	public LinearLayout frequencyEndDateLayout;
	public TextView frequencyEndTextView;
	public iScheduleDB dbHelper = new iScheduleDB(this);
	
	public SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日");
	public SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
	public SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public Date pickDate = new Date(System.currentTimeMillis());;
	public Date curDate = new Date(System.currentTimeMillis());
	public int editOrNew;
	public int eventId;
	public Event event;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE); 
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_event);
		
		Bundle bundle = this.getIntent().getExtras();
		editOrNew = bundle.getInt("editOrNew");
		eventId = bundle.getInt("eventId");
		
		eventTitleEditText = (EditText) this.findViewById(R.id.editTitle);
		eventContentEditText = (EditText)this.findViewById(R.id.editContent);
		eventPlaceEditText = (EditText) this.findViewById(R.id.editPlace);
		frequencyResultText = (TextView) this.findViewById(R.id.frequencyResult);
		modifyResultText = (TextView)this.findViewById(R.id.modifyResult);
		frequencyEndTextView = (TextView) this.findViewById(R.id.repeatTime);
		frequencyEndDateLayout = (LinearLayout) this.findViewById(R.id.frequencyEndDate);
		frequencyEndDateLayout.setVisibility(8);
		frequencyEndTextView.setVisibility(8);
		Log.d("test", "pause2");
		// set Date and time
		fromDatePickerButton = (Button)this.findViewById(R.id.fromDatePicker);
		fromDatePickerButton.setText(dateFormat.format(curDate));
		fromDatePickerButton.setOnClickListener(fromDateOnClick);
		toDatePickerButton = (Button)this.findViewById(R.id.toDatePicker);
		toDatePickerButton.setText(dateFormat.format(curDate));
		toDatePickerButton.setOnClickListener(toDateOnClick);
		fromTimePickerButton = (Button)this.findViewById(R.id.fromTimePicker);
		fromTimePickerButton.setText(timeFormat.format(curDate));
		fromTimePickerButton.setOnClickListener(fromTimeOnClick);
		toTimePickerButton = (Button)this.findViewById(R.id.toTimePicker);
		toTimePickerButton.setText(timeFormat.format(curDate));
		toTimePickerButton.setOnClickListener(toTimeOnClick);
		
		
		Calendar newCalendar = new GregorianCalendar();
		newCalendar.setTime(curDate);
		newCalendar.add(Calendar.DATE, 1);
		Date nextDate = new Date(newCalendar.getTime().getTime());
		endDatePickerButton = (Button)this.findViewById(R.id.endDatePicker);
		endDatePickerButton.setText(dateFormat.format(nextDate));
		endDatePickerButton.setOnClickListener(endDateOnClick);

		//set frequency spinner
		frequencyResultText = (TextView)this.findViewById(R.id.frequencyResult);
		frequencySpinner = (Spinner)this.findViewById(R.id.frequencySpinner);
		Log.d("test", "pause3");
		String[] week = {"日", "一", "二", "三", "四", "五", "六"};
		String dayOfWeek = week[curDate.getDay()];
		String dayOfMonth = Integer.toString(curDate.getDate()) + "日";
		String dayOfYear = Integer.toString(curDate.getMonth()+1)+ "月" + Integer.toString(curDate.getDate()) + "日";
		String NoRepeat = "一次性活动";
		String EveryDay = "每天";
		String OnceAWeek = "每周(每周的星期"+dayOfWeek+")";
		String OnceAMonth = "每月(每月的"+dayOfMonth+")";
		String OnceAYear = "每年(每年的"+dayOfYear+")";
		String[] frequencyItems = new String[]{NoRepeat, EveryDay, OnceAWeek, 
			OnceAMonth, OnceAYear};
		ArrayAdapter<String> frequencySpinnerAdapter = new ArrayAdapter<String>(this,
				    android.R.layout.simple_spinner_item, frequencyItems);
		frequencySpinner.setAdapter(frequencySpinnerAdapter);

		frequencySpinner.setOnItemSelectedListener(frequencySpinnerOnItemSelect);
		Log.d("test", "pause4");
		//set modify spinner
		modifyModeSpinner = (Spinner) this.findViewById(R.id.modifyModeSpinner);
		modifyResultText = (TextView) this.findViewById(R.id.modifyResult);
		Log.d("test", "pause5");
		List<Mode> list = dbHelper.getAllModes();
		List<String> modeName = new ArrayList<String>();
		for(int i = 1; i < list.size(); i++){
			modeName.add(list.get(i).getName());
		}
		Log.d("test", Integer.toString(list.size()));
		ArrayAdapter<String> modifySpinnerAdapter = new ArrayAdapter<String>(this,
			    android.R.layout.simple_spinner_item, modeName);
		modifyModeSpinner.setAdapter(modifySpinnerAdapter);
		modifyModeSpinner.setOnItemSelectedListener(modifySpinnerOnItemSelect);
		
		if(editOrNew != -1){
			try {
				event = dbHelper.getEventById(eventId);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			eventTitleEditText.setText(event.getTitle());
			eventContentEditText.setText(event.getContent());
			fromDatePickerButton.setText(dateFormat.format(event.getStartTime()));
			fromTimePickerButton.setText(timeFormat.format(event.getStartTime()));
			toDatePickerButton.setText(dateFormat.format(event.getEndTime()));
			toTimePickerButton.setText(timeFormat.format(event.getEndTime()));
			
			eventPlaceEditText.setText(event.getPlace());
			int modifyMode = (int) dbHelper.getModeByEventId(Integer.valueOf(eventId)).getModeId();
			modifyModeSpinner.setSelection(modifyMode - 2);
		}
		
		
		
		
		//back button
		backButton = (Button) this.findViewById(R.id.back);
		backButton.setOnClickListener(backButtonOnClick);
		
		//done button
		doneButton = (Button) this.findViewById(R.id.done);
		doneButton.setOnClickListener(doneButtonOnClick);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	
	DatePickerDialog.OnDateSetListener fromDatePicker = new DatePickerDialog.OnDateSetListener(){ 

		@Override 
		public void onDateSet(DatePicker view, int year, int monthOfYear,int dayOfMonth) { 
			pickDate.setYear(year - 1900);
			pickDate.setMonth(monthOfYear);
			pickDate.setDate(dayOfMonth);
			fromDatePickerButton.setText(dateFormat.format(pickDate));
		}
	}; 
	
	DatePickerDialog.OnDateSetListener toDatePicker = new DatePickerDialog.OnDateSetListener(){ 

		@Override 
		public void onDateSet(DatePicker view, int year, int monthOfYear,int dayOfMonth) { 
			pickDate.setYear(year - 1900);
			pickDate.setMonth(monthOfYear);
			pickDate.setDate(dayOfMonth);
			toDatePickerButton.setText(dateFormat.format(pickDate));
		}
	}; 
	
	TimePickerDialog.OnTimeSetListener fromTimePicker = new TimePickerDialog.OnTimeSetListener() {
		
		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			java.util.Date newDate = new java.util.Date(System.currentTimeMillis());
			newDate.setHours(hourOfDay);
			newDate.setMinutes(minute);
			pickDate.setTime(newDate.getTime());
			fromTimePickerButton.setText(timeFormat.format(pickDate));
		}
	};
	
	TimePickerDialog.OnTimeSetListener toTimePicker = new TimePickerDialog.OnTimeSetListener() {
		
		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			java.util.Date newDate = new java.util.Date(System.currentTimeMillis());
			newDate.setHours(hourOfDay);
			newDate.setMinutes(minute);
			pickDate.setTime(newDate.getTime());
			toTimePickerButton.setText(timeFormat.format(pickDate));
		}
	};
	
	public OnClickListener fromDateOnClick = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			try {
				Date date = new Date(dateFormat.parse(fromDatePickerButton.getText()+" 00:00:00").getTime());
				new DatePickerDialog(AddEvent.this, fromDatePicker,date.getYear() + 1900, date.getMonth(), date.getDate()).show();
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
	};
	
	public OnClickListener toDateOnClick = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			try {
				Date date = new Date(dateFormat.parse(toDatePickerButton.getText()+" 00:00:00").getTime());
				new DatePickerDialog(AddEvent.this, toDatePicker, date.getYear() + 1900, date.getMonth(), date.getDate()).show();
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
	};

	public OnClickListener fromTimeOnClick = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			java.util.Date newDate;
			try {
				newDate = new java.util.Date(format.parse("2013-01-01 " + fromTimePickerButton.getText() + ":00").getTime());
				new TimePickerDialog(AddEvent.this, fromTimePicker, newDate.getHours(), newDate.getMinutes(), true).show();
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
	};
	
	public OnClickListener toTimeOnClick = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			java.util.Date newDate;
			try {
				newDate = new java.util.Date(format.parse("2013-01-01 " + toTimePickerButton.getText() + ":00").getTime());
				new TimePickerDialog(AddEvent.this, toTimePicker, newDate.getHours(), newDate.getMinutes(), true).show();
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
	};
	
	DatePickerDialog.OnDateSetListener endDatePicker = new DatePickerDialog.OnDateSetListener(){ 

		@Override 
		public void onDateSet(DatePicker view, int year, int monthOfYear,int dayOfMonth) { 
			pickDate.setYear(year - 1900);
			pickDate.setMonth(monthOfYear);
			pickDate.setDate(dayOfMonth);
			endDatePickerButton.setText(dateFormat.format(pickDate));
		}
	}; 
	
	public OnClickListener endDateOnClick = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			try {
				Date date = new Date(dateFormat.parse(endDatePickerButton.getText()+" 00:00:00").getTime());
				new DatePickerDialog(AddEvent.this, endDatePicker,date.getYear() + 1900, date.getMonth(), date.getDate()).show();
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
	};
	
	public OnItemSelectedListener frequencySpinnerOnItemSelect = new OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			frequencyResultText.setText(Integer.toString(arg2));
			if(arg2 != 0){
				frequencyEndDateLayout.setVisibility(0);
				frequencyEndTextView.setVisibility(0);
			}
			else {
				frequencyEndDateLayout.setVisibility(8);
				frequencyEndTextView.setVisibility(8);
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {}
	};
	
	public OnItemSelectedListener modifySpinnerOnItemSelect = new OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			modifyResultText.setText(Integer.toString(arg2));
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {}
	};
	
	public OnClickListener backButtonOnClick = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			Intent intent=new Intent();
    		intent.setClass(AddEvent.this,Main.class);
    		startActivity(intent);
			finish();
		}
	};
	
	public OnClickListener doneButtonOnClick = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			Intent intent=new Intent();
    		intent.setClass(AddEvent.this,Main.class);
    		
    		if(editOrNew == 0){
    			String title = event.getTitle();
				List<Event> list = new ArrayList<Event>();
				try {
					list = dbHelper.getEventByTitle(title);
					for(int i = 0; i < list.size(); i++){
						dbHelper.deleteModify(list.get(i));
					}
				} catch (ParseException e) {
					e.printStackTrace();
				}	
				dbHelper.deleteEventByTitle(title);
    		}
    		else if(editOrNew == 1){
    			dbHelper.deleteModify(event);
    			dbHelper.deleteEventById(eventId);
    		}    		
    		
			String eventTitle;
			String eventContent;
			String eventPlace;
			int frequency;
			int frequencyTime;
			Date beginDate;
			Date endDate;
			Date now = new Date(System.currentTimeMillis());
			int modifyMode;
			
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
			
			if(eventTitleEditText.getText().toString().equals("")){
				Toast.makeText(AddEvent.this, "还没填事件主题哦~~", Toast.LENGTH_SHORT).show();
				eventTitleEditText.requestFocus();
				return;
			}
			else {
				eventTitle = eventTitleEditText.getText().toString();
			}
			
			eventContent = eventContentEditText.getText().toString();
			eventPlace = eventPlaceEditText.getText().toString();
			frequency = Integer.parseInt(frequencyResultText.getText().toString());
			
			
			modifyMode = 2 + Integer.parseInt(modifyResultText.getText().toString());
			
			String beginString = fromDatePickerButton.getText() + " " + fromTimePickerButton.getText() + ":00";
			String endString = toDatePickerButton.getText() + " " + toTimePickerButton.getText() + ":00";
			int result = beginString.compareTo(endString);
			if(result > 0){
				Toast.makeText(AddEvent.this, "别逗了，开始怎么会在结束后呢o(╯□╰)o\n快快去修改吧~~", Toast.LENGTH_SHORT).show();
				return;
			}
			String frequencyEndString = endDatePickerButton.getText() + " 00:00:00";
			result = endString.compareTo(frequencyEndString);
			if(result > 0){
				Toast.makeText(AddEvent.this, "别逗了，重复结束时间怎么会在结束前呢(╯□╰)\n快快去修改吧~~", Toast.LENGTH_SHORT).show();
				return;
			}
			
    		startActivity(intent);
    		
			try {
				Mode mode = dbHelper.getModeById(modifyMode);
				beginDate = new Date(dateFormat.parse(fromDatePickerButton.getText() + " " + fromTimePickerButton.getText() + ":00").getTime());
				endDate = new Date(dateFormat.parse(toDatePickerButton.getText() + " " + toTimePickerButton.getText() + ":00").getTime());
				Date frequencyEndDate = new Date(dateFormat.parse(endDatePickerButton.getText() + " 00:00:00").getTime());
				
				Calendar curBeginCalendar = new GregorianCalendar();
				curBeginCalendar.setTime(beginDate);
				Calendar curEndCalendar = new GregorianCalendar();
				curEndCalendar.setTime(endDate);
				
				if(frequency == 0){
					Event newEvent = new Event(eventTitle, eventPlace, eventContent, now, now, beginDate, endDate);
					dbHelper.insert(newEvent);
					dbHelper.insert(newEvent, mode);

					//若新增的闹钟与今天相关，立刻设置闹钟
					AudioManager audio = (AudioManager) AddEvent.this.getSystemService(Context.AUDIO_SERVICE);
					AlarmManager aManager;
					aManager  = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
					Mode curMode = dbHelper.getModeById(1);
						
					if(newEvent.getStartTime().getDay() == curDate.getDay()){
						Log.i("AddEvent", "modifying=======================================================");
						Log.i("AddEvent", "MID=" + (int)(newEvent.getEventId() * 2));
						Log.i("AddEvent", format.format(newEvent.getStartTime()));
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
						dbHelper.updateModeById(curMode);
						
						Intent eIntent = new Intent(AddEvent.this, ModifyReceiver.class);
						eIntent.putExtra("VOLUME", dbHelper.getModeByEventId((int) newEvent.getEventId()).getVolume());
						eIntent.putExtra("VIBRATE", dbHelper.getModeByEventId((int) newEvent.getEventId()).getVibrate());
						eIntent.putExtra("MID", (int)newEvent.getEventId() * 2);
						
						PendingIntent ePendingIntent = PendingIntent.getBroadcast(AddEvent.this, (int)(newEvent.getEventId() * 2), eIntent, 0);
						
						aManager.set(AlarmManager.RTC_WAKEUP, newEvent.getStartTime().getTime(), ePendingIntent);
					}
					if(newEvent.getEndTime().getDay() == curDate.getDay()){
						Log.i("AddEvent", "resuming=======================================================");
						Log.i("AddEvent", "MID=" + (int)(newEvent.getEventId() * 2 + 1));
						Log.i("AddEvent", format.format(newEvent.getEndTime()));
						
						Intent eIntent = new Intent(AddEvent.this, ModifyReceiver.class); 

						eIntent.putExtra("VOLUME", dbHelper.getModeById(1).getVolume());
						eIntent.putExtra("VIBRATE", dbHelper.getModeById(1).getVibrate());
						eIntent.putExtra("MID", (int)newEvent.getEventId() * 2 + 1);
							
						PendingIntent ePendingIntent = PendingIntent.getBroadcast(AddEvent.this, (int)(newEvent.getEventId() * 2 + 1), eIntent, 0);
							
						aManager.set(AlarmManager.RTC_WAKEUP, newEvent.getEndTime().getTime(), ePendingIntent);
					}
				}				
				else if(frequency == 1){
					Date nextDate = new Date(curBeginCalendar.getTime().getTime());
					while(! nextDate.after(frequencyEndDate)){
						beginDate.setTime(curBeginCalendar.getTime().getTime());
						endDate.setTime(curEndCalendar.getTime().getTime());
						Event newEvent = new Event(eventTitle, eventPlace, eventContent, now, now, beginDate, endDate);
						dbHelper.insert(newEvent);
						dbHelper.insert(newEvent, mode);

						//若新增的闹钟与今天相关，立刻设置闹钟
						AudioManager audio = (AudioManager) AddEvent.this.getSystemService(Context.AUDIO_SERVICE);
						AlarmManager aManager;
						aManager  = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
						Mode curMode = dbHelper.getModeById(1);
						
						if(newEvent.getStartTime().getDay() == curDate.getDay()){
							Log.i("AddEvent", "modifying=======================================================");
							Log.i("AddEvent", "MID=" + (int)(newEvent.getEventId() * 2));
							Log.i("AddEvent", format.format(newEvent.getStartTime()));
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
							dbHelper.updateModeById(curMode);
							
							Intent eIntent = new Intent(AddEvent.this, ModifyReceiver.class);
							eIntent.putExtra("VOLUME", dbHelper.getModeByEventId((int) newEvent.getEventId()).getVolume());
							eIntent.putExtra("VIBRATE", dbHelper.getModeByEventId((int) newEvent.getEventId()).getVibrate());
							eIntent.putExtra("MID", (int)newEvent.getEventId() * 2);
							
							PendingIntent ePendingIntent = PendingIntent.getBroadcast(AddEvent.this, (int)(newEvent.getEventId() * 2), eIntent, 0);
							
							aManager.set(AlarmManager.RTC_WAKEUP, newEvent.getStartTime().getTime(), ePendingIntent);
						}
						if(newEvent.getEndTime().getDay() == curDate.getDay()){
							Log.i("AddEvent", "resuming=======================================================");
							Log.i("AddEvent", "MID=" + (int)(newEvent.getEventId() * 2 + 1));
							Log.i("AddEvent", format.format(newEvent.getEndTime()));
							
							Intent eIntent = new Intent(AddEvent.this, ModifyReceiver.class); 

							eIntent.putExtra("VOLUME", dbHelper.getModeById(1).getVolume());
							eIntent.putExtra("VIBRATE", dbHelper.getModeById(1).getVibrate());
							eIntent.putExtra("MID", (int)newEvent.getEventId() * 2 + 1);
							
							PendingIntent ePendingIntent = PendingIntent.getBroadcast(AddEvent.this, (int)(newEvent.getEventId() * 2 + 1), eIntent, 0);
							
							aManager.set(AlarmManager.RTC_WAKEUP, newEvent.getEndTime().getTime(), ePendingIntent);
						}	
						
						
						curBeginCalendar.add(Calendar.DATE, 1);	
						curEndCalendar.add(Calendar.DATE, 1);
						nextDate.setTime(curBeginCalendar.getTime().getTime());
					}

				}
				
				else if(frequency == 2){
					Date nextDate = new Date(curBeginCalendar.getTime().getTime());
					while(! nextDate.after(frequencyEndDate)){
						beginDate.setTime(curBeginCalendar.getTime().getTime());
						endDate.setTime(curEndCalendar.getTime().getTime());
						Event newEvent = new Event(eventTitle, eventPlace, eventContent, now, now, beginDate, endDate);
						dbHelper.insert(newEvent);
						dbHelper.insert(newEvent, mode);

						//若新增的闹钟与今天相关，立刻设置闹钟
						AudioManager audio = (AudioManager) AddEvent.this.getSystemService(Context.AUDIO_SERVICE);
						AlarmManager aManager;
						aManager  = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
						Mode curMode = dbHelper.getModeById(1);
						
						if(newEvent.getStartTime().getDay() == curDate.getDay()){
							Log.i("AddEvent", "modifying=======================================================");
							Log.i("AddEvent", "MID=" + (int)(newEvent.getEventId() * 2));
							Log.i("AddEvent", format.format(newEvent.getStartTime()));
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
							dbHelper.updateModeById(curMode);
							
							Intent eIntent = new Intent(AddEvent.this, ModifyReceiver.class);
							eIntent.putExtra("VOLUME", dbHelper.getModeByEventId((int) newEvent.getEventId()).getVolume());
							eIntent.putExtra("VIBRATE", dbHelper.getModeByEventId((int) newEvent.getEventId()).getVibrate());
							eIntent.putExtra("MID", (int)newEvent.getEventId() * 2);
							
							PendingIntent ePendingIntent = PendingIntent.getBroadcast(AddEvent.this, (int)(newEvent.getEventId() * 2), eIntent, 0);
							
							aManager.set(AlarmManager.RTC_WAKEUP, newEvent.getStartTime().getTime(), ePendingIntent);
						}
						if(newEvent.getEndTime().getDay() == curDate.getDay()){
							Log.i("AddEvent", "resuming=======================================================");
							Log.i("AddEvent", "MID=" + (int)(newEvent.getEventId() * 2 + 1));
							Log.i("AddEvent", format.format(newEvent.getEndTime()));
							
							Intent eIntent = new Intent(AddEvent.this, ModifyReceiver.class); 

							eIntent.putExtra("VOLUME", dbHelper.getModeById(1).getVolume());
							eIntent.putExtra("VIBRATE", dbHelper.getModeById(1).getVibrate());
							eIntent.putExtra("MID", (int)newEvent.getEventId() * 2 + 1);
							
							PendingIntent ePendingIntent = PendingIntent.getBroadcast(AddEvent.this, (int)(newEvent.getEventId() * 2 + 1), eIntent, 0);
							
							aManager.set(AlarmManager.RTC_WAKEUP, newEvent.getEndTime().getTime(), ePendingIntent);
						}	
						
						
						curBeginCalendar.add(Calendar.DATE, 7);	
						curEndCalendar.add(Calendar.DATE, 7);
						nextDate.setTime(curBeginCalendar.getTime().getTime());
					}
				}
				
				else if(frequency == 3){
					Date nextDate = new Date(curBeginCalendar.getTime().getTime());
					while(! nextDate.after(frequencyEndDate)){
						beginDate.setTime(curBeginCalendar.getTime().getTime());
						endDate.setTime(curEndCalendar.getTime().getTime());
						Event newEvent = new Event(eventTitle, eventPlace, eventContent, now, now, beginDate, endDate);
						dbHelper.insert(newEvent);
						dbHelper.insert(newEvent, mode);

						//若新增的闹钟与今天相关，立刻设置闹钟
						AudioManager audio = (AudioManager) AddEvent.this.getSystemService(Context.AUDIO_SERVICE);
						AlarmManager aManager;
						aManager  = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
						Mode curMode = dbHelper.getModeById(1);
						
						if(newEvent.getStartTime().getDay() == curDate.getDay()){
							Log.i("AddEvent", "modifying=======================================================");
							Log.i("AddEvent", "MID=" + (int)(newEvent.getEventId() * 2));
							Log.i("AddEvent", format.format(newEvent.getStartTime()));
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
							dbHelper.updateModeById(curMode);
							
							Intent eIntent = new Intent(AddEvent.this, ModifyReceiver.class);
							eIntent.putExtra("VOLUME", dbHelper.getModeByEventId((int) newEvent.getEventId()).getVolume());
							eIntent.putExtra("VIBRATE", dbHelper.getModeByEventId((int) newEvent.getEventId()).getVibrate());
							eIntent.putExtra("MID", (int)newEvent.getEventId() * 2);
							
							PendingIntent ePendingIntent = PendingIntent.getBroadcast(AddEvent.this, (int)(newEvent.getEventId() * 2), eIntent, 0);
							
							aManager.set(AlarmManager.RTC_WAKEUP, newEvent.getStartTime().getTime(), ePendingIntent);
						}
						if(newEvent.getEndTime().getDay() == curDate.getDay()){
							Log.i("AddEvent", "resuming=======================================================");
							Log.i("AddEvent", "MID=" + (int)(newEvent.getEventId() * 2 + 1));
							Log.i("AddEvent", format.format(newEvent.getEndTime()));
							
							Intent eIntent = new Intent(AddEvent.this, ModifyReceiver.class); 

							eIntent.putExtra("VOLUME", dbHelper.getModeById(1).getVolume());
							eIntent.putExtra("VIBRATE", dbHelper.getModeById(1).getVibrate());
							eIntent.putExtra("MID", (int)newEvent.getEventId() * 2 + 1);
							
							PendingIntent ePendingIntent = PendingIntent.getBroadcast(AddEvent.this, (int)(newEvent.getEventId() * 2 + 1), eIntent, 0);
							
							aManager.set(AlarmManager.RTC_WAKEUP, newEvent.getEndTime().getTime(), ePendingIntent);
						}	
						
						
						curBeginCalendar.add(Calendar.MONTH, 1);	
						curEndCalendar.add(Calendar.MONTH, 1);
						nextDate.setTime(curBeginCalendar.getTime().getTime());
					}
				}

				else if(frequency == 4){
					Date nextDate = new Date(curBeginCalendar.getTime().getTime());
					while(! nextDate.after(frequencyEndDate)){
						beginDate.setTime(curBeginCalendar.getTime().getTime());
						endDate.setTime(curEndCalendar.getTime().getTime());
						Event newEvent = new Event(eventTitle, eventPlace, eventContent, now, now, beginDate, endDate);
						dbHelper.insert(newEvent);
						dbHelper.insert(newEvent, mode);

						//若新增的闹钟与今天相关，立刻设置闹钟
						AudioManager audio = (AudioManager) AddEvent.this.getSystemService(Context.AUDIO_SERVICE);
						AlarmManager aManager;
						aManager  = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
						Mode curMode = dbHelper.getModeById(1);
						
						if(newEvent.getStartTime().getDay() == curDate.getDay()){
							Log.i("AddEvent", "modifying=======================================================");
							Log.i("AddEvent", "MID=" + (int)(newEvent.getEventId() * 2));
							Log.i("AddEvent", format.format(newEvent.getStartTime()));
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
							dbHelper.updateModeById(curMode);
							
							Intent eIntent = new Intent(AddEvent.this, ModifyReceiver.class);
							eIntent.putExtra("VOLUME", dbHelper.getModeByEventId((int) newEvent.getEventId()).getVolume());
							eIntent.putExtra("VIBRATE", dbHelper.getModeByEventId((int) newEvent.getEventId()).getVibrate());
							eIntent.putExtra("MID", (int)newEvent.getEventId() * 2);
							
							PendingIntent ePendingIntent = PendingIntent.getBroadcast(AddEvent.this, (int)(newEvent.getEventId() * 2), eIntent, 0);
							
							aManager.set(AlarmManager.RTC_WAKEUP, newEvent.getStartTime().getTime(), ePendingIntent);
						}
						if(newEvent.getEndTime().getDay() == curDate.getDay()){
							Log.i("AddEvent", "resuming=======================================================");
							Log.i("AddEvent", "MID=" + (int)(newEvent.getEventId() * 2 + 1));
							Log.i("AddEvent", format.format(newEvent.getEndTime()));
							
							Intent eIntent = new Intent(AddEvent.this, ModifyReceiver.class); 

							eIntent.putExtra("VOLUME", dbHelper.getModeById(1).getVolume());
							eIntent.putExtra("VIBRATE", dbHelper.getModeById(1).getVibrate());
							eIntent.putExtra("MID", (int)newEvent.getEventId() * 2 + 1);
							
							PendingIntent ePendingIntent = PendingIntent.getBroadcast(AddEvent.this, (int)(newEvent.getEventId() * 2 + 1), eIntent, 0);
							
							aManager.set(AlarmManager.RTC_WAKEUP, newEvent.getEndTime().getTime(), ePendingIntent);
						}	
						
						
						curBeginCalendar.add(Calendar.YEAR, 1);	
						curEndCalendar.add(Calendar.YEAR, 1);
						nextDate.setTime(curBeginCalendar.getTime().getTime());
					}
				}
				
				else {
					Toast.makeText(AddEvent.this, "ERROR!!!", Toast.LENGTH_LONG).show();
				}
				
				Toast.makeText(AddEvent.this, "事件"+ eventTitle +"已经添加~\\(^o^)/~", Toast.LENGTH_SHORT).show();
				finish();
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
	};
}
