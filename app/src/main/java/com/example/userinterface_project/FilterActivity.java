package com.example.userinterface_project;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioGroup;

public class FilterActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {

    private RadioGroup radioGroup;
    private CheckBox checkBoxEasy;
    private CheckBox checkBoxNormal;
    private CheckBox checkBoxHard;
    private CheckBox checkBoxWord;
    private CheckBox checkBoxMeaning;
    private Button buttonApply;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("단어장 메뉴");

        radioGroup = findViewById(R.id.radio_group_difficulty);
        checkBoxEasy = findViewById(R.id.checkbox_easy);
        checkBoxNormal = findViewById(R.id.checkbox_normal);
        checkBoxHard = findViewById(R.id.checkbox_hard);
        checkBoxWord = findViewById(R.id.checkbox_word);
        checkBoxMeaning = findViewById(R.id.checkbox_meaning);
        buttonApply = findViewById(R.id.button_apply);

        if (savedInstanceState == null) {
            radioGroup.check(R.id.radio_recent);
        } // 첫 실행 시 기본값으로 최근 등록 순 선택

        checkBoxEasy.setOnCheckedChangeListener(this);
        checkBoxNormal.setOnCheckedChangeListener(this);
        checkBoxHard.setOnCheckedChangeListener(this);
        checkBoxWord.setOnCheckedChangeListener(this);
        checkBoxMeaning.setOnCheckedChangeListener(this);
        buttonApply.setOnClickListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        //난이도가 모두 unchecked 되거나, 단어와 뜻이 모두 unchecked 되는 것을 막는다.
        if(!(checkBoxEasy.isChecked()||checkBoxNormal.isChecked()||checkBoxHard.isChecked()))
            buttonView.setChecked(true);
        if(!(checkBoxWord.isChecked()||checkBoxMeaning.isChecked()))
            buttonView.setChecked(true);
    }

    @Override
    public void onClick(View v) {

    }
}