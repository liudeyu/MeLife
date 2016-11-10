package com.gzmelife.app.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class CookHelper extends SQLiteOpenHelper {
	private static String DB_NAME = "cook.db";
	private static int VERSION = 1;
	public CookHelper(Context context) {
		 super(context, DB_NAME, null, VERSION);
	}
	@Override
	public void onCreate(SQLiteDatabase db) {
		  String sql = "CREATE TABLE IF NOT EXISTS [cook] ([c_name] TEXT(10), [c_time] TEXT(20));";
	      db.execSQL(sql);

	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}
	
	
	
}
