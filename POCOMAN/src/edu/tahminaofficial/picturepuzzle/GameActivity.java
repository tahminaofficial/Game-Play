package edu.marufhassan.picturepuzzle;

import java.io.File;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import edu.marufhassan.picturepuzzle.data.GameState;
import edu.marufhassan.picturepuzzle.data.GameStateManager;
import edu.marufhassan.picturepuzzle.data.GamesDatasource;
import edu.marufhassan.picturepuzzle.data.Score;
import edu.marufhassan.slidepuzzle.R;

public class GameActivity extends Activity implements OnClickListener,
		OnTouchListener, GameChangeListener {
	public static final String LOG_TAG = "puzzle";

	private PuzzleView puzzleView;
	private TextView moveTextView;
	private StopWatch stopWatch;

	private Button stopButton;
	private Button refreshButton;
	private Button pauseButton;
	private Button scoreButton;

	private Typeface font;
	
	private MediaPlayer tilesClick;
	private MediaPlayer solved;

	private int totalMoves = 0;
	private int puzzleType = 3; // default puzzle type is 3 * 3

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		tilesClick = MediaPlayer.create(this, R.raw.tileclick);
		solved = MediaPlayer.create(this, R.raw.congratulation);

		setContentView(R.layout.activity_game);

		font = Typeface.createFromAsset(getAssets(), "biocom.ttf");

		puzzleView = (PuzzleView) findViewById(R.id.puzzleView);
		puzzleView.setOnGameChangeListener(this);

		moveTextView = (TextView) findViewById(R.id.movesTextView);
		moveTextView.setTypeface(font);
		stopWatch = new StopWatch(this, R.id.timer);

		stopButton = (Button) findViewById(R.id.stopButton);
		refreshButton = (Button) findViewById(R.id.refreshButton);
		pauseButton = (Button) findViewById(R.id.pauseButton);
		scoreButton = (Button) findViewById(R.id.highScore);

		stopButton.setOnClickListener(this);
		refreshButton.setOnClickListener(this);
		pauseButton.setOnTouchListener(this);
		scoreButton.setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (puzzleView.isPauseState()) {
			return super.onOptionsItemSelected(item);
		}
		switch (item.getItemId()) {
		case R.id.action_choose_photo:
			Intent intent = new Intent(
					Intent.ACTION_PICK,
					android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
			startActivityForResult(intent, 0);
			break;

		case R.id.action_type_easy:
			if (puzzleView.isRunningState() || puzzleView.isSolvedState()) {
				puzzleType = 3;
				puzzleView.refresh(new Puzzle(puzzleType));
			}
			break;

		case R.id.action_type_medium:
			if (puzzleView.isRunningState() || puzzleView.isSolvedState()) {
				puzzleType = 4;
				puzzleView.refresh(new Puzzle(puzzleType));
			}
			break;

		case R.id.action_type_hard:
			if (puzzleView.isRunningState() || puzzleView.isSolvedState()) {
				puzzleType = 5;
				puzzleView.refresh(new Puzzle(puzzleType));
			}
			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View view) {
		if (puzzleView.isPauseState()) {
			return;
		}
		switch (view.getId()) {
		case R.id.refreshButton:
			if (puzzleView.isRunningState() || puzzleView.isSolvedState()) {
				puzzleView.refresh(new Puzzle(puzzleType));
			}
			break;

		case R.id.stopButton:
			finish();
			break;

		case R.id.highScore:
			Intent intent = new Intent(this, HighScoreActivity.class);
			startActivity(intent);
			break;

		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == RESULT_OK && requestCode == 0) {
			Uri uri = data.getData();
			File file = new File(getRealPathFromURI(uri));
			String bitmapPathName = file.getAbsolutePath();

			GameStateManager manager = new GameStateManager(this);
			manager.setPathName(bitmapPathName);
			manager.setSavedGame(false);
			
			Puzzle puzzle = new Puzzle(3);
			puzzleView.setPuzzle(puzzle);

			puzzleView.loadBitmap(manager
					.getValueFromKey(GameStateManager.PATH_NAME));

			puzzleView.setRunningState(true);
			stopWatch.resetClock();
			stopWatch.startClock();
			puzzleView.invalidate();

			pauseButton.setBackgroundResource(R.drawable.pause);
			
			moveTextView.setText("Move: 0");
		}
	}

	private String getRealPathFromURI(Uri contentURI) {
		String result;
		Cursor cursor = getContentResolver().query(contentURI, null, null,
				null, null);
		if (cursor == null) {
			result = contentURI.getPath();
		} else {
			cursor.moveToFirst();
			int idx = cursor
					.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
			result = cursor.getString(idx);
			cursor.close();
		}
		return result;
	}

	@Override
	protected void onResume() {
		super.onResume();

		GameStateManager manager = new GameStateManager(this);
		if (manager.isSavedGame()) {
			GameState gameState = manager.getSavedGameState();

			stopWatch.setElapsedTime(gameState.getElapsedTime());
			stopWatch.stopClock();
			stopWatch.startClock();

			moveTextView.setText("Move: " + gameState.getTotalMoves());

			int[] tiles = gameState.getTilesPosition();

			Puzzle puzzle = new Puzzle(gameState.getPuzzleType());
			puzzle.setTotalMoves(gameState.getTotalMoves());
			puzzle.setTiles(tiles);

			puzzleView.setPuzzle(puzzle);
			puzzleView.loadBitmap(manager
					.getValueFromKey(GameStateManager.PATH_NAME));
			puzzleView.setRunningState(true);
			puzzleView.invalidate();

			pauseButton.setBackgroundResource(R.drawable.pause);
		} else if (!puzzleView.isRunningState()) {
			moveTextView.setText("Move: 0");
			stopWatch.resetClock();
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		GameStateManager manager = new GameStateManager(this);
		if (puzzleView.isRunningState()) {
			manager.setSavedGame(true);
			GameState gameState = new GameState();
			gameState.setElapsedTime(stopWatch.getElapsedTime());
			gameState.setTotalMoves(puzzleView.getPuzzle().getTotalMoves());
			gameState.setPuzzleType(puzzleView.getPuzzle().getPuzzleType());
			gameState.setTilesPosition(puzzleView.getPuzzle().getTiles());
			gameState.setRunningState(puzzleView.isRunningState());
			manager.saveGameState(gameState);
		} else {
			manager.setSavedGame(false);
			manager.setPathName(null);
			puzzleView.setSolvedState(false);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		stopWatch.stopClock();
	}
	
	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			if (v.getId() == R.id.pauseButton) {
				if (puzzleView.isSolvedState() || !puzzleView.isRunningState()) {
					Intent intent = new Intent(
							Intent.ACTION_PICK,
							android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
					startActivityForResult(intent, 0);
					return false;
				}
				if (puzzleView.isPauseState()) {
					v.setBackgroundResource(R.drawable.pause);
					stopWatch.startClock();
					puzzleView.setPauseState(false);
					puzzleView.invalidate();
				} else {
					v.setBackgroundResource(R.drawable.play);
					stopWatch.stopClock();
					puzzleView.setPauseState(true);
					puzzleView.invalidate();
				}
			}
		}
		return false;
	}

	@Override
	public void onBackPressed() {
		if (puzzleView.isPauseState()) {
			stopWatch.startClock();
			puzzleView.setPauseState(false);
			pauseButton.setBackgroundResource(R.drawable.pause);
			puzzleView.invalidate();
		} else {
			finish();
		}
	}

	@Override
	public void updateTotalMoves(int totalMoves) {
		moveTextView.setText("Move: " + totalMoves);
		this.totalMoves = totalMoves;
	}

	@Override
	public void updatePlayButton() {
		pauseButton.setBackgroundResource(R.drawable.play);
	}

	@Override
	public void updatePauseButton() {
		pauseButton.setBackgroundResource(R.drawable.pause);
	}

	@Override
	public void stopWatchStart() {
		stopWatch.startClock();
	}

	@Override
	public void stopWatchStop() {
		stopWatch.stopClock();
	}

	@Override
	public void stopWatchReset() {
		stopWatch.resetClock();
	}

	@Override
	public void updateScore() {
		GamesDatasource datasource = new GamesDatasource(this);
		datasource.openDatabase();

		Score score = new Score();
		score.setMoves(totalMoves);
		score.setTime(stopWatch.getElapsedTime());
		score.setPuzzleType(puzzleView.getPuzzle().getPuzzleType());

		datasource.addScore(score);
		
		datasource.closeDatabase();
	}

	@Override
	public void playTilesClick() {
		GameStateManager manager = new GameStateManager(this);
		if (manager.isSoundEnabled()) {
			tilesClick.start();
		}
	}

	@Override
	public void playCongratulationSound() {
		GameStateManager manager = new GameStateManager(this);
		if (manager.isSoundEnabled()) {
			solved.start();
		}
	}
}
