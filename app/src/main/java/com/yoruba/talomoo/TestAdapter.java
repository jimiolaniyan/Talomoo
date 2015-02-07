package com.yoruba.talomoo;

import java.io.IOException;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class TestAdapter {
	protected static final String TAG = "TestAdpter";
	private final Context mContext;

	private SQLiteDatabase mDB;
	private DBHelper mDbhelper;

	public TestAdapter(Context context){
		this.mContext = context;
		mDbhelper = new DBHelper(mContext);
	}

	public TestAdapter createDatabase() throws SQLException{
		try {
			mDbhelper.createDatabase();
		} catch (IOException e) {
			Log.e(TAG, e.toString() + "Unable To create DB");
			throw new Error("Unable To create DB");
		}
		
		
		return this;

	}

	public TestAdapter open() throws SQLException{

		try {
			mDbhelper.openDatabase();
			mDbhelper.close();
			mDB = mDbhelper.getReadableDatabase();
		} catch (SQLException e) {
			Log.e(TAG, "open >>" + e.toString());
			throw e;
		} 
		return this;
	}

	public void close(){
		mDbhelper.close();
	}

	public Cursor fetchCatgories(){
		putValues();
		return mDB.query(DBHelper.KEY_TABLE, new String[] {"_id", DBHelper.KEY_NAME, DBHelper.KEY_COUNT}, null, null, null, null, null,null);
	}

	public void putValues(){
		for (int i = 1; i < 18; i++  ) {
			 Cursor contentCursor = mDB
					.rawQuery("select count(*) from questions_en where used = 0 and category" + " = " + i, null);

			if(contentCursor.getCount() >0 )
				contentCursor.moveToFirst();

			if (contentCursor.isAfterLast()) {
				contentCursor.close();
				mDB.close();
				return;
			}
			int contentCursorInt = contentCursor.getInt(0);
			ContentValues row = new  ContentValues();
			row.put(DBHelper.KEY_COUNT, contentCursorInt);
			mDB.update(DBHelper.KEY_TABLE, row, DBHelper.KEY_ID + " = " + i, null);
		}
		
	}


	public void test(){
		int id = 8;
		ContentValues data = new ContentValues();
		data.put(DBHelper.KEY_USED, "1");
		mDB.update(DBHelper.KEY_QUESTIONS_TABLE, data, DBHelper.KEY_ID + " = " + id , null);
	}
	
	public Cursor fetchQuestions(long row) throws SQLException{ 
		Cursor cursor = mDB.rawQuery("SELECT * FROM questions_en WHERE category " + " = " + row, null);
//		query(DBHelper.KEY_QUESTIONS_TABLE, new String[] {DBHelper.KEY_ID, "used", "question_level", "question_bundle"}, "category = " + row, null, null, null, null);
		return cursor;
	}
	
	public Cursor getQuestionData(long id){
		return mDB.rawQuery("select * from questions_en where _id " + " = " + id, null);
	}
	
	
	public Cursor getCategoryName(long param){
		return mDB.rawQuery("select _id, name from categories_en where _id = " + param, null);
	}
}


