package com.yoruba.talomoo;

import android.annotation.TargetApi;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class QuestionSelection extends FragmentActivity implements LoaderCallbacks<Cursor>  {

    Long rowId;
    SimpleCursorAdapter mAdapter;
    Bundle extras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.selection_grid);
        extras = getIntent().getExtras();
        String title = extras.getString(DBHelper.KEY_CATEGORY);
        setTitle(title);
        if (rowId == null) {
            rowId = ( extras != null ) ? extras.getLong(DBHelper.KEY_ID) :  null;
        }
        populategrid();
        setupActionBar();
        startQuestion();
        getSupportLoaderManager().initLoader(1, null, this);
    }



    public Long getRowId() {
        return this.rowId;
    }

    private void populategrid() {
        String[] from = {DBHelper.KEY_ID, "question_level", "used"};
        int[] to = {R.id.grid_text, R.id.grid_image_right, R.id.grid_image_left};
        GridView grid = (GridView)findViewById(R.id.question_grid);
        mAdapter = new SimpleCursorAdapter(this, R.layout.grid_item, null, from, to, 0);
        grid.setAdapter(mAdapter);
        mAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {

            @Override
            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {

                switch (view.getId()) {

                    case R.id.grid_text:
                        TextView txt = (TextView)view;
                        int CursorPos = cursor.getPosition() + 1;
                        txt.setText(Integer.toString(CursorPos));
                        int getI = cursor.getInt(cursor.getColumnIndex("used"));
                        String color = Integer.toString(getI);
                        if (color.equals("1") || color.equals("2")) {
                            ((LinearLayout)view.getParent()).setBackgroundColor(0xffdddddd);
                        }else if (color.equals("0")) {
                            ((LinearLayout)view.getParent()).setBackgroundColor(0xef33b5e5);
                        }
                        return true;

                    case R.id.grid_image_right:
                        int viewI = cursor.getInt(cursor.getColumnIndex("question_level"));
                        String image1 = Integer.toString(viewI);
                        if (image1.equals("1") || image1.equals("2")){
                            ((ImageView)view).setVisibility(View.GONE);
                        }else if (image1.equals("3") || image1.equals("4") || image1.equals("5") || image1.equals("6")) {
                            ((ImageView)view).setVisibility(View.VISIBLE);
                        }
                        return true;

                    case R.id.grid_image_left:
                        int view2 = cursor.getInt(cursor.getColumnIndex("question_level"));
                        String image2 = Integer.toString(view2);
                        if (image2.equals("1") ){
                            ((ImageView)view).setVisibility(View.GONE);
                        }else if (image2.equals("2") || image2.equals("3") || image2.equals("4") || image2.equals("5") || image2.equals("6")) {
                            ((ImageView)view).setVisibility(View.VISIBLE);
                        }
                        return true;

                }
                return false;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getSupportLoaderManager().restartLoader(1, null, this);
    }

    private void startQuestion() {

        GridView myGrid = (GridView) findViewById(R.id.question_grid);
        myGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {

                DBHelper mDbHelper = new DBHelper(QuestionSelection.this);
                mDbHelper.openDatabase();
                Cursor c = mDbHelper.fetchQuestionData(id);
                if (c != null && c.moveToFirst()) {
                    long t = c.getInt(c.getColumnIndex("category"));
                    c.close();
                    mDbHelper.close();
                    Intent i = new Intent(QuestionSelection.this, QuestionsActivity.class);
                    i.putExtra(DBHelper.KEY_ID, id);
                    i.putExtra(DBHelper.KEY_QUESTION_LEVEL, position);
                    Log.d(DBHelper.TAG,  position + " onClick Position ");
                    i.putExtra(DBHelper.KEY_HINT, t);
                    i.putExtra(DBHelper.KEY_COUNT, rowId);
                    startActivity(i);
                }
            }

        });
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setupActionBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String pathSegment = Long.toString(getRowId());
        Uri uri = AppContentProvider.CONTENT_URI_QUESTIONS;
        uri = Uri.withAppendedPath(uri, pathSegment);
        return new CursorLoader(this, uri, null, null, null, null);
    }
    @Override
    public void onLoadFinished(Loader<Cursor> arg0, Cursor cursor) {
        mAdapter.swapCursor(cursor);

    }
    @Override
    public void onLoaderReset(Loader<Cursor> arg0) {
        mAdapter.swapCursor(null);

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


}

