package com.android.iSchedule;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class Main extends Activity {
	
	public ImageButton menuButton;
	public Button datePickButton;
	public ImageButton addEventButton;
	public ListView eventList;
	public List<Map<String, String>> events = new ArrayList<Map<String,String>>();
	iScheduleDB helper = new iScheduleDB(this);
	SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	Date curDate = new Date(System.currentTimeMillis());
	String curDateString = formatter.format(curDate);
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		menuButton = (ImageButton) this.findViewById(R.id.Menu);
		datePickButton = (Button) this.findViewById(R.id.datePick);
		addEventButton = (ImageButton) this.findViewById(R.id.addEvent);
		eventList = (ListView) this.findViewById(R.id.Event);
		
		SimpleAdapter adapter = new SimpleAdapter(this, events, R.layout.event_item
				,new String[]{"eid", "title", "beginTime", "endTime"}, new int []{R.id.event_id, R.id.event_title, 
				R.id.event_begin_time, R.id.event_end_time});
		eventList.setAdapter(adapter);
		
		// ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, item);
		// ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, item); 
		
		List<Mode> modes = new ArrayList<Mode>();
		modes = helper.getAllModes();
		datePickButton.setText(curDateString);
		
		addEventButton.setOnClickListener(addOnClick);

		Event e = new Event("hehe", "hehe", "hehe", curDate, curDate, curDate, curDate);

		helper.insert(e);
		
		Mode m = new Mode(1, 2);
		helper.insert(m);
		helper.insert(e, m);
		helper.deleteModify(e, m);
		
		// eventList.setAdapter(adapter);
		

	
		updateList();
		
	}
	
	public OnClickListener addOnClick = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO 自动生成的方法存根
			Intent intent=new Intent();
    		intent.setClass
    		(Main.this,AddEvent.class);
    		startActivity(intent);
		}
	};
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void updateList() {
		if(events.size() > 0){
			events.clear();
		}
		
		List<Event> list = new ArrayList<Event>();
		try {
			list = helper.getEventByDate(curDate);
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		for(int i = 0; i < list.size(); i++)
		{
			Map<String, String> map = new HashMap<String, String>();
			map.put("eid", Integer.toString((int) list.get(i).getEventId()));
			map.put("title", list.get(i).getTitle());
			map.put("beginTime", "开始时间："+formatter.format(list.get(i).getStartTime()));
			map.put("endTime", "结束时间："+formatter.format(list.get(i).getEndTime()));
			events.add(map);
		}
		
		((SimpleAdapter) eventList.getAdapter()).notifyDataSetChanged();
	}
}


