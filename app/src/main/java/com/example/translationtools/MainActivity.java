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

import android.os.Environment;
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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
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
            Toast.makeText(getApplicationContext(),"ÐšÐ»Ð¸Ðº", Toast.LENGTH_SHORT).show();
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
                builder.setTitle("Ð’Ñ‹Ð³Ñ€ÑƒÐ·ÐºÐ° Ð¸Ð»Ð¸ ÑƒÐ´Ð°Ð»ÐµÐ½Ð¸Ð¸Ðµ Ð¿Ñ€Ð¾ÐµÐºÑ‚Ð°");
                builder.setMessage("Ð’Ñ‹ Ð´ÐµÐ¹ÑÑ‚Ð²Ð¸Ñ‚ÐµÐ»ÑŒÐ½Ð¾ Ñ…Ð¾Ñ‚Ð¸Ñ‚Ðµ Ð²Ñ‹Ð³Ñ€ÑƒÐ·Ð¸Ñ‚ÑŒ ÑÑ‚Ð¾Ñ‚ Ð¿Ñ€Ð¾ÐµÐºÑ‚?");

                builder.setPositiveButton("Ð’Ñ‹Ð³Ñ€ÑƒÐ·Ð¸Ñ‚ÑŒ", (dialog, which) -> {
                    String name = (String) dataList.get(pos);
                    try {
                        saveTranslation(name);
                    } catch (IOException e) {
                        System.out.println("oooooops");
                        e.printStackTrace();
                    }
                });
                builder.setNegativeButton("Ð£Ð´Ð°Ð»Ð¸Ñ‚ÑŒ Ð¿Ñ€Ð¾ÐµÐºÑ‚", (dialog, which) -> {
                    showDeleteWindow(pos);
                });
                AlertDialog dialog = builder.create();
                dialog.show();
                return true;
            }
        });
    }

    public void showDeleteWindow(int pos) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Ð£Ð´Ð°Ð»ÐµÐ½Ð¸Ðµ ");
        builder.setMessage("Ð’Ñ‹ Ð´ÐµÐ¹ÑÑ‚Ð²Ð¸Ñ‚ÐµÐ»ÑŒÐ½Ð¾ Ñ…Ð¾Ñ‚Ð¸Ñ‚Ðµ ÑƒÐ´Ð°Ð»Ð¸Ñ‚ÑŒ ÑÑ‚Ð¾Ñ‚ Ð¿Ñ€Ð¾ÐµÐºÑ‚?");

        builder.setPositiveButton("Ð£Ð´Ð°Ð»Ð¸Ñ‚ÑŒ", (dialog, which) -> {
            Toast.makeText(getApplicationContext(),
                    "ÐŸÑ€Ð¾ÐµÐºÑ‚ ÑƒÐ´Ð°Ð»ÐµÐ½.", Toast.LENGTH_SHORT).show();
            SQLiteDatabase sqLiteDatabase = db.getWritableDatabase();

            String name = (String) dataList.get(pos);
            System.out.println(name);
            sqLiteDatabase.delete("Projects", "name = \"" + name + "\"", null);
            sqLiteDatabase.delete(name, null, null);

            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + name + '"');

            dataList.remove(pos);
            startList.invalidateViews();

        });
        builder.setNegativeButton("ÐžÑ‚Ð¼ÐµÐ½Ð°", (dialog, which) -> {
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(),"Ð Ð°Ð·Ñ€ÐµÑˆÐµÐ½Ð¸Ñ Ð¿Ð¾Ð»ÑƒÑ‡ÐµÐ½Ñ‹", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(),"Ð¢ÐµÐºÐ°Ð¹ Ñ Ð³Ð¾Ñ€Ð¾Ð´Ñƒ, Ñ‚Ð¾Ð±i....", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     *  Ð—Ð°Ð¿Ð¾Ð»Ð½ÐµÐ½Ð¸Ðµ Ð¡Ð¿Ð¸ÑÐºÐ° ÑƒÐ¶Ðµ ÑÐ¾Ð·Ð´Ð°Ð½Ð½Ñ‹Ñ… Ð¿Ñ€Ð¾ÐµÐºÑ‚Ð¾Ð² Ð¸Ð· Ð±Ð°Ð·Ñ‹ Ð´Ð°Ð½Ð½Ñ‹Ñ…
     */
    public void setDataListView() {
        ContentValues cv = new ContentValues();
        SQLiteDatabase sqlDb = db.getWritableDatabase();

        Cursor c =
                sqlDb.query("Projects", null, null,
                        null, null, null, null);

        if (c.moveToFirst()) {

            // Ð¾Ð¿Ñ€ÐµÐ´ÐµÐ»ÑÐµÐ¼ Ð½Ð¾Ð¼ÐµÑ€Ð° ÑÑ‚Ð¾Ð»Ð±Ñ†Ð¾Ð² Ð¿Ð¾ Ð¸Ð¼ÐµÐ½Ð¸ Ð² Ð²Ñ‹Ð±Ð¾Ñ€ÐºÐµ
            int nameColIndex = c.getColumnIndex("name");

            do {
                dataList.add(c.getString(nameColIndex));
            } while (c.moveToNext());
        } else {
            return;
        }

        //Ð¡Ð¾Ð·Ð´Ð°ÐµÐ¼ Ð°Ð´Ð°Ð¿Ñ‚ÐµÑ€ (Ð¿ÐµÑ€ÐµÑ…Ð¾Ð´Ð½Ð¸Ðº Ð¼ÐµÐ¶Ð´Ñƒ ÑÐ¿Ð¸ÑÐºÐ¾Ð¼ Ð¸ Ð»Ð¸ÑÑ‚Ð²ÑŒÑŽ)
        la = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dataList);


        //ÐÐ°Ð·Ð½Ð°Ñ‡Ð°ÐµÐ¼ Ð°Ð´Ð°Ð¿Ñ‚ÐµÑ€ Ð»Ð¸ÑÑ‚Ð²ÑŒÑŽ
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
                new AlertDialog.Builder(MainActivity.this).setTitle("ÐÐ¾Ð²Ñ‹Ð¹ Ð¿Ñ€Ð¾ÐµÐºÑ‚");

        // Setting Dialog Message
        alertDialog.setMessage("Ð’Ð²ÐµÐ´Ð¸Ñ‚Ðµ Ð½Ð°Ð·Ð²Ð°Ð½Ð¸Ðµ Ð¿Ñ€Ð¾ÐµÐºÑ‚Ð°");
        final EditText input = new EditText(MainActivity.this);
        alertDialog.setView(input);

        // Setting Positive "Yes" Button
        alertDialog.setPositiveButton("Ð¡Ð¾Ð·Ð´Ð°Ñ‚ÑŒ",
                (dialog, which) -> {

                    if (spaceCheck(input.getText().toString())) {
                        Toast.makeText(getApplicationContext(),
                                "ÐÐ°Ð·Ð²Ð°Ð½Ð¸Ðµ Ð½Ðµ Ð´Ð¾Ð»Ð¶Ð½Ð¾ ÑÐ¾Ð´ÐµÑ€Ð¶Ð°Ñ‚ÑŒ Ð¿Ñ€Ð¾Ð±ÐµÐ»Ð¾Ð²", Toast.LENGTH_SHORT).show();
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

                    Toast.makeText(getApplicationContext(),"ÐŸÑ€Ð¾ÐµÐºÑ‚ ÑÐ¾Ð·Ð´Ð°Ð½", Toast.LENGTH_SHORT).show();

                });
        // Setting Negative "Cancel" Button
        alertDialog.setNegativeButton("ÐžÑ‚Ð¼ÐµÐ½Ð°",
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
     * Ð¡Ð¾Ð·Ð´Ð°Ð½Ð¸Ðµ Ñ‚Ð°Ð±Ð»Ð¸Ñ†Ñ‹ Ñ Ð·Ð°Ð´Ð°Ð½Ð½Ñ‹Ð¼ Ð¸Ð¼ÐµÐ½ÐµÐ¼
     * @param tableName Ð¸Ð¼Ñ Ñ‚Ð°Ð±Ð»Ð¸Ñ†Ñ‹
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
     *  ÐœÐµÑ‚Ð¾Ð´ Ð¾Ñ‚ÐºÑ€Ñ‹Ð²Ð°ÐµÑ‚ Ð¼ÐµÐ½ÐµÐ´Ð¶ÐµÑ€ Ñ„Ð°Ð¹Ð»Ð¾Ð² Ð¸ Ð¿Ð¾Ð·Ð²Ð¾Ð»ÑÐµÑ‚ Ð¿Ð¾Ð»ÑŒÐ·Ð¾Ð²Ð°Ñ‚ÐµÐ»ÑŽ
     *  Ð²Ñ‹Ð±Ñ€Ð°Ñ‚ÑŒ Ð½ÐµÐ¾Ð±Ñ…Ð¾Ð´Ð¸Ð¼Ñ‹Ð¹ Ñ„Ð°Ð¹Ð» Ñ„Ð¾Ñ€Ð¼Ð°Ñ‚Ð° .txt
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

    private String saveTranslation(String projectName) throws IOException {
//        System.out.println("!!!!!!!!");
        File root = getDir("data", 0);
        File textfile = new File(root, projectName + ".txt");
        System.out.println(root.toString() + projectName + ".txt");
        textfile.createNewFile();
        BufferedWriter out = new BufferedWriter(new FileWriter(textfile));
        SQLiteDatabase sqlDb = db.getWritableDatabase();
        Cursor crsr  = sqlDb.rawQuery("SELECT * from " + projectName + " ORDER BY id asc", null);
        if (crsr.moveToFirst()) {
            do{
                String paragraph = crsr.getString(crsr.getColumnIndex("translate_text"));
                String orig = crsr.getString(crsr.getColumnIndex("original_text"));
                if (paragraph != null){
                    out.write(paragraph);
                }
                else{
                    out.write(orig);
                }
                out.newLine();
            }while (crsr.moveToNext());
        }
        crsr.close();
        sqlDb.close();
        return root.toString() + projectName + ".txt";
    }

    private void parseFile_(Uri path, String projectName) throws FileNotFoundException {
        InputStream inputStream = getContentResolver().openInputStream(path);
        Scanner sc = new Scanner(inputStream);

        SQLiteDatabase sqDb = db.getWritableDatabase();
        while (sc.hasNextLine()) {
            ContentValues cv = new ContentValues();
            String tmp = sc.nextLine();
            if (!tmp.equals("")) {
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
        String tmp = "";
        String next;

        while (sc.hasNextLine()){
            while (tmp.equals("") && sc.hasNextLine()){
                tmp = sc.nextLine();
            }
            if (sc.hasNextLine()){
                next = sc.nextLine();
                if (!next.equals("")) {
                    if (!Character.isUpperCase(next.charAt(0))) {
                        tmp += next;
                    }
                    else {
                        ContentValues cv = new ContentValues();
                        cv.put("original_text", tmp);
                        cv.put("status", 0);
                        sqDb.insert(projectName, null, cv);
                        tmp = next;
                    }
                }
                else{
//                    System.out.println(tmp);
//                    System.out.println("!!!!!!!!!!!!!");
                    ContentValues cv = new ContentValues();
                    cv.put("original_text", tmp);
                    cv.put("status", 0);
                    sqDb.insert(projectName, null, cv);
                    tmp = "";
                }
            }
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
                            "Ð¢Ñ‹ ÐºÐ°Ðº Ð²ÑÐµÐ³Ð´Ð° Ð¾Ð±Ð»Ð°Ð¶Ð°Ð»ÑÑ", Toast.LENGTH_SHORT).show();
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
