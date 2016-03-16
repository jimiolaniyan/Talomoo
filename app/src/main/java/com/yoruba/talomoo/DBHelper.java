package com.yoruba.talomoo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class DBHelper extends SQLiteOpenHelper{

	public static final String TAG = "DBHelper";
	public static final String KEY_NAME = "name";
	public static final String KEY_COUNT = "questions_count";
	public static final int COL_COUNT = 2;
	public static final String KEY_CATEGORY = "category";
	public static final String KEY_USED = "used";
	public static final String KEY_ID = "_id";
	public static final String KEY_HINT = "question_hint";
	public static final String KEY_QUESTION_LEVEL = "question_level";
	public static final String KEY_QUESTION_TEXT = "question_text";
	private static String DB_PATH = "";
	public static String DB_NAME = "tester.db";
	private SQLiteDatabase myDataBase; 
	private final Context myContext;
	public static final String KEY_QUESTIONS_TABLE = "questions_en";	
	public static final String KEY_TABLE = "categories_en";	
	public static final String KEY_IMAGES= "listimage";
	public static final String RIGHT_ANSWER = "r_answer";
    public static final String WRONG_ANSWER = "wrong";
	public static final String WRONG_ANSWER1= "w_answer1";
	public static final String WRONG_ANSWER2= "w_answer2";
	public static final String KEY_SCORE = "score";
    private static int categories = 3;
    private static DBHelper sInstance;



    public static DBHelper getInstance(Context context) {

        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (sInstance == null) {
            sInstance = new DBHelper(context.getApplicationContext());
        }
        return sInstance;
    }

	public DBHelper(Context context) {
		super(context, DB_NAME, null, 1);
		DB_PATH = "/data/data/" + context.getPackageName() + "/databases/";
		Log.d(TAG, DB_PATH + "Path Found");
		this.myContext = context;
	}

	public void createDatabase() throws IOException{
		boolean dbExists = checkDBExists();
		if(!dbExists){
			this.getReadableDatabase();
			this.close();
			try {
				copydatabase();
				Log.d("From CopyDatabase", DB_PATH + "Path Found and copied");
			} catch (Exception e) {
				throw new Error("Can't Copy DB");
			}
		}
	}


	private boolean checkDBExists() {
		File file = new File(DB_PATH + DB_NAME);
		return file.exists();
	}

	private void copydatabase() throws IOException {
		InputStream is = myContext.getAssets().open(DB_NAME);
		String outFileName = DB_PATH + DB_NAME;
		OutputStream os = null;
		try {
			os = new FileOutputStream(outFileName);
		} catch (Exception e) {
			Log.e(TAG, os + "Cannot copy file to output ");
		}

		byte[] mBuffer = new byte[1024];
		int mLength;
		while ((mLength = is.read(mBuffer)) > 0) {
			os.write(mBuffer, 0, mLength);
		}
		os.flush();
		os.close();
		is.close();
	}

	public boolean openDatabase() throws SQLException{
			String mPath = DB_PATH + DB_NAME;
			myDataBase = SQLiteDatabase.openDatabase(mPath, null, SQLiteDatabase.NO_LOCALIZED_COLLATORS);
		    return myDataBase != null;
	}
	@Override
	public synchronized void close(){
		if (myDataBase != null) 
			myDataBase.close();
		super.close();
	}


	@Override
	public void onCreate(SQLiteDatabase db) {
	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}
	public Cursor fetchCatgories(){
//		putValues();
		return myDataBase.query(DBHelper.KEY_TABLE, new String[] {DBHelper.KEY_ID, DBHelper.KEY_NAME, DBHelper.KEY_COUNT, DBHelper.KEY_IMAGES}, null, null, null, null, null,null);

	}
	
	public void putValues(){
		for (int i = 1; i < categories; i++  ) {
			 Cursor contentCursor = myDataBase
					.rawQuery("select count(_id) from questions_en where used = 0 and category" + " = " + i, null);

			if(contentCursor.getCount() >0 )
				contentCursor.moveToFirst();

			if (contentCursor.isAfterLast()) {
				contentCursor.close();
				myDataBase.close();
				return;
			}
			int contentCursorInt = contentCursor.getInt(0);
			ContentValues row = new  ContentValues();
			row.put(DBHelper.KEY_COUNT, contentCursorInt);
			myDataBase.update(KEY_TABLE, row, KEY_ID + " = " + i, null);
		}
		
	}

//    public Cursor getUsed(Long category){
//        myDataBase.rawQuery("select count(_id) from questions_en where used = 0 and category" + " = " + category, null);
//        return myDataBase.query(DB);
//    }
	
	public void updateScoreInDB(int newScore, int category_id){
		ContentValues mScore = new ContentValues();
		mScore.put(DBHelper.KEY_SCORE, newScore);
		myDataBase.update(DBHelper.KEY_TABLE, mScore, DBHelper.KEY_ID + " = " + category_id , null);
		
	}
	
	public Cursor fetchQuestionsByCategory(long row, int count) throws SQLException{
		return myDataBase.rawQuery("select * from questions_en where category" + " = " + row + " and used = 0 order by RANDOM() limit " + count, null);
	}

	
	public Cursor fetchQuestionData(long id){
		return myDataBase.rawQuery("select * from questions_en where _id = " + id, null);
	}
	
	public Cursor fetchCategoryName(long param){
		return myDataBase.rawQuery("select _id, name, images, score from categories_en where _id = " + param, null);
	}

    public Cursor fetchScore(long id){
        return myDataBase.rawQuery("select score from categories_en where _id = " + id, null);
    }


	public void  updateUsed(long id) {
		ContentValues data = new ContentValues();
		data.put(DBHelper.KEY_USED, "1");
		myDataBase.update(DBHelper.KEY_QUESTIONS_TABLE, data, DBHelper.KEY_ID + " = " + id , null);
		myDataBase.close();
	}

}
