package com.android.iSchedule;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class watchEvent extends Activity{
	
	TextView eventTitleTextView;
	TextView eventPlaceTextView;
	TextView createTimeTextView;
	TextView startTimeTextView;
	TextView endTimeTextView;
	TextView contentTextView;
	
	Button backButton;
	Button editButton;
	
	Event pickEvent ;
	
	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日  HH:mm:ss");
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE); 
		super.onCreate(savedInstanceState);
		setContentView(R.layout.watch_event);
		
		Integer EventId;
		Bundle bundle = this.getIntent().getExtras();
		EventId = bundle.getInt("eventId");
		
		eventTitleTextView = (TextView)this.findViewById(R.id.eventTitle);
		eventPlaceTextView = (TextView)this.findViewById(R.id.eventPlace);
		createTimeTextView = (TextView)this.findViewById(R.id.eventCreateTime);
		startTimeTextView = (TextView)this.findViewById(R.id.eventStartTime);
		endTimeTextView = (TextView)this.findViewById(R.id.eventEndTime);
		contentTextView = (TextView)this.findViewById(R.id.eventContent);
		
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
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
	}

	public OnClickListener backOnclick = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			finish();
		}
	}; 
	
	public OnClickListener editOnclick = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			
			String[] item = new String[] {"更改这个活动主题一致的所有活动","仅更改当前活动" };

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
				.setSingleChoiceItems(item, 0, onclick).setNegativeButton("取消", null).show();
		}
	};
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
}