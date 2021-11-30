package com.example.userinterface_project;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.userinterface_project.db.Word;
import com.example.userinterface_project.db.WordDbHelper;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class MultipleChoiceQuizActivity extends AppCompatActivity
        implements QuizResultFragment.OnResultButtonClickListener {
    public static final String EXTRA_NOTE_ID = "noteId";
    public static final String WRONG_QUESTIONS = "wrongQuestions";
    private final WordDbHelper dbHelper = WordDbHelper.getInstance(this);
    private ArrayList<Word> words;
    private LinearProgressIndicator progressBar;
    private List<Word> wrongQuestions;
    private boolean isTypeWord;
    private long noteId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiple_choice_quiz);

        Intent intent = getIntent();
        isTypeWord = "word".equals(intent.getStringExtra("WordMeaning")); // 퀴즈 유형
        noteId = intent.getLongExtra(EXTRA_NOTE_ID, -1);
        getWords();

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("퀴즈 - " + dbHelper.getNote(noteId).getName());
        actionBar.setDisplayHomeAsUpEnabled(true);

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

            WordDbHelper dbHelper = WordDbHelper.getInstance(this);
            dbHelper.updateLastStudiedDate(noteId, new Date()); // 최근 학습 날짜 업데이트
        } else {
            int[] wrongIds = savedInstanceState.getIntArray(WRONG_QUESTIONS);
            if (wrongIds != null) {
                wrongQuestions = getWordsByIds(wrongIds);
            }
            Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.container);
            if (fragment instanceof QuizResultFragment) {
                setQuizProgress(1, 1);
            }
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

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (wrongQuestions != null) {
            int[] wrongIds = new int[wrongQuestions.size()];
            for (int i = 0; i < wrongIds.length; i++) {
                wrongIds[i] = (int) wrongQuestions.get(i).getId();
            }
            outState.putIntArray(WRONG_QUESTIONS, wrongIds);
        }
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
        return words = dbHelper.getWordList(noteId);
    }

    public ArrayList<Word> getWordsByIds(int[] ids) {
        WordDbHelper dbHelper = WordDbHelper.getInstance(this);
        ArrayList<Word> result = new ArrayList<>(ids.length);
        for (int i : ids) {
            result.add(dbHelper.getWordById(i));
        }
        return result;
    }

    public void setQuizProgress(int progress, int max) {
        progressBar.setMax(max);
        progressBar.setProgressCompat(progress, true);
    }

    public void showResult(int right, int wrong, List<Word> wrongQuestions) {
        this.wrongQuestions = wrongQuestions;
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, QuizResultFragment.newInstance(right, wrong))
                .commit();
    }

    @Override
    public void onRetryButtonClick() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, MultipleChoiceQuizFragment.newInstance(
                        this, makeQuestions(), isTypeWord))
                .commit();
    }

    @Override
    public void onRetryButton2Click() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, MultipleChoiceQuizFragment.newInstance(
                        this, wrongQuestions, isTypeWord))
                .commit();
    }
}