package com.example.translationtools;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
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
    private EditText pageNumField;
    private TextView jumpToPage;
    List list;

    private  static String TABLE_NAME;
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


        TABLE_NAME = getIntent().getStringExtra("Table name");
        setData(TABLE_NAME);
    }

    public void setData(String tableName) {
        SQLiteDatabase sqlDb = db.getWritableDatabase();
        String[] tableColumns = new String[] {"original_text", "id", "status", "translate_text"};
        String whereClause = "status = ?";
        String[] whereArgs = new String[] {
                "0"
        };

        cursor =
                sqlDb.query(tableName,  tableColumns, whereClause,
                        whereArgs, null, null, null);

        if (cursor.moveToFirst()) {
            inputField.setText(cursor.getString(cursor.getColumnIndex("original_text")));
            //pageNumField.setText(cursor.getString(cursor.getColumnIndex("id")));
            outputField.setText(cursor.getString(cursor.getColumnIndex("translate_text")));

        }
        // Здесь устанавливаем в поле текст, который вытащим из бд

    }

    public void next() {

        // Хотим идти на следующее предложение
        // Сохраняем то что есть в поле транслайтед в БД если не была нажата кнопка сейв

        if (cursor.moveToNext())
        {
            inputField.setText(cursor.getString(cursor.getColumnIndex("original_text")));
            if (!cursor.isNull(cursor.getColumnIndex("translate_text")))
            {
                outputField.setText(cursor.getString(cursor.getColumnIndex("translate_text")));
            } else
            {
                outputField.setText("");
            }
        } else {
            /**
             * do smthg
             */
        }
    }

    public void prev() {

        if (cursor.moveToPrevious()) {

            int position = cursor.getPosition();

            SQLiteDatabase sqlDb = db.getWritableDatabase();
            String[] tableColumns = new String[] {"original_text", "id", "status", "translate_text"};
            String whereClause = "status = ?";
            String[] whereArgs = new String[] {
                    "0"
            };

            cursor =
                    sqlDb.query(TABLE_NAME,  tableColumns, whereClause,
                            whereArgs, null, null, null);

            cursor.moveToPosition(position);

            inputField.setText(cursor.getString(cursor.getColumnIndex("original_text")));
            //pageNumField.setText(cursor.getString(cursor.getColumnIndex("id")));

            if (!cursor.isNull(cursor.getColumnIndex("translate_text"))) {
                outputField.setText(cursor.getString(cursor.getColumnIndex("translate_text")));
//                outputField.setText(prevCursor.getString(prevCursor.getColumnIndex("translate_text")));
            } else {
                outputField.setText("");
            }

            sqlDb.close();
        }

    }

    public void save(boolean update) {
        String currentId = cursor.getString(cursor.getColumnIndex("id"));
        int currentStatus = cursor.getInt(cursor.getColumnIndex("status"));

        SQLiteDatabase sqlDb = db.getWritableDatabase();

        ContentValues cv = new ContentValues();

        if (!outputField.getText().toString().isEmpty())
        {
            cv.put("translate_text", outputField.getText().toString());

            if (update)
            {
                cv.put("status", "1");
            }
            if ((currentStatus == 0 && !update) || update) {
                int updCount = sqlDb.update(TABLE_NAME, cv, "id = ?",
                        new String[]{currentId});
            }
        }
        sqlDb.close();

    }



    int currentIndex = 1;
    String str;
    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.save:
                save(true);
                Toast.makeText(getApplicationContext(),"Клик", Toast.LENGTH_SHORT).show();
                break;
            case R.id.next:
                next();
                break;
            case R.id.prev:
                prev();
                break;

        }
    }


    public void jumpToPage() {
        Toast.makeText(getApplicationContext(),"ffffff", Toast.LENGTH_SHORT).show();
    }
}