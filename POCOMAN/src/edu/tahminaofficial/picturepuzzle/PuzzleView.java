package edu.marufhassan.picturepuzzle;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Point;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

public class PuzzleView extends View {
	public static final String LOG_TAG = "puzzle";
	
	private GameChangeListener gameListener;
	
	public static final int TILES_DISTANCE = 3; // in pixels
	public static int DISPLAY_WIDTH;
	public static int DISPLAY_HEIGHT;

	private List<Bitmap> tilesBitmapsList;

	private Typeface font;
	private Context context;
	private Puzzle puzzle;
	private Bitmap puzzleBitmap; // hold the bitmap

	private Paint tilesNumberPaint = new Paint();
	private Paint pausedPaint = new Paint();

	private int touchPositionX;
	private int touchPositionY;

	/**/
	private int bitmapAreaStartX;
	private int bitmapAreaStartY;
	private int bitmapAreaEndX;
	private int bitmapAreaEndY;

	private int totalMoves = 0; // number of moves needed to solve the puzzle

	private boolean runningState = false;
	private boolean pauseState = false; // determine whether the games is in
										// pause state or not
	private boolean solvedState = false; // determine whether the games is
											// solved or not

	private int puzzleType; // puzzle type: 3 * 3 or 4 * 4 or 5 * 5
	private int puzzleWidth; // 3 or 4 or 5

	private int tilesWidth;
	private int tilesHeight;


	public PuzzleView(Context context) {
		super(context);
		this.context = context;
		init();
	}

	public PuzzleView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		this.context = context;
		init();
	}

	public PuzzleView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		init();
	}

	private void init() {
		font = Typeface.createFromAsset(context.getAssets(), "biocom.ttf");
		
		pausedPaint.setTextSize(80);
		pausedPaint.setColor(Color.WHITE);
		pausedPaint.setAlpha(170);
		pausedPaint.setTextAlign(Align.CENTER);
		pausedPaint.setTypeface(font);

		tilesNumberPaint.setARGB(255, 50, 205, 50);
		tilesNumberPaint.setTypeface(font);
	}

	public void loadBitmap(String pathName) {
		puzzleBitmap = BitmapFactory.decodeFile(pathName);
		scaleBitmap();
		
		bitmapAreaStartX = 0;
		bitmapAreaStartY = DISPLAY_HEIGHT / 2 - DISPLAY_WIDTH / 2;
		bitmapAreaEndX = puzzleBitmap.getWidth();
		bitmapAreaEndY = puzzleBitmap.getHeight() + bitmapAreaStartY;

		createTilesBitmapsList();
	}
	
	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (isPauseState() || isSolvedState() || !isRunningState()) {
			return true;
		}

		if (puzzle.isSolved()) {
			gameListener.stopWatchStop();
			gameListener.updatePlayButton();
			setSolvedState(true);
			invalidate();
			
			Toast.makeText(context, "Congratulations! It's solved",
					Toast.LENGTH_SHORT).show();
			gameListener.playCongratulationSound();
			gameListener.updateScore();
			return true;
		}

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			touchPositionX = (int) event.getX();
			touchPositionY = (int) event.getY();
			int count = 0;
			if (checkTouchPosition()) {
				int tempX = TILES_DISTANCE;
				int tempY = DISPLAY_HEIGHT / 2 - DISPLAY_WIDTH / 2
						+ TILES_DISTANCE;
				for (int i = 0; i < puzzleType; i++) {
					if (touchPositionX >= tempX
							&& touchPositionY >= tempY
							&& touchPositionX <= tempX + tilesWidth
									+ TILES_DISTANCE
							&& touchPositionY <= tempY + tilesHeight
									+ TILES_DISTANCE) {
						break;
					}
					if ((i + 1) % puzzleWidth == 0) {
						tempX = TILES_DISTANCE;
						tempY += tilesHeight + TILES_DISTANCE * 2;
					} else {
						tempX += tilesHeight + TILES_DISTANCE * 2;
					}
					count++;
				}
				if (puzzle.checkMove(count)) {
					gameListener.playTilesClick();
					puzzle.moveTiles(count);
					invalidate();
				}
			}
			gameListener.updateTotalMoves(puzzle.getTotalMoves());
			break;

		default:
			break;
		}

		return true;
	}

	private boolean checkTouchPosition() {
		if ((touchPositionX >= bitmapAreaStartX
				&& touchPositionY >= bitmapAreaStartY
				&& touchPositionX <= bitmapAreaEndX && touchPositionY <= bitmapAreaEndY)) {
			return true;
		}

		return false;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		if (!isRunningState()) {
			return;
		}

		if (isSolvedState()) {
			canvas.drawBitmap(puzzleBitmap, 0, DISPLAY_HEIGHT / 2
					- DISPLAY_WIDTH / 2, null);
			setRunningState(false);
			return;
		}

		int tempX = TILES_DISTANCE;
		int tempY = DISPLAY_HEIGHT / 2 - DISPLAY_WIDTH / 2 + TILES_DISTANCE;

		int tiles[] = puzzle.getTiles();

		for (int i = 0; i < puzzleType; i++) {
			if (tiles[i] == puzzleType - 1) {
				if ((i + 1) % puzzleWidth == 0) {
					tempX = TILES_DISTANCE;
					tempY += tilesHeight + TILES_DISTANCE * 2;
				} else {
					tempX += tilesHeight + TILES_DISTANCE * 2;
				}
				continue;
			}

			canvas.drawBitmap(tilesBitmapsList.get(tiles[i]), tempX, tempY,
					null);

			/* draw the tiles number in every tiles */
			String string = String.valueOf(tiles[i] + 1);
			int xPos = tempX
					+ (tilesWidth / 2)
					- (int) tilesNumberPaint.measureText(string, 0,
							string.length()) / 2;
			int yPos = tempY
					+ (int) ((tilesHeight / 2) - ((tilesNumberPaint.descent() + tilesNumberPaint
							.ascent()) / 2));
			tilesNumberPaint.setTextSize((tilesWidth / tilesHeight) * 30);
			canvas.drawText(string, xPos, yPos, tilesNumberPaint);

			if ((i + 1) % puzzleWidth == 0) {
				tempX = TILES_DISTANCE;
				tempY += tilesHeight + TILES_DISTANCE * 2;
			} else {
				tempX += tilesHeight + TILES_DISTANCE * 2;
			}

			if (isPauseState()) {
				canvas.drawARGB(100, 0, 0, 0);
				canvas.drawText("PAUSED", DISPLAY_WIDTH / 2,
						DISPLAY_HEIGHT / 2, pausedPaint);
			}
		}
	}

	private void scaleBitmap() {
		WindowManager manager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		Display defaultdDisplay = manager.getDefaultDisplay();
		Point displayDimension = new Point();
		defaultdDisplay.getSize(displayDimension);
		DISPLAY_WIDTH = displayDimension.x;
		DISPLAY_HEIGHT = displayDimension.y;

		int resizedWidth = 0;
		int resizedHeight = 0;

		resizedWidth = DISPLAY_WIDTH;
		if (puzzleBitmap.getWidth() < DISPLAY_WIDTH) {
			resizedHeight = (int) ((double) puzzleBitmap.getHeight() * ((double) DISPLAY_WIDTH / (double) puzzleBitmap
					.getWidth()));
		} else {
			resizedHeight = (int) ((double) puzzleBitmap.getHeight() / (double) ((double) puzzleBitmap
					.getWidth() / (double) DISPLAY_WIDTH));
		}

		if (resizedHeight < DISPLAY_WIDTH) {
			if (puzzleBitmap.getWidth() < DISPLAY_WIDTH) {
				while (true) {
					resizedWidth = (int) ((double) resizedWidth * (double) ((double) DISPLAY_WIDTH / (double) puzzleBitmap
							.getWidth()));
					resizedHeight = (int) ((double) resizedHeight * (double) ((double) DISPLAY_WIDTH / (double) puzzleBitmap
							.getWidth()));
					if (resizedHeight >= DISPLAY_WIDTH) {
						break;
					}
				}
			} else if (puzzleBitmap.getWidth() == DISPLAY_WIDTH) {
				while (true) {
					resizedWidth = resizedWidth + 20;
					resizedHeight = resizedHeight + 20;
					if (resizedHeight >= DISPLAY_WIDTH) {
						break;
					}
				}
			} else {
				while (true) {
					resizedWidth = (int) ((double) resizedWidth * (double) ((double) puzzleBitmap
							.getWidth() / (double) DISPLAY_WIDTH));
					resizedHeight = (int) ((double) resizedHeight * (double) ((double) puzzleBitmap
							.getWidth() / (double) DISPLAY_WIDTH));
					if (resizedHeight >= DISPLAY_WIDTH) {
						break;
					}
				}
			}
		}
		puzzleBitmap = Bitmap.createScaledBitmap(puzzleBitmap, resizedWidth,
				resizedHeight, false);

		int subtractWidth = (resizedWidth - DISPLAY_WIDTH) / 2;
		int subtractHeight = (resizedHeight - DISPLAY_WIDTH) / 2;
		int fromWidth = subtractWidth;
		int fromHeight = subtractHeight;
		int toHeight = resizedHeight - subtractHeight * 2;
		int toWidth = resizedWidth - subtractWidth * 2;

		puzzleBitmap = Bitmap.createBitmap(puzzleBitmap, fromWidth, fromHeight,
				toWidth, toHeight);

	}

	private void createTilesBitmapsList() {
		tilesBitmapsList = new ArrayList<>();

		tilesWidth = puzzleBitmap.getWidth() / puzzleWidth - TILES_DISTANCE * 2;
		tilesHeight = puzzleBitmap.getHeight() / puzzleWidth - TILES_DISTANCE
				* 2;

		int tempX = TILES_DISTANCE;
		int tempY = TILES_DISTANCE;

		for (int i = 0; i < puzzleType; i++) {
			Bitmap tempBitmap = Bitmap.createBitmap(puzzleBitmap, tempX, tempY,
					tilesWidth, tilesHeight, null, false);

			tilesBitmapsList.add(tempBitmap);

			if ((i + 1) % puzzleWidth == 0) {
				tempX = TILES_DISTANCE;
				tempY += tilesHeight + TILES_DISTANCE * 2;
			} else {
				tempX += tilesWidth + TILES_DISTANCE * 2;
			}
		}

	}

	public void refresh(Puzzle puzzle) {
		gameListener.stopWatchReset();
		gameListener.stopWatchStart();
		gameListener.updateTotalMoves(0);
		
		this.puzzle = puzzle;
		
		puzzleWidth = puzzle.getPuzzleType();
		this.puzzleType = puzzleWidth * puzzleWidth; // 3 * 3
		
		createTilesBitmapsList();
		setSolvedState(false);
		setRunningState(true);
		invalidate();
	}

	public boolean isPauseState() {
		return pauseState;
	}

	public void setPauseState(boolean pauseState) {
		this.pauseState = pauseState;
	}

	public Puzzle getPuzzle() {
		return puzzle;
	}

	public boolean isSolvedState() {
		return solvedState;
	}

	public void setSolvedState(boolean solvedState) {
		this.solvedState = solvedState;
	}

	public boolean isRunningState() {
		return runningState;
	}

	public void setRunningState(boolean runningState) {
		this.runningState = runningState;
	}

	public int getTotalMoves() {
		return totalMoves;
	}

	public void setTotalMoves(int totalMoves) {
		this.totalMoves = totalMoves;
	}

	public void setPuzzle(Puzzle puzzle) {
		puzzleWidth = puzzle.getPuzzleType();
		this.puzzleType = puzzleWidth * puzzleWidth; // 3 * 3
		this.puzzle = puzzle;
		setSolvedState(false);
	}
	
	public void setOnGameChangeListener(GameChangeListener gameListener) {
		this.gameListener = gameListener;
	}
}
