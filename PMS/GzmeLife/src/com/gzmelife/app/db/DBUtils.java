package com.gzmelife.app.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DBUtils {
	private Context context;
	private SQLiteDatabase db;
	private CookHelper helper;
	
	public DBUtils(Context context){
		this.context = context;
		helper = new CookHelper(context);
	}
	
	public long insert(ContentValues values){
		db = helper.getWritableDatabase();
		return db.insert("cook", null, values);
	}
	
	public int delete(String whereClause,String[] whereArgs){
		db = helper.getWritableDatabase();
		return db.delete("cook", whereClause, whereArgs);
	}
	
	public int update(ContentValues values,String whereClause,String[] whereArgs){
		db = helper.getWritableDatabase();
		return db.update("cook", values, whereClause, whereArgs);
	}
	
	public Cursor queryAll(){
		db = helper.getReadableDatabase();
		return db.query("cook", null, null, null, null,null,null);
	}
	
	public Cursor queryAll(String orderBy){
		db = helper.getReadableDatabase();
		return db.query("cook", null, null, null, null,null,orderBy);
	}
	
	public Cursor queryWhere(String selection,String[] selectionArgs){
		db = helper.getReadableDatabase();
		return db.query("cook",null,selection,selectionArgs,null,null,null);
	}
}
