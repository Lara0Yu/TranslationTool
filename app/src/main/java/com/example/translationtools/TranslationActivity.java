package com.example.translationtools;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class TranslationActivity extends AppCompatActivity implements View.OnClickListener {


    private Button save;
    private Button next;
    private Button prev;
    Cursor cursor;
    private DBHelper db;
    private TextView inputField;
    private TextView outputField;
    private EditText pageNumField;
    private TextView jumpToPage;
    private EditText numbPage;
    private String currentParagraphId = "1";
    private String paragraphCount;

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
        numbPage = findViewById(R.id.numbPage);

        TABLE_NAME = getIntent().getStringExtra("Table name");
        setData(TABLE_NAME);


        numbPage.setOnKeyListener((v, keyCode, event) -> {
            if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                    (keyCode == KeyEvent.KEYCODE_ENTER)) {

                String currentId = numbPage.getText().toString();

                if (Integer.parseInt(currentId) >Integer.parseInt(paragraphCount))  {
                    Toast.makeText(getApplicationContext(),"Такого абзаца не существует!", Toast.LENGTH_SHORT).show();
                } else {
                    jumpToPage(currentId);
                }



                return true;
            }
            return false;
        });
    }

    public void setCurrentProgress() {

//        Toast.makeText(getApplicationContext(),currentParagraphId, Toast.LENGTH_SHORT).show();
//        Toast.makeText(getApplicationContext(),paragraphCount, Toast.LENGTH_SHORT).show();
        String s = currentParagraphId + "/" + paragraphCount;
        numbPage.setHint(s);
    }

    public void setData(String tableName) {
        SQLiteDatabase sqlDb = db.getWritableDatabase();
//        String[] tableColumns = new String[] {"original_text", "id", "status", "translate_text"};
//        String whereClause = "status = ?";
//        String[] whereArgs = new String[] {
//                "0"
//        };
//
//        cursor =
//                sqlDb.query(tableName,  tableColumns, whereClause,
//                        whereArgs, null, null, null);
//
//        if (cursor.moveToFirst()) {
//            inputField.setText(cursor.getString(cursor.getColumnIndex("original_text")));
//            //pageNumField.setText(cursor.getString(cursor.getColumnIndex("id")));
//            outputField.setText(cursor.getString(cursor.getColumnIndex("translate_text")));
//
//        }

        Cursor crsr  = sqlDb.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE status = 0 ORDER BY id asc limit 1;", null);

        if (crsr.moveToFirst()) {
            inputField.setText(crsr.getString(crsr.getColumnIndex("original_text")));
            //pageNumField.setText(cursor.getString(cursor.getColumnIndex("id")));
            outputField.setText(crsr.getString(crsr.getColumnIndex("translate_text")));
            currentParagraphId = crsr.getString(crsr.getColumnIndex("id"));

        }
        sqlDb.close();
        crsr.close();
        getParagraphCount();
        setCurrentProgress();
    }

    public void getParagraphCount() {
        SQLiteDatabase sqlDb = db.getWritableDatabase();

        Cursor crsr  = sqlDb.rawQuery("SELECT count(*) FROM " + TABLE_NAME + ";", null);
        if (crsr.moveToFirst()) {
            paragraphCount = crsr.getString(0);
        } else {
            paragraphCount = "0";
        }
        sqlDb.close();
        crsr.close();
    }

    public void next() {
        SQLiteDatabase sqlDb = db.getWritableDatabase();
        Cursor crsr  = sqlDb.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE status = 0 and id > " + currentParagraphId + " ORDER BY id asc limit 1;", null);
        if (crsr.moveToFirst()) {
            currentParagraphId = crsr.getString(crsr.getColumnIndex("id"));

            String originalText = crsr.getString(crsr.getColumnIndex("original_text"));
            String translateText = crsr.getString(crsr.getColumnIndex("translate_text"));

            inputField.setText(originalText);
            outputField.setText(translateText);
        } else {
            crsr  = sqlDb.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE status = 0 ORDER BY id asc limit 1;", null);
            if (crsr.moveToFirst()){
                currentParagraphId = crsr.getString(crsr.getColumnIndex("id"));

                String originalText = crsr.getString(crsr.getColumnIndex("original_text"));
                String translateText = crsr.getString(crsr.getColumnIndex("translate_text"));

                inputField.setText(originalText);
                outputField.setText(translateText);
                Toast.makeText(getApplicationContext(),"Начинаем с начала", Toast.LENGTH_SHORT).show();
            } else{
                Toast.makeText(getApplicationContext(),"ВЫ ВСЁ ПЕРЕВЕЛИ", Toast.LENGTH_SHORT).show();
            }

        }

        crsr.close();
        sqlDb.close();
    }

    public void prev() {
        SQLiteDatabase sqlDb = db.getWritableDatabase();
        Cursor crsr  = sqlDb.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE status = 0 and id < " + currentParagraphId + " ORDER BY id DESC limit 1;", null);
        if (crsr.moveToFirst()) {
            currentParagraphId = crsr.getString(crsr.getColumnIndex("id"));

            String originalText = crsr.getString(crsr.getColumnIndex("original_text"));
            String translateText = crsr.getString(crsr.getColumnIndex("translate_text"));

            inputField.setText(originalText);
            outputField.setText(translateText);
        } else {
            Toast.makeText(getApplicationContext(),"Everything is translated up to this point.", Toast.LENGTH_SHORT).show();
        }
        crsr.close();
        sqlDb.close();
    }

    public void save(boolean update) {

        SQLiteDatabase sqlDb = db.getWritableDatabase();
        Cursor crsr  = sqlDb.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE id = " + currentParagraphId + ";", null);
        if (crsr.moveToFirst()) {
            int currentStatus = crsr.getInt(crsr.getColumnIndex("status"));

            ContentValues cv = new ContentValues();

            if (!outputField.getText().toString().isEmpty()) {
                cv.put("translate_text", outputField.getText().toString());

                if (update) {
                    cv.put("status", "1");
                }
                if ((currentStatus == 0 && !update) || update) {
                    int updCount = sqlDb.update(TABLE_NAME, cv, "id = ?",
                            new String[]{currentParagraphId});
                }
            }
        }

        sqlDb.close();
        crsr.close();
    }

    public void jumpToPage(String paragraphId) {

        SQLiteDatabase sqlDb = db.getWritableDatabase();
        Cursor crsr  = sqlDb.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE id = " + paragraphId + ";", null);
        if (crsr.moveToFirst()) {
            currentParagraphId = crsr.getString(crsr.getColumnIndex("id"));

            String originalText = crsr.getString(crsr.getColumnIndex("original_text"));
            String translateText = crsr.getString(crsr.getColumnIndex("translate_text"));

            inputField.setText(originalText);
            outputField.setText(translateText);
        } else {
            Toast.makeText(
                    TranslationActivity.this, "Такого парграфа не существует.", Toast.LENGTH_SHORT).show();
        }

        sqlDb.close();
        crsr.close();
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
                save(false);
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