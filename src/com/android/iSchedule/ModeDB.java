package com.android.iSchedule;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class ModeDB extends SQLiteOpenHelper{
	
	private static final String DB_NAME = "mode.db";
	private static final int DB_VRESION = 1;
	private static final String TABLE_NAME = "mode";
	// private static final String SQL_CREATE_TABLE = "TO DO"
	
	public ModeDB(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
		// TODO 自动生成的构造函数存根
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO 自动生成的方法存根
		
	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO 自动生成的方法存根
		// panxiaoning edit
	}
	
	// insert operation
	// delete operation
	// update operation
	// each search operation
	
}