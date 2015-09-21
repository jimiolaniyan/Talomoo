package com.yoruba.talomoo;

import android.annotation.TargetApi;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

public class QuestionsActivity extends FragmentActivity implements LoaderCallbacks<Cursor>{

	private static final int QUESTION_TEXT = 1;
	private static final int CATEGORY = 2;
	Long rowInDB;
//	Long gridRow;
	ViewPager pager;
	int selcectionPosition;
	CustomQuestionPagerAdapter mAdapter;
	Long category;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setupActionBar();
		setContentView(R.layout.activity_slider);

		Bundle ext = getIntent().getExtras();
		rowInDB = ext.getLong(DBHelper.KEY_ID); //for DB Id;
		selcectionPosition = ext.getInt(DBHelper.KEY_QUESTION_LEVEL); //for position
		category = ext.getLong(DBHelper.KEY_HINT);

		mAdapter = new CustomQuestionPagerAdapter(this, null, null);
		pager = (ViewPager) findViewById(R.id.pager_nav);
		pager.setAdapter(mAdapter);


		getSupportLoaderManager().initLoader(QUESTION_TEXT, null, this);
		getSupportLoaderManager().initLoader(CATEGORY, null, this);
	}

	//	@Override
	//	protected void onResume() {s
	//		super.onResume();
	//		getSupportLoaderManager().restartLoader(QUESTION_TEXT, null, this);
	//		getSupportLoaderManager().restartLoader(CATEGORY, null, this);
	//	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}
	@Override
	protected void onPause() {
		super.onPause();
		DBHelper db = new DBHelper(this);
		db.close();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.questions, menu);
		return true;
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			onBackPressed();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle arg1) {
		switch (id) {
		case QUESTION_TEXT:
			Uri uri = AppContentProvider.CONTENT_URI_QUESTIONS;
			String pathSegment = Long.toString(category);
			uri = Uri.withAppendedPath(uri, pathSegment);
			return new CursorLoader(this, uri, null, null, null, null);

		case CATEGORY:
			Uri categoryUri = AppContentProvider.CONTENT_URI_CATEGORY_NAME;
			String categoryPath = Long.toString(category);
			categoryUri = Uri.withAppendedPath(categoryUri, categoryPath);
			return new CursorLoader(this, categoryUri, null, null, null, null);
		default:
			return null;
		}

	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

		switch (loader.getId()) {
		case QUESTION_TEXT:
			mAdapter.swapCursor(cursor);
			mAdapter.notifyDataSetChanged();
			pager.setCurrentItem(selcectionPosition, true);
			break;


		case CATEGORY:
			mAdapter.swapAnotherCursor(cursor);
		default:
			break;
		}


	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		mAdapter.swapCursor(null);
	}
}
