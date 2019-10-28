package com.example.translationtools;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
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

    private  static String TABLE_NAME;
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translation);

        db = new DBHelper(this);
        save = (Button) findViewById(R.id.save);
        save.setOnClickListener(this);

        inputField = findViewById(R.id.inputField);
        outputField = findViewById(R.id.outputField);

        inputField.setOnTouchListener(new OnSwipeTouchListener(this) {
            @Override
            public void onSwipeLeft() {
                super.onSwipeLeft();
                next();

            }


            @Override
            public void onSwipeRight() {
               super.onSwipeRight();
               prev();

            // Put your logic here for text visibility and for timer like progress bar for 5 second and setText
        }
    });

        TABLE_NAME = getIntent().getStringExtra("Table name");
        setData(TABLE_NAME);
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


            if (!cursor.isNull(cursor.getColumnIndex("translate_text"))) {
                outputField.setText(cursor.getString(cursor.getColumnIndex("translate_text")));
            }
        }
        // Здесь устанавливаем в поле текст, который вытащим из бд

    }

    public void next() {
        if (cursor.moveToNext())
        {
            inputField.setText(cursor.getString(cursor.getColumnIndex("original_text")));

            if (!cursor.isNull(cursor.getColumnIndex("translate_text"))) {
                outputField.setText(cursor.getString(cursor.getColumnIndex("translate_text")));
            }
        } else {
            /**
             * do smthg
             */
        }
    }

    public void prev() {

        if (cursor.moveToPrevious()) {
            inputField.setText(cursor.getString(cursor.getColumnIndex("original_text")));
        }
    }

    public void save() {


    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.save:
                save();
                Toast.makeText(getApplicationContext(),"Клик", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}