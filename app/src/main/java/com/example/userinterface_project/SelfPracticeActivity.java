package com.example.userinterface_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.userinterface_project.db.Word;
import com.example.userinterface_project.db.WordDbHelper;

import java.util.ArrayList;
import java.util.Collections;

public class SelfPracticeActivity extends AppCompatActivity {
    public static final String EXTRA_NOTE_ID = "noteId";
    private final WordDbHelper dbHelper = WordDbHelper.getInstance(this);
    private ArrayList<Word> words;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_self_practice);


        Intent intent = getIntent();
        long noteId = intent.getLongExtra(EXTRA_NOTE_ID, -1); // noteId
        boolean isTypeWord = "word".equals(intent.getStringExtra("WordMeaning")); // 퀴즈 유형
        getWords();

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("퀴즈 - " + dbHelper.getNote(noteId).getName());
        actionBar.setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, SelfPracticeFragment.newInstance(
                            this, makeQuestions(), isTypeWord)).commit();
        }

        progressBar = findViewById(R.id.progress_indicator);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private ArrayList<Word> makeQuestions() {
        int difficulty;
        switch (getIntent().getStringExtra("Difficulty")) {
            case "easy":
                difficulty = Word.DIFFICULTY_EASY;
                break;
            case "normal":
                difficulty = Word.DIFFICULTY_NORMAL;
                break;
            case "hard":
                difficulty = Word.DIFFICULTY_HARD;
                break;
            default: // 모두
                difficulty = -1;
        }

        ArrayList<Word> questions = new ArrayList<>();
        for (Word w : words) {
            if (difficulty == -1 || w.getDifficulty() == difficulty) {
                questions.add(w);
            }
        }
        Collections.shuffle(questions); // 문제 순서 섞기

        return questions;
    }

    public ArrayList<Word> getWords() {
        if (words != null) {
            return words;
        }
        WordDbHelper dbHelper = WordDbHelper.getInstance(this);
        return words = dbHelper.getWordList(getIntent().getLongExtra(EXTRA_NOTE_ID, -1));
    }

    public ArrayList<Word> getWordsByIds(int[] ids) {
        ArrayList<Word> result = new ArrayList<>(ids.length);
        for (int i : ids) {
            result.add(dbHelper.getWordById(i));
        }
        return result;
    }

    public void setQuizProgress(int progress, int max) {
        progressBar.setMax(max);
        progressBar.setProgress(progress);
    }
}