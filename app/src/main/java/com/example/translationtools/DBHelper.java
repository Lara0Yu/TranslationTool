package com.example.translationtools;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

class DBHelper extends SQLiteOpenHelper {

    public DBHelper(Context context) {
        // конструктор суперкласса
        super(context, "myDB", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // создаем таблицу с полями
        db.execSQL("create table Projects ("
                + "id integer primary key autoincrement,"
                + "name text" + ");");
        db.execSQL("create table Sentences ("
                + "id integer primary key autoincrement,"
                + "original_text text NOT NULL,"
                + "translate_text text,"
                + "status integer,"
                + "text_id integer,"
                + "FOREIGN KEY(text_id) REFERENCES artist(id)" + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}