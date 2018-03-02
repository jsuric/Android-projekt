package com.example.josipa.alias;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Josipa on 28.2.2018..
 */

public class DBAdapter {
    static final String KEY_ROWID = "_id";
    static final String KEY_WORD = "word";
    static final String KEY_FORBIDDEN = "forbidden";
    static final String TAG = "DBAdapter";

    static final String DATABASE_NAME = "MyDB";
    static final String DATABASE_TABLE = "words";
    static final int DATABASE_VERSION = 3;

    static final String DATABASE_CREATE =
            "create table words (_id integer primary key autoincrement, "
                    + "word text not null, forbidden text not null);";

    final Context context;

    DatabaseHelper DBHelper;
    SQLiteDatabase db;

    public DBAdapter(Context ctx)
    {
        this.context = ctx;
        DBHelper = new DatabaseHelper(context);
    }

    private static class DatabaseHelper extends SQLiteOpenHelper
    {
        DatabaseHelper(Context context)
        {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db)
        {
            try {
                db.execSQL(DATABASE_CREATE);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
        {
            Log.w(TAG, "Upgrading db from" + oldVersion + "to"
                    + newVersion );
            db.execSQL("DROP TABLE IF EXISTS words");
            onCreate(db);
        }
    }

    //---opens the database---
    public DBAdapter open() throws SQLException
    {
        db = DBHelper.getWritableDatabase();
        return this;
    }

    //---closes the database---
    public void close()
    {
        DBHelper.close();
    }

    //---insert a contact into the database---
    public long insertWord(String word, String forbidden)
    {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_WORD, word);
        initialValues.put(KEY_FORBIDDEN, forbidden);
        return db.insert(DATABASE_TABLE, null, initialValues);
    }


    //---retrieves all the contacts---
    public Cursor getAllWords()
    {
        return db.query(DATABASE_TABLE, new String[] {KEY_ROWID, KEY_WORD,
                KEY_FORBIDDEN}, null, null, null, null, null);
    }

    //---retrieves a particular contact---
    public Cursor getWord(long rowId) throws SQLException
    {
        Cursor mCursor =
                db.query(true, DATABASE_TABLE, new String[] {KEY_ROWID,
                                KEY_WORD, KEY_FORBIDDEN}, KEY_ROWID + "=" + rowId, null,
                        null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }
    //---deletes a particular contact---
    public boolean deleteWord(long rowId)
    {
        return db.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
    }

}
