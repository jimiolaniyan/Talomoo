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
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.avast.android.dialogs.fragment.ListDialogFragment;

import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;


public class CategoryActivity extends FragmentActivity implements android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor> {
    final String REFRESH = "Refresh";
    //    public static final String EXTRA_MESSAGE = "com.yoruba.talomoo.MESSAGE";
    SimpleCursorAdapter mSimpleCursorAdapter;

    @Bind(R.id.linlaHeaderProgress) RelativeLayout ll;
    @Bind(R.id.textView) TextView headerText;
    Typeface tpf;
    Locale locale;
    int refresh;


    CategoryActivity aContext = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        ButterKnife.bind(this);
        ll.setVisibility(View.VISIBLE);
        tpf = Typeface.createFromAsset(getAssets(), "Purisa.ttf");
        headerText.setTypeface(tpf);
        fillList();
        setPreferredLanguage();
        getSupportLoaderManager().initLoader(0, null, this);
    }

    private void setPreferredLanguage() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences preferences = getSharedPreferences(FirstRun.PREF_NAME, 0);
        boolean refresh = preferences.getBoolean(REFRESH, true);
        String tempPref = sharedPreferences.getString(getString(R.string.language_preference),
                getString(R.string.pref_language_eng));
//        Toast.makeText(this, tempPref, Toast.LENGTH_SHORT).show();
        if (tempPref.equals(getString(R.string.pref_language_yor))) {
            String lang = "es";
            locale = new Locale(lang);
            Resources res = getBaseContext().getResources();
            DisplayMetrics disp = res.getDisplayMetrics();
            Configuration conf = res.getConfiguration();
            conf.locale = locale;
            res.updateConfiguration(conf, disp);

            if (refresh) {
                SharedPreferences.Editor edit = preferences.edit();
                edit.putBoolean(REFRESH, false);
                edit.apply();
                finish();
                startActivity(getIntent());
            }

        } else if (tempPref.equals(getString(R.string.pref_language_eng))) {
            String lang = "en";
            locale = new Locale(lang);
//            Locale.setDefault(locale);
            Resources res = getBaseContext().getResources();
            DisplayMetrics disp = res.getDisplayMetrics();
            Configuration conf = res.getConfiguration();
            conf.locale = locale;
            res.updateConfiguration(conf, disp);
            if (!refresh) {
                SharedPreferences.Editor edit = preferences.edit();
                edit.putBoolean(REFRESH, true);
                edit.apply();
                finish();
                startActivity(getIntent());
            }
        }

    }

    @OnClick(R.id.composite_item)
    public void handleSettingsButton() {
        Intent intent = new Intent(CategoryActivity.this, SettingsActivity.class);
        startActivity(intent);
    }

    @OnItemClick(R.id.category_list) void clickCallBacks(int position, long id) {
//                        ListDialogFragment.createBuilder(aContext, getSupportFragmentManager())
//                                .setTitle("Select Question")
//                                .setItems(R.array.number_of_questions)
//                                .setRequestCode(9)
//                                .setChoiceMode(AbsListView.CHOICE_MODE_SINGLE)
//                                .show();

                        DBHelper mDbHelper = DBHelper.getInstance(CategoryActivity.this);
                        mDbHelper.openDatabase();
                        Cursor cursor = mDbHelper.fetchCategoryName(position + 1);
                        if (cursor != null && cursor.moveToFirst()) {
                            String category = cursor.getString(cursor.getColumnIndex("name"));
                            cursor.close();
                            mDbHelper.close();
                            Intent i = new Intent(CategoryActivity.this, QuestionSelection.class);
                            i.putExtra(DBHelper.KEY_ID, id);
                            i.putExtra(DBHelper.KEY_CATEGORY, category);
                            startActivity(i);
//                        }
                    }
    }


    @Override
    protected void onResume() {
        super.onResume();
        getSupportLoaderManager().restartLoader(0, null, this);
        setPreferredLanguage();
    }

    private void fillList() {
        String[] from = new String[]{DBHelper.KEY_IMAGES, DBHelper.KEY_NAME, DBHelper.KEY_COUNT, DBHelper.KEY_ID};
        int[] to = new int[]{R.id.image, R.id.list_question_type, R.id.list_questions_remaining};
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
                            ImageView imageView = (ImageView) view;
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
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
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
