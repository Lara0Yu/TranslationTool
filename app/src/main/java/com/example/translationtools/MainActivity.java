package com.example.translationtools;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.InputType;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ListView startList;
    private DBHelper db;
    private TextView test;
    private List dataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        db =  new DBHelper(this);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Creating alert Dialog with one Button
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);

                //AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();

                // Setting Dialog Title
                alertDialog.setTitle("Новый проект");

                // Setting Dialog Message
                alertDialog.setMessage("Введите название проекта");
                final EditText input = new EditText(MainActivity.this);
                alertDialog.setView(input);



                // Setting Positive "Yes" Button
                alertDialog.setPositiveButton("Создать",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int which) {
                                // Write your code here to execute after dialog
                                Toast.makeText(getApplicationContext(),"Проект создан", Toast.LENGTH_SHORT).show();

                                SQLiteDatabase sqDb = db.getWritableDatabase();
                                ContentValues cv = new ContentValues();
                                cv.put("name", input.getText().toString());

                                sqDb.insert("Projects", null, cv);

                                sqDb.close();




                                //Create new window
                                Intent intent =  new Intent(MainActivity.this, TranslationActivity.class);
                                startActivity(intent);


                            }
                        });
                // Setting Negative "Cancel" Button
                alertDialog.setNegativeButton("Отмена",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Write your code here to execute after dialog
                                dialog.cancel();
                            }
                        });

                alertDialog.show();
            }
        });

        startList = (ListView) findViewById(R.id.listOfProject);
        dataList = new ArrayList<String>();
        setDataListView();


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
        ListAdapter la = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dataList);

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
}
