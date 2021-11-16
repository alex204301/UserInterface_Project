package com.example.userinterface_project;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("단어장 메뉴");

        radioGroup = findViewById(R.id.radio_group_sorting);
        checkBoxEasy = findViewById(R.id.checkbox_easy);
        checkBoxNormal = findViewById(R.id.checkbox_normal);
        checkBoxHard = findViewById(R.id.checkbox_hard);
        checkBoxWord = findViewById(R.id.checkbox_word);
        checkBoxMeaning = findViewById(R.id.checkbox_meaning);
        Button buttonApply = findViewById(R.id.button_apply);

        radioGroup.check(radioGroup.getChildAt(getIntent().getIntExtra("sortBy", 0)).getId());
        checkBoxEasy.setChecked(getIntent().getBooleanExtra("showEasy", true));
        checkBoxNormal.setChecked(getIntent().getBooleanExtra("showNormal", true));
        checkBoxHard.setChecked(getIntent().getBooleanExtra("showHard", true));
        checkBoxWord.setChecked(getIntent().getBooleanExtra("showWord", true));
        checkBoxMeaning.setChecked(getIntent().getBooleanExtra("showMeaning", true));

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
        Intent returnIntent = new Intent();
        returnIntent.putExtra("sortBy",
                radioGroup.indexOfChild(radioGroup.findViewById(radioGroup.getCheckedRadioButtonId())));
        returnIntent.putExtra("showEasy", checkBoxEasy.isChecked());
        returnIntent.putExtra("showNormal", checkBoxNormal.isChecked());
        returnIntent.putExtra("showHard", checkBoxHard.isChecked());
        returnIntent.putExtra("showWord", checkBoxWord.isChecked());
        returnIntent.putExtra("showMeaning", checkBoxMeaning.isChecked());
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }
}