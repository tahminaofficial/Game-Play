package edu.marufhassan.picturepuzzle.data;

public class Score {

	private long time;
	private int moves;
	private int puzzleType;

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public int getMoves() {
		return moves;
	}

	public void setMoves(int moves) {
		this.moves = moves;
	}

	public int getPuzzleType() {
		return puzzleType;
	}

	public void setPuzzleType(int puzzleType) {
		this.puzzleType = puzzleType;
	}
	
	@Override
	public String toString() {
		return getTime() + " " + getMoves();
	}

}
