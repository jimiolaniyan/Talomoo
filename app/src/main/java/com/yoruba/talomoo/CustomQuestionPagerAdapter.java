package com.yoruba.talomoo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class CustomQuestionPagerAdapter extends PagerAdapter {

	private Cursor cursor;
	private Cursor cursorCategory;
	private LayoutInflater inflater;
	private Context context;



	private static final String PREFS = "Selected";

	public CustomQuestionPagerAdapter(Context context, Cursor cursor1, Cursor cursor2) {
		this.context = context;
		this.cursor = cursor1;
		this.cursorCategory = cursor2;
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}        

	public void swapCursor(Cursor cursor) {
        this.cursor = cursor;
	}

	public void swapAnotherCursor(Cursor cursor) {
		this.cursorCategory = cursor;
	}


	public void destroyItem(ViewGroup view, int position, Object object) {
		(view).removeView((RelativeLayout) object);
	}

	@Override
	public int getCount() {
		if (cursor == null) {
			return 0;
		}else {
			return cursor.getCount();
		}
	}

	@Override
	public int getItemPosition(Object object) {
		return POSITION_NONE;
	}

	@Override
	public Object instantiateItem(final View view, final int position) {

		cursor.moveToPosition(position);

		// General Definitions.

		final ViewPager pager = (ViewPager) view.findViewById(R.id.pager_nav);
		final RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.question_activity, null);
		final RelativeLayout eplanationLayout = (RelativeLayout) layout.findViewById(R.id.explain);

		final int id = cursor.getInt(cursor.getColumnIndex(DBHelper.KEY_ID));
		final String s_id = Integer.toString(id);
		final Button next = (Button) layout.findViewById(R.id.next_button);
		final TextView choose_text = (TextView) layout.findViewById(R.id.choose_answer);
		final ScrollView scroll = (ScrollView) layout.findViewById(R.id.page_content);

		SharedPreferences setted = context.getSharedPreferences(PREFS, 0);

		if (cursorCategory != null && cursorCategory.moveToFirst()){
			final String s = cursorCategory.getString(cursorCategory.getColumnIndex("name"));
			((TextView)layout.findViewById(R.id.text_category)).setText(s);
			final int currentScore = setted.getInt(s, -1);
			if (currentScore > -1) {
				((TextView)layout.findViewById(R.id.user_category_score)).setText(Integer.toString(currentScore));
			}
		}


		// Typeface Related

		Typeface tpf = Typeface.createFromAsset(context.getAssets(), "Roboto-Light.ttf");
		final String text = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_QUESTION_TEXT));
		final TextView question_text =  (TextView)layout.findViewById(R.id.text_question);
		question_text.setText(text);
		question_text.setTypeface(tpf);

		final TextView temp = (TextView) layout.findViewById(R.id.choose_answer);
		temp.setTypeface(tpf);

		final String qPos = Integer.toString(position + 1) + ".";
		final TextView question_position = (TextView) layout.findViewById(R.id.text_position);
		question_position.setText(qPos);
		question_position.setTypeface(tpf);

		final int difficulty = cursor.getInt(cursor.getColumnIndex(DBHelper.KEY_QUESTION_LEVEL));

		// Options Handling

		final String rightAnswer = cursor.getString(cursor.getColumnIndex(DBHelper.RIGHT_ANSWER));
		final String wrongAnswer1 = cursor.getString(cursor.getColumnIndex(DBHelper.WRONG_ANSWER1));
		final String wrongAnswer2 = cursor.getString(cursor.getColumnIndex(DBHelper.WRONG_ANSWER2));
		final ArrayList<String> optionsArray = new ArrayList<String>();
		optionsArray.clear();
		optionsArray.add(rightAnswer);
		optionsArray.add(wrongAnswer1);
		optionsArray.add(wrongAnswer2);
		Collections.shuffle(optionsArray);

		final Button option1; 
		option1 = (Button) layout.findViewById(R.id.button_option1); 
		option1.setText((CharSequence) optionsArray.get(0));

		final Button option2;
		option2 = (Button) layout.findViewById(R.id.button_option2);
		option2.setText((CharSequence) optionsArray.get(1));

		final Button option3;
		option3 = (Button) layout.findViewById(R.id.button_option3);
		option3.setText((CharSequence) optionsArray.get(2));

		// Appending tags to the options
		if (optionsArray.get(0).equalsIgnoreCase(rightAnswer)) {
			option1.setTag(DBHelper.RIGHT_ANSWER);
		} else if (!rightAnswer.equalsIgnoreCase((String) optionsArray.get(0))) {
			option1.setTag("wrong");
		}

		if (rightAnswer.equalsIgnoreCase((String) optionsArray.get(1))) {
			option2.setTag(DBHelper.RIGHT_ANSWER);
		} else if (!rightAnswer.equalsIgnoreCase((String) optionsArray.get(1))) {
			option2.setTag("wrong");
		}

		if (rightAnswer.equalsIgnoreCase((String) optionsArray.get(2))) {
			option3.setTag(DBHelper.RIGHT_ANSWER);
		} else if (!rightAnswer.equalsIgnoreCase((String) optionsArray.get(2))){
			option3.setTag("wrong");
		}

		// Setting up sharedpreferences for buttons according status


		final boolean isClicked = setted.getBoolean(s_id, false);
		if (isClicked) {
			option1.setEnabled(false);
			option2.setEnabled(false);
			option3.setEnabled(false);

			eplanationLayout.setVisibility(View.VISIBLE);

			final boolean right = setted.getBoolean(s_id + " Right", false);
			if(right){
				choose_text.setText(R.string.correct);
				choose_text.setTextColor(0xff2ad094);
				if (optionsArray.get(0).toString().equals(rightAnswer)) {
					option1.setBackgroundColor(0xff9dcf94);
				}else if (optionsArray.get(1).toString().equals(rightAnswer)) {
					option2.setBackgroundColor(0xff9dcf94);
				}else if (optionsArray.get(2).toString().equals(rightAnswer)) {
					option3.setBackgroundColor(0xff9dcf94);
				}
			}

			boolean wrong = setted.getBoolean(s_id + " Wrong", false);
			if (wrong) {
				choose_text.setText(R.string.wrong);
				choose_text.setTextColor(0xffec6d4b);
				final String button1 = setted.getString(s_id + " Button1Text", null);

				if (button1 != null) {
					if (optionsArray.get(0).toString().equals(button1)) {
						option1.setBackgroundColor(0xffec6d42);
					}else if (optionsArray.get(1).toString().equals(button1)) {
						option2.setBackgroundColor(0xffec6d42);
					}else if (optionsArray.get(2).toString().equals(button1)) {
						option3.setBackgroundColor(0xffec6d42);
					}

					if (optionsArray.get(0).toString().equals(rightAnswer)) {
						option1.setBackgroundColor(0xff9dcf94);
					}else if (optionsArray.get(1).toString().equals(rightAnswer)) {
						option2.setBackgroundColor(0xff9dcf94);
					}else if (optionsArray.get(2).toString().equals(rightAnswer)) {
						option3.setBackgroundColor(0xff9dcf94);
					}
				}
			}

		}

		// Handle Button clicks


		final DBHelper mDbHelper = new DBHelper(context);
        try {
            mDbHelper.openDatabase();  //DB operation
        } finally {
            mDbHelper.close();
        }

		View.OnClickListener checkAnswer = new View.OnClickListener() {

			@Override
			public void onClick(View viewClicked) {
				option1.setClickable(false);
				option2.setClickable(false);
				option3.setClickable(false);

				eplanationLayout.setVisibility(View.VISIBLE);

				new Thread(new Runnable() {
					@Override
					public void run() {
						mDbHelper.updateUsed(id);
					}
				}).start();

				scroll.post(new Runnable() {
					@Override
					public void run() {
						scroll.fullScroll(View.FOCUS_DOWN);
					}
				});

				SharedPreferences setted = context.getSharedPreferences(PREFS, 0);
				SharedPreferences.Editor editor = setted.edit();
				editor.putBoolean(s_id, true);
				editor.commit();

				Button localButton = (Button) viewClicked;

				if (localButton.getTag().equals("r_answer")) {
					localButton.setBackgroundColor(0xff9dcf94);
					choose_text.setText(R.string.correct);
					choose_text.setTextColor(0xff2ad094);
					if (cursorCategory != null && cursorCategory.moveToFirst()){
						final String s = cursorCategory.getString(cursorCategory.getColumnIndex("name"));
						final int currentScore = setted.getInt(s, 0);
								switch (difficulty ) {
								case 3:
									editor.putInt(s , 400 + currentScore);
									editor.commit();
									break;
								case 2:
									editor.putInt(s , 500 + currentScore);
									editor.commit();
									break;
								case 1:
									editor.putInt(s , 600 + currentScore);
									editor.commit();
									break;
								default:
									break;
								}
					}

					editor.putBoolean(s_id + " Right", true);
					editor.commit();
				}
				else {
					localButton.setBackgroundColor(0xffec6d42);
					choose_text.setText(R.string.wrong);
					choose_text.setTextColor(0xffec6d4b);
					final String button1_text = localButton.getText().toString();

					editor.putBoolean(s_id + " Wrong", true);
					editor.putString(s_id + " Button1Text", button1_text);
					editor.commit();

					if (option1.getTag().toString().equalsIgnoreCase("r_answer")) {
						option1.setBackgroundColor(0xff9dcf94);
					}else if (option2.getTag().toString().equalsIgnoreCase("r_answer")) {
						option2.setBackgroundColor(0xff9dcf94);
					}else if(option3.getTag().toString().equalsIgnoreCase("r_answer")){
						option3.setBackgroundColor(0xff9dcf94);
					}

				}
			}

		};

		option1.setOnClickListener(checkAnswer);
		option2.setOnClickListener(checkAnswer);
		option3.setOnClickListener(checkAnswer);

		//		private void calculateScore() {
		//			if (cursorCategory != null && cursorCategory.moveToFirst()) {
		//				int currentScore = cursorCategory.getInt(cursorCategory.getColumnIndex(DBHelper.KEY_SCORE));
		//				final int idForScore = cursorCategory.getInt(cursorCategory.getColumnIndex(DBHelper.KEY_ID));
		//				switch (difficulty) {
		//				case 1:
		//					currentScore += 100;
		//					Log.d(" 1 New Score", currentScore + " 1 current Score" + " " + idForScore);
		//					mDbHelper.updateScoreInDB(currentScore, idForScore);
		//					break;
		//
		//				case 2:
		//					currentScore += 200;
		//					Log.d("2 New Score", currentScore + " 2 current Score");
		//					mDbHelper.updateScoreInDB(currentScore, idForScore);
		//					break;
		//
		//				case 3:
		//					currentScore += 300;
		//					Log.d("2 New Score", currentScore + " 3 current Score");
		//					mDbHelper.updateScoreInDB(currentScore, idForScore);
		//				default:
		//
		//					break;
		//				}
		//			}
		//		}
		//	});

		Button playButton = (Button) layout.findViewById(R.id.play_button);
		playButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (cursor.moveToPosition(pager.getCurrentItem())) {
					String audio = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_HINT));
					if (audio == null || audio.isEmpty()) {
						Toast.makeText(context, "Nothing to Play " + audio, Toast.LENGTH_SHORT).show();
					}else if (audio != null && !audio.isEmpty()) {
						try {
							AssetFileDescriptor afd = context.getAssets().openFd(audio);
							MediaPlayer player = new MediaPlayer();
							player.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
							player.prepare();
							player.start();
						} catch (IOException e) {
							Log.d(" Audio Error ", e.getMessage());
							e.printStackTrace();
						}
						//						MediaPlayer mPlayer = MediaPlayer.create(context, context.getAssets().open(audio));
						//						/*(context,getResources().getIdentifier(audio, "raw", context.getPackageName()));*/
						//						mPlayer.start();
					} 

				}

			}
		});

		Button postButton = (Button) layout.findViewById(R.id.post_online);
		postButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(context, Login.class);
				intent.putExtra(DBHelper.KEY_QUESTION_TEXT, text);
				context.startActivity(intent);

			}
		});

		next.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				int j = getItem(+1) + 1;
				if (position != cursor.getCount()-1) {
					Toast.makeText(context, context.getResources().getString(R.string.question_no) + " " + j, Toast.LENGTH_SHORT).show();

                }else {
					Toast.makeText(context, "This is the end of this category. Check for other unanswered questions", Toast.LENGTH_LONG).show();

                }

				pager.setCurrentItem(getItem(+1), true);
			}

			private int getItem(int i) {
				return i += pager.getCurrentItem();
			}
		});

		((ViewPager)view).addView(layout);
		return layout;
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == object;
	}

}