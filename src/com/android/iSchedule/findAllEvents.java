package com.android.iSchedule;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.R.integer;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class findAllEvents extends Activity{
	
	ListView listView;
	AutoCompleteTextView inputTitle;
	Button selectAllButton;
	Button unSecectAllButton;
	Button deleteEventButton;
	Button backButton;
	iScheduleDB dbHelper = new iScheduleDB(this);
	SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
	SparseBooleanArray position = new SparseBooleanArray();
	List<Event> allEvents = new ArrayList<Event>();	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE); 
		super.onCreate(savedInstanceState);
		setContentView(R.layout.find_all_events);
		
		listView = (ListView)this.findViewById(R.id.eventList);
		selectAllButton = (Button)this.findViewById(R.id.selectAll);
		unSecectAllButton = (Button)this.findViewById(R.id.unSelectAll);
		deleteEventButton = (Button)this.findViewById(R.id.deleteEvent);
		inputTitle = (AutoCompleteTextView)this.findViewById(R.id.find_event_title);
		backButton = (Button)this.findViewById(R.id.back);
		
		try {
			allEvents = dbHelper.getALLEvent();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		List<String> eventTitle = new ArrayList<String>();
		Set<String> eventSet = new HashSet<String>();  
		for(int i =0; i< allEvents.size();i++){
			eventSet.add(allEvents.get(i).getTitle());
		}
		Iterator<String> it = eventSet.iterator();  
		while (it.hasNext()) {  
		  String str = it.next();  
		  eventTitle.add(str);
		}
		
		ArrayAdapter<String> eventTitleAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, eventTitle);
		inputTitle.setAdapter(eventTitleAdapter);
		inputTitle.setThreshold(1);
		inputTitle.addTextChangedListener(watcher);
		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		
		selectAllButton.setOnClickListener(selectAllOnclick);
		unSecectAllButton.setOnClickListener(unSecectAllOnclick);
		deleteEventButton.setOnClickListener(deleteOnclick);
		backButton.setOnClickListener(backOnclick);
		updateList("");
	}
	
	public TextWatcher watcher = new TextWatcher() {
		
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			 String eventTitleString = s.toString();
			 updateList(eventTitleString);
		}
		
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// nothing
		}
		
		@Override
		public void afterTextChanged(Editable s) {
			// nothing
		}
	};
	
	public void updateList(String eventsTitle) {
		if(allEvents.size() > 0){
			allEvents.clear();
		}
		try {
			if(eventsTitle.equals("")){
				allEvents = dbHelper.getALLEvent();
			} else {
				allEvents = dbHelper.getEventByTitle(eventsTitle);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		List<String> eventsList = new ArrayList<String>();
		
		for(int i = 0; i < allEvents.size(); i++)
		{
			Event event = allEvents.get(i);
			String text = "" ;
			text += event.getTitle() + "\n";
			text += formatter.format(event.getStartTime()) + "-" + formatter.format(event.getEndTime());
			eventsList.add(text);
		}
		ArrayAdapter<String> eventsAdapter = new ArrayAdapter<String>(findAllEvents.this, android.R.layout.simple_list_item_multiple_choice, eventsList);
		listView.setAdapter(eventsAdapter);
	}
	
	public OnClickListener selectAllOnclick = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			for(int i = 0; i < allEvents.size();i++){
				listView.setItemChecked(i, true);
			}
		}
	};
	
	public OnClickListener unSecectAllOnclick = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			for(int i = 0; i < allEvents.size();i++){
				listView.setItemChecked(i, false);
			}
		}
	};
	
	public OnClickListener backOnclick = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			Intent intent=new Intent();
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    		intent.setClass(findAllEvents.this,Main.class);
    		startActivity(intent);
			finish();
		}
	};
	public OnClickListener deleteOnclick = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			SparseBooleanArray checkEvent = new SparseBooleanArray();
			checkEvent = listView.getCheckedItemPositions();
			for(int i = 0; i < allEvents.size(); i++){
				if(checkEvent.get(i)){
					dbHelper.deleteEventById((int) allEvents.get(i).getEventId());
				}
				Log.d("checkEventsize", Boolean.toString(checkEvent.get(i)));
			}
			updateList(inputTitle.getText().toString());
		}
	};
	
	public boolean onKeyDown(int keyCode, KeyEvent event){
		if(keyCode == KeyEvent.KEYCODE_BACK){
			Intent intent=new Intent();
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    		intent.setClass(findAllEvents.this,Main.class);
    		startActivity(intent);
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.watch_event, menu);
		return true;
	}
}