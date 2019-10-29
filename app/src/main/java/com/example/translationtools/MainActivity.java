package com.example.translationtools;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ListView startList;
    private static DBHelper db;
    private TextView test;
    private List dataList;
    private ListAdapter la;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        db =  new DBHelper(this);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(this);

        startList = (ListView) findViewById(R.id.listOfProject);
        dataList = new ArrayList<String>();
        setDataListView();

        startList.setOnItemClickListener((parent, view, position, id) -> {

            String tableName = (String) dataList.get(position);
            Toast.makeText(getApplicationContext(),tableName, Toast.LENGTH_SHORT).show();

            Intent newWindow = new Intent(MainActivity.this, TranslationActivity.class);
            newWindow.putExtra("Table name", tableName);
            startActivity(newWindow);

        });


    }

    /**
     *  Заполнение Списка уже созданных проектов из базы данных
     */
    public void setDataListView() {
        ContentValues cv = new ContentValues();
        SQLiteDatabase sqlDb = db.getWritableDatabase();

        Cursor c =
                sqlDb.query("Projects", null, null,
                        null, null, null, null);

        if (c.moveToFirst()) {

            // определяем номера столбцов по имени в выборке
            int nameColIndex = c.getColumnIndex("name");

            do {
                dataList.add(c.getString(nameColIndex));
            } while (c.moveToNext());
        } else {
            return;
        }

        //Создаем адаптер (переходник между списком и листвью)
        la = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dataList);


        //Назначаем адаптер листвью
        startList.setAdapter(la);

        c.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void showAlert() {
        AlertDialog.Builder alertDialog =
                new AlertDialog.Builder(MainActivity.this).setTitle("Новый проект");

        // Setting Dialog Message
        alertDialog.setMessage("Введите название проекта");
        final EditText input = new EditText(MainActivity.this);
        alertDialog.setView(input);

        // Setting Positive "Yes" Button
        alertDialog.setPositiveButton("Создать",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int which) {

                        SQLiteDatabase sqDb = db.getWritableDatabase();
                        ContentValues cv = new ContentValues();
                        cv.put("name", input.getText().toString());

                        sqDb.insert("Projects", null, cv);

                        sqDb.close();

                        createTable(input.getText().toString());

                        openFileManager();
                        dataList.add(input.getText().toString());
                        startList.invalidateViews();

                        Toast.makeText(getApplicationContext(),"Проект создан", Toast.LENGTH_SHORT).show();

                    }
                });
        // Setting Negative "Cancel" Button
        alertDialog.setNegativeButton("Отмена",
                (dialog, which) -> {
                    // Write your code here to execute after dialog
                    dialog.cancel();
                });

        alertDialog.show();
    }

    /**
     * Создание таблицы с заданным именем
     * @param tableName имя таблицы
     */
    private void createTable(String tableName) {

        SQLiteDatabase sqlHelper = db.getWritableDatabase();

        sqlHelper.execSQL("create table "  + tableName + " ("
                + "id integer primary key autoincrement,"
                + "original_text text NOT NULL,"
                + "translate_text text,"
                + "status integer,"
                + "text_id integer);");
    }


    private static final int READ_REQUEST_CODE = 42;
    /**
     *  Метод открывает менеджер файлов и позволяет пользователю
     *  выбрать необходимый файл формата .txt
     */
    public void openFileManager() {

        // ACTION_OPEN_DOCUMENT is the intent to choose a file via the system's file
        // browser.
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);

        // Filter to only show results that can be "opened", such as a
        // file (as opposed to a list of contacts or timezones)
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        // Filter to show only images, using the image MIME data type.
        // If one wanted to search for ogg vorbis files, the type would be "audio/ogg".
        // To search for all documents available via installed storage providers,
        // it would be "*/*".
        intent.setType("text/*");

        startActivityForResult(intent, READ_REQUEST_CODE);
        startActivityForResult(intent, RESULT_OK);

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {

        // The ACTION_OPEN_DOCUMENT intent was sent with the request code
        // READ_REQUEST_CODE. If the request code seen here doesn't match, it's the
        // response to some other intent, and the code below shouldn't run at all.

        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // The document selected by the user won't be returned in the intent.
            // Instead, a URI to that document will be contained in the return intent
            // provided to this method as a parameter.
            // Pull that URI using resultData.getData().
            String path = null;
            if (resultData != null) {
                path = resultData.getData().getPath();

                /*
                Здесь должен быть вызван код парсера.
                Парсер должен открыть по указанному пути
                файл и, разбив его по предложениям, занести в бд.
                 */

            }
        }
    }


    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.fab:
                showAlert();


                break;
        }

    }
}
