package com.example.mas;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * @author Administrator
 * @des ${TODO}
 * @verson $Rev$
 * @updateAuthor $Author$
 * @updateDes ${TODO}
 */
public class DBAdapter {

    private static final String TAG = "DBAdapter";
    public static final String KEY_ROWID = "_id";
    public static final String KEY_ADMINNO = "adminNo";
    public static final String KEY_NAME = "name";
    public static final String KEY_SCG = "scg";
    public static final String KEY_MYPROFILE_URL = "myProfileUrl";
    public static final String KEY_MYPROFILE_HEIGHT = "myProfileHeight";
    public static final String KEY_MYPROFILE_WIDTH = "myProfileWidth";
    public static final String KEY_TIMETABLE_URL = "timeTableUrl";
    public static final String KEY_TIMETABLE_HEIGHT = "timeTableHeight";
    public static final String KEY_TIMETABLE_WIDTH  = "timeTableWidth";

    private static final String DATABASE_NAME = "MAS";
    private static final String DATABASE_TABLE = "student";
    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_CREATE =
            "create table student (" +
                    "_id integer primary key autoincrement, " +
                    "adminNo text not null, " +
                    "name text, " +
                    "scg text," +
                    "myProfileUrl text," +
                    "myProfileHeight integer," +
                    "myProfileWidth integer," +
                    "timeTableUrl text," +
                    "timeTableHeight integer," +
                    "timeTableWidth integer); ";

    private Context context;

    private DatabaseHelper DBHelper;
    private SQLiteDatabase db;

    public DBAdapter(Context ctx) {
        this.context = ctx;
        DBHelper = new DatabaseHelper(context);
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        public void onCreate(SQLiteDatabase db) {
            try {
                db.execSQL(DATABASE_CREATE);
            }
            catch(SQLException e) {
                e.printStackTrace();
            }
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.d(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS student");
            onCreate(db);
        }
    }

    public DBAdapter open() throws SQLException {
        db = DBHelper.getWritableDatabase();
        return this;
    }

    //‐‐‐closes the database‐‐‐
    public void close() {
        DBHelper.close();
    }

    public void insertAdminNo(String adminNo) {
        String sqlStmt = "Insert into " + DATABASE_TABLE +
                " (" + KEY_ADMINNO + ") " +
                "VALUES ('" + adminNo + "')";
        db.execSQL(sqlStmt);
    }

    public void updateNameByAdminNo(String adminNo, String name) {
        String sqlStmt = "Update " + DATABASE_TABLE + " SET " +
                KEY_NAME + " = '" + name + "'" +
                " where " + KEY_ADMINNO + " = '" + adminNo + "'";
        db.execSQL(sqlStmt);
    }

    public void updateSCGByAdminNo(String adminNo, String scg) {
        String sqlStmt = "Update " + DATABASE_TABLE + " SET " +
                KEY_SCG + " = '" + scg + "'" +
                " where " + KEY_ADMINNO + " = '" + adminNo + "'";
        db.execSQL(sqlStmt);
    }

    public Cursor getInfoByAdminNo(String adminNo) {
        Cursor mCursor = db.rawQuery(
                "Select adminNo, name, scg " +
                    "from " + DATABASE_TABLE + " " +
                    "where " + KEY_ADMINNO + " = '" + adminNo + "'", null);
        return mCursor;
    }

    public Cursor getTimeTableByAdminNo(String adminNo) {
        Cursor mCursor = db.rawQuery(
                "Select adminNo, timeTableUrl, timeTableHeight, timeTableWidth " +
                    "from " + DATABASE_TABLE + " " +
                    "where " + KEY_ADMINNO + " = '" + adminNo + "'", null);
        return mCursor;
    }

    public void updateTimeTableByAdminNo(String adminNo, String timeTableUrl, int timeTableHeight, int timeTableWidth) {
        String sqlStmt = "Update " + DATABASE_TABLE + " SET " +
                KEY_TIMETABLE_URL + " = '" + timeTableUrl + "', " +
                KEY_TIMETABLE_HEIGHT + " = " + timeTableHeight + ", " +
                KEY_TIMETABLE_WIDTH + " = " + timeTableWidth +
                " where " + KEY_ADMINNO + " = '" + adminNo + "'";
        db.execSQL(sqlStmt);
    }

    public Cursor getMyProfileByAdminNo(String adminNo) {
        Cursor mCursor = db.rawQuery(
                "Select adminNo, name, myProfileUrl, myProfileHeight, myProfileWidth " +
                        "from " + DATABASE_TABLE + " " +
                        "where " + KEY_ADMINNO + " = '" + adminNo + "'", null);
        return mCursor;
    }

    public void updateMyProfileByAdminNo(String adminNo, String myProfileUrl, int myProfileHeight, int myProfileWidth) {
        String sqlStmt = "Update " + DATABASE_TABLE + " SET " +
                KEY_MYPROFILE_URL + " = '" + myProfileUrl + "', " +
                KEY_MYPROFILE_HEIGHT + " = " + myProfileHeight + ", " +
                KEY_MYPROFILE_WIDTH + " = " + myProfileWidth +
                " where " + KEY_ADMINNO + " = '" + adminNo + "'";
        db.execSQL(sqlStmt);
    }
}
