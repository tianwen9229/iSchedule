package com.android.iSchedule;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class Main extends Activity {
    //
	AlarmManager diary_alarm;
	
	public ImageButton menuButton;
	public Button datePickButton;
	public ImageButton findEventButton;
	public ImageButton addEventButton;
	public ListView eventList;
	public List<Map<String, String>> events = new ArrayList<Map<String,String>>();
	iScheduleDB helper = new iScheduleDB(this);
	SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日");
	Date curDate = new Date(System.currentTimeMillis());
	Date pickDate = new Date(System.currentTimeMillis());
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE); 
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);	
		String tag = "Main";
		
		menuButton = (ImageButton) this.findViewById(R.id.Menu);
		datePickButton = (Button) this.findViewById(R.id.datePick);
		addEventButton = (ImageButton) this.findViewById(R.id.addEvent);
		findEventButton = (ImageButton) this.findViewById(R.id.findEvent);
		eventList = (ListView) this.findViewById(R.id.Event);

		SimpleAdapter adapter = new SimpleAdapter(this, events, R.layout.event_item
				,new String[]{"eid", "title", "beginTime", "endTime"}, new int []{R.id.event_id, R.id.event_title, 
				R.id.event_begin_time, R.id.event_end_time});
		eventList.setAdapter(adapter);
		eventList.setOnItemClickListener(itemClick);
		eventList.setOnItemLongClickListener(longClick);
    	updateList(curDate);
		List<Mode> modes = new ArrayList<Mode>();
	    modes = helper.getAllModes();
	    if(modes.size() == 0){
	    	Mode curMode = new Mode("curMode", 1, 0);
	    	Mode mode_1 = new Mode("振动响铃", 1, 1);
	    	Mode mode_2 = new Mode("响铃", 1, 0);
	    	Mode mode_3 = new Mode("振动", 0, 1);
	    	Mode mode_4 = new Mode("静音", 0, 0);
	    	helper.insert(curMode);
	    	helper.insert(mode_1);
	    	helper.insert(mode_2);
	    	helper.insert(mode_3);
	    	helper.insert(mode_4);
	    }
		
		
		datePickButton.setText(dateFormat.format(curDate));
		datePickButton.setOnClickListener(DatePickOnClick);
		addEventButton.setOnClickListener(addOnClick);
		findEventButton.setOnClickListener(findOnClick);
		menuButton.setOnClickListener(menuOnClick);
		
		//闹钟管理
		diary_alarm  = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(this, DiaryReceiver.class);
		PendingIntent senderPI = PendingIntent.getBroadcast(this, 0, intent, 0);
    	diary_alarm.setRepeating(AlarmManager.RTC_WAKEUP,  getTime(), 24 * 60 * 60 * 1000, senderPI);
	 
	}
	public OnItemClickListener itemClick = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			try {
				Date date = new Date(dateFormat.parse(datePickButton.getText() + " 00:00:00").getTime());
				List<Event> list = new ArrayList<Event>();
				list = helper.getEventByDate(date);
				
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				bundle.putInt("eventId", (int)list.get(arg2).getEventId());
				intent.putExtras(bundle);
				intent.setClass(Main.this, watchEvent.class);
				startActivity(intent);
				finish();
			} catch (ParseException e) {
				e.printStackTrace();
			}	
		}
	};
	
	public OnItemLongClickListener longClick = new OnItemLongClickListener() {

		@Override
		public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
				int arg2, long arg3) {
			 try {
				Date date = new Date(dateFormat.parse(datePickButton.getText() + " 00:00:00").getTime());
				List<Event> list = new ArrayList<Event>();
				list = helper.getEventByDate(date);
				delEvent(Main.this, (int)list.get(arg2).getEventId());
			} catch (ParseException e) {
				e.printStackTrace();
			}
			return false;
		}
	};
	public OnClickListener menuOnClick = new OnClickListener()	
	{
		@Override
		public void onClick(View v) {
			openOptionsMenu();
		}
	};



	public OnClickListener addOnClick = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			Intent intent = new Intent();
			Bundle bundle = new Bundle();
			bundle.putInt("editOrNew", -1);
			bundle.putInt("eventId", -1);
			intent.putExtras(bundle);
			intent.setClass(Main.this, AddEvent.class);
			startActivity(intent);
    		finish();
		}
	};
	public OnClickListener findOnClick = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			Intent intent = new Intent();
			intent.setClass(Main.this, findAllEvents.class);
			startActivity(intent);
    		finish();
		}
	};
	public void updateList(Date date) {
		if(events.size() > 0){
			events.clear();
		}
		
		List<Event> list = new ArrayList<Event>();
		try {
			list = helper.getEventByDate(date);
			if(list.size() == 0)
				eventList.setBackgroundResource(R.drawable.nothing);
			else eventList.setBackgroundColor(0);
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
	private long getTime() {
    	Date dateNow = new Date(System.currentTimeMillis());
    	java.util.Date dateNow2 = new java.util.Date (dateNow.getTime());
    	long hour = 24 - dateNow2.getHours();
    	long min = 0 - dateNow2.getMinutes();
    	long second = dateNow2.getSeconds();
    	return (dateNow2.getTime() + (hour*60 + min)*60*1000 - second*1000);
    }
	
	
	DatePickerDialog.OnDateSetListener DatePicker = new DatePickerDialog.OnDateSetListener(){ 

		@Override 
		public void onDateSet(DatePicker view, int year, int monthOfYear,int dayOfMonth) { 
			pickDate.setYear(year - 1900);
			pickDate.setMonth(monthOfYear);
			pickDate.setDate(dayOfMonth);
			datePickButton.setText(dateFormat.format(pickDate));
			updateList(pickDate);
		}
	}; 
	
	public OnClickListener DatePickOnClick = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			try {
				Date date = new Date(dateFormat.parse(datePickButton.getText()+" 00:00:00").getTime());
				new DatePickerDialog(Main.this, DatePicker,date.getYear() + 1900, date.getMonth(), date.getDate()).show();
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
	};
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		if(item.getItemId() == R.id.add_event)
		{		
			Intent intent = new Intent();
			Bundle bundle = new Bundle();
			bundle.putInt("editOrNew", -1);
			bundle.putInt("eventId", -1);
			intent.putExtras(bundle);
			intent.setClass(Main.this, AddEvent.class);
			startActivity(intent);
    		finish();
		}
		if(item.getItemId() == R.id.today)
		{
			updateList(curDate);
			datePickButton.setText(dateFormat.format(curDate));
		}
		if(item.getItemId() == R.id.feedback)
		{
			Dialog dialog = new AlertDialog.Builder(Main.this)
			.setTitle("反馈")
			.setMessage("联系我们:\nQQ:1206418761\nEmail:1206418761@qq.com\n电话：18666832643")
			.setPositiveButton("返回", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					dialog.cancel();
				}
			}).create();
			dialog.show();
		}
		if(item.getItemId() == R.id.about)
		{
			Dialog dialog = new AlertDialog.Builder(Main.this)
			.setTitle("关于")
			.setMessage("iSchedule V0.9 Beta")
			.setPositiveButton("确定", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					dialog.cancel();
				}
			}).create();
			dialog.show();
		};
		if(item.getItemId() == R.id.exit)
			finish();
		return true;
	}
	
	public void delEvent(Context context, final int eventId)  {
		final iScheduleDB dbHelper = new iScheduleDB(context);
		final Event event;
		String[] item = new String[] {"删除这个活动主题一致的所有活动","仅删除当前活动" };

		try {
			event = dbHelper.getEventById(eventId);
			final Date date = new Date(dateFormat.parse(datePickButton.getText() + " 00:00:00").getTime());
			DialogInterface.OnClickListener onclick = new  DialogInterface.OnClickListener() {
	            
				public void onClick(DialogInterface dialog, int which) {
					List<Event> list = new ArrayList<Event>();
					iScheduleDB helper = new iScheduleDB(Main.this);
					if(which == 0){
						String title = event.getTitle();
						try {
							list = helper.getEventByTitle(title);
						} catch (ParseException e) {
							// TODO 自动生成的 catch 块
							e.printStackTrace();
						}
						for(int i = 0; i < list.size(); i++){
							dbHelper.deleteModify(list.get(i));
							Date eventStartDate = list.get(i).getStartTime();
							if(eventStartDate.getYear() ==  curDate.getYear() && 
									eventStartDate.getMonth() == curDate.getMonth() &&
											eventStartDate.getDate() == curDate.getDate()){
								if(System.currentTimeMillis() < eventStartDate.getTime()){
									cancelAlarmByEvent(Main.this, (int)list.get(i).getEventId() * 2);
									if(list.get(i).getEndTime().getTime() < System.currentTimeMillis()){
										cancelAlarmByEvent(Main.this, (int)list.get(i).getEventId() * 2 + 1);
									}	
								}
							}
						}
						dbHelper.deleteEventByTitle(title);
					}
					else if(which == 1){
						if(event.getStartTime().getYear() ==  curDate.getYear() && 
								event.getStartTime().getMonth() == curDate.getMonth() &&
										event.getStartTime().getDate() == curDate.getDate()){
							if(System.currentTimeMillis() < event.getStartTime().getTime()){
								cancelAlarmByEvent(Main.this, (int)event.getEventId() * 2);
								if(event.getEndTime().getDay() == curDate.getDay()){
									cancelAlarmByEvent(Main.this, (int)event.getEventId() * 2 + 1);
								}	
							}
						}
						try {
							dbHelper.deleteModify(dbHelper.getEventById(eventId));
						} catch (ParseException e) {
							// TODO 自动生成的 catch 块
							e.printStackTrace();
						}
						dbHelper.deleteEventById(eventId);
					}
					
					updateList(date);
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
	
	
	private static Boolean isExit = false;  
    private static Boolean hasTask = false;  
    Timer tExit = new Timer();  
    TimerTask task = new TimerTask() {   
        public void run() {  
            isExit = false;  
            hasTask = true;  
        }  
    };  
	public boolean onKeyDown(int keyCode, KeyEvent event){		
		if(keyCode == KeyEvent.KEYCODE_BACK){
			if(isExit == false ) {  
				isExit = true;  
				Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();  
				if(!hasTask) {  
					tExit.schedule(task, 1500);  
				}
			} else {
				isExit = false;
				hasTask = false;
				finish();
			}
		}
		return false;
	}
	
}


