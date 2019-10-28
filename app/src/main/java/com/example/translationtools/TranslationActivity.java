package com.example.translationtools;

import androidx.appcompat.app.AppCompatActivity;

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
    private DBHelper db;
    private TextView inputField;
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

        list = new ArrayList<String>();
        list.add("Call me Ishmael.");
        list.add("Some years ago—never mind how long precisely— having little or no money in my purse, and nothing particular to interest me on shore.");
        list.add("I thought I would sail about a little and see the watery part of the world.");
        list.add("It is a way I have of driving off the spleen and regulating the circulation.");
        list.add("Whenever I find myself growing grim about the mouth; whenever it is a damp, drizzly November in my soul;");
        list.add("Whenever I find myself involuntarily pausing before coffin warehouses, and bringing up the rear of every funeral I meet;");
        list.add("And especially whenever my hypos get such an upper hand of me, that it requires a strong moral principle.");
//        list.add("It is a way I have of driving off the spleen and regulating the circulation.");
//        list.add("It is a way I have of driving off the spleen and regulating the circulation.");
//        list.add("It is a way I have of driving off the spleen and regulating the circulation.");
//        list.add("It is a way I have of driving offd regulating the circulation.");
////        list.add("It is a way I have of driving off the spleen and regulating the circulation.");
////        list.add("It is a way I have of driving off the spleen and regulating the circulation.");
////        list.add("It is a way I have of driving off the spleen and regulating the circulation.");
////        list.add("It is a way I have of driving off the spleen and regulating the circulation.");
////        list.add("It is a way I have of driving off the spleen and regulating the circulation."); the spleen and regulating the circulation.");
//        list.add("It is a way I have of driving off the spleen and regulating the circulation.");
//        list.add("It is a way I have of driving off the spleen and regulating the circulation.");

        inputField = findViewById(R.id.inputField);

    }

    public void next() {

    }

    public void prev() {

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
                if (currentIndex == 0) break;
                currentIndex--;
                str = (String) list.get(currentIndex);
                inputField.setText(str);
                Toast.makeText(getApplicationContext(),"Клик", Toast.LENGTH_SHORT).show();
                break;
            case R.id.next:
                str = (String) list.get(currentIndex);
                inputField.setText(str);
                currentIndex++;
                Toast.makeText(getApplicationContext(),"Клик", Toast.LENGTH_SHORT).show();
                break;

        }
    }
}
