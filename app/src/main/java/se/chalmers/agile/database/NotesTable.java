package se.chalmers.agile.database;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class NotesTable {

    // Database table
    public static final String KEY_TITLE = "title";
    public static final String KEY_BODY = "body";
    public static final String KEY_ROWID = "_id";
    public static final String DATABASE_TABLE = "notes";

    // Database creation SQL statement
    private static final String DATABASE_CREATE = "create table "
            + DATABASE_TABLE
            + "("
            + KEY_ROWID + " integer primary key autoincrement, "
            + KEY_TITLE + " text not null,"
            + KEY_BODY  + " text not null"
            + ");";

    public static void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(NotesTable.class.getName(), "Upgrading database from version " + oldVersion + " to "
                + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS "+DATABASE_TABLE);
        onCreate(db);
    }
}

