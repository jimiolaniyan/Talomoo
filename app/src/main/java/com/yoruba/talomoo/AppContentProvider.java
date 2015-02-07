package com.yoruba.talomoo;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

import java.io.IOException;

public class AppContentProvider extends ContentProvider {
	QuestionSelection qSelect;
	public static final String AUTHORITY = "com.yoruba.talomoo.AppContentProvider";
	public static final Uri CONTENT_URI_CATEGORY = Uri.parse("content://" + AUTHORITY + "/categories");
	public static final Uri CONTENT_URI_QUESTIONS = Uri.parse("content://" + AUTHORITY + "/questions");
	public static final Uri CONTENT_URI_IBEERE = Uri.parse("content://" + AUTHORITY + "/ibeere");
	public static final Uri CONTENT_URI_CATEGORY_NAME = Uri.parse("content://" + AUTHORITY + "/title");
	
	private static final int QUESTIONS = 1;
	private static final int GRID = 2;
	private static final int IBEERE = 3;
	private static final int TITLE = 4;

	
	TestAdapter myTestAdapter;
	DBHelper myDH;
	CategoryActivity cat;
	Long id;
	private static final UriMatcher mUriMatcher;

	static {
		mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		mUriMatcher.addURI(AUTHORITY, "categories", QUESTIONS);
		mUriMatcher.addURI(AUTHORITY, "questions/#", GRID);
		mUriMatcher.addURI(AUTHORITY, "ibeere/#", IBEERE);
		mUriMatcher.addURI(AUTHORITY, "title/#", TITLE);

	}

	@Override
	public boolean onCreate() {
		myDH = new DBHelper(getContext());
		try {
			myDH.createDatabase();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		myDH.openDatabase();
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {

		switch (mUriMatcher.match(uri)) {
		case QUESTIONS:
			return  myDH.fetchCatgories();
		case GRID:
			String clickedRow = uri.getPathSegments().get(1);
			long row = Long.parseLong(clickedRow);
			return myDH.fetchQuestionsByCategory(row);

		case IBEERE:
			String clickedGrid = uri.getPathSegments().get(1);
			long qRow = Long.parseLong(clickedGrid);
			return myDH.fetchQuestionData(qRow);
			
		case TITLE:
			String clickedName = uri.getPathSegments().get(1);
			long category = Long.parseLong(clickedName);
			return myDH.fetchCategoryName(category);

		default:
			throw new IllegalArgumentException("Unsupported URI: " + uri);
		}
	}

	@Override
	public int delete(Uri arg0, String arg1, String[] arg2)  throws UnsupportedOperationException{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getType(Uri uri) throws UnsupportedOperationException{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) throws UnsupportedOperationException {
		
		return null;
	}


	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

}
