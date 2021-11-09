package com.example.userinterface_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class WordListActivity extends AppCompatActivity implements View.OnClickListener {

    FloatingActionButton addbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_empty_word_list);
        //setContentView(R.layout.activity_word_list);

        //액션바에 뒤로가기 버튼 만들기
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);


        addbtn = (FloatingActionButton) findViewById(R.id.plus_btn);
        addbtn.setOnClickListener(this);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if(v == addbtn) {//더하기 버튼 누르면 단어 추가 화면으로 바뀜
            Intent intent = new Intent();
            ComponentName componentName = new ComponentName(
                    "com.example.userinterface_project",
                    "com.example.userinterface_project.AddWordActivity"
            );
            intent.setComponent(componentName);
            startActivity(intent);
        }
    }
}