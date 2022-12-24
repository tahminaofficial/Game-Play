package edu.marufhassan.picturepuzzle;


public interface GameChangeListener {

	public void updateTotalMoves(int totalMoves);
	
	public void updatePlayButton();
	
	public void updatePauseButton();
	
	public void stopWatchStart();
	
	public void stopWatchStop();
	
	public void stopWatchReset();
	
	public void updateScore();
	
	public void playTilesClick();
	
	public void playCongratulationSound();
	
}
