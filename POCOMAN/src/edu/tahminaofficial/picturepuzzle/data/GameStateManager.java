package edu.marufhassan.picturepuzzle.data;

import android.content.Context;
import android.content.SharedPreferences;

public class GameStateManager {

	public static final String PREFS_NAME = "gameState";
	
	public static final String NUMBER_OF_MOVES = "moves";
	public static final String ELAPSED_TIME = "times";
	public static final String TILES_POSITION = "tilesPosition_";
	public static final String TILES_NUMBER = "tilesNumber";
	public static final String PATH_NAME = "pathname";
	public static final String GAME_STATE = "saved";
	public static final String PUZZLE_TYPE = "puzzleType";
	public static final String RUNNING_STATE = "runningState";
	public static final String SOUND_STATE = "soundState";
	
	private SharedPreferences gameStatePrefs;
	
	public GameStateManager(Context context) {
		gameStatePrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
	}
	
	public void saveGameState(GameState gameState) {
		SharedPreferences.Editor editor = gameStatePrefs.edit();
		editor.putBoolean(GAME_STATE, true);
		editor.putLong(ELAPSED_TIME, gameState.getElapsedTime());
		editor.putInt(NUMBER_OF_MOVES, gameState.getTotalMoves());
		int[] tilesPosition = gameState.getTilesPosition();
		for (int i = 0; i < gameState.getTilesPosition().length; i++) {
			editor.putInt(TILES_POSITION + i, tilesPosition[i]);
		}
		editor.putInt(TILES_NUMBER, gameState.getTilesPosition().length);
		editor.putInt(PUZZLE_TYPE, gameState.getPuzzleType());
		editor.putBoolean(RUNNING_STATE, gameState.isRunningState());
		editor.commit();
	}
	
	public GameState getSavedGameState() {
		GameState gameState = new GameState();
		gameState.setElapsedTime(gameStatePrefs.getLong(ELAPSED_TIME, 0));
		gameState.setTotalMoves(gameStatePrefs.getInt(NUMBER_OF_MOVES, 0));
		
		int size = gameStatePrefs.getInt(TILES_NUMBER, 0);
		int[] tilesPosition = new int[size];
		for (int i = 0; i < size; i++) {
			tilesPosition[i] = gameStatePrefs.getInt(TILES_POSITION + i, 0);
		}
		gameState.setTilesPosition(tilesPosition);
		gameState.setPuzzleType(gameStatePrefs.getInt(PUZZLE_TYPE, 0));
		gameState.setRunningState(gameStatePrefs.getBoolean(RUNNING_STATE, false));
		return gameState;
	}
	
	public String getValueFromKey(String key) {
		return gameStatePrefs.getString(key, null);
	}

	public void setPathName(String pathName) {
		SharedPreferences.Editor editor = gameStatePrefs.edit();
		editor.putString(PATH_NAME, pathName);
		editor.commit();
	}
	
	public boolean isSavedGame() {
		return gameStatePrefs.getBoolean(GAME_STATE, false);
	}
	
	public void setSavedGame(boolean savedGame) {
		SharedPreferences.Editor editor = gameStatePrefs.edit();
		editor.putBoolean(GAME_STATE, savedGame);
		editor.commit();
	}
	
	public void setSoundState(boolean soundState) {
		SharedPreferences.Editor editor = gameStatePrefs.edit();
		editor.putBoolean(SOUND_STATE, soundState);
		editor.commit();
	}
	
	public boolean isSoundEnabled() {
		return gameStatePrefs.getBoolean(SOUND_STATE, false);
	}
}
