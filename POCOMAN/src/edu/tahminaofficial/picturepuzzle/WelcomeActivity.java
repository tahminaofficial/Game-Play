package edu.marufhassan.picturepuzzle;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import edu.marufhassan.picturepuzzle.data.GameStateManager;
import edu.marufhassan.slidepuzzle.R;

public class WelcomeActivity extends Activity implements OnTouchListener {
	private Button playButton;
	private Button scoreButton;
	private Button soundOnOffButton;
	private Button quitButton;
	private GameStateManager manager;

	private Typeface font;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		manager = new GameStateManager(this);
		setContentView(R.layout.activity_welcome);

		font = Typeface.createFromAsset(getAssets(), "biocom.ttf");

		playButton = (Button) findViewById(R.id.playButton);
		scoreButton = (Button) findViewById(R.id.scoreButton);
		soundOnOffButton = (Button) findViewById(R.id.soundOnOffButton);
		quitButton = (Button) findViewById(R.id.quitButton);

		playButton.getBackground().setAlpha(0);
		playButton.setTypeface(font);

		scoreButton.getBackground().setAlpha(0);
		scoreButton.setTypeface(font);

		soundOnOffButton.getBackground().setAlpha(0);
		soundOnOffButton.setTypeface(font);
		
		if (manager.isSoundEnabled()) {
			soundOnOffButton.setText("Sound Off");
		} else {
			soundOnOffButton.setText("Sound On");
		}

		quitButton.getBackground().setAlpha(0);
		quitButton.setTypeface(font);

		playButton.setOnTouchListener(this);
		scoreButton.setOnTouchListener(this);
		soundOnOffButton.setOnTouchListener(this);
		quitButton.setOnTouchListener(this);
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouch(View view, MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			view.setBackgroundColor(Color.WHITE);
			view.getBackground().setAlpha(100);
			if (view.getId() == R.id.soundOnOffButton) {
				if (manager.isSoundEnabled()) {
					((Button)view).setText("Sound On");
					manager.setSoundState(false);
				} else {
					((Button)view).setText("Sound Off");
					manager.setSoundState(true);
				}
			}
			break;

		case MotionEvent.ACTION_UP:
			view.getBackground().setAlpha(0);
			
			if (view.getId() == R.id.playButton) {
				Intent intent = new Intent(WelcomeActivity.this, GameActivity.class);
				startActivity(intent);
			} else if (view.getId() == R.id.scoreButton) {
				Intent intent = new Intent(WelcomeActivity.this, HighScoreActivity.class);
				startActivity(intent);
			} else if (view.getId() == R.id.soundOnOffButton) {
				
			} else if (view.getId() == R.id.quitButton) {
				finish();
			}
			
			break;

		default:
			break;
		}
		return false;
	}
}
