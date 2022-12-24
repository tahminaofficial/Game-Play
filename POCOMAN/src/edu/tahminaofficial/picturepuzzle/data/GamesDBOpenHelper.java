package edu.marufhassan.picturepuzzle.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class GamesDBOpenHelper extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "puzzle";
	private static final int DATABASE_VERSION = 1;

	public static final String TABLE_NAME = "score";
	public static final String COLUMN_TIME = "time";
	public static final String COLUMN_MOVES = "moves";
	public static final String COLUMN_PUZZLETYPE = "type";

	private static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME
			+ " ( " + COLUMN_TIME + " INTEGER, " + COLUMN_MOVES + " INTEGER, "
			+ COLUMN_PUZZLETYPE + " INTEGER );";

	public GamesDBOpenHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

}
