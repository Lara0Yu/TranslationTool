package com.example.translationtools;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
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
    }

    public void next() {

//        if (cursor.moveToNext()) {
//            inputField.setText(cursor.getString(cursor.getColumnIndex("original_text")));
//            if (!cursor.isNull(cursor.getColumnIndex("translate_text")))
//            {
//                outputField.setText(cursor.getString(cursor.getColumnIndex("translate_text")));
//            } else
//            {
//                outputField.setText("");
//            }
//        } else {
//            /**
//             * do smthg
//             */
//        }

        jumpToPage("5");
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
                save(false);
                next();
                break;
            case R.id.prev:
                prev();
                break;

        }
    }



    public void jumpToPage(String pageId) {
        SQLiteDatabase sqlDb = db.getWritableDatabase();

//        String[] tableColumns = new String[] {"original_text", "id", "status", "translate_text"};
//        String whereClause = "id = ?";
//        String[] whereArgs = new String[] {
//                pageId
//        };
//
//        Cursor specificPage =
//                sqlDb.query(TABLE_NAME,  tableColumns, whereClause,
//                        whereArgs, null, null, null);

        Cursor specificPage = sqlDb.rawQuery("SELECT id, original_text, translate_text, status FROM "
                + TABLE_NAME +
                " where id=" + pageId + ";", null);
//
        if (specificPage == null){
            Toast.makeText(getApplicationContext(),"specificPage is null!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (specificPage.moveToFirst()) {
            inputField.setText(specificPage.getString(specificPage.getColumnIndex("original_text")));
            outputField.setText(specificPage.getString(specificPage.getColumnIndex("translate_text")));

//            String[] cursorTableColumns = new String[] {"original_text", "id", "status", "translate_text"};
//            String cursorWhereClause = "status = ? and id < ?";
//            String[] cursorWhereArgs = new String[] {
//                    "0", pageId
//            };

            cursor = sqlDb.rawQuery("SELECT id, original_text, translate_text, status FROM "
                    + TABLE_NAME +
                    " where status=0 and id>" + pageId + ";", null);
//            cursor = sqlDb.query(TABLE_NAME,  cursorTableColumns, cursorWhereClause,
//                    cursorWhereArgs, null, null, null);

            if (cursor == null)
            {
                Toast.makeText(getApplicationContext(),"cursor is Null!", Toast.LENGTH_SHORT).show();
                return;
            }
            else {
//                Toast.makeText(getApplicationContext(),"cursor is not Null!", Toast.LENGTH_SHORT).show();

            }

            if (cursor.moveToFirst())
            {
                Toast.makeText(getApplicationContext(),"Next may not work!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(),"Nothing will work!", Toast.LENGTH_SHORT).show();
            };
        } else {
            Toast.makeText(getApplicationContext(),"Paragraph not found!", Toast.LENGTH_SHORT).show();
        };



    }
}

