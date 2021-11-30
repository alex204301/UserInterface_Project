package com.example.userinterface_project;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.userinterface_project.db.Word;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class SelfPracticeFragment extends Fragment {

    private static final String ARG_QUESTIONS = "questions";
    private static final String ARG_IS_TYPE_WORD = "isTypeWord";

    private final Random random = new Random();

    private ArrayList<Word> words; // 전체 단어 목록
    private ArrayList<Word> questions;
    private int currentQuiz = -1;
    private boolean isTypeWord;
    private ArrayList<Word> answers; // 선택지

    private TextView questionTextView;
    private TextView answerTextView;
    private Button answerBtn;
    private Button correctBtn;
    private Button wrongBtn;

    public SelfPracticeFragment() {
    }


    public static SelfPracticeFragment newInstance(
            SelfPracticeActivity activity, ArrayList<Word> questions, boolean isTypeWord) {
        ArrayList<Word> words = activity.getWords();
        int[] questionIds = new int[questions.size()]; // bundle 저장용
        int i = 0;
        for (Word w : questions) {
            questionIds[i++] = (int) w.getId();
        }

        SelfPracticeFragment fragment = new SelfPracticeFragment();
        Bundle args = new Bundle();
        args.putIntArray(ARG_QUESTIONS, questionIds);
        args.putBoolean(ARG_IS_TYPE_WORD, isTypeWord);
        fragment.setArguments(args);

        fragment.words = words;
        fragment.questions = questions;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SelfPracticeActivity activity = (SelfPracticeActivity) requireActivity();
        Bundle args = requireArguments();
        if (words == null) {
            words = activity.getWords();
            questions = activity.getWordsByIds(args.getIntArray(ARG_QUESTIONS));
        }
        isTypeWord = args.getBoolean(ARG_IS_TYPE_WORD);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_self_practice, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        questionTextView = view.findViewById(R.id.word_textview);
        answerTextView = view.findViewById(R.id.answer_textView);

        answerBtn = view.findViewById(R.id.answerBtn);
        correctBtn = view.findViewById(R.id.correctBtn);
        wrongBtn = view.findViewById(R.id.wrongBtn);

        answerBtn.setOnClickListener(v -> {
            answerBtn.setVisibility(View.INVISIBLE);
            answerTextView.setVisibility(View.VISIBLE);
            correctBtn.setVisibility(View.VISIBLE);
            wrongBtn.setVisibility(View.VISIBLE);
        });

        correctBtn.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.choice_button_color_correct));
        correctBtn.setText("O");
        correctBtn.setOnClickListener(v -> {
            currentQuiz++;
            showCurrentQuiz();
            correctBtn.setVisibility(View.INVISIBLE);
            wrongBtn.setVisibility(View.INVISIBLE);
            answerBtn.setVisibility(View.VISIBLE);
        });
        wrongBtn.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.choice_button_color_incorrect));
        wrongBtn.setText("X");
        wrongBtn.setOnClickListener(v -> {
            currentQuiz++;
            showCurrentQuiz();
            wrongBtn.setVisibility(View.INVISIBLE);
            correctBtn.setVisibility(View.INVISIBLE);
            answerBtn.setVisibility(View.VISIBLE);
        });

        currentQuiz = 0;
        showCurrentQuiz();
    }

        private void showCurrentQuiz() {
        SelfPracticeActivity activity = (SelfPracticeActivity) requireActivity();
        activity.setQuizProgress(currentQuiz, questions.size());
        Word word = questions.get(currentQuiz);
        questionTextView.setText(isTypeWord ? word.getMeaning() : word.getWord());
        questionTextView.setTag(word);
        answerTextView.setVisibility(View.INVISIBLE);
        answerTextView.setText(isTypeWord ? word.getWord() : word.getMeaning());
    }
}