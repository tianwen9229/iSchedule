package com.android.iSchedule;

import java.security.PublicKey;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.R.integer;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

public class AddEvent extends Activity {
	
	public Button backButton;
	public Button doneButton;
	public EditText eventTitleEditText;
	public EditText eventContentEditText;
	public Button fromDatePickerButton;
	public Button fromTimePickerButton;
	public Button toDatePickerButton;
	public Button toTimePickerButton;
	public Spinner frequencySpinner;
	public TextView frequencyResultText;
	public EditText eventPlaceEditText;
	public Spinner modifyModeSpinner;
	public TextView modifyResultText;
	
	public iScheduleDB dbHelper = new iScheduleDB(this);
	
	public SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日");
	public SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
	public SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public Date pickDate = new Date(System.currentTimeMillis());;
	public Date curDate = new Date(System.currentTimeMillis());
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_event);
		
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
		
		//set frequency spinner
		frequencyResultText = (TextView)this.findViewById(R.id.frequencyResult);
		frequencySpinner = (Spinner)this.findViewById(R.id.frequencySpinner);
		String[] week = {"一", "二", "三", "四", "五", "六", "日"};
		String dayOfWeek = week[curDate.getDay()-1];
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
		
		//set modify spinner
		modifyModeSpinner = (Spinner) this.findViewById(R.id.modifyModeSpinner);
		modifyResultText = (TextView) this.findViewById(R.id.modifyResult);
		
		List<Mode> list = dbHelper.getAllModes();
		List<String> modeName = new ArrayList<String>();
		for(int i = 0; i < list.size(); i++){
			modeName.add(list.get(i).getName());
		}
		Log.d("test", Integer.toString(list.size()));
		ArrayAdapter<String> modifySpinnerAdapter = new ArrayAdapter<String>(this,
			    android.R.layout.simple_spinner_item, modeName);
		modifyModeSpinner.setAdapter(modifySpinnerAdapter);
		modifyModeSpinner.setOnItemSelectedListener(modifySpinnerOnItemSelect);
		
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
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
	
	public OnItemSelectedListener frequencySpinnerOnItemSelect = new OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			frequencyResultText.setText(Integer.toString(arg2));
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
}
