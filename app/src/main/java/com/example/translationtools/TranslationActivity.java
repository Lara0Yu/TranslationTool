package com.example.translationtools;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class TranslationActivity extends AppCompatActivity implements View.OnClickListener {


    private Button save;
    private Button next;
    private Button prev;
    private DBHelper db;
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

    }

    public void next() {

    }

    public void prev() {

    }

    public void save() {

    }


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
                Toast.makeText(getApplicationContext(),"Клик", Toast.LENGTH_SHORT).show();
                break;

        }
    }
}
