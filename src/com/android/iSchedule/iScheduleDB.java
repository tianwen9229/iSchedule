package com.android.iSchedule;

import java.text.SimpleDateFormat;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class iScheduleDB extends SQLiteOpenHelper {

	private static final String DB_NAME = "iSchedule.db";
	private static final int DB_VRESION = 1;
	private static final String EVENT_TABLE_NAME = "event";
	private static final String MODE_TABLE_NAME = "mode";
	private static final String MODIFY_TABLE_NAME = "modify";
	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	
	private static final String EVENT_SQL_CREATE= "create table " + EVENT_TABLE_NAME +
	" ( eid integer primary key autoincrement,"
	+ " title text not null, " 
	+ " place text, "
	+ " content text, "
	+ " creattime text, "
	+ " remindtime text, "
	+ " starttime text, "
	+ " endtime text);";
	
	private static final String MODE_SQL_CREATE = "create table " + MODE_TABLE_NAME +
	" ( mid integer primary key autoincrement,"
	+ " volume integer, "
	+ " vibrate integer); ";
	
	private static final String MODIFY_SQL_CREATE = "create table " + MODIFY_TABLE_NAME +
	" ( eid integer , "
	+ " mid integer , "
	+ " primary key (eid, mid), "
	+ " foreign key (eid) references event , "
	+ " foreign key (mid) references mode ); ";
	
	
	public iScheduleDB(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
	}
	public iScheduleDB(Context context){
		super(context, DB_NAME, null, DB_VRESION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(EVENT_SQL_CREATE);
		db.execSQL(MODE_SQL_CREATE);
		db.execSQL(MODIFY_SQL_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO 自动生成的方法存根
	}
	
	public long insert(Event entity) {
		SQLiteDatabase db = getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("title", entity.getTitle());
		values.put("place", entity.getPlace());
		values.put("content", entity.getContent());
		values.put("creattime", dateFormat.format(entity.getCreatTime()));
		values.put("remindtime", dateFormat.format(entity.getRemindTime()));
		values.put("starttime", dateFormat.format(entity.getStartTime()));
		values.put("endtime", dateFormat.format(entity.getEndTime()));
		// 必须保证 values 至少一个字段不为null ，否则出错
		long rid = db.insert(EVENT_TABLE_NAME, null, values);
		entity.setEventId(rid);
		db.close();
		return rid;
	}
	
	public long insert(Mode entity) {
		SQLiteDatabase db = getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("volume", entity.getVolume());
		values.put("vibrate", entity.getVibrate());
		// 必须保证 values 至少一个字段不为null ，否则出错
		long rid = db.insert(MODE_TABLE_NAME, null, values);
		entity.setModeId(rid);
		db.close();
		return rid;
	}
	
	public long insert(Event event, Mode mode) {
		SQLiteDatabase db = getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("eid", event.getEventId());
		values.put("mid", mode.getModeId());
		// 必须保证 values 至少一个字段不为null ，否则出错
		long rid = db.insert(MODIFY_TABLE_NAME, null, values);
		db.close();
		return rid;
	}
	
	// insert operation
	// delete operation
	// update operation
	// each search operation
	
	
}