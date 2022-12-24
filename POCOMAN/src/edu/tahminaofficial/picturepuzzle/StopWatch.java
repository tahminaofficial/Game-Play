package edu.marufhassan.picturepuzzle;

import edu.marufhassan.slidepuzzle.R;
import android.app.Activity;
import android.graphics.Typeface;
import android.os.Handler;
import android.widget.TextView;

public class StopWatch {

	private Activity activity;
	
	private TextView timerTextView;
	
	private Typeface font;
	
	private Handler mHandler = new Handler();
	private long startTime;
	private long elapsedTime;
	private final int REFRESH_RATE = 100;
	private boolean stopped = false;

	public StopWatch(Activity activity, int timer) {
		this.activity = activity;
		timerTextView =  (TextView) activity.findViewById(timer);
		font = Typeface.createFromAsset(activity.getAssets(), "biocom.ttf");
		timerTextView.setTypeface(font);
	}

	public void startClock() {
		if (stopped) {
			startTime = System.currentTimeMillis() - elapsedTime;
		} else {
			startTime = System.currentTimeMillis();
		}
		mHandler.removeCallbacks(startTimer);
		mHandler.postDelayed(startTimer, 0);
	}

	public void stopClock() {
		mHandler.removeCallbacks(startTimer);
		stopped = true;
	}

	public void resetClock() {
		mHandler.removeCallbacks(startTimer);
		stopped = false;
		((TextView) activity.findViewById(R.id.timer)).setText("00:00:00");
	}

	private Runnable startTimer = new Runnable() {
		public void run() {
			elapsedTime = System.currentTimeMillis() - startTime;
			timerTextView.setText(updateTimer(elapsedTime));
			mHandler.postDelayed(this, REFRESH_RATE);
		}
	};

	public static String updateTimer(float time) {
		long secs = (long) (time / 1000);
		long mins = (long) ((time / 1000) / 60);
		long hrs = (long) (((time / 1000) / 60) / 60);

		/*
		 * Convert the seconds to String and format to ensure it has a leading
		 * zero when required
		 */
		secs = secs % 60;
		String seconds = String.valueOf(secs);
		if (secs == 0) {
			seconds = "00";
		}
		if (secs < 10 && secs > 0) {
			seconds = "0" + seconds;
		}

		/* Convert the minutes to String and format the String */

		mins = mins % 60;
		String minutes = String.valueOf(mins);
		if (mins == 0) {
			minutes = "00";
		}
		if (mins < 10 && mins > 0) {
			minutes = "0" + minutes;
		}

		/* Convert the hours to String and format the String */

		String hours = String.valueOf(hrs);
		if (hrs == 0) {
			hours = "00";
		}
		if (hrs < 10 && hrs > 0) {
			hours = "0" + hours;
		}
		
		return hours + ":" + minutes + ":" + seconds;
	}

	public long getElapsedTime() {
		return elapsedTime;
	}

	public void setElapsedTime(long elapsedTime) {
		this.elapsedTime = elapsedTime;
	}

	
	public boolean isStopped() {
		return stopped;
	}
}
