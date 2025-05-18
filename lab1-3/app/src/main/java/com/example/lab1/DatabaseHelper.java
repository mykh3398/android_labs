package com.example.lab1;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "app_data.db";
    private static final int DATABASE_VERSION = 2;

    private static final String TABLE_NAME = "student_selection";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_FACULTY = "faculty";
    private static final String COLUMN_COURSE = "course";

    private static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_FACULTY + " TEXT, " +
            COLUMN_COURSE + " TEXT);";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void insertData(String faculty, String course) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_FACULTY, faculty);
        values.put(COLUMN_COURSE, course);

        long result = db.insert(TABLE_NAME, null, values);
        db.close();

        if (result == -1) {
            Log.e("DatabaseHelper", "Помилка при вставці даних");
        } else {
            Log.d("DatabaseHelper", "Вибір успішно збережено");
        }
    }

    public String getData() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);

        StringBuilder data = new StringBuilder();
        if (cursor.moveToFirst()) {
            do {
                data.append("Факультет: ").append(cursor.getString(1)).append("\n");
                data.append("Курс: ").append(cursor.getString(2)).append("\n\n");
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return data.toString().trim();
    }

    public void clearDatabase() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NAME);
        db.close();
    }
}
