package edu.marufhassan.picturepuzzle.data;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class GamesDatasource {
	private SQLiteOpenHelper helper;
	private SQLiteDatabase database;
	Context context;

	private static final String[] COLUMNS = { GamesDBOpenHelper.COLUMN_TIME,
			GamesDBOpenHelper.COLUMN_MOVES, GamesDBOpenHelper.COLUMN_PUZZLETYPE };

	public GamesDatasource(Context context) {
		helper = new GamesDBOpenHelper(context);
		this.context = context;
	}

	public void openDatabase() {
		database = helper.getWritableDatabase();
	}

	public void closeDatabase() {
		helper.close();
	}

	public boolean addScore(Score score) {
		List<Score> list = getScores(score.getPuzzleType(), " DESC");
		ContentValues values = new ContentValues();
		values.put(GamesDBOpenHelper.COLUMN_TIME, score.getTime());
		values.put(GamesDBOpenHelper.COLUMN_MOVES, score.getMoves());
		values.put(GamesDBOpenHelper.COLUMN_PUZZLETYPE, score.getPuzzleType());

		if (getCount(score.getPuzzleType()) < 5) {
			database.insert(GamesDBOpenHelper.TABLE_NAME, null, values);
		} else {
			Score temp = list.get(0);
			if (score.getTime() > temp.getTime()) {
				return false;
			} else if (score.getTime() == temp.getTime()) {
				if (score.getMoves() < temp.getMoves()) {
					database.update(GamesDBOpenHelper.TABLE_NAME, values,
							GamesDBOpenHelper.COLUMN_TIME + " = ?",
							new String[] { String.valueOf(temp.getTime()) });
				} else {
					return false;
				}
			} else {
				database.delete(GamesDBOpenHelper.TABLE_NAME,
						GamesDBOpenHelper.COLUMN_TIME + " = ?",
						new String[] { String.valueOf(temp.getTime()) });
				database.insert(GamesDBOpenHelper.TABLE_NAME, null, values);
			}
		}
		return true;
	}

	public List<Score> getScores(int puzzleType, String orderBy) {
		List<Score> list = null;

		Cursor cursor = database.query(GamesDBOpenHelper.TABLE_NAME, COLUMNS,
				GamesDBOpenHelper.COLUMN_PUZZLETYPE + " = ?",
				new String[] { String.valueOf(puzzleType) }, null, null,
				GamesDBOpenHelper.COLUMN_TIME + orderBy, null);
		if (cursor.getCount() > 0) {
			list = new ArrayList<>();
			while (cursor.moveToNext()) {
				Score score = new Score();
				score.setTime(cursor.getInt(cursor
						.getColumnIndex(GamesDBOpenHelper.COLUMN_TIME)));
				score.setMoves(cursor.getInt(cursor
						.getColumnIndex(GamesDBOpenHelper.COLUMN_MOVES)));
				score.setPuzzleType(cursor.getInt(cursor
						.getColumnIndex(GamesDBOpenHelper.COLUMN_PUZZLETYPE)));
				list.add(score);
			}
		}
		return list;
	}

	public int getCount(int puzzleType) {
		Cursor cursor = database.query(GamesDBOpenHelper.TABLE_NAME, COLUMNS,
				GamesDBOpenHelper.COLUMN_PUZZLETYPE + " = ?",
				new String[] { String.valueOf(puzzleType) }, null, null, null,
				null);
		return cursor.getCount();
	}

}
