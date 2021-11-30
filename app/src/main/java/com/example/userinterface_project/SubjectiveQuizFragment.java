package com.example.userinterface_project;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.userinterface_project.db.Word;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class SubjectiveQuizFragment extends Fragment {

    private static final String ARG_QUESTIONS = "questions";
    private static final String ARG_IS_TYPE_WORD = "isTypeWord";

    private ArrayList<Word> words; // 전체 단어 목록
    private ArrayList<Word> questions;
    private int currentQuiz = -1;
    private boolean isTypeWord;

    private TextView questionTextView;
    private EditText editText;
    private Button checkButton;
    private TextView correctAnswer;
    private Button nextButton;

    public SubjectiveQuizFragment() {
    }

    public static SubjectiveQuizFragment newInstance(
            SubjectiveQuizActivity activity, ArrayList<Word> questions, boolean isTypeWord) {
        ArrayList<Word> words = activity.getWords();
        int[] questionIds = new int[questions.size()]; // bundle 저장용
        int i = 0;
        for (Word w : questions) {
            questionIds[i++] = (int) w.getId();
        }

        SubjectiveQuizFragment fragment = new SubjectiveQuizFragment();
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
        SubjectiveQuizActivity activity = (SubjectiveQuizActivity) requireActivity();

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
        return inflater.inflate(R.layout.fragment_subjective_quiz, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        questionTextView = view.findViewById(R.id.word_textview);
        editText = view.findViewById(R.id.edit_text);
        checkButton = view.findViewById(R.id.check_button);
        checkButton.setOnClickListener(v -> onCheckButtonClick());
        correctAnswer = view.findViewById(R.id.correct_answer);
        nextButton = view.findViewById(R.id.next_button);
        nextButton.setOnClickListener(v -> {
            currentQuiz++;
            showCurrentQuiz();
        });
        currentQuiz = 0;
        showCurrentQuiz();
    }

    @SuppressLint("ResourceAsColor")
    private void onCheckButtonClick(){
        Context context = requireContext();
        editText.setClickable(false);
        editText.setFocusable(false);
        checkButton.setVisibility(View.GONE);
        Word word = (Word) questionTextView.getTag();
        if(isTypeWord) {// eqitText 수정 불가능하게하고 색칠하기, 틀렸으면 맞는 답 출력
            if(word.getWord().equals(editText.getText().toString())) {
                editText.setBackgroundTintList(ContextCompat.getColorStateList(
                        context, R.color.choice_button_color_correct));
            }
            else {
                editText.setBackgroundTintList(ContextCompat.getColorStateList(
                        context, R.color.choice_button_color_incorrect));
                correctAnswer.setVisibility(View.VISIBLE);
                correctAnswer.setText(word.getWord());
            }
        }
        else {
            if(word.getMeaning().equals(editText.getText().toString())) {
                editText.setBackgroundTintList(ContextCompat.getColorStateList(
                        context, R.color.choice_button_color_correct));
            }
            else {
                editText.setBackgroundTintList(ContextCompat.getColorStateList(
                        context, R.color.choice_button_color_incorrect));
                correctAnswer.setVisibility(View.VISIBLE);
                correctAnswer.setText(word.getMeaning());
            }
        }
        nextButton.setVisibility(View.VISIBLE);
        nextButton.setEnabled(true);
    }

    @SuppressLint("ResourceAsColor")
    private void showCurrentQuiz() {
        SubjectiveQuizActivity activity = (SubjectiveQuizActivity) requireActivity();
        activity.setQuizProgress(currentQuiz, questions.size());
        Word word = questions.get(currentQuiz);
        questionTextView.setText(isTypeWord ? word.getMeaning() : word.getWord());
        questionTextView.setTag(word);
        editText.setFocusable(true);
        editText.setClickable(true);
        editText.setBackgroundColor(android.R.drawable.editbox_background);
        editText.setHint(isTypeWord ? "단어를 입력해주세요" : "뜻을 입력해주세요");
        checkButton.setVisibility(View.VISIBLE);
        correctAnswer.setVisibility(View.GONE);

        // 다음 버튼
        nextButton.setEnabled(false);
    }
}
