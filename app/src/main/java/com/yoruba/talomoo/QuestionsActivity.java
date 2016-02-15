package com.yoruba.talomoo;

import android.annotation.TargetApi;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.avast.android.dialogs.fragment.SimpleDialogFragment;
import com.avast.android.dialogs.iface.ISimpleDialogListener;
import com.yoruba.talomoo.util.Constant;

import butterknife.ButterKnife;

public class QuestionsActivity extends AppCompatActivity implements LoaderCallbacks<Cursor>, ISimpleDialogListener{
	private static final int QUESTION_TEXT = 1;
	private static final int CATEGORY = 2;
	Long rowInDB;
	ViewPager pager;
	int count;
	CustomQuestionPagerAdapter mAdapter;
	Long category;
    QuestionsActivity aContext = this;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_slider);
        ButterKnife.bind(this);

		Bundle ext = getIntent().getExtras();

        category = ext.getLong(DBHelper.KEY_ID);
		count = ext.getInt(Constant.QUESTION_COUNT);

        mAdapter = new CustomQuestionPagerAdapter(this, null, null);
		pager = (ViewPager) findViewById(R.id.pager_nav);
		pager.setAdapter(mAdapter);


		getSupportLoaderManager().initLoader(QUESTION_TEXT, null, this);
		getSupportLoaderManager().initLoader(CATEGORY, null, this);
	}

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
    public void onBackPressed() {
        SimpleDialogFragment.createBuilder(aContext, getSupportFragmentManager()).setMessage("Want to Quit game? ").setPositiveButtonText("Yes").setNegativeButtonText("No").show();
    }

    @Override
	public Loader<Cursor> onCreateLoader(int id, Bundle arg1) {
		switch (id) {
		case QUESTION_TEXT:
			Uri uri = AppContentProvider.CONTENT_URI_QUESTIONS;
			String pathSegment = Long.toString(category);
			uri = Uri.withAppendedPath(uri, pathSegment);
			return new CursorLoader(this, uri, null, Integer.toString(count), null, null);

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
			break;


		case CATEGORY:
			mAdapter.swapAnotherCursor(cursor);
            mAdapter.notifyDataSetChanged();
			break;
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		mAdapter.swapCursor(null);
	}

    @Override
    public void onNegativeButtonClicked(int requestCode) {

    }

    @Override
    public void onNeutralButtonClicked(int requestCode) {

    }

    @Override
    public void onPositiveButtonClicked(int requestCode) {
        Intent intent = new Intent(QuestionsActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
