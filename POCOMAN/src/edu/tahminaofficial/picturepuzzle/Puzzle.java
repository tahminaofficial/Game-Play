package edu.marufhassan.picturepuzzle;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Puzzle {
	public static final String LOG_TAG = "puzzle";

	public static final int DIRECTION_LEFT = 0;
	public static final int DIRECTION_UP = 1;
	public static final int DIRECTION_RIGHT = 2;
	public static final int DIRECTION_DOWN = 3;

	public static final int[] DIRECTION_X = { -1, 0, +1, 0 };
	public static final int[] DIRECTION_Y = { 0, -1, 0, +1 };

	private int handleLocation;

	private Random random = new Random();

	private int directionTop = -1;
	private int directionLeft = -1;
	private int directionRight = -1;
	private int directionBottom = -1;

	private boolean moveToTop = false;
	private boolean moveToBottom = false;
	private boolean moveToRight = false;
	private boolean moveToLeft = false;

	private int totalMoves;

	private int totalTiles;
	private int puzzleType;

	private int tiles[];

	public Puzzle(int puzzleType) {
		this.puzzleType = puzzleType;
		totalTiles = puzzleType * puzzleType;
		totalMoves = 0;
		tiles = new int[puzzleType * puzzleType];

		for (int i = 0; i < tiles.length; i++) {
			tiles[i] = i;
		}

		handleLocation = tiles.length - 1;
		shuffle();
	}

	public void setTiles(int[] tiles) {
		this.tiles = tiles;
	}

	public int getColumnAt(int location) {
		return location % puzzleType;
	}

	public int getRowAt(int location) {
		return location / puzzleType;
	}
	
	public int distance() {
		int dist = 0;

		for (int i = 0; i < tiles.length; i++) {
			dist += Math.abs(i - tiles[i]);
		}

		return dist;
	}

	public void moveTiles(int move) {
		if (moveToBottom) {
			int temp = tiles[move];
			tiles[move] = tiles[move + puzzleType];
			tiles[move + puzzleType] = temp;
			totalMoves++;
		} else if (moveToRight) {
			int temp = tiles[move];
			tiles[move] = tiles[move + 1];
			tiles[move + 1] = temp;
			totalMoves++;
		} else if (moveToLeft) {
			int temp = tiles[move];
			tiles[move] = tiles[move - 1];
			tiles[move - 1] = temp;
			totalMoves++;
		} else if (moveToTop) {
			int temp = tiles[move];
			tiles[move] = tiles[move - puzzleType];
			tiles[move - puzzleType] = temp;
			totalMoves++;
		}

		moveToTop = false;
		moveToBottom = false;
		moveToLeft = false;
		moveToRight = false;
	}

	public boolean checkMove(int move) {
		directionTop = move - puzzleType;
		directionBottom = move + puzzleType;
		directionLeft = move - 1;
		directionRight = move + 1;

		try {
			if (directionTop >= 0 && tiles[directionTop] == totalTiles - 1) {
				moveToTop = true;
				return true;
			} else if (directionBottom <= totalTiles - 1
					&& tiles[directionBottom] == totalTiles - 1) {
				moveToBottom = true;
				return true;
			} else if (directionLeft >= 0
					&& tiles[directionLeft] == totalTiles - 1) {
				if (move % puzzleType != 0) {
					moveToLeft = true;
					return true;
				}
			} else if (directionRight <= totalTiles - 1
					&& tiles[directionRight] == totalTiles - 1) {
				if ((move + 1) % puzzleType != 0) {
					moveToRight = true;
					return true;
				}
			}
		} catch (Exception e) {
		}
		return false;
	}

	public boolean isSolved() {
		for (int i = 0; i < tiles.length; i++) {
			if (tiles[i] != i) {
				return false;
			}
		}
		return true;
	}

	public void shuffle() {
		if (puzzleType < 2) {
			return;
		}

		int limit = puzzleType * puzzleType * Math.max(puzzleType, puzzleType);
		int move = 0;

		while (distance() < limit) {

			move = pickRandomMove(invertMove(move));
			moveTile(move, 1);
		}

	}

	private int invertMove(int move) {
		if (move == 0) {
			return 0;
		}

		if (move == 1 << DIRECTION_LEFT) {
			return 1 << DIRECTION_RIGHT;
		}

		if (move == 1 << DIRECTION_UP) {
			return 1 << DIRECTION_DOWN;
		}

		if (move == 1 << DIRECTION_RIGHT) {
			return 1 << DIRECTION_LEFT;
		}

		if (move == 1 << DIRECTION_DOWN) {
			return 1 << DIRECTION_UP;
		}

		return 0;
	}

	private int pickRandomMove(int exclude) {
		List<Integer> moves = new ArrayList<Integer>(4);
		int possibleMoves = getPossibleMoves() & ~exclude;

		if ((possibleMoves & (1 << DIRECTION_LEFT)) > 0) {
			moves.add(DIRECTION_LEFT);
		}

		if ((possibleMoves & (1 << DIRECTION_UP)) > 0) {
			moves.add(DIRECTION_UP);
		}

		if ((possibleMoves & (1 << DIRECTION_RIGHT)) > 0) {
			moves.add(DIRECTION_RIGHT);
		}

		if ((possibleMoves & (1 << DIRECTION_DOWN)) > 0) {
			moves.add(DIRECTION_DOWN);
		}

		return moves.get(random.nextInt(moves.size()));
	}

	public boolean moveTile(int direction, int count) {
		boolean match = false;

		for (int i = 0; i < count; i++) {
			int targetLocation = handleLocation + DIRECTION_X[direction]
					+ DIRECTION_Y[direction] * puzzleType;
			tiles[handleLocation] = tiles[targetLocation];
			match |= tiles[handleLocation] == handleLocation;
			tiles[targetLocation] = tiles.length - 1; // handle tile
			handleLocation = targetLocation;
		}
		return match;
	}

	public boolean moveTile(int direction) {
		boolean match = false;

		int targetLocation = handleLocation + DIRECTION_X[direction]
				+ DIRECTION_Y[direction] * puzzleType;
		tiles[handleLocation] = tiles[targetLocation];
		match |= tiles[handleLocation] == handleLocation;
		tiles[targetLocation] = tiles.length - 1; // handle tile
		handleLocation = targetLocation;
		return match;
	}

	public int getPossibleMoves() {
		int x = getColumnAt(handleLocation);
		int y = getRowAt(handleLocation);

		boolean left = x > 0;
		boolean right = x < puzzleType - 1;
		boolean up = y > 0;
		boolean down = y < puzzleType - 1;

		return (left ? 1 << DIRECTION_LEFT : 0)
				| (right ? 1 << DIRECTION_RIGHT : 0)
				| (up ? 1 << DIRECTION_UP : 0)
				| (down ? 1 << DIRECTION_DOWN : 0);
	}

	public int getDirection(int location) {
		int delta = location - handleLocation;
		if (delta % puzzleType == 0) {
			return delta < 0 ? DIRECTION_UP : DIRECTION_DOWN;
		} else if (handleLocation / puzzleType == (handleLocation + delta)
				/ puzzleType) {
			return delta < 0 ? DIRECTION_LEFT : DIRECTION_RIGHT;
		} else {
			return -1;
		}
	}

	public int[] getTiles() {
		return tiles;
	}

	public int getHandleLocation() {
		return handleLocation;
	}

	public int getTotalMoves() {
		return totalMoves;
	}

	public void setTotalMoves(int totalMoves) {
		this.totalMoves = totalMoves;
	}
	
	public int getTotalTiles() {
		return totalTiles;
	}

	public void setTotalTiles(int totalTiles) {
		this.totalTiles = totalTiles;
	}

	public int getPuzzleType() {
		return puzzleType;
	}

	public void setPuzzleType(int puzzleType) {
		this.puzzleType = puzzleType;
	}

	public static int getDirectionLeft() {
		return DIRECTION_LEFT;
	}

	public static int getDirectionRight() {
		return DIRECTION_RIGHT;
	}

	public static int getDirectionDown() {
		return DIRECTION_DOWN;
	}

	public boolean isMoveToBottom() {
		return moveToBottom;
	}
}
