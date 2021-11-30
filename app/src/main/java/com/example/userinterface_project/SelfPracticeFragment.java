package com.example.userinterface_project;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import android.widget.TextView;

import com.example.userinterface_project.db.Word;
import com.example.userinterface_project.db.WordDbHelper;

import java.util.ArrayList;
import java.util.List;

public class SelfPracticeFragment extends Fragment {

    private static final String ARG_QUESTIONS = "questions";
    private static final String ARG_IS_TYPE_WORD = "isTypeWord";
    private static final String CURRENT_QUIZ = "currentQuiz";

    private ArrayList<Word> words; // 전체 단어 목록
    private List<Word> questions;
    private int currentQuiz = -1;
    private boolean isTypeWord;
    private final ArrayList<Word> wrong = new ArrayList<>();

    private TextView questionTextView;
    private TextView answerTextView;
    private Button answerBtn;
    private Button correctBtn;
    private Button wrongBtn;
    private WordDbHelper dbHelper;

    private int right = 0;

    public SelfPracticeFragment() {
    }


    public static SelfPracticeFragment newInstance(
            SelfPracticeActivity activity, List<Word> questions, boolean isTypeWord) {
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
        dbHelper = WordDbHelper.getInstance(activity);
        Bundle args = requireArguments();
        if (words == null) {
            words = activity.getWords();
            questions = activity.getWordsByIds(args.getIntArray(ARG_QUESTIONS));
        }
        isTypeWord = args.getBoolean(ARG_IS_TYPE_WORD);

        if (savedInstanceState != null) {
            currentQuiz = savedInstanceState.getInt(CURRENT_QUIZ, currentQuiz);
        } else {
            currentQuiz = 0;
        }
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
            Word currentQuestion = questions.get(currentQuiz);
            right++;
            dbHelper.incrementCountCorrect(currentQuestion.getId());
            if (currentQuiz + 1 >= questions.size()) {
                SelfPracticeActivity activity = (SelfPracticeActivity) requireActivity();
                activity.setQuizProgress(1, 1);
                activity.showResult(right, questions.size() - right, wrong);
            } else {
                currentQuiz++;
                showCurrentQuiz();
            }
        });

        wrongBtn.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.choice_button_color_incorrect));
        wrongBtn.setText("X");
        wrongBtn.setOnClickListener(v -> {
            Word currentQuestion = questions.get(currentQuiz);
            wrong.add(currentQuestion);
            dbHelper.incrementCountIncorrect(currentQuestion.getId());
            if (currentQuiz + 1 >= questions.size()) {
                SelfPracticeActivity activity = (SelfPracticeActivity) requireActivity();
                activity.setQuizProgress(1, 1);
                activity.showResult(right, questions.size() - right, wrong);
            } else {
                currentQuiz++;
                showCurrentQuiz();
            }
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
        correctBtn.setVisibility(View.INVISIBLE);
        wrongBtn.setVisibility(View.INVISIBLE);
        answerBtn.setVisibility(View.VISIBLE);
    }
}