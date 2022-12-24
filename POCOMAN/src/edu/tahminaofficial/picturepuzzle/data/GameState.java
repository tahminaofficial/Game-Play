package edu.marufhassan.picturepuzzle.data;


public class GameState {

	private int totalMoves;
	private long elapsedTime;
	private String pathName;
	private int[] tilesPosition;
	private int puzzleType;
	private boolean runningState;

	public int getTotalMoves() {
		return totalMoves;
	}

	public void setTotalMoves(int totalMoves) {
		this.totalMoves = totalMoves;
	}

	public long getElapsedTime() {
		return elapsedTime;
	}

	public void setElapsedTime(long elapsedTime) {
		this.elapsedTime = elapsedTime;
	}

	public int[] getTilesPosition() {
		return tilesPosition;
	}

	public void setTilesPosition(int[] tilesPosition) {
		this.tilesPosition = tilesPosition;
	}

	public String getPathName() {
		return pathName;
	}

	public void setPathName(String pathName) {
		this.pathName = pathName;
	}

	public int getPuzzleType() {
		return puzzleType;
	}

	public void setPuzzleType(int puzzleType) {
		this.puzzleType = puzzleType;
	}

	public boolean isRunningState() {
		return runningState;
	}

	public void setRunningState(boolean runningState) {
		this.runningState = runningState;
	}

}
