package com.yoruba.talomoo;


import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter.ViewBinder;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Locale;


public class CategoryActivity extends FragmentActivity implements  android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor>{
    final String REFRESH = "Refresh";
//    public static final String EXTRA_MESSAGE = "com.yoruba.talomoo.MESSAGE";
	SimpleCursorAdapter mSimpleCursorAdapter;
	RelativeLayout ll;
    LinearLayout ln;
    Locale locale;
    int refresh;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list);
        // Show the Up button
		//setupActionBar();
		ll = (RelativeLayout) findViewById(R.id.linlaHeaderProgress);
		ll.setVisibility(View.VISIBLE);
        Typeface tpf = Typeface.createFromAsset(getAssets(), "Purisa.ttf");
        ((TextView) findViewById(R.id.textView)).setTypeface(tpf);
		fillList();
        setPreferredLanguage();
        handleSettingsButton();
		clickCallBacks();
		getSupportLoaderManager().initLoader(0, null, this);
	}

    private void setPreferredLanguage() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences preferences = getSharedPreferences(FirstRun.PREF_NAME, 0);
        boolean refresh =  preferences.getBoolean(REFRESH, true);
        String tempPref = sharedPreferences.getString(getString(R.string.language_preference),
                getString(R.string.pref_language_eng));
//        Toast.makeText(this, tempPref, Toast.LENGTH_SHORT).show();
        if(tempPref.equals(getString(R.string.pref_language_yor))){
            String lang ="es";
            locale = new Locale(lang);
//            Locale.setDefault(locale);
            Resources res = getBaseContext().getResources();
            DisplayMetrics disp = res.getDisplayMetrics();
            Configuration conf = res.getConfiguration();
            conf.locale = locale;
            res.updateConfiguration(conf, disp);

            if (refresh){
                SharedPreferences.Editor edit = preferences.edit();
                edit.putBoolean(REFRESH, false);
                edit.apply();
                finish();
                startActivity(getIntent());
            }

        }else if (tempPref.equals(getString(R.string.pref_language_eng))) {
            String lang ="en";
            locale = new Locale(lang);
//            Locale.setDefault(locale);
            Resources res = getBaseContext().getResources();
            DisplayMetrics disp = res.getDisplayMetrics();
            Configuration conf = res.getConfiguration();
            conf.locale = locale;
            res.updateConfiguration(conf, disp);
            if (!refresh){
                SharedPreferences.Editor edit = preferences.edit();
                edit.putBoolean(REFRESH, true);
                edit.apply();
                finish();
                startActivity(getIntent());
            }
        }

    }
    private void handleSettingsButton() {
        ln = (LinearLayout) findViewById(R.id.composite_item);
        ln.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CategoryActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });
    }
    private void clickCallBacks() {
		ListView clickList = (ListView) findViewById(R.id.category_list);
		clickList.
		setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> l, View v, int position,
					long id) {
				
				DBHelper mDbHelper = DBHelper.getInstance(CategoryActivity.this);
				mDbHelper.openDatabase();
				Cursor cursor = mDbHelper.fetchCategoryName(position+1);
				if (cursor != null && cursor.moveToFirst()) {
					String category = cursor.getString(cursor.getColumnIndex("name"));
					cursor.close();
					mDbHelper.close();
					Intent i = new Intent(CategoryActivity.this, QuestionSelection.class);
					i.putExtra(DBHelper.KEY_ID, id);
					i.putExtra(DBHelper.KEY_CATEGORY, category);
					startActivity(i);
				}
				
			}
		});
	}


	@Override
	protected void onResume() {
		super.onResume();
		getSupportLoaderManager().restartLoader(0, null, this);
        setPreferredLanguage();
	}

	private void fillList() {
		String[] from = new String[] {DBHelper.KEY_IMAGES, DBHelper.KEY_NAME, DBHelper.KEY_COUNT, DBHelper.KEY_ID};
		int [] to = new int[] {R.id.image, R.id.list_question_type, R.id.list_questions_remaining};
		ListView list = (ListView) findViewById(R.id.category_list);

		mSimpleCursorAdapter = new SimpleCursorAdapter(this, R.layout.customlist, null, from, to, 0);
		list.setAdapter(mSimpleCursorAdapter);
		mSimpleCursorAdapter.setViewBinder(new ViewBinder() {

			@Override
			public boolean setViewValue(View view, Cursor cursor, int colunmIndex) {
				switch (view.getId()) {
				case R.id.image:
					String image = cursor.getString(cursor.getColumnIndex("listimage"));
					if (image != null && !image.isEmpty()) {
						ImageView imageView = (ImageView)view;
						imageView.setImageResource(getResources().getIdentifier(image, "drawable", getPackageName()));
					}
					return true;

				default:
					break;
				}
				return false;
			}
		});
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle bundle	) {
		Uri uri = AppContentProvider.CONTENT_URI_CATEGORY;
		return new CursorLoader(this, uri, null, null, null, null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor cursor) {
		mSimpleCursorAdapter.swapCursor(cursor);
		ll.setVisibility(View.GONE);

	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		mSimpleCursorAdapter.swapCursor(null);

	}

}
