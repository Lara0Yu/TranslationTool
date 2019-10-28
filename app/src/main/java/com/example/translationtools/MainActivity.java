package com.example.translationtools;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ListView startList;
    private static DBHelper db;
    private TextView test;
    private List dataList;
    private ListAdapter la;

    private final static int PERMISSION_REQUEST_CODE =1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
        db =  new DBHelper(this);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(this);

        startList = (ListView) findViewById(R.id.listOfProject);
        dataList = new ArrayList<String>();
        setDataListView();

        startList.setOnItemClickListener((parent, view, position, id) -> {
            Toast.makeText(getApplicationContext(),"Клик", Toast.LENGTH_SHORT).show();
            String tableName = (String) dataList.get(position);
            Toast.makeText(getApplicationContext(),tableName, Toast.LENGTH_SHORT).show();

            Intent newWindow = new Intent(MainActivity.this, TranslationActivity.class);
            newWindow.putExtra("Table name", tableName);
            startActivity(newWindow);

        });

        startList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int pos, long id) {

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Удаление ");
                builder.setMessage("Вы действительно хотите удалить этот проект?");

                builder.setPositiveButton("Удалить", (dialog, which) -> {
                    Toast.makeText(getApplicationContext(),
                            "Проект удален.", Toast.LENGTH_SHORT).show();
                    SQLiteDatabase sqLiteDatabase = db.getWritableDatabase();

                    String name = (String) dataList.get(pos);
                    System.out.println(name);
                    sqLiteDatabase.delete("Projects", "name = \"" + name + "\"", null);
                    sqLiteDatabase.delete(name, null, null);

                    //sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + name + '"');

                    dataList.remove(pos);
                    startList.invalidateViews();

                });
                builder.setNegativeButton("Отмена", (dialog, which) -> {


                });

                AlertDialog dialog = builder.create();
                dialog.show();

                showDeleteWindow();

                return true;
            }
        });
    }

    public void showDeleteWindow() {


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(),"Разрешения получены", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(),"Текай с городу, тобi....", Toast.LENGTH_SHORT).show();
            }
        }
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


    private static String dbNAme;
    public void showAlert() {
        AlertDialog.Builder alertDialog =
                new AlertDialog.Builder(MainActivity.this).setTitle("Новый проект");

        // Setting Dialog Message
        alertDialog.setMessage("Введите название проекта");
        final EditText input = new EditText(MainActivity.this);
        alertDialog.setView(input);

        // Setting Positive "Yes" Button
        alertDialog.setPositiveButton("Создать",
                (dialog, which) -> {

                    if (spaceCheck(input.getText().toString())) {
                        Toast.makeText(getApplicationContext(),
                                "Название не должно содержать пробелов", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    dbNAme = input.getText().toString();

                    SQLiteDatabase sqDb = db.getWritableDatabase();
                    ContentValues cv    = new ContentValues();
                    cv.put("name", input.getText().toString());

                    sqDb.insert("Projects", null, cv);

                    sqDb.close();

                    createTable(dbNAme);

                    openFileManager();
                    dataList.add(dbNAme);
                    startList.invalidateViews();

                    Toast.makeText(getApplicationContext(),"Проект создан", Toast.LENGTH_SHORT).show();

                });
        // Setting Negative "Cancel" Button
        alertDialog.setNegativeButton("Отмена",
                (dialog, which) -> {
                    // Write your code here to execute after dialog
                    dialog.cancel();
                });

        alertDialog.show();
    }

    private boolean spaceCheck(String string) {

        if (string.contains(" ")) return true;

        return false;
    }

    /**
     * Создание таблицы с заданным именем
     * @param tableName имя таблицы
     */
    private void createTable(String tableName) {

        SQLiteDatabase sqlHelper = db.getWritableDatabase();

        sqlHelper.execSQL("create table "  + tableName + " ("
                + "id integer primary key autoincrement,"
                + "original_text text,"
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
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);


        // Filter to only show results that can be "opened", such as a
        // file (as opposed to a list of contacts or timezones)
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

        // Filter to show only images, using the image MIME data type.
        // If one wanted to search for ogg vorbis files, the type would be "audio/ogg".
        // To search for all documents available via installed storage providers,
        // it would be "*/*".
        intent.setType("text/*");

        startActivityForResult(intent, READ_REQUEST_CODE);

    }

    private void parseFile_(Uri path, String projectName) throws FileNotFoundException
    {
        InputStream inputStream = getContentResolver().openInputStream(path);
        Scanner sc = new Scanner(inputStream);

        SQLiteDatabase sqDb = db.getWritableDatabase();
        while (sc.hasNextLine()) {
            ContentValues cv = new ContentValues();
            String tmp = sc.nextLine();
            if (!tmp.equals(""))
            {
                cv.put("original_text", tmp);
                cv.put("status", 0);
                sqDb.insert(projectName, null, cv);
            }
        }
        sqDb.close();
    }

    private void parseFile(Uri path, String projectName) throws FileNotFoundException {
        InputStream inputStream = getContentResolver().openInputStream(path);
        Scanner sc = new Scanner(inputStream);

        SQLiteDatabase sqDb = db.getWritableDatabase();
        String next="";
        while (sc.hasNextLine()) {
            ContentValues cv = new ContentValues();
            String tmp = sc.nextLine();
            if (sc.hasNextLine()){
                next = sc.nextLine();

                while ((!next.equals("")) && sc.hasNextLine() && (!Character.isUpperCase(next.charAt(0)))) {
                    tmp = tmp + next;
                    next = sc.nextLine();
                }
            }
            if (!tmp.equals("")) {
                cv.put("original_text", tmp);
                cv.put("status", 0);
                sqDb.insert(projectName, null, cv);
            }

        }
        ContentValues cv = new ContentValues();
        if (!next.equals("")) {
            cv.put("original_text", next);
            cv.put("status", 0);
            sqDb.insert(projectName, null, cv);
        }
        sqDb.close();
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {

        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {

            if (resultData != null) {
                Uri uri = resultData.getData();
                try {
                    parseFile(uri, dbNAme);
                } catch (IOException e) {
                    Toast.makeText(getApplicationContext(),
                            "Ты как всегда облажался", Toast.LENGTH_SHORT).show();
                }
                //path = path.substring(path.indexOf(":") + 1);

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
