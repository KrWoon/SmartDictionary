package com.explain;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by 박지운 on 2018-11-23.
 */

public class SQLite extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "dictionary.db";
    private static final String TABLE_NAME = "usedwords";
    private static final String COLUMN_WORD = "word";
    private static final String COLUMN_CALLS = "calls";

    public SQLite(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE usedwords (word text, calls integer)";
        db.execSQL(sql);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    /** Map의 모든 데이터 집어넣기 */
    public void insertAllData(HashMap<String, Integer> map) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();

        for( Map.Entry<String, Integer> elem : map.entrySet() ) {
            cv.put(COLUMN_WORD, elem.getKey());
            cv.put(COLUMN_CALLS, elem.getValue());
            db.insert(TABLE_NAME, null, cv);
        }

        db.close();
    }

    /** 모든 데이터 가져오기 */
    public HashMap<String, Integer> getAllData() {
        HashMap<String, Integer> map = new HashMap<String, Integer>();
        String selectQuery = "select * from " + TABLE_NAME;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if(cursor.moveToFirst()) {
            do {
                String word = cursor.getString(0);
                int calls = Integer.parseInt(cursor.getString(1));
                System.out.println(cursor.getString(0));
                map.put(word, calls);
            } while(cursor.moveToNext());
        }

        return map;
    }

    public void deleteData() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + TABLE_NAME);
    }

}

