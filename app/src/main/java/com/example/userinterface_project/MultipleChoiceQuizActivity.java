package com.example.userinterface_project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.userinterface_project.db.Word;
import com.example.userinterface_project.db.WordDbHelper;

import java.util.ArrayList;
import java.util.Collections;

public class MultipleChoiceQuizActivity extends AppCompatActivity {
    public static final String EXTRA_NOTE_ID = "noteId";
    private final WordDbHelper dbHelper = WordDbHelper.getInstance(this);
    private ArrayList<Word> words;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiple_choice_quiz);


        Intent intent = getIntent();
        long noteId = intent.getLongExtra(EXTRA_NOTE_ID, -1); // noteId
        boolean isTypeWord = "word".equals(intent.getStringExtra("WordMeaning")); // 퀴즈 유형
        getWords();

        if (words.size() < MultipleChoiceQuizFragment.NUMBER_OF_CHOICES) {
            Toast.makeText(this,
                    getString(R.string.multiple_choice_error, MultipleChoiceQuizFragment.NUMBER_OF_CHOICES),
                    Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, MultipleChoiceQuizFragment.newInstance(
                            this, makeQuestions(), isTypeWord)).commit();
        }

        progressBar = findViewById(R.id.progress_indicator);
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