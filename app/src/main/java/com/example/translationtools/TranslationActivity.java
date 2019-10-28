package com.example.translationtools;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class TranslationActivity extends AppCompatActivity implements View.OnClickListener {


    private Button save;
    private Button next;
    private Button prev;
//    private int currentId;
    Cursor cursor;
    private DBHelper db;
    private TextView inputField;
    private TextView outputField;
    List list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translation);

        db = new DBHelper(this);
        save = (Button) findViewById(R.id.save);
        save.setOnClickListener(this);

        next = (Button) findViewById(R.id.next);
        next.setOnClickListener(this);

        prev = (Button) findViewById(R.id.prev);
        prev.setOnClickListener(this);


        inputField = findViewById(R.id.inputField);
        outputField = findViewById(R.id.outputField);

        String paramValue = getIntent().getStringExtra("Table name");
        setData(paramValue);
    }

    public void setData(String tableName) {
        SQLiteDatabase sqlDb = db.getWritableDatabase();
        String[] tableColumns = new String[] {"original_text"};
        String whereClause = "status = ?";
        String[] whereArgs = new String[] {
                "0"
        };

        cursor =
                sqlDb.query(tableName,  tableColumns, whereClause,
                        whereArgs, null, null, null);

        if (cursor.moveToFirst()) {
            inputField.setText(cursor.getString(cursor.getColumnIndex("original_text")));

            outputField.setText(cursor.getString(cursor.getColumnIndex("original_text")));
//            currentId = cursor.getInt(cursor.getColumnIndex("id"));
        }
        // Здесь устанавливаем в поле текст, который вытащим из бд

    }

    public void next() {

    // Хотим идти на следующее предложение
        // Сохраняем то что есть в поле транслайтед в БД если не была нажата кнопка сейв

        if (cursor.moveToNext())
        {
            inputField.setText(cursor.getString(cursor.getColumnIndex("original_text")));
        } else {
            /**
             * do smthg
             */
        }
    }

    public void prev() {

        if (cursor.moveToPrevious()) {
            inputField.setText(cursor.getString(cursor.getColumnIndex("original_text")));
            o
        }

    }

    public void save() {

    }

    int currentIndex = 1;
    String str;
    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.save:
                Toast.makeText(getApplicationContext(),"Клик", Toast.LENGTH_SHORT).show();
                break;
            case R.id.prev:
                Toast.makeText(getApplicationContext(),"Клик", Toast.LENGTH_SHORT).show();
                break;
            case R.id.next:
                next();

                Toast.makeText(getApplicationContext(),"Клик", Toast.LENGTH_SHORT).show();
                break;

        }
    }
}
